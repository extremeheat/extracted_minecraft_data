package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public record EntityFlagsPredicate(Optional<Boolean> b, Optional<Boolean> c, Optional<Boolean> d, Optional<Boolean> e, Optional<Boolean> f) {
   private final Optional<Boolean> isOnFire;
   private final Optional<Boolean> isCrouching;
   private final Optional<Boolean> isSprinting;
   private final Optional<Boolean> isSwimming;
   private final Optional<Boolean> isBaby;
   public static final Codec<EntityFlagsPredicate> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               Codec.BOOL.optionalFieldOf("is_on_fire").forGetter(EntityFlagsPredicate::isOnFire),
               Codec.BOOL.optionalFieldOf("is_sneaking").forGetter(EntityFlagsPredicate::isCrouching),
               Codec.BOOL.optionalFieldOf("is_sprinting").forGetter(EntityFlagsPredicate::isSprinting),
               Codec.BOOL.optionalFieldOf("is_swimming").forGetter(EntityFlagsPredicate::isSwimming),
               Codec.BOOL.optionalFieldOf("is_baby").forGetter(EntityFlagsPredicate::isBaby)
            )
            .apply(var0, EntityFlagsPredicate::new)
   );

   public EntityFlagsPredicate(Optional<Boolean> var1, Optional<Boolean> var2, Optional<Boolean> var3, Optional<Boolean> var4, Optional<Boolean> var5) {
      super();
      this.isOnFire = var1;
      this.isCrouching = var2;
      this.isSprinting = var3;
      this.isSwimming = var4;
      this.isBaby = var5;
   }

   public boolean matches(Entity var1) {
      if (this.isOnFire.isPresent() && var1.isOnFire() != this.isOnFire.get()) {
         return false;
      } else if (this.isCrouching.isPresent() && var1.isCrouching() != this.isCrouching.get()) {
         return false;
      } else if (this.isSprinting.isPresent() && var1.isSprinting() != this.isSprinting.get()) {
         return false;
      } else if (this.isSwimming.isPresent() && var1.isSwimming() != this.isSwimming.get()) {
         return false;
      } else {
         if (this.isBaby.isPresent() && var1 instanceof LivingEntity var2 && var2.isBaby() != this.isBaby.get()) {
            return false;
         }

         return true;
      }
   }

   public static class Builder {
      private Optional<Boolean> isOnFire = Optional.empty();
      private Optional<Boolean> isCrouching = Optional.empty();
      private Optional<Boolean> isSprinting = Optional.empty();
      private Optional<Boolean> isSwimming = Optional.empty();
      private Optional<Boolean> isBaby = Optional.empty();

      public Builder() {
         super();
      }

      public static EntityFlagsPredicate.Builder flags() {
         return new EntityFlagsPredicate.Builder();
      }

      public EntityFlagsPredicate.Builder setOnFire(Boolean var1) {
         this.isOnFire = Optional.of(var1);
         return this;
      }

      public EntityFlagsPredicate.Builder setCrouching(Boolean var1) {
         this.isCrouching = Optional.of(var1);
         return this;
      }

      public EntityFlagsPredicate.Builder setSprinting(Boolean var1) {
         this.isSprinting = Optional.of(var1);
         return this;
      }

      public EntityFlagsPredicate.Builder setSwimming(Boolean var1) {
         this.isSwimming = Optional.of(var1);
         return this;
      }

      public EntityFlagsPredicate.Builder setIsBaby(Boolean var1) {
         this.isBaby = Optional.of(var1);
         return this;
      }

      public EntityFlagsPredicate build() {
         return new EntityFlagsPredicate(this.isOnFire, this.isCrouching, this.isSprinting, this.isSwimming, this.isBaby);
      }
   }
}
