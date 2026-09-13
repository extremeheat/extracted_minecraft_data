package net.minecraft.entity.monster;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.world.World;

public abstract class EntityGolem extends EntityCreature implements IAnimals {
   public EntityGolem(World var1) {
      super(var1);
   }

   @Override
   protected void func_70069_a(float var1) {
   }

   @Override
   protected String func_70639_aQ() {
      return "none";
   }

   @Override
   protected String func_70621_aR() {
      return "none";
   }

   @Override
   protected String func_70673_aS() {
      return "none";
   }

   @Override
   public int func_70627_aG() {
      return 120;
   }

   @Override
   protected boolean func_70692_ba() {
      return false;
   }
}
