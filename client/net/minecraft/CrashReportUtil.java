package net.minecraft;

import java.util.Locale;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class CrashReportUtil {
   public CrashReportUtil() {
      super();
   }

   public static String formatLocation(final LevelHeightAccessor levelHeightAccessor, final double x, final double y, final double z) {
      return String.format(Locale.ROOT, "%.2f,%.2f,%.2f - %s", x, y, z, formatLocation(levelHeightAccessor, BlockPos.containing(x, y, z)));
   }

   public static String formatLocation(final LevelHeightAccessor levelHeightAccessor, final BlockPos pos) {
      return formatLocation(levelHeightAccessor, pos.getX(), pos.getY(), pos.getZ());
   }

   public static String formatLocation(final LevelHeightAccessor levelHeightAccessor, final int x, final int y, final int z) {
      StringBuilder result = new StringBuilder();

      try {
         result.append(String.format(Locale.ROOT, "World: (%d,%d,%d)", x, y, z));
      } catch (Throwable var19) {
         result.append("(Error finding world loc)");
      }

      result.append(", ");

      try {
         int sectionX = SectionPos.blockToSectionCoord(x);
         int sectionY = SectionPos.blockToSectionCoord(y);
         int sectionZ = SectionPos.blockToSectionCoord(z);
         int relativeX = x & 15;
         int relativeY = y & 15;
         int relativeZ = z & 15;
         int minBlockX = SectionPos.sectionToBlockCoord(sectionX);
         int minBlockY = levelHeightAccessor.getMinY();
         int minBlockZ = SectionPos.sectionToBlockCoord(sectionZ);
         int maxBlockX = SectionPos.sectionToBlockCoord(sectionX + 1) - 1;
         int maxBlockY = levelHeightAccessor.getMaxY();
         int maxBlockZ = SectionPos.sectionToBlockCoord(sectionZ + 1) - 1;
         result.append(String.format(Locale.ROOT, "Section: (at %d,%d,%d in %d,%d,%d; chunk contains blocks %d,%d,%d to %d,%d,%d)", relativeX, relativeY, relativeZ, sectionX, sectionY, sectionZ, minBlockX, minBlockY, minBlockZ, maxBlockX, maxBlockY, maxBlockZ));
      } catch (Throwable var18) {
         result.append("(Error finding chunk loc)");
      }

      result.append(", ");

      try {
         int regionX = x >> 9;
         int regionZ = z >> 9;
         int minChunkX = regionX << 5;
         int minChunkZ = regionZ << 5;
         int maxChunkX = (regionX + 1 << 5) - 1;
         int maxChunkZ = (regionZ + 1 << 5) - 1;
         int minBlockX = regionX << 9;
         int minBlockY = levelHeightAccessor.getMinY();
         int minBlockZ = regionZ << 9;
         int maxBlockX = (regionX + 1 << 9) - 1;
         int maxBlockY = levelHeightAccessor.getMaxY();
         int maxBlockZ = (regionZ + 1 << 9) - 1;
         result.append(String.format(Locale.ROOT, "Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,%d,%d to %d,%d,%d)", regionX, regionZ, minChunkX, minChunkZ, maxChunkX, maxChunkZ, minBlockX, minBlockY, minBlockZ, maxBlockX, maxBlockY, maxBlockZ));
      } catch (Throwable var17) {
         result.append("(Error finding world loc)");
      }

      return result.toString();
   }

   public static void populateBlockDetails(final CrashReportCategory category, final LevelHeightAccessor levelHeightAccessor, final BlockPos pos, final BlockState state) {
      Objects.requireNonNull(state);
      category.setDetail("Block", state::toString);
      populateBlockLocationDetails(category, levelHeightAccessor, pos);
   }

   public static CrashReportCategory populateBlockLocationDetails(final CrashReportCategory category, final LevelHeightAccessor levelHeightAccessor, final BlockPos pos) {
      return category.setDetail("Block location", (CrashReportDetail)(() -> formatLocation(levelHeightAccessor, pos)));
   }
}
