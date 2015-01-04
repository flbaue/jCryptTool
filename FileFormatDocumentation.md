# File Format Documentation #


## Current Development Version ##
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

### License ###
Copyright 2015 Florian Bauer, https://github.com/flbaue

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
