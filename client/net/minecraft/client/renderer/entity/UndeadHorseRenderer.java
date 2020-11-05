package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.model.HorseModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public class UndeadHorseRenderer extends AbstractHorseRenderer<AbstractHorse, HorseModel<AbstractHorse>> {
   private static final Map<EntityType<?>, ResourceLocation> MAP;

   public UndeadHorseRenderer(EntityRenderDispatcher var1) {
      super(var1, new HorseModel(0.0F), 1.0F);
   }

   public ResourceLocation getTextureLocation(AbstractHorse var1) {
      return (ResourceLocation)MAP.get(var1.getType());
   }

   static {
      MAP = Maps.newHashMap(ImmutableMap.of(EntityType.ZOMBIE_HORSE, new ResourceLocation("textures/entity/horse/horse_zombie.png"), EntityType.SKELETON_HORSE, new ResourceLocation("textures/entity/horse/horse_skeleton.png")));
   }
}
