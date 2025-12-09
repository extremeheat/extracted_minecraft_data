package net.minecraft.util.debug;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public class DebugSubscription<T> {
   public static final int DOES_NOT_EXPIRE = 0;
   final @Nullable StreamCodec<? super RegistryFriendlyByteBuf, T> valueStreamCodec;
   private final int expireAfterTicks;

   public DebugSubscription(@Nullable StreamCodec<? super RegistryFriendlyByteBuf, T> var1, int var2) {
      super();
      this.valueStreamCodec = var1;
      this.expireAfterTicks = var2;
   }

   public DebugSubscription(@Nullable StreamCodec<? super RegistryFriendlyByteBuf, T> var1) {
      this(var1, 0);
   }

   public Update<T> packUpdate(@Nullable T var1) {
      return new Update<T>(this, Optional.ofNullable(var1));
   }

   public Update<T> emptyUpdate() {
      return new Update<T>(this, Optional.empty());
   }

   public Event<T> packEvent(T var1) {
      return new Event<T>(this, var1);
   }

   public String toString() {
      return Util.getRegisteredName(BuiltInRegistries.DEBUG_SUBSCRIPTION, this);
   }

   public @Nullable StreamCodec<? super RegistryFriendlyByteBuf, T> valueStreamCodec() {
      return this.valueStreamCodec;
   }

   public int expireAfterTicks() {
      return this.expireAfterTicks;
   }

   public static record Update<T>(DebugSubscription<T> subscription, Optional<T> value) {
      public static final StreamCodec<RegistryFriendlyByteBuf, Update<?>> STREAM_CODEC;

      public Update(DebugSubscription<T> var1, Optional<T> var2) {
         super();
         this.subscription = var1;
         this.value = var2;
      }

      private static <T> StreamCodec<? super RegistryFriendlyByteBuf, Update<T>> streamCodec(DebugSubscription<T> var0) {
         return ByteBufCodecs.optional((StreamCodec)Objects.requireNonNull(var0.valueStreamCodec)).map((var1) -> new Update(var0, var1), Update::value);
      }

      static {
         STREAM_CODEC = ByteBufCodecs.registry(Registries.DEBUG_SUBSCRIPTION).dispatch(Update::subscription, Update::streamCodec);
      }
   }

   public static record Event<T>(DebugSubscription<T> subscription, T value) {
      public static final StreamCodec<RegistryFriendlyByteBuf, Event<?>> STREAM_CODEC;

      public Event(DebugSubscription<T> var1, T var2) {
         super();
         this.subscription = var1;
         this.value = var2;
      }

      private static <T> StreamCodec<? super RegistryFriendlyByteBuf, Event<T>> streamCodec(DebugSubscription<T> var0) {
         return ((StreamCodec)Objects.requireNonNull(var0.valueStreamCodec)).map((var1) -> new Event(var0, var1), Event::value);
      }

      static {
         STREAM_CODEC = ByteBufCodecs.registry(Registries.DEBUG_SUBSCRIPTION).dispatch(Event::subscription, Event::streamCodec);
      }
   }
}
