package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;

public class DamageSourcePredicate {
   public static final DamageSourcePredicate ANY = DamageSourcePredicate.Builder.damageType().build();
   @Nullable
   private final Boolean isProjectile;
   @Nullable
   private final Boolean isExplosion;
   @Nullable
   private final Boolean bypassesArmor;
   @Nullable
   private final Boolean bypassesInvulnerability;
   @Nullable
   private final Boolean bypassesMagic;
   @Nullable
   private final Boolean isFire;
   @Nullable
   private final Boolean isMagic;
   @Nullable
   private final Boolean isLightning;
   private final EntityPredicate directEntity;
   private final EntityPredicate sourceEntity;

   public DamageSourcePredicate(@Nullable Boolean var1, @Nullable Boolean var2, @Nullable Boolean var3, @Nullable Boolean var4, @Nullable Boolean var5, @Nullable Boolean var6, @Nullable Boolean var7, @Nullable Boolean var8, EntityPredicate var9, EntityPredicate var10) {
      super();
      this.isProjectile = var1;
      this.isExplosion = var2;
      this.bypassesArmor = var3;
      this.bypassesInvulnerability = var4;
      this.bypassesMagic = var5;
      this.isFire = var6;
      this.isMagic = var7;
      this.isLightning = var8;
      this.directEntity = var9;
      this.sourceEntity = var10;
   }

   public boolean matches(ServerPlayer var1, DamageSource var2) {
      return this.matches(var1.getLevel(), var1.position(), var2);
   }

   public boolean matches(ServerLevel var1, Vec3 var2, DamageSource var3) {
      if (this == ANY) {
         return true;
      } else if (this.isProjectile != null && this.isProjectile != var3.isProjectile()) {
         return false;
      } else if (this.isExplosion != null && this.isExplosion != var3.isExplosion()) {
         return false;
      } else if (this.bypassesArmor != null && this.bypassesArmor != var3.isBypassArmor()) {
         return false;
      } else if (this.bypassesInvulnerability != null && this.bypassesInvulnerability != var3.isBypassInvul()) {
         return false;
      } else if (this.bypassesMagic != null && this.bypassesMagic != var3.isBypassMagic()) {
         return false;
      } else if (this.isFire != null && this.isFire != var3.isFire()) {
         return false;
      } else if (this.isMagic != null && this.isMagic != var3.isMagic()) {
         return false;
      } else if (this.isLightning != null && this.isLightning != (var3 == DamageSource.LIGHTNING_BOLT)) {
         return false;
      } else if (!this.directEntity.matches(var1, var2, var3.getDirectEntity())) {
         return false;
      } else {
         return this.sourceEntity.matches(var1, var2, var3.getEntity());
      }
   }

   public static DamageSourcePredicate fromJson(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(var0, "damage type");
         Boolean var2 = getOptionalBoolean(var1, "is_projectile");
         Boolean var3 = getOptionalBoolean(var1, "is_explosion");
         Boolean var4 = getOptionalBoolean(var1, "bypasses_armor");
         Boolean var5 = getOptionalBoolean(var1, "bypasses_invulnerability");
         Boolean var6 = getOptionalBoolean(var1, "bypasses_magic");
         Boolean var7 = getOptionalBoolean(var1, "is_fire");
         Boolean var8 = getOptionalBoolean(var1, "is_magic");
         Boolean var9 = getOptionalBoolean(var1, "is_lightning");
         EntityPredicate var10 = EntityPredicate.fromJson(var1.get("direct_entity"));
         EntityPredicate var11 = EntityPredicate.fromJson(var1.get("source_entity"));
         return new DamageSourcePredicate(var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      } else {
         return ANY;
      }
   }

   @Nullable
   private static Boolean getOptionalBoolean(JsonObject var0, String var1) {
      return var0.has(var1) ? GsonHelper.getAsBoolean(var0, var1) : null;
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         this.addOptionally(var1, "is_projectile", this.isProjectile);
         this.addOptionally(var1, "is_explosion", this.isExplosion);
         this.addOptionally(var1, "bypasses_armor", this.bypassesArmor);
         this.addOptionally(var1, "bypasses_invulnerability", this.bypassesInvulnerability);
         this.addOptionally(var1, "bypasses_magic", this.bypassesMagic);
         this.addOptionally(var1, "is_fire", this.isFire);
         this.addOptionally(var1, "is_magic", this.isMagic);
         this.addOptionally(var1, "is_lightning", this.isLightning);
         var1.add("direct_entity", this.directEntity.serializeToJson());
         var1.add("source_entity", this.sourceEntity.serializeToJson());
         return var1;
      }
   }

   private void addOptionally(JsonObject var1, String var2, @Nullable Boolean var3) {
      if (var3 != null) {
         var1.addProperty(var2, var3);
      }

   }

   public static class Builder {
      @Nullable
      private Boolean isProjectile;
      @Nullable
      private Boolean isExplosion;
      @Nullable
      private Boolean bypassesArmor;
      @Nullable
      private Boolean bypassesInvulnerability;
      @Nullable
      private Boolean bypassesMagic;
      @Nullable
      private Boolean isFire;
      @Nullable
      private Boolean isMagic;
      @Nullable
      private Boolean isLightning;
      private EntityPredicate directEntity;
      private EntityPredicate sourceEntity;

      public Builder() {
         super();
         this.directEntity = EntityPredicate.ANY;
         this.sourceEntity = EntityPredicate.ANY;
      }

      public static DamageSourcePredicate.Builder damageType() {
         return new DamageSourcePredicate.Builder();
      }

      public DamageSourcePredicate.Builder isProjectile(Boolean var1) {
         this.isProjectile = var1;
         return this;
      }

      public DamageSourcePredicate.Builder isExplosion(Boolean var1) {
         this.isExplosion = var1;
         return this;
      }

      public DamageSourcePredicate.Builder bypassesArmor(Boolean var1) {
         this.bypassesArmor = var1;
         return this;
      }

      public DamageSourcePredicate.Builder bypassesInvulnerability(Boolean var1) {
         this.bypassesInvulnerability = var1;
         return this;
      }

      public DamageSourcePredicate.Builder bypassesMagic(Boolean var1) {
         this.bypassesMagic = var1;
         return this;
      }

      public DamageSourcePredicate.Builder isFire(Boolean var1) {
         this.isFire = var1;
         return this;
      }

      public DamageSourcePredicate.Builder isMagic(Boolean var1) {
         this.isMagic = var1;
         return this;
      }

      public DamageSourcePredicate.Builder isLightning(Boolean var1) {
         this.isLightning = var1;
         return this;
      }

      public DamageSourcePredicate.Builder direct(EntityPredicate var1) {
         this.directEntity = var1;
         return this;
      }

      public DamageSourcePredicate.Builder direct(EntityPredicate.Builder var1) {
         this.directEntity = var1.build();
         return this;
      }

      public DamageSourcePredicate.Builder source(EntityPredicate var1) {
         this.sourceEntity = var1;
         return this;
      }

      public DamageSourcePredicate.Builder source(EntityPredicate.Builder var1) {
         this.sourceEntity = var1.build();
         return this;
      }

      public DamageSourcePredicate build() {
         return new DamageSourcePredicate(this.isProjectile, this.isExplosion, this.bypassesArmor, this.bypassesInvulnerability, this.bypassesMagic, this.isFire, this.isMagic, this.isLightning, this.directEntity, this.sourceEntity);
      }
   }
}
