# Kothonen

![](img/som3d.gif)

This repository contains an implementation of a Self Organizing Map
(SOM) and some examples. The core implementation of the 
SOM can be found 
[here](https://github.com/TomasVolker/kothonen/blob/master/src/main/kotlin/volkerandreasen/som/SelfOrganizingMap.kt).

## Build instructions

To compile and run the project, run the `build` task with
the gradle wrapper (no need to install gradle):
```
./gradlew build
```

This will compile the self contained executable `kothonen-1.0.jar`,
to execute use:
```
java -jar kothonen-1.0.jar
```

