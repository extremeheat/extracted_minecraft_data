package net.minecraft.client;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.Mth;
import net.minecraft.util.OptionEnum;

public enum PrioritizeChunkUpdates implements OptionEnum {
   NONE(0, "options.prioritizeChunkUpdates.none"),
   PLAYER_AFFECTED(1, "options.prioritizeChunkUpdates.byPlayer"),
   NEARBY(2, "options.prioritizeChunkUpdates.nearby");

   private static final PrioritizeChunkUpdates[] BY_ID = (PrioritizeChunkUpdates[])Arrays.stream(values()).sorted(Comparator.comparingInt(PrioritizeChunkUpdates::getId)).toArray((var0) -> {
      return new PrioritizeChunkUpdates[var0];
   });
   private final int id;
   private final String key;

   private PrioritizeChunkUpdates(int var3, String var4) {
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
      return BY_ID[Mth.positiveModulo(var0, BY_ID.length)];
   }

   // $FF: synthetic method
   private static PrioritizeChunkUpdates[] $values() {
      return new PrioritizeChunkUpdates[]{NONE, PLAYER_AFFECTED, NEARBY};
   }
}
