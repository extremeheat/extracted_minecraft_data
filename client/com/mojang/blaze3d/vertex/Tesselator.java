package com.mojang.blaze3d.vertex;

import javax.annotation.Nullable;

public class Tesselator {
   private static final int MAX_BYTES = 786432;
   private final BufferBuilder builder;
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
      this.builder = new BufferBuilder(var1);
   }

   public Tesselator() {
      this(786432);
   }

   public void end() {
      BufferUploader.drawWithShader(this.builder.end());
   }

   public BufferBuilder getBuilder() {
      return this.builder;
   }
}
