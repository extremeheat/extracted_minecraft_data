package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.DrownedOuterLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Zombie;

public class DrownedRenderer extends AbstractZombieRenderer<Drowned, DrownedModel<Drowned>> {
   private static final ResourceLocation DROWNED_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/zombie/drowned.png");

   public DrownedRenderer(EntityRendererProvider.Context var1) {
      super(var1, new DrownedModel(var1.bakeLayer(ModelLayers.DROWNED)), new DrownedModel(var1.bakeLayer(ModelLayers.DROWNED_INNER_ARMOR)), new DrownedModel(var1.bakeLayer(ModelLayers.DROWNED_OUTER_ARMOR)));
      this.addLayer(new DrownedOuterLayer(this, var1.getModelSet()));
   }

   public ResourceLocation getTextureLocation(Zombie var1) {
      return DROWNED_LOCATION;
   }

   protected void setupRotations(Drowned var1, PoseStack var2, float var3, float var4, float var5, float var6) {
      super.setupRotations(var1, var2, var3, var4, var5, var6);
      float var7 = var1.getSwimAmount(var5);
      if (var7 > 0.0F) {
         float var8 = -10.0F - var1.getXRot();
         float var9 = Mth.lerp(var7, 0.0F, var8);
         var2.rotateAround(Axis.XP.rotationDegrees(var9), 0.0F, var1.getBbHeight() / 2.0F / var6, 0.0F);
      }

   }
}
