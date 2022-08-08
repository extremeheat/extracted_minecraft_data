package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;

public class EffectsChangedTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("effects_changed");

   public EffectsChangedTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      MobEffectsPredicate var4 = MobEffectsPredicate.fromJson(var1.get("effects"));
      EntityPredicate.Composite var5 = EntityPredicate.Composite.fromJson(var1, "source", var3);
      return new TriggerInstance(var2, var4, var5);
   }

   public void trigger(ServerPlayer var1, @Nullable Entity var2) {
      LootContext var3 = var2 != null ? EntityPredicate.createContext(var1, var2) : null;
      this.trigger(var1, (var2x) -> {
         return var2x.matches(var1, var3);
      });
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MobEffectsPredicate effects;
      private final EntityPredicate.Composite source;

      public TriggerInstance(EntityPredicate.Composite var1, MobEffectsPredicate var2, EntityPredicate.Composite var3) {
         super(EffectsChangedTrigger.ID, var1);
         this.effects = var2;
         this.source = var3;
      }

      public static TriggerInstance hasEffects(MobEffectsPredicate var0) {
         return new TriggerInstance(EntityPredicate.Composite.ANY, var0, EntityPredicate.Composite.ANY);
      }

      public static TriggerInstance gotEffectsFrom(EntityPredicate var0) {
         return new TriggerInstance(EntityPredicate.Composite.ANY, MobEffectsPredicate.ANY, EntityPredicate.Composite.wrap(var0));
      }

      public boolean matches(ServerPlayer var1, @Nullable LootContext var2) {
         if (!this.effects.matches((LivingEntity)var1)) {
            return false;
         } else {
            return this.source == EntityPredicate.Composite.ANY || var2 != null && this.source.matches(var2);
         }
      }

      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("effects", this.effects.serializeToJson());
         var2.add("source", this.source.toJson(var1));
         return var2;
      }
   }
}
