package net.minecraft.world.gen.feature;

import com.google.common.base.Predicates;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockStateHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class WorldGenDesertWells extends WorldGenerator {
   private static final BlockStateHelper field_175913_a;
   private final IBlockState field_175911_b;
   private final IBlockState field_175912_c;
   private final IBlockState field_175910_d;

   public WorldGenDesertWells() {
      super();
      this.field_175911_b = Blocks.field_150333_U.func_176223_P().func_177226_a(BlockStoneSlab.field_176556_M, BlockStoneSlab.EnumType.SAND).func_177226_a(BlockSlab.field_176554_a, BlockSlab.EnumBlockHalf.BOTTOM);
      this.field_175912_c = Blocks.field_150322_A.func_176223_P();
      this.field_175910_d = Blocks.field_150358_i.func_176223_P();
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      while(var1.func_175623_d(var3) && var3.func_177956_o() > 2) {
         var3 = var3.func_177977_b();
      }

      if (!field_175913_a.apply(var1.func_180495_p(var3))) {
         return false;
      } else {
         int var4;
         int var5;
         for(var4 = -2; var4 <= 2; ++var4) {
            for(var5 = -2; var5 <= 2; ++var5) {
               if (var1.func_175623_d(var3.func_177982_a(var4, -1, var5)) && var1.func_175623_d(var3.func_177982_a(var4, -2, var5))) {
                  return false;
               }
            }
         }

         for(var4 = -1; var4 <= 0; ++var4) {
            for(var5 = -2; var5 <= 2; ++var5) {
               for(int var6 = -2; var6 <= 2; ++var6) {
                  var1.func_180501_a(var3.func_177982_a(var5, var4, var6), this.field_175912_c, 2);
               }
            }
         }

         var1.func_180501_a(var3, this.field_175910_d, 2);
         Iterator var7 = EnumFacing.Plane.HORIZONTAL.iterator();

         while(var7.hasNext()) {
            EnumFacing var8 = (EnumFacing)var7.next();
            var1.func_180501_a(var3.func_177972_a(var8), this.field_175910_d, 2);
         }

         for(var4 = -2; var4 <= 2; ++var4) {
            for(var5 = -2; var5 <= 2; ++var5) {
               if (var4 == -2 || var4 == 2 || var5 == -2 || var5 == 2) {
                  var1.func_180501_a(var3.func_177982_a(var4, 1, var5), this.field_175912_c, 2);
               }
            }
         }

         var1.func_180501_a(var3.func_177982_a(2, 1, 0), this.field_175911_b, 2);
         var1.func_180501_a(var3.func_177982_a(-2, 1, 0), this.field_175911_b, 2);
         var1.func_180501_a(var3.func_177982_a(0, 1, 2), this.field_175911_b, 2);
         var1.func_180501_a(var3.func_177982_a(0, 1, -2), this.field_175911_b, 2);

         for(var4 = -1; var4 <= 1; ++var4) {
            for(var5 = -1; var5 <= 1; ++var5) {
               if (var4 == 0 && var5 == 0) {
                  var1.func_180501_a(var3.func_177982_a(var4, 4, var5), this.field_175912_c, 2);
               } else {
                  var1.func_180501_a(var3.func_177982_a(var4, 4, var5), this.field_175911_b, 2);
               }
            }
         }

         for(var4 = 1; var4 <= 3; ++var4) {
            var1.func_180501_a(var3.func_177982_a(-1, var4, -1), this.field_175912_c, 2);
            var1.func_180501_a(var3.func_177982_a(-1, var4, 1), this.field_175912_c, 2);
            var1.func_180501_a(var3.func_177982_a(1, var4, -1), this.field_175912_c, 2);
            var1.func_180501_a(var3.func_177982_a(1, var4, 1), this.field_175912_c, 2);
         }

         return true;
      }
   }

   static {
      field_175913_a = BlockStateHelper.func_177638_a(Blocks.field_150354_m).func_177637_a(BlockSand.field_176504_a, Predicates.equalTo(BlockSand.EnumType.SAND));
   }
}
