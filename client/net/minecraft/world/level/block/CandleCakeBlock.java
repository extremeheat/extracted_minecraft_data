package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CandleCakeBlock extends AbstractCandleBlock {
   public static final MapCodec<CandleCakeBlock> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("candle").forGetter((b) -> b.candleBlock), propertiesCodec()).apply(i, CandleCakeBlock::new));
   public static final BooleanProperty LIT;
   private static final VoxelShape SHAPE;
   private static final Map<CandleBlock, CandleCakeBlock> BY_CANDLE;
   private static final Iterable<Vec3> PARTICLE_OFFSETS;
   private final CandleBlock candleBlock;

   public MapCodec<CandleCakeBlock> codec() {
      return CODEC;
   }

   protected CandleCakeBlock(final Block block, final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LIT, false));
      if (block instanceof CandleBlock matchingCandleBlock) {
         BY_CANDLE.put(matchingCandleBlock, this);
         this.candleBlock = matchingCandleBlock;
      } else {
         String var10002 = String.valueOf(CandleBlock.class);
         throw new IllegalArgumentException("Expected block to be of " + var10002 + " was " + String.valueOf(block.getClass()));
      }
   }

   protected Iterable<Vec3> getParticleOffsets(final BlockState state) {
      return PARTICLE_OFFSETS;
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPE;
   }

   protected InteractionResult useItemOn(final ItemStack itemStack, final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final BlockHitResult hitResult) {
      if (!itemStack.is(Items.FLINT_AND_STEEL) && !itemStack.is(Items.FIRE_CHARGE)) {
         if (candleHit(hitResult) && itemStack.isEmpty() && (Boolean)state.getValue(LIT)) {
            extinguish(player, state, level, pos);
            return InteractionResult.SUCCESS;
         } else {
            return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      InteractionResult eatResult = CakeBlock.eat(level, pos, Blocks.CAKE.defaultBlockState(), player);
      if (eatResult.consumesAction()) {
         dropResources(state, level, pos);
      }

      return eatResult;
   }

   private static boolean candleHit(final BlockHitResult hitResult) {
      return hitResult.getLocation().y - (double)hitResult.getBlockPos().getY() > 0.5;
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(LIT);
   }

   protected ItemStack getCloneItemStack(final LevelReader level, final BlockPos pos, final BlockState state, final boolean includeData) {
      return new ItemStack(Blocks.CAKE);
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      return directionToNeighbour == Direction.DOWN && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      return level.getBlockState(pos.below()).isSolid();
   }

   protected int getAnalogOutputSignal(final BlockState state, final Level level, final BlockPos pos, final Direction direction) {
      return CakeBlock.FULL_CAKE_SIGNAL;
   }

   protected boolean hasAnalogOutputSignal(final BlockState state) {
      return true;
   }

   protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
      return false;
   }

   public static BlockState byCandle(final CandleBlock block) {
      return ((CandleCakeBlock)BY_CANDLE.get(block)).defaultBlockState();
   }

   public static boolean canLight(final BlockState state) {
      return state.is(BlockTags.CANDLE_CAKES, (s) -> s.hasProperty(LIT) && !(Boolean)state.getValue(LIT));
   }

   static {
      LIT = AbstractCandleBlock.LIT;
      SHAPE = Shapes.or(Block.column(2.0, 8.0, 14.0), Block.column(14.0, 0.0, 8.0));
      BY_CANDLE = Maps.newHashMap();
      PARTICLE_OFFSETS = List.of((new Vec3(8.0, 16.0, 8.0)).scale(0.0625));
   }
}
