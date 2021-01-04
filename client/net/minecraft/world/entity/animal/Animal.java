package net.minecraft.world.entity.animal;

import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;

public abstract class Animal extends AgableMob {
   private int inLove;
   private UUID loveCause;

   protected Animal(EntityType<? extends Animal> var1, Level var2) {
      super(var1, var2);
   }

   protected void customServerAiStep() {
      if (this.getAge() != 0) {
         this.inLove = 0;
      }

      super.customServerAiStep();
   }

   public void aiStep() {
      super.aiStep();
      if (this.getAge() != 0) {
         this.inLove = 0;
      }

      if (this.inLove > 0) {
         --this.inLove;
         if (this.inLove % 10 == 0) {
            double var1 = this.random.nextGaussian() * 0.02D;
            double var3 = this.random.nextGaussian() * 0.02D;
            double var5 = this.random.nextGaussian() * 0.02D;
            this.level.addParticle(ParticleTypes.HEART, this.x + (double)(this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double)this.getBbWidth(), this.y + 0.5D + (double)(this.random.nextFloat() * this.getBbHeight()), this.z + (double)(this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double)this.getBbWidth(), var1, var3, var5);
         }
      }

   }

   public boolean hurt(DamageSource var1, float var2) {
      if (this.isInvulnerableTo(var1)) {
         return false;
      } else {
         this.inLove = 0;
         return super.hurt(var1, var2);
      }
   }

   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      return var2.getBlockState(var1.below()).getBlock() == Blocks.GRASS_BLOCK ? 10.0F : var2.getBrightness(var1) - 0.5F;
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("InLove", this.inLove);
      if (this.loveCause != null) {
         var1.putUUID("LoveCause", this.loveCause);
      }

   }

   public double getRidingHeight() {
      return 0.14D;
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.inLove = var1.getInt("InLove");
      this.loveCause = var1.hasUUID("LoveCause") ? var1.getUUID("LoveCause") : null;
   }

   public static boolean checkAnimalSpawnRules(EntityType<? extends Animal> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, Random var4) {
      return var1.getBlockState(var3.below()).getBlock() == Blocks.GRASS_BLOCK && var1.getRawBrightness(var3, 0) > 8;
   }

   public int getAmbientSoundInterval() {
      return 120;
   }

   public boolean removeWhenFarAway(double var1) {
      return false;
   }

   protected int getExperienceReward(Player var1) {
      return 1 + this.level.random.nextInt(3);
   }

   public boolean isFood(ItemStack var1) {
      return var1.getItem() == Items.WHEAT;
   }

   public boolean mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (this.isFood(var3)) {
         if (this.getAge() == 0 && this.canFallInLove()) {
            this.usePlayerItem(var1, var3);
            this.setInLove(var1);
            return true;
         }

         if (this.isBaby()) {
            this.usePlayerItem(var1, var3);
            this.ageUp((int)((float)(-this.getAge() / 20) * 0.1F), true);
            return true;
         }
      }

      return super.mobInteract(var1, var2);
   }

   protected void usePlayerItem(Player var1, ItemStack var2) {
      if (!var1.abilities.instabuild) {
         var2.shrink(1);
      }

   }

   public boolean canFallInLove() {
      return this.inLove <= 0;
   }

   public void setInLove(@Nullable Player var1) {
      this.inLove = 600;
      if (var1 != null) {
         this.loveCause = var1.getUUID();
      }

      this.level.broadcastEntityEvent(this, (byte)18);
   }

   public void setInLoveTime(int var1) {
      this.inLove = var1;
   }

   @Nullable
   public ServerPlayer getLoveCause() {
      if (this.loveCause == null) {
         return null;
      } else {
         Player var1 = this.level.getPlayerByUUID(this.loveCause);
         return var1 instanceof ServerPlayer ? (ServerPlayer)var1 : null;
      }
   }

   public boolean isInLove() {
      return this.inLove > 0;
   }

   public void resetLove() {
      this.inLove = 0;
   }

   public boolean canMate(Animal var1) {
      if (var1 == this) {
         return false;
      } else if (var1.getClass() != this.getClass()) {
         return false;
      } else {
         return this.isInLove() && var1.isInLove();
      }
   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 18) {
         for(int var2 = 0; var2 < 7; ++var2) {
            double var3 = this.random.nextGaussian() * 0.02D;
            double var5 = this.random.nextGaussian() * 0.02D;
            double var7 = this.random.nextGaussian() * 0.02D;
            this.level.addParticle(ParticleTypes.HEART, this.x + (double)(this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double)this.getBbWidth(), this.y + 0.5D + (double)(this.random.nextFloat() * this.getBbHeight()), this.z + (double)(this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double)this.getBbWidth(), var3, var5, var7);
         }
      } else {
         super.handleEntityEvent(var1);
      }

   }
}
