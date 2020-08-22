package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.server.PlayerAdvancements;

public abstract class SimpleCriterionTrigger implements CriterionTrigger {
   private final Map players = Maps.newIdentityHashMap();

   public final void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener var2) {
      ((Set)this.players.computeIfAbsent(var1, (var0) -> {
         return Sets.newHashSet();
      })).add(var2);
   }

   public final void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener var2) {
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

   protected void trigger(PlayerAdvancements var1, Predicate var2) {
      Set var3 = (Set)this.players.get(var1);
      if (var3 != null) {
         ArrayList var4 = null;
         Iterator var5 = var3.iterator();

         CriterionTrigger.Listener var6;
         while(var5.hasNext()) {
            var6 = (CriterionTrigger.Listener)var5.next();
            if (var2.test(var6.getTriggerInstance())) {
               if (var4 == null) {
                  var4 = Lists.newArrayList();
               }

               var4.add(var6);
            }
         }

         if (var4 != null) {
            var5 = var4.iterator();

            while(var5.hasNext()) {
               var6 = (CriterionTrigger.Listener)var5.next();
               var6.run(var1);
            }
         }

      }
   }

   protected void trigger(PlayerAdvancements var1) {
      Set var2 = (Set)this.players.get(var1);
      if (var2 != null && !var2.isEmpty()) {
         UnmodifiableIterator var3 = ImmutableSet.copyOf(var2).iterator();

         while(var3.hasNext()) {
            CriterionTrigger.Listener var4 = (CriterionTrigger.Listener)var3.next();
            var4.run(var1);
         }
      }

   }
}
