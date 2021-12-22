package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class ShotCrossbowTrigger extends SimpleCriterionTrigger<ShotCrossbowTrigger.TriggerInstance> {
   // $FF: renamed from: ID net.minecraft.resources.ResourceLocation
   static final ResourceLocation field_90 = new ResourceLocation("shot_crossbow");

   public ShotCrossbowTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return field_90;
   }

   public ShotCrossbowTrigger.TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      ItemPredicate var4 = ItemPredicate.fromJson(var1.get("item"));
      return new ShotCrossbowTrigger.TriggerInstance(var2, var4);
   }

   public void trigger(ServerPlayer var1, ItemStack var2) {
      this.trigger(var1, (var1x) -> {
         return var1x.matches(var2);
      });
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;

      public TriggerInstance(EntityPredicate.Composite var1, ItemPredicate var2) {
         super(ShotCrossbowTrigger.field_90, var1);
         this.item = var2;
      }

      public static ShotCrossbowTrigger.TriggerInstance shotCrossbow(ItemPredicate var0) {
         return new ShotCrossbowTrigger.TriggerInstance(EntityPredicate.Composite.ANY, var0);
      }

      public static ShotCrossbowTrigger.TriggerInstance shotCrossbow(ItemLike var0) {
         return new ShotCrossbowTrigger.TriggerInstance(EntityPredicate.Composite.ANY, ItemPredicate.Builder.item().method_90(var0).build());
      }

      public boolean matches(ItemStack var1) {
         return this.item.matches(var1);
      }

      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("item", this.item.serializeToJson());
         return var2;
      }
   }
}
