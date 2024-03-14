package net.minecraft.world.level.block;

import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class MultifaceSpreader {
   public static final MultifaceSpreader.SpreadType[] DEFAULT_SPREAD_ORDER = new MultifaceSpreader.SpreadType[]{
      MultifaceSpreader.SpreadType.SAME_POSITION, MultifaceSpreader.SpreadType.SAME_PLANE, MultifaceSpreader.SpreadType.WRAP_AROUND
   };
   private final MultifaceSpreader.SpreadConfig config;

   public MultifaceSpreader(MultifaceBlock var1) {
      this(new MultifaceSpreader.DefaultSpreaderConfig(var1));
   }

   public MultifaceSpreader(MultifaceSpreader.SpreadConfig var1) {
      super();
      this.config = var1;
   }

   public boolean canSpreadInAnyDirection(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return Direction.stream().anyMatch(var5 -> this.getSpreadFromFaceTowardDirection(var1, var2, var3, var4, var5, this.config::canSpreadInto).isPresent());
   }

   public Optional<MultifaceSpreader.SpreadPos> spreadFromRandomFaceTowardRandomDirection(BlockState var1, LevelAccessor var2, BlockPos var3, RandomSource var4) {
      return Direction.allShuffled(var4)
         .stream()
         .filter(var2x -> this.config.canSpreadFrom(var1, var2x))
         .map(var5 -> this.spreadFromFaceTowardRandomDirection(var1, var2, var3, var5, var4, false))
         .filter(Optional::isPresent)
         .findFirst()
         .orElse(Optional.empty());
   }

   public long spreadAll(BlockState var1, LevelAccessor var2, BlockPos var3, boolean var4) {
      return Direction.stream()
         .filter(var2x -> this.config.canSpreadFrom(var1, var2x))
         .map(var5 -> this.spreadFromFaceTowardAllDirections(var1, var2, var3, var5, var4))
         .reduce(0L, Long::sum);
   }

   public Optional<MultifaceSpreader.SpreadPos> spreadFromFaceTowardRandomDirection(
      BlockState var1, LevelAccessor var2, BlockPos var3, Direction var4, RandomSource var5, boolean var6
   ) {
      return Direction.allShuffled(var5)
         .stream()
         .map(var6x -> this.spreadFromFaceTowardDirection(var1, var2, var3, var4, var6x, var6))
         .filter(Optional::isPresent)
         .findFirst()
         .orElse(Optional.empty());
   }

   private long spreadFromFaceTowardAllDirections(BlockState var1, LevelAccessor var2, BlockPos var3, Direction var4, boolean var5) {
      return Direction.stream().map(var6 -> this.spreadFromFaceTowardDirection(var1, var2, var3, var4, var6, var5)).filter(Optional::isPresent).count();
   }

   @VisibleForTesting
   public Optional<MultifaceSpreader.SpreadPos> spreadFromFaceTowardDirection(
      BlockState var1, LevelAccessor var2, BlockPos var3, Direction var4, Direction var5, boolean var6
   ) {
      return this.getSpreadFromFaceTowardDirection(var1, var2, var3, var4, var5, this.config::canSpreadInto)
         .flatMap(var3x -> this.spreadToFace(var2, var3x, var6));
   }

   public Optional<MultifaceSpreader.SpreadPos> getSpreadFromFaceTowardDirection(
      BlockState var1, BlockGetter var2, BlockPos var3, Direction var4, Direction var5, MultifaceSpreader.SpreadPredicate var6
   ) {
      if (var5.getAxis() == var4.getAxis()) {
         return Optional.empty();
      } else if (this.config.isOtherBlockValidAsSource(var1) || this.config.hasFace(var1, var4) && !this.config.hasFace(var1, var5)) {
         for(MultifaceSpreader.SpreadType var10 : this.config.getSpreadTypes()) {
            MultifaceSpreader.SpreadPos var11 = var10.getSpreadPos(var3, var5, var4);
            if (var6.test(var2, var3, var11)) {
               return Optional.of(var11);
            }
         }

         return Optional.empty();
      } else {
         return Optional.empty();
      }
   }

   public Optional<MultifaceSpreader.SpreadPos> spreadToFace(LevelAccessor var1, MultifaceSpreader.SpreadPos var2, boolean var3) {
      BlockState var4 = var1.getBlockState(var2.pos());
      return this.config.placeBlock(var1, var2, var4, var3) ? Optional.of(var2) : Optional.empty();
   }

   public static class DefaultSpreaderConfig implements MultifaceSpreader.SpreadConfig {
      protected MultifaceBlock block;

      public DefaultSpreaderConfig(MultifaceBlock var1) {
         super();
         this.block = var1;
      }

      @Nullable
      @Override
      public BlockState getStateForPlacement(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
         return this.block.getStateForPlacement(var1, var2, var3, var4);
      }

      protected boolean stateCanBeReplaced(BlockGetter var1, BlockPos var2, BlockPos var3, Direction var4, BlockState var5) {
         return var5.isAir() || var5.is(this.block) || var5.is(Blocks.WATER) && var5.getFluidState().isSource();
      }

      @Override
      public boolean canSpreadInto(BlockGetter var1, BlockPos var2, MultifaceSpreader.SpreadPos var3) {
         BlockState var4 = var1.getBlockState(var3.pos());
         return this.stateCanBeReplaced(var1, var2, var3.pos(), var3.face(), var4) && this.block.isValidStateForPlacement(var1, var4, var3.pos(), var3.face());
      }
   }

   public interface SpreadConfig {
      @Nullable
      BlockState getStateForPlacement(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4);

      boolean canSpreadInto(BlockGetter var1, BlockPos var2, MultifaceSpreader.SpreadPos var3);

      default MultifaceSpreader.SpreadType[] getSpreadTypes() {
         return MultifaceSpreader.DEFAULT_SPREAD_ORDER;
      }

      default boolean hasFace(BlockState var1, Direction var2) {
         return MultifaceBlock.hasFace(var1, var2);
      }

      default boolean isOtherBlockValidAsSource(BlockState var1) {
         return false;
      }

      default boolean canSpreadFrom(BlockState var1, Direction var2) {
         return this.isOtherBlockValidAsSource(var1) || this.hasFace(var1, var2);
      }

      default boolean placeBlock(LevelAccessor var1, MultifaceSpreader.SpreadPos var2, BlockState var3, boolean var4) {
         BlockState var5 = this.getStateForPlacement(var3, var1, var2.pos(), var2.face());
         if (var5 != null) {
            if (var4) {
               var1.getChunk(var2.pos()).markPosForPostprocessing(var2.pos());
            }

            return var1.setBlock(var2.pos(), var5, 2);
         } else {
            return false;
         }
      }
   }

   public static record SpreadPos(BlockPos a, Direction b) {
      private final BlockPos pos;
      private final Direction face;

      public SpreadPos(BlockPos var1, Direction var2) {
         super();
         this.pos = var1;
         this.face = var2;
      }
   }

   @FunctionalInterface
   public interface SpreadPredicate {
      boolean test(BlockGetter var1, BlockPos var2, MultifaceSpreader.SpreadPos var3);
   }

   public static enum SpreadType {
      SAME_POSITION {
         @Override
         public MultifaceSpreader.SpreadPos getSpreadPos(BlockPos var1, Direction var2, Direction var3) {
            return new MultifaceSpreader.SpreadPos(var1, var2);
         }
      },
      SAME_PLANE {
         @Override
         public MultifaceSpreader.SpreadPos getSpreadPos(BlockPos var1, Direction var2, Direction var3) {
            return new MultifaceSpreader.SpreadPos(var1.relative(var2), var3);
         }
      },
      WRAP_AROUND {
         @Override
         public MultifaceSpreader.SpreadPos getSpreadPos(BlockPos var1, Direction var2, Direction var3) {
            return new MultifaceSpreader.SpreadPos(var1.relative(var2).relative(var3), var2.getOpposite());
         }
      };

      SpreadType() {
      }

      public abstract MultifaceSpreader.SpreadPos getSpreadPos(BlockPos var1, Direction var2, Direction var3);
   }
}
