package net.minecraft.block;

import com.google.common.base.Predicates;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockStateMatcher;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockEndPortalFrame extends Block {
   public static final DirectionProperty field_176508_a;
   public static final BooleanProperty field_176507_b;
   protected static final VoxelShape field_196428_c;
   protected static final VoxelShape field_196429_y;
   protected static final VoxelShape field_196430_z;
   private static BlockPattern field_185664_e;

   public BlockEndPortalFrame(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176508_a, EnumFacing.NORTH)).func_206870_a(field_176507_b, false));
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return (Boolean)var1.func_177229_b(field_176507_b) ? field_196430_z : field_196428_c;
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return Items.field_190931_a;
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      return (IBlockState)((IBlockState)this.func_176223_P().func_206870_a(field_176508_a, var1.func_195992_f().func_176734_d())).func_206870_a(field_176507_b, false);
   }

   public boolean func_149740_M(IBlockState var1) {
      return true;
   }

   public int func_180641_l(IBlockState var1, World var2, BlockPos var3) {
      return (Boolean)var1.func_177229_b(field_176507_b) ? 15 : 0;
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return (IBlockState)var1.func_206870_a(field_176508_a, var2.func_185831_a((EnumFacing)var1.func_177229_b(field_176508_a)));
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      return var1.func_185907_a(var2.func_185800_a((EnumFacing)var1.func_177229_b(field_176508_a)));
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176508_a, field_176507_b);
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public static BlockPattern func_185661_e() {
      if (field_185664_e == null) {
         field_185664_e = FactoryBlockPattern.func_177660_a().func_177659_a("?vvv?", ">???<", ">???<", ">???<", "?^^^?").func_177662_a('?', BlockWorldState.func_177510_a(BlockStateMatcher.field_185928_a)).func_177662_a('^', BlockWorldState.func_177510_a(BlockStateMatcher.func_177638_a(Blocks.field_150378_br).func_201028_a(field_176507_b, Predicates.equalTo(true)).func_201028_a(field_176508_a, Predicates.equalTo(EnumFacing.SOUTH)))).func_177662_a('>', BlockWorldState.func_177510_a(BlockStateMatcher.func_177638_a(Blocks.field_150378_br).func_201028_a(field_176507_b, Predicates.equalTo(true)).func_201028_a(field_176508_a, Predicates.equalTo(EnumFacing.WEST)))).func_177662_a('v', BlockWorldState.func_177510_a(BlockStateMatcher.func_177638_a(Blocks.field_150378_br).func_201028_a(field_176507_b, Predicates.equalTo(true)).func_201028_a(field_176508_a, Predicates.equalTo(EnumFacing.NORTH)))).func_177662_a('<', BlockWorldState.func_177510_a(BlockStateMatcher.func_177638_a(Blocks.field_150378_br).func_201028_a(field_176507_b, Predicates.equalTo(true)).func_201028_a(field_176508_a, Predicates.equalTo(EnumFacing.EAST)))).func_177661_b();
      }

      return field_185664_e;
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return var4 == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      return false;
   }

   static {
      field_176508_a = BlockHorizontal.field_185512_D;
      field_176507_b = BlockStateProperties.field_208182_i;
      field_196428_c = Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 13.0D, 16.0D);
      field_196429_y = Block.func_208617_a(4.0D, 13.0D, 4.0D, 12.0D, 16.0D, 12.0D);
      field_196430_z = VoxelShapes.func_197872_a(field_196428_c, field_196429_y);
   }
}
