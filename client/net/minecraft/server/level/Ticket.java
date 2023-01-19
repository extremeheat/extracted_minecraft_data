package net.minecraft.server.level;

import java.util.Objects;

public final class Ticket<T> implements Comparable<Ticket<?>> {
   private final TicketType<T> type;
   private final int ticketLevel;
   private final T key;
   private long createdTick;

   protected Ticket(TicketType<T> var1, int var2, T var3) {
      super();
      this.type = var1;
      this.ticketLevel = var2;
      this.key = (T)var3;
   }

   public int compareTo(Ticket<?> var1) {
      int var2 = Integer.compare(this.ticketLevel, var1.ticketLevel);
      if (var2 != 0) {
         return var2;
      } else {
         int var3 = Integer.compare(System.identityHashCode(this.type), System.identityHashCode(var1.type));
         return var3 != 0 ? var3 : this.type.getComparator().compare(this.key, var1.key);
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Ticket)) {
         return false;
      } else {
         Ticket var2 = (Ticket)var1;
         return this.ticketLevel == var2.ticketLevel && Objects.equals(this.type, var2.type) && Objects.equals(this.key, var2.key);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.type, this.ticketLevel, this.key);
   }

   @Override
   public String toString() {
      return "Ticket[" + this.type + " " + this.ticketLevel + " (" + this.key + ")] at " + this.createdTick;
   }

   public TicketType<T> getType() {
      return this.type;
   }

   public int getTicketLevel() {
      return this.ticketLevel;
   }

   protected void setCreatedTick(long var1) {
      this.createdTick = var1;
   }

   protected boolean timedOut(long var1) {
      long var3 = this.type.timeout();
      return var3 != 0L && var1 - this.createdTick > var3;
   }
}
