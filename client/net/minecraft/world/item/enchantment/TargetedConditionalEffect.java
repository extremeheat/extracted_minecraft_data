package net.minecraft.world.item.enchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public record TargetedConditionalEffect<T>(EnchantmentTarget enchanted, EnchantmentTarget affected, T effect, Optional<LootItemCondition> requirements) {
   public TargetedConditionalEffect(EnchantmentTarget var1, EnchantmentTarget var2, T var3, Optional<LootItemCondition> var4) {
      super();
      this.enchanted = var1;
      this.affected = var2;
      this.effect = var3;
      this.requirements = var4;
   }

   public static <S> Codec<TargetedConditionalEffect<S>> codec(Codec<S> var0, ContextKeySet var1) {
      return RecordCodecBuilder.create((var2) -> var2.group(EnchantmentTarget.CODEC.fieldOf("enchanted").forGetter(TargetedConditionalEffect::enchanted), EnchantmentTarget.CODEC.fieldOf("affected").forGetter(TargetedConditionalEffect::affected), var0.fieldOf("effect").forGetter(TargetedConditionalEffect::effect), ConditionalEffect.conditionCodec(var1).optionalFieldOf("requirements").forGetter(TargetedConditionalEffect::requirements)).apply(var2, TargetedConditionalEffect::new));
   }

   public static <S> Codec<TargetedConditionalEffect<S>> equipmentDropsCodec(Codec<S> var0, ContextKeySet var1) {
      return RecordCodecBuilder.create((var2) -> var2.group(EnchantmentTarget.CODEC.validate((var0x) -> var0x != EnchantmentTarget.DAMAGING_ENTITY ? DataResult.success(var0x) : DataResult.error(() -> "enchanted must be attacker or victim")).fieldOf("enchanted").forGetter(TargetedConditionalEffect::enchanted), var0.fieldOf("effect").forGetter(TargetedConditionalEffect::effect), ConditionalEffect.conditionCodec(var1).optionalFieldOf("requirements").forGetter(TargetedConditionalEffect::requirements)).apply(var2, (var0x, var1x, var2x) -> new TargetedConditionalEffect(var0x, EnchantmentTarget.VICTIM, var1x, var2x)));
   }

   public boolean matches(LootContext var1) {
      return this.requirements.isEmpty() ? true : ((LootItemCondition)this.requirements.get()).test(var1);
   }
}
