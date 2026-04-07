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
      displayer.addPriorityLine(String.format(Locale.ROOT, "%d fps T: %s%s", minecraft.getFps(), framerateLimit == 260 ? "inf" : framerateLimit, surfaceConfiguration.isPresent() && ((GpuSurface.Configuration)surfaceConfiguration.get()).vsync() ? " vsync" : ""));
   }

   public boolean isAllowed(final boolean reducedDebugInfo) {
      return true;
   }
}
