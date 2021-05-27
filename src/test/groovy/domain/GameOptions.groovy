package domain

class GameOptions {

    boolean drawShroud
    float initialMapZoom
    String gameSpeed



    GameOptions(boolean drawShroud, float initialMapZoom, String gameSpeed ) {
        this.drawShroud = drawShroud
        this.initialMapZoom = initialMapZoom
//        this.gameSpeedDelayDivisor = gameSpeedDelayDivisor
        this.gameSpeed = gameSpeed
   }

    GameOptions() {}
}
