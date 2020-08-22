package net.minecraft.realms.pluginapi;

import net.minecraft.realms.RealmsScreen;

public interface LoadedRealmsPlugin {
   RealmsScreen getMainScreen(RealmsScreen var1);

   RealmsScreen getNotificationsScreen(RealmsScreen var1);
}
