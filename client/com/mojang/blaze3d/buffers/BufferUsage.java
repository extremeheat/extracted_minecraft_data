package com.mojang.blaze3d.buffers;

public enum BufferUsage {
   DYNAMIC_WRITE(35048, false, true),
   STATIC_WRITE(35044, false, true),
   STREAM_WRITE(35040, false, true),
   STATIC_READ(35045, true, false),
   DYNAMIC_READ(35049, true, false),
   STREAM_READ(35041, true, false),
   DYNAMIC_COPY(35050, false, false),
   STATIC_COPY(35046, false, false),
   STREAM_COPY(35042, false, false);

   final int id;
   final boolean readable;
   final boolean writable;

   private BufferUsage(final int var3, final boolean var4, final boolean var5) {
      this.id = var3;
      this.readable = var4;
      this.writable = var5;
   }

   // $FF: synthetic method
   private static BufferUsage[] $values() {
      return new BufferUsage[]{DYNAMIC_WRITE, STATIC_WRITE, STREAM_WRITE, STATIC_READ, DYNAMIC_READ, STREAM_READ, DYNAMIC_COPY, STATIC_COPY, STREAM_COPY};
   }
}
