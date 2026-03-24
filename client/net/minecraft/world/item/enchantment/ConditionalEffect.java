package net.minecraft.world.item.enchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public record ConditionalEffect<T>(T effect, Optional<LootItemCondition> requirements) implements Validatable {
   public ConditionalEffect {
      super();
   }

   public static <T> Codec<ConditionalEffect<T>> codec(final Codec<T> effectCodec) {
      return RecordCodecBuilder.create((i) -> i.group(effectCodec.fieldOf("effect").forGetter(ConditionalEffect::effect), LootItemCondition.DIRECT_CODEC.optionalFieldOf("requirements").forGetter(ConditionalEffect::requirements)).apply(i, ConditionalEffect::new));
   }

   public boolean matches(final LootContext context) {
      return this.requirements.isEmpty() || ((LootItemCondition)this.requirements.get()).test(context);
   }

   public void validate(final ValidationContext context) {
      Validatable.validate(context, "requirements", this.requirements);
   }
}
