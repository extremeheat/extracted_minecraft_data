package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.Feature;

public class LocationPredicate {
   public static final LocationPredicate field_193455_a;
   private final MinMaxBounds.FloatBound field_193457_c;
   private final MinMaxBounds.FloatBound field_193458_d;
   private final MinMaxBounds.FloatBound field_193459_e;
   @Nullable
   private final Biome field_193456_b;
   @Nullable
   private final String field_193460_f;
   @Nullable
   private final DimensionType field_193461_g;

   public LocationPredicate(MinMaxBounds.FloatBound var1, MinMaxBounds.FloatBound var2, MinMaxBounds.FloatBound var3, @Nullable Biome var4, @Nullable String var5, @Nullable DimensionType var6) {
      super();
      this.field_193457_c = var1;
      this.field_193458_d = var2;
      this.field_193459_e = var3;
      this.field_193456_b = var4;
      this.field_193460_f = var5;
      this.field_193461_g = var6;
   }

   public static LocationPredicate func_204010_a(Biome var0) {
      return new LocationPredicate(MinMaxBounds.FloatBound.field_211359_e, MinMaxBounds.FloatBound.field_211359_e, MinMaxBounds.FloatBound.field_211359_e, var0, (String)null, (DimensionType)null);
   }

   public static LocationPredicate func_204008_a(DimensionType var0) {
      return new LocationPredicate(MinMaxBounds.FloatBound.field_211359_e, MinMaxBounds.FloatBound.field_211359_e, MinMaxBounds.FloatBound.field_211359_e, (Biome)null, (String)null, var0);
   }

   public static LocationPredicate func_204007_a(String var0) {
      return new LocationPredicate(MinMaxBounds.FloatBound.field_211359_e, MinMaxBounds.FloatBound.field_211359_e, MinMaxBounds.FloatBound.field_211359_e, (Biome)null, var0, (DimensionType)null);
   }

   public boolean func_193452_a(WorldServer var1, double var2, double var4, double var6) {
      return this.func_193453_a(var1, (float)var2, (float)var4, (float)var6);
   }

   public boolean func_193453_a(WorldServer var1, float var2, float var3, float var4) {
      if (!this.field_193457_c.func_211354_d(var2)) {
         return false;
      } else if (!this.field_193458_d.func_211354_d(var3)) {
         return false;
      } else if (!this.field_193459_e.func_211354_d(var4)) {
         return false;
      } else if (this.field_193461_g != null && this.field_193461_g != var1.field_73011_w.func_186058_p()) {
         return false;
      } else {
         BlockPos var5 = new BlockPos((double)var2, (double)var3, (double)var4);
         if (this.field_193456_b != null && this.field_193456_b != var1.func_180494_b(var5)) {
            return false;
         } else {
            return this.field_193460_f == null || Feature.func_202280_a(var1, this.field_193460_f, var5);
         }
      }
   }

   public JsonElement func_204009_a() {
      if (this == field_193455_a) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         if (!this.field_193457_c.func_211335_c() || !this.field_193458_d.func_211335_c() || !this.field_193459_e.func_211335_c()) {
            JsonObject var2 = new JsonObject();
            var2.add("x", this.field_193457_c.func_200321_c());
            var2.add("y", this.field_193458_d.func_200321_c());
            var2.add("z", this.field_193459_e.func_200321_c());
            var1.add("position", var2);
         }

         if (this.field_193461_g != null) {
            var1.addProperty("dimension", DimensionType.func_212678_a(this.field_193461_g).toString());
         }

         if (this.field_193460_f != null) {
            var1.addProperty("feature", this.field_193460_f);
         }

         if (this.field_193456_b != null) {
            var1.addProperty("biome", IRegistry.field_212624_m.func_177774_c(this.field_193456_b).toString());
         }

         return var1;
      }
   }

   public static LocationPredicate func_193454_a(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = JsonUtils.func_151210_l(var0, "location");
         JsonObject var2 = JsonUtils.func_151218_a(var1, "position", new JsonObject());
         MinMaxBounds.FloatBound var3 = MinMaxBounds.FloatBound.func_211356_a(var2.get("x"));
         MinMaxBounds.FloatBound var4 = MinMaxBounds.FloatBound.func_211356_a(var2.get("y"));
         MinMaxBounds.FloatBound var5 = MinMaxBounds.FloatBound.func_211356_a(var2.get("z"));
         DimensionType var6 = var1.has("dimension") ? DimensionType.func_193417_a(new ResourceLocation(JsonUtils.func_151200_h(var1, "dimension"))) : null;
         String var7 = var1.has("feature") ? JsonUtils.func_151200_h(var1, "feature") : null;
         Biome var8 = null;
         if (var1.has("biome")) {
            ResourceLocation var9 = new ResourceLocation(JsonUtils.func_151200_h(var1, "biome"));
            var8 = (Biome)IRegistry.field_212624_m.func_212608_b(var9);
            if (var8 == null) {
               throw new JsonSyntaxException("Unknown biome '" + var9 + "'");
            }
         }

         return new LocationPredicate(var3, var4, var5, var8, var7, var6);
      } else {
         return field_193455_a;
      }
   }

   static {
      field_193455_a = new LocationPredicate(MinMaxBounds.FloatBound.field_211359_e, MinMaxBounds.FloatBound.field_211359_e, MinMaxBounds.FloatBound.field_211359_e, (Biome)null, (String)null, (DimensionType)null);
   }
}
