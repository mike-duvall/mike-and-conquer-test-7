package main

import domain.GDIBarracks
import domain.GDIConstructionYard
import domain.MCV
import domain.Minigunner
import domain.Point
import domain.ResetOptions
import domain.Sidebar
import util.Util


class BuldingPlacementTests extends MikeAndConquerTestBase {


    def setup() {
        ResetOptions resetOptions = new ResetOptions(false)
        gameClient.resetGame(resetOptions)
        // Add bogus minigunner to not delete so game state stays in "Playing"
        gameClient.addGDIMinigunnerAtMapSquare(4,5)
    }


    def "should be able to build construction yard, then barracks, then minigunner"() {
        given:
        int mcvDestinationX = 350
        int mcvDestinationY = 150

        Point mcvStartLocation = new Point(16,8)
        gameClient.addMCVAtMapSquare(mcvStartLocation.x, mcvStartLocation.y)

        when:
        gameClient.leftClickMCV(666)

        and:
        gameClient.leftClickInWorldCoordinates(mcvDestinationX, mcvDestinationY )

        and:
        gameClient.rightClick(200,200)

        then:
        assertMCVArrivesAtDestination(mcvDestinationX, mcvDestinationY)

        when: "Test scenario 1"
        int testScenarioNumber = 1
        String scenarioPrefix = 'mcv'
        int startX = 306
        int startY = 124
        int screenshotCompareWidth = 73
        int screenshotCompareHeight = 57

        then:
        assertScreenshotMatches(scenarioPrefix, testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)

        when:
        gameClient.leftClickMCV(666)
        gameClient.leftClickMCV(666)

        then:
        GDIConstructionYard constructionYard = gameClient.getGDIConstructionYard()
        assert constructionYard != null

        Point expectedConstructionyardMapSquareLocation = Util.convertWorldCoordinatesToMapSquareCoordinates(mcvDestinationX, mcvDestinationY)
        Point expectedConstructionYardLocationInWorldCoordinates = Util.convertMapSquareCoordinatesToWorldCoordinates(expectedConstructionyardMapSquareLocation.x,
                expectedConstructionyardMapSquareLocation.y)

        assert expectedConstructionYardLocationInWorldCoordinates.x == constructionYard.x
        assert expectedConstructionYardLocationInWorldCoordinates.y == constructionYard.y

        when:
        MCV anMCV = gameClient.getMCV()

        then:
        assert anMCV == null

        when:
        Sidebar sidebar = gameClient.getSidebar()

        then:
        assert sidebar != null
        assert sidebar.buildBarracksEnabled == true
        assert sidebar.buildMinigunnerEnabled == false

        when:
        testScenarioNumber = 1
        scenarioPrefix = 'construction-yard-placed'
        startX = 340
        startY = 117
        screenshotCompareWidth = 43
        screenshotCompareHeight = 22

        then:
        assertScreenshotMatches(scenarioPrefix, testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)

        when:
        gameClient.leftClickSidebar("Barracks")

        then:
        assertBarracksIsBuilding()

        and:
        assertBarracksIsReadyToPlace()

        when:
        gameClient.leftClickSidebar("Barracks")

        and:
        gameClient.leftClickInMapSquareCoordinates(16,5)

        and:
        assertGDIBarracksExists()

        and:
        GDIBarracks barracks = gameClient.getGDIBarracks()

        assert barracks.x == 408
        assert barracks.y == 144

        and:
        testScenarioNumber = 1
        scenarioPrefix = 'barracks-placed'
        startX = 387
        startY = 118
        screenshotCompareWidth = 47
        screenshotCompareHeight = 6

        then:
        assertScreenshotMatches(scenarioPrefix, testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)

        when:
        sidebar = gameClient.getSidebar()

        then:
        assert sidebar != null
        assert sidebar.buildBarracksEnabled == true
        assert sidebar.buildMinigunnerEnabled == true

        when:
        gameClient.leftClickSidebar("Minigunner")

        then:
        assertOneMinigunnerExists()
    }



    def "should be able to build barracks when a minigunner is selected"() {
        given:
        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)
        Minigunner minigunner = gameClient.addGDIMinigunnerAtMapSquare(18,10)

        when:
        gameClient.leftClickMCV(666)
        gameClient.leftClickMCV(666)

        and:
        gameClient.leftClickMinigunner(minigunner.id)

        and:
        gameClient.leftClickSidebar("Barracks")

        then:
        assertBarracksIsBuilding()

        and:
        assertBarracksIsReadyToPlace()

    }





}