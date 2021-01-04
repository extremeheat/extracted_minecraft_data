package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.ByteBuffer;
import java.util.List;

public class BufferUploader {
   public BufferUploader() {
      super();
   }

   public void end(BufferBuilder var1) {
      if (var1.getVertexCount() > 0) {
         VertexFormat var2 = var1.getVertexFormat();
         int var3 = var2.getVertexSize();
         ByteBuffer var4 = var1.getBuffer();
         List var5 = var2.getElements();

         int var6;
         int var10;
         for(var6 = 0; var6 < var5.size(); ++var6) {
            VertexFormatElement var7 = (VertexFormatElement)var5.get(var6);
            VertexFormatElement.Usage var8 = var7.getUsage();
            int var9 = var7.getType().getGlType();
            var10 = var7.getIndex();
            var4.position(var2.getOffset(var6));
            switch(var8) {
            case POSITION:
               GlStateManager.vertexPointer(var7.getCount(), var9, var3, var4);
               GlStateManager.enableClientState(32884);
               break;
            case UV:
               GLX.glClientActiveTexture(GLX.GL_TEXTURE0 + var10);
               GlStateManager.texCoordPointer(var7.getCount(), var9, var3, var4);
               GlStateManager.enableClientState(32888);
               GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
               break;
            case COLOR:
               GlStateManager.colorPointer(var7.getCount(), var9, var3, var4);
               GlStateManager.enableClientState(32886);
               break;
            case NORMAL:
               GlStateManager.normalPointer(var9, var3, var4);
               GlStateManager.enableClientState(32885);
            }
         }

         GlStateManager.drawArrays(var1.getDrawMode(), 0, var1.getVertexCount());
         var6 = 0;

         for(int var11 = var5.size(); var6 < var11; ++var6) {
            VertexFormatElement var12 = (VertexFormatElement)var5.get(var6);
            VertexFormatElement.Usage var13 = var12.getUsage();
            var10 = var12.getIndex();
            switch(var13) {
            case POSITION:
               GlStateManager.disableClientState(32884);
               break;
            case UV:
               GLX.glClientActiveTexture(GLX.GL_TEXTURE0 + var10);
               GlStateManager.disableClientState(32888);
               GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
               break;
            case COLOR:
               GlStateManager.disableClientState(32886);
               GlStateManager.clearCurrentColor();
               break;
            case NORMAL:
               GlStateManager.disableClientState(32885);
            }
         }
      }

      var1.clear();
   }
}
