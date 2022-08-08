package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class EntityFlagsPredicate {
   public static final EntityFlagsPredicate ANY = (new Builder()).build();
   @Nullable
   private final Boolean isOnFire;
   @Nullable
   private final Boolean isCrouching;
   @Nullable
   private final Boolean isSprinting;
   @Nullable
   private final Boolean isSwimming;
   @Nullable
   private final Boolean isBaby;

   public EntityFlagsPredicate(@Nullable Boolean var1, @Nullable Boolean var2, @Nullable Boolean var3, @Nullable Boolean var4, @Nullable Boolean var5) {
      super();
      this.isOnFire = var1;
      this.isCrouching = var2;
      this.isSprinting = var3;
      this.isSwimming = var4;
      this.isBaby = var5;
   }

   public boolean matches(Entity var1) {
      if (this.isOnFire != null && var1.isOnFire() != this.isOnFire) {
         return false;
      } else if (this.isCrouching != null && var1.isCrouching() != this.isCrouching) {
         return false;
      } else if (this.isSprinting != null && var1.isSprinting() != this.isSprinting) {
         return false;
      } else if (this.isSwimming != null && var1.isSwimming() != this.isSwimming) {
         return false;
      } else {
         return this.isBaby == null || !(var1 instanceof LivingEntity) || ((LivingEntity)var1).isBaby() == this.isBaby;
      }
   }

   @Nullable
   private static Boolean getOptionalBoolean(JsonObject var0, String var1) {
      return var0.has(var1) ? GsonHelper.getAsBoolean(var0, var1) : null;
   }

   public static EntityFlagsPredicate fromJson(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(var0, "entity flags");
         Boolean var2 = getOptionalBoolean(var1, "is_on_fire");
         Boolean var3 = getOptionalBoolean(var1, "is_sneaking");
         Boolean var4 = getOptionalBoolean(var1, "is_sprinting");
         Boolean var5 = getOptionalBoolean(var1, "is_swimming");
         Boolean var6 = getOptionalBoolean(var1, "is_baby");
         return new EntityFlagsPredicate(var2, var3, var4, var5, var6);
      } else {
         return ANY;
      }
   }

   private void addOptionalBoolean(JsonObject var1, String var2, @Nullable Boolean var3) {
      if (var3 != null) {
         var1.addProperty(var2, var3);
      }

   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         this.addOptionalBoolean(var1, "is_on_fire", this.isOnFire);
         this.addOptionalBoolean(var1, "is_sneaking", this.isCrouching);
         this.addOptionalBoolean(var1, "is_sprinting", this.isSprinting);
         this.addOptionalBoolean(var1, "is_swimming", this.isSwimming);
         this.addOptionalBoolean(var1, "is_baby", this.isBaby);
         return var1;
      }
   }

   public static class Builder {
      @Nullable
      private Boolean isOnFire;
      @Nullable
      private Boolean isCrouching;
      @Nullable
      private Boolean isSprinting;
      @Nullable
      private Boolean isSwimming;
      @Nullable
      private Boolean isBaby;

      public Builder() {
         super();
      }

      public static Builder flags() {
         return new Builder();
      }

      public Builder setOnFire(@Nullable Boolean var1) {
         this.isOnFire = var1;
         return this;
      }

      public Builder setCrouching(@Nullable Boolean var1) {
         this.isCrouching = var1;
         return this;
      }

      public Builder setSprinting(@Nullable Boolean var1) {
         this.isSprinting = var1;
         return this;
      }

      public Builder setSwimming(@Nullable Boolean var1) {
         this.isSwimming = var1;
         return this;
      }

      public Builder setIsBaby(@Nullable Boolean var1) {
         this.isBaby = var1;
         return this;
      }

      public EntityFlagsPredicate build() {
         return new EntityFlagsPredicate(this.isOnFire, this.isCrouching, this.isSprinting, this.isSwimming, this.isBaby);
      }
   }
}
