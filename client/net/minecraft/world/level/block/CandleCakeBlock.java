package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
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
   public static final BooleanProperty LIT;
   protected static final VoxelShape CAKE_SHAPE;
   protected static final VoxelShape CANDLE_SHAPE;
   protected static final VoxelShape SHAPE;
   private static final Map<Block, CandleCakeBlock> BY_CANDLE;
   private static final Iterable<Vec3> PARTICLE_OFFSETS;

   protected CandleCakeBlock(Block var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LIT, false));
      BY_CANDLE.put(var1, this);
   }

   protected Iterable<Vec3> getParticleOffsets(BlockState var1) {
      return PARTICLE_OFFSETS;
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      ItemStack var7 = var4.getItemInHand(var5);
      if (!var7.is(Items.FLINT_AND_STEEL) && !var7.is(Items.FIRE_CHARGE)) {
         if (candleHit(var6) && var4.getItemInHand(var5).isEmpty() && (Boolean)var1.getValue(LIT)) {
            extinguish(var1, var2, var3);
            return InteractionResult.sidedSuccess(var2.isClientSide);
         } else {
            dropResources(var1, var2, var3);
            return CakeBlock.eat(var2, var3, Blocks.CAKE.defaultBlockState(), var4);
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   private static boolean candleHit(BlockHitResult var0) {
      return var0.getLocation().y - (double)var0.getBlockPos().getY() > 0.5D;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(LIT);
   }

   public ItemStack getCloneItemStack(BlockGetter var1, BlockPos var2, BlockState var3) {
      return new ItemStack(Blocks.CAKE);
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var2 == Direction.DOWN && !var1.canSurvive(var4, var5) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return var2.getBlockState(var3.below()).getMaterial().isSolid();
   }

   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return CakeBlock.FULL_CAKE_SIGNAL;
   }

   public boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   public static BlockState byCandle(Block var0) {
      return ((CandleCakeBlock)BY_CANDLE.get(var0)).defaultBlockState();
   }

   public static boolean canLight(BlockState var0) {
      return var0.is(BlockTags.CANDLE_CAKES, (var1) -> {
         return var1.hasProperty(LIT) && !(Boolean)var0.getValue(LIT);
      });
   }

   static {
      LIT = AbstractCandleBlock.LIT;
      CAKE_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D);
      CANDLE_SHAPE = Block.box(7.0D, 8.0D, 7.0D, 9.0D, 14.0D, 9.0D);
      SHAPE = Shapes.or(CAKE_SHAPE, CANDLE_SHAPE);
      BY_CANDLE = Maps.newHashMap();
      PARTICLE_OFFSETS = ImmutableList.of(new Vec3(0.5D, 1.0D, 0.5D));
   }
}
