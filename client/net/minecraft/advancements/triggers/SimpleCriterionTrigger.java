package net.minecraft.advancements.triggers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;

public abstract class SimpleCriterionTrigger<T extends SimpleCriterionTrigger.SimpleInstance> implements CriterionTrigger<T> {
   public SimpleCriterionTrigger() {
      super();
   }

   protected void trigger(final ServerPlayer player, final Predicate<T> matcher) {
      PlayerAdvancements advancements = player.getAdvancements();
      Map<PlayerAdvancements.TriggerInstanceKey, T> listenersForType = advancements.getTriggerMapForType(this);
      if (listenersForType != null && !listenersForType.isEmpty()) {
         LootContext playerContext = EntityPredicate.createContext(player, player);
         List<PlayerAdvancements.TriggerInstanceKey> matchedConditions = null;

         for(Map.Entry<PlayerAdvancements.TriggerInstanceKey, T> entry : listenersForType.entrySet()) {
            T value = (T)(entry.getValue());
            if (matcher.test(value)) {
               Optional<ContextAwarePredicate> predicate = value.player();
               if (!predicate.isPresent() || ((ContextAwarePredicate)predicate.get()).matches(playerContext)) {
                  if (matchedConditions == null) {
                     matchedConditions = new ArrayList();
                  }

                  matchedConditions.add((PlayerAdvancements.TriggerInstanceKey)entry.getKey());
               }
            }
         }

         if (matchedConditions != null) {
            for(PlayerAdvancements.TriggerInstanceKey criterion : matchedConditions) {
               advancements.award(criterion.advancement(), criterion.criterion());
            }
         }

      }
   }

   public interface SimpleInstance extends CriterionTriggerInstance {
      default void validate(final ValidationContextSource validator) {
         Validatable.validate(validator.entityContext(), "player", this.player());
      }

      Optional<ContextAwarePredicate> player();
   }
}
