package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.GLFWErrorCapture;
import com.mojang.blaze3d.shaders.GpuDebugOptions;
import com.mojang.blaze3d.shaders.ShaderSource;

public interface GpuBackend {
   String getName();

   void setWindowHints();

   void handleWindowCreationErrors(final GLFWErrorCapture.Error error) throws BackendCreationException;

   GpuDevice createDevice(long window, ShaderSource defaultShaderSource, GpuDebugOptions debugOptions);
}
