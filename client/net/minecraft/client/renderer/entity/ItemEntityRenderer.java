package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import java.util.Random;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemEntityRenderer extends EntityRenderer<ItemEntity> {
   private final ItemRenderer itemRenderer;
   private final Random random = new Random();

   public ItemEntityRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.itemRenderer = var1.getItemRenderer();
      this.shadowRadius = 0.15F;
      this.shadowStrength = 0.75F;
   }

   private int getRenderAmount(ItemStack var1) {
      byte var2 = 1;
      if (var1.getCount() > 48) {
         var2 = 5;
      } else if (var1.getCount() > 32) {
         var2 = 4;
      } else if (var1.getCount() > 16) {
         var2 = 3;
      } else if (var1.getCount() > 1) {
         var2 = 2;
      }

      return var2;
   }

   public void render(ItemEntity var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      var4.pushPose();
      ItemStack var7 = var1.getItem();
      int var8 = var7.isEmpty() ? 187 : Item.getId(var7.getItem()) + var7.getDamageValue();
      this.random.setSeed((long)var8);
      BakedModel var9 = this.itemRenderer.getModel(var7, var1.level, (LivingEntity)null);
      boolean var10 = var9.isGui3d();
      int var11 = this.getRenderAmount(var7);
      float var12 = 0.25F;
      float var13 = Mth.sin(((float)var1.getAge() + var3) / 10.0F + var1.bobOffs) * 0.1F + 0.1F;
      float var14 = var9.getTransforms().getTransform(ItemTransforms.TransformType.GROUND).scale.y();
      var4.translate(0.0D, (double)(var13 + 0.25F * var14), 0.0D);
      float var15 = var1.getSpin(var3);
      var4.mulPose(Vector3f.YP.rotation(var15));
      float var16 = var9.getTransforms().ground.scale.x();
      float var17 = var9.getTransforms().ground.scale.y();
      float var18 = var9.getTransforms().ground.scale.z();
      float var20;
      float var21;
      if (!var10) {
         float var19 = -0.0F * (float)(var11 - 1) * 0.5F * var16;
         var20 = -0.0F * (float)(var11 - 1) * 0.5F * var17;
         var21 = -0.09375F * (float)(var11 - 1) * 0.5F * var18;
         var4.translate((double)var19, (double)var20, (double)var21);
      }

      for(int var23 = 0; var23 < var11; ++var23) {
         var4.pushPose();
         if (var23 > 0) {
            if (var10) {
               var20 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               var21 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float var22 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               var4.translate((double)var20, (double)var21, (double)var22);
            } else {
               var20 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               var21 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               var4.translate((double)var20, (double)var21, 0.0D);
            }
         }

         this.itemRenderer.render(var7, ItemTransforms.TransformType.GROUND, false, var4, var5, var6, OverlayTexture.NO_OVERLAY, var9);
         var4.popPose();
         if (!var10) {
            var4.translate((double)(0.0F * var16), (double)(0.0F * var17), (double)(0.09375F * var18));
         }
      }

      var4.popPose();
      super.render(var1, var2, var3, var4, var5, var6);
   }

   public ResourceLocation getTextureLocation(ItemEntity var1) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}
