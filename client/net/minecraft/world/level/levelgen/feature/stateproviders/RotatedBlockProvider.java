package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public record RotatedBlockProvider(BlockStateProvider state, Optional<Direction> direction) implements BlockStateProvider {
   public static final MapCodec<RotatedBlockProvider> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(BlockStateProvider.CODEC.fieldOf("state").forGetter(RotatedBlockProvider::state), Direction.CODEC.optionalFieldOf("direction").forGetter(RotatedBlockProvider::direction)).apply(i, RotatedBlockProvider::new));

   public RotatedBlockProvider(final BlockStateProvider state) {
      this(state, Optional.empty());
   }

   public RotatedBlockProvider {
      super();
   }

   public MapCodec<RotatedBlockProvider> codec() {
      return CODEC;
   }

   public BlockState getState(final LevelAccessor level, final RandomSource random, final BlockPos pos) {
      Direction direction = (Direction)this.direction.orElseGet(() -> Direction.getRandom(random));
      BlockState newState = (BlockState)((BlockState)this.state.getState(level, random, pos).trySetValue(BlockStateProperties.AXIS, direction.getAxis())).trySetValue(BlockStateProperties.FACING, direction);
      return direction.getAxis().isHorizontal() ? (BlockState)newState.trySetValue(BlockStateProperties.HORIZONTAL_FACING, direction) : newState;
   }
}
