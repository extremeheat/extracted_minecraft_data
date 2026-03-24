package net.minecraft.client.gui.components.debug;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntryNoop implements DebugScreenEntry {
   private final boolean isAllowedWithReducedDebugInfo;

   public DebugEntryNoop() {
      this(false);
   }

   public DebugEntryNoop(final boolean isAllowedWithReducedDebugInfo) {
      super();
      this.isAllowedWithReducedDebugInfo = isAllowedWithReducedDebugInfo;
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
   }

   public boolean isAllowed(final boolean reducedDebugInfo) {
      return this.isAllowedWithReducedDebugInfo || !reducedDebugInfo;
   }

   public DebugEntryCategory category() {
      return DebugEntryCategory.RENDERER;
   }
}
