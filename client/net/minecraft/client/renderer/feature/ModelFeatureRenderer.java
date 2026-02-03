package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.ModelBakery;
import org.joml.Vector3f;

public class ModelFeatureRenderer {
   private final PoseStack poseStack = new PoseStack();

   public ModelFeatureRenderer() {
      super();
   }

   public void renderSolid(final SubmitNodeCollection nodeCollection, final MultiBufferSource.BufferSource bufferSource, final OutlineBufferSource outlineBufferSource, final MultiBufferSource.BufferSource crumblingBufferSource) {
      Storage storage = nodeCollection.getModelSubmits();
      this.renderBatch(bufferSource, outlineBufferSource, storage.solidModelSubmits, crumblingBufferSource);
   }

   public void renderTranslucent(final SubmitNodeCollection nodeCollection, final MultiBufferSource.BufferSource bufferSource, final OutlineBufferSource outlineBufferSource, final MultiBufferSource.BufferSource crumblingBufferSource) {
      Storage storage = nodeCollection.getModelSubmits();
      storage.translucentModelSubmits.sort(Comparator.comparingDouble((submit) -> (double)(-submit.position().lengthSquared())));
      this.renderTranslucents(bufferSource, outlineBufferSource, storage.translucentModelSubmits, crumblingBufferSource);
   }

   private void renderTranslucents(final MultiBufferSource.BufferSource bufferSource, final OutlineBufferSource outlineBufferSource, final List<SubmitNodeStorage.TranslucentModelSubmit<?>> submits, final MultiBufferSource.BufferSource crumblingBufferSource) {
      for(SubmitNodeStorage.TranslucentModelSubmit<?> submit : submits) {
         this.renderModel(submit.modelSubmit(), submit.renderType(), bufferSource.getBuffer(submit.renderType()), outlineBufferSource, crumblingBufferSource);
      }

   }

   private void renderBatch(final MultiBufferSource.BufferSource bufferSource, final OutlineBufferSource outlineBufferSource, final Map<RenderType, List<SubmitNodeStorage.ModelSubmit<?>>> map, final MultiBufferSource.BufferSource crumblingBufferSource) {
      Iterable<Map.Entry<RenderType, List<SubmitNodeStorage.ModelSubmit<?>>>> entries;
      if (SharedConstants.DEBUG_SHUFFLE_MODELS) {
         List<Map.Entry<RenderType, List<SubmitNodeStorage.ModelSubmit<?>>>> shuffledCopy = new ArrayList(map.entrySet());
         Collections.shuffle(shuffledCopy);
         entries = shuffledCopy;
      } else {
         entries = map.entrySet();
      }

      for(Map.Entry<RenderType, List<SubmitNodeStorage.ModelSubmit<?>>> entry : entries) {
         VertexConsumer buffer = bufferSource.getBuffer((RenderType)entry.getKey());

         for(SubmitNodeStorage.ModelSubmit<?> submit : (List)entry.getValue()) {
            this.renderModel(submit, (RenderType)entry.getKey(), buffer, outlineBufferSource, crumblingBufferSource);
         }
      }

   }

   private <S> void renderModel(final SubmitNodeStorage.ModelSubmit<S> submit, final RenderType renderType, final VertexConsumer buffer, final OutlineBufferSource outlineBufferSource, final MultiBufferSource.BufferSource crumblingBufferSource) {
      this.poseStack.pushPose();
      this.poseStack.last().set(submit.pose());
      Model<? super S> model = submit.model();
      VertexConsumer wrappedBuffer = submit.sprite() == null ? buffer : submit.sprite().wrap(buffer);
      model.setupAnim(submit.state());
      model.renderToBuffer(this.poseStack, wrappedBuffer, submit.lightCoords(), submit.overlayCoords(), submit.tintedColor());
      if (submit.outlineColor() != 0 && (renderType.outline().isPresent() || renderType.isOutline())) {
         outlineBufferSource.setColor(submit.outlineColor());
         VertexConsumer outlineBuffer = outlineBufferSource.getBuffer(renderType);
         model.renderToBuffer(this.poseStack, submit.sprite() == null ? outlineBuffer : submit.sprite().wrap(outlineBuffer), submit.lightCoords(), submit.overlayCoords(), submit.tintedColor());
      }

      if (submit.crumblingOverlay() != null && renderType.affectsCrumbling()) {
         VertexConsumer breakingBuffer = new SheetedDecalTextureGenerator(crumblingBufferSource.getBuffer((RenderType)ModelBakery.DESTROY_TYPES.get(submit.crumblingOverlay().progress())), submit.crumblingOverlay().cameraPose(), 1.0F);
         model.renderToBuffer(this.poseStack, submit.sprite() == null ? breakingBuffer : submit.sprite().wrap(breakingBuffer), submit.lightCoords(), submit.overlayCoords(), submit.tintedColor());
      }

      this.poseStack.popPose();
   }

   public static record CrumblingOverlay(int progress, PoseStack.Pose cameraPose) {
      public CrumblingOverlay {
         super();
      }
   }

   public static class Storage {
      private final Map<RenderType, List<SubmitNodeStorage.ModelSubmit<?>>> solidModelSubmits = new HashMap();
      private final List<SubmitNodeStorage.TranslucentModelSubmit<?>> translucentModelSubmits = new ArrayList();
      private final Set<RenderType> usedModelSubmitBuckets = new ObjectOpenHashSet();

      public Storage() {
         super();
      }

      public void add(final RenderType renderType, final SubmitNodeStorage.ModelSubmit<?> modelSubmit) {
         if (!renderType.hasBlending()) {
            ((List)this.solidModelSubmits.computeIfAbsent(renderType, (ignored) -> new ArrayList())).add(modelSubmit);
         } else {
            Vector3f position = modelSubmit.pose().pose().transformPosition(new Vector3f());
            this.translucentModelSubmits.add(new SubmitNodeStorage.TranslucentModelSubmit(modelSubmit, renderType, position));
         }

      }

      public void clear() {
         this.translucentModelSubmits.clear();

         for(Map.Entry<RenderType, List<SubmitNodeStorage.ModelSubmit<?>>> bucketEntry : this.solidModelSubmits.entrySet()) {
            List<SubmitNodeStorage.ModelSubmit<?>> bucket = (List)bucketEntry.getValue();
            if (!bucket.isEmpty()) {
               this.usedModelSubmitBuckets.add((RenderType)bucketEntry.getKey());
               bucket.clear();
            }
         }

      }

      public void endFrame() {
         this.solidModelSubmits.keySet().removeIf((renderType) -> !this.usedModelSubmitBuckets.contains(renderType));
         this.usedModelSubmitBuckets.clear();
      }
   }
}
