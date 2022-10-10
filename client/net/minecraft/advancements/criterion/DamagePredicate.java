package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.JsonUtils;

public class DamagePredicate {
   public static final DamagePredicate field_192366_a = DamagePredicate.Builder.func_203971_a().func_203970_b();
   private final MinMaxBounds.FloatBound field_192367_b;
   private final MinMaxBounds.FloatBound field_192368_c;
   private final EntityPredicate field_192369_d;
   private final Boolean field_192370_e;
   private final DamageSourcePredicate field_192371_f;

   public DamagePredicate() {
      super();
      this.field_192367_b = MinMaxBounds.FloatBound.field_211359_e;
      this.field_192368_c = MinMaxBounds.FloatBound.field_211359_e;
      this.field_192369_d = EntityPredicate.field_192483_a;
      this.field_192370_e = null;
      this.field_192371_f = DamageSourcePredicate.field_192449_a;
   }

   public DamagePredicate(MinMaxBounds.FloatBound var1, MinMaxBounds.FloatBound var2, EntityPredicate var3, @Nullable Boolean var4, DamageSourcePredicate var5) {
      super();
      this.field_192367_b = var1;
      this.field_192368_c = var2;
      this.field_192369_d = var3;
      this.field_192370_e = var4;
      this.field_192371_f = var5;
   }

   public boolean func_192365_a(EntityPlayerMP var1, DamageSource var2, float var3, float var4, boolean var5) {
      if (this == field_192366_a) {
         return true;
      } else if (!this.field_192367_b.func_211354_d(var3)) {
         return false;
      } else if (!this.field_192368_c.func_211354_d(var4)) {
         return false;
      } else if (!this.field_192369_d.func_192482_a(var1, var2.func_76346_g())) {
         return false;
      } else if (this.field_192370_e != null && this.field_192370_e != var5) {
         return false;
      } else {
         return this.field_192371_f.func_193418_a(var1, var2);
      }
   }

   public static DamagePredicate func_192364_a(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = JsonUtils.func_151210_l(var0, "damage");
         MinMaxBounds.FloatBound var2 = MinMaxBounds.FloatBound.func_211356_a(var1.get("dealt"));
         MinMaxBounds.FloatBound var3 = MinMaxBounds.FloatBound.func_211356_a(var1.get("taken"));
         Boolean var4 = var1.has("blocked") ? JsonUtils.func_151212_i(var1, "blocked") : null;
         EntityPredicate var5 = EntityPredicate.func_192481_a(var1.get("source_entity"));
         DamageSourcePredicate var6 = DamageSourcePredicate.func_192447_a(var1.get("type"));
         return new DamagePredicate(var2, var3, var5, var4, var6);
      } else {
         return field_192366_a;
      }
   }

   public JsonElement func_203977_a() {
      if (this == field_192366_a) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         var1.add("dealt", this.field_192367_b.func_200321_c());
         var1.add("taken", this.field_192368_c.func_200321_c());
         var1.add("source_entity", this.field_192369_d.func_204006_a());
         var1.add("type", this.field_192371_f.func_203991_a());
         if (this.field_192370_e != null) {
            var1.addProperty("blocked", this.field_192370_e);
         }

         return var1;
      }
   }

   public static class Builder {
      private MinMaxBounds.FloatBound field_203972_a;
      private MinMaxBounds.FloatBound field_203973_b;
      private EntityPredicate field_203974_c;
      private Boolean field_203975_d;
      private DamageSourcePredicate field_203976_e;

      public Builder() {
         super();
         this.field_203972_a = MinMaxBounds.FloatBound.field_211359_e;
         this.field_203973_b = MinMaxBounds.FloatBound.field_211359_e;
         this.field_203974_c = EntityPredicate.field_192483_a;
         this.field_203976_e = DamageSourcePredicate.field_192449_a;
      }

      public static DamagePredicate.Builder func_203971_a() {
         return new DamagePredicate.Builder();
      }

      public DamagePredicate.Builder func_203968_a(Boolean var1) {
         this.field_203975_d = var1;
         return this;
      }

      public DamagePredicate.Builder func_203969_a(DamageSourcePredicate.Builder var1) {
         this.field_203976_e = var1.func_203979_b();
         return this;
      }

      public DamagePredicate func_203970_b() {
         return new DamagePredicate(this.field_203972_a, this.field_203973_b, this.field_203974_c, this.field_203975_d, this.field_203976_e);
      }
   }
}
