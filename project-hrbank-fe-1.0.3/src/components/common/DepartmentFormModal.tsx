import {
  CalendarToday as CalendarIcon,
  Close as CloseIcon
} from '@mui/icons-material';
import {
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Grid,
  IconButton,
  InputAdornment,
  InputLabel,
  styled,
  TextField,
  Typography
} from '@mui/material';
import React, { useEffect, useState } from 'react';
import { DepartmentCreateRequest, DepartmentDto, DepartmentUpdateRequest } from '../../api/department/model.ts';

interface DepartmentFormModalProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (data: DepartmentCreateRequest | DepartmentUpdateRequest) => Promise<void>;
  department?: DepartmentDto;
  title: string;
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

const DepartmentFormModal: React.FC<DepartmentFormModalProps> = ({
  open,
  onClose,
  onSubmit,
  department,
  title
}) => {
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [establishedDate, setEstablishedDate] = useState('');
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (department) {
      setName(department.name);
      setDescription(department.description);
      setEstablishedDate(department.establishedDate);
    } else {
      resetForm();
    }
  }, [department, open]);

  const resetForm = () => {
    setName('');
    setDescription('');
    setEstablishedDate('');
    setErrors({});
  };

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!name.trim()) {
      newErrors.name = '부서명을 입력해주세요.';
    }

    if (!description.trim()) {
      newErrors.description = '부서 설명을 입력해주세요.';
    }

    if (!establishedDate) {
      newErrors.establishedDate = '설립일을 입력해주세요.';
    } else {
      const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
      if (!dateRegex.test(establishedDate)) {
        newErrors.establishedDate = '설립일 형식이 올바르지 않습니다. (YYYY-MM-DD)';
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async () => {
    if (!validateForm()) return;

    setSubmitting(true);
    try {
      const formData: DepartmentCreateRequest | DepartmentUpdateRequest = {
        name,
        description,
        establishedDate
      };
      await onSubmit(formData);
      onClose();
      resetForm();
    } catch (error) {
      console.error('부서 저장 중 오류가 발생했습니다:', error);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog 
      open={open} 
      onClose={onClose} 
      maxWidth="sm" 
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
          <Grid item xs={12}>
            <FormLabel required>부서명</FormLabel>
            <TextField
              fullWidth
              placeholder="부서명을 입력하세요"
              value={name}
              onChange={(e) => setName(e.target.value)}
              error={!!errors.name}
              helperText={errors.name}
              disabled={submitting}
              size="small"
              variant="outlined"
            />
          </Grid>
          
          <Grid item xs={12}>
            <FormLabel required>설립일</FormLabel>
            <TextField
              fullWidth
              type="date"
              value={establishedDate}
              onChange={(e) => setEstablishedDate(e.target.value)}
              error={!!errors.establishedDate}
              helperText={errors.establishedDate}
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
          
          <Grid item xs={12}>
            <FormLabel required>설명</FormLabel>
            <TextField
              fullWidth
              multiline
              rows={4}
              placeholder="부서에 대한 설명을 입력하세요"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              error={!!errors.description}
              helperText={errors.description}
              disabled={submitting}
              size="small"
              variant="outlined"
            />
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
          {submitting ? '저장 중...' : '등록'}
        </Button>
      </StyledDialogActions>
    </Dialog>
  );
};

export default DepartmentFormModal; 