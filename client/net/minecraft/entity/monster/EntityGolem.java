package net.minecraft.entity.monster;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.world.World;

public abstract class EntityGolem extends EntityCreature implements IAnimals {
   public EntityGolem(World var1) {
      super(var1);
   }

   public void func_180430_e(float var1, float var2) {
   }

   protected String func_70639_aQ() {
      return "none";
   }

   protected String func_70621_aR() {
      return "none";
   }

   protected String func_70673_aS() {
      return "none";
   }

   public int func_70627_aG() {
      return 120;
   }

   protected boolean func_70692_ba() {
      return false;
   }
}
