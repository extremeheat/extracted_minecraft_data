package com.mojang.blaze3d.shaders;

public interface Effect {
   int getId();

   void markDirty();

   Program getVertexProgram();

   Program getFragmentProgram();
}
