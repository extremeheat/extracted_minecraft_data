package net.minecraft.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class FriendlyByteBuf extends ByteBuf {
   public static final int DEFAULT_NBT_QUOTA = 2097152;
   private final ByteBuf source;
   public static final short MAX_STRING_LENGTH = 32767;
   public static final int MAX_COMPONENT_STRING_LENGTH = 262144;
   private static final int PUBLIC_KEY_SIZE = 256;
   private static final int MAX_PUBLIC_KEY_HEADER_SIZE = 256;
   private static final int MAX_PUBLIC_KEY_LENGTH = 512;
   private static final Gson GSON = new Gson();

   public FriendlyByteBuf(ByteBuf var1) {
      super();
      this.source = var1;
   }

   @Deprecated
   public <T> T readWithCodecTrusted(DynamicOps<Tag> var1, Codec<T> var2) {
      return this.readWithCodec(var1, var2, NbtAccounter.unlimitedHeap());
   }

   @Deprecated
   public <T> T readWithCodec(DynamicOps<Tag> var1, Codec<T> var2, NbtAccounter var3) {
      Tag var4 = this.readNbt(var3);
      return Util.getOrThrow(var2.parse(var1, var4), var1x -> new DecoderException("Failed to decode: " + var1x + " " + var4));
   }

   @Deprecated
   public <T> FriendlyByteBuf writeWithCodec(DynamicOps<Tag> var1, Codec<T> var2, T var3) {
      Tag var4 = Util.getOrThrow(var2.encodeStart(var1, var3), var1x -> new EncoderException("Failed to encode: " + var1x + " " + var3));
      this.writeNbt(var4);
      return this;
   }

   public <T> T readJsonWithCodec(Codec<T> var1) {
      JsonElement var2 = GsonHelper.fromJson(GSON, this.readUtf(), JsonElement.class);
      DataResult var3 = var1.parse(JsonOps.INSTANCE, var2);
      return Util.getOrThrow(var3, var0 -> new DecoderException("Failed to decode json: " + var0));
   }

   public <T> void writeJsonWithCodec(Codec<T> var1, T var2) {
      DataResult var3 = var1.encodeStart(JsonOps.INSTANCE, var2);
      this.writeUtf(GSON.toJson(Util.getOrThrow(var3, var1x -> new EncoderException("Failed to encode: " + var1x + " " + var2))));
   }

   public static <T> IntFunction<T> limitValue(IntFunction<T> var0, int var1) {
      return var2 -> {
         if (var2 > var1) {
            throw new DecoderException("Value " + var2 + " is larger than limit " + var1);
         } else {
            return (T)var0.apply(var2);
         }
      };
   }

   public <T, C extends Collection<T>> C readCollection(IntFunction<C> var1, StreamDecoder<? super FriendlyByteBuf, T> var2) {
      int var3 = this.readVarInt();
      Collection var4 = (Collection)var1.apply(var3);

      for(int var5 = 0; var5 < var3; ++var5) {
         var4.add(var2.decode(this));
      }

      return (C)var4;
   }

   public <T> void writeCollection(Collection<T> var1, StreamEncoder<? super FriendlyByteBuf, T> var2) {
      this.writeVarInt(var1.size());

      for(Object var4 : var1) {
         var2.encode(this, var4);
      }
   }

   public <T> List<T> readList(StreamDecoder<? super FriendlyByteBuf, T> var1) {
      return this.readCollection(Lists::newArrayListWithCapacity, var1);
   }

   public IntList readIntIdList() {
      int var1 = this.readVarInt();
      IntArrayList var2 = new IntArrayList();

      for(int var3 = 0; var3 < var1; ++var3) {
         var2.add(this.readVarInt());
      }

      return var2;
   }

   public void writeIntIdList(IntList var1) {
      this.writeVarInt(var1.size());
      var1.forEach(this::writeVarInt);
   }

   public <K, V, M extends Map<K, V>> M readMap(
      IntFunction<M> var1, StreamDecoder<? super FriendlyByteBuf, K> var2, StreamDecoder<? super FriendlyByteBuf, V> var3
   ) {
      int var4 = this.readVarInt();
      Map var5 = (Map)var1.apply(var4);

      for(int var6 = 0; var6 < var4; ++var6) {
         Object var7 = var2.decode(this);
         Object var8 = var3.decode(this);
         var5.put(var7, var8);
      }

      return (M)var5;
   }

   public <K, V> Map<K, V> readMap(StreamDecoder<? super FriendlyByteBuf, K> var1, StreamDecoder<? super FriendlyByteBuf, V> var2) {
      return this.readMap(Maps::newHashMapWithExpectedSize, var1, var2);
   }

   public <K, V> void writeMap(Map<K, V> var1, StreamEncoder<? super FriendlyByteBuf, K> var2, StreamEncoder<? super FriendlyByteBuf, V> var3) {
      this.writeVarInt(var1.size());
      var1.forEach((var3x, var4) -> {
         var2.encode(this, var3x);
         var3.encode(this, var4);
      });
   }

   public void readWithCount(Consumer<FriendlyByteBuf> var1) {
      int var2 = this.readVarInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         var1.accept(this);
      }
   }

   public <E extends Enum<E>> void writeEnumSet(EnumSet<E> var1, Class<E> var2) {
      Enum[] var3 = (Enum[])var2.getEnumConstants();
      BitSet var4 = new BitSet(var3.length);

      for(int var5 = 0; var5 < var3.length; ++var5) {
         var4.set(var5, var1.contains(var3[var5]));
      }

      this.writeFixedBitSet(var4, var3.length);
   }

   public <E extends Enum<E>> EnumSet<E> readEnumSet(Class<E> var1) {
      Enum[] var2 = (Enum[])var1.getEnumConstants();
      BitSet var3 = this.readFixedBitSet(var2.length);
      EnumSet var4 = EnumSet.noneOf(var1);

      for(int var5 = 0; var5 < var2.length; ++var5) {
         if (var3.get(var5)) {
            var4.add((E)var2[var5]);
         }
      }

      return var4;
   }

   public <T> void writeOptional(Optional<T> var1, StreamEncoder<? super FriendlyByteBuf, T> var2) {
      if (var1.isPresent()) {
         this.writeBoolean(true);
         var2.encode(this, var1.get());
      } else {
         this.writeBoolean(false);
      }
   }

   public <T> Optional<T> readOptional(StreamDecoder<? super FriendlyByteBuf, T> var1) {
      return this.readBoolean() ? Optional.of((T)var1.decode(this)) : Optional.empty();
   }

   @Nullable
   public <T> T readNullable(StreamDecoder<? super FriendlyByteBuf, T> var1) {
      return readNullable(this, var1);
   }

   @Nullable
   public static <T, B extends ByteBuf> T readNullable(B var0, StreamDecoder<? super B, T> var1) {
      return (T)(var0.readBoolean() ? var1.decode(var0) : null);
   }

   public <T> void writeNullable(@Nullable T var1, StreamEncoder<? super FriendlyByteBuf, T> var2) {
      writeNullable(this, var1, var2);
   }

   public static <T, B extends ByteBuf> void writeNullable(B var0, @Nullable T var1, StreamEncoder<? super B, T> var2) {
      if (var1 != null) {
         var0.writeBoolean(true);
         var2.encode(var0, var1);
      } else {
         var0.writeBoolean(false);
      }
   }

   public byte[] readByteArray() {
      return readByteArray(this);
   }

   public static byte[] readByteArray(ByteBuf var0) {
      return readByteArray(var0, var0.readableBytes());
   }

   public FriendlyByteBuf writeByteArray(byte[] var1) {
      writeByteArray(this, var1);
      return this;
   }

   public static void writeByteArray(ByteBuf var0, byte[] var1) {
      VarInt.write(var0, var1.length);
      var0.writeBytes(var1);
   }

   public byte[] readByteArray(int var1) {
      return readByteArray(this, var1);
   }

   public static byte[] readByteArray(ByteBuf var0, int var1) {
      int var2 = VarInt.read(var0);
      if (var2 > var1) {
         throw new DecoderException("ByteArray with size " + var2 + " is bigger than allowed " + var1);
      } else {
         byte[] var3 = new byte[var2];
         var0.readBytes(var3);
         return var3;
      }
   }

   public FriendlyByteBuf writeVarIntArray(int[] var1) {
      this.writeVarInt(var1.length);

      for(int var5 : var1) {
         this.writeVarInt(var5);
      }

      return this;
   }

   public int[] readVarIntArray() {
      return this.readVarIntArray(this.readableBytes());
   }

   public int[] readVarIntArray(int var1) {
      int var2 = this.readVarInt();
      if (var2 > var1) {
         throw new DecoderException("VarIntArray with size " + var2 + " is bigger than allowed " + var1);
      } else {
         int[] var3 = new int[var2];

         for(int var4 = 0; var4 < var3.length; ++var4) {
            var3[var4] = this.readVarInt();
         }

         return var3;
      }
   }

   public FriendlyByteBuf writeLongArray(long[] var1) {
      this.writeVarInt(var1.length);

      for(long var5 : var1) {
         this.writeLong(var5);
      }

      return this;
   }

   public long[] readLongArray() {
      return this.readLongArray(null);
   }

   public long[] readLongArray(@Nullable long[] var1) {
      return this.readLongArray(var1, this.readableBytes() / 8);
   }

   public long[] readLongArray(@Nullable long[] var1, int var2) {
      int var3 = this.readVarInt();
      if (var1 == null || var1.length != var3) {
         if (var3 > var2) {
            throw new DecoderException("LongArray with size " + var3 + " is bigger than allowed " + var2);
         }

         var1 = new long[var3];
      }

      for(int var4 = 0; var4 < var1.length; ++var4) {
         var1[var4] = this.readLong();
      }

      return var1;
   }

   public BlockPos readBlockPos() {
      return readBlockPos(this);
   }

   public static BlockPos readBlockPos(ByteBuf var0) {
      return BlockPos.of(var0.readLong());
   }

   public FriendlyByteBuf writeBlockPos(BlockPos var1) {
      writeBlockPos(this, var1);
      return this;
   }

   public static void writeBlockPos(ByteBuf var0, BlockPos var1) {
      var0.writeLong(var1.asLong());
   }

   public ChunkPos readChunkPos() {
      return new ChunkPos(this.readLong());
   }

   public FriendlyByteBuf writeChunkPos(ChunkPos var1) {
      this.writeLong(var1.toLong());
      return this;
   }

   public SectionPos readSectionPos() {
      return SectionPos.of(this.readLong());
   }

   public FriendlyByteBuf writeSectionPos(SectionPos var1) {
      this.writeLong(var1.asLong());
      return this;
   }

   public GlobalPos readGlobalPos() {
      ResourceKey var1 = this.readResourceKey(Registries.DIMENSION);
      BlockPos var2 = this.readBlockPos();
      return GlobalPos.of(var1, var2);
   }

   public void writeGlobalPos(GlobalPos var1) {
      this.writeResourceKey(var1.dimension());
      this.writeBlockPos(var1.pos());
   }

   public Vector3f readVector3f() {
      return readVector3f(this);
   }

   public static Vector3f readVector3f(ByteBuf var0) {
      return new Vector3f(var0.readFloat(), var0.readFloat(), var0.readFloat());
   }

   public void writeVector3f(Vector3f var1) {
      writeVector3f(this, var1);
   }

   public static void writeVector3f(ByteBuf var0, Vector3f var1) {
      var0.writeFloat(var1.x());
      var0.writeFloat(var1.y());
      var0.writeFloat(var1.z());
   }

   public Quaternionf readQuaternion() {
      return readQuaternion(this);
   }

   public static Quaternionf readQuaternion(ByteBuf var0) {
      return new Quaternionf(var0.readFloat(), var0.readFloat(), var0.readFloat(), var0.readFloat());
   }

   public void writeQuaternion(Quaternionf var1) {
      writeQuaternion(this, var1);
   }

   public static void writeQuaternion(ByteBuf var0, Quaternionf var1) {
      var0.writeFloat(var1.x);
      var0.writeFloat(var1.y);
      var0.writeFloat(var1.z);
      var0.writeFloat(var1.w);
   }

   public Vec3 readVec3() {
      return new Vec3(this.readDouble(), this.readDouble(), this.readDouble());
   }

   public void writeVec3(Vec3 var1) {
      this.writeDouble(var1.x());
      this.writeDouble(var1.y());
      this.writeDouble(var1.z());
   }

   public <T extends Enum<T>> T readEnum(Class<T> var1) {
      return (T)var1.getEnumConstants()[this.readVarInt()];
   }

   public FriendlyByteBuf writeEnum(Enum<?> var1) {
      return this.writeVarInt(var1.ordinal());
   }

   public <T> T readById(IntFunction<T> var1) {
      int var2 = this.readVarInt();
      return (T)var1.apply(var2);
   }

   public <T> FriendlyByteBuf writeById(ToIntFunction<T> var1, T var2) {
      int var3 = var1.applyAsInt(var2);
      return this.writeVarInt(var3);
   }

   public int readVarInt() {
      return VarInt.read(this.source);
   }

   public long readVarLong() {
      return VarLong.read(this.source);
   }

   public FriendlyByteBuf writeUUID(UUID var1) {
      writeUUID(this, var1);
      return this;
   }

   public static void writeUUID(ByteBuf var0, UUID var1) {
      var0.writeLong(var1.getMostSignificantBits());
      var0.writeLong(var1.getLeastSignificantBits());
   }

   public UUID readUUID() {
      return readUUID(this);
   }

   public static UUID readUUID(ByteBuf var0) {
      return new UUID(var0.readLong(), var0.readLong());
   }

   public FriendlyByteBuf writeVarInt(int var1) {
      VarInt.write(this.source, var1);
      return this;
   }

   public FriendlyByteBuf writeVarLong(long var1) {
      VarLong.write(this.source, var1);
      return this;
   }

   public FriendlyByteBuf writeNbt(@Nullable Tag var1) {
      writeNbt(this, var1);
      return this;
   }

   public static void writeNbt(ByteBuf var0, @Nullable Tag var1) {
      if (var1 == null) {
         var1 = EndTag.INSTANCE;
      }

      try {
         NbtIo.writeAnyTag((Tag)var1, new ByteBufOutputStream(var0));
      } catch (IOException var3) {
         throw new EncoderException(var3);
      }
   }

   @Nullable
   public CompoundTag readNbt() {
      return readNbt(this);
   }

   @Nullable
   public static CompoundTag readNbt(ByteBuf var0) {
      Tag var1 = readNbt(var0, NbtAccounter.create(2097152L));
      if (var1 != null && !(var1 instanceof CompoundTag)) {
         throw new DecoderException("Not a compound tag: " + var1);
      } else {
         return (CompoundTag)var1;
      }
   }

   @Nullable
   public static Tag readNbt(ByteBuf var0, NbtAccounter var1) {
      try {
         Tag var2 = NbtIo.readAnyTag(new ByteBufInputStream(var0), var1);
         return var2.getId() == 0 ? null : var2;
      } catch (IOException var3) {
         throw new EncoderException(var3);
      }
   }

   @Nullable
   public Tag readNbt(NbtAccounter var1) {
      return readNbt(this, var1);
   }

   public String readUtf() {
      return this.readUtf(32767);
   }

   public String readUtf(int var1) {
      return Utf8String.read(this.source, var1);
   }

   public FriendlyByteBuf writeUtf(String var1) {
      return this.writeUtf(var1, 32767);
   }

   public FriendlyByteBuf writeUtf(String var1, int var2) {
      Utf8String.write(this.source, var1, var2);
      return this;
   }

   public ResourceLocation readResourceLocation() {
      return new ResourceLocation(this.readUtf(32767));
   }

   public FriendlyByteBuf writeResourceLocation(ResourceLocation var1) {
      this.writeUtf(var1.toString());
      return this;
   }

   public <T> ResourceKey<T> readResourceKey(ResourceKey<? extends Registry<T>> var1) {
      ResourceLocation var2 = this.readResourceLocation();
      return ResourceKey.create(var1, var2);
   }

   public void writeResourceKey(ResourceKey<?> var1) {
      this.writeResourceLocation(var1.location());
   }

   public <T> ResourceKey<? extends Registry<T>> readRegistryKey() {
      ResourceLocation var1 = this.readResourceLocation();
      return ResourceKey.createRegistryKey(var1);
   }

   public Date readDate() {
      return new Date(this.readLong());
   }

   public FriendlyByteBuf writeDate(Date var1) {
      this.writeLong(var1.getTime());
      return this;
   }

   public Instant readInstant() {
      return Instant.ofEpochMilli(this.readLong());
   }

   public void writeInstant(Instant var1) {
      this.writeLong(var1.toEpochMilli());
   }

   public PublicKey readPublicKey() {
      try {
         return Crypt.byteToPublicKey(this.readByteArray(512));
      } catch (CryptException var2) {
         throw new DecoderException("Malformed public key bytes", var2);
      }
   }

   public FriendlyByteBuf writePublicKey(PublicKey var1) {
      this.writeByteArray(var1.getEncoded());
      return this;
   }

   public BlockHitResult readBlockHitResult() {
      BlockPos var1 = this.readBlockPos();
      Direction var2 = this.readEnum(Direction.class);
      float var3 = this.readFloat();
      float var4 = this.readFloat();
      float var5 = this.readFloat();
      boolean var6 = this.readBoolean();
      return new BlockHitResult(
         new Vec3((double)var1.getX() + (double)var3, (double)var1.getY() + (double)var4, (double)var1.getZ() + (double)var5), var2, var1, var6
      );
   }

   public void writeBlockHitResult(BlockHitResult var1) {
      BlockPos var2 = var1.getBlockPos();
      this.writeBlockPos(var2);
      this.writeEnum(var1.getDirection());
      Vec3 var3 = var1.getLocation();
      this.writeFloat((float)(var3.x - (double)var2.getX()));
      this.writeFloat((float)(var3.y - (double)var2.getY()));
      this.writeFloat((float)(var3.z - (double)var2.getZ()));
      this.writeBoolean(var1.isInside());
   }

   public BitSet readBitSet() {
      return BitSet.valueOf(this.readLongArray());
   }

   public void writeBitSet(BitSet var1) {
      this.writeLongArray(var1.toLongArray());
   }

   public BitSet readFixedBitSet(int var1) {
      byte[] var2 = new byte[Mth.positiveCeilDiv(var1, 8)];
      this.readBytes(var2);
      return BitSet.valueOf(var2);
   }

   public void writeFixedBitSet(BitSet var1, int var2) {
      if (var1.length() > var2) {
         throw new EncoderException("BitSet is larger than expected size (" + var1.length() + ">" + var2 + ")");
      } else {
         byte[] var3 = var1.toByteArray();
         this.writeBytes(Arrays.copyOf(var3, Mth.positiveCeilDiv(var2, 8)));
      }
   }

   public boolean isContiguous() {
      return this.source.isContiguous();
   }

   public int maxFastWritableBytes() {
      return this.source.maxFastWritableBytes();
   }

   public int capacity() {
      return this.source.capacity();
   }

   public FriendlyByteBuf capacity(int var1) {
      this.source.capacity(var1);
      return this;
   }

   public int maxCapacity() {
      return this.source.maxCapacity();
   }

   public ByteBufAllocator alloc() {
      return this.source.alloc();
   }

   public ByteOrder order() {
      return this.source.order();
   }

   public ByteBuf order(ByteOrder var1) {
      return this.source.order(var1);
   }

   public ByteBuf unwrap() {
      return this.source;
   }

   public boolean isDirect() {
      return this.source.isDirect();
   }

   public boolean isReadOnly() {
      return this.source.isReadOnly();
   }

   public ByteBuf asReadOnly() {
      return this.source.asReadOnly();
   }

   public int readerIndex() {
      return this.source.readerIndex();
   }

   public FriendlyByteBuf readerIndex(int var1) {
      this.source.readerIndex(var1);
      return this;
   }

   public int writerIndex() {
      return this.source.writerIndex();
   }

   public FriendlyByteBuf writerIndex(int var1) {
      this.source.writerIndex(var1);
      return this;
   }

   public FriendlyByteBuf setIndex(int var1, int var2) {
      this.source.setIndex(var1, var2);
      return this;
   }

   public int readableBytes() {
      return this.source.readableBytes();
   }

   public int writableBytes() {
      return this.source.writableBytes();
   }

   public int maxWritableBytes() {
      return this.source.maxWritableBytes();
   }

   public boolean isReadable() {
      return this.source.isReadable();
   }

   public boolean isReadable(int var1) {
      return this.source.isReadable(var1);
   }

   public boolean isWritable() {
      return this.source.isWritable();
   }

   public boolean isWritable(int var1) {
      return this.source.isWritable(var1);
   }

   public FriendlyByteBuf clear() {
      this.source.clear();
      return this;
   }

   public FriendlyByteBuf markReaderIndex() {
      this.source.markReaderIndex();
      return this;
   }

   public FriendlyByteBuf resetReaderIndex() {
      this.source.resetReaderIndex();
      return this;
   }

   public FriendlyByteBuf markWriterIndex() {
      this.source.markWriterIndex();
      return this;
   }

   public FriendlyByteBuf resetWriterIndex() {
      this.source.resetWriterIndex();
      return this;
   }

   public FriendlyByteBuf discardReadBytes() {
      this.source.discardReadBytes();
      return this;
   }

   public FriendlyByteBuf discardSomeReadBytes() {
      this.source.discardSomeReadBytes();
      return this;
   }

   public FriendlyByteBuf ensureWritable(int var1) {
      this.source.ensureWritable(var1);
      return this;
   }

   public int ensureWritable(int var1, boolean var2) {
      return this.source.ensureWritable(var1, var2);
   }

   public boolean getBoolean(int var1) {
      return this.source.getBoolean(var1);
   }

   public byte getByte(int var1) {
      return this.source.getByte(var1);
   }

   public short getUnsignedByte(int var1) {
      return this.source.getUnsignedByte(var1);
   }

   public short getShort(int var1) {
      return this.source.getShort(var1);
   }

   public short getShortLE(int var1) {
      return this.source.getShortLE(var1);
   }

   public int getUnsignedShort(int var1) {
      return this.source.getUnsignedShort(var1);
   }

   public int getUnsignedShortLE(int var1) {
      return this.source.getUnsignedShortLE(var1);
   }

   public int getMedium(int var1) {
      return this.source.getMedium(var1);
   }

   public int getMediumLE(int var1) {
      return this.source.getMediumLE(var1);
   }

   public int getUnsignedMedium(int var1) {
      return this.source.getUnsignedMedium(var1);
   }

   public int getUnsignedMediumLE(int var1) {
      return this.source.getUnsignedMediumLE(var1);
   }

   public int getInt(int var1) {
      return this.source.getInt(var1);
   }

   public int getIntLE(int var1) {
      return this.source.getIntLE(var1);
   }

   public long getUnsignedInt(int var1) {
      return this.source.getUnsignedInt(var1);
   }

   public long getUnsignedIntLE(int var1) {
      return this.source.getUnsignedIntLE(var1);
   }

   public long getLong(int var1) {
      return this.source.getLong(var1);
   }

   public long getLongLE(int var1) {
      return this.source.getLongLE(var1);
   }

   public char getChar(int var1) {
      return this.source.getChar(var1);
   }

   public float getFloat(int var1) {
      return this.source.getFloat(var1);
   }

   public double getDouble(int var1) {
      return this.source.getDouble(var1);
   }

   public FriendlyByteBuf getBytes(int var1, ByteBuf var2) {
      this.source.getBytes(var1, var2);
      return this;
   }

   public FriendlyByteBuf getBytes(int var1, ByteBuf var2, int var3) {
      this.source.getBytes(var1, var2, var3);
      return this;
   }

   public FriendlyByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.source.getBytes(var1, var2, var3, var4);
      return this;
   }

   public FriendlyByteBuf getBytes(int var1, byte[] var2) {
      this.source.getBytes(var1, var2);
      return this;
   }

   public FriendlyByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      this.source.getBytes(var1, var2, var3, var4);
      return this;
   }

   public FriendlyByteBuf getBytes(int var1, ByteBuffer var2) {
      this.source.getBytes(var1, var2);
      return this;
   }

   public FriendlyByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      this.source.getBytes(var1, var2, var3);
      return this;
   }

   public int getBytes(int var1, GatheringByteChannel var2, int var3) throws IOException {
      return this.source.getBytes(var1, var2, var3);
   }

   public int getBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      return this.source.getBytes(var1, var2, var3, var5);
   }

   public CharSequence getCharSequence(int var1, int var2, Charset var3) {
      return this.source.getCharSequence(var1, var2, var3);
   }

   public FriendlyByteBuf setBoolean(int var1, boolean var2) {
      this.source.setBoolean(var1, var2);
      return this;
   }

   public FriendlyByteBuf setByte(int var1, int var2) {
      this.source.setByte(var1, var2);
      return this;
   }

   public FriendlyByteBuf setShort(int var1, int var2) {
      this.source.setShort(var1, var2);
      return this;
   }

   public FriendlyByteBuf setShortLE(int var1, int var2) {
      this.source.setShortLE(var1, var2);
      return this;
   }

   public FriendlyByteBuf setMedium(int var1, int var2) {
      this.source.setMedium(var1, var2);
      return this;
   }

   public FriendlyByteBuf setMediumLE(int var1, int var2) {
      this.source.setMediumLE(var1, var2);
      return this;
   }

   public FriendlyByteBuf setInt(int var1, int var2) {
      this.source.setInt(var1, var2);
      return this;
   }

   public FriendlyByteBuf setIntLE(int var1, int var2) {
      this.source.setIntLE(var1, var2);
      return this;
   }

   public FriendlyByteBuf setLong(int var1, long var2) {
      this.source.setLong(var1, var2);
      return this;
   }

   public FriendlyByteBuf setLongLE(int var1, long var2) {
      this.source.setLongLE(var1, var2);
      return this;
   }

   public FriendlyByteBuf setChar(int var1, int var2) {
      this.source.setChar(var1, var2);
      return this;
   }

   public FriendlyByteBuf setFloat(int var1, float var2) {
      this.source.setFloat(var1, var2);
      return this;
   }

   public FriendlyByteBuf setDouble(int var1, double var2) {
      this.source.setDouble(var1, var2);
      return this;
   }

   public FriendlyByteBuf setBytes(int var1, ByteBuf var2) {
      this.source.setBytes(var1, var2);
      return this;
   }

   public FriendlyByteBuf setBytes(int var1, ByteBuf var2, int var3) {
      this.source.setBytes(var1, var2, var3);
      return this;
   }

   public FriendlyByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.source.setBytes(var1, var2, var3, var4);
      return this;
   }

   public FriendlyByteBuf setBytes(int var1, byte[] var2) {
      this.source.setBytes(var1, var2);
      return this;
   }

   public FriendlyByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      this.source.setBytes(var1, var2, var3, var4);
      return this;
   }

   public FriendlyByteBuf setBytes(int var1, ByteBuffer var2) {
      this.source.setBytes(var1, var2);
      return this;
   }

   public int setBytes(int var1, InputStream var2, int var3) throws IOException {
      return this.source.setBytes(var1, var2, var3);
   }

   public int setBytes(int var1, ScatteringByteChannel var2, int var3) throws IOException {
      return this.source.setBytes(var1, var2, var3);
   }

   public int setBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      return this.source.setBytes(var1, var2, var3, var5);
   }

   public FriendlyByteBuf setZero(int var1, int var2) {
      this.source.setZero(var1, var2);
      return this;
   }

   public int setCharSequence(int var1, CharSequence var2, Charset var3) {
      return this.source.setCharSequence(var1, var2, var3);
   }

   public boolean readBoolean() {
      return this.source.readBoolean();
   }

   public byte readByte() {
      return this.source.readByte();
   }

   public short readUnsignedByte() {
      return this.source.readUnsignedByte();
   }

   public short readShort() {
      return this.source.readShort();
   }

   public short readShortLE() {
      return this.source.readShortLE();
   }

   public int readUnsignedShort() {
      return this.source.readUnsignedShort();
   }

   public int readUnsignedShortLE() {
      return this.source.readUnsignedShortLE();
   }

   public int readMedium() {
      return this.source.readMedium();
   }

   public int readMediumLE() {
      return this.source.readMediumLE();
   }

   public int readUnsignedMedium() {
      return this.source.readUnsignedMedium();
   }

   public int readUnsignedMediumLE() {
      return this.source.readUnsignedMediumLE();
   }

   public int readInt() {
      return this.source.readInt();
   }

   public int readIntLE() {
      return this.source.readIntLE();
   }

   public long readUnsignedInt() {
      return this.source.readUnsignedInt();
   }

   public long readUnsignedIntLE() {
      return this.source.readUnsignedIntLE();
   }

   public long readLong() {
      return this.source.readLong();
   }

   public long readLongLE() {
      return this.source.readLongLE();
   }

   public char readChar() {
      return this.source.readChar();
   }

   public float readFloat() {
      return this.source.readFloat();
   }

   public double readDouble() {
      return this.source.readDouble();
   }

   public ByteBuf readBytes(int var1) {
      return this.source.readBytes(var1);
   }

   public ByteBuf readSlice(int var1) {
      return this.source.readSlice(var1);
   }

   public ByteBuf readRetainedSlice(int var1) {
      return this.source.readRetainedSlice(var1);
   }

   public FriendlyByteBuf readBytes(ByteBuf var1) {
      this.source.readBytes(var1);
      return this;
   }

   public FriendlyByteBuf readBytes(ByteBuf var1, int var2) {
      this.source.readBytes(var1, var2);
      return this;
   }

   public FriendlyByteBuf readBytes(ByteBuf var1, int var2, int var3) {
      this.source.readBytes(var1, var2, var3);
      return this;
   }

   public FriendlyByteBuf readBytes(byte[] var1) {
      this.source.readBytes(var1);
      return this;
   }

   public FriendlyByteBuf readBytes(byte[] var1, int var2, int var3) {
      this.source.readBytes(var1, var2, var3);
      return this;
   }

   public FriendlyByteBuf readBytes(ByteBuffer var1) {
      this.source.readBytes(var1);
      return this;
   }

   public FriendlyByteBuf readBytes(OutputStream var1, int var2) throws IOException {
      this.source.readBytes(var1, var2);
      return this;
   }

   public int readBytes(GatheringByteChannel var1, int var2) throws IOException {
      return this.source.readBytes(var1, var2);
   }

   public CharSequence readCharSequence(int var1, Charset var2) {
      return this.source.readCharSequence(var1, var2);
   }

   public int readBytes(FileChannel var1, long var2, int var4) throws IOException {
      return this.source.readBytes(var1, var2, var4);
   }

   public FriendlyByteBuf skipBytes(int var1) {
      this.source.skipBytes(var1);
      return this;
   }

   public FriendlyByteBuf writeBoolean(boolean var1) {
      this.source.writeBoolean(var1);
      return this;
   }

   public FriendlyByteBuf writeByte(int var1) {
      this.source.writeByte(var1);
      return this;
   }

   public FriendlyByteBuf writeShort(int var1) {
      this.source.writeShort(var1);
      return this;
   }

   public FriendlyByteBuf writeShortLE(int var1) {
      this.source.writeShortLE(var1);
      return this;
   }

   public FriendlyByteBuf writeMedium(int var1) {
      this.source.writeMedium(var1);
      return this;
   }

   public FriendlyByteBuf writeMediumLE(int var1) {
      this.source.writeMediumLE(var1);
      return this;
   }

   public FriendlyByteBuf writeInt(int var1) {
      this.source.writeInt(var1);
      return this;
   }

   public FriendlyByteBuf writeIntLE(int var1) {
      this.source.writeIntLE(var1);
      return this;
   }

   public FriendlyByteBuf writeLong(long var1) {
      this.source.writeLong(var1);
      return this;
   }

   public FriendlyByteBuf writeLongLE(long var1) {
      this.source.writeLongLE(var1);
      return this;
   }

   public FriendlyByteBuf writeChar(int var1) {
      this.source.writeChar(var1);
      return this;
   }

   public FriendlyByteBuf writeFloat(float var1) {
      this.source.writeFloat(var1);
      return this;
   }

   public FriendlyByteBuf writeDouble(double var1) {
      this.source.writeDouble(var1);
      return this;
   }

   public FriendlyByteBuf writeBytes(ByteBuf var1) {
      this.source.writeBytes(var1);
      return this;
   }

   public FriendlyByteBuf writeBytes(ByteBuf var1, int var2) {
      this.source.writeBytes(var1, var2);
      return this;
   }

   public FriendlyByteBuf writeBytes(ByteBuf var1, int var2, int var3) {
      this.source.writeBytes(var1, var2, var3);
      return this;
   }

   public FriendlyByteBuf writeBytes(byte[] var1) {
      this.source.writeBytes(var1);
      return this;
   }

   public FriendlyByteBuf writeBytes(byte[] var1, int var2, int var3) {
      this.source.writeBytes(var1, var2, var3);
      return this;
   }

   public FriendlyByteBuf writeBytes(ByteBuffer var1) {
      this.source.writeBytes(var1);
      return this;
   }

   public int writeBytes(InputStream var1, int var2) throws IOException {
      return this.source.writeBytes(var1, var2);
   }

   public int writeBytes(ScatteringByteChannel var1, int var2) throws IOException {
      return this.source.writeBytes(var1, var2);
   }

   public int writeBytes(FileChannel var1, long var2, int var4) throws IOException {
      return this.source.writeBytes(var1, var2, var4);
   }

   public FriendlyByteBuf writeZero(int var1) {
      this.source.writeZero(var1);
      return this;
   }

   public int writeCharSequence(CharSequence var1, Charset var2) {
      return this.source.writeCharSequence(var1, var2);
   }

   public int indexOf(int var1, int var2, byte var3) {
      return this.source.indexOf(var1, var2, var3);
   }

   public int bytesBefore(byte var1) {
      return this.source.bytesBefore(var1);
   }

   public int bytesBefore(int var1, byte var2) {
      return this.source.bytesBefore(var1, var2);
   }

   public int bytesBefore(int var1, int var2, byte var3) {
      return this.source.bytesBefore(var1, var2, var3);
   }

   public int forEachByte(ByteProcessor var1) {
      return this.source.forEachByte(var1);
   }

   public int forEachByte(int var1, int var2, ByteProcessor var3) {
      return this.source.forEachByte(var1, var2, var3);
   }

   public int forEachByteDesc(ByteProcessor var1) {
      return this.source.forEachByteDesc(var1);
   }

   public int forEachByteDesc(int var1, int var2, ByteProcessor var3) {
      return this.source.forEachByteDesc(var1, var2, var3);
   }

   public ByteBuf copy() {
      return this.source.copy();
   }

   public ByteBuf copy(int var1, int var2) {
      return this.source.copy(var1, var2);
   }

   public ByteBuf slice() {
      return this.source.slice();
   }

   public ByteBuf retainedSlice() {
      return this.source.retainedSlice();
   }

   public ByteBuf slice(int var1, int var2) {
      return this.source.slice(var1, var2);
   }

   public ByteBuf retainedSlice(int var1, int var2) {
      return this.source.retainedSlice(var1, var2);
   }

   public ByteBuf duplicate() {
      return this.source.duplicate();
   }

   public ByteBuf retainedDuplicate() {
      return this.source.retainedDuplicate();
   }

   public int nioBufferCount() {
      return this.source.nioBufferCount();
   }

   public ByteBuffer nioBuffer() {
      return this.source.nioBuffer();
   }

   public ByteBuffer nioBuffer(int var1, int var2) {
      return this.source.nioBuffer(var1, var2);
   }

   public ByteBuffer internalNioBuffer(int var1, int var2) {
      return this.source.internalNioBuffer(var1, var2);
   }

   public ByteBuffer[] nioBuffers() {
      return this.source.nioBuffers();
   }

   public ByteBuffer[] nioBuffers(int var1, int var2) {
      return this.source.nioBuffers(var1, var2);
   }

   public boolean hasArray() {
      return this.source.hasArray();
   }

   public byte[] array() {
      return this.source.array();
   }

   public int arrayOffset() {
      return this.source.arrayOffset();
   }

   public boolean hasMemoryAddress() {
      return this.source.hasMemoryAddress();
   }

   public long memoryAddress() {
      return this.source.memoryAddress();
   }

   public String toString(Charset var1) {
      return this.source.toString(var1);
   }

   public String toString(int var1, int var2, Charset var3) {
      return this.source.toString(var1, var2, var3);
   }

   public int hashCode() {
      return this.source.hashCode();
   }

   public boolean equals(Object var1) {
      return this.source.equals(var1);
   }

   public int compareTo(ByteBuf var1) {
      return this.source.compareTo(var1);
   }

   public String toString() {
      return this.source.toString();
   }

   public FriendlyByteBuf retain(int var1) {
      this.source.retain(var1);
      return this;
   }

   public FriendlyByteBuf retain() {
      this.source.retain();
      return this;
   }

   public FriendlyByteBuf touch() {
      this.source.touch();
      return this;
   }

   public FriendlyByteBuf touch(Object var1) {
      this.source.touch(var1);
      return this;
   }

   public int refCnt() {
      return this.source.refCnt();
   }

   public boolean release() {
      return this.source.release();
   }

   public boolean release(int var1) {
      return this.source.release(var1);
   }
}
