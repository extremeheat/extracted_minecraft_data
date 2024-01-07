package net.minecraft.world.level.storage.loot;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.DataResult.PartialResult;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.slf4j.Logger;

public class LootDataType<T> {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final LootDataType<LootItemCondition> PREDICATE = new LootDataType<>(LootItemConditions.CODEC, "predicates", createSimpleValidator());
   public static final LootDataType<LootItemFunction> MODIFIER = new LootDataType<>(LootItemFunctions.CODEC, "item_modifiers", createSimpleValidator());
   public static final LootDataType<LootTable> TABLE = new LootDataType<>(LootTable.CODEC, "loot_tables", createLootTableValidator());
   private final Codec<T> codec;
   private final String directory;
   private final LootDataType.Validator<T> validator;

   private LootDataType(Codec<T> var1, String var2, LootDataType.Validator<T> var3) {
      super();
      this.codec = var1;
      this.directory = var2;
      this.validator = var3;
   }

   public String directory() {
      return this.directory;
   }

   public void runValidation(ValidationContext var1, LootDataId<T> var2, T var3) {
      this.validator.run(var1, var2, (T)var3);
   }

   public Optional<T> deserialize(ResourceLocation var1, JsonElement var2) {
      DataResult var3 = this.codec.parse(JsonOps.INSTANCE, var2);
      var3.error().ifPresent(var2x -> LOGGER.error("Couldn't parse element {}:{} - {}", new Object[]{this.directory, var1, var2x.message()}));
      return var3.result();
   }

   public static Stream<LootDataType<?>> values() {
      return Stream.of(PREDICATE, MODIFIER, TABLE);
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
