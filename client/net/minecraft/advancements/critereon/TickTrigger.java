package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;

public class TickTrigger implements CriterionTrigger<TickTrigger.TriggerInstance> {
   public static final ResourceLocation ID = new ResourceLocation("tick");
   private final Map<PlayerAdvancements, TickTrigger.PlayerListeners> players = Maps.newHashMap();

   public TickTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<TickTrigger.TriggerInstance> var2) {
      TickTrigger.PlayerListeners var3 = (TickTrigger.PlayerListeners)this.players.get(var1);
      if (var3 == null) {
         var3 = new TickTrigger.PlayerListeners(var1);
         this.players.put(var1, var3);
      }

      var3.addListener(var2);
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<TickTrigger.TriggerInstance> var2) {
      TickTrigger.PlayerListeners var3 = (TickTrigger.PlayerListeners)this.players.get(var1);
      if (var3 != null) {
         var3.removeListener(var2);
         if (var3.isEmpty()) {
            this.players.remove(var1);
         }
      }

   }

   public void removePlayerListeners(PlayerAdvancements var1) {
      this.players.remove(var1);
   }

   public TickTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return new TickTrigger.TriggerInstance();
   }

   public void trigger(ServerPlayer var1) {
      TickTrigger.PlayerListeners var2 = (TickTrigger.PlayerListeners)this.players.get(var1.getAdvancements());
      if (var2 != null) {
         var2.trigger();
      }

   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   static class PlayerListeners {
      private final PlayerAdvancements player;
      private final Set<CriterionTrigger.Listener<TickTrigger.TriggerInstance>> listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements var1) {
         super();
         this.player = var1;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener<TickTrigger.TriggerInstance> var1) {
         this.listeners.add(var1);
      }

      public void removeListener(CriterionTrigger.Listener<TickTrigger.TriggerInstance> var1) {
         this.listeners.remove(var1);
      }

      public void trigger() {
         Iterator var1 = Lists.newArrayList(this.listeners).iterator();

         while(var1.hasNext()) {
            CriterionTrigger.Listener var2 = (CriterionTrigger.Listener)var1.next();
            var2.run(this.player);
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      public TriggerInstance() {
         super(TickTrigger.ID);
      }
   }
}
