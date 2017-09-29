package main

import client.MikeAndConquerGameClient
import spock.lang.Specification
import spock.util.concurrent.PollingConditions



class MikeAndConquerTest1 extends Specification {


    MikeAndConquerGameClient gameClient


    def setup() {
        // String host = "192.168.0.179"
        String host = "192.168.0.195"
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


    def "should be able to move to and attack target" () {

        when:
        Minigunner gdiMinigunner = gameClient.addGDIMinigunner(300,700)
        Minigunner nodMinigunner = gameClient.addNODMinigunner(1000,300)

        then:
        assert gdiMinigunner.x == 300
        assert gdiMinigunner.y == 700


        and:
        assert nodMinigunner.x == 1000
        assert nodMinigunner.y == 300
        assert nodMinigunner.health == 1000


        when:
        gameClient.leftClick(300,700)

        and:
        gameClient.leftClick(1000,300)

        then:
        def conditions = new PollingConditions(timeout: 10, initialDelay: 1.5, factor: 1.25)
        conditions.eventually {
            def expectedDeadMinigunner = gameClient.getNodMinigunnerById(nodMinigunner.id)
            assert expectedDeadMinigunner.health == 0
        }

        and:
        String expectedGameState = "Game Over"

        conditions.eventually {
            String gameState = gameClient.getGameState()
            assert gameState == expectedGameState
        }

    }

    Minigunner createRandomGdiMinigunner() {

        Random rand = new Random()

        int maxX = 1000
        int maxY = 800

        int randomX = rand.nextInt(maxX)
        int randomY = rand.nextInt(maxY)
        return gameClient.addGDIMinigunner(randomX,randomY)
    }


    def "should handle selecting a different player unit when player unit already selected"() {
        given:
        Minigunner gdiMinigunner1 = createRandomGdiMinigunner()
        Minigunner gdiMinigunner2 = createRandomGdiMinigunner()

        when:
        gameClient.leftClick(gdiMinigunner1.x, gdiMinigunner1.y)

        and:
        Minigunner retrievedMinigunner1 = gameClient.getGdiMinigunnerById(gdiMinigunner1.id)
        Minigunner retrievedMinigunner2 = gameClient.getGdiMinigunnerById(gdiMinigunner2.id)

        then:
        assert retrievedMinigunner1.selected == true
        assert retrievedMinigunner2.selected == false

        when:
        gameClient.leftClick(gdiMinigunner2.x, gdiMinigunner2.y)

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
        gameClient.leftClick(300,700)

        and:
        gameClient.leftClick(minigunner1DestinationX, minigunner1DestinationY )

        and:
        gameClient.leftClick(500,700)

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