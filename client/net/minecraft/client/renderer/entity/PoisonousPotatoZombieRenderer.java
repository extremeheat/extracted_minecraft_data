package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.PoisonousPotatoZombieModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public class PoisonousPotatoZombieRenderer extends AbstractZombieRenderer<Zombie, PoisonousPotatoZombieModel<Zombie>> {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/zombie/poisonous_potato_zombie.png");

   public PoisonousPotatoZombieRenderer(EntityRendererProvider.Context var1) {
      this(var1, ModelLayers.ZOMBIE, ModelLayers.ZOMBIE_INNER_ARMOR, ModelLayers.ZOMBIE_OUTER_ARMOR);
   }

   @Override
   public ResourceLocation getTextureLocation(Zombie var1) {
      return TEXTURE_LOCATION;
   }

   public PoisonousPotatoZombieRenderer(EntityRendererProvider.Context var1, ModelLayerLocation var2, ModelLayerLocation var3, ModelLayerLocation var4) {
      super(
         var1,
         new PoisonousPotatoZombieModel<>(var1.bakeLayer(var2)),
         new PoisonousPotatoZombieModel<>(var1.bakeLayer(var3)),
         new PoisonousPotatoZombieModel<>(var1.bakeLayer(var4))
      );
   }
}
