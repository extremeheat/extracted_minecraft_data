package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;
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
import java.util.Date;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

public class PacketBuffer extends ByteBuf {
   private final ByteBuf field_150794_a;

   public PacketBuffer(ByteBuf var1) {
      super();
      this.field_150794_a = var1;
   }

   public static int func_150790_a(int var0) {
      for(int var1 = 1; var1 < 5; ++var1) {
         if ((var0 & -1 << var1 * 7) == 0) {
            return var1;
         }
      }

      return 5;
   }

   public PacketBuffer func_179250_a(byte[] var1) {
      this.func_150787_b(var1.length);
      this.writeBytes(var1);
      return this;
   }

   public byte[] func_179251_a() {
      return this.func_189425_b(this.readableBytes());
   }

   public byte[] func_189425_b(int var1) {
      int var2 = this.func_150792_a();
      if (var2 > var1) {
         throw new DecoderException("ByteArray with size " + var2 + " is bigger than allowed " + var1);
      } else {
         byte[] var3 = new byte[var2];
         this.readBytes(var3);
         return var3;
      }
   }

   public PacketBuffer func_186875_a(int[] var1) {
      this.func_150787_b(var1.length);
      int[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var2[var4];
         this.func_150787_b(var5);
      }

      return this;
   }

   public int[] func_186863_b() {
      return this.func_189424_c(this.readableBytes());
   }

   public int[] func_189424_c(int var1) {
      int var2 = this.func_150792_a();
      if (var2 > var1) {
         throw new DecoderException("VarIntArray with size " + var2 + " is bigger than allowed " + var1);
      } else {
         int[] var3 = new int[var2];

         for(int var4 = 0; var4 < var3.length; ++var4) {
            var3[var4] = this.func_150792_a();
         }

         return var3;
      }
   }

   public PacketBuffer func_186865_a(long[] var1) {
      this.func_150787_b(var1.length);
      long[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         long var5 = var2[var4];
         this.writeLong(var5);
      }

      return this;
   }

   public long[] func_186873_b(@Nullable long[] var1) {
      return this.func_189423_a(var1, this.readableBytes() / 8);
   }

   public long[] func_189423_a(@Nullable long[] var1, int var2) {
      int var3 = this.func_150792_a();
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

   public BlockPos func_179259_c() {
      return BlockPos.func_177969_a(this.readLong());
   }

   public PacketBuffer func_179255_a(BlockPos var1) {
      this.writeLong(var1.func_177986_g());
      return this;
   }

   public ITextComponent func_179258_d() {
      return ITextComponent.Serializer.func_150699_a(this.func_150789_c(262144));
   }

   public PacketBuffer func_179256_a(ITextComponent var1) {
      return this.func_211400_a(ITextComponent.Serializer.func_150696_a(var1), 262144);
   }

   public <T extends Enum<T>> T func_179257_a(Class<T> var1) {
      return ((Enum[])var1.getEnumConstants())[this.func_150792_a()];
   }

   public PacketBuffer func_179249_a(Enum<?> var1) {
      return this.func_150787_b(var1.ordinal());
   }

   public int func_150792_a() {
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

   public long func_179260_f() {
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

   public PacketBuffer func_179252_a(UUID var1) {
      this.writeLong(var1.getMostSignificantBits());
      this.writeLong(var1.getLeastSignificantBits());
      return this;
   }

   public UUID func_179253_g() {
      return new UUID(this.readLong(), this.readLong());
   }

   public PacketBuffer func_150787_b(int var1) {
      while((var1 & -128) != 0) {
         this.writeByte(var1 & 127 | 128);
         var1 >>>= 7;
      }

      this.writeByte(var1);
      return this;
   }

   public PacketBuffer func_179254_b(long var1) {
      while((var1 & -128L) != 0L) {
         this.writeByte((int)(var1 & 127L) | 128);
         var1 >>>= 7;
      }

      this.writeByte((int)var1);
      return this;
   }

   public PacketBuffer func_150786_a(@Nullable NBTTagCompound var1) {
      if (var1 == null) {
         this.writeByte(0);
      } else {
         try {
            CompressedStreamTools.func_74800_a(var1, new ByteBufOutputStream(this));
         } catch (IOException var3) {
            throw new EncoderException(var3);
         }
      }

      return this;
   }

   @Nullable
   public NBTTagCompound func_150793_b() {
      int var1 = this.readerIndex();
      byte var2 = this.readByte();
      if (var2 == 0) {
         return null;
      } else {
         this.readerIndex(var1);

         try {
            return CompressedStreamTools.func_152456_a(new ByteBufInputStream(this), new NBTSizeTracker(2097152L));
         } catch (IOException var4) {
            throw new EncoderException(var4);
         }
      }
   }

   public PacketBuffer func_150788_a(ItemStack var1) {
      if (var1.func_190926_b()) {
         this.writeBoolean(false);
      } else {
         this.writeBoolean(true);
         Item var2 = var1.func_77973_b();
         this.func_150787_b(Item.func_150891_b(var2));
         this.writeByte(var1.func_190916_E());
         NBTTagCompound var3 = null;
         if (var2.func_77645_m() || var2.func_77651_p()) {
            var3 = var1.func_77978_p();
         }

         this.func_150786_a(var3);
      }

      return this;
   }

   public ItemStack func_150791_c() {
      if (!this.readBoolean()) {
         return ItemStack.field_190927_a;
      } else {
         int var1 = this.func_150792_a();
         byte var2 = this.readByte();
         ItemStack var3 = new ItemStack(Item.func_150899_d(var1), var2);
         var3.func_77982_d(this.func_150793_b());
         return var3;
      }
   }

   public String func_150789_c(int var1) {
      int var2 = this.func_150792_a();
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

   public PacketBuffer func_180714_a(String var1) {
      return this.func_211400_a(var1, 32767);
   }

   public PacketBuffer func_211400_a(String var1, int var2) {
      byte[] var3 = var1.getBytes(StandardCharsets.UTF_8);
      if (var3.length > var2) {
         throw new EncoderException("String too big (was " + var3.length + " bytes encoded, max " + var2 + ")");
      } else {
         this.func_150787_b(var3.length);
         this.writeBytes(var3);
         return this;
      }
   }

   public ResourceLocation func_192575_l() {
      return new ResourceLocation(this.func_150789_c(32767));
   }

   public PacketBuffer func_192572_a(ResourceLocation var1) {
      this.func_180714_a(var1.toString());
      return this;
   }

   public Date func_192573_m() {
      return new Date(this.readLong());
   }

   public PacketBuffer func_192574_a(Date var1) {
      this.writeLong(var1.getTime());
      return this;
   }

   public int capacity() {
      return this.field_150794_a.capacity();
   }

   public ByteBuf capacity(int var1) {
      return this.field_150794_a.capacity(var1);
   }

   public int maxCapacity() {
      return this.field_150794_a.maxCapacity();
   }

   public ByteBufAllocator alloc() {
      return this.field_150794_a.alloc();
   }

   public ByteOrder order() {
      return this.field_150794_a.order();
   }

   public ByteBuf order(ByteOrder var1) {
      return this.field_150794_a.order(var1);
   }

   public ByteBuf unwrap() {
      return this.field_150794_a.unwrap();
   }

   public boolean isDirect() {
      return this.field_150794_a.isDirect();
   }

   public boolean isReadOnly() {
      return this.field_150794_a.isReadOnly();
   }

   public ByteBuf asReadOnly() {
      return this.field_150794_a.asReadOnly();
   }

   public int readerIndex() {
      return this.field_150794_a.readerIndex();
   }

   public ByteBuf readerIndex(int var1) {
      return this.field_150794_a.readerIndex(var1);
   }

   public int writerIndex() {
      return this.field_150794_a.writerIndex();
   }

   public ByteBuf writerIndex(int var1) {
      return this.field_150794_a.writerIndex(var1);
   }

   public ByteBuf setIndex(int var1, int var2) {
      return this.field_150794_a.setIndex(var1, var2);
   }

   public int readableBytes() {
      return this.field_150794_a.readableBytes();
   }

   public int writableBytes() {
      return this.field_150794_a.writableBytes();
   }

   public int maxWritableBytes() {
      return this.field_150794_a.maxWritableBytes();
   }

   public boolean isReadable() {
      return this.field_150794_a.isReadable();
   }

   public boolean isReadable(int var1) {
      return this.field_150794_a.isReadable(var1);
   }

   public boolean isWritable() {
      return this.field_150794_a.isWritable();
   }

   public boolean isWritable(int var1) {
      return this.field_150794_a.isWritable(var1);
   }

   public ByteBuf clear() {
      return this.field_150794_a.clear();
   }

   public ByteBuf markReaderIndex() {
      return this.field_150794_a.markReaderIndex();
   }

   public ByteBuf resetReaderIndex() {
      return this.field_150794_a.resetReaderIndex();
   }

   public ByteBuf markWriterIndex() {
      return this.field_150794_a.markWriterIndex();
   }

   public ByteBuf resetWriterIndex() {
      return this.field_150794_a.resetWriterIndex();
   }

   public ByteBuf discardReadBytes() {
      return this.field_150794_a.discardReadBytes();
   }

   public ByteBuf discardSomeReadBytes() {
      return this.field_150794_a.discardSomeReadBytes();
   }

   public ByteBuf ensureWritable(int var1) {
      return this.field_150794_a.ensureWritable(var1);
   }

   public int ensureWritable(int var1, boolean var2) {
      return this.field_150794_a.ensureWritable(var1, var2);
   }

   public boolean getBoolean(int var1) {
      return this.field_150794_a.getBoolean(var1);
   }

   public byte getByte(int var1) {
      return this.field_150794_a.getByte(var1);
   }

   public short getUnsignedByte(int var1) {
      return this.field_150794_a.getUnsignedByte(var1);
   }

   public short getShort(int var1) {
      return this.field_150794_a.getShort(var1);
   }

   public short getShortLE(int var1) {
      return this.field_150794_a.getShortLE(var1);
   }

   public int getUnsignedShort(int var1) {
      return this.field_150794_a.getUnsignedShort(var1);
   }

   public int getUnsignedShortLE(int var1) {
      return this.field_150794_a.getUnsignedShortLE(var1);
   }

   public int getMedium(int var1) {
      return this.field_150794_a.getMedium(var1);
   }

   public int getMediumLE(int var1) {
      return this.field_150794_a.getMediumLE(var1);
   }

   public int getUnsignedMedium(int var1) {
      return this.field_150794_a.getUnsignedMedium(var1);
   }

   public int getUnsignedMediumLE(int var1) {
      return this.field_150794_a.getUnsignedMediumLE(var1);
   }

   public int getInt(int var1) {
      return this.field_150794_a.getInt(var1);
   }

   public int getIntLE(int var1) {
      return this.field_150794_a.getIntLE(var1);
   }

   public long getUnsignedInt(int var1) {
      return this.field_150794_a.getUnsignedInt(var1);
   }

   public long getUnsignedIntLE(int var1) {
      return this.field_150794_a.getUnsignedIntLE(var1);
   }

   public long getLong(int var1) {
      return this.field_150794_a.getLong(var1);
   }

   public long getLongLE(int var1) {
      return this.field_150794_a.getLongLE(var1);
   }

   public char getChar(int var1) {
      return this.field_150794_a.getChar(var1);
   }

   public float getFloat(int var1) {
      return this.field_150794_a.getFloat(var1);
   }

   public double getDouble(int var1) {
      return this.field_150794_a.getDouble(var1);
   }

   public ByteBuf getBytes(int var1, ByteBuf var2) {
      return this.field_150794_a.getBytes(var1, var2);
   }

   public ByteBuf getBytes(int var1, ByteBuf var2, int var3) {
      return this.field_150794_a.getBytes(var1, var2, var3);
   }

   public ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      return this.field_150794_a.getBytes(var1, var2, var3, var4);
   }

   public ByteBuf getBytes(int var1, byte[] var2) {
      return this.field_150794_a.getBytes(var1, var2);
   }

   public ByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      return this.field_150794_a.getBytes(var1, var2, var3, var4);
   }

   public ByteBuf getBytes(int var1, ByteBuffer var2) {
      return this.field_150794_a.getBytes(var1, var2);
   }

   public ByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      return this.field_150794_a.getBytes(var1, var2, var3);
   }

   public int getBytes(int var1, GatheringByteChannel var2, int var3) throws IOException {
      return this.field_150794_a.getBytes(var1, var2, var3);
   }

   public int getBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      return this.field_150794_a.getBytes(var1, var2, var3, var5);
   }

   public CharSequence getCharSequence(int var1, int var2, Charset var3) {
      return this.field_150794_a.getCharSequence(var1, var2, var3);
   }

   public ByteBuf setBoolean(int var1, boolean var2) {
      return this.field_150794_a.setBoolean(var1, var2);
   }

   public ByteBuf setByte(int var1, int var2) {
      return this.field_150794_a.setByte(var1, var2);
   }

   public ByteBuf setShort(int var1, int var2) {
      return this.field_150794_a.setShort(var1, var2);
   }

   public ByteBuf setShortLE(int var1, int var2) {
      return this.field_150794_a.setShortLE(var1, var2);
   }

   public ByteBuf setMedium(int var1, int var2) {
      return this.field_150794_a.setMedium(var1, var2);
   }

   public ByteBuf setMediumLE(int var1, int var2) {
      return this.field_150794_a.setMediumLE(var1, var2);
   }

   public ByteBuf setInt(int var1, int var2) {
      return this.field_150794_a.setInt(var1, var2);
   }

   public ByteBuf setIntLE(int var1, int var2) {
      return this.field_150794_a.setIntLE(var1, var2);
   }

   public ByteBuf setLong(int var1, long var2) {
      return this.field_150794_a.setLong(var1, var2);
   }

   public ByteBuf setLongLE(int var1, long var2) {
      return this.field_150794_a.setLongLE(var1, var2);
   }

   public ByteBuf setChar(int var1, int var2) {
      return this.field_150794_a.setChar(var1, var2);
   }

   public ByteBuf setFloat(int var1, float var2) {
      return this.field_150794_a.setFloat(var1, var2);
   }

   public ByteBuf setDouble(int var1, double var2) {
      return this.field_150794_a.setDouble(var1, var2);
   }

   public ByteBuf setBytes(int var1, ByteBuf var2) {
      return this.field_150794_a.setBytes(var1, var2);
   }

   public ByteBuf setBytes(int var1, ByteBuf var2, int var3) {
      return this.field_150794_a.setBytes(var1, var2, var3);
   }

   public ByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      return this.field_150794_a.setBytes(var1, var2, var3, var4);
   }

   public ByteBuf setBytes(int var1, byte[] var2) {
      return this.field_150794_a.setBytes(var1, var2);
   }

   public ByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      return this.field_150794_a.setBytes(var1, var2, var3, var4);
   }

   public ByteBuf setBytes(int var1, ByteBuffer var2) {
      return this.field_150794_a.setBytes(var1, var2);
   }

   public int setBytes(int var1, InputStream var2, int var3) throws IOException {
      return this.field_150794_a.setBytes(var1, var2, var3);
   }

   public int setBytes(int var1, ScatteringByteChannel var2, int var3) throws IOException {
      return this.field_150794_a.setBytes(var1, var2, var3);
   }

   public int setBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      return this.field_150794_a.setBytes(var1, var2, var3, var5);
   }

   public ByteBuf setZero(int var1, int var2) {
      return this.field_150794_a.setZero(var1, var2);
   }

   public int setCharSequence(int var1, CharSequence var2, Charset var3) {
      return this.field_150794_a.setCharSequence(var1, var2, var3);
   }

   public boolean readBoolean() {
      return this.field_150794_a.readBoolean();
   }

   public byte readByte() {
      return this.field_150794_a.readByte();
   }

   public short readUnsignedByte() {
      return this.field_150794_a.readUnsignedByte();
   }

   public short readShort() {
      return this.field_150794_a.readShort();
   }

   public short readShortLE() {
      return this.field_150794_a.readShortLE();
   }

   public int readUnsignedShort() {
      return this.field_150794_a.readUnsignedShort();
   }

   public int readUnsignedShortLE() {
      return this.field_150794_a.readUnsignedShortLE();
   }

   public int readMedium() {
      return this.field_150794_a.readMedium();
   }

   public int readMediumLE() {
      return this.field_150794_a.readMediumLE();
   }

   public int readUnsignedMedium() {
      return this.field_150794_a.readUnsignedMedium();
   }

   public int readUnsignedMediumLE() {
      return this.field_150794_a.readUnsignedMediumLE();
   }

   public int readInt() {
      return this.field_150794_a.readInt();
   }

   public int readIntLE() {
      return this.field_150794_a.readIntLE();
   }

   public long readUnsignedInt() {
      return this.field_150794_a.readUnsignedInt();
   }

   public long readUnsignedIntLE() {
      return this.field_150794_a.readUnsignedIntLE();
   }

   public long readLong() {
      return this.field_150794_a.readLong();
   }

   public long readLongLE() {
      return this.field_150794_a.readLongLE();
   }

   public char readChar() {
      return this.field_150794_a.readChar();
   }

   public float readFloat() {
      return this.field_150794_a.readFloat();
   }

   public double readDouble() {
      return this.field_150794_a.readDouble();
   }

   public ByteBuf readBytes(int var1) {
      return this.field_150794_a.readBytes(var1);
   }

   public ByteBuf readSlice(int var1) {
      return this.field_150794_a.readSlice(var1);
   }

   public ByteBuf readRetainedSlice(int var1) {
      return this.field_150794_a.readRetainedSlice(var1);
   }

   public ByteBuf readBytes(ByteBuf var1) {
      return this.field_150794_a.readBytes(var1);
   }

   public ByteBuf readBytes(ByteBuf var1, int var2) {
      return this.field_150794_a.readBytes(var1, var2);
   }

   public ByteBuf readBytes(ByteBuf var1, int var2, int var3) {
      return this.field_150794_a.readBytes(var1, var2, var3);
   }

   public ByteBuf readBytes(byte[] var1) {
      return this.field_150794_a.readBytes(var1);
   }

   public ByteBuf readBytes(byte[] var1, int var2, int var3) {
      return this.field_150794_a.readBytes(var1, var2, var3);
   }

   public ByteBuf readBytes(ByteBuffer var1) {
      return this.field_150794_a.readBytes(var1);
   }

   public ByteBuf readBytes(OutputStream var1, int var2) throws IOException {
      return this.field_150794_a.readBytes(var1, var2);
   }

   public int readBytes(GatheringByteChannel var1, int var2) throws IOException {
      return this.field_150794_a.readBytes(var1, var2);
   }

   public CharSequence readCharSequence(int var1, Charset var2) {
      return this.field_150794_a.readCharSequence(var1, var2);
   }

   public int readBytes(FileChannel var1, long var2, int var4) throws IOException {
      return this.field_150794_a.readBytes(var1, var2, var4);
   }

   public ByteBuf skipBytes(int var1) {
      return this.field_150794_a.skipBytes(var1);
   }

   public ByteBuf writeBoolean(boolean var1) {
      return this.field_150794_a.writeBoolean(var1);
   }

   public ByteBuf writeByte(int var1) {
      return this.field_150794_a.writeByte(var1);
   }

   public ByteBuf writeShort(int var1) {
      return this.field_150794_a.writeShort(var1);
   }

   public ByteBuf writeShortLE(int var1) {
      return this.field_150794_a.writeShortLE(var1);
   }

   public ByteBuf writeMedium(int var1) {
      return this.field_150794_a.writeMedium(var1);
   }

   public ByteBuf writeMediumLE(int var1) {
      return this.field_150794_a.writeMediumLE(var1);
   }

   public ByteBuf writeInt(int var1) {
      return this.field_150794_a.writeInt(var1);
   }

   public ByteBuf writeIntLE(int var1) {
      return this.field_150794_a.writeIntLE(var1);
   }

   public ByteBuf writeLong(long var1) {
      return this.field_150794_a.writeLong(var1);
   }

   public ByteBuf writeLongLE(long var1) {
      return this.field_150794_a.writeLongLE(var1);
   }

   public ByteBuf writeChar(int var1) {
      return this.field_150794_a.writeChar(var1);
   }

   public ByteBuf writeFloat(float var1) {
      return this.field_150794_a.writeFloat(var1);
   }

   public ByteBuf writeDouble(double var1) {
      return this.field_150794_a.writeDouble(var1);
   }

   public ByteBuf writeBytes(ByteBuf var1) {
      return this.field_150794_a.writeBytes(var1);
   }

   public ByteBuf writeBytes(ByteBuf var1, int var2) {
      return this.field_150794_a.writeBytes(var1, var2);
   }

   public ByteBuf writeBytes(ByteBuf var1, int var2, int var3) {
      return this.field_150794_a.writeBytes(var1, var2, var3);
   }

   public ByteBuf writeBytes(byte[] var1) {
      return this.field_150794_a.writeBytes(var1);
   }

   public ByteBuf writeBytes(byte[] var1, int var2, int var3) {
      return this.field_150794_a.writeBytes(var1, var2, var3);
   }

   public ByteBuf writeBytes(ByteBuffer var1) {
      return this.field_150794_a.writeBytes(var1);
   }

   public int writeBytes(InputStream var1, int var2) throws IOException {
      return this.field_150794_a.writeBytes(var1, var2);
   }

   public int writeBytes(ScatteringByteChannel var1, int var2) throws IOException {
      return this.field_150794_a.writeBytes(var1, var2);
   }

   public int writeBytes(FileChannel var1, long var2, int var4) throws IOException {
      return this.field_150794_a.writeBytes(var1, var2, var4);
   }

   public ByteBuf writeZero(int var1) {
      return this.field_150794_a.writeZero(var1);
   }

   public int writeCharSequence(CharSequence var1, Charset var2) {
      return this.field_150794_a.writeCharSequence(var1, var2);
   }

   public int indexOf(int var1, int var2, byte var3) {
      return this.field_150794_a.indexOf(var1, var2, var3);
   }

   public int bytesBefore(byte var1) {
      return this.field_150794_a.bytesBefore(var1);
   }

   public int bytesBefore(int var1, byte var2) {
      return this.field_150794_a.bytesBefore(var1, var2);
   }

   public int bytesBefore(int var1, int var2, byte var3) {
      return this.field_150794_a.bytesBefore(var1, var2, var3);
   }

   public int forEachByte(ByteProcessor var1) {
      return this.field_150794_a.forEachByte(var1);
   }

   public int forEachByte(int var1, int var2, ByteProcessor var3) {
      return this.field_150794_a.forEachByte(var1, var2, var3);
   }

   public int forEachByteDesc(ByteProcessor var1) {
      return this.field_150794_a.forEachByteDesc(var1);
   }

   public int forEachByteDesc(int var1, int var2, ByteProcessor var3) {
      return this.field_150794_a.forEachByteDesc(var1, var2, var3);
   }

   public ByteBuf copy() {
      return this.field_150794_a.copy();
   }

   public ByteBuf copy(int var1, int var2) {
      return this.field_150794_a.copy(var1, var2);
   }

   public ByteBuf slice() {
      return this.field_150794_a.slice();
   }

   public ByteBuf retainedSlice() {
      return this.field_150794_a.retainedSlice();
   }

   public ByteBuf slice(int var1, int var2) {
      return this.field_150794_a.slice(var1, var2);
   }

   public ByteBuf retainedSlice(int var1, int var2) {
      return this.field_150794_a.retainedSlice(var1, var2);
   }

   public ByteBuf duplicate() {
      return this.field_150794_a.duplicate();
   }

   public ByteBuf retainedDuplicate() {
      return this.field_150794_a.retainedDuplicate();
   }

   public int nioBufferCount() {
      return this.field_150794_a.nioBufferCount();
   }

   public ByteBuffer nioBuffer() {
      return this.field_150794_a.nioBuffer();
   }

   public ByteBuffer nioBuffer(int var1, int var2) {
      return this.field_150794_a.nioBuffer(var1, var2);
   }

   public ByteBuffer internalNioBuffer(int var1, int var2) {
      return this.field_150794_a.internalNioBuffer(var1, var2);
   }

   public ByteBuffer[] nioBuffers() {
      return this.field_150794_a.nioBuffers();
   }

   public ByteBuffer[] nioBuffers(int var1, int var2) {
      return this.field_150794_a.nioBuffers(var1, var2);
   }

   public boolean hasArray() {
      return this.field_150794_a.hasArray();
   }

   public byte[] array() {
      return this.field_150794_a.array();
   }

   public int arrayOffset() {
      return this.field_150794_a.arrayOffset();
   }

   public boolean hasMemoryAddress() {
      return this.field_150794_a.hasMemoryAddress();
   }

   public long memoryAddress() {
      return this.field_150794_a.memoryAddress();
   }

   public String toString(Charset var1) {
      return this.field_150794_a.toString(var1);
   }

   public String toString(int var1, int var2, Charset var3) {
      return this.field_150794_a.toString(var1, var2, var3);
   }

   public int hashCode() {
      return this.field_150794_a.hashCode();
   }

   public boolean equals(Object var1) {
      return this.field_150794_a.equals(var1);
   }

   public int compareTo(ByteBuf var1) {
      return this.field_150794_a.compareTo(var1);
   }

   public String toString() {
      return this.field_150794_a.toString();
   }

   public ByteBuf retain(int var1) {
      return this.field_150794_a.retain(var1);
   }

   public ByteBuf retain() {
      return this.field_150794_a.retain();
   }

   public ByteBuf touch() {
      return this.field_150794_a.touch();
   }

   public ByteBuf touch(Object var1) {
      return this.field_150794_a.touch(var1);
   }

   public int refCnt() {
      return this.field_150794_a.refCnt();
   }

   public boolean release() {
      return this.field_150794_a.release();
   }

   public boolean release(int var1) {
      return this.field_150794_a.release(var1);
   }
}
