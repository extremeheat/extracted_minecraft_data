package com.mojang.blaze3d.opengl.dsa;

public interface DirectStateAccess {
   int createFrameBufferObject();

   void bindFrameBufferTextures(int var1, int var2, int var3, int var4);
}
