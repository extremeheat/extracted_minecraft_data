package net.minecraft.client.gui.components.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntryGpuUtilization implements DebugScreenEntry {
   public DebugEntryGpuUtilization() {
      super();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      Minecraft minecraft = Minecraft.getInstance();
      displayer.addFactToGroup(DebugGroups.MISC, "GPU Utilization", (fact) -> {
         if (minecraft.getGpuUtilization() > 100.0) {
            fact.text((Component)Component.literal("100%").withColor(-65536));
         } else {
            fact.value((int)Math.round(minecraft.getGpuUtilization())).text("%");
         }

      });
   }

   public boolean isAllowed(final boolean reducedDebugInfo) {
      return true;
   }
}
