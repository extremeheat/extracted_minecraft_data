package net.minecraft.client.gui.components.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntryEntityRenderStats implements DebugScreenEntry {
   public DebugEntryEntityRenderStats() {
      super();
   }

   public void display(DebugScreenDisplayer var1, @Nullable Level var2, @Nullable LevelChunk var3, @Nullable LevelChunk var4) {
      String var5 = Minecraft.getInstance().levelRenderer.getEntityStatistics();
      if (var5 != null) {
         var1.addLine(var5);
      }

   }

   public boolean isAllowed(boolean var1) {
      return true;
   }
}
