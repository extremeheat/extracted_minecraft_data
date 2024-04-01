package net.minecraft.world.level.block.entity.vault;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class VaultBlockEntity extends BlockEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final VaultServerData serverData = new VaultServerData();
   private final VaultSharedData sharedData = new VaultSharedData();
   private final VaultClientData clientData = new VaultClientData();
   private VaultConfig config = VaultConfig.DEFAULT;

   public VaultBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.VAULT, var1, var2);
   }

   @Nullable
   @Override
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @Override
   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      return Util.make(new CompoundTag(), var2 -> var2.put("shared_data", encode(VaultSharedData.CODEC, this.sharedData, var1)));
   }

   @Override
   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      var1.put("config", encode(VaultConfig.CODEC, this.config, var2));
      var1.put("shared_data", encode(VaultSharedData.CODEC, this.sharedData, var2));
      var1.put("server_data", encode(VaultServerData.CODEC, this.serverData, var2));
   }

   private static <T> Tag encode(Codec<T> var0, T var1, HolderLookup.Provider var2) {
      return Util.getOrThrow(var0.encodeStart(var2.createSerializationContext(NbtOps.INSTANCE), var1), IllegalStateException::new);
   }

   @Override
   public void load(CompoundTag var1, HolderLookup.Provider var2) {
      super.load(var1, var2);
      RegistryOps var3 = var2.createSerializationContext(NbtOps.INSTANCE);
      if (var1.contains("server_data")) {
         VaultServerData.CODEC.parse(var3, var1.get("server_data")).resultOrPartial(LOGGER::error).ifPresent(this.serverData::set);
      }

      if (var1.contains("config")) {
         VaultConfig.CODEC.parse(var3, var1.get("config")).resultOrPartial(LOGGER::error).ifPresent(var1x -> this.config = var1x);
      }

      if (var1.contains("shared_data")) {
         VaultSharedData.CODEC.parse(var3, var1.get("shared_data")).resultOrPartial(LOGGER::error).ifPresent(this.sharedData::set);
      }
   }

   @Nullable
   public VaultServerData getServerData() {
      return this.level != null && !this.level.isClientSide ? this.serverData : null;
   }

   public VaultSharedData getSharedData() {
      return this.sharedData;
   }

   public VaultClientData getClientData() {
      return this.clientData;
   }

   public VaultConfig getConfig() {
      return this.config;
   }

   @VisibleForTesting
   public void setConfig(VaultConfig var1) {
      this.config = var1;
   }

   public static final class Client {
      private static final int PARTICLE_TICK_RATE = 20;
      private static final float IDLE_PARTICLE_CHANCE = 0.5F;
      private static final float AMBIENT_SOUND_CHANCE = 0.02F;
      private static final int ACTIVATION_PARTICLE_COUNT = 20;
      private static final int DEACTIVATION_PARTICLE_COUNT = 20;

      public Client() {
         super();
      }

      public static void tick(Level var0, BlockPos var1, BlockState var2, VaultClientData var3, VaultSharedData var4) {
         var3.updateDisplayItemSpin();
         if (var0.getGameTime() % 20L == 0L) {
            emitConnectionParticlesForNearbyPlayers(var0, var1, var2, var4);
         }

         emitIdleParticles(var0, var1, var4);
         playIdleSounds(var0, var1, var4);
      }

      public static void emitActivationParticles(Level var0, BlockPos var1, BlockState var2, VaultSharedData var3) {
         emitConnectionParticlesForNearbyPlayers(var0, var1, var2, var3);
         RandomSource var4 = var0.random;

         for(int var5 = 0; var5 < 20; ++var5) {
            Vec3 var6 = randomPosInsideCage(var1, var4);
            var0.addParticle(ParticleTypes.SMOKE, var6.x(), var6.y(), var6.z(), 0.0, 0.0, 0.0);
            var0.addParticle(ParticleTypes.SMALL_FLAME, var6.x(), var6.y(), var6.z(), 0.0, 0.0, 0.0);
         }
      }

      public static void emitDeactivationParticles(Level var0, BlockPos var1) {
         RandomSource var2 = var0.random;

         for(int var3 = 0; var3 < 20; ++var3) {
            Vec3 var4 = randomPosCenterOfCage(var1, var2);
            Vec3 var5 = new Vec3(var2.nextGaussian() * 0.02, var2.nextGaussian() * 0.02, var2.nextGaussian() * 0.02);
            var0.addParticle(ParticleTypes.SMALL_FLAME, var4.x(), var4.y(), var4.z(), var5.x(), var5.y(), var5.z());
         }
      }

      private static void emitIdleParticles(Level var0, BlockPos var1, VaultSharedData var2) {
         RandomSource var3 = var0.getRandom();
         if (var3.nextFloat() <= 0.5F) {
            Vec3 var4 = randomPosInsideCage(var1, var3);
            var0.addParticle(ParticleTypes.SMOKE, var4.x(), var4.y(), var4.z(), 0.0, 0.0, 0.0);
            if (shouldDisplayActiveEffects(var2)) {
               var0.addParticle(ParticleTypes.SMALL_FLAME, var4.x(), var4.y(), var4.z(), 0.0, 0.0, 0.0);
            }
         }
      }

      private static void emitConnectionParticlesForPlayer(Level var0, Vec3 var1, Player var2) {
         RandomSource var3 = var0.random;
         Vec3 var4 = var1.vectorTo(var2.position().add(0.0, (double)(var2.getBbHeight() / 2.0F), 0.0));
         int var5 = Mth.nextInt(var3, 2, 5);

         for(int var6 = 0; var6 < var5; ++var6) {
            Vec3 var7 = var4.offsetRandom(var3, 1.0F);
            var0.addParticle(ParticleTypes.VAULT_CONNECTION, var1.x(), var1.y(), var1.z(), var7.x(), var7.y(), var7.z());
         }
      }

      private static void emitConnectionParticlesForNearbyPlayers(Level var0, BlockPos var1, BlockState var2, VaultSharedData var3) {
         Set var4 = var3.getConnectedPlayers();
         if (!var4.isEmpty()) {
            Vec3 var5 = keyholePos(var1, var2.getValue(VaultBlock.FACING));

            for(UUID var7 : var4) {
               Player var8 = var0.getPlayerByUUID(var7);
               if (var8 != null && isWithinConnectionRange(var1, var3, var8)) {
                  emitConnectionParticlesForPlayer(var0, var5, var8);
               }
            }
         }
      }

      private static boolean isWithinConnectionRange(BlockPos var0, VaultSharedData var1, Player var2) {
         return var2.blockPosition().distSqr(var0) <= Mth.square(var1.connectedParticlesRange());
      }

      private static void playIdleSounds(Level var0, BlockPos var1, VaultSharedData var2) {
         if (shouldDisplayActiveEffects(var2)) {
            RandomSource var3 = var0.getRandom();
            if (var3.nextFloat() <= 0.02F) {
               var0.playLocalSound(var1, SoundEvents.VAULT_AMBIENT, SoundSource.BLOCKS, var3.nextFloat() * 0.25F + 0.75F, var3.nextFloat() + 0.5F, false);
            }
         }
      }

      public static boolean shouldDisplayActiveEffects(VaultSharedData var0) {
         return var0.hasDisplayItem();
      }

      private static Vec3 randomPosCenterOfCage(BlockPos var0, RandomSource var1) {
         return Vec3.atLowerCornerOf(var0).add(Mth.nextDouble(var1, 0.4, 0.6), Mth.nextDouble(var1, 0.4, 0.6), Mth.nextDouble(var1, 0.4, 0.6));
      }

      private static Vec3 randomPosInsideCage(BlockPos var0, RandomSource var1) {
         return Vec3.atLowerCornerOf(var0).add(Mth.nextDouble(var1, 0.1, 0.9), Mth.nextDouble(var1, 0.25, 0.75), Mth.nextDouble(var1, 0.1, 0.9));
      }

      private static Vec3 keyholePos(BlockPos var0, Direction var1) {
         return Vec3.atBottomCenterOf(var0).add((double)var1.getStepX() * 0.5, 1.75, (double)var1.getStepZ() * 0.5);
      }
   }

   public static final class Server {
      private static final int UNLOCKING_DELAY_TICKS = 14;
      private static final int DISPLAY_CYCLE_TICK_RATE = 20;
      private static final int INSERT_FAIL_SOUND_BUFFER_TICKS = 15;

      public Server() {
         super();
      }

      public static void tick(ServerLevel var0, BlockPos var1, BlockState var2, VaultConfig var3, VaultServerData var4, VaultSharedData var5) {
         VaultState var6 = var2.getValue(VaultBlock.STATE);
         if (shouldCycleDisplayItem(var0.getGameTime(), var6)) {
            cycleDisplayItemFromLootTable(var0, var6, var3, var5, var1);
         }

         BlockState var7 = var2;
         if (var0.getGameTime() >= var4.stateUpdatingResumesAt()) {
            var7 = var2.setValue(VaultBlock.STATE, var6.tickAndGetNext(var0, var1, var3, var4, var5));
            if (!var2.equals(var7)) {
               setVaultState(var0, var1, var2, var7, var3, var5);
            }
         }

         if (var4.isDirty || var5.isDirty) {
            VaultBlockEntity.setChanged(var0, var1, var2);
            if (var5.isDirty) {
               var0.sendBlockUpdated(var1, var2, var7, 2);
            }

            var4.isDirty = false;
            var5.isDirty = false;
         }
      }

      public static void tryInsertKey(
         ServerLevel var0, BlockPos var1, BlockState var2, VaultConfig var3, VaultServerData var4, VaultSharedData var5, Player var6, ItemStack var7
      ) {
         VaultState var8 = var2.getValue(VaultBlock.STATE);
         if (canEjectReward(var3, var8)) {
            if (!isValidToInsert(var3, var7)) {
               playInsertFailSound(var0, var4, var1);
            } else if (var4.hasRewardedPlayer(var6)) {
               playInsertFailSound(var0, var4, var1);
            } else {
               List var9 = resolveItemsToEject(var0, var3, var1, var6);
               if (!var9.isEmpty()) {
                  var6.awardStat(Stats.ITEM_USED.get(var7.getItem()));
                  if (!var6.isCreative()) {
                     var7.shrink(var3.keyItem().getCount());
                  }

                  unlock(var0, var2, var1, var3, var4, var5, var9);
                  var4.addToRewardedPlayers(var6);
                  var5.updateConnectedPlayersWithinRange(var0, var1, var4, var3, var3.deactivationRange());
               }
            }
         }
      }

      static void setVaultState(ServerLevel var0, BlockPos var1, BlockState var2, BlockState var3, VaultConfig var4, VaultSharedData var5) {
         VaultState var6 = var2.getValue(VaultBlock.STATE);
         VaultState var7 = var3.getValue(VaultBlock.STATE);
         var0.setBlock(var1, var3, 3);
         var6.onTransition(var0, var1, var7, var4, var5);
      }

      static void cycleDisplayItemFromLootTable(ServerLevel var0, VaultState var1, VaultConfig var2, VaultSharedData var3, BlockPos var4) {
         if (!canEjectReward(var2, var1)) {
            var3.setDisplayItem(ItemStack.EMPTY);
         } else {
            ItemStack var5 = getRandomDisplayItemFromLootTable(var0, var4, var2.overrideLootTableToDisplay().orElse(var2.lootTable()));
            var3.setDisplayItem(var5);
         }
      }

      private static ItemStack getRandomDisplayItemFromLootTable(ServerLevel var0, BlockPos var1, ResourceKey<LootTable> var2) {
         LootTable var3 = var0.getServer().reloadableRegistries().getLootTable(var2);
         LootParams var4 = new LootParams.Builder(var0).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(var1)).create(LootContextParamSets.VAULT);
         ObjectArrayList var5 = var3.getRandomItems(var4);
         return var5.isEmpty() ? ItemStack.EMPTY : Util.getRandom(var5, var0.getRandom());
      }

      private static void unlock(
         ServerLevel var0, BlockState var1, BlockPos var2, VaultConfig var3, VaultServerData var4, VaultSharedData var5, List<ItemStack> var6
      ) {
         var4.setItemsToEject(var6);
         var5.setDisplayItem(var4.getNextItemToEject());
         var4.pauseStateUpdatingUntil(var0.getGameTime() + 14L);
         setVaultState(var0, var2, var1, var1.setValue(VaultBlock.STATE, VaultState.UNLOCKING), var3, var5);
      }

      private static List<ItemStack> resolveItemsToEject(ServerLevel var0, VaultConfig var1, BlockPos var2, Player var3) {
         LootTable var4 = var0.getServer().reloadableRegistries().getLootTable(var1.lootTable());
         LootParams var5 = new LootParams.Builder(var0)
            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(var2))
            .withLuck(var3.getLuck())
            .withParameter(LootContextParams.THIS_ENTITY, var3)
            .create(LootContextParamSets.VAULT);
         return var4.getRandomItems(var5);
      }

      private static boolean canEjectReward(VaultConfig var0, VaultState var1) {
         return var0.lootTable() != BuiltInLootTables.EMPTY && !var0.keyItem().isEmpty() && var1 != VaultState.INACTIVE;
      }

      private static boolean isValidToInsert(VaultConfig var0, ItemStack var1) {
         return ItemStack.isSameItemSameComponents(var1, var0.keyItem()) && var1.getCount() >= var0.keyItem().getCount();
      }

      private static boolean shouldCycleDisplayItem(long var0, VaultState var2) {
         return var0 % 20L == 0L && var2 == VaultState.ACTIVE;
      }

      private static void playInsertFailSound(ServerLevel var0, VaultServerData var1, BlockPos var2) {
         if (var0.getGameTime() >= var1.getLastInsertFailTimestamp() + 15L) {
            var0.playSound(null, var2, SoundEvents.VAULT_INSERT_ITEM_FAIL, SoundSource.BLOCKS);
            var1.setLastInsertFailTimestamp(var0.getGameTime());
         }
      }
   }
}
