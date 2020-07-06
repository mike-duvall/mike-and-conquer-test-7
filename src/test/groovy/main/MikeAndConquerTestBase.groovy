package main

import client.MikeAndConquerGameClient
import domain.MCV
import domain.Point
import domain.Sidebar
import spock.lang.Specification
import spock.util.concurrent.PollingConditions
import util.ImageUtil

import javax.imageio.ImageIO
import java.awt.image.BufferedImage


class MikeAndConquerTestBase extends Specification {

    MikeAndConquerGameClient gameClient

    def setup() {
        String localhost = "localhost"
        String remoteHost = "192.168.0.146"
//        String host = localhost
        String host = remoteHost

        int port = 11369
        boolean useTimeouts = true
//        boolean useTimeouts = false
        gameClient = new MikeAndConquerGameClient(host, port, useTimeouts )
        gameClient.resetGame()
        gameClient.leftClickInWorldCoordinates(1,1)  // to get mouse clicks in default state
    }




    void assertScreenshotMatches(String scenarioPrefix, int testScenarioNumber, int startX, int startY, int screenshotCompareWidth, int screenshotCompareHeight) {

        // Move cursor so it's not in the screenshot
        gameClient.moveMouseToWorldCoordinates(new Point(startX + screenshotCompareWidth + 50,startY + screenshotCompareHeight + 50))

        String realGameFilename = "real-game-" + scenarioPrefix + "-" + testScenarioNumber + "-start-x" + startX + "-y" + startY + "-" + screenshotCompareWidth + "x" + screenshotCompareHeight + ".png"

        File imageFile = new File(
                getClass().getClassLoader().getResource(realGameFilename).getFile()
        );

        BufferedImage realGameScreenshot = ImageIO.read(imageFile)

        BufferedImage fullScreenShot = gameClient.getScreenshot()
        BufferedImage screenshotSubImage = fullScreenShot.getSubimage(startX,startY,screenshotCompareWidth,screenshotCompareHeight)

        String realGameCopiedFilename = realGameFilename.replaceAll("real-game", "copied-real-game")
        String mikeAndConquerCopiedFilename = realGameFilename.replaceAll("real-game", "actual-mike-and-conquer")

        writeImageToFileInBuildDirectory(realGameScreenshot, realGameCopiedFilename )
        writeImageToFileInBuildDirectory(screenshotSubImage, mikeAndConquerCopiedFilename )

        assert ImageUtil.imagesAreEqual(screenshotSubImage, realGameScreenshot)
    }


    void assertScreenshotMatches(int testScenarioNumber, int startX, int startY, int screenshotCompareWidth, int screenshotCompareHeight) {
        assertScreenshotMatches('shroud', testScenarioNumber,startX,startY,screenshotCompareWidth, screenshotCompareHeight)
    }

    void writeImageToFileInBuildDirectory(BufferedImage bufferedImage, String fileName) {
        String relPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        File targetDir = new File(relPath+"../../../../build/screenshot")
        // TODO:  come up with more reliable way to find this path
        // Seems to work differently between IntelliJ versions
//        File targetDir = new File(relPath+"../../../build/screenshot")
        if(!targetDir.exists()) {
            targetDir.mkdir();
        }
        String absPath = targetDir.getAbsolutePath()
        File outputfile = new File(absPath + "\\" + fileName);
        ImageIO.write(bufferedImage, "png", outputfile);
    }


    def assertMCVIsAtDesignatedDestination(MCV mcv, int destinationX, int destinationY)
    {
        int leeway = 15
        assert (mcv.x >= destinationX - leeway) && (mcv.x <= destinationX + leeway)
        assert (mcv.y >= destinationY - leeway) && (mcv.y <= destinationY + leeway)
    }


    def assertBarracksIsBuilding() {
        def conditions = new PollingConditions(timeout: 80, initialDelay: 1.5, factor: 1.25)
        conditions.eventually {
            Sidebar sidebar = gameClient.getSidebar()
            assert sidebar.barracksIsBuilding == true
        }
        return true
    }

    def assertBarracksIsReadyToPlace() {
        def conditions = new PollingConditions(timeout: 80, initialDelay: 1.5, factor: 1.25)
        conditions.eventually {
            Sidebar sidebar = gameClient.getSidebar()
            assert sidebar.barracksReadyToPlace == true
        }
        return true

    }



}