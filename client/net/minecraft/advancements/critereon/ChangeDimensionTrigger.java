package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;

public class ChangeDimensionTrigger extends SimpleCriterionTrigger<ChangeDimensionTrigger.TriggerInstance> {
   // $FF: renamed from: ID net.minecraft.resources.ResourceLocation
   static final ResourceLocation field_75 = new ResourceLocation("changed_dimension");

   public ChangeDimensionTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return field_75;
   }

   public ChangeDimensionTrigger.TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      ResourceKey var4 = var1.has("from") ? ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(GsonHelper.getAsString(var1, "from"))) : null;
      ResourceKey var5 = var1.has("to") ? ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(GsonHelper.getAsString(var1, "to"))) : null;
      return new ChangeDimensionTrigger.TriggerInstance(var2, var4, var5);
   }

   public void trigger(ServerPlayer var1, ResourceKey<Level> var2, ResourceKey<Level> var3) {
      this.trigger(var1, (var2x) -> {
         return var2x.matches(var2, var3);
      });
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      @Nullable
      private final ResourceKey<Level> from;
      // $FF: renamed from: to net.minecraft.resources.ResourceKey
      @Nullable
      private final ResourceKey<Level> field_142;

      public TriggerInstance(EntityPredicate.Composite var1, @Nullable ResourceKey<Level> var2, @Nullable ResourceKey<Level> var3) {
         super(ChangeDimensionTrigger.field_75, var1);
         this.from = var2;
         this.field_142 = var3;
      }

      public static ChangeDimensionTrigger.TriggerInstance changedDimension() {
         return new ChangeDimensionTrigger.TriggerInstance(EntityPredicate.Composite.ANY, (ResourceKey)null, (ResourceKey)null);
      }

      public static ChangeDimensionTrigger.TriggerInstance changedDimension(ResourceKey<Level> var0, ResourceKey<Level> var1) {
         return new ChangeDimensionTrigger.TriggerInstance(EntityPredicate.Composite.ANY, var0, var1);
      }

      public static ChangeDimensionTrigger.TriggerInstance changedDimensionTo(ResourceKey<Level> var0) {
         return new ChangeDimensionTrigger.TriggerInstance(EntityPredicate.Composite.ANY, (ResourceKey)null, var0);
      }

      public static ChangeDimensionTrigger.TriggerInstance changedDimensionFrom(ResourceKey<Level> var0) {
         return new ChangeDimensionTrigger.TriggerInstance(EntityPredicate.Composite.ANY, var0, (ResourceKey)null);
      }

      public boolean matches(ResourceKey<Level> var1, ResourceKey<Level> var2) {
         if (this.from != null && this.from != var1) {
            return false;
         } else {
            return this.field_142 == null || this.field_142 == var2;
         }
      }

      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         if (this.from != null) {
            var2.addProperty("from", this.from.location().toString());
         }

         if (this.field_142 != null) {
            var2.addProperty("to", this.field_142.location().toString());
         }

         return var2;
      }
   }
}
