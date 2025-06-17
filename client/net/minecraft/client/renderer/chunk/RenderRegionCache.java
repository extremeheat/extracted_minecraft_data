package net.minecraft.client.renderer.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public class RenderRegionCache {
   private final Long2ObjectMap<SectionCopy> sectionCopyCache = new Long2ObjectOpenHashMap();

   public RenderRegionCache() {
      super();
   }

   public RenderSectionRegion createRegion(Level var1, long var2) {
      int var4 = SectionPos.x(var2);
      int var5 = SectionPos.y(var2);
      int var6 = SectionPos.z(var2);
      int var7 = var4 - 1;
      int var8 = var5 - 1;
      int var9 = var6 - 1;
      int var10 = var4 + 1;
      int var11 = var5 + 1;
      int var12 = var6 + 1;
      SectionCopy[] var13 = new SectionCopy[27];

      for(int var14 = var9; var14 <= var12; ++var14) {
         for(int var15 = var8; var15 <= var11; ++var15) {
            for(int var16 = var7; var16 <= var10; ++var16) {
               int var17 = RenderSectionRegion.index(var7, var8, var9, var16, var15, var14);
               var13[var17] = this.getSectionDataCopy(var1, var16, var15, var14);
            }
         }
      }

      return new RenderSectionRegion(var1, var7, var8, var9, var13);
   }

   private SectionCopy getSectionDataCopy(Level var1, int var2, int var3, int var4) {
      return (SectionCopy)this.sectionCopyCache.computeIfAbsent(SectionPos.asLong(var2, var3, var4), (var4x) -> {
         LevelChunk var6 = var1.getChunk(var2, var4);
         return new SectionCopy(var6, var6.getSectionIndexFromSectionY(var3));
      });
   }
}
