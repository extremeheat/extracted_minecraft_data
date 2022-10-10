package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockAttachedStem extends BlockBush {
   public static final DirectionProperty field_196280_a;
   private final BlockStemGrown field_196281_b;
   private static final Map<EnumFacing, VoxelShape> field_196282_c;

   protected BlockAttachedStem(BlockStemGrown var1, Block.Properties var2) {
      super(var2);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_196280_a, EnumFacing.NORTH));
      this.field_196281_b = var1;
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return (VoxelShape)field_196282_c.get(var1.func_177229_b(field_196280_a));
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      return var3.func_177230_c() != this.field_196281_b && var2 == var1.func_177229_b(field_196280_a) ? (IBlockState)this.field_196281_b.func_196524_d().func_176223_P().func_206870_a(BlockStem.field_176484_a, 7) : super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   protected boolean func_200014_a_(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return var1.func_177230_c() == Blocks.field_150458_ak;
   }

   protected Item func_196279_O_() {
      if (this.field_196281_b == Blocks.field_150423_aK) {
         return Items.field_151080_bb;
      } else {
         return this.field_196281_b == Blocks.field_150440_ba ? Items.field_151081_bc : Items.field_190931_a;
      }
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return Items.field_190931_a;
   }

   public ItemStack func_185473_a(IBlockReader var1, BlockPos var2, IBlockState var3) {
      return new ItemStack(this.func_196279_O_());
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return (IBlockState)var1.func_206870_a(field_196280_a, var2.func_185831_a((EnumFacing)var1.func_177229_b(field_196280_a)));
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      return var1.func_185907_a(var2.func_185800_a((EnumFacing)var1.func_177229_b(field_196280_a)));
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_196280_a);
   }

   static {
      field_196280_a = BlockHorizontal.field_185512_D;
      field_196282_c = Maps.newEnumMap(ImmutableMap.of(EnumFacing.SOUTH, Block.func_208617_a(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 16.0D), EnumFacing.WEST, Block.func_208617_a(0.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D), EnumFacing.NORTH, Block.func_208617_a(6.0D, 0.0D, 0.0D, 10.0D, 10.0D, 10.0D), EnumFacing.EAST, Block.func_208617_a(6.0D, 0.0D, 6.0D, 16.0D, 10.0D, 10.0D)));
   }
}
