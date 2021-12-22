package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.Window;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;

public final class VirtualScreen implements AutoCloseable {
   private final Minecraft minecraft;
   private final ScreenManager screenManager;

   public VirtualScreen(Minecraft var1) {
      super();
      this.minecraft = var1;
      this.screenManager = new ScreenManager(Monitor::new);
   }

   public Window newWindow(DisplayData var1, @Nullable String var2, String var3) {
      return new Window(this.minecraft, this.screenManager, var1, var2, var3);
   }

   public void close() {
      this.screenManager.shutdown();
   }
}
