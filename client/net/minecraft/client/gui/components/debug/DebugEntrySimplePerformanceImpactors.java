package net.minecraft.client.gui.components.debug;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public class DebugEntrySimplePerformanceImpactors implements DebugScreenEntry {
   public DebugEntrySimplePerformanceImpactors() {
      super();
   }

   public void display(DebugScreenDisplayer var1, @Nullable Level var2, @Nullable LevelChunk var3, @Nullable LevelChunk var4) {
      Minecraft var5 = Minecraft.getInstance();
      Options var6 = var5.options;
      var1.addLine(String.format(Locale.ROOT, "%s%s B: %d", var6.graphicsMode().get(), var6.cloudStatus().get() == CloudStatus.OFF ? "" : (var6.cloudStatus().get() == CloudStatus.FAST ? " fast-clouds" : " fancy-clouds"), var6.biomeBlendRadius().get()));
   }

   public boolean isAllowed(boolean var1) {
      return true;
   }
}
