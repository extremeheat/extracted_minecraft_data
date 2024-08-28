package net.minecraft.network.codec;

import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IdMap;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.Utf8String;
import net.minecraft.network.VarInt;
import net.minecraft.network.VarLong;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface ByteBufCodecs {
   int MAX_INITIAL_COLLECTION_SIZE = 65536;
   StreamCodec<ByteBuf, Boolean> BOOL = new StreamCodec<ByteBuf, Boolean>() {
      public Boolean decode(ByteBuf var1) {
         return var1.readBoolean();
      }

      public void encode(ByteBuf var1, Boolean var2) {
         var1.writeBoolean(var2);
      }
   };
   StreamCodec<ByteBuf, Byte> BYTE = new StreamCodec<ByteBuf, Byte>() {
      public Byte decode(ByteBuf var1) {
         return var1.readByte();
      }

      public void encode(ByteBuf var1, Byte var2) {
         var1.writeByte(var2);
      }
   };
   StreamCodec<ByteBuf, Float> ROTATION_BYTE = BYTE.map(Mth::unpackDegrees, Mth::packDegrees);
   StreamCodec<ByteBuf, Short> SHORT = new StreamCodec<ByteBuf, Short>() {
      public Short decode(ByteBuf var1) {
         return var1.readShort();
      }

      public void encode(ByteBuf var1, Short var2) {
         var1.writeShort(var2);
      }
   };
   StreamCodec<ByteBuf, Integer> UNSIGNED_SHORT = new StreamCodec<ByteBuf, Integer>() {
      public Integer decode(ByteBuf var1) {
         return var1.readUnsignedShort();
      }

      public void encode(ByteBuf var1, Integer var2) {
         var1.writeShort(var2);
      }
   };
   StreamCodec<ByteBuf, Integer> INT = new StreamCodec<ByteBuf, Integer>() {
      public Integer decode(ByteBuf var1) {
         return var1.readInt();
      }

      public void encode(ByteBuf var1, Integer var2) {
         var1.writeInt(var2);
      }
   };
   StreamCodec<ByteBuf, Integer> VAR_INT = new StreamCodec<ByteBuf, Integer>() {
      public Integer decode(ByteBuf var1) {
         return VarInt.read(var1);
      }

      public void encode(ByteBuf var1, Integer var2) {
         VarInt.write(var1, var2);
      }
   };
   StreamCodec<ByteBuf, Long> VAR_LONG = new StreamCodec<ByteBuf, Long>() {
      public Long decode(ByteBuf var1) {
         return VarLong.read(var1);
      }

      public void encode(ByteBuf var1, Long var2) {
         VarLong.write(var1, var2);
      }
   };
   StreamCodec<ByteBuf, Float> FLOAT = new StreamCodec<ByteBuf, Float>() {
      public Float decode(ByteBuf var1) {
         return var1.readFloat();
      }

      public void encode(ByteBuf var1, Float var2) {
         var1.writeFloat(var2);
      }
   };
   StreamCodec<ByteBuf, Double> DOUBLE = new StreamCodec<ByteBuf, Double>() {
      public Double decode(ByteBuf var1) {
         return var1.readDouble();
      }

      public void encode(ByteBuf var1, Double var2) {
         var1.writeDouble(var2);
      }
   };
   StreamCodec<ByteBuf, byte[]> BYTE_ARRAY = new StreamCodec<ByteBuf, byte[]>() {
      public byte[] decode(ByteBuf var1) {
         return FriendlyByteBuf.readByteArray(var1);
      }

      public void encode(ByteBuf var1, byte[] var2) {
         FriendlyByteBuf.writeByteArray(var1, var2);
      }
   };
   StreamCodec<ByteBuf, String> STRING_UTF8 = stringUtf8(32767);
   StreamCodec<ByteBuf, Tag> TAG = tagCodec(() -> NbtAccounter.create(2097152L));
   StreamCodec<ByteBuf, Tag> TRUSTED_TAG = tagCodec(NbtAccounter::unlimitedHeap);
   StreamCodec<ByteBuf, CompoundTag> COMPOUND_TAG = compoundTagCodec(() -> NbtAccounter.create(2097152L));
   StreamCodec<ByteBuf, CompoundTag> TRUSTED_COMPOUND_TAG = compoundTagCodec(NbtAccounter::unlimitedHeap);
   StreamCodec<ByteBuf, Optional<CompoundTag>> OPTIONAL_COMPOUND_TAG = new StreamCodec<ByteBuf, Optional<CompoundTag>>() {
      public Optional<CompoundTag> decode(ByteBuf var1) {
         return Optional.ofNullable(FriendlyByteBuf.readNbt(var1));
      }

      public void encode(ByteBuf var1, Optional<CompoundTag> var2) {
         FriendlyByteBuf.writeNbt(var1, (Tag)var2.orElse(null));
      }
   };
   StreamCodec<ByteBuf, Vector3f> VECTOR3F = new StreamCodec<ByteBuf, Vector3f>() {
      public Vector3f decode(ByteBuf var1) {
         return FriendlyByteBuf.readVector3f(var1);
      }

      public void encode(ByteBuf var1, Vector3f var2) {
         FriendlyByteBuf.writeVector3f(var1, var2);
      }
   };
   StreamCodec<ByteBuf, Quaternionf> QUATERNIONF = new StreamCodec<ByteBuf, Quaternionf>() {
      public Quaternionf decode(ByteBuf var1) {
         return FriendlyByteBuf.readQuaternion(var1);
      }

      public void encode(ByteBuf var1, Quaternionf var2) {
         FriendlyByteBuf.writeQuaternion(var1, var2);
      }
   };
   StreamCodec<ByteBuf, Integer> CONTAINER_ID = new StreamCodec<ByteBuf, Integer>() {
      public Integer decode(ByteBuf var1) {
         return FriendlyByteBuf.readContainerId(var1);
      }

      public void encode(ByteBuf var1, Integer var2) {
         FriendlyByteBuf.writeContainerId(var1, var2);
      }
   };
   StreamCodec<ByteBuf, PropertyMap> GAME_PROFILE_PROPERTIES = new StreamCodec<ByteBuf, PropertyMap>() {
      private static final int MAX_PROPERTY_NAME_LENGTH = 64;
      private static final int MAX_PROPERTY_VALUE_LENGTH = 32767;
      private static final int MAX_PROPERTY_SIGNATURE_LENGTH = 1024;
      private static final int MAX_PROPERTIES = 16;

      public PropertyMap decode(ByteBuf var1) {
         int var2 = ByteBufCodecs.readCount(var1, 16);
         PropertyMap var3 = new PropertyMap();

         for (int var4 = 0; var4 < var2; var4++) {
            String var5 = Utf8String.read(var1, 64);
            String var6 = Utf8String.read(var1, 32767);
            String var7 = FriendlyByteBuf.readNullable(var1, var0 -> Utf8String.read(var0, 1024));
            Property var8 = new Property(var5, var6, var7);
            var3.put(var8.name(), var8);
         }

         return var3;
      }

      public void encode(ByteBuf var1, PropertyMap var2) {
         ByteBufCodecs.writeCount(var1, var2.size(), 16);

         for (Property var4 : var2.values()) {
            Utf8String.write(var1, var4.name(), 64);
            Utf8String.write(var1, var4.value(), 32767);
            FriendlyByteBuf.writeNullable(var1, var4.signature(), (var0, var1x) -> Utf8String.write(var0, var1x, 1024));
         }
      }
   };
   StreamCodec<ByteBuf, GameProfile> GAME_PROFILE = new StreamCodec<ByteBuf, GameProfile>() {
      public GameProfile decode(ByteBuf var1) {
         UUID var2 = UUIDUtil.STREAM_CODEC.decode(var1);
         String var3 = Utf8String.read(var1, 16);
         GameProfile var4 = new GameProfile(var2, var3);
         var4.getProperties().putAll((Multimap)ByteBufCodecs.GAME_PROFILE_PROPERTIES.decode(var1));
         return var4;
      }

      public void encode(ByteBuf var1, GameProfile var2) {
         UUIDUtil.STREAM_CODEC.encode(var1, var2.getId());
         Utf8String.write(var1, var2.getName(), 16);
         ByteBufCodecs.GAME_PROFILE_PROPERTIES.encode(var1, var2.getProperties());
      }
   };

   static StreamCodec<ByteBuf, byte[]> byteArray(final int var0) {
      return new StreamCodec<ByteBuf, byte[]>() {
         public byte[] decode(ByteBuf var1) {
            return FriendlyByteBuf.readByteArray(var1, var0);
         }

         public void encode(ByteBuf var1, byte[] var2) {
            if (var2.length > var0) {
               throw new EncoderException("ByteArray with size " + var2.length + " is bigger than allowed " + var0);
            } else {
               FriendlyByteBuf.writeByteArray(var1, var2);
            }
         }
      };
   }

   static StreamCodec<ByteBuf, String> stringUtf8(final int var0) {
      return new StreamCodec<ByteBuf, String>() {
         public String decode(ByteBuf var1) {
            return Utf8String.read(var1, var0);
         }

         public void encode(ByteBuf var1, String var2) {
            Utf8String.write(var1, var2, var0);
         }
      };
   }

   static StreamCodec<ByteBuf, Tag> tagCodec(final Supplier<NbtAccounter> var0) {
      return new StreamCodec<ByteBuf, Tag>() {
         public Tag decode(ByteBuf var1) {
            Tag var2 = FriendlyByteBuf.readNbt(var1, (NbtAccounter)var0.get());
            if (var2 == null) {
               throw new DecoderException("Expected non-null compound tag");
            } else {
               return var2;
            }
         }

         public void encode(ByteBuf var1, Tag var2) {
            if (var2 == EndTag.INSTANCE) {
               throw new EncoderException("Expected non-null compound tag");
            } else {
               FriendlyByteBuf.writeNbt(var1, var2);
            }
         }
      };
   }

   static StreamCodec<ByteBuf, CompoundTag> compoundTagCodec(Supplier<NbtAccounter> var0) {
      return tagCodec(var0).map(var0x -> {
         if (var0x instanceof CompoundTag) {
            return (CompoundTag)var0x;
         } else {
            throw new DecoderException("Not a compound tag: " + var0x);
         }
      }, var0x -> (Tag)var0x);
   }

   static <T> StreamCodec<ByteBuf, T> fromCodecTrusted(Codec<T> var0) {
      return fromCodec(var0, NbtAccounter::unlimitedHeap);
   }

   static <T> StreamCodec<ByteBuf, T> fromCodec(Codec<T> var0) {
      return fromCodec(var0, () -> NbtAccounter.create(2097152L));
   }

   static <T> StreamCodec<ByteBuf, T> fromCodec(Codec<T> var0, Supplier<NbtAccounter> var1) {
      return tagCodec(var1)
         .map(
            var1x -> (T)var0.parse(NbtOps.INSTANCE, var1x).getOrThrow(var1xx -> new DecoderException("Failed to decode: " + var1xx + " " + var1x)),
            var1x -> (Tag)var0.encodeStart(NbtOps.INSTANCE, var1x).getOrThrow(var1xx -> new EncoderException("Failed to encode: " + var1xx + " " + var1x))
         );
   }

   static <T> StreamCodec<RegistryFriendlyByteBuf, T> fromCodecWithRegistriesTrusted(Codec<T> var0) {
      return fromCodecWithRegistries(var0, NbtAccounter::unlimitedHeap);
   }

   static <T> StreamCodec<RegistryFriendlyByteBuf, T> fromCodecWithRegistries(Codec<T> var0) {
      return fromCodecWithRegistries(var0, () -> NbtAccounter.create(2097152L));
   }

   static <T> StreamCodec<RegistryFriendlyByteBuf, T> fromCodecWithRegistries(final Codec<T> var0, Supplier<NbtAccounter> var1) {
      final StreamCodec var2 = tagCodec(var1);
      return new StreamCodec<RegistryFriendlyByteBuf, T>() {
         public T decode(RegistryFriendlyByteBuf var1) {
            Tag var2x = (Tag)var2.decode(var1);
            RegistryOps var3 = var1.registryAccess().createSerializationContext(NbtOps.INSTANCE);
            return (T)var0.parse(var3, var2x).getOrThrow(var1x -> new DecoderException("Failed to decode: " + var1x + " " + var2x));
         }

         public void encode(RegistryFriendlyByteBuf var1, T var2x) {
            RegistryOps var3 = var1.registryAccess().createSerializationContext(NbtOps.INSTANCE);
            Tag var4 = (Tag)var0.encodeStart(var3, var2x).getOrThrow(var1x -> new EncoderException("Failed to encode: " + var1x + " " + var2x));
            var2.encode(var1, var4);
         }
      };
   }

   static <B extends ByteBuf, V> StreamCodec<B, Optional<V>> optional(final StreamCodec<B, V> var0) {
      return new StreamCodec<B, Optional<V>>() {
         public Optional<V> decode(B var1) {
            return var1.readBoolean() ? Optional.of(var0.decode((B)var1)) : Optional.empty();
         }

         public void encode(B var1, Optional<V> var2) {
            if (var2.isPresent()) {
               var1.writeBoolean(true);
               var0.encode((B)var1, (V)var2.get());
            } else {
               var1.writeBoolean(false);
            }
         }
      };
   }

   static int readCount(ByteBuf var0, int var1) {
      int var2 = VarInt.read(var0);
      if (var2 > var1) {
         throw new DecoderException(var2 + " elements exceeded max size of: " + var1);
      } else {
         return var2;
      }
   }

   static void writeCount(ByteBuf var0, int var1, int var2) {
      if (var1 > var2) {
         throw new EncoderException(var1 + " elements exceeded max size of: " + var2);
      } else {
         VarInt.write(var0, var1);
      }
   }

   static <B extends ByteBuf, V, C extends Collection<V>> StreamCodec<B, C> collection(IntFunction<C> var0, StreamCodec<? super B, V> var1) {
      return collection(var0, var1, 2147483647);
   }

   static <B extends ByteBuf, V, C extends Collection<V>> StreamCodec<B, C> collection(
      final IntFunction<C> var0, final StreamCodec<? super B, V> var1, final int var2
   ) {
      return new StreamCodec<B, C>() {
         public C decode(B var1x) {
            int var2x = ByteBufCodecs.readCount(var1x, var2);
            Collection var3 = (Collection)var0.apply(Math.min(var2x, 65536));

            for (int var4 = 0; var4 < var2x; var4++) {
               var3.add(var1.decode((B)var1x));
            }

            return (C)var3;
         }

         public void encode(B var1x, C var2x) {
            ByteBufCodecs.writeCount(var1x, var2x.size(), var2);

            for (Object var4 : var2x) {
               var1.encode((B)var1x, (V)var4);
            }
         }
      };
   }

   static <B extends ByteBuf, V, C extends Collection<V>> StreamCodec.CodecOperation<B, V, C> collection(IntFunction<C> var0) {
      return var1 -> collection(var0, var1);
   }

   static <B extends ByteBuf, V> StreamCodec.CodecOperation<B, V, List<V>> list() {
      return var0 -> collection(ArrayList::new, var0);
   }

   static <B extends ByteBuf, V> StreamCodec.CodecOperation<B, V, List<V>> list(int var0) {
      return var1 -> collection(ArrayList::new, var1, var0);
   }

   static <B extends ByteBuf, K, V, M extends Map<K, V>> StreamCodec<B, M> map(
      IntFunction<? extends M> var0, StreamCodec<? super B, K> var1, StreamCodec<? super B, V> var2
   ) {
      return map(var0, var1, var2, 2147483647);
   }

   static <B extends ByteBuf, K, V, M extends Map<K, V>> StreamCodec<B, M> map(
      final IntFunction<? extends M> var0, final StreamCodec<? super B, K> var1, final StreamCodec<? super B, V> var2, final int var3
   ) {
      return new StreamCodec<B, M>() {
         public void encode(B var1x, M var2x) {
            ByteBufCodecs.writeCount(var1x, var2x.size(), var3);
            var2x.forEach((var3xx, var4) -> {
               var1.encode((B)var1x, (V)var3xx);
               var2.encode((B)var1x, (V)var4);
            });
         }

         public M decode(B var1x) {
            int var2x = ByteBufCodecs.readCount(var1x, var3);
            Map var3x = (Map)var0.apply(Math.min(var2x, 65536));

            for (int var4 = 0; var4 < var2x; var4++) {
               Object var5 = var1.decode((B)var1x);
               Object var6 = var2.decode((B)var1x);
               var3x.put(var5, var6);
            }

            return (M)var3x;
         }
      };
   }

   static <B extends ByteBuf, L, R> StreamCodec<B, Either<L, R>> either(final StreamCodec<? super B, L> var0, final StreamCodec<? super B, R> var1) {
      return new StreamCodec<B, Either<L, R>>() {
         public Either<L, R> decode(B var1x) {
            return var1x.readBoolean() ? Either.left(var0.decode((B)var1x)) : Either.right(var1.decode((B)var1x));
         }

         public void encode(B var1x, Either<L, R> var2) {
            var2.ifLeft(var2x -> {
               var1x.writeBoolean(true);
               var0.encode((B)var1x, var2x);
            }).ifRight(var2x -> {
               var1x.writeBoolean(false);
               var1.encode((B)var1x, var2x);
            });
         }
      };
   }

   static <T> StreamCodec<ByteBuf, T> idMapper(final IntFunction<T> var0, final ToIntFunction<T> var1) {
      return new StreamCodec<ByteBuf, T>() {
         public T decode(ByteBuf var1x) {
            int var2 = VarInt.read(var1x);
            return (T)var0.apply(var2);
         }

         public void encode(ByteBuf var1x, T var2) {
            int var3 = var1.applyAsInt(var2);
            VarInt.write(var1x, var3);
         }
      };
   }

   static <T> StreamCodec<ByteBuf, T> idMapper(IdMap<T> var0) {
      return idMapper(var0::byIdOrThrow, var0::getIdOrThrow);
   }

   private static <T, R> StreamCodec<RegistryFriendlyByteBuf, R> registry(
      final ResourceKey<? extends Registry<T>> var0, final Function<Registry<T>, IdMap<R>> var1
   ) {
      return new StreamCodec<RegistryFriendlyByteBuf, R>() {
         private IdMap<R> getRegistryOrThrow(RegistryFriendlyByteBuf var1x) {
            return (IdMap<R>)var1.apply(var1x.registryAccess().lookupOrThrow(var0));
         }

         public R decode(RegistryFriendlyByteBuf var1x) {
            int var2 = VarInt.read(var1x);
            return (R)this.getRegistryOrThrow(var1x).byIdOrThrow(var2);
         }

         public void encode(RegistryFriendlyByteBuf var1x, R var2) {
            int var3 = this.getRegistryOrThrow(var1x).getIdOrThrow(var2);
            VarInt.write(var1x, var3);
         }
      };
   }

   static <T> StreamCodec<RegistryFriendlyByteBuf, T> registry(ResourceKey<? extends Registry<T>> var0) {
      return registry(var0, var0x -> var0x);
   }

   static <T> StreamCodec<RegistryFriendlyByteBuf, Holder<T>> holderRegistry(ResourceKey<? extends Registry<T>> var0) {
      return registry(var0, Registry::asHolderIdMap);
   }

   static <T> StreamCodec<RegistryFriendlyByteBuf, Holder<T>> holder(
      final ResourceKey<? extends Registry<T>> var0, final StreamCodec<? super RegistryFriendlyByteBuf, T> var1
   ) {
      return new StreamCodec<RegistryFriendlyByteBuf, Holder<T>>() {
         private static final int DIRECT_HOLDER_ID = 0;

         private IdMap<Holder<T>> getRegistryOrThrow(RegistryFriendlyByteBuf var1x) {
            return var1x.registryAccess().<T>lookupOrThrow(var0).asHolderIdMap();
         }

         public Holder<T> decode(RegistryFriendlyByteBuf var1x) {
            int var2 = VarInt.read(var1x);
            return var2 == 0 ? Holder.direct((T)var1.decode(var1x)) : (Holder)this.getRegistryOrThrow(var1x).byIdOrThrow(var2 - 1);
         }

         public void encode(RegistryFriendlyByteBuf var1x, Holder<T> var2) {
            switch (var2.kind()) {
               case REFERENCE:
                  int var3 = this.getRegistryOrThrow(var1x).getIdOrThrow(var2);
                  VarInt.write(var1x, var3 + 1);
                  break;
               case DIRECT:
                  VarInt.write(var1x, 0);
                  var1.encode(var1x, var2.value());
            }
         }
      };
   }

   static <T> StreamCodec<RegistryFriendlyByteBuf, HolderSet<T>> holderSet(final ResourceKey<? extends Registry<T>> var0) {
      return new StreamCodec<RegistryFriendlyByteBuf, HolderSet<T>>() {
         private static final int NAMED_SET = -1;
         private final StreamCodec<RegistryFriendlyByteBuf, Holder<T>> holderCodec = ByteBufCodecs.holderRegistry(var0);

         public HolderSet<T> decode(RegistryFriendlyByteBuf var1) {
            int var2 = VarInt.read(var1) - 1;
            if (var2 == -1) {
               Registry var5 = var1.registryAccess().lookupOrThrow(var0);
               return var5.get(TagKey.create(var0, ResourceLocation.STREAM_CODEC.decode(var1))).orElseThrow();
            } else {
               ArrayList var3 = new ArrayList(Math.min(var2, 65536));

               for (int var4 = 0; var4 < var2; var4++) {
                  var3.add(this.holderCodec.decode(var1));
               }

               return HolderSet.direct(var3);
            }
         }

         public void encode(RegistryFriendlyByteBuf var1, HolderSet<T> var2) {
            Optional var3 = var2.unwrapKey();
            if (var3.isPresent()) {
               VarInt.write(var1, 0);
               ResourceLocation.STREAM_CODEC.encode(var1, ((TagKey)var3.get()).location());
            } else {
               VarInt.write(var1, var2.size() + 1);

               for (Holder var5 : var2) {
                  this.holderCodec.encode(var1, var5);
               }
            }
         }
      };
   }
}
