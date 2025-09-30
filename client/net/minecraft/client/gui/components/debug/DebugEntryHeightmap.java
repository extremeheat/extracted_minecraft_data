package net.minecraft.client.gui.components.debug;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

public class DebugEntryHeightmap implements DebugScreenEntry {
   private static final Map<Heightmap.Types, String> HEIGHTMAP_NAMES;
   private static final ResourceLocation GROUP;

   public DebugEntryHeightmap() {
      super();
   }

   public void display(DebugScreenDisplayer var1, @Nullable Level var2, @Nullable LevelChunk var3, @Nullable LevelChunk var4) {
      Minecraft var5 = Minecraft.getInstance();
      Entity var6 = var5.getCameraEntity();
      if (var6 != null && var5.level != null && var3 != null) {
         BlockPos var7 = var6.blockPosition();
         ArrayList var8 = new ArrayList();
         StringBuilder var9 = new StringBuilder("CH");

         for(Heightmap.Types var13 : Heightmap.Types.values()) {
            if (var13.sendToClient()) {
               var9.append(" ").append((String)HEIGHTMAP_NAMES.get(var13)).append(": ").append(var3.getHeight(var13, var7.getX(), var7.getZ()));
            }
         }

         var8.add(var9.toString());
         var9.setLength(0);
         var9.append("SH");

         for(Heightmap.Types var17 : Heightmap.Types.values()) {
            if (var17.keepAfterWorldgen()) {
               var9.append(" ").append((String)HEIGHTMAP_NAMES.get(var17)).append(": ");
               if (var4 != null) {
                  var9.append(var4.getHeight(var17, var7.getX(), var7.getZ()));
               } else {
                  var9.append("??");
               }
            }
         }

         var8.add(var9.toString());
         var1.addToGroup(GROUP, var8);
      }
   }

   static {
      HEIGHTMAP_NAMES = Maps.newEnumMap(Map.of(Heightmap.Types.WORLD_SURFACE_WG, "SW", Heightmap.Types.WORLD_SURFACE, "S", Heightmap.Types.OCEAN_FLOOR_WG, "OW", Heightmap.Types.OCEAN_FLOOR, "O", Heightmap.Types.MOTION_BLOCKING, "M", Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, "ML"));
      GROUP = ResourceLocation.withDefaultNamespace("heightmaps");
   }
}
