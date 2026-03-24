package net.minecraft.client.gui.components.debug;

import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

class DebugEntryVersion implements DebugScreenEntry {
   DebugEntryVersion() {
      super();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level level, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      String var10001 = SharedConstants.getCurrentVersion().name();
      displayer.addPriorityLine("Minecraft " + var10001 + " (" + Minecraft.getInstance().getLaunchedVersion() + "/" + ClientBrandRetriever.getClientModName() + ")");
   }

   public boolean isAllowed(final boolean reducedDebugInfo) {
      return true;
   }
}
