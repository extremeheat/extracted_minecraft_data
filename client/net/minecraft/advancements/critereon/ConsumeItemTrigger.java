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
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.ItemLike;

public class ConsumeItemTrigger implements CriterionTrigger<ConsumeItemTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("consume_item");
   private final Map<PlayerAdvancements, ConsumeItemTrigger.PlayerListeners> players = Maps.newHashMap();

   public ConsumeItemTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<ConsumeItemTrigger.TriggerInstance> var2) {
      ConsumeItemTrigger.PlayerListeners var3 = (ConsumeItemTrigger.PlayerListeners)this.players.get(var1);
      if (var3 == null) {
         var3 = new ConsumeItemTrigger.PlayerListeners(var1);
         this.players.put(var1, var3);
      }

      var3.addListener(var2);
   }

   public void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener<ConsumeItemTrigger.TriggerInstance> var2) {
      ConsumeItemTrigger.PlayerListeners var3 = (ConsumeItemTrigger.PlayerListeners)this.players.get(var1);
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

   public ConsumeItemTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return new ConsumeItemTrigger.TriggerInstance(ItemPredicate.fromJson(var1.get("item")));
   }

   public void trigger(ServerPlayer var1, ItemStack var2) {
      ConsumeItemTrigger.PlayerListeners var3 = (ConsumeItemTrigger.PlayerListeners)this.players.get(var1.getAdvancements());
      if (var3 != null) {
         var3.trigger(var2);
      }

   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   static class PlayerListeners {
      private final PlayerAdvancements player;
      private final Set<CriterionTrigger.Listener<ConsumeItemTrigger.TriggerInstance>> listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements var1) {
         super();
         this.player = var1;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener<ConsumeItemTrigger.TriggerInstance> var1) {
         this.listeners.add(var1);
      }

      public void removeListener(CriterionTrigger.Listener<ConsumeItemTrigger.TriggerInstance> var1) {
         this.listeners.remove(var1);
      }

      public void trigger(ItemStack var1) {
         ArrayList var2 = null;
         Iterator var3 = this.listeners.iterator();

         CriterionTrigger.Listener var4;
         while(var3.hasNext()) {
            var4 = (CriterionTrigger.Listener)var3.next();
            if (((ConsumeItemTrigger.TriggerInstance)var4.getTriggerInstance()).matches(var1)) {
               if (var2 == null) {
                  var2 = Lists.newArrayList();
               }

               var2.add(var4);
            }
         }

         if (var2 != null) {
            var3 = var2.iterator();

            while(var3.hasNext()) {
               var4 = (CriterionTrigger.Listener)var3.next();
               var4.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;

      public TriggerInstance(ItemPredicate var1) {
         super(ConsumeItemTrigger.ID);
         this.item = var1;
      }

      public static ConsumeItemTrigger.TriggerInstance usedItem() {
         return new ConsumeItemTrigger.TriggerInstance(ItemPredicate.ANY);
      }

      public static ConsumeItemTrigger.TriggerInstance usedItem(ItemLike var0) {
         return new ConsumeItemTrigger.TriggerInstance(new ItemPredicate((Tag)null, var0.asItem(), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, new EnchantmentPredicate[0], (Potion)null, NbtPredicate.ANY));
      }

      public boolean matches(ItemStack var1) {
         return this.item.matches(var1);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("item", this.item.serializeToJson());
         return var1;
      }
   }
}
