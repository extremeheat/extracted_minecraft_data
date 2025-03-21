package com.mojang.blaze3d.opengl;

import java.util.Set;
import org.lwjgl.opengl.ARBDirectStateAccess;
import org.lwjgl.opengl.GLCapabilities;

public abstract class DirectStateAccess {
   public DirectStateAccess() {
      super();
   }

   public static DirectStateAccess create(GLCapabilities var0, Set<String> var1) {
      if (var0.GL_ARB_direct_state_access && GlDevice.USE_GL_ARB_direct_state_access) {
         var1.add("GL_ARB_direct_state_access");
         return new Core();
      } else {
         return new Emulated();
      }
   }

   abstract int createFrameBufferObject();

   abstract void bindFrameBufferTextures(int var1, int var2, int var3, int var4, int var5);

   abstract void blitFrameBuffers(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12);

   static class Core extends DirectStateAccess {
      Core() {
         super();
      }

      public int createFrameBufferObject() {
         return ARBDirectStateAccess.glCreateFramebuffers();
      }

      public void bindFrameBufferTextures(int var1, int var2, int var3, int var4, int var5) {
         ARBDirectStateAccess.glNamedFramebufferTexture(var1, 36064, var2, var4);
         ARBDirectStateAccess.glNamedFramebufferTexture(var1, 36096, var3, var4);
         if (var5 != 0) {
            GlStateManager._glBindFramebuffer(var5, var1);
         }

      }

      public void blitFrameBuffers(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12) {
         ARBDirectStateAccess.glBlitNamedFramebuffer(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
      }
   }

   static class Emulated extends DirectStateAccess {
      Emulated() {
         super();
      }

      public int createFrameBufferObject() {
         return GlStateManager.glGenFramebuffers();
      }

      public void bindFrameBufferTextures(int var1, int var2, int var3, int var4, int var5) {
         int var6 = var5 == 0 ? '\u8ca9' : var5;
         int var7 = GlStateManager.getFrameBuffer(var6);
         GlStateManager._glBindFramebuffer(var6, var1);
         GlStateManager._glFramebufferTexture2D(var6, 36064, 3553, var2, var4);
         GlStateManager._glFramebufferTexture2D(var6, 36096, 3553, var3, var4);
         if (var5 == 0) {
            GlStateManager._glBindFramebuffer(var6, var7);
         }

      }

      public void blitFrameBuffers(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12) {
         int var13 = GlStateManager.getFrameBuffer(36008);
         int var14 = GlStateManager.getFrameBuffer(36009);
         GlStateManager._glBindFramebuffer(36008, var1);
         GlStateManager._glBindFramebuffer(36009, var2);
         GlStateManager._glBlitFrameBuffer(var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
         GlStateManager._glBindFramebuffer(36008, var13);
         GlStateManager._glBindFramebuffer(36009, var14);
      }
   }
}
