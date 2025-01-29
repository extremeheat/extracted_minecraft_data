package net.minecraft.server.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;

public class Ticket {
   public static final MapCodec<Ticket> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(BuiltInRegistries.TICKET_TYPE.byNameCodec().fieldOf("type").forGetter(Ticket::getType), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("level").forGetter(Ticket::getTicketLevel), Codec.LONG.optionalFieldOf("ticks_left", 0L).forGetter((var0x) -> var0x.ticksLeft)).apply(var0, Ticket::new));
   private final TicketType type;
   private final int ticketLevel;
   private long ticksLeft;

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
