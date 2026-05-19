package com.mojang.blaze3d.systems;

public record DeviceFeatures(boolean shaderDrawParameters, boolean multiDrawDirectInterleaved, boolean multiDrawDirectSeparate, boolean multiDrawIndirect, boolean drawIndirect, boolean nonZeroFirstInstance, boolean persistentMapping) {
   public DeviceFeatures {
      super();
   }
}
