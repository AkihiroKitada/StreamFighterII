# Stream Fighter II
Sample implementation to deliver "Shoryuken" and other Ryu's deathblows based on JavaFX and Reactor, powered by Reactive Streams/Extensions technology.

## Regarding each classes

* quitada.RyuWithoutReactiveStreamsV2 - This is a class without Reactive Streams. It deliver dethblows with driven by key events.
* quitada.RyuWithReactiveStreamsV2 - This is a class with Reactive Streams, based on Reactor's Flunx and FluxSink. It deliver dethblows with driven by Flux.subscribe until the buffered data strem based on key events is flahsed by pushing punch key or kick key.
* quitada.RyuWithReactiveStreamsV3 - This is a class with Reactive Streams, based on Reactor's Flunx and FluxSink. It deliver dethblows with driven by Flux.subscribe until the buffered data strem based on key events is flahsed by pushing punch key or kick key, with more sophisticated animation compared with quitada.RyuWithReactiveStreamsV2 (adding animations for going back and forward).

## Operation

* Direcion key: up - "W", down - "Z", left - "A", right - "S"
  * [V3 only] going back - keep pressing "A", going forward - keep pressing "S"
* Puch key: "X"
* Kick key: "C"
* Syoruken command: right -> donw -> [right + down with punch]
* Hadouken command: down -> [down + right] -> [right with punch]
* Tatsumaki-senpukyaku command: down -> [down + left] -> [left with kick] 

## Prepartion to run this appliction

You have to prepare additional gif files and mp3 files to run this application becasue they are grabbed from other public site (they are not mine). Please get those files according to the instruction below and put them under /src/main/resources/quitada.

* Sound files for each actions and back ground music (for Ryu stage)
  * Shoryuken: get a mp3 file from the following site and save as "shouryuuken.mp3".
    * https://www.zedge.net/ringtone/1454346/
  * Hadouken: get a mp3 file from the following site and save as "hadouken.mp3".
    * https://www.zedge.net/ringtone/1309265/
  * Tatsumaki-senpukyaku: get a mp3 file from the following site and save as "tatsumaki_senpukyaku.mp3".
    * https://www.zedge.net/ringtone/316537/
  * Punch: get a mp3 file from the following site and save as "punch.mp3".
    * https://www.freesoundeffects.com/free-track/punch2-426857/
  * Kick: get a mp3 file from the following site and save as "kick.mp3".
    * https://www.freesoundeffects.com/free-track/punch-426855/
  * Back-ground music: get a mp3 file from the following site and save as "ryu-stage.mp3".
    * https://downloads.khinsider.com/game-soundtracks/album/street-fighter-2-turbo/09.-ryu-stage.mp3
    
* Gif files for each actions' animation
  * As far as I know, JavaFX can't recognize animated gif files properly in terms of viewing as animated gif files. So, I grabbed some animated gif files for Ryu's action and divided each animated gif files into several static gif files with using the following kind of web services.
    * https://ja.bloggif.com/gif-extract 
  * Shoryuken: get an animated gif file at the middle of upper pane from the following site, divide it into 17 static gif files and save each files like ryu-syoryuken-0.gif, ryu-syoryuken-1.gif, ... , ryu-syoryuken-16.gif in order.
    * http://www.fightersgeneration.com/characters3/ryu-a3.html
  * Hadouken: get an animated gif file at the left side of upper pane from the following site, divide it into 14 static gif files and save each files like ryu-hadou-0.gif, ryu-hadou-1.gif, ... , ryu-hadou-13.gif in order.
    * http://www.fightersgeneration.com/characters3/ryu-a2.html
  * Tatsumaki-senpukyaku: get an animated gif file at the left side of upper pane from the following site, divide it into 27 static gif files and save each files like ryu-tasumaki-0.gif, ryu-tasumaki-1.gif, ... , ryu-tasumaki-26.gif in order.
    * http://www.fightersgeneration.com/characters3/ryu-a10.html
  * Punch: get an animated gif file at the left side of second upper pane from the following site, divide it into 8 static gif files and save each files like ryu-punch-0.gif, ryu-punch-1.gif, ... , ryu-punch-7.gif in order.
    * http://www.fightersgeneration.com/characters3/ryu-a4.html
  * Kick: get an animated gif file at the left side of upper pane from the following site, divide it into 15 static gif files and save each files like ryu-kick-0.gif, ryu-kick-1.gif, ... , ryu-kick-14.gif in order.
    * http://www.fightersgeneration.com/characters3/ryu-a4.html
  * Going forward: get an animated gif file at the left side of second pane from the following site, divide it into 11 static gif files and save each files like ryu-walkf-0.gif, ryu-walkf-1.gif, ... , ryu-walkf-10.gif in order. This is only used for quitada.RyuWithReactiveStreamsV3.
    * http://www.fightersgeneration.com/characters3/ryu-a.html
  * Going back: get an animated gif file at the left side of third pane from the following site, divide it into 11 static gif files and save each files like ryu-walkb-0.gif, ryu-walkb-1.gif, ... , ryu-walkb-10.gif in order. This is only used for quitada.RyuWithReactiveStreamsV3.
    * http://www.fightersgeneration.com/characters3/ryu-a.html
  * Kamae (default action): get an animated gif file at the second left side of upper pane from the following site, divide it into 10 static gif files and save each files like ryu-kamae-0.gif, ryu-kamae-1.gif, ... , ryu-kamae-9.gif in order.
    * http://www.fightersgeneration.com/characters3/ryu-a.html

## Run this appliction

Please just execute quitada.RyuWithoutReactiveStreamsV2 or quitada.RyuWithReactiveStreamsV2 or quitada.RyuWithReactiveStreamsV3 from your favorite Java IDE or console.