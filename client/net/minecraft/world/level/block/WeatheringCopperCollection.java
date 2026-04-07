package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.apache.commons.lang3.function.TriConsumer;
import org.apache.commons.lang3.function.TriFunction;

public record WeatheringCopperCollection<T>(T unaffected, T exposed, T weathered, T oxidized, T waxed, T waxedExposed, T waxedWeathered, T waxedOxidized) {
   public WeatheringCopperCollection {
      super();
   }

   public static <WaxedBlock extends Block, WeatheringBlock extends Block & WeatheringCopper> WeatheringCopperCollection<Block> registerBlocks(final String id, final TriFunction<String, Function<BlockBehaviour.Properties, Block>, BlockBehaviour.Properties, Block> register, final BiFunction<WeatheringCopper.WeatherState, BlockBehaviour.Properties, WaxedBlock> waxedBlockFactory, final BiFunction<WeatheringCopper.WeatherState, BlockBehaviour.Properties, WeatheringBlock> weatheringFactory, final Function<WeatheringCopper.WeatherState, BlockBehaviour.Properties> propertiesSupplier) {
      return registerBlocks((BiFunction)((var1, var2) -> id), register, waxedBlockFactory, weatheringFactory, propertiesSupplier);
   }

   public static <WaxedBlock extends Block, WeatheringBlock extends Block & WeatheringCopper> WeatheringCopperCollection<Block> registerBlocks(final BiFunction<WeatheringCopper.WeatherState, Boolean, String> name, final TriFunction<String, Function<BlockBehaviour.Properties, Block>, BlockBehaviour.Properties, Block> register, final BiFunction<WeatheringCopper.WeatherState, BlockBehaviour.Properties, WaxedBlock> waxedBlockFactory, final BiFunction<WeatheringCopper.WeatherState, BlockBehaviour.Properties, WeatheringBlock> weatheringFactory, final Function<WeatheringCopper.WeatherState, BlockBehaviour.Properties> propertiesSupplier) {
      return new WeatheringCopperCollection<Block>(registerWeatheringBlock(WeatheringCopper.WeatherState.UNAFFECTED, false, name, register, waxedBlockFactory, weatheringFactory, propertiesSupplier), registerWeatheringBlock(WeatheringCopper.WeatherState.EXPOSED, false, name, register, waxedBlockFactory, weatheringFactory, propertiesSupplier), registerWeatheringBlock(WeatheringCopper.WeatherState.WEATHERED, false, name, register, waxedBlockFactory, weatheringFactory, propertiesSupplier), registerWeatheringBlock(WeatheringCopper.WeatherState.OXIDIZED, false, name, register, waxedBlockFactory, weatheringFactory, propertiesSupplier), registerWeatheringBlock(WeatheringCopper.WeatherState.UNAFFECTED, true, name, register, waxedBlockFactory, weatheringFactory, propertiesSupplier), registerWeatheringBlock(WeatheringCopper.WeatherState.EXPOSED, true, name, register, waxedBlockFactory, weatheringFactory, propertiesSupplier), registerWeatheringBlock(WeatheringCopper.WeatherState.WEATHERED, true, name, register, waxedBlockFactory, weatheringFactory, propertiesSupplier), registerWeatheringBlock(WeatheringCopper.WeatherState.OXIDIZED, true, name, register, waxedBlockFactory, weatheringFactory, propertiesSupplier));
   }

   private static <WaxedBlock extends Block, WeatheringBlock extends Block & WeatheringCopper> Block registerWeatheringBlock(final WeatheringCopper.WeatherState state, final Boolean isWaxed, final BiFunction<WeatheringCopper.WeatherState, Boolean, String> name, final TriFunction<String, Function<BlockBehaviour.Properties, Block>, BlockBehaviour.Properties, Block> register, final BiFunction<WeatheringCopper.WeatherState, BlockBehaviour.Properties, WaxedBlock> waxedBlockFactory, final BiFunction<WeatheringCopper.WeatherState, BlockBehaviour.Properties, WeatheringBlock> weatheringFactory, final Function<WeatheringCopper.WeatherState, BlockBehaviour.Properties> propertiesSupplier) {
      String id = (String)name.apply(state, isWaxed);
      return (Block)register.apply(prefixByState(state, isWaxed) + id, (Function)(p) -> (Block)(isWaxed ? waxedBlockFactory : weatheringFactory).apply(state, p), (BlockBehaviour.Properties)propertiesSupplier.apply(state));
   }

   private static String prefixByState(final WeatheringCopper.WeatherState state, final boolean isWaxed) {
      String var10000;
      switch (state) {
         case UNAFFECTED -> var10000 = isWaxed ? "waxed_" : "";
         case EXPOSED -> var10000 = isWaxed ? "waxed_exposed_" : "exposed_";
         case WEATHERED -> var10000 = isWaxed ? "waxed_weathered_" : "weathered_";
         case OXIDIZED -> var10000 = isWaxed ? "waxed_oxidized_" : "oxidized_";
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static WeatheringCopperCollection<Item> registerItems(final WeatheringCopperCollection<Block> blocks, final Function<Block, Item> itemFactory) {
      return new WeatheringCopperCollection<Item>((Item)itemFactory.apply(blocks.unaffected()), (Item)itemFactory.apply(blocks.exposed()), (Item)itemFactory.apply(blocks.weathered()), (Item)itemFactory.apply(blocks.oxidized()), (Item)itemFactory.apply(blocks.waxed()), (Item)itemFactory.apply(blocks.waxedExposed()), (Item)itemFactory.apply(blocks.waxedWeathered()), (Item)itemFactory.apply(blocks.waxedOxidized()));
   }

   public static WeatheringCopperCollection<BlockFamily> createFamily(final BiFunction<String, WeatheringCopper.WeatherState, BlockFamily> waxedProvider, final BiFunction<String, WeatheringCopper.WeatherState, BlockFamily> weatheringProvider) {
      return new WeatheringCopperCollection<BlockFamily>((BlockFamily)weatheringProvider.apply(prefixByState(WeatheringCopper.WeatherState.UNAFFECTED, false), WeatheringCopper.WeatherState.UNAFFECTED), (BlockFamily)weatheringProvider.apply(prefixByState(WeatheringCopper.WeatherState.EXPOSED, false), WeatheringCopper.WeatherState.EXPOSED), (BlockFamily)weatheringProvider.apply(prefixByState(WeatheringCopper.WeatherState.WEATHERED, false), WeatheringCopper.WeatherState.WEATHERED), (BlockFamily)weatheringProvider.apply(prefixByState(WeatheringCopper.WeatherState.OXIDIZED, false), WeatheringCopper.WeatherState.OXIDIZED), (BlockFamily)waxedProvider.apply(prefixByState(WeatheringCopper.WeatherState.UNAFFECTED, true), WeatheringCopper.WeatherState.UNAFFECTED), (BlockFamily)waxedProvider.apply(prefixByState(WeatheringCopper.WeatherState.EXPOSED, true), WeatheringCopper.WeatherState.EXPOSED), (BlockFamily)waxedProvider.apply(prefixByState(WeatheringCopper.WeatherState.WEATHERED, true), WeatheringCopper.WeatherState.WEATHERED), (BlockFamily)waxedProvider.apply(prefixByState(WeatheringCopper.WeatherState.OXIDIZED, true), WeatheringCopper.WeatherState.OXIDIZED));
   }

   public ImmutableBiMap<T, T> weatheringMapping() {
      return ImmutableBiMap.of(this.unaffected, this.exposed, this.exposed, this.weathered, this.weathered, this.oxidized);
   }

   public ImmutableBiMap<T, T> waxedMapping() {
      return ImmutableBiMap.of(this.unaffected, this.waxed, this.exposed, this.waxedExposed, this.weathered, this.waxedWeathered, this.oxidized, this.waxedOxidized);
   }

   public ImmutableList<T> asList() {
      return ImmutableList.of(this.unaffected, this.waxed, this.exposed, this.waxedExposed, this.weathered, this.waxedWeathered, this.oxidized, this.waxedOxidized);
   }

   public void forEach(final Consumer<T> consumer) {
      consumer.accept(this.unaffected);
      consumer.accept(this.exposed);
      consumer.accept(this.weathered);
      consumer.accept(this.oxidized);
      consumer.accept(this.waxed);
      consumer.accept(this.waxedExposed);
      consumer.accept(this.waxedWeathered);
      consumer.accept(this.waxedOxidized);
   }

   public T pick(final WeatheringCopper.WeatherState state, final boolean isWaxed) {
      Object var10000;
      switch (state) {
         case UNAFFECTED -> var10000 = isWaxed ? this.waxed : this.unaffected;
         case EXPOSED -> var10000 = isWaxed ? this.waxedExposed : this.exposed;
         case WEATHERED -> var10000 = isWaxed ? this.waxedWeathered : this.weathered;
         case OXIDIZED -> var10000 = isWaxed ? this.waxedOxidized : this.oxidized;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return (T)var10000;
   }

   public Collection<T> select(final BiPredicate<WeatheringCopper.WeatherState, Boolean> selector) {
      ImmutableList.Builder<T> builder = ImmutableList.builder();
      if (selector.test(WeatheringCopper.WeatherState.UNAFFECTED, false)) {
         builder.add(this.unaffected);
      }

      if (selector.test(WeatheringCopper.WeatherState.EXPOSED, false)) {
         builder.add(this.exposed);
      }

      if (selector.test(WeatheringCopper.WeatherState.WEATHERED, false)) {
         builder.add(this.weathered);
      }

      if (selector.test(WeatheringCopper.WeatherState.OXIDIZED, false)) {
         builder.add(this.oxidized);
      }

      if (selector.test(WeatheringCopper.WeatherState.UNAFFECTED, true)) {
         builder.add(this.waxed);
      }

      if (selector.test(WeatheringCopper.WeatherState.EXPOSED, true)) {
         builder.add(this.waxedExposed);
      }

      if (selector.test(WeatheringCopper.WeatherState.WEATHERED, true)) {
         builder.add(this.waxedWeathered);
      }

      if (selector.test(WeatheringCopper.WeatherState.OXIDIZED, true)) {
         builder.add(this.waxedOxidized);
      }

      return builder.build();
   }

   public static <T> void zipApply(final BiConsumer<T, T> consumer, final WeatheringCopperCollection<T> first, final WeatheringCopperCollection<T> second) {
      consumer.accept(first.unaffected, second.unaffected());
      consumer.accept(first.exposed, second.exposed());
      consumer.accept(first.weathered, second.weathered());
      consumer.accept(first.oxidized, second.oxidized());
      consumer.accept(first.waxed, second.waxed());
      consumer.accept(first.waxedExposed, second.waxedExposed());
      consumer.accept(first.waxedWeathered, second.waxedWeathered());
      consumer.accept(first.waxedOxidized, second.waxedOxidized());
   }

   public static <T> void zipApply(final TriConsumer<T, T, T> consumer, final WeatheringCopperCollection<T> first, final WeatheringCopperCollection<T> second, final WeatheringCopperCollection<T> third) {
      consumer.accept(first.unaffected, second.unaffected(), third.unaffected());
      consumer.accept(first.exposed, second.exposed(), third.exposed());
      consumer.accept(first.weathered, second.weathered(), third.weathered());
      consumer.accept(first.oxidized, second.oxidized(), third.oxidized());
      consumer.accept(first.waxed, second.waxed(), third.waxed());
      consumer.accept(first.waxedExposed, second.waxedExposed(), third.waxedExposed());
      consumer.accept(first.waxedWeathered, second.waxedWeathered(), third.waxedWeathered());
      consumer.accept(first.waxedOxidized, second.waxedOxidized(), third.waxedOxidized());
   }

   public static <T> void zipApply(final QuadConsumer<T, T, T, T> consumer, final WeatheringCopperCollection<T> first, final WeatheringCopperCollection<T> second, final WeatheringCopperCollection<T> third, final WeatheringCopperCollection<T> fourth) {
      consumer.accept(first.unaffected, second.unaffected(), third.unaffected(), fourth.unaffected());
      consumer.accept(first.exposed, second.exposed(), third.exposed(), fourth.exposed());
      consumer.accept(first.weathered, second.weathered(), third.weathered(), fourth.weathered());
      consumer.accept(first.oxidized, second.oxidized(), third.oxidized(), fourth.oxidized());
      consumer.accept(first.waxed, second.waxed(), third.waxed(), fourth.waxed());
      consumer.accept(first.waxedExposed, second.waxedExposed(), third.waxedExposed(), fourth.waxedExposed());
      consumer.accept(first.waxedWeathered, second.waxedWeathered(), third.waxedWeathered(), fourth.waxedWeathered());
      consumer.accept(first.waxedOxidized, second.waxedOxidized(), third.waxedOxidized(), fourth.waxedOxidized());
   }

   @FunctionalInterface
   public interface QuadConsumer<T, U, V, W> {
      void accept(T t, U u, V v, W w);

      default QuadConsumer<T, U, V, W> andThen(final QuadConsumer<? super T, ? super U, ? super V, ? super W> after) {
         Objects.requireNonNull(after);
         return (t, u, v, w) -> {
            this.accept(t, u, v, w);
            after.accept(t, u, v, w);
         };
      }
   }
}
