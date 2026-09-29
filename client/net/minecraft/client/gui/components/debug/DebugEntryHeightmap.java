package net.minecraft.client.gui.components.debug;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jspecify.annotations.Nullable;

public class DebugEntryHeightmap implements DebugScreenEntry {
   private static final Map<Heightmap.Types, String> HEIGHTMAP_NAMES;

   public DebugEntryHeightmap() {
      super();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      Minecraft minecraft = Minecraft.getInstance();
      Entity entity = minecraft.getCameraEntity();
      if (entity != null && minecraft.level != null && clientChunk != null) {
         BlockPos feetPos = entity.blockPosition();

         for(Heightmap.Types type : Heightmap.Types.values()) {
            displayer.addFactToGroup(DebugGroups.HEIGHTMAP, (String)HEIGHTMAP_NAMES.get(type), (fact) -> {
               int clientHeight = clientChunk.getHeight(type, feetPos.getX(), feetPos.getZ());
               int serverHeight = serverChunk == null ? -1 : serverChunk.getHeight(type, feetPos.getX(), feetPos.getZ());
               boolean verbose = type.sendToClient() && serverChunk != null && clientHeight != serverHeight;
               if (verbose) {
                  fact.value(clientHeight).text(" (client), ").value(serverHeight).text(" (server)");
               } else if (type.sendToClient()) {
                  fact.value(clientHeight);
               } else if (serverChunk != null) {
                  fact.value(serverHeight);
               }

            });
         }

      }
   }

   static {
      HEIGHTMAP_NAMES = Maps.newEnumMap(Map.of(Heightmap.Types.WORLD_SURFACE_WG, "(WG) Surface", Heightmap.Types.WORLD_SURFACE, "Surface", Heightmap.Types.OCEAN_FLOOR_WG, "(WG) Ocean Floor", Heightmap.Types.OCEAN_FLOOR, "Ocean Floor", Heightmap.Types.MOTION_BLOCKING, "Motion", Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, "Motion (w/o Leaves)"));
   }
}
