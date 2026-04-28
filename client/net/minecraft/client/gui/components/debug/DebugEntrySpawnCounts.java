package net.minecraft.client.gui.components.debug;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntrySpawnCounts implements DebugScreenEntry {
   public DebugEntrySpawnCounts() {
      super();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      Minecraft minecraft = Minecraft.getInstance();
      Entity entity = minecraft.getCameraEntity();
      ServerLevel var10000;
      if (serverOrClientLevel instanceof ServerLevel level) {
         var10000 = level;
      } else {
         var10000 = null;
      }

      ServerLevel serverLevel = var10000;
      if (entity != null && serverLevel != null) {
         ServerChunkCache chunkSource = serverLevel.getChunkSource();
         NaturalSpawner.SpawnState lastSpawnState = chunkSource.getLastSpawnState();
         if (lastSpawnState != null) {
            Object2IntMap<MobCategory> mobCategoryCounts = lastSpawnState.getMobCategoryCounts();
            int chunkCount = lastSpawnState.getSpawnableChunkCount();
            displayer.addLine("SC: " + chunkCount + ", " + (String)Stream.of(MobCategory.values()).map((c) -> {
               String var10000 = c.getDebugAbbreviation();
               return var10000 + ": " + mobCategoryCounts.getInt(c);
            }).collect(Collectors.joining(", ")));
         }

      }
   }
}
