package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.GLFWErrorCapture;
import com.mojang.blaze3d.shaders.GpuDebugOptions;

public interface GpuBackend {
   String getName();

   void setWindowHints();

   void handleWindowCreationErrors(final GLFWErrorCapture.Error error) throws BackendCreationException;

   GpuDevice createDevice(long window, GpuDebugOptions debugOptions) throws BackendCreationException;
}
