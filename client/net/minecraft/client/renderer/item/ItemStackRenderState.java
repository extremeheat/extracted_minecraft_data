package net.minecraft.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.Material;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class ItemStackRenderState {
   ItemDisplayContext displayContext;
   private int activeLayerCount;
   private boolean animated;
   private boolean oversizedInGui;
   private @Nullable AABB cachedModelBoundingBox;
   private LayerRenderState[] layers;

   public ItemStackRenderState() {
      super();
      this.displayContext = ItemDisplayContext.NONE;
      this.layers = new LayerRenderState[]{new LayerRenderState()};
   }

   public void ensureCapacity(final int requestedCount) {
      int currentCapacity = this.layers.length;
      int requiredNewCapacity = this.activeLayerCount + requestedCount;
      if (requiredNewCapacity > currentCapacity) {
         this.layers = (LayerRenderState[])Arrays.copyOf(this.layers, requiredNewCapacity);

         for(int i = currentCapacity; i < requiredNewCapacity; ++i) {
            this.layers[i] = new LayerRenderState();
         }
      }

   }

   public LayerRenderState newLayer() {
      this.ensureCapacity(1);
      return this.layers[this.activeLayerCount++];
   }

   public void clear() {
      this.displayContext = ItemDisplayContext.NONE;

      for(int i = 0; i < this.activeLayerCount; ++i) {
         this.layers[i].clear();
      }

      this.activeLayerCount = 0;
      this.animated = false;
      this.oversizedInGui = false;
      this.cachedModelBoundingBox = null;
   }

   public void setAnimated() {
      this.animated = true;
   }

   public boolean isAnimated() {
      return this.animated;
   }

   public void appendModelIdentityElement(final Object element) {
   }

   private LayerRenderState firstLayer() {
      return this.layers[0];
   }

   public boolean isEmpty() {
      return this.activeLayerCount == 0;
   }

   public boolean usesBlockLight() {
      return this.firstLayer().usesBlockLight;
   }

   public Material.@Nullable Baked pickParticleMaterial(final RandomSource randomSource) {
      return this.activeLayerCount == 0 ? null : this.layers[randomSource.nextInt(this.activeLayerCount)].particleMaterial;
   }

   public void visitExtents(final Consumer<Vector3fc> output) {
      Vector3f scratch = new Vector3f();
      PoseStack.Pose pose = new PoseStack.Pose();

      for(int i = 0; i < this.activeLayerCount; ++i) {
         LayerRenderState layer = this.layers[i];
         layer.transform.apply(this.displayContext.leftHand(), pose);
         Matrix4f poseTransform = pose.pose();
         Vector3fc[] layerExtents = (Vector3fc[])layer.extents.get();

         for(Vector3fc extent : layerExtents) {
            output.accept(scratch.set(extent).mulPosition(poseTransform));
         }

         pose.setIdentity();
      }

   }

   public void submit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final int outlineColor) {
      for(int i = 0; i < this.activeLayerCount; ++i) {
         this.layers[i].submit(poseStack, submitNodeCollector, lightCoords, overlayCoords, outlineColor);
      }

   }

   public AABB getModelBoundingBox() {
      if (this.cachedModelBoundingBox != null) {
         return this.cachedModelBoundingBox;
      } else {
         AABB.Builder collector = new AABB.Builder();
         Objects.requireNonNull(collector);
         this.visitExtents(collector::include);
         AABB aabb = collector.build();
         this.cachedModelBoundingBox = aabb;
         return aabb;
      }
   }

   public void setOversizedInGui(final boolean oversizedInGui) {
      this.oversizedInGui = oversizedInGui;
   }

   public boolean isOversizedInGui() {
      return this.oversizedInGui;
   }

   public static enum FoilType {
      NONE,
      STANDARD,
      SPECIAL;

      private FoilType() {
      }

      // $FF: synthetic method
      private static FoilType[] $values() {
         return new FoilType[]{NONE, STANDARD, SPECIAL};
      }
   }

   public class LayerRenderState {
      private static final Vector3fc[] NO_EXTENTS = new Vector3fc[0];
      public static final Supplier<Vector3fc[]> NO_EXTENTS_SUPPLIER = () -> NO_EXTENTS;
      private final List<BakedQuad> quads;
      private boolean usesBlockLight;
      private Material.@Nullable Baked particleMaterial;
      private ItemTransform transform;
      private FoilType foilType;
      private int[] tintLayers;
      private @Nullable SpecialModelRenderer<Object> specialRenderer;
      private @Nullable Object argumentForSpecialRendering;
      private Supplier<Vector3fc[]> extents;

      public LayerRenderState() {
         Objects.requireNonNull(ItemStackRenderState.this);
         super();
         this.quads = new ArrayList();
         this.transform = ItemTransform.NO_TRANSFORM;
         this.foilType = ItemStackRenderState.FoilType.NONE;
         this.tintLayers = new int[0];
         this.extents = NO_EXTENTS_SUPPLIER;
      }

      public void clear() {
         this.quads.clear();
         this.foilType = ItemStackRenderState.FoilType.NONE;
         this.specialRenderer = null;
         this.argumentForSpecialRendering = null;
         Arrays.fill(this.tintLayers, -1);
         this.usesBlockLight = false;
         this.particleMaterial = null;
         this.transform = ItemTransform.NO_TRANSFORM;
         this.extents = NO_EXTENTS_SUPPLIER;
      }

      public List<BakedQuad> prepareQuadList() {
         return this.quads;
      }

      public void setUsesBlockLight(final boolean usesBlockLight) {
         this.usesBlockLight = usesBlockLight;
      }

      public void setExtents(final Supplier<Vector3fc[]> extents) {
         this.extents = extents;
      }

      public void setParticleMaterial(final Material.Baked particleMaterial) {
         this.particleMaterial = particleMaterial;
      }

      public void setTransform(final ItemTransform transform) {
         this.transform = transform;
      }

      public <T> void setupSpecialModel(final SpecialModelRenderer<T> renderer, final @Nullable T argument) {
         this.specialRenderer = eraseSpecialRenderer(renderer);
         this.argumentForSpecialRendering = argument;
      }

      private static SpecialModelRenderer<Object> eraseSpecialRenderer(final SpecialModelRenderer<?> renderer) {
         return renderer;
      }

      public void setFoilType(final FoilType foilType) {
         this.foilType = foilType;
      }

      public int[] prepareTintLayers(final int activeTints) {
         if (activeTints > this.tintLayers.length) {
            this.tintLayers = new int[activeTints];
            Arrays.fill(this.tintLayers, -1);
         }

         return this.tintLayers;
      }

      private void submit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final int outlineColor) {
         poseStack.pushPose();
         this.transform.apply(ItemStackRenderState.this.displayContext.leftHand(), poseStack.last());
         if (this.specialRenderer != null) {
            this.specialRenderer.submit(this.argumentForSpecialRendering, ItemStackRenderState.this.displayContext, poseStack, submitNodeCollector, lightCoords, overlayCoords, this.foilType != ItemStackRenderState.FoilType.NONE, outlineColor);
         } else {
            submitNodeCollector.submitItem(poseStack, ItemStackRenderState.this.displayContext, lightCoords, overlayCoords, outlineColor, this.tintLayers, this.quads, this.foilType);
         }

         poseStack.popPose();
      }
   }
}
