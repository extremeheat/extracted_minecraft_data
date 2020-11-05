package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.WorldTemplate;
import javax.annotation.Nullable;
import net.minecraft.realms.RealmsScreen;

public abstract class RealmsScreenWithCallback extends RealmsScreen {
   public RealmsScreenWithCallback() {
      super();
   }

   protected abstract void callback(@Nullable WorldTemplate var1);
}
