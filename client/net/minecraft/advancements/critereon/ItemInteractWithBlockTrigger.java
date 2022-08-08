package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class ItemInteractWithBlockTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   final ResourceLocation id;

   public ItemInteractWithBlockTrigger(ResourceLocation var1) {
      super();
      this.id = var1;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      LocationPredicate var4 = LocationPredicate.fromJson(var1.get("location"));
      ItemPredicate var5 = ItemPredicate.fromJson(var1.get("item"));
      return new TriggerInstance(this.id, var2, var4, var5);
   }

   public void trigger(ServerPlayer var1, BlockPos var2, ItemStack var3) {
      BlockState var4 = var1.getLevel().getBlockState(var2);
      this.trigger(var1, (var4x) -> {
         return var4x.matches(var4, var1.getLevel(), var2, var3);
      });
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final LocationPredicate location;
      private final ItemPredicate item;

      public TriggerInstance(ResourceLocation var1, EntityPredicate.Composite var2, LocationPredicate var3, ItemPredicate var4) {
         super(var1, var2);
         this.location = var3;
         this.item = var4;
      }

      public static TriggerInstance itemUsedOnBlock(LocationPredicate.Builder var0, ItemPredicate.Builder var1) {
         return new TriggerInstance(CriteriaTriggers.ITEM_USED_ON_BLOCK.id, EntityPredicate.Composite.ANY, var0.build(), var1.build());
      }

      public static TriggerInstance allayDropItemOnBlock(LocationPredicate.Builder var0, ItemPredicate.Builder var1) {
         return new TriggerInstance(CriteriaTriggers.ALLAY_DROP_ITEM_ON_BLOCK.id, EntityPredicate.Composite.ANY, var0.build(), var1.build());
      }

      public boolean matches(BlockState var1, ServerLevel var2, BlockPos var3, ItemStack var4) {
         return !this.location.matches(var2, (double)var3.getX() + 0.5, (double)var3.getY() + 0.5, (double)var3.getZ() + 0.5) ? false : this.item.matches(var4);
      }

      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("location", this.location.serializeToJson());
         var2.add("item", this.item.serializeToJson());
         return var2;
      }
   }
}
