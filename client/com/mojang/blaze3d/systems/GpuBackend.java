package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.shaders.GpuDebugOptions;
import com.mojang.blaze3d.shaders.ShaderSource;

public interface GpuBackend {
   String getName();

   WindowAndDevice createDeviceWithWindow(int width, int height, String title, long monitor, ShaderSource defaultShaderSource, GpuDebugOptions debugOptions) throws BackendCreationException;
}
