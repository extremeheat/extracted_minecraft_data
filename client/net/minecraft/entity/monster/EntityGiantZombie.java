package net.minecraft.entity.monster;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

public class EntityGiantZombie extends EntityMob {
   public EntityGiantZombie(World var1) {
      super(var1);
      this.field_70129_M *= 6.0F;
      this.func_70105_a(this.field_70130_N * 6.0F, this.field_70131_O * 6.0F);
   }

   @Override
   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(100.0);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.5);
      this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(50.0);
   }

   @Override
   public float func_70783_a(int var1, int var2, int var3) {
      return this.field_70170_p.func_72801_o(var1, var2, var3) - 0.5F;
   }
}
