package net.minecraft.tags;

import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.core.Holder;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.RegistryLayer;

public class TagNetworkSerialization {
   public TagNetworkSerialization() {
      super();
   }

   public static Map<ResourceKey<? extends Registry<?>>, NetworkPayload> serializeTagsToNetwork(final LayeredRegistryAccess<RegistryLayer> registries) {
      return (Map)RegistrySynchronization.networkSafeRegistries(registries).map((e) -> Pair.of(e.key(), serializeToNetwork(e.value()))).filter((e) -> !((NetworkPayload)e.getSecond()).isEmpty()).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
   }

   private static <T> NetworkPayload serializeToNetwork(final Registry<T> registry) {
      Map<Identifier, IntList> result = new HashMap();
      registry.getTags().forEach((tag) -> {
         IntList ids = new IntArrayList(tag.size());

         for(Holder<T> holder : tag) {
            if (holder.kind() != Holder.Kind.REFERENCE) {
               throw new IllegalStateException("Can't serialize unregistered value " + String.valueOf(holder));
            }

            ids.add(registry.getId(holder.value()));
         }

         result.put(tag.key().location(), ids);
      });
      return new NetworkPayload(result);
   }

   private static <T> TagLoader.LoadResult<T> deserializeTagsFromNetwork(final Registry<T> registry, final NetworkPayload payload) {
      ResourceKey<? extends Registry<T>> registryKey = registry.key();
      Map<TagKey<T>, List<Holder<T>>> tags = new HashMap();
      payload.tags.forEach((key, ids) -> {
         TagKey<T> tagKey = TagKey.<T>create(registryKey, key);
         IntStream var10000 = ids.intStream();
         Objects.requireNonNull(registry);
         List<Holder<T>> values = (List)var10000.mapToObj(registry::get).flatMap(Optional::stream).collect(Collectors.toUnmodifiableList());
         tags.put(tagKey, values);
      });
      return new TagLoader.LoadResult<T>(registryKey, tags);
   }

   public static record NetworkPayload(Map<Identifier, IntList> tags) {
      public static final NetworkPayload EMPTY = new NetworkPayload(Map.of());
      private static final StreamCodec<ByteBuf, IntList> ID_LIST_STREAM_CODEC;
      public static final StreamCodec<ByteBuf, NetworkPayload> STREAM_CODEC;

      public NetworkPayload {
         super();
      }

      public boolean isEmpty() {
         return this.tags.isEmpty();
      }

      public int size() {
         return this.tags.size();
      }

      public <T> TagLoader.LoadResult<T> resolve(final Registry<T> registry) {
         return TagNetworkSerialization.<T>deserializeTagsFromNetwork(registry, this);
      }

      static {
         ID_LIST_STREAM_CODEC = ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.collection(IntArrayList::new));
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.map(HashMap::new, Identifier.STREAM_CODEC, ID_LIST_STREAM_CODEC), NetworkPayload::tags, NetworkPayload::new);
      }
   }
}
