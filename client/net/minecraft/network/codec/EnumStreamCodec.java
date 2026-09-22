package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import net.minecraft.util.ByIdMap;

public class EnumStreamCodec<T extends Enum<T>> implements StreamCodec<ByteBuf, T> {
   private final IntFunction<T> byId;
   private final StreamCodec<ByteBuf, T> delegate;

   private EnumStreamCodec(final IntFunction<T> byId, final StreamCodec<ByteBuf, T> delegate) {
      super();
      this.byId = byId;
      this.delegate = delegate;
   }

   public static <T extends Enum<T>> EnumStreamCodec<T> custom(final ToIntFunction<T> toId, final IntFunction<T> byId) {
      return new EnumStreamCodec<T>(byId, ByteBufCodecs.idMapper(byId, toId));
   }

   public static <T extends Enum<T>> EnumStreamCodec<T> continuous(final Class<T> enumClass, final ToIntFunction<T> toId, final ByIdMap.OutOfBoundsStrategy strategy) {
      IntFunction<T> byId = ByIdMap.<T>continuous(toId, (Enum[])enumClass.getEnumConstants(), strategy);
      return custom(toId, byId);
   }

   public static <T extends Enum<T>> EnumStreamCodec<T> sparse(final Class<T> enumClass, final ToIntFunction<T> toId, final T defaultEntry) {
      IntFunction<T> byId = ByIdMap.<T>sparse(toId, (Enum[])enumClass.getEnumConstants(), defaultEntry);
      return custom(toId, byId);
   }

   public T decode(final ByteBuf input) {
      return (T)(this.delegate.decode(input));
   }

   public void encode(final ByteBuf output, final T value) {
      this.delegate.encode(output, value);
   }

   public T byId(final int id) {
      return (T)(this.byId.apply(id));
   }
}
