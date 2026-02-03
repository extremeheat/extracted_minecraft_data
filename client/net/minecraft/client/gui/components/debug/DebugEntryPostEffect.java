package net.minecraft.client.gui.components.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntryPostEffect implements DebugScreenEntry {
   public DebugEntryPostEffect() {
      super();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      Minecraft minecraft = Minecraft.getInstance();
      Identifier effectId = minecraft.gameRenderer.currentPostEffect();
      if (effectId != null) {
         displayer.addLine("Post: " + String.valueOf(effectId));
      }

   }
}
