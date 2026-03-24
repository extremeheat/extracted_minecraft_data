package net.minecraft.client.gui.components.debug;

import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntrySectionPosition implements DebugScreenEntry {
   public DebugEntrySectionPosition() {
      super();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      Minecraft minecraft = Minecraft.getInstance();
      Entity entity = minecraft.getCameraEntity();
      if (entity != null) {
         BlockPos feetPos = minecraft.getCameraEntity().blockPosition();
         displayer.addToGroup(DebugEntryPosition.GROUP, String.format(Locale.ROOT, "Section-relative: %02d %02d %02d", feetPos.getX() & 15, feetPos.getY() & 15, feetPos.getZ() & 15));
      }
   }

   public boolean isAllowed(final boolean reducedDebugInfo) {
      return true;
   }
}
