package net.minecraft.client.gui.components.debug;

import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntryPosition implements DebugScreenEntry {
   public static final Identifier GROUP = Identifier.withDefaultNamespace("position");

   public DebugEntryPosition() {
      super();
   }

   public void display(DebugScreenDisplayer var1, @Nullable Level var2, @Nullable LevelChunk var3, @Nullable LevelChunk var4) {
      Minecraft var5 = Minecraft.getInstance();
      Entity var6 = var5.getCameraEntity();
      if (var6 != null) {
         BlockPos var7 = var5.getCameraEntity().blockPosition();
         ChunkPos var8 = new ChunkPos(var7);
         Direction var9 = var6.getDirection();
         String var10000;
         switch (var9) {
            case NORTH -> var10000 = "Towards negative Z";
            case SOUTH -> var10000 = "Towards positive Z";
            case WEST -> var10000 = "Towards negative X";
            case EAST -> var10000 = "Towards positive X";
            default -> var10000 = "Invalid";
         }

         String var10 = var10000;
         Object var11 = var2 instanceof ServerLevel ? ((ServerLevel)var2).getForceLoadedChunks() : LongSets.EMPTY_SET;
         Identifier var10001 = GROUP;
         String var10002 = String.format(Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", var5.getCameraEntity().getX(), var5.getCameraEntity().getY(), var5.getCameraEntity().getZ());
         String var10003 = String.format(Locale.ROOT, "Block: %d %d %d", var7.getX(), var7.getY(), var7.getZ());
         String var10004 = String.format(Locale.ROOT, "Chunk: %d %d %d [%d %d in r.%d.%d.mca]", var8.x, SectionPos.blockToSectionCoord(var7.getY()), var8.z, var8.getRegionLocalX(), var8.getRegionLocalZ(), var8.getRegionX(), var8.getRegionZ());
         String var10005 = String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", var9, var10, Mth.wrapDegrees(var6.getYRot()), Mth.wrapDegrees(var6.getXRot()));
         String var10006 = String.valueOf(var5.level.dimension().identifier());
         var1.addToGroup(var10001, List.of(var10002, var10003, var10004, var10005, var10006 + " FC: " + ((LongSet)var11).size()));
      }
   }
}
