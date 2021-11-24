import { LogEntry } from './types';
import { axios } from './axios';
import { useQuery } from 'react-query';

type LogFilter = {
  from?: number;
  to?: number;
  count?: number;
  offset?: number;
};

const getLogs = (filter?: LogFilter): Promise<LogEntry[]> => {
  const queryString: string = filter
    ? Object.keys(filter)
        .map((key) => key + '=' + (filter as any)[key])
        .join('&')
    : '';
  return axios.get(`/logs${queryString.length > 0 ? `?${queryString}` : ''}`);
};

export const useLogs = () => {
  return useQuery<LogEntry[], Error>({
    queryKey: ['logs'],
    queryFn: () => getLogs({ count: 100 }),
    refetchInterval: 10000,
  });
};
