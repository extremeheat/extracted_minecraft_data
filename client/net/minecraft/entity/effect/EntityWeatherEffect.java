package net.minecraft.entity.effect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public abstract class EntityWeatherEffect extends Entity {
   public EntityWeatherEffect(EntityType<?> var1, World var2) {
      super(var1, var2);
   }
}
