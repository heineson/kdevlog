import { useEffect, useState } from 'react';
import { Button, FormControl, Icon, List, ListItem } from '@chakra-ui/react';
import { useForm } from 'react-hook-form';
import { FiFile } from 'react-icons/fi';
import FileUpload from './components/FileUpload';

type FormValues = {
  file_: FileList;
};

const AddSourcesForm = () => {
  const {
    register,
    handleSubmit,
    reset,
    watch,
    formState: { errors },
  } = useForm<FormValues>();

  const [files, setFiles] = useState<File[]>([]);

  useEffect(() => {
    const subscription = watch((value) => {
      if (!value.file_) {
        setFiles([]);
      } else {
        setFiles((prev) => prev.concat(Array.from(value.file_)));
      }
    });
    return () => subscription.unsubscribe();
  }, [watch]);

  const onSubmit = handleSubmit(() => {
    console.log('On Submit: ', files);
    reset();
  });

  const renderListItem = (file: File) => {
    return <ListItem key={file.name}>{file.name}</ListItem>;
  };

  return (
    <form onSubmit={onSubmit}>
      {files.length > 0 && <List>{files.map((value) => renderListItem(value))}</List>}

      <FormControl isInvalid={!!errors.file_}>
        <FileUpload accept={'text/*'} register={register(`file_`)}>
          <Button variant={'outline'} leftIcon={<Icon as={FiFile} />} isFullWidth={true}>
            {files.length > 0 ? 'Add another' : 'Add file'}
          </Button>
        </FileUpload>
      </FormControl>

      <Button mt={2} colorScheme={'blue'} type={'submit'} isFullWidth={true} disabled={files.length == 0}>
        {files.length > 1 ? 'Upload file' : 'Upload files'}
      </Button>
    </form>
  );
};

export default AddSourcesForm;
