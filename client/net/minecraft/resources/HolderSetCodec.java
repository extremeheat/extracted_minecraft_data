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
      return var1 ? var2 : ExtraCodecs.compactListCodec(var0, var2);
   }

   public static <E> Codec<HolderSet<E>> create(ResourceKey<? extends Registry<E>> var0, Codec<Holder<E>> var1, boolean var2) {
      return new HolderSetCodec<HolderSet<E>>(var0, var1, var2);
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
            return this.registryAwareCodec.decode(var1, var2).flatMap((var1x) -> {
               DataResult var2 = (DataResult)((Either)var1x.getFirst()).map((var1) -> lookupTag(var5, var1), (var0) -> DataResult.success(HolderSet.direct(var0)));
               return var2.map((var1) -> Pair.of(var1, var1x.getSecond()));
            });
         }
      }

      return this.decodeWithoutRegistry(var1, var2);
   }

   private static <E> DataResult<HolderSet<E>> lookupTag(HolderGetter<E> var0, TagKey<E> var1) {
      return (DataResult)var0.get(var1).map(DataResult::success).orElseGet(() -> DataResult.error(() -> {
            String var10000 = String.valueOf(var1.location());
            return "Missing tag: '" + var10000 + "' in '" + String.valueOf(var1.registry().location()) + "'";
         }));
   }

   public <T> DataResult<T> encode(HolderSet<E> var1, DynamicOps<T> var2, T var3) {
      if (var2 instanceof RegistryOps var4) {
         Optional var5 = var4.owner(this.registryKey);
         if (var5.isPresent()) {
            if (!var1.canSerializeIn((HolderOwner)var5.get())) {
               return DataResult.error(() -> "HolderSet " + String.valueOf(var1) + " is not valid in current registry set");
            }

            return this.registryAwareCodec.encode(var1.unwrap().mapRight(List::copyOf), var2, var3);
         }
      }

      return this.<T>encodeWithoutRegistry(var1, var2, var3);
   }

   private <T> DataResult<Pair<HolderSet<E>, T>> decodeWithoutRegistry(DynamicOps<T> var1, T var2) {
      return this.elementCodec.listOf().decode(var1, var2).flatMap((var0) -> {
         ArrayList var1 = new ArrayList();

         for(Holder var3 : (List)var0.getFirst()) {
            if (!(var3 instanceof Holder.Direct)) {
               return DataResult.error(() -> "Can't decode element " + String.valueOf(var3) + " without registry");
            }

            Holder.Direct var4 = (Holder.Direct)var3;
            var1.add(var4);
         }

         return DataResult.success(new Pair(HolderSet.direct(var1), var0.getSecond()));
      });
   }

   private <T> DataResult<T> encodeWithoutRegistry(HolderSet<E> var1, DynamicOps<T> var2, T var3) {
      return this.homogenousListCodec.encode(var1.stream().toList(), var2, var3);
   }

   // $FF: synthetic method
   public DataResult encode(final Object var1, final DynamicOps var2, final Object var3) {
      return this.encode((HolderSet)var1, var2, var3);
   }
}
