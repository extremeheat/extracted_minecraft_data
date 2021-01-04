package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public class HuskRenderer extends ZombieRenderer {
   private static final ResourceLocation HUSK_LOCATION = new ResourceLocation("textures/entity/zombie/husk.png");

   public HuskRenderer(EntityRenderDispatcher var1) {
      super(var1);
   }

   protected void scale(Zombie var1, float var2) {
      float var3 = 1.0625F;
      GlStateManager.scalef(1.0625F, 1.0625F, 1.0625F);
      super.scale(var1, var2);
   }

   protected ResourceLocation getTextureLocation(Zombie var1) {
      return HUSK_LOCATION;
   }
}
