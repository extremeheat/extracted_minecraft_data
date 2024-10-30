package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
   public static final MapCodec<CandleCakeBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("candle").forGetter((var0x) -> {
         return var0x.candleBlock;
      }), propertiesCodec()).apply(var0, CandleCakeBlock::new);
   });
   public static final BooleanProperty LIT;
   protected static final float AABB_OFFSET = 1.0F;
   protected static final VoxelShape CAKE_SHAPE;
   protected static final VoxelShape CANDLE_SHAPE;
   protected static final VoxelShape SHAPE;
   private static final Map<CandleBlock, CandleCakeBlock> BY_CANDLE;
   private static final Iterable<Vec3> PARTICLE_OFFSETS;
   private final CandleBlock candleBlock;

   public MapCodec<CandleCakeBlock> codec() {
      return CODEC;
   }

   protected CandleCakeBlock(Block var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LIT, false));
      if (var1 instanceof CandleBlock var3) {
         BY_CANDLE.put(var3, this);
         this.candleBlock = var3;
      } else {
         String var10002 = String.valueOf(CandleBlock.class);
         throw new IllegalArgumentException("Expected block to be of " + var10002 + " was " + String.valueOf(var1.getClass()));
      }
   }

   protected Iterable<Vec3> getParticleOffsets(BlockState var1) {
      return PARTICLE_OFFSETS;
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   protected InteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      if (!var1.is(Items.FLINT_AND_STEEL) && !var1.is(Items.FIRE_CHARGE)) {
         if (candleHit(var7) && var1.isEmpty() && (Boolean)var2.getValue(LIT)) {
            extinguish(var5, var2, var3, var4);
            return InteractionResult.SUCCESS;
         } else {
            return super.useItemOn(var1, var2, var3, var4, var5, var6, var7);
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      InteractionResult var6 = CakeBlock.eat(var2, var3, Blocks.CAKE.defaultBlockState(), var4);
      if (var6.consumesAction()) {
         dropResources(var1, var2, var3);
      }

      return var6;
   }

   private static boolean candleHit(BlockHitResult var0) {
      return var0.getLocation().y - (double)var0.getBlockPos().getY() > 0.5;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(LIT);
   }

   protected ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3) {
      return new ItemStack(Blocks.CAKE);
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      return var5 == Direction.DOWN && !var1.canSurvive(var2, var4) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return var2.getBlockState(var3.below()).isSolid();
   }

   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return CakeBlock.FULL_CAKE_SIGNAL;
   }

   protected boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   public static BlockState byCandle(CandleBlock var0) {
      return ((CandleCakeBlock)BY_CANDLE.get(var0)).defaultBlockState();
   }

   public static boolean canLight(BlockState var0) {
      return var0.is(BlockTags.CANDLE_CAKES, (var1) -> {
         return var1.hasProperty(LIT) && !(Boolean)var0.getValue(LIT);
      });
   }

   static {
      LIT = AbstractCandleBlock.LIT;
      CAKE_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 8.0, 15.0);
      CANDLE_SHAPE = Block.box(7.0, 8.0, 7.0, 9.0, 14.0, 9.0);
      SHAPE = Shapes.or(CAKE_SHAPE, CANDLE_SHAPE);
      BY_CANDLE = Maps.newHashMap();
      PARTICLE_OFFSETS = ImmutableList.of(new Vec3(0.5, 1.0, 0.5));
   }
}
