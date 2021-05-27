package main


import domain.Minigunner
import domain.MovementDirection
import domain.Point


class ShroudTests extends MikeAndConquerTestBase {


    def setup() {
        boolean showShroud = true
        float initialMapZoom = 1

        setAndAssertGameOptions(showShroud, initialMapZoom, GameSpeed.Fastest)

        // Add bogus minigunner to not delete so game state stays in "Playing"
        gameClient.addGDIMinigunnerAtMapSquare(4,5)

    }

    def "Shroud screenshot scenario 1"() {
        given:
        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when: "Test scenario 1"
        int testScenarioNumber = 1
        int startX = 408
        int startY = 129
        int screenshotCompareWidth = 232
        int screenshotCompareHeight = 159

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)
    }

    def "Shroud screenshot scenario 2"() {
        given:
        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when: "Test scenario 2"
        int testScenarioNumber = 2
        int startX = 408
        int startY = 129
        int screenshotCompareWidth = 232
        int screenshotCompareHeight = 159

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH]

        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)
    }



    def "Shroud screenshot scenario 3"() {
        given:
        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:  "Test scenario 3"
        int testScenarioNumber = 3
        int startX = 408
        int startY = 129
        int screenshotCompareWidth = 232
        int screenshotCompareHeight = 159

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.EAST]
        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)
    }

    def "Shroud screenshot scenario 4"() {
        given:
        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:  "Test scenario 4"
        int testScenarioNumber = 4
        int startX = 408
        int startY = 129
        int screenshotCompareWidth = 232
        int screenshotCompareHeight = 159

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.EAST, MovementDirection.WEST, MovementDirection.WEST]
        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)
    }

    def "Shroud screenshot scenario 5"() {
        given:
        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:  "Test scenario 5"
        int testScenarioNumber = 5
        int startX = 503
        int startY = 158
        int screenshotCompareWidth = 145
        int screenshotCompareHeight = 130

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.EAST, MovementDirection.WEST, MovementDirection.WEST,
        MovementDirection.EAST, MovementDirection.EAST, MovementDirection.EAST]
        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)
    }


    def "Shroud screenshot scenario 6"() {
        given:
        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:  "Test scenario 6"
        int testScenarioNumber = 6
        int startX = 527
        int startY = 167
        int screenshotCompareWidth = 121
        int screenshotCompareHeight = 121

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.EAST, MovementDirection.WEST, MovementDirection.WEST,
                         MovementDirection.EAST, MovementDirection.EAST, MovementDirection.EAST, MovementDirection.EAST]
        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)


    }


    def "Shroud screenshot scenario 7"() {
        given:
        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:  "Test scenario 7"
        int testScenarioNumber = 7
        int startX = 507
        int startY = 165
        int screenshotCompareWidth = 141
        int screenshotCompareHeight = 123

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH,  MovementDirection.EAST,
                         MovementDirection.EAST, MovementDirection.EAST,MovementDirection.EAST]

        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)

    }

    def "Shroud screenshot scenario 8"() {
        given:
        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:  "Test scenario 8"
        int testScenarioNumber = 8
        int startX = 532
        int startY = 170
        int screenshotCompareWidth = 116
        int screenshotCompareHeight = 149

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH,  MovementDirection.EAST,
                         MovementDirection.EAST, MovementDirection.EAST,MovementDirection.EAST, MovementDirection.EAST, MovementDirection.SOUTH]

        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)

    }



    def "Shroud screenshot scenario 9"() {
        given:
        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:
        int testScenarioNumber = 9
        int startX = 519
        int startY = 72
        int screenshotCompareWidth = 129
        int screenshotCompareHeight = 121

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH,  MovementDirection.EAST,
                         MovementDirection.EAST, MovementDirection.EAST,MovementDirection.EAST, MovementDirection.EAST, MovementDirection.SOUTH,
        MovementDirection.NORTH, MovementDirection.NORTH,MovementDirection.NORTH,MovementDirection.NORTH,MovementDirection.NORTH,MovementDirection.WEST ]

        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)

    }


    def "Shroud screenshot scenario 10"() {
        given:
        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:
        int testScenarioNumber = 10
        int startX = 525
        int startY = 67
        int screenshotCompareWidth = 123
        int screenshotCompareHeight = 102

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH,  MovementDirection.EAST,
                         MovementDirection.EAST, MovementDirection.EAST,MovementDirection.EAST, MovementDirection.EAST,
                         MovementDirection.NORTH,
                         MovementDirection.NORTH, MovementDirection.NORTH,MovementDirection.WEST, MovementDirection.WEST, MovementDirection.NORTH ]

        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)

    }

    def "Shroud screenshot scenario 11"() {
        given:
        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:
        int testScenarioNumber = 11
        int startX = 310
        int startY = 92
        int screenshotCompareWidth = 157
        int screenshotCompareHeight = 183

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH,  MovementDirection.NORTH,
                         MovementDirection.WEST, MovementDirection.NORTH,MovementDirection.WEST, MovementDirection.NORTH,
                         MovementDirection.WEST, MovementDirection.WEST, MovementDirection.WEST,MovementDirection.WEST,
                         MovementDirection.SOUTH, MovementDirection.SOUTH ]

        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)
    }

    def "Shroud screenshot scenario 12"() {
        given:
        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:
        int testScenarioNumber = 12
        int startX = 314
        int startY = 191
        int screenshotCompareWidth = 144
        int screenshotCompareHeight = 98

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH,  MovementDirection.NORTH,
                         MovementDirection.WEST, MovementDirection.NORTH,MovementDirection.WEST, MovementDirection.NORTH,
                         MovementDirection.WEST, MovementDirection.WEST, MovementDirection.WEST,MovementDirection.WEST,
                         MovementDirection.SOUTH, MovementDirection.SOUTH, MovementDirection.SOUTH ]

        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)
    }

    def "Shroud screenshot scenario 13"() {
        given:
        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:
        int testScenarioNumber = 13
        int startX = 303
        int startY = 87
        int screenshotCompareWidth = 302
        int screenshotCompareHeight = 189

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH,  MovementDirection.NORTH,
                         MovementDirection.NORTHWEST, MovementDirection.NORTHWEST,
                         MovementDirection.WEST, MovementDirection.WEST, MovementDirection.WEST, MovementDirection.WEST,
                         MovementDirection.SOUTH ]

        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)
    }



    def "Shroud screenshot scenario 14"() {
        given:
        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:
        int testScenarioNumber = 14
        int startX = 307
        int startY = 174
        int screenshotCompareWidth = 171
        int screenshotCompareHeight = 107

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH,  MovementDirection.NORTH,
                         MovementDirection.NORTHWEST, MovementDirection.NORTHWEST,
                         MovementDirection.WEST, MovementDirection.WEST, MovementDirection.WEST, MovementDirection.WEST,
                         MovementDirection.SOUTH, MovementDirection.SOUTH]

        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)
    }


    def "Shroud screenshot scenario 15"() {
        given:
        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:
        int testScenarioNumber = 15
        int startX = 311
        int startY = 93
        int screenshotCompareWidth = 190
        int screenshotCompareHeight = 242

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH,  MovementDirection.NORTH,
                         MovementDirection.NORTHWEST, MovementDirection.NORTHWEST,
                         MovementDirection.WEST, MovementDirection.WEST, MovementDirection.WEST, MovementDirection.WEST,
                         MovementDirection.SOUTH, MovementDirection.SOUTH, MovementDirection.SOUTH]

        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)
    }

    def "Shroud screenshot scenario 16"() {
        given:
        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:
        int testScenarioNumber = 16
        int startX = 297
        int startY = 92
        int screenshotCompareWidth = 185
        int screenshotCompareHeight = 229

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH,  MovementDirection.NORTH,
                         MovementDirection.NORTHWEST, MovementDirection.NORTHWEST,
                         MovementDirection.WEST, MovementDirection.WEST, MovementDirection.WEST, MovementDirection.WEST,
                         MovementDirection.SOUTH, MovementDirection.SOUTH, MovementDirection.SOUTH, MovementDirection.SOUTH]

        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)
    }

    def "Shroud screenshot scenario 17"() {
        given:
        Point mcvLocation = new Point(21,12)
        gameClient.addMCVAtMapSquare(mcvLocation.x, mcvLocation.y)

        when:
        int testScenarioNumber = 17
        int startX = 232
        int startY = 184
        int screenshotCompareWidth = 132
        int screenshotCompareHeight = 158

        def movements = [MovementDirection.NORTH, MovementDirection.NORTH, MovementDirection.NORTH,  MovementDirection.NORTH,
                         MovementDirection.NORTHWEST, MovementDirection.NORTHWEST,
                         MovementDirection.WEST, MovementDirection.WEST, MovementDirection.WEST, MovementDirection.WEST,
                         MovementDirection.SOUTH, MovementDirection.SOUTH, MovementDirection.SOUTH, MovementDirection.SOUTH, MovementDirection.SOUTH,
                         MovementDirection.WEST, MovementDirection.WEST
        ]

        doMinigunnerPlacements(mcvLocation, movements)

        then:
        assertScreenshotMatches(testScenarioNumber, startX , startY, screenshotCompareWidth, screenshotCompareHeight)
    }


    private void doMinigunnerPlacements(Point mcvLocation, List<MovementDirection> movements) {
        Point currentLocation = mcvLocation
        movements.each { movementDirection ->
            if (movementDirection == MovementDirection.NORTH) {
                currentLocation.y--
            }
            else if (movementDirection == MovementDirection.NORTHEAST) {
                currentLocation.x++
                currentLocation.y--
            }
            else if (movementDirection == MovementDirection.EAST) {
                currentLocation.x++
            }
            else if (movementDirection == MovementDirection.SOUTHEAST) {
                currentLocation.x++
                currentLocation.y++
            }
            else if (movementDirection == MovementDirection.SOUTH) {
                currentLocation.y++
            }
            else if (movementDirection == MovementDirection.SOUTHWEST) {
                currentLocation.x--
                currentLocation.y++
            }
            else if (movementDirection == MovementDirection.WEST) {
                currentLocation.x--
            }
            else if (movementDirection == MovementDirection.NORTHWEST) {
                currentLocation.x--
                currentLocation.y--
            }


            Minigunner minigunner = gameClient.addGDIMinigunnerAtMapSquare(currentLocation.x, currentLocation.y)
            gameClient.deleteGdiMinigunnerById(minigunner.id)
        }
    }





}