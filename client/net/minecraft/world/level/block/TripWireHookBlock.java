package net.minecraft.world.level.block;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TripWireHookBlock extends Block {
   public static final MapCodec<TripWireHookBlock> CODEC = simpleCodec(TripWireHookBlock::new);
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
   protected static final int WIRE_DIST_MIN = 1;
   protected static final int WIRE_DIST_MAX = 42;
   private static final int RECHECK_PERIOD = 10;
   protected static final int AABB_OFFSET = 3;
   protected static final VoxelShape NORTH_AABB = Block.box(5.0, 0.0, 10.0, 11.0, 10.0, 16.0);
   protected static final VoxelShape SOUTH_AABB = Block.box(5.0, 0.0, 0.0, 11.0, 10.0, 6.0);
   protected static final VoxelShape WEST_AABB = Block.box(10.0, 0.0, 5.0, 16.0, 10.0, 11.0);
   protected static final VoxelShape EAST_AABB = Block.box(0.0, 0.0, 5.0, 6.0, 10.0, 11.0);

   @Override
   public MapCodec<TripWireHookBlock> codec() {
      return CODEC;
   }

   public TripWireHookBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(
         this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, Boolean.valueOf(false)).setValue(ATTACHED, Boolean.valueOf(false))
      );
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      switch ((Direction)var1.getValue(FACING)) {
         case EAST:
         default:
            return EAST_AABB;
         case WEST:
            return WEST_AABB;
         case SOUTH:
            return SOUTH_AABB;
         case NORTH:
            return NORTH_AABB;
      }
   }

   @Override
   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      Direction var4 = var1.getValue(FACING);
      BlockPos var5 = var3.relative(var4.getOpposite());
      BlockState var6 = var2.getBlockState(var5);
      return var4.getAxis().isHorizontal() && var6.isFaceSturdy(var2, var5, var4);
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var2.getOpposite() == var1.getValue(FACING) && !var1.canSurvive(var4, var5)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = this.defaultBlockState().setValue(POWERED, Boolean.valueOf(false)).setValue(ATTACHED, Boolean.valueOf(false));
      Level var3 = var1.getLevel();
      BlockPos var4 = var1.getClickedPos();
      Direction[] var5 = var1.getNearestLookingDirections();

      for (Direction var9 : var5) {
         if (var9.getAxis().isHorizontal()) {
            Direction var10 = var9.getOpposite();
            var2 = var2.setValue(FACING, var10);
            if (var2.canSurvive(var3, var4)) {
               return var2;
            }
         }
      }

      return null;
   }

   @Override
   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5) {
      calculateState(var1, var2, var3, false, false, -1, null);
   }

   public static void calculateState(Level var0, BlockPos var1, BlockState var2, boolean var3, boolean var4, int var5, @Nullable BlockState var6) {
      Optional var7 = var2.getOptionalValue(FACING);
      if (var7.isPresent()) {
         Direction var8 = (Direction)var7.get();
         boolean var9 = var2.getOptionalValue(ATTACHED).orElse(false);
         boolean var10 = var2.getOptionalValue(POWERED).orElse(false);
         Block var11 = var2.getBlock();
         boolean var12 = !var3;
         boolean var13 = false;
         int var14 = 0;
         BlockState[] var15 = new BlockState[42];

         for (int var16 = 1; var16 < 42; var16++) {
            BlockPos var17 = var1.relative(var8, var16);
            BlockState var18 = var0.getBlockState(var17);
            if (var18.is(Blocks.TRIPWIRE_HOOK)) {
               if (var18.getValue(FACING) == var8.getOpposite()) {
                  var14 = var16;
               }
               break;
            }

            if (!var18.is(Blocks.TRIPWIRE) && var16 != var5) {
               var15[var16] = null;
               var12 = false;
            } else {
               if (var16 == var5) {
                  var18 = (BlockState)MoreObjects.firstNonNull(var6, var18);
               }

               boolean var19 = !var18.getValue(TripWireBlock.DISARMED);
               boolean var20 = var18.getValue(TripWireBlock.POWERED);
               var13 |= var19 && var20;
               var15[var16] = var18;
               if (var16 == var5) {
                  var0.scheduleTick(var1, var11, 10);
                  var12 &= var19;
               }
            }
         }

         var12 &= var14 > 1;
         var13 &= var12;
         BlockState var23 = var11.defaultBlockState().trySetValue(ATTACHED, Boolean.valueOf(var12)).trySetValue(POWERED, Boolean.valueOf(var13));
         if (var14 > 0) {
            BlockPos var24 = var1.relative(var8, var14);
            Direction var26 = var8.getOpposite();
            var0.setBlock(var24, var23.setValue(FACING, var26), 3);
            notifyNeighbors(var11, var0, var24, var26);
            emitState(var0, var24, var12, var13, var9, var10);
         }

         emitState(var0, var1, var12, var13, var9, var10);
         if (!var3) {
            var0.setBlock(var1, var23.setValue(FACING, var8), 3);
            if (var4) {
               notifyNeighbors(var11, var0, var1, var8);
            }
         }

         if (var9 != var12) {
            for (int var25 = 1; var25 < var14; var25++) {
               BlockPos var27 = var1.relative(var8, var25);
               BlockState var28 = var15[var25];
               if (var28 != null) {
                  BlockState var29 = var0.getBlockState(var27);
                  if (var29.is(Blocks.TRIPWIRE) || var29.is(Blocks.TRIPWIRE_HOOK)) {
                     var0.setBlock(var27, var28.trySetValue(ATTACHED, Boolean.valueOf(var12)), 3);
                  }
               }
            }
         }
      }
   }

   @Override
   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      calculateState(var2, var3, var1, false, true, -1, null);
   }

   private static void emitState(Level var0, BlockPos var1, boolean var2, boolean var3, boolean var4, boolean var5) {
      if (var3 && !var5) {
         var0.playSound(null, var1, SoundEvents.TRIPWIRE_CLICK_ON, SoundSource.BLOCKS, 0.4F, 0.6F);
         var0.gameEvent(null, GameEvent.BLOCK_ACTIVATE, var1);
      } else if (!var3 && var5) {
         var0.playSound(null, var1, SoundEvents.TRIPWIRE_CLICK_OFF, SoundSource.BLOCKS, 0.4F, 0.5F);
         var0.gameEvent(null, GameEvent.BLOCK_DEACTIVATE, var1);
      } else if (var2 && !var4) {
         var0.playSound(null, var1, SoundEvents.TRIPWIRE_ATTACH, SoundSource.BLOCKS, 0.4F, 0.7F);
         var0.gameEvent(null, GameEvent.BLOCK_ATTACH, var1);
      } else if (!var2 && var4) {
         var0.playSound(null, var1, SoundEvents.TRIPWIRE_DETACH, SoundSource.BLOCKS, 0.4F, 1.2F / (var0.random.nextFloat() * 0.2F + 0.9F));
         var0.gameEvent(null, GameEvent.BLOCK_DETACH, var1);
      }
   }

   private static void notifyNeighbors(Block var0, Level var1, BlockPos var2, Direction var3) {
      Direction var4 = var3.getOpposite();
      Orientation var5 = ExperimentalRedstoneUtils.initialOrientation(var1, var4, Direction.UP);
      var1.updateNeighborsAt(var2, var0, var5);
      var1.updateNeighborsAt(var2.relative(var4), var0, var5);
   }

   @Override
   protected void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var5 && !var1.is(var4.getBlock())) {
         boolean var6 = var1.getValue(ATTACHED);
         boolean var7 = var1.getValue(POWERED);
         if (var6 || var7) {
            calculateState(var2, var3, var1, true, false, -1, null);
         }

         if (var7) {
            notifyNeighbors(this, var2, var3, var1.getValue(FACING));
         }

         super.onRemove(var1, var2, var3, var4, var5);
      }
   }

   @Override
   protected int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return var1.getValue(POWERED) ? 15 : 0;
   }

   @Override
   protected int getDirectSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      if (!var1.getValue(POWERED)) {
         return 0;
      } else {
         return var1.getValue(FACING) == var4 ? 15 : 0;
      }
   }

   @Override
   protected boolean isSignalSource(BlockState var1) {
      return true;
   }

   @Override
   protected BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(FACING, var2.rotate(var1.getValue(FACING)));
   }

   @Override
   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation(var1.getValue(FACING)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, POWERED, ATTACHED);
   }
}
