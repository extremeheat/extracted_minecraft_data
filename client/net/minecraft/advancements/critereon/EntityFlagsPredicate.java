package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public record EntityFlagsPredicate(
   Optional<Boolean> isOnGround,
   Optional<Boolean> isOnFire,
   Optional<Boolean> isCrouching,
   Optional<Boolean> isSprinting,
   Optional<Boolean> isSwimming,
   Optional<Boolean> isFlying,
   Optional<Boolean> isBaby
) {
   public static final Codec<EntityFlagsPredicate> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               Codec.BOOL.optionalFieldOf("is_on_ground").forGetter(EntityFlagsPredicate::isOnGround),
               Codec.BOOL.optionalFieldOf("is_on_fire").forGetter(EntityFlagsPredicate::isOnFire),
               Codec.BOOL.optionalFieldOf("is_sneaking").forGetter(EntityFlagsPredicate::isCrouching),
               Codec.BOOL.optionalFieldOf("is_sprinting").forGetter(EntityFlagsPredicate::isSprinting),
               Codec.BOOL.optionalFieldOf("is_swimming").forGetter(EntityFlagsPredicate::isSwimming),
               Codec.BOOL.optionalFieldOf("is_flying").forGetter(EntityFlagsPredicate::isFlying),
               Codec.BOOL.optionalFieldOf("is_baby").forGetter(EntityFlagsPredicate::isBaby)
            )
            .apply(var0, EntityFlagsPredicate::new)
   );

   public EntityFlagsPredicate(
      Optional<Boolean> isOnGround,
      Optional<Boolean> isOnFire,
      Optional<Boolean> isCrouching,
      Optional<Boolean> isSprinting,
      Optional<Boolean> isSwimming,
      Optional<Boolean> isFlying,
      Optional<Boolean> isBaby
   ) {
      super();
      this.isOnGround = isOnGround;
      this.isOnFire = isOnFire;
      this.isCrouching = isCrouching;
      this.isSprinting = isSprinting;
      this.isSwimming = isSwimming;
      this.isFlying = isFlying;
      this.isBaby = isBaby;
   }

   public boolean matches(Entity var1) {
      if (this.isOnGround.isPresent() && var1.onGround() != this.isOnGround.get()) {
         return false;
      } else if (this.isOnFire.isPresent() && var1.isOnFire() != this.isOnFire.get()) {
         return false;
      } else if (this.isCrouching.isPresent() && var1.isCrouching() != this.isCrouching.get()) {
         return false;
      } else if (this.isSprinting.isPresent() && var1.isSprinting() != this.isSprinting.get()) {
         return false;
      } else if (this.isSwimming.isPresent() && var1.isSwimming() != this.isSwimming.get()) {
         return false;
      } else {
         if (this.isFlying.isPresent()) {
            boolean var10000;
            label53: {
               if (var1 instanceof LivingEntity var4 && (var4.isFallFlying() || var4 instanceof Player var3 && var3.getAbilities().flying)) {
                  var10000 = true;
                  break label53;
               }

               var10000 = false;
            }

            boolean var2 = var10000;
            if (var2 != this.isFlying.get()) {
               return false;
            }
         }

         if (this.isBaby.isPresent() && var1 instanceof LivingEntity var5 && var5.isBaby() != this.isBaby.get()) {
            return false;
         }

         return true;
      }
   }

   public static class Builder {
      private Optional<Boolean> isOnGround = Optional.empty();
      private Optional<Boolean> isOnFire = Optional.empty();
      private Optional<Boolean> isCrouching = Optional.empty();
      private Optional<Boolean> isSprinting = Optional.empty();
      private Optional<Boolean> isSwimming = Optional.empty();
      private Optional<Boolean> isFlying = Optional.empty();
      private Optional<Boolean> isBaby = Optional.empty();

      public Builder() {
         super();
      }

      public static EntityFlagsPredicate.Builder flags() {
         return new EntityFlagsPredicate.Builder();
      }

      public EntityFlagsPredicate.Builder setOnGround(Boolean var1) {
         this.isOnGround = Optional.of(var1);
         return this;
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

      public EntityFlagsPredicate.Builder setIsFlying(Boolean var1) {
         this.isFlying = Optional.of(var1);
         return this;
      }

      public EntityFlagsPredicate.Builder setIsBaby(Boolean var1) {
         this.isBaby = Optional.of(var1);
         return this;
      }

      public EntityFlagsPredicate build() {
         return new EntityFlagsPredicate(this.isOnGround, this.isOnFire, this.isCrouching, this.isSprinting, this.isSwimming, this.isFlying, this.isBaby);
      }
   }
}
