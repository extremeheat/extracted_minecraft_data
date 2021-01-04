package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.renderer.entity.layers.IronGolemFlowerLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem;

public class IronGolemRenderer extends MobRenderer<IronGolem, IronGolemModel<IronGolem>> {
   private static final ResourceLocation GOLEM_LOCATION = new ResourceLocation("textures/entity/iron_golem.png");

   public IronGolemRenderer(EntityRenderDispatcher var1) {
      super(var1, new IronGolemModel(), 0.7F);
      this.addLayer(new IronGolemFlowerLayer(this));
   }

   protected ResourceLocation getTextureLocation(IronGolem var1) {
      return GOLEM_LOCATION;
   }

   protected void setupRotations(IronGolem var1, float var2, float var3, float var4) {
      super.setupRotations(var1, var2, var3, var4);
      if ((double)var1.animationSpeed >= 0.01D) {
         float var5 = 13.0F;
         float var6 = var1.animationPosition - var1.animationSpeed * (1.0F - var4) + 6.0F;
         float var7 = (Math.abs(var6 % 13.0F - 6.5F) - 3.25F) / 3.25F;
         GlStateManager.rotatef(6.5F * var7, 0.0F, 0.0F, 1.0F);
      }
   }
}
