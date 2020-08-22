package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class ChanneledLightningTrigger extends SimpleCriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("channeled_lightning");

   public ResourceLocation getId() {
      return ID;
   }

   public ChanneledLightningTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      EntityPredicate[] var3 = EntityPredicate.fromJsonArray(var1.get("victims"));
      return new ChanneledLightningTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer var1, Collection var2) {
      this.trigger(var1.getAdvancements(), (var2x) -> {
         return var2x.matches(var1, var2);
      });
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate[] victims;

      public TriggerInstance(EntityPredicate[] var1) {
         super(ChanneledLightningTrigger.ID);
         this.victims = var1;
      }

      public static ChanneledLightningTrigger.TriggerInstance channeledLightning(EntityPredicate... var0) {
         return new ChanneledLightningTrigger.TriggerInstance(var0);
      }

      public boolean matches(ServerPlayer var1, Collection var2) {
         EntityPredicate[] var3 = this.victims;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            EntityPredicate var6 = var3[var5];
            boolean var7 = false;
            Iterator var8 = var2.iterator();

            while(var8.hasNext()) {
               Entity var9 = (Entity)var8.next();
               if (var6.matches(var1, var9)) {
                  var7 = true;
                  break;
               }
            }

            if (!var7) {
               return false;
            }
         }

         return true;
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("victims", EntityPredicate.serializeArrayToJson(this.victims));
         return var1;
      }
   }
}
