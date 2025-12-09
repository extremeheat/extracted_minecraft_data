package net.minecraft.client.gui.components.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntryParticleRenderStats implements DebugScreenEntry {
   public DebugEntryParticleRenderStats() {
      super();
   }

   public void display(DebugScreenDisplayer var1, @Nullable Level var2, @Nullable LevelChunk var3, @Nullable LevelChunk var4) {
      var1.addLine("P: " + Minecraft.getInstance().particleEngine.countParticles());
   }
}
