package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockRedstoneOre extends Block {
   private final boolean field_150187_a;

   public BlockRedstoneOre(boolean var1) {
      super(Material.field_151576_e);
      if (var1) {
         this.func_149675_a(true);
      }

      this.field_150187_a = var1;
   }

   public int func_149738_a(World var1) {
      return 30;
   }

   public void func_180649_a(World var1, BlockPos var2, EntityPlayer var3) {
      this.func_176352_d(var1, var2);
      super.func_180649_a(var1, var2, var3);
   }

   public void func_176199_a(World var1, BlockPos var2, Entity var3) {
      this.func_176352_d(var1, var2);
      super.func_176199_a(var1, var2, var3);
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      this.func_176352_d(var1, var2);
      return super.func_180639_a(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   private void func_176352_d(World var1, BlockPos var2) {
      this.func_180691_e(var1, var2);
      if (this == Blocks.field_150450_ax) {
         var1.func_175656_a(var2, Blocks.field_150439_ay.func_176223_P());
      }

   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (this == Blocks.field_150439_ay) {
         var1.func_175656_a(var2, Blocks.field_150450_ax.func_176223_P());
      }

   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Items.field_151137_ax;
   }

   public int func_149679_a(int var1, Random var2) {
      return this.func_149745_a(var2) + var2.nextInt(var1 + 1);
   }

   public int func_149745_a(Random var1) {
      return 4 + var1.nextInt(2);
   }

   public void func_180653_a(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      super.func_180653_a(var1, var2, var3, var4, var5);
      if (this.func_180660_a(var3, var1.field_73012_v, var5) != Item.func_150898_a(this)) {
         int var6 = 1 + var1.field_73012_v.nextInt(5);
         this.func_180637_b(var1, var2, var6);
      }

   }

   public void func_180655_c(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (this.field_150187_a) {
         this.func_180691_e(var1, var2);
      }

   }

   private void func_180691_e(World var1, BlockPos var2) {
      Random var3 = var1.field_73012_v;
      double var4 = 0.0625D;

      for(int var6 = 0; var6 < 6; ++var6) {
         double var7 = (double)((float)var2.func_177958_n() + var3.nextFloat());
         double var9 = (double)((float)var2.func_177956_o() + var3.nextFloat());
         double var11 = (double)((float)var2.func_177952_p() + var3.nextFloat());
         if (var6 == 0 && !var1.func_180495_p(var2.func_177984_a()).func_177230_c().func_149662_c()) {
            var9 = (double)var2.func_177956_o() + var4 + 1.0D;
         }

         if (var6 == 1 && !var1.func_180495_p(var2.func_177977_b()).func_177230_c().func_149662_c()) {
            var9 = (double)var2.func_177956_o() - var4;
         }

         if (var6 == 2 && !var1.func_180495_p(var2.func_177968_d()).func_177230_c().func_149662_c()) {
            var11 = (double)var2.func_177952_p() + var4 + 1.0D;
         }

         if (var6 == 3 && !var1.func_180495_p(var2.func_177978_c()).func_177230_c().func_149662_c()) {
            var11 = (double)var2.func_177952_p() - var4;
         }

         if (var6 == 4 && !var1.func_180495_p(var2.func_177974_f()).func_177230_c().func_149662_c()) {
            var7 = (double)var2.func_177958_n() + var4 + 1.0D;
         }

         if (var6 == 5 && !var1.func_180495_p(var2.func_177976_e()).func_177230_c().func_149662_c()) {
            var7 = (double)var2.func_177958_n() - var4;
         }

         if (var7 < (double)var2.func_177958_n() || var7 > (double)(var2.func_177958_n() + 1) || var9 < 0.0D || var9 > (double)(var2.func_177956_o() + 1) || var11 < (double)var2.func_177952_p() || var11 > (double)(var2.func_177952_p() + 1)) {
            var1.func_175688_a(EnumParticleTypes.REDSTONE, var7, var9, var11, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   protected ItemStack func_180643_i(IBlockState var1) {
      return new ItemStack(Blocks.field_150450_ax);
   }
}
