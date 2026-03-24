package net.minecraft.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import java.util.Objects;
import java.util.function.IntFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ByIdMap;

public enum PrioritizeChunkUpdates {
   NONE(0, "options.prioritizeChunkUpdates.none"),
   PLAYER_AFFECTED(1, "options.prioritizeChunkUpdates.byPlayer"),
   NEARBY(2, "options.prioritizeChunkUpdates.nearby");

   private static final IntFunction<PrioritizeChunkUpdates> BY_ID = ByIdMap.<PrioritizeChunkUpdates>continuous((p) -> p.id, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
   public static final Codec<PrioritizeChunkUpdates> LEGACY_CODEC;
   private final int id;
   private final Component caption;

   private PrioritizeChunkUpdates(final int id, final String key) {
      this.id = id;
      this.caption = Component.translatable(key);
   }

   public Component caption() {
      return this.caption;
   }

   // $FF: synthetic method
   private static PrioritizeChunkUpdates[] $values() {
      return new PrioritizeChunkUpdates[]{NONE, PLAYER_AFFECTED, NEARBY};
   }

   static {
      PrimitiveCodec var10000 = Codec.INT;
      IntFunction var10001 = BY_ID;
      Objects.requireNonNull(var10001);
      LEGACY_CODEC = var10000.xmap(var10001::apply, (p) -> p.id);
   }
}
