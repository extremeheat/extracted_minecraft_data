package com.mojang.blaze3d.systems;

public record DeviceLimits(int maxAnisotropy, int minUniformOffsetAlignment, int maxTextureSize) {
   public DeviceLimits {
      super();
   }
}
