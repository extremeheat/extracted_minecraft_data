package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootTableTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public LootTableTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return LootTableTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final ResourceKey<LootTable> lootTable) {
      this.trigger(player, (t) -> t.matches(lootTable));
   }

   public static record TriggerInstance(Optional<Holder<LootItemCondition>> player, HolderSet<LootTable> lootTable) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), LootTable.LIST_CODEC.fieldOf("loot_tables").forGetter(TriggerInstance::lootTable)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> lootTableUsed(final Holder<LootTable> lootTable) {
         return lootTableUsed(HolderSet.direct(lootTable));
      }

      public static Criterion<TriggerInstance> lootTableUsed(final HolderSet<LootTable> lootTable) {
         return CriteriaTriggers.GENERATE_LOOT.createCriterion(new TriggerInstance(Optional.empty(), lootTable));
      }

      public boolean matches(final ResourceKey<LootTable> lootTable) {
         return this.lootTable.stream().anyMatch((table) -> table.is(lootTable));
      }
   }
}
