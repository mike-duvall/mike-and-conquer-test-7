package main

import client.MikeAndConquerGameClient
import spock.lang.Specification


class MikeAndConquerTest1 extends Specification {



    def "Should place GDI minigunner"() {
        given:
        MikeAndConquerGameClient gameClient = new MikeAndConquerGameClient()

        when:
        gameClient.addGDIMinigunner(300,700)

        then:
        GDIMinigunner gdiMinigunner = gameClient.getGDIMinigunner()
        assert gdiMinigunner.x == 300
        assert gdiMinigunner.y == 700

    }

    def "should be able to move to and attack target" () {
        given:
        MikeAndConquerGameClient gameClient = new MikeAndConquerGameClient()


        when:
        gameClient.addGDIMinigunner(300,700)
        gameClient.addNODMinigunner(1000,300)

        then:
        GDIMinigunner gdiMinigunner = gameClient.getGDIMinigunner()
        assert gdiMinigunner.x == 300
        assert gdiMinigunner.y == 700


        and:
        GDIMinigunner nodMinigunner = gameClient.getNODMinigunner()
        assert nodMinigunner.x == 1000
        assert nodMinigunner.y == 300



//        and:
        // Nod minigunner exists at 500, 500

//        when:
//        // GDI minigunner is commanded to attack nod minigunner
//        i = 3
//
//        then:
//        // The GDI minigunner moves into range
//        i == 3


//        and:
        // The GID minigunner attacks the Nod minigunner

//        and:
        // The Nod mingunner loses health until it is destroyed

    }

}