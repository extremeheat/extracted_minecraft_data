package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.GiantZombieModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Giant;

public class GiantMobRenderer extends MobRenderer<Giant, HumanoidModel<Giant>> {
   private static final ResourceLocation ZOMBIE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/zombie/zombie.png");
   private final float scale;

   public GiantMobRenderer(EntityRendererProvider.Context var1, float var2) {
      super(var1, new GiantZombieModel(var1.bakeLayer(ModelLayers.GIANT)), 0.5F * var2);
      this.scale = var2;
      this.addLayer(new ItemInHandLayer(this, var1.getItemInHandRenderer()));
      this.addLayer(new HumanoidArmorLayer(this, new GiantZombieModel(var1.bakeLayer(ModelLayers.GIANT_INNER_ARMOR)), new GiantZombieModel(var1.bakeLayer(ModelLayers.GIANT_OUTER_ARMOR)), var1.getModelManager()));
   }

   protected void scale(Giant var1, PoseStack var2, float var3) {
      var2.scale(this.scale, this.scale, this.scale);
   }

   public ResourceLocation getTextureLocation(Giant var1) {
      return ZOMBIE_LOCATION;
   }
}
