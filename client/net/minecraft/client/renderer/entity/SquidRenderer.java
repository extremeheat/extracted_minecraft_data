package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.SquidModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.SquidRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Squid;

public class SquidRenderer<T extends Squid> extends AgeableMobRenderer<T, SquidRenderState, SquidModel> {
   private static final ResourceLocation SQUID_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/squid/squid.png");

   public SquidRenderer(EntityRendererProvider.Context var1, SquidModel var2, SquidModel var3) {
      super(var1, var2, var3, 0.7F);
   }

   public ResourceLocation getTextureLocation(SquidRenderState var1) {
      return SQUID_LOCATION;
   }

   public SquidRenderState createRenderState() {
      return new SquidRenderState();
   }

   public void extractRenderState(T var1, SquidRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.tentacleAngle = Mth.lerp(var3, var1.oldTentacleAngle, var1.tentacleAngle);
      var2.xBodyRot = Mth.lerp(var3, var1.xBodyRotO, var1.xBodyRot);
      var2.zBodyRot = Mth.lerp(var3, var1.zBodyRotO, var1.zBodyRot);
   }

   protected void setupRotations(SquidRenderState var1, PoseStack var2, float var3, float var4) {
      var2.translate(0.0F, var1.isBaby ? 0.25F : 0.5F, 0.0F);
      var2.mulPose(Axis.YP.rotationDegrees(180.0F - var3));
      var2.mulPose(Axis.XP.rotationDegrees(var1.xBodyRot));
      var2.mulPose(Axis.YP.rotationDegrees(var1.zBodyRot));
      var2.translate(0.0F, var1.isBaby ? -0.6F : -1.2F, 0.0F);
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((SquidRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
