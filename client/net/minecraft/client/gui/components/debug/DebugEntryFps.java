package net.minecraft.client.gui.components.debug;

import com.mojang.blaze3d.systems.GpuSurface;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntryFps implements DebugScreenEntry {
   public DebugEntryFps() {
      super();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      Minecraft minecraft = Minecraft.getInstance();
      int framerateLimit = minecraft.getFramerateLimitTracker().getFramerateLimit();
      Optional<GpuSurface.Configuration> surfaceConfiguration = minecraft.windowSurface().currentConfiguration();
      displayer.addPriorityLine(String.format(Locale.ROOT, "%d fps T: %s%s", minecraft.getFps(), framerateLimit == 260 ? "inf" : framerateLimit, presentModeName((GpuSurface.PresentMode)surfaceConfiguration.map(GpuSurface.Configuration::presentMode).orElse((Object)null))));
   }

   public boolean isAllowed(final boolean reducedDebugInfo) {
      return true;
   }

   private static String presentModeName(final GpuSurface.@Nullable PresentMode mode) {
      byte var2 = 0;
      String var10000;
      //$FF: var2->value
      //0->IMMEDIATE
      //1->MAILBOX
      //2->FIFO
      //3->FIFO_RELAXED
      switch (mode.enumSwitch<invokedynamic>(mode, var2)) {
         case -1 -> var10000 = "";
         case 0 -> var10000 = " (immediate)";
         case 1 -> var10000 = " (mailbox)";
         case 2 -> var10000 = " (fifo)";
         case 3 -> var10000 = " (fifo relaxed)";
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }
}
