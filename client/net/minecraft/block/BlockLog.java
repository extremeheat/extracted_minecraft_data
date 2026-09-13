package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public abstract class BlockLog extends BlockRotatedPillar {
   protected IIcon[] field_150167_a;
   protected IIcon[] field_150166_b;

   public BlockLog() {
      super(Material.field_151575_d);
      this.func_149647_a(CreativeTabs.field_78030_b);
      this.func_149711_c(2.0F);
      this.func_149672_a(field_149766_f);
   }

   public static int func_150165_c(int var0) {
      return var0 & 3;
   }

   @Override
   public int func_149745_a(Random var1) {
      return 1;
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Item.func_150898_a(this);
   }

   @Override
   public void func_149749_a(World var1, int var2, int var3, int var4, Block var5, int var6) {
      byte var7 = 4;
      int var8 = var7 + 1;
      if (var1.func_72904_c(var2 - var8, var3 - var8, var4 - var8, var2 + var8, var3 + var8, var4 + var8)) {
         for(int var9 = -var7; var9 <= var7; ++var9) {
            for(int var10 = -var7; var10 <= var7; ++var10) {
               for(int var11 = -var7; var11 <= var7; ++var11) {
                  if (var1.func_147439_a(var2 + var9, var3 + var10, var4 + var11).func_149688_o() == Material.field_151584_j) {
                     int var12 = var1.func_72805_g(var2 + var9, var3 + var10, var4 + var11);
                     if ((var12 & 8) == 0) {
                        var1.func_72921_c(var2 + var9, var3 + var10, var4 + var11, var12 | 8, 4);
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   protected IIcon func_150163_b(int var1) {
      return this.field_150167_a[var1 % this.field_150167_a.length];
   }

   @Override
   protected IIcon func_150161_d(int var1) {
      return this.field_150166_b[var1 % this.field_150166_b.length];
   }
}
