package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.VertexBuffer;
import java.util.Iterator;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.world.level.BlockLayer;

public class VboRenderList extends ChunkRenderList {
   public VboRenderList() {
      super();
   }

   public void render(BlockLayer var1) {
      if (this.ready) {
         Iterator var2 = this.chunks.iterator();

         while(var2.hasNext()) {
            RenderChunk var3 = (RenderChunk)var2.next();
            VertexBuffer var4 = var3.getBuffer(var1.ordinal());
            GlStateManager.pushMatrix();
            this.translateToRelativeChunkPosition(var3);
            var4.bind();
            this.applyVertexDeclaration();
            var4.draw(7);
            GlStateManager.popMatrix();
         }

         VertexBuffer.unbind();
         GlStateManager.clearCurrentColor();
         this.chunks.clear();
      }
   }

   private void applyVertexDeclaration() {
      GlStateManager.vertexPointer(3, 5126, 28, 0);
      GlStateManager.colorPointer(4, 5121, 28, 12);
      GlStateManager.texCoordPointer(2, 5126, 28, 16);
      GLX.glClientActiveTexture(GLX.GL_TEXTURE1);
      GlStateManager.texCoordPointer(2, 5122, 28, 24);
      GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
   }
}
