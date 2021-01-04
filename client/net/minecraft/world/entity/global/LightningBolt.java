package net.minecraft.world.entity.global;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddGlobalEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class LightningBolt extends Entity {
   private int life;
   public long seed;
   private int flashes;
   private final boolean visualOnly;
   @Nullable
   private ServerPlayer cause;

   public LightningBolt(Level var1, double var2, double var4, double var6, boolean var8) {
      super(EntityType.LIGHTNING_BOLT, var1);
      this.noCulling = true;
      this.moveTo(var2, var4, var6, 0.0F, 0.0F);
      this.life = 2;
      this.seed = this.random.nextLong();
      this.flashes = this.random.nextInt(3) + 1;
      this.visualOnly = var8;
      Difficulty var9 = var1.getDifficulty();
      if (var9 == Difficulty.NORMAL || var9 == Difficulty.HARD) {
         this.spawnFire(4);
      }

   }

   public SoundSource getSoundSource() {
      return SoundSource.WEATHER;
   }

   public void setCause(@Nullable ServerPlayer var1) {
      this.cause = var1;
   }

   public void tick() {
      super.tick();
      if (this.life == 2) {
         this.level.playSound((Player)null, this.x, this.y, this.z, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F);
         this.level.playSound((Player)null, this.x, this.y, this.z, SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.WEATHER, 2.0F, 0.5F + this.random.nextFloat() * 0.2F);
      }

      --this.life;
      if (this.life < 0) {
         if (this.flashes == 0) {
            this.remove();
         } else if (this.life < -this.random.nextInt(10)) {
            --this.flashes;
            this.life = 1;
            this.seed = this.random.nextLong();
            this.spawnFire(0);
         }
      }

      if (this.life >= 0) {
         if (this.level.isClientSide) {
            this.level.setSkyFlashTime(2);
         } else if (!this.visualOnly) {
            double var1 = 3.0D;
            List var3 = this.level.getEntities((Entity)this, new AABB(this.x - 3.0D, this.y - 3.0D, this.z - 3.0D, this.x + 3.0D, this.y + 6.0D + 3.0D, this.z + 3.0D), Entity::isAlive);
            Iterator var4 = var3.iterator();

            while(var4.hasNext()) {
               Entity var5 = (Entity)var4.next();
               var5.thunderHit(this);
            }

            if (this.cause != null) {
               CriteriaTriggers.CHANNELED_LIGHTNING.trigger(this.cause, var3);
            }
         }
      }

   }

   private void spawnFire(int var1) {
      if (!this.visualOnly && !this.level.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
         BlockState var2 = Blocks.FIRE.defaultBlockState();
         BlockPos var3 = new BlockPos(this);
         if (this.level.getBlockState(var3).isAir() && var2.canSurvive(this.level, var3)) {
            this.level.setBlockAndUpdate(var3, var2);
         }

         for(int var4 = 0; var4 < var1; ++var4) {
            BlockPos var5 = var3.offset(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1, this.random.nextInt(3) - 1);
            if (this.level.getBlockState(var5).isAir() && var2.canSurvive(this.level, var5)) {
               this.level.setBlockAndUpdate(var5, var2);
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
      return new ClientboundAddGlobalEntityPacket(this);
   }
}
