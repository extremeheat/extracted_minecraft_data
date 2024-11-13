package net.minecraft.world.level.block;

import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class MultifaceSpreader {
   public static final SpreadType[] DEFAULT_SPREAD_ORDER;
   private final SpreadConfig config;

   public MultifaceSpreader(MultifaceBlock var1) {
      this((SpreadConfig)(new DefaultSpreaderConfig(var1)));
   }

   public MultifaceSpreader(SpreadConfig var1) {
      super();
      this.config = var1;
   }

   public boolean canSpreadInAnyDirection(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return Direction.stream().anyMatch((var5) -> {
         SpreadConfig var10006 = this.config;
         Objects.requireNonNull(var10006);
         return this.getSpreadFromFaceTowardDirection(var1, var2, var3, var4, var5, var10006::canSpreadInto).isPresent();
      });
   }

   public Optional<SpreadPos> spreadFromRandomFaceTowardRandomDirection(BlockState var1, LevelAccessor var2, BlockPos var3, RandomSource var4) {
      return (Optional)Direction.allShuffled(var4).stream().filter((var2x) -> this.config.canSpreadFrom(var1, var2x)).map((var5) -> this.spreadFromFaceTowardRandomDirection(var1, var2, var3, var5, var4, false)).filter(Optional::isPresent).findFirst().orElse(Optional.empty());
   }

   public long spreadAll(BlockState var1, LevelAccessor var2, BlockPos var3, boolean var4) {
      return (Long)Direction.stream().filter((var2x) -> this.config.canSpreadFrom(var1, var2x)).map((var5) -> this.spreadFromFaceTowardAllDirections(var1, var2, var3, var5, var4)).reduce(0L, Long::sum);
   }

   public Optional<SpreadPos> spreadFromFaceTowardRandomDirection(BlockState var1, LevelAccessor var2, BlockPos var3, Direction var4, RandomSource var5, boolean var6) {
      return (Optional)Direction.allShuffled(var5).stream().map((var6x) -> this.spreadFromFaceTowardDirection(var1, var2, var3, var4, var6x, var6)).filter(Optional::isPresent).findFirst().orElse(Optional.empty());
   }

   private long spreadFromFaceTowardAllDirections(BlockState var1, LevelAccessor var2, BlockPos var3, Direction var4, boolean var5) {
      return Direction.stream().map((var6) -> this.spreadFromFaceTowardDirection(var1, var2, var3, var4, var6, var5)).filter(Optional::isPresent).count();
   }

   @VisibleForTesting
   public Optional<SpreadPos> spreadFromFaceTowardDirection(BlockState var1, LevelAccessor var2, BlockPos var3, Direction var4, Direction var5, boolean var6) {
      SpreadConfig var10006 = this.config;
      Objects.requireNonNull(var10006);
      return this.getSpreadFromFaceTowardDirection(var1, var2, var3, var4, var5, var10006::canSpreadInto).flatMap((var3x) -> this.spreadToFace(var2, var3x, var6));
   }

   public Optional<SpreadPos> getSpreadFromFaceTowardDirection(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4, Direction var5, SpreadPredicate var6) {
      if (var5.getAxis() == var4.getAxis()) {
         return Optional.empty();
      } else if (this.config.isOtherBlockValidAsSource(var1) || this.config.hasFace(var1, var4) && !this.config.hasFace(var1, var5)) {
         for(SpreadType var10 : this.config.getSpreadTypes()) {
            SpreadPos var11 = var10.getSpreadPos(var3, var5, var4);
            if (var6.test(var2, var3, var11)) {
               return Optional.of(var11);
            }
         }

         return Optional.empty();
      } else {
         return Optional.empty();
      }
   }

   public Optional<SpreadPos> spreadToFace(LevelAccessor var1, SpreadPos var2, boolean var3) {
      BlockState var4 = var1.getBlockState(var2.pos());
      return this.config.placeBlock(var1, var2, var4, var3) ? Optional.of(var2) : Optional.empty();
   }

   static {
      DEFAULT_SPREAD_ORDER = new SpreadType[]{MultifaceSpreader.SpreadType.SAME_POSITION, MultifaceSpreader.SpreadType.SAME_PLANE, MultifaceSpreader.SpreadType.WRAP_AROUND};
   }

   public static record SpreadPos(BlockPos pos, Direction face) {
      public SpreadPos(BlockPos var1, Direction var2) {
         super();
         this.pos = var1;
         this.face = var2;
      }
   }

   public interface SpreadConfig {
      @Nullable
      BlockState getStateForPlacement(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4);

      boolean canSpreadInto(BlockGetter var1, BlockPos var2, SpreadPos var3);

      default SpreadType[] getSpreadTypes() {
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

      default boolean placeBlock(LevelAccessor var1, SpreadPos var2, BlockState var3, boolean var4) {
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

   public static class DefaultSpreaderConfig implements SpreadConfig {
      protected MultifaceBlock block;

      public DefaultSpreaderConfig(MultifaceBlock var1) {
         super();
         this.block = var1;
      }

      @Nullable
      public BlockState getStateForPlacement(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
         return this.block.getStateForPlacement(var1, var2, var3, var4);
      }

      protected boolean stateCanBeReplaced(BlockGetter var1, BlockPos var2, BlockPos var3, Direction var4, BlockState var5) {
         return var5.isAir() || var5.is(this.block) || var5.is(Blocks.WATER) && var5.getFluidState().isSource();
      }

      public boolean canSpreadInto(BlockGetter var1, BlockPos var2, SpreadPos var3) {
         BlockState var4 = var1.getBlockState(var3.pos());
         return this.stateCanBeReplaced(var1, var2, var3.pos(), var3.face(), var4) && this.block.isValidStateForPlacement(var1, var4, var3.pos(), var3.face());
      }
   }

   public static enum SpreadType {
      SAME_POSITION {
         public SpreadPos getSpreadPos(BlockPos var1, Direction var2, Direction var3) {
            return new SpreadPos(var1, var2);
         }
      },
      SAME_PLANE {
         public SpreadPos getSpreadPos(BlockPos var1, Direction var2, Direction var3) {
            return new SpreadPos(var1.relative(var2), var3);
         }
      },
      WRAP_AROUND {
         public SpreadPos getSpreadPos(BlockPos var1, Direction var2, Direction var3) {
            return new SpreadPos(var1.relative(var2).relative(var3), var2.getOpposite());
         }
      };

      SpreadType() {
      }

      public abstract SpreadPos getSpreadPos(BlockPos var1, Direction var2, Direction var3);

      // $FF: synthetic method
      private static SpreadType[] $values() {
         return new SpreadType[]{SAME_POSITION, SAME_PLANE, WRAP_AROUND};
      }
   }

   @FunctionalInterface
   public interface SpreadPredicate {
      boolean test(BlockGetter var1, BlockPos var2, SpreadPos var3);
   }
}
