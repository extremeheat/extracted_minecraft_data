package net.minecraft.world.item.enchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public record ConditionalEffect<T>(T effect, Optional<LootItemCondition> requirements) {
   public ConditionalEffect(T effect, Optional<LootItemCondition> requirements) {
      super();
      this.effect = (T)effect;
      this.requirements = requirements;
   }

   public static Codec<LootItemCondition> conditionCodec(LootContextParamSet var0) {
      return LootItemCondition.DIRECT_CODEC
         .validate(
            var1 -> {
               ProblemReporter.Collector var2 = new ProblemReporter.Collector();
               var0.validateUser(var2, var1);
               return var2.getReport()
                  .map(var0xx -> DataResult.error(() -> "Validation error in enchantment effect condition: " + var0xx))
                  .orElseGet(() -> DataResult.success(var1));
            }
         );
   }

   public static <T> Codec<ConditionalEffect<T>> codec(Codec<T> var0, LootContextParamSet var1) {
      return RecordCodecBuilder.create(
         var2 -> var2.group(
                  var0.fieldOf("effect").forGetter(ConditionalEffect::effect),
                  conditionCodec(var1).optionalFieldOf("requirements").forGetter(ConditionalEffect::requirements)
               )
               .apply(var2, ConditionalEffect::new)
      );
   }

   public boolean matches(LootContext var1) {
      return this.requirements.isEmpty() ? true : this.requirements.get().test(var1);
   }
}
