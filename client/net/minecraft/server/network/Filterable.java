package net.minecraft.server.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record Filterable<T>(T a, Optional<T> b) {
   private final T raw;
   private final Optional<T> filtered;

   public Filterable(T var1, Optional<T> var2) {
      super();
      this.raw = (T)var1;
      this.filtered = var2;
   }

   public static <T> Codec<Filterable<T>> codec(Codec<T> var0) {
      Codec var1 = RecordCodecBuilder.create(
         var1x -> var1x.group(var0.fieldOf("text").forGetter(Filterable::raw), var0.optionalFieldOf("filtered").forGetter(Filterable::filtered))
               .apply(var1x, Filterable::new)
      );
      Codec var2 = var0.xmap(Filterable::passThrough, Filterable::raw);
      return Codec.withAlternative(var1, var2);
   }

   public static <B extends ByteBuf, T> StreamCodec<B, Filterable<T>> streamCodec(StreamCodec<B, T> var0) {
      return StreamCodec.composite(var0, Filterable::raw, var0.apply(ByteBufCodecs::optional), Filterable::filtered, Filterable::new);
   }

   public static <T> Filterable<T> passThrough(T var0) {
      return new Filterable<>(var0, Optional.empty());
   }

   public static Filterable<String> from(FilteredText var0) {
      return new Filterable<>(var0.raw(), var0.isFiltered() ? Optional.of(var0.filteredOrEmpty()) : Optional.empty());
   }

   public T get(boolean var1) {
      return (T)(var1 ? this.filtered.orElse(this.raw) : this.raw);
   }

   public <U> Filterable<U> map(Function<T, U> var1) {
      return new Filterable<>((U)var1.apply(this.raw), this.filtered.map(var1));
   }

   public <U> Optional<Filterable<U>> resolve(Function<T, Optional<U>> var1) {
      Optional var2 = (Optional)var1.apply(this.raw);
      if (var2.isEmpty()) {
         return Optional.empty();
      } else if (this.filtered.isPresent()) {
         Optional var3 = (Optional)var1.apply(this.filtered.get());
         return var3.isEmpty() ? Optional.empty() : Optional.of((T)(new Filterable<Object>(var2.get(), var3)));
      } else {
         return Optional.of((T)(new Filterable<Object>(var2.get(), Optional.empty())));
      }
   }
}
