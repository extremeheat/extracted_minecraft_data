package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ZombieVillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.ZombieVillagerRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.ZombieVillager;

public class ZombieVillagerRenderer extends HumanoidMobRenderer<ZombieVillager, ZombieVillagerRenderState, ZombieVillagerModel<ZombieVillagerRenderState>> {
   private static final ResourceLocation ZOMBIE_VILLAGER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/zombie_villager/zombie_villager.png");

   public ZombieVillagerRenderer(EntityRendererProvider.Context var1) {
      super(var1, new ZombieVillagerModel(var1.bakeLayer(ModelLayers.ZOMBIE_VILLAGER)), new ZombieVillagerModel(var1.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_BABY)), 0.5F, VillagerRenderer.CUSTOM_HEAD_TRANSFORMS);
      this.addLayer(new HumanoidArmorLayer(this, new ZombieVillagerModel(var1.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_INNER_ARMOR)), new ZombieVillagerModel(var1.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR)), new ZombieVillagerModel(var1.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_BABY_INNER_ARMOR)), new ZombieVillagerModel(var1.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_BABY_OUTER_ARMOR)), var1.getEquipmentRenderer()));
      this.addLayer(new VillagerProfessionLayer(this, var1.getResourceManager(), "zombie_villager"));
   }

   public ResourceLocation getTextureLocation(ZombieVillagerRenderState var1) {
      return ZOMBIE_VILLAGER_LOCATION;
   }

   public ZombieVillagerRenderState createRenderState() {
      return new ZombieVillagerRenderState();
   }

   public void extractRenderState(ZombieVillager var1, ZombieVillagerRenderState var2, float var3) {
      super.extractRenderState((Mob)var1, (HumanoidRenderState)var2, var3);
      var2.isConverting = var1.isConverting();
      var2.villagerData = var1.getVillagerData();
      var2.isAggressive = var1.isAggressive();
   }

   protected boolean isShaking(ZombieVillagerRenderState var1) {
      return super.isShaking(var1) || var1.isConverting;
   }

   // $FF: synthetic method
   protected boolean isShaking(final LivingEntityRenderState var1) {
      return this.isShaking((ZombieVillagerRenderState)var1);
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((ZombieVillagerRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
