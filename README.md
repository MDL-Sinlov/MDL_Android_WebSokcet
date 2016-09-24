[TOC]

# MDL Android WebSocket

- this library is for Android WebSocket client
- use single instance in reducing memory usage
- callback use safe Handler
- thread safe
- use `org.apache.http.legacy`

~~> version 0.0.2 method count~~

Less Runtime :
- minSdkVersion 15
- gradle or maven

# Last Update Info

- Use Release https://github.com/MDL-Sinlov/MDL_Android_WebSokcet/releases
- aar [Download just like this Path](https://github.com/MDL-Sinlov/MDL-Android-Repo/raw/master/mvn-repo/mdl/sinlov/android/websocket/0.0.2/websocket-0.0.2.aar)
- version 0.0.2
    change wsHost set, must use full URL

# TODO

- cookie set more head
- ping pong send
- auto relink service

# Dependency

at root project `build.gradle`

```gradle
repositories {
    maven {
        url 'https://raw.githubusercontent.com/MDL-Sinlov/MDL-Android-Repo/master/mvn-repo/'
    }
    jcenter()
    ...
}
```

in module `build.gradle`

```gradle
dependencies {
    compile 'mdl.sinlov.android:websocket:0.0.2'
}
```


# Usage

- version websocket:0.0.2

## Params

Link in cookies

|Key|Desc|
|---|---|
|session|this client session id|


## base use

test server use http://www.blue-zero.com/WebSocket/

- init WebSocketClient before use

```java
String sessionId = "1234567890";
String wsHost = "ws://127.0.0.1:18080";
WebSocketEngine.getInstance().initClient(sessionId, wsHost);
```

- create WebSocketListener

```java
    private class MyWebSocketListener implements WebSocketListener {
        @Override
        public void onConnect() {
            ALog.d("onConnect" + "server: " + wsHost + " is connect!");
        }

        @Override
        public void onMessage(String message) {
            ALog.i(message);
        }

        @Override
        public void onMessage(byte[] data) {
            String message = MessageUtils.byteArray2String(data);
            ALog.i(message);
        }

        @Override
        public void onDisconnect(int code, String reason, Exception error) {
            ALog.d("onDisconnect" + "server: " + wsHost + " is disconnect!");
        }

        @Override
        public void onError(Exception error) {
            ALog.w("onError: " + error.getMessage());
        }
    }
```

`ALog` just use https://github.com/MDL-Sinlov/MDL_Android_Log

- in LifeCycle

```java
    @Override
    protected void onResume() {
        super.onResume();
        WebSocketEngine.getInstance().onWebSocketListener(new MyWebSocketListener());
        WebSocketEngine.getInstance().connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // this not necessary
        WebSocketEngine.getInstance().disconnect();
    }

```

- send support String or byte[]

```java
    WebSocketEngine.getInstance().send();
```


- MessageUtils

```java
    MessageUtils.byteArray2String();
    MessageUtils.string2ByteArray();
```


# Run Demo

Runtime info


Project Runtime:
- Android Studio 2.2
- appcompat-v7:23.4.0
- Gradle 2.14.2
- com.android.tools.build:gradle:2.2.0

This Demo use gradle res build so

input your websocket server info at [gradle.properties](gradle.properties)

```conf
ws_server_host=ws://115.29.193.48:8088
```

then build this app `Demo module at app`

#License

---

Copyright 2016 sinlovgm@gmail.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.