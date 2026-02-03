package net.minecraft.world.entity.ai.memory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.util.VisibleForDebug;

public class ExpirableValue<T> {
   private final T value;
   private long timeToLive;

   public ExpirableValue(final T value, final long timeToLive) {
      super();
      this.value = value;
      this.timeToLive = timeToLive;
   }

   public void tick() {
      if (this.canExpire()) {
         --this.timeToLive;
      }

   }

   public static <T> ExpirableValue<T> of(final T value) {
      return new ExpirableValue<T>(value, 9223372036854775807L);
   }

   public static <T> ExpirableValue<T> of(final T value, final long ticksUntilExpiry) {
      return new ExpirableValue<T>(value, ticksUntilExpiry);
   }

   public long getTimeToLive() {
      return this.timeToLive;
   }

   public T getValue() {
      return this.value;
   }

   public boolean hasExpired() {
      return this.timeToLive <= 0L;
   }

   public String toString() {
      String var10000 = String.valueOf(this.value);
      return var10000 + (this.canExpire() ? " (ttl: " + this.timeToLive + ")" : "");
   }

   @VisibleForDebug
   public boolean canExpire() {
      return this.timeToLive != 9223372036854775807L;
   }

   public static <T> Codec<ExpirableValue<T>> codec(final Codec<T> valueCodec) {
      return RecordCodecBuilder.create((i) -> i.group(valueCodec.fieldOf("value").forGetter((v) -> v.value), Codec.LONG.lenientOptionalFieldOf("ttl").forGetter((v) -> v.canExpire() ? Optional.of(v.timeToLive) : Optional.empty())).apply(i, (value, ttl) -> new ExpirableValue(value, (Long)ttl.orElse(9223372036854775807L))));
   }
}
