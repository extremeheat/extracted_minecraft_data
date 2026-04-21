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
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import org.jspecify.annotations.Nullable;

public class ModelFeatureRenderer {
   private final PoseStack poseStack = new PoseStack();

   public ModelFeatureRenderer() {
      super();
   }

   public void renderSolid(final SubmitNodeCollection nodeCollection, final FeatureFrameContext context) {
      Storage storage = nodeCollection.getModelSubmits();
      this.renderBatch(context, storage.solidModelSubmits);
   }

   public void renderTranslucent(final SubmitNodeCollection nodeCollection, final FeatureFrameContext context) {
      Storage storage = nodeCollection.getModelSubmits();
      storage.translucentModelSubmits.sort(Comparator.comparingDouble((submit) -> (double)(-submit.distanceToCameraSq())));
      this.renderTranslucents(context, storage.translucentModelSubmits);
   }

   private void renderTranslucents(final FeatureFrameContext context, final List<Submit<?>> submits) {
      for(Submit<?> submit : submits) {
         this.renderModel(submit, submit.renderType(), context.bufferSource(), context.outlineBufferSource());
      }

   }

   private void renderBatch(final FeatureFrameContext context, final Map<RenderType, List<Submit<?>>> map) {
      Iterable<Map.Entry<RenderType, List<Submit<?>>>> entries;
      if (SharedConstants.DEBUG_SHUFFLE_MODELS) {
         List<Map.Entry<RenderType, List<Submit<?>>>> shuffledCopy = new ArrayList(map.entrySet());
         Collections.shuffle(shuffledCopy);
         entries = shuffledCopy;
      } else {
         entries = map.entrySet();
      }

      for(Map.Entry<RenderType, List<Submit<?>>> entry : entries) {
         for(Submit<?> submit : (List)entry.getValue()) {
            this.renderModel(submit, (RenderType)entry.getKey(), context.bufferSource(), context.outlineBufferSource());
         }
      }

   }

   private <S> void renderModel(final Submit<S> submit, final RenderType renderType, final MultiBufferSource bufferSource, final OutlineBufferSource outlineBufferSource) {
      this.poseStack.pushPose();
      this.poseStack.last().set(submit.pose());
      VertexConsumer buffer = bufferSource.getBuffer(renderType);
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
         VertexConsumer breakingBuffer = new SheetedDecalTextureGenerator(bufferSource.getBuffer((RenderType)ModelBakery.DESTROY_TYPES.get(submit.crumblingOverlay().progress())), submit.crumblingOverlay().cameraPose(), 1.0F);
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
      private final Map<RenderType, List<Submit<?>>> solidModelSubmits = new HashMap();
      private final List<Submit<?>> translucentModelSubmits = new ArrayList();
      private final Set<RenderType> usedModelSubmitBuckets = new ObjectOpenHashSet();

      public Storage() {
         super();
      }

      public void add(final RenderType renderType, final Submit<?> submit) {
         if (!renderType.hasBlending()) {
            ((List)this.solidModelSubmits.computeIfAbsent(renderType, (ignored) -> new ArrayList())).add(submit);
         } else {
            this.translucentModelSubmits.add(submit);
         }

      }

      public void clear() {
         this.translucentModelSubmits.clear();

         for(Map.Entry<RenderType, List<Submit<?>>> bucketEntry : this.solidModelSubmits.entrySet()) {
            List<Submit<?>> bucket = (List)bucketEntry.getValue();
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

   public static record Submit<S>(RenderType renderType, PoseStack.Pose pose, Model<? super S> model, S state, int lightCoords, int overlayCoords, int tintedColor, @Nullable TextureAtlasSprite sprite, int outlineColor, @Nullable CrumblingOverlay crumblingOverlay) implements TranslucentSubmit {
      public Submit {
         super();
      }

      public float distanceToCameraSq() {
         return TranslucentSubmit.computeDistanceToCameraSq(this.pose.pose());
      }
   }
}
