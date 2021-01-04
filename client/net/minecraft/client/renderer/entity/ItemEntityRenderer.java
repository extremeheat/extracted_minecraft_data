package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.Random;
import net.minecraft.client.renderer.block.model.ItemTransforms;
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

   public ItemEntityRenderer(EntityRenderDispatcher var1, ItemRenderer var2) {
      super(var1);
      this.itemRenderer = var2;
      this.shadowRadius = 0.15F;
      this.shadowStrength = 0.75F;
   }

   private int setupBobbingItem(ItemEntity var1, double var2, double var4, double var6, float var8, BakedModel var9) {
      ItemStack var10 = var1.getItem();
      Item var11 = var10.getItem();
      if (var11 == null) {
         return 0;
      } else {
         boolean var12 = var9.isGui3d();
         int var13 = this.getRenderAmount(var10);
         float var14 = 0.25F;
         float var15 = Mth.sin(((float)var1.getAge() + var8) / 10.0F + var1.bobOffs) * 0.1F + 0.1F;
         float var16 = var9.getTransforms().getTransform(ItemTransforms.TransformType.GROUND).scale.y();
         GlStateManager.translatef((float)var2, (float)var4 + var15 + 0.25F * var16, (float)var6);
         if (var12 || this.entityRenderDispatcher.options != null) {
            float var17 = (((float)var1.getAge() + var8) / 20.0F + var1.bobOffs) * 57.295776F;
            GlStateManager.rotatef(var17, 0.0F, 1.0F, 0.0F);
         }

         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         return var13;
      }
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

   public void render(ItemEntity var1, double var2, double var4, double var6, float var8, float var9) {
      ItemStack var10 = var1.getItem();
      int var11 = var10.isEmpty() ? 187 : Item.getId(var10.getItem()) + var10.getDamageValue();
      this.random.setSeed((long)var11);
      boolean var12 = false;
      if (this.bindTexture(var1)) {
         this.entityRenderDispatcher.textureManager.getTexture(this.getTextureLocation(var1)).pushFilter(false, false);
         var12 = true;
      }

      GlStateManager.enableRescaleNormal();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableBlend();
      Lighting.turnOn();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.pushMatrix();
      BakedModel var13 = this.itemRenderer.getModel(var10, var1.level, (LivingEntity)null);
      int var14 = this.setupBobbingItem(var1, var2, var4, var6, var9, var13);
      float var15 = var13.getTransforms().ground.scale.x();
      float var16 = var13.getTransforms().ground.scale.y();
      float var17 = var13.getTransforms().ground.scale.z();
      boolean var18 = var13.isGui3d();
      float var20;
      float var21;
      if (!var18) {
         float var19 = -0.0F * (float)(var14 - 1) * 0.5F * var15;
         var20 = -0.0F * (float)(var14 - 1) * 0.5F * var16;
         var21 = -0.09375F * (float)(var14 - 1) * 0.5F * var17;
         GlStateManager.translatef(var19, var20, var21);
      }

      if (this.solidRender) {
         GlStateManager.enableColorMaterial();
         GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(var1));
      }

      for(int var23 = 0; var23 < var14; ++var23) {
         if (var18) {
            GlStateManager.pushMatrix();
            if (var23 > 0) {
               var20 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               var21 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float var22 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               GlStateManager.translatef(var20, var21, var22);
            }

            var13.getTransforms().apply(ItemTransforms.TransformType.GROUND);
            this.itemRenderer.render(var10, var13);
            GlStateManager.popMatrix();
         } else {
            GlStateManager.pushMatrix();
            if (var23 > 0) {
               var20 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               var21 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               GlStateManager.translatef(var20, var21, 0.0F);
            }

            var13.getTransforms().apply(ItemTransforms.TransformType.GROUND);
            this.itemRenderer.render(var10, var13);
            GlStateManager.popMatrix();
            GlStateManager.translatef(0.0F * var15, 0.0F * var16, 0.09375F * var17);
         }
      }

      if (this.solidRender) {
         GlStateManager.tearDownSolidRenderingTextureCombine();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.popMatrix();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableBlend();
      this.bindTexture(var1);
      if (var12) {
         this.entityRenderDispatcher.textureManager.getTexture(this.getTextureLocation(var1)).popFilter();
      }

      super.render(var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation getTextureLocation(ItemEntity var1) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}
