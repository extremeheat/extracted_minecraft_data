package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.ModelBakery;

public class ModelPartFeatureRenderer {
   private final PoseStack poseStack = new PoseStack();

   public ModelPartFeatureRenderer() {
      super();
   }

   public void renderSolid(final SubmitNodeCollection nodeCollection, final MultiBufferSource.BufferSource bufferSource, final OutlineBufferSource outlineBufferSource, final MultiBufferSource.BufferSource crumblingBufferSource) {
      Storage storage = nodeCollection.getModelPartSubmits();
      this.render(storage.solidModelPartSubmits, bufferSource, outlineBufferSource, crumblingBufferSource);
   }

   public void renderTranslucent(final SubmitNodeCollection nodeCollection, final MultiBufferSource.BufferSource bufferSource, final OutlineBufferSource outlineBufferSource, final MultiBufferSource.BufferSource crumblingBufferSource) {
      Storage storage = nodeCollection.getModelPartSubmits();
      this.render(storage.translucentModelPartSubmits, bufferSource, outlineBufferSource, crumblingBufferSource);
   }

   private void render(final Map<RenderType, List<SubmitNodeStorage.ModelPartSubmit>> modelPartSubmitsMap, final MultiBufferSource.BufferSource bufferSource, final OutlineBufferSource outlineBufferSource, final MultiBufferSource.BufferSource crumblingBufferSource) {
      for(Map.Entry<RenderType, List<SubmitNodeStorage.ModelPartSubmit>> entry : modelPartSubmitsMap.entrySet()) {
         RenderType renderType = (RenderType)entry.getKey();
         List<SubmitNodeStorage.ModelPartSubmit> modelPartSubmits = (List)entry.getValue();
         VertexConsumer buffer = bufferSource.getBuffer(renderType);

         for(SubmitNodeStorage.ModelPartSubmit modelPartSubmit : modelPartSubmits) {
            VertexConsumer actualBuffer;
            if (modelPartSubmit.sprite() != null) {
               if (modelPartSubmit.hasFoil()) {
                  actualBuffer = modelPartSubmit.sprite().wrap(ItemFeatureRenderer.getFoilBuffer(bufferSource, renderType, modelPartSubmit.sheeted(), true));
               } else {
                  actualBuffer = modelPartSubmit.sprite().wrap(buffer);
               }
            } else if (modelPartSubmit.hasFoil()) {
               actualBuffer = ItemFeatureRenderer.getFoilBuffer(bufferSource, renderType, modelPartSubmit.sheeted(), true);
            } else {
               actualBuffer = buffer;
            }

            this.poseStack.last().set(modelPartSubmit.pose());
            modelPartSubmit.modelPart().render(this.poseStack, actualBuffer, modelPartSubmit.lightCoords(), modelPartSubmit.overlayCoords(), modelPartSubmit.tintedColor());
            if (modelPartSubmit.outlineColor() != 0 && (renderType.outline().isPresent() || renderType.isOutline())) {
               outlineBufferSource.setColor(modelPartSubmit.outlineColor());
               VertexConsumer outlineBuffer = outlineBufferSource.getBuffer(renderType);
               modelPartSubmit.modelPart().render(this.poseStack, modelPartSubmit.sprite() == null ? outlineBuffer : modelPartSubmit.sprite().wrap(outlineBuffer), modelPartSubmit.lightCoords(), modelPartSubmit.overlayCoords(), modelPartSubmit.tintedColor());
            }

            if (modelPartSubmit.crumblingOverlay() != null) {
               VertexConsumer breakingBuffer = new SheetedDecalTextureGenerator(crumblingBufferSource.getBuffer((RenderType)ModelBakery.DESTROY_TYPES.get(modelPartSubmit.crumblingOverlay().progress())), modelPartSubmit.crumblingOverlay().cameraPose(), 1.0F);
               modelPartSubmit.modelPart().render(this.poseStack, breakingBuffer, modelPartSubmit.lightCoords(), modelPartSubmit.overlayCoords(), modelPartSubmit.tintedColor());
            }
         }
      }

   }

   public static class Storage {
      private final Map<RenderType, List<SubmitNodeStorage.ModelPartSubmit>> solidModelPartSubmits = new HashMap();
      private final Map<RenderType, List<SubmitNodeStorage.ModelPartSubmit>> translucentModelPartSubmits = new HashMap();
      private final Set<RenderType> solidModelPartSubmitsUsage = new ObjectOpenHashSet();
      private final Set<RenderType> translucentModelPartSubmitsUsage = new ObjectOpenHashSet();

      public Storage() {
         super();
      }

      public void add(final RenderType renderType, final SubmitNodeStorage.ModelPartSubmit submit) {
         if (!renderType.hasBlending()) {
            ((List)this.solidModelPartSubmits.computeIfAbsent(renderType, (ignored) -> new ArrayList())).add(submit);
         } else {
            ((List)this.translucentModelPartSubmits.computeIfAbsent(renderType, (ignored) -> new ArrayList())).add(submit);
         }

      }

      public void clear() {
         for(Map.Entry<RenderType, List<SubmitNodeStorage.ModelPartSubmit>> entry : this.solidModelPartSubmits.entrySet()) {
            if (!((List)entry.getValue()).isEmpty()) {
               this.solidModelPartSubmitsUsage.add((RenderType)entry.getKey());
               ((List)entry.getValue()).clear();
            }
         }

         for(Map.Entry<RenderType, List<SubmitNodeStorage.ModelPartSubmit>> entry : this.translucentModelPartSubmits.entrySet()) {
            if (!((List)entry.getValue()).isEmpty()) {
               this.translucentModelPartSubmitsUsage.add((RenderType)entry.getKey());
               ((List)entry.getValue()).clear();
            }
         }

      }

      public void endFrame() {
         this.solidModelPartSubmits.keySet().removeIf((renderType) -> !this.solidModelPartSubmitsUsage.contains(renderType));
         this.solidModelPartSubmitsUsage.clear();
         this.translucentModelPartSubmits.keySet().removeIf((renderType) -> !this.translucentModelPartSubmitsUsage.contains(renderType));
         this.translucentModelPartSubmitsUsage.clear();
      }
   }
}
