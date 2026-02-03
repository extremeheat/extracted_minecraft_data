package net.minecraft.world.entity.ai.memory;

import net.minecraft.world.entity.ai.Brain;
import org.jspecify.annotations.Nullable;

public class MemorySlot<T> {
   private static final long NEVER_EXPIRE = 9223372036854775807L;
   private @Nullable T value;
   private long timeToLive;

   private MemorySlot(final @Nullable T value, final long timeToLive) {
      super();
      this.value = value;
      this.timeToLive = timeToLive;
   }

   public void tick() {
      if (this.hasValue() && this.canExpire()) {
         if (this.hasExpired()) {
            this.clear();
         } else {
            --this.timeToLive;
         }
      }

   }

   public static <T> MemorySlot<T> create() {
      return new MemorySlot<T>((Object)null, 9223372036854775807L);
   }

   public void set(final T value, final long timeToLive) {
      this.value = value;
      this.timeToLive = timeToLive;
   }

   public void set(final T value) {
      this.set(value, 9223372036854775807L);
   }

   public void clear() {
      this.value = null;
      this.timeToLive = 9223372036854775807L;
   }

   public boolean hasValue() {
      return this.value != null;
   }

   public @Nullable T value() {
      return this.value;
   }

   public boolean canExpire() {
      return this.timeToLive != 9223372036854775807L;
   }

   public boolean hasExpired() {
      return this.timeToLive <= 0L;
   }

   public long timeToLive() {
      return this.timeToLive;
   }

   public String toString() {
      if (this.value == null) {
         return "<empty>";
      } else {
         String var10000 = String.valueOf(this.value);
         return var10000 + (this.canExpire() ? " (ttl: " + this.timeToLive + ")" : "");
      }
   }

   public void visit(final MemoryModuleType<T> type, final Brain.Visitor visitor) {
      if (this.value != null) {
         if (this.canExpire()) {
            visitor.accept(type, this.value, this.timeToLive);
         } else {
            visitor.accept(type, this.value);
         }
      } else {
         visitor.acceptEmpty(type);
      }

   }
}
