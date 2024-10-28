package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TrapDoorBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<TrapDoorBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(BlockSetType.CODEC.fieldOf("block_set_type").forGetter((var0x) -> {
         return var0x.type;
      }), propertiesCodec()).apply(var0, TrapDoorBlock::new);
   });
   public static final BooleanProperty OPEN;
   public static final EnumProperty<Half> HALF;
   public static final BooleanProperty POWERED;
   public static final BooleanProperty WATERLOGGED;
   protected static final int AABB_THICKNESS = 3;
   protected static final VoxelShape EAST_OPEN_AABB;
   protected static final VoxelShape WEST_OPEN_AABB;
   protected static final VoxelShape SOUTH_OPEN_AABB;
   protected static final VoxelShape NORTH_OPEN_AABB;
   protected static final VoxelShape BOTTOM_AABB;
   protected static final VoxelShape TOP_AABB;
   private final BlockSetType type;

   public MapCodec<? extends TrapDoorBlock> codec() {
      return CODEC;
   }

   protected TrapDoorBlock(BlockSetType var1, BlockBehaviour.Properties var2) {
      super(var2.sound(var1.soundType()));
      this.type = var1;
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(OPEN, false)).setValue(HALF, Half.BOTTOM)).setValue(POWERED, false)).setValue(WATERLOGGED, false));
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      if (!(Boolean)var1.getValue(OPEN)) {
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

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      switch (var2) {
         case LAND -> {
            return (Boolean)var1.getValue(OPEN);
         }
         case WATER -> {
            return (Boolean)var1.getValue(WATERLOGGED);
         }
         case AIR -> {
            return (Boolean)var1.getValue(OPEN);
         }
         default -> {
            return false;
         }
      }
   }

   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (!this.type.canOpenByHand()) {
         return InteractionResult.PASS;
      } else {
         this.toggle(var1, var2, var3, var4);
         return InteractionResult.sidedSuccess(var2.isClientSide);
      }
   }

   protected void onExplosionHit(BlockState var1, Level var2, BlockPos var3, Explosion var4, BiConsumer<ItemStack, BlockPos> var5) {
      if (var4.getBlockInteraction() == Explosion.BlockInteraction.TRIGGER_BLOCK && !var2.isClientSide() && this.type.canOpenByWindCharge() && !(Boolean)var1.getValue(POWERED)) {
         this.toggle(var1, var2, var3, (Player)null);
      }

      super.onExplosionHit(var1, var2, var3, var4, var5);
   }

   private void toggle(BlockState var1, Level var2, BlockPos var3, @Nullable Player var4) {
      BlockState var5 = (BlockState)var1.cycle(OPEN);
      var2.setBlock(var3, var5, 2);
      if ((Boolean)var5.getValue(WATERLOGGED)) {
         var2.scheduleTick(var3, Fluids.WATER, Fluids.WATER.getTickDelay(var2));
      }

      this.playSound(var4, var2, var3, (Boolean)var5.getValue(OPEN));
   }

   protected void playSound(@Nullable Player var1, Level var2, BlockPos var3, boolean var4) {
      var2.playSound(var1, var3, var4 ? this.type.trapdoorOpen() : this.type.trapdoorClose(), SoundSource.BLOCKS, 1.0F, var2.getRandom().nextFloat() * 0.1F + 0.9F);
      var2.gameEvent(var1, var4 ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, var3);
   }

   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if (!var2.isClientSide) {
         boolean var7 = var2.hasNeighborSignal(var3);
         if (var7 != (Boolean)var1.getValue(POWERED)) {
            if ((Boolean)var1.getValue(OPEN) != var7) {
               var1 = (BlockState)var1.setValue(OPEN, var7);
               this.playSound((Player)null, var2, var3, var7);
            }

            var2.setBlock(var3, (BlockState)var1.setValue(POWERED, var7), 2);
            if ((Boolean)var1.getValue(WATERLOGGED)) {
               var2.scheduleTick(var3, Fluids.WATER, Fluids.WATER.getTickDelay(var2));
            }
         }

      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = this.defaultBlockState();
      FluidState var3 = var1.getLevel().getFluidState(var1.getClickedPos());
      Direction var4 = var1.getClickedFace();
      if (!var1.replacingClickedOnBlock() && var4.getAxis().isHorizontal()) {
         var2 = (BlockState)((BlockState)var2.setValue(FACING, var4)).setValue(HALF, var1.getClickLocation().y - (double)var1.getClickedPos().getY() > 0.5 ? Half.TOP : Half.BOTTOM);
      } else {
         var2 = (BlockState)((BlockState)var2.setValue(FACING, var1.getHorizontalDirection().getOpposite())).setValue(HALF, var4 == Direction.UP ? Half.BOTTOM : Half.TOP);
      }

      if (var1.getLevel().hasNeighborSignal(var1.getClickedPos())) {
         var2 = (BlockState)((BlockState)var2.setValue(OPEN, true)).setValue(POWERED, true);
      }

      return (BlockState)var2.setValue(WATERLOGGED, var3.getType() == Fluids.WATER);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, OPEN, HALF, POWERED, WATERLOGGED);
   }

   protected FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   protected BlockSetType getType() {
      return this.type;
   }

   static {
      OPEN = BlockStateProperties.OPEN;
      HALF = BlockStateProperties.HALF;
      POWERED = BlockStateProperties.POWERED;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      EAST_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
      WEST_OPEN_AABB = Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
      SOUTH_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
      NORTH_OPEN_AABB = Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
      BOTTOM_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0);
      TOP_AABB = Block.box(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);
   }
}
