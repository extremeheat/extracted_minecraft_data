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
   private final @Nullable StreamCodec<? super RegistryFriendlyByteBuf, T> valueStreamCodec;
   private final int expireAfterTicks;

   public DebugSubscription(final @Nullable StreamCodec<? super RegistryFriendlyByteBuf, T> valueStreamCodec, final int expireAfterTicks) {
      super();
      this.valueStreamCodec = valueStreamCodec;
      this.expireAfterTicks = expireAfterTicks;
   }

   public DebugSubscription(final @Nullable StreamCodec<? super RegistryFriendlyByteBuf, T> valueStreamCodec) {
      this(valueStreamCodec, 0);
   }

   public Update<T> packUpdate(final @Nullable T value) {
      return new Update<T>(this, Optional.ofNullable(value));
   }

   public Update<T> emptyUpdate() {
      return new Update<T>(this, Optional.empty());
   }

   public Event<T> packEvent(final T value) {
      return new Event<T>(this, value);
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

      public Update {
         super();
      }

      private static <T> StreamCodec<? super RegistryFriendlyByteBuf, Update<T>> streamCodec(final DebugSubscription<T> subscription) {
         return ByteBufCodecs.optional((StreamCodec)Objects.requireNonNull(subscription.valueStreamCodec)).map((value) -> new Update(subscription, value), Update::value);
      }

      static {
         STREAM_CODEC = ByteBufCodecs.registry(Registries.DEBUG_SUBSCRIPTION).dispatch(Update::subscription, Update::streamCodec);
      }
   }

   public static record Event<T>(DebugSubscription<T> subscription, T value) {
      public static final StreamCodec<RegistryFriendlyByteBuf, Event<?>> STREAM_CODEC;

      public Event {
         super();
      }

      private static <T> StreamCodec<? super RegistryFriendlyByteBuf, Event<T>> streamCodec(final DebugSubscription<T> subscription) {
         return ((StreamCodec)Objects.requireNonNull(subscription.valueStreamCodec)).map((value) -> new Event(subscription, value), Event::value);
      }

      static {
         STREAM_CODEC = ByteBufCodecs.registry(Registries.DEBUG_SUBSCRIPTION).dispatch(Event::subscription, Event::streamCodec);
      }
   }
}
