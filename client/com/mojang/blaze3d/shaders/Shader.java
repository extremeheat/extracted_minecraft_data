package com.mojang.blaze3d.shaders;

public interface Shader {
   int getId();

   void markDirty();

   Program getVertexProgram();

   Program getFragmentProgram();

   void attachToProgram();
}
