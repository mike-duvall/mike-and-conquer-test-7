package main



class Util {

    static int mapSquareWidth = 24

    static Point convertWorldCoordinatesToMapSquareCoordinates(int x, int y) {
        int halfMapSquareWidth = mapSquareWidth / 2
        int worldX = (x * mapSquareWidth) + halfMapSquareWidth
        int worldY = (y * mapSquareWidth) + halfMapSquareWidth
        return new Point(worldX, worldY)
    }


}