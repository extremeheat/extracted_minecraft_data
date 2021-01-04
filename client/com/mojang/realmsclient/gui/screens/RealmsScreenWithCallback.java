package com.mojang.realmsclient.gui.screens;

import net.minecraft.realms.RealmsScreen;

public abstract class RealmsScreenWithCallback<T> extends RealmsScreen {
   public RealmsScreenWithCallback() {
      super();
   }

   abstract void callback(T var1);
}
