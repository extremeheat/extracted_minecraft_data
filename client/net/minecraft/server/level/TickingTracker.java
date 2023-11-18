package net.minecraft.server.level;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import net.minecraft.util.SortedArraySet;
import net.minecraft.world.level.ChunkPos;

public class TickingTracker extends ChunkTracker {
   public static final int MAX_LEVEL = 33;
   private static final int INITIAL_TICKET_LIST_CAPACITY = 4;
   protected final Long2ByteMap chunks = new Long2ByteOpenHashMap();
   private final Long2ObjectOpenHashMap<SortedArraySet<Ticket<?>>> tickets = new Long2ObjectOpenHashMap();

   public TickingTracker() {
      super(34, 16, 256);
      this.chunks.defaultReturnValue((byte)33);
   }

   private SortedArraySet<Ticket<?>> getTickets(long var1) {
      return (SortedArraySet<Ticket<?>>)this.tickets.computeIfAbsent(var1, var0 -> SortedArraySet.create(4));
   }

   private int getTicketLevelAt(SortedArraySet<Ticket<?>> var1) {
      return var1.isEmpty() ? 34 : ((Ticket)var1.first()).getTicketLevel();
   }

   public void addTicket(long var1, Ticket<?> var3) {
      SortedArraySet var4 = this.getTickets(var1);
      int var5 = this.getTicketLevelAt(var4);
      var4.add(var3);
      if (var3.getTicketLevel() < var5) {
         this.update(var1, var3.getTicketLevel(), true);
      }
   }

   public void removeTicket(long var1, Ticket<?> var3) {
      SortedArraySet var4 = this.getTickets(var1);
      var4.remove(var3);
      if (var4.isEmpty()) {
         this.tickets.remove(var1);
      }

      this.update(var1, this.getTicketLevelAt(var4), false);
   }

   public <T> void addTicket(TicketType<T> var1, ChunkPos var2, int var3, T var4) {
      this.addTicket(var2.toLong(), new Ticket<>(var1, var3, var4));
   }

   public <T> void removeTicket(TicketType<T> var1, ChunkPos var2, int var3, T var4) {
      Ticket var5 = new Ticket<>(var1, var3, var4);
      this.removeTicket(var2.toLong(), var5);
   }

   public void replacePlayerTicketsLevel(int var1) {
      ArrayList var2 = new ArrayList();
      ObjectIterator var3 = this.tickets.long2ObjectEntrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();

         for(Ticket var6 : (SortedArraySet)var4.getValue()) {
            if (var6.getType() == TicketType.PLAYER) {
               var2.add(Pair.of(var6, var4.getLongKey()));
            }
         }
      }

      for(Pair var10 : var2) {
         Long var11 = (Long)var10.getSecond();
         Ticket var12 = (Ticket)var10.getFirst();
         this.removeTicket(var11, var12);
         ChunkPos var7 = new ChunkPos(var11);
         TicketType var8 = var12.getType();
         this.addTicket(var8, var7, var1, var7);
      }
   }

   @Override
   protected int getLevelFromSource(long var1) {
      SortedArraySet var3 = (SortedArraySet)this.tickets.get(var1);
      return var3 != null && !var3.isEmpty() ? ((Ticket)var3.first()).getTicketLevel() : 2147483647;
   }

   public int getLevel(ChunkPos var1) {
      return this.getLevel(var1.toLong());
   }

   @Override
   protected int getLevel(long var1) {
      return this.chunks.get(var1);
   }

   @Override
   protected void setLevel(long var1, int var3) {
      if (var3 > 33) {
         this.chunks.remove(var1);
      } else {
         this.chunks.put(var1, (byte)var3);
      }
   }

   public void runAllUpdates() {
      this.runUpdates(2147483647);
   }

   public String getTicketDebugString(long var1) {
      SortedArraySet var3 = (SortedArraySet)this.tickets.get(var1);
      return var3 != null && !var3.isEmpty() ? ((Ticket)var3.first()).toString() : "no_ticket";
   }
}
