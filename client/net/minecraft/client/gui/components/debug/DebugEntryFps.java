package net.minecraft.client.gui.components.debug;

import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntryFps implements DebugScreenEntry {
   public DebugEntryFps() {
      super();
   }

   public void display(DebugScreenDisplayer var1, @Nullable Level var2, @Nullable LevelChunk var3, @Nullable LevelChunk var4) {
      Minecraft var5 = Minecraft.getInstance();
      int var6 = var5.getFramerateLimitTracker().getFramerateLimit();
      Options var7 = var5.options;
      var1.addPriorityLine(String.format(Locale.ROOT, "%d fps T: %s%s", var5.getFps(), var6 == 260 ? "inf" : var6, (Boolean)var7.enableVsync().get() ? " vsync" : ""));
   }

   public boolean isAllowed(boolean var1) {
      return true;
   }
}
