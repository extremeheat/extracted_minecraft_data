package net.minecraft.world.level.storage.loot;

import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.item.slot.SlotSource;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public record LootDataType<T extends Validatable>(ResourceKey<Registry<T>> registryKey, ContextGetter<T> contextGetter) {
   public static final LootDataType<LootItemCondition> PREDICATE;
   public static final LootDataType<LootItemFunction> MODIFIER;
   public static final LootDataType<SlotSource> SLOT_SOURCE;
   public static final LootDataType<LootTable> TABLE;
   public static final LootDataType<NumberProvider> NUMBER_PROVIDER;

   public LootDataType {
      super();
   }

   public void runValidation(final ValidationContextSource contextSource, final ResourceKey<T> key, final T value) {
      ContextKeySet contextKeys = this.contextGetter.context(value);
      ValidationContext rootContext = contextSource.context(contextKeys).enterElement(new ProblemReporter.RootElementPathElement(key), key);
      value.validate(rootContext);
   }

   public void runValidation(final ValidationContextSource contextSource, final HolderLookup<T> lookup) {
      lookup.listElements().forEach((holder) -> this.runValidation(contextSource, holder.key(), (Validatable)holder.value()));
   }

   public void runValidation(final ValidationContextSource contextSource, final HolderLookup.Provider registries) {
      HolderLookup<T> registry = registries.lookupOrThrow(this.registryKey());
      this.runValidation(contextSource, registry);
   }

   public void runValidationIfPresent(final ValidationContextSource contextSource, final HolderLookup.Provider registries) {
      registries.lookup(this.registryKey()).ifPresent((registry) -> this.runValidation(contextSource, registry));
   }

   public static Stream<LootDataType<?>> values() {
      return Stream.of(PREDICATE, MODIFIER, SLOT_SOURCE, TABLE, NUMBER_PROVIDER);
   }

   static {
      PREDICATE = new LootDataType<LootItemCondition>(Registries.PREDICATE, LootDataType.ContextGetter.constant(LootContextParamSets.ALL_PARAMS));
      MODIFIER = new LootDataType<LootItemFunction>(Registries.ITEM_MODIFIER, LootDataType.ContextGetter.constant(LootContextParamSets.ALL_PARAMS));
      SLOT_SOURCE = new LootDataType<SlotSource>(Registries.SLOT_SOURCE, LootDataType.ContextGetter.constant(LootContextParamSets.ALL_PARAMS));
      TABLE = new LootDataType<LootTable>(Registries.LOOT_TABLE, LootTable::getParamSet);
      NUMBER_PROVIDER = new LootDataType<NumberProvider>(Registries.NUMBER_PROVIDER, LootDataType.ContextGetter.constant(LootContextParamSets.ALL_PARAMS));
   }

   @FunctionalInterface
   public interface ContextGetter<T> {
      ContextKeySet context(T value);

      static <T> ContextGetter<T> constant(final ContextKeySet v) {
         return (value) -> v;
      }
   }
}
