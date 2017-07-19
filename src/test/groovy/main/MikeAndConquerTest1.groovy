package main

import client.MikeAndConquerGameClient
import spock.lang.Specification
import spock.util.concurrent.PollingConditions


class MikeAndConquerTest1 extends Specification {


    MikeAndConquerGameClient gameClient


    def setup() {
        //gameClient = new MikeAndConquerGameClient("localhost", 11369)
//        gameClient = new MikeAndConquerGameClient("192.168.0.179", 11369)
        gameClient = new MikeAndConquerGameClient("192.168.0.195", 11369)
        gameClient.resetGame()
    }

    def "clicking nod mingunner should not initiate attack unless gdi minigunner is selected" () {

        given:
        int originalGDIX = 300
        int originalGDIY = 700
        gameClient.addGDIMinigunner(originalGDIX, originalGDIY)
        gameClient.addNODMinigunner(1000,300)


        when:
        gameClient.leftClick(1000,300)

        and:
        sleep(2000)


        then:
        Minigunner gdiMinigunner = gameClient.getGDIMinigunner()
        assert gdiMinigunner.x == originalGDIX
        assert gdiMinigunner.y == originalGDIY
    }


    def "should be able to move to and attack target" () {

        when:
        gameClient.addGDIMinigunner(300,700)
        gameClient.addNODMinigunner(1000,300)

        then:
        Minigunner gdiMinigunner = gameClient.getGDIMinigunner()
        assert gdiMinigunner.x == 300
        assert gdiMinigunner.y == 700


        and:
        Minigunner nodMinigunner = gameClient.getNODMinigunner()
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
            def expectedDeadMinigunner = gameClient.getNODMinigunner()
            assert expectedDeadMinigunner.health == 0
        }

        and:
        String gameState = gameClient.getGameState()
        String expectedGameState = "Game Over"

        assert gameState == expectedGameState
    }

    def "Nod minigunner should wait 8 seconds and then attack GDI minigunner" () {

        given:
        gameClient.addGDIMinigunner(300,700)
        gameClient.addNODMinigunner(1000,300)


        when:
        sleep(3000)

        then:
        Minigunner gdiMinigunner = gameClient.getGDIMinigunner()
        assert gdiMinigunner.x == 300
        assert gdiMinigunner.y == 700


        and:
        Minigunner nodMinigunner = gameClient.getNODMinigunner()
        assert nodMinigunner.x == 1000
        assert nodMinigunner.y == 300
        assert nodMinigunner.health == 1000

        then:
        def conditions = new PollingConditions(timeout: 30, initialDelay: 1.5, factor: 1.25)
        conditions.eventually {
            def expectedDeadMinigunner = gameClient.getGDIMinigunner()
            assert expectedDeadMinigunner.health == 0
        }

        when:
        nodMinigunner = gameClient.getNODMinigunner()

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