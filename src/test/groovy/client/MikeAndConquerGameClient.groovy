package client

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import main.Minigunner
import main.MinigunnerId
import main.Point
import org.apache.http.params.CoreConnectionPNames

class MikeAndConquerGameClient {

    // Document firewall setup


    String hostUrl
    RESTClient  restClient


    private static final String GDI_MINIGUNNERS_BASE_URL = '/mac/gdiMinigunners'
    private static final String NOD_MINIGUNNERS_BASE_URL = '/mac/nodMinigunners'


    MikeAndConquerGameClient(String host, int port, boolean useTimeouts = true) {
        hostUrl = "http://$host:$port"
        restClient = new RESTClient(hostUrl)

        if(useTimeouts) {
            restClient.client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, new Integer(3000))
            restClient.client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Integer(3000))
        }
    }

    void resetGame() {
        def resp = restClient.post(
                path: '/mac/resetGame',
                requestContentType: 'application/json' )

        assert resp.status == 204
    }



    void leftClick(int mouseX, int mouseY) {
        Point point = new Point();
        point.x = mouseX
        point.y = mouseY

        def resp = restClient.post(
                path: '/mac/leftClick',
                body: point,
                requestContentType: 'application/json' )

        assert resp.status == 200
    }


    void leftClickMinigunner(int minigunnerId) {
        MinigunnerId minigunnerId1 = new MinigunnerId()
        minigunnerId1.id = minigunnerId


        def resp = restClient.post(
                path: '/mac/leftClickMinigunner',
                body: minigunnerId1,
                requestContentType: 'application/json' )

        assert resp.status == 200
    }



    void rightClick(int mouseX, int mouseY) {
        Point point = new Point()
        point.x = mouseX
        point.y = mouseY


        def resp = restClient.post(
                path: '/mac/rightClick',
                body: point,
                requestContentType: 'application/json' )

        assert resp.status == 200
    }


    String getGameState() {
        def resp = restClient.get( path : '/mac/gameState' )
        assert resp.status == 200  // HTTP response code; 404 means not found, etc.
        return resp.responseData.gameState
    }

    Minigunner addMinigunner(String baseUrl, int minigunnerX, int minigunnerY, boolean aiIsOn) {
        Minigunner inputMinigunner = new Minigunner()
        inputMinigunner.x = minigunnerX
        inputMinigunner.y = minigunnerY
        inputMinigunner.aiIsOn = aiIsOn
        def resp = restClient.post(
                path: baseUrl,
                body:   inputMinigunner ,
                requestContentType: 'application/json' )

        assert resp.status == 200

        Minigunner minigunner = new Minigunner()
        minigunner.id = resp.responseData.id
        minigunner.x = resp.responseData.x
        minigunner.y = resp.responseData.y
        minigunner.health = resp.responseData.health
        return minigunner
    }


    Minigunner addGDIMinigunner(int minigunnerX, int minigunnerY) {
        boolean aiIsOn = false
        return addMinigunner(GDI_MINIGUNNERS_BASE_URL, minigunnerX, minigunnerY, aiIsOn)
    }

    Minigunner addNODMinigunner(int minigunnerX, int minigunnerY, boolean aiIsOn) {
        return addMinigunner(NOD_MINIGUNNERS_BASE_URL, minigunnerX, minigunnerY, aiIsOn)
    }



    Minigunner getMinigunnerById(String baseUrl, int minigunnerId) {

        String aPath = baseUrl + '/' + minigunnerId
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
        minigunner.screenX = resp.responseData.screenX
        minigunner.screenY = resp.responseData.screenY
        minigunner.health = resp.responseData.health
        minigunner.id = resp.responseData.id
        minigunner.selected = resp.responseData.selected
        return minigunner
    }

    Minigunner getGdiMinigunnerById(int minigunnerId) {
        return getMinigunnerById(GDI_MINIGUNNERS_BASE_URL, minigunnerId)
    }

    Minigunner getNodMinigunnerById(int minigunnerId) {
        return getMinigunnerById(NOD_MINIGUNNERS_BASE_URL, minigunnerId)
    }


    List<Minigunner> getGdiMinigunners() {
        String aPath = GDI_MINIGUNNERS_BASE_URL
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


    Minigunner getGdiMinigunnerAtLocation(int minigunnerX, int mingunnerY) {
        String aPath = GDI_MINIGUNNERS_BASE_URL
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

}
