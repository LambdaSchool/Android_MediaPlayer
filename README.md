# Android Media Player

For this app, you'll build a simple media player app which will allow a user to retrieve audio files and play them.

## Requirements

- Add a button to post an intent to request a file. 

  > You can use `.setType("audio/*")` to specify which type of file to return

- Use `MediaPlayer` or `VideoView` to play the files as required

- Allow for basic controls:

  - Play/Pause
  - Display progress in track with a  `SeekBar`
  - Allow user to use the `SeekBar` to move to different points in the track

- To call `mediaPlayer.release()` when you activity is stopped and create a new one in `onStart`

#### Go Further

There are a few additional features which you can include in order to improve your app

- Allow users to select either audio or video files and play each one accordingly
  > Use `setType("video/*")` to retreive a video
- Save each track persistently with the last played position to allow the user to pick up where they left off
- When tracks are added, add them to a list and allow the user to select previous tracks from that list
- Instead of an intent to retrieve the images, use that intent to request a directory using `setType("file/*")` then step through that directory to add all media files inside of it to your list.
