package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Guardian;

public class ElderGuardianRenderer extends GuardianRenderer {
   private static final ResourceLocation GUARDIAN_ELDER_LOCATION = new ResourceLocation("textures/entity/guardian_elder.png");

   public ElderGuardianRenderer(EntityRenderDispatcher var1) {
      super(var1, 1.2F);
   }

   protected void scale(Guardian var1, float var2) {
      GlStateManager.scalef(ElderGuardian.ELDER_SIZE_SCALE, ElderGuardian.ELDER_SIZE_SCALE, ElderGuardian.ELDER_SIZE_SCALE);
   }

   protected ResourceLocation getTextureLocation(Guardian var1) {
      return GUARDIAN_ELDER_LOCATION;
   }
}
