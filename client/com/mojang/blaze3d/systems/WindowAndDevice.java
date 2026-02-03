package com.mojang.blaze3d.systems;

public record WindowAndDevice(long window, GpuDevice device) {
   public WindowAndDevice {
      super();
   }
}
