package com.mojang.blaze3d.systems;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import java.util.OptionalDouble;

public class SamplerCache {
   private final GpuSampler[] samplers = new GpuSampler[32];

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
                     for(boolean var21 : new boolean[]{true, false}) {
                        this.samplers[encode(var5, var9, var13, var17, var21)] = var1.createSampler(var5, var9, var13, var17, 1, var21 ? OptionalDouble.empty() : OptionalDouble.of(0.0));
                     }
                  }
               }
            }
         }

      } else {
         throw new IllegalStateException("AddressMode and FilterMode enum sizes must be 2 - if you expanded them, please update SamplerCache");
      }
   }

   public GpuSampler getSampler(AddressMode var1, AddressMode var2, FilterMode var3, FilterMode var4, boolean var5) {
      return this.samplers[encode(var1, var2, var3, var4, var5)];
   }

   public GpuSampler getClampToEdge(FilterMode var1) {
      return this.getSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, var1, var1, false);
   }

   public GpuSampler getClampToEdge(FilterMode var1, boolean var2) {
      return this.getSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, var1, var1, var2);
   }

   public GpuSampler getRepeat(FilterMode var1) {
      return this.getSampler(AddressMode.REPEAT, AddressMode.REPEAT, var1, var1, false);
   }

   public GpuSampler getRepeat(FilterMode var1, boolean var2) {
      return this.getSampler(AddressMode.REPEAT, AddressMode.REPEAT, var1, var1, var2);
   }

   public void close() {
      for(GpuSampler var4 : this.samplers) {
         var4.close();
      }

   }

   @VisibleForTesting
   static int encode(AddressMode var0, AddressMode var1, FilterMode var2, FilterMode var3, boolean var4) {
      int var5 = 0;
      var5 |= var0.ordinal() & 1;
      var5 |= (var1.ordinal() & 1) << 1;
      var5 |= (var2.ordinal() & 1) << 2;
      var5 |= (var3.ordinal() & 1) << 3;
      if (var4) {
         var5 |= 16;
      }

      return var5;
   }
}
