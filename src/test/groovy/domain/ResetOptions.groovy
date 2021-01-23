package domain

class ResetOptions {

    boolean drawShroud
    float initialMapZoom
    int gameSpeedDelayDivisor


    ResetOptions(boolean drawShroud, float initialMapZoom, int gameSpeedDelayDivisor) {
        this.drawShroud = drawShroud
        this.initialMapZoom = initialMapZoom
        this.gameSpeedDelayDivisor = gameSpeedDelayDivisor
   }

    ResetOptions() {}
}
