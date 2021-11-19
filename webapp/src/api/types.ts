export type Input = {
  id: string;
  value: string;
  type: string;
  state: string;
};

export type LogEntry = {
  id: string;
  source: string;
  timestamp: number;
  level: string;
  message: string;
};
