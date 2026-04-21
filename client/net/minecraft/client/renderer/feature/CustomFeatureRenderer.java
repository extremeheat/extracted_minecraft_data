package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;

public class CustomFeatureRenderer {
   public CustomFeatureRenderer() {
      super();
   }

   public void renderSolid(final SubmitNodeCollection nodeCollection, final FeatureFrameContext context) {
      Storage storage = nodeCollection.getCustomGeometrySubmits();

      for(Map.Entry<RenderType, List<Submit>> entry : storage.solidCustomGeometrySubmits.entrySet()) {
         for(Submit submit : (List)entry.getValue()) {
            VertexConsumer buffer = context.bufferSource().getBuffer((RenderType)entry.getKey());
            submit.customGeometryRenderer().render(submit.pose(), buffer);
            this.renderOutline(submit, context.outlineBufferSource());
         }
      }

   }

   public void renderTranslucent(final SubmitNodeCollection nodeCollection, final FeatureFrameContext context) {
      Storage storage = nodeCollection.getCustomGeometrySubmits();

      for(Map.Entry<RenderType, List<Submit>> entry : storage.translucentCustomGeometrySubmits.entrySet()) {
         for(Submit submit : (List)entry.getValue()) {
            VertexConsumer buffer = context.bufferSource().getBuffer((RenderType)entry.getKey());
            submit.customGeometryRenderer().render(submit.pose(), buffer);
            this.renderOutline(submit, context.outlineBufferSource());
         }
      }

   }

   public void renderOutline(final Submit submit, final OutlineBufferSource outlineBufferSource) {
      RenderType renderType = submit.renderType();
      int outlineColor = submit.outlineColor();
      if (outlineColor != 0 && (renderType.outline().isPresent() || renderType.isOutline())) {
         outlineBufferSource.setColor(outlineColor);
         VertexConsumer outlineBuffer = outlineBufferSource.getBuffer(renderType);
         submit.customGeometryRenderer().render(submit.pose(), outlineBuffer);
      }

   }

   public static class Storage {
      private final Map<RenderType, List<Submit>> solidCustomGeometrySubmits = new HashMap();
      private final Map<RenderType, List<Submit>> translucentCustomGeometrySubmits = new HashMap();
      private final Set<RenderType> solidCustomGeometrySubmitsUsage = new ObjectOpenHashSet();
      private final Set<RenderType> translucentCustomGeometrySubmitsUsage = new ObjectOpenHashSet();

      public Storage() {
         super();
      }

      public void add(final PoseStack poseStack, final RenderType renderType, final int outlineColor, final SubmitNodeCollector.CustomGeometryRenderer customGeometryRenderer) {
         Submit submit = new Submit(poseStack.last().copy(), renderType, outlineColor, customGeometryRenderer);
         if (!renderType.hasBlending()) {
            ((List)this.solidCustomGeometrySubmits.computeIfAbsent(renderType, (rt) -> new ArrayList())).add(submit);
         } else {
            ((List)this.translucentCustomGeometrySubmits.computeIfAbsent(renderType, (rt) -> new ArrayList())).add(submit);
         }

      }

      public void clear() {
         for(Map.Entry<RenderType, List<Submit>> entry : this.solidCustomGeometrySubmits.entrySet()) {
            if (!((List)entry.getValue()).isEmpty()) {
               this.solidCustomGeometrySubmitsUsage.add((RenderType)entry.getKey());
               ((List)entry.getValue()).clear();
            }
         }

         for(Map.Entry<RenderType, List<Submit>> entry : this.translucentCustomGeometrySubmits.entrySet()) {
            if (!((List)entry.getValue()).isEmpty()) {
               this.translucentCustomGeometrySubmitsUsage.add((RenderType)entry.getKey());
               ((List)entry.getValue()).clear();
            }
         }

      }

      public void endFrame() {
         this.solidCustomGeometrySubmits.keySet().removeIf((renderType) -> !this.solidCustomGeometrySubmitsUsage.contains(renderType));
         this.solidCustomGeometrySubmitsUsage.clear();
         this.translucentCustomGeometrySubmits.keySet().removeIf((renderType) -> !this.translucentCustomGeometrySubmitsUsage.contains(renderType));
         this.translucentCustomGeometrySubmitsUsage.clear();
      }
   }

   public static record Submit(PoseStack.Pose pose, RenderType renderType, int outlineColor, SubmitNodeCollector.CustomGeometryRenderer customGeometryRenderer) {
      public Submit {
         super();
      }
   }
}
