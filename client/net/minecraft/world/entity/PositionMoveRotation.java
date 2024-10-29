package net.minecraft.world.entity;

import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

public record PositionMoveRotation(Vec3 position, Vec3 deltaMovement, float yRot, float xRot) {
   public static final StreamCodec<FriendlyByteBuf, PositionMoveRotation> STREAM_CODEC;

   public PositionMoveRotation(Vec3 var1, Vec3 var2, float var3, float var4) {
      super();
      this.position = var1;
      this.deltaMovement = var2;
      this.yRot = var3;
      this.xRot = var4;
   }

   public static PositionMoveRotation of(Entity var0) {
      return new PositionMoveRotation(var0.position(), var0.getKnownMovement(), var0.getYRot(), var0.getXRot());
   }

   public static PositionMoveRotation ofEntityUsingLerpTarget(Entity var0) {
      return new PositionMoveRotation(new Vec3(var0.lerpTargetX(), var0.lerpTargetY(), var0.lerpTargetZ()), var0.getKnownMovement(), var0.getYRot(), var0.getXRot());
   }

   public static PositionMoveRotation of(TeleportTransition var0) {
      return new PositionMoveRotation(var0.position(), var0.deltaMovement(), var0.yRot(), var0.xRot());
   }

   public static PositionMoveRotation calculateAbsolute(PositionMoveRotation var0, PositionMoveRotation var1, Set<Relative> var2) {
      double var3 = var2.contains(Relative.X) ? var0.position.x : 0.0;
      double var5 = var2.contains(Relative.Y) ? var0.position.y : 0.0;
      double var7 = var2.contains(Relative.Z) ? var0.position.z : 0.0;
      float var9 = var2.contains(Relative.Y_ROT) ? var0.yRot : 0.0F;
      float var10 = var2.contains(Relative.X_ROT) ? var0.xRot : 0.0F;
      Vec3 var11 = new Vec3(var3 + var1.position.x, var5 + var1.position.y, var7 + var1.position.z);
      float var12 = var9 + var1.yRot;
      float var13 = var10 + var1.xRot;
      Vec3 var14 = var0.deltaMovement;
      if (var2.contains(Relative.ROTATE_DELTA)) {
         float var15 = var0.yRot - var12;
         float var16 = var0.xRot - var13;
         var14 = var14.xRot((float)Math.toRadians((double)var16));
         var14 = var14.yRot((float)Math.toRadians((double)var15));
      }

      Vec3 var17 = new Vec3(calculateDelta(var14.x, var1.deltaMovement.x, var2, Relative.DELTA_X), calculateDelta(var14.y, var1.deltaMovement.y, var2, Relative.DELTA_Y), calculateDelta(var14.z, var1.deltaMovement.z, var2, Relative.DELTA_Z));
      return new PositionMoveRotation(var11, var17, var12, var13);
   }

   private static double calculateDelta(double var0, double var2, Set<Relative> var4, Relative var5) {
      return var4.contains(var5) ? var0 + var2 : var2;
   }

   public Vec3 position() {
      return this.position;
   }

   public Vec3 deltaMovement() {
      return this.deltaMovement;
   }

   public float yRot() {
      return this.yRot;
   }

   public float xRot() {
      return this.xRot;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(Vec3.STREAM_CODEC, PositionMoveRotation::position, Vec3.STREAM_CODEC, PositionMoveRotation::deltaMovement, ByteBufCodecs.FLOAT, PositionMoveRotation::yRot, ByteBufCodecs.FLOAT, PositionMoveRotation::xRot, PositionMoveRotation::new);
   }
}
