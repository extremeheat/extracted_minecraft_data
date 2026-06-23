package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class LeverBlock extends FaceAttachedHorizontalDirectionalBlock {
   public static final MapCodec<LeverBlock> CODEC = simpleCodec(LeverBlock::new);
   public static final BooleanProperty POWERED;
   private final Function<BlockState, VoxelShape> shapes;

   public MapCodec<LeverBlock> codec() {
      return CODEC;
   }

   protected LeverBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false)).setValue(FACE, AttachFace.WALL));
      this.shapes = this.makeShapes();
   }

   private Function<BlockState, VoxelShape> makeShapes() {
      Map<AttachFace, Map<Direction, VoxelShape>> attachFace = Shapes.rotateAttachFace(Block.boxZ(6.0, 8.0, 10.0, 16.0));
      return this.getShapeForEachState((state) -> (VoxelShape)((Map)attachFace.get(state.getValue(FACE))).get(state.getValue(FACING)), new Property[]{POWERED});
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return (VoxelShape)this.shapes.apply(state);
   }

   protected InteractionResult useWithoutItem(final BlockState stateBefore, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      if (level.isClientSide()) {
         BlockState stateAfter = (BlockState)stateBefore.cycle(POWERED);
         if ((Boolean)stateAfter.getValue(POWERED)) {
            makeParticle(stateAfter, level, pos, 1.0F);
         }
      } else {
         this.pull(stateBefore, level, pos, (Player)null);
      }

      return InteractionResult.SUCCESS;
   }

   protected void onExplosionHit(final BlockState state, final ServerLevel level, final BlockPos pos, final Explosion explosion, final BiConsumer<ItemStack, BlockPos> onHit) {
      if (explosion.canTriggerBlocks()) {
         this.pull(state, level, pos, (Player)null);
      }

      super.onExplosionHit(state, level, pos, explosion, onHit);
   }

   public void pull(BlockState state, final Level level, final BlockPos pos, final @Nullable Player player) {
      state = (BlockState)state.cycle(POWERED);
      level.setBlockAndUpdate(pos, state);
      this.updateNeighbours(state, level, pos);
      playSound(player, level, pos, state);
      level.gameEvent(player, (Boolean)state.getValue(POWERED) ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pos);
   }

   protected static void playSound(final @Nullable Player player, final LevelAccessor level, final BlockPos pos, final BlockState stateAfter) {
      float pitch = (Boolean)stateAfter.getValue(POWERED) ? 0.6F : 0.5F;
      level.playSound(player, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, pitch);
   }

   private static void makeParticle(final BlockState state, final LevelAccessor level, final BlockPos pos, final float scale) {
      Direction opposite = ((Direction)state.getValue(FACING)).getOpposite();
      Direction oppositeConnect = getConnectedDirection(state).getOpposite();
      double x = (double)pos.getX() + 0.5 + 0.1 * (double)opposite.getStepX() + 0.2 * (double)oppositeConnect.getStepX();
      double y = (double)pos.getY() + 0.5 + 0.1 * (double)opposite.getStepY() + 0.2 * (double)oppositeConnect.getStepY();
      double z = (double)pos.getZ() + 0.5 + 0.1 * (double)opposite.getStepZ() + 0.2 * (double)oppositeConnect.getStepZ();
      level.addParticle(new DustParticleOptions(16711680, scale), x, y, z, 0.0, 0.0, 0.0);
   }

   public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
      if ((Boolean)state.getValue(POWERED) && random.nextFloat() < 0.25F) {
         makeParticle(state, level, pos, 0.5F);
      }

   }

   protected void affectNeighborsAfterRemoval(final BlockState state, final ServerLevel level, final BlockPos pos, final boolean movedByPiston) {
      if (!movedByPiston && (Boolean)state.getValue(POWERED)) {
         this.updateNeighbours(state, level, pos);
      }

   }

   protected int getDirectSignal(final BlockState state, final BlockGetter level, final BlockPos pos, final Direction direction) {
      return (Boolean)state.getValue(POWERED) && getConnectedDirection(state) == direction ? 15 : 0;
   }

   protected boolean isSignalSource(final BlockState state) {
      return true;
   }

   protected int ownSignal(final BlockState state, final BlockGetter level, final BlockPos pos) {
      return (Boolean)state.getValue(POWERED) ? 15 : 0;
   }

   private void updateNeighbours(final BlockState state, final Level level, final BlockPos pos) {
      Direction front = getConnectedDirection(state).getOpposite();
      Orientation orientation = ExperimentalRedstoneUtils.initialOrientation(level, front, front.getAxis().isHorizontal() ? Direction.UP : (Direction)state.getValue(FACING));
      level.updateNeighborsAt(pos, this, orientation);
      level.updateNeighborsAt(pos.relative(front), this, orientation);
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(FACE, FACING, POWERED);
   }

   static {
      POWERED = BlockStateProperties.POWERED;
   }
}
