import React from 'react';
import dayjs from 'dayjs';
import { Td, Tr } from '@chakra-ui/react';
import { LogEntry } from '../../../../api/types';
import { useInputs } from '../../../../api';

type LogEntryProps = {
  entry: LogEntry;
};

const formatTimestamp = (t: number) => dayjs(t).format('MMM DD HH:mm:ss.SSS');

const LogEntryComp = ({ entry }: LogEntryProps) => {
  const inputs = useInputs();

  const formattedTimestamp = formatTimestamp(entry.timestamp);
  const sourceName = inputs?.data?.find((input) => input.id === entry.source)?.value || entry.source;

  return (
    <Tr key={entry.id}>
      <Td>{formattedTimestamp}</Td>
      <Td>{sourceName}</Td>
      <Td>{entry.level}</Td>
      <Td>{entry.message}</Td>
    </Tr>
  );
};

export default LogEntryComp;
