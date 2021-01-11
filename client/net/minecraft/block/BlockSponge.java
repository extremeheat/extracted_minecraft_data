package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;

public class BlockSponge extends Block {
   public static final PropertyBool field_176313_a = PropertyBool.func_177716_a("wet");

   protected BlockSponge() {
      super(Material.field_151583_m);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176313_a, false));
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public String func_149732_F() {
      return StatCollector.func_74838_a(this.func_149739_a() + ".dry.name");
   }

   public int func_180651_a(IBlockState var1) {
      return (Boolean)var1.func_177229_b(field_176313_a) ? 1 : 0;
   }

   public void func_176213_c(World var1, BlockPos var2, IBlockState var3) {
      this.func_176311_e(var1, var2, var3);
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      this.func_176311_e(var1, var2, var3);
      super.func_176204_a(var1, var2, var3, var4);
   }

   protected void func_176311_e(World var1, BlockPos var2, IBlockState var3) {
      if (!(Boolean)var3.func_177229_b(field_176313_a) && this.func_176312_d(var1, var2)) {
         var1.func_180501_a(var2, var3.func_177226_a(field_176313_a, true), 2);
         var1.func_175718_b(2001, var2, Block.func_149682_b(Blocks.field_150355_j));
      }

   }

   private boolean func_176312_d(World var1, BlockPos var2) {
      LinkedList var3 = Lists.newLinkedList();
      ArrayList var4 = Lists.newArrayList();
      var3.add(new Tuple(var2, 0));
      int var5 = 0;

      BlockPos var7;
      while(!var3.isEmpty()) {
         Tuple var6 = (Tuple)var3.poll();
         var7 = (BlockPos)var6.func_76341_a();
         int var8 = (Integer)var6.func_76340_b();
         EnumFacing[] var9 = EnumFacing.values();
         int var10 = var9.length;

         for(int var11 = 0; var11 < var10; ++var11) {
            EnumFacing var12 = var9[var11];
            BlockPos var13 = var7.func_177972_a(var12);
            if (var1.func_180495_p(var13).func_177230_c().func_149688_o() == Material.field_151586_h) {
               var1.func_180501_a(var13, Blocks.field_150350_a.func_176223_P(), 2);
               var4.add(var13);
               ++var5;
               if (var8 < 6) {
                  var3.add(new Tuple(var13, var8 + 1));
               }
            }
         }

         if (var5 > 64) {
            break;
         }
      }

      Iterator var14 = var4.iterator();

      while(var14.hasNext()) {
         var7 = (BlockPos)var14.next();
         var1.func_175685_c(var7, Blocks.field_150350_a);
      }

      return var5 > 0;
   }

   public void func_149666_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      var3.add(new ItemStack(var1, 1, 0));
      var3.add(new ItemStack(var1, 1, 1));
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176313_a, (var1 & 1) == 1);
   }

   public int func_176201_c(IBlockState var1) {
      return (Boolean)var1.func_177229_b(field_176313_a) ? 1 : 0;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176313_a});
   }

   public void func_180655_c(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if ((Boolean)var3.func_177229_b(field_176313_a)) {
         EnumFacing var5 = EnumFacing.func_176741_a(var4);
         if (var5 != EnumFacing.UP && !World.func_175683_a(var1, var2.func_177972_a(var5))) {
            double var6 = (double)var2.func_177958_n();
            double var8 = (double)var2.func_177956_o();
            double var10 = (double)var2.func_177952_p();
            if (var5 == EnumFacing.DOWN) {
               var8 -= 0.05D;
               var6 += var4.nextDouble();
               var10 += var4.nextDouble();
            } else {
               var8 += var4.nextDouble() * 0.8D;
               if (var5.func_176740_k() == EnumFacing.Axis.X) {
                  var10 += var4.nextDouble();
                  if (var5 == EnumFacing.EAST) {
                     ++var6;
                  } else {
                     var6 += 0.05D;
                  }
               } else {
                  var6 += var4.nextDouble();
                  if (var5 == EnumFacing.SOUTH) {
                     ++var10;
                  } else {
                     var10 += 0.05D;
                  }
               }
            }

            var1.func_175688_a(EnumParticleTypes.DRIP_WATER, var6, var8, var10, 0.0D, 0.0D, 0.0D);
         }
      }
   }
}
