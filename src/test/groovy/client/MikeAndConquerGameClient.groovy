package client

import groovyx.net.http.RESTClient
import main.GDIMinigunner

class MikeAndConquerGameClient {
    void addGDIMinigunner(int x, int y) {

        RESTClient  restClient = new RESTClient( 'http://localhost:11369/' )

        def resp = restClient.get( path : '/gdiMinigunner' ) // ACME boomerang
        assert resp.status == 200  // HTTP response code; 404 means not found, etc.
    }

    GDIMinigunner getGDIMinigunner() {
        RESTClient  restClient = new RESTClient( 'http://localhost:11369' )
        def resp = restClient.get( path : '/getMinigunner' ) // ACME boomerang
        assert resp.status == 200  // HTTP response code; 404 means not found, etc.
        GDIMinigunner minigunner = new GDIMinigunner()
        minigunner.x = resp.responseData.x
        minigunner.y = resp.responseData.y
        return minigunner

    }
}
