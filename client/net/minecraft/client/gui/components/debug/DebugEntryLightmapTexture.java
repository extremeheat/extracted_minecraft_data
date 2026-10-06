package net.minecraft.client.gui.components.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntryLightmapTexture implements DebugScreenEntry {
   private static final int DISPLAY_SIZE = 64;

   public DebugEntryLightmapTexture() {
      super();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level != null) {
         displayer.addToGroup(DebugGroups.LIGHT, new Renderer(minecraft.gameRenderer.levelLightmap()));
      }
   }

   public boolean isAllowed(final boolean reducedDebugInfo) {
      return true;
   }

   private static record Renderer(GpuTextureView textureView) implements DebugCustomRenderer {
      private Renderer {
         super();
      }

      public void extract(final GuiGraphicsExtractor graphics, final int left, final int top, final DebugColumn.Side side) {
         graphics.blit(this.textureView, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST), left, top, left + 64, top + 64, 0.0F, 1.0F, 1.0F, 0.0F);
      }

      public int height() {
         return 64;
      }

      public int width(final int groupWidth) {
         return 64;
      }
   }
}
