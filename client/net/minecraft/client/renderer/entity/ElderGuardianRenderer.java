package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Guardian;

public class ElderGuardianRenderer extends GuardianRenderer {
   public static final ResourceLocation GUARDIAN_ELDER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/guardian_elder.png");

   public ElderGuardianRenderer(EntityRendererProvider.Context var1) {
      super(var1, 1.2F, ModelLayers.ELDER_GUARDIAN);
   }

   protected void scale(Guardian var1, PoseStack var2, float var3) {
      var2.scale(ElderGuardian.ELDER_SIZE_SCALE, ElderGuardian.ELDER_SIZE_SCALE, ElderGuardian.ELDER_SIZE_SCALE);
   }

   public ResourceLocation getTextureLocation(Guardian var1) {
      return GUARDIAN_ELDER_LOCATION;
   }
}
