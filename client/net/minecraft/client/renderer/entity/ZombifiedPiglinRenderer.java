package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.monster.piglin.ZombifiedPiglinModel;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.ZombifiedPiglinRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;

public class ZombifiedPiglinRenderer extends HumanoidMobRenderer<ZombifiedPiglin, ZombifiedPiglinRenderState, ZombifiedPiglinModel> {
   private static final Identifier ZOMBIFIED_PIGLIN_LOCATION = Identifier.withDefaultNamespace("textures/entity/piglin/zombified_piglin.png");

   public ZombifiedPiglinRenderer(EntityRendererProvider.Context var1, ModelLayerLocation var2, ModelLayerLocation var3, ArmorModelSet<ModelLayerLocation> var4, ArmorModelSet<ModelLayerLocation> var5) {
      super(var1, new ZombifiedPiglinModel(var1.bakeLayer(var2)), new ZombifiedPiglinModel(var1.bakeLayer(var3)), 0.5F, PiglinRenderer.PIGLIN_CUSTOM_HEAD_TRANSFORMS);
      this.addLayer(new HumanoidArmorLayer(this, ArmorModelSet.bake(var4, var1.getModelSet(), ZombifiedPiglinModel::new), ArmorModelSet.bake(var5, var1.getModelSet(), ZombifiedPiglinModel::new), var1.getEquipmentRenderer()));
   }

   public Identifier getTextureLocation(ZombifiedPiglinRenderState var1) {
      return ZOMBIFIED_PIGLIN_LOCATION;
   }

   public ZombifiedPiglinRenderState createRenderState() {
      return new ZombifiedPiglinRenderState();
   }

   public void extractRenderState(ZombifiedPiglin var1, ZombifiedPiglinRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isAggressive = var1.isAggressive();
   }

   // $FF: synthetic method
   public Identifier getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((ZombifiedPiglinRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
