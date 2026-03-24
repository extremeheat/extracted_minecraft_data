package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;

public abstract class SimpleCriterionTrigger<T extends SimpleCriterionTrigger.SimpleInstance> implements CriterionTrigger<T> {
   private final Map<PlayerAdvancements, Set<CriterionTrigger.Listener<T>>> players = Maps.newIdentityHashMap();

   public SimpleCriterionTrigger() {
      super();
   }

   public final void addPlayerListener(final PlayerAdvancements player, final CriterionTrigger.Listener<T> listener) {
      ((Set)this.players.computeIfAbsent(player, (k) -> Sets.newHashSet())).add(listener);
   }

   public final void removePlayerListener(final PlayerAdvancements player, final CriterionTrigger.Listener<T> listener) {
      Set<CriterionTrigger.Listener<T>> listeners = (Set)this.players.get(player);
      if (listeners != null) {
         listeners.remove(listener);
         if (listeners.isEmpty()) {
            this.players.remove(player);
         }
      }

   }

   public final void removePlayerListeners(final PlayerAdvancements player) {
      this.players.remove(player);
   }

   protected void trigger(final ServerPlayer player, final Predicate<T> matcher) {
      PlayerAdvancements advancements = player.getAdvancements();
      Set<CriterionTrigger.Listener<T>> allListeners = (Set)this.players.get(advancements);
      if (allListeners != null && !allListeners.isEmpty()) {
         LootContext playerContext = EntityPredicate.createContext(player, player);
         List<CriterionTrigger.Listener<T>> listeners = null;

         for(CriterionTrigger.Listener<T> listener : allListeners) {
            T triggerInstance = listener.trigger();
            if (matcher.test(triggerInstance)) {
               Optional<ContextAwarePredicate> predicate = triggerInstance.player();
               if (predicate.isEmpty() || ((ContextAwarePredicate)predicate.get()).matches(playerContext)) {
                  if (listeners == null) {
                     listeners = Lists.newArrayList();
                  }

                  listeners.add(listener);
               }
            }
         }

         if (listeners != null) {
            for(CriterionTrigger.Listener<T> listener : listeners) {
               listener.run(advancements);
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
