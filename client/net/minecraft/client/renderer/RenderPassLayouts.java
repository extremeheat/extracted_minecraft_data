package net.minecraft.client.renderer;

import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.pipeline.RenderPassLayout;
import java.util.List;

public class RenderPassLayouts {
   public static final RenderPassLayout COLOR_ONLY;
   public static final RenderPassLayout DEPTH_ONLY;
   public static final RenderPassLayout COLOR_DEPTH;
   public static final RenderPassLayout WATER_MASK;
   public static final RenderPassLayout OIT;
   public static final RenderPassLayout.Subpass OIT_DEPTH_BOUNDS_SUBPASS;
   public static final RenderPassLayout.Subpass OIT_DEPTH_BOUNDS_CULL_SUBPASS;
   public static final RenderPassLayout.Subpass OIT_TRANSMITTANCE_SUBPASS;
   public static final RenderPassLayout.Subpass OIT_ACCUMULATE_SUBPASS;
   public static final RenderPassLayout.Subpass OIT_COMPOSITE_SUBPASS;

   public RenderPassLayouts() {
      super();
   }

   static {
      COLOR_ONLY = RenderPassLayout.simple("Color Only", GpuFormat.RGBA8_UNORM);
      DEPTH_ONLY = RenderPassLayout.simple("Depth Only", GpuFormat.D32_FLOAT);
      COLOR_DEPTH = RenderPassLayout.simple("Color + Depth", GpuFormat.RGBA8_UNORM, GpuFormat.D32_FLOAT);
      WATER_MASK = RenderPassLayout.simple("Water Mask", GpuFormat.R32_FLOAT);
      RenderPassLayout.Builder oitRenderPassBuilder = new RenderPassLayout.Builder("OIT");
      RenderPassLayout.Builder.Attachment mainColorAttachment = oitRenderPassBuilder.withAttachment(GpuFormat.RGBA8_UNORM);
      RenderPassLayout.Builder.Attachment mainDepthAttachment = oitRenderPassBuilder.withAttachment(GpuFormat.D32_FLOAT);
      RenderPassLayout.Builder.Attachment depthBoundsAttachment = oitRenderPassBuilder.withAttachment(GpuFormat.RGBA32_FLOAT);
      RenderPassLayout.Builder.Subpass depthBoundsSubpass = oitRenderPassBuilder.withSubpass("Depth Bounds");
      depthBoundsSubpass.withColorAttachment(depthBoundsAttachment);
      depthBoundsSubpass.withDepthAttachment(mainDepthAttachment);
      RenderPassLayout.Builder.Attachment depthBoundsCullAttachment = oitRenderPassBuilder.withAttachment(GpuFormat.RGBA32_FLOAT);
      RenderPassLayout.Builder.Subpass depthBoundsCullSubpass = oitRenderPassBuilder.withSubpass("Depth Bounds Cull");
      depthBoundsCullSubpass.withInputAttachment(depthBoundsAttachment);
      depthBoundsCullSubpass.withColorAttachment(depthBoundsCullAttachment);
      depthBoundsCullSubpass.withDepthAttachment(mainDepthAttachment);
      List<RenderPassLayout.Builder.Attachment> transmittanceAttachments = oitRenderPassBuilder.withAttachments(GpuFormat.RGBA16_FLOAT, 2);
      RenderPassLayout.Builder.Subpass transmittanceSubpass = oitRenderPassBuilder.withSubpass("Transmittance");
      transmittanceSubpass.withInputAttachment(depthBoundsCullAttachment);
      transmittanceSubpass.withColorAttachments(transmittanceAttachments);
      transmittanceSubpass.withDepthAttachment(mainDepthAttachment);
      RenderPassLayout.Builder.Attachment accumulateAttachment = oitRenderPassBuilder.withAttachment(GpuFormat.RGBA16_FLOAT);
      RenderPassLayout.Builder.Subpass accumulateSubpass = oitRenderPassBuilder.withSubpass("Accumulate");
      accumulateSubpass.withInputAttachment(depthBoundsCullAttachment);
      accumulateSubpass.withInputAttachment((RenderPassLayout.Builder.Attachment)null);
      accumulateSubpass.withInputAttachments(transmittanceAttachments);
      accumulateSubpass.withColorAttachment(accumulateAttachment);
      accumulateSubpass.withDepthAttachment(mainDepthAttachment);
      RenderPassLayout.Builder.Subpass compositeSubpass = oitRenderPassBuilder.withSubpass("Composite");
      compositeSubpass.withInputAttachment(depthBoundsCullAttachment);
      compositeSubpass.withInputAttachment(accumulateAttachment);
      compositeSubpass.withInputAttachments(transmittanceAttachments);
      compositeSubpass.withColorAttachment(mainColorAttachment);
      compositeSubpass.withDepthAttachment(mainDepthAttachment);
      OIT = oitRenderPassBuilder.build();
      OIT_DEPTH_BOUNDS_SUBPASS = OIT.subpass(depthBoundsSubpass.index());
      OIT_DEPTH_BOUNDS_CULL_SUBPASS = OIT.subpass(depthBoundsCullSubpass.index());
      OIT_TRANSMITTANCE_SUBPASS = OIT.subpass(transmittanceSubpass.index());
      OIT_ACCUMULATE_SUBPASS = OIT.subpass(accumulateSubpass.index());
      OIT_COMPOSITE_SUBPASS = OIT.subpass(compositeSubpass.index());
   }
}
