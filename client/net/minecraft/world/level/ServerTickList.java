package net.minecraft.world.level;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class ServerTickList<T> implements TickList<T> {
   protected final Predicate<T> ignore;
   private final Function<T, ResourceLocation> toId;
   private final Function<ResourceLocation, T> fromId;
   private final Set<TickNextTickData<T>> tickNextTickSet = Sets.newHashSet();
   private final TreeSet<TickNextTickData<T>> tickNextTickList = Sets.newTreeSet(TickNextTickData.createTimeComparator());
   private final ServerLevel level;
   private final Queue<TickNextTickData<T>> currentlyTicking = Queues.newArrayDeque();
   private final List<TickNextTickData<T>> alreadyTicked = Lists.newArrayList();
   private final Consumer<TickNextTickData<T>> ticker;

   public ServerTickList(ServerLevel var1, Predicate<T> var2, Function<T, ResourceLocation> var3, Function<ResourceLocation, T> var4, Consumer<TickNextTickData<T>> var5) {
      super();
      this.ignore = var2;
      this.toId = var3;
      this.fromId = var4;
      this.level = var1;
      this.ticker = var5;
   }

   public void tick() {
      int var1 = this.tickNextTickList.size();
      if (var1 != this.tickNextTickSet.size()) {
         throw new IllegalStateException("TickNextTick list out of synch");
      } else {
         if (var1 > 65536) {
            var1 = 65536;
         }

         ServerChunkCache var2 = this.level.getChunkSource();
         Iterator var3 = this.tickNextTickList.iterator();
         this.level.getProfiler().push("cleaning");

         TickNextTickData var4;
         while(var1 > 0 && var3.hasNext()) {
            var4 = (TickNextTickData)var3.next();
            if (var4.delay > this.level.getGameTime()) {
               break;
            }

            if (var2.isTickingChunk(var4.pos)) {
               var3.remove();
               this.tickNextTickSet.remove(var4);
               this.currentlyTicking.add(var4);
               --var1;
            }
         }

         this.level.getProfiler().popPush("ticking");

         while((var4 = (TickNextTickData)this.currentlyTicking.poll()) != null) {
            if (var2.isTickingChunk(var4.pos)) {
               try {
                  this.alreadyTicked.add(var4);
                  this.ticker.accept(var4);
               } catch (Throwable var8) {
                  CrashReport var6 = CrashReport.forThrowable(var8, "Exception while ticking");
                  CrashReportCategory var7 = var6.addCategory("Block being ticked");
                  CrashReportCategory.populateBlockDetails(var7, var4.pos, (BlockState)null);
                  throw new ReportedException(var6);
               }
            } else {
               this.scheduleTick(var4.pos, var4.getType(), 0);
            }
         }

         this.level.getProfiler().pop();
         this.alreadyTicked.clear();
         this.currentlyTicking.clear();
      }
   }

   public boolean willTickThisTick(BlockPos var1, T var2) {
      return this.currentlyTicking.contains(new TickNextTickData(var1, var2));
   }

   public void addAll(Stream<TickNextTickData<T>> var1) {
      var1.forEach(this::addTickData);
   }

   public List<TickNextTickData<T>> fetchTicksInChunk(ChunkPos var1, boolean var2, boolean var3) {
      int var4 = (var1.x << 4) - 2;
      int var5 = var4 + 16 + 2;
      int var6 = (var1.z << 4) - 2;
      int var7 = var6 + 16 + 2;
      return this.fetchTicksInArea(new BoundingBox(var4, 0, var6, var5, 256, var7), var2, var3);
   }

   public List<TickNextTickData<T>> fetchTicksInArea(BoundingBox var1, boolean var2, boolean var3) {
      List var4 = this.fetchTicksInArea((List)null, this.tickNextTickList, var1, var2);
      if (var2 && var4 != null) {
         this.tickNextTickSet.removeAll(var4);
      }

      var4 = this.fetchTicksInArea(var4, this.currentlyTicking, var1, var2);
      if (!var3) {
         var4 = this.fetchTicksInArea(var4, this.alreadyTicked, var1, var2);
      }

      return var4 == null ? Collections.emptyList() : var4;
   }

   @Nullable
   private List<TickNextTickData<T>> fetchTicksInArea(@Nullable List<TickNextTickData<T>> var1, Collection<TickNextTickData<T>> var2, BoundingBox var3, boolean var4) {
      Iterator var5 = var2.iterator();

      while(var5.hasNext()) {
         TickNextTickData var6 = (TickNextTickData)var5.next();
         BlockPos var7 = var6.pos;
         if (var7.getX() >= var3.x0 && var7.getX() < var3.x1 && var7.getZ() >= var3.z0 && var7.getZ() < var3.z1) {
            if (var4) {
               var5.remove();
            }

            if (var1 == null) {
               var1 = Lists.newArrayList();
            }

            ((List)var1).add(var6);
         }
      }

      return (List)var1;
   }

   public void copy(BoundingBox var1, BlockPos var2) {
      List var3 = this.fetchTicksInArea(var1, false, false);
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         TickNextTickData var5 = (TickNextTickData)var4.next();
         if (var1.isInside(var5.pos)) {
            BlockPos var6 = var5.pos.offset(var2);
            Object var7 = var5.getType();
            this.addTickData(new TickNextTickData(var6, var7, var5.delay, var5.priority));
         }
      }

   }

   public ListTag save(ChunkPos var1) {
      List var2 = this.fetchTicksInChunk(var1, false, true);
      return saveTickList(this.toId, var2, this.level.getGameTime());
   }

   public static <T> ListTag saveTickList(Function<T, ResourceLocation> var0, Iterable<TickNextTickData<T>> var1, long var2) {
      ListTag var4 = new ListTag();
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         TickNextTickData var6 = (TickNextTickData)var5.next();
         CompoundTag var7 = new CompoundTag();
         var7.putString("i", ((ResourceLocation)var0.apply(var6.getType())).toString());
         var7.putInt("x", var6.pos.getX());
         var7.putInt("y", var6.pos.getY());
         var7.putInt("z", var6.pos.getZ());
         var7.putInt("t", (int)(var6.delay - var2));
         var7.putInt("p", var6.priority.getValue());
         var4.add(var7);
      }

      return var4;
   }

   public boolean hasScheduledTick(BlockPos var1, T var2) {
      return this.tickNextTickSet.contains(new TickNextTickData(var1, var2));
   }

   public void scheduleTick(BlockPos var1, T var2, int var3, TickPriority var4) {
      if (!this.ignore.test(var2)) {
         this.addTickData(new TickNextTickData(var1, var2, (long)var3 + this.level.getGameTime(), var4));
      }

   }

   private void addTickData(TickNextTickData<T> var1) {
      if (!this.tickNextTickSet.contains(var1)) {
         this.tickNextTickSet.add(var1);
         this.tickNextTickList.add(var1);
      }

   }

   public int size() {
      return this.tickNextTickSet.size();
   }
}
