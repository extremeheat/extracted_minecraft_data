package net.minecraft.client.renderer;

import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.pipeline.RenderPassLayout;

public class RenderPassLayouts {
   public static final RenderPassLayout COLOR_ONLY;
   public static final RenderPassLayout DEPTH_ONLY;
   public static final RenderPassLayout COLOR_DEPTH;
   public static final RenderPassLayout OIT_DEPTH_BOUNDS;
   public static final RenderPassLayout OIT_TRANSMITTANCE;
   public static final RenderPassLayout OIT_ACCUMULATE;

   public RenderPassLayouts() {
      super();
   }

   static {
      COLOR_ONLY = RenderPassLayout.simple("Color Only", GpuFormat.RGBA8_UNORM);
      DEPTH_ONLY = RenderPassLayout.simple("Depth Only", GpuFormat.D32_FLOAT);
      COLOR_DEPTH = RenderPassLayout.simple("Color + Depth", GpuFormat.RGBA8_UNORM, GpuFormat.D32_FLOAT);
      OIT_DEPTH_BOUNDS = RenderPassLayout.simple("OIT Depth Bounds", GpuFormat.RGBA32_FLOAT, GpuFormat.D32_FLOAT);
      OIT_TRANSMITTANCE = RenderPassLayout.simple("OIT Transmittance", GpuFormat.RGBA16_FLOAT, 2, GpuFormat.D32_FLOAT);
      OIT_ACCUMULATE = RenderPassLayout.simple("OIT Accumulate", GpuFormat.RGBA16_FLOAT, GpuFormat.D32_FLOAT);
   }
}
