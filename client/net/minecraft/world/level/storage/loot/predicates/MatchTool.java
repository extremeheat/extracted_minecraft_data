package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record MatchTool(Optional<ItemPredicate> predicate) implements LootItemCondition {
   public static final MapCodec<MatchTool> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(ItemPredicate.CODEC.optionalFieldOf("predicate").forGetter(MatchTool::predicate)).apply(var0, MatchTool::new)
   );

   public MatchTool(Optional<ItemPredicate> predicate) {
      super();
      this.predicate = predicate;
   }

   @Override
   public LootItemConditionType getType() {
      return LootItemConditions.MATCH_TOOL;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.TOOL);
   }

   public boolean test(LootContext var1) {
      ItemStack var2 = var1.getParamOrNull(LootContextParams.TOOL);
      return var2 != null && (this.predicate.isEmpty() || this.predicate.get().test(var2));
   }

   public static LootItemCondition.Builder toolMatches(ItemPredicate.Builder var0) {
      return () -> new MatchTool(Optional.of(var0.build()));
   }
}