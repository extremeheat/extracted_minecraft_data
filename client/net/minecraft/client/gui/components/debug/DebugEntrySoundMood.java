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

   public void display(DebugScreenDisplayer var1, @Nullable Level var2, @Nullable LevelChunk var3, @Nullable LevelChunk var4) {
      Minecraft var5 = Minecraft.getInstance();
      if (var5.player != null) {
         String var10001 = var5.getSoundManager().getDebugString();
         var1.addLine(var10001 + String.format(Locale.ROOT, " (Mood %d%%)", Math.round(var5.player.getCurrentMood() * 100.0F)));
      }
   }
}
