package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public abstract class AbstractZombieRenderer extends HumanoidMobRenderer {
   private static final ResourceLocation ZOMBIE_LOCATION = new ResourceLocation("textures/entity/zombie/zombie.png");

   protected AbstractZombieRenderer(EntityRenderDispatcher var1, ZombieModel var2, ZombieModel var3, ZombieModel var4) {
      super(var1, var2, 0.5F);
      this.addLayer(new HumanoidArmorLayer(this, var3, var4));
   }

   public ResourceLocation getTextureLocation(Zombie var1) {
      return ZOMBIE_LOCATION;
   }

   protected void setupRotations(Zombie var1, PoseStack var2, float var3, float var4, float var5) {
      if (var1.isUnderWaterConverting()) {
         var4 += (float)(Math.cos((double)var1.tickCount * 3.25D) * 3.141592653589793D * 0.25D);
      }

      super.setupRotations(var1, var2, var3, var4, var5);
   }
}
