[TOC]

- just in develop do not use!

# MDL Android WebSocket

- this library is for Android WebSocket client
- use single instance in reducing memory usage

> version 0.0.1 method count 

Less Runtime :
- minSdkVersion 15
- gradle or maven
- jar [Download just like this Path](https://github.com/MDL-Sinlov/MDL_Android_Updater/raw/master/mvn-repo/mdl/sinlov)

> eclipse just use every repo at version `-x.x.x-jarLib.zip`

Project Runtime:
- Android Studio 2.1.2
- appcompat-v7:24.1.1
- Gradle 2.10
- com.android.tools.build:gradle:2.1.2

# Last Update Info

- version 0.0.1



# Dependency

at root project `build.gradle`

```gradle
repositories {
    maven {
        url 'https://raw.githubusercontent.com/MDL-Sinlov/MDL_Android_WebSocket/master/mvn-repo/'
    }
    jcenter()
    ...
}
```

in module `build.gradle`

```gradle
dependencies {
    compile 'mdl.sinlov.android:websocket:0.0.1'
}
```

# Usage

## base use



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