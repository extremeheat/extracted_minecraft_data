package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.storage.loot.LootContext;

public class LightningStrikeTrigger extends SimpleCriterionTrigger<LightningStrikeTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("lightning_strike");

   public LightningStrikeTrigger() {
      super();
   }

   @Override
   public ResourceLocation getId() {
      return ID;
   }

   public LightningStrikeTrigger.TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      EntityPredicate.Composite var4 = EntityPredicate.Composite.fromJson(var1, "lightning", var3);
      EntityPredicate.Composite var5 = EntityPredicate.Composite.fromJson(var1, "bystander", var3);
      return new LightningStrikeTrigger.TriggerInstance(var2, var4, var5);
   }

   public void trigger(ServerPlayer var1, LightningBolt var2, List<Entity> var3) {
      List var4 = var3.stream().map(var1x -> EntityPredicate.createContext(var1, var1x)).collect(Collectors.toList());
      LootContext var5 = EntityPredicate.createContext(var1, var2);
      this.trigger(var1, var2x -> var2x.matches(var5, var4));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate.Composite lightning;
      private final EntityPredicate.Composite bystander;

      public TriggerInstance(EntityPredicate.Composite var1, EntityPredicate.Composite var2, EntityPredicate.Composite var3) {
         super(LightningStrikeTrigger.ID, var1);
         this.lightning = var2;
         this.bystander = var3;
      }

      public static LightningStrikeTrigger.TriggerInstance lighthingStrike(EntityPredicate var0, EntityPredicate var1) {
         return new LightningStrikeTrigger.TriggerInstance(
            EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap(var0), EntityPredicate.Composite.wrap(var1)
         );
      }

      public boolean matches(LootContext var1, List<LootContext> var2) {
         if (!this.lightning.matches(var1)) {
            return false;
         } else {
            return this.bystander == EntityPredicate.Composite.ANY || !var2.stream().noneMatch(this.bystander::matches);
         }
      }

      @Override
      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("lightning", this.lightning.toJson(var1));
         var2.add("bystander", this.bystander.toJson(var1));
         return var2;
      }
   }
}
