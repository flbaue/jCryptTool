# File Format Doc #


## Current Version ##
The file starts with the initialization block which has a length of 32 byte. It consists of two parts. The first part is the salt which has a length of 16 byte. It is followed by the initialization vector (iv) which has a length of 16 byte as well. After the initialization block the encrypted data begins. The data itself is first compressed with gzip and than encrypted.

Illustration:

```
[byte_1, byte_2, byte_3, ... , byte_32, byte_33, ...]
 ------------------------------------   ------------
           initBlock (32byte)           encrypted data
  ----------------  ----------------    ------------
   salt (16 byte)     iv (16 byte)          gzip
```

## Version 0.1 ##
Plain Java byte stream that is first compressed with GZIP and than encrypted.
