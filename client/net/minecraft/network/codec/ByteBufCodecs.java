package net.minecraft.network.codec;

import com.google.common.collect.ImmutableMultimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.BiFunction;
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
import net.minecraft.util.ARGB;
import net.minecraft.util.LenientJsonParser;
import net.minecraft.util.Mth;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;

public interface ByteBufCodecs {
   int MAX_INITIAL_COLLECTION_SIZE = 65536;
   StreamCodec<ByteBuf, Boolean> BOOL = new StreamCodec<ByteBuf, Boolean>() {
      public Boolean decode(ByteBuf var1) {
         return var1.readBoolean();
      }

      public void encode(ByteBuf var1, Boolean var2) {
         var1.writeBoolean(var2);
      }

      // $FF: synthetic method
      public void encode(final Object var1, final Object var2) {
         this.encode((ByteBuf)var1, (Boolean)var2);
      }

      // $FF: synthetic method
      public Object decode(final Object var1) {
         return this.decode((ByteBuf)var1);
      }
   };
   StreamCodec<ByteBuf, Byte> BYTE = new StreamCodec<ByteBuf, Byte>() {
      public Byte decode(ByteBuf var1) {
         return var1.readByte();
      }

      public void encode(ByteBuf var1, Byte var2) {
         var1.writeByte(var2);
      }

      // $FF: synthetic method
      public void encode(final Object var1, final Object var2) {
         this.encode((ByteBuf)var1, (Byte)var2);
      }

      // $FF: synthetic method
      public Object decode(final Object var1) {
         return this.decode((ByteBuf)var1);
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

      // $FF: synthetic method
      public void encode(final Object var1, final Object var2) {
         this.encode((ByteBuf)var1, (Short)var2);
      }

      // $FF: synthetic method
      public Object decode(final Object var1) {
         return this.decode((ByteBuf)var1);
      }
   };
   StreamCodec<ByteBuf, Integer> UNSIGNED_SHORT = new StreamCodec<ByteBuf, Integer>() {
      public Integer decode(ByteBuf var1) {
         return var1.readUnsignedShort();
      }

      public void encode(ByteBuf var1, Integer var2) {
         var1.writeShort(var2);
      }

      // $FF: synthetic method
      public void encode(final Object var1, final Object var2) {
         this.encode((ByteBuf)var1, (Integer)var2);
      }

      // $FF: synthetic method
      public Object decode(final Object var1) {
         return this.decode((ByteBuf)var1);
      }
   };
   StreamCodec<ByteBuf, Integer> INT = new StreamCodec<ByteBuf, Integer>() {
      public Integer decode(ByteBuf var1) {
         return var1.readInt();
      }

      public void encode(ByteBuf var1, Integer var2) {
         var1.writeInt(var2);
      }

      // $FF: synthetic method
      public void encode(final Object var1, final Object var2) {
         this.encode((ByteBuf)var1, (Integer)var2);
      }

      // $FF: synthetic method
      public Object decode(final Object var1) {
         return this.decode((ByteBuf)var1);
      }
   };
   StreamCodec<ByteBuf, Integer> VAR_INT = new StreamCodec<ByteBuf, Integer>() {
      public Integer decode(ByteBuf var1) {
         return VarInt.read(var1);
      }

      public void encode(ByteBuf var1, Integer var2) {
         VarInt.write(var1, var2);
      }

      // $FF: synthetic method
      public void encode(final Object var1, final Object var2) {
         this.encode((ByteBuf)var1, (Integer)var2);
      }

      // $FF: synthetic method
      public Object decode(final Object var1) {
         return this.decode((ByteBuf)var1);
      }
   };
   StreamCodec<ByteBuf, OptionalInt> OPTIONAL_VAR_INT = VAR_INT.map((var0) -> var0 == 0 ? OptionalInt.empty() : OptionalInt.of(var0 - 1), (var0) -> var0.isPresent() ? var0.getAsInt() + 1 : 0);
   StreamCodec<ByteBuf, Long> LONG = new StreamCodec<ByteBuf, Long>() {
      public Long decode(ByteBuf var1) {
         return var1.readLong();
      }

      public void encode(ByteBuf var1, Long var2) {
         var1.writeLong(var2);
      }

      // $FF: synthetic method
      public void encode(final Object var1, final Object var2) {
         this.encode((ByteBuf)var1, (Long)var2);
      }

      // $FF: synthetic method
      public Object decode(final Object var1) {
         return this.decode((ByteBuf)var1);
      }
   };
   StreamCodec<ByteBuf, Long> VAR_LONG = new StreamCodec<ByteBuf, Long>() {
      public Long decode(ByteBuf var1) {
         return VarLong.read(var1);
      }

      public void encode(ByteBuf var1, Long var2) {
         VarLong.write(var1, var2);
      }

      // $FF: synthetic method
      public void encode(final Object var1, final Object var2) {
         this.encode((ByteBuf)var1, (Long)var2);
      }

      // $FF: synthetic method
      public Object decode(final Object var1) {
         return this.decode((ByteBuf)var1);
      }
   };
   StreamCodec<ByteBuf, Float> FLOAT = new StreamCodec<ByteBuf, Float>() {
      public Float decode(ByteBuf var1) {
         return var1.readFloat();
      }

      public void encode(ByteBuf var1, Float var2) {
         var1.writeFloat(var2);
      }

      // $FF: synthetic method
      public void encode(final Object var1, final Object var2) {
         this.encode((ByteBuf)var1, (Float)var2);
      }

      // $FF: synthetic method
      public Object decode(final Object var1) {
         return this.decode((ByteBuf)var1);
      }
   };
   StreamCodec<ByteBuf, Double> DOUBLE = new StreamCodec<ByteBuf, Double>() {
      public Double decode(ByteBuf var1) {
         return var1.readDouble();
      }

      public void encode(ByteBuf var1, Double var2) {
         var1.writeDouble(var2);
      }

      // $FF: synthetic method
      public void encode(final Object var1, final Object var2) {
         this.encode((ByteBuf)var1, (Double)var2);
      }

      // $FF: synthetic method
      public Object decode(final Object var1) {
         return this.decode((ByteBuf)var1);
      }
   };
   StreamCodec<ByteBuf, byte[]> BYTE_ARRAY = new StreamCodec<ByteBuf, byte[]>() {
      public byte[] decode(ByteBuf var1) {
         return FriendlyByteBuf.readByteArray(var1);
      }

      public void encode(ByteBuf var1, byte[] var2) {
         FriendlyByteBuf.writeByteArray(var1, var2);
      }

      // $FF: synthetic method
      public void encode(final Object var1, final Object var2) {
         this.encode((ByteBuf)var1, (byte[])var2);
      }

      // $FF: synthetic method
      public Object decode(final Object var1) {
         return this.decode((ByteBuf)var1);
      }
   };
   StreamCodec<ByteBuf, long[]> LONG_ARRAY = new StreamCodec<ByteBuf, long[]>() {
      public long[] decode(ByteBuf var1) {
         return FriendlyByteBuf.readLongArray(var1);
      }

      public void encode(ByteBuf var1, long[] var2) {
         FriendlyByteBuf.writeLongArray(var1, var2);
      }

      // $FF: synthetic method
      public void encode(final Object var1, final Object var2) {
         this.encode((ByteBuf)var1, (long[])var2);
      }

      // $FF: synthetic method
      public Object decode(final Object var1) {
         return this.decode((ByteBuf)var1);
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
         FriendlyByteBuf.writeNbt(var1, (Tag)var2.orElse((Object)null));
      }

      // $FF: synthetic method
      public void encode(final Object var1, final Object var2) {
         this.encode((ByteBuf)var1, (Optional)var2);
      }

      // $FF: synthetic method
      public Object decode(final Object var1) {
         return this.decode((ByteBuf)var1);
      }
   };
   StreamCodec<ByteBuf, Vector3fc> VECTOR3F = new StreamCodec<ByteBuf, Vector3fc>() {
      public Vector3fc decode(ByteBuf var1) {
         return FriendlyByteBuf.readVector3f(var1);
      }

      public void encode(ByteBuf var1, Vector3fc var2) {
         FriendlyByteBuf.writeVector3f(var1, var2);
      }

      // $FF: synthetic method
      public void encode(final Object var1, final Object var2) {
         this.encode((ByteBuf)var1, (Vector3fc)var2);
      }

      // $FF: synthetic method
      public Object decode(final Object var1) {
         return this.decode((ByteBuf)var1);
      }
   };
   StreamCodec<ByteBuf, Quaternionfc> QUATERNIONF = new StreamCodec<ByteBuf, Quaternionfc>() {
      public Quaternionfc decode(ByteBuf var1) {
         return FriendlyByteBuf.readQuaternion(var1);
      }

      public void encode(ByteBuf var1, Quaternionfc var2) {
         FriendlyByteBuf.writeQuaternion(var1, var2);
      }

      // $FF: synthetic method
      public void encode(final Object var1, final Object var2) {
         this.encode((ByteBuf)var1, (Quaternionfc)var2);
      }

      // $FF: synthetic method
      public Object decode(final Object var1) {
         return this.decode((ByteBuf)var1);
      }
   };
   StreamCodec<ByteBuf, Integer> CONTAINER_ID = new StreamCodec<ByteBuf, Integer>() {
      public Integer decode(ByteBuf var1) {
         return FriendlyByteBuf.readContainerId(var1);
      }

      public void encode(ByteBuf var1, Integer var2) {
         FriendlyByteBuf.writeContainerId(var1, var2);
      }

      // $FF: synthetic method
      public void encode(final Object var1, final Object var2) {
         this.encode((ByteBuf)var1, (Integer)var2);
      }

      // $FF: synthetic method
      public Object decode(final Object var1) {
         return this.decode((ByteBuf)var1);
      }
   };
   StreamCodec<ByteBuf, PropertyMap> GAME_PROFILE_PROPERTIES = new StreamCodec<ByteBuf, PropertyMap>() {
      private static final int MAX_PROPERTY_NAME_LENGTH = 64;
      private static final int MAX_PROPERTY_VALUE_LENGTH = 32767;
      private static final int MAX_PROPERTY_SIGNATURE_LENGTH = 1024;
      private static final int MAX_PROPERTIES = 16;

      public PropertyMap decode(ByteBuf var1) {
         int var2 = ByteBufCodecs.readCount(var1, 16);
         ImmutableMultimap.Builder var3 = ImmutableMultimap.builder();

         for(int var4 = 0; var4 < var2; ++var4) {
            String var5 = Utf8String.read(var1, 64);
            String var6 = Utf8String.read(var1, 32767);
            String var7 = (String)FriendlyByteBuf.readNullable(var1, (var0) -> Utf8String.read(var0, 1024));
            Property var8 = new Property(var5, var6, var7);
            var3.put(var8.name(), var8);
         }

         return new PropertyMap(var3.build());
      }

      public void encode(ByteBuf var1, PropertyMap var2) {
         ByteBufCodecs.writeCount(var1, var2.size(), 16);

         for(Property var4 : var2.values()) {
            Utf8String.write(var1, var4.name(), 64);
            Utf8String.write(var1, var4.value(), 32767);
            FriendlyByteBuf.writeNullable(var1, var4.signature(), (var0, var1x) -> Utf8String.write(var0, var1x, 1024));
         }

      }

      // $FF: synthetic method
      public void encode(final Object var1, final Object var2) {
         this.encode((ByteBuf)var1, (PropertyMap)var2);
      }

      // $FF: synthetic method
      public Object decode(final Object var1) {
         return this.decode((ByteBuf)var1);
      }
   };
   StreamCodec<ByteBuf, String> PLAYER_NAME = stringUtf8(16);
   StreamCodec<ByteBuf, GameProfile> GAME_PROFILE = StreamCodec.composite(UUIDUtil.STREAM_CODEC, GameProfile::id, PLAYER_NAME, GameProfile::name, GAME_PROFILE_PROPERTIES, GameProfile::properties, GameProfile::new);
   StreamCodec<ByteBuf, Integer> RGB_COLOR = new StreamCodec<ByteBuf, Integer>() {
      public Integer decode(ByteBuf var1) {
         return ARGB.color(var1.readByte() & 255, var1.readByte() & 255, var1.readByte() & 255);
      }

      public void encode(ByteBuf var1, Integer var2) {
         var1.writeByte(ARGB.red(var2));
         var1.writeByte(ARGB.green(var2));
         var1.writeByte(ARGB.blue(var2));
      }

      // $FF: synthetic method
      public void encode(final Object var1, final Object var2) {
         this.encode((ByteBuf)var1, (Integer)var2);
      }

      // $FF: synthetic method
      public Object decode(final Object var1) {
         return this.decode((ByteBuf)var1);
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

         // $FF: synthetic method
         public void encode(final Object var1, final Object var2) {
            this.encode((ByteBuf)var1, (byte[])var2);
         }

         // $FF: synthetic method
         public Object decode(final Object var1) {
            return this.decode((ByteBuf)var1);
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

         // $FF: synthetic method
         public void encode(final Object var1, final Object var2) {
            this.encode((ByteBuf)var1, (String)var2);
         }

         // $FF: synthetic method
         public Object decode(final Object var1) {
            return this.decode((ByteBuf)var1);
         }
      };
   }

   static StreamCodec<ByteBuf, Optional<Tag>> optionalTagCodec(final Supplier<NbtAccounter> var0) {
      return new StreamCodec<ByteBuf, Optional<Tag>>() {
         public Optional<Tag> decode(ByteBuf var1) {
            return Optional.ofNullable(FriendlyByteBuf.readNbt(var1, (NbtAccounter)var0.get()));
         }

         public void encode(ByteBuf var1, Optional<Tag> var2) {
            FriendlyByteBuf.writeNbt(var1, (Tag)var2.orElse((Object)null));
         }

         // $FF: synthetic method
         public void encode(final Object var1, final Object var2) {
            this.encode((ByteBuf)var1, (Optional)var2);
         }

         // $FF: synthetic method
         public Object decode(final Object var1) {
            return this.decode((ByteBuf)var1);
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

         // $FF: synthetic method
         public void encode(final Object var1, final Object var2) {
            this.encode((ByteBuf)var1, (Tag)var2);
         }

         // $FF: synthetic method
         public Object decode(final Object var1) {
            return this.decode((ByteBuf)var1);
         }
      };
   }

   static StreamCodec<ByteBuf, CompoundTag> compoundTagCodec(Supplier<NbtAccounter> var0) {
      return tagCodec(var0).map((var0x) -> {
         if (var0x instanceof CompoundTag var1) {
            return var1;
         } else {
            throw new DecoderException("Not a compound tag: " + String.valueOf(var0x));
         }
      }, (var0x) -> var0x);
   }

   static <T> StreamCodec<ByteBuf, T> fromCodecTrusted(Codec<T> var0) {
      return fromCodec(var0, NbtAccounter::unlimitedHeap);
   }

   static <T> StreamCodec<ByteBuf, T> fromCodec(Codec<T> var0) {
      return fromCodec(var0, (Supplier)(() -> NbtAccounter.create(2097152L)));
   }

   static <T, B extends ByteBuf, V> StreamCodec.CodecOperation<B, T, V> fromCodec(DynamicOps<T> var0, Codec<V> var1) {
      return (var2) -> new StreamCodec<B, V>() {
            public V decode(B var1x) {
               Object var2x = var2.decode(var1x);
               return (V)var1.parse(var0, var2x).getOrThrow((var1xx) -> new DecoderException("Failed to decode: " + var1xx + " " + String.valueOf(var2x)));
            }

            public void encode(B var1x, V var2x) {
               Object var3 = var1.encodeStart(var0, var2x).getOrThrow((var1xx) -> new EncoderException("Failed to encode: " + var1xx + " " + String.valueOf(var2x)));
               var2.encode(var1x, var3);
            }

            // $FF: synthetic method
            public void encode(final Object var1x, final Object var2x) {
               this.encode((ByteBuf)var1x, var2x);
            }

            // $FF: synthetic method
            public Object decode(final Object var1x) {
               return this.decode((ByteBuf)var1x);
            }
         };
   }

   static <T> StreamCodec<ByteBuf, T> fromCodec(Codec<T> var0, Supplier<NbtAccounter> var1) {
      return tagCodec(var1).apply(fromCodec(NbtOps.INSTANCE, var0));
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
            return (T)var0.parse(var3, var2x).getOrThrow((var1x) -> new DecoderException("Failed to decode: " + var1x + " " + String.valueOf(var2x)));
         }

         public void encode(RegistryFriendlyByteBuf var1, T var2x) {
            RegistryOps var3 = var1.registryAccess().createSerializationContext(NbtOps.INSTANCE);
            Tag var4 = (Tag)var0.encodeStart(var3, var2x).getOrThrow((var1x) -> new EncoderException("Failed to encode: " + var1x + " " + String.valueOf(var2x)));
            var2.encode(var1, var4);
         }

         // $FF: synthetic method
         public void encode(final Object var1, final Object var2x) {
            this.encode((RegistryFriendlyByteBuf)var1, var2x);
         }

         // $FF: synthetic method
         public Object decode(final Object var1) {
            return this.decode((RegistryFriendlyByteBuf)var1);
         }
      };
   }

   static <B extends ByteBuf, V> StreamCodec<B, Optional<V>> optional(final StreamCodec<? super B, V> var0) {
      return new StreamCodec<B, Optional<V>>() {
         public Optional<V> decode(B var1) {
            return var1.readBoolean() ? Optional.of(var0.decode(var1)) : Optional.empty();
         }

         public void encode(B var1, Optional<V> var2) {
            if (var2.isPresent()) {
               var1.writeBoolean(true);
               var0.encode(var1, var2.get());
            } else {
               var1.writeBoolean(false);
            }

         }

         // $FF: synthetic method
         public void encode(final Object var1, final Object var2) {
            this.encode((ByteBuf)var1, (Optional)var2);
         }

         // $FF: synthetic method
         public Object decode(final Object var1) {
            return this.decode((ByteBuf)var1);
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

   static <B extends ByteBuf, V, C extends Collection<V>> StreamCodec<B, C> collection(final IntFunction<C> var0, final StreamCodec<? super B, V> var1, final int var2) {
      return new StreamCodec<B, C>() {
         public C decode(B var1x) {
            int var2x = ByteBufCodecs.readCount(var1x, var2);
            Collection var3 = (Collection)var0.apply(Math.min(var2x, 65536));

            for(int var4 = 0; var4 < var2x; ++var4) {
               var3.add(var1.decode(var1x));
            }

            return (C)var3;
         }

         public void encode(B var1x, C var2x) {
            ByteBufCodecs.writeCount(var1x, var2x.size(), var2);

            for(Object var4 : var2x) {
               var1.encode(var1x, var4);
            }

         }

         // $FF: synthetic method
         public void encode(final Object var1x, final Object var2x) {
            this.encode((ByteBuf)var1x, (Collection)var2x);
         }

         // $FF: synthetic method
         public Object decode(final Object var1x) {
            return this.decode((ByteBuf)var1x);
         }
      };
   }

   static <B extends ByteBuf, V, C extends Collection<V>> StreamCodec.CodecOperation<B, V, C> collection(IntFunction<C> var0) {
      return (var1) -> collection(var0, var1);
   }

   static <B extends ByteBuf, V> StreamCodec.CodecOperation<B, V, List<V>> list() {
      return (var0) -> collection(ArrayList::new, var0);
   }

   static <B extends ByteBuf, V> StreamCodec.CodecOperation<B, V, List<V>> list(int var0) {
      return (var1) -> collection(ArrayList::new, var1, var0);
   }

   static <B extends ByteBuf, K, V, M extends Map<K, V>> StreamCodec<B, M> map(IntFunction<? extends M> var0, StreamCodec<? super B, K> var1, StreamCodec<? super B, V> var2) {
      return map(var0, var1, var2, 2147483647);
   }

   static <B extends ByteBuf, K, V, M extends Map<K, V>> StreamCodec<B, M> map(final IntFunction<? extends M> var0, final StreamCodec<? super B, K> var1, final StreamCodec<? super B, V> var2, final int var3) {
      return new StreamCodec<B, M>() {
         public void encode(B var1x, M var2x) {
            ByteBufCodecs.writeCount(var1x, var2x.size(), var3);
            var2x.forEach((var3x, var4) -> {
               var1.encode(var1x, var3x);
               var2.encode(var1x, var4);
            });
         }

         public M decode(B var1x) {
            int var2x = ByteBufCodecs.readCount(var1x, var3);
            Map var3x = (Map)var0.apply(Math.min(var2x, 65536));

            for(int var4 = 0; var4 < var2x; ++var4) {
               Object var5 = var1.decode(var1x);
               Object var6 = var2.decode(var1x);
               var3x.put(var5, var6);
            }

            return (M)var3x;
         }

         // $FF: synthetic method
         public void encode(final Object var1x, final Object var2x) {
            this.encode((ByteBuf)var1x, (Map)var2x);
         }

         // $FF: synthetic method
         public Object decode(final Object var1x) {
            return this.decode((ByteBuf)var1x);
         }
      };
   }

   static <B extends ByteBuf, L, R> StreamCodec<B, Either<L, R>> either(final StreamCodec<? super B, L> var0, final StreamCodec<? super B, R> var1) {
      return new StreamCodec<B, Either<L, R>>() {
         public Either<L, R> decode(B var1x) {
            return var1x.readBoolean() ? Either.left(var0.decode(var1x)) : Either.right(var1.decode(var1x));
         }

         public void encode(B var1x, Either<L, R> var2) {
            var2.ifLeft((var2x) -> {
               var1x.writeBoolean(true);
               var0.encode(var1x, var2x);
            }).ifRight((var2x) -> {
               var1x.writeBoolean(false);
               var1.encode(var1x, var2x);
            });
         }

         // $FF: synthetic method
         public void encode(final Object var1x, final Object var2) {
            this.encode((ByteBuf)var1x, (Either)var2);
         }

         // $FF: synthetic method
         public Object decode(final Object var1x) {
            return this.decode((ByteBuf)var1x);
         }
      };
   }

   static <B extends ByteBuf, V> StreamCodec.CodecOperation<B, V, V> lengthPrefixed(int var0, BiFunction<B, ByteBuf, B> var1) {
      return (var2) -> new StreamCodec<B, V>() {
            public V decode(B var1x) {
               int var2x = VarInt.read(var1x);
               if (var2x > var0) {
                  throw new DecoderException("Buffer size " + var2x + " is larger than allowed limit of " + var0);
               } else {
                  int var3 = var1x.readerIndex();
                  ByteBuf var4 = (ByteBuf)var1.apply(var1x, var1x.slice(var3, var2x));
                  var1x.readerIndex(var3 + var2x);
                  return (V)var2.decode(var4);
               }
            }

            public void encode(B var1x, V var2x) {
               ByteBuf var3 = (ByteBuf)var1.apply(var1x, var1x.alloc().buffer());

               try {
                  var2.encode(var3, var2x);
                  int var4 = var3.readableBytes();
                  if (var4 > var0) {
                     throw new EncoderException("Buffer size " + var4 + " is  larger than allowed limit of " + var0);
                  }

                  VarInt.write(var1x, var4);
                  var1x.writeBytes(var3);
               } finally {
                  var3.release();
               }

            }

            // $FF: synthetic method
            public void encode(final Object var1x, final Object var2x) {
               this.encode((ByteBuf)var1x, var2x);
            }

            // $FF: synthetic method
            public Object decode(final Object var1x) {
               return this.decode((ByteBuf)var1x);
            }
         };
   }

   static <V> StreamCodec.CodecOperation<ByteBuf, V, V> lengthPrefixed(int var0) {
      return lengthPrefixed(var0, (var0x, var1) -> var1);
   }

   static <V> StreamCodec.CodecOperation<RegistryFriendlyByteBuf, V, V> registryFriendlyLengthPrefixed(int var0) {
      return lengthPrefixed(var0, (var0x, var1) -> new RegistryFriendlyByteBuf(var1, var0x.registryAccess()));
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

         // $FF: synthetic method
         public void encode(final Object var1x, final Object var2) {
            this.encode((ByteBuf)var1x, var2);
         }

         // $FF: synthetic method
         public Object decode(final Object var1x) {
            return this.decode((ByteBuf)var1x);
         }
      };
   }

   static <T> StreamCodec<ByteBuf, T> idMapper(IdMap<T> var0) {
      Objects.requireNonNull(var0);
      IntFunction var10000 = var0::byIdOrThrow;
      Objects.requireNonNull(var0);
      return idMapper(var10000, var0::getIdOrThrow);
   }

   private static <T, R> StreamCodec<RegistryFriendlyByteBuf, R> registry(final ResourceKey<? extends Registry<T>> var0, final Function<Registry<T>, IdMap<R>> var1) {
      return new StreamCodec<RegistryFriendlyByteBuf, R>() {
         private IdMap<R> getRegistryOrThrow(RegistryFriendlyByteBuf var1x) {
            return (IdMap)var1.apply(var1x.registryAccess().lookupOrThrow(var0));
         }

         public R decode(RegistryFriendlyByteBuf var1x) {
            int var2 = VarInt.read(var1x);
            return (R)this.getRegistryOrThrow(var1x).byIdOrThrow(var2);
         }

         public void encode(RegistryFriendlyByteBuf var1x, R var2) {
            int var3 = this.getRegistryOrThrow(var1x).getIdOrThrow(var2);
            VarInt.write(var1x, var3);
         }

         // $FF: synthetic method
         public void encode(final Object var1x, final Object var2) {
            this.encode((RegistryFriendlyByteBuf)var1x, var2);
         }

         // $FF: synthetic method
         public Object decode(final Object var1x) {
            return this.decode((RegistryFriendlyByteBuf)var1x);
         }
      };
   }

   static <T> StreamCodec<RegistryFriendlyByteBuf, T> registry(ResourceKey<? extends Registry<T>> var0) {
      return registry(var0, (var0x) -> var0x);
   }

   static <T> StreamCodec<RegistryFriendlyByteBuf, Holder<T>> holderRegistry(ResourceKey<? extends Registry<T>> var0) {
      return registry(var0, Registry::asHolderIdMap);
   }

   static <T> StreamCodec<RegistryFriendlyByteBuf, Holder<T>> holder(final ResourceKey<? extends Registry<T>> var0, final StreamCodec<? super RegistryFriendlyByteBuf, T> var1) {
      return new StreamCodec<RegistryFriendlyByteBuf, Holder<T>>() {
         private static final int DIRECT_HOLDER_ID = 0;

         private IdMap<Holder<T>> getRegistryOrThrow(RegistryFriendlyByteBuf var1x) {
            return var1x.registryAccess().lookupOrThrow(var0).asHolderIdMap();
         }

         public Holder<T> decode(RegistryFriendlyByteBuf var1x) {
            int var2 = VarInt.read(var1x);
            return var2 == 0 ? Holder.direct(var1.decode(var1x)) : (Holder)this.getRegistryOrThrow(var1x).byIdOrThrow(var2 - 1);
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

         // $FF: synthetic method
         public void encode(final Object var1x, final Object var2) {
            this.encode((RegistryFriendlyByteBuf)var1x, (Holder)var2);
         }

         // $FF: synthetic method
         public Object decode(final Object var1x) {
            return this.decode((RegistryFriendlyByteBuf)var1x);
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
               return (HolderSet)var5.get(TagKey.create(var0, (ResourceLocation)ResourceLocation.STREAM_CODEC.decode(var1))).orElseThrow();
            } else {
               ArrayList var3 = new ArrayList(Math.min(var2, 65536));

               for(int var4 = 0; var4 < var2; ++var4) {
                  var3.add((Holder)this.holderCodec.decode(var1));
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

               for(Holder var5 : var2) {
                  this.holderCodec.encode(var1, var5);
               }
            }

         }

         // $FF: synthetic method
         public void encode(final Object var1, final Object var2) {
            this.encode((RegistryFriendlyByteBuf)var1, (HolderSet)var2);
         }

         // $FF: synthetic method
         public Object decode(final Object var1) {
            return this.decode((RegistryFriendlyByteBuf)var1);
         }
      };
   }

   static StreamCodec<ByteBuf, JsonElement> lenientJson(final int var0) {
      return new StreamCodec<ByteBuf, JsonElement>() {
         private static final Gson GSON = (new GsonBuilder()).disableHtmlEscaping().create();

         public JsonElement decode(ByteBuf var1) {
            String var2 = Utf8String.read(var1, var0);

            try {
               return LenientJsonParser.parse(var2);
            } catch (JsonSyntaxException var4) {
               throw new DecoderException("Failed to parse JSON", var4);
            }
         }

         public void encode(ByteBuf var1, JsonElement var2) {
            String var3 = GSON.toJson(var2);
            Utf8String.write(var1, var3, var0);
         }

         // $FF: synthetic method
         public void encode(final Object var1, final Object var2) {
            this.encode((ByteBuf)var1, (JsonElement)var2);
         }

         // $FF: synthetic method
         public Object decode(final Object var1) {
            return this.decode((ByteBuf)var1);
         }
      };
   }
}
