package net.minecraft.world.item.enchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public record TargetedConditionalEffect<T>(EnchantmentTarget enchanted, EnchantmentTarget affected, T effect, Optional<Holder<LootItemCondition>> requirements) implements Validatable {
   public TargetedConditionalEffect {
      super();
   }

   public static <S> Codec<TargetedConditionalEffect<S>> codec(final Codec<S> effectCodec) {
      return RecordCodecBuilder.create((i) -> i.group(EnchantmentTarget.CODEC.fieldOf("enchanted").forGetter(TargetedConditionalEffect::enchanted), EnchantmentTarget.CODEC.fieldOf("affected").forGetter(TargetedConditionalEffect::affected), effectCodec.fieldOf("effect").forGetter(TargetedConditionalEffect::effect), LootItemCondition.CODEC.optionalFieldOf("requirements").forGetter(TargetedConditionalEffect::requirements)).apply(i, TargetedConditionalEffect::new));
   }

   public static <S> Codec<TargetedConditionalEffect<S>> equipmentDropsCodec(final Codec<S> effectCodec) {
      return RecordCodecBuilder.create((i) -> i.group(EnchantmentTarget.NON_DAMAGE_CODEC.fieldOf("enchanted").forGetter(TargetedConditionalEffect::enchanted), effectCodec.fieldOf("effect").forGetter(TargetedConditionalEffect::effect), LootItemCondition.CODEC.optionalFieldOf("requirements").forGetter(TargetedConditionalEffect::requirements)).apply(i, (target, effect, requirements) -> new TargetedConditionalEffect(target, EnchantmentTarget.VICTIM, effect, requirements)));
   }

   public boolean matches(final LootContext context) {
      return this.requirements.isEmpty() || ((LootItemCondition)((Holder)this.requirements.get()).value()).test(context);
   }

   public void validate(final ValidationContext context) {
      Validatable.validateHolder(context, "requirements", this.requirements);
   }
}
