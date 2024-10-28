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
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetInstrumentFunction extends LootItemConditionalFunction {
   public static final MapCodec<SetInstrumentFunction> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return commonFields(var0).and(TagKey.hashedCodec(Registries.INSTRUMENT).fieldOf("options").forGetter((var0x) -> {
         return var0x.options;
      })).apply(var0, SetInstrumentFunction::new);
   });
   private final TagKey<Instrument> options;

   private SetInstrumentFunction(List<LootItemCondition> var1, TagKey<Instrument> var2) {
      super(var1);
      this.options = var2;
   }

   public LootItemFunctionType<SetInstrumentFunction> getType() {
      return LootItemFunctions.SET_INSTRUMENT;
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      Registry var3 = var2.getLevel().registryAccess().lookupOrThrow(Registries.INSTRUMENT);
      Optional var4 = var3.getRandomElementOf(this.options, var2.getRandom());
      if (var4.isPresent()) {
         var1.set(DataComponents.INSTRUMENT, (Holder)var4.get());
      }

      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> setInstrumentOptions(TagKey<Instrument> var0) {
      return simpleBuilder((var1) -> {
         return new SetInstrumentFunction(var1, var0);
      });
   }
}
