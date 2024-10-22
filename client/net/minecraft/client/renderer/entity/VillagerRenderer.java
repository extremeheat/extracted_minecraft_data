package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;

public class VillagerRenderer extends MobRenderer<Villager, VillagerRenderState, VillagerModel> {
   private static final ResourceLocation VILLAGER_BASE_SKIN = ResourceLocation.withDefaultNamespace("textures/entity/villager/villager.png");
   public static final CustomHeadLayer.Transforms CUSTOM_HEAD_TRANSFORMS = new CustomHeadLayer.Transforms(-0.1171875F, -0.07421875F, 1.0F);

   public VillagerRenderer(EntityRendererProvider.Context var1) {
      super(var1, new VillagerModel(var1.bakeLayer(ModelLayers.VILLAGER)), 0.5F);
      this.addLayer(new CustomHeadLayer<>(this, var1.getModelSet(), CUSTOM_HEAD_TRANSFORMS, var1.getItemRenderer()));
      this.addLayer(new VillagerProfessionLayer<>(this, var1.getResourceManager(), "villager"));
      this.addLayer(new CrossedArmsItemLayer<>(this, var1.getItemRenderer()));
   }

   protected void scale(VillagerRenderState var1, PoseStack var2) {
      super.scale(var1, var2);
      float var3 = var1.ageScale;
      var2.scale(var3, var3, var3);
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
      var2.isUnhappy = var1.getUnhappyCounter() > 0;
      var2.villagerData = var1.getVillagerData();
   }
}
