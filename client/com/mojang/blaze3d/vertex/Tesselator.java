package com.mojang.blaze3d.vertex;

public class Tesselator {
   private final BufferBuilder builder;
   private final BufferUploader uploader = new BufferUploader();
   private static final Tesselator INSTANCE = new Tesselator(2097152);

   public static Tesselator getInstance() {
      return INSTANCE;
   }

   public Tesselator(int var1) {
      super();
      this.builder = new BufferBuilder(var1);
   }

   public void end() {
      this.builder.end();
      this.uploader.end(this.builder);
   }

   public BufferBuilder getBuilder() {
      return this.builder;
   }
}
