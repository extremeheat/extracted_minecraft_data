package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;

public class DamagePredicate {
   public static final DamagePredicate ANY = DamagePredicate.Builder.damageInstance().build();
   private final MinMaxBounds.Floats dealtDamage;
   private final MinMaxBounds.Floats takenDamage;
   private final EntityPredicate sourceEntity;
   private final Boolean blocked;
   private final DamageSourcePredicate type;

   public DamagePredicate() {
      super();
      this.dealtDamage = MinMaxBounds.Floats.ANY;
      this.takenDamage = MinMaxBounds.Floats.ANY;
      this.sourceEntity = EntityPredicate.ANY;
      this.blocked = null;
      this.type = DamageSourcePredicate.ANY;
   }

   public DamagePredicate(MinMaxBounds.Floats var1, MinMaxBounds.Floats var2, EntityPredicate var3, @Nullable Boolean var4, DamageSourcePredicate var5) {
      super();
      this.dealtDamage = var1;
      this.takenDamage = var2;
      this.sourceEntity = var3;
      this.blocked = var4;
      this.type = var5;
   }

   public boolean matches(ServerPlayer var1, DamageSource var2, float var3, float var4, boolean var5) {
      if (this == ANY) {
         return true;
      } else if (!this.dealtDamage.matches(var3)) {
         return false;
      } else if (!this.takenDamage.matches(var4)) {
         return false;
      } else if (!this.sourceEntity.matches(var1, var2.getEntity())) {
         return false;
      } else if (this.blocked != null && this.blocked != var5) {
         return false;
      } else {
         return this.type.matches(var1, var2);
      }
   }

   public static DamagePredicate fromJson(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(var0, "damage");
         MinMaxBounds.Floats var2 = MinMaxBounds.Floats.fromJson(var1.get("dealt"));
         MinMaxBounds.Floats var3 = MinMaxBounds.Floats.fromJson(var1.get("taken"));
         Boolean var4 = var1.has("blocked") ? GsonHelper.getAsBoolean(var1, "blocked") : null;
         EntityPredicate var5 = EntityPredicate.fromJson(var1.get("source_entity"));
         DamageSourcePredicate var6 = DamageSourcePredicate.fromJson(var1.get("type"));
         return new DamagePredicate(var2, var3, var5, var4, var6);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         var1.add("dealt", this.dealtDamage.serializeToJson());
         var1.add("taken", this.takenDamage.serializeToJson());
         var1.add("source_entity", this.sourceEntity.serializeToJson());
         var1.add("type", this.type.serializeToJson());
         if (this.blocked != null) {
            var1.addProperty("blocked", this.blocked);
         }

         return var1;
      }
   }

   public static class Builder {
      private MinMaxBounds.Floats dealtDamage;
      private MinMaxBounds.Floats takenDamage;
      private EntityPredicate sourceEntity;
      private Boolean blocked;
      private DamageSourcePredicate type;

      public Builder() {
         super();
         this.dealtDamage = MinMaxBounds.Floats.ANY;
         this.takenDamage = MinMaxBounds.Floats.ANY;
         this.sourceEntity = EntityPredicate.ANY;
         this.type = DamageSourcePredicate.ANY;
      }

      public static DamagePredicate.Builder damageInstance() {
         return new DamagePredicate.Builder();
      }

      public DamagePredicate.Builder blocked(Boolean var1) {
         this.blocked = var1;
         return this;
      }

      public DamagePredicate.Builder type(DamageSourcePredicate.Builder var1) {
         this.type = var1.build();
         return this;
      }

      public DamagePredicate build() {
         return new DamagePredicate(this.dealtDamage, this.takenDamage, this.sourceEntity, this.blocked, this.type);
      }
   }
}
