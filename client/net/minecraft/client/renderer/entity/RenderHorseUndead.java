package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.renderer.entity.model.ModelHorseArmorBase;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntityZombieHorse;
import net.minecraft.util.ResourceLocation;

public class RenderHorseUndead extends RenderAbstractHorse<EntityHorse> {
   private static final Map<Class<?>, ResourceLocation> field_195638_a = Maps.newHashMap(ImmutableMap.of(EntityZombieHorse.class, new ResourceLocation("textures/entity/horse/horse_zombie.png"), EntitySkeletonHorse.class, new ResourceLocation("textures/entity/horse/horse_skeleton.png")));

   public RenderHorseUndead(RenderManager var1) {
      super(var1, new ModelHorseArmorBase(), 1.0F);
   }

   protected ResourceLocation func_110775_a(AbstractHorse var1) {
      return (ResourceLocation)field_195638_a.get(var1.getClass());
   }
}
