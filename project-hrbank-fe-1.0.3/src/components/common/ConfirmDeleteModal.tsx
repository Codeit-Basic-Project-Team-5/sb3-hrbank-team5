import {
  Close as CloseIcon,
  WarningAmber as WarningIcon
} from '@mui/icons-material';
import {
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  IconButton,
  Typography,
  styled
} from '@mui/material';
import React, { useState } from 'react';

interface ConfirmDeleteModalProps {
  open: boolean;
  onClose: () => void;
  onConfirm: () => Promise<void>;
  title: string;
  content: string;
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

const WarningContainer = styled('div')(({ theme }) => ({
  display: 'flex',
  alignItems: 'center',
  marginBottom: theme.spacing(2),
}));

const ConfirmDeleteModal: React.FC<ConfirmDeleteModalProps> = ({
  open,
  onClose,
  onConfirm,
  title,
  content
}) => {
  const [deleting, setDeleting] = useState(false);

  const handleConfirm = async () => {
    setDeleting(true);
    try {
      await onConfirm();
      onClose();
    } catch (error) {
      console.error('삭제 중 오류가 발생했습니다:', error);
    } finally {
      setDeleting(false);
    }
  };

  return (
    <Dialog 
      open={open} 
      onClose={onClose}
      maxWidth="sm"
      PaperProps={{
        sx: { borderRadius: 2, width: '400px' }
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
        <WarningContainer>
          <WarningIcon color="warning" sx={{ fontSize: 28, mr: 1.5 }} />
          <Typography variant="body1" color="text.primary">
            {content}
          </Typography>
        </WarningContainer>
        <DialogContentText color="text.secondary">
          삭제된 데이터는 복구할 수 없습니다.
        </DialogContentText>
      </StyledDialogContent>
      
      <StyledDialogActions>
        <Button 
          onClick={onClose} 
          disabled={deleting}
          variant="outlined"
          color="inherit"
          sx={{ minWidth: 80 }}
        >
          취소
        </Button>
        <Button 
          onClick={handleConfirm} 
          color="error" 
          variant="contained"
          disabled={deleting}
          sx={{ minWidth: 80 }}
          startIcon={deleting ? <CircularProgress size={20} color="inherit" /> : null}
        >
          {deleting ? '삭제 중...' : '삭제'}
        </Button>
      </StyledDialogActions>
    </Dialog>
  );
};

export default ConfirmDeleteModal; 