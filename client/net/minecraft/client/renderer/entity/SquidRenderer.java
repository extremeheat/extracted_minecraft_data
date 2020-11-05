package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.SquidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Squid;

public class SquidRenderer extends MobRenderer<Squid, SquidModel<Squid>> {
   private static final ResourceLocation SQUID_LOCATION = new ResourceLocation("textures/entity/squid.png");

   public SquidRenderer(EntityRenderDispatcher var1) {
      super(var1, new SquidModel(), 0.7F);
   }

   public ResourceLocation getTextureLocation(Squid var1) {
      return SQUID_LOCATION;
   }

   protected void setupRotations(Squid var1, PoseStack var2, float var3, float var4, float var5) {
      float var6 = Mth.lerp(var5, var1.xBodyRotO, var1.xBodyRot);
      float var7 = Mth.lerp(var5, var1.zBodyRotO, var1.zBodyRot);
      var2.translate(0.0D, 0.5D, 0.0D);
      var2.mulPose(Vector3f.YP.rotationDegrees(180.0F - var4));
      var2.mulPose(Vector3f.XP.rotationDegrees(var6));
      var2.mulPose(Vector3f.YP.rotationDegrees(var7));
      var2.translate(0.0D, -1.2000000476837158D, 0.0D);
   }

   protected float getBob(Squid var1, float var2) {
      return Mth.lerp(var2, var1.oldTentacleAngle, var1.tentacleAngle);
   }
}
