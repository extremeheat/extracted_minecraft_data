package net.minecraft.client.multiplayer.prediction;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BlockStatePredictionHandler implements AutoCloseable {
   private final Long2ObjectOpenHashMap<ServerVerifiedState> serverVerifiedStates = new Long2ObjectOpenHashMap();
   private int currentSequenceNr;
   private boolean isPredicting;

   public BlockStatePredictionHandler() {
      super();
   }

   public void retainKnownServerState(BlockPos var1, BlockState var2, LocalPlayer var3) {
      this.serverVerifiedStates.compute(var1.asLong(), (var3x, var4) -> var4 != null ? var4.setSequence(this.currentSequenceNr) : new ServerVerifiedState(this.currentSequenceNr, var2, var3.position()));
   }

   public boolean updateKnownServerState(BlockPos var1, BlockState var2) {
      ServerVerifiedState var3 = (ServerVerifiedState)this.serverVerifiedStates.get(var1.asLong());
      if (var3 == null) {
         return false;
      } else {
         var3.setBlockState(var2);
         return true;
      }
   }

   public void endPredictionsUpTo(int var1, ClientLevel var2) {
      ObjectIterator var3 = this.serverVerifiedStates.long2ObjectEntrySet().iterator();

      while(var3.hasNext()) {
         Long2ObjectMap.Entry var4 = (Long2ObjectMap.Entry)var3.next();
         ServerVerifiedState var5 = (ServerVerifiedState)var4.getValue();
         if (var5.sequence <= var1) {
            BlockPos var6 = BlockPos.of(var4.getLongKey());
            var3.remove();
            var2.syncBlockState(var6, var5.blockState, var5.playerPos);
         }
      }

   }

   public BlockStatePredictionHandler startPredicting() {
      ++this.currentSequenceNr;
      this.isPredicting = true;
      return this;
   }

   public void close() {
      this.isPredicting = false;
   }

   public int currentSequence() {
      return this.currentSequenceNr;
   }

   public boolean isPredicting() {
      return this.isPredicting;
   }

   static class ServerVerifiedState {
      final Vec3 playerPos;
      int sequence;
      BlockState blockState;

      ServerVerifiedState(int var1, BlockState var2, Vec3 var3) {
         super();
         this.sequence = var1;
         this.blockState = var2;
         this.playerPos = var3;
      }

      ServerVerifiedState setSequence(int var1) {
         this.sequence = var1;
         return this;
      }

      void setBlockState(BlockState var1) {
         this.blockState = var1;
      }
   }
}
