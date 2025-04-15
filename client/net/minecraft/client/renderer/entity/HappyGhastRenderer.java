package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.HappyGhastHarnessModel;
import net.minecraft.client.model.HappyGhastModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.RopesLayer;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HappyGhastRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.HappyGhast;
import net.minecraft.world.phys.AABB;

public class HappyGhastRenderer extends AgeableMobRenderer<HappyGhast, HappyGhastRenderState, HappyGhastModel> {
   private static final ResourceLocation GHAST_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/ghast/happy_ghast.png");
   private static final ResourceLocation GHAST_BABY_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/ghast/happy_ghast_baby.png");
   private static final ResourceLocation GHAST_ROPES = ResourceLocation.withDefaultNamespace("textures/entity/ghast/happy_ghast_ropes.png");

   public HappyGhastRenderer(EntityRendererProvider.Context var1) {
      super(var1, new HappyGhastModel(var1.bakeLayer(ModelLayers.HAPPY_GHAST)), new HappyGhastModel(var1.bakeLayer(ModelLayers.HAPPY_GHAST_BABY)), 1.5F);
      this.addLayer(new SimpleEquipmentLayer(this, var1.getEquipmentRenderer(), EquipmentClientInfo.LayerType.HAPPY_GHAST_BODY, (var0) -> var0.bodyItem, new HappyGhastHarnessModel(var1.bakeLayer(ModelLayers.HAPPY_GHAST_HARNESS)), new HappyGhastHarnessModel(var1.bakeLayer(ModelLayers.HAPPY_GHAST_BABY_HARNESS))));
      this.addLayer(new RopesLayer(this, var1.getModelSet(), GHAST_ROPES));
   }

   public ResourceLocation getTextureLocation(HappyGhastRenderState var1) {
      return var1.isBaby ? GHAST_BABY_LOCATION : GHAST_LOCATION;
   }

   public HappyGhastRenderState createRenderState() {
      return new HappyGhastRenderState();
   }

   protected AABB getBoundingBoxForCulling(HappyGhast var1) {
      AABB var2 = super.getBoundingBoxForCulling(var1);
      float var3 = var1.getBbHeight();
      return var2.setMinY(var2.minY - (double)(var3 / 2.0F));
   }

   public void extractRenderState(HappyGhast var1, HappyGhastRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.bodyItem = var1.getItemBySlot(EquipmentSlot.BODY).copy();
      var2.isRidden = var1.isVehicle();
      var2.isLeashHolder = var1.isLeashHolder();
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((HappyGhastRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
