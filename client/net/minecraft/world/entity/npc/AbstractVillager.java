package net.minecraft.world.entity.npc;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import java.util.ArrayList;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public abstract class AbstractVillager extends AgeableMob implements InventoryCarrier, Npc, Merchant {
   private static final EntityDataAccessor<Integer> DATA_UNHAPPY_COUNTER;
   private static final Logger LOGGER;
   public static final int VILLAGER_SLOT_OFFSET = 300;
   private static final int VILLAGER_INVENTORY_SIZE = 8;
   @Nullable
   private Player tradingPlayer;
   @Nullable
   protected MerchantOffers offers;
   private final SimpleContainer inventory = new SimpleContainer(8);

   public AbstractVillager(EntityType<? extends AbstractVillager> var1, Level var2) {
      super(var1, var2);
      this.setPathfindingMalus(PathType.DANGER_FIRE, 16.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      if (var4 == null) {
         var4 = new AgeableMob.AgeableMobGroupData(false);
      }

      return super.finalizeSpawn(var1, var2, var3, (SpawnGroupData)var4);
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

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_UNHAPPY_COUNTER, 0);
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
      if (this.level().isClientSide) {
         throw new IllegalStateException("Cannot load Villager offers on the client");
      } else {
         if (this.offers == null) {
            this.offers = new MerchantOffers();
            this.updateTrades();
         }

         return this.offers;
      }
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
      if (!this.level().isClientSide && this.ambientSoundTime > -this.getAmbientSoundInterval() + 20) {
         this.ambientSoundTime = -this.getAmbientSoundInterval();
         this.makeSound(this.getTradeUpdatedSound(!var1.isEmpty()));
      }

   }

   public SoundEvent getNotifyTradeSound() {
      return SoundEvents.VILLAGER_YES;
   }

   protected SoundEvent getTradeUpdatedSound(boolean var1) {
      return var1 ? SoundEvents.VILLAGER_YES : SoundEvents.VILLAGER_NO;
   }

   public void playCelebrateSound() {
      this.makeSound(SoundEvents.VILLAGER_CELEBRATE);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      if (!this.level().isClientSide) {
         MerchantOffers var2 = this.getOffers();
         if (!var2.isEmpty()) {
            var1.put("Offers", (Tag)MerchantOffers.CODEC.encodeStart(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), var2).getOrThrow());
         }
      }

      this.writeInventoryToTag(var1, this.registryAccess());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("Offers")) {
         DataResult var10000 = MerchantOffers.CODEC.parse(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), var1.get("Offers"));
         Logger var10002 = LOGGER;
         Objects.requireNonNull(var10002);
         var10000.resultOrPartial(Util.prefix("Failed to load offers: ", var10002::warn)).ifPresent((var1x) -> this.offers = var1x);
      }

      this.readInventoryFromTag(var1, this.registryAccess());
   }

   @Nullable
   public Entity teleport(TeleportTransition var1) {
      this.stopTrading();
      return super.teleport(var1);
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
         double var3 = this.random.nextGaussian() * 0.02;
         double var5 = this.random.nextGaussian() * 0.02;
         double var7 = this.random.nextGaussian() * 0.02;
         this.level().addParticle(var1, this.getRandomX(1.0), this.getRandomY() + 1.0, this.getRandomZ(1.0), var3, var5, var7);
      }

   }

   public boolean canBeLeashed() {
      return false;
   }

   public SimpleContainer getInventory() {
      return this.inventory;
   }

   public SlotAccess getSlot(int var1) {
      int var2 = var1 - 300;
      return var2 >= 0 && var2 < this.inventory.getContainerSize() ? SlotAccess.forContainer(this.inventory, var2) : super.getSlot(var1);
   }

   protected abstract void updateTrades();

   protected void addOffersFromItemListings(MerchantOffers var1, VillagerTrades.ItemListing[] var2, int var3) {
      ArrayList var4 = Lists.newArrayList(var2);
      int var5 = 0;

      while(var5 < var3 && !var4.isEmpty()) {
         MerchantOffer var6 = ((VillagerTrades.ItemListing)var4.remove(this.random.nextInt(var4.size()))).getOffer(this, this.random);
         if (var6 != null) {
            var1.add(var6);
            ++var5;
         }
      }

   }

   public Vec3 getRopeHoldPosition(float var1) {
      float var2 = Mth.lerp(var1, this.yBodyRotO, this.yBodyRot) * 0.017453292F;
      Vec3 var3 = new Vec3(0.0, this.getBoundingBox().getYsize() - 1.0, 0.2);
      return this.getPosition(var1).add(var3.yRot(-var2));
   }

   public boolean isClientSide() {
      return this.level().isClientSide;
   }

   public boolean stillValid(Player var1) {
      return this.getTradingPlayer() == var1 && this.isAlive() && var1.canInteractWithEntity((Entity)this, 4.0);
   }

   static {
      DATA_UNHAPPY_COUNTER = SynchedEntityData.<Integer>defineId(AbstractVillager.class, EntityDataSerializers.INT);
      LOGGER = LogUtils.getLogger();
   }
}
