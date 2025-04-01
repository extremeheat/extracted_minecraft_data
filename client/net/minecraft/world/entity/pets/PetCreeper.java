package net.minecraft.world.entity.pets;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PetCreeper extends AbstractPet {
   public PetCreeper(EntityType<? extends PetCreeper> var1, Level var2) {
      super(var1, var2);
   }

   public boolean isFood(ItemStack var1) {
      return false;
   }
}
