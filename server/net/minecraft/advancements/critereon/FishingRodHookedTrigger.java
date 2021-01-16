package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class FishingRodHookedTrigger extends SimpleCriterionTrigger<FishingRodHookedTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("fishing_rod_hooked");

   public FishingRodHookedTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public FishingRodHookedTrigger.TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      ItemPredicate var4 = ItemPredicate.fromJson(var1.get("rod"));
      EntityPredicate.Composite var5 = EntityPredicate.Composite.fromJson(var1, "entity", var3);
      ItemPredicate var6 = ItemPredicate.fromJson(var1.get("item"));
      return new FishingRodHookedTrigger.TriggerInstance(var2, var4, var5, var6);
   }

   public void trigger(ServerPlayer var1, ItemStack var2, FishingHook var3, Collection<ItemStack> var4) {
      LootContext var5 = EntityPredicate.createContext(var1, (Entity)(var3.getHookedIn() != null ? var3.getHookedIn() : var3));
      this.trigger(var1, (var3x) -> {
         return var3x.matches(var2, var5, var4);
      });
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate rod;
      private final EntityPredicate.Composite entity;
      private final ItemPredicate item;

      public TriggerInstance(EntityPredicate.Composite var1, ItemPredicate var2, EntityPredicate.Composite var3, ItemPredicate var4) {
         super(FishingRodHookedTrigger.ID, var1);
         this.rod = var2;
         this.entity = var3;
         this.item = var4;
      }

      public static FishingRodHookedTrigger.TriggerInstance fishedItem(ItemPredicate var0, EntityPredicate var1, ItemPredicate var2) {
         return new FishingRodHookedTrigger.TriggerInstance(EntityPredicate.Composite.ANY, var0, EntityPredicate.Composite.wrap(var1), var2);
      }

      public boolean matches(ItemStack var1, LootContext var2, Collection<ItemStack> var3) {
         if (!this.rod.matches(var1)) {
            return false;
         } else if (!this.entity.matches(var2)) {
            return false;
         } else {
            if (this.item != ItemPredicate.ANY) {
               boolean var4 = false;
               Entity var5 = (Entity)var2.getParamOrNull(LootContextParams.THIS_ENTITY);
               if (var5 instanceof ItemEntity) {
                  ItemEntity var6 = (ItemEntity)var5;
                  if (this.item.matches(var6.getItem())) {
                     var4 = true;
                  }
               }

               Iterator var8 = var3.iterator();

               while(var8.hasNext()) {
                  ItemStack var7 = (ItemStack)var8.next();
                  if (this.item.matches(var7)) {
                     var4 = true;
                     break;
                  }
               }

               if (!var4) {
                  return false;
               }
            }

            return true;
         }
      }

      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("rod", this.rod.serializeToJson());
         var2.add("entity", this.entity.toJson(var1));
         var2.add("item", this.item.serializeToJson());
         return var2;
      }
   }
}
