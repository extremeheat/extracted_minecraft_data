package net.minecraft.world.level.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AmethystClusterBlock extends AmethystBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<AmethystClusterBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.FLOAT.fieldOf("height").forGetter((var0x) -> var0x.height), Codec.FLOAT.fieldOf("aabb_offset").forGetter((var0x) -> var0x.aabbOffset), propertiesCodec()).apply(var0, AmethystClusterBlock::new));
   public static final BooleanProperty WATERLOGGED;
   public static final EnumProperty<Direction> FACING;
   private final float height;
   private final float aabbOffset;
   protected final VoxelShape northAabb;
   protected final VoxelShape southAabb;
   protected final VoxelShape eastAabb;
   protected final VoxelShape westAabb;
   protected final VoxelShape upAabb;
   protected final VoxelShape downAabb;

   public MapCodec<AmethystClusterBlock> codec() {
      return CODEC;
   }

   public AmethystClusterBlock(float var1, float var2, BlockBehaviour.Properties var3) {
      super(var3);
      this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(WATERLOGGED, false)).setValue(FACING, Direction.UP));
      this.upAabb = Block.box((double)var2, 0.0, (double)var2, (double)(16.0F - var2), (double)var1, (double)(16.0F - var2));
      this.downAabb = Block.box((double)var2, (double)(16.0F - var1), (double)var2, (double)(16.0F - var2), 16.0, (double)(16.0F - var2));
      this.northAabb = Block.box((double)var2, (double)var2, (double)(16.0F - var1), (double)(16.0F - var2), (double)(16.0F - var2), 16.0);
      this.southAabb = Block.box((double)var2, (double)var2, 0.0, (double)(16.0F - var2), (double)(16.0F - var2), (double)var1);
      this.eastAabb = Block.box(0.0, (double)var2, (double)var2, (double)var1, (double)(16.0F - var2), (double)(16.0F - var2));
      this.westAabb = Block.box((double)(16.0F - var1), (double)var2, (double)var2, 16.0, (double)(16.0F - var2), (double)(16.0F - var2));
      this.height = var1;
      this.aabbOffset = var2;
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      Direction var5 = (Direction)var1.getValue(FACING);
      switch (var5) {
         case NORTH:
            return this.northAabb;
         case SOUTH:
            return this.southAabb;
         case EAST:
            return this.eastAabb;
         case WEST:
            return this.westAabb;
         case DOWN:
            return this.downAabb;
         case UP:
         default:
            return this.upAabb;
      }
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      Direction var4 = (Direction)var1.getValue(FACING);
      BlockPos var5 = var3.relative(var4.getOpposite());
      return var2.getBlockState(var5).isFaceSturdy(var2, var5, var4);
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var3.scheduleTick(var4, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var2));
      }

      return var5 == ((Direction)var1.getValue(FACING)).getOpposite() && !var1.canSurvive(var2, var4) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      return (BlockState)((BlockState)this.defaultBlockState().setValue(WATERLOGGED, var2.getFluidState(var3).getType() == Fluids.WATER)).setValue(FACING, var1.getClickedFace());
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(WATERLOGGED, FACING);
   }

   static {
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      FACING = BlockStateProperties.FACING;
   }
}
