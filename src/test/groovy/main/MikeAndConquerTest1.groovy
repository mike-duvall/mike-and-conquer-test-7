package main

import client.MikeAndConquerGameClient
import spock.lang.Ignore
import spock.lang.Specification
import spock.util.concurrent.PollingConditions



class MikeAndConquerTest1 extends Specification {


    MikeAndConquerGameClient gameClient


    def setup() {
        //String host = "192.168.0.179"
        String host = "localhost"
        //String host = "192.168.0.195"
        int port = 11369
        boolean useTimeouts = true
        gameClient = new MikeAndConquerGameClient(host, port, useTimeouts )
        gameClient.resetGame()
    }



    def "clicking nod mingunner should not initiate attack unless gdi minigunner is selected" () {

        given:
        int originalGDIX = 300
        int originalGDIY = 700
        Minigunner gdiMinigunner = gameClient.addGDIMinigunner(originalGDIX, originalGDIY)

        int nodMinigunnerX = 1000
        int nodMinigunnerY = 300
        gameClient.addNODMinigunner( nodMinigunnerX, nodMinigunnerY )

        when:
        gameClient.leftClick(nodMinigunnerX, nodMinigunnerY )

        and:
        int TWO_SECONDS_IN_MILLIS = 2000
        sleep( TWO_SECONDS_IN_MILLIS )


        then:
        Minigunner retrievedGdiMinigunner = gameClient.getGdiMinigunnerById(gdiMinigunner.id)
        assert retrievedGdiMinigunner.x == originalGDIX
        assert retrievedGdiMinigunner.y == originalGDIY
    }

    def "Nod successively attacks two gdi minigunners"() {
        given:
        Minigunner gdiMinigunner1 = createRandomGdiMinigunner()
        Minigunner gdiMinigunner2 = createRandomGdiMinigunner()

        Minigunner nodMinigunner = createRandomNodMinigunner()


        when:
        int TEN_SECONDS_IN_MILLIS = 10000
        sleep( TEN_SECONDS_IN_MILLIS )

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

        Minigunner nodMinigunner1 = createRandomNodMinigunner()
        Minigunner nodMinigunner2 = createRandomNodMinigunner()


        when:
        int TEN_SECONDS_IN_MILLIS = 10000
        sleep( TEN_SECONDS_IN_MILLIS )

        then:
        assertGdiMinigunnerDies(gdiMinigunner1.id)
        assertGdiMinigunnerDies(gdiMinigunner2.id)
        assertGdiMinigunnerDies(gdiMinigunner3.id)
        assertGameStateGoesToMissionFailed()
    }


//    @Ignore
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


    def assertNodMinigunnerDies(int id) {
        def conditions = new PollingConditions(timeout: 20, initialDelay: 1.5, factor: 1.25)
        conditions.eventually {
            def expectedDeadMinigunner = gameClient.getNodMinigunnerById(id)
            assert expectedDeadMinigunner.health == 0
        }
        return true
    }


    def assertGdiMinigunnerDies(int id) {
        def conditions = new PollingConditions(timeout: 10, initialDelay: 1.5, factor: 1.25)
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

    def "should be able to move to and attack target" () {

        given:
        Minigunner gdiMinigunner = gameClient.addGDIMinigunner(300,700)
        Minigunner nodMinigunner = gameClient.addNODMinigunner(1000,300)


        when:
        Minigunner retrievedGdiMinigunner = gameClient.getGdiMinigunnerById(gdiMinigunner.id)

        then:
        assert retrievedGdiMinigunner.x == 300
        assert retrievedGdiMinigunner.y == 700

        when:
        leftClickMinigunner(gdiMinigunner)

        and:
        leftClickMinigunner(nodMinigunner)

        then:
        assertNodMinigunnerDies(nodMinigunner.id)

        and:
        assertGameStateGoesToGameOver()

    }


    def "two gdi minigunners attack two nod minigunners" () {
        given:
        Minigunner gdiMinigunner1 = gameClient.addGDIMinigunner(300,700)
        Minigunner gdiMinigunner2 = gameClient.addGDIMinigunner(700,700)
        Minigunner nodMinigunner1 = gameClient.addNODMinigunner(1000,300)
        Minigunner nodMinigunner2 = gameClient.addNODMinigunner(1400,300)

        when:
        leftClickMinigunner(gdiMinigunner1)
        leftClickMinigunner(nodMinigunner1)

        and:
        leftClickMinigunner(gdiMinigunner2)
        leftClickMinigunner(nodMinigunner2)

        then:
        assertNodMinigunnerDies(nodMinigunner1.id)

        and:
        assertNodMinigunnerDies(nodMinigunner2.id)


    }

    Minigunner createRandomGdiMinigunner() {
        Random rand = new Random()

        int minX = 100
        int minY = 100
        int maxX = 1000
        int maxY = 800

        int randomX = rand.nextInt(maxX) + minX
        int randomY = rand.nextInt(maxY) + minY
        return gameClient.addGDIMinigunner(randomX,randomY)
    }


    Minigunner createRandomNodMinigunner() {
        Random rand = new Random()

        int minX = 100
        int minY = 100
        int maxX = 1000
        int maxY = 800

        int randomX = rand.nextInt(maxX) + minX
        int randomY = rand.nextInt(maxY) + minY
        return gameClient.addNODMinigunner(randomX,randomY)
    }


    void leftClickMinigunner(Minigunner minigunner) {
        gameClient.leftClick(minigunner.x, minigunner.y)
    }

    def "should handle selecting a different player unit when player unit already selected"() {
        given:
        Minigunner gdiMinigunner1 = createRandomGdiMinigunner()
        Minigunner gdiMinigunner2 = createRandomGdiMinigunner()

        when:
        leftClickMinigunner(gdiMinigunner1)

        and:
        Minigunner retrievedMinigunner1 = gameClient.getGdiMinigunnerById(gdiMinigunner1.id)
        Minigunner retrievedMinigunner2 = gameClient.getGdiMinigunnerById(gdiMinigunner2.id)

        then:
        assert retrievedMinigunner1.selected == true
        assert retrievedMinigunner2.selected == false

        when:
        leftClickMinigunner(gdiMinigunner2)

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

    def "should be able to move two separate GDI minigunners" () {

        given:

        int minigunner1DestinationX = 1000
        int minigunner1DestinationY = 300
        Minigunner createdMinigunner1 = gameClient.addGDIMinigunner(300,700)

        int minigunner2DestinationX = 1100
        int minigunner2DestinationY = 400

        Minigunner createdMinigunner2 = gameClient.addGDIMinigunner(500,700)

        when:
        leftClickMinigunner(createdMinigunner1)

        and:
        gameClient.leftClick(minigunner1DestinationX, minigunner1DestinationY )

        and:
        leftClickMinigunner(createdMinigunner2)

        and:
        gameClient.leftClick(minigunner2DestinationX, minigunner2DestinationY )


        then:
        def conditions = new PollingConditions(timeout: 10, initialDelay: 1.5, factor: 1.25)
        conditions.eventually {
            def expectedMinigunner = gameClient.getGdiMinigunnerById(createdMinigunner1.id)
            assert expectedMinigunner.x == minigunner1DestinationX
            assert expectedMinigunner.y == minigunner1DestinationY
            assert expectedMinigunner.health != 0
        }

        and:
        def conditions2 = new PollingConditions(timeout: 10, initialDelay: 1.5, factor: 1.25)
        conditions2.eventually {
            def expectedMinigunner = gameClient.getGdiMinigunnerById(createdMinigunner2.id)
            assert expectedMinigunner.x == minigunner2DestinationX
            assert expectedMinigunner.y == minigunner2DestinationY
            assert expectedMinigunner.health != 0
        }


        and:
        String gameState = gameClient.getGameState()
        String expectedGameState = "Playing"  // not sure if Playing is correct state

        assert gameState == expectedGameState
    }



    def "Nod minigunner should wait 8 seconds and then attack GDI minigunner" () {

        given:
        Minigunner gdiMinigunner = gameClient.addGDIMinigunner(300,700)
        Minigunner nodMinigunner = gameClient.addNODMinigunner(1000,300)


        when:
        sleep(3000)

        then:
        assert gdiMinigunner.x == 300
        assert gdiMinigunner.y == 700


        and:
        assert nodMinigunner.x == 1000
        assert nodMinigunner.y == 300
        assert nodMinigunner.health == 1000

        then:
        def conditions = new PollingConditions(timeout: 30, initialDelay: 1.5, factor: 1.25)
        conditions.eventually {
            gdiMinigunner = gameClient.getGdiMinigunnerById(gdiMinigunner.id)
            assert gdiMinigunner.health == 0
        }

        when:
        nodMinigunner = gameClient.getNodMinigunnerById(nodMinigunner.id)

        then:
        assert nodMinigunner.x < 1000
        assert nodMinigunner.y > 300
        assert nodMinigunner.health == 1000

        and:
        String gameState = gameClient.getGameState()
        String expectedGameState = "Mission Failed"

        assert gameState == expectedGameState
    }


}