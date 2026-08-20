package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ChanneledLightningTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public ChanneledLightningTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return ChanneledLightningTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final Collection<? extends Entity> victims) {
      List<LootContext> victimsContexts = (List)victims.stream().map((v) -> EntityPredicate.createContext(player, v)).collect(Collectors.toList());
      this.trigger(player, (t) -> t.matches(victimsContexts));
   }

   public static record TriggerInstance(Optional<Holder<LootItemCondition>> player, List<Holder<LootItemCondition>> victims) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), LootItemCondition.CODEC.listOf().optionalFieldOf("victims", List.of()).forGetter(TriggerInstance::victims)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> channeledLightning(final EntityPredicate.Builder... victims) {
         return CriteriaTriggers.CHANNELED_LIGHTNING.createCriterion(new TriggerInstance(Optional.empty(), EntityPredicate.wrap(victims)));
      }

      public boolean matches(final Collection<? extends LootContext> victims) {
         for(Holder<LootItemCondition> predicate : this.victims) {
            boolean found = false;

            for(LootContext victim : victims) {
               if (((LootItemCondition)predicate.value()).test(victim)) {
                  found = true;
                  break;
               }
            }

            if (!found) {
               return false;
            }
         }

         return true;
      }

      public void validate(final ValidationContextSource validator) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(validator);
         Validatable.validateHolder(validator.entityContext(), "victims", this.victims);
      }
   }
}
