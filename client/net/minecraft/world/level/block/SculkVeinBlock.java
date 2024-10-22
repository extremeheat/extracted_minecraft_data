package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Collection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class SculkVeinBlock extends MultifaceBlock implements SculkBehaviour, SimpleWaterloggedBlock {
   public static final MapCodec<SculkVeinBlock> CODEC = simpleCodec(SculkVeinBlock::new);
   private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   private final MultifaceSpreader veinSpreader = new MultifaceSpreader(new SculkVeinBlock.SculkVeinSpreaderConfig(MultifaceSpreader.DEFAULT_SPREAD_ORDER));
   private final MultifaceSpreader sameSpaceSpreader = new MultifaceSpreader(
      new SculkVeinBlock.SculkVeinSpreaderConfig(MultifaceSpreader.SpreadType.SAME_POSITION)
   );

   @Override
   public MapCodec<SculkVeinBlock> codec() {
      return CODEC;
   }

   public SculkVeinBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   @Override
   public MultifaceSpreader getSpreader() {
      return this.veinSpreader;
   }

   public MultifaceSpreader getSameSpaceSpreader() {
      return this.sameSpaceSpreader;
   }

   public static boolean regrow(LevelAccessor var0, BlockPos var1, BlockState var2, Collection<Direction> var3) {
      boolean var4 = false;
      BlockState var5 = Blocks.SCULK_VEIN.defaultBlockState();

      for (Direction var7 : var3) {
         BlockPos var8 = var1.relative(var7);
         if (canAttachTo(var0, var7, var8, var0.getBlockState(var8))) {
            var5 = var5.setValue(getFaceProperty(var7), Boolean.valueOf(true));
            var4 = true;
         }
      }

      if (!var4) {
         return false;
      } else {
         if (!var2.getFluidState().isEmpty()) {
            var5 = var5.setValue(WATERLOGGED, Boolean.valueOf(true));
         }

         var0.setBlock(var1, var5, 3);
         return true;
      }
   }

   @Override
   public void onDischarged(LevelAccessor var1, BlockState var2, BlockPos var3, RandomSource var4) {
      if (var2.is(this)) {
         for (Direction var8 : DIRECTIONS) {
            BooleanProperty var9 = getFaceProperty(var8);
            if (var2.getValue(var9) && var1.getBlockState(var3.relative(var8)).is(Blocks.SCULK)) {
               var2 = var2.setValue(var9, Boolean.valueOf(false));
            }
         }

         if (!hasAnyFace(var2)) {
            FluidState var10 = var1.getFluidState(var3);
            var2 = (var10.isEmpty() ? Blocks.AIR : Blocks.WATER).defaultBlockState();
         }

         var1.setBlock(var3, var2, 3);
         SculkBehaviour.super.onDischarged(var1, var2, var3, var4);
      }
   }

   @Override
   public int attemptUseCharge(SculkSpreader.ChargeCursor var1, LevelAccessor var2, BlockPos var3, RandomSource var4, SculkSpreader var5, boolean var6) {
      if (var6 && this.attemptPlaceSculk(var5, var2, var1.getPos(), var4)) {
         return var1.getCharge() - 1;
      } else {
         return var4.nextInt(var5.chargeDecayRate()) == 0 ? Mth.floor((float)var1.getCharge() * 0.5F) : var1.getCharge();
      }
   }

   private boolean attemptPlaceSculk(SculkSpreader var1, LevelAccessor var2, BlockPos var3, RandomSource var4) {
      BlockState var5 = var2.getBlockState(var3);
      TagKey var6 = var1.replaceableBlocks();

      for (Direction var8 : Direction.allShuffled(var4)) {
         if (hasFace(var5, var8)) {
            BlockPos var9 = var3.relative(var8);
            BlockState var10 = var2.getBlockState(var9);
            if (var10.is(var6)) {
               BlockState var11 = Blocks.SCULK.defaultBlockState();
               var2.setBlock(var9, var11, 3);
               Block.pushEntitiesUp(var10, var11, var2, var9);
               var2.playSound(null, var9, SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.BLOCKS, 1.0F, 1.0F);
               this.veinSpreader.spreadAll(var11, var2, var9, var1.isWorldGeneration());
               Direction var12 = var8.getOpposite();

               for (Direction var16 : DIRECTIONS) {
                  if (var16 != var12) {
                     BlockPos var17 = var9.relative(var16);
                     BlockState var18 = var2.getBlockState(var17);
                     if (var18.is(this)) {
                        this.onDischarged(var2, var18, var17, var4);
                     }
                  }
               }

               return true;
            }
         }
      }

      return false;
   }

   public static boolean hasSubstrateAccess(LevelAccessor var0, BlockState var1, BlockPos var2) {
      if (!var1.is(Blocks.SCULK_VEIN)) {
         return false;
      } else {
         for (Direction var6 : DIRECTIONS) {
            if (hasFace(var1, var6) && var0.getBlockState(var2.relative(var6)).is(BlockTags.SCULK_REPLACEABLE)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   protected BlockState updateShape(
      BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8
   ) {
      if (var1.getValue(WATERLOGGED)) {
         var3.scheduleTick(var4, Fluids.WATER, Fluids.WATER.getTickDelay(var2));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      super.createBlockStateDefinition(var1);
      var1.add(WATERLOGGED);
   }

   @Override
   protected boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      return !var2.getItemInHand().is(Items.SCULK_VEIN) || super.canBeReplaced(var1, var2);
   }

   @Override
   protected FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   class SculkVeinSpreaderConfig extends MultifaceSpreader.DefaultSpreaderConfig {
      private final MultifaceSpreader.SpreadType[] spreadTypes;

      public SculkVeinSpreaderConfig(final MultifaceSpreader.SpreadType... nullx) {
         super(SculkVeinBlock.this);
         this.spreadTypes = nullx;
      }

      @Override
      public boolean stateCanBeReplaced(BlockGetter var1, BlockPos var2, BlockPos var3, Direction var4, BlockState var5) {
         BlockState var6 = var1.getBlockState(var3.relative(var4));
         if (!var6.is(Blocks.SCULK) && !var6.is(Blocks.SCULK_CATALYST) && !var6.is(Blocks.MOVING_PISTON)) {
            if (var2.distManhattan(var3) == 2) {
               BlockPos var7 = var2.relative(var4.getOpposite());
               if (var1.getBlockState(var7).isFaceSturdy(var1, var7, var4)) {
                  return false;
               }
            }

            FluidState var8 = var5.getFluidState();
            if (!var8.isEmpty() && !var8.is(Fluids.WATER)) {
               return false;
            } else {
               return var5.is(BlockTags.FIRE) ? false : var5.canBeReplaced() || super.stateCanBeReplaced(var1, var2, var3, var4, var5);
            }
         } else {
            return false;
         }
      }

      @Override
      public MultifaceSpreader.SpreadType[] getSpreadTypes() {
         return this.spreadTypes;
      }

      @Override
      public boolean isOtherBlockValidAsSource(BlockState var1) {
         return !var1.is(Blocks.SCULK_VEIN);
      }
   }
}
