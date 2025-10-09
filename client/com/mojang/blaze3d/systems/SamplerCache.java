package com.mojang.blaze3d.systems;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;

public class SamplerCache {
   private final GpuSampler[] samplers = new GpuSampler[16];

   public SamplerCache() {
      super();
   }

   public void initialize() {
      GpuDevice var1 = RenderSystem.getDevice();
      if (AddressMode.values().length == 2 && FilterMode.values().length == 2) {
         for(AddressMode var5 : AddressMode.values()) {
            for(AddressMode var9 : AddressMode.values()) {
               for(FilterMode var13 : FilterMode.values()) {
                  for(FilterMode var17 : FilterMode.values()) {
                     this.samplers[encode(var5, var9, var13, var17)] = var1.createSampler(var5, var9, var13, var17);
                  }
               }
            }
         }

      } else {
         throw new IllegalStateException("AddressMode and FilterMode enum sizes must be 2 - if you expanded them, please update SamplerCache");
      }
   }

   public GpuSampler getSampler(AddressMode var1, AddressMode var2, FilterMode var3, FilterMode var4) {
      return this.samplers[encode(var1, var2, var3, var4)];
   }

   public GpuSampler getClampToEdge(FilterMode var1) {
      return this.getSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, var1, var1);
   }

   public GpuSampler getRepeat(FilterMode var1) {
      return this.getSampler(AddressMode.REPEAT, AddressMode.REPEAT, var1, var1);
   }

   public void close() {
      for(GpuSampler var4 : this.samplers) {
         var4.close();
      }

   }

   @VisibleForTesting
   static int encode(AddressMode var0, AddressMode var1, FilterMode var2, FilterMode var3) {
      int var4 = 0;
      var4 |= var0.ordinal() & 1;
      var4 |= (var1.ordinal() & 1) << 1;
      var4 |= (var2.ordinal() & 1) << 2;
      var4 |= (var3.ordinal() & 1) << 3;
      return var4;
   }
}
