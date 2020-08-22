package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.animal.Animal;

public class BredAnimalsTrigger extends SimpleCriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("bred_animals");

   public ResourceLocation getId() {
      return ID;
   }

   public BredAnimalsTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      EntityPredicate var3 = EntityPredicate.fromJson(var1.get("parent"));
      EntityPredicate var4 = EntityPredicate.fromJson(var1.get("partner"));
      EntityPredicate var5 = EntityPredicate.fromJson(var1.get("child"));
      return new BredAnimalsTrigger.TriggerInstance(var3, var4, var5);
   }

   public void trigger(ServerPlayer var1, Animal var2, @Nullable Animal var3, @Nullable AgableMob var4) {
      this.trigger(var1.getAdvancements(), (var4x) -> {
         return var4x.matches(var1, var2, var3, var4);
      });
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate parent;
      private final EntityPredicate partner;
      private final EntityPredicate child;

      public TriggerInstance(EntityPredicate var1, EntityPredicate var2, EntityPredicate var3) {
         super(BredAnimalsTrigger.ID);
         this.parent = var1;
         this.partner = var2;
         this.child = var3;
      }

      public static BredAnimalsTrigger.TriggerInstance bredAnimals() {
         return new BredAnimalsTrigger.TriggerInstance(EntityPredicate.ANY, EntityPredicate.ANY, EntityPredicate.ANY);
      }

      public static BredAnimalsTrigger.TriggerInstance bredAnimals(EntityPredicate.Builder var0) {
         return new BredAnimalsTrigger.TriggerInstance(var0.build(), EntityPredicate.ANY, EntityPredicate.ANY);
      }

      public boolean matches(ServerPlayer var1, Animal var2, @Nullable Animal var3, @Nullable AgableMob var4) {
         if (!this.child.matches(var1, var4)) {
            return false;
         } else {
            return this.parent.matches(var1, var2) && this.partner.matches(var1, var3) || this.parent.matches(var1, var3) && this.partner.matches(var1, var2);
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("parent", this.parent.serializeToJson());
         var1.add("partner", this.partner.serializeToJson());
         var1.add("child", this.child.serializeToJson());
         return var1;
      }
   }
}
