package net.minecraft.client.gui.screens.achievement;

public interface StatsUpdateListener {
   String[] LOADING_SYMBOLS = new String[]{"oooooo", "Oooooo", "oOoooo", "ooOooo", "oooOoo", "ooooOo", "oooooO"};

   void onStatsUpdated();
}
