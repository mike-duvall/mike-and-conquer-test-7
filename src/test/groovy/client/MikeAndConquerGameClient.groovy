package client

import groovyx.net.http.RESTClient
import main.Minigunner

class MikeAndConquerGameClient {

    String hostUrl
    RESTClient  restClient

    MikeAndConquerGameClient(String host, int port) {
        hostUrl = "http://$host:$port"
        restClient = new RESTClient(hostUrl)
    }

    void addGDIMinigunner(int minigunnerX, int minigunnerY) {
        def resp = restClient.post(
                path: '/gdiMinigunner',
                body: [ x: minigunnerX, y: minigunnerY ],
                requestContentType: 'application/json' )

//        * Determine what firewalls need to be off
//        * Debug BadRequest when both firewalls on both machines are turned off

        assert resp.status == 200
    }


    Minigunner getMinigunner(String aPath) {
        def resp = restClient.get( path : aPath ) // ACME boomerang
        assert resp.status == 200  // HTTP response code; 404 means not found, etc.
        Minigunner minigunner = new Minigunner()
        minigunner.x = resp.responseData.x
        minigunner.y = resp.responseData.y
        minigunner.health = resp.responseData.health
        return minigunner
    }



    Minigunner getGDIMinigunner() {
        return getMinigunner('/gdiMinigunner')
    }


    void addNODMinigunner(int minigunnerX, int minigunnerY) {
        def resp = restClient.post(
                path: '/nodMinigunner',
                body: [ x: minigunnerX, y: minigunnerY ],
                requestContentType: 'application/json' )

        assert resp.status == 200  // HTTP response code; 404 means not found, etc.
    }

    Minigunner getNODMinigunner() {
        return getMinigunner('/nodMinigunner')
    }

    void leftClick(int mouseX, int mouseY) {
        def resp = restClient.post(
                path: '/leftClick',
                body: [ x: mouseX, y: mouseY ],
                requestContentType: 'application/json' )

        assert resp.status == 200  // HTTP response code; 404 means not found, etc.

    }
}
