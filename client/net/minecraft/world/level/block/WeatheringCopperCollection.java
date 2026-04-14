package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.apache.commons.lang3.function.TriFunction;

public record WeatheringCopperCollection<T>(ByState<T> weathering, ByState<T> waxed) {
   public static final ByState<WeatheringCopper.WeatherState> STATES;
   public static final WeatheringCopperCollection<String> PREFIXES;

   public WeatheringCopperCollection {
      super();
   }

   public static WeatheringCopperCollection<String> prefixWithState(final WeatheringCopperCollection<String> ids) {
      return zipMap(PREFIXES, ids, (state, id) -> state + id);
   }

   public static WeatheringCopperCollection<String> create(final String name) {
      return same(WeatheringCopperCollection.ByState.create(name));
   }

   public static WeatheringCopperCollection<String> same(final ByState<String> byState) {
      return new WeatheringCopperCollection<String>(byState, byState);
   }

   public static <WaxedBlock extends Block, WeatheringBlock extends Block & WeatheringCopper, Id> WeatheringCopperCollection<Block> registerBlocks(final WeatheringCopperCollection<Id> ids, final TriFunction<Id, Function<BlockBehaviour.Properties, Block>, BlockBehaviour.Properties, Block> register, final BiFunction<WeatheringCopper.WeatherState, BlockBehaviour.Properties, WaxedBlock> waxedBlockFactory, final BiFunction<WeatheringCopper.WeatherState, BlockBehaviour.Properties, WeatheringBlock> weatheringFactory, final Function<WeatheringCopper.WeatherState, BlockBehaviour.Properties> propertiesSupplier) {
      return ids.<Block>apply((weatheringIds) -> zipMap(STATES, weatheringIds, (state, id) -> (Block)register.apply(id, (Function)(p) -> (Block)weatheringFactory.apply(state, p), (BlockBehaviour.Properties)propertiesSupplier.apply(state))), (waxedIds) -> zipMap(STATES, waxedIds, (state, id) -> (Block)register.apply(id, (Function)(p) -> (Block)waxedBlockFactory.apply(state, p), (BlockBehaviour.Properties)propertiesSupplier.apply(state))));
   }

   public static <Id> WeatheringCopperCollection<Item> registerItems(final WeatheringCopperCollection<Id> ids, final WeatheringCopperCollection<Block> blocks, final BiFunction<Id, Block, Item> itemFactory) {
      return zipMap(ids, blocks, itemFactory);
   }

   public static WeatheringCopperCollection<BlockFamily> createFamily(final BiFunction<String, WeatheringCopper.WeatherState, BlockFamily> waxedProvider, final BiFunction<String, WeatheringCopper.WeatherState, BlockFamily> weatheringProvider) {
      return PREFIXES.<BlockFamily>apply((weatheringPrefixes) -> zipMap(weatheringPrefixes, STATES, weatheringProvider), (waxedPrefixes) -> zipMap(waxedPrefixes, STATES, waxedProvider));
   }

   public List<T> asList() {
      ImmutableList.Builder<T> builder = ImmutableList.builderWithExpectedSize(8);
      Objects.requireNonNull(builder);
      this.forEach(builder::add);
      return builder.build();
   }

   public void forEach(final Consumer<T> consumer) {
      this.weathering.forEach(consumer);
      this.waxed.forEach(consumer);
   }

   public <U> WeatheringCopperCollection<U> map(final Function<T, U> mapper) {
      return new WeatheringCopperCollection<U>(this.weathering.map(mapper), this.waxed.map(mapper));
   }

   public <U> WeatheringCopperCollection<U> apply(final Function<ByState<T>, ByState<U>> mapper) {
      return this.<U>apply(mapper, mapper);
   }

   public <U> WeatheringCopperCollection<U> apply(final Function<ByState<T>, ByState<U>> weatheringMapper, final Function<ByState<T>, ByState<U>> waxedMapper) {
      return new WeatheringCopperCollection<U>((ByState)weatheringMapper.apply(this.weathering), (ByState)waxedMapper.apply(this.waxed));
   }

   public static <T, U> void zipApply(final WeatheringCopperCollection<T> first, final WeatheringCopperCollection<U> second, final BiConsumer<T, U> consumer) {
      zipApply(first.weathering, second.weathering, consumer);
      zipApply(first.waxed, second.waxed, consumer);
   }

   public static <T, U, R> WeatheringCopperCollection<R> zipMap(final WeatheringCopperCollection<T> first, final WeatheringCopperCollection<U> second, final BiFunction<T, U, R> operation) {
      return new WeatheringCopperCollection<R>(zipMap(first.weathering, second.weathering, operation), zipMap(first.waxed, second.waxed, operation));
   }

   public void zipUnwaxedWaxed(final BiConsumer<T, T> consumer) {
      zipApply(this.weathering, this.waxed, consumer);
   }

   public static <T, U> void zipApply(final ByState<T> first, final ByState<U> second, final BiConsumer<T, U> consumer) {
      consumer.accept(first.unaffected, second.unaffected);
      consumer.accept(first.exposed, second.exposed);
      consumer.accept(first.weathered, second.weathered);
      consumer.accept(first.oxidized, second.oxidized);
   }

   public static <T, U, R> ByState<R> zipMap(final ByState<T> first, final ByState<U> second, final BiFunction<T, U, R> operation) {
      return new ByState<R>(operation.apply(first.unaffected, second.unaffected), operation.apply(first.exposed, second.exposed), operation.apply(first.weathered, second.weathered), operation.apply(first.oxidized, second.oxidized));
   }

   public void progressMapping(final BiConsumer<T, T> consumer) {
      this.weathering.progressMapping(consumer);
      this.waxed.progressMapping(consumer);
   }

   static {
      STATES = new ByState<WeatheringCopper.WeatherState>(WeatheringCopper.WeatherState.UNAFFECTED, WeatheringCopper.WeatherState.EXPOSED, WeatheringCopper.WeatherState.WEATHERED, WeatheringCopper.WeatherState.OXIDIZED);
      PREFIXES = new WeatheringCopperCollection<String>(new ByState("", "exposed_", "weathered_", "oxidized_"), new ByState("waxed_", "waxed_exposed_", "waxed_weathered_", "waxed_oxidized_"));
   }

   public static record ByState<T>(T unaffected, T exposed, T weathered, T oxidized) {
      public ByState {
         super();
      }

      public static <T> ByState<T> create(final T value) {
         return new ByState<T>(value, value, value, value);
      }

      public <U> ByState<U> map(final Function<T, U> mapper) {
         return new ByState<U>(mapper.apply(this.unaffected), mapper.apply(this.exposed), mapper.apply(this.weathered), mapper.apply(this.oxidized));
      }

      public T pick(final WeatheringCopper.WeatherState state) {
         Object var10000;
         switch (state) {
            case UNAFFECTED -> var10000 = this.unaffected;
            case EXPOSED -> var10000 = this.exposed;
            case WEATHERED -> var10000 = this.weathered;
            case OXIDIZED -> var10000 = this.oxidized;
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return (T)var10000;
      }

      public void forEach(final Consumer<T> consumer) {
         consumer.accept(this.unaffected);
         consumer.accept(this.exposed);
         consumer.accept(this.weathered);
         consumer.accept(this.oxidized);
      }

      public void progressMapping(final BiConsumer<T, T> consumer) {
         consumer.accept(this.unaffected, this.exposed);
         consumer.accept(this.exposed, this.weathered);
         consumer.accept(this.weathered, this.oxidized);
      }
   }
}
