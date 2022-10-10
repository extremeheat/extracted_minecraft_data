package net.minecraft.entity.passive;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public abstract class EntityWaterMob extends EntityCreature implements IAnimal {
   protected EntityWaterMob(EntityType<?> var1, World var2) {
      super(var1, var2);
   }

   public boolean func_70648_aU() {
      return true;
   }

   public CreatureAttribute func_70668_bt() {
      return CreatureAttribute.field_203100_e;
   }

   public boolean func_205019_a(IWorldReaderBase var1) {
      return var1.func_195587_c(this, this.func_174813_aQ()) && var1.func_195586_b(this, this.func_174813_aQ());
   }

   public int func_70627_aG() {
      return 120;
   }

   public boolean func_70692_ba() {
      return true;
   }

   protected int func_70693_a(EntityPlayer var1) {
      return 1 + this.field_70170_p.field_73012_v.nextInt(3);
   }

   protected void func_209207_l(int var1) {
      if (this.func_70089_S() && !this.func_203005_aq()) {
         this.func_70050_g(var1 - 1);
         if (this.func_70086_ai() == -20) {
            this.func_70050_g(0);
            this.func_70097_a(DamageSource.field_76369_e, 2.0F);
         }
      } else {
         this.func_70050_g(300);
      }

   }

   public void func_70030_z() {
      int var1 = this.func_70086_ai();
      super.func_70030_z();
      this.func_209207_l(var1);
   }

   public boolean func_96092_aw() {
      return false;
   }

   public boolean func_184652_a(EntityPlayer var1) {
      return false;
   }
}
