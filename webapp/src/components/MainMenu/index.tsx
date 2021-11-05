import { PropsWithChildren, ReactNode } from 'react';
import {
  Box,
  Flex,
  HStack,
  Icon,
  IconButton,
  Link,
  Popover,
  PopoverContent,
  PopoverTrigger,
  Stack,
  useColorModeValue,
  useDisclosure,
} from '@chakra-ui/react';
import { MdAdd, MdClose, MdMenu } from 'react-icons/md';
import AddSourcesForm from '../AddSourcesForm';

const Links = ['Dashboard', 'Projects', 'Team'];

const NavLink = ({ children }: { children: ReactNode }) => (
  <Link
    px={2}
    py={1}
    rounded={'md'}
    _hover={{
      textDecoration: 'none',
      bg: useColorModeValue('gray.200', 'gray.700'),
    }}
    href={'#'}
  >
    {children}
  </Link>
);

function MainMenu({ children }: PropsWithChildren<unknown>) {
  const { isOpen, onOpen, onClose } = useDisclosure();

  return (
    <>
      <Box bg={useColorModeValue('gray.100', 'gray.900')} px={4}>
        <Flex h={16} alignItems={'center'} justifyContent={'space-between'}>
          <IconButton
            size={'md'}
            icon={isOpen ? <Icon as={MdClose} w={6} h={6} /> : <Icon as={MdMenu} w={6} h={6} />}
            aria-label={'Open Menu'}
            display={{ md: 'none' }}
            onClick={isOpen ? onClose : onOpen}
          />
          <HStack spacing={8} alignItems={'center'}>
            <Popover>
              <PopoverTrigger>
                <IconButton
                  colorScheme={'blue'}
                  size={'sm'}
                  icon={<Icon as={MdAdd} w={6} h={6} />}
                  aria-label={'Add source'}
                  title={'Add source'}
                />
              </PopoverTrigger>
              <PopoverContent p={3}>
                <AddSourcesForm />
              </PopoverContent>
            </Popover>
            <HStack as={'nav'} spacing={4} display={{ base: 'none', md: 'flex' }}>
              {Links.map((link) => (
                <NavLink key={link}>{link}</NavLink>
              ))}
            </HStack>
          </HStack>
        </Flex>

        {isOpen ? (
          <Box pb={4} display={{ md: 'none' }}>
            <Stack as={'nav'} spacing={4}>
              {Links.map((link) => (
                <NavLink key={link}>{link}</NavLink>
              ))}
            </Stack>
          </Box>
        ) : null}
      </Box>

      <Box>{children}</Box>
    </>
  );
}

export default MainMenu;
