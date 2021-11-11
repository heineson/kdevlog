import React from 'react';
import './App.css';
import MainMenu from './components/MainMenu';
import NotificationContainer from './components/NotificationContainer';
import { useNotificationStore } from './store/notifications';
import { Button } from '@chakra-ui/react';

function App() {
  const [notifications, addNotification, removeNotification] = useNotificationStore((state) => [
    state.notifications,
    state.addNotification,
    state.dismissNotification,
  ]);

  const handleAddNotification = () => {
    addNotification({
      title: 'test',
      type: 'error',
      message: 'desc',
    });
  };

  return (
    <MainMenu>
      <div className="App">
        <header className="App-header">
          <p>
            Edit <code>src/App.tsx</code> and save to reload.
          </p>
          <a className="App-link" href="https://reactjs.org" target="_blank" rel="noopener noreferrer">
            Learn React
          </a>
          <Button colorScheme={'blue'} onClick={handleAddNotification}>
            Test add notification
          </Button>
        </header>
      </div>
      <NotificationContainer notifications={notifications} removeNotification={removeNotification} />
    </MainMenu>
  );
}

export default App;
