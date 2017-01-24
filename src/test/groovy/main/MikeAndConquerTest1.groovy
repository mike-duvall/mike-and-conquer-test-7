package main

import client.MikeAndConquerGameClient
import spock.lang.Specification
import spock.util.concurrent.PollingConditions


class MikeAndConquerTest1 extends Specification {


    MikeAndConquerGameClient gameClient


    def setup() {
        //gameClient = new MikeAndConquerGameClient("localhost", 11369)
        //gameClient = new MikeAndConquerGameClient("192.168.0.179", 11369)
        gameClient = new MikeAndConquerGameClient("192.168.0.195", 11369)
    }

    def "Should place GDI minigunner"() {

        when:
        gameClient.addGDIMinigunner(300,700)

        then:
        Minigunner gdiMinigunner = gameClient.getGDIMinigunner()
        assert gdiMinigunner.x == 300
        assert gdiMinigunner.y == 700

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

//        then:
//        // The GDI minigunner moves into range
//        i == 3

        then:
        def conditions = new PollingConditions(timeout: 10, initialDelay: 1.5, factor: 1.25)
        conditions.eventually {
            def expectedDeadMinigunner = gameClient.getNODMinigunner()
            assert expectedDeadMinigunner.health == 0
        }

    }

}