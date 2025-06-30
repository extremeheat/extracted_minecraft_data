package net.minecraft.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class ItemStackRenderState {
   ItemDisplayContext displayContext;
   private int activeLayerCount;
   private boolean animated;
   private boolean oversizedInGui;
   @Nullable
   private AABB cachedModelBoundingBox;
   private LayerRenderState[] layers;

   public ItemStackRenderState() {
      super();
      this.displayContext = ItemDisplayContext.NONE;
      this.layers = new LayerRenderState[]{new LayerRenderState()};
   }

   public void ensureCapacity(int var1) {
      int var2 = this.layers.length;
      int var3 = this.activeLayerCount + var1;
      if (var3 > var2) {
         this.layers = (LayerRenderState[])Arrays.copyOf(this.layers, var3);

         for(int var4 = var2; var4 < var3; ++var4) {
            this.layers[var4] = new LayerRenderState();
         }
      }

   }

   public LayerRenderState newLayer() {
      this.ensureCapacity(1);
      return this.layers[this.activeLayerCount++];
   }

   public void clear() {
      this.displayContext = ItemDisplayContext.NONE;

      for(int var1 = 0; var1 < this.activeLayerCount; ++var1) {
         this.layers[var1].clear();
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

   public void appendModelIdentityElement(Object var1) {
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

   @Nullable
   public TextureAtlasSprite pickParticleIcon(RandomSource var1) {
      return this.activeLayerCount == 0 ? null : this.layers[var1.nextInt(this.activeLayerCount)].particleIcon;
   }

   public void visitExtents(Consumer<Vector3fc> var1) {
      Vector3f var2 = new Vector3f();
      PoseStack.Pose var3 = new PoseStack.Pose();

      for(int var4 = 0; var4 < this.activeLayerCount; ++var4) {
         LayerRenderState var5 = this.layers[var4];
         var5.transform.apply(this.displayContext.leftHand(), var3);
         Matrix4f var6 = var3.pose();
         Vector3f[] var7 = (Vector3f[])var5.extents.get();

         for(Vector3f var11 : var7) {
            var1.accept(var2.set(var11).mulPosition(var6));
         }

         var3.setIdentity();
      }

   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, int var4) {
      for(int var5 = 0; var5 < this.activeLayerCount; ++var5) {
         this.layers[var5].render(var1, var2, var3, var4);
      }

   }

   public AABB getModelBoundingBox() {
      if (this.cachedModelBoundingBox != null) {
         return this.cachedModelBoundingBox;
      } else {
         AABB.Builder var1 = new AABB.Builder();
         Objects.requireNonNull(var1);
         this.visitExtents(var1::include);
         AABB var2 = var1.build();
         this.cachedModelBoundingBox = var2;
         return var2;
      }
   }

   public void setOversizedInGui(boolean var1) {
      this.oversizedInGui = var1;
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
      private static final Vector3f[] NO_EXTENTS = new Vector3f[0];
      public static final Supplier<Vector3f[]> NO_EXTENTS_SUPPLIER = () -> NO_EXTENTS;
      private final List<BakedQuad> quads = new ArrayList();
      boolean usesBlockLight;
      @Nullable
      TextureAtlasSprite particleIcon;
      ItemTransform transform;
      @Nullable
      private RenderType renderType;
      private FoilType foilType;
      private int[] tintLayers;
      @Nullable
      private SpecialModelRenderer<Object> specialRenderer;
      @Nullable
      private Object argumentForSpecialRendering;
      Supplier<Vector3f[]> extents;

      public LayerRenderState() {
         super();
         this.transform = ItemTransform.NO_TRANSFORM;
         this.foilType = ItemStackRenderState.FoilType.NONE;
         this.tintLayers = new int[0];
         this.extents = NO_EXTENTS_SUPPLIER;
      }

      public void clear() {
         this.quads.clear();
         this.renderType = null;
         this.foilType = ItemStackRenderState.FoilType.NONE;
         this.specialRenderer = null;
         this.argumentForSpecialRendering = null;
         Arrays.fill(this.tintLayers, -1);
         this.usesBlockLight = false;
         this.particleIcon = null;
         this.transform = ItemTransform.NO_TRANSFORM;
         this.extents = NO_EXTENTS_SUPPLIER;
      }

      public List<BakedQuad> prepareQuadList() {
         return this.quads;
      }

      public void setRenderType(RenderType var1) {
         this.renderType = var1;
      }

      public void setUsesBlockLight(boolean var1) {
         this.usesBlockLight = var1;
      }

      public void setExtents(Supplier<Vector3f[]> var1) {
         this.extents = var1;
      }

      public void setParticleIcon(TextureAtlasSprite var1) {
         this.particleIcon = var1;
      }

      public void setTransform(ItemTransform var1) {
         this.transform = var1;
      }

      public <T> void setupSpecialModel(SpecialModelRenderer<T> var1, @Nullable T var2) {
         this.specialRenderer = eraseSpecialRenderer(var1);
         this.argumentForSpecialRendering = var2;
      }

      private static SpecialModelRenderer<Object> eraseSpecialRenderer(SpecialModelRenderer<?> var0) {
         return var0;
      }

      public void setFoilType(FoilType var1) {
         this.foilType = var1;
      }

      public int[] prepareTintLayers(int var1) {
         if (var1 > this.tintLayers.length) {
            this.tintLayers = new int[var1];
            Arrays.fill(this.tintLayers, -1);
         }

         return this.tintLayers;
      }

      void render(PoseStack var1, MultiBufferSource var2, int var3, int var4) {
         var1.pushPose();
         this.transform.apply(ItemStackRenderState.this.displayContext.leftHand(), var1.last());
         if (this.specialRenderer != null) {
            this.specialRenderer.render(this.argumentForSpecialRendering, ItemStackRenderState.this.displayContext, var1, var2, var3, var4, this.foilType != ItemStackRenderState.FoilType.NONE);
         } else if (this.renderType != null) {
            ItemRenderer.renderItem(ItemStackRenderState.this.displayContext, var1, var2, var3, var4, this.tintLayers, this.quads, this.renderType, this.foilType);
         }

         var1.popPose();
      }
   }
}
