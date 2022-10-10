package net.minecraft.entity.passive;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public abstract class EntityAmbientCreature extends EntityLiving implements IAnimal {
   protected EntityAmbientCreature(EntityType<?> var1, World var2) {
      super(var1, var2);
   }

   public boolean func_184652_a(EntityPlayer var1) {
      return false;
   }
}
