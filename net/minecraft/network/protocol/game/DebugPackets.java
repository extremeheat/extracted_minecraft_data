package net.minecraft.network.protocol.game;

import io.netty.buffer.Unpooled;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.pathfinder.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugPackets {
   private static final Logger LOGGER = LogManager.getLogger();

   public static void sendGameTestAddMarker(ServerLevel var0, BlockPos var1, String var2, int var3, int var4) {
      FriendlyByteBuf var5 = new FriendlyByteBuf(Unpooled.buffer());
      var5.writeBlockPos(var1);
      var5.writeInt(var3);
      var5.writeUtf(var2);
      var5.writeInt(var4);
      sendPacketToAllPlayers(var0, var5, ClientboundCustomPayloadPacket.DEBUG_GAME_TEST_ADD_MARKER);
   }

   public static void sendGameTestClearPacket(ServerLevel var0) {
      FriendlyByteBuf var1 = new FriendlyByteBuf(Unpooled.buffer());
      sendPacketToAllPlayers(var0, var1, ClientboundCustomPayloadPacket.DEBUG_GAME_TEST_CLEAR);
   }

   public static void sendPoiPacketsForChunk(ServerLevel var0, ChunkPos var1) {
   }

   public static void sendPoiAddedPacket(ServerLevel var0, BlockPos var1) {
   }

   public static void sendPoiRemovedPacket(ServerLevel var0, BlockPos var1) {
   }

   public static void sendPoiTicketCountPacket(ServerLevel var0, BlockPos var1) {
   }

   public static void sendPathFindingPacket(Level var0, Mob var1, @Nullable Path var2, float var3) {
   }

   public static void sendNeighborsUpdatePacket(Level var0, BlockPos var1) {
   }

   public static void sendStructurePacket(LevelAccessor var0, StructureStart var1) {
   }

   public static void sendGoalSelector(Level var0, Mob var1, GoalSelector var2) {
   }

   public static void sendRaids(ServerLevel var0, Collection var1) {
   }

   public static void sendEntityBrain(LivingEntity var0) {
   }

   public static void sendBeeInfo(Bee var0) {
   }

   public static void sendHiveInfo(BeehiveBlockEntity var0) {
   }

   private static void sendPacketToAllPlayers(ServerLevel var0, FriendlyByteBuf var1, ResourceLocation var2) {
      ClientboundCustomPayloadPacket var3 = new ClientboundCustomPayloadPacket(var2, var1);
      Iterator var4 = var0.getLevel().players().iterator();

      while(var4.hasNext()) {
         Player var5 = (Player)var4.next();
         ((ServerPlayer)var5).connection.send(var3);
      }

   }
}
