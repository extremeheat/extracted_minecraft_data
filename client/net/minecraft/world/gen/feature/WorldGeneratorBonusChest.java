package net.minecraft.world.gen.feature;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class WorldGeneratorBonusChest extends WorldGenerator {
   private final List<WeightedRandomChestContent> field_175909_a;
   private final int field_76545_b;

   public WorldGeneratorBonusChest(List<WeightedRandomChestContent> var1, int var2) {
      super();
      this.field_175909_a = var1;
      this.field_76545_b = var2;
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      Block var4;
      while(((var4 = var1.func_180495_p(var3).func_177230_c()).func_149688_o() == Material.field_151579_a || var4.func_149688_o() == Material.field_151584_j) && var3.func_177956_o() > 1) {
         var3 = var3.func_177977_b();
      }

      if (var3.func_177956_o() < 1) {
         return false;
      } else {
         var3 = var3.func_177984_a();

         for(int var5 = 0; var5 < 4; ++var5) {
            BlockPos var6 = var3.func_177982_a(var2.nextInt(4) - var2.nextInt(4), var2.nextInt(3) - var2.nextInt(3), var2.nextInt(4) - var2.nextInt(4));
            if (var1.func_175623_d(var6) && World.func_175683_a(var1, var6.func_177977_b())) {
               var1.func_180501_a(var6, Blocks.field_150486_ae.func_176223_P(), 2);
               TileEntity var7 = var1.func_175625_s(var6);
               if (var7 instanceof TileEntityChest) {
                  WeightedRandomChestContent.func_177630_a(var2, this.field_175909_a, (TileEntityChest)var7, this.field_76545_b);
               }

               BlockPos var8 = var6.func_177974_f();
               BlockPos var9 = var6.func_177976_e();
               BlockPos var10 = var6.func_177978_c();
               BlockPos var11 = var6.func_177968_d();
               if (var1.func_175623_d(var9) && World.func_175683_a(var1, var9.func_177977_b())) {
                  var1.func_180501_a(var9, Blocks.field_150478_aa.func_176223_P(), 2);
               }

               if (var1.func_175623_d(var8) && World.func_175683_a(var1, var8.func_177977_b())) {
                  var1.func_180501_a(var8, Blocks.field_150478_aa.func_176223_P(), 2);
               }

               if (var1.func_175623_d(var10) && World.func_175683_a(var1, var10.func_177977_b())) {
                  var1.func_180501_a(var10, Blocks.field_150478_aa.func_176223_P(), 2);
               }

               if (var1.func_175623_d(var11) && World.func_175683_a(var1, var11.func_177977_b())) {
                  var1.func_180501_a(var11, Blocks.field_150478_aa.func_176223_P(), 2);
               }

               return true;
            }
         }

         return false;
      }
   }
}
