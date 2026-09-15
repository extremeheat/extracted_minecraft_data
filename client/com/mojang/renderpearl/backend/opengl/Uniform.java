package com.mojang.renderpearl.backend.opengl;

import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.util.UncheckedAutoCloseable;

public sealed interface Uniform extends UncheckedAutoCloseable {
   default void close() {
   }

   public static record Ubo(int blockBinding) implements Uniform {
      public Ubo {
         super();
      }
   }

   public static record Utb(int samplerIndex, GpuFormat format, int texture) implements Uniform {
      public Utb(final int samplerIndex, final GpuFormat format) {
         this(samplerIndex, format, GlStateManager._genTexture());
      }

      public Utb {
         super();
      }

      public void close() {
         GlStateManager._deleteTexture(this.texture);
      }
   }

   public static record Sampler(int samplerIndex) implements Uniform {
      public Sampler {
         super();
      }
   }
}
