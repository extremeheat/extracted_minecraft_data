package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockChorusPlant extends BlockSixWay {
   protected BlockChorusPlant(Block.Properties var1) {
      super(0.3125F, var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_196488_a, false)).func_206870_a(field_196490_b, false)).func_206870_a(field_196492_c, false)).func_206870_a(field_196495_y, false)).func_206870_a(field_196496_z, false)).func_206870_a(field_196489_A, false));
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      return this.func_196497_a(var1.func_195991_k(), var1.func_195995_a());
   }

   public IBlockState func_196497_a(IBlockReader var1, BlockPos var2) {
      Block var3 = var1.func_180495_p(var2.func_177977_b()).func_177230_c();
      Block var4 = var1.func_180495_p(var2.func_177984_a()).func_177230_c();
      Block var5 = var1.func_180495_p(var2.func_177978_c()).func_177230_c();
      Block var6 = var1.func_180495_p(var2.func_177974_f()).func_177230_c();
      Block var7 = var1.func_180495_p(var2.func_177968_d()).func_177230_c();
      Block var8 = var1.func_180495_p(var2.func_177976_e()).func_177230_c();
      return (IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.func_176223_P().func_206870_a(field_196489_A, var3 == this || var3 == Blocks.field_185766_cS || var3 == Blocks.field_150377_bs)).func_206870_a(field_196496_z, var4 == this || var4 == Blocks.field_185766_cS)).func_206870_a(field_196488_a, var5 == this || var5 == Blocks.field_185766_cS)).func_206870_a(field_196490_b, var6 == this || var6 == Blocks.field_185766_cS)).func_206870_a(field_196492_c, var7 == this || var7 == Blocks.field_185766_cS)).func_206870_a(field_196495_y, var8 == this || var8 == Blocks.field_185766_cS);
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if (!var1.func_196955_c(var4, var5)) {
         var4.func_205220_G_().func_205360_a(var5, this, 1);
         return super.func_196271_a(var1, var2, var3, var4, var5, var6);
      } else {
         Block var7 = var3.func_177230_c();
         boolean var8 = var7 == this || var7 == Blocks.field_185766_cS || var2 == EnumFacing.DOWN && var7 == Blocks.field_150377_bs;
         return (IBlockState)var1.func_206870_a((IProperty)field_196491_B.get(var2), var8);
      }
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (!var1.func_196955_c(var2, var3)) {
         var2.func_175655_b(var3, true);
      }

   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return Items.field_185161_cS;
   }

   public int func_196264_a(IBlockState var1, Random var2) {
      return var2.nextInt(2);
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      IBlockState var4 = var2.func_180495_p(var3.func_177977_b());
      boolean var5 = !var2.func_180495_p(var3.func_177984_a()).func_196958_f() && !var4.func_196958_f();
      Iterator var6 = EnumFacing.Plane.HORIZONTAL.iterator();

      Block var10;
      do {
         BlockPos var8;
         Block var9;
         do {
            if (!var6.hasNext()) {
               Block var11 = var4.func_177230_c();
               return var11 == this || var11 == Blocks.field_150377_bs;
            }

            EnumFacing var7 = (EnumFacing)var6.next();
            var8 = var3.func_177972_a(var7);
            var9 = var2.func_180495_p(var8).func_177230_c();
         } while(var9 != this);

         if (var5) {
            return false;
         }

         var10 = var2.func_180495_p(var8.func_177977_b()).func_177230_c();
      } while(var10 != this && var10 != Blocks.field_150377_bs);

      return true;
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_196488_a, field_196490_b, field_196492_c, field_196495_y, field_196496_z, field_196489_A);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      return false;
   }
}
