package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.DrownedOuterLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Drowned;

public class DrownedRenderer extends AbstractZombieRenderer<Drowned, ZombieRenderState, DrownedModel> {
   private static final ResourceLocation DROWNED_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/zombie/drowned.png");

   public DrownedRenderer(EntityRendererProvider.Context var1) {
      super(var1, new DrownedModel(var1.bakeLayer(ModelLayers.DROWNED)), new DrownedModel(var1.bakeLayer(ModelLayers.DROWNED_BABY)), new DrownedModel(var1.bakeLayer(ModelLayers.DROWNED_INNER_ARMOR)), new DrownedModel(var1.bakeLayer(ModelLayers.DROWNED_OUTER_ARMOR)), new DrownedModel(var1.bakeLayer(ModelLayers.DROWNED_BABY_INNER_ARMOR)), new DrownedModel(var1.bakeLayer(ModelLayers.DROWNED_BABY_OUTER_ARMOR)));
      this.addLayer(new DrownedOuterLayer(this, var1.getModelSet()));
   }

   public ZombieRenderState createRenderState() {
      return new ZombieRenderState();
   }

   public ResourceLocation getTextureLocation(ZombieRenderState var1) {
      return DROWNED_LOCATION;
   }

   protected void setupRotations(ZombieRenderState var1, PoseStack var2, float var3, float var4) {
      super.setupRotations(var1, var2, var3, var4);
      float var5 = var1.swimAmount;
      if (var5 > 0.0F) {
         float var6 = -10.0F - var1.xRot;
         float var7 = Mth.lerp(var5, 0.0F, var6);
         var2.rotateAround(Axis.XP.rotationDegrees(var7), 0.0F, var1.boundingBoxHeight / 2.0F / var4, 0.0F);
      }

   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((ZombieRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
