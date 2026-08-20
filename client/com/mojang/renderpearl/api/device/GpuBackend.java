package com.mojang.renderpearl.api.device;

import org.jspecify.annotations.Nullable;

public interface GpuBackend {
   String getName();

   void loadLibrary() throws BackendCreationException;

   void unloadLibrary();

   long createWindow(@Nullable String title, int width, int height, long flags);

   GpuDevice createDevice(GpuDebugOptions debugOptions) throws BackendCreationException;
}
