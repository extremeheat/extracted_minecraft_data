package net.minecraft.world.entity.livingblock.movement;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.Target;
import net.minecraft.world.phys.Vec3;

public class FloatingMovement implements MovementStrategy<Data> {
   private static final double SPEED = 0.06;
   private static final double ARBITRARY_STUCK_SPEED_THRESHOLD = 0.001;

   public FloatingMovement() {
      super();
   }

   public Data initData() {
      return new Data();
   }

   public boolean moveTowardsTarget(final LivingBlock entity, final Target target, final Vec3 targetPos) {
      Vec3 pos = entity.position();
      Vec3 delta = targetPos.add(0.0, 0.5, 0.0).subtract(pos);
      Data data = (Data)this.getData(entity);
      boolean stuck = data.wantedHorizontalSpeed > 0.001 && entity.getDeltaMovement().horizontalDistance() < 0.001;
      if (delta.horizontalDistanceSqr() > Mth.square(target.distance())) {
         Vec3 dir = delta.normalize();
         double speed = Math.min(delta.length(), 0.06);
         if (!entity.isAttacking()) {
            entity.setDeltaMovement(entity.getDeltaMovement().scale(0.9));
         }

         entity.addDeltaMovement(dir.scale(speed).add(0.0, entity.getGravity(), 0.0));
         data.wantedHorizontalSpeed = entity.getDeltaMovement().horizontalDistance();
         if (stuck) {
            entity.addDeltaMovement(new Vec3(0.0, 0.06, 0.0));
         }

         return true;
      } else {
         return false;
      }
   }

   public void resetMovement(final LivingBlock entity) {
      MovementStrategy.resetVelocity(entity, false);
   }

   public static class Data implements MovementData {
      public static final StreamCodec<ByteBuf, Data> STREAM_CODEC;
      public double wantedHorizontalSpeed;

      public Data(final double wantedHorizontalSpeed) {
         super();
         this.wantedHorizontalSpeed = wantedHorizontalSpeed;
      }

      public Data() {
         this(0.0);
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.DOUBLE, (d) -> d.wantedHorizontalSpeed, Data::new);
      }
   }
}
