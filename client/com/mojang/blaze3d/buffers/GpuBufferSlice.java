package com.mojang.blaze3d.buffers;

import com.mojang.blaze3d.DontObfuscate;

@DontObfuscate
public record GpuBufferSlice(GpuBuffer buffer, int offset, int length) {
   public GpuBufferSlice(GpuBuffer var1, int var2, int var3) {
      super();
      this.buffer = var1;
      this.offset = var2;
      this.length = var3;
   }

   public GpuBufferSlice slice(int var1, int var2) {
      if (var1 >= 0 && var2 >= 0 && var1 + var2 < this.length) {
         return new GpuBufferSlice(this.buffer, this.offset + var1, var2);
      } else {
         throw new IllegalArgumentException("Offset of " + var1 + " and length " + var2 + " would put new slice outside existing slice's range (of " + var1 + "," + var2 + ")");
      }
   }
}
