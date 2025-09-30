package net.minecraft.world.item;

import com.google.common.collect.ImmutableBiMap;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopperBlocks;

public record WeatheringCopperItems(Item unaffected, Item exposed, Item weathered, Item oxidized, Item waxed, Item waxedExposed, Item waxedWeathered, Item waxedOxidized) {
   public WeatheringCopperItems(Item var1, Item var2, Item var3, Item var4, Item var5, Item var6, Item var7, Item var8) {
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

   public static WeatheringCopperItems create(WeatheringCopperBlocks var0, Function<Block, Item> var1) {
      return new WeatheringCopperItems((Item)var1.apply(var0.unaffected()), (Item)var1.apply(var0.exposed()), (Item)var1.apply(var0.weathered()), (Item)var1.apply(var0.oxidized()), (Item)var1.apply(var0.waxed()), (Item)var1.apply(var0.waxedExposed()), (Item)var1.apply(var0.waxedWeathered()), (Item)var1.apply(var0.waxedOxidized()));
   }

   public ImmutableBiMap<Item, Item> waxedMapping() {
      return ImmutableBiMap.of(this.unaffected, this.waxed, this.exposed, this.waxedExposed, this.weathered, this.waxedWeathered, this.oxidized, this.waxedOxidized);
   }

   public void forEach(Consumer<Item> var1) {
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
