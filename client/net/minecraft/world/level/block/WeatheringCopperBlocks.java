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
   public WeatheringCopperBlocks(Block var1, Block var2, Block var3, Block var4, Block var5, Block var6, Block var7, Block var8) {
      super();
      this.unaffected = var1;
      this.exposed = var2;
      this.weathered = var3;
      this.oxidized = var4;
      this.waxed = var5;
      this.waxedExposed = var6;
      this.waxedWeathered = var7;
      this.waxedOxidized = var8;
   }

   public static <WaxedBlock extends Block, WeatheringBlock extends Block & WeatheringCopper> WeatheringCopperBlocks create(String var0, TriFunction<String, Function<BlockBehaviour.Properties, Block>, BlockBehaviour.Properties, Block> var1, Function<BlockBehaviour.Properties, WaxedBlock> var2, BiFunction<WeatheringCopper.WeatherState, BlockBehaviour.Properties, WeatheringBlock> var3, Function<WeatheringCopper.WeatherState, BlockBehaviour.Properties> var4) {
      Block var10002 = (Block)var1.apply(var0, (Function)(var1x) -> (Block)var3.apply(WeatheringCopper.WeatherState.UNAFFECTED, var1x), (BlockBehaviour.Properties)var4.apply(WeatheringCopper.WeatherState.UNAFFECTED));
      Block var10003 = (Block)var1.apply("exposed_" + var0, (Function)(var1x) -> (Block)var3.apply(WeatheringCopper.WeatherState.EXPOSED, var1x), (BlockBehaviour.Properties)var4.apply(WeatheringCopper.WeatherState.EXPOSED));
      Block var10004 = (Block)var1.apply("weathered_" + var0, (Function)(var1x) -> (Block)var3.apply(WeatheringCopper.WeatherState.WEATHERED, var1x), (BlockBehaviour.Properties)var4.apply(WeatheringCopper.WeatherState.WEATHERED));
      Block var10005 = (Block)var1.apply("oxidized_" + var0, (Function)(var1x) -> (Block)var3.apply(WeatheringCopper.WeatherState.OXIDIZED, var1x), (BlockBehaviour.Properties)var4.apply(WeatheringCopper.WeatherState.OXIDIZED));
      String var10007 = "waxed_" + var0;
      Objects.requireNonNull(var2);
      Block var10006 = (Block)var1.apply(var10007, var2::apply, (BlockBehaviour.Properties)var4.apply(WeatheringCopper.WeatherState.UNAFFECTED));
      String var10008 = "waxed_exposed_" + var0;
      Objects.requireNonNull(var2);
      Block var5 = (Block)var1.apply(var10008, var2::apply, (BlockBehaviour.Properties)var4.apply(WeatheringCopper.WeatherState.EXPOSED));
      String var10009 = "waxed_weathered_" + var0;
      Objects.requireNonNull(var2);
      Block var6 = (Block)var1.apply(var10009, var2::apply, (BlockBehaviour.Properties)var4.apply(WeatheringCopper.WeatherState.WEATHERED));
      String var10010 = "waxed_oxidized_" + var0;
      Objects.requireNonNull(var2);
      return new WeatheringCopperBlocks(var10002, var10003, var10004, var10005, var10006, var5, var6, (Block)var1.apply(var10010, var2::apply, (BlockBehaviour.Properties)var4.apply(WeatheringCopper.WeatherState.OXIDIZED)));
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

   public void forEach(Consumer<Block> var1) {
      var1.accept(this.unaffected);
      var1.accept(this.exposed);
      var1.accept(this.weathered);
      var1.accept(this.oxidized);
      var1.accept(this.waxed);
      var1.accept(this.waxedExposed);
      var1.accept(this.waxedWeathered);
      var1.accept(this.waxedOxidized);
   }
}
