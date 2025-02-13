package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

public record BlendFunction(GlStateManager.SourceFactor sourceColor, GlStateManager.DestFactor destColor, GlStateManager.SourceFactor sourceAlpha, GlStateManager.DestFactor destAlpha) {
   public static final BlendFunction LIGHTNING;
   public static final BlendFunction GLINT;
   public static final BlendFunction OVERLAY;
   public static final BlendFunction TRANSLUCENT;
   public static final BlendFunction ADDITIVE;
   public static final BlendFunction PANORAMA;
   public static final BlendFunction ENTITY_OUTLINE_BLIT;

   public BlendFunction(GlStateManager.SourceFactor var1, GlStateManager.DestFactor var2) {
      this(var1, var2, var1, var2);
   }

   public BlendFunction(GlStateManager.SourceFactor var1, GlStateManager.DestFactor var2, GlStateManager.SourceFactor var3, GlStateManager.DestFactor var4) {
      super();
      this.sourceColor = var1;
      this.destColor = var2;
      this.sourceAlpha = var3;
      this.destAlpha = var4;
   }

   public void apply() {
      RenderSystem.blendFuncSeparate(this.sourceColor, this.destColor, this.sourceAlpha, this.destAlpha);
   }

   static {
      LIGHTNING = new BlendFunction(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
      GLINT = new BlendFunction(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
      OVERLAY = new BlendFunction(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      TRANSLUCENT = new BlendFunction(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      ADDITIVE = new BlendFunction(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
      PANORAMA = new BlendFunction(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      ENTITY_OUTLINE_BLIT = new BlendFunction(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
   }
}
