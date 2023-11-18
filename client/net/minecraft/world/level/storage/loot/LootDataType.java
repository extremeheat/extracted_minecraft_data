package net.minecraft.world.level.storage.loot;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class LootDataType<T> {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final LootDataType<LootItemCondition> PREDICATE = new LootDataType<>(
      Deserializers.createConditionSerializer().create(),
      createSingleOrMultipleDeserialiser(LootItemCondition.class, LootDataManager::createComposite),
      "predicates",
      createSimpleValidator()
   );
   public static final LootDataType<LootItemFunction> MODIFIER = new LootDataType<>(
      Deserializers.createFunctionSerializer().create(),
      createSingleOrMultipleDeserialiser(LootItemFunction.class, LootDataManager::createComposite),
      "item_modifiers",
      createSimpleValidator()
   );
   public static final LootDataType<LootTable> TABLE = new LootDataType<>(
      Deserializers.createLootTableSerializer().create(), createSingleDeserialiser(LootTable.class), "loot_tables", createLootTableValidator()
   );
   private final Gson parser;
   private final BiFunction<ResourceLocation, JsonElement, Optional<T>> topDeserializer;
   private final String directory;
   private final LootDataType.Validator<T> validator;

   private LootDataType(
      Gson var1, BiFunction<Gson, String, BiFunction<ResourceLocation, JsonElement, Optional<T>>> var2, String var3, LootDataType.Validator<T> var4
   ) {
      super();
      this.parser = var1;
      this.directory = var3;
      this.validator = var4;
      this.topDeserializer = (BiFunction)var2.apply((T)var1, var3);
   }

   public Gson parser() {
      return this.parser;
   }

   public String directory() {
      return this.directory;
   }

   public void runValidation(ValidationContext var1, LootDataId<T> var2, T var3) {
      this.validator.run(var1, var2, (T)var3);
   }

   public Optional<T> deserialize(ResourceLocation var1, JsonElement var2) {
      return this.topDeserializer.apply(var1, var2);
   }

   public static Stream<LootDataType<?>> values() {
      return Stream.of(PREDICATE, MODIFIER, TABLE);
   }

   private static <T> BiFunction<Gson, String, BiFunction<ResourceLocation, JsonElement, Optional<T>>> createSingleDeserialiser(Class<T> var0) {
      return (var1, var2) -> (var3, var4) -> {
            try {
               return Optional.of((T)var1.fromJson(var4, var0));
            } catch (Exception var6) {
               LOGGER.error("Couldn't parse element {}:{}", new Object[]{var2, var3, var6});
               return Optional.empty();
            }
         };
   }

   private static <T> BiFunction<Gson, String, BiFunction<ResourceLocation, JsonElement, Optional<T>>> createSingleOrMultipleDeserialiser(
      Class<T> var0, Function<T[], T> var1
   ) {
      Class var2 = var0.arrayType();
      return (var3, var4) -> (var5, var6) -> {
            try {
               if (var6.isJsonArray()) {
                  Object[] var7 = (Object[])var3.fromJson(var6, var2);
                  return Optional.of((T)var1.apply(var7));
               } else {
                  return Optional.of((T)var3.fromJson(var6, var0));
               }
            } catch (Exception var8) {
               LOGGER.error("Couldn't parse element {}:{}", new Object[]{var4, var5, var8});
               return Optional.empty();
            }
         };
   }

   private static <T extends LootContextUser> LootDataType.Validator<T> createSimpleValidator() {
      return (var0, var1, var2) -> var2.validate(var0.enterElement("{" + var1.type().directory + ":" + var1.location() + "}", var1));
   }

   private static LootDataType.Validator<LootTable> createLootTableValidator() {
      return (var0, var1, var2) -> var2.validate(
            var0.setParams(var2.getParamSet()).enterElement("{" + var1.type().directory + ":" + var1.location() + "}", var1)
         );
   }

   @FunctionalInterface
   public interface Validator<T> {
      void run(ValidationContext var1, LootDataId<T> var2, T var3);
   }
}
