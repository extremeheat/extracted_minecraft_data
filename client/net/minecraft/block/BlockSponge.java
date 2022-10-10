package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.LinkedList;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSponge extends Block {
   protected BlockSponge(Block.Properties var1) {
      super(var1);
   }

   public void func_196259_b(IBlockState var1, World var2, BlockPos var3, IBlockState var4) {
      if (var4.func_177230_c() != var1.func_177230_c()) {
         this.func_196510_a(var2, var3);
      }
   }

   public void func_189540_a(IBlockState var1, World var2, BlockPos var3, Block var4, BlockPos var5) {
      this.func_196510_a(var2, var3);
      super.func_189540_a(var1, var2, var3, var4, var5);
   }

   protected void func_196510_a(World var1, BlockPos var2) {
      if (this.func_176312_d(var1, var2)) {
         var1.func_180501_a(var2, Blocks.field_196577_ad.func_176223_P(), 2);
         var1.func_175718_b(2001, var2, Block.func_196246_j(Blocks.field_150355_j.func_176223_P()));
      }

   }

   private boolean func_176312_d(World var1, BlockPos var2) {
      LinkedList var3 = Lists.newLinkedList();
      var3.add(new Tuple(var2, 0));
      int var4 = 0;

      while(!var3.isEmpty()) {
         Tuple var5 = (Tuple)var3.poll();
         BlockPos var6 = (BlockPos)var5.func_76341_a();
         int var7 = (Integer)var5.func_76340_b();
         EnumFacing[] var8 = EnumFacing.values();
         int var9 = var8.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            EnumFacing var11 = var8[var10];
            BlockPos var12 = var6.func_177972_a(var11);
            IBlockState var13 = var1.func_180495_p(var12);
            IFluidState var14 = var1.func_204610_c(var12);
            Material var15 = var13.func_185904_a();
            if (var14.func_206884_a(FluidTags.field_206959_a)) {
               if (var13.func_177230_c() instanceof IBucketPickupHandler && ((IBucketPickupHandler)var13.func_177230_c()).func_204508_a(var1, var12, var13) != Fluids.field_204541_a) {
                  ++var4;
                  if (var7 < 6) {
                     var3.add(new Tuple(var12, var7 + 1));
                  }
               } else if (var13.func_177230_c() instanceof BlockFlowingFluid) {
                  var1.func_180501_a(var12, Blocks.field_150350_a.func_176223_P(), 3);
                  ++var4;
                  if (var7 < 6) {
                     var3.add(new Tuple(var12, var7 + 1));
                  }
               } else if (var15 == Material.field_203243_f || var15 == Material.field_204868_h) {
                  var13.func_196949_c(var1, var12, 0);
                  var1.func_180501_a(var12, Blocks.field_150350_a.func_176223_P(), 3);
                  ++var4;
                  if (var7 < 6) {
                     var3.add(new Tuple(var12, var7 + 1));
                  }
               }
            }
         }

         if (var4 > 64) {
            break;
         }
      }

      return var4 > 0;
   }
}
