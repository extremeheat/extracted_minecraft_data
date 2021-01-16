package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

@Beta
@GwtIncompatible
public final class LittleEndianDataInputStream extends FilterInputStream implements DataInput {
   public LittleEndianDataInputStream(InputStream var1) {
      super((InputStream)Preconditions.checkNotNull(var1));
   }

   @CanIgnoreReturnValue
   public String readLine() {
      throw new UnsupportedOperationException("readLine is not supported");
   }

   public void readFully(byte[] var1) throws IOException {
      ByteStreams.readFully(this, var1);
   }

   public void readFully(byte[] var1, int var2, int var3) throws IOException {
      ByteStreams.readFully(this, var1, var2, var3);
   }

   public int skipBytes(int var1) throws IOException {
      return (int)this.in.skip((long)var1);
   }

   @CanIgnoreReturnValue
   public int readUnsignedByte() throws IOException {
      int var1 = this.in.read();
      if (0 > var1) {
         throw new EOFException();
      } else {
         return var1;
      }
   }

   @CanIgnoreReturnValue
   public int readUnsignedShort() throws IOException {
      byte var1 = this.readAndCheckByte();
      byte var2 = this.readAndCheckByte();
      return Ints.fromBytes((byte)0, (byte)0, var2, var1);
   }

   @CanIgnoreReturnValue
   public int readInt() throws IOException {
      byte var1 = this.readAndCheckByte();
      byte var2 = this.readAndCheckByte();
      byte var3 = this.readAndCheckByte();
      byte var4 = this.readAndCheckByte();
      return Ints.fromBytes(var4, var3, var2, var1);
   }

   @CanIgnoreReturnValue
   public long readLong() throws IOException {
      byte var1 = this.readAndCheckByte();
      byte var2 = this.readAndCheckByte();
      byte var3 = this.readAndCheckByte();
      byte var4 = this.readAndCheckByte();
      byte var5 = this.readAndCheckByte();
      byte var6 = this.readAndCheckByte();
      byte var7 = this.readAndCheckByte();
      byte var8 = this.readAndCheckByte();
      return Longs.fromBytes(var8, var7, var6, var5, var4, var3, var2, var1);
   }

   @CanIgnoreReturnValue
   public float readFloat() throws IOException {
      return Float.intBitsToFloat(this.readInt());
   }

   @CanIgnoreReturnValue
   public double readDouble() throws IOException {
      return Double.longBitsToDouble(this.readLong());
   }

   @CanIgnoreReturnValue
   public String readUTF() throws IOException {
      return (new DataInputStream(this.in)).readUTF();
   }

   @CanIgnoreReturnValue
   public short readShort() throws IOException {
      return (short)this.readUnsignedShort();
   }

   @CanIgnoreReturnValue
   public char readChar() throws IOException {
      return (char)this.readUnsignedShort();
   }

   @CanIgnoreReturnValue
   public byte readByte() throws IOException {
      return (byte)this.readUnsignedByte();
   }

   @CanIgnoreReturnValue
   public boolean readBoolean() throws IOException {
      return this.readUnsignedByte() != 0;
   }

   private byte readAndCheckByte() throws IOException, EOFException {
      int var1 = this.in.read();
      if (-1 == var1) {
         throw new EOFException();
      } else {
         return (byte)var1;
      }
   }
}
