package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.storage.loot.LootContext;

public class BredAnimalsTrigger extends SimpleCriterionTrigger<BredAnimalsTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("bred_animals");

   public BredAnimalsTrigger() {
      super();
   }

   @Override
   public ResourceLocation getId() {
      return ID;
   }

   public BredAnimalsTrigger.TriggerInstance createInstance(JsonObject var1, ContextAwarePredicate var2, DeserializationContext var3) {
      ContextAwarePredicate var4 = EntityPredicate.fromJson(var1, "parent", var3);
      ContextAwarePredicate var5 = EntityPredicate.fromJson(var1, "partner", var3);
      ContextAwarePredicate var6 = EntityPredicate.fromJson(var1, "child", var3);
      return new BredAnimalsTrigger.TriggerInstance(var2, var4, var5, var6);
   }

   public void trigger(ServerPlayer var1, Animal var2, Animal var3, @Nullable AgeableMob var4) {
      LootContext var5 = EntityPredicate.createContext(var1, var2);
      LootContext var6 = EntityPredicate.createContext(var1, var3);
      LootContext var7 = var4 != null ? EntityPredicate.createContext(var1, var4) : null;
      this.trigger(var1, var3x -> var3x.matches(var5, var6, var7));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ContextAwarePredicate parent;
      private final ContextAwarePredicate partner;
      private final ContextAwarePredicate child;

      public TriggerInstance(ContextAwarePredicate var1, ContextAwarePredicate var2, ContextAwarePredicate var3, ContextAwarePredicate var4) {
         super(BredAnimalsTrigger.ID, var1);
         this.parent = var2;
         this.partner = var3;
         this.child = var4;
      }

      public static BredAnimalsTrigger.TriggerInstance bredAnimals() {
         return new BredAnimalsTrigger.TriggerInstance(
            ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, ContextAwarePredicate.ANY
         );
      }

      public static BredAnimalsTrigger.TriggerInstance bredAnimals(EntityPredicate.Builder var0) {
         return new BredAnimalsTrigger.TriggerInstance(
            ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, EntityPredicate.wrap(var0.build())
         );
      }

      public static BredAnimalsTrigger.TriggerInstance bredAnimals(EntityPredicate var0, EntityPredicate var1, EntityPredicate var2) {
         return new BredAnimalsTrigger.TriggerInstance(
            ContextAwarePredicate.ANY, EntityPredicate.wrap(var0), EntityPredicate.wrap(var1), EntityPredicate.wrap(var2)
         );
      }

      public boolean matches(LootContext var1, LootContext var2, @Nullable LootContext var3) {
         if (this.child == ContextAwarePredicate.ANY || var3 != null && this.child.matches(var3)) {
            return this.parent.matches(var1) && this.partner.matches(var2) || this.parent.matches(var2) && this.partner.matches(var1);
         } else {
            return false;
         }
      }

      @Override
      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("parent", this.parent.toJson(var1));
         var2.add("partner", this.partner.toJson(var1));
         var2.add("child", this.child.toJson(var1));
         return var2;
      }
   }
}
