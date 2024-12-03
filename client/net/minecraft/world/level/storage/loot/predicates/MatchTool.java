package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record MatchTool(Optional<ItemPredicate> predicate) implements LootItemCondition {
   public static final MapCodec<MatchTool> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ItemPredicate.CODEC.optionalFieldOf("predicate").forGetter(MatchTool::predicate)).apply(var0, MatchTool::new));

   public MatchTool(Optional<ItemPredicate> var1) {
      super();
      this.predicate = var1;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.MATCH_TOOL;
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(LootContextParams.TOOL);
   }

   public boolean test(LootContext var1) {
      ItemStack var2 = (ItemStack)var1.getOptionalParameter(LootContextParams.TOOL);
      return var2 != null && (this.predicate.isEmpty() || ((ItemPredicate)this.predicate.get()).test(var2));
   }

   public static LootItemCondition.Builder toolMatches(ItemPredicate.Builder var0) {
      return () -> new MatchTool(Optional.of(var0.build()));
   }

   // $FF: synthetic method
   public boolean test(final Object var1) {
      return this.test((LootContext)var1);
   }
}
