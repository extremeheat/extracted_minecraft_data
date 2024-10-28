package net.minecraft.world.entity.ai.memory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.util.VisibleForDebug;

public class ExpirableValue<T> {
   private final T value;
   private long timeToLive;

   public ExpirableValue(T var1, long var2) {
      super();
      this.value = var1;
      this.timeToLive = var2;
   }

   public void tick() {
      if (this.canExpire()) {
         --this.timeToLive;
      }

   }

   public static <T> ExpirableValue<T> of(T var0) {
      return new ExpirableValue(var0, 9223372036854775807L);
   }

   public static <T> ExpirableValue<T> of(T var0, long var1) {
      return new ExpirableValue(var0, var1);
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

   public static <T> Codec<ExpirableValue<T>> codec(Codec<T> var0) {
      return RecordCodecBuilder.create((var1) -> {
         return var1.group(var0.fieldOf("value").forGetter((var0x) -> {
            return var0x.value;
         }), Codec.LONG.lenientOptionalFieldOf("ttl").forGetter((var0x) -> {
            return var0x.canExpire() ? Optional.of(var0x.timeToLive) : Optional.empty();
         })).apply(var1, (var0x, var1x) -> {
            return new ExpirableValue(var0x, (Long)var1x.orElse(9223372036854775807L));
         });
      });
   }
}
