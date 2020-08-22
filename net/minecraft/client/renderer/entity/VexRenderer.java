package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.VexModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Vex;

public class VexRenderer extends HumanoidMobRenderer {
   private static final ResourceLocation VEX_LOCATION = new ResourceLocation("textures/entity/illager/vex.png");
   private static final ResourceLocation VEX_CHARGING_LOCATION = new ResourceLocation("textures/entity/illager/vex_charging.png");

   public VexRenderer(EntityRenderDispatcher var1) {
      super(var1, new VexModel(), 0.3F);
   }

   protected int getBlockLightLevel(Vex var1, float var2) {
      return 15;
   }

   public ResourceLocation getTextureLocation(Vex var1) {
      return var1.isCharging() ? VEX_CHARGING_LOCATION : VEX_LOCATION;
   }

   protected void scale(Vex var1, PoseStack var2, float var3) {
      var2.scale(0.4F, 0.4F, 0.4F);
   }
}
