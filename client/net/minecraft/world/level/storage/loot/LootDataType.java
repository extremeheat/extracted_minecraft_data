package net.minecraft.world.level.storage.loot;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public record LootDataType<T>(ResourceKey<Registry<T>> registryKey, Codec<T> codec, Validator<T> validator) {
   public static final LootDataType<LootItemCondition> PREDICATE;
   public static final LootDataType<LootItemFunction> MODIFIER;
   public static final LootDataType<LootTable> TABLE;

   public LootDataType(ResourceKey<Registry<T>> var1, Codec<T> var2, Validator<T> var3) {
      super();
      this.registryKey = var1;
      this.codec = var2;
      this.validator = var3;
   }

   public void runValidation(ValidationContext var1, ResourceKey<T> var2, T var3) {
      this.validator.run(var1, var2, var3);
   }

   public static Stream<LootDataType<?>> values() {
      return Stream.of(PREDICATE, MODIFIER, TABLE);
   }

   private static <T extends LootContextUser> Validator<T> createSimpleValidator() {
      return (var0, var1, var2) -> {
         var2.validate(var0.enterElement("{" + String.valueOf(var1.registry()) + "/" + String.valueOf(var1.location()) + "}", var1));
      };
   }

   private static Validator<LootTable> createLootTableValidator() {
      return (var0, var1, var2) -> {
         var2.validate(var0.setContextKeySet(var2.getParamSet()).enterElement("{" + String.valueOf(var1.registry()) + "/" + String.valueOf(var1.location()) + "}", var1));
      };
   }

   public ResourceKey<Registry<T>> registryKey() {
      return this.registryKey;
   }

   public Codec<T> codec() {
      return this.codec;
   }

   public Validator<T> validator() {
      return this.validator;
   }

   static {
      PREDICATE = new LootDataType(Registries.PREDICATE, LootItemCondition.DIRECT_CODEC, createSimpleValidator());
      MODIFIER = new LootDataType(Registries.ITEM_MODIFIER, LootItemFunctions.ROOT_CODEC, createSimpleValidator());
      TABLE = new LootDataType(Registries.LOOT_TABLE, LootTable.DIRECT_CODEC, createLootTableValidator());
   }

   @FunctionalInterface
   public interface Validator<T> {
      void run(ValidationContext var1, ResourceKey<T> var2, T var3);
   }
}
