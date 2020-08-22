package net.minecraft.world.entity.monster;

import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class ElderGuardian extends Guardian {
   public static final float ELDER_SIZE_SCALE;

   public ElderGuardian(EntityType var1, Level var2) {
      super(var1, var2);
      this.setPersistenceRequired();
      if (this.randomStrollGoal != null) {
         this.randomStrollGoal.setInterval(400);
      }

   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0D);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(80.0D);
   }

   public int getAttackDuration() {
      return 60;
   }

   protected SoundEvent getAmbientSound() {
      return this.isInWaterOrBubble() ? SoundEvents.ELDER_GUARDIAN_AMBIENT : SoundEvents.ELDER_GUARDIAN_AMBIENT_LAND;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return this.isInWaterOrBubble() ? SoundEvents.ELDER_GUARDIAN_HURT : SoundEvents.ELDER_GUARDIAN_HURT_LAND;
   }

   protected SoundEvent getDeathSound() {
      return this.isInWaterOrBubble() ? SoundEvents.ELDER_GUARDIAN_DEATH : SoundEvents.ELDER_GUARDIAN_DEATH_LAND;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.ELDER_GUARDIAN_FLOP;
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      boolean var1 = true;
      if ((this.tickCount + this.getId()) % 1200 == 0) {
         MobEffect var2 = MobEffects.DIG_SLOWDOWN;
         List var3 = ((ServerLevel)this.level).getPlayers((var1x) -> {
            return this.distanceToSqr(var1x) < 2500.0D && var1x.gameMode.isSurvival();
         });
         boolean var4 = true;
         boolean var5 = true;
         boolean var6 = true;
         Iterator var7 = var3.iterator();

         label28:
         while(true) {
            ServerPlayer var8;
            do {
               if (!var7.hasNext()) {
                  break label28;
               }

               var8 = (ServerPlayer)var7.next();
            } while(var8.hasEffect(var2) && var8.getEffect(var2).getAmplifier() >= 2 && var8.getEffect(var2).getDuration() >= 1200);

            var8.connection.send(new ClientboundGameEventPacket(10, 0.0F));
            var8.addEffect(new MobEffectInstance(var2, 6000, 2));
         }
      }

      if (!this.hasRestriction()) {
         this.restrictTo(new BlockPos(this), 16);
      }

   }

   static {
      ELDER_SIZE_SCALE = EntityType.ELDER_GUARDIAN.getWidth() / EntityType.GUARDIAN.getWidth();
   }
}
