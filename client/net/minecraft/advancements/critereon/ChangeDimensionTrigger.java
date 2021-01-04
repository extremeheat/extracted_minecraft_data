package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.dimension.DimensionType;

public class ChangeDimensionTrigger implements CriterionTrigger<ChangeDimensionTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("changed_dimension");
   private final Map<PlayerAdvancements, ChangeDimensionTrigger.PlayerListeners> players = Maps.newHashMap();

   public ChangeDimensionTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<ChangeDimensionTrigger.TriggerInstance> var2) {
      ChangeDimensionTrigger.PlayerListeners var3 = (ChangeDimensionTrigger.PlayerListeners)this.players.get(var1);
      if (var3 == null) {
         var3 = new ChangeDimensionTrigger.PlayerListeners(var1);
         this.players.put(var1, var3);
      }

      var3.addListener(var2);
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<ChangeDimensionTrigger.TriggerInstance> var2) {
      ChangeDimensionTrigger.PlayerListeners var3 = (ChangeDimensionTrigger.PlayerListeners)this.players.get(var1);
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

   public ChangeDimensionTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      DimensionType var3 = var1.has("from") ? DimensionType.getByName(new ResourceLocation(GsonHelper.getAsString(var1, "from"))) : null;
      DimensionType var4 = var1.has("to") ? DimensionType.getByName(new ResourceLocation(GsonHelper.getAsString(var1, "to"))) : null;
      return new ChangeDimensionTrigger.TriggerInstance(var3, var4);
   }

   public void trigger(ServerPlayer var1, DimensionType var2, DimensionType var3) {
      ChangeDimensionTrigger.PlayerListeners var4 = (ChangeDimensionTrigger.PlayerListeners)this.players.get(var1.getAdvancements());
      if (var4 != null) {
         var4.trigger(var2, var3);
      }

   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   static class PlayerListeners {
      private final PlayerAdvancements player;
      private final Set<CriterionTrigger.Listener<ChangeDimensionTrigger.TriggerInstance>> listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements var1) {
         super();
         this.player = var1;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener<ChangeDimensionTrigger.TriggerInstance> var1) {
         this.listeners.add(var1);
      }

      public void removeListener(CriterionTrigger.Listener<ChangeDimensionTrigger.TriggerInstance> var1) {
         this.listeners.remove(var1);
      }

      public void trigger(DimensionType var1, DimensionType var2) {
         ArrayList var3 = null;
         Iterator var4 = this.listeners.iterator();

         CriterionTrigger.Listener var5;
         while(var4.hasNext()) {
            var5 = (CriterionTrigger.Listener)var4.next();
            if (((ChangeDimensionTrigger.TriggerInstance)var5.getTriggerInstance()).matches(var1, var2)) {
               if (var3 == null) {
                  var3 = Lists.newArrayList();
               }

               var3.add(var5);
            }
         }

         if (var3 != null) {
            var4 = var3.iterator();

            while(var4.hasNext()) {
               var5 = (CriterionTrigger.Listener)var4.next();
               var5.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      @Nullable
      private final DimensionType from;
      @Nullable
      private final DimensionType to;

      public TriggerInstance(@Nullable DimensionType var1, @Nullable DimensionType var2) {
         super(ChangeDimensionTrigger.ID);
         this.from = var1;
         this.to = var2;
      }

      public static ChangeDimensionTrigger.TriggerInstance changedDimensionTo(DimensionType var0) {
         return new ChangeDimensionTrigger.TriggerInstance((DimensionType)null, var0);
      }

      public boolean matches(DimensionType var1, DimensionType var2) {
         if (this.from != null && this.from != var1) {
            return false;
         } else {
            return this.to == null || this.to == var2;
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         if (this.from != null) {
            var1.addProperty("from", DimensionType.getName(this.from).toString());
         }

         if (this.to != null) {
            var1.addProperty("to", DimensionType.getName(this.to).toString());
         }

         return var1;
      }
   }
}
