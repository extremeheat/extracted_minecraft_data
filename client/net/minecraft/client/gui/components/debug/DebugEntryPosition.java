package net.minecraft.client.gui.components.debug;

import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntryPosition implements DebugScreenEntry {
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
         Object var13;
         if (serverOrClientLevel instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)serverOrClientLevel;
            var13 = serverLevel.getForceLoadedChunks();
         } else {
            var13 = LongSets.EMPTY_SET;
         }

         LongSet chunks = (LongSet)var13;
         displayer.addFactToGroup(DebugGroups.POSITION, "XYZ", (fact) -> fact.formattedValue("%.3f", minecraft.getCameraEntity().getX()).text(" / ").formattedValue("%.5f", minecraft.getCameraEntity().getY()).text(" / ").formattedValue("%.3f", minecraft.getCameraEntity().getZ()));
         displayer.addFactToGroup(DebugGroups.POSITION, "Block", (fact) -> fact.value(feetPos.getX()).text(" ").value(feetPos.getY()).text(" ").value(feetPos.getZ()));
         displayer.addFactToGroup(DebugGroups.POSITION, "Chunk", (fact) -> fact.value(chunkPos.x()).text(" ").value(SectionPos.blockToSectionCoord(feetPos.getY())).text(" ").value(chunkPos.z()).text(" [").value(chunkPos.getRegionLocalX()).text(" ").value(chunkPos.getRegionLocalZ()).text(" in ").formattedValue("r.%d.%d.mca", chunkPos.getRegionX(), chunkPos.getRegionZ()).text("]"));
         displayer.addFactToGroup(DebugGroups.POSITION, "Facing", (fact) -> fact.value(direction.toString()).text(" (").value(faceString).text(") (").formattedValue("%.1f", Mth.wrapDegrees(entity.getYRot())).text(" / ").formattedValue("%.1f", Mth.wrapDegrees(entity.getXRot())).text(")"));
         displayer.addFactToGroup(DebugGroups.POSITION, "Dimension", (fact) -> fact.value(minecraft.level.dimension().identifier().toString()));
         if (!chunks.isEmpty()) {
            displayer.addFactToGroup(DebugGroups.POSITION, "Forced Chunks", (fact) -> fact.value(chunks.size()));
         }

      }
   }
}
