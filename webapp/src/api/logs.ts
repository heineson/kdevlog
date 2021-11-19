import { LogEntry } from './types';
import { axios } from './axios';
import { useQuery } from 'react-query';

const getLogs = (): Promise<LogEntry[]> => {
  return axios.get('/logs');
};

export const useLogs = () => {
  return useQuery<LogEntry[], Error>({
    queryKey: ['logs'],
    queryFn: () => getLogs(),
  });
};
