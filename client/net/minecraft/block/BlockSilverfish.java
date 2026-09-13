package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class BlockSilverfish extends Block {
   public static final String[] field_150198_a = new String[]{"stone", "cobble", "brick", "mossybrick", "crackedbrick", "chiseledbrick"};

   public BlockSilverfish() {
      super(Material.field_151571_B);
      this.func_149711_c(0.0F);
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      switch(var2) {
         case 1:
            return Blocks.field_150347_e.func_149733_h(var1);
         case 2:
            return Blocks.field_150417_aV.func_149733_h(var1);
         case 3:
            return Blocks.field_150417_aV.func_149691_a(var1, 1);
         case 4:
            return Blocks.field_150417_aV.func_149691_a(var1, 2);
         case 5:
            return Blocks.field_150417_aV.func_149691_a(var1, 3);
         default:
            return Blocks.field_150348_b.func_149733_h(var1);
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
   }

   @Override
   public void func_149664_b(World var1, int var2, int var3, int var4, int var5) {
      if (!var1.field_72995_K) {
         EntitySilverfish var6 = new EntitySilverfish(var1);
         var6.func_70012_b((double)var2 + 0.5, (double)var3, (double)var4 + 0.5, 0.0F, 0.0F);
         var1.func_72838_d(var6);
         var6.func_70656_aK();
      }

      super.func_149664_b(var1, var2, var3, var4, var5);
   }

   @Override
   public int func_149745_a(Random var1) {
      return 0;
   }

   public static boolean func_150196_a(Block var0) {
      return var0 == Blocks.field_150348_b || var0 == Blocks.field_150347_e || var0 == Blocks.field_150417_aV;
   }

   public static int func_150195_a(Block var0, int var1) {
      if (var1 == 0) {
         if (var0 == Blocks.field_150347_e) {
            return 1;
         }

         if (var0 == Blocks.field_150417_aV) {
            return 2;
         }
      } else if (var0 == Blocks.field_150417_aV) {
         switch(var1) {
            case 1:
               return 3;
            case 2:
               return 4;
            case 3:
               return 5;
         }
      }

      return 0;
   }

   public static ImmutablePair func_150197_b(int var0) {
      switch(var0) {
         case 1:
            return new ImmutablePair(Blocks.field_150347_e, 0);
         case 2:
            return new ImmutablePair(Blocks.field_150417_aV, 0);
         case 3:
            return new ImmutablePair(Blocks.field_150417_aV, 1);
         case 4:
            return new ImmutablePair(Blocks.field_150417_aV, 2);
         case 5:
            return new ImmutablePair(Blocks.field_150417_aV, 3);
         default:
            return new ImmutablePair(Blocks.field_150348_b, 0);
      }
   }

   @Override
   protected ItemStack func_149644_j(int var1) {
      switch(var1) {
         case 1:
            return new ItemStack(Blocks.field_150347_e);
         case 2:
            return new ItemStack(Blocks.field_150417_aV);
         case 3:
            return new ItemStack(Blocks.field_150417_aV, 1, 1);
         case 4:
            return new ItemStack(Blocks.field_150417_aV, 1, 2);
         case 5:
            return new ItemStack(Blocks.field_150417_aV, 1, 3);
         default:
            return new ItemStack(Blocks.field_150348_b);
      }
   }

   @Override
   public void func_149690_a(World var1, int var2, int var3, int var4, int var5, float var6, int var7) {
      if (!var1.field_72995_K) {
         EntitySilverfish var8 = new EntitySilverfish(var1);
         var8.func_70012_b((double)var2 + 0.5, (double)var3, (double)var4 + 0.5, 0.0F, 0.0F);
         var1.func_72838_d(var8);
         var8.func_70656_aK();
      }
   }

   @Override
   public int func_149643_k(World var1, int var2, int var3, int var4) {
      return var1.func_72805_g(var2, var3, var4);
   }

   @Override
   public void func_149666_a(Item var1, CreativeTabs var2, List var3) {
      for(int var4 = 0; var4 < field_150198_a.length; ++var4) {
         var3.add(new ItemStack(var1, 1, var4));
      }
   }
}
