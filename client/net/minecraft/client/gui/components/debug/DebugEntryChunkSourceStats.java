package net.minecraft.client.gui.components.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntryChunkSourceStats implements DebugScreenEntry {
   public DebugEntryChunkSourceStats() {
      super();
   }

   public void display(DebugScreenDisplayer var1, @Nullable Level var2, @Nullable LevelChunk var3, @Nullable LevelChunk var4) {
      Minecraft var5 = Minecraft.getInstance();
      if (var5.level != null) {
         var1.addLine(var5.level.gatherChunkSourceStats());
      }

      if (var2 != null && var2 != var5.level) {
         var1.addLine(var2.gatherChunkSourceStats());
      }

   }

   public boolean isAllowed(boolean var1) {
      return true;
   }
}
