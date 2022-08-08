package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.storage.loot.LootContext;

public class BredAnimalsTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("bred_animals");

   public BredAnimalsTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      EntityPredicate.Composite var4 = EntityPredicate.Composite.fromJson(var1, "parent", var3);
      EntityPredicate.Composite var5 = EntityPredicate.Composite.fromJson(var1, "partner", var3);
      EntityPredicate.Composite var6 = EntityPredicate.Composite.fromJson(var1, "child", var3);
      return new TriggerInstance(var2, var4, var5, var6);
   }

   public void trigger(ServerPlayer var1, Animal var2, Animal var3, @Nullable AgeableMob var4) {
      LootContext var5 = EntityPredicate.createContext(var1, var2);
      LootContext var6 = EntityPredicate.createContext(var1, var3);
      LootContext var7 = var4 != null ? EntityPredicate.createContext(var1, var4) : null;
      this.trigger(var1, (var3x) -> {
         return var3x.matches(var5, var6, var7);
      });
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate.Composite parent;
      private final EntityPredicate.Composite partner;
      private final EntityPredicate.Composite child;

      public TriggerInstance(EntityPredicate.Composite var1, EntityPredicate.Composite var2, EntityPredicate.Composite var3, EntityPredicate.Composite var4) {
         super(BredAnimalsTrigger.ID, var1);
         this.parent = var2;
         this.partner = var3;
         this.child = var4;
      }

      public static TriggerInstance bredAnimals() {
         return new TriggerInstance(EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY);
      }

      public static TriggerInstance bredAnimals(EntityPredicate.Builder var0) {
         return new TriggerInstance(EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap(var0.build()));
      }

      public static TriggerInstance bredAnimals(EntityPredicate var0, EntityPredicate var1, EntityPredicate var2) {
         return new TriggerInstance(EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap(var0), EntityPredicate.Composite.wrap(var1), EntityPredicate.Composite.wrap(var2));
      }

      public boolean matches(LootContext var1, LootContext var2, @Nullable LootContext var3) {
         if (this.child == EntityPredicate.Composite.ANY || var3 != null && this.child.matches(var3)) {
            return this.parent.matches(var1) && this.partner.matches(var2) || this.parent.matches(var2) && this.partner.matches(var1);
         } else {
            return false;
         }
      }

      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("parent", this.parent.toJson(var1));
         var2.add("partner", this.partner.toJson(var1));
         var2.add("child", this.child.toJson(var1));
         return var2;
      }
   }
}
