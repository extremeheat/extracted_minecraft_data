package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.MathHelper;

public class DistancePredicate {
   public static final DistancePredicate field_193423_a;
   private final MinMaxBounds.FloatBound field_193424_b;
   private final MinMaxBounds.FloatBound field_193425_c;
   private final MinMaxBounds.FloatBound field_193426_d;
   private final MinMaxBounds.FloatBound field_193427_e;
   private final MinMaxBounds.FloatBound field_193428_f;

   public DistancePredicate(MinMaxBounds.FloatBound var1, MinMaxBounds.FloatBound var2, MinMaxBounds.FloatBound var3, MinMaxBounds.FloatBound var4, MinMaxBounds.FloatBound var5) {
      super();
      this.field_193424_b = var1;
      this.field_193425_c = var2;
      this.field_193426_d = var3;
      this.field_193427_e = var4;
      this.field_193428_f = var5;
   }

   public static DistancePredicate func_203995_a(MinMaxBounds.FloatBound var0) {
      return new DistancePredicate(MinMaxBounds.FloatBound.field_211359_e, MinMaxBounds.FloatBound.field_211359_e, MinMaxBounds.FloatBound.field_211359_e, var0, MinMaxBounds.FloatBound.field_211359_e);
   }

   public static DistancePredicate func_203993_b(MinMaxBounds.FloatBound var0) {
      return new DistancePredicate(MinMaxBounds.FloatBound.field_211359_e, var0, MinMaxBounds.FloatBound.field_211359_e, MinMaxBounds.FloatBound.field_211359_e, MinMaxBounds.FloatBound.field_211359_e);
   }

   public boolean func_193422_a(double var1, double var3, double var5, double var7, double var9, double var11) {
      float var13 = (float)(var1 - var7);
      float var14 = (float)(var3 - var9);
      float var15 = (float)(var5 - var11);
      if (this.field_193424_b.func_211354_d(MathHelper.func_76135_e(var13)) && this.field_193425_c.func_211354_d(MathHelper.func_76135_e(var14)) && this.field_193426_d.func_211354_d(MathHelper.func_76135_e(var15))) {
         if (!this.field_193427_e.func_211351_a((double)(var13 * var13 + var15 * var15))) {
            return false;
         } else {
            return this.field_193428_f.func_211351_a((double)(var13 * var13 + var14 * var14 + var15 * var15));
         }
      } else {
         return false;
      }
   }

   public static DistancePredicate func_193421_a(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = JsonUtils.func_151210_l(var0, "distance");
         MinMaxBounds.FloatBound var2 = MinMaxBounds.FloatBound.func_211356_a(var1.get("x"));
         MinMaxBounds.FloatBound var3 = MinMaxBounds.FloatBound.func_211356_a(var1.get("y"));
         MinMaxBounds.FloatBound var4 = MinMaxBounds.FloatBound.func_211356_a(var1.get("z"));
         MinMaxBounds.FloatBound var5 = MinMaxBounds.FloatBound.func_211356_a(var1.get("horizontal"));
         MinMaxBounds.FloatBound var6 = MinMaxBounds.FloatBound.func_211356_a(var1.get("absolute"));
         return new DistancePredicate(var2, var3, var4, var5, var6);
      } else {
         return field_193423_a;
      }
   }

   public JsonElement func_203994_a() {
      if (this == field_193423_a) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         var1.add("x", this.field_193424_b.func_200321_c());
         var1.add("y", this.field_193425_c.func_200321_c());
         var1.add("z", this.field_193426_d.func_200321_c());
         var1.add("horizontal", this.field_193427_e.func_200321_c());
         var1.add("absolute", this.field_193428_f.func_200321_c());
         return var1;
      }
   }

   static {
      field_193423_a = new DistancePredicate(MinMaxBounds.FloatBound.field_211359_e, MinMaxBounds.FloatBound.field_211359_e, MinMaxBounds.FloatBound.field_211359_e, MinMaxBounds.FloatBound.field_211359_e, MinMaxBounds.FloatBound.field_211359_e);
   }
}
