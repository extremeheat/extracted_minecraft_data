package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;

public class ItemEntityRenderer extends EntityRenderer<ItemEntity, ItemEntityRenderState> {
   private static final float ITEM_BUNDLE_OFFSET_SCALE = 0.15F;
   private static final float FLAT_ITEM_BUNDLE_OFFSET_X = 0.0F;
   private static final float FLAT_ITEM_BUNDLE_OFFSET_Y = 0.0F;
   private static final float FLAT_ITEM_BUNDLE_OFFSET_Z = 0.09375F;
   private final ItemModelResolver itemModelResolver;
   private final RandomSource random = RandomSource.create();

   public ItemEntityRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.itemModelResolver = var1.getItemModelResolver();
      this.shadowRadius = 0.15F;
      this.shadowStrength = 0.75F;
   }

   public ItemEntityRenderState createRenderState() {
      return new ItemEntityRenderState();
   }

   public void extractRenderState(ItemEntity var1, ItemEntityRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.ageInTicks = (float)var1.getAge() + var3;
      var2.bobOffset = var1.bobOffs;
      var2.extractItemGroupRenderState(var1, var1.getItem(), this.itemModelResolver);
   }

   public void render(ItemEntityRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      if (!var1.item.isEmpty()) {
         var2.pushPose();
         float var5 = 0.25F;
         float var6 = Mth.sin(var1.ageInTicks / 10.0F + var1.bobOffset) * 0.1F + 0.1F;
         float var7 = var1.item.transform().scale.y();
         var2.translate(0.0F, var6 + 0.25F * var7, 0.0F);
         float var8 = ItemEntity.getSpin(var1.ageInTicks, var1.bobOffset);
         var2.mulPose(Axis.YP.rotation(var8));
         renderMultipleFromCount(var2, var3, var4, var1, this.random);
         var2.popPose();
         super.render(var1, var2, var3, var4);
      }
   }

   public static void renderMultipleFromCount(PoseStack var0, MultiBufferSource var1, int var2, ItemClusterRenderState var3, RandomSource var4) {
      var4.setSeed((long)var3.seed);
      int var5 = var3.count;
      ItemStackRenderState var6 = var3.item;
      boolean var7 = var6.isGui3d();
      float var8 = var6.transform().scale.x();
      float var9 = var6.transform().scale.y();
      float var10 = var6.transform().scale.z();
      if (!var7) {
         float var11 = -0.0F * (float)(var5 - 1) * 0.5F * var8;
         float var12 = -0.0F * (float)(var5 - 1) * 0.5F * var9;
         float var13 = -0.09375F * (float)(var5 - 1) * 0.5F * var10;
         var0.translate(var11, var12, var13);
      }

      for(int var15 = 0; var15 < var5; ++var15) {
         var0.pushPose();
         if (var15 > 0) {
            if (var7) {
               float var16 = (var4.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float var18 = (var4.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float var14 = (var4.nextFloat() * 2.0F - 1.0F) * 0.15F;
               var0.translate(var16, var18, var14);
            } else {
               float var17 = (var4.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               float var19 = (var4.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               var0.translate(var17, var19, 0.0F);
            }
         }

         var6.render(var0, var1, var2, OverlayTexture.NO_OVERLAY);
         var0.popPose();
         if (!var7) {
            var0.translate(0.0F * var8, 0.0F * var9, 0.09375F * var10);
         }
      }

   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
