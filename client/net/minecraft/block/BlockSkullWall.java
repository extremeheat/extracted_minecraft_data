package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockSkullWall extends BlockAbstractSkull {
   public static final DirectionProperty field_196302_a;
   private static final Map<EnumFacing, VoxelShape> field_196303_A;

   protected BlockSkullWall(BlockSkull.ISkullType var1, Block.Properties var2) {
      super(var1, var2);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_196302_a, EnumFacing.NORTH));
   }

   public String func_149739_a() {
      return this.func_199767_j().func_77658_a();
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return (VoxelShape)field_196303_A.get(var1.func_177229_b(field_196302_a));
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      IBlockState var2 = this.func_176223_P();
      World var3 = var1.func_195991_k();
      BlockPos var4 = var1.func_195995_a();
      EnumFacing[] var5 = var1.func_196009_e();
      EnumFacing[] var6 = var5;
      int var7 = var5.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         EnumFacing var9 = var6[var8];
         if (var9.func_176740_k().func_176722_c()) {
            EnumFacing var10 = var9.func_176734_d();
            var2 = (IBlockState)var2.func_206870_a(field_196302_a, var10);
            if (!var3.func_180495_p(var4.func_177972_a(var9)).func_196953_a(var1)) {
               return var2;
            }
         }
      }

      return null;
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return (IBlockState)var1.func_206870_a(field_196302_a, var2.func_185831_a((EnumFacing)var1.func_177229_b(field_196302_a)));
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      return var1.func_185907_a(var2.func_185800_a((EnumFacing)var1.func_177229_b(field_196302_a)));
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_196302_a);
   }

   static {
      field_196302_a = BlockHorizontal.field_185512_D;
      field_196303_A = Maps.newEnumMap(ImmutableMap.of(EnumFacing.NORTH, Block.func_208617_a(4.0D, 4.0D, 8.0D, 12.0D, 12.0D, 16.0D), EnumFacing.SOUTH, Block.func_208617_a(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 8.0D), EnumFacing.EAST, Block.func_208617_a(0.0D, 4.0D, 4.0D, 8.0D, 12.0D, 12.0D), EnumFacing.WEST, Block.func_208617_a(8.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D)));
   }
}
