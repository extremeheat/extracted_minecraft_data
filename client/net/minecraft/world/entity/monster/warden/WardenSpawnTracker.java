package net.minecraft.world.entity.monster.warden;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class WardenSpawnTracker {
   public static final Codec<WardenSpawnTracker> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ExtraCodecs.NON_NEGATIVE_INT.fieldOf("ticks_since_last_warning").orElse(0).forGetter(var0x -> var0x.ticksSinceLastWarning),
               ExtraCodecs.NON_NEGATIVE_INT.fieldOf("warning_level").orElse(0).forGetter(var0x -> var0x.warningLevel),
               ExtraCodecs.NON_NEGATIVE_INT.fieldOf("cooldown_ticks").orElse(0).forGetter(var0x -> var0x.cooldownTicks)
            )
            .apply(var0, WardenSpawnTracker::new)
   );
   public static final int MAX_WARNING_LEVEL = 4;
   private static final double PLAYER_SEARCH_RADIUS = 16.0;
   private static final int WARNING_CHECK_DIAMETER = 48;
   private static final int DECREASE_WARNING_LEVEL_EVERY_INTERVAL = 12000;
   private static final int WARNING_LEVEL_INCREASE_COOLDOWN = 200;
   private int ticksSinceLastWarning;
   private int warningLevel;
   private int cooldownTicks;

   public WardenSpawnTracker(int var1, int var2, int var3) {
      super();
      this.ticksSinceLastWarning = var1;
      this.warningLevel = var2;
      this.cooldownTicks = var3;
   }

   public void tick() {
      if (this.ticksSinceLastWarning >= 12000) {
         this.decreaseWarningLevel();
         this.ticksSinceLastWarning = 0;
      } else {
         ++this.ticksSinceLastWarning;
      }

      if (this.cooldownTicks > 0) {
         --this.cooldownTicks;
      }
   }

   public void reset() {
      this.ticksSinceLastWarning = 0;
      this.warningLevel = 0;
      this.cooldownTicks = 0;
   }

   public static OptionalInt tryWarn(ServerLevel var0, BlockPos var1, ServerPlayer var2) {
      if (hasNearbyWarden(var0, var1)) {
         return OptionalInt.empty();
      } else {
         List var3 = getNearbyPlayers(var0, var1);
         if (!var3.contains(var2)) {
            var3.add(var2);
         }

         if (var3.stream().anyMatch(var0x -> var0x.getWardenSpawnTracker().map(WardenSpawnTracker::onCooldown).orElse(false))) {
            return OptionalInt.empty();
         } else {
            Optional var4 = var3.stream()
               .flatMap(var0x -> var0x.getWardenSpawnTracker().stream())
               .max(Comparator.comparingInt(WardenSpawnTracker::getWarningLevel));
            if (var4.isPresent()) {
               WardenSpawnTracker var5 = (WardenSpawnTracker)var4.get();
               var5.increaseWarningLevel();
               var3.forEach(var1x -> var1x.getWardenSpawnTracker().ifPresent(var1xx -> var1xx.copyData(var5)));
               return OptionalInt.of(var5.warningLevel);
            } else {
               return OptionalInt.empty();
            }
         }
      }
   }

   private boolean onCooldown() {
      return this.cooldownTicks > 0;
   }

   private static boolean hasNearbyWarden(ServerLevel var0, BlockPos var1) {
      AABB var2 = AABB.ofSize(Vec3.atCenterOf(var1), 48.0, 48.0, 48.0);
      return !var0.getEntitiesOfClass(Warden.class, var2).isEmpty();
   }

   private static List<ServerPlayer> getNearbyPlayers(ServerLevel var0, BlockPos var1) {
      Vec3 var2 = Vec3.atCenterOf(var1);
      Predicate var3 = var1x -> var1x.position().closerThan(var2, 16.0);
      return var0.getPlayers(var3.and(LivingEntity::isAlive).and(EntitySelector.NO_SPECTATORS));
   }

   private void increaseWarningLevel() {
      if (!this.onCooldown()) {
         this.ticksSinceLastWarning = 0;
         this.cooldownTicks = 200;
         this.setWarningLevel(this.getWarningLevel() + 1);
      }
   }

   private void decreaseWarningLevel() {
      this.setWarningLevel(this.getWarningLevel() - 1);
   }

   public void setWarningLevel(int var1) {
      this.warningLevel = Mth.clamp(var1, 0, 4);
   }

   public int getWarningLevel() {
      return this.warningLevel;
   }

   private void copyData(WardenSpawnTracker var1) {
      this.warningLevel = var1.warningLevel;
      this.cooldownTicks = var1.cooldownTicks;
      this.ticksSinceLastWarning = var1.ticksSinceLastWarning;
   }
}
