package net.minecraft.world.entity.monster;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SlotAccess;
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
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class ZombieVillager extends Zombie implements VillagerDataHolder {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final EntityDataAccessor<Boolean> DATA_CONVERTING_ID;
   private static final EntityDataAccessor<VillagerData> DATA_VILLAGER_DATA;
   private static final int VILLAGER_CONVERSION_WAIT_MIN = 3600;
   private static final int VILLAGER_CONVERSION_WAIT_MAX = 6000;
   private static final int MAX_SPECIAL_BLOCKS_COUNT = 14;
   private static final int SPECIAL_BLOCK_RADIUS = 4;
   private int villagerConversionTime;
   @Nullable
   private UUID conversionStarter;
   @Nullable
   private Tag gossips;
   @Nullable
   private MerchantOffers tradeOffers;
   private int villagerXp;

   public ZombieVillager(EntityType<? extends ZombieVillager> var1, Level var2) {
      super(var1, var2);
      BuiltInRegistries.VILLAGER_PROFESSION.getRandom(this.random).ifPresent((var1x) -> {
         this.setVillagerData(this.getVillagerData().setProfession((VillagerProfession)var1x.value()));
      });
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_CONVERTING_ID, false);
      var1.define(DATA_VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      DataResult var10000 = VillagerData.CODEC.encodeStart(NbtOps.INSTANCE, this.getVillagerData());
      Logger var10001 = LOGGER;
      Objects.requireNonNull(var10001);
      var10000.resultOrPartial(var10001::error).ifPresent((var1x) -> {
         var1.put("VillagerData", var1x);
      });
      if (this.tradeOffers != null) {
         var1.put("Offers", (Tag)MerchantOffers.CODEC.encodeStart(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), this.tradeOffers).getOrThrow());
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
         DataResult var2 = VillagerData.CODEC.parse(new Dynamic(NbtOps.INSTANCE, var1.get("VillagerData")));
         Logger var10001 = LOGGER;
         Objects.requireNonNull(var10001);
         var2.resultOrPartial(var10001::error).ifPresent(this::setVillagerData);
      }

      if (var1.contains("Offers")) {
         DataResult var10000 = MerchantOffers.CODEC.parse(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), var1.get("Offers"));
         Logger var10002 = LOGGER;
         Objects.requireNonNull(var10002);
         var10000.resultOrPartial(Util.prefix("Failed to load offers: ", var10002::warn)).ifPresent((var1x) -> {
            this.tradeOffers = var1x;
         });
      }

      if (var1.contains("Gossips", 9)) {
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

            return InteractionResult.SUCCESS;
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
      this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, var2, Math.min(this.level().getDifficulty().getId() - 1, 0)));
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
      Villager var2 = (Villager)this.convertTo(EntityType.VILLAGER, false);
      if (var2 != null) {
         Iterator var3 = this.dropPreservedEquipment((var0) -> {
            return !EnchantmentHelper.has(var0, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE);
         }).iterator();

         while(var3.hasNext()) {
            EquipmentSlot var4 = (EquipmentSlot)var3.next();
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
         var2.finalizeSpawn(var1, var1.getCurrentDifficultyAt(var2.blockPosition()), MobSpawnType.CONVERSION, (SpawnGroupData)null);
         var2.refreshBrain(var1);
         if (this.conversionStarter != null) {
            Player var6 = var1.getPlayerByUUID(this.conversionStarter);
            if (var6 instanceof ServerPlayer) {
               CriteriaTriggers.CURED_ZOMBIE_VILLAGER.trigger((ServerPlayer)var6, this, var2);
               var1.onReputationEvent(ReputationEventType.ZOMBIE_VILLAGER_CURED, var6, var2);
            }
         }

         var2.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
         if (!this.isSilent()) {
            var1.levelEvent((Player)null, 1027, this.blockPosition(), 0);
         }

      }
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

   public void setGossips(Tag var1) {
      this.gossips = var1;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4) {
      this.setVillagerData(this.getVillagerData().setType(VillagerType.byBiome(var1.getBiome(this.blockPosition()))));
      return super.finalizeSpawn(var1, var2, var3, var4);
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

   public int getVillagerXp() {
      return this.villagerXp;
   }

   public void setVillagerXp(int var1) {
      this.villagerXp = var1;
   }

   static {
      DATA_CONVERTING_ID = SynchedEntityData.defineId(ZombieVillager.class, EntityDataSerializers.BOOLEAN);
      DATA_VILLAGER_DATA = SynchedEntityData.defineId(ZombieVillager.class, EntityDataSerializers.VILLAGER_DATA);
   }
}
