package net.minecraft.server.level;

import java.util.Comparator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Unit;
import net.minecraft.world.level.ChunkPos;

public class TicketType<T> {
   private final String name;
   private final Comparator<T> comparator;
   private final long timeout;
   public static final TicketType<Unit> START = create("start", (var0, var1) -> {
      return 0;
   });
   public static final TicketType<Unit> DRAGON = create("dragon", (var0, var1) -> {
      return 0;
   });
   public static final TicketType<ChunkPos> PLAYER = create("player", Comparator.comparingLong(ChunkPos::toLong));
   public static final TicketType<ChunkPos> FORCED = create("forced", Comparator.comparingLong(ChunkPos::toLong));
   public static final TicketType<BlockPos> PORTAL = create("portal", Vec3i::compareTo, 300);
   public static final TicketType<Integer> POST_TELEPORT = create("post_teleport", Integer::compareTo, 5);
   public static final TicketType<ChunkPos> UNKNOWN = create("unknown", Comparator.comparingLong(ChunkPos::toLong), 1);

   public static <T> TicketType<T> create(String var0, Comparator<T> var1) {
      return new TicketType(var0, var1, 0L);
   }

   public static <T> TicketType<T> create(String var0, Comparator<T> var1, int var2) {
      return new TicketType(var0, var1, (long)var2);
   }

   protected TicketType(String var1, Comparator<T> var2, long var3) {
      super();
      this.name = var1;
      this.comparator = var2;
      this.timeout = var3;
   }

   public String toString() {
      return this.name;
   }

   public Comparator<T> getComparator() {
      return this.comparator;
   }

   public long timeout() {
      return this.timeout;
   }
}
