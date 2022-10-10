package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;

public class BlockHorizontalFace extends BlockHorizontal {
   public static final EnumProperty<AttachFace> field_196366_M;

   protected BlockHorizontalFace(Block.Properties var1) {
      super(var1);
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      EnumFacing var4 = func_196365_i(var1).func_176734_d();
      BlockPos var5 = var3.func_177972_a(var4);
      IBlockState var6 = var2.func_180495_p(var5);
      Block var7 = var6.func_177230_c();
      if (func_193384_b(var7)) {
         return false;
      } else {
         boolean var8 = var6.func_193401_d(var2, var5, var4.func_176734_d()) == BlockFaceShape.SOLID;
         if (var4 == EnumFacing.UP) {
            return var7 == Blocks.field_150438_bZ || var8;
         } else {
            return !func_193382_c(var7) && var8;
         }
      }
   }

   @Nullable
   public IBlockState func_196258_a(BlockItemUseContext var1) {
      EnumFacing[] var2 = var1.func_196009_e();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EnumFacing var5 = var2[var4];
         IBlockState var6;
         if (var5.func_176740_k() == EnumFacing.Axis.Y) {
            var6 = (IBlockState)((IBlockState)this.func_176223_P().func_206870_a(field_196366_M, var5 == EnumFacing.UP ? AttachFace.CEILING : AttachFace.FLOOR)).func_206870_a(field_185512_D, var1.func_195992_f());
         } else {
            var6 = (IBlockState)((IBlockState)this.func_176223_P().func_206870_a(field_196366_M, AttachFace.WALL)).func_206870_a(field_185512_D, var5.func_176734_d());
         }

         if (var6.func_196955_c(var1.func_195991_k(), var1.func_195995_a())) {
            return var6;
         }
      }

      return null;
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      return func_196365_i(var1).func_176734_d() == var2 && !var1.func_196955_c(var4, var5) ? Blocks.field_150350_a.func_176223_P() : super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   protected static EnumFacing func_196365_i(IBlockState var0) {
      switch((AttachFace)var0.func_177229_b(field_196366_M)) {
      case CEILING:
         return EnumFacing.DOWN;
      case FLOOR:
         return EnumFacing.UP;
      default:
         return (EnumFacing)var0.func_177229_b(field_185512_D);
      }
   }

   static {
      field_196366_M = BlockStateProperties.field_208158_K;
   }
}
