package net.minecraft.world.entity;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class LightningBolt extends Entity {
   private int life;
   public long seed;
   private int flashes;
   private boolean visualOnly;
   @Nullable
   private ServerPlayer cause;

   public LightningBolt(EntityType<? extends LightningBolt> var1, Level var2) {
      super(var1, var2);
      this.noCulling = true;
      this.life = 2;
      this.seed = this.random.nextLong();
      this.flashes = this.random.nextInt(3) + 1;
   }

   public void setVisualOnly(boolean var1) {
      this.visualOnly = var1;
   }

   public SoundSource getSoundSource() {
      return SoundSource.WEATHER;
   }

   public void setCause(@Nullable ServerPlayer var1) {
      this.cause = var1;
   }

   private void powerLightningRod() {
      BlockPos var1 = this.blockPosition().below();
      BlockState var2 = this.level.getBlockState(var1);
      if (var2.is(Blocks.LIGHTNING_ROD)) {
         ((LightningRodBlock)var2.getBlock()).onLightningStrike(var2, this.level, var1);
      }

   }

   public void tick() {
      super.tick();
      if (this.life == 2) {
         Difficulty var1 = this.level.getDifficulty();
         if (var1 == Difficulty.NORMAL || var1 == Difficulty.HARD) {
            this.spawnFire(4);
         }

         this.powerLightningRod();
         this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F);
         this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.WEATHER, 2.0F, 0.5F + this.random.nextFloat() * 0.2F);
      }

      --this.life;
      if (this.life < 0) {
         if (this.flashes == 0) {
            this.discard();
         } else if (this.life < -this.random.nextInt(10)) {
            --this.flashes;
            this.life = 1;
            this.seed = this.random.nextLong();
            this.spawnFire(0);
         }
      }

      if (this.life >= 0) {
         if (!(this.level instanceof ServerLevel)) {
            this.level.setSkyFlashTime(2);
         } else if (!this.visualOnly) {
            double var6 = 3.0D;
            List var3 = this.level.getEntities((Entity)this, new AABB(this.getX() - 3.0D, this.getY() - 3.0D, this.getZ() - 3.0D, this.getX() + 3.0D, this.getY() + 6.0D + 3.0D, this.getZ() + 3.0D), Entity::isAlive);
            Iterator var4 = var3.iterator();

            while(var4.hasNext()) {
               Entity var5 = (Entity)var4.next();
               var5.thunderHit((ServerLevel)this.level, this);
            }

            if (this.cause != null) {
               CriteriaTriggers.CHANNELED_LIGHTNING.trigger(this.cause, var3);
            }
         }
      }

   }

   private void spawnFire(int var1) {
      if (!this.visualOnly && !this.level.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
         BlockPos var2 = this.blockPosition();
         BlockState var3 = BaseFireBlock.getState(this.level, var2);
         if (this.level.getBlockState(var2).isAir() && var3.canSurvive(this.level, var2)) {
            this.level.setBlockAndUpdate(var2, var3);
         }

         for(int var4 = 0; var4 < var1; ++var4) {
            BlockPos var5 = var2.offset(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1, this.random.nextInt(3) - 1);
            var3 = BaseFireBlock.getState(this.level, var5);
            if (this.level.getBlockState(var5).isAir() && var3.canSurvive(this.level, var5)) {
               this.level.setBlockAndUpdate(var5, var3);
            }
         }

      }
   }

   public boolean shouldRenderAtSqrDistance(double var1) {
      double var3 = 64.0D * getViewScale();
      return var1 < var3 * var3;
   }

   protected void defineSynchedData() {
   }

   protected void readAdditionalSaveData(CompoundTag var1) {
   }

   protected void addAdditionalSaveData(CompoundTag var1) {
   }

   public Packet<?> getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }
}
