package net.minecraft.entity.passive;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public abstract class EntityAmbientCreature extends EntityLiving implements IAnimals {
   public EntityAmbientCreature(World var1) {
      super(var1);
   }

   public boolean func_110164_bC() {
      return false;
   }

   protected boolean func_70085_c(EntityPlayer var1) {
      return false;
   }
}
