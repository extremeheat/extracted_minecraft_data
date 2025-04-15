package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.textures.TextureFormat;

public sealed interface Uniform extends AutoCloseable {
   default void close() {
   }

   public static record Ubo(int blockBinding) implements Uniform {
      public Ubo(int var1) {
         super();
         this.blockBinding = var1;
      }
   }

   public static record Utb(int location, int samplerIndex, TextureFormat format, int texture) implements Uniform {
      public Utb(int var1, int var2, TextureFormat var3) {
         this(var1, var2, var3, GlStateManager._genTexture());
      }

      public Utb(int var1, int var2, TextureFormat var3, int var4) {
         super();
         this.location = var1;
         this.samplerIndex = var2;
         this.format = var3;
         this.texture = var4;
      }

      public void close() {
         GlStateManager._deleteTexture(this.texture);
      }
   }

   public static record Sampler(int location, int samplerIndex) implements Uniform {
      public Sampler(int var1, int var2) {
         super();
         this.location = var1;
         this.samplerIndex = var2;
      }
   }
}
