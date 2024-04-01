package net.minecraft.world.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class EyeOfPotato extends EyeOfEnder {
   public EyeOfPotato(EntityType<? extends EyeOfPotato> var1, Level var2) {
      super(var1, var2);
   }

   public EyeOfPotato(Level var1, double var2, double var4, double var6) {
      this(EntityType.EYE_OF_POTATO, var1);
      this.setPos(var2, var4, var6);
   }
}
