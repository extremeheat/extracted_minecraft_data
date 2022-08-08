package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.phys.Vec3;

public class FishingHookPredicate implements EntitySubPredicate {
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

   public static FishingHookPredicate fromJson(JsonObject var0) {
      JsonElement var1 = var0.get("in_open_water");
      return var1 != null ? new FishingHookPredicate(GsonHelper.convertToBoolean(var1, "in_open_water")) : ANY;
   }

   public JsonObject serializeCustomData() {
      if (this == ANY) {
         return new JsonObject();
      } else {
         JsonObject var1 = new JsonObject();
         var1.add("in_open_water", new JsonPrimitive(this.inOpenWater));
         return var1;
      }
   }

   public EntitySubPredicate.Type type() {
      return EntitySubPredicate.Types.FISHING_HOOK;
   }

   public boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3) {
      if (this == ANY) {
         return true;
      } else if (!(var1 instanceof FishingHook)) {
         return false;
      } else {
         FishingHook var4 = (FishingHook)var1;
         return this.inOpenWater == var4.isOpenWaterFishing();
      }
   }
}
