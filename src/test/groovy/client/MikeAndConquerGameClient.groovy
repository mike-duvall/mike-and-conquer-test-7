package client

import domain.GDIConstructionYard
import domain.Sidebar
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import domain.MCV
import domain.Minigunner
import domain.MinigunnerId
import domain.Point
import domain.ResetOptions
import domain.Sandbag
import util.Util
import org.apache.http.params.CoreConnectionPNames

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

class MikeAndConquerGameClient {


    String hostUrl
    RESTClient  restClient

    private static final String GDI_MINIGUNNERS_BASE_URL = '/mac/gdiMinigunners'
    private static final String NOD_MINIGUNNERS_BASE_URL = '/mac/nodMinigunners'
    private static final String MCV_BASE_URL = '/mac/MCV'
    private static final String GDI_CONSTRUCTION_YARD = '/mac/GDIConstructionYard'
    private static final String SIDEBAR_BASE_URL = '/mac/Sidebar'


    MikeAndConquerGameClient(String host, int port, boolean useTimeouts = true) {
        hostUrl = "http://$host:$port"
        restClient = new RESTClient(hostUrl)

        if(useTimeouts) {
            restClient.client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, new Integer(3000))
            restClient.client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Integer(3000))
        }
    }

    void resetGame() {
        ResetOptions resetOptions = new ResetOptions(false)
        resetGame(resetOptions)
    }

    void resetGame(ResetOptions resetOptions) {
        def resp = restClient.post(
                path: '/mac/resetGame',
                body: resetOptions,
                requestContentType: 'application/json' )

        assert resp.status == 204
    }

    void leftClickInWorldCoordinates(int x, int y) {
        Point point = new Point()
        point.x = x
        point.y = y

        def resp = restClient.post(
                path: '/mac/leftClickInWorldCoordinates',
                body: point,
                requestContentType: 'application/json' )

        assert resp.status == 200
    }


    void leftClickInMapSquareCoordinates(int x, int y) {
        Point worldCoordinates = Util.convertMapSqaureCoordinatesToWorldCoordinates(x,y)
        def resp = restClient.post(
                path: '/mac/leftClickInWorldCoordinates',
                body: worldCoordinates,
                requestContentType: 'application/json' )

        assert resp.status == 200
    }


    void moveMouseToWorldCoordinates(Point point) {

        def resp = restClient.post(
                path: '/mac/moveMouseToWorldCoordinates',
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


    void leftClickMCV(int mcvId) {
        MinigunnerId minigunnerId1 = new MinigunnerId()
        minigunnerId1.id = mcvId


        def resp = restClient.post(
                path: '/mac/leftClickMCV',
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


    String getMouseCursorState() {
        def resp = restClient.get( path : '/mac/mouseCursorState' )
        assert resp.status == 200  // HTTP response code; 404 means not found, etc.
        return resp.responseData.cursorState
    }

    def addSandbag(int x, int y, int index) {

        Sandbag sandbag = new Sandbag(x,y,index)

        def resp = restClient.post(
                path: '/mac/sandbag',
                body: sandbag,
                requestContentType: 'application/json' )

        assert resp.status == 200

    }

    Minigunner addMinigunnerAtWorldCoordinates(String baseUrl, int minigunnerX, int minigunnerY, boolean aiIsOn) {
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


    Minigunner addGDIMinigunnerAtWorldCoordinates(int minigunnerX, int minigunnerY) {
        boolean aiIsOn = false
        return addMinigunnerAtWorldCoordinates(GDI_MINIGUNNERS_BASE_URL, minigunnerX, minigunnerY, aiIsOn)
    }


    def addGDIMinigunnerAtMapSquare(int x, int y) {
        int halfMapSquareWidth = Util.mapSquareWidth / 2
        int worldX = (x * Util.mapSquareWidth) + halfMapSquareWidth
        int worldY = (y * Util.mapSquareWidth) + halfMapSquareWidth

        return addGDIMinigunnerAtWorldCoordinates(worldX, worldY)
    }

    MCV addMCVAtMapSquare(int x, int y) {
        int halfMapSquareWidth = Util.mapSquareWidth / 2
        int worldX = (x * Util.mapSquareWidth) + halfMapSquareWidth
        int worldY = (y * Util.mapSquareWidth) + halfMapSquareWidth

        MCV inputMCV = new MCV()
        inputMCV.x = worldX
        inputMCV.y = worldY
        def resp = restClient.post(
                path: MCV_BASE_URL,
                body:   inputMCV ,
                requestContentType: 'application/json' )

        assert resp.status == 200

        MCV createdMCV = new MCV()
//        createdMCV.id = resp.responseData.id
        createdMCV.x = resp.responseData.x
        createdMCV.y = resp.responseData.y

        return createdMCV
    }



    def deleteGdiMinigunnerById(int minigunnerId) {

        MinigunnerId minigunnerId1 = new MinigunnerId()
        minigunnerId1.id = minigunnerId

        String aPath = GDI_MINIGUNNERS_BASE_URL + '/' + minigunnerId

        def resp = restClient.delete(
                path: aPath,
                requestContentType: 'application/json' )

        assert resp.status == 204


    }

    Minigunner addNodMinigunnerAtWorldCoordinates(int minigunnerX, int minigunnerY, boolean aiIsOn) {
        return addMinigunnerAtWorldCoordinates(NOD_MINIGUNNERS_BASE_URL, minigunnerX, minigunnerY, aiIsOn)
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
        minigunner.destinationX = resp.responseData.destinationX
        minigunner.destinationY = resp.responseData.destinationY
        return minigunner
    }

    GDIConstructionYard getGDIConstructionYard() {

        String aPath = GDI_CONSTRUCTION_YARD
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
//        Minigunner minigunner = new Minigunner()
//        minigunner.x = resp.responseData.x
//        minigunner.y = resp.responseData.y
//        minigunner.screenX = resp.responseData.screenX
//        minigunner.screenY = resp.responseData.screenY
//        minigunner.health = resp.responseData.health
//        minigunner.id = resp.responseData.id
//        minigunner.selected = resp.responseData.selected
//        minigunner.destinationX = resp.responseData.destinationX
//        minigunner.destinationY = resp.responseData.destinationY
//        return minigunner
        GDIConstructionYard gdiConstructionYard = new GDIConstructionYard()
        gdiConstructionYard.x = resp.responseData.x
        gdiConstructionYard.y = resp.responseData.y
        return gdiConstructionYard
    }



    MCV getMCV() {
        def resp
        try {
            resp = restClient.get(path: MCV_BASE_URL)
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
        MCV mcv = new MCV()
        mcv.x = resp.responseData.x
        mcv.y = resp.responseData.y
        return mcv
    }

    Sidebar getSidebar() {
        def resp
        try {
            resp = restClient.get(path: SIDEBAR_BASE_URL)
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
        Sidebar sidebar = new Sidebar()
        sidebar.buildMinigunnerEnabled = resp.responseData.buildMinigunnerEnabled
        sidebar.buildBarracksEnabled = resp.responseData.buildBarracksEnabled
        return sidebar

    }


    BufferedImage  getScreenshot() {
        def resp = restClient.get( path : '/mac/screenshot' )
        ByteArrayInputStream byteArrayInputStream = resp.responseData
        BufferedImage screenShotImage = ImageIO.read(byteArrayInputStream)
        return screenShotImage
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

    void dragSelect(int x1, int y1, int x2, int y2) {

        Point point1 = new Point()
        point1.x = x1
        point1.y = y1

        Point point2 = new Point()
        point2.x = x2
        point2.y = y2

        def resp = restClient.post(
                path: '/mac/leftClickAndHoldInWorldCoordinates',
                body: point1,
                requestContentType: 'application/json' )

        assert resp.status == 200

        resp = restClient.post(
                path: '/mac/moveMouseToWorldCoordinates',
                body: point2,
                requestContentType: 'application/json' )

        assert resp.status == 200


        resp = restClient.post(
                path: '/mac/releaseLeftMouseClick',
                body: point2,
                requestContentType: 'application/json' )

        assert resp.status == 200


    }
}
