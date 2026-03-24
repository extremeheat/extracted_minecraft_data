package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.apache.commons.lang3.function.TriFunction;

public record WeatheringCopperBlocks(Block unaffected, Block exposed, Block weathered, Block oxidized, Block waxed, Block waxedExposed, Block waxedWeathered, Block waxedOxidized) {
   public WeatheringCopperBlocks {
      super();
   }

   public static <WaxedBlock extends Block, WeatheringBlock extends Block & WeatheringCopper> WeatheringCopperBlocks create(final String id, final TriFunction<String, Function<BlockBehaviour.Properties, Block>, BlockBehaviour.Properties, Block> register, final Function<BlockBehaviour.Properties, WaxedBlock> waxedBlockFactory, final BiFunction<WeatheringCopper.WeatherState, BlockBehaviour.Properties, WeatheringBlock> weatheringFactory, final Function<WeatheringCopper.WeatherState, BlockBehaviour.Properties> propertiesSupplier) {
      Block var10002 = (Block)register.apply(id, (Function)(p) -> (Block)weatheringFactory.apply(WeatheringCopper.WeatherState.UNAFFECTED, p), (BlockBehaviour.Properties)propertiesSupplier.apply(WeatheringCopper.WeatherState.UNAFFECTED));
      Block var10003 = (Block)register.apply("exposed_" + id, (Function)(p) -> (Block)weatheringFactory.apply(WeatheringCopper.WeatherState.EXPOSED, p), (BlockBehaviour.Properties)propertiesSupplier.apply(WeatheringCopper.WeatherState.EXPOSED));
      Block var10004 = (Block)register.apply("weathered_" + id, (Function)(p) -> (Block)weatheringFactory.apply(WeatheringCopper.WeatherState.WEATHERED, p), (BlockBehaviour.Properties)propertiesSupplier.apply(WeatheringCopper.WeatherState.WEATHERED));
      Block var10005 = (Block)register.apply("oxidized_" + id, (Function)(p) -> (Block)weatheringFactory.apply(WeatheringCopper.WeatherState.OXIDIZED, p), (BlockBehaviour.Properties)propertiesSupplier.apply(WeatheringCopper.WeatherState.OXIDIZED));
      String var10007 = "waxed_" + id;
      Objects.requireNonNull(waxedBlockFactory);
      Block var10006 = (Block)register.apply(var10007, waxedBlockFactory::apply, (BlockBehaviour.Properties)propertiesSupplier.apply(WeatheringCopper.WeatherState.UNAFFECTED));
      String var10008 = "waxed_exposed_" + id;
      Objects.requireNonNull(waxedBlockFactory);
      Block var5 = (Block)register.apply(var10008, waxedBlockFactory::apply, (BlockBehaviour.Properties)propertiesSupplier.apply(WeatheringCopper.WeatherState.EXPOSED));
      String var10009 = "waxed_weathered_" + id;
      Objects.requireNonNull(waxedBlockFactory);
      Block var6 = (Block)register.apply(var10009, waxedBlockFactory::apply, (BlockBehaviour.Properties)propertiesSupplier.apply(WeatheringCopper.WeatherState.WEATHERED));
      String var10010 = "waxed_oxidized_" + id;
      Objects.requireNonNull(waxedBlockFactory);
      return new WeatheringCopperBlocks(var10002, var10003, var10004, var10005, var10006, var5, var6, (Block)register.apply(var10010, waxedBlockFactory::apply, (BlockBehaviour.Properties)propertiesSupplier.apply(WeatheringCopper.WeatherState.OXIDIZED)));
   }

   public ImmutableBiMap<Block, Block> weatheringMapping() {
      return ImmutableBiMap.of(this.unaffected, this.exposed, this.exposed, this.weathered, this.weathered, this.oxidized);
   }

   public ImmutableBiMap<Block, Block> waxedMapping() {
      return ImmutableBiMap.of(this.unaffected, this.waxed, this.exposed, this.waxedExposed, this.weathered, this.waxedWeathered, this.oxidized, this.waxedOxidized);
   }

   public ImmutableList<Block> asList() {
      return ImmutableList.of(this.unaffected, this.waxed, this.exposed, this.waxedExposed, this.weathered, this.waxedWeathered, this.oxidized, this.waxedOxidized);
   }

   public void forEach(final Consumer<Block> consumer) {
      consumer.accept(this.unaffected);
      consumer.accept(this.exposed);
      consumer.accept(this.weathered);
      consumer.accept(this.oxidized);
      consumer.accept(this.waxed);
      consumer.accept(this.waxedExposed);
      consumer.accept(this.waxedWeathered);
      consumer.accept(this.waxedOxidized);
   }
}
