# Server Proxy HTTP.

A proxy for Orwell's systems written in Clojure.

## Usage

Before installing the server you need to install the LibZMQ runtime and the JZMQ library:
```bash
$ apt-get install libzmq3-dev
$ git clone http://github.com/zeromq/jzmq.git jzmq-build
$ cd jzmq-build
$ ./autogen.sh
$ ./configure --prefix=/opt/jzmq
$ make
$ make install
```

After that, we need to patch the protobuf definitions in order to have the Reflection
activated
```bash
$ git submodule update --init
$ cd resources/messages
$ patch -p1 < ../../messages.patch
```

Check that the tests are passing by running the following command in the project's root
```bash
$ lein deps
$ lein test-all
```


If everything is ok you can start sending messages to the server, provided that you have
a running instance of it.

```bash
$ lein ring server-headless 9090
$ curl http://localhost:9090/router/Hello?name=Damien
{"tag":"Welcome",
 "message":[{
    "robot":"dandi",
    "team":"red",
    "id":"robot_0",
    "video-address":
    "localhost",
    "video-port":147808239}]}
```

## License

FreeBSD license

```license
Copyright (c) 2014, Orwell development group.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer. 
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those
of the authors and should not be interpreted as representing official policies, 
either expressed or implied, of the FreeBSD Project.
```
