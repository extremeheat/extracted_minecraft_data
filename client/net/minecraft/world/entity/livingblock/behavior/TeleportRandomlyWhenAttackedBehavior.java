package net.minecraft.world.entity.livingblock.behavior;

import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class TeleportRandomlyWhenAttackedBehavior implements LivingBlockBehavior {
   public static final double BACK_AWAY_DISTANCE = 32.0;
   private static final int BACK_AWAY_COOLDOWN_TICKS = 15;
   private boolean triggered = false;
   public static final LivingBlockBehaviorType BEHAVIOR = LivingBlockBehaviorType.behaviorType((Function)((a) -> new TeleportRandomlyWhenAttackedBehavior()));

   public TeleportRandomlyWhenAttackedBehavior() {
      super();
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return entity.getAttackedBy() != null;
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      if (!this.triggered && entity.getAttackedBy() != null) {
         Vec3 position = entity.position();
         RandomSource random = level.getRandom();
         double x = position.x() + (random.nextDouble() - 0.5) * 32.0;
         double y = position.y() + (double)random.nextInt(32);
         double z = position.z() + (random.nextDouble() - 0.5) * 32.0;
         entity.randomTeleport(x, y, z, true);
         entity.level().playSound((Entity)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.SHULKER_TELEPORT, SoundSource.BLOCKS);
         entity.lastAttackedTick = tickCount;
         this.triggered = true;
      }

      return entity.lastAttackedTick + 15 >= tickCount;
   }

   public void onStop(final LivingBlock entity) {
      if (entity.getAttackedBy() != null) {
         entity.setAttackedBy((Player)null);
         this.triggered = false;
      }

   }

   public void onStart(final LivingBlock entity) {
      this.triggered = false;
   }

   private static Vec3 getTargetPosition(final Vec3 entityPos, final Vec3 playerPos) {
      double dx = entityPos.x() - playerPos.x();
      double dz = entityPos.z() - playerPos.z();
      double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
      if (horizontalDistance < 0.01) {
         return new Vec3(entityPos.x() + 32.0, entityPos.y(), entityPos.z());
      } else {
         double scale = 32.0 / horizontalDistance;
         return new Vec3(entityPos.x() + dx * scale, entityPos.y(), entityPos.z() + dz * scale);
      }
   }
}
