package net.minecraft.world.entity.npc;

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractVillager extends AgableMob implements Npc, Merchant {
   private static final EntityDataAccessor<Integer> DATA_UNHAPPY_COUNTER;
   @Nullable
   private Player tradingPlayer;
   @Nullable
   protected MerchantOffers offers;
   private final SimpleContainer inventory = new SimpleContainer(8);

   public AbstractVillager(EntityType<? extends AbstractVillager> var1, Level var2) {
      super(var1, var2);
      this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 16.0F);
      this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      if (var4 == null) {
         var4 = new AgableMob.AgableMobGroupData(false);
      }

      return super.finalizeSpawn(var1, var2, var3, (SpawnGroupData)var4, var5);
   }

   public int getUnhappyCounter() {
      return (Integer)this.entityData.get(DATA_UNHAPPY_COUNTER);
   }

   public void setUnhappyCounter(int var1) {
      this.entityData.set(DATA_UNHAPPY_COUNTER, var1);
   }

   public int getVillagerXp() {
      return 0;
   }

   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return this.isBaby() ? 0.81F : 1.62F;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_UNHAPPY_COUNTER, 0);
   }

   public void setTradingPlayer(@Nullable Player var1) {
      this.tradingPlayer = var1;
   }

   @Nullable
   public Player getTradingPlayer() {
      return this.tradingPlayer;
   }

   public boolean isTrading() {
      return this.tradingPlayer != null;
   }

   public MerchantOffers getOffers() {
      if (this.offers == null) {
         this.offers = new MerchantOffers();
         this.updateTrades();
      }

      return this.offers;
   }

   public void overrideOffers(@Nullable MerchantOffers var1) {
   }

   public void overrideXp(int var1) {
   }

   public void notifyTrade(MerchantOffer var1) {
      var1.increaseUses();
      this.ambientSoundTime = -this.getAmbientSoundInterval();
      this.rewardTradeXp(var1);
      if (this.tradingPlayer instanceof ServerPlayer) {
         CriteriaTriggers.TRADE.trigger((ServerPlayer)this.tradingPlayer, this, var1.getResult());
      }

   }

   protected abstract void rewardTradeXp(MerchantOffer var1);

   public boolean showProgressBar() {
      return true;
   }

   public void notifyTradeUpdated(ItemStack var1) {
      if (!this.level.isClientSide && this.ambientSoundTime > -this.getAmbientSoundInterval() + 20) {
         this.ambientSoundTime = -this.getAmbientSoundInterval();
         this.playSound(this.getTradeUpdatedSound(!var1.isEmpty()), this.getSoundVolume(), this.getVoicePitch());
      }

   }

   public SoundEvent getNotifyTradeSound() {
      return SoundEvents.VILLAGER_YES;
   }

   protected SoundEvent getTradeUpdatedSound(boolean var1) {
      return var1 ? SoundEvents.VILLAGER_YES : SoundEvents.VILLAGER_NO;
   }

   public void playCelebrateSound() {
      this.playSound(SoundEvents.VILLAGER_CELEBRATE, this.getSoundVolume(), this.getVoicePitch());
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      MerchantOffers var2 = this.getOffers();
      if (!var2.isEmpty()) {
         var1.put("Offers", var2.createTag());
      }

      var1.put("Inventory", this.inventory.createTag());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("Offers", 10)) {
         this.offers = new MerchantOffers(var1.getCompound("Offers"));
      }

      this.inventory.fromTag(var1.getList("Inventory", 10));
   }

   @Nullable
   public Entity changeDimension(ServerLevel var1) {
      this.stopTrading();
      return super.changeDimension(var1);
   }

   protected void stopTrading() {
      this.setTradingPlayer((Player)null);
   }

   public void die(DamageSource var1) {
      super.die(var1);
      this.stopTrading();
   }

   protected void addParticlesAroundSelf(ParticleOptions var1) {
      for(int var2 = 0; var2 < 5; ++var2) {
         double var3 = this.random.nextGaussian() * 0.02D;
         double var5 = this.random.nextGaussian() * 0.02D;
         double var7 = this.random.nextGaussian() * 0.02D;
         this.level.addParticle(var1, this.getRandomX(1.0D), this.getRandomY() + 1.0D, this.getRandomZ(1.0D), var3, var5, var7);
      }

   }

   public boolean canBeLeashed(Player var1) {
      return false;
   }

   public SimpleContainer getInventory() {
      return this.inventory;
   }

   public boolean setSlot(int var1, ItemStack var2) {
      if (super.setSlot(var1, var2)) {
         return true;
      } else {
         int var3 = var1 - 300;
         if (var3 >= 0 && var3 < this.inventory.getContainerSize()) {
            this.inventory.setItem(var3, var2);
            return true;
         } else {
            return false;
         }
      }
   }

   public Level getLevel() {
      return this.level;
   }

   protected abstract void updateTrades();

   protected void addOffersFromItemListings(MerchantOffers var1, VillagerTrades.ItemListing[] var2, int var3) {
      HashSet var4 = Sets.newHashSet();
      if (var2.length > var3) {
         while(var4.size() < var3) {
            var4.add(this.random.nextInt(var2.length));
         }
      } else {
         for(int var5 = 0; var5 < var2.length; ++var5) {
            var4.add(var5);
         }
      }

      Iterator var9 = var4.iterator();

      while(var9.hasNext()) {
         Integer var6 = (Integer)var9.next();
         VillagerTrades.ItemListing var7 = var2[var6];
         MerchantOffer var8 = var7.getOffer(this, this.random);
         if (var8 != null) {
            var1.add(var8);
         }
      }

   }

   public Vec3 getRopeHoldPosition(float var1) {
      float var2 = Mth.lerp(var1, this.yBodyRotO, this.yBodyRot) * 0.017453292F;
      Vec3 var3 = new Vec3(0.0D, this.getBoundingBox().getYsize() - 1.0D, 0.2D);
      return this.getPosition(var1).add(var3.yRot(-var2));
   }

   static {
      DATA_UNHAPPY_COUNTER = SynchedEntityData.defineId(AbstractVillager.class, EntityDataSerializers.INT);
   }
}
