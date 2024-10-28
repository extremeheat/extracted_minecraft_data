package net.minecraft.client.renderer.entity;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemEntityRenderer extends EntityRenderer<ItemEntity> {
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

   public ResourceLocation getTextureLocation(ItemEntity var1) {
      return TextureAtlas.LOCATION_BLOCKS;
   }

   public void render(ItemEntity var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      var4.pushPose();
      ItemStack var7 = var1.getItem();
      this.random.setSeed((long)getSeedForItemStack(var7));
      BakedModel var8 = this.itemRenderer.getModel(var7, var1.level(), (LivingEntity)null, var1.getId());
      boolean var9 = var8.isGui3d();
      float var10 = 0.25F;
      float var11 = Mth.sin(((float)var1.getAge() + var3) / 10.0F + var1.bobOffs) * 0.1F + 0.1F;
      float var12 = var8.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();
      var4.translate(0.0F, var11 + 0.25F * var12, 0.0F);
      float var13 = var1.getSpin(var3);
      var4.mulPose(Axis.YP.rotation(var13));
      renderMultipleFromCount(this.itemRenderer, var4, var5, var6, var7, var8, var9, this.random);
      var4.popPose();
      super.render(var1, var2, var3, var4, var5, var6);
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

   public static void renderMultipleFromCount(ItemRenderer var0, PoseStack var1, MultiBufferSource var2, int var3, ItemStack var4, RandomSource var5, Level var6) {
      BakedModel var7 = var0.getModel(var4, var6, (LivingEntity)null, 0);
      renderMultipleFromCount(var0, var1, var2, var3, var4, var7, var7.isGui3d(), var5);
   }

   public static void renderMultipleFromCount(ItemRenderer var0, PoseStack var1, MultiBufferSource var2, int var3, ItemStack var4, BakedModel var5, boolean var6, RandomSource var7) {
      int var8 = getRenderedAmount(var4.getCount());
      float var9 = var5.getTransforms().ground.scale.x();
      float var10 = var5.getTransforms().ground.scale.y();
      float var11 = var5.getTransforms().ground.scale.z();
      float var13;
      float var14;
      if (!var6) {
         float var12 = -0.0F * (float)(var8 - 1) * 0.5F * var9;
         var13 = -0.0F * (float)(var8 - 1) * 0.5F * var10;
         var14 = -0.09375F * (float)(var8 - 1) * 0.5F * var11;
         var1.translate(var12, var13, var14);
      }

      for(int var16 = 0; var16 < var8; ++var16) {
         var1.pushPose();
         if (var16 > 0) {
            if (var6) {
               var13 = (var7.nextFloat() * 2.0F - 1.0F) * 0.15F;
               var14 = (var7.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float var15 = (var7.nextFloat() * 2.0F - 1.0F) * 0.15F;
               var1.translate(var13, var14, var15);
            } else {
               var13 = (var7.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               var14 = (var7.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               var1.translate(var13, var14, 0.0F);
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
