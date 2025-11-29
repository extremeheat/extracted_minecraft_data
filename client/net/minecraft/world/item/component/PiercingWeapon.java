package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.EntityHitResult;

public record PiercingWeapon(boolean dealsKnockback, boolean dismounts, Optional<Holder<SoundEvent>> sound, Optional<Holder<SoundEvent>> hitSound) {
   public static final Codec<PiercingWeapon> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.BOOL.optionalFieldOf("deals_knockback", true).forGetter(PiercingWeapon::dealsKnockback), Codec.BOOL.optionalFieldOf("dismounts", false).forGetter(PiercingWeapon::dismounts), SoundEvent.CODEC.optionalFieldOf("sound").forGetter(PiercingWeapon::sound), SoundEvent.CODEC.optionalFieldOf("hit_sound").forGetter(PiercingWeapon::hitSound)).apply(var0, PiercingWeapon::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, PiercingWeapon> STREAM_CODEC;

   public PiercingWeapon(boolean var1, boolean var2, Optional<Holder<SoundEvent>> var3, Optional<Holder<SoundEvent>> var4) {
      super();
      this.dealsKnockback = var1;
      this.dismounts = var2;
      this.sound = var3;
      this.hitSound = var4;
   }

   public void makeSound(Entity var1) {
      this.sound.ifPresent((var1x) -> var1.level().playSound(var1, var1.getX(), var1.getY(), var1.getZ(), var1x, var1.getSoundSource(), 1.0F, 1.0F));
   }

   public void makeHitSound(Entity var1) {
      this.hitSound.ifPresent((var1x) -> var1.level().playSound((Entity)null, var1.getX(), var1.getY(), var1.getZ(), (Holder)var1x, var1.getSoundSource(), 1.0F, 1.0F));
   }

   public static boolean canHitEntity(Entity var0, Entity var1) {
      if (!var1.isInvulnerable() && var1.isAlive()) {
         if (var1 instanceof Interaction) {
            return true;
         } else if (!var1.canBeHitByProjectile()) {
            return false;
         } else {
            if (var1 instanceof Player) {
               Player var2 = (Player)var1;
               if (var0 instanceof Player) {
                  Player var3 = (Player)var0;
                  if (!var3.canHarmPlayer(var2)) {
                     return false;
                  }
               }
            }

            return !var0.isPassengerOfSameVehicle(var1);
         }
      } else {
         return false;
      }
   }

   public void attack(LivingEntity var1, EquipmentSlot var2) {
      float var3 = (float)var1.getAttributeValue(Attributes.ATTACK_DAMAGE);
      AttackRange var4 = var1.entityAttackRange();
      boolean var5 = false;

      for(EntityHitResult var7 : (Collection)ProjectileUtil.getHitEntitiesAlong(var1, var4, (var1x) -> canHitEntity(var1, var1x), ClipContext.Block.COLLIDER).map((var0) -> List.of(), (var0) -> var0)) {
         var5 |= var1.stabAttack(var2, var7.getEntity(), var3, true, this.dealsKnockback, this.dismounts);
      }

      var1.onAttack();
      var1.lungeForwardMaybe();
      if (var5) {
         this.makeHitSound(var1);
      }

      this.makeSound(var1);
      var1.swing(InteractionHand.MAIN_HAND, false);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, PiercingWeapon::dealsKnockback, ByteBufCodecs.BOOL, PiercingWeapon::dismounts, SoundEvent.STREAM_CODEC.apply(ByteBufCodecs::optional), PiercingWeapon::sound, SoundEvent.STREAM_CODEC.apply(ByteBufCodecs::optional), PiercingWeapon::hitSound, PiercingWeapon::new);
   }
}
