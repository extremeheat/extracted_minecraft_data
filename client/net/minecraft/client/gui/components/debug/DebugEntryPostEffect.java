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

   public void display(DebugScreenDisplayer var1, @Nullable Level var2, @Nullable LevelChunk var3, @Nullable LevelChunk var4) {
      Minecraft var5 = Minecraft.getInstance();
      Identifier var6 = var5.gameRenderer.currentPostEffect();
      if (var6 != null) {
         var1.addLine("Post: " + String.valueOf(var6));
      }

   }
}
