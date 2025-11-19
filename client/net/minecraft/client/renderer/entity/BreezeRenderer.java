package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.breeze.BreezeModel;
import net.minecraft.client.renderer.entity.layers.BreezeEyesLayer;
import net.minecraft.client.renderer.entity.layers.BreezeWindLayer;
import net.minecraft.client.renderer.entity.state.BreezeRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.breeze.Breeze;

public class BreezeRenderer extends MobRenderer<Breeze, BreezeRenderState, BreezeModel> {
   private static final Identifier TEXTURE_LOCATION = Identifier.withDefaultNamespace("textures/entity/breeze/breeze.png");

   public BreezeRenderer(EntityRendererProvider.Context var1) {
      super(var1, new BreezeModel(var1.bakeLayer(ModelLayers.BREEZE)), 0.5F);
      this.addLayer(new BreezeWindLayer(this, var1.getModelSet()));
      this.addLayer(new BreezeEyesLayer(this, var1.getModelSet()));
   }

   public Identifier getTextureLocation(BreezeRenderState var1) {
      return TEXTURE_LOCATION;
   }

   public BreezeRenderState createRenderState() {
      return new BreezeRenderState();
   }

   public void extractRenderState(Breeze var1, BreezeRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.idle.copyFrom(var1.idle);
      var2.shoot.copyFrom(var1.shoot);
      var2.slide.copyFrom(var1.slide);
      var2.slideBack.copyFrom(var1.slideBack);
      var2.inhale.copyFrom(var1.inhale);
      var2.longJump.copyFrom(var1.longJump);
   }

   // $FF: synthetic method
   public Identifier getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((BreezeRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
