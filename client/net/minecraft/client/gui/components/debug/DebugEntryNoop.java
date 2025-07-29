package net.minecraft.client.gui.components.debug;

import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public class DebugEntryNoop implements DebugScreenEntry {
   private final boolean isAllowedWithReducedDebugInfo;

   public DebugEntryNoop() {
      this(false);
   }

   public DebugEntryNoop(boolean var1) {
      super();
      this.isAllowedWithReducedDebugInfo = var1;
   }

   public void display(DebugScreenDisplayer var1, @Nullable Level var2, @Nullable LevelChunk var3, @Nullable LevelChunk var4) {
   }

   public boolean isAllowed(boolean var1) {
      return this.isAllowedWithReducedDebugInfo || !var1;
   }

   public DebugEntryCategory category() {
      return DebugEntryCategory.RENDERER;
   }
}
