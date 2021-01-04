package net.minecraft.world.entity.monster;

import com.mojang.datafixers.Dynamic;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class ZombieVillager extends Zombie implements VillagerDataHolder {
   private static final EntityDataAccessor<Boolean> DATA_CONVERTING_ID;
   private static final EntityDataAccessor<VillagerData> DATA_VILLAGER_DATA;
   private int villagerConversionTime;
   private UUID conversionStarter;
   private Tag gossips;
   private CompoundTag tradeOffers;
   private int villagerXp;

   public ZombieVillager(EntityType<? extends ZombieVillager> var1, Level var2) {
      super(var1, var2);
      this.setVillagerData(this.getVillagerData().setProfession((VillagerProfession)Registry.VILLAGER_PROFESSION.getRandom(this.random)));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_CONVERTING_ID, false);
      this.entityData.define(DATA_VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.put("VillagerData", (Tag)this.getVillagerData().serialize(NbtOps.INSTANCE));
      if (this.tradeOffers != null) {
         var1.put("Offers", this.tradeOffers);
      }

      if (this.gossips != null) {
         var1.put("Gossips", this.gossips);
      }

      var1.putInt("ConversionTime", this.isConverting() ? this.villagerConversionTime : -1);
      if (this.conversionStarter != null) {
         var1.putUUID("ConversionPlayer", this.conversionStarter);
      }

      var1.putInt("Xp", this.villagerXp);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("VillagerData", 10)) {
         this.setVillagerData(new VillagerData(new Dynamic(NbtOps.INSTANCE, var1.get("VillagerData"))));
      }

      if (var1.contains("Offers", 10)) {
         this.tradeOffers = var1.getCompound("Offers");
      }

      if (var1.contains("Gossips", 10)) {
         this.gossips = var1.getList("Gossips", 10);
      }

      if (var1.contains("ConversionTime", 99) && var1.getInt("ConversionTime") > -1) {
         this.startConverting(var1.hasUUID("ConversionPlayer") ? var1.getUUID("ConversionPlayer") : null, var1.getInt("ConversionTime"));
      }

      if (var1.contains("Xp", 3)) {
         this.villagerXp = var1.getInt("Xp");
      }

   }

   public void tick() {
      if (!this.level.isClientSide && this.isAlive() && this.isConverting()) {
         int var1 = this.getConversionProgress();
         this.villagerConversionTime -= var1;
         if (this.villagerConversionTime <= 0) {
            this.finishConversion((ServerLevel)this.level);
         }
      }

      super.tick();
   }

   public boolean mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (var3.getItem() == Items.GOLDEN_APPLE && this.hasEffect(MobEffects.WEAKNESS)) {
         if (!var1.abilities.instabuild) {
            var3.shrink(1);
         }

         if (!this.level.isClientSide) {
            this.startConverting(var1.getUUID(), this.random.nextInt(2401) + 3600);
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean convertsInWater() {
      return false;
   }

   public boolean removeWhenFarAway(double var1) {
      return !this.isConverting();
   }

   public boolean isConverting() {
      return (Boolean)this.getEntityData().get(DATA_CONVERTING_ID);
   }

   private void startConverting(@Nullable UUID var1, int var2) {
      this.conversionStarter = var1;
      this.villagerConversionTime = var2;
      this.getEntityData().set(DATA_CONVERTING_ID, true);
      this.removeEffect(MobEffects.WEAKNESS);
      this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, var2, Math.min(this.level.getDifficulty().getId() - 1, 0)));
      this.level.broadcastEntityEvent(this, (byte)16);
   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 16) {
         if (!this.isSilent()) {
            this.level.playLocalSound(this.x + 0.5D, this.y + 0.5D, this.z + 0.5D, SoundEvents.ZOMBIE_VILLAGER_CURE, this.getSoundSource(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
         }

      } else {
         super.handleEntityEvent(var1);
      }
   }

   private void finishConversion(ServerLevel var1) {
      Villager var2 = (Villager)EntityType.VILLAGER.create(var1);
      var2.copyPosition(this);
      var2.setVillagerData(this.getVillagerData());
      if (this.gossips != null) {
         var2.setGossips(this.gossips);
      }

      if (this.tradeOffers != null) {
         var2.setOffers(new MerchantOffers(this.tradeOffers));
      }

      var2.setVillagerXp(this.villagerXp);
      var2.finalizeSpawn(var1, var1.getCurrentDifficultyAt(new BlockPos(var2)), MobSpawnType.CONVERSION, (SpawnGroupData)null, (CompoundTag)null);
      if (this.isBaby()) {
         var2.setAge(-24000);
      }

      this.remove();
      var2.setNoAi(this.isNoAi());
      if (this.hasCustomName()) {
         var2.setCustomName(this.getCustomName());
         var2.setCustomNameVisible(this.isCustomNameVisible());
      }

      var1.addFreshEntity(var2);
      if (this.conversionStarter != null) {
         Player var3 = var1.getPlayerByUUID(this.conversionStarter);
         if (var3 instanceof ServerPlayer) {
            CriteriaTriggers.CURED_ZOMBIE_VILLAGER.trigger((ServerPlayer)var3, this, var2);
            var1.onReputationEvent(ReputationEventType.ZOMBIE_VILLAGER_CURED, var3, var2);
         }
      }

      var2.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
      var1.levelEvent((Player)null, 1027, new BlockPos(this), 0);
   }

   private int getConversionProgress() {
      int var1 = 1;
      if (this.random.nextFloat() < 0.01F) {
         int var2 = 0;
         BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos();

         for(int var4 = (int)this.x - 4; var4 < (int)this.x + 4 && var2 < 14; ++var4) {
            for(int var5 = (int)this.y - 4; var5 < (int)this.y + 4 && var2 < 14; ++var5) {
               for(int var6 = (int)this.z - 4; var6 < (int)this.z + 4 && var2 < 14; ++var6) {
                  Block var7 = this.level.getBlockState(var3.set(var4, var5, var6)).getBlock();
                  if (var7 == Blocks.IRON_BARS || var7 instanceof BedBlock) {
                     if (this.random.nextFloat() < 0.3F) {
                        ++var1;
                     }

                     ++var2;
                  }
               }
            }
         }
      }

      return var1;
   }

   protected float getVoicePitch() {
      return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 2.0F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
   }

   public SoundEvent getAmbientSound() {
      return SoundEvents.ZOMBIE_VILLAGER_AMBIENT;
   }

   public SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.ZOMBIE_VILLAGER_HURT;
   }

   public SoundEvent getDeathSound() {
      return SoundEvents.ZOMBIE_VILLAGER_DEATH;
   }

   public SoundEvent getStepSound() {
      return SoundEvents.ZOMBIE_VILLAGER_STEP;
   }

   protected ItemStack getSkull() {
      return ItemStack.EMPTY;
   }

   public void setTradeOffers(CompoundTag var1) {
      this.tradeOffers = var1;
   }

   public void setGossips(Tag var1) {
      this.gossips = var1;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      this.setVillagerData(this.getVillagerData().setType(VillagerType.byBiome(var1.getBiome(new BlockPos(this)))));
      return super.finalizeSpawn(var1, var2, var3, var4, var5);
   }

   public void setVillagerData(VillagerData var1) {
      VillagerData var2 = this.getVillagerData();
      if (var2.getProfession() != var1.getProfession()) {
         this.tradeOffers = null;
      }

      this.entityData.set(DATA_VILLAGER_DATA, var1);
   }

   public VillagerData getVillagerData() {
      return (VillagerData)this.entityData.get(DATA_VILLAGER_DATA);
   }

   public void setVillagerXp(int var1) {
      this.villagerXp = var1;
   }

   static {
      DATA_CONVERTING_ID = SynchedEntityData.defineId(ZombieVillager.class, EntityDataSerializers.BOOLEAN);
      DATA_VILLAGER_DATA = SynchedEntityData.defineId(ZombieVillager.class, EntityDataSerializers.VILLAGER_DATA);
   }
}
