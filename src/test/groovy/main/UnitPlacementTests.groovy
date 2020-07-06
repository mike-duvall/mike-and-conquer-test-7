package main

import domain.GDIConstructionYard
import domain.MCV
import domain.Point
import domain.ResetOptions
import domain.Sidebar
import spock.util.concurrent.PollingConditions
import util.Util


class UnitPlacementTests extends MikeAndConquerTestBase {


    def setup() {
        ResetOptions resetOptions = new ResetOptions(false)
        gameClient.resetGame(resetOptions)
        // Add bogus minigunner to not delete so game state stays in "Playing"
        gameClient.addGDIMinigunnerAtMapSquare(4,5)

    }

//    def "MCV and barracks placement scenario 1"() {
//        given:
//        int destinationX = 350
//        int destinationY = 150
//
//        Point mcvLocation = new Point(21,12)
//        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)
//
//        when:
//        gameClient.leftClickMCV(666)
//
//        and:
//        gameClient.leftClickInWorldCoordinates(destinationX, destinationY )
//
//        and:
//        gameClient.rightClick(200,200)
//
//        then:
//        def conditions = new PollingConditions(timeout: 60, initialDelay: 1.5, factor: 1.25)
//        conditions.eventually {
//            MCV retrievedMCV = gameClient.getMCV()
//            assertMCVIsAtDesignatedDestination(retrievedMCV, destinationX, destinationY)
//            }
//
//        when: "Test scenario 1"
//        int testScenarioNumber = 1
//        String scenarioPrefix = 'mcv'
//        int startX = 306
//        int startY = 124
//        int screenshotCompareWidth = 73
//        int screenshotCompareHeight = 57
//
//
//        then:
//        assertScreenshotMatches(scenarioPrefix, testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)
//    }

    def "MCV and barracks placement scenario 1"() {
        given:
        int destinationX = 350
        int destinationY = 150

        Point macStartLocation = new Point(16,8)
        gameClient.addMCVAtMapSquare(macStartLocation.x, macStartLocation.y)

        when:
        gameClient.leftClickMCV(666)

        and:
        gameClient.leftClickInWorldCoordinates(destinationX, destinationY )

        and:
        gameClient.rightClick(200,200)

        then:
        def conditions = new PollingConditions(timeout: 60, initialDelay: 1.5, factor: 1.25)
        conditions.eventually {
            MCV retrievedMCV = gameClient.getMCV()
            assertMCVIsAtDesignatedDestination(retrievedMCV, destinationX, destinationY)
        }

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

        Point expectedConstructionyardMapSquareLocation = Util.convertWorldCoordinatesToMapSquareCoordinates(destinationX, destinationY)
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
        testScenarioNumber = 1
        scenarioPrefix = 'barracks-placed'
        startX = 387
        startY = 118
        screenshotCompareWidth = 47
        screenshotCompareHeight = 6

        then:
        assertScreenshotMatches(scenarioPrefix, testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)


    }




}