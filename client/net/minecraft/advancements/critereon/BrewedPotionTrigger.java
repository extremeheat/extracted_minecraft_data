package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.alchemy.Potion;

public class BrewedPotionTrigger extends SimpleCriterionTrigger<BrewedPotionTrigger.TriggerInstance> {
   public BrewedPotionTrigger() {
      super();
   }

   public BrewedPotionTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      Potion var4 = null;
      if (var1.has("potion")) {
         ResourceLocation var5 = new ResourceLocation(GsonHelper.getAsString(var1, "potion"));
         var4 = BuiltInRegistries.POTION.getOptional(var5).orElseThrow(() -> new JsonSyntaxException("Unknown potion '" + var5 + "'"));
      }

      return new BrewedPotionTrigger.TriggerInstance(var2, var4);
   }

   public void trigger(ServerPlayer var1, Potion var2) {
      this.trigger(var1, var1x -> var1x.matches(var2));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      @Nullable
      private final Potion potion;

      public TriggerInstance(Optional<ContextAwarePredicate> var1, @Nullable Potion var2) {
         super(var1);
         this.potion = var2;
      }

      public static Criterion<BrewedPotionTrigger.TriggerInstance> brewedPotion() {
         return CriteriaTriggers.BREWED_POTION.createCriterion(new BrewedPotionTrigger.TriggerInstance(Optional.empty(), null));
      }

      public boolean matches(Potion var1) {
         return this.potion == null || this.potion == var1;
      }

      @Override
      public JsonObject serializeToJson() {
         JsonObject var1 = super.serializeToJson();
         if (this.potion != null) {
            var1.addProperty("potion", BuiltInRegistries.POTION.getKey(this.potion).toString());
         }

         return var1;
      }
   }
}
