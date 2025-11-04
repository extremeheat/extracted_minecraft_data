package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PiglinRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.item.CrossbowItem;

public class PiglinRenderer extends HumanoidMobRenderer<AbstractPiglin, PiglinRenderState, PiglinModel> {
   private static final Identifier PIGLIN_LOCATION = Identifier.withDefaultNamespace("textures/entity/piglin/piglin.png");
   private static final Identifier PIGLIN_BRUTE_LOCATION = Identifier.withDefaultNamespace("textures/entity/piglin/piglin_brute.png");
   public static final CustomHeadLayer.Transforms PIGLIN_CUSTOM_HEAD_TRANSFORMS = new CustomHeadLayer.Transforms(0.0F, 0.0F, 1.0019531F);

   public PiglinRenderer(EntityRendererProvider.Context var1, ModelLayerLocation var2, ModelLayerLocation var3, ArmorModelSet<ModelLayerLocation> var4, ArmorModelSet<ModelLayerLocation> var5) {
      super(var1, new PiglinModel(var1.bakeLayer(var2)), new PiglinModel(var1.bakeLayer(var3)), 0.5F, PIGLIN_CUSTOM_HEAD_TRANSFORMS);
      this.addLayer(new HumanoidArmorLayer(this, ArmorModelSet.bake(var4, var1.getModelSet(), PiglinModel::new), ArmorModelSet.bake(var5, var1.getModelSet(), PiglinModel::new), var1.getEquipmentRenderer()));
   }

   public Identifier getTextureLocation(PiglinRenderState var1) {
      return var1.isBrute ? PIGLIN_BRUTE_LOCATION : PIGLIN_LOCATION;
   }

   public PiglinRenderState createRenderState() {
      return new PiglinRenderState();
   }

   public void extractRenderState(AbstractPiglin var1, PiglinRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isBrute = var1.getType() == EntityType.PIGLIN_BRUTE;
      var2.armPose = var1.getArmPose();
      var2.maxCrossbowChageDuration = (float)CrossbowItem.getChargeDuration(var1.getUseItem(), var1);
      var2.isConverting = var1.isConverting();
   }

   protected boolean isShaking(PiglinRenderState var1) {
      return super.isShaking(var1) || var1.isConverting;
   }

   // $FF: synthetic method
   protected boolean isShaking(final LivingEntityRenderState var1) {
      return this.isShaking((PiglinRenderState)var1);
   }

   // $FF: synthetic method
   public Identifier getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((PiglinRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
