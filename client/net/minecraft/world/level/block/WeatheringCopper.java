package net.minecraft.world.level.block;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;

public interface WeatheringCopper extends ChangeOverTimeBlock<WeatherState> {
   Supplier<BiMap<Block, Block>> NEXT_BY_BLOCK = Suppliers.memoize(() -> ImmutableBiMap.builder().put(Blocks.COPPER_BLOCK, Blocks.EXPOSED_COPPER).put(Blocks.EXPOSED_COPPER, Blocks.WEATHERED_COPPER).put(Blocks.WEATHERED_COPPER, Blocks.OXIDIZED_COPPER).put(Blocks.CUT_COPPER, Blocks.EXPOSED_CUT_COPPER).put(Blocks.EXPOSED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER).put(Blocks.WEATHERED_CUT_COPPER, Blocks.OXIDIZED_CUT_COPPER).put(Blocks.CHISELED_COPPER, Blocks.EXPOSED_CHISELED_COPPER).put(Blocks.EXPOSED_CHISELED_COPPER, Blocks.WEATHERED_CHISELED_COPPER).put(Blocks.WEATHERED_CHISELED_COPPER, Blocks.OXIDIZED_CHISELED_COPPER).put(Blocks.CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER_SLAB).put(Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER_SLAB).put(Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER_SLAB).put(Blocks.CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER_STAIRS).put(Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER_STAIRS).put(Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_CUT_COPPER_STAIRS).put(Blocks.COPPER_DOOR, Blocks.EXPOSED_COPPER_DOOR).put(Blocks.EXPOSED_COPPER_DOOR, Blocks.WEATHERED_COPPER_DOOR).put(Blocks.WEATHERED_COPPER_DOOR, Blocks.OXIDIZED_COPPER_DOOR).put(Blocks.COPPER_TRAPDOOR, Blocks.EXPOSED_COPPER_TRAPDOOR).put(Blocks.EXPOSED_COPPER_TRAPDOOR, Blocks.WEATHERED_COPPER_TRAPDOOR).put(Blocks.WEATHERED_COPPER_TRAPDOOR, Blocks.OXIDIZED_COPPER_TRAPDOOR).putAll(Blocks.COPPER_BARS.weatheringMapping()).put(Blocks.COPPER_GRATE, Blocks.EXPOSED_COPPER_GRATE).put(Blocks.EXPOSED_COPPER_GRATE, Blocks.WEATHERED_COPPER_GRATE).put(Blocks.WEATHERED_COPPER_GRATE, Blocks.OXIDIZED_COPPER_GRATE).put(Blocks.COPPER_BULB, Blocks.EXPOSED_COPPER_BULB).put(Blocks.EXPOSED_COPPER_BULB, Blocks.WEATHERED_COPPER_BULB).put(Blocks.WEATHERED_COPPER_BULB, Blocks.OXIDIZED_COPPER_BULB).putAll(Blocks.COPPER_LANTERN.weatheringMapping()).put(Blocks.COPPER_CHEST, Blocks.EXPOSED_COPPER_CHEST).put(Blocks.EXPOSED_COPPER_CHEST, Blocks.WEATHERED_COPPER_CHEST).put(Blocks.WEATHERED_COPPER_CHEST, Blocks.OXIDIZED_COPPER_CHEST).put(Blocks.COPPER_GOLEM_STATUE, Blocks.EXPOSED_COPPER_GOLEM_STATUE).put(Blocks.EXPOSED_COPPER_GOLEM_STATUE, Blocks.WEATHERED_COPPER_GOLEM_STATUE).put(Blocks.WEATHERED_COPPER_GOLEM_STATUE, Blocks.OXIDIZED_COPPER_GOLEM_STATUE).put(Blocks.LIGHTNING_ROD, Blocks.EXPOSED_LIGHTNING_ROD).put(Blocks.EXPOSED_LIGHTNING_ROD, Blocks.WEATHERED_LIGHTNING_ROD).put(Blocks.WEATHERED_LIGHTNING_ROD, Blocks.OXIDIZED_LIGHTNING_ROD).putAll(Blocks.COPPER_CHAIN.weatheringMapping()).build());
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
