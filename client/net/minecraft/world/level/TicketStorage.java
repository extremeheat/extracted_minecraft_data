package net.minecraft.world.level;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.Ticket;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import org.slf4j.Logger;

public class TicketStorage extends SavedData {
   private static final int INITIAL_TICKET_LIST_CAPACITY = 4;
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String FILE_ID = "chunks";
   private static final String TAG_TICKETS = "tickets";
   private static final String TAG_CHUNK_POS = "chunk_pos";
   private final Long2ObjectOpenHashMap<List<Ticket>> tickets;
   private final Long2ObjectOpenHashMap<List<Ticket>> deactivatedTickets;
   private LongSet chunksWithForcedTickets;
   @Nullable
   private ChunkUpdated loadingChunkUpdatedListener;
   @Nullable
   private ChunkUpdated simulationChunkUpdatedListener;

   public static SavedData.Factory<TicketStorage> factory() {
      return new SavedData.Factory<TicketStorage>(TicketStorage::new, TicketStorage::load, DataFixTypes.SAVED_DATA_FORCED_CHUNKS);
   }

   private TicketStorage(Long2ObjectOpenHashMap<List<Ticket>> var1, Long2ObjectOpenHashMap<List<Ticket>> var2) {
      super();
      this.chunksWithForcedTickets = new LongOpenHashSet();
      this.tickets = var1;
      this.deactivatedTickets = var2;
      this.updateForcedChunks();
   }

   public TicketStorage() {
      this(new Long2ObjectOpenHashMap(4), new Long2ObjectOpenHashMap());
   }

   public static TicketStorage load(CompoundTag var0, HolderLookup.Provider var1) {
      Long2ObjectOpenHashMap var2 = new Long2ObjectOpenHashMap();
      ListTag var3 = var0.getList("tickets", 10);

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         CompoundTag var5 = var3.getCompound(var4);
         Ticket var6 = Ticket.load(var5);
         if (var6 == null) {
            LOGGER.warn("Failed to load invalid saved ticket: {} ", var5);
         } else {
            ChunkPos var7 = new ChunkPos(var5.getLong("chunk_pos"));
            List var8 = (List)var2.computeIfAbsent(var7.toLong(), (var0x) -> new ObjectArrayList(4));
            var8.add(var6);
         }
      }

      return new TicketStorage(new Long2ObjectOpenHashMap(4), var2);
   }

   public CompoundTag save(CompoundTag var1, HolderLookup.Provider var2) {
      ListTag var3 = new ListTag();
      this.tickets.forEach((var1x, var2x) -> saveTicketsForChunk(var3, var1x, var2x));
      this.deactivatedTickets.forEach((var1x, var2x) -> saveTicketsForChunk(var3, var1x, var2x));
      var1.put("tickets", var3);
      return var1;
   }

   private static void saveTicketsForChunk(ListTag var0, long var1, List<Ticket> var3) {
      for(Ticket var5 : var3) {
         if (var5.getType().persist()) {
            CompoundTag var6 = new CompoundTag();
            var6.putLong("chunk_pos", var1);
            var5.save(var6);
            var0.add(var6);
         }
      }

   }

   public void activateAllDeactivatedTickets() {
      ObjectIterator var1 = Long2ObjectMaps.fastIterable(this.deactivatedTickets).iterator();

      while(var1.hasNext()) {
         Long2ObjectMap.Entry var2 = (Long2ObjectMap.Entry)var1.next();

         for(Ticket var4 : (List)var2.getValue()) {
            this.addTicket(var2.getLongKey(), var4);
         }
      }

      this.deactivatedTickets.clear();
   }

   public void setLoadingChunkUpdatedListener(@Nullable ChunkUpdated var1) {
      this.loadingChunkUpdatedListener = var1;
   }

   public void setSimulationChunkUpdatedListener(@Nullable ChunkUpdated var1) {
      this.simulationChunkUpdatedListener = var1;
   }

   public boolean hasTickets() {
      return !this.tickets.isEmpty();
   }

   public List<Ticket> getTickets(long var1) {
      return (List)this.tickets.getOrDefault(var1, List.of());
   }

   private List<Ticket> getOrCreateTickets(long var1) {
      return (List)this.tickets.computeIfAbsent(var1, (var0) -> new ObjectArrayList(4));
   }

   public void addTicketWithRadius(TicketType var1, ChunkPos var2, int var3) {
      Ticket var4 = new Ticket(var1, ChunkLevel.byStatus(FullChunkStatus.FULL) - var3);
      this.addTicket(var2.toLong(), var4);
   }

   public void addTicket(Ticket var1, ChunkPos var2) {
      this.addTicket(var2.toLong(), var1);
   }

   public boolean addTicket(long var1, Ticket var3) {
      List var4 = this.getOrCreateTickets(var1);

      for(Ticket var6 : var4) {
         if (isTicketSameTypeAndLevel(var3, var6)) {
            var6.resetTicksLeft();
            this.setDirty();
            return false;
         }
      }

      int var7 = getTicketLevelAt(var4, true);
      int var8 = getTicketLevelAt(var4, false);
      var4.add(var3);
      if (var3.getType().doesSimulate() && var3.getTicketLevel() < var7 && this.simulationChunkUpdatedListener != null) {
         this.simulationChunkUpdatedListener.update(var1, var3.getTicketLevel(), true);
      }

      if (var3.getType().doesLoad() && var3.getTicketLevel() < var8 && this.loadingChunkUpdatedListener != null) {
         this.loadingChunkUpdatedListener.update(var1, var3.getTicketLevel(), true);
      }

      if (var3.getType().equals(TicketType.FORCED)) {
         this.chunksWithForcedTickets.add(var1);
      }

      this.setDirty();
      return true;
   }

   private static boolean isTicketSameTypeAndLevel(Ticket var0, Ticket var1) {
      return var1.getType() == var0.getType() && var1.getTicketLevel() == var0.getTicketLevel();
   }

   public int getTicketLevelAt(long var1, boolean var3) {
      return getTicketLevelAt(this.getTickets(var1), var3);
   }

   private static int getTicketLevelAt(List<Ticket> var0, boolean var1) {
      Ticket var2 = getLowestTicket(var0, var1);
      return var2 == null ? ChunkLevel.MAX_LEVEL + 1 : var2.getTicketLevel();
   }

   @Nullable
   private static Ticket getLowestTicket(@Nullable List<Ticket> var0, boolean var1) {
      if (var0 == null) {
         return null;
      } else {
         Ticket var2 = null;

         for(Ticket var4 : var0) {
            if (var2 == null || var4.getTicketLevel() < var2.getTicketLevel()) {
               if (var1 && var4.getType().doesSimulate()) {
                  var2 = var4;
               } else if (!var1 && var4.getType().doesLoad()) {
                  var2 = var4;
               }
            }
         }

         return var2;
      }
   }

   public void removeTicketWithRadius(TicketType var1, ChunkPos var2, int var3) {
      Ticket var4 = new Ticket(var1, ChunkLevel.byStatus(FullChunkStatus.FULL) - var3);
      this.removeTicket(var2.toLong(), var4);
   }

   public void removeTicket(Ticket var1, ChunkPos var2) {
      this.removeTicket(var2.toLong(), var1);
   }

   public boolean removeTicket(long var1, Ticket var3) {
      List var4 = (List)this.tickets.get(var1);
      if (var4 == null) {
         return false;
      } else {
         boolean var5 = false;
         Iterator var6 = var4.iterator();

         while(var6.hasNext()) {
            Ticket var7 = (Ticket)var6.next();
            if (isTicketSameTypeAndLevel(var3, var7)) {
               var6.remove();
               var5 = true;
               break;
            }
         }

         if (!var5) {
            return false;
         } else {
            if (var4.isEmpty()) {
               this.tickets.remove(var1);
            }

            if (var3.getType().doesSimulate() && this.simulationChunkUpdatedListener != null) {
               this.simulationChunkUpdatedListener.update(var1, getTicketLevelAt(var4, true), false);
            }

            if (var3.getType().doesLoad() && this.loadingChunkUpdatedListener != null) {
               this.loadingChunkUpdatedListener.update(var1, getTicketLevelAt(var4, false), false);
            }

            if (var3.getType().equals(TicketType.FORCED)) {
               this.updateForcedChunks();
            }

            this.setDirty();
            return true;
         }
      }
   }

   private void updateForcedChunks() {
      this.chunksWithForcedTickets = this.getAllChunksWithTicketThat((var0) -> var0.getType().equals(TicketType.FORCED));
   }

   public String getTicketDebugString(long var1, boolean var3) {
      List var4 = this.getTickets(var1);
      Ticket var5 = getLowestTicket(var4, var3);
      return var5 == null ? "no_ticket" : var5.toString();
   }

   public void purgeStaleTickets() {
      this.removeTicketIf((var0) -> {
         var0.decreaseTicksLeft();
         return var0.isTimedOut();
      }, (Long2ObjectOpenHashMap)null);
      this.setDirty();
   }

   public void deactivateTicketsOnClosing() {
      this.removeTicketIf((var0) -> var0.getType() != TicketType.UNKNOWN, this.deactivatedTickets);
   }

   public void removeTicketIf(Predicate<Ticket> var1, @Nullable Long2ObjectOpenHashMap<List<Ticket>> var2) {
      ObjectIterator var3 = this.tickets.long2ObjectEntrySet().fastIterator();
      boolean var4 = false;

      while(var3.hasNext()) {
         Long2ObjectMap.Entry var5 = (Long2ObjectMap.Entry)var3.next();
         Iterator var6 = ((List)var5.getValue()).iterator();
         boolean var7 = false;
         boolean var8 = false;

         while(var6.hasNext()) {
            Ticket var9 = (Ticket)var6.next();
            if (var1.test(var9)) {
               if (var2 != null) {
                  List var10 = (List)var2.computeIfAbsent(var5.getLongKey(), (var1x) -> new ObjectArrayList(((List)var5.getValue()).size()));
                  var10.add(var9);
               }

               var6.remove();
               if (var9.getType().doesLoad()) {
                  var8 = true;
               }

               if (var9.getType().doesSimulate()) {
                  var7 = true;
               }

               if (var9.getType().equals(TicketType.FORCED)) {
                  var4 = true;
               }
            }
         }

         if (var8 || var7) {
            if (var8 && this.loadingChunkUpdatedListener != null) {
               this.loadingChunkUpdatedListener.update(var5.getLongKey(), getTicketLevelAt((List)var5.getValue(), false), false);
            }

            if (var7 && this.simulationChunkUpdatedListener != null) {
               this.simulationChunkUpdatedListener.update(var5.getLongKey(), getTicketLevelAt((List)var5.getValue(), true), false);
            }

            this.setDirty();
            if (((List)var5.getValue()).isEmpty()) {
               var3.remove();
            }
         }
      }

      if (var4) {
         this.updateForcedChunks();
      }

   }

   public void replaceTicketLevelOfType(int var1, TicketType var2) {
      ArrayList var3 = new ArrayList();
      ObjectIterator var4 = this.tickets.long2ObjectEntrySet().iterator();

      while(var4.hasNext()) {
         Long2ObjectMap.Entry var5 = (Long2ObjectMap.Entry)var4.next();

         for(Ticket var7 : (List)var5.getValue()) {
            if (var7.getType() == var2) {
               var3.add(Pair.of(var7, var5.getLongKey()));
            }
         }
      }

      for(Pair var10 : var3) {
         Long var11 = (Long)var10.getSecond();
         Ticket var12 = (Ticket)var10.getFirst();
         this.removeTicket(var11, var12);
         TicketType var8 = var12.getType();
         this.addTicket(var11, new Ticket(var8, var1));
      }

   }

   public boolean updateChunkForced(ChunkPos var1, boolean var2) {
      Ticket var3 = new Ticket(TicketType.FORCED, ChunkMap.FORCED_TICKET_LEVEL);
      return var2 ? this.addTicket(var1.toLong(), var3) : this.removeTicket(var1.toLong(), var3);
   }

   public LongSet getForceLoadedChunks() {
      return this.chunksWithForcedTickets;
   }

   private LongSet getAllChunksWithTicketThat(Predicate<Ticket> var1) {
      LongOpenHashSet var2 = new LongOpenHashSet();
      ObjectIterator var3 = Long2ObjectMaps.fastIterable(this.tickets).iterator();

      while(var3.hasNext()) {
         Long2ObjectMap.Entry var4 = (Long2ObjectMap.Entry)var3.next();

         for(Ticket var6 : (List)var4.getValue()) {
            if (var1.test(var6)) {
               var2.add(var4.getLongKey());
               break;
            }
         }
      }

      return var2;
   }

   @FunctionalInterface
   public interface ChunkUpdated {
      void update(long var1, int var3, boolean var4);
   }
}
