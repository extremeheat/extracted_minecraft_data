package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public record KineticWeapon(int contactCooldownTicks, int delayTicks, Optional<Condition> dismountConditions, Optional<Condition> knockbackConditions, Optional<Condition> damageConditions, float forwardMovement, float damageMultiplier, Optional<Holder<SoundEvent>> sound, Optional<Holder<SoundEvent>> hitSound) {
   public static final Codec<KineticWeapon> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("contact_cooldown_ticks", 10).forGetter(KineticWeapon::contactCooldownTicks), ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("delay_ticks", 0).forGetter(KineticWeapon::delayTicks), KineticWeapon.Condition.CODEC.optionalFieldOf("dismount_conditions").forGetter(KineticWeapon::dismountConditions), KineticWeapon.Condition.CODEC.optionalFieldOf("knockback_conditions").forGetter(KineticWeapon::knockbackConditions), KineticWeapon.Condition.CODEC.optionalFieldOf("damage_conditions").forGetter(KineticWeapon::damageConditions), Codec.FLOAT.optionalFieldOf("forward_movement", 0.0F).forGetter(KineticWeapon::forwardMovement), Codec.FLOAT.optionalFieldOf("damage_multiplier", 1.0F).forGetter(KineticWeapon::damageMultiplier), SoundEvent.CODEC.optionalFieldOf("sound").forGetter(KineticWeapon::sound), SoundEvent.CODEC.optionalFieldOf("hit_sound").forGetter(KineticWeapon::hitSound)).apply(var0, KineticWeapon::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, KineticWeapon> STREAM_CODEC;

   public KineticWeapon(int var1, int var2, Optional<Condition> var3, Optional<Condition> var4, Optional<Condition> var5, float var6, float var7, Optional<Holder<SoundEvent>> var8, Optional<Holder<SoundEvent>> var9) {
      super();
      this.contactCooldownTicks = var1;
      this.delayTicks = var2;
      this.dismountConditions = var3;
      this.knockbackConditions = var4;
      this.damageConditions = var5;
      this.forwardMovement = var6;
      this.damageMultiplier = var7;
      this.sound = var8;
      this.hitSound = var9;
   }

   public static Vec3 getMotion(Entity var0) {
      if (!(var0 instanceof Player) && var0.isPassenger()) {
         var0 = var0.getRootVehicle();
      }

      return var0.getKnownSpeed().scale(20.0);
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
         AttackRange var10 = var3.entityAttackRange();
         double var11 = var3.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
         boolean var13 = false;

         for(EntityHitResult var15 : (Collection)ProjectileUtil.getHitEntitiesAlong(var3, var10, (var1x) -> PiercingWeapon.canHitEntity(var3, var1x)).map((var0) -> List.of(), (var0) -> var0)) {
            Object var16 = var15.getEntity();
            if (var16 instanceof EnderDragonPart) {
               EnderDragonPart var17 = (EnderDragonPart)var16;
               var16 = var17.parentMob;
            }

            boolean var28 = var3.wasRecentlyStabbed((Entity)var16, this.contactCooldownTicks);
            if (!var28) {
               var3.rememberStabbedEntity((Entity)var16);
               double var18 = var6.dot(getMotion((Entity)var16));
               double var20 = Math.max(0.0, var7 - var18);
               boolean var22 = this.dismountConditions.isPresent() && ((Condition)this.dismountConditions.get()).test(var5, var7, var20, (double)var9);
               boolean var23 = this.knockbackConditions.isPresent() && ((Condition)this.knockbackConditions.get()).test(var5, var7, var20, (double)var9);
               boolean var24 = this.damageConditions.isPresent() && ((Condition)this.damageConditions.get()).test(var5, var7, var20, (double)var9);
               if (var22 || var23 || var24) {
                  float var25 = (float)var11 + (float)Mth.floor(var20 * (double)this.damageMultiplier);
                  var13 |= var3.stabAttack(var4, (Entity)var16, var25, var24, var23, var22);
               }
            }
         }

         if (var13) {
            this.makeHitSound(var3);
            var3.level().broadcastEntityEvent(var3, (byte)2);
            if (var3 instanceof ServerPlayer) {
               ServerPlayer var27 = (ServerPlayer)var3;
               CriteriaTriggers.SPEAR_MOBS_TRIGGER.trigger(var27, var3.stabbedEntities((var0) -> var0 instanceof LivingEntity));
            }
         }

      }
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, KineticWeapon::contactCooldownTicks, ByteBufCodecs.VAR_INT, KineticWeapon::delayTicks, KineticWeapon.Condition.STREAM_CODEC.apply(ByteBufCodecs::optional), KineticWeapon::dismountConditions, KineticWeapon.Condition.STREAM_CODEC.apply(ByteBufCodecs::optional), KineticWeapon::knockbackConditions, KineticWeapon.Condition.STREAM_CODEC.apply(ByteBufCodecs::optional), KineticWeapon::damageConditions, ByteBufCodecs.FLOAT, KineticWeapon::forwardMovement, ByteBufCodecs.FLOAT, KineticWeapon::damageMultiplier, SoundEvent.STREAM_CODEC.apply(ByteBufCodecs::optional), KineticWeapon::sound, SoundEvent.STREAM_CODEC.apply(ByteBufCodecs::optional), KineticWeapon::hitSound, KineticWeapon::new);
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
