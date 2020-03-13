package main

import client.MikeAndConquerGameClient
import groovyx.net.http.HttpResponseException
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll
import spock.util.concurrent.PollingConditions

import javax.imageio.ImageIO
import java.awt.image.BufferedImage


class ShroudTests extends Specification {

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



    def "Shroud screenshot scenario 1"() {
        given:
        ResetOptions resetOptions = new ResetOptions(true)
        gameClient.resetGame(resetOptions)
        // Add bogus minigunner to not delete so game state stays in "Playing"
        gameClient.addGDIMinigunnerAtMapSquare(4,5)

        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when: "Test scenario 1"
        int testScenarioNumber = 1
        int startX = 408
        int startY = 129
        int screenshotCompareWidth = 232
        int screenshotCompareHeight = 159

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)
    }



    def "Shroud screenshot scenario 2"() {
        given:
        ResetOptions resetOptions = new ResetOptions(true)
        gameClient.resetGame(resetOptions)
        // Add bogus minigunner to not delete so game state stays in "Playing"
        gameClient.addGDIMinigunnerAtMapSquare(4,5)

        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when: "Test scenario 2"
        int testScenarioNumber = 2
        int startX = 408
        int startY = 129
        int screenshotCompareWidth = 232
        int screenshotCompareHeight = 159

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH]

        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)
    }



    def "Shroud screenshot scenario 3"() {
        given:
        ResetOptions resetOptions = new ResetOptions(true)
        gameClient.resetGame(resetOptions)
        gameClient.addGDIMinigunnerAtMapSquare(4,5)

        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:  "Test scenario 3"
        int testScenarioNumber = 3
        int startX = 408
        int startY = 129
        int screenshotCompareWidth = 232
        int screenshotCompareHeight = 159

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.EAST]
        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)
    }

    def "Shroud screenshot scenario 4"() {
        given:
        ResetOptions resetOptions = new ResetOptions(true)
        gameClient.resetGame(resetOptions)
        gameClient.addGDIMinigunnerAtMapSquare(4,5)

        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:  "Test scenario 4"
        int testScenarioNumber = 4
        int startX = 408
        int startY = 129
        int screenshotCompareWidth = 232
        int screenshotCompareHeight = 159

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.EAST, MovementDirection.WEST, MovementDirection.WEST]
        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)
    }

    def "Shroud screenshot scenario 5"() {
        given:
        ResetOptions resetOptions = new ResetOptions(true)
        gameClient.resetGame(resetOptions)
        gameClient.addGDIMinigunnerAtMapSquare(4,5)

        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:  "Test scenario 5"
        int testScenarioNumber = 5
        int startX = 503
        int startY = 158
        int screenshotCompareWidth = 145
        int screenshotCompareHeight = 130

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.EAST, MovementDirection.WEST, MovementDirection.WEST,
        MovementDirection.EAST, MovementDirection.EAST, MovementDirection.EAST]
        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)
    }


    def "Shroud screenshot scenario 6"() {
        given:
        ResetOptions resetOptions = new ResetOptions(true)
        gameClient.resetGame(resetOptions)
        gameClient.addGDIMinigunnerAtMapSquare(4,5)

        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:  "Test scenario 6"
        int testScenarioNumber = 6
        int startX = 527
        int startY = 167
        int screenshotCompareWidth = 121
        int screenshotCompareHeight = 121

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.EAST, MovementDirection.WEST, MovementDirection.WEST,
                         MovementDirection.EAST, MovementDirection.EAST, MovementDirection.EAST, MovementDirection.EAST]
        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)


    }


    def "Shroud screenshot scenario 7"() {
        given:
        ResetOptions resetOptions = new ResetOptions(true)
        gameClient.resetGame(resetOptions)
        gameClient.addGDIMinigunnerAtMapSquare(4,5)

        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:  "Test scenario 7"
        int testScenarioNumber = 7
        int startX = 507
        int startY = 165
        int screenshotCompareWidth = 141
        int screenshotCompareHeight = 123

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH,  MovementDirection.EAST,
                         MovementDirection.EAST, MovementDirection.EAST,MovementDirection.EAST]

        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)

    }

    def "Shroud screenshot scenario 8"() {
        given:
        ResetOptions resetOptions = new ResetOptions(true)
        gameClient.resetGame(resetOptions)
        gameClient.addGDIMinigunnerAtMapSquare(4,5)

        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:  "Test scenario 8"
        int testScenarioNumber = 8
        int startX = 532
        int startY = 170
        int screenshotCompareWidth = 116
        int screenshotCompareHeight = 149

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH,  MovementDirection.EAST,
                         MovementDirection.EAST, MovementDirection.EAST,MovementDirection.EAST, MovementDirection.EAST, MovementDirection.SOUTH]

        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)

    }



    def "Shroud screenshot scenario 9"() {
        given:
        ResetOptions resetOptions = new ResetOptions(true)
        gameClient.resetGame(resetOptions)
        gameClient.addGDIMinigunnerAtMapSquare(4,5)

        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:
        int testScenarioNumber = 9
        int startX = 519
        int startY = 72
        int screenshotCompareWidth = 129
        int screenshotCompareHeight = 121

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH,  MovementDirection.EAST,
                         MovementDirection.EAST, MovementDirection.EAST,MovementDirection.EAST, MovementDirection.EAST, MovementDirection.SOUTH,
        MovementDirection.NORTH, MovementDirection.NORTH,MovementDirection.NORTH,MovementDirection.NORTH,MovementDirection.NORTH,MovementDirection.WEST ]

        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)

    }


    def "Shroud screenshot scenario 10"() {
        given:
        ResetOptions resetOptions = new ResetOptions(true)
        gameClient.resetGame(resetOptions)
        gameClient.addGDIMinigunnerAtMapSquare(4,5)

        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:
        int testScenarioNumber = 10
        int startX = 525
        int startY = 67
        int screenshotCompareWidth = 123
        int screenshotCompareHeight = 102

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH,  MovementDirection.EAST,
                         MovementDirection.EAST, MovementDirection.EAST,MovementDirection.EAST, MovementDirection.EAST,
                         MovementDirection.NORTH,
                         MovementDirection.NORTH, MovementDirection.NORTH,MovementDirection.WEST, MovementDirection.WEST, MovementDirection.NORTH ]

        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)

    }

    def "Shroud screenshot scenario 11"() {
        given:
        ResetOptions resetOptions = new ResetOptions(true)
        gameClient.resetGame(resetOptions)
        gameClient.addGDIMinigunnerAtMapSquare(4,5)

        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:
        int testScenarioNumber = 11
        int startX = 310
        int startY = 92
        int screenshotCompareWidth = 157
        int screenshotCompareHeight = 183

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH,  MovementDirection.NORTH,
                         MovementDirection.WEST, MovementDirection.NORTH,MovementDirection.WEST, MovementDirection.NORTH,
                         MovementDirection.WEST, MovementDirection.WEST, MovementDirection.WEST,MovementDirection.WEST,
                         MovementDirection.SOUTH, MovementDirection.SOUTH ]

        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)
    }

    def "Shroud screenshot scenario 12"() {
        given:
        ResetOptions resetOptions = new ResetOptions(true)
        gameClient.resetGame(resetOptions)
        gameClient.addGDIMinigunnerAtMapSquare(4,5)

        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:
        int testScenarioNumber = 12
        int startX = 314
        int startY = 191
        int screenshotCompareWidth = 144
        int screenshotCompareHeight = 98

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH,  MovementDirection.NORTH,
                         MovementDirection.WEST, MovementDirection.NORTH,MovementDirection.WEST, MovementDirection.NORTH,
                         MovementDirection.WEST, MovementDirection.WEST, MovementDirection.WEST,MovementDirection.WEST,
                         MovementDirection.SOUTH, MovementDirection.SOUTH, MovementDirection.SOUTH ]

        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)
    }

    def "Shroud screenshot scenario 13"() {
        given:
        ResetOptions resetOptions = new ResetOptions(true)
        gameClient.resetGame(resetOptions)
        gameClient.addGDIMinigunnerAtMapSquare(4,5)

        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:
        int testScenarioNumber = 13
        int startX = 303
        int startY = 87
        int screenshotCompareWidth = 302
        int screenshotCompareHeight = 189

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH,  MovementDirection.NORTH,
                         MovementDirection.NORTHWEST, MovementDirection.NORTHWEST,
                         MovementDirection.WEST, MovementDirection.WEST, MovementDirection.WEST, MovementDirection.WEST,
                         MovementDirection.SOUTH ]

        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)
    }



    def "Shroud screenshot scenario 14"() {
        given:
        ResetOptions resetOptions = new ResetOptions(true)
        gameClient.resetGame(resetOptions)
        gameClient.addGDIMinigunnerAtMapSquare(4,5)

        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:
        int testScenarioNumber = 14
        int startX = 307
        int startY = 174
        int screenshotCompareWidth = 171
        int screenshotCompareHeight = 107

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH,  MovementDirection.NORTH,
                         MovementDirection.NORTHWEST, MovementDirection.NORTHWEST,
                         MovementDirection.WEST, MovementDirection.WEST, MovementDirection.WEST, MovementDirection.WEST,
                         MovementDirection.SOUTH, MovementDirection.SOUTH]

        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)
    }


    def "Shroud screenshot scenario 15"() {
        given:
        ResetOptions resetOptions = new ResetOptions(true)
        gameClient.resetGame(resetOptions)
        gameClient.addGDIMinigunnerAtMapSquare(4,5)

        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:
        int testScenarioNumber = 15
        int startX = 311
        int startY = 93
        int screenshotCompareWidth = 190
        int screenshotCompareHeight = 242

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH,  MovementDirection.NORTH,
                         MovementDirection.NORTHWEST, MovementDirection.NORTHWEST,
                         MovementDirection.WEST, MovementDirection.WEST, MovementDirection.WEST, MovementDirection.WEST,
                         MovementDirection.SOUTH, MovementDirection.SOUTH, MovementDirection.SOUTH]

        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)
    }


    private void doMinigunnerPlacements(Point mcvLocation, List<MovementDirection> movements) {
        Point currentLocation = mcvLocation
        movements.each { movementDirection ->
            if (movementDirection == MovementDirection.NORTH) {
                currentLocation.y--
            }
            else if (movementDirection == MovementDirection.NORTHEAST) {
                currentLocation.x++
                currentLocation.y--
            }
            else if (movementDirection == MovementDirection.EAST) {
                currentLocation.x++
            }
            else if (movementDirection == MovementDirection.SOUTHEAST) {
                currentLocation.x++
                currentLocation.y++
            }
            else if (movementDirection == MovementDirection.SOUTH) {
                currentLocation.y++
            }
            else if (movementDirection == MovementDirection.SOUTHWEST) {
                currentLocation.x--
                currentLocation.y++
            }
            else if (movementDirection == MovementDirection.WEST) {
                currentLocation.x--
            }
            else if (movementDirection == MovementDirection.NORTHWEST) {
                currentLocation.x--
                currentLocation.y--
            }


            Minigunner minigunner = gameClient.addGDIMinigunnerAtMapSquare(currentLocation.x, currentLocation.y)
            gameClient.deleteGdiMinigunnerById(minigunner.id)
        }
    }

    void assertScreenshotMatches(int testScenarioNumber, int startX, int startY, int screenshotCompareWidth, int screenshotCompareHeight) {

        gameClient.moveMouseToWorldCoordinates(new Point(startX + screenshotCompareWidth + 50,startY + screenshotCompareHeight + 50))

        String realGameFilename = "real-game-shroud-" + testScenarioNumber + "-start-x" + startX + "-y" + startY + "-" + screenshotCompareWidth + "x" + screenshotCompareHeight + ".png"

        File imageFile = new File(
                getClass().getClassLoader().getResource(realGameFilename).getFile()
        );

        BufferedImage realGameScreenshot = ImageIO.read(imageFile)

        BufferedImage fullScreenShot = gameClient.getScreenshot()
        BufferedImage screenshotSubImage = fullScreenShot.getSubimage(startX,startY,screenshotCompareWidth,screenshotCompareHeight)

        String realGameCopiedFilename = realGameFilename.replaceAll("real-game-shroud", "real-game-shroud-copied")
        String mikeAndConquerCopiedFilename = realGameFilename.replaceAll("real-game-shroud", "mike-and-conquer-shroud-actual")

        writeImageToFileInBuildDirectory(realGameScreenshot, realGameCopiedFilename )
        writeImageToFileInBuildDirectory(screenshotSubImage, mikeAndConquerCopiedFilename )

        assert ImageUtil.imagesAreEqual(screenshotSubImage, realGameScreenshot)


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




    def assertMinigunnerIsAtScreenPosition(Minigunner minigunner, int screenX, int screenY)
    {
        int leeway = 4
        assert (minigunner.screenX >= screenX - leeway) && (minigunner.screenX <= screenX + leeway)
        assert (minigunner.screenY >= screenY - leeway) && (minigunner.screenY <= screenY + leeway)
    }

    def assertMinigunnerIsAtDestination(Minigunner minigunner)
    {
        int leeway = 4
        assert (minigunner.x >= minigunner.destinationX - leeway) && (minigunner.x <= minigunner.destinationX + leeway)
        assert (minigunner.y >= minigunner.destinationY - leeway) && (minigunner.y <= minigunner.destinationY + leeway)
    }

    def assertMinigunnerIsAtDesignatedDestinationInMapSquareCoordinates(Minigunner minigunner,int mapSquareX, int mapSquareY)
    {
        Point worldCoordinates = Util.convertWorldCoordinatesToMapSquareCoordinates(mapSquareX, mapSquareY)

        int destinationX = worldCoordinates.x
        int destinationY = worldCoordinates.y
        int leeway = 15
        assert (minigunner.x >= destinationX - leeway) && (minigunner.x <= destinationX + leeway)
        assert (minigunner.y >= destinationY - leeway) && (minigunner.y <= destinationY + leeway)
    }



    def assertMinigunnerIsAtDesignatedDestination(Minigunner minigunner,int destinationX, int destinationY)
    {
        int leeway = 15
        assert (minigunner.x >= destinationX - leeway) && (minigunner.x <= destinationX + leeway)
        assert (minigunner.y >= destinationY - leeway) && (minigunner.y <= destinationY + leeway)
    }



    def assertNodMinigunnerDies(int id) {
        def conditions = new PollingConditions(timeout: 80, initialDelay: 1.5, factor: 1.25)
        conditions.eventually {
            def expectedDeadMinigunner = gameClient.getNodMinigunnerById(id)
            assert expectedDeadMinigunner.health == 0
        }
        return true
    }


    def assertGdiMinigunnerDies(int id) {
        def conditions = new PollingConditions(timeout: 80, initialDelay: 1.5, factor: 1.25)
        conditions.eventually {
            def expectedDeadMinigunner = gameClient.getGdiMinigunnerById(id)
            assert expectedDeadMinigunner.health == 0
        }
        return true
    }


    def assertGameStateGoesToMissionFailed() {
        String expectedGameState = "Mission Failed"

        def conditions = new PollingConditions(timeout: 10, initialDelay: 1.5, factor: 1.25)
        conditions.eventually {
            String gameState = gameClient.getGameState()
            assert gameState == expectedGameState
        }

        return true
    }


    def assertGameStateGoesToGameOver() {
        String expectedGameState = "Game Over"

        def conditions = new PollingConditions(timeout: 10, initialDelay: 1.5, factor: 1.25)
        conditions.eventually {
            String gameState = gameClient.getGameState()
            assert gameState == expectedGameState
        }

        return true
    }


    Point createRandomMinigunnerPosition()
    {
        Random rand = new Random()

        int minX = 10
        int minY = 10


        // Capping max so it will fit on screen
        int maxX = 600
        int maxY = 400

        int randomX = rand.nextInt(maxX) + minX
        int randomY = rand.nextInt(maxY) + minY

        Point point = new Point()
        point.x = randomX
        point.y = randomY
        return point

    }

    Minigunner createRandomGDIMinigunner() {
        int numTiesTried = 0
        int maxTimesToTry = 10

        while(true) {
            try {
               Point randomPosition = createRandomMinigunnerPosition()
               return gameClient.addGDIMinigunnerAtWorldCoordinates(randomPosition.x, randomPosition.y)
            }
            catch(HttpResponseException e) {
                if(numTiesTried < maxTimesToTry) {
                    if (e.response.responseData["Message"] == "Cannot create on blocking terrain") {
                        numTiesTried++
                    }
                    print e
                    println "::" + e.response.responseData["Message"]
                }
                else {
                    print e
                    println "::" + e.response.responseData["Message"]
                    throw e
                }
            }
        }

    }

    Minigunner createGDIMinigunnerAtLocation(int x, int y) {
        try {
            Point position = new Point(x,y)
            return gameClient.addGDIMinigunnerAtWorldCoordinates(position.x, position.y)
        }
        catch(HttpResponseException e) {
            print e
            println "::" + e.response.responseData["Message"]
            throw e
        }
    }

    Minigunner createNodMinigunnerAtLocation(int x, int y, boolean aiIsOn) {
        try {
            Point position = new Point(x,y)
            return gameClient.addNodMinigunnerAtWorldCoordinates(position.x, position.y,aiIsOn)
        }
        catch(HttpResponseException e) {
            print e
            println "::" + e.response.responseData["Message"]
            throw e
        }
    }


    // TODO:  Unduplicate this retry code
    Minigunner createRandomNodMinigunner(boolean aiIsOn) {
        int numTiesTried = 0
        int maxTimesToTry = 10

        while(true) {
            try {
                Point randomPosition = createRandomMinigunnerPosition()
                return gameClient.addNodMinigunnerAtWorldCoordinates(randomPosition.x, randomPosition.y, aiIsOn)
            }
            catch(HttpResponseException e) {
                if(numTiesTried < maxTimesToTry) {
                    if (e.response.responseData["Message"] == "Cannot create on blocking terrain") {
                        numTiesTried++
                    }
                    print e
                    println "::" + e.response.responseData["Message"]
                }
                else {
                    print e
                    println "::" + e.response.responseData["Message"]
                    throw e
                }
            }
        }

    }

    Minigunner createRandomNodMinigunnerWithAiTurnedOff() {
        boolean aiIsOn = false
        return createRandomNodMinigunner(aiIsOn)
    }

    Minigunner createRandomNodMinigunnerWithAiTurnedOn() {
        boolean aiIsOn = true
        return createRandomNodMinigunner(aiIsOn)
    }

}