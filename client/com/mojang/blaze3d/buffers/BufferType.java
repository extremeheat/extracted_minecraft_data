package com.mojang.blaze3d.buffers;

public enum BufferType {
   VERTICES(34962),
   INDICES(34963),
   PIXEL_PACK(35051),
   COPY_READ(36662),
   COPY_WRITE(36663),
   PIXEL_UNPACK(35052),
   UNIFORM(35345);

   final int id;

   private BufferType(final int var3) {
      this.id = var3;
   }

   // $FF: synthetic method
   private static BufferType[] $values() {
      return new BufferType[]{VERTICES, INDICES, PIXEL_PACK, COPY_READ, COPY_WRITE, PIXEL_UNPACK, UNIFORM};
   }
}
