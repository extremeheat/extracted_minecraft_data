package net.minecraft.client.gui.components.debug;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntryGpuUtilization implements DebugScreenEntry {
   public DebugEntryGpuUtilization() {
      super();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      Minecraft minecraft = Minecraft.getInstance();
      String var10000 = minecraft.getGpuUtilization() > 100.0 ? String.valueOf(ChatFormatting.RED) + "100%" : Math.round(minecraft.getGpuUtilization()) + "%";
      String gpuUtilizationString = "GPU: " + var10000;
      displayer.addLine(gpuUtilizationString);
   }

   public boolean isAllowed(final boolean reducedDebugInfo) {
      return true;
   }
}
