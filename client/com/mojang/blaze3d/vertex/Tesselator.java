package com.mojang.blaze3d.vertex;

import javax.annotation.Nullable;

public class Tesselator {
   private static final int MAX_BYTES = 786432;
   private final ByteBufferBuilder buffer;
   @Nullable
   private static Tesselator instance;

   public static void init() {
      if (instance != null) {
         throw new IllegalStateException("Tesselator has already been initialized");
      } else {
         instance = new Tesselator();
      }
   }

   public static Tesselator getInstance() {
      if (instance == null) {
         throw new IllegalStateException("Tesselator has not been initialized");
      } else {
         return instance;
      }
   }

   public Tesselator(int var1) {
      super();
      this.buffer = new ByteBufferBuilder(var1);
   }

   public Tesselator() {
      this(786432);
   }

   public BufferBuilder begin(VertexFormat.Mode var1, VertexFormat var2) {
      return new BufferBuilder(this.buffer, var1, var2);
   }

   public void clear() {
      this.buffer.clear();
   }
}
