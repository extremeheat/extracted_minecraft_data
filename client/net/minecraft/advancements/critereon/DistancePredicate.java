package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;

public class DistancePredicate {
   public static final DistancePredicate ANY;
   // $FF: renamed from: x net.minecraft.advancements.critereon.MinMaxBounds$Doubles
   private final MinMaxBounds.Doubles field_145;
   // $FF: renamed from: y net.minecraft.advancements.critereon.MinMaxBounds$Doubles
   private final MinMaxBounds.Doubles field_146;
   // $FF: renamed from: z net.minecraft.advancements.critereon.MinMaxBounds$Doubles
   private final MinMaxBounds.Doubles field_147;
   private final MinMaxBounds.Doubles horizontal;
   private final MinMaxBounds.Doubles absolute;

   public DistancePredicate(MinMaxBounds.Doubles var1, MinMaxBounds.Doubles var2, MinMaxBounds.Doubles var3, MinMaxBounds.Doubles var4, MinMaxBounds.Doubles var5) {
      super();
      this.field_145 = var1;
      this.field_146 = var2;
      this.field_147 = var3;
      this.horizontal = var4;
      this.absolute = var5;
   }

   public static DistancePredicate horizontal(MinMaxBounds.Doubles var0) {
      return new DistancePredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, var0, MinMaxBounds.Doubles.ANY);
   }

   public static DistancePredicate vertical(MinMaxBounds.Doubles var0) {
      return new DistancePredicate(MinMaxBounds.Doubles.ANY, var0, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY);
   }

   public static DistancePredicate absolute(MinMaxBounds.Doubles var0) {
      return new DistancePredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, var0);
   }

   public boolean matches(double var1, double var3, double var5, double var7, double var9, double var11) {
      float var13 = (float)(var1 - var7);
      float var14 = (float)(var3 - var9);
      float var15 = (float)(var5 - var11);
      if (this.field_145.matches((double)Mth.abs(var13)) && this.field_146.matches((double)Mth.abs(var14)) && this.field_147.matches((double)Mth.abs(var15))) {
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
         MinMaxBounds.Doubles var2 = MinMaxBounds.Doubles.fromJson(var1.get("x"));
         MinMaxBounds.Doubles var3 = MinMaxBounds.Doubles.fromJson(var1.get("y"));
         MinMaxBounds.Doubles var4 = MinMaxBounds.Doubles.fromJson(var1.get("z"));
         MinMaxBounds.Doubles var5 = MinMaxBounds.Doubles.fromJson(var1.get("horizontal"));
         MinMaxBounds.Doubles var6 = MinMaxBounds.Doubles.fromJson(var1.get("absolute"));
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
         var1.add("x", this.field_145.serializeToJson());
         var1.add("y", this.field_146.serializeToJson());
         var1.add("z", this.field_147.serializeToJson());
         var1.add("horizontal", this.horizontal.serializeToJson());
         var1.add("absolute", this.absolute.serializeToJson());
         return var1;
      }
   }

   static {
      ANY = new DistancePredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY);
   }
}
