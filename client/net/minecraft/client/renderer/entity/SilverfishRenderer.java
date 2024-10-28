package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SilverfishModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Silverfish;

public class SilverfishRenderer extends MobRenderer<Silverfish, SilverfishModel<Silverfish>> {
   private static final ResourceLocation SILVERFISH_LOCATION = new ResourceLocation("textures/entity/silverfish.png");

   public SilverfishRenderer(EntityRendererProvider.Context var1) {
      super(var1, new SilverfishModel(var1.bakeLayer(ModelLayers.SILVERFISH)), 0.3F);
   }

   protected float getFlipDegrees(Silverfish var1) {
      return 180.0F;
   }

   public ResourceLocation getTextureLocation(Silverfish var1) {
      return SILVERFISH_LOCATION;
   }

   // $FF: synthetic method
   protected float getFlipDegrees(LivingEntity var1) {
      return this.getFlipDegrees((Silverfish)var1);
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(Entity var1) {
      return this.getTextureLocation((Silverfish)var1);
   }
}
