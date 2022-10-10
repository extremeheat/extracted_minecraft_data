package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockCoralWallFanDead extends BlockCoralFan {
   public static final DirectionProperty field_211884_b;
   private static final Map<EnumFacing, VoxelShape> field_211885_c;

   protected BlockCoralWallFanDead(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_211884_b, EnumFacing.NORTH)).func_206870_a(field_212560_b, true));
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return (VoxelShape)field_211885_c.get(var1.func_177229_b(field_211884_b));
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return (IBlockState)var1.func_206870_a(field_211884_b, var2.func_185831_a((EnumFacing)var1.func_177229_b(field_211884_b)));
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      return var1.func_185907_a(var2.func_185800_a((EnumFacing)var1.func_177229_b(field_211884_b)));
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_211884_b, field_212560_b);
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if ((Boolean)var1.func_177229_b(field_212560_b)) {
         var4.func_205219_F_().func_205360_a(var5, Fluids.field_204546_a, Fluids.field_204546_a.func_205569_a(var4));
      }

      return var2.func_176734_d() == var1.func_177229_b(field_211884_b) && !var1.func_196955_c(var4, var5) ? Blocks.field_150350_a.func_176223_P() : var1;
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      EnumFacing var4 = (EnumFacing)var1.func_177229_b(field_211884_b);
      BlockPos var5 = var3.func_177972_a(var4.func_176734_d());
      IBlockState var6 = var2.func_180495_p(var5);
      return var6.func_193401_d(var2, var5, var4) == BlockFaceShape.SOLID && !func_193382_c(var6.func_177230_c());
   }

   @Nullable
   public IBlockState func_196258_a(BlockItemUseContext var1) {
      IBlockState var2 = super.func_196258_a(var1);
      World var3 = var1.func_195991_k();
      BlockPos var4 = var1.func_195995_a();
      EnumFacing[] var5 = var1.func_196009_e();
      EnumFacing[] var6 = var5;
      int var7 = var5.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         EnumFacing var9 = var6[var8];
         if (var9.func_176740_k().func_176722_c()) {
            var2 = (IBlockState)var2.func_206870_a(field_211884_b, var9.func_176734_d());
            if (var2.func_196955_c(var3, var4)) {
               return var2;
            }
         }
      }

      return null;
   }

   static {
      field_211884_b = BlockHorizontal.field_185512_D;
      field_211885_c = Maps.newEnumMap(ImmutableMap.of(EnumFacing.NORTH, Block.func_208617_a(0.0D, 4.0D, 5.0D, 16.0D, 12.0D, 16.0D), EnumFacing.SOUTH, Block.func_208617_a(0.0D, 4.0D, 0.0D, 16.0D, 12.0D, 11.0D), EnumFacing.WEST, Block.func_208617_a(5.0D, 4.0D, 0.0D, 16.0D, 12.0D, 16.0D), EnumFacing.EAST, Block.func_208617_a(0.0D, 4.0D, 0.0D, 11.0D, 12.0D, 16.0D)));
   }
}
