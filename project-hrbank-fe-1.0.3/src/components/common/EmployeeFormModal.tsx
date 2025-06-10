import {
  CalendarToday as CalendarIcon,
  Close as CloseIcon,
  PhotoCamera as PhotoCameraIcon
} from '@mui/icons-material';
import {
  Avatar,
  Box,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  Grid,
  IconButton,
  InputAdornment,
  InputLabel,
  MenuItem,
  Select,
  styled,
  TextField,
  Typography
} from '@mui/material';
import React, { useEffect, useState } from 'react';
import { DepartmentDto } from '../../api/department/model.ts';
import {
  EmployeeCreateRequest,
  EmployeeDto,
  EmployeeStatus,
  EmployeeUpdateRequest
} from '../../api/employee/model.ts';

interface EmployeeFormModalProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (data: FormData) => Promise<void>;
  employee?: EmployeeDto;
  title: string;
  departments: DepartmentDto[];
}

const StyledDialogTitle = styled(DialogTitle)(({ theme }) => ({
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  padding: theme.spacing(2, 3),
  borderBottom: `1px solid ${theme.palette.divider}`
}));

const StyledDialogContent = styled(DialogContent)(({ theme }) => ({
  padding: theme.spacing(3),
}));

const StyledDialogActions = styled(DialogActions)(({ theme }) => ({
  padding: theme.spacing(2, 3),
  borderTop: `1px solid ${theme.palette.divider}`
}));

const FormLabel = styled(InputLabel)(({ theme }) => ({
  fontWeight: 500,
  marginBottom: theme.spacing(1),
  color: theme.palette.text.primary,
}));

const UploadButton = styled('label')(({ theme }) => ({
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  justifyContent: 'center',
  width: '100%',
  height: '100%',
  cursor: 'pointer',
  border: `1px dashed ${theme.palette.divider}`,
  borderRadius: '50%',
  padding: theme.spacing(1),
  '&:hover': {
    backgroundColor: theme.palette.action.hover,
  }
}));

const EmployeeFormModal: React.FC<EmployeeFormModalProps> = ({
  open,
  onClose,
  onSubmit,
  employee,
  title,
  departments
}) => {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [departmentId, setDepartmentId] = useState<number>(0);
  const [position, setPosition] = useState('');
  const [hireDate, setHireDate] = useState('');
  const [status, setStatus] = useState<EmployeeStatus>(EmployeeStatus.ACTIVE);
  const [memo, setMemo] = useState('');
  const [profileImage, setProfileImage] = useState<File | null>(null);
  const [profilePreview, setProfilePreview] = useState<string | null>(null);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (employee) {
      setName(employee.name);
      setEmail(employee.email);
      setDepartmentId(employee.departmentId);
      setPosition(employee.position);
      setHireDate(employee.hireDate);
      setStatus(employee.status);
      setMemo('');
      setProfilePreview(employee.profileImageId 
        ? `/api/files/${employee.profileImageId}/download` 
        : null);
    } else {
      resetForm();
    }
  }, [employee, open]);

  const resetForm = () => {
    setName('');
    setEmail('');
    setDepartmentId(departments.length > 0 ? departments[0].id : 0);
    setPosition('');
    setHireDate('');
    setStatus(EmployeeStatus.ACTIVE);
    setMemo('');
    setProfileImage(null);
    setProfilePreview(null);
    setErrors({});
  };

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!name.trim()) {
      newErrors.name = '이름을 입력해주세요.';
    }

    if (!email.trim()) {
      newErrors.email = '이메일을 입력해주세요.';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      newErrors.email = '유효한 이메일 주소를 입력해주세요.';
    }

    if (!departmentId) {
      newErrors.departmentId = '부서를 선택해주세요.';
    }

    if (!position.trim()) {
      newErrors.position = '직함을 입력해주세요.';
    }

    if (!hireDate) {
      newErrors.hireDate = '입사일을 입력해주세요.';
    } else {
      const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
      if (!dateRegex.test(hireDate)) {
        newErrors.hireDate = '입사일 형식이 올바르지 않습니다. (YYYY-MM-DD)';
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async () => {
    if (!validateForm()) return;
    
    setSubmitting(true);
    try {
      const formData = new FormData();
      console.log({memo});
      const employeeData = employee 
        ? {
            name,
            email,
            departmentId,
            position,
            hireDate,
            status,
            memo: memo || '직원 정보 수정'
          } as EmployeeUpdateRequest
        : {
            name,
            email,
            departmentId,
            position,
            hireDate,
            memo: memo || '신규 직원 등록'
          } as EmployeeCreateRequest;
      
      formData.append('employee', new Blob([JSON.stringify(employeeData)], { type: 'application/json' }));
      
      if (profileImage) {
        formData.append('profile', profileImage);
      }
      
      await onSubmit(formData);
      onClose();
      resetForm();
    } catch (error) {
      console.error('직원 저장 중 오류가 발생했습니다:', error);
    } finally {
      setSubmitting(false);
    }
  };

  const handleProfileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      setProfileImage(file);
      
      // 이미지 미리보기 생성
      const reader = new FileReader();
      reader.onload = () => {
        setProfilePreview(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  return (
    <Dialog 
      open={open} 
      onClose={onClose} 
      maxWidth="md" 
      fullWidth
      PaperProps={{
        sx: { borderRadius: 2 }
      }}
    >
      <StyledDialogTitle>
        <Typography variant="h6" component="div" fontWeight={600}>
          {title}
        </Typography>
        <IconButton edge="end" color="inherit" onClick={onClose} aria-label="close">
          <CloseIcon />
        </IconButton>
      </StyledDialogTitle>
      
      <StyledDialogContent>
        <Grid container spacing={3}>
          <Grid item xs={12} md={3} sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
            <Box sx={{ width: 150, height: 150, mb: 2 }}>
              {profilePreview ? (
                <Box position="relative">
                  <Avatar 
                    src={profilePreview} 
                    sx={{ width: 150, height: 150 }}
                  />
                  <Box 
                    position="absolute" 
                    bottom={0} 
                    right={0}
                    sx={{ 
                      backgroundColor: 'rgba(0, 0, 0, 0.5)',
                      borderRadius: '50%',
                      p: 0.5
                    }}
                  >
                    <UploadButton htmlFor="profile-upload">
                      <PhotoCameraIcon color="action" />
                    </UploadButton>
                    <input
                      id="profile-upload"
                      type="file"
                      accept="image/*"
                      style={{ display: 'none' }}
                      onChange={handleProfileChange}
                      disabled={submitting}
                    />
                  </Box>
                </Box>
              ) : (
                <UploadButton htmlFor="profile-upload">
                  <PhotoCameraIcon sx={{ fontSize: 40, color: 'text.secondary', mb: 1 }} />
                  <Typography variant="body2" color="text.secondary">
                    프로필 사진 업로드
                  </Typography>
                  <input
                    id="profile-upload"
                    type="file"
                    accept="image/*"
                    style={{ display: 'none' }}
                    onChange={handleProfileChange}
                    disabled={submitting}
                  />
                </UploadButton>
              )}
            </Box>
          </Grid>
          
          <Grid item xs={12} md={9}>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <FormLabel required>이름</FormLabel>
                <TextField
                  fullWidth
                  placeholder="이름을 입력하세요"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  error={!!errors.name}
                  helperText={errors.name}
                  disabled={submitting}
                  size="small"
                  variant="outlined"
                />
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <FormLabel required>이메일</FormLabel>
                <TextField
                  fullWidth
                  placeholder="이메일을 입력하세요"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  error={!!errors.email}
                  helperText={errors.email}
                  disabled={submitting}
                  size="small"
                  variant="outlined"
                />
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <FormLabel required>부서</FormLabel>
                <FormControl fullWidth size="small" error={!!errors.departmentId}>
                  <Select
                    value={departmentId}
                    onChange={(e) => setDepartmentId(Number(e.target.value))}
                    disabled={submitting}
                  >
                    {departments.map((dept) => (
                      <MenuItem key={dept.id} value={dept.id}>
                        {dept.name}
                      </MenuItem>
                    ))}
                  </Select>
                  {errors.departmentId && (
                    <Typography variant="caption" color="error">
                      {errors.departmentId}
                    </Typography>
                  )}
                </FormControl>
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <FormLabel required>직함</FormLabel>
                <TextField
                  fullWidth
                  placeholder="직함을 입력하세요"
                  value={position}
                  onChange={(e) => setPosition(e.target.value)}
                  error={!!errors.position}
                  helperText={errors.position}
                  disabled={submitting}
                  size="small"
                  variant="outlined"
                />
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <FormLabel required>입사일</FormLabel>
                <TextField
                  fullWidth
                  type="date"
                  value={hireDate}
                  onChange={(e) => setHireDate(e.target.value)}
                  error={!!errors.hireDate}
                  helperText={errors.hireDate}
                  disabled={submitting}
                  size="small"
                  variant="outlined"
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <CalendarIcon fontSize="small" color="action" />
                      </InputAdornment>
                    ),
                  }}
                  sx={{
                    '& input[type="date"]::-webkit-calendar-picker-indicator': {
                      opacity: 0,
                      position: 'absolute',
                      width: '100%',
                      height: '100%',
                      cursor: 'pointer',
                      zIndex: 1
                    }
                  }}
                />
              </Grid>
              
              {employee && (
                <Grid item xs={12} sm={6}>
                  <FormLabel required>상태</FormLabel>
                  <FormControl fullWidth size="small">
                    <Select
                      value={status}
                      onChange={(e) => setStatus(e.target.value as EmployeeStatus)}
                      disabled={submitting}
                    >
                      <MenuItem value={EmployeeStatus.ACTIVE}>재직중</MenuItem>
                      <MenuItem value={EmployeeStatus.ON_LEAVE}>휴직중</MenuItem>
                      <MenuItem value={EmployeeStatus.RESIGNED}>퇴사</MenuItem>
                    </Select>
                  </FormControl>
                </Grid>
              )}
              
              <Grid item xs={12}>
                <FormLabel>메모 (변경 이력에 기록됨)</FormLabel>
                <TextField
                  fullWidth
                  multiline
                  rows={3}
                  placeholder="변경 사유나 메모를 입력하세요"
                  value={memo}
                  onChange={(e) => setMemo(e.target.value)}
                  disabled={submitting}
                  size="small"
                  variant="outlined"
                />
              </Grid>
            </Grid>
          </Grid>
        </Grid>
      </StyledDialogContent>
      
      <StyledDialogActions>
        <Button 
          onClick={onClose} 
          disabled={submitting}
          variant="outlined"
          color="inherit"
          sx={{ minWidth: 80 }}
        >
          취소
        </Button>
        <Button 
          onClick={handleSubmit} 
          variant="contained" 
          disabled={submitting}
          sx={{ minWidth: 80 }}
          startIcon={submitting ? <CircularProgress size={20} color="inherit" /> : null}
        >
          {submitting ? '저장 중...' : '저장'}
        </Button>
      </StyledDialogActions>
    </Dialog>
  );
};

export default EmployeeFormModal; 