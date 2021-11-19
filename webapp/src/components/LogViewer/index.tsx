import React from 'react';
import { useInputs } from '../../api';

const LogViewer = () => {
  const inputs = useInputs();

  return (
    <div>
      <ul>
        {inputs.data?.map((d) => (
          <li key={d.id}>{d.value}</li>
        ))}
      </ul>
    </div>
  );
};

export default LogViewer;
