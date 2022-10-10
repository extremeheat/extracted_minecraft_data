package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.JsonUtils;

public class DamageSourcePredicate {
   public static final DamageSourcePredicate field_192449_a = DamageSourcePredicate.Builder.func_203981_a().func_203979_b();
   private final Boolean field_192450_b;
   private final Boolean field_192451_c;
   private final Boolean field_192452_d;
   private final Boolean field_192453_e;
   private final Boolean field_192454_f;
   private final Boolean field_192455_g;
   private final Boolean field_192456_h;
   private final EntityPredicate field_193419_i;
   private final EntityPredicate field_193420_j;

   public DamageSourcePredicate(@Nullable Boolean var1, @Nullable Boolean var2, @Nullable Boolean var3, @Nullable Boolean var4, @Nullable Boolean var5, @Nullable Boolean var6, @Nullable Boolean var7, EntityPredicate var8, EntityPredicate var9) {
      super();
      this.field_192450_b = var1;
      this.field_192451_c = var2;
      this.field_192452_d = var3;
      this.field_192453_e = var4;
      this.field_192454_f = var5;
      this.field_192455_g = var6;
      this.field_192456_h = var7;
      this.field_193419_i = var8;
      this.field_193420_j = var9;
   }

   public boolean func_193418_a(EntityPlayerMP var1, DamageSource var2) {
      if (this == field_192449_a) {
         return true;
      } else if (this.field_192450_b != null && this.field_192450_b != var2.func_76352_a()) {
         return false;
      } else if (this.field_192451_c != null && this.field_192451_c != var2.func_94541_c()) {
         return false;
      } else if (this.field_192452_d != null && this.field_192452_d != var2.func_76363_c()) {
         return false;
      } else if (this.field_192453_e != null && this.field_192453_e != var2.func_76357_e()) {
         return false;
      } else if (this.field_192454_f != null && this.field_192454_f != var2.func_151517_h()) {
         return false;
      } else if (this.field_192455_g != null && this.field_192455_g != var2.func_76347_k()) {
         return false;
      } else if (this.field_192456_h != null && this.field_192456_h != var2.func_82725_o()) {
         return false;
      } else if (!this.field_193419_i.func_192482_a(var1, var2.func_76364_f())) {
         return false;
      } else {
         return this.field_193420_j.func_192482_a(var1, var2.func_76346_g());
      }
   }

   public static DamageSourcePredicate func_192447_a(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = JsonUtils.func_151210_l(var0, "damage type");
         Boolean var2 = func_192448_a(var1, "is_projectile");
         Boolean var3 = func_192448_a(var1, "is_explosion");
         Boolean var4 = func_192448_a(var1, "bypasses_armor");
         Boolean var5 = func_192448_a(var1, "bypasses_invulnerability");
         Boolean var6 = func_192448_a(var1, "bypasses_magic");
         Boolean var7 = func_192448_a(var1, "is_fire");
         Boolean var8 = func_192448_a(var1, "is_magic");
         EntityPredicate var9 = EntityPredicate.func_192481_a(var1.get("direct_entity"));
         EntityPredicate var10 = EntityPredicate.func_192481_a(var1.get("source_entity"));
         return new DamageSourcePredicate(var2, var3, var4, var5, var6, var7, var8, var9, var10);
      } else {
         return field_192449_a;
      }
   }

   @Nullable
   private static Boolean func_192448_a(JsonObject var0, String var1) {
      return var0.has(var1) ? JsonUtils.func_151212_i(var0, var1) : null;
   }

   public JsonElement func_203991_a() {
      if (this == field_192449_a) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         this.func_203992_a(var1, "is_projectile", this.field_192450_b);
         this.func_203992_a(var1, "is_explosion", this.field_192451_c);
         this.func_203992_a(var1, "bypasses_armor", this.field_192452_d);
         this.func_203992_a(var1, "bypasses_invulnerability", this.field_192453_e);
         this.func_203992_a(var1, "bypasses_magic", this.field_192454_f);
         this.func_203992_a(var1, "is_fire", this.field_192455_g);
         this.func_203992_a(var1, "is_magic", this.field_192456_h);
         var1.add("direct_entity", this.field_193419_i.func_204006_a());
         var1.add("source_entity", this.field_193420_j.func_204006_a());
         return var1;
      }
   }

   private void func_203992_a(JsonObject var1, String var2, @Nullable Boolean var3) {
      if (var3 != null) {
         var1.addProperty(var2, var3);
      }

   }

   public static class Builder {
      private Boolean field_203982_a;
      private Boolean field_203983_b;
      private Boolean field_203984_c;
      private Boolean field_203985_d;
      private Boolean field_203986_e;
      private Boolean field_203987_f;
      private Boolean field_203988_g;
      private EntityPredicate field_203989_h;
      private EntityPredicate field_203990_i;

      public Builder() {
         super();
         this.field_203989_h = EntityPredicate.field_192483_a;
         this.field_203990_i = EntityPredicate.field_192483_a;
      }

      public static DamageSourcePredicate.Builder func_203981_a() {
         return new DamageSourcePredicate.Builder();
      }

      public DamageSourcePredicate.Builder func_203978_a(Boolean var1) {
         this.field_203982_a = var1;
         return this;
      }

      public DamageSourcePredicate.Builder func_203980_a(EntityPredicate.Builder var1) {
         this.field_203989_h = var1.func_204000_b();
         return this;
      }

      public DamageSourcePredicate func_203979_b() {
         return new DamageSourcePredicate(this.field_203982_a, this.field_203983_b, this.field_203984_c, this.field_203985_d, this.field_203986_e, this.field_203987_f, this.field_203988_g, this.field_203989_h, this.field_203990_i);
      }
   }
}
