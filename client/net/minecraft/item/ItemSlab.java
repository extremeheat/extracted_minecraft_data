package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemSlab extends ItemBlock {
   private final BlockSlab field_150949_c;
   private final BlockSlab field_179226_c;

   public ItemSlab(Block var1, BlockSlab var2, BlockSlab var3) {
      super(var1);
      this.field_150949_c = var2;
      this.field_179226_c = var3;
      this.func_77656_e(0);
      this.func_77627_a(true);
   }

   public int func_77647_b(int var1) {
      return var1;
   }

   public String func_77667_c(ItemStack var1) {
      return this.field_150949_c.func_150002_b(var1.func_77960_j());
   }

   public boolean func_180614_a(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.field_77994_a == 0) {
         return false;
      } else if (!var2.func_175151_a(var4.func_177972_a(var5), var5, var1)) {
         return false;
      } else {
         Object var9 = this.field_150949_c.func_176553_a(var1);
         IBlockState var10 = var3.func_180495_p(var4);
         if (var10.func_177230_c() == this.field_150949_c) {
            IProperty var11 = this.field_150949_c.func_176551_l();
            Comparable var12 = var10.func_177229_b(var11);
            BlockSlab.EnumBlockHalf var13 = (BlockSlab.EnumBlockHalf)var10.func_177229_b(BlockSlab.field_176554_a);
            if ((var5 == EnumFacing.UP && var13 == BlockSlab.EnumBlockHalf.BOTTOM || var5 == EnumFacing.DOWN && var13 == BlockSlab.EnumBlockHalf.TOP) && var12 == var9) {
               IBlockState var14 = this.field_179226_c.func_176223_P().func_177226_a(var11, var12);
               if (var3.func_72855_b(this.field_179226_c.func_180640_a(var3, var4, var14)) && var3.func_180501_a(var4, var14, 3)) {
                  var3.func_72908_a((double)((float)var4.func_177958_n() + 0.5F), (double)((float)var4.func_177956_o() + 0.5F), (double)((float)var4.func_177952_p() + 0.5F), this.field_179226_c.field_149762_H.func_150496_b(), (this.field_179226_c.field_149762_H.func_150497_c() + 1.0F) / 2.0F, this.field_179226_c.field_149762_H.func_150494_d() * 0.8F);
                  --var1.field_77994_a;
               }

               return true;
            }
         }

         return this.func_180615_a(var1, var3, var4.func_177972_a(var5), var9) ? true : super.func_180614_a(var1, var2, var3, var4, var5, var6, var7, var8);
      }
   }

   public boolean func_179222_a(World var1, BlockPos var2, EnumFacing var3, EntityPlayer var4, ItemStack var5) {
      BlockPos var6 = var2;
      IProperty var7 = this.field_150949_c.func_176551_l();
      Object var8 = this.field_150949_c.func_176553_a(var5);
      IBlockState var9 = var1.func_180495_p(var2);
      if (var9.func_177230_c() == this.field_150949_c) {
         boolean var10 = var9.func_177229_b(BlockSlab.field_176554_a) == BlockSlab.EnumBlockHalf.TOP;
         if ((var3 == EnumFacing.UP && !var10 || var3 == EnumFacing.DOWN && var10) && var8 == var9.func_177229_b(var7)) {
            return true;
         }
      }

      var2 = var2.func_177972_a(var3);
      IBlockState var11 = var1.func_180495_p(var2);
      return var11.func_177230_c() == this.field_150949_c && var8 == var11.func_177229_b(var7) ? true : super.func_179222_a(var1, var6, var3, var4, var5);
   }

   private boolean func_180615_a(ItemStack var1, World var2, BlockPos var3, Object var4) {
      IBlockState var5 = var2.func_180495_p(var3);
      if (var5.func_177230_c() == this.field_150949_c) {
         Comparable var6 = var5.func_177229_b(this.field_150949_c.func_176551_l());
         if (var6 == var4) {
            IBlockState var7 = this.field_179226_c.func_176223_P().func_177226_a(this.field_150949_c.func_176551_l(), var6);
            if (var2.func_72855_b(this.field_179226_c.func_180640_a(var2, var3, var7)) && var2.func_180501_a(var3, var7, 3)) {
               var2.func_72908_a((double)((float)var3.func_177958_n() + 0.5F), (double)((float)var3.func_177956_o() + 0.5F), (double)((float)var3.func_177952_p() + 0.5F), this.field_179226_c.field_149762_H.func_150496_b(), (this.field_179226_c.field_149762_H.func_150497_c() + 1.0F) / 2.0F, this.field_179226_c.field_149762_H.func_150494_d() * 0.8F);
               --var1.field_77994_a;
            }

            return true;
         }
      }

      return false;
   }
}
