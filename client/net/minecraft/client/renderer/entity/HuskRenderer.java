package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public class HuskRenderer extends ZombieRenderer {
   private static final ResourceLocation HUSK_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/zombie/husk.png");

   public HuskRenderer(EntityRendererProvider.Context var1) {
      super(var1, ModelLayers.HUSK, ModelLayers.HUSK_INNER_ARMOR, ModelLayers.HUSK_OUTER_ARMOR);
   }

   protected void scale(Zombie var1, PoseStack var2, float var3) {
      float var4 = 1.0625F;
      var2.scale(1.0625F, 1.0625F, 1.0625F);
      super.scale(var1, var2, var3);
   }

   public ResourceLocation getTextureLocation(Zombie var1) {
      return HUSK_LOCATION;
   }
}
