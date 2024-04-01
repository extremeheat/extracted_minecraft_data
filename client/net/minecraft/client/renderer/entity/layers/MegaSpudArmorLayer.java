package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.MegaSpudModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.MegaSpud;

public class MegaSpudArmorLayer extends EnergySwirlLayer<MegaSpud, MegaSpudModel<MegaSpud>> {
   private static final ResourceLocation WITHER_ARMOR_LOCATION = new ResourceLocation("textures/entity/wither/wither_armor.png");
   private final MegaSpudModel<MegaSpud> model;

   public MegaSpudArmorLayer(RenderLayerParent<MegaSpud, MegaSpudModel<MegaSpud>> var1, EntityModelSet var2) {
      super(var1);
      this.model = new MegaSpudModel<>(var2.bakeLayer(ModelLayers.MEGA_SPUD_OUTER));
   }

   @Override
   protected float xOffset(float var1) {
      return Mth.cos(var1 * 0.02F) * 3.0F;
   }

   @Override
   protected ResourceLocation getTextureLocation() {
      return WITHER_ARMOR_LOCATION;
   }

   @Override
   protected EntityModel<MegaSpud> model() {
      return this.model;
   }
}
