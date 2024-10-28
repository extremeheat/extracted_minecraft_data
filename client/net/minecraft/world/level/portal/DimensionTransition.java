package net.minecraft.world.level.portal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public record DimensionTransition(ServerLevel newLevel, Vec3 pos, Vec3 speed, float yRot, float xRot, boolean missingRespawnBlock) {
   public DimensionTransition(ServerLevel var1, Vec3 var2, Vec3 var3, float var4, float var5) {
      this(var1, var2, var3, var4, var5, false);
   }

   public DimensionTransition(ServerLevel var1) {
      this(var1, var1.getSharedSpawnPos().getCenter(), Vec3.ZERO, 0.0F, 0.0F, false);
   }

   public DimensionTransition(ServerLevel var1, Vec3 var2, Vec3 var3, float var4, float var5, boolean var6) {
      super();
      this.newLevel = var1;
      this.pos = var2;
      this.speed = var3;
      this.yRot = var4;
      this.xRot = var5;
      this.missingRespawnBlock = var6;
   }

   public static DimensionTransition missingRespawnBlock(ServerLevel var0) {
      return new DimensionTransition(var0, var0.getSharedSpawnPos().getCenter(), Vec3.ZERO, 0.0F, 0.0F, true);
   }

   public ServerLevel newLevel() {
      return this.newLevel;
   }

   public Vec3 pos() {
      return this.pos;
   }

   public Vec3 speed() {
      return this.speed;
   }

   public float yRot() {
      return this.yRot;
   }

   public float xRot() {
      return this.xRot;
   }

   public boolean missingRespawnBlock() {
      return this.missingRespawnBlock;
   }
}
