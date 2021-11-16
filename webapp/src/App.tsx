import React from 'react';
import { Button } from '@chakra-ui/react';
import './App.css';
import MainMenu from './components/MainMenu';
import NotificationContainer from './components/NotificationContainer';
import { useNotificationStore } from './store/notifications';
import { useAddInput, useInputs } from './api';

function App() {
  const [notifications, addNotification, removeNotification] = useNotificationStore((state) => [
    state.notifications,
    state.addNotification,
    state.dismissNotification,
  ]);

  const mutation = useAddInput();
  const inputs = useInputs();

  const handleAddNotification = () => {
    addNotification({
      title: 'test',
      type: 'error',
      message: 'desc',
    });
  };

  const handleAddInput = () => {
    mutation.mutate({
      id: '',
      type: 'FILE',
      state: 'STOPPED',
      value: '/var/log/syslog',
    });
  };

  return (
    <MainMenu>
      <div className="App">
        <header className="App-header">
          <Button colorScheme={'blue'} onClick={handleAddNotification}>
            Test add notification
          </Button>
          <Button mt={1} colorScheme={'blue'} onClick={handleAddInput}>
            Add syslog input
          </Button>
          <ul>
            {inputs.data?.map((d) => (
              <li key={d.id}>{d.value}</li>
            ))}
          </ul>
        </header>
      </div>
      <NotificationContainer notifications={notifications} removeNotification={removeNotification} />
    </MainMenu>
  );
}

export default App;
