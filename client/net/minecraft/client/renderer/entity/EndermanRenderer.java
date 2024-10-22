package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.CarriedBlockLayer;
import net.minecraft.client.renderer.entity.layers.EnderEyesLayer;
import net.minecraft.client.renderer.entity.state.EndermanRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.phys.Vec3;

public class EndermanRenderer extends MobRenderer<EnderMan, EndermanRenderState, EndermanModel<EndermanRenderState>> {
   private static final ResourceLocation ENDERMAN_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/enderman/enderman.png");
   private final RandomSource random = RandomSource.create();

   public EndermanRenderer(EntityRendererProvider.Context var1) {
      super(var1, new EndermanModel<>(var1.bakeLayer(ModelLayers.ENDERMAN)), 0.5F);
      this.addLayer(new EnderEyesLayer(this));
      this.addLayer(new CarriedBlockLayer(this, var1.getBlockRenderDispatcher()));
   }

   public Vec3 getRenderOffset(EndermanRenderState var1) {
      Vec3 var2 = super.getRenderOffset(var1);
      if (var1.isCreepy) {
         double var3 = 0.02 * (double)var1.scale;
         return var2.add(this.random.nextGaussian() * var3, 0.0, this.random.nextGaussian() * var3);
      } else {
         return var2;
      }
   }

   public ResourceLocation getTextureLocation(EndermanRenderState var1) {
      return ENDERMAN_LOCATION;
   }

   public EndermanRenderState createRenderState() {
      return new EndermanRenderState();
   }

   public void extractRenderState(EnderMan var1, EndermanRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      HumanoidMobRenderer.extractHumanoidRenderState(var1, var2, var3);
      var2.isCreepy = var1.isCreepy();
      var2.carriedBlock = var1.getCarriedBlock();
   }
}
