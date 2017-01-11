package client

import groovyx.net.http.RESTClient
import main.GDIMinigunner

class MikeAndConquerGameClient {
    void addGDIMinigunner(int minigunnerX, int minigunnerY) {

        RESTClient  restClient = new RESTClient( 'http://localhost:11369/' )
        def resp = restClient.post(
                path: '/gdiMinigunner',
                body: [ x: minigunnerX, y: minigunnerY ],
                requestContentType: 'application/json' )

        assert resp.status == 200  // HTTP response code; 404 means not found, etc.
    }

    GDIMinigunner getGDIMinigunner() {
        RESTClient  restClient = new RESTClient( 'http://localhost:11369' )
        def resp = restClient.get( path : '/gdiMinigunner' ) // ACME boomerang
        assert resp.status == 200  // HTTP response code; 404 means not found, etc.
        GDIMinigunner minigunner = new GDIMinigunner()
        minigunner.x = resp.responseData.x
        minigunner.y = resp.responseData.y
        return minigunner
    }


    void addNODMinigunner(int minigunnerX, int minigunnerY) {

        RESTClient  restClient = new RESTClient( 'http://localhost:11369/' )
        def resp = restClient.post(
                path: '/nodMinigunner',
                body: [ x: minigunnerX, y: minigunnerY ],
                requestContentType: 'application/json' )

        assert resp.status == 200  // HTTP response code; 404 means not found, etc.
    }

    GDIMinigunner getNODMinigunner() {
        RESTClient  restClient = new RESTClient( 'http://localhost:11369' )
        def resp = restClient.get( path : '/nodMinigunner' ) // ACME boomerang
        assert resp.status == 200  // HTTP response code; 404 means not found, etc.
        GDIMinigunner minigunner = new GDIMinigunner()
        minigunner.x = resp.responseData.x
        minigunner.y = resp.responseData.y
        return minigunner
    }

    void leftClick(int mouseX, int mouseY) {
        RESTClient  restClient = new RESTClient( 'http://localhost:11369/' )
        def resp = restClient.post(
                path: '/leftClick',
                body: [ x: mouseX, y: mouseY ],
                requestContentType: 'application/json' )

        assert resp.status == 200  // HTTP response code; 404 means not found, etc.

    }
}
