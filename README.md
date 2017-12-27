SubPlayer
=========
[![Build Status](https://travis-ci.org/zzpxyx/subplayer.svg?branch=master)](https://travis-ci.org/zzpxyx/subplayer)

A simple subtitle player.

## Notice
This application is under heavy development. Currently, it is only a working prototype with many limitations such as:

- Rudimentary user interface.
- Only SRT subtitle files are supported.
- No user settings.

## Build
The [project's release page on GitHub](https://github.com/zzpxyx/subplayer/releases) has pre-built packages.

Building from source requires JDK 1.8 and Apache Maven 3:

``` bash
mvn package
```

## Run
Running requires JRE 1.8:

``` bash
java -jar <path_to_subplayer_jar_file>
```

## Usage
From left to right, the buttons on the user interface are:

- Open: open an SRT subtitle file. Shortcut key "O".
- Previous: jump back to the previous subtitle. Shortcut key "P".
- Backward: jump back 50 ms so that the next subtitle is delayed 50 ms. Shortcut key "B".
- Play/Pause: play or pause the playing. Shortcut key "Space".
- Stop: stop the playing. Shortcut key "S".
- Forward: jump ahead 50 ms so that the next subtitle appears 50 ms earlier. Shortcut key "F".
- Next: jump ahead to the next subtitle. Shortcut key "N".

Double clicking on the black displaying area will hide and show the buttons. Shortcut key "H".

Mouse dragging the black displaying area will move the entire application window. 

Currently, there is no "exit" button. Please use your operating system's window close function or press ALT+F4 in most cases.


## LICENSE
See file LICENSE.
