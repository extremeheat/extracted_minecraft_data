package com.mojang.blaze3d.buffers;

public enum BufferUsage {
   DYNAMIC_WRITE(false, true),
   STATIC_WRITE(false, true),
   STREAM_WRITE(false, true),
   STATIC_READ(true, false),
   DYNAMIC_READ(true, false),
   STREAM_READ(true, false),
   DYNAMIC_COPY(false, false),
   STATIC_COPY(false, false),
   STREAM_COPY(false, false);

   final boolean readable;
   final boolean writable;

   private BufferUsage(final boolean var3, final boolean var4) {
      this.readable = var3;
      this.writable = var4;
   }

   public boolean isReadable() {
      return this.readable;
   }

   public boolean isWritable() {
      return this.writable;
   }

   // $FF: synthetic method
   private static BufferUsage[] $values() {
      return new BufferUsage[]{DYNAMIC_WRITE, STATIC_WRITE, STREAM_WRITE, STATIC_READ, DYNAMIC_READ, STREAM_READ, DYNAMIC_COPY, STATIC_COPY, STREAM_COPY};
   }
}
