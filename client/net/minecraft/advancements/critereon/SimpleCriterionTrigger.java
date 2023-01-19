package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;

public abstract class SimpleCriterionTrigger<T extends AbstractCriterionTriggerInstance> implements CriterionTrigger<T> {
   private final Map<PlayerAdvancements, Set<CriterionTrigger.Listener<T>>> players = Maps.newIdentityHashMap();

   public SimpleCriterionTrigger() {
      super();
   }

   @Override
   public final void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<T> var2) {
      this.players.computeIfAbsent(var1, var0 -> Sets.newHashSet()).add(var2);
   }

   @Override
   public final void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<T> var2) {
      Set var3 = this.players.get(var1);
      if (var3 != null) {
         var3.remove(var2);
         if (var3.isEmpty()) {
            this.players.remove(var1);
         }
      }
   }

   @Override
   public final void removePlayerListeners(PlayerAdvancements var1) {
      this.players.remove(var1);
   }

   protected abstract T createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3);

   public final T createInstance(JsonObject var1, DeserializationContext var2) {
      EntityPredicate.Composite var3 = EntityPredicate.Composite.fromJson(var1, "player", var2);
      return this.createInstance(var1, var3, var2);
   }

   protected void trigger(ServerPlayer var1, Predicate<T> var2) {
      PlayerAdvancements var3 = var1.getAdvancements();
      Set var4 = this.players.get(var3);
      if (var4 != null && !var4.isEmpty()) {
         LootContext var5 = EntityPredicate.createContext(var1, var1);
         ArrayList var6 = null;

         for(CriterionTrigger.Listener var8 : var4) {
            AbstractCriterionTriggerInstance var9 = (AbstractCriterionTriggerInstance)var8.getTriggerInstance();
            if (var2.test(var9) && var9.getPlayerPredicate().matches(var5)) {
               if (var6 == null) {
                  var6 = Lists.newArrayList();
               }

               var6.add(var8);
            }
         }

         if (var6 != null) {
            for(CriterionTrigger.Listener var11 : var6) {
               var11.run(var3);
            }
         }
      }
   }
}
