package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.function.ToIntFunction;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CandleBlock extends AbstractCandleBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<CandleBlock> CODEC = simpleCodec(CandleBlock::new);
   public static final int MIN_CANDLES = 1;
   public static final int MAX_CANDLES = 4;
   public static final IntegerProperty CANDLES;
   public static final BooleanProperty LIT;
   public static final BooleanProperty WATERLOGGED;
   public static final ToIntFunction<BlockState> LIGHT_EMISSION;
   private static final Int2ObjectMap<List<Vec3>> PARTICLE_OFFSETS;
   private static final VoxelShape ONE_AABB;
   private static final VoxelShape TWO_AABB;
   private static final VoxelShape THREE_AABB;
   private static final VoxelShape FOUR_AABB;

   public MapCodec<CandleBlock> codec() {
      return CODEC;
   }

   public CandleBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(CANDLES, 1)).setValue(LIT, false)).setValue(WATERLOGGED, false));
   }

   protected InteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      if (var1.isEmpty() && var5.getAbilities().mayBuild && (Boolean)var2.getValue(LIT)) {
         extinguish(var5, var2, var3, var4);
         return InteractionResult.SUCCESS;
      } else {
         return super.useItemOn(var1, var2, var3, var4, var5, var6, var7);
      }
   }

   protected boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      return !var2.isSecondaryUseActive() && var2.getItemInHand().getItem() == this.asItem() && (Integer)var1.getValue(CANDLES) < 4 ? true : super.canBeReplaced(var1, var2);
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = var1.getLevel().getBlockState(var1.getClickedPos());
      if (var2.is(this)) {
         return (BlockState)var2.cycle(CANDLES);
      } else {
         FluidState var3 = var1.getLevel().getFluidState(var1.getClickedPos());
         boolean var4 = var3.getType() == Fluids.WATER;
         return (BlockState)super.getStateForPlacement(var1).setValue(WATERLOGGED, var4);
      }
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var3.scheduleTick(var4, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var2));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   protected FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      switch ((Integer)var1.getValue(CANDLES)) {
         case 1:
         default:
            return ONE_AABB;
         case 2:
            return TWO_AABB;
         case 3:
            return THREE_AABB;
         case 4:
            return FOUR_AABB;
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(CANDLES, LIT, WATERLOGGED);
   }

   public boolean placeLiquid(LevelAccessor var1, BlockPos var2, BlockState var3, FluidState var4) {
      if (!(Boolean)var3.getValue(WATERLOGGED) && var4.getType() == Fluids.WATER) {
         BlockState var5 = (BlockState)var3.setValue(WATERLOGGED, true);
         if ((Boolean)var3.getValue(LIT)) {
            extinguish((Player)null, var5, var1, var2);
         } else {
            var1.setBlock(var2, var5, 3);
         }

         var1.scheduleTick(var2, var4.getType(), var4.getType().getTickDelay(var1));
         return true;
      } else {
         return false;
      }
   }

   public static boolean canLight(BlockState var0) {
      return var0.is(BlockTags.CANDLES, (var0x) -> var0x.hasProperty(LIT) && var0x.hasProperty(WATERLOGGED)) && !(Boolean)var0.getValue(LIT) && !(Boolean)var0.getValue(WATERLOGGED);
   }

   protected Iterable<Vec3> getParticleOffsets(BlockState var1) {
      return (Iterable)PARTICLE_OFFSETS.get((Integer)var1.getValue(CANDLES));
   }

   protected boolean canBeLit(BlockState var1) {
      return !(Boolean)var1.getValue(WATERLOGGED) && super.canBeLit(var1);
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return Block.canSupportCenter(var2, var3.below(), Direction.UP);
   }

   static {
      CANDLES = BlockStateProperties.CANDLES;
      LIT = AbstractCandleBlock.LIT;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      LIGHT_EMISSION = (var0) -> (Boolean)var0.getValue(LIT) ? 3 * (Integer)var0.getValue(CANDLES) : 0;
      PARTICLE_OFFSETS = (Int2ObjectMap)Util.make(() -> {
         Int2ObjectOpenHashMap var0 = new Int2ObjectOpenHashMap();
         var0.defaultReturnValue(ImmutableList.of());
         var0.put(1, ImmutableList.of(new Vec3(0.5, 0.5, 0.5)));
         var0.put(2, ImmutableList.of(new Vec3(0.375, 0.44, 0.5), new Vec3(0.625, 0.5, 0.44)));
         var0.put(3, ImmutableList.of(new Vec3(0.5, 0.313, 0.625), new Vec3(0.375, 0.44, 0.5), new Vec3(0.56, 0.5, 0.44)));
         var0.put(4, ImmutableList.of(new Vec3(0.44, 0.313, 0.56), new Vec3(0.625, 0.44, 0.56), new Vec3(0.375, 0.44, 0.375), new Vec3(0.56, 0.5, 0.375)));
         return Int2ObjectMaps.unmodifiable(var0);
      });
      ONE_AABB = Block.box(7.0, 0.0, 7.0, 9.0, 6.0, 9.0);
      TWO_AABB = Block.box(5.0, 0.0, 6.0, 11.0, 6.0, 9.0);
      THREE_AABB = Block.box(5.0, 0.0, 6.0, 10.0, 6.0, 11.0);
      FOUR_AABB = Block.box(5.0, 0.0, 5.0, 11.0, 6.0, 10.0);
   }
}
