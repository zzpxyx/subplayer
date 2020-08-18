SubPlayer
=========
[![Build Status](https://travis-ci.org/zzpxyx/subplayer.svg?branch=master)](https://travis-ci.org/zzpxyx/subplayer)

A simple subtitle player.

## Build
The [project's release page on GitHub](https://github.com/zzpxyx/subplayer/releases) has pre-built packages.

Building from source requires JDK 1.8 and Apache Maven 3:

``` bash
mvn package
```

## Run
Running requires JRE 1.8:

``` bash
java -jar path/to/subplayer/file.jar
```

Depending on your system, you may be able to double click on the jar file and launch the application immediately.

## Usage
From left to right, the buttons on the user interface are:

- Open: open a subtitle file. Shortcut key "O".
- Exit: exit the application. Shortcut key "ESC".
- Play/Pause: play or pause the playing. Shortcut key "Space".
- Stop: stop the playing. Shortcut key "S".
- Backward: jump back 50ms so that the next subtitle is delayed 50ms. Shortcut key "B".
- Forward: jump ahead 50ms so that the next subtitle appears 50ms earlier. Shortcut key "F".
- Previous: jump back to the previous subtitle. Shortcut key "P".
- Next: jump ahead to the next subtitle. Shortcut key "N".

The spinner shows the play speed. By default, it will show "100%". The up and down arrow buttons (shortcut keys "I" and "D") near it can increase or decrease the play speed by 2% each click.

Clicking on the seek bar will jump to the subtitle at that time mark. 

Double clicking on the black displaying area will make the window transparent. Double click again to restore. Shortcut key "H".

Mouse dragging the black displaying area will move the entire application window.

## Settings
A limited set of user settings are supported. All user settings should be in the form of `key=value` and put into a file with the name `config.properties` in the same folder as the application's jar file.

Available user setting keys:

- `WindowWidth`: width of the application window. Default to 80% of the screen width.
- `WindowHeight`: height of the application window. Default to 150 pixels.
- `WindowXPosition`: horizontal position of the application window. Default to 10% of the screen width.
- `WindowYPosition`: vertical position of the application window. Default to screen height minus 150 pixels.
- `FontSize`: subtitle font size. Default to 40 point.

All numerical values should be set without units like "px" or "pt".

Note that the default values will make the application window centered at the bottom of the screen.

## LICENSE
See file LICENSE.
