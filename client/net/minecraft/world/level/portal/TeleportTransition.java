package net.minecraft.world.level.portal;

import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.phys.Vec3;

public record TeleportTransition(ServerLevel newLevel, Vec3 position, Vec3 deltaMovement, float yRot, float xRot, boolean missingRespawnBlock, boolean asPassenger, Set<Relative> relatives, PostTeleportTransition postTeleportTransition) {
   public static final PostTeleportTransition DO_NOTHING = (var0) -> {
   };
   public static final PostTeleportTransition PLAY_PORTAL_SOUND = TeleportTransition::playPortalSound;
   public static final PostTeleportTransition PLACE_PORTAL_TICKET = TeleportTransition::placePortalTicket;

   public TeleportTransition(ServerLevel var1, Vec3 var2, Vec3 var3, float var4, float var5, PostTeleportTransition var6) {
      this(var1, var2, var3, var4, var5, Set.of(), var6);
   }

   public TeleportTransition(ServerLevel var1, Vec3 var2, Vec3 var3, float var4, float var5, Set<Relative> var6, PostTeleportTransition var7) {
      this(var1, var2, var3, var4, var5, false, false, var6, var7);
   }

   public TeleportTransition(ServerLevel var1, Entity var2, PostTeleportTransition var3) {
      this(var1, findAdjustedSharedSpawnPos(var1, var2), Vec3.ZERO, 0.0F, 0.0F, false, false, Set.of(), var3);
   }

   public TeleportTransition(ServerLevel var1, Vec3 var2, Vec3 var3, float var4, float var5, boolean var6, boolean var7, Set<Relative> var8, PostTeleportTransition var9) {
      super();
      this.newLevel = var1;
      this.position = var2;
      this.deltaMovement = var3;
      this.yRot = var4;
      this.xRot = var5;
      this.missingRespawnBlock = var6;
      this.asPassenger = var7;
      this.relatives = var8;
      this.postTeleportTransition = var9;
   }

   private static void playPortalSound(Entity var0) {
      if (var0 instanceof ServerPlayer var1) {
         var1.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
      }

   }

   private static void placePortalTicket(Entity var0) {
      var0.placePortalTicket(BlockPos.containing(var0.position()));
   }

   public static TeleportTransition missingRespawnBlock(ServerLevel var0, Entity var1, PostTeleportTransition var2) {
      return new TeleportTransition(var0, findAdjustedSharedSpawnPos(var0, var1), Vec3.ZERO, 0.0F, 0.0F, true, false, Set.of(), var2);
   }

   private static Vec3 findAdjustedSharedSpawnPos(ServerLevel var0, Entity var1) {
      return var1.adjustSpawnLocation(var0, var0.getSharedSpawnPos()).getBottomCenter();
   }

   public TeleportTransition withRotation(float var1, float var2) {
      return new TeleportTransition(this.newLevel(), this.position(), this.deltaMovement(), var1, var2, this.missingRespawnBlock(), this.asPassenger(), this.relatives(), this.postTeleportTransition());
   }

   public TeleportTransition withPosition(Vec3 var1) {
      return new TeleportTransition(this.newLevel(), var1, this.deltaMovement(), this.yRot(), this.xRot(), this.missingRespawnBlock(), this.asPassenger(), this.relatives(), this.postTeleportTransition());
   }

   public TeleportTransition transitionAsPassenger() {
      return new TeleportTransition(this.newLevel(), this.position(), this.deltaMovement(), this.yRot(), this.xRot(), this.missingRespawnBlock(), true, this.relatives(), this.postTeleportTransition());
   }

   public ServerLevel newLevel() {
      return this.newLevel;
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

   public boolean missingRespawnBlock() {
      return this.missingRespawnBlock;
   }

   public boolean asPassenger() {
      return this.asPassenger;
   }

   public Set<Relative> relatives() {
      return this.relatives;
   }

   public PostTeleportTransition postTeleportTransition() {
      return this.postTeleportTransition;
   }

   @FunctionalInterface
   public interface PostTeleportTransition {
      void onTransition(Entity var1);

      default PostTeleportTransition then(PostTeleportTransition var1) {
         return (var2) -> {
            this.onTransition(var2);
            var1.onTransition(var2);
         };
      }
   }
}
