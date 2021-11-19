import React from 'react';
import MainMenu from './components/MainMenu';
import NotificationContainer from './components/NotificationContainer';
import { useNotificationStore } from './store/notifications';
import LogViewer from './components/LogViewer';

function App() {
  const [notifications, removeNotification] = useNotificationStore((state) => [
    state.notifications,
    state.dismissNotification,
  ]);

  return (
    <MainMenu>
      <LogViewer />
      <NotificationContainer notifications={notifications} removeNotification={removeNotification} />
    </MainMenu>
  );
}

export default App;
