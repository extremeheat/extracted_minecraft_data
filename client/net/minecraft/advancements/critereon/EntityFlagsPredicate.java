package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public record EntityFlagsPredicate(Optional<Boolean> isOnGround, Optional<Boolean> isOnFire, Optional<Boolean> isCrouching, Optional<Boolean> isSprinting, Optional<Boolean> isSwimming, Optional<Boolean> isFlying, Optional<Boolean> isBaby) {
   public static final Codec<EntityFlagsPredicate> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.BOOL.optionalFieldOf("is_on_ground").forGetter(EntityFlagsPredicate::isOnGround), Codec.BOOL.optionalFieldOf("is_on_fire").forGetter(EntityFlagsPredicate::isOnFire), Codec.BOOL.optionalFieldOf("is_sneaking").forGetter(EntityFlagsPredicate::isCrouching), Codec.BOOL.optionalFieldOf("is_sprinting").forGetter(EntityFlagsPredicate::isSprinting), Codec.BOOL.optionalFieldOf("is_swimming").forGetter(EntityFlagsPredicate::isSwimming), Codec.BOOL.optionalFieldOf("is_flying").forGetter(EntityFlagsPredicate::isFlying), Codec.BOOL.optionalFieldOf("is_baby").forGetter(EntityFlagsPredicate::isBaby)).apply(var0, EntityFlagsPredicate::new);
   });

   public EntityFlagsPredicate(Optional<Boolean> var1, Optional<Boolean> var2, Optional<Boolean> var3, Optional<Boolean> var4, Optional<Boolean> var5, Optional<Boolean> var6, Optional<Boolean> var7) {
      super();
      this.isOnGround = var1;
      this.isOnFire = var2;
      this.isCrouching = var3;
      this.isSprinting = var4;
      this.isSwimming = var5;
      this.isFlying = var6;
      this.isBaby = var7;
   }

   public boolean matches(Entity var1) {
      if (this.isOnGround.isPresent() && var1.onGround() != (Boolean)this.isOnGround.get()) {
         return false;
      } else if (this.isOnFire.isPresent() && var1.isOnFire() != (Boolean)this.isOnFire.get()) {
         return false;
      } else if (this.isCrouching.isPresent() && var1.isCrouching() != (Boolean)this.isCrouching.get()) {
         return false;
      } else if (this.isSprinting.isPresent() && var1.isSprinting() != (Boolean)this.isSprinting.get()) {
         return false;
      } else if (this.isSwimming.isPresent() && var1.isSwimming() != (Boolean)this.isSwimming.get()) {
         return false;
      } else {
         if (this.isFlying.isPresent()) {
            boolean var10000;
            label54: {
               label53: {
                  if (var1 instanceof LivingEntity) {
                     LivingEntity var4 = (LivingEntity)var1;
                     if (var4.isFallFlying()) {
                        break label53;
                     }

                     if (var4 instanceof Player) {
                        Player var3 = (Player)var4;
                        if (var3.getAbilities().flying) {
                           break label53;
                        }
                     }
                  }

                  var10000 = false;
                  break label54;
               }

               var10000 = true;
            }

            boolean var2 = var10000;
            if (var2 != (Boolean)this.isFlying.get()) {
               return false;
            }
         }

         if (this.isBaby.isPresent() && var1 instanceof LivingEntity) {
            LivingEntity var5 = (LivingEntity)var1;
            if (var5.isBaby() != (Boolean)this.isBaby.get()) {
               return false;
            }
         }

         return true;
      }
   }

   public Optional<Boolean> isOnGround() {
      return this.isOnGround;
   }

   public Optional<Boolean> isOnFire() {
      return this.isOnFire;
   }

   public Optional<Boolean> isCrouching() {
      return this.isCrouching;
   }

   public Optional<Boolean> isSprinting() {
      return this.isSprinting;
   }

   public Optional<Boolean> isSwimming() {
      return this.isSwimming;
   }

   public Optional<Boolean> isFlying() {
      return this.isFlying;
   }

   public Optional<Boolean> isBaby() {
      return this.isBaby;
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

      public static Builder flags() {
         return new Builder();
      }

      public Builder setOnGround(Boolean var1) {
         this.isOnGround = Optional.of(var1);
         return this;
      }

      public Builder setOnFire(Boolean var1) {
         this.isOnFire = Optional.of(var1);
         return this;
      }

      public Builder setCrouching(Boolean var1) {
         this.isCrouching = Optional.of(var1);
         return this;
      }

      public Builder setSprinting(Boolean var1) {
         this.isSprinting = Optional.of(var1);
         return this;
      }

      public Builder setSwimming(Boolean var1) {
         this.isSwimming = Optional.of(var1);
         return this;
      }

      public Builder setIsFlying(Boolean var1) {
         this.isFlying = Optional.of(var1);
         return this;
      }

      public Builder setIsBaby(Boolean var1) {
         this.isBaby = Optional.of(var1);
         return this;
      }

      public EntityFlagsPredicate build() {
         return new EntityFlagsPredicate(this.isOnGround, this.isOnFire, this.isCrouching, this.isSprinting, this.isSwimming, this.isFlying, this.isBaby);
      }
   }
}
