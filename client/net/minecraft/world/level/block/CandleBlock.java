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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
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
   public static final IntegerProperty CANDLES = BlockStateProperties.CANDLES;
   public static final BooleanProperty LIT = AbstractCandleBlock.LIT;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final ToIntFunction<BlockState> LIGHT_EMISSION = var0 -> var0.getValue(LIT) ? 3 * var0.getValue(CANDLES) : 0;
   private static final Int2ObjectMap<List<Vec3>> PARTICLE_OFFSETS = Util.make(() -> {
      Int2ObjectOpenHashMap var0 = new Int2ObjectOpenHashMap();
      var0.defaultReturnValue(ImmutableList.of());
      var0.put(1, ImmutableList.of(new Vec3(0.5, 0.5, 0.5)));
      var0.put(2, ImmutableList.of(new Vec3(0.375, 0.44, 0.5), new Vec3(0.625, 0.5, 0.44)));
      var0.put(3, ImmutableList.of(new Vec3(0.5, 0.313, 0.625), new Vec3(0.375, 0.44, 0.5), new Vec3(0.56, 0.5, 0.44)));
      var0.put(4, ImmutableList.of(new Vec3(0.44, 0.313, 0.56), new Vec3(0.625, 0.44, 0.56), new Vec3(0.375, 0.44, 0.375), new Vec3(0.56, 0.5, 0.375)));
      return Int2ObjectMaps.unmodifiable(var0);
   });
   private static final VoxelShape ONE_AABB = Block.box(7.0, 0.0, 7.0, 9.0, 6.0, 9.0);
   private static final VoxelShape TWO_AABB = Block.box(5.0, 0.0, 6.0, 11.0, 6.0, 9.0);
   private static final VoxelShape THREE_AABB = Block.box(5.0, 0.0, 6.0, 10.0, 6.0, 11.0);
   private static final VoxelShape FOUR_AABB = Block.box(5.0, 0.0, 5.0, 11.0, 6.0, 10.0);

   @Override
   public MapCodec<CandleBlock> codec() {
      return CODEC;
   }

   public CandleBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(
         this.stateDefinition.any().setValue(CANDLES, Integer.valueOf(1)).setValue(LIT, Boolean.valueOf(false)).setValue(WATERLOGGED, Boolean.valueOf(false))
      );
   }

   @Override
   protected ItemInteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      if (var1.isEmpty() && var5.getAbilities().mayBuild && var2.getValue(LIT)) {
         extinguish(var5, var2, var3, var4);
         return ItemInteractionResult.sidedSuccess(var3.isClientSide);
      } else {
         return super.useItemOn(var1, var2, var3, var4, var5, var6, var7);
      }
   }

   @Override
   protected boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      return !var2.isSecondaryUseActive() && var2.getItemInHand().getItem() == this.asItem() && var1.getValue(CANDLES) < 4
         ? true
         : super.canBeReplaced(var1, var2);
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = var1.getLevel().getBlockState(var1.getClickedPos());
      if (var2.is(this)) {
         return var2.cycle(CANDLES);
      } else {
         FluidState var3 = var1.getLevel().getFluidState(var1.getClickedPos());
         boolean var4 = var3.getType() == Fluids.WATER;
         return super.getStateForPlacement(var1).setValue(WATERLOGGED, Boolean.valueOf(var4));
      }
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   protected FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      switch(var1.getValue(CANDLES)) {
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

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(CANDLES, LIT, WATERLOGGED);
   }

   @Override
   public boolean placeLiquid(LevelAccessor var1, BlockPos var2, BlockState var3, FluidState var4) {
      if (!var3.getValue(WATERLOGGED) && var4.getType() == Fluids.WATER) {
         BlockState var5 = var3.setValue(WATERLOGGED, Boolean.valueOf(true));
         if (var3.getValue(LIT)) {
            extinguish(null, var5, var1, var2);
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
      return var0.is(BlockTags.CANDLES, var0x -> var0x.hasProperty(LIT) && var0x.hasProperty(WATERLOGGED))
         && !var0.getValue(LIT)
         && !var0.getValue(WATERLOGGED);
   }

   @Override
   protected Iterable<Vec3> getParticleOffsets(BlockState var1) {
      return (Iterable<Vec3>)PARTICLE_OFFSETS.get(var1.getValue(CANDLES));
   }

   @Override
   protected boolean canBeLit(BlockState var1) {
      return !var1.getValue(WATERLOGGED) && super.canBeLit(var1);
   }

   @Override
   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return Block.canSupportCenter(var2, var3.below(), Direction.UP);
   }
}
