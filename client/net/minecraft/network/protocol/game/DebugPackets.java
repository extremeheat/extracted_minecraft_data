package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.GameTestAddMarkerDebugPayload;
import net.minecraft.network.protocol.common.custom.GameTestClearMarkersDebugPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Nameable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class DebugPackets {
   private static final Logger LOGGER = LogUtils.getLogger();

   public DebugPackets() {
      super();
   }

   public static void sendGameTestAddMarker(ServerLevel var0, BlockPos var1, String var2, int var3, int var4) {
      sendPacketToAllPlayers(var0, new GameTestAddMarkerDebugPayload(var1, var3, var2, var4));
   }

   public static void sendGameTestClearPacket(ServerLevel var0) {
      sendPacketToAllPlayers(var0, new GameTestClearMarkersDebugPayload());
   }

   public static void sendPoiPacketsForChunk(ServerLevel var0, ChunkPos var1) {
   }

   public static void sendPoiAddedPacket(ServerLevel var0, BlockPos var1) {
      sendVillageSectionsPacket(var0, var1);
   }

   public static void sendPoiRemovedPacket(ServerLevel var0, BlockPos var1) {
      sendVillageSectionsPacket(var0, var1);
   }

   public static void sendPoiTicketCountPacket(ServerLevel var0, BlockPos var1) {
      sendVillageSectionsPacket(var0, var1);
   }

   private static void sendVillageSectionsPacket(ServerLevel var0, BlockPos var1) {
   }

   public static void sendPathFindingPacket(Level var0, Mob var1, @Nullable Path var2, float var3) {
   }

   public static void sendNeighborsUpdatePacket(Level var0, BlockPos var1) {
   }

   public static void sendStructurePacket(WorldGenLevel var0, StructureStart var1) {
   }

   public static void sendGoalSelector(Level var0, Mob var1, GoalSelector var2) {
   }

   public static void sendRaids(ServerLevel var0, Collection<Raid> var1) {
   }

   public static void sendEntityBrain(LivingEntity var0) {
   }

   public static void sendBeeInfo(Bee var0) {
   }

   public static void sendGameEventInfo(Level var0, GameEvent var1, Vec3 var2) {
   }

   public static void sendGameEventListenerInfo(Level var0, GameEventListener var1) {
   }

   public static void sendHiveInfo(Level var0, BlockPos var1, BlockState var2, BeehiveBlockEntity var3) {
   }

   private static List<String> getMemoryDescriptions(LivingEntity var0, long var1) {
      Map var3 = var0.getBrain().getMemories();
      ArrayList var4 = Lists.newArrayList();

      for(Entry var6 : var3.entrySet()) {
         MemoryModuleType var7 = (MemoryModuleType)var6.getKey();
         Optional var8 = (Optional)var6.getValue();
         String var9;
         if (var8.isPresent()) {
            ExpirableValue var10 = (ExpirableValue)var8.get();
            Object var11 = var10.getValue();
            if (var7 == MemoryModuleType.HEARD_BELL_TIME) {
               long var12 = var1 - (Long)var11;
               var9 = var12 + " ticks ago";
            } else if (var10.canExpire()) {
               var9 = getShortDescription((ServerLevel)var0.level(), var11) + " (ttl: " + var10.getTimeToLive() + ")";
            } else {
               var9 = getShortDescription((ServerLevel)var0.level(), var11);
            }
         } else {
            var9 = "-";
         }

         var4.add(BuiltInRegistries.MEMORY_MODULE_TYPE.getKey(var7).getPath() + ": " + var9);
      }

      var4.sort(String::compareTo);
      return var4;
   }

   private static String getShortDescription(ServerLevel var0, @Nullable Object var1) {
      if (var1 == null) {
         return "-";
      } else if (var1 instanceof UUID) {
         return getShortDescription(var0, var0.getEntity((UUID)var1));
      } else if (var1 instanceof LivingEntity) {
         Entity var6 = (Entity)var1;
         return DebugEntityNameGenerator.getEntityName(var6);
      } else if (var1 instanceof Nameable) {
         return ((Nameable)var1).getName().getString();
      } else if (var1 instanceof WalkTarget) {
         return getShortDescription(var0, ((WalkTarget)var1).getTarget());
      } else if (var1 instanceof EntityTracker) {
         return getShortDescription(var0, ((EntityTracker)var1).getEntity());
      } else if (var1 instanceof GlobalPos) {
         return getShortDescription(var0, ((GlobalPos)var1).pos());
      } else if (var1 instanceof BlockPosTracker) {
         return getShortDescription(var0, ((BlockPosTracker)var1).currentBlockPosition());
      } else if (var1 instanceof DamageSource) {
         Entity var5 = ((DamageSource)var1).getEntity();
         return var5 == null ? var1.toString() : getShortDescription(var0, var5);
      } else if (!(var1 instanceof Collection)) {
         return var1.toString();
      } else {
         ArrayList var2 = Lists.newArrayList();

         for(Object var4 : (Iterable)var1) {
            var2.add(getShortDescription(var0, var4));
         }

         return var2.toString();
      }
   }

   private static void sendPacketToAllPlayers(ServerLevel var0, CustomPacketPayload var1) {
      ClientboundCustomPayloadPacket var2 = new ClientboundCustomPayloadPacket(var1);

      for(ServerPlayer var4 : var0.players()) {
         var4.connection.send(var2);
      }
   }
}
