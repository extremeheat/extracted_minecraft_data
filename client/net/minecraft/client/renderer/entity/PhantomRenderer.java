package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.renderer.entity.layers.PhantomEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Phantom;

public class PhantomRenderer extends MobRenderer<Phantom, PhantomModel<Phantom>> {
   private static final ResourceLocation PHANTOM_LOCATION = new ResourceLocation("textures/entity/phantom.png");

   public PhantomRenderer(EntityRenderDispatcher var1) {
      super(var1, new PhantomModel(), 0.75F);
      this.addLayer(new PhantomEyesLayer(this));
   }

   public ResourceLocation getTextureLocation(Phantom var1) {
      return PHANTOM_LOCATION;
   }

   protected void scale(Phantom var1, PoseStack var2, float var3) {
      int var4 = var1.getPhantomSize();
      float var5 = 1.0F + 0.15F * (float)var4;
      var2.scale(var5, var5, var5);
      var2.translate(0.0D, 1.3125D, 0.1875D);
   }

   protected void setupRotations(Phantom var1, PoseStack var2, float var3, float var4, float var5) {
      super.setupRotations(var1, var2, var3, var4, var5);
      var2.mulPose(Vector3f.XP.rotationDegrees(var1.xRot));
   }
}
