package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockPressurePlateWeighted extends BlockBasePressurePlate {
   private final int field_150068_a;

   protected BlockPressurePlateWeighted(String var1, Material var2, int var3) {
      super(var1, var2);
      this.field_150068_a = var3;
   }

   @Override
   protected int func_150065_e(World var1, int var2, int var3, int var4) {
      int var5 = Math.min(var1.func_72872_a(Entity.class, this.func_150061_a(var2, var3, var4)).size(), this.field_150068_a);
      if (var5 <= 0) {
         return 0;
      } else {
         float var6 = (float)Math.min(this.field_150068_a, var5) / (float)this.field_150068_a;
         return MathHelper.func_76123_f(var6 * 15.0F);
      }
   }

   @Override
   protected int func_150060_c(int var1) {
      return var1;
   }

   @Override
   protected int func_150066_d(int var1) {
      return var1;
   }

   @Override
   public int func_149738_a(World var1) {
      return 10;
   }
}
