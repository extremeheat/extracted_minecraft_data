package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.PhantomEyesLayer;
import net.minecraft.client.renderer.entity.state.PhantomRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Phantom;

public class PhantomRenderer extends MobRenderer<Phantom, PhantomRenderState, PhantomModel> {
   private static final ResourceLocation PHANTOM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/phantom.png");

   public PhantomRenderer(EntityRendererProvider.Context var1) {
      super(var1, new PhantomModel(var1.bakeLayer(ModelLayers.PHANTOM)), 0.75F);
      this.addLayer(new PhantomEyesLayer(this));
   }

   public ResourceLocation getTextureLocation(PhantomRenderState var1) {
      return PHANTOM_LOCATION;
   }

   public PhantomRenderState createRenderState() {
      return new PhantomRenderState();
   }

   public void extractRenderState(Phantom var1, PhantomRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.flapTime = (float)var1.getUniqueFlapTickOffset() + var2.ageInTicks;
      var2.size = var1.getPhantomSize();
   }

   protected void scale(PhantomRenderState var1, PoseStack var2) {
      float var3 = 1.0F + 0.15F * (float)var1.size;
      var2.scale(var3, var3, var3);
      var2.translate(0.0F, 1.3125F, 0.1875F);
   }

   protected void setupRotations(PhantomRenderState var1, PoseStack var2, float var3, float var4) {
      super.setupRotations(var1, var2, var3, var4);
      var2.mulPose(Axis.XP.rotationDegrees(var1.xRot));
   }
}
