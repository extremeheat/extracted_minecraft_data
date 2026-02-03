package net.minecraft.world.level.storage.loot;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public record LootDataType<T extends Validatable>(ResourceKey<Registry<T>> registryKey, Codec<T> codec, ContextGetter<T> contextGetter) {
   public static final LootDataType<LootItemCondition> PREDICATE;
   public static final LootDataType<LootItemFunction> MODIFIER;
   public static final LootDataType<LootTable> TABLE;

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

   public static Stream<LootDataType<?>> values() {
      return Stream.of(PREDICATE, MODIFIER, TABLE);
   }

   static {
      PREDICATE = new LootDataType<LootItemCondition>(Registries.PREDICATE, LootItemCondition.DIRECT_CODEC, LootDataType.ContextGetter.constant(LootContextParamSets.ALL_PARAMS));
      MODIFIER = new LootDataType<LootItemFunction>(Registries.ITEM_MODIFIER, LootItemFunctions.ROOT_CODEC, LootDataType.ContextGetter.constant(LootContextParamSets.ALL_PARAMS));
      TABLE = new LootDataType<LootTable>(Registries.LOOT_TABLE, LootTable.DIRECT_CODEC, LootTable::getParamSet);
   }

   @FunctionalInterface
   public interface ContextGetter<T> {
      ContextKeySet context(T value);

      static <T> ContextGetter<T> constant(final ContextKeySet v) {
         return (value) -> v;
      }
   }
}
