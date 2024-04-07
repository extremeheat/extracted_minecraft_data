package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public record InvertedLootItemCondition(LootItemCondition term) implements LootItemCondition {
   public static final MapCodec<InvertedLootItemCondition> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(LootItemConditions.DIRECT_CODEC.fieldOf("term").forGetter(InvertedLootItemCondition::term))
            .apply(var0, InvertedLootItemCondition::new)
   );

   public InvertedLootItemCondition(LootItemCondition term) {
      super();
      this.term = term;
   }

   @Override
   public LootItemConditionType getType() {
      return LootItemConditions.INVERTED;
   }

   public boolean test(LootContext var1) {
      return !this.term.test(var1);
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.term.getReferencedContextParams();
   }

   @Override
   public void validate(ValidationContext var1) {
      LootItemCondition.super.validate(var1);
      this.term.validate(var1);
   }

   public static LootItemCondition.Builder invert(LootItemCondition.Builder var0) {
      InvertedLootItemCondition var1 = new InvertedLootItemCondition(var0.build());
      return () -> var1;
   }
}
