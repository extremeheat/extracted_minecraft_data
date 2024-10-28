package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootTable;

public class LootTableTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public LootTableTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return LootTableTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, ResourceKey<LootTable> var2) {
      this.trigger(var1, (var1x) -> {
         return var1x.matches(var2);
      });
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, ResourceKey<LootTable> lootTable) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table").forGetter(TriggerInstance::lootTable)).apply(var0, TriggerInstance::new);
      });

      public TriggerInstance(Optional<ContextAwarePredicate> var1, ResourceKey<LootTable> var2) {
         super();
         this.player = var1;
         this.lootTable = var2;
      }

      public static Criterion<TriggerInstance> lootTableUsed(ResourceKey<LootTable> var0) {
         return CriteriaTriggers.GENERATE_LOOT.createCriterion(new TriggerInstance(Optional.empty(), var0));
      }

      public boolean matches(ResourceKey<LootTable> var1) {
         return this.lootTable == var1;
      }

      public Optional<ContextAwarePredicate> player() {
         return this.player;
      }

      public ResourceKey<LootTable> lootTable() {
         return this.lootTable;
      }
   }
}
