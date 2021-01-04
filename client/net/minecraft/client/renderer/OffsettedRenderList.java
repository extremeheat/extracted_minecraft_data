package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import net.minecraft.client.renderer.chunk.ListedRenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.world.level.BlockLayer;

public class OffsettedRenderList extends ChunkRenderList {
   public OffsettedRenderList() {
      super();
   }

   public void render(BlockLayer var1) {
      if (this.ready) {
         Iterator var2 = this.chunks.iterator();

         while(var2.hasNext()) {
            RenderChunk var3 = (RenderChunk)var2.next();
            ListedRenderChunk var4 = (ListedRenderChunk)var3;
            GlStateManager.pushMatrix();
            this.translateToRelativeChunkPosition(var3);
            GlStateManager.callList(var4.getGlListId(var1, var4.getCompiledChunk()));
            GlStateManager.popMatrix();
         }

         GlStateManager.clearCurrentColor();
         this.chunks.clear();
      }
   }
}
