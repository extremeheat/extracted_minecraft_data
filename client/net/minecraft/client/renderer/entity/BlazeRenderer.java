package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.blaze.BlazeModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.Blaze;

public class BlazeRenderer extends MobRenderer<Blaze, LivingEntityRenderState, BlazeModel> {
   private static final Identifier BLAZE_LOCATION = Identifier.withDefaultNamespace("textures/entity/blaze.png");

   public BlazeRenderer(EntityRendererProvider.Context var1) {
      super(var1, new BlazeModel(var1.bakeLayer(ModelLayers.BLAZE)), 0.5F);
   }

   protected int getBlockLightLevel(Blaze var1, BlockPos var2) {
      return 15;
   }

   public Identifier getTextureLocation(LivingEntityRenderState var1) {
      return BLAZE_LOCATION;
   }

   public LivingEntityRenderState createRenderState() {
      return new LivingEntityRenderState();
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
