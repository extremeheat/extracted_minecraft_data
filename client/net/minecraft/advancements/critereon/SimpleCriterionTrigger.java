package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;

public abstract class SimpleCriterionTrigger<T extends SimpleInstance> implements CriterionTrigger<T> {
   private final Map<PlayerAdvancements, Set<CriterionTrigger.Listener<T>>> players = Maps.newIdentityHashMap();

   public SimpleCriterionTrigger() {
      super();
   }

   public final void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<T> var2) {
      ((Set)this.players.computeIfAbsent(var1, (var0) -> {
         return Sets.newHashSet();
      })).add(var2);
   }

   public final void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<T> var2) {
      Set var3 = (Set)this.players.get(var1);
      if (var3 != null) {
         var3.remove(var2);
         if (var3.isEmpty()) {
            this.players.remove(var1);
         }
      }

   }

   public final void removePlayerListeners(PlayerAdvancements var1) {
      this.players.remove(var1);
   }

   protected void trigger(ServerPlayer var1, Predicate<T> var2) {
      PlayerAdvancements var3 = var1.getAdvancements();
      Set var4 = (Set)this.players.get(var3);
      if (var4 != null && !var4.isEmpty()) {
         LootContext var5 = EntityPredicate.createContext(var1, var1);
         ArrayList var6 = null;
         Iterator var7 = var4.iterator();

         while(true) {
            CriterionTrigger.Listener var8;
            Optional var10;
            do {
               SimpleInstance var9;
               do {
                  if (!var7.hasNext()) {
                     if (var6 != null) {
                        var7 = var6.iterator();

                        while(var7.hasNext()) {
                           var8 = (CriterionTrigger.Listener)var7.next();
                           var8.run(var3);
                        }
                     }

                     return;
                  }

                  var8 = (CriterionTrigger.Listener)var7.next();
                  var9 = (SimpleInstance)var8.trigger();
               } while(!var2.test(var9));

               var10 = var9.player();
            } while(!var10.isEmpty() && !((ContextAwarePredicate)var10.get()).matches(var5));

            if (var6 == null) {
               var6 = Lists.newArrayList();
            }

            var6.add(var8);
         }
      }
   }

   public interface SimpleInstance extends CriterionTriggerInstance {
      default void validate(CriterionValidator var1) {
         var1.validateEntity(this.player(), ".player");
      }

      Optional<ContextAwarePredicate> player();
   }
}
