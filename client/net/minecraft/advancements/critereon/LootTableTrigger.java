package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;

public class LootTableTrigger extends SimpleCriterionTrigger<LootTableTrigger.TriggerInstance> {
   public LootTableTrigger() {
      super();
   }

   @Override
   public Codec<LootTableTrigger.TriggerInstance> codec() {
      return LootTableTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, ResourceLocation var2) {
      this.trigger(var1, var1x -> var1x.matches(var2));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> b, ResourceLocation c) implements SimpleCriterionTrigger.SimpleInstance {
      private final Optional<ContextAwarePredicate> player;
      private final ResourceLocation lootTable;
      public static final Codec<LootTableTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(LootTableTrigger.TriggerInstance::player),
                  ResourceLocation.CODEC.fieldOf("loot_table").forGetter(LootTableTrigger.TriggerInstance::lootTable)
               )
               .apply(var0, LootTableTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> var1, ResourceLocation var2) {
         super();
         this.player = var1;
         this.lootTable = var2;
      }

      public static Criterion<LootTableTrigger.TriggerInstance> lootTableUsed(ResourceLocation var0) {
         return CriteriaTriggers.GENERATE_LOOT.createCriterion(new LootTableTrigger.TriggerInstance(Optional.empty(), var0));
      }

      public boolean matches(ResourceLocation var1) {
         return this.lootTable.equals(var1);
      }
   }
}
