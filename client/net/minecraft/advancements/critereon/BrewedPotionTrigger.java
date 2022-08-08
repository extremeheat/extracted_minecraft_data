package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.alchemy.Potion;

public class BrewedPotionTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("brewed_potion");

   public BrewedPotionTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      Potion var4 = null;
      if (var1.has("potion")) {
         ResourceLocation var5 = new ResourceLocation(GsonHelper.getAsString(var1, "potion"));
         var4 = (Potion)Registry.POTION.getOptional(var5).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown potion '" + var5 + "'");
         });
      }

      return new TriggerInstance(var2, var4);
   }

   public void trigger(ServerPlayer var1, Potion var2) {
      this.trigger(var1, (var1x) -> {
         return var1x.matches(var2);
      });
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      @Nullable
      private final Potion potion;

      public TriggerInstance(EntityPredicate.Composite var1, @Nullable Potion var2) {
         super(BrewedPotionTrigger.ID, var1);
         this.potion = var2;
      }

      public static TriggerInstance brewedPotion() {
         return new TriggerInstance(EntityPredicate.Composite.ANY, (Potion)null);
      }

      public boolean matches(Potion var1) {
         return this.potion == null || this.potion == var1;
      }

      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         if (this.potion != null) {
            var2.addProperty("potion", Registry.POTION.getKey(this.potion).toString());
         }

         return var2;
      }
   }
}
