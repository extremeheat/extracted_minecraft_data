package com.mojang.blaze3d.opengl.dsa;

import org.lwjgl.opengl.ARBDirectStateAccess;

public class CoreDsa implements DirectStateAccess {
   public CoreDsa() {
      super();
   }

   public int createFrameBufferObject() {
      return ARBDirectStateAccess.glCreateFramebuffers();
   }

   public void bindFrameBufferTextures(int var1, int var2, int var3, int var4) {
      ARBDirectStateAccess.glNamedFramebufferTexture(var1, 36064, var2, var4);
      ARBDirectStateAccess.glNamedFramebufferTexture(var1, 36096, var3, var4);
   }
}
