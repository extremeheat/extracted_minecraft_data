package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.VexModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.VexRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Vex;

public class VexRenderer extends MobRenderer<Vex, VexRenderState, VexModel> {
   private static final ResourceLocation VEX_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/illager/vex.png");
   private static final ResourceLocation VEX_CHARGING_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/illager/vex_charging.png");

   public VexRenderer(EntityRendererProvider.Context var1) {
      super(var1, new VexModel(var1.bakeLayer(ModelLayers.VEX)), 0.3F);
      this.addLayer(new ItemInHandLayer(this));
   }

   protected int getBlockLightLevel(Vex var1, BlockPos var2) {
      return 15;
   }

   public ResourceLocation getTextureLocation(VexRenderState var1) {
      return var1.isCharging ? VEX_CHARGING_LOCATION : VEX_LOCATION;
   }

   public VexRenderState createRenderState() {
      return new VexRenderState();
   }

   public void extractRenderState(Vex var1, VexRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      ArmedEntityRenderState.extractArmedEntityRenderState(var1, var2, this.itemModelResolver);
      var2.isCharging = var1.isCharging();
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((VexRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
