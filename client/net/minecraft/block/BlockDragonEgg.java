package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDragonEgg extends Block {
   public BlockDragonEgg() {
      super(Material.field_151566_D, MapColor.field_151646_E);
      this.func_149676_a(0.0625F, 0.0F, 0.0625F, 0.9375F, 1.0F, 0.9375F);
   }

   public void func_176213_c(World var1, BlockPos var2, IBlockState var3) {
      var1.func_175684_a(var2, this, this.func_149738_a(var1));
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      var1.func_175684_a(var2, this, this.func_149738_a(var1));
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      this.func_180683_d(var1, var2);
   }

   private void func_180683_d(World var1, BlockPos var2) {
      if (BlockFalling.func_180685_d(var1, var2.func_177977_b()) && var2.func_177956_o() >= 0) {
         byte var3 = 32;
         if (!BlockFalling.field_149832_M && var1.func_175707_a(var2.func_177982_a(-var3, -var3, -var3), var2.func_177982_a(var3, var3, var3))) {
            var1.func_72838_d(new EntityFallingBlock(var1, (double)((float)var2.func_177958_n() + 0.5F), (double)var2.func_177956_o(), (double)((float)var2.func_177952_p() + 0.5F), this.func_176223_P()));
         } else {
            var1.func_175698_g(var2);

            BlockPos var4;
            for(var4 = var2; BlockFalling.func_180685_d(var1, var4) && var4.func_177956_o() > 0; var4 = var4.func_177977_b()) {
            }

            if (var4.func_177956_o() > 0) {
               var1.func_180501_a(var4, this.func_176223_P(), 2);
            }
         }

      }
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      this.func_180684_e(var1, var2);
      return true;
   }

   public void func_180649_a(World var1, BlockPos var2, EntityPlayer var3) {
      this.func_180684_e(var1, var2);
   }

   private void func_180684_e(World var1, BlockPos var2) {
      IBlockState var3 = var1.func_180495_p(var2);
      if (var3.func_177230_c() == this) {
         for(int var4 = 0; var4 < 1000; ++var4) {
            BlockPos var5 = var2.func_177982_a(var1.field_73012_v.nextInt(16) - var1.field_73012_v.nextInt(16), var1.field_73012_v.nextInt(8) - var1.field_73012_v.nextInt(8), var1.field_73012_v.nextInt(16) - var1.field_73012_v.nextInt(16));
            if (var1.func_180495_p(var5).func_177230_c().field_149764_J == Material.field_151579_a) {
               if (var1.field_72995_K) {
                  for(int var6 = 0; var6 < 128; ++var6) {
                     double var7 = var1.field_73012_v.nextDouble();
                     float var9 = (var1.field_73012_v.nextFloat() - 0.5F) * 0.2F;
                     float var10 = (var1.field_73012_v.nextFloat() - 0.5F) * 0.2F;
                     float var11 = (var1.field_73012_v.nextFloat() - 0.5F) * 0.2F;
                     double var12 = (double)var5.func_177958_n() + (double)(var2.func_177958_n() - var5.func_177958_n()) * var7 + (var1.field_73012_v.nextDouble() - 0.5D) * 1.0D + 0.5D;
                     double var14 = (double)var5.func_177956_o() + (double)(var2.func_177956_o() - var5.func_177956_o()) * var7 + var1.field_73012_v.nextDouble() * 1.0D - 0.5D;
                     double var16 = (double)var5.func_177952_p() + (double)(var2.func_177952_p() - var5.func_177952_p()) * var7 + (var1.field_73012_v.nextDouble() - 0.5D) * 1.0D + 0.5D;
                     var1.func_175688_a(EnumParticleTypes.PORTAL, var12, var14, var16, (double)var9, (double)var10, (double)var11);
                  }
               } else {
                  var1.func_180501_a(var5, var3, 2);
                  var1.func_175698_g(var2);
               }

               return;
            }
         }

      }
   }

   public int func_149738_a(World var1) {
      return 5;
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_149686_d() {
      return false;
   }

   public boolean func_176225_a(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return true;
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return null;
   }
}
