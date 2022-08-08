package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class PickedUpItemTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   private final ResourceLocation id;

   public PickedUpItemTrigger(ResourceLocation var1) {
      super();
      this.id = var1;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   protected TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      ItemPredicate var4 = ItemPredicate.fromJson(var1.get("item"));
      EntityPredicate.Composite var5 = EntityPredicate.Composite.fromJson(var1, "entity", var3);
      return new TriggerInstance(this.id, var2, var4, var5);
   }

   public void trigger(ServerPlayer var1, ItemStack var2, @Nullable Entity var3) {
      LootContext var4 = EntityPredicate.createContext(var1, var3);
      this.trigger(var1, (var3x) -> {
         return var3x.matches(var1, var2, var4);
      });
   }

   // $FF: synthetic method
   protected AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;
      private final EntityPredicate.Composite entity;

      public TriggerInstance(ResourceLocation var1, EntityPredicate.Composite var2, ItemPredicate var3, EntityPredicate.Composite var4) {
         super(var1, var2);
         this.item = var3;
         this.entity = var4;
      }

      public static TriggerInstance thrownItemPickedUpByEntity(EntityPredicate.Composite var0, ItemPredicate var1, EntityPredicate.Composite var2) {
         return new TriggerInstance(CriteriaTriggers.THROWN_ITEM_PICKED_UP_BY_ENTITY.getId(), var0, var1, var2);
      }

      public static TriggerInstance thrownItemPickedUpByPlayer(EntityPredicate.Composite var0, ItemPredicate var1, EntityPredicate.Composite var2) {
         return new TriggerInstance(CriteriaTriggers.THROWN_ITEM_PICKED_UP_BY_PLAYER.getId(), var0, var1, var2);
      }

      public boolean matches(ServerPlayer var1, ItemStack var2, LootContext var3) {
         if (!this.item.matches(var2)) {
            return false;
         } else {
            return this.entity.matches(var3);
         }
      }

      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("item", this.item.serializeToJson());
         var2.add("entity", this.entity.toJson(var1));
         return var2;
      }
   }
}
