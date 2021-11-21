import { useMutation, useQuery, useQueryClient } from 'react-query';
import { axios } from './axios';
import { Input } from './types';
import { useNotificationStore } from '../store/notifications';
import { AxiosError } from 'axios';

const getInputs = (): Promise<Input[]> => {
  return axios.get('/inputs');
};

const addInput = (input: Input) => {
  return axios.post('/inputs', input);
};

const updateInput = (input: Input) => {
  return axios.put(`/inputs/${input.id}`, input);
};

export const useInputs = () => {
  return useQuery<Input[], Error>({
    queryKey: ['inputs'],
    queryFn: () => getInputs(),
  });
};

export const useAddInput = () => {
  const { addNotification } = useNotificationStore();
  const queryClient = useQueryClient();

  return useMutation(addInput, {
    onError: (e: AxiosError) => {
      addNotification({
        type: 'error',
        title: e.response?.status === 409 ? 'Already added' : 'Failed to add input',
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries('inputs');
      addNotification({
        type: 'success',
        title: 'Log Input Added',
      });
    },
  });
};

export const useChangeInputState = () => {
  const { addNotification } = useNotificationStore();
  const queryClient = useQueryClient();

  return useMutation(updateInput, {
    onError: () => {
      addNotification({
        type: 'error',
        title: 'Failed to change input state',
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries('inputs');
      addNotification({
        type: 'success',
        title: 'Input started',
      });
    },
  });
};
