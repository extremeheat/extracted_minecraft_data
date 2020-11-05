package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.TickPriority;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;

public class ProtoTickList<T> implements TickList<T> {
   protected final Predicate<T> ignore;
   private final ChunkPos chunkPos;
   private final ShortList[] toBeTicked;
   private LevelHeightAccessor levelHeightAccessor;

   public ProtoTickList(Predicate<T> var1, ChunkPos var2, LevelHeightAccessor var3) {
      this(var1, var2, new ListTag(), var3);
   }

   public ProtoTickList(Predicate<T> var1, ChunkPos var2, ListTag var3, LevelHeightAccessor var4) {
      super();
      this.ignore = var1;
      this.chunkPos = var2;
      this.levelHeightAccessor = var4;
      this.toBeTicked = new ShortList[var4.getSectionsCount()];

      for(int var5 = 0; var5 < var3.size(); ++var5) {
         ListTag var6 = var3.getList(var5);

         for(int var7 = 0; var7 < var6.size(); ++var7) {
            ChunkAccess.getOrCreateOffsetList(this.toBeTicked, var5).add(var6.getShort(var7));
         }
      }

   }

   public ListTag save() {
      return ChunkSerializer.packOffsets(this.toBeTicked);
   }

   public void copyOut(TickList<T> var1, Function<BlockPos, T> var2) {
      for(int var3 = 0; var3 < this.toBeTicked.length; ++var3) {
         if (this.toBeTicked[var3] != null) {
            ShortListIterator var4 = this.toBeTicked[var3].iterator();

            while(var4.hasNext()) {
               Short var5 = (Short)var4.next();
               BlockPos var6 = ProtoChunk.unpackOffsetCoordinates(var5, this.levelHeightAccessor.getSectionYFromSectionIndex(var3), this.chunkPos);
               var1.scheduleTick(var6, var2.apply(var6), 0);
            }

            this.toBeTicked[var3].clear();
         }
      }

   }

   public boolean hasScheduledTick(BlockPos var1, T var2) {
      return false;
   }

   public void scheduleTick(BlockPos var1, T var2, int var3, TickPriority var4) {
      ChunkAccess.getOrCreateOffsetList(this.toBeTicked, this.levelHeightAccessor.getSectionIndex(var1.getY())).add(ProtoChunk.packOffsetCoordinates(var1));
   }

   public boolean willTickThisTick(BlockPos var1, T var2) {
      return false;
   }
}
