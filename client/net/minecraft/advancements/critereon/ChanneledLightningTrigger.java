package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class ChanneledLightningTrigger extends SimpleCriterionTrigger<ChanneledLightningTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("channeled_lightning");

   public ChanneledLightningTrigger() {
      super();
   }

   @Override
   public ResourceLocation getId() {
      return ID;
   }

   public ChanneledLightningTrigger.TriggerInstance createInstance(JsonObject var1, ContextAwarePredicate var2, DeserializationContext var3) {
      ContextAwarePredicate[] var4 = EntityPredicate.fromJsonArray(var1, "victims", var3);
      return new ChanneledLightningTrigger.TriggerInstance(var2, var4);
   }

   public void trigger(ServerPlayer var1, Collection<? extends Entity> var2) {
      List var3 = var2.stream().map(var1x -> EntityPredicate.createContext(var1, var1x)).collect(Collectors.toList());
      this.trigger(var1, var1x -> var1x.matches(var3));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ContextAwarePredicate[] victims;

      public TriggerInstance(ContextAwarePredicate var1, ContextAwarePredicate[] var2) {
         super(ChanneledLightningTrigger.ID, var1);
         this.victims = var2;
      }

      public static ChanneledLightningTrigger.TriggerInstance channeledLightning(EntityPredicate... var0) {
         return new ChanneledLightningTrigger.TriggerInstance(
            ContextAwarePredicate.ANY, Stream.of(var0).map(EntityPredicate::wrap).toArray(var0x -> new ContextAwarePredicate[var0x])
         );
      }

      public boolean matches(Collection<? extends LootContext> var1) {
         for(ContextAwarePredicate var5 : this.victims) {
            boolean var6 = false;

            for(LootContext var8 : var1) {
               if (var5.matches(var8)) {
                  var6 = true;
                  break;
               }
            }

            if (!var6) {
               return false;
            }
         }

         return true;
      }

      @Override
      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("victims", ContextAwarePredicate.toJson(this.victims, var1));
         return var2;
      }
   }
}
