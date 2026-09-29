package net.minecraft.client.gui.components.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntrySoundMood implements DebugScreenEntry {
   public DebugEntrySoundMood() {
      super();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.player != null) {
         displayer.addFactToGroup(DebugGroups.MISC, "Sounds", (fact) -> minecraft.getSoundManager().fillChannelDebug(fact));
         displayer.addFactToGroup(DebugGroups.MISC, "Mood", (fact) -> fact.formattedValue("%.2f", minecraft.player.getCurrentMood() * 100.0F).text("%"));
      }
   }
}
