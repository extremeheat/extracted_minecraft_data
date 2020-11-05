package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;

public class DistancePredicate {
   public static final DistancePredicate ANY;
   private final MinMaxBounds.Floats x;
   private final MinMaxBounds.Floats y;
   private final MinMaxBounds.Floats z;
   private final MinMaxBounds.Floats horizontal;
   private final MinMaxBounds.Floats absolute;

   public DistancePredicate(MinMaxBounds.Floats var1, MinMaxBounds.Floats var2, MinMaxBounds.Floats var3, MinMaxBounds.Floats var4, MinMaxBounds.Floats var5) {
      super();
      this.x = var1;
      this.y = var2;
      this.z = var3;
      this.horizontal = var4;
      this.absolute = var5;
   }

   public static DistancePredicate horizontal(MinMaxBounds.Floats var0) {
      return new DistancePredicate(MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, var0, MinMaxBounds.Floats.ANY);
   }

   public static DistancePredicate vertical(MinMaxBounds.Floats var0) {
      return new DistancePredicate(MinMaxBounds.Floats.ANY, var0, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY);
   }

   public boolean matches(double var1, double var3, double var5, double var7, double var9, double var11) {
      float var13 = (float)(var1 - var7);
      float var14 = (float)(var3 - var9);
      float var15 = (float)(var5 - var11);
      if (this.x.matches(Mth.abs(var13)) && this.y.matches(Mth.abs(var14)) && this.z.matches(Mth.abs(var15))) {
         if (!this.horizontal.matchesSqr((double)(var13 * var13 + var15 * var15))) {
            return false;
         } else {
            return this.absolute.matchesSqr((double)(var13 * var13 + var14 * var14 + var15 * var15));
         }
      } else {
         return false;
      }
   }

   public static DistancePredicate fromJson(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(var0, "distance");
         MinMaxBounds.Floats var2 = MinMaxBounds.Floats.fromJson(var1.get("x"));
         MinMaxBounds.Floats var3 = MinMaxBounds.Floats.fromJson(var1.get("y"));
         MinMaxBounds.Floats var4 = MinMaxBounds.Floats.fromJson(var1.get("z"));
         MinMaxBounds.Floats var5 = MinMaxBounds.Floats.fromJson(var1.get("horizontal"));
         MinMaxBounds.Floats var6 = MinMaxBounds.Floats.fromJson(var1.get("absolute"));
         return new DistancePredicate(var2, var3, var4, var5, var6);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         var1.add("x", this.x.serializeToJson());
         var1.add("y", this.y.serializeToJson());
         var1.add("z", this.z.serializeToJson());
         var1.add("horizontal", this.horizontal.serializeToJson());
         var1.add("absolute", this.absolute.serializeToJson());
         return var1;
      }
   }

   static {
      ANY = new DistancePredicate(MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY);
   }
}
