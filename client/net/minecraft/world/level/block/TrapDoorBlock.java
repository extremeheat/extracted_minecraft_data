package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TrapDoorBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<TrapDoorBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(BlockSetType.CODEC.fieldOf("block_set_type").forGetter((var0x) -> var0x.type), propertiesCodec()).apply(var0, TrapDoorBlock::new));
   public static final BooleanProperty OPEN;
   public static final EnumProperty<Half> HALF;
   public static final BooleanProperty POWERED;
   public static final BooleanProperty WATERLOGGED;
   private static final Map<Direction, VoxelShape> SHAPES;
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
      return (VoxelShape)SHAPES.get((Boolean)var1.getValue(OPEN) ? var1.getValue(FACING) : (var1.getValue(HALF) == Half.TOP ? Direction.DOWN : Direction.UP));
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
         return InteractionResult.SUCCESS;
      }
   }

   protected void onExplosionHit(BlockState var1, ServerLevel var2, BlockPos var3, Explosion var4, BiConsumer<ItemStack, BlockPos> var5) {
      if (var4.canTriggerBlocks() && this.type.canOpenByWindCharge() && !(Boolean)var1.getValue(POWERED)) {
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
      var2.playSound(var1, (BlockPos)var3, var4 ? this.type.trapdoorOpen() : this.type.trapdoorClose(), SoundSource.BLOCKS, 1.0F, var2.getRandom().nextFloat() * 0.1F + 0.9F);
      var2.gameEvent(var1, var4 ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, var3);
   }

   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, @Nullable Orientation var5, boolean var6) {
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

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var3.scheduleTick(var4, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var2));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   protected BlockSetType getType() {
      return this.type;
   }

   static {
      OPEN = BlockStateProperties.OPEN;
      HALF = BlockStateProperties.HALF;
      POWERED = BlockStateProperties.POWERED;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      SHAPES = Shapes.rotateAll(Block.boxZ(16.0, 13.0, 16.0));
   }
}
