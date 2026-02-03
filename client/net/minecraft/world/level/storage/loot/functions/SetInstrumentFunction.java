package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.InstrumentComponent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetInstrumentFunction extends LootItemConditionalFunction {
   public static final MapCodec<SetInstrumentFunction> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(TagKey.hashedCodec(Registries.INSTRUMENT).fieldOf("options").forGetter((f) -> f.options)).apply(i, SetInstrumentFunction::new));
   private final TagKey<Instrument> options;

   private SetInstrumentFunction(final List<LootItemCondition> predicates, final TagKey<Instrument> options) {
      super(predicates);
      this.options = options;
   }

   public MapCodec<SetInstrumentFunction> codec() {
      return MAP_CODEC;
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      Registry<Instrument> instruments = context.getLevel().registryAccess().lookupOrThrow(Registries.INSTRUMENT);
      Optional<Holder<Instrument>> instrument = instruments.getRandomElementOf(this.options, context.getRandom());
      if (instrument.isPresent()) {
         itemStack.set(DataComponents.INSTRUMENT, new InstrumentComponent((Holder)instrument.get()));
      }

      return itemStack;
   }

   public static LootItemConditionalFunction.Builder<?> setInstrumentOptions(final TagKey<Instrument> options) {
      return simpleBuilder((conditions) -> new SetInstrumentFunction(conditions, options));
   }
}
