import React from 'react';
import { Box, HStack, IconButton, Table, Tbody, Th, Thead, Tr } from '@chakra-ui/react';
import { useInputs } from '../../api';
import { useLogs } from '../../api/logs';
import LogEntry from './components/LogEntry';
import { MdFilterList } from 'react-icons/md';

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
        <Table variant="simple">
          <Thead>
            <Tr>
              <Th>
                <span>Timestamp</span>
              </Th>
              <Th>
                <HStack>
                  <span>Source</span>
                  <IconButton aria-label="Source filter" icon={<MdFilterList />} size="xs" />
                </HStack>
              </Th>
              <Th>Level</Th>
              <Th>Message</Th>
            </Tr>
          </Thead>
          <Tbody>
            {logs?.data?.map((d) => (
              <LogEntry key={d.id} entry={d} />
            ))}
          </Tbody>
        </Table>
      </Box>
    </Box>
  );
};

export default LogViewer;
