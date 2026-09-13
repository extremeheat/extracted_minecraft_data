package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockStoneSlab extends BlockSlab {
   public static final String[] field_150006_b = new String[]{"stone", "sand", "wood", "cobble", "brick", "smoothStoneBrick", "netherBrick", "quartz"};
   private IIcon field_150007_M;

   public BlockStoneSlab(boolean var1) {
      super(var1, Material.field_151576_e);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      int var3 = var2 & 7;
      if (this.field_150004_a && (var2 & 8) != 0) {
         var1 = 1;
      }

      if (var3 == 0) {
         return var1 != 1 && var1 != 0 ? this.field_150007_M : this.field_149761_L;
      } else if (var3 == 1) {
         return Blocks.field_150322_A.func_149733_h(var1);
      } else if (var3 == 2) {
         return Blocks.field_150344_f.func_149733_h(var1);
      } else if (var3 == 3) {
         return Blocks.field_150347_e.func_149733_h(var1);
      } else if (var3 == 4) {
         return Blocks.field_150336_V.func_149733_h(var1);
      } else if (var3 == 5) {
         return Blocks.field_150417_aV.func_149691_a(var1, 0);
      } else if (var3 == 6) {
         return Blocks.field_150385_bj.func_149733_h(1);
      } else {
         return var3 == 7 ? Blocks.field_150371_ca.func_149733_h(var1) : this.field_149761_L;
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a("stone_slab_top");
      this.field_150007_M = var1.func_94245_a("stone_slab_side");
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Item.func_150898_a(Blocks.field_150333_U);
   }

   @Override
   protected ItemStack func_149644_j(int var1) {
      return new ItemStack(Item.func_150898_a(Blocks.field_150333_U), 2, var1 & 7);
   }

   @Override
   public String func_150002_b(int var1) {
      if (var1 < 0 || var1 >= field_150006_b.length) {
         var1 = 0;
      }

      return super.func_149739_a() + "." + field_150006_b[var1];
   }

   @Override
   public void func_149666_a(Item var1, CreativeTabs var2, List var3) {
      if (var1 != Item.func_150898_a(Blocks.field_150334_T)) {
         for(int var4 = 0; var4 <= 7; ++var4) {
            if (var4 != 2) {
               var3.add(new ItemStack(var1, 1, var4));
            }
         }
      }
   }
}
