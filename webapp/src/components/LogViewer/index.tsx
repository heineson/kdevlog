import React from 'react';
import { useInputs } from '../../api';
import { Box } from '@chakra-ui/react';
import { useLogs } from '../../api/logs';
import LogEntry from './components/LogEntry';

const LogViewer = () => {
  const inputs = useInputs();
  const logs = useLogs();

  return (
    <Box>
      <ul>
        {inputs.data?.map((d) => (
          <li key={d.id}>{d.value}</li>
        ))}
      </ul>

      <Box mt={2} bg={'gray.50'}>
        <ul>
          {logs?.data?.map((d) => (
            <LogEntry key={d.id} entry={d} />
          ))}
        </ul>
      </Box>
    </Box>
  );
};

export default LogViewer;
