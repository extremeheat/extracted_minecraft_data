package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionfc;

public class ItemEntityRenderer extends EntityRenderer<ItemEntity, ItemEntityRenderState> {
   private static final float ITEM_MIN_HOVER_HEIGHT = 0.0625F;
   private static final float ITEM_BUNDLE_OFFSET_SCALE = 0.15F;
   private static final float FLAT_ITEM_DEPTH_THRESHOLD = 0.0625F;
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
      var2.bobOffset = var1.bobOffs;
      var2.extractItemGroupRenderState(var1, var1.getItem(), this.itemModelResolver);
   }

   public void submit(ItemEntityRenderState var1, PoseStack var2, SubmitNodeCollector var3) {
      if (!var1.item.isEmpty()) {
         var2.pushPose();
         AABB var4 = var1.item.getModelBoundingBox();
         float var5 = -((float)var4.minY) + 0.0625F;
         float var6 = Mth.sin(var1.ageInTicks / 10.0F + var1.bobOffset) * 0.1F + 0.1F;
         var2.translate(0.0F, var6 + var5, 0.0F);
         float var7 = ItemEntity.getSpin(var1.ageInTicks, var1.bobOffset);
         var2.mulPose((Quaternionfc)Axis.YP.rotation(var7));
         submitMultipleFromCount(var2, var3, var1.lightCoords, var1, this.random, var4);
         var2.popPose();
         super.submit(var1, var2, var3);
      }
   }

   public static void submitMultipleFromCount(PoseStack var0, SubmitNodeCollector var1, int var2, ItemClusterRenderState var3, RandomSource var4) {
      submitMultipleFromCount(var0, var1, var2, var3, var4, var3.item.getModelBoundingBox());
   }

   public static void submitMultipleFromCount(PoseStack var0, SubmitNodeCollector var1, int var2, ItemClusterRenderState var3, RandomSource var4, AABB var5) {
      int var6 = var3.count;
      if (var6 != 0) {
         var4.setSeed((long)var3.seed);
         ItemStackRenderState var7 = var3.item;
         float var8 = (float)var5.getZsize();
         if (var8 > 0.0625F) {
            var7.submit(var0, var1, var2, OverlayTexture.NO_OVERLAY, var3.outlineColor);

            for(int var9 = 1; var9 < var6; ++var9) {
               var0.pushPose();
               float var10 = (var4.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float var11 = (var4.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float var12 = (var4.nextFloat() * 2.0F - 1.0F) * 0.15F;
               var0.translate(var10, var11, var12);
               var7.submit(var0, var1, var2, OverlayTexture.NO_OVERLAY, var3.outlineColor);
               var0.popPose();
            }
         } else {
            float var13 = var8 * 1.5F;
            var0.translate(0.0F, 0.0F, -(var13 * (float)(var6 - 1) / 2.0F));
            var7.submit(var0, var1, var2, OverlayTexture.NO_OVERLAY, var3.outlineColor);
            var0.translate(0.0F, 0.0F, var13);

            for(int var14 = 1; var14 < var6; ++var14) {
               var0.pushPose();
               float var15 = (var4.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               float var16 = (var4.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               var0.translate(var15, var16, 0.0F);
               var7.submit(var0, var1, var2, OverlayTexture.NO_OVERLAY, var3.outlineColor);
               var0.popPose();
               var0.translate(0.0F, 0.0F, var13);
            }
         }

      }
   }

   public static void renderMultipleFromCount(PoseStack var0, SubmitNodeCollector var1, int var2, ItemClusterRenderState var3, RandomSource var4) {
      AABB var5 = var3.item.getModelBoundingBox();
      int var6 = var3.count;
      if (var6 != 0) {
         var4.setSeed((long)var3.seed);
         ItemStackRenderState var7 = var3.item;
         float var8 = (float)var5.getZsize();
         if (var8 > 0.0625F) {
            var7.submit(var0, var1, var2, OverlayTexture.NO_OVERLAY, var3.outlineColor);

            for(int var9 = 1; var9 < var6; ++var9) {
               var0.pushPose();
               float var10 = (var4.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float var11 = (var4.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float var12 = (var4.nextFloat() * 2.0F - 1.0F) * 0.15F;
               var0.translate(var10, var11, var12);
               var7.submit(var0, var1, var2, OverlayTexture.NO_OVERLAY, var3.outlineColor);
               var0.popPose();
            }
         } else {
            float var13 = var8 * 1.5F;
            var0.translate(0.0F, 0.0F, -(var13 * (float)(var6 - 1) / 2.0F));
            var7.submit(var0, var1, var2, OverlayTexture.NO_OVERLAY, var3.outlineColor);
            var0.translate(0.0F, 0.0F, var13);

            for(int var14 = 1; var14 < var6; ++var14) {
               var0.pushPose();
               float var15 = (var4.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               float var16 = (var4.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               var0.translate(var15, var16, 0.0F);
               var7.submit(var0, var1, var2, OverlayTexture.NO_OVERLAY, var3.outlineColor);
               var0.popPose();
               var0.translate(0.0F, 0.0F, var13);
            }
         }

      }
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
