package net.minecraft.world.entity.ambient;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

public abstract class AmbientCreature extends Mob {
   protected AmbientCreature(EntityType<? extends AmbientCreature> var1, Level var2) {
      super(var1, var2);
   }

   public boolean canBeLeashed() {
      return false;
   }
}
