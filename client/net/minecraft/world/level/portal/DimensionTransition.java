package net.minecraft.world.level.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public record DimensionTransition(ServerLevel newLevel, Vec3 pos, Vec3 speed, float yRot, float xRot, boolean missingRespawnBlock, PostDimensionTransition postDimensionTransition) {
   public static final PostDimensionTransition DO_NOTHING = (var0) -> {
   };
   public static final PostDimensionTransition PLAY_PORTAL_SOUND = DimensionTransition::playPortalSound;
   public static final PostDimensionTransition PLACE_PORTAL_TICKET = DimensionTransition::placePortalTicket;

   public DimensionTransition(ServerLevel var1, Vec3 var2, Vec3 var3, float var4, float var5, PostDimensionTransition var6) {
      this(var1, var2, var3, var4, var5, false, var6);
   }

   public DimensionTransition(ServerLevel var1, Entity var2, PostDimensionTransition var3) {
      this(var1, findAdjustedSharedSpawnPos(var1, var2), Vec3.ZERO, 0.0F, 0.0F, false, var3);
   }

   public DimensionTransition(ServerLevel var1, Vec3 var2, Vec3 var3, float var4, float var5, boolean var6, PostDimensionTransition var7) {
      super();
      this.newLevel = var1;
      this.pos = var2;
      this.speed = var3;
      this.yRot = var4;
      this.xRot = var5;
      this.missingRespawnBlock = var6;
      this.postDimensionTransition = var7;
   }

   private static void playPortalSound(Entity var0) {
      if (var0 instanceof ServerPlayer var1) {
         var1.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
      }

   }

   private static void placePortalTicket(Entity var0) {
      var0.placePortalTicket(BlockPos.containing(var0.position()));
   }

   public static DimensionTransition missingRespawnBlock(ServerLevel var0, Entity var1, PostDimensionTransition var2) {
      return new DimensionTransition(var0, findAdjustedSharedSpawnPos(var0, var1), Vec3.ZERO, 0.0F, 0.0F, true, var2);
   }

   private static Vec3 findAdjustedSharedSpawnPos(ServerLevel var0, Entity var1) {
      return var1.adjustSpawnLocation(var0, var0.getSharedSpawnPos()).getBottomCenter();
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

   public PostDimensionTransition postDimensionTransition() {
      return this.postDimensionTransition;
   }

   @FunctionalInterface
   public interface PostDimensionTransition {
      void onTransition(Entity var1);

      default PostDimensionTransition then(PostDimensionTransition var1) {
         return (var2) -> {
            this.onTransition(var2);
            var1.onTransition(var2);
         };
      }
   }
}
