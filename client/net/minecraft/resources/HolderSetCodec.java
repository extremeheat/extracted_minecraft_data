package net.minecraft.resources;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;

public class HolderSetCodec<E> implements Codec<HolderSet<E>> {
   private final ResourceKey<? extends Registry<E>> registryKey;
   private final Codec<Holder<E>> elementCodec;
   private final Codec<List<Holder<E>>> homogenousListCodec;
   private final Codec<Either<TagKey<E>, List<Holder<E>>>> registryAwareCodec;

   private static <E> Codec<List<Holder<E>>> homogenousList(Codec<Holder<E>> var0, boolean var1) {
      Codec var2 = var0.listOf().validate(ExtraCodecs.ensureHomogenous(Holder::kind));
      return var1
         ? var2
         : Codec.either(var2, var0)
            .xmap(var0x -> (List)var0x.map(var0xx -> var0xx, List::of), var0x -> var0x.size() == 1 ? Either.right((Holder)var0x.get(0)) : Either.left(var0x));
   }

   public static <E> Codec<HolderSet<E>> create(ResourceKey<? extends Registry<E>> var0, Codec<Holder<E>> var1, boolean var2) {
      return new HolderSetCodec<>(var0, var1, var2);
   }

   private HolderSetCodec(ResourceKey<? extends Registry<E>> var1, Codec<Holder<E>> var2, boolean var3) {
      super();
      this.registryKey = var1;
      this.elementCodec = var2;
      this.homogenousListCodec = homogenousList(var2, var3);
      this.registryAwareCodec = Codec.either(TagKey.hashedCodec(var1), this.homogenousListCodec);
   }

   public <T> DataResult<Pair<HolderSet<E>, T>> decode(DynamicOps<T> var1, T var2) {
      if (var1 instanceof RegistryOps var3) {
         Optional var4 = var3.getter(this.registryKey);
         if (var4.isPresent()) {
            HolderGetter var5 = (HolderGetter)var4.get();
            return this.registryAwareCodec
               .decode(var1, var2)
               .flatMap(
                  var1x -> {
                     DataResult var2x = (DataResult)((Either)var1x.getFirst())
                        .map(var1xx -> lookupTag(var5, var1xx), var0x -> DataResult.success(HolderSet.direct(var0x)));
                     return var2x.map(var1xx -> Pair.of(var1xx, var1x.getSecond()));
                  }
               );
         }
      }

      return this.decodeWithoutRegistry(var1, (T)var2);
   }

   private static <E> DataResult<HolderSet<E>> lookupTag(HolderGetter<E> var0, TagKey<E> var1) {
      return var0.get(var1)
         .<DataResult<HolderSet<E>>>map(DataResult::success)
         .orElseGet(() -> DataResult.error(() -> "Missing tag: '" + var1.location() + "' in '" + var1.registry().location() + "'"));
   }

   public <T> DataResult<T> encode(HolderSet<E> var1, DynamicOps<T> var2, T var3) {
      if (var2 instanceof RegistryOps var4) {
         Optional var5 = var4.owner(this.registryKey);
         if (var5.isPresent()) {
            if (!var1.canSerializeIn((HolderOwner<T>)var5.get())) {
               return DataResult.error(() -> "HolderSet " + var1 + " is not valid in current registry set");
            }

            return this.registryAwareCodec.encode(var1.unwrap().mapRight(List::copyOf), var2, var3);
         }
      }

      return this.encodeWithoutRegistry(var1, var2, (T)var3);
   }

   private <T> DataResult<Pair<HolderSet<E>, T>> decodeWithoutRegistry(DynamicOps<T> var1, T var2) {
      return this.elementCodec.listOf().decode(var1, var2).flatMap(var0 -> {
         ArrayList var1x = new ArrayList();

         for (Holder var3 : (List)var0.getFirst()) {
            if (!(var3 instanceof Holder.Direct var4)) {
               return DataResult.error(() -> "Can't decode element " + var3 + " without registry");
            }

            var1x.add(var4);
         }

         return DataResult.success(new Pair(HolderSet.direct(var1x), var0.getSecond()));
      });
   }

   private <T> DataResult<T> encodeWithoutRegistry(HolderSet<E> var1, DynamicOps<T> var2, T var3) {
      return this.homogenousListCodec.encode(var1.stream().toList(), var2, var3);
   }
}
