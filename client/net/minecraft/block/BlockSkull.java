package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class BlockSkull extends BlockAbstractSkull {
   public static final IntegerProperty field_196294_a;
   protected static final VoxelShape field_196295_b;

   protected BlockSkull(BlockSkull.ISkullType var1, Block.Properties var2) {
      super(var1, var2);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_196294_a, 0));
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196295_b;
   }

   public VoxelShape func_196247_c(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return VoxelShapes.func_197880_a();
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      return (IBlockState)this.func_176223_P().func_206870_a(field_196294_a, MathHelper.func_76128_c((double)(var1.func_195990_h() * 16.0F / 360.0F) + 0.5D) & 15);
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return (IBlockState)var1.func_206870_a(field_196294_a, var2.func_185833_a((Integer)var1.func_177229_b(field_196294_a), 16));
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      return (IBlockState)var1.func_206870_a(field_196294_a, var2.func_185802_a((Integer)var1.func_177229_b(field_196294_a), 16));
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_196294_a);
   }

   static {
      field_196294_a = BlockStateProperties.field_208138_am;
      field_196295_b = Block.func_208617_a(4.0D, 0.0D, 4.0D, 12.0D, 8.0D, 12.0D);
   }

   public static enum Types implements BlockSkull.ISkullType {
      SKELETON,
      WITHER_SKELETON,
      PLAYER,
      ZOMBIE,
      CREEPER,
      DRAGON;

      private Types() {
      }
   }

   public interface ISkullType {
   }
}
