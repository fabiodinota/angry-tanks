package com.angrytanks.util;

public class TankSelectionUtil {
    public static String[] tankNames = { "Yellow", "Blue", "Red", "Green", "Purple", "BMW", "Cyan", "Pink", "Dark Red" };
    public static int player1Tank = 0;
    public static  int player2Tank = 1;

    public String[] getTankNames() {
        return tankNames;
    }

    public void setTankNames(String[] tankNames) {
        this.tankNames = tankNames;
    }

    public static String getPlayer1Tank() {
        return tankNames[player1Tank];
    }

    public static void setPlayer1Tank(int player1Tank) {
        TankSelectionUtil.player1Tank = player1Tank;
    }

    public static String getPlayer2Tank() {
        return tankNames[player2Tank];
    }

    public static void setPlayer2Tank(int player2Tank) {
        TankSelectionUtil.player2Tank = player2Tank;
    }
}
