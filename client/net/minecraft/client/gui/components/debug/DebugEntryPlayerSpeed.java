package net.minecraft.client.gui.components.debug;

import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntryPlayerSpeed implements DebugScreenEntry {
   public DebugEntryPlayerSpeed() {
      super();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      if (Minecraft.getInstance().getCameraEntity() != null) {
         displayer.addToGroup(DebugEntryPosition.GROUP, String.format(Locale.ROOT, "Speed: %.3f blocks/tick", Minecraft.getInstance().getCameraEntity().getKnownSpeed().length()));
      }
   }
}
