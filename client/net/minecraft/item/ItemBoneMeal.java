package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCoralWallFanDead;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class ItemBoneMeal extends ItemDye {
   public ItemBoneMeal(EnumDyeColor var1, Item.Properties var2) {
      super(var1, var2);
   }

   public EnumActionResult func_195939_a(ItemUseContext var1) {
      World var2 = var1.func_195991_k();
      BlockPos var3 = var1.func_195995_a();
      BlockPos var4 = var3.func_177972_a(var1.func_196000_l());
      if (func_195966_a(var1.func_195996_i(), var2, var3)) {
         if (!var2.field_72995_K) {
            var2.func_175718_b(2005, var3, 0);
         }

         return EnumActionResult.SUCCESS;
      } else {
         IBlockState var5 = var2.func_180495_p(var3);
         boolean var6 = var5.func_193401_d(var2, var3, var1.func_196000_l()) == BlockFaceShape.SOLID;
         if (var6 && func_203173_b(var1.func_195996_i(), var2, var4, var1.func_196000_l())) {
            if (!var2.field_72995_K) {
               var2.func_175718_b(2005, var4, 0);
            }

            return EnumActionResult.SUCCESS;
         } else {
            return EnumActionResult.PASS;
         }
      }
   }

   public static boolean func_195966_a(ItemStack var0, World var1, BlockPos var2) {
      IBlockState var3 = var1.func_180495_p(var2);
      if (var3.func_177230_c() instanceof IGrowable) {
         IGrowable var4 = (IGrowable)var3.func_177230_c();
         if (var4.func_176473_a(var1, var2, var3, var1.field_72995_K)) {
            if (!var1.field_72995_K) {
               if (var4.func_180670_a(var1, var1.field_73012_v, var2, var3)) {
                  var4.func_176474_b(var1, var1.field_73012_v, var2, var3);
               }

               var0.func_190918_g(1);
            }

            return true;
         }
      }

      return false;
   }

   public static boolean func_203173_b(ItemStack var0, World var1, BlockPos var2, @Nullable EnumFacing var3) {
      if (var1.func_180495_p(var2).func_177230_c() == Blocks.field_150355_j && var1.func_204610_c(var2).func_206882_g() == 8) {
         if (!var1.field_72995_K) {
            label79:
            for(int var4 = 0; var4 < 128; ++var4) {
               BlockPos var5 = var2;
               Biome var6 = var1.func_180494_b(var2);
               IBlockState var7 = Blocks.field_203198_aQ.func_176223_P();

               int var8;
               for(var8 = 0; var8 < var4 / 16; ++var8) {
                  var5 = var5.func_177982_a(field_77697_d.nextInt(3) - 1, (field_77697_d.nextInt(3) - 1) * field_77697_d.nextInt(3) / 2, field_77697_d.nextInt(3) - 1);
                  var6 = var1.func_180494_b(var5);
                  if (var1.func_180495_p(var5).func_185898_k()) {
                     continue label79;
                  }
               }

               if (var6 == Biomes.field_203614_T || var6 == Biomes.field_203617_W) {
                  if (var4 == 0 && var3 != null && var3.func_176740_k().func_176722_c()) {
                     var7 = (IBlockState)((Block)BlockTags.field_211922_B.func_205596_a(var1.field_73012_v)).func_176223_P().func_206870_a(BlockCoralWallFanDead.field_211884_b, var3);
                  } else if (field_77697_d.nextInt(4) == 0) {
                     var7 = ((Block)BlockTags.field_212741_H.func_205596_a(field_77697_d)).func_176223_P();
                  }
               }

               if (var7.func_177230_c().func_203417_a(BlockTags.field_211922_B)) {
                  for(var8 = 0; !var7.func_196955_c(var1, var5) && var8 < 4; ++var8) {
                     var7 = (IBlockState)var7.func_206870_a(BlockCoralWallFanDead.field_211884_b, EnumFacing.Plane.HORIZONTAL.func_179518_a(field_77697_d));
                  }
               }

               if (var7.func_196955_c(var1, var5)) {
                  IBlockState var9 = var1.func_180495_p(var5);
                  if (var9.func_177230_c() == Blocks.field_150355_j && var1.func_204610_c(var5).func_206882_g() == 8) {
                     var1.func_180501_a(var5, var7, 3);
                  } else if (var9.func_177230_c() == Blocks.field_203198_aQ && field_77697_d.nextInt(10) == 0) {
                     ((IGrowable)Blocks.field_203198_aQ).func_176474_b(var1, field_77697_d, var5, var9);
                  }
               }
            }

            var0.func_190918_g(1);
         }

         return true;
      } else {
         return false;
      }
   }

   public static void func_195965_a(IWorld var0, BlockPos var1, int var2) {
      if (var2 == 0) {
         var2 = 15;
      }

      IBlockState var3 = var0.func_180495_p(var1);
      if (!var3.func_196958_f()) {
         for(int var4 = 0; var4 < var2; ++var4) {
            double var5 = field_77697_d.nextGaussian() * 0.02D;
            double var7 = field_77697_d.nextGaussian() * 0.02D;
            double var9 = field_77697_d.nextGaussian() * 0.02D;
            var0.func_195594_a(Particles.field_197632_y, (double)((float)var1.func_177958_n() + field_77697_d.nextFloat()), (double)var1.func_177956_o() + (double)field_77697_d.nextFloat() * var3.func_196954_c(var0, var1).func_197758_c(EnumFacing.Axis.Y), (double)((float)var1.func_177952_p() + field_77697_d.nextFloat()), var5, var7, var9);
         }

      }
   }
}
