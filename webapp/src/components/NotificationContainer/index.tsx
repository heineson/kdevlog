import { useEffect } from 'react';
import { useToast } from '@chakra-ui/react';
import { Notification } from '../../store/notifications';

type NotificationContainerProps = {
  notifications: Notification[];
  removeNotification: (id: string) => void;
};

const NotificationContainer = ({ notifications, removeNotification }: NotificationContainerProps) => {
  const toast = useToast();

  useEffect(() => {
    if (notifications && notifications.length > 0) {
      const n = notifications.pop();
      if (n) {
        toast({
          title: n.title,
          description: n.message,
          status: n.type,
          duration: 9000,
          isClosable: true,
        });
        removeNotification(n.id);
      }
    }
  }, [notifications, removeNotification, toast]);

  return <div />;
};

export default NotificationContainer;
