package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;

public record InvertedLootItemCondition(LootItemCondition term) implements LootItemCondition {
   public static final MapCodec<InvertedLootItemCondition> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(LootItemCondition.DIRECT_CODEC.fieldOf("term").forGetter(InvertedLootItemCondition::term)).apply(var0, InvertedLootItemCondition::new);
   });

   public InvertedLootItemCondition(LootItemCondition var1) {
      super();
      this.term = var1;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.INVERTED;
   }

   public boolean test(LootContext var1) {
      return !this.term.test(var1);
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return this.term.getReferencedContextParams();
   }

   public void validate(ValidationContext var1) {
      LootItemCondition.super.validate(var1);
      this.term.validate(var1);
   }

   public static LootItemCondition.Builder invert(LootItemCondition.Builder var0) {
      InvertedLootItemCondition var1 = new InvertedLootItemCondition(var0.build());
      return () -> {
         return var1;
      };
   }

   public LootItemCondition term() {
      return this.term;
   }

   // $FF: synthetic method
   public boolean test(final Object var1) {
      return this.test((LootContext)var1);
   }
}
