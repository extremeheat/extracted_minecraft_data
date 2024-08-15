package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TrapDoorBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<TrapDoorBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(BlockSetType.CODEC.fieldOf("block_set_type").forGetter(var0x -> var0x.type), propertiesCodec()).apply(var0, TrapDoorBlock::new)
   );
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final int AABB_THICKNESS = 3;
   protected static final VoxelShape EAST_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
   protected static final VoxelShape WEST_OPEN_AABB = Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
   protected static final VoxelShape SOUTH_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
   protected static final VoxelShape NORTH_OPEN_AABB = Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
   protected static final VoxelShape BOTTOM_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0);
   protected static final VoxelShape TOP_AABB = Block.box(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);
   private final BlockSetType type;

   @Override
   public MapCodec<? extends TrapDoorBlock> codec() {
      return CODEC;
   }

   protected TrapDoorBlock(BlockSetType var1, BlockBehaviour.Properties var2) {
      super(var2.sound(var1.soundType()));
      this.type = var1;
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(FACING, Direction.NORTH)
            .setValue(OPEN, Boolean.valueOf(false))
            .setValue(HALF, Half.BOTTOM)
            .setValue(POWERED, Boolean.valueOf(false))
            .setValue(WATERLOGGED, Boolean.valueOf(false))
      );
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      if (!var1.getValue(OPEN)) {
         return var1.getValue(HALF) == Half.TOP ? TOP_AABB : BOTTOM_AABB;
      } else {
         switch ((Direction)var1.getValue(FACING)) {
            case NORTH:
            default:
               return NORTH_OPEN_AABB;
            case SOUTH:
               return SOUTH_OPEN_AABB;
            case WEST:
               return WEST_OPEN_AABB;
            case EAST:
               return EAST_OPEN_AABB;
         }
      }
   }

   @Override
   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      switch (var2) {
         case LAND:
            return var1.getValue(OPEN);
         case WATER:
            return var1.getValue(WATERLOGGED);
         case AIR:
            return var1.getValue(OPEN);
         default:
            return false;
      }
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (!this.type.canOpenByHand()) {
         return InteractionResult.PASS;
      } else {
         this.toggle(var1, var2, var3, var4);
         return InteractionResult.SUCCESS;
      }
   }

   @Override
   protected void onExplosionHit(BlockState var1, ServerLevel var2, BlockPos var3, Explosion var4, BiConsumer<ItemStack, BlockPos> var5) {
      if (var4.canTriggerBlocks() && this.type.canOpenByWindCharge() && !var1.getValue(POWERED)) {
         this.toggle(var1, var2, var3, null);
      }

      super.onExplosionHit(var1, var2, var3, var4, var5);
   }

   private void toggle(BlockState var1, Level var2, BlockPos var3, @Nullable Player var4) {
      BlockState var5 = var1.cycle(OPEN);
      var2.setBlock(var3, var5, 2);
      if (var5.getValue(WATERLOGGED)) {
         var2.scheduleTick(var3, Fluids.WATER, Fluids.WATER.getTickDelay(var2));
      }

      this.playSound(var4, var2, var3, var5.getValue(OPEN));
   }

   protected void playSound(@Nullable Player var1, Level var2, BlockPos var3, boolean var4) {
      var2.playSound(
         var1, var3, var4 ? this.type.trapdoorOpen() : this.type.trapdoorClose(), SoundSource.BLOCKS, 1.0F, var2.getRandom().nextFloat() * 0.1F + 0.9F
      );
      var2.gameEvent(var1, var4 ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, var3);
   }

   @Override
   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, @Nullable Orientation var5, boolean var6) {
      if (!var2.isClientSide) {
         boolean var7 = var2.hasNeighborSignal(var3);
         if (var7 != var1.getValue(POWERED)) {
            if (var1.getValue(OPEN) != var7) {
               var1 = var1.setValue(OPEN, Boolean.valueOf(var7));
               this.playSound(null, var2, var3, var7);
            }

            var2.setBlock(var3, var1.setValue(POWERED, Boolean.valueOf(var7)), 2);
            if (var1.getValue(WATERLOGGED)) {
               var2.scheduleTick(var3, Fluids.WATER, Fluids.WATER.getTickDelay(var2));
            }
         }
      }
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = this.defaultBlockState();
      FluidState var3 = var1.getLevel().getFluidState(var1.getClickedPos());
      Direction var4 = var1.getClickedFace();
      if (!var1.replacingClickedOnBlock() && var4.getAxis().isHorizontal()) {
         var2 = var2.setValue(FACING, var4).setValue(HALF, var1.getClickLocation().y - (double)var1.getClickedPos().getY() > 0.5 ? Half.TOP : Half.BOTTOM);
      } else {
         var2 = var2.setValue(FACING, var1.getHorizontalDirection().getOpposite()).setValue(HALF, var4 == Direction.UP ? Half.BOTTOM : Half.TOP);
      }

      if (var1.getLevel().hasNeighborSignal(var1.getClickedPos())) {
         var2 = var2.setValue(OPEN, Boolean.valueOf(true)).setValue(POWERED, Boolean.valueOf(true));
      }

      return var2.setValue(WATERLOGGED, Boolean.valueOf(var3.getType() == Fluids.WATER));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, OPEN, HALF, POWERED, WATERLOGGED);
   }

   @Override
   protected FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   protected BlockSetType getType() {
      return this.type;
   }
}
