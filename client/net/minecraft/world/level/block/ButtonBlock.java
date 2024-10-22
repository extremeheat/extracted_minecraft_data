package net.minecraft.world.level.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ButtonBlock extends FaceAttachedHorizontalDirectionalBlock {
   public static final MapCodec<ButtonBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               BlockSetType.CODEC.fieldOf("block_set_type").forGetter(var0x -> var0x.type),
               Codec.intRange(1, 1024).fieldOf("ticks_to_stay_pressed").forGetter(var0x -> var0x.ticksToStayPressed),
               propertiesCodec()
            )
            .apply(var0, ButtonBlock::new)
   );
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   private static final int PRESSED_DEPTH = 1;
   private static final int UNPRESSED_DEPTH = 2;
   protected static final int HALF_AABB_HEIGHT = 2;
   protected static final int HALF_AABB_WIDTH = 3;
   protected static final VoxelShape CEILING_AABB_X = Block.box(6.0, 14.0, 5.0, 10.0, 16.0, 11.0);
   protected static final VoxelShape CEILING_AABB_Z = Block.box(5.0, 14.0, 6.0, 11.0, 16.0, 10.0);
   protected static final VoxelShape FLOOR_AABB_X = Block.box(6.0, 0.0, 5.0, 10.0, 2.0, 11.0);
   protected static final VoxelShape FLOOR_AABB_Z = Block.box(5.0, 0.0, 6.0, 11.0, 2.0, 10.0);
   protected static final VoxelShape NORTH_AABB = Block.box(5.0, 6.0, 14.0, 11.0, 10.0, 16.0);
   protected static final VoxelShape SOUTH_AABB = Block.box(5.0, 6.0, 0.0, 11.0, 10.0, 2.0);
   protected static final VoxelShape WEST_AABB = Block.box(14.0, 6.0, 5.0, 16.0, 10.0, 11.0);
   protected static final VoxelShape EAST_AABB = Block.box(0.0, 6.0, 5.0, 2.0, 10.0, 11.0);
   protected static final VoxelShape PRESSED_CEILING_AABB_X = Block.box(6.0, 15.0, 5.0, 10.0, 16.0, 11.0);
   protected static final VoxelShape PRESSED_CEILING_AABB_Z = Block.box(5.0, 15.0, 6.0, 11.0, 16.0, 10.0);
   protected static final VoxelShape PRESSED_FLOOR_AABB_X = Block.box(6.0, 0.0, 5.0, 10.0, 1.0, 11.0);
   protected static final VoxelShape PRESSED_FLOOR_AABB_Z = Block.box(5.0, 0.0, 6.0, 11.0, 1.0, 10.0);
   protected static final VoxelShape PRESSED_NORTH_AABB = Block.box(5.0, 6.0, 15.0, 11.0, 10.0, 16.0);
   protected static final VoxelShape PRESSED_SOUTH_AABB = Block.box(5.0, 6.0, 0.0, 11.0, 10.0, 1.0);
   protected static final VoxelShape PRESSED_WEST_AABB = Block.box(15.0, 6.0, 5.0, 16.0, 10.0, 11.0);
   protected static final VoxelShape PRESSED_EAST_AABB = Block.box(0.0, 6.0, 5.0, 1.0, 10.0, 11.0);
   private final BlockSetType type;
   private final int ticksToStayPressed;

   @Override
   public MapCodec<ButtonBlock> codec() {
      return CODEC;
   }

   protected ButtonBlock(BlockSetType var1, int var2, BlockBehaviour.Properties var3) {
      super(var3.sound(var1.soundType()));
      this.type = var1;
      this.registerDefaultState(
         this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, Boolean.valueOf(false)).setValue(FACE, AttachFace.WALL)
      );
      this.ticksToStayPressed = var2;
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      Direction var5 = var1.getValue(FACING);
      boolean var6 = var1.getValue(POWERED);
      switch ((AttachFace)var1.getValue(FACE)) {
         case FLOOR:
            if (var5.getAxis() == Direction.Axis.X) {
               return var6 ? PRESSED_FLOOR_AABB_X : FLOOR_AABB_X;
            }

            return var6 ? PRESSED_FLOOR_AABB_Z : FLOOR_AABB_Z;
         case WALL:
            return switch (var5) {
               case EAST -> var6 ? PRESSED_EAST_AABB : EAST_AABB;
               case WEST -> var6 ? PRESSED_WEST_AABB : WEST_AABB;
               case SOUTH -> var6 ? PRESSED_SOUTH_AABB : SOUTH_AABB;
               case NORTH, UP, DOWN -> var6 ? PRESSED_NORTH_AABB : NORTH_AABB;
            };
         case CEILING:
         default:
            if (var5.getAxis() == Direction.Axis.X) {
               return var6 ? PRESSED_CEILING_AABB_X : CEILING_AABB_X;
            } else {
               return var6 ? PRESSED_CEILING_AABB_Z : CEILING_AABB_Z;
            }
      }
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (var1.getValue(POWERED)) {
         return InteractionResult.CONSUME;
      } else {
         this.press(var1, var2, var3, var4);
         return InteractionResult.SUCCESS;
      }
   }

   @Override
   protected void onExplosionHit(BlockState var1, ServerLevel var2, BlockPos var3, Explosion var4, BiConsumer<ItemStack, BlockPos> var5) {
      if (var4.canTriggerBlocks() && !var1.getValue(POWERED)) {
         this.press(var1, var2, var3, null);
      }

      super.onExplosionHit(var1, var2, var3, var4, var5);
   }

   public void press(BlockState var1, Level var2, BlockPos var3, @Nullable Player var4) {
      var2.setBlock(var3, var1.setValue(POWERED, Boolean.valueOf(true)), 3);
      this.updateNeighbours(var1, var2, var3);
      var2.scheduleTick(var3, this, this.ticksToStayPressed);
      this.playSound(var4, var2, var3, true);
      var2.gameEvent(var4, GameEvent.BLOCK_ACTIVATE, var3);
   }

   protected void playSound(@Nullable Player var1, LevelAccessor var2, BlockPos var3, boolean var4) {
      var2.playSound(var4 ? var1 : null, var3, this.getSound(var4), SoundSource.BLOCKS);
   }

   protected SoundEvent getSound(boolean var1) {
      return var1 ? this.type.buttonClickOn() : this.type.buttonClickOff();
   }

   @Override
   protected void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var5 && !var1.is(var4.getBlock())) {
         if (var1.getValue(POWERED)) {
            this.updateNeighbours(var1, var2, var3);
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
      return var1.getValue(POWERED) && getConnectedDirection(var1) == var4 ? 15 : 0;
   }

   @Override
   protected boolean isSignalSource(BlockState var1) {
      return true;
   }

   @Override
   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (var1.getValue(POWERED)) {
         this.checkPressed(var1, var2, var3);
      }
   }

   @Override
   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (!var2.isClientSide && this.type.canButtonBeActivatedByArrows() && !var1.getValue(POWERED)) {
         this.checkPressed(var1, var2, var3);
      }
   }

   protected void checkPressed(BlockState var1, Level var2, BlockPos var3) {
      AbstractArrow var4 = this.type.canButtonBeActivatedByArrows()
         ? var2.getEntitiesOfClass(AbstractArrow.class, var1.getShape(var2, var3).bounds().move(var3)).stream().findFirst().orElse(null)
         : null;
      boolean var5 = var4 != null;
      boolean var6 = var1.getValue(POWERED);
      if (var5 != var6) {
         var2.setBlock(var3, var1.setValue(POWERED, Boolean.valueOf(var5)), 3);
         this.updateNeighbours(var1, var2, var3);
         this.playSound(null, var2, var3, var5);
         var2.gameEvent(var4, var5 ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, var3);
      }

      if (var5) {
         var2.scheduleTick(new BlockPos(var3), this, this.ticksToStayPressed);
      }
   }

   private void updateNeighbours(BlockState var1, Level var2, BlockPos var3) {
      Direction var4 = getConnectedDirection(var1).getOpposite();
      Orientation var5 = ExperimentalRedstoneUtils.initialOrientation(var2, var4, var4.getAxis().isHorizontal() ? Direction.UP : var1.getValue(FACING));
      var2.updateNeighborsAt(var3, this, var5);
      var2.updateNeighborsAt(var3.relative(var4), this, var5);
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, POWERED, FACE);
   }
}
