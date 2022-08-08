package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.DyeableHorseArmorItem;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.ItemStack;

public class HorseArmorLayer extends RenderLayer<Horse, HorseModel<Horse>> {
   private final HorseModel<Horse> model;

   public HorseArmorLayer(RenderLayerParent<Horse, HorseModel<Horse>> var1, EntityModelSet var2) {
      super(var1);
      this.model = new HorseModel(var2.bakeLayer(ModelLayers.HORSE_ARMOR));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, Horse var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      ItemStack var11 = var4.getArmor();
      if (var11.getItem() instanceof HorseArmorItem) {
         HorseArmorItem var12 = (HorseArmorItem)var11.getItem();
         ((HorseModel)this.getParentModel()).copyPropertiesTo(this.model);
         this.model.prepareMobModel((AbstractHorse)var4, var5, var6, var7);
         this.model.setupAnim((AbstractHorse)var4, var5, var6, var8, var9, var10);
         float var13;
         float var14;
         float var15;
         if (var12 instanceof DyeableHorseArmorItem) {
            int var16 = ((DyeableHorseArmorItem)var12).getColor(var11);
            var13 = (float)(var16 >> 16 & 255) / 255.0F;
            var14 = (float)(var16 >> 8 & 255) / 255.0F;
            var15 = (float)(var16 & 255) / 255.0F;
         } else {
            var13 = 1.0F;
            var14 = 1.0F;
            var15 = 1.0F;
         }

         VertexConsumer var17 = var2.getBuffer(RenderType.entityCutoutNoCull(var12.getTexture()));
         this.model.renderToBuffer(var1, var17, var3, OverlayTexture.NO_OVERLAY, var13, var14, var15, 1.0F);
      }
   }
}
