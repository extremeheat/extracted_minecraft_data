package net.minecraft.server.level;

import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class Ticket {
   private final TicketType type;
   private final int ticketLevel;
   private long ticksLeft;

   @Nullable
   public static Ticket load(CompoundTag var0) {
      TicketType var1 = (TicketType)BuiltInRegistries.TICKET_TYPE.getValue(ResourceLocation.tryParse(var0.getString("type")));
      if (var1 == null) {
         return null;
      } else {
         int var2 = var0.getInt("level");
         if (var1.hasTimeout()) {
            long var3 = var0.getLong("ticks_left");
            return new Ticket(var1, var2, var3);
         } else {
            return new Ticket(var1, var2, 0L);
         }
      }
   }

   public void save(CompoundTag var1) {
      ResourceLocation var2 = BuiltInRegistries.TICKET_TYPE.getKey(this.type);
      if (var2 == null) {
         throw new IllegalStateException("Unrecognised ticket type: " + String.valueOf(this.type));
      } else {
         var1.putString("type", var2.toString());
         var1.putInt("level", this.ticketLevel);
         if (this.type.hasTimeout()) {
            var1.putLong("ticks_left", this.ticksLeft);
         }

      }
   }

   public Ticket(TicketType var1, int var2) {
      this(var1, var2, var1.timeout());
   }

   private Ticket(TicketType var1, int var2, long var3) {
      super();
      this.type = var1;
      this.ticketLevel = var2;
      this.ticksLeft = var3;
   }

   public String toString() {
      if (this.type.hasTimeout()) {
         String var1 = Util.getRegisteredName(BuiltInRegistries.TICKET_TYPE, this.type);
         return "Ticket[" + var1 + " " + this.ticketLevel + "] with " + this.ticksLeft + " ticks left ( out of" + this.type.timeout() + ")";
      } else {
         String var10000 = Util.getRegisteredName(BuiltInRegistries.TICKET_TYPE, this.type);
         return "Ticket[" + var10000 + " " + this.ticketLevel + "] with no timeout";
      }
   }

   public TicketType getType() {
      return this.type;
   }

   public int getTicketLevel() {
      return this.ticketLevel;
   }

   public void resetTicksLeft() {
      this.ticksLeft = this.type.timeout();
   }

   public void decreaseTicksLeft() {
      if (this.type.hasTimeout()) {
         --this.ticksLeft;
      }

   }

   public boolean isTimedOut() {
      return this.type.hasTimeout() && this.ticksLeft <= 0L;
   }
}
