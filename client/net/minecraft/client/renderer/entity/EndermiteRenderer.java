package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.EndermiteModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Endermite;

public class EndermiteRenderer extends MobRenderer<Endermite, EndermiteModel<Endermite>> {
   private static final ResourceLocation ENDERMITE_LOCATION = new ResourceLocation("textures/entity/endermite.png");

   public EndermiteRenderer(EntityRenderDispatcher var1) {
      super(var1, new EndermiteModel(), 0.3F);
   }

   protected float getFlipDegrees(Endermite var1) {
      return 180.0F;
   }

   public ResourceLocation getTextureLocation(Endermite var1) {
      return ENDERMITE_LOCATION;
   }

   // $FF: synthetic method
   protected float getFlipDegrees(LivingEntity var1) {
      return this.getFlipDegrees((Endermite)var1);
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(Entity var1) {
      return this.getTextureLocation((Endermite)var1);
   }
}
