package net.minecraft.client.gui.components.debug;

import java.util.Locale;
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
         String var10001 = minecraft.getSoundManager().getDebugString();
         displayer.addLine(var10001 + String.format(Locale.ROOT, " (Mood %d%%)", Math.round(minecraft.player.getCurrentMood() * 100.0F)));
      }
   }
}
