package net.minecraft.client;

import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.OptionEnum;

public enum PrioritizeChunkUpdates implements OptionEnum {
   NONE(0, "options.prioritizeChunkUpdates.none"),
   PLAYER_AFFECTED(1, "options.prioritizeChunkUpdates.byPlayer"),
   NEARBY(2, "options.prioritizeChunkUpdates.nearby");

   private static final IntFunction<PrioritizeChunkUpdates> BY_ID = ByIdMap.continuous(PrioritizeChunkUpdates::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
   private final int id;
   private final String key;

   private PrioritizeChunkUpdates(final int var3, final String var4) {
      this.id = var3;
      this.key = var4;
   }

   public int getId() {
      return this.id;
   }

   public String getKey() {
      return this.key;
   }

   public static PrioritizeChunkUpdates byId(int var0) {
      return (PrioritizeChunkUpdates)BY_ID.apply(var0);
   }

   // $FF: synthetic method
   private static PrioritizeChunkUpdates[] $values() {
      return new PrioritizeChunkUpdates[]{NONE, PLAYER_AFFECTED, NEARBY};
   }
}
