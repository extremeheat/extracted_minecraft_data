package net.minecraft.world.entity.ambient;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class Batato extends Bat {
   public Batato(EntityType<? extends Bat> var1, Level var2) {
      super(var1, var2);
   }

   @Override
   public boolean isPotato() {
      return true;
   }
}
