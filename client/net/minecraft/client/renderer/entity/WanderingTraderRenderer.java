package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.WanderingTrader;

public class WanderingTraderRenderer extends MobRenderer<WanderingTrader, VillagerRenderState, VillagerModel> {
   private static final ResourceLocation VILLAGER_BASE_SKIN = ResourceLocation.withDefaultNamespace("textures/entity/wandering_trader.png");

   public WanderingTraderRenderer(EntityRendererProvider.Context var1) {
      super(var1, new VillagerModel(var1.bakeLayer(ModelLayers.WANDERING_TRADER)), 0.5F);
      this.addLayer(new CustomHeadLayer<>(this, var1.getModelSet(), var1.getItemRenderer()));
      this.addLayer(new CrossedArmsItemLayer<>(this, var1.getItemRenderer()));
   }

   public ResourceLocation getTextureLocation(VillagerRenderState var1) {
      return VILLAGER_BASE_SKIN;
   }

   public VillagerRenderState createRenderState() {
      return new VillagerRenderState();
   }

   public void extractRenderState(WanderingTrader var1, VillagerRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isUnhappy = var1.getUnhappyCounter() > 0;
   }
}
