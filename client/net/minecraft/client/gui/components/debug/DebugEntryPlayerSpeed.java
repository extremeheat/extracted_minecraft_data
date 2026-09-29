package net.minecraft.client.gui.components.debug;

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
         displayer.addFactToGroup(DebugGroups.POSITION, "Speed", (fact) -> fact.formattedValue("%.3f", Minecraft.getInstance().getCameraEntity().getKnownSpeed().length()).text(" blocks/tick"));
      }
   }
}
