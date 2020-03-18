package main

import client.MikeAndConquerGameClient
import groovyx.net.http.HttpResponseException
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll
import spock.util.concurrent.PollingConditions

import javax.imageio.ImageIO
import java.awt.image.BufferedImage


class MikeAndConquerTest1 extends MikeAndConquerTestBase {

//    MikeAndConquerGameClient gameClient
//
//    def setup() {
//        String localhost = "localhost"
//        String remoteHost = "192.168.0.146"
////        String host = localhost
//        String host = remoteHost
//
//        int port = 11369
//        //boolean useTimeouts = true
//        boolean useTimeouts = false
//        gameClient = new MikeAndConquerGameClient(host, port, useTimeouts )
//        gameClient.resetGame()
//        gameClient.leftClickInWorldCoordinates(1,1)  // to get mouse clicks in default state
//    }

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

    def "clicking nod mingunner should not initiate attack unless gdi minigunner is selected" () {

        given:
        Minigunner gdiMinigunner = createRandomGDIMinigunner()
        Minigunner nodMinigunner = createRandomNodMinigunnerWithAiTurnedOff()

        when:
        gameClient.getScreenshot()
        gameClient.leftClickMinigunner(nodMinigunner.id)

        and:
        int TWO_SECONDS_IN_MILLIS = 2000
        sleep( TWO_SECONDS_IN_MILLIS )

        then:
        Minigunner retrievedGdiMinigunner = gameClient.getGdiMinigunnerById(gdiMinigunner.id)
        assert retrievedGdiMinigunner.x == gdiMinigunner.x
        assert retrievedGdiMinigunner.y == gdiMinigunner.y
    }

    def "Nod successively attacks two gdi minigunners"() {
        given:
        Minigunner gdiMinigunner1 = createRandomGDIMinigunner()
        Minigunner gdiMinigunner2 = createRandomGDIMinigunner()

        when:
        createRandomNodMinigunnerWithAiTurnedOn()

        then:
        assertGdiMinigunnerDies(gdiMinigunner1.id)
        assertGdiMinigunnerDies(gdiMinigunner2.id)
        assertGameStateGoesToMissionFailed()
    }

    def "Multiple Nod attack superior GDI forces"() {
        given:

        Minigunner gdiMinigunner1 = createRandomGDIMinigunner()
        Minigunner gdiMinigunner2 = createRandomGDIMinigunner()
        Minigunner gdiMinigunner3 = createRandomGDIMinigunner()

        when:
        createRandomNodMinigunnerWithAiTurnedOn()
        createRandomNodMinigunnerWithAiTurnedOn()

        then:
        assertGdiMinigunnerDies(gdiMinigunner1.id)
        assertGdiMinigunnerDies(gdiMinigunner2.id)
        assertGdiMinigunnerDies(gdiMinigunner3.id)
        assertGameStateGoesToMissionFailed()
    }


    def "should be able to move to and attack target through obstacle" () {

        given:
        Minigunner gdiMinigunner = createGDIMinigunnerAtLocation(82,369)
        Minigunner nodMinigunner = createNodMinigunnerAtLocation(346,320, false)

        when:
        gameClient.leftClickMinigunner(gdiMinigunner.id)

        and:
        gameClient.leftClickMinigunner(nodMinigunner.id)

        then:
        assertNodMinigunnerDies(nodMinigunner.id)

        and:
        assertGameStateGoesToGameOver()
    }


    // Unfortunately, these have to be static(or @Shared) to be accessible in the "where" block
    // https://stackoverflow.com/questions/22707195/how-to-use-instance-variable-in-where-section-of-spock-test
    static int selectionBoxLeftmostX = 75
    static int selectionBoxRightmostX = 100
    static int selectionBoxTopmostY = 350
    static int selectionBoxBottommostY = 400

    @Unroll
    def "should be able to drag select multiple GDI minigunners" () {

        when:
        Minigunner gdiMinigunner1 = createGDIMinigunnerAtLocation(82,369)
        Minigunner gdiMinigunner2 = createGDIMinigunnerAtLocation(92,380)

        Minigunner gdiMinigunner3 = createGDIMinigunnerAtLocation(230,300)

        then:
        assert gdiMinigunner1.selected == false
        assert gdiMinigunner2.selected == false
        assert gdiMinigunner3.selected == false

        when:
        gameClient.dragSelect(dragStartX, dragStartY, dragEndX, dragEndY)

        and:
        gdiMinigunner1 = gameClient.getGdiMinigunnerById(gdiMinigunner1.id)
        gdiMinigunner2 = gameClient.getGdiMinigunnerById(gdiMinigunner2.id)
        gdiMinigunner3 = gameClient.getGdiMinigunnerById(gdiMinigunner3.id)


        then:
        assert gdiMinigunner1.selected == true
        assert gdiMinigunner2.selected == true
        assert gdiMinigunner3.selected == false

        when:
        gameClient.rightClick(10,10)

        and:
        gdiMinigunner1 = gameClient.getGdiMinigunnerById(gdiMinigunner1.id)
        gdiMinigunner2 = gameClient.getGdiMinigunnerById(gdiMinigunner2.id)
        gdiMinigunner3 = gameClient.getGdiMinigunnerById(gdiMinigunner3.id)


        then:
        assert gdiMinigunner1.selected == false
        assert gdiMinigunner2.selected == false
        assert gdiMinigunner3.selected == false


        where:
        dragStartX              | dragStartY                | dragEndX                  | dragEndY
        selectionBoxLeftmostX   | selectionBoxTopmostY      | selectionBoxRightmostX    | selectionBoxBottommostY    // Top left to bottom right
        selectionBoxRightmostX  | selectionBoxBottommostY   | selectionBoxLeftmostX     | selectionBoxTopmostY    // Bottom right to top left
        selectionBoxRightmostX  | selectionBoxTopmostY      | selectionBoxLeftmostX     | selectionBoxBottommostY    // Top right to bottom left
        selectionBoxLeftmostX   | selectionBoxBottommostY   | selectionBoxRightmostX    | selectionBoxTopmostY    // Bottom left to top right

    }



    def "two gdi minigunners attack two nod minigunners" () {
        given:
        Minigunner gdiMinigunner1 = createRandomGDIMinigunner()
        Minigunner gdiMinigunner2 = createRandomGDIMinigunner()

        Minigunner nodMinigunner1 = createRandomNodMinigunnerWithAiTurnedOff()
        Minigunner nodMinigunner2 = createRandomNodMinigunnerWithAiTurnedOff()

        when:
        gameClient.leftClickMinigunner(gdiMinigunner1.id)
        gameClient.leftClickMinigunner(nodMinigunner1.id)

        and:
        gameClient.leftClickMinigunner(gdiMinigunner2.id)
        gameClient.leftClickMinigunner(nodMinigunner2.id)


        then:
        assertNodMinigunnerDies(nodMinigunner1.id)

        and:
        assertNodMinigunnerDies(nodMinigunner2.id)

    }


    def "should handle selecting deselecting gdi minigunner"() {
        given:
        Minigunner gdiMinigunner1 = createRandomGDIMinigunner()

        when:
        Minigunner retrievedMinigunner1 = gameClient.getGdiMinigunnerById(gdiMinigunner1.id)

        then:
        assert retrievedMinigunner1.selected == false

        when:
        gameClient.leftClickMinigunner(gdiMinigunner1.id)

        and:
        retrievedMinigunner1 = gameClient.getGdiMinigunnerById(gdiMinigunner1.id)

        then:
        assert retrievedMinigunner1.selected == true

        when:
        gameClient.rightClick(200,200)

        and:
        retrievedMinigunner1 = gameClient.getGdiMinigunnerById(gdiMinigunner1.id)

        then:
        assert retrievedMinigunner1.selected == false
    }

    def "should handle selecting a different player unit when player unit already selected"() {
        given:
        Minigunner gdiMinigunner1 = createRandomGDIMinigunner()
        Minigunner gdiMinigunner2 = createRandomGDIMinigunner()

        when:
        gameClient.leftClickMinigunner(gdiMinigunner1.id)

        and:
        Minigunner retrievedMinigunner1 = gameClient.getGdiMinigunnerById(gdiMinigunner1.id)
        Minigunner retrievedMinigunner2 = gameClient.getGdiMinigunnerById(gdiMinigunner2.id)

        then:
        assert retrievedMinigunner1.selected == true
        assert retrievedMinigunner2.selected == false

        when:
        gameClient.leftClickMinigunner(gdiMinigunner2.id)

        and:
        retrievedMinigunner1 = gameClient.getGdiMinigunnerById(gdiMinigunner1.id)
        retrievedMinigunner2 = gameClient.getGdiMinigunnerById(gdiMinigunner2.id)

        then:
        assert retrievedMinigunner1.selected == false
        assert retrievedMinigunner2.selected == true

        assert retrievedMinigunner1.x == gdiMinigunner1.x
        assert retrievedMinigunner1.y == gdiMinigunner1.y

        assert retrievedMinigunner2.x == gdiMinigunner2.x
        assert retrievedMinigunner2.y == gdiMinigunner2.y

    }


    def "should set mouse cursor correctly when minigunner is selected" () {

        given:
        Minigunner gdiMinigunner = createRandomGDIMinigunner()
        Point mountainSquareLocation = new Point(79,20)
        Point clearSquare = new Point(10,10)

        when:
        gameClient.leftClickMinigunner(gdiMinigunner.id)

        and:
        gameClient.moveMouseToWorldCoordinates(mountainSquareLocation)

        then:
        String mouseCursorState = gameClient.getMouseCursorState()
        assert mouseCursorState == "MovementNoteAllowedCursor"

        when:
        gameClient.moveMouseToWorldCoordinates(clearSquare)
        mouseCursorState = gameClient.getMouseCursorState()

        then:
        assert mouseCursorState == "MoveToLocationCursor"


        when:
        Minigunner nodMinigunner = createRandomNodMinigunnerWithAiTurnedOff()
        gameClient.moveMouseToWorldCoordinates(new Point(nodMinigunner.x, nodMinigunner.y))
        mouseCursorState = gameClient.getMouseCursorState()

        then:
        assert mouseCursorState == "AttackEnemyCursor"


        when:
        gameClient.rightClick(20,20)
        mouseCursorState = gameClient.getMouseCursorState()

        then:
        assert mouseCursorState == "DefaultArrowCursor"

    }

    def "should be able to move two separate GDI minigunners" () {
        given:
        int minigunner1DestinationX = 300
        int minigunner1DestinationY = 100
        Minigunner createdMinigunner1 = createRandomGDIMinigunner()

        int minigunner2DestinationX = 320
        int minigunner2DestinationY = 140
        Minigunner createdMinigunner2 = createRandomGDIMinigunner()

        when:
        gameClient.leftClickMinigunner(createdMinigunner1.id)

        and:
        gameClient.leftClickInWorldCoordinates(minigunner1DestinationX, minigunner1DestinationY )

        and:
        gameClient.leftClickMinigunner(createdMinigunner2.id)

        and:
        gameClient.leftClickInWorldCoordinates(minigunner2DestinationX, minigunner2DestinationY )

        and:
        println "createdMinigunner1.x=" + createdMinigunner1.x
        println "createdMinigunner1.x=" + createdMinigunner1.y
        println "createdMinigunner2.x=" + createdMinigunner2.x
        println "createdMinigunner2.x=" + createdMinigunner2.y

        then:
        def conditions = new PollingConditions(timeout: 60, initialDelay: 1.5, factor: 1.25)
        conditions.eventually {
            Minigunner retrievedMinigunner = gameClient.getGdiMinigunnerById(createdMinigunner1.id)
            assertMinigunnerIsAtDesignatedDestination(retrievedMinigunner, minigunner1DestinationX, minigunner1DestinationY)
            assert retrievedMinigunner.health != 0
        }

        and:
        def conditions2 = new PollingConditions(timeout: 60, initialDelay: 1.5, factor: 1.25)
        conditions2.eventually {
            def retrievedMinigunner = gameClient.getGdiMinigunnerById(createdMinigunner2.id)
            assertMinigunnerIsAtDesignatedDestination(retrievedMinigunner, minigunner2DestinationX, minigunner2DestinationY)

            assert retrievedMinigunner.health != 0
        }


        and:
        String gameState = gameClient.getGameState()
        String expectedGameState = "Playing"  // not sure if Playing is correct state

        assert gameState == expectedGameState
    }


    @Ignore
    def "minigunner should be able to navigate around simple sandbag obstacle" () {
        given:
        def createdMinigunner1 =  gameClient.addGDIMinigunnerAtMapSquare(11,2)

        and:
        gameClient.addSandbag(9,6, 5)
        gameClient.addSandbag(9,5, 5)
        gameClient.addSandbag(9,4, 10)
        gameClient.addSandbag(10,4, 10)
        gameClient.addSandbag(11,4, 10)
        gameClient.addSandbag(12,4, 10)
        gameClient.addSandbag(13,4, 10)
        gameClient.addSandbag(14,4, 10)
        gameClient.addSandbag(14,5, 5)
        gameClient.addSandbag(14,6, 5)

        gameClient.addSandbag(9,7, 10)
        gameClient.addSandbag(10,7, 10)
        gameClient.addSandbag(11,7, 10)
        gameClient.addSandbag(12,7, 10)

        gameClient.addSandbag(12,8, 5)
        gameClient.addSandbag(12,9, 5)
        gameClient.addSandbag(12,10, 5)
        gameClient.addSandbag(12,11, 5)
        gameClient.addSandbag(12,12, 5)

        gameClient.addSandbag(14,7, 5)
        gameClient.addSandbag(14,8, 5)
        gameClient.addSandbag(14,9, 5)
        gameClient.addSandbag(14,10, 5)

        gameClient.addSandbag(15,10, 10)
        gameClient.addSandbag(16,10, 10)
        gameClient.addSandbag(17,10, 10)
        gameClient.addSandbag(18,10, 10)


        gameClient.addSandbag(13,12, 10)
        gameClient.addSandbag(14,12, 10)
        gameClient.addSandbag(15,12, 10)
        gameClient.addSandbag(16,12, 10)
        gameClient.addSandbag(17,12, 10)
        gameClient.addSandbag(18,12, 10)


        when:
        gameClient.leftClickMinigunner(createdMinigunner1.id)
        gameClient.leftClick(800,400)

        then:
        true
//        then:
//        def conditions = new PollingConditions(timeout: 40, initialDelay: 1.5, factor: 1.25)
//        conditions.eventually {
//            Minigunner retrievedMinigunner = gameClient.getGdiMinigunnerById(createdMinigunner1.id)
//            assertMinigunnerIsAtDestination(retrievedMinigunner)
//            assert retrievedMinigunner.health != 0
//        }

//        and:
//        def conditions2 = new PollingConditions(timeout: 40, initialDelay: 1.5, factor: 1.25)
//        conditions2.eventually {
//            def retrievedMinigunner = gameClient.getGdiMinigunnerById(createdMinigunner2.id)
//            assertMinigunnerIsAtDestination(retrievedMinigunner)
//
//            assert retrievedMinigunner.health != 0
//        }
//
//
//        and:
//        String gameState = gameClient.getGameState()
//        String expectedGameState = "Playing"  // not sure if Playing is correct state
//
//        assert gameState == expectedGameState
    }



    @Ignore
    def "multithread tests"() {
        given:
        createRandomGDIMinigunner()
        createRandomGDIMinigunner()
        createRandomGDIMinigunner()
        createRandomGDIMinigunner()
        createRandomGDIMinigunner()
        createRandomGDIMinigunner()
        createRandomGDIMinigunner()
        createRandomGDIMinigunner()

        when:
        gameClient.resetGame()

        then:
        true
    }

    @Ignore
    def "Stress test for memory leaks" () {

        given:
        int originalGDIX = 300
        int originalGDIY = 700

        Minigunner gdiMinigunner = gameClient.addGDIMinigunnerAtWorldCoordinates(originalGDIX, originalGDIY)

        int nodMinigunnerX = 1000
        int nodMinigunnerY = 300
        gameClient.addNodMinigunnerAtWorldCoordinates( nodMinigunnerX, nodMinigunnerY )

        expect:
        gameClient.leftClick(nodMinigunnerX, nodMinigunnerY )
        Minigunner retrievedGdiMinigunner = gameClient.getGdiMinigunnerById(gdiMinigunner.id)
        assert retrievedGdiMinigunner.x == originalGDIX
        assert retrievedGdiMinigunner.y == originalGDIY

        where:
        i << (1..45)
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
        def conditions = new PollingConditions(timeout: 100, initialDelay: 1.5, factor: 1.25)
        conditions.eventually {
            def expectedDeadMinigunner = gameClient.getGdiMinigunnerById(id)
            assert expectedDeadMinigunner.health == 0
        }
        return true
    }


    def assertGameStateGoesToMissionFailed() {
        String expectedGameState = "Mission Failed"

        def conditions = new PollingConditions(timeout: 15, initialDelay: 1.5, factor: 1.25)
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