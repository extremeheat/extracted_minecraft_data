package net.minecraft.client.renderer.entity;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemEntityRenderer extends EntityRenderer<ItemEntity, ItemEntityRenderState> {
   private static final float ITEM_BUNDLE_OFFSET_SCALE = 0.15F;
   private static final float FLAT_ITEM_BUNDLE_OFFSET_X = 0.0F;
   private static final float FLAT_ITEM_BUNDLE_OFFSET_Y = 0.0F;
   private static final float FLAT_ITEM_BUNDLE_OFFSET_Z = 0.09375F;
   private final ItemRenderer itemRenderer;
   private final RandomSource random = RandomSource.create();

   public ItemEntityRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.itemRenderer = var1.getItemRenderer();
      this.shadowRadius = 0.15F;
      this.shadowStrength = 0.75F;
   }

   public ResourceLocation getTextureLocation(ItemEntityRenderState var1) {
      return TextureAtlas.LOCATION_BLOCKS;
   }

   public ItemEntityRenderState createRenderState() {
      return new ItemEntityRenderState();
   }

   public void extractRenderState(ItemEntity var1, ItemEntityRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.ageInTicks = (float)var1.getAge() + var3;
      var2.bobOffset = var1.bobOffs;
      ItemStack var4 = var1.getItem();
      var2.item = var4.copy();
      var2.itemModel = this.itemRenderer.getModel(var4, var1.level(), null, var1.getId());
   }

   public void render(ItemEntityRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      BakedModel var5 = var1.itemModel;
      if (var5 != null) {
         var2.pushPose();
         ItemStack var6 = var1.item;
         this.random.setSeed((long)getSeedForItemStack(var6));
         boolean var7 = var5.isGui3d();
         float var8 = 0.25F;
         float var9 = Mth.sin(var1.ageInTicks / 10.0F + var1.bobOffset) * 0.1F + 0.1F;
         float var10 = var5.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();
         var2.translate(0.0F, var9 + 0.25F * var10, 0.0F);
         float var11 = ItemEntity.getSpin(var1.ageInTicks, var1.bobOffset);
         var2.mulPose(Axis.YP.rotation(var11));
         renderMultipleFromCount(this.itemRenderer, var2, var3, var4, var6, var5, var7, this.random);
         var2.popPose();
         super.render(var1, var2, var3, var4);
      }
   }

   public static int getSeedForItemStack(ItemStack var0) {
      return var0.isEmpty() ? 187 : Item.getId(var0.getItem()) + var0.getDamageValue();
   }

   @VisibleForTesting
   static int getRenderedAmount(int var0) {
      if (var0 <= 1) {
         return 1;
      } else if (var0 <= 16) {
         return 2;
      } else if (var0 <= 32) {
         return 3;
      } else {
         return var0 <= 48 ? 4 : 5;
      }
   }

   public static void renderMultipleFromCount(
      ItemRenderer var0, PoseStack var1, MultiBufferSource var2, int var3, ItemStack var4, RandomSource var5, Level var6
   ) {
      BakedModel var7 = var0.getModel(var4, var6, null, 0);
      renderMultipleFromCount(var0, var1, var2, var3, var4, var7, var7.isGui3d(), var5);
   }

   public static void renderMultipleFromCount(
      ItemRenderer var0, PoseStack var1, MultiBufferSource var2, int var3, ItemStack var4, BakedModel var5, boolean var6, RandomSource var7
   ) {
      int var8 = getRenderedAmount(var4.getCount());
      float var9 = var5.getTransforms().ground.scale.x();
      float var10 = var5.getTransforms().ground.scale.y();
      float var11 = var5.getTransforms().ground.scale.z();
      if (!var6) {
         float var12 = -0.0F * (float)(var8 - 1) * 0.5F * var9;
         float var13 = -0.0F * (float)(var8 - 1) * 0.5F * var10;
         float var14 = -0.09375F * (float)(var8 - 1) * 0.5F * var11;
         var1.translate(var12, var13, var14);
      }

      for (int var16 = 0; var16 < var8; var16++) {
         var1.pushPose();
         if (var16 > 0) {
            if (var6) {
               float var17 = (var7.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float var19 = (var7.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float var15 = (var7.nextFloat() * 2.0F - 1.0F) * 0.15F;
               var1.translate(var17, var19, var15);
            } else {
               float var18 = (var7.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               float var20 = (var7.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               var1.translate(var18, var20, 0.0F);
            }
         }

         var0.render(var4, ItemDisplayContext.GROUND, false, var1, var2, var3, OverlayTexture.NO_OVERLAY, var5);
         var1.popPose();
         if (!var6) {
            var1.translate(0.0F * var9, 0.0F * var10, 0.09375F * var11);
         }
      }
   }
}
