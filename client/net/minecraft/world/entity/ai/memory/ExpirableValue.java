package net.minecraft.world.entity.ai.memory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;

public record ExpirableValue<T>(T value, Optional<Long> timeToLive) {
   public ExpirableValue {
      super();
   }

   public static <T> ExpirableValue<T> of(final T value) {
      return new ExpirableValue<T>(value, Optional.empty());
   }

   public static <T> ExpirableValue<T> of(final T value, final long ticksUntilExpiry) {
      return new ExpirableValue<T>(value, Optional.of(ticksUntilExpiry));
   }

   public String toString() {
      String var10000 = String.valueOf(this.value);
      return var10000 + (this.timeToLive.isPresent() ? " (ttl: " + String.valueOf(this.timeToLive.get()) + ")" : "");
   }

   public static <T> Codec<ExpirableValue<T>> codec(final Codec<T> valueCodec) {
      return RecordCodecBuilder.create((i) -> i.group(valueCodec.fieldOf("value").forGetter(ExpirableValue::value), Codec.LONG.lenientOptionalFieldOf("ttl").forGetter(ExpirableValue::timeToLive)).apply(i, ExpirableValue::new));
   }
}
