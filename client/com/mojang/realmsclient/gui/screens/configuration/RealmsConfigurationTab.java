package com.mojang.realmsclient.gui.screens.configuration;

import com.mojang.realmsclient.dto.RealmsServer;

public interface RealmsConfigurationTab {
   void updateData(RealmsServer var1);

   default void onSelected(RealmsServer var1) {
   }

   default void onDeselected(RealmsServer var1) {
   }
}
