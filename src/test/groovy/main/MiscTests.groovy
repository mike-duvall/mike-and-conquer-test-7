package main

import domain.GameHistoryEvent
import domain.MCV
import domain.NodTurret
import groovyx.net.http.HttpResponseException
import domain.Minigunner
import domain.Point
import util.Util
import spock.lang.Ignore
import spock.lang.Unroll
import spock.util.concurrent.PollingConditions

class MiscTests extends MikeAndConquerTestBase {


    def setup() {
        boolean showShroud = false
        float initialMapZoom = 1
        setAndAssertGameOptions(showShroud, initialMapZoom, GameSpeed.Fastest)
    }



    def "clicking nod mingunner should not initiate attack unless gdi minigunner is selected" () {

        given:
        Minigunner gdiMinigunner = createRandomGDIMinigunner()
        Minigunner nodMinigunner = createRandomNodMinigunnerWithAiTurnedOff()

        when:
        gameClient.leftClickMinigunner(nodMinigunner.id)

        and:
        int TWO_SECONDS_IN_MILLIS = 2000
        sleep( TWO_SECONDS_IN_MILLIS )

        then:
        Minigunner retrievedGdiMinigunner = gameClient.getGdiMinigunnerById(gdiMinigunner.id)
        assert retrievedGdiMinigunner.x == gdiMinigunner.x
        assert retrievedGdiMinigunner.y == gdiMinigunner.y
    }

    def "Nod successively attacks two gdi minigunners"() {
        given:
        Minigunner gdiMinigunner1 = createRandomGDIMinigunner()
        Minigunner gdiMinigunner2 = createRandomGDIMinigunner()

        when:
        createRandomNodMinigunnerWithAiTurnedOn()

        then:
        assertGdiMinigunnerDies(gdiMinigunner1.id)
        assertGdiMinigunnerDies(gdiMinigunner2.id)
        assertGameStateGoesToMissionFailed()
    }

    def "Multiple Nod attack superior GDI forces"() {
        given:

        Minigunner gdiMinigunner1 = createRandomGDIMinigunner()
        Minigunner gdiMinigunner2 = createRandomGDIMinigunner()
        Minigunner gdiMinigunner3 = createRandomGDIMinigunner()

        when:
        createRandomNodMinigunnerWithAiTurnedOn()
        createRandomNodMinigunnerWithAiTurnedOn()

        then:
        assertGdiMinigunnerDies(gdiMinigunner1.id)
        assertGdiMinigunnerDies(gdiMinigunner2.id)
        assertGdiMinigunnerDies(gdiMinigunner3.id)
        assertGameStateGoesToMissionFailed()
    }


    def "Nod Turret kills GDI minigunner"() {
        given:
        Minigunner gdiMinigunner = createGDIMinigunnerAtLocation(420,250)

        when:
        float direction = 90.0 - 11.25
        gameClient.addNodTurret(14,12,direction, 0)

        and:
        gdiMinigunner = gameClient.getGdiMinigunnerById(gdiMinigunner.id)

        then:
        assert gdiMinigunner.health == 50

        then:
        assertGdiMinigunnerHealthGoesTo(gdiMinigunner.id, 40)
        assertGdiMinigunnerHealthGoesTo(gdiMinigunner.id, 30)
        assertGdiMinigunnerHealthGoesTo(gdiMinigunner.id, 20)
        assertGdiMinigunnerHealthGoesTo(gdiMinigunner.id, 10)

        then:
        assertGdiMinigunnerDies(gdiMinigunner.id)
    }

    def "Minigunner health bar and selection cursor show up correctly"() {
        given:
        boolean showShroud = false
        float initialMapZoom = 1
        setAndAssertGameOptions(showShroud, initialMapZoom, GameSpeed.Slowest)

        Minigunner gdiMinigunner = createGDIMinigunnerAtLocation(420,250)

        when:
        float direction = 90.0 - 11.25
        gameClient.addNodTurret(14,12,direction, 0)

        then:
        assertGdiMinigunnerHealthGoesTo(gdiMinigunner.id, 40)

        when:
        gameClient.leftClickMinigunner(gdiMinigunner.id)
        int testScenarioNumber = 1
        String scenarioPrefix = 'minigunner-selection-and-health-bar'

        int startX = 414
        int startY = 236
        int screenshotCompareWidth = 13
        int screenshotCompareHeight = 17

        then:
        assertScreenshotMatches(scenarioPrefix, testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)

    }


    def "MCV health bar and selection cursor show up correctly"() {
        given:
        Point mcvLocation = new Point(17,12)
        MCV anMCV = gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:
        float direction = 90.0 - 11.25
        gameClient.addNodTurret(14,12,direction, 0)

//        then:
//        assertGdiMinigunnerHealthGoesTo(gdiMinigunner.id, 40)

        and:
//        gameClient.leftClickMinigunner(gdiMinigunner.id)
        gameClient.leftClickMCV(anMCV.id)
        int testScenarioNumber = 1
        String scenarioPrefix = 'mcv-selection-and-health-bar'

        int startX = 402
        int startY = 282
        int screenshotCompareWidth = 37
        int screenshotCompareHeight = 37

        then:
        assertScreenshotMatches(scenarioPrefix, testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)

    }



    // Unfortunately, these have to be static(or @Shared) to be accessible in the "where" block
    // https://stackoverflow.com/questions/22707195/how-to-use-instance-variable-in-where-section-of-spock-test
    static int selectionBoxLeftmostX = 75
    static int selectionBoxRightmostX = 100
    static int selectionBoxTopmostY = 350
    static int selectionBoxBottommostY = 400

    @Unroll
    def "should be able to drag select multiple GDI minigunners" () {

        when:
        Minigunner gdiMinigunner1 = createGDIMinigunnerAtLocation(82,369)
        Minigunner gdiMinigunner2 = createGDIMinigunnerAtLocation(92,380)

        Minigunner gdiMinigunner3 = createGDIMinigunnerAtLocation(230,300)
        Minigunner gdiMinigunner4 = createGDIMinigunnerAtLocation(82,300)

        then:
        assert gdiMinigunner1.selected == false
        assert gdiMinigunner2.selected == false
        assert gdiMinigunner3.selected == false
        assert gdiMinigunner4.selected == false

        when:
        gameClient.dragSelect(dragStartX, dragStartY, dragEndX, dragEndY)

        and:
        gdiMinigunner1 = gameClient.getGdiMinigunnerById(gdiMinigunner1.id)
        gdiMinigunner2 = gameClient.getGdiMinigunnerById(gdiMinigunner2.id)
        gdiMinigunner3 = gameClient.getGdiMinigunnerById(gdiMinigunner3.id)
        gdiMinigunner4 = gameClient.getGdiMinigunnerById(gdiMinigunner4.id)


        then:
        assert gdiMinigunner1.selected == true
        assert gdiMinigunner2.selected == true
        assert gdiMinigunner3.selected == false
        assert gdiMinigunner4.selected == false

        when:
        gameClient.rightClick(10,10)

        and:
        gdiMinigunner1 = gameClient.getGdiMinigunnerById(gdiMinigunner1.id)
        gdiMinigunner2 = gameClient.getGdiMinigunnerById(gdiMinigunner2.id)
        gdiMinigunner3 = gameClient.getGdiMinigunnerById(gdiMinigunner3.id)


        then:
        assert gdiMinigunner1.selected == false
        assert gdiMinigunner2.selected == false
        assert gdiMinigunner3.selected == false


        where:
        dragStartX              | dragStartY                | dragEndX                  | dragEndY
        selectionBoxLeftmostX   | selectionBoxTopmostY      | selectionBoxRightmostX    | selectionBoxBottommostY    // Top left to bottom right
        selectionBoxRightmostX  | selectionBoxBottommostY   | selectionBoxLeftmostX     | selectionBoxTopmostY    // Bottom right to top left
        selectionBoxRightmostX  | selectionBoxTopmostY      | selectionBoxLeftmostX     | selectionBoxBottommostY    // Top right to bottom left
        selectionBoxLeftmostX   | selectionBoxBottommostY   | selectionBoxRightmostX    | selectionBoxTopmostY    // Bottom left to top right

    }

    def "two gdi minigunners attack two nod minigunners" () {
        given:
        Minigunner gdiMinigunner1 = createRandomGDIMinigunner()
        Minigunner gdiMinigunner2 = createRandomGDIMinigunner()

        Minigunner nodMinigunner1 = createRandomNodMinigunnerWithAiTurnedOff()
        Minigunner nodMinigunner2 = createRandomNodMinigunnerWithAiTurnedOff()

        when:
        gameClient.leftClickMinigunner(gdiMinigunner1.id)
        gameClient.leftClickMinigunner(nodMinigunner1.id)

        and:
        gameClient.leftClickMinigunner(gdiMinigunner2.id)
        gameClient.leftClickMinigunner(nodMinigunner2.id)


        then:
        assertNodMinigunnerDies(nodMinigunner1.id)

        and:
        assertNodMinigunnerDies(nodMinigunner2.id)

    }

//    def "GDI minigunner attacks Nod minigunner with correct damage and correct reload time"() {
//        given:
//        boolean showShroud = false
//        float initialMapZoom = 3
//        int expectedReloadDelayInMillseconds = 4500
//        int expectedReloadDelayCeilingInMilliseconds = expectedReloadDelayInMillseconds + 500
//        int expectedReloadDelayFloorInMilliseconds = expectedReloadDelayInMillseconds - 500
//        //  Delay for Slowest is 4.5 seconds, as measured in game
//        setAndAssertGameOptions(showShroud, initialMapZoom, GameSpeed.Slowest)
////        gameClient.turnOnGameHistoryEvents()
//        // gameClient.clearGameHistoryEvents()
////        turn them off in setup()
//        Minigunner gdiMinigunner = gameClient.addGDIMinigunnerAtMapSquare(15,10)
//        boolean aiIsOn = false
//        Minigunner nodMinigunner = gameClient.addNodMinigunnerAtMapSquare(17,10, aiIsOn)
////        Time starTime = now()
//        long startTime = new Date().getTime()
//
//        when:
//        gameClient.leftClickMinigunner(gdiMinigunner.id)
//        gameClient.leftClickMinigunner(nodMinigunner.id)
//
//        then:
//        assertNodMinigunnerDies(nodMinigunner.id)
//
//        when:
//        List<GameHistoryEvent> gameHistoryEventList = gameClient.getGameHistoryEvents()
//
//        then:
//        assert gameHistoryEventList.size() == 5
//
//        when:
//        GameHistoryEvent gameHistoryEvent1 = gameHistoryEventList.get(0)
//        GameHistoryEvent gameHistoryEvent2 = gameHistoryEventList.get(1)
//        GameHistoryEvent gameHistoryEvent3 = gameHistoryEventList.get(2)
//        GameHistoryEvent gameHistoryEvent4 = gameHistoryEventList.get(3)
//        GameHistoryEvent gameHistoryEvent5 = gameHistoryEventList.get(4)
//
//        then:
//        assert gameHistoryEvent1.eventType == "FirePrimaryWeapon"
//        assert gameHistoryEvent1.unitId == gdiMinigunner.id
//        assert gameHistoryEvent1.wallClockTime > startTime
//
//        when:
//        int reloadTime1 = gameHistoryEvent2.wallClockTime - gameHistoryEvent1.wallClockTime
//        int reloadTime2 = gameHistoryEvent3.wallClockTime - gameHistoryEvent2.wallClockTime
//        int reloadTime3 = gameHistoryEvent4.wallClockTime - gameHistoryEvent3.wallClockTime
//        int reloadTime4 = gameHistoryEvent5.wallClockTime - gameHistoryEvent4.wallClockTime
//
//        then:
//        assert reloadTime1 < expectedReloadDelayCeilingInMilliseconds
//        assert reloadTime1 > expectedReloadDelayFloorInMilliseconds
//
//        assert reloadTime2 < expectedReloadDelayCeilingInMilliseconds
//        assert reloadTime2 > expectedReloadDelayFloorInMilliseconds
//
//        assert reloadTime3 < expectedReloadDelayCeilingInMilliseconds
//        assert reloadTime3 > expectedReloadDelayFloorInMilliseconds
//
//        assert reloadTime4 < expectedReloadDelayCeilingInMilliseconds
//        assert reloadTime4 > expectedReloadDelayFloorInMilliseconds
//    }
//
//    def "GDI minigunner attacks Nod minigunner with correct damage and correct reload time for Normal time"() {
//        given:
//        boolean showShroud = false
//        float initialMapZoom = 3
//        int expectedReloadDelayInMillseconds = 750
//        int expectedReloadDelayCeilingInMilliseconds = expectedReloadDelayInMillseconds + 10
//        int expectedReloadDelayFloorInMilliseconds = expectedReloadDelayInMillseconds - 10
//
//        setAndAssertGameOptions(showShroud, initialMapZoom, GameSpeed.Normal)
//        Minigunner gdiMinigunner = gameClient.addGDIMinigunnerAtMapSquare(15,10)
//        boolean aiIsOn = false
//        Minigunner nodMinigunner = gameClient.addNodMinigunnerAtMapSquare(17,10, aiIsOn)
//        long startTime = new Date().getTime()
//
//        when:
//        gameClient.leftClickMinigunner(gdiMinigunner.id)
//        gameClient.leftClickMinigunner(nodMinigunner.id)
//
//        then:
//        assertNodMinigunnerDies(nodMinigunner.id)
//
//        when:
//        List<GameHistoryEvent> gameHistoryEventList = gameClient.getGameHistoryEvents()
//
//        then:
//        assert gameHistoryEventList.size() == 5
//
//        and:
//        GameHistoryEvent previousEvent = null
//        for(GameHistoryEvent event : gameHistoryEventList) {
//            assert event.eventType == "FirePrimaryWeapon"
//            assert event.unitId == gdiMinigunner.id
//            assert event.wallClockTime > startTime
//            if(previousEvent != null) {
//                int reloadTime = event.wallClockTime - previousEvent.wallClockTime
//                assert reloadTime < expectedReloadDelayCeilingInMilliseconds
//                assert reloadTime > expectedReloadDelayFloorInMilliseconds
//
//            }
//            previousEvent = event
//        }
//
//    }


    @Unroll
    def "GDI minigunner attacks Nod minigunner with correct damage and correct reload time of #expectedReloadDelayInMillseconds for game speed delay #gameSpeed"() {
        given:
        boolean showShroud = false
        float initialMapZoom = 3
        int allowedDelta = 15
//        int allowedDelta = 50
        int expectedReloadDelayCeilingInMilliseconds = expectedReloadDelayInMillseconds + allowedDelta
        int expectedReloadDelayFloorInMilliseconds = expectedReloadDelayInMillseconds - allowedDelta

        setAndAssertGameOptions(showShroud, initialMapZoom, gameSpeed)
        Minigunner gdiMinigunner = gameClient.addGDIMinigunnerAtMapSquare(15,10)
        boolean aiIsOn = false
        Minigunner nodMinigunner = gameClient.addNodMinigunnerAtMapSquare(17,10, aiIsOn)
        long startTime = new Date().getTime()

        when:
        gameClient.leftClickMinigunner(gdiMinigunner.id)
        gameClient.leftClickMinigunner(nodMinigunner.id)

        then:
        assertNodMinigunnerDies(nodMinigunner.id)

        when:
        List<GameHistoryEvent> gameHistoryEventList = gameClient.getGameHistoryEvents()

        then:
        assert gameHistoryEventList.size() == 5

        and:
        GameHistoryEvent previousEvent = null
        for(GameHistoryEvent event : gameHistoryEventList) {
            assert event.eventType == "FirePrimaryWeapon"
            assert event.unitId == gdiMinigunner.id
            assert event.wallClockTime > startTime
            if(previousEvent != null) {
                int reloadTime = event.wallClockTime - previousEvent.wallClockTime
                println "reloadTime=" + reloadTime
                assert reloadTime < expectedReloadDelayCeilingInMilliseconds
                assert reloadTime >= expectedReloadDelayFloorInMilliseconds

            }
            previousEvent = event
        }

        where:
        gameSpeed               | expectedReloadDelayInMillseconds
        GameSpeed.Slowest       | 4500
        GameSpeed.Normal        | 750
        GameSpeed.Fastest       | 425

    }




    def "should handle selecting deselecting gdi minigunner"() {
        given:
        Minigunner gdiMinigunner1 = createRandomGDIMinigunner()

        when:
        Minigunner retrievedMinigunner1 = gameClient.getGdiMinigunnerById(gdiMinigunner1.id)

        then:
        assert retrievedMinigunner1.selected == false

        when:
        gameClient.leftClickMinigunner(gdiMinigunner1.id)

        and:
        retrievedMinigunner1 = gameClient.getGdiMinigunnerById(gdiMinigunner1.id)

        then:
        assert retrievedMinigunner1.selected == true

        when:
        gameClient.rightClick(200,200)

        and:
        retrievedMinigunner1 = gameClient.getGdiMinigunnerById(gdiMinigunner1.id)

        then:
        assert retrievedMinigunner1.selected == false
    }

    def "should handle selecting a different player unit when player unit already selected"() {
        given:
        Minigunner gdiMinigunner1 = createRandomGDIMinigunner()
        Minigunner gdiMinigunner2 = createRandomGDIMinigunner()

        when:
        gameClient.leftClickMinigunner(gdiMinigunner1.id)

        and:
        Minigunner retrievedMinigunner1 = gameClient.getGdiMinigunnerById(gdiMinigunner1.id)
        Minigunner retrievedMinigunner2 = gameClient.getGdiMinigunnerById(gdiMinigunner2.id)

        then:
        assert retrievedMinigunner1.selected == true
        assert retrievedMinigunner2.selected == false

        when:
        gameClient.leftClickMinigunner(gdiMinigunner2.id)

        and:
        retrievedMinigunner1 = gameClient.getGdiMinigunnerById(gdiMinigunner1.id)
        retrievedMinigunner2 = gameClient.getGdiMinigunnerById(gdiMinigunner2.id)

        then:
        assert retrievedMinigunner1.selected == false
        assert retrievedMinigunner2.selected == true

        assert retrievedMinigunner1.x == gdiMinigunner1.x
        assert retrievedMinigunner1.y == gdiMinigunner1.y

        assert retrievedMinigunner2.x == gdiMinigunner2.x
        assert retrievedMinigunner2.y == gdiMinigunner2.y

    }

    def "should set mouse cursor correctly when minigunner is selected" () {

        given:
        Minigunner gdiMinigunner = createRandomGDIMinigunner()
        Point mountainSquareLocation = new Point(79,20)
        Point clearSquare = new Point(10,10)
        Point overMapButNotOverTerrain = new Point(675,20)


        when:
        gameClient.leftClickMinigunner(gdiMinigunner.id)
        gdiMinigunner = gameClient.getGdiMinigunnerById(gdiMinigunner.id)

        then:
        assert gdiMinigunner.selected == true


        and:
        gameClient.moveMouseToWorldCoordinates(overMapButNotOverTerrain)

        then:
        String mouseCursorState = gameClient.getMouseCursorState()
        assert mouseCursorState == "MovementNoteAllowedCursor"

        when:
        gameClient.moveMouseToWorldCoordinates(mountainSquareLocation)
        mouseCursorState = gameClient.getMouseCursorState()

        then:
        assert mouseCursorState == "MovementNoteAllowedCursor"

        when:
        gameClient.moveMouseToWorldCoordinates(clearSquare)
        mouseCursorState = gameClient.getMouseCursorState()

        then:
        assert mouseCursorState == "MoveToLocationCursor"


        when:
        Minigunner nodMinigunner = createRandomNodMinigunnerWithAiTurnedOff()
        gameClient.moveMouseToWorldCoordinates(new Point(nodMinigunner.x, nodMinigunner.y))
        mouseCursorState = gameClient.getMouseCursorState()

        then:
        assert mouseCursorState == "AttackEnemyCursor"

        when:
        gameClient.rightClick(20,20)
        mouseCursorState = gameClient.getMouseCursorState()

        then:
        assert mouseCursorState == "DefaultArrowCursor"

    }

    def "should set mouse cursor correctly when unit is NOT selected" () {

        given:
        Minigunner gdiMinigunner = createRandomGDIMinigunner()
        Point mountainSquareLocation = new Point(79,20)
        Point clearSquare = new Point(10,10)

        when:
        gameClient.moveMouseToWorldCoordinates(mountainSquareLocation)

        then:
        String mouseCursorState = gameClient.getMouseCursorState()
        assert mouseCursorState == "DefaultArrowCursor"

        when:
        gameClient.moveMouseToWorldCoordinates(clearSquare)
        mouseCursorState = gameClient.getMouseCursorState()

        then:
        assert mouseCursorState == "DefaultArrowCursor"


        when:
        Minigunner nodMinigunner = createRandomNodMinigunnerWithAiTurnedOff()
        gameClient.moveMouseToWorldCoordinates(new Point(nodMinigunner.x, nodMinigunner.y))
        mouseCursorState = gameClient.getMouseCursorState()

        then:
        assert mouseCursorState == "DefaultArrowCursor"

        when:
        gameClient.rightClick(20,20)
        mouseCursorState = gameClient.getMouseCursorState()

        then:
        assert mouseCursorState == "DefaultArrowCursor"


    }

    def "should set mouse cursor correctly when MCV is selected" () {

        given:
        Point mcvLocation = new Point(21,12)
        MCV anMCV = gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        Point mountainSquareLocation = new Point(79,20)
        Point clearSquare = new Point(10,10)

        when:
        gameClient.leftClickMCV(666)

        and:
        gameClient.moveMouseToWorldCoordinates(mountainSquareLocation)

        then:
        String mouseCursorState = gameClient.getMouseCursorState()
        assert mouseCursorState == "MovementNoteAllowedCursor"

        when:
        gameClient.moveMouseToWorldCoordinates(clearSquare)
        mouseCursorState = gameClient.getMouseCursorState()

        then:
        assert mouseCursorState == "MoveToLocationCursor"

        when:
        Point mcvInWorldCoordinates = Util.convertMapSquareCoordinatesToWorldCoordinates(mcvLocation.x, mcvLocation.y)
        gameClient.moveMouseToWorldCoordinates(mcvInWorldCoordinates)
        mouseCursorState = gameClient.getMouseCursorState()

        then:
        assert mouseCursorState == "BuildConstructionYardCursor"

        when:
        gameClient.rightClick(20,20)
        mouseCursorState = gameClient.getMouseCursorState()

        then:
        assert mouseCursorState == "DefaultArrowCursor"

    }

    def "should be able to move two separate GDI minigunners" () {
        given:
        int minigunner1DestinationX = 300
        int minigunner1DestinationY = 100
        Minigunner createdMinigunner1 = createRandomGDIMinigunner()

        int minigunner2DestinationX = 320
        int minigunner2DestinationY = 140
        Minigunner createdMinigunner2 = createRandomGDIMinigunner()

        when:
        gameClient.leftClickMinigunner(createdMinigunner1.id)

        and:
        gameClient.leftClickInWorldCoordinates(minigunner1DestinationX, minigunner1DestinationY )

        and:
        gameClient.leftClickMinigunner(createdMinigunner2.id)

        and:
        gameClient.leftClickInWorldCoordinates(minigunner2DestinationX, minigunner2DestinationY )

        and:
        println "createdMinigunner1.x=" + createdMinigunner1.x
        println "createdMinigunner1.x=" + createdMinigunner1.y
        println "createdMinigunner2.x=" + createdMinigunner2.x
        println "createdMinigunner2.x=" + createdMinigunner2.y

        then:
        def conditions = new PollingConditions(timeout: 70, initialDelay: 1.5, factor: 1.25)
        conditions.eventually {
            Minigunner retrievedMinigunner = gameClient.getGdiMinigunnerById(createdMinigunner1.id)
            assertMinigunnerIsAtDesignatedDestination(retrievedMinigunner, minigunner1DestinationX, minigunner1DestinationY)
            assert retrievedMinigunner.health != 0
        }

        and:
        def conditions2 = new PollingConditions(timeout: 90, initialDelay: 1.5, factor: 1.25)
        conditions2.eventually {
            def retrievedMinigunner = gameClient.getGdiMinigunnerById(createdMinigunner2.id)
            assertMinigunnerIsAtDesignatedDestination(retrievedMinigunner, minigunner2DestinationX, minigunner2DestinationY)

            assert retrievedMinigunner.health != 0
        }


        and:
        String gameState = gameClient.getGameState()
        String expectedGameState = "Playing"  // not sure if Playing is correct state

        assert gameState == expectedGameState
    }


    def "should be able to move an MCV" () {
        given:
        int destinationX = 300
        int destinationY = 100
        Point mcvLocation = new Point(21,12)
        MCV anMCV = gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:
        gameClient.leftClickMCV(666)

        and:
        gameClient.leftClickInWorldCoordinates(destinationX, destinationY )

        then:
        assertMCVArrivesAtDestination(destinationX,destinationY )

        and:
        String gameState = gameClient.getGameState()
        String expectedGameState = "Playing"  // not sure if Playing is correct state

        assert gameState == expectedGameState
    }


    @Ignore
    def "minigunner should be able to navigate around simple sandbag obstacle" () {
        given:
        def createdMinigunner1 =  gameClient.addGDIMinigunnerAtMapSquare(11,2)

        and:
        gameClient.addSandbag(9,6, 5)
        gameClient.addSandbag(9,5, 5)
        gameClient.addSandbag(9,4, 10)
        gameClient.addSandbag(10,4, 10)
        gameClient.addSandbag(11,4, 10)
        gameClient.addSandbag(12,4, 10)
        gameClient.addSandbag(13,4, 10)
        gameClient.addSandbag(14,4, 10)
        gameClient.addSandbag(14,5, 5)
        gameClient.addSandbag(14,6, 5)

        gameClient.addSandbag(9,7, 10)
        gameClient.addSandbag(10,7, 10)
        gameClient.addSandbag(11,7, 10)
        gameClient.addSandbag(12,7, 10)

        gameClient.addSandbag(12,8, 5)
        gameClient.addSandbag(12,9, 5)
        gameClient.addSandbag(12,10, 5)
        gameClient.addSandbag(12,11, 5)
        gameClient.addSandbag(12,12, 5)

        gameClient.addSandbag(14,7, 5)
        gameClient.addSandbag(14,8, 5)
        gameClient.addSandbag(14,9, 5)
        gameClient.addSandbag(14,10, 5)

        gameClient.addSandbag(15,10, 10)
        gameClient.addSandbag(16,10, 10)
        gameClient.addSandbag(17,10, 10)
        gameClient.addSandbag(18,10, 10)


        gameClient.addSandbag(13,12, 10)
        gameClient.addSandbag(14,12, 10)
        gameClient.addSandbag(15,12, 10)
        gameClient.addSandbag(16,12, 10)
        gameClient.addSandbag(17,12, 10)
        gameClient.addSandbag(18,12, 10)


        when:
        gameClient.leftClickMinigunner(createdMinigunner1.id)
        gameClient.leftClick(800,400)

        then:
        true
//        then:
//        def conditions = new PollingConditions(timeout: 40, initialDelay: 1.5, factor: 1.25)
//        conditions.eventually {
//            Minigunner retrievedMinigunner = gameClient.getGdiMinigunnerById(createdMinigunner1.id)
//            assertMinigunnerIsAtDestination(retrievedMinigunner)
//            assert retrievedMinigunner.health != 0
//        }

//        and:
//        def conditions2 = new PollingConditions(timeout: 40, initialDelay: 1.5, factor: 1.25)
//        conditions2.eventually {
//            def retrievedMinigunner = gameClient.getGdiMinigunnerById(createdMinigunner2.id)
//            assertMinigunnerIsAtDestination(retrievedMinigunner)
//
//            assert retrievedMinigunner.health != 0
//        }
//
//
//        and:
//        String gameState = gameClient.getGameState()
//        String expectedGameState = "Playing"  // not sure if Playing is correct state
//
//        assert gameState == expectedGameState
    }



    @Ignore
    def "multithread tests"() {
        given:
        createRandomGDIMinigunner()
        createRandomGDIMinigunner()
        createRandomGDIMinigunner()
        createRandomGDIMinigunner()
        createRandomGDIMinigunner()
        createRandomGDIMinigunner()
        createRandomGDIMinigunner()
        createRandomGDIMinigunner()

        when:
        gameClient.setGameOptions()

        then:
        true
    }

    @Ignore
    def "Stress test for memory leaks" () {

        given:
        int originalGDIX = 300
        int originalGDIY = 700

        Minigunner gdiMinigunner = gameClient.addGDIMinigunnerAtWorldCoordinates(originalGDIX, originalGDIY)

        int nodMinigunnerX = 1000
        int nodMinigunnerY = 300
        gameClient.addNodMinigunnerAtWorldCoordinates( nodMinigunnerX, nodMinigunnerY )

        expect:
        gameClient.leftClick(nodMinigunnerX, nodMinigunnerY )
        Minigunner retrievedGdiMinigunner = gameClient.getGdiMinigunnerById(gdiMinigunner.id)
        assert retrievedGdiMinigunner.x == originalGDIX
        assert retrievedGdiMinigunner.y == originalGDIY

        where:
        i << (1..45)
    }

    def "Nod Turret should turn counterclockwise when appropriate to aim at minigunner" () {
        given:
        boolean showShroud = false
        float initialMapZoom = 1
        setAndAssertGameOptions(showShroud, initialMapZoom, GameSpeed.Slowest)

        and:
        float startingTurretFacing = 179.0

        when:
        NodTurret nodTurret = gameClient.addNodTurret(17,12, startingTurretFacing, 0)

        then:
        assert nodTurret.direction == startingTurretFacing

        when:
        gameClient.addGDIMinigunnerAtMapSquare(17, 8)

        then:
        // TODO:  this test sometimes fails because the turret turns to fast
        // Once turret speed has been updated to respect game speed
        // Consider updating this test to run at a slower game speed
        // to hopefully prevent this intermittent failure
        assertNodTurretIsNearDirection(nodTurret.id, 45.0)
        assertNodTurretHasDirection(nodTurret.id, 0.0)

    }

    def "Nod Turret should turn clockwise when appropriate to aim at minigunner" () {
        given:
        boolean showShroud = false
        float initialMapZoom = 1
        setAndAssertGameOptions(showShroud, initialMapZoom, GameSpeed.Slowest)

        and:
        float startingTurretFacing = 1

        when:
        NodTurret nodTurret = gameClient.addNodTurret(20,12, startingTurretFacing, 0)

        then:
        assert nodTurret.direction == startingTurretFacing

        when:
        gameClient.addGDIMinigunnerAtMapSquare(20, 14)

        then:
        assertNodTurretIsNearDirection(nodTurret.id, 135.0)
        assertNodTurretHasDirection(nodTurret.id, 180.0)

    }


    def assertNodTurretIsNearDirection(int nodTurretId, float direction) {
        def conditions = new PollingConditions(timeout: 20, initialDelay: 0, factor: 1.0)
        conditions.eventually {
            NodTurret nodTurret = gameClient.getNodTurretById(nodTurretId)
            float upsilon = 15.0
            float lowValue = direction - upsilon
            float highValue = direction + upsilon
//            println "Checking if nodTurret.direction: ${nodTurret.direction} is betweens ${lowValue} and ${highValue}"
            assert (nodTurret.direction > lowValue) && (nodTurret.direction < highValue)
        }
        return true
    }



    def assertNodTurretHasDirection(int nodTurretId, float direction) {
        def conditions = new PollingConditions(timeout: 20, initialDelay: 1.5, factor: 1.25)
        conditions.eventually {
            NodTurret nodTurret = gameClient.getNodTurretById(nodTurretId)
            assert nodTurret.direction == direction
        }
        return true
    }

    def assertMinigunnerIsAtScreenPosition(Minigunner minigunner, int screenX, int screenY)
    {
        int leeway = 4
        assert (minigunner.screenX >= screenX - leeway) && (minigunner.screenX <= screenX + leeway)
        assert (minigunner.screenY >= screenY - leeway) && (minigunner.screenY <= screenY + leeway)
    }

    def assertMinigunnerIsAtDestination(Minigunner minigunner)
    {
        int leeway = 4
        assert (minigunner.x >= minigunner.destinationX - leeway) && (minigunner.x <= minigunner.destinationX + leeway)
        assert (minigunner.y >= minigunner.destinationY - leeway) && (minigunner.y <= minigunner.destinationY + leeway)
    }

    def assertMinigunnerIsAtDesignatedDestinationInMapSquareCoordinates(Minigunner minigunner,int mapSquareX, int mapSquareY)
    {
        Point worldCoordinates = Util.convertMapSquareCoordinatesToWorldCoordinates(mapSquareX, mapSquareY)

        int destinationX = worldCoordinates.x
        int destinationY = worldCoordinates.y
        int leeway = 15
        assert (minigunner.x >= destinationX - leeway) && (minigunner.x <= destinationX + leeway)
        assert (minigunner.y >= destinationY - leeway) && (minigunner.y <= destinationY + leeway)
    }



    def assertMinigunnerIsAtDesignatedDestination(Minigunner minigunner,int destinationX, int destinationY)
    {
        int leeway = 15
        assert (minigunner.x >= destinationX - leeway) && (minigunner.x <= destinationX + leeway)
        assert (minigunner.y >= destinationY - leeway) && (minigunner.y <= destinationY + leeway)
    }


    def assertNodMinigunnerDies(int id) {
        def conditions = new PollingConditions(timeout: 90, initialDelay: 1.5, factor: 1.25)
        conditions.eventually {
            def expectedDeadMinigunner = gameClient.getNodMinigunnerById(id)
            assert expectedDeadMinigunner.health == 0
        }
        return true
    }


    def assertGdiMinigunnerDies(int id) {
        def conditions = new PollingConditions(timeout: 100, initialDelay: 1.5, factor: 1.25)
        conditions.eventually {
            def expectedDeadMinigunner = gameClient.getGdiMinigunnerById(id)
            assert expectedDeadMinigunner.health == 0
        }
        return true
    }



    def assertGdiMinigunnerHealthGoesTo(int id, int targetHealth) {
        def conditions = new PollingConditions(timeout: 100, initialDelay: 1.5, factor: 1.25)
        conditions.eventually {
            def expectedDeadMinigunner = gameClient.getGdiMinigunnerById(id)
            assert expectedDeadMinigunner.health == targetHealth
        }
        return true

    }



    def assertGameStateGoesToMissionFailed() {
        String expectedGameState = "Mission Failed"

        def conditions = new PollingConditions(timeout: 15, initialDelay: 1.5, factor: 1.25)
        conditions.eventually {
            String gameState = gameClient.getGameState()
            assert gameState == expectedGameState
        }

        return true
    }


    def assertGameStateGoesToGameOver() {
        String expectedGameState = "Game Over"

        def conditions = new PollingConditions(timeout: 10, initialDelay: 1.5, factor: 1.25)
        conditions.eventually {
            String gameState = gameClient.getGameState()
            assert gameState == expectedGameState
        }

        return true
    }


    Point createRandomMinigunnerPosition()
    {
        Random rand = new Random()

        int minX = 10
        int minY = 10


        // Capping max so it will fit on screen
        int maxX = 600
        int maxY = 400

        int randomX = rand.nextInt(maxX) + minX
        int randomY = rand.nextInt(maxY) + minY

        Point point = new Point()
        point.x = randomX
        point.y = randomY
        return point

    }

    Minigunner createRandomGDIMinigunner() {
        int numTiesTried = 0
        int maxTimesToTry = 10

        while(true) {
            try {
               Point randomPosition = createRandomMinigunnerPosition()
               return gameClient.addGDIMinigunnerAtWorldCoordinates(randomPosition.x, randomPosition.y)
            }
            catch(HttpResponseException e) {
                if(numTiesTried < maxTimesToTry) {
                    if (e.response.responseData["Message"] == "Cannot create on blocking terrain") {
                        numTiesTried++
                    }
                    print e
                    println "::" + e.response.responseData["Message"]
                }
                else {
                    print e
                    println "::" + e.response.responseData["Message"]
                    throw e
                }
            }
        }

    }

    Minigunner createGDIMinigunnerAtLocation(int x, int y) {
        try {
            Point position = new Point(x,y)
            return gameClient.addGDIMinigunnerAtWorldCoordinates(position.x, position.y)
        }
        catch(HttpResponseException e) {
            print e
            println "::" + e.response.responseData["Message"]
            throw e
        }
    }

    Minigunner createNodMinigunnerAtLocation(int x, int y, boolean aiIsOn) {
        try {
            Point position = new Point(x,y)
            return gameClient.addNodMinigunnerAtWorldCoordinates(position.x, position.y,aiIsOn)
        }
        catch(HttpResponseException e) {
            print e
            println "::" + e.response.responseData["Message"]
            throw e
        }
    }


    // TODO:  Unduplicate this retry code
    Minigunner createRandomNodMinigunner(boolean aiIsOn) {
        int numTiesTried = 0
        int maxTimesToTry = 10

        while(true) {
            try {
                Point randomPosition = createRandomMinigunnerPosition()
                return gameClient.addNodMinigunnerAtWorldCoordinates(randomPosition.x, randomPosition.y, aiIsOn)
            }
            catch(HttpResponseException e) {
                if(numTiesTried < maxTimesToTry) {
                    if (e.response.responseData["Message"] == "Cannot create on blocking terrain") {
                        numTiesTried++
                    }
                    print e
                    println "::" + e.response.responseData["Message"]
                }
                else {
                    print e
                    println "::" + e.response.responseData["Message"]
                    throw e
                }
            }
        }

    }

    Minigunner createRandomNodMinigunnerWithAiTurnedOff() {
        boolean aiIsOn = false
        return createRandomNodMinigunner(aiIsOn)
    }

    Minigunner createRandomNodMinigunnerWithAiTurnedOn() {
        boolean aiIsOn = true
        return createRandomNodMinigunner(aiIsOn)
    }

}