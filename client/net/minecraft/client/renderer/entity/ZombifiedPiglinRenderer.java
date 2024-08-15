package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.ZombifiedPiglinModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.ZombifiedPiglinRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.ZombifiedPiglin;

public class ZombifiedPiglinRenderer extends HumanoidMobRenderer<ZombifiedPiglin, ZombifiedPiglinRenderState, ZombifiedPiglinModel> {
   private static final ResourceLocation ZOMBIFIED_PIGLIN_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/piglin/zombified_piglin.png");

   public ZombifiedPiglinRenderer(
      EntityRendererProvider.Context var1,
      ModelLayerLocation var2,
      ModelLayerLocation var3,
      ModelLayerLocation var4,
      ModelLayerLocation var5,
      ModelLayerLocation var6,
      ModelLayerLocation var7
   ) {
      super(
         var1,
         new ZombifiedPiglinModel(var1.bakeLayer(var2)),
         new ZombifiedPiglinModel(var1.bakeLayer(var3)),
         0.5F,
         PiglinRenderer.PIGLIN_CUSTOM_HEAD_TRANSFORMS
      );
      this.addLayer(
         new HumanoidArmorLayer<>(
            this,
            new HumanoidArmorModel(var1.bakeLayer(var4)),
            new HumanoidArmorModel(var1.bakeLayer(var5)),
            new HumanoidArmorModel(var1.bakeLayer(var6)),
            new HumanoidArmorModel(var1.bakeLayer(var6)),
            var1.getModelManager()
         )
      );
   }

   public ResourceLocation getTextureLocation(ZombifiedPiglinRenderState var1) {
      return ZOMBIFIED_PIGLIN_LOCATION;
   }

   public ZombifiedPiglinRenderState createRenderState() {
      return new ZombifiedPiglinRenderState();
   }

   public void extractRenderState(ZombifiedPiglin var1, ZombifiedPiglinRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isAggressive = var1.isAggressive();
   }
}
