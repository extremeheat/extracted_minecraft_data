package net.minecraft.world.level.block;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.world.level.block.state.BlockState;

public interface WeatheringCopper extends ChangeOverTimeBlock<WeatherState> {
   Supplier<BiMap<Block, Block>> NEXT_BY_BLOCK = Suppliers.memoize(() -> {
      return ImmutableBiMap.builder().put(Blocks.COPPER_BLOCK, Blocks.EXPOSED_COPPER).put(Blocks.EXPOSED_COPPER, Blocks.WEATHERED_COPPER).put(Blocks.WEATHERED_COPPER, Blocks.OXIDIZED_COPPER).put(Blocks.CUT_COPPER, Blocks.EXPOSED_CUT_COPPER).put(Blocks.EXPOSED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER).put(Blocks.WEATHERED_CUT_COPPER, Blocks.OXIDIZED_CUT_COPPER).put(Blocks.CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER_SLAB).put(Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER_SLAB).put(Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER_SLAB).put(Blocks.CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER_STAIRS).put(Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER_STAIRS).put(Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_CUT_COPPER_STAIRS).build();
   });
   Supplier<BiMap<Block, Block>> PREVIOUS_BY_BLOCK = Suppliers.memoize(() -> {
      return ((BiMap)NEXT_BY_BLOCK.get()).inverse();
   });

   static Optional<Block> getPrevious(Block var0) {
      return Optional.ofNullable((Block)((BiMap)PREVIOUS_BY_BLOCK.get()).get(var0));
   }

   static Block getFirst(Block var0) {
      Block var1 = var0;

      for(Block var2 = (Block)((BiMap)PREVIOUS_BY_BLOCK.get()).get(var0); var2 != null; var2 = (Block)((BiMap)PREVIOUS_BY_BLOCK.get()).get(var2)) {
         var1 = var2;
      }

      return var1;
   }

   static Optional<BlockState> getPrevious(BlockState var0) {
      return getPrevious(var0.getBlock()).map((var1) -> {
         return var1.withPropertiesOf(var0);
      });
   }

   static Optional<Block> getNext(Block var0) {
      return Optional.ofNullable((Block)((BiMap)NEXT_BY_BLOCK.get()).get(var0));
   }

   static BlockState getFirst(BlockState var0) {
      return getFirst(var0.getBlock()).withPropertiesOf(var0);
   }

   default Optional<BlockState> getNext(BlockState var1) {
      return getNext(var1.getBlock()).map((var1x) -> {
         return var1x.withPropertiesOf(var1);
      });
   }

   default float getChanceModifier() {
      return this.getAge() == WeatheringCopper.WeatherState.UNAFFECTED ? 0.75F : 1.0F;
   }

   public static enum WeatherState {
      UNAFFECTED,
      EXPOSED,
      WEATHERED,
      OXIDIZED;

      private WeatherState() {
      }

      // $FF: synthetic method
      private static WeatherState[] $values() {
         return new WeatherState[]{UNAFFECTED, EXPOSED, WEATHERED, OXIDIZED};
      }
   }
}
