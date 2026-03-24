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

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      Minecraft minecraft = Minecraft.getInstance();
      Entity entity = minecraft.getCameraEntity();
      if (entity != null) {
         BlockPos feetPos = minecraft.getCameraEntity().blockPosition();
         ChunkPos chunkPos = ChunkPos.containing(feetPos);
         Direction direction = entity.getDirection();
         String var10000;
         switch (direction) {
            case NORTH -> var10000 = "Towards negative Z";
            case SOUTH -> var10000 = "Towards positive Z";
            case WEST -> var10000 = "Towards negative X";
            case EAST -> var10000 = "Towards positive X";
            default -> var10000 = "Invalid";
         }

         String faceString = var10000;
         LongSet chunks = (LongSet)(serverOrClientLevel instanceof ServerLevel ? ((ServerLevel)serverOrClientLevel).getForceLoadedChunks() : LongSets.EMPTY_SET);
         Identifier var10001 = GROUP;
         String var10002 = String.format(Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", minecraft.getCameraEntity().getX(), minecraft.getCameraEntity().getY(), minecraft.getCameraEntity().getZ());
         String var10003 = String.format(Locale.ROOT, "Block: %d %d %d", feetPos.getX(), feetPos.getY(), feetPos.getZ());
         String var10004 = String.format(Locale.ROOT, "Chunk: %d %d %d [%d %d in r.%d.%d.mca]", chunkPos.x(), SectionPos.blockToSectionCoord(feetPos.getY()), chunkPos.z(), chunkPos.getRegionLocalX(), chunkPos.getRegionLocalZ(), chunkPos.getRegionX(), chunkPos.getRegionZ());
         String var10005 = String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", direction, faceString, Mth.wrapDegrees(entity.getYRot()), Mth.wrapDegrees(entity.getXRot()));
         String var10006 = String.valueOf(minecraft.level.dimension().identifier());
         displayer.addToGroup(var10001, List.of(var10002, var10003, var10004, var10005, var10006 + " FC: " + chunks.size()));
      }
   }
}
