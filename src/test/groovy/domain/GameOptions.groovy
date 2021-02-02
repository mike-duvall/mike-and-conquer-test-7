package domain

class GameOptions {

    boolean drawShroud
    float initialMapZoom
    int gameSpeedDelayDivisor


    GameOptions(boolean drawShroud, float initialMapZoom, int gameSpeedDelayDivisor) {
        this.drawShroud = drawShroud
        this.initialMapZoom = initialMapZoom
        this.gameSpeedDelayDivisor = gameSpeedDelayDivisor
   }

    GameOptions() {}
}
