package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;

public class FishingHookPredicate {
   public static final FishingHookPredicate ANY = new FishingHookPredicate(false);
   private static final String IN_OPEN_WATER_KEY = "in_open_water";
   private final boolean inOpenWater;

   private FishingHookPredicate(boolean var1) {
      super();
      this.inOpenWater = var1;
   }

   public static FishingHookPredicate inOpenWater(boolean var0) {
      return new FishingHookPredicate(var0);
   }

   public static FishingHookPredicate fromJson(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(var0, "fishing_hook");
         JsonElement var2 = var1.get("in_open_water");
         return var2 != null ? new FishingHookPredicate(GsonHelper.convertToBoolean(var2, "in_open_water")) : ANY;
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         var1.add("in_open_water", new JsonPrimitive(this.inOpenWater));
         return var1;
      }
   }

   public boolean matches(Entity var1) {
      if (this == ANY) {
         return true;
      } else if (!(var1 instanceof FishingHook)) {
         return false;
      } else {
         FishingHook var2 = (FishingHook)var1;
         return this.inOpenWater == var2.isOpenWaterFishing();
      }
   }
}
