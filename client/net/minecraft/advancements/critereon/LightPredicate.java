package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;

public class LightPredicate {
   public static final LightPredicate ANY = new LightPredicate(MinMaxBounds.Ints.ANY);
   private final MinMaxBounds.Ints composite;

   LightPredicate(MinMaxBounds.Ints var1) {
      super();
      this.composite = var1;
   }

   public boolean matches(ServerLevel var1, BlockPos var2) {
      if (this == ANY) {
         return true;
      } else if (!var1.isLoaded(var2)) {
         return false;
      } else {
         return this.composite.matches(var1.getMaxLocalRawBrightness(var2));
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         var1.add("light", this.composite.serializeToJson());
         return var1;
      }
   }

   public static LightPredicate fromJson(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(var0, "light");
         MinMaxBounds.Ints var2 = MinMaxBounds.Ints.fromJson(var1.get("light"));
         return new LightPredicate(var2);
      } else {
         return ANY;
      }
   }

   public static class Builder {
      private MinMaxBounds.Ints composite = MinMaxBounds.Ints.ANY;

      public Builder() {
         super();
      }

      public static LightPredicate.Builder light() {
         return new LightPredicate.Builder();
      }

      public LightPredicate.Builder setComposite(MinMaxBounds.Ints var1) {
         this.composite = var1;
         return this;
      }

      public LightPredicate build() {
         return new LightPredicate(this.composite);
      }
   }
}
