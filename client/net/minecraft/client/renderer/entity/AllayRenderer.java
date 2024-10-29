package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.AllayModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.AllayRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.allay.Allay;

public class AllayRenderer extends MobRenderer<Allay, AllayRenderState, AllayModel> {
   private static final ResourceLocation ALLAY_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/allay/allay.png");

   public AllayRenderer(EntityRendererProvider.Context var1) {
      super(var1, new AllayModel(var1.bakeLayer(ModelLayers.ALLAY)), 0.4F);
      this.addLayer(new ItemInHandLayer(this, var1.getItemRenderer()));
   }

   public ResourceLocation getTextureLocation(AllayRenderState var1) {
      return ALLAY_TEXTURE;
   }

   public AllayRenderState createRenderState() {
      return new AllayRenderState();
   }

   public void extractRenderState(Allay var1, AllayRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isDancing = var1.isDancing();
      var2.isSpinning = var1.isSpinning();
      var2.spinningProgress = var1.getSpinningProgress(var3);
      var2.holdingAnimationProgress = var1.getHoldingItemAnimationProgress(var3);
   }

   protected int getBlockLightLevel(Allay var1, BlockPos var2) {
      return 15;
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((AllayRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
