package net.minecraft.client.gui.components.debug;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public class DebugEntryPostEffect implements DebugScreenEntry {
   public DebugEntryPostEffect() {
      super();
   }

   public void display(DebugScreenDisplayer var1, @Nullable Level var2, @Nullable LevelChunk var3, @Nullable LevelChunk var4) {
      Minecraft var5 = Minecraft.getInstance();
      ResourceLocation var6 = var5.gameRenderer.currentPostEffect();
      if (var6 != null) {
         var1.addLine("Post: " + String.valueOf(var6));
      }

   }
}
