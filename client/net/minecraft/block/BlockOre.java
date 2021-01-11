package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockOre extends Block {
   public BlockOre() {
      this(Material.field_151576_e.func_151565_r());
   }

   public BlockOre(MapColor var1) {
      super(Material.field_151576_e, var1);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
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

   public int func_149745_a(Random var1) {
      return this == Blocks.field_150369_x ? 4 + var1.nextInt(5) : 1;
   }

   public int func_149679_a(int var1, Random var2) {
      if (var1 > 0 && Item.func_150898_a(this) != this.func_180660_a((IBlockState)this.func_176194_O().func_177619_a().iterator().next(), var2, var1)) {
         int var3 = var2.nextInt(var1 + 2) - 1;
         if (var3 < 0) {
            var3 = 0;
         }

         return this.func_149745_a(var2) * (var3 + 1);
      } else {
         return this.func_149745_a(var2);
      }
   }

   public void func_180653_a(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      super.func_180653_a(var1, var2, var3, var4, var5);
      if (this.func_180660_a(var3, var1.field_73012_v, var5) != Item.func_150898_a(this)) {
         int var6 = 0;
         if (this == Blocks.field_150365_q) {
            var6 = MathHelper.func_76136_a(var1.field_73012_v, 0, 2);
         } else if (this == Blocks.field_150482_ag) {
            var6 = MathHelper.func_76136_a(var1.field_73012_v, 3, 7);
         } else if (this == Blocks.field_150412_bA) {
            var6 = MathHelper.func_76136_a(var1.field_73012_v, 3, 7);
         } else if (this == Blocks.field_150369_x) {
            var6 = MathHelper.func_76136_a(var1.field_73012_v, 2, 5);
         } else if (this == Blocks.field_150449_bY) {
            var6 = MathHelper.func_76136_a(var1.field_73012_v, 2, 5);
         }

         this.func_180637_b(var1, var2, var6);
      }

   }

   public int func_176222_j(World var1, BlockPos var2) {
      return 0;
   }

   public int func_180651_a(IBlockState var1) {
      return this == Blocks.field_150369_x ? EnumDyeColor.BLUE.func_176767_b() : 0;
   }
}
