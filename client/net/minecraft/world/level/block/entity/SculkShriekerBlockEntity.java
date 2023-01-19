package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.warden.WardenSpawnTracker;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class SculkShriekerBlockEntity extends BlockEntity implements VibrationListener.VibrationListenerConfig {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int LISTENER_RADIUS = 8;
   private static final int WARNING_SOUND_RADIUS = 10;
   private static final int WARDEN_SPAWN_ATTEMPTS = 20;
   private static final int WARDEN_SPAWN_RANGE_XZ = 5;
   private static final int WARDEN_SPAWN_RANGE_Y = 6;
   private static final int DARKNESS_RADIUS = 40;
   private static final Int2ObjectMap<SoundEvent> SOUND_BY_LEVEL = Util.make(new Int2ObjectOpenHashMap(), var0 -> {
      var0.put(1, SoundEvents.WARDEN_NEARBY_CLOSE);
      var0.put(2, SoundEvents.WARDEN_NEARBY_CLOSER);
      var0.put(3, SoundEvents.WARDEN_NEARBY_CLOSEST);
      var0.put(4, SoundEvents.WARDEN_LISTENING_ANGRY);
   });
   private static final int SHRIEKING_TICKS = 90;
   private int warningLevel;
   private VibrationListener listener = new VibrationListener(new BlockPositionSource(this.worldPosition), 8, this, null, 0.0F, 0);

   public SculkShriekerBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.SCULK_SHRIEKER, var1, var2);
   }

   public VibrationListener getListener() {
      return this.listener;
   }

   @Override
   public void load(CompoundTag var1) {
      super.load(var1);
      if (var1.contains("warning_level", 99)) {
         this.warningLevel = var1.getInt("warning_level");
      }

      if (var1.contains("listener", 10)) {
         VibrationListener.codec(this)
            .parse(new Dynamic(NbtOps.INSTANCE, var1.getCompound("listener")))
            .resultOrPartial(LOGGER::error)
            .ifPresent(var1x -> this.listener = var1x);
      }
   }

   @Override
   protected void saveAdditional(CompoundTag var1) {
      super.saveAdditional(var1);
      var1.putInt("warning_level", this.warningLevel);
      VibrationListener.codec(this).encodeStart(NbtOps.INSTANCE, this.listener).resultOrPartial(LOGGER::error).ifPresent(var1x -> var1.put("listener", var1x));
   }

   @Override
   public TagKey<GameEvent> getListenableEvents() {
      return GameEventTags.SHRIEKER_CAN_LISTEN;
   }

   @Override
   public boolean shouldListen(ServerLevel var1, GameEventListener var2, BlockPos var3, GameEvent var4, GameEvent.Context var5) {
      return !this.isRemoved() && !this.getBlockState().getValue(SculkShriekerBlock.SHRIEKING) && tryGetPlayer(var5.sourceEntity()) != null;
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Nullable
   public static ServerPlayer tryGetPlayer(@Nullable Entity var0) {
      if (var0 instanceof ServerPlayer) {
         return (ServerPlayer)var0;
      } else {
         if (var0 != null) {
            Entity var2 = var0.getControllingPassenger();
            if (var2 instanceof ServerPlayer) {
               return (ServerPlayer)var2;
            }
         }

         if (var0 instanceof Projectile var1) {
            Entity var3 = var1.getOwner();
            if (var3 instanceof ServerPlayer) {
               return (ServerPlayer)var3;
            }
         }

         return null;
      }
   }

   @Override
   public void onSignalReceive(
      ServerLevel var1, GameEventListener var2, BlockPos var3, GameEvent var4, @Nullable Entity var5, @Nullable Entity var6, float var7
   ) {
      this.tryShriek(var1, tryGetPlayer(var6 != null ? var6 : var5));
   }

   public void tryShriek(ServerLevel var1, @Nullable ServerPlayer var2) {
      if (var2 != null) {
         BlockState var3 = this.getBlockState();
         if (!var3.getValue(SculkShriekerBlock.SHRIEKING)) {
            this.warningLevel = 0;
            if (!this.canRespond(var1) || this.tryToWarn(var1, var2)) {
               this.shriek(var1, var2);
            }
         }
      }
   }

   private boolean tryToWarn(ServerLevel var1, ServerPlayer var2) {
      OptionalInt var3 = WardenSpawnTracker.tryWarn(var1, this.getBlockPos(), var2);
      var3.ifPresent(var1x -> this.warningLevel = var1x);
      return var3.isPresent();
   }

   private void shriek(ServerLevel var1, @Nullable Entity var2) {
      BlockPos var3 = this.getBlockPos();
      BlockState var4 = this.getBlockState();
      var1.setBlock(var3, var4.setValue(SculkShriekerBlock.SHRIEKING, Boolean.valueOf(true)), 2);
      var1.scheduleTick(var3, var4.getBlock(), 90);
      var1.levelEvent(3007, var3, 0);
      var1.gameEvent(GameEvent.SHRIEK, var3, GameEvent.Context.of(var2));
   }

   private boolean canRespond(ServerLevel var1) {
      return this.getBlockState().getValue(SculkShriekerBlock.CAN_SUMMON)
         && var1.getDifficulty() != Difficulty.PEACEFUL
         && var1.getGameRules().getBoolean(GameRules.RULE_DO_WARDEN_SPAWNING);
   }

   public void tryRespond(ServerLevel var1) {
      if (this.canRespond(var1) && this.warningLevel > 0) {
         if (!this.trySummonWarden(var1)) {
            this.playWardenReplySound();
         }

         Warden.applyDarknessAround(var1, Vec3.atCenterOf(this.getBlockPos()), null, 40);
      }
   }

   private void playWardenReplySound() {
      SoundEvent var1 = (SoundEvent)SOUND_BY_LEVEL.get(this.warningLevel);
      if (var1 != null) {
         BlockPos var2 = this.getBlockPos();
         int var3 = var2.getX() + Mth.randomBetweenInclusive(this.level.random, -10, 10);
         int var4 = var2.getY() + Mth.randomBetweenInclusive(this.level.random, -10, 10);
         int var5 = var2.getZ() + Mth.randomBetweenInclusive(this.level.random, -10, 10);
         this.level.playSound(null, (double)var3, (double)var4, (double)var5, var1, SoundSource.HOSTILE, 5.0F, 1.0F);
      }
   }

   private boolean trySummonWarden(ServerLevel var1) {
      return this.warningLevel < 4
         ? false
         : SpawnUtil.trySpawnMob(EntityType.WARDEN, MobSpawnType.TRIGGERED, var1, this.getBlockPos(), 20, 5, 6, SpawnUtil.Strategy.ON_TOP_OF_COLLIDER)
            .isPresent();
   }

   @Override
   public void onSignalSchedule() {
      this.setChanged();
   }
}
