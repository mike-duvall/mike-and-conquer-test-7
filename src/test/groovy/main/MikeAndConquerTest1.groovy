package main

import client.MikeAndConquerGameClient
import spock.lang.Ignore
import spock.lang.Specification
import spock.util.concurrent.PollingConditions



class MikeAndConquerTest1 extends Specification {

    MikeAndConquerGameClient gameClient

    def setup() {
        String localhost = "localhost"
        String remoteHost = "192.168.0.195"
//        String host = localhost
        String host = remoteHost

        int port = 11369
        //boolean useTimeouts = true
        boolean useTimeouts = false
        gameClient = new MikeAndConquerGameClient(host, port, useTimeouts )
        gameClient.resetGame()
    }

    def "clicking nod mingunner should not initiate attack unless gdi minigunner is selected" () {

        given:
        Minigunner gdiMinigunner = createRandomGdiMinigunner()
        Minigunner nodMinigunner = createRandomNodMinigunnerWithAiTurnedOff()

        when:
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
        Minigunner gdiMinigunner1 = createRandomGdiMinigunner()
        Minigunner gdiMinigunner2 = createRandomGdiMinigunner()

        when:
        createRandomNodMinigunnerWithAiTurnedOn()

        then:
        assertGdiMinigunnerDies(gdiMinigunner1.id)
        assertGdiMinigunnerDies(gdiMinigunner2.id)
        assertGameStateGoesToMissionFailed()
    }

    def "Multiple Nod attack superior GDI forces"() {
        given:
        Minigunner gdiMinigunner1 = createRandomGdiMinigunner()
        Minigunner gdiMinigunner2 = createRandomGdiMinigunner()
        Minigunner gdiMinigunner3 = createRandomGdiMinigunner()


        when:
        createRandomNodMinigunnerWithAiTurnedOn()
        createRandomNodMinigunnerWithAiTurnedOn()

        then:
        assertGdiMinigunnerDies(gdiMinigunner1.id)
        assertGdiMinigunnerDies(gdiMinigunner2.id)
        assertGdiMinigunnerDies(gdiMinigunner3.id)
        assertGameStateGoesToMissionFailed()
    }

    def "should be able to move to and attack target" () {

        given:
        Minigunner gdiMinigunner = createRandomGdiMinigunner()
        Minigunner nodMinigunner = createRandomNodMinigunnerWithAiTurnedOff()

        when:
        gameClient.leftClickMinigunner(gdiMinigunner.id)

        and:
        gameClient.leftClickMinigunner(nodMinigunner.id)

        then:
        assertNodMinigunnerDies(nodMinigunner.id)

        and:
        assertGameStateGoesToGameOver()
    }

    def "two gdi minigunners attack two nod minigunners" () {
        given:
        Minigunner gdiMinigunner1 = createRandomGdiMinigunner()
        Minigunner gdiMinigunner2 = createRandomGdiMinigunner()
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
        Minigunner gdiMinigunner1 = createRandomGdiMinigunner()

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
        Minigunner gdiMinigunner1 = createRandomGdiMinigunner()
        Minigunner gdiMinigunner2 = createRandomGdiMinigunner()

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


    // Note this test is hard coded to work with zoom = 3.0f and
    // the map scrolled all the way up and left
    def "movement destination should snap to center of map square"() {
        given:
        Minigunner createdMinigunner1 = createRandomGdiMinigunner()

        when:
        gameClient.leftClickMinigunner(createdMinigunner1.id)

        and:
        // click square all the way to the left and
        // one down
        gameClient.leftClick(20,90)

        and:
        Minigunner retrievedMinigunner = gameClient.getGdiMinigunnerById(createdMinigunner1.id)

        then:
        assert retrievedMinigunner.destinationX == 12
        assert retrievedMinigunner.destinationY == 36

    }

    def "should be able to move two separate GDI minigunners" () {
        given:

        int minigunner1DestinationX = 100
        int minigunner1DestinationY = 200
        Minigunner createdMinigunner1 = createRandomGdiMinigunner()

        int minigunner2DestinationX = 300
        int minigunner2DestinationY = 400
        Minigunner createdMinigunner2 = createRandomGdiMinigunner()

        when:
        gameClient.leftClickMinigunner(createdMinigunner1.id)

        and:
        gameClient.leftClick(minigunner1DestinationX, minigunner1DestinationY )

        and:
        gameClient.leftClickMinigunner(createdMinigunner2.id)

        and:
        gameClient.leftClick(minigunner2DestinationX, minigunner2DestinationY )



        then:
        def conditions = new PollingConditions(timeout: 40, initialDelay: 1.5, factor: 1.25)
        conditions.eventually {
            Minigunner retrievedMinigunner = gameClient.getGdiMinigunnerById(createdMinigunner1.id)
//            assertMinigunnerIsAtScreenPosition(retrievedMinigunner, minigunner1DestinationX, minigunner1DestinationY)
            assertMinigunnerIsAtWorldPosition(
                    retrievedMinigunner,
                    retrievedMinigunner.destinationX,
                    retrievedMinigunner.destinationY)
            assert retrievedMinigunner.health != 0
        }

        and:
        def conditions2 = new PollingConditions(timeout: 40, initialDelay: 1.5, factor: 1.25)
        conditions2.eventually {
            def retrievedMinigunner = gameClient.getGdiMinigunnerById(createdMinigunner2.id)
//            assertMinigunnerIsAtScreenPosition(retrievedMinigunner, minigunner2DestinationX, minigunner2DestinationY)
            assertMinigunnerIsAtWorldPosition(
                    retrievedMinigunner,
                    retrievedMinigunner.destinationX,
                    retrievedMinigunner.destinationY)

            assert retrievedMinigunner.health != 0
        }


        and:
        String gameState = gameClient.getGameState()
        String expectedGameState = "Playing"  // not sure if Playing is correct state

        assert gameState == expectedGameState
    }


    def "multithread tests"() {
        given:
        createRandomGdiMinigunner()
        createRandomGdiMinigunner()
        createRandomGdiMinigunner()
        createRandomGdiMinigunner()
        createRandomGdiMinigunner()
        createRandomGdiMinigunner()
        createRandomGdiMinigunner()
        createRandomGdiMinigunner()

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

        Minigunner gdiMinigunner = gameClient.addGDIMinigunner(originalGDIX, originalGDIY)

        int nodMinigunnerX = 1000
        int nodMinigunnerY = 300
        gameClient.addNODMinigunner( nodMinigunnerX, nodMinigunnerY )

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

    def assertMinigunnerIsAtWorldPosition(Minigunner minigunner, int expectedX, int expectedY)
    {
        int leeway = 4
        assert (minigunner.x >= expectedX - leeway) && (minigunner.x <= expectedX + leeway)
        assert (minigunner.y >= expectedY - leeway) && (minigunner.y <= expectedY + leeway)
    }

    def assertNodMinigunnerDies(int id) {
        def conditions = new PollingConditions(timeout: 40, initialDelay: 1.5, factor: 1.25)
        conditions.eventually {
            def expectedDeadMinigunner = gameClient.getNodMinigunnerById(id)
            assert expectedDeadMinigunner.health == 0
        }
        return true
    }


    def assertGdiMinigunnerDies(int id) {
        def conditions = new PollingConditions(timeout: 40, initialDelay: 1.5, factor: 1.25)
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

        int minX = 20
        int minY = 20
//        int maxX = 610
//        int maxY = 540
        // Currently capping max x and y so that they appear on screen with Zoom = 3 in the app
        // and so that any clicks won't also initiate scrolling of screen
        int maxX = 580
        int maxY = 290

        int randomX = rand.nextInt(maxX) + minX
        int randomY = rand.nextInt(maxY) + minY

        Point point = new Point()
        point.x = randomX
        point.y = randomY
        return point

    }

    Minigunner createRandomGdiMinigunner() {
        Point randomPosition = createRandomMinigunnerPosition()
        return gameClient.addGDIMinigunner(randomPosition.x, randomPosition.y)
    }


    Minigunner createRandomNodMinigunner(boolean aiIsOn) {
        Point randomPosition = createRandomMinigunnerPosition()
        return gameClient.addNODMinigunner(randomPosition.x, randomPosition.y, aiIsOn)
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