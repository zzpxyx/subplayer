SubPlayer
=========
[![Build Status](https://travis-ci.org/zzpxyx/subplayer.svg?branch=master)](https://travis-ci.org/zzpxyx/subplayer)

A simple subtitle player.

## Notice
This application is under heavy development. Currently, it is only a working prototype with many limitations such as:

- Rudimentary user interface.
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

- Open: open a subtitle file. Shortcut key "O".
- Play/Pause: play or pause the playing. Shortcut key "Space".
- Stop: stop the playing. Shortcut key "S".
- Backward: jump back 50 ms so that the next subtitle is delayed 50 ms. Shortcut key "B".
- Forward: jump ahead 50 ms so that the next subtitle appears 50 ms earlier. Shortcut key "F".
- Previous: jump back to the previous subtitle. Shortcut key "P".
- Next: jump ahead to the next subtitle. Shortcut key "N".
- Decrease speed: decrease the playing speed by 2%. Shortcut key "D".
- Increase speed: increase the playing speed by 2%. Shortcut key "I".
- Exit: exit the application. Shortcut key "ESC".

Double clicking on the black displaying area will hide and show the buttons. Shortcut key "H".

Mouse dragging the black displaying area will move the entire application window. 

## LICENSE
See file LICENSE.
