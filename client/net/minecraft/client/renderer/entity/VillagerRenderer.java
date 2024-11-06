package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HoldingEntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;

public class VillagerRenderer extends AgeableMobRenderer<Villager, VillagerRenderState, VillagerModel> {
   private static final ResourceLocation VILLAGER_BASE_SKIN = ResourceLocation.withDefaultNamespace("textures/entity/villager/villager.png");
   public static final CustomHeadLayer.Transforms CUSTOM_HEAD_TRANSFORMS = new CustomHeadLayer.Transforms(-0.1171875F, -0.07421875F, 1.0F);

   public VillagerRenderer(EntityRendererProvider.Context var1) {
      super(var1, new VillagerModel(var1.bakeLayer(ModelLayers.VILLAGER)), new VillagerModel(var1.bakeLayer(ModelLayers.VILLAGER_BABY)), 0.5F);
      this.addLayer(new CustomHeadLayer(this, var1.getModelSet(), CUSTOM_HEAD_TRANSFORMS));
      this.addLayer(new VillagerProfessionLayer(this, var1.getResourceManager(), "villager"));
      this.addLayer(new CrossedArmsItemLayer(this));
   }

   public ResourceLocation getTextureLocation(VillagerRenderState var1) {
      return VILLAGER_BASE_SKIN;
   }

   protected float getShadowRadius(VillagerRenderState var1) {
      float var2 = super.getShadowRadius(var1);
      return var1.isBaby ? var2 * 0.5F : var2;
   }

   public VillagerRenderState createRenderState() {
      return new VillagerRenderState();
   }

   public void extractRenderState(Villager var1, VillagerRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      HoldingEntityRenderState.extractHoldingEntityRenderState(var1, var2, this.itemModelResolver);
      var2.isUnhappy = var1.getUnhappyCounter() > 0;
      var2.villagerData = var1.getVillagerData();
   }

   // $FF: synthetic method
   protected float getShadowRadius(final LivingEntityRenderState var1) {
      return this.getShadowRadius((VillagerRenderState)var1);
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((VillagerRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }

   // $FF: synthetic method
   protected float getShadowRadius(final EntityRenderState var1) {
      return this.getShadowRadius((VillagerRenderState)var1);
   }
}
