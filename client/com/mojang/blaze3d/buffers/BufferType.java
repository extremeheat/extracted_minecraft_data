package com.mojang.blaze3d.buffers;

import com.mojang.blaze3d.DontObfuscate;

@DontObfuscate
public enum BufferType {
   VERTICES,
   INDICES,
   PIXEL_PACK,
   COPY_READ,
   COPY_WRITE,
   PIXEL_UNPACK,
   UNIFORM;

   private BufferType() {
   }

   // $FF: synthetic method
   private static BufferType[] $values() {
      return new BufferType[]{VERTICES, INDICES, PIXEL_PACK, COPY_READ, COPY_WRITE, PIXEL_UNPACK, UNIFORM};
   }
}
