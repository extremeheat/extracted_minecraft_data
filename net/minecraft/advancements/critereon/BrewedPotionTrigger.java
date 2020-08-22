package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.alchemy.Potion;

public class BrewedPotionTrigger extends SimpleCriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("brewed_potion");

   public ResourceLocation getId() {
      return ID;
   }

   public BrewedPotionTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      Potion var3 = null;
      if (var1.has("potion")) {
         ResourceLocation var4 = new ResourceLocation(GsonHelper.getAsString(var1, "potion"));
         var3 = (Potion)Registry.POTION.getOptional(var4).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown potion '" + var4 + "'");
         });
      }

      return new BrewedPotionTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer var1, Potion var2) {
      this.trigger(var1.getAdvancements(), (var1x) -> {
         return var1x.matches(var2);
      });
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final Potion potion;

      public TriggerInstance(@Nullable Potion var1) {
         super(BrewedPotionTrigger.ID);
         this.potion = var1;
      }

      public static BrewedPotionTrigger.TriggerInstance brewedPotion() {
         return new BrewedPotionTrigger.TriggerInstance((Potion)null);
      }

      public boolean matches(Potion var1) {
         return this.potion == null || this.potion == var1;
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         if (this.potion != null) {
            var1.addProperty("potion", Registry.POTION.getKey(this.potion).toString());
         }

         return var1;
      }
   }
}
