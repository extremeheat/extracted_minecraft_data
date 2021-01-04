package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.TickNextTickData;
import net.minecraft.world.level.TickPriority;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;

public class ProtoTickList<T> implements TickList<T> {
   protected final Predicate<T> ignore;
   private final ChunkPos chunkPos;
   private final ShortList[] toBeTicked;

   public ProtoTickList(Predicate<T> var1, ChunkPos var2) {
      this(var1, var2, new ListTag());
   }

   public ProtoTickList(Predicate<T> var1, ChunkPos var2, ListTag var3) {
      super();
      this.toBeTicked = new ShortList[16];
      this.ignore = var1;
      this.chunkPos = var2;

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         ListTag var5 = var3.getList(var4);

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            ChunkAccess.getOrCreateOffsetList(this.toBeTicked, var4).add(var5.getShort(var6));
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
               BlockPos var6 = ProtoChunk.unpackOffsetCoordinates(var5, var3, this.chunkPos);
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
      ChunkAccess.getOrCreateOffsetList(this.toBeTicked, var1.getY() >> 4).add(ProtoChunk.packOffsetCoordinates(var1));
   }

   public boolean willTickThisTick(BlockPos var1, T var2) {
      return false;
   }

   public void addAll(Stream<TickNextTickData<T>> var1) {
      var1.forEach((var1x) -> {
         this.scheduleTick(var1x.pos, var1x.getType(), 0, var1x.priority);
      });
   }
}
