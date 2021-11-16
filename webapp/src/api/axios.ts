import Axios from 'axios';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

export const axios = Axios.create({
  baseURL: API_URL,
});

axios.interceptors.response.use((response) => {
  return response.data;
});
