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

   public void display(DebugScreenDisplayer var1, @Nullable Level var2, @Nullable LevelChunk var3, @Nullable LevelChunk var4) {
      Minecraft var5 = Minecraft.getInstance();
      Entity var6 = var5.getCameraEntity();
      ServerLevel var7 = var2 instanceof ServerLevel ? (ServerLevel)var2 : null;
      if (var6 != null && var7 != null) {
         ServerChunkCache var8 = var7.getChunkSource();
         NaturalSpawner.SpawnState var9 = var8.getLastSpawnState();
         if (var9 != null) {
            Object2IntMap var10 = var9.getMobCategoryCounts();
            int var11 = var9.getSpawnableChunkCount();
            var1.addLine("SC: " + var11 + ", " + (String)Stream.of(MobCategory.values()).map((var1x) -> {
               char var10000 = Character.toUpperCase(var1x.getName().charAt(0));
               return var10000 + ": " + var10.getInt(var1x);
            }).collect(Collectors.joining(", ")));
         }

      }
   }
}
