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

   public void display(DebugScreenDisplayer var1, @Nullable Level var2, @Nullable LevelChunk var3, @Nullable LevelChunk var4) {
      String var10001 = SharedConstants.getCurrentVersion().name();
      var1.addPriorityLine("Minecraft " + var10001 + " (" + Minecraft.getInstance().getLaunchedVersion() + "/" + ClientBrandRetriever.getClientModName() + ")");
   }

   public boolean isAllowed(boolean var1) {
      return true;
   }
}
