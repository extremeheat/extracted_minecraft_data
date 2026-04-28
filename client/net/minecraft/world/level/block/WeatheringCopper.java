package net.minecraft.world.level.block;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;

public interface WeatheringCopper extends ChangeOverTimeBlock<WeatherState> {
   Supplier<BiMap<Block, Block>> NEXT_BY_BLOCK = Suppliers.memoize(() -> {
      ImmutableBiMap.Builder<Block, Block> builder = ImmutableBiMap.builder();
      Stream.of(Blocks.COPPER_BLOCK, Blocks.CUT_COPPER, Blocks.CHISELED_COPPER, Blocks.CUT_COPPER_SLAB, Blocks.CUT_COPPER_STAIRS, Blocks.COPPER_DOOR, Blocks.COPPER_TRAPDOOR, Blocks.COPPER_BARS, Blocks.COPPER_GRATE, Blocks.COPPER_BULB, Blocks.COPPER_LANTERN, Blocks.COPPER_CHEST, Blocks.COPPER_GOLEM_STATUE, Blocks.LIGHTNING_ROD, Blocks.COPPER_CHAIN).forEach((collection) -> {
         WeatheringCopperCollection.ByState var10000 = collection.weathering();
         Objects.requireNonNull(builder);
         var10000.progressMapping(builder::put);
      });
      return builder.build();
   });
   Supplier<BiMap<Block, Block>> PREVIOUS_BY_BLOCK = Suppliers.memoize(() -> ((BiMap)NEXT_BY_BLOCK.get()).inverse());

   static Optional<Block> getPrevious(final Block block) {
      return Optional.ofNullable((Block)((BiMap)PREVIOUS_BY_BLOCK.get()).get(block));
   }

   static Block getFirst(final Block block) {
      Block candiate = block;

      for(Block previous = (Block)((BiMap)PREVIOUS_BY_BLOCK.get()).get(block); previous != null; previous = (Block)((BiMap)PREVIOUS_BY_BLOCK.get()).get(previous)) {
         candiate = previous;
      }

      return candiate;
   }

   static Optional<BlockState> getPrevious(final BlockState state) {
      return getPrevious(state.getBlock()).map((s) -> s.withPropertiesOf(state));
   }

   static Optional<Block> getNext(final Block block) {
      return Optional.ofNullable((Block)((BiMap)NEXT_BY_BLOCK.get()).get(block));
   }

   static BlockState getFirst(final BlockState state) {
      return getFirst(state.getBlock()).withPropertiesOf(state);
   }

   default Optional<BlockState> getNext(final BlockState state) {
      return getNext(state.getBlock()).map((s) -> s.withPropertiesOf(state));
   }

   default float getChanceModifier() {
      return this.getAge() == WeatheringCopper.WeatherState.UNAFFECTED ? 0.75F : 1.0F;
   }

   public static enum WeatherState implements StringRepresentable {
      UNAFFECTED("unaffected"),
      EXPOSED("exposed"),
      WEATHERED("weathered"),
      OXIDIZED("oxidized");

      public static final IntFunction<WeatherState> BY_ID = ByIdMap.<WeatherState>continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.CLAMP);
      public static final Codec<WeatherState> CODEC = StringRepresentable.<WeatherState>fromEnum(WeatherState::values);
      public static final StreamCodec<ByteBuf, WeatherState> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);
      private final String name;

      private WeatherState(final String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      public static void forEach(final Consumer<WeatherState> consumer) {
         for(WeatherState weatherState : values()) {
            consumer.accept(weatherState);
         }

      }

      public WeatherState next() {
         return (WeatherState)BY_ID.apply(this.ordinal() + 1);
      }

      public WeatherState previous() {
         return (WeatherState)BY_ID.apply(this.ordinal() - 1);
      }

      // $FF: synthetic method
      private static WeatherState[] $values() {
         return new WeatherState[]{UNAFFECTED, EXPOSED, WEATHERED, OXIDIZED};
      }
   }
}
