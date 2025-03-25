package net.minecraft.world.entity.monster;

import com.google.common.annotations.VisibleForTesting;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ZombieVillager extends Zombie implements VillagerDataHolder {
   private static final EntityDataAccessor<Boolean> DATA_CONVERTING_ID;
   private static final EntityDataAccessor<VillagerData> DATA_VILLAGER_DATA;
   private static final int VILLAGER_CONVERSION_WAIT_MIN = 3600;
   private static final int VILLAGER_CONVERSION_WAIT_MAX = 6000;
   private static final int MAX_SPECIAL_BLOCKS_COUNT = 14;
   private static final int SPECIAL_BLOCK_RADIUS = 4;
   private static final int NOT_CONVERTING = -1;
   private static final int DEFAULT_XP = 0;
   private int villagerConversionTime;
   @Nullable
   private UUID conversionStarter;
   @Nullable
   private GossipContainer gossips;
   @Nullable
   private MerchantOffers tradeOffers;
   private int villagerXp = 0;

   public ZombieVillager(EntityType<? extends ZombieVillager> var1, Level var2) {
      super(var1, var2);
      BuiltInRegistries.VILLAGER_PROFESSION.getRandom(this.random).ifPresent((var1x) -> this.setVillagerData(this.getVillagerData().withProfession(var1x)));
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_CONVERTING_ID, false);
      var1.define(DATA_VILLAGER_DATA, Villager.createDefaultVillagerData());
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.store("VillagerData", VillagerData.CODEC, this.getVillagerData());
      var1.storeNullable("Offers", MerchantOffers.CODEC, this.registryAccess().createSerializationContext(NbtOps.INSTANCE), this.tradeOffers);
      var1.storeNullable("Gossips", GossipContainer.CODEC, this.gossips);
      var1.putInt("ConversionTime", this.isConverting() ? this.villagerConversionTime : -1);
      var1.storeNullable("ConversionPlayer", UUIDUtil.CODEC, this.conversionStarter);
      var1.putInt("Xp", this.villagerXp);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.entityData.set(DATA_VILLAGER_DATA, (VillagerData)var1.read("VillagerData", VillagerData.CODEC).orElseGet(Villager::createDefaultVillagerData));
      this.tradeOffers = (MerchantOffers)var1.read("Offers", MerchantOffers.CODEC, this.registryAccess().createSerializationContext(NbtOps.INSTANCE)).orElse((Object)null);
      this.gossips = (GossipContainer)var1.read("Gossips", GossipContainer.CODEC).orElse((Object)null);
      int var2 = var1.getIntOr("ConversionTime", -1);
      if (var2 != -1) {
         UUID var3 = (UUID)var1.read("ConversionPlayer", UUIDUtil.CODEC).orElse((Object)null);
         this.startConverting(var3, var2);
      } else {
         this.getEntityData().set(DATA_CONVERTING_ID, false);
         this.villagerConversionTime = -1;
      }

      this.villagerXp = var1.getIntOr("Xp", 0);
   }

   public void tick() {
      if (!this.level().isClientSide && this.isAlive() && this.isConverting()) {
         int var1 = this.getConversionProgress();
         this.villagerConversionTime -= var1;
         if (this.villagerConversionTime <= 0) {
            this.finishConversion((ServerLevel)this.level());
         }
      }

      super.tick();
   }

   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (var3.is(Items.GOLDEN_APPLE)) {
         if (this.hasEffect(MobEffects.WEAKNESS)) {
            var3.consume(1, var1);
            if (!this.level().isClientSide) {
               this.startConverting(var1.getUUID(), this.random.nextInt(2401) + 3600);
            }

            return InteractionResult.SUCCESS_SERVER;
         } else {
            return InteractionResult.CONSUME;
         }
      } else {
         return super.mobInteract(var1, var2);
      }
   }

   protected boolean convertsInWater() {
      return false;
   }

   public boolean removeWhenFarAway(double var1) {
      return !this.isConverting() && this.villagerXp == 0;
   }

   public boolean isConverting() {
      return (Boolean)this.getEntityData().get(DATA_CONVERTING_ID);
   }

   private void startConverting(@Nullable UUID var1, int var2) {
      this.conversionStarter = var1;
      this.villagerConversionTime = var2;
      this.getEntityData().set(DATA_CONVERTING_ID, true);
      this.removeEffect(MobEffects.WEAKNESS);
      this.addEffect(new MobEffectInstance(MobEffects.STRENGTH, var2, Math.min(this.level().getDifficulty().getId() - 1, 0)));
      this.level().broadcastEntityEvent(this, (byte)16);
   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 16) {
         if (!this.isSilent()) {
            this.level().playLocalSound(this.getX(), this.getEyeY(), this.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, this.getSoundSource(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
         }

      } else {
         super.handleEntityEvent(var1);
      }
   }

   private void finishConversion(ServerLevel var1) {
      this.convertTo(EntityType.VILLAGER, ConversionParams.single(this, false, false), (var2) -> {
         for(EquipmentSlot var4 : this.dropPreservedEquipment(var1, (var0) -> !EnchantmentHelper.has(var0, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE))) {
            SlotAccess var5 = var2.getSlot(var4.getIndex() + 300);
            var5.set(this.getItemBySlot(var4));
         }

         var2.setVillagerData(this.getVillagerData());
         if (this.gossips != null) {
            var2.setGossips(this.gossips);
         }

         if (this.tradeOffers != null) {
            var2.setOffers(this.tradeOffers.copy());
         }

         var2.setVillagerXp(this.villagerXp);
         var2.finalizeSpawn(var1, var1.getCurrentDifficultyAt(var2.blockPosition()), EntitySpawnReason.CONVERSION, (SpawnGroupData)null);
         var2.refreshBrain(var1);
         if (this.conversionStarter != null) {
            Player var6 = var1.getPlayerByUUID(this.conversionStarter);
            if (var6 instanceof ServerPlayer) {
               CriteriaTriggers.CURED_ZOMBIE_VILLAGER.trigger((ServerPlayer)var6, this, var2);
               var1.onReputationEvent(ReputationEventType.ZOMBIE_VILLAGER_CURED, var6, var2);
            }
         }

         var2.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 200, 0));
         if (!this.isSilent()) {
            var1.levelEvent((Entity)null, 1027, this.blockPosition(), 0);
         }

      });
   }

   @VisibleForTesting
   public void setVillagerConversionTime(int var1) {
      this.villagerConversionTime = var1;
   }

   private int getConversionProgress() {
      int var1 = 1;
      if (this.random.nextFloat() < 0.01F) {
         int var2 = 0;
         BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos();

         for(int var4 = (int)this.getX() - 4; var4 < (int)this.getX() + 4 && var2 < 14; ++var4) {
            for(int var5 = (int)this.getY() - 4; var5 < (int)this.getY() + 4 && var2 < 14; ++var5) {
               for(int var6 = (int)this.getZ() - 4; var6 < (int)this.getZ() + 4 && var2 < 14; ++var6) {
                  BlockState var7 = this.level().getBlockState(var3.set(var4, var5, var6));
                  if (var7.is(Blocks.IRON_BARS) || var7.getBlock() instanceof BedBlock) {
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

   public float getVoicePitch() {
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

   public void setTradeOffers(MerchantOffers var1) {
      this.tradeOffers = var1;
   }

   public void setGossips(GossipContainer var1) {
      this.gossips = var1;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      this.setVillagerData(this.getVillagerData().withType(var1.registryAccess(), VillagerType.byBiome(var1.getBiome(this.blockPosition()))));
      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   public void setVillagerData(VillagerData var1) {
      VillagerData var2 = this.getVillagerData();
      if (!var2.profession().equals(var1.profession())) {
         this.tradeOffers = null;
      }

      this.entityData.set(DATA_VILLAGER_DATA, var1);
   }

   public VillagerData getVillagerData() {
      return (VillagerData)this.entityData.get(DATA_VILLAGER_DATA);
   }

   public int getVillagerXp() {
      return this.villagerXp;
   }

   public void setVillagerXp(int var1) {
      this.villagerXp = var1;
   }

   @Nullable
   public <T> T get(DataComponentType<? extends T> var1) {
      return (T)(var1 == DataComponents.VILLAGER_VARIANT ? castComponentValue(var1, this.getVillagerData().type()) : super.get(var1));
   }

   protected void applyImplicitComponents(DataComponentGetter var1) {
      this.applyImplicitComponentIfPresent(var1, DataComponents.VILLAGER_VARIANT);
      super.applyImplicitComponents(var1);
   }

   protected <T> boolean applyImplicitComponent(DataComponentType<T> var1, T var2) {
      if (var1 == DataComponents.VILLAGER_VARIANT) {
         Holder var3 = (Holder)castComponentValue(DataComponents.VILLAGER_VARIANT, var2);
         this.setVillagerData(this.getVillagerData().withType(var3));
         return true;
      } else {
         return super.applyImplicitComponent(var1, var2);
      }
   }

   static {
      DATA_CONVERTING_ID = SynchedEntityData.<Boolean>defineId(ZombieVillager.class, EntityDataSerializers.BOOLEAN);
      DATA_VILLAGER_DATA = SynchedEntityData.<VillagerData>defineId(ZombieVillager.class, EntityDataSerializers.VILLAGER_DATA);
   }
}
