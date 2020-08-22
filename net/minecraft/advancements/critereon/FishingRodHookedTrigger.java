package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.fishing.FishingHook;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class FishingRodHookedTrigger extends SimpleCriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("fishing_rod_hooked");

   public ResourceLocation getId() {
      return ID;
   }

   public FishingRodHookedTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      ItemPredicate var3 = ItemPredicate.fromJson(var1.get("rod"));
      EntityPredicate var4 = EntityPredicate.fromJson(var1.get("entity"));
      ItemPredicate var5 = ItemPredicate.fromJson(var1.get("item"));
      return new FishingRodHookedTrigger.TriggerInstance(var3, var4, var5);
   }

   public void trigger(ServerPlayer var1, ItemStack var2, FishingHook var3, Collection var4) {
      this.trigger(var1.getAdvancements(), (var4x) -> {
         return var4x.matches(var1, var2, var3, var4);
      });
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate rod;
      private final EntityPredicate entity;
      private final ItemPredicate item;

      public TriggerInstance(ItemPredicate var1, EntityPredicate var2, ItemPredicate var3) {
         super(FishingRodHookedTrigger.ID);
         this.rod = var1;
         this.entity = var2;
         this.item = var3;
      }

      public static FishingRodHookedTrigger.TriggerInstance fishedItem(ItemPredicate var0, EntityPredicate var1, ItemPredicate var2) {
         return new FishingRodHookedTrigger.TriggerInstance(var0, var1, var2);
      }

      public boolean matches(ServerPlayer var1, ItemStack var2, FishingHook var3, Collection var4) {
         if (!this.rod.matches(var2)) {
            return false;
         } else if (!this.entity.matches(var1, var3.hookedIn)) {
            return false;
         } else {
            if (this.item != ItemPredicate.ANY) {
               boolean var5 = false;
               if (var3.hookedIn instanceof ItemEntity) {
                  ItemEntity var6 = (ItemEntity)var3.hookedIn;
                  if (this.item.matches(var6.getItem())) {
                     var5 = true;
                  }
               }

               Iterator var8 = var4.iterator();

               while(var8.hasNext()) {
                  ItemStack var7 = (ItemStack)var8.next();
                  if (this.item.matches(var7)) {
                     var5 = true;
                     break;
                  }
               }

               if (!var5) {
                  return false;
               }
            }

            return true;
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("rod", this.rod.serializeToJson());
         var1.add("entity", this.entity.serializeToJson());
         var1.add("item", this.item.serializeToJson());
         return var1;
      }
   }
}
