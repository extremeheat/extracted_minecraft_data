package net.minecraft.client.gui.components.debug;

import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public class DebugEntryGpuUtilization implements DebugScreenEntry {
   public DebugEntryGpuUtilization() {
      super();
   }

   public void display(DebugScreenDisplayer var1, @Nullable Level var2, @Nullable LevelChunk var3, @Nullable LevelChunk var4) {
      Minecraft var5 = Minecraft.getInstance();
      String var10000 = var5.getGpuUtilization() > 100.0 ? String.valueOf(ChatFormatting.RED) + "100%" : Math.round(var5.getGpuUtilization()) + "%";
      String var6 = "GPU: " + var10000;
      var1.addLine(var6);
   }

   public boolean isAllowed(boolean var1) {
      return true;
   }
}
