package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.dimension.DimensionType;

public class ChangeDimensionTrigger extends SimpleCriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("changed_dimension");

   public ResourceLocation getId() {
      return ID;
   }

   public ChangeDimensionTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      DimensionType var3 = var1.has("from") ? DimensionType.getByName(new ResourceLocation(GsonHelper.getAsString(var1, "from"))) : null;
      DimensionType var4 = var1.has("to") ? DimensionType.getByName(new ResourceLocation(GsonHelper.getAsString(var1, "to"))) : null;
      return new ChangeDimensionTrigger.TriggerInstance(var3, var4);
   }

   public void trigger(ServerPlayer var1, DimensionType var2, DimensionType var3) {
      this.trigger(var1.getAdvancements(), (var2x) -> {
         return var2x.matches(var2, var3);
      });
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      @Nullable
      private final DimensionType from;
      @Nullable
      private final DimensionType to;

      public TriggerInstance(@Nullable DimensionType var1, @Nullable DimensionType var2) {
         super(ChangeDimensionTrigger.ID);
         this.from = var1;
         this.to = var2;
      }

      public static ChangeDimensionTrigger.TriggerInstance changedDimensionTo(DimensionType var0) {
         return new ChangeDimensionTrigger.TriggerInstance((DimensionType)null, var0);
      }

      public boolean matches(DimensionType var1, DimensionType var2) {
         if (this.from != null && this.from != var1) {
            return false;
         } else {
            return this.to == null || this.to == var2;
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         if (this.from != null) {
            var1.addProperty("from", DimensionType.getName(this.from).toString());
         }

         if (this.to != null) {
            var1.addProperty("to", DimensionType.getName(this.to).toString());
         }

         return var1;
      }
   }
}
