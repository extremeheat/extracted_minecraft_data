package net.minecraft.world.item.enchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public record TargetedConditionalEffect<T>(EnchantmentTarget enchanted, EnchantmentTarget affected, T effect, Optional<LootItemCondition> requirements) {
   public TargetedConditionalEffect(EnchantmentTarget enchanted, EnchantmentTarget affected, T effect, Optional<LootItemCondition> requirements) {
      super();
      this.enchanted = enchanted;
      this.affected = affected;
      this.effect = (T)effect;
      this.requirements = requirements;
   }

   public static <S> Codec<TargetedConditionalEffect<S>> codec(Codec<S> var0, LootContextParamSet var1) {
      return RecordCodecBuilder.create(
         var2 -> var2.group(
                  EnchantmentTarget.CODEC.fieldOf("enchanted").forGetter(TargetedConditionalEffect::enchanted),
                  EnchantmentTarget.CODEC.fieldOf("affected").forGetter(TargetedConditionalEffect::affected),
                  var0.fieldOf("effect").forGetter(TargetedConditionalEffect::effect),
                  ConditionalEffect.conditionCodec(var1).optionalFieldOf("requirements").forGetter(TargetedConditionalEffect::requirements)
               )
               .apply(var2, TargetedConditionalEffect::new)
      );
   }

   public static <S> Codec<TargetedConditionalEffect<S>> equipmentDropsCodec(Codec<S> var0, LootContextParamSet var1) {
      return RecordCodecBuilder.create(
         var2 -> var2.group(
                  EnchantmentTarget.CODEC
                     .validate(
                        var0xx -> var0xx != EnchantmentTarget.DAMAGING_ENTITY
                              ? DataResult.success(var0xx)
                              : DataResult.error(() -> "enchanted must be attacker or victim")
                     )
                     .fieldOf("enchanted")
                     .forGetter(TargetedConditionalEffect::enchanted),
                  var0.fieldOf("effect").forGetter(TargetedConditionalEffect::effect),
                  ConditionalEffect.conditionCodec(var1).optionalFieldOf("requirements").forGetter(TargetedConditionalEffect::requirements)
               )
               .apply(var2, (var0xx, var1xx, var2x) -> new TargetedConditionalEffect<>(var0xx, EnchantmentTarget.VICTIM, var1xx, var2x))
      );
   }

   public boolean matches(LootContext var1) {
      return this.requirements.isEmpty() ? true : this.requirements.get().test(var1);
   }
}
