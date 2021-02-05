package main


import domain.Point
import util.ImageUtil

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

class ScreenshotTests extends MikeAndConquerTestBase {


    def setup() {
        boolean showShroud = false
        float initialMapZoom = 1
        int gameSpeedDelayDivisor = 50
        setAndAssertGameOptions(showShroud, initialMapZoom, gameSpeedDelayDivisor)
    }



    def "top left corner of screenshot of game should match equivalent reference screenshot of real command and conquer" () {

        given:
        int screenshotCompareWidth = 162
        int screenshotCompareHeight = 218

        // move mouse out of screenshot
        gameClient.moveMouseToWorldCoordinates(new Point(screenshotCompareWidth + 50,screenshotCompareHeight + 50))

        File imageFile = new File(
                getClass().getClassLoader().getResource("real-game-162x218-manual.png").getFile()
        );
        BufferedImage realGameScreenshot = ImageIO.read(imageFile)


        when:
        BufferedImage fullScreenShot = gameClient.getScreenshot()

        then:
        BufferedImage screenshotSubImage = fullScreenShot.getSubimage(0,0,162,218)

        writeImageToFileInBuildDirectory(screenshotSubImage, "mike-and-conquer-screenshot.jpg" )
        writeImageToFileInBuildDirectory(realGameScreenshot, "real-game-reference-screenshot.jpg" )

        and:
        assert ImageUtil.imagesAreEqual(screenshotSubImage, realGameScreenshot)

    }

    def "screenshot with trees" () {

        given:
        int screenshotCompareWidth = 480
        int screenshotCompareHeight = 302

        // move mouse out of screenshot
        gameClient.moveMouseToWorldCoordinates(new Point(screenshotCompareWidth + 50,screenshotCompareHeight + 50))

        File imageFile = new File(
                getClass().getClassLoader().getResource("real-game-480x302-manual.png").getFile()
        );
        BufferedImage realGameScreenshot = ImageIO.read(imageFile)


        when:
        BufferedImage fullScreenShot = gameClient.getScreenshot()

        then:
        BufferedImage screenshotSubImage = fullScreenShot.getSubimage(0,0,screenshotCompareWidth,screenshotCompareHeight)

        writeImageToFileInBuildDirectory(screenshotSubImage, "mike-and-conquer-screenshot-480x302.jpg" )
        writeImageToFileInBuildDirectory(realGameScreenshot, "real-game-reference-screenshot-480x302.jpg" )

        and:
        assert ImageUtil.imagesAreEqual(screenshotSubImage, realGameScreenshot)

    }


}