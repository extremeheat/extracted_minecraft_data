package net.minecraft.world.level;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobCategory;

public class LocalMobCapCalculator {
   private final Long2ObjectMap<List<ServerPlayer>> playersNearChunk = new Long2ObjectOpenHashMap();
   private final Map<ServerPlayer, LocalMobCapCalculator.MobCounts> playerMobCounts = Maps.newHashMap();
   private final ChunkMap chunkMap;

   public LocalMobCapCalculator(ChunkMap var1) {
      super();
      this.chunkMap = var1;
   }

   private List<ServerPlayer> getPlayersNear(ChunkPos var1) {
      return (List)this.playersNearChunk.computeIfAbsent(var1.toLong(), (var2) -> {
         return this.chunkMap.getPlayersCloseForSpawning(var1);
      });
   }

   public void addMob(ChunkPos var1, MobCategory var2) {
      Iterator var3 = this.getPlayersNear(var1).iterator();

      while(var3.hasNext()) {
         ServerPlayer var4 = (ServerPlayer)var3.next();
         ((LocalMobCapCalculator.MobCounts)this.playerMobCounts.computeIfAbsent(var4, (var0) -> {
            return new LocalMobCapCalculator.MobCounts();
         })).add(var2);
      }

   }

   public boolean canSpawn(MobCategory var1, ChunkPos var2) {
      Iterator var3 = this.getPlayersNear(var2).iterator();

      LocalMobCapCalculator.MobCounts var5;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         ServerPlayer var4 = (ServerPlayer)var3.next();
         var5 = (LocalMobCapCalculator.MobCounts)this.playerMobCounts.get(var4);
      } while(var5 != null && !var5.canSpawn(var1));

      return true;
   }

   static class MobCounts {
      private final Object2IntMap<MobCategory> counts = new Object2IntOpenHashMap(MobCategory.values().length);

      MobCounts() {
         super();
      }

      public void add(MobCategory var1) {
         this.counts.computeInt(var1, (var0, var1x) -> {
            return var1x == null ? 1 : var1x + 1;
         });
      }

      public boolean canSpawn(MobCategory var1) {
         return this.counts.getOrDefault(var1, 0) < var1.getMaxInstancesPerChunk();
      }
   }
}
