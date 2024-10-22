package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.RavagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.RavagerRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Ravager;

public class RavagerRenderer extends MobRenderer<Ravager, RavagerRenderState, RavagerModel> {
   private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/illager/ravager.png");

   public RavagerRenderer(EntityRendererProvider.Context var1) {
      super(var1, new RavagerModel(var1.bakeLayer(ModelLayers.RAVAGER)), 1.1F);
   }

   public ResourceLocation getTextureLocation(RavagerRenderState var1) {
      return TEXTURE_LOCATION;
   }

   public RavagerRenderState createRenderState() {
      return new RavagerRenderState();
   }

   public void extractRenderState(Ravager var1, RavagerRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.stunnedTicksRemaining = (float)var1.getStunnedTick() > 0.0F ? (float)var1.getStunnedTick() - var3 : 0.0F;
      var2.attackTicksRemaining = (float)var1.getAttackTick() > 0.0F ? (float)var1.getAttackTick() - var3 : 0.0F;
      if (var1.getRoarTick() > 0) {
         var2.roarAnimation = ((float)(20 - var1.getRoarTick()) + var3) / 20.0F;
      } else {
         var2.roarAnimation = 0.0F;
      }
   }
}
