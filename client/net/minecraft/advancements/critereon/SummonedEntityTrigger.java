package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class SummonedEntityTrigger extends SimpleCriterionTrigger<SummonedEntityTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("summoned_entity");

   public SummonedEntityTrigger() {
      super();
   }

   @Override
   public ResourceLocation getId() {
      return ID;
   }

   public SummonedEntityTrigger.TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      EntityPredicate.Composite var4 = EntityPredicate.Composite.fromJson(var1, "entity", var3);
      return new SummonedEntityTrigger.TriggerInstance(var2, var4);
   }

   public void trigger(ServerPlayer var1, Entity var2) {
      LootContext var3 = EntityPredicate.createContext(var1, var2);
      this.trigger(var1, var1x -> var1x.matches(var3));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate.Composite entity;

      public TriggerInstance(EntityPredicate.Composite var1, EntityPredicate.Composite var2) {
         super(SummonedEntityTrigger.ID, var1);
         this.entity = var2;
      }

      public static SummonedEntityTrigger.TriggerInstance summonedEntity(EntityPredicate.Builder var0) {
         return new SummonedEntityTrigger.TriggerInstance(EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap(var0.build()));
      }

      public boolean matches(LootContext var1) {
         return this.entity.matches(var1);
      }

      @Override
      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("entity", this.entity.toJson(var1));
         return var2;
      }
   }
}
