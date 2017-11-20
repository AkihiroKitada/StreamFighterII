# Stream Fighter II
Sample implementation to deliver "Shoryuken" and other Ryu's deathblows based on JavaFX and Reactor, powered by Reactive Streams/Extensions technology.

## Regarding each classes

* quitada.RyuWithoutReactiveStreamsV2 - This is a class without Reactive Streams. It deliver dethblows with driven by key events.
* quitada.RyuWithReactiveStreamsV2 - This is a class with Reactive Streams, based on Reactor's Flunx and FluxSink. It deliver dethblows with driven by Flux.subscribe until the buffered data strem based on key events is flahsed by pushing punch key or kick key.

## Operation

* Direcion key: up - "W", down - "Z", left - "A", right - "S"
* Puch key: "X"
* Kick key: "C"
* Syoruken command: right -> donw -> [right + down with punch]
* Hadouken command: down -> [down + right] -> [right with punch]
* Tatsumaki Senpukyaku command: down -> [down + left] -> [left with kick] 

## Prepartion to run this appliction

You have to prepare additional gif files and mp3 files to run this application becasue they are grabbed from other public site (they are not mine). Please get those files according to the instruction below and put them under /src/main/resources/quitada.


