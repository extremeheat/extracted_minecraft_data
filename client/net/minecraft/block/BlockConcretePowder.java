package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockConcretePowder extends BlockFalling {
   private final IBlockState field_200294_a;

   public BlockConcretePowder(Block var1, Block.Properties var2) {
      super(var2);
      this.field_200294_a = var1.func_176223_P();
   }

   public void func_176502_a_(World var1, BlockPos var2, IBlockState var3, IBlockState var4) {
      if (func_212566_x(var4)) {
         var1.func_180501_a(var2, this.field_200294_a, 3);
      }

   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      World var2 = var1.func_195991_k();
      BlockPos var3 = var1.func_195995_a();
      return !func_212566_x(var2.func_180495_p(var3)) && !func_196441_b(var2, var3) ? super.func_196258_a(var1) : this.field_200294_a;
   }

   private static boolean func_196441_b(IBlockReader var0, BlockPos var1) {
      boolean var2 = false;
      BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos(var1);
      EnumFacing[] var4 = EnumFacing.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         EnumFacing var7 = var4[var6];
         IBlockState var8 = var0.func_180495_p(var3);
         if (var7 != EnumFacing.DOWN || func_212566_x(var8)) {
            var3.func_189533_g(var1).func_189536_c(var7);
            var8 = var0.func_180495_p(var3);
            if (func_212566_x(var8) && !Block.func_208061_a(var8.func_196952_d(var0, var1), var7.func_176734_d())) {
               var2 = true;
               break;
            }
         }
      }

      return var2;
   }

   private static boolean func_212566_x(IBlockState var0) {
      return var0.func_204520_s().func_206884_a(FluidTags.field_206959_a);
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      return func_196441_b(var4, var5) ? this.field_200294_a : super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }
}
