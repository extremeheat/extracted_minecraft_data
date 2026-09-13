package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockOre extends Block {
   public BlockOre() {
      super(Material.field_151576_e);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      if (this == Blocks.field_150365_q) {
         return Items.field_151044_h;
      } else if (this == Blocks.field_150482_ag) {
         return Items.field_151045_i;
      } else if (this == Blocks.field_150369_x) {
         return Items.field_151100_aR;
      } else if (this == Blocks.field_150412_bA) {
         return Items.field_151166_bC;
      } else {
         return this == Blocks.field_150449_bY ? Items.field_151128_bU : Item.func_150898_a(this);
      }
   }

   @Override
   public int func_149745_a(Random var1) {
      return this == Blocks.field_150369_x ? 4 + var1.nextInt(5) : 1;
   }

   @Override
   public int func_149679_a(int var1, Random var2) {
      if (var1 > 0 && Item.func_150898_a(this) != this.func_149650_a(0, var2, var1)) {
         int var3 = var2.nextInt(var1 + 2) - 1;
         if (var3 < 0) {
            var3 = 0;
         }

         return this.func_149745_a(var2) * (var3 + 1);
      } else {
         return this.func_149745_a(var2);
      }
   }

   @Override
   public void func_149690_a(World var1, int var2, int var3, int var4, int var5, float var6, int var7) {
      super.func_149690_a(var1, var2, var3, var4, var5, var6, var7);
      if (this.func_149650_a(var5, var1.field_73012_v, var7) != Item.func_150898_a(this)) {
         int var8 = 0;
         if (this == Blocks.field_150365_q) {
            var8 = MathHelper.func_76136_a(var1.field_73012_v, 0, 2);
         } else if (this == Blocks.field_150482_ag) {
            var8 = MathHelper.func_76136_a(var1.field_73012_v, 3, 7);
         } else if (this == Blocks.field_150412_bA) {
            var8 = MathHelper.func_76136_a(var1.field_73012_v, 3, 7);
         } else if (this == Blocks.field_150369_x) {
            var8 = MathHelper.func_76136_a(var1.field_73012_v, 2, 5);
         } else if (this == Blocks.field_150449_bY) {
            var8 = MathHelper.func_76136_a(var1.field_73012_v, 2, 5);
         }

         this.func_149657_c(var1, var2, var3, var4, var8);
      }
   }

   @Override
   public int func_149692_a(int var1) {
      return this == Blocks.field_150369_x ? 4 : 0;
   }
}
