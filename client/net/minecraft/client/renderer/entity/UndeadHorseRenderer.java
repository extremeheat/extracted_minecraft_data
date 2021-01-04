package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.model.HorseModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.ZombieHorse;

public class UndeadHorseRenderer extends AbstractHorseRenderer<AbstractHorse, HorseModel<AbstractHorse>> {
   private static final Map<Class<?>, ResourceLocation> MAP = Maps.newHashMap(ImmutableMap.of(ZombieHorse.class, new ResourceLocation("textures/entity/horse/horse_zombie.png"), SkeletonHorse.class, new ResourceLocation("textures/entity/horse/horse_skeleton.png")));

   public UndeadHorseRenderer(EntityRenderDispatcher var1) {
      super(var1, new HorseModel(0.0F), 1.0F);
   }

   protected ResourceLocation getTextureLocation(AbstractHorse var1) {
      return (ResourceLocation)MAP.get(var1.getClass());
   }
}
