package com.mojang.blaze3d.opengl.dsa;

import com.mojang.blaze3d.platform.GlStateManager;

public class EmulatedDsa implements DirectStateAccess {
   public EmulatedDsa() {
      super();
   }

   public int createFrameBufferObject() {
      return GlStateManager.glGenFramebuffers();
   }

   public void bindFrameBufferTextures(int var1, int var2, int var3, int var4, boolean var5) {
      GlStateManager._glBindFramebuffer(36160, var1);
      GlStateManager._glFramebufferTexture2D(36160, 36064, 3553, var2, var4);
      GlStateManager._glFramebufferTexture2D(36160, 36096, 3553, var3, var4);
      if (!var5) {
         GlStateManager._glBindFramebuffer(36160, 0);
      }

   }
}
