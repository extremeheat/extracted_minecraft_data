package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.WitherBossModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class WitherArmorLayer extends EnergySwirlLayer {
   private static final ResourceLocation WITHER_ARMOR_LOCATION = new ResourceLocation("textures/entity/wither/wither_armor.png");
   private final WitherBossModel model = new WitherBossModel(0.5F);

   public WitherArmorLayer(RenderLayerParent var1) {
      super(var1);
   }

   protected float xOffset(float var1) {
      return Mth.cos(var1 * 0.02F) * 3.0F;
   }

   protected ResourceLocation getTextureLocation() {
      return WITHER_ARMOR_LOCATION;
   }

   protected EntityModel model() {
      return this.model;
   }
}
