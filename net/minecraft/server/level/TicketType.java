package net.minecraft.server.level;

import java.util.Comparator;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;

public class TicketType {
   private final String name;
   private final Comparator comparator;
   private final long timeout;
   public static final TicketType START = create("start", (var0, var1) -> {
      return 0;
   });
   public static final TicketType DRAGON = create("dragon", (var0, var1) -> {
      return 0;
   });
   public static final TicketType PLAYER = create("player", Comparator.comparingLong(ChunkPos::toLong));
   public static final TicketType FORCED = create("forced", Comparator.comparingLong(ChunkPos::toLong));
   public static final TicketType LIGHT = create("light", Comparator.comparingLong(ChunkPos::toLong));
   public static final TicketType PORTAL = create("portal", Vec3i::compareTo, 300);
   public static final TicketType POST_TELEPORT = create("post_teleport", Integer::compareTo, 5);
   public static final TicketType UNKNOWN = create("unknown", Comparator.comparingLong(ChunkPos::toLong), 1);

   public static TicketType create(String var0, Comparator var1) {
      return new TicketType(var0, var1, 0L);
   }

   public static TicketType create(String var0, Comparator var1, int var2) {
      return new TicketType(var0, var1, (long)var2);
   }

   protected TicketType(String var1, Comparator var2, long var3) {
      this.name = var1;
      this.comparator = var2;
      this.timeout = var3;
   }

   public String toString() {
      return this.name;
   }

   public Comparator getComparator() {
      return this.comparator;
   }

   public long timeout() {
      return this.timeout;
   }
}
