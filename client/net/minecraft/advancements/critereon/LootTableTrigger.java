package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootTable;

public class LootTableTrigger extends SimpleCriterionTrigger<LootTableTrigger.TriggerInstance> {
   public LootTableTrigger() {
      super();
   }

   @Override
   public Codec<LootTableTrigger.TriggerInstance> codec() {
      return LootTableTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, ResourceKey<LootTable> var2) {
      this.trigger(var1, var1x -> var1x.matches(var2));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> b, ResourceKey<LootTable> c) implements SimpleCriterionTrigger.SimpleInstance {
      private final Optional<ContextAwarePredicate> player;
      private final ResourceKey<LootTable> lootTable;
      public static final Codec<LootTableTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(LootTableTrigger.TriggerInstance::player),
                  ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table").forGetter(LootTableTrigger.TriggerInstance::lootTable)
               )
               .apply(var0, LootTableTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> var1, ResourceKey<LootTable> var2) {
         super();
         this.player = var1;
         this.lootTable = var2;
      }

      public static Criterion<LootTableTrigger.TriggerInstance> lootTableUsed(ResourceKey<LootTable> var0) {
         return CriteriaTriggers.GENERATE_LOOT.createCriterion(new LootTableTrigger.TriggerInstance(Optional.empty(), var0));
      }

      public boolean matches(ResourceKey<LootTable> var1) {
         return this.lootTable == var1;
      }
   }
}
