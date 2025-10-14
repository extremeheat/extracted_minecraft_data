package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public record KineticWeapon(float minReach, float maxReach, float hitboxMargin, int contactCooldownTicks, int delayTicks, Optional<Condition> dismountConditions, Optional<Condition> knockbackConditions, Optional<Condition> damageConditions, float forwardMovement, float damageMultiplier, Optional<Holder<SoundEvent>> sound, Optional<Holder<SoundEvent>> hitSound) {
   public static final Codec<KineticWeapon> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("min_reach", 0.0F).forGetter(KineticWeapon::minReach), ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("max_reach", 3.0F).forGetter(KineticWeapon::maxReach), ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("hitbox_margin", 0.3F).forGetter(KineticWeapon::hitboxMargin), ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("contact_cooldown_ticks", 10).forGetter(KineticWeapon::contactCooldownTicks), ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("delay_ticks", 0).forGetter(KineticWeapon::delayTicks), KineticWeapon.Condition.CODEC.optionalFieldOf("dismount_conditions").forGetter(KineticWeapon::dismountConditions), KineticWeapon.Condition.CODEC.optionalFieldOf("knockback_conditions").forGetter(KineticWeapon::knockbackConditions), KineticWeapon.Condition.CODEC.optionalFieldOf("damage_conditions").forGetter(KineticWeapon::damageConditions), Codec.FLOAT.optionalFieldOf("forward_movement", 0.0F).forGetter(KineticWeapon::forwardMovement), Codec.FLOAT.optionalFieldOf("damage_multiplier", 1.0F).forGetter(KineticWeapon::damageMultiplier), SoundEvent.CODEC.optionalFieldOf("sound").forGetter(KineticWeapon::sound), SoundEvent.CODEC.optionalFieldOf("hit_sound").forGetter(KineticWeapon::hitSound)).apply(var0, KineticWeapon::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, KineticWeapon> STREAM_CODEC;

   public KineticWeapon(float var1, float var2, float var3, int var4, int var5, Optional<Condition> var6, Optional<Condition> var7, Optional<Condition> var8, float var9, float var10, Optional<Holder<SoundEvent>> var11, Optional<Holder<SoundEvent>> var12) {
      super();
      this.minReach = var1;
      this.maxReach = var2;
      this.hitboxMargin = var3;
      this.contactCooldownTicks = var4;
      this.delayTicks = var5;
      this.dismountConditions = var6;
      this.knockbackConditions = var7;
      this.damageConditions = var8;
      this.forwardMovement = var9;
      this.damageMultiplier = var10;
      this.sound = var11;
      this.hitSound = var12;
   }

   public static Vec3 getMotion(Entity var0) {
      if (!(var0 instanceof Player) && var0.isPassenger()) {
         var0 = var0.getRootVehicle();
      }

      Vec3 var1 = var0.getKnownMovement().scale(20.0);
      return var0.onGround() ? var1.with(Direction.Axis.Y, 0.0) : var1;
   }

   public void makeSound(Entity var1) {
      this.sound.ifPresent((var1x) -> var1.level().playSound(var1, var1.getX(), var1.getY(), var1.getZ(), var1x, var1.getSoundSource(), 1.0F, 1.0F));
   }

   public void makeHitSound(Entity var1) {
      this.hitSound.ifPresent((var1x) -> var1.level().playSound((Entity)null, var1.getX(), var1.getY(), var1.getZ(), (Holder)var1x, var1.getSoundSource(), 1.0F, 1.0F));
   }

   public int computeDamageUseDuration() {
      return this.delayTicks + (Integer)this.damageConditions.map(Condition::maxDurationTicks).orElse(0);
   }

   public void damageEntities(ItemStack var1, int var2, LivingEntity var3, EquipmentSlot var4) {
      int var5 = var1.getUseDuration(var3) - var2;
      if (var5 >= this.delayTicks) {
         var5 -= this.delayTicks;
         Vec3 var6 = var3.getLookAngle();
         double var7 = var6.dot(getMotion(var3));
         float var9 = var3 instanceof Player ? 1.0F : 0.2F;
         float var10 = var3 instanceof Player ? 1.0F : 0.5F;
         boolean var11 = false;

         for(EntityHitResult var13 : ProjectileUtil.getHitEntitiesAlong(var3, var10 * this.minReach, var10 * this.maxReach, this.hitboxMargin, (var1x) -> PiercingWeapon.canHitEntity(var3, var1x))) {
            Entity var14 = var13.getEntity();
            boolean var15 = var3.wasRecentlyStabbed(var14, this.contactCooldownTicks);
            var3.rememberStabbedEntity(var14);
            if (!var15) {
               double var16 = var6.dot(getMotion(var14));
               double var18 = Math.max(0.0, var7 - var16);
               boolean var20 = this.dismountConditions.isPresent() && ((Condition)this.dismountConditions.get()).test(var5, var7, var18, (double)var9);
               boolean var21 = this.knockbackConditions.isPresent() && ((Condition)this.knockbackConditions.get()).test(var5, var7, var18, (double)var9);
               boolean var22 = this.damageConditions.isPresent() && ((Condition)this.damageConditions.get()).test(var5, var7, var18, (double)var9);
               if (var20 || var21 || var22) {
                  var11 |= var3.stabAttack(var4, var14, (float)Mth.floor(var18 * (double)this.damageMultiplier), var22, var21, var20);
               }
            }
         }

         if (var11) {
            this.makeHitSound(var3);
         }

      }
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT, KineticWeapon::minReach, ByteBufCodecs.FLOAT, KineticWeapon::maxReach, ByteBufCodecs.FLOAT, KineticWeapon::hitboxMargin, ByteBufCodecs.VAR_INT, KineticWeapon::contactCooldownTicks, ByteBufCodecs.VAR_INT, KineticWeapon::delayTicks, KineticWeapon.Condition.STREAM_CODEC.apply(ByteBufCodecs::optional), KineticWeapon::dismountConditions, KineticWeapon.Condition.STREAM_CODEC.apply(ByteBufCodecs::optional), KineticWeapon::knockbackConditions, KineticWeapon.Condition.STREAM_CODEC.apply(ByteBufCodecs::optional), KineticWeapon::damageConditions, ByteBufCodecs.FLOAT, KineticWeapon::forwardMovement, ByteBufCodecs.FLOAT, KineticWeapon::damageMultiplier, SoundEvent.STREAM_CODEC.apply(ByteBufCodecs::optional), KineticWeapon::sound, SoundEvent.STREAM_CODEC.apply(ByteBufCodecs::optional), KineticWeapon::hitSound, KineticWeapon::new);
   }

   public static record Condition(int maxDurationTicks, float minSpeed, float minRelativeSpeed) {
      public static final Codec<Condition> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("max_duration_ticks").forGetter(Condition::maxDurationTicks), Codec.FLOAT.optionalFieldOf("min_speed", 0.0F).forGetter(Condition::minSpeed), Codec.FLOAT.optionalFieldOf("min_relative_speed", 0.0F).forGetter(Condition::minRelativeSpeed)).apply(var0, Condition::new));
      public static final StreamCodec<ByteBuf, Condition> STREAM_CODEC;

      public Condition(int var1, float var2, float var3) {
         super();
         this.maxDurationTicks = var1;
         this.minSpeed = var2;
         this.minRelativeSpeed = var3;
      }

      public boolean test(int var1, double var2, double var4, double var6) {
         return var1 <= this.maxDurationTicks && var2 >= (double)this.minSpeed * var6 && var4 >= (double)this.minRelativeSpeed * var6;
      }

      public static Optional<Condition> ofAttackerSpeed(int var0, float var1) {
         return Optional.of(new Condition(var0, var1, 0.0F));
      }

      public static Optional<Condition> ofRelativeSpeed(int var0, float var1) {
         return Optional.of(new Condition(var0, 0.0F, var1));
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, Condition::maxDurationTicks, ByteBufCodecs.FLOAT, Condition::minSpeed, ByteBufCodecs.FLOAT, Condition::minRelativeSpeed, Condition::new);
      }
   }
}
