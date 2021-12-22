package net.minecraft.network;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class FriendlyByteBuf extends ByteBuf {
   private static final int MAX_VARINT_SIZE = 5;
   private static final int MAX_VARLONG_SIZE = 10;
   private static final int DEFAULT_NBT_QUOTA = 2097152;
   private final ByteBuf source;
   public static final short MAX_STRING_LENGTH = 32767;
   public static final int MAX_COMPONENT_STRING_LENGTH = 262144;

   public FriendlyByteBuf(ByteBuf var1) {
      super();
      this.source = var1;
   }

   public static int getVarIntSize(int var0) {
      for(int var1 = 1; var1 < 5; ++var1) {
         if ((var0 & -1 << var1 * 7) == 0) {
            return var1;
         }
      }

      return 5;
   }

   public static int getVarLongSize(long var0) {
      for(int var2 = 1; var2 < 10; ++var2) {
         if ((var0 & -1L << var2 * 7) == 0L) {
            return var2;
         }
      }

      return 10;
   }

   public <T> T readWithCodec(Codec<T> var1) {
      CompoundTag var2 = this.readAnySizeNbt();
      DataResult var3 = var1.parse(NbtOps.INSTANCE, var2);
      var3.error().ifPresent((var1x) -> {
         String var10002 = var1x.message();
         throw new EncoderException("Failed to decode: " + var10002 + " " + var2);
      });
      return var3.result().get();
   }

   public <T> void writeWithCodec(Codec<T> var1, T var2) {
      DataResult var3 = var1.encodeStart(NbtOps.INSTANCE, var2);
      var3.error().ifPresent((var1x) -> {
         String var10002 = var1x.message();
         throw new EncoderException("Failed to encode: " + var10002 + " " + var2);
      });
      this.writeNbt((CompoundTag)var3.result().get());
   }

   public static <T> IntFunction<T> limitValue(IntFunction<T> var0, int var1) {
      return (var2) -> {
         if (var2 > var1) {
            throw new DecoderException("Value " + var2 + " is larger than limit " + var1);
         } else {
            return var0.apply(var2);
         }
      };
   }

   public <T, C extends Collection<T>> C readCollection(IntFunction<C> var1, Function<FriendlyByteBuf, T> var2) {
      int var3 = this.readVarInt();
      Collection var4 = (Collection)var1.apply(var3);

      for(int var5 = 0; var5 < var3; ++var5) {
         var4.add(var2.apply(this));
      }

      return var4;
   }

   public <T> void writeCollection(Collection<T> var1, BiConsumer<FriendlyByteBuf, T> var2) {
      this.writeVarInt(var1.size());
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Object var4 = var3.next();
         var2.accept(this, var4);
      }

   }

   public <T> List<T> readList(Function<FriendlyByteBuf, T> var1) {
      return (List)this.readCollection(Lists::newArrayListWithCapacity, var1);
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

   public <K, V, M extends Map<K, V>> M readMap(IntFunction<M> var1, Function<FriendlyByteBuf, K> var2, Function<FriendlyByteBuf, V> var3) {
      int var4 = this.readVarInt();
      Map var5 = (Map)var1.apply(var4);

      for(int var6 = 0; var6 < var4; ++var6) {
         Object var7 = var2.apply(this);
         Object var8 = var3.apply(this);
         var5.put(var7, var8);
      }

      return var5;
   }

   public <K, V> Map<K, V> readMap(Function<FriendlyByteBuf, K> var1, Function<FriendlyByteBuf, V> var2) {
      return this.readMap(Maps::newHashMapWithExpectedSize, var1, var2);
   }

   public <K, V> void writeMap(Map<K, V> var1, BiConsumer<FriendlyByteBuf, K> var2, BiConsumer<FriendlyByteBuf, V> var3) {
      this.writeVarInt(var1.size());
      var1.forEach((var3x, var4) -> {
         var2.accept(this, var3x);
         var3.accept(this, var4);
      });
   }

   public void readWithCount(Consumer<FriendlyByteBuf> var1) {
      int var2 = this.readVarInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         var1.accept(this);
      }

   }

   public <T> void writeOptional(Optional<T> var1, BiConsumer<FriendlyByteBuf, T> var2) {
      if (var1.isPresent()) {
         this.writeBoolean(true);
         var2.accept(this, var1.get());
      } else {
         this.writeBoolean(false);
      }

   }

   public <T> Optional<T> readOptional(Function<FriendlyByteBuf, T> var1) {
      return this.readBoolean() ? Optional.of(var1.apply(this)) : Optional.empty();
   }

   public byte[] readByteArray() {
      return this.readByteArray(this.readableBytes());
   }

   public FriendlyByteBuf writeByteArray(byte[] var1) {
      this.writeVarInt(var1.length);
      this.writeBytes(var1);
      return this;
   }

   public byte[] readByteArray(int var1) {
      int var2 = this.readVarInt();
      if (var2 > var1) {
         throw new DecoderException("ByteArray with size " + var2 + " is bigger than allowed " + var1);
      } else {
         byte[] var3 = new byte[var2];
         this.readBytes(var3);
         return var3;
      }
   }

   public FriendlyByteBuf writeVarIntArray(int[] var1) {
      this.writeVarInt(var1.length);
      int[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var2[var4];
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
      long[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         long var5 = var2[var4];
         this.writeLong(var5);
      }

      return this;
   }

   public long[] readLongArray() {
      return this.readLongArray((long[])null);
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

   @VisibleForTesting
   public byte[] accessByteBufWithCorrectSize() {
      int var1 = this.writerIndex();
      byte[] var2 = new byte[var1];
      this.getBytes(0, (byte[])var2);
      return var2;
   }

   public BlockPos readBlockPos() {
      return BlockPos.method_81(this.readLong());
   }

   public FriendlyByteBuf writeBlockPos(BlockPos var1) {
      this.writeLong(var1.asLong());
      return this;
   }

   public ChunkPos readChunkPos() {
      return new ChunkPos(this.readLong());
   }

   public FriendlyByteBuf writeChunkPos(ChunkPos var1) {
      this.writeLong(var1.toLong());
      return this;
   }

   public SectionPos readSectionPos() {
      return SectionPos.method_74(this.readLong());
   }

   public FriendlyByteBuf writeSectionPos(SectionPos var1) {
      this.writeLong(var1.asLong());
      return this;
   }

   public Component readComponent() {
      return Component.Serializer.fromJson(this.readUtf(262144));
   }

   public FriendlyByteBuf writeComponent(Component var1) {
      return this.writeUtf(Component.Serializer.toJson(var1), 262144);
   }

   public <T extends Enum<T>> T readEnum(Class<T> var1) {
      return ((Enum[])var1.getEnumConstants())[this.readVarInt()];
   }

   public FriendlyByteBuf writeEnum(Enum<?> var1) {
      return this.writeVarInt(var1.ordinal());
   }

   public int readVarInt() {
      int var1 = 0;
      int var2 = 0;

      byte var3;
      do {
         var3 = this.readByte();
         var1 |= (var3 & 127) << var2++ * 7;
         if (var2 > 5) {
            throw new RuntimeException("VarInt too big");
         }
      } while((var3 & 128) == 128);

      return var1;
   }

   public long readVarLong() {
      long var1 = 0L;
      int var3 = 0;

      byte var4;
      do {
         var4 = this.readByte();
         var1 |= (long)(var4 & 127) << var3++ * 7;
         if (var3 > 10) {
            throw new RuntimeException("VarLong too big");
         }
      } while((var4 & 128) == 128);

      return var1;
   }

   public FriendlyByteBuf writeUUID(UUID var1) {
      this.writeLong(var1.getMostSignificantBits());
      this.writeLong(var1.getLeastSignificantBits());
      return this;
   }

   public UUID readUUID() {
      return new UUID(this.readLong(), this.readLong());
   }

   public FriendlyByteBuf writeVarInt(int var1) {
      while((var1 & -128) != 0) {
         this.writeByte(var1 & 127 | 128);
         var1 >>>= 7;
      }

      this.writeByte(var1);
      return this;
   }

   public FriendlyByteBuf writeVarLong(long var1) {
      while((var1 & -128L) != 0L) {
         this.writeByte((int)(var1 & 127L) | 128);
         var1 >>>= 7;
      }

      this.writeByte((int)var1);
      return this;
   }

   public FriendlyByteBuf writeNbt(@Nullable CompoundTag var1) {
      if (var1 == null) {
         this.writeByte(0);
      } else {
         try {
            NbtIo.write(var1, (DataOutput)(new ByteBufOutputStream(this)));
         } catch (IOException var3) {
            throw new EncoderException(var3);
         }
      }

      return this;
   }

   @Nullable
   public CompoundTag readNbt() {
      return this.readNbt(new NbtAccounter(2097152L));
   }

   @Nullable
   public CompoundTag readAnySizeNbt() {
      return this.readNbt(NbtAccounter.UNLIMITED);
   }

   @Nullable
   public CompoundTag readNbt(NbtAccounter var1) {
      int var2 = this.readerIndex();
      byte var3 = this.readByte();
      if (var3 == 0) {
         return null;
      } else {
         this.readerIndex(var2);

         try {
            return NbtIo.read(new ByteBufInputStream(this), var1);
         } catch (IOException var5) {
            throw new EncoderException(var5);
         }
      }
   }

   public FriendlyByteBuf writeItem(ItemStack var1) {
      if (var1.isEmpty()) {
         this.writeBoolean(false);
      } else {
         this.writeBoolean(true);
         Item var2 = var1.getItem();
         this.writeVarInt(Item.getId(var2));
         this.writeByte(var1.getCount());
         CompoundTag var3 = null;
         if (var2.canBeDepleted() || var2.shouldOverrideMultiplayerNbt()) {
            var3 = var1.getTag();
         }

         this.writeNbt(var3);
      }

      return this;
   }

   public ItemStack readItem() {
      if (!this.readBoolean()) {
         return ItemStack.EMPTY;
      } else {
         int var1 = this.readVarInt();
         byte var2 = this.readByte();
         ItemStack var3 = new ItemStack(Item.byId(var1), var2);
         var3.setTag(this.readNbt());
         return var3;
      }
   }

   public String readUtf() {
      return this.readUtf(32767);
   }

   public String readUtf(int var1) {
      int var2 = this.readVarInt();
      if (var2 > var1 * 4) {
         throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + var2 + " > " + var1 * 4 + ")");
      } else if (var2 < 0) {
         throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
      } else {
         String var3 = this.toString(this.readerIndex(), var2, StandardCharsets.UTF_8);
         this.readerIndex(this.readerIndex() + var2);
         if (var3.length() > var1) {
            throw new DecoderException("The received string length is longer than maximum allowed (" + var2 + " > " + var1 + ")");
         } else {
            return var3;
         }
      }
   }

   public FriendlyByteBuf writeUtf(String var1) {
      return this.writeUtf(var1, 32767);
   }

   public FriendlyByteBuf writeUtf(String var1, int var2) {
      byte[] var3 = var1.getBytes(StandardCharsets.UTF_8);
      if (var3.length > var2) {
         throw new EncoderException("String too big (was " + var3.length + " bytes encoded, max " + var2 + ")");
      } else {
         this.writeVarInt(var3.length);
         this.writeBytes(var3);
         return this;
      }
   }

   public ResourceLocation readResourceLocation() {
      return new ResourceLocation(this.readUtf(32767));
   }

   public FriendlyByteBuf writeResourceLocation(ResourceLocation var1) {
      this.writeUtf(var1.toString());
      return this;
   }

   public Date readDate() {
      return new Date(this.readLong());
   }

   public FriendlyByteBuf writeDate(Date var1) {
      this.writeLong(var1.getTime());
      return this;
   }

   public BlockHitResult readBlockHitResult() {
      BlockPos var1 = this.readBlockPos();
      Direction var2 = (Direction)this.readEnum(Direction.class);
      float var3 = this.readFloat();
      float var4 = this.readFloat();
      float var5 = this.readFloat();
      boolean var6 = this.readBoolean();
      return new BlockHitResult(new Vec3((double)var1.getX() + (double)var3, (double)var1.getY() + (double)var4, (double)var1.getZ() + (double)var5), var2, var1, var6);
   }

   public void writeBlockHitResult(BlockHitResult var1) {
      BlockPos var2 = var1.getBlockPos();
      this.writeBlockPos(var2);
      this.writeEnum(var1.getDirection());
      Vec3 var3 = var1.getLocation();
      this.writeFloat((float)(var3.field_414 - (double)var2.getX()));
      this.writeFloat((float)(var3.field_415 - (double)var2.getY()));
      this.writeFloat((float)(var3.field_416 - (double)var2.getZ()));
      this.writeBoolean(var1.isInside());
   }

   public BitSet readBitSet() {
      return BitSet.valueOf(this.readLongArray());
   }

   public void writeBitSet(BitSet var1) {
      this.writeLongArray(var1.toLongArray());
   }

   public int capacity() {
      return this.source.capacity();
   }

   public ByteBuf capacity(int var1) {
      return this.source.capacity(var1);
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
      return this.source.unwrap();
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

   public ByteBuf readerIndex(int var1) {
      return this.source.readerIndex(var1);
   }

   public int writerIndex() {
      return this.source.writerIndex();
   }

   public ByteBuf writerIndex(int var1) {
      return this.source.writerIndex(var1);
   }

   public ByteBuf setIndex(int var1, int var2) {
      return this.source.setIndex(var1, var2);
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

   public ByteBuf clear() {
      return this.source.clear();
   }

   public ByteBuf markReaderIndex() {
      return this.source.markReaderIndex();
   }

   public ByteBuf resetReaderIndex() {
      return this.source.resetReaderIndex();
   }

   public ByteBuf markWriterIndex() {
      return this.source.markWriterIndex();
   }

   public ByteBuf resetWriterIndex() {
      return this.source.resetWriterIndex();
   }

   public ByteBuf discardReadBytes() {
      return this.source.discardReadBytes();
   }

   public ByteBuf discardSomeReadBytes() {
      return this.source.discardSomeReadBytes();
   }

   public ByteBuf ensureWritable(int var1) {
      return this.source.ensureWritable(var1);
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

   public ByteBuf getBytes(int var1, ByteBuf var2) {
      return this.source.getBytes(var1, var2);
   }

   public ByteBuf getBytes(int var1, ByteBuf var2, int var3) {
      return this.source.getBytes(var1, var2, var3);
   }

   public ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      return this.source.getBytes(var1, var2, var3, var4);
   }

   public ByteBuf getBytes(int var1, byte[] var2) {
      return this.source.getBytes(var1, var2);
   }

   public ByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      return this.source.getBytes(var1, var2, var3, var4);
   }

   public ByteBuf getBytes(int var1, ByteBuffer var2) {
      return this.source.getBytes(var1, var2);
   }

   public ByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      return this.source.getBytes(var1, var2, var3);
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

   public ByteBuf setBoolean(int var1, boolean var2) {
      return this.source.setBoolean(var1, var2);
   }

   public ByteBuf setByte(int var1, int var2) {
      return this.source.setByte(var1, var2);
   }

   public ByteBuf setShort(int var1, int var2) {
      return this.source.setShort(var1, var2);
   }

   public ByteBuf setShortLE(int var1, int var2) {
      return this.source.setShortLE(var1, var2);
   }

   public ByteBuf setMedium(int var1, int var2) {
      return this.source.setMedium(var1, var2);
   }

   public ByteBuf setMediumLE(int var1, int var2) {
      return this.source.setMediumLE(var1, var2);
   }

   public ByteBuf setInt(int var1, int var2) {
      return this.source.setInt(var1, var2);
   }

   public ByteBuf setIntLE(int var1, int var2) {
      return this.source.setIntLE(var1, var2);
   }

   public ByteBuf setLong(int var1, long var2) {
      return this.source.setLong(var1, var2);
   }

   public ByteBuf setLongLE(int var1, long var2) {
      return this.source.setLongLE(var1, var2);
   }

   public ByteBuf setChar(int var1, int var2) {
      return this.source.setChar(var1, var2);
   }

   public ByteBuf setFloat(int var1, float var2) {
      return this.source.setFloat(var1, var2);
   }

   public ByteBuf setDouble(int var1, double var2) {
      return this.source.setDouble(var1, var2);
   }

   public ByteBuf setBytes(int var1, ByteBuf var2) {
      return this.source.setBytes(var1, var2);
   }

   public ByteBuf setBytes(int var1, ByteBuf var2, int var3) {
      return this.source.setBytes(var1, var2, var3);
   }

   public ByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      return this.source.setBytes(var1, var2, var3, var4);
   }

   public ByteBuf setBytes(int var1, byte[] var2) {
      return this.source.setBytes(var1, var2);
   }

   public ByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      return this.source.setBytes(var1, var2, var3, var4);
   }

   public ByteBuf setBytes(int var1, ByteBuffer var2) {
      return this.source.setBytes(var1, var2);
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

   public ByteBuf setZero(int var1, int var2) {
      return this.source.setZero(var1, var2);
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

   public ByteBuf readBytes(ByteBuf var1) {
      return this.source.readBytes(var1);
   }

   public ByteBuf readBytes(ByteBuf var1, int var2) {
      return this.source.readBytes(var1, var2);
   }

   public ByteBuf readBytes(ByteBuf var1, int var2, int var3) {
      return this.source.readBytes(var1, var2, var3);
   }

   public ByteBuf readBytes(byte[] var1) {
      return this.source.readBytes(var1);
   }

   public ByteBuf readBytes(byte[] var1, int var2, int var3) {
      return this.source.readBytes(var1, var2, var3);
   }

   public ByteBuf readBytes(ByteBuffer var1) {
      return this.source.readBytes(var1);
   }

   public ByteBuf readBytes(OutputStream var1, int var2) throws IOException {
      return this.source.readBytes(var1, var2);
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

   public ByteBuf skipBytes(int var1) {
      return this.source.skipBytes(var1);
   }

   public ByteBuf writeBoolean(boolean var1) {
      return this.source.writeBoolean(var1);
   }

   public ByteBuf writeByte(int var1) {
      return this.source.writeByte(var1);
   }

   public ByteBuf writeShort(int var1) {
      return this.source.writeShort(var1);
   }

   public ByteBuf writeShortLE(int var1) {
      return this.source.writeShortLE(var1);
   }

   public ByteBuf writeMedium(int var1) {
      return this.source.writeMedium(var1);
   }

   public ByteBuf writeMediumLE(int var1) {
      return this.source.writeMediumLE(var1);
   }

   public ByteBuf writeInt(int var1) {
      return this.source.writeInt(var1);
   }

   public ByteBuf writeIntLE(int var1) {
      return this.source.writeIntLE(var1);
   }

   public ByteBuf writeLong(long var1) {
      return this.source.writeLong(var1);
   }

   public ByteBuf writeLongLE(long var1) {
      return this.source.writeLongLE(var1);
   }

   public ByteBuf writeChar(int var1) {
      return this.source.writeChar(var1);
   }

   public ByteBuf writeFloat(float var1) {
      return this.source.writeFloat(var1);
   }

   public ByteBuf writeDouble(double var1) {
      return this.source.writeDouble(var1);
   }

   public ByteBuf writeBytes(ByteBuf var1) {
      return this.source.writeBytes(var1);
   }

   public ByteBuf writeBytes(ByteBuf var1, int var2) {
      return this.source.writeBytes(var1, var2);
   }

   public ByteBuf writeBytes(ByteBuf var1, int var2, int var3) {
      return this.source.writeBytes(var1, var2, var3);
   }

   public ByteBuf writeBytes(byte[] var1) {
      return this.source.writeBytes(var1);
   }

   public ByteBuf writeBytes(byte[] var1, int var2, int var3) {
      return this.source.writeBytes(var1, var2, var3);
   }

   public ByteBuf writeBytes(ByteBuffer var1) {
      return this.source.writeBytes(var1);
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

   public ByteBuf writeZero(int var1) {
      return this.source.writeZero(var1);
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

   public ByteBuf retain(int var1) {
      return this.source.retain(var1);
   }

   public ByteBuf retain() {
      return this.source.retain();
   }

   public ByteBuf touch() {
      return this.source.touch();
   }

   public ByteBuf touch(Object var1) {
      return this.source.touch(var1);
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
