package net.minecraft.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;

public class ItemStackRenderState {
   ItemDisplayContext displayContext;
   boolean isLeftHand;
   private int activeLayerCount;
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
      this.isLeftHand = false;

      for(int var1 = 0; var1 < this.activeLayerCount; ++var1) {
         this.layers[var1].clear();
      }

      this.activeLayerCount = 0;
   }

   private LayerRenderState firstLayer() {
      return this.layers[0];
   }

   public boolean isEmpty() {
      return this.activeLayerCount == 0;
   }

   public boolean isGui3d() {
      return this.firstLayer().isGui3d();
   }

   public boolean usesBlockLight() {
      return this.firstLayer().usesBlockLight();
   }

   @Nullable
   public TextureAtlasSprite pickParticleIcon(RandomSource var1) {
      if (this.activeLayerCount == 0) {
         return null;
      } else {
         BakedModel var2 = this.layers[var1.nextInt(this.activeLayerCount)].model;
         return var2 == null ? null : var2.getParticleIcon();
      }
   }

   public ItemTransform transform() {
      return this.firstLayer().transform();
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, int var4) {
      for(int var5 = 0; var5 < this.activeLayerCount; ++var5) {
         this.layers[var5].render(var1, var2, var3, var4);
      }

   }

   public class LayerRenderState {
      @Nullable
      BakedModel model;
      @Nullable
      private RenderType renderType;
      private FoilType foilType;
      private int[] tintLayers;
      @Nullable
      private SpecialModelRenderer<Object> specialRenderer;
      @Nullable
      private Object argumentForSpecialRendering;

      public LayerRenderState() {
         super();
         this.foilType = ItemStackRenderState.FoilType.NONE;
         this.tintLayers = new int[0];
      }

      public void clear() {
         this.model = null;
         this.renderType = null;
         this.foilType = ItemStackRenderState.FoilType.NONE;
         this.specialRenderer = null;
         this.argumentForSpecialRendering = null;
         Arrays.fill(this.tintLayers, -1);
      }

      public void setupBlockModel(BakedModel var1, RenderType var2) {
         this.model = var1;
         this.renderType = var2;
      }

      public <T> void setupSpecialModel(SpecialModelRenderer<T> var1, @Nullable T var2, BakedModel var3) {
         this.model = var3;
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

      ItemTransform transform() {
         return this.model != null ? this.model.getTransforms().getTransform(ItemStackRenderState.this.displayContext) : ItemTransform.NO_TRANSFORM;
      }

      void render(PoseStack var1, MultiBufferSource var2, int var3, int var4) {
         var1.pushPose();
         this.transform().apply(ItemStackRenderState.this.isLeftHand, var1);
         var1.translate(-0.5F, -0.5F, -0.5F);
         if (this.specialRenderer != null) {
            this.specialRenderer.render(this.argumentForSpecialRendering, ItemStackRenderState.this.displayContext, var1, var2, var3, var4, this.foilType != ItemStackRenderState.FoilType.NONE);
         } else if (this.model != null) {
            ItemRenderer.renderItem(ItemStackRenderState.this.displayContext, var1, var2, var3, var4, this.tintLayers, this.model, this.renderType, this.foilType);
         }

         var1.popPose();
      }

      boolean isGui3d() {
         return this.model != null && this.model.isGui3d();
      }

      boolean usesBlockLight() {
         return this.model != null && this.model.usesBlockLight();
      }
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
}
