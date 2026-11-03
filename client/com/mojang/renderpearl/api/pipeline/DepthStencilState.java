package com.mojang.renderpearl.api.pipeline;

import org.jspecify.annotations.Nullable;

public record DepthStencilState(CompareOp depthTest, boolean writeDepth, float depthBiasScaleFactor, float depthBiasConstant, @Nullable StencilTest stencilTest) {
   public static final DepthStencilState DEFAULT;

   public DepthStencilState(final StencilTest stencilTest) {
      this(CompareOp.ALWAYS_PASS, false, 0.0F, 0.0F, stencilTest);
   }

   public DepthStencilState(final CompareOp depthTest, final boolean depthWrite) {
      this(depthTest, depthWrite, 0.0F, 0.0F, (StencilTest)null);
   }

   public DepthStencilState(final CompareOp depthTest, final boolean depthWrite, final float depthBiasScaleFactor, final float depthBiasConstant) {
      this(depthTest, depthWrite, depthBiasScaleFactor, depthBiasConstant, (StencilTest)null);
   }

   public DepthStencilState(final CompareOp depthTest, final boolean depthWrite, final StencilTest stencilTest) {
      this(depthTest, depthWrite, 0.0F, 0.0F, stencilTest);
   }

   public DepthStencilState {
      super();
   }

   static {
      DEFAULT = new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true);
   }
}
