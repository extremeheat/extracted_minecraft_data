package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.SquidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Squid;

public class SquidRenderer extends MobRenderer<Squid, SquidModel<Squid>> {
   private static final ResourceLocation SQUID_LOCATION = new ResourceLocation("textures/entity/squid.png");

   public SquidRenderer(EntityRenderDispatcher var1) {
      super(var1, new SquidModel(), 0.7F);
   }

   protected ResourceLocation getTextureLocation(Squid var1) {
      return SQUID_LOCATION;
   }

   protected void setupRotations(Squid var1, float var2, float var3, float var4) {
      float var5 = Mth.lerp(var4, var1.xBodyRotO, var1.xBodyRot);
      float var6 = Mth.lerp(var4, var1.zBodyRotO, var1.zBodyRot);
      GlStateManager.translatef(0.0F, 0.5F, 0.0F);
      GlStateManager.rotatef(180.0F - var3, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(var5, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(var6, 0.0F, 1.0F, 0.0F);
      GlStateManager.translatef(0.0F, -1.2F, 0.0F);
   }

   protected float getBob(Squid var1, float var2) {
      return Mth.lerp(var2, var1.oldTentacleAngle, var1.tentacleAngle);
   }

   // $FF: synthetic method
   protected float getBob(LivingEntity var1, float var2) {
      return this.getBob((Squid)var1, var2);
   }
}
