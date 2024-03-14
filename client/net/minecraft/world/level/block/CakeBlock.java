package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CakeBlock extends Block {
   public static final MapCodec<CakeBlock> CODEC = simpleCodec(CakeBlock::new);
   public static final int MAX_BITES = 6;
   public static final IntegerProperty BITES = BlockStateProperties.BITES;
   public static final int FULL_CAKE_SIGNAL = getOutputSignal(0);
   protected static final float AABB_OFFSET = 1.0F;
   protected static final float AABB_SIZE_PER_BITE = 2.0F;
   protected static final VoxelShape[] SHAPE_BY_BITE = new VoxelShape[]{
      Block.box(1.0, 0.0, 1.0, 15.0, 8.0, 15.0),
      Block.box(3.0, 0.0, 1.0, 15.0, 8.0, 15.0),
      Block.box(5.0, 0.0, 1.0, 15.0, 8.0, 15.0),
      Block.box(7.0, 0.0, 1.0, 15.0, 8.0, 15.0),
      Block.box(9.0, 0.0, 1.0, 15.0, 8.0, 15.0),
      Block.box(11.0, 0.0, 1.0, 15.0, 8.0, 15.0),
      Block.box(13.0, 0.0, 1.0, 15.0, 8.0, 15.0)
   };

   @Override
   public MapCodec<CakeBlock> codec() {
      return CODEC;
   }

   protected CakeBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(BITES, Integer.valueOf(0)));
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE_BY_BITE[var1.getValue(BITES)];
   }

   @Override
   protected ItemInteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      Item var8 = var1.getItem();
      if (var1.is(ItemTags.CANDLES) && var2.getValue(BITES) == 0) {
         Block var10 = Block.byItem(var8);
         if (var10 instanceof CandleBlock var9) {
            if (!var5.isCreative()) {
               var1.shrink(1);
            }

            var3.playSound(null, var4, SoundEvents.CAKE_ADD_CANDLE, SoundSource.BLOCKS, 1.0F, 1.0F);
            var3.setBlockAndUpdate(var4, CandleCakeBlock.byCandle((CandleBlock)var9));
            var3.gameEvent(var5, GameEvent.BLOCK_CHANGE, var4);
            var5.awardStat(Stats.ITEM_USED.get(var8));
            return ItemInteractionResult.SUCCESS;
         }
      }

      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (var2.isClientSide) {
         if (eat(var2, var3, var1, var4).consumesAction()) {
            return InteractionResult.SUCCESS;
         }

         if (var4.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            return InteractionResult.CONSUME;
         }
      }

      return eat(var2, var3, var1, var4);
   }

   protected static InteractionResult eat(LevelAccessor var0, BlockPos var1, BlockState var2, Player var3) {
      if (!var3.canEat(false)) {
         return InteractionResult.PASS;
      } else {
         var3.awardStat(Stats.EAT_CAKE_SLICE);
         var3.getFoodData().eat(2, 0.1F);
         int var4 = var2.getValue(BITES);
         var0.gameEvent(var3, GameEvent.EAT, var1);
         if (var4 < 6) {
            var0.setBlock(var1, var2.setValue(BITES, Integer.valueOf(var4 + 1)), 3);
         } else {
            var0.removeBlock(var1, false);
            var0.gameEvent(var3, GameEvent.BLOCK_DESTROY, var1);
         }

         return InteractionResult.SUCCESS;
      }
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var2 == Direction.DOWN && !var1.canSurvive(var4, var5) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return var2.getBlockState(var3.below()).isSolid();
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(BITES);
   }

   @Override
   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return getOutputSignal(var1.getValue(BITES));
   }

   public static int getOutputSignal(int var0) {
      return (7 - var0) * 2;
   }

   @Override
   protected boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   @Override
   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }
}
