package net.minecraft.world.entity.ai.memory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.util.VisibleForDebug;

public class ExpirableValue<T> {
   private final T value;
   private long timeToLive;

   public ExpirableValue(T var1, long var2) {
      super();
      this.value = (T)var1;
      this.timeToLive = var2;
   }

   public void tick() {
      if (this.canExpire()) {
         --this.timeToLive;
      }
   }

   public static <T> ExpirableValue<T> of(T var0) {
      return new ExpirableValue<>((T)var0, 9223372036854775807L);
   }

   public static <T> ExpirableValue<T> of(T var0, long var1) {
      return new ExpirableValue<>((T)var0, var1);
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

   @Override
   public String toString() {
      return this.value + (this.canExpire() ? " (ttl: " + this.timeToLive + ")" : "");
   }

   @VisibleForDebug
   public boolean canExpire() {
      return this.timeToLive != 9223372036854775807L;
   }

   public static <T> Codec<ExpirableValue<T>> codec(Codec<T> var0) {
      return RecordCodecBuilder.create(
         var1 -> var1.group(
                  var0.fieldOf("value").forGetter(var0xx -> var0xx.value),
                  Codec.LONG.optionalFieldOf("ttl").forGetter(var0xx -> var0xx.canExpire() ? Optional.of(var0xx.timeToLive) : Optional.empty())
               )
               .apply(var1, (var0xx, var1x) -> new ExpirableValue<>(var0xx, var1x.orElse(9223372036854775807L)))
      );
   }
}
