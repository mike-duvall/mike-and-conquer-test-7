package main

import client.MikeAndConquerGameClient
import spock.lang.Specification


class MikeAndConquerTest1 extends Specification {



    def "Should place GDI minigunner"() {
        given:
        MikeAndConquerGameClient gameClient = new MikeAndConquerGameClient()

        when:
        gameClient.addGDIMinigunner(200,200)

        then:
        GDIMinigunner gdiMinigunner = gameClient.getGDIMinigunner()
        assert gdiMinigunner.x == 200
        assert gdiMinigunner.y == 200

    }

    def "should be able to move to and attack target" () {
        given:
        int i = 3
        // Gdi minigunner exists at 200,200
        MikeAndConquerGameClient gameClient = new MikeAndConquerGameClient()
        gameClient.addGDIMinigunner(200,200)


//        and:
        // Nod minigunner exists at 500, 500

        when:
        // GDI minigunner is commanded to attack nod minigunner
        i = 3

        then:
        // The GDI minigunner moves into range
        i == 3


//        and:
        // The GID minigunner attacks the Nod minigunner

//        and:
        // The Nod mingunner loses health until it is destroyed

    }

}