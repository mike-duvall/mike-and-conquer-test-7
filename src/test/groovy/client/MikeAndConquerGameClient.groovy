package client

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import main.Minigunner
import org.apache.http.params.CoreConnectionPNames

class MikeAndConquerGameClient {

    // Document firewall setup


    String hostUrl
    RESTClient  restClient

    MikeAndConquerGameClient(String host, int port, boolean useTimeouts = true) {
        hostUrl = "http://$host:$port"
        restClient = new RESTClient(hostUrl)

        if(useTimeouts) {
            restClient.client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, new Integer(3000))
            restClient.client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Integer(3000))
        }
    }

    void addMinigunner(int minigunnerX, int minigunnerY, String aPath) {
        def resp = restClient.post(
                path: aPath,
                body: [ x: minigunnerX, y: minigunnerY ],
                requestContentType: 'application/json' )

        assert resp.status == 200
    }

    void resetGame() {
        def resp = restClient.post(
                path: '/mac/resetGame',
                requestContentType: 'application/json' )

        assert resp.status == 200
    }

    Minigunner addGDIMinigunner(int minigunnerX, int minigunnerY) {
        def resp = restClient.post(
                path: '/mac/gdiMinigunners',
                body: [ x: minigunnerX, y: minigunnerY ],
                requestContentType: 'application/json' )

        assert resp.status == 200

        Minigunner minigunner = new Minigunner()
        minigunner.id = resp.responseData.id
        minigunner.x = resp.responseData.x
        minigunner.y = resp.responseData.y
        minigunner.health = resp.responseData.health
        return minigunner
    }

    void addNODMinigunner(int minigunnerX, int minigunnerY) {
        addMinigunner(minigunnerX, minigunnerY, '/mac/nodMinigunner' )
    }



    Minigunner getMinigunner(String aPath) {
        def resp = restClient.get( path : aPath )
        assert resp.status == 200  // HTTP response code; 404 means not found, etc.
        Minigunner minigunner = new Minigunner()
        minigunner.x = resp.responseData.x
        minigunner.y = resp.responseData.y
        minigunner.health = resp.responseData.health
        return minigunner
    }


    List<Minigunner> getGDIMinigunners() {
        String aPath = '/mac/gdiMinigunners'
        def resp
        resp = restClient.get(path: aPath)

        assert resp.status == 200  // HTTP response code; 404 means not found, etc.

        int numItems = resp.responseData.size

        List<Minigunner> allMinigunnersList = []
        for (int i = 0; i < numItems; i++) {
            Minigunner newMingunner = new Minigunner()
            newMingunner.x = resp.responseData[i]['x']
            newMingunner.y = resp.responseData[i]['y']
            newMingunner.health = resp.responseData[i]['health']
            allMinigunnersList.add(newMingunner)
        }
        return allMinigunnersList
    }


    Minigunner getMinigunner(int minigunnerX, int mingunnerY) {
        String aPath = '/mac/gdiMinigunner'
        def resp
        try {
            resp = restClient.get(path: aPath, query: [x: minigunnerX, y: mingunnerY])
        }
        catch(HttpResponseException e) {
            if(e.statusCode == 404) {
                return null
            }
            else {
                throw e
            }
        }
        if( resp.status == 404) {
            return null
        }
        assert resp.status == 200  // HTTP response code; 404 means not found, etc.
        Minigunner minigunner = new Minigunner()
        minigunner.x = resp.responseData.x
        minigunner.y = resp.responseData.y
        minigunner.health = resp.responseData.health
        return minigunner
    }

    String getGameState() {
        def resp = restClient.get( path : '/mac/gameState' )
        assert resp.status == 200  // HTTP response code; 404 means not found, etc.
        return resp.responseData.gameState
        Minigunner minigunner = new Minigunner()
    }



//    Minigunner getGDIMinigunner() {
//        return getMinigunner('/mac/gdiMinigunner')
//    }


    Minigunner getMinigunnerAtLocation(int x, int y) {
//        return getMinigunner('/mac/gdiMinigunner')
        return getMinigunner(x, y)
    }


    Minigunner getNODMinigunner() {
        return getMinigunner('/mac/nodMinigunner')
    }

    void leftClick(int mouseX, int mouseY) {
        def resp = restClient.post(
                path: '/mac/leftClick',
                body: [ x: mouseX, y: mouseY ],
                requestContentType: 'application/json' )

        assert resp.status == 200

    }

    Minigunner getMinigunnerById(int minigunnerId) {

        String aPath = '/mac/gdiMinigunners/' + minigunnerId
        def resp
        try {
            resp = restClient.get(path: aPath)
        }
        catch(HttpResponseException e) {
            if(e.statusCode == 404) {
                return null
            }
            else {
                throw e
            }
        }
        if( resp.status == 404) {
            return null
        }
        assert resp.status == 200  // HTTP response code; 404 means not found, etc.
        Minigunner minigunner = new Minigunner()
        minigunner.x = resp.responseData.x
        minigunner.y = resp.responseData.y
        minigunner.health = resp.responseData.health
        minigunner.id = resp.responseData.id
        minigunner.selected = resp.responseData.selected
        return minigunner
    }
}
