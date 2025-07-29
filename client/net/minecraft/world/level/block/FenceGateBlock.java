package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FenceGateBlock extends HorizontalDirectionalBlock {
   public static final MapCodec<FenceGateBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(WoodType.CODEC.fieldOf("wood_type").forGetter((var0x) -> var0x.type), propertiesCodec()).apply(var0, FenceGateBlock::new));
   public static final BooleanProperty OPEN;
   public static final BooleanProperty POWERED;
   public static final BooleanProperty IN_WALL;
   private static final Map<Direction.Axis, VoxelShape> SHAPES;
   private static final Map<Direction.Axis, VoxelShape> SHAPES_WALL;
   private static final Map<Direction.Axis, VoxelShape> SHAPE_COLLISION;
   private static final Map<Direction.Axis, VoxelShape> SHAPE_SUPPORT;
   private static final Map<Direction.Axis, VoxelShape> SHAPE_OCCLUSION;
   private static final Map<Direction.Axis, VoxelShape> SHAPE_OCCLUSION_WALL;
   private final WoodType type;

   public MapCodec<FenceGateBlock> codec() {
      return CODEC;
   }

   public FenceGateBlock(WoodType var1, BlockBehaviour.Properties var2) {
      super(var2.sound(var1.soundType()));
      this.type = var1;
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(OPEN, false)).setValue(POWERED, false)).setValue(IN_WALL, false));
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      Direction.Axis var5 = ((Direction)var1.getValue(FACING)).getAxis();
      return (VoxelShape)((Boolean)var1.getValue(IN_WALL) ? SHAPES_WALL : SHAPES).get(var5);
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      Direction.Axis var9 = var5.getAxis();
      if (((Direction)var1.getValue(FACING)).getClockWise().getAxis() != var9) {
         return super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
      } else {
         boolean var10 = this.isWall(var7) || this.isWall(var2.getBlockState(var4.relative(var5.getOpposite())));
         return (BlockState)var1.setValue(IN_WALL, var10);
      }
   }

   protected VoxelShape getBlockSupportShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      Direction.Axis var4 = ((Direction)var1.getValue(FACING)).getAxis();
      return (Boolean)var1.getValue(OPEN) ? Shapes.empty() : (VoxelShape)SHAPE_SUPPORT.get(var4);
   }

   protected VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      Direction.Axis var5 = ((Direction)var1.getValue(FACING)).getAxis();
      return (Boolean)var1.getValue(OPEN) ? Shapes.empty() : (VoxelShape)SHAPE_COLLISION.get(var5);
   }

   protected VoxelShape getOcclusionShape(BlockState var1) {
      Direction.Axis var2 = ((Direction)var1.getValue(FACING)).getAxis();
      return (VoxelShape)((Boolean)var1.getValue(IN_WALL) ? SHAPE_OCCLUSION_WALL : SHAPE_OCCLUSION).get(var2);
   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      switch (var2) {
         case LAND -> {
            return (Boolean)var1.getValue(OPEN);
         }
         case WATER -> {
            return false;
         }
         case AIR -> {
            return (Boolean)var1.getValue(OPEN);
         }
         default -> {
            return false;
         }
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      boolean var4 = var2.hasNeighborSignal(var3);
      Direction var5 = var1.getHorizontalDirection();
      Direction.Axis var6 = var5.getAxis();
      boolean var7 = var6 == Direction.Axis.Z && (this.isWall(var2.getBlockState(var3.west())) || this.isWall(var2.getBlockState(var3.east()))) || var6 == Direction.Axis.X && (this.isWall(var2.getBlockState(var3.north())) || this.isWall(var2.getBlockState(var3.south())));
      return (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, var5)).setValue(OPEN, var4)).setValue(POWERED, var4)).setValue(IN_WALL, var7);
   }

   private boolean isWall(BlockState var1) {
      return var1.is(BlockTags.WALLS);
   }

   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if ((Boolean)var1.getValue(OPEN)) {
         var1 = (BlockState)var1.setValue(OPEN, false);
         var2.setBlock(var3, var1, 10);
      } else {
         Direction var6 = var4.getDirection();
         if (var1.getValue(FACING) == var6.getOpposite()) {
            var1 = (BlockState)var1.setValue(FACING, var6);
         }

         var1 = (BlockState)var1.setValue(OPEN, true);
         var2.setBlock(var3, var1, 10);
      }

      boolean var8 = (Boolean)var1.getValue(OPEN);
      var2.playSound(var4, (BlockPos)var3, var8 ? this.type.fenceGateOpen() : this.type.fenceGateClose(), SoundSource.BLOCKS, 1.0F, var2.getRandom().nextFloat() * 0.1F + 0.9F);
      var2.gameEvent(var4, var8 ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, var3);
      return InteractionResult.SUCCESS;
   }

   protected void onExplosionHit(BlockState var1, ServerLevel var2, BlockPos var3, Explosion var4, BiConsumer<ItemStack, BlockPos> var5) {
      if (var4.canTriggerBlocks() && !(Boolean)var1.getValue(POWERED)) {
         boolean var6 = (Boolean)var1.getValue(OPEN);
         var2.setBlockAndUpdate(var3, (BlockState)var1.setValue(OPEN, !var6));
         var2.playSound((Entity)null, var3, var6 ? this.type.fenceGateClose() : this.type.fenceGateOpen(), SoundSource.BLOCKS, 1.0F, var2.getRandom().nextFloat() * 0.1F + 0.9F);
         var2.gameEvent(var6 ? GameEvent.BLOCK_CLOSE : GameEvent.BLOCK_OPEN, var3, GameEvent.Context.of(var1));
      }

      super.onExplosionHit(var1, var2, var3, var4, var5);
   }

   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, @Nullable Orientation var5, boolean var6) {
      if (!var2.isClientSide()) {
         boolean var7 = var2.hasNeighborSignal(var3);
         if ((Boolean)var1.getValue(POWERED) != var7) {
            var2.setBlock(var3, (BlockState)((BlockState)var1.setValue(POWERED, var7)).setValue(OPEN, var7), 2);
            if ((Boolean)var1.getValue(OPEN) != var7) {
               var2.playSound((Entity)null, (BlockPos)var3, var7 ? this.type.fenceGateOpen() : this.type.fenceGateClose(), SoundSource.BLOCKS, 1.0F, var2.getRandom().nextFloat() * 0.1F + 0.9F);
               var2.gameEvent((Entity)null, var7 ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, var3);
            }
         }

      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, OPEN, POWERED, IN_WALL);
   }

   public static boolean connectsToDirection(BlockState var0, Direction var1) {
      return ((Direction)var0.getValue(FACING)).getAxis() == var1.getClockWise().getAxis();
   }

   static {
      OPEN = BlockStateProperties.OPEN;
      POWERED = BlockStateProperties.POWERED;
      IN_WALL = BlockStateProperties.IN_WALL;
      SHAPES = Shapes.rotateHorizontalAxis(Block.cube(16.0, 16.0, 4.0));
      SHAPES_WALL = Maps.newEnumMap(Util.mapValues(SHAPES, (var0) -> Shapes.join(var0, Block.column(16.0, 13.0, 16.0), BooleanOp.ONLY_FIRST)));
      SHAPE_COLLISION = Shapes.rotateHorizontalAxis(Block.column(16.0, 4.0, 0.0, 24.0));
      SHAPE_SUPPORT = Shapes.rotateHorizontalAxis(Block.column(16.0, 4.0, 5.0, 24.0));
      SHAPE_OCCLUSION = Shapes.rotateHorizontalAxis(Shapes.or(Block.box(0.0, 5.0, 7.0, 2.0, 16.0, 9.0), Block.box(14.0, 5.0, 7.0, 16.0, 16.0, 9.0)));
      SHAPE_OCCLUSION_WALL = Maps.newEnumMap(Util.mapValues(SHAPE_OCCLUSION, (var0) -> var0.move(0.0, -0.1875, 0.0).optimize()));
   }
}
