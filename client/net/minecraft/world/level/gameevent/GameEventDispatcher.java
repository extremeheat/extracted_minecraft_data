package net.minecraft.world.level.gameevent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

public class GameEventDispatcher {
   private final ServerLevel level;

   public GameEventDispatcher(ServerLevel var1) {
      super();
      this.level = var1;
   }

   public void post(GameEvent var1, Vec3 var2, GameEvent.Context var3) {
      int var4 = var1.getNotificationRadius();
      BlockPos var5 = new BlockPos(var2);
      int var6 = SectionPos.blockToSectionCoord(var5.getX() - var4);
      int var7 = SectionPos.blockToSectionCoord(var5.getY() - var4);
      int var8 = SectionPos.blockToSectionCoord(var5.getZ() - var4);
      int var9 = SectionPos.blockToSectionCoord(var5.getX() + var4);
      int var10 = SectionPos.blockToSectionCoord(var5.getY() + var4);
      int var11 = SectionPos.blockToSectionCoord(var5.getZ() + var4);
      ArrayList var12 = new ArrayList();
      GameEventListenerRegistry.ListenerVisitor var13 = (var5x, var6x) -> {
         if (var5x.getDeliveryMode() == GameEventListener.DeliveryMode.BY_DISTANCE) {
            var12.add(new GameEvent.ListenerInfo(var1, var2, var3, var5x, var6x));
         } else {
            var5x.handleGameEvent(this.level, var1, var3, var2);
         }
      };
      boolean var14 = false;

      for(int var15 = var6; var15 <= var9; ++var15) {
         for(int var16 = var8; var16 <= var11; ++var16) {
            LevelChunk var17 = this.level.getChunkSource().getChunkNow(var15, var16);
            if (var17 != null) {
               for(int var18 = var7; var18 <= var10; ++var18) {
                  var14 |= var17.getListenerRegistry(var18).visitInRangeListeners(var1, var2, var3, var13);
               }
            }
         }
      }

      if (!var12.isEmpty()) {
         this.handleGameEventMessagesInQueue(var12);
      }

      if (var14) {
         DebugPackets.sendGameEventInfo(this.level, var1, var2);
      }
   }

   private void handleGameEventMessagesInQueue(List<GameEvent.ListenerInfo> var1) {
      Collections.sort(var1);

      for(GameEvent.ListenerInfo var3 : var1) {
         GameEventListener var4 = var3.recipient();
         var4.handleGameEvent(this.level, var3.gameEvent(), var3.context(), var3.source());
      }
   }
}
