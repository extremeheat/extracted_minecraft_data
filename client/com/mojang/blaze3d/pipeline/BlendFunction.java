package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;

@DontObfuscate
public record BlendFunction(SourceFactor sourceColor, DestFactor destColor, SourceFactor sourceAlpha, DestFactor destAlpha) {
   public static final BlendFunction LIGHTNING;
   public static final BlendFunction GLINT;
   public static final BlendFunction OVERLAY;
   public static final BlendFunction TRANSLUCENT;
   public static final BlendFunction TRANSLUCENT_PREMULTIPLIED_ALPHA;
   public static final BlendFunction ADDITIVE;
   public static final BlendFunction ENTITY_OUTLINE_BLIT;

   public BlendFunction(SourceFactor var1, DestFactor var2) {
      this(var1, var2, var1, var2);
   }

   public BlendFunction(SourceFactor var1, DestFactor var2, SourceFactor var3, DestFactor var4) {
      super();
      this.sourceColor = var1;
      this.destColor = var2;
      this.sourceAlpha = var3;
      this.destAlpha = var4;
   }

   static {
      LIGHTNING = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE);
      GLINT = new BlendFunction(SourceFactor.SRC_COLOR, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE);
      OVERLAY = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ONE, DestFactor.ZERO);
      TRANSLUCENT = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
      TRANSLUCENT_PREMULTIPLIED_ALPHA = new BlendFunction(SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
      ADDITIVE = new BlendFunction(SourceFactor.ONE, DestFactor.ONE);
      ENTITY_OUTLINE_BLIT = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE);
   }
}
