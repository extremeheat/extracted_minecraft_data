package net.minecraft.advancements.criterion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;

public class EntityPredicate {
   public static final EntityPredicate field_192483_a;
   public static final EntityPredicate[] field_204851_b;
   private final EntityTypePredicate field_192484_b;
   private final DistancePredicate field_192485_c;
   private final LocationPredicate field_193435_d;
   private final MobEffectsPredicate field_193436_e;
   private final NBTPredicate field_193437_f;

   private EntityPredicate(EntityTypePredicate var1, DistancePredicate var2, LocationPredicate var3, MobEffectsPredicate var4, NBTPredicate var5) {
      super();
      this.field_192484_b = var1;
      this.field_192485_c = var2;
      this.field_193435_d = var3;
      this.field_193436_e = var4;
      this.field_193437_f = var5;
   }

   public boolean func_192482_a(EntityPlayerMP var1, @Nullable Entity var2) {
      if (this == field_192483_a) {
         return true;
      } else if (var2 == null) {
         return false;
      } else if (!this.field_192484_b.func_209368_a(var2.func_200600_R())) {
         return false;
      } else if (!this.field_192485_c.func_193422_a(var1.field_70165_t, var1.field_70163_u, var1.field_70161_v, var2.field_70165_t, var2.field_70163_u, var2.field_70161_v)) {
         return false;
      } else if (!this.field_193435_d.func_193452_a(var1.func_71121_q(), var2.field_70165_t, var2.field_70163_u, var2.field_70161_v)) {
         return false;
      } else if (!this.field_193436_e.func_193469_a(var2)) {
         return false;
      } else {
         return this.field_193437_f.func_193475_a(var2);
      }
   }

   public static EntityPredicate func_192481_a(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = JsonUtils.func_151210_l(var0, "entity");
         EntityTypePredicate var2 = EntityTypePredicate.func_209370_a(var1.get("type"));
         DistancePredicate var3 = DistancePredicate.func_193421_a(var1.get("distance"));
         LocationPredicate var4 = LocationPredicate.func_193454_a(var1.get("location"));
         MobEffectsPredicate var5 = MobEffectsPredicate.func_193471_a(var1.get("effects"));
         NBTPredicate var6 = NBTPredicate.func_193476_a(var1.get("nbt"));
         return (new EntityPredicate.Builder()).func_209366_a(var2).func_203997_a(var3).func_203999_a(var4).func_209367_a(var5).func_209365_a(var6).func_204000_b();
      } else {
         return field_192483_a;
      }
   }

   public static EntityPredicate[] func_204849_b(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonArray var1 = JsonUtils.func_151207_m(var0, "entities");
         EntityPredicate[] var2 = new EntityPredicate[var1.size()];

         for(int var3 = 0; var3 < var1.size(); ++var3) {
            var2[var3] = func_192481_a(var1.get(var3));
         }

         return var2;
      } else {
         return field_204851_b;
      }
   }

   public JsonElement func_204006_a() {
      if (this == field_192483_a) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         var1.add("type", this.field_192484_b.func_209369_a());
         var1.add("distance", this.field_192485_c.func_203994_a());
         var1.add("location", this.field_193435_d.func_204009_a());
         var1.add("effects", this.field_193436_e.func_204013_b());
         var1.add("nbt", this.field_193437_f.func_200322_a());
         return var1;
      }
   }

   public static JsonElement func_204850_a(EntityPredicate[] var0) {
      if (var0 == field_204851_b) {
         return JsonNull.INSTANCE;
      } else {
         JsonArray var1 = new JsonArray();

         for(int var2 = 0; var2 < var0.length; ++var2) {
            JsonElement var3 = var0[var2].func_204006_a();
            if (!var3.isJsonNull()) {
               var1.add(var3);
            }
         }

         return var1;
      }
   }

   // $FF: synthetic method
   EntityPredicate(EntityTypePredicate var1, DistancePredicate var2, LocationPredicate var3, MobEffectsPredicate var4, NBTPredicate var5, Object var6) {
      this(var1, var2, var3, var4, var5);
   }

   static {
      field_192483_a = new EntityPredicate(EntityTypePredicate.field_209371_a, DistancePredicate.field_193423_a, LocationPredicate.field_193455_a, MobEffectsPredicate.field_193473_a, NBTPredicate.field_193479_a);
      field_204851_b = new EntityPredicate[0];
   }

   public static class Builder {
      private EntityTypePredicate field_204001_a;
      private DistancePredicate field_204002_b;
      private LocationPredicate field_204003_c;
      private MobEffectsPredicate field_204004_d;
      private NBTPredicate field_204005_e;

      public Builder() {
         super();
         this.field_204001_a = EntityTypePredicate.field_209371_a;
         this.field_204002_b = DistancePredicate.field_193423_a;
         this.field_204003_c = LocationPredicate.field_193455_a;
         this.field_204004_d = MobEffectsPredicate.field_193473_a;
         this.field_204005_e = NBTPredicate.field_193479_a;
      }

      public static EntityPredicate.Builder func_203996_a() {
         return new EntityPredicate.Builder();
      }

      public EntityPredicate.Builder func_203998_a(EntityType<?> var1) {
         this.field_204001_a = new EntityTypePredicate(var1);
         return this;
      }

      public EntityPredicate.Builder func_209366_a(EntityTypePredicate var1) {
         this.field_204001_a = var1;
         return this;
      }

      public EntityPredicate.Builder func_203997_a(DistancePredicate var1) {
         this.field_204002_b = var1;
         return this;
      }

      public EntityPredicate.Builder func_203999_a(LocationPredicate var1) {
         this.field_204003_c = var1;
         return this;
      }

      public EntityPredicate.Builder func_209367_a(MobEffectsPredicate var1) {
         this.field_204004_d = var1;
         return this;
      }

      public EntityPredicate.Builder func_209365_a(NBTPredicate var1) {
         this.field_204005_e = var1;
         return this;
      }

      public EntityPredicate func_204000_b() {
         return this.field_204001_a == EntityTypePredicate.field_209371_a && this.field_204002_b == DistancePredicate.field_193423_a && this.field_204003_c == LocationPredicate.field_193455_a && this.field_204004_d == MobEffectsPredicate.field_193473_a && this.field_204005_e == NBTPredicate.field_193479_a ? EntityPredicate.field_192483_a : new EntityPredicate(this.field_204001_a, this.field_204002_b, this.field_204003_c, this.field_204004_d, this.field_204005_e);
      }
   }
}
