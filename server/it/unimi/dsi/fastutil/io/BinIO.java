package it.unimi.dsi.fastutil.io;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanBigArrays;
import it.unimi.dsi.fastutil.booleans.BooleanIterable;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteBigArrays;
import it.unimi.dsi.fastutil.bytes.ByteIterable;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.chars.CharArrays;
import it.unimi.dsi.fastutil.chars.CharBigArrays;
import it.unimi.dsi.fastutil.chars.CharIterable;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleBigArrays;
import it.unimi.dsi.fastutil.doubles.DoubleIterable;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatBigArrays;
import it.unimi.dsi.fastutil.floats.FloatIterable;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntBigArrays;
import it.unimi.dsi.fastutil.ints.IntIterable;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongBigArrays;
import it.unimi.dsi.fastutil.longs.LongIterable;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortBigArrays;
import it.unimi.dsi.fastutil.shorts.ShortIterable;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.NoSuchElementException;

public class BinIO {
   private static final int MAX_IO_LENGTH = 1048576;

   private BinIO() {
      super();
   }

   public static void storeObject(Object var0, File var1) throws IOException {
      ObjectOutputStream var2 = new ObjectOutputStream(new FastBufferedOutputStream(new FileOutputStream(var1)));
      var2.writeObject(var0);
      var2.close();
   }

   public static void storeObject(Object var0, CharSequence var1) throws IOException {
      storeObject(var0, new File(var1.toString()));
   }

   public static Object loadObject(File var0) throws IOException, ClassNotFoundException {
      ObjectInputStream var1 = new ObjectInputStream(new FastBufferedInputStream(new FileInputStream(var0)));
      Object var2 = var1.readObject();
      var1.close();
      return var2;
   }

   public static Object loadObject(CharSequence var0) throws IOException, ClassNotFoundException {
      return loadObject(new File(var0.toString()));
   }

   public static void storeObject(Object var0, OutputStream var1) throws IOException {
      ObjectOutputStream var2 = new ObjectOutputStream(new FastBufferedOutputStream(var1));
      var2.writeObject(var0);
      var2.flush();
   }

   public static Object loadObject(InputStream var0) throws IOException, ClassNotFoundException {
      ObjectInputStream var1 = new ObjectInputStream(new FastBufferedInputStream(var0));
      Object var2 = var1.readObject();
      return var2;
   }

   private static int read(InputStream var0, byte[] var1, int var2, int var3) throws IOException {
      if (var3 == 0) {
         return 0;
      } else {
         int var4 = 0;

         do {
            int var5 = var0.read(var1, var2 + var4, Math.min(var3 - var4, 1048576));
            if (var5 < 0) {
               return var4;
            }

            var4 += var5;
         } while(var4 < var3);

         return var4;
      }
   }

   private static void write(OutputStream var0, byte[] var1, int var2, int var3) throws IOException {
      for(int var4 = 0; var4 < var3; var4 += Math.min(var3 - var4, 1048576)) {
         var0.write(var1, var2 + var4, Math.min(var3 - var4, 1048576));
      }

   }

   private static void write(DataOutput var0, byte[] var1, int var2, int var3) throws IOException {
      for(int var4 = 0; var4 < var3; var4 += Math.min(var3 - var4, 1048576)) {
         var0.write(var1, var2 + var4, Math.min(var3 - var4, 1048576));
      }

   }

   public static int loadBytes(InputStream var0, byte[] var1, int var2, int var3) throws IOException {
      return read(var0, var1, var2, var3);
   }

   public static int loadBytes(InputStream var0, byte[] var1) throws IOException {
      return read(var0, var1, 0, var1.length);
   }

   public static void storeBytes(byte[] var0, int var1, int var2, OutputStream var3) throws IOException {
      write(var3, var0, var1, var2);
   }

   public static void storeBytes(byte[] var0, OutputStream var1) throws IOException {
      write((OutputStream)var1, var0, 0, var0.length);
   }

   private static long read(InputStream var0, byte[][] var1, long var2, long var4) throws IOException {
      if (var4 == 0L) {
         return 0L;
      } else {
         long var6 = 0L;
         int var8 = BigArrays.segment(var2);
         int var9 = BigArrays.displacement(var2);

         do {
            int var10 = var0.read(var1[var8], var9, (int)Math.min((long)(var1[var8].length - var9), Math.min(var4 - var6, 1048576L)));
            if (var10 < 0) {
               return var6;
            }

            var6 += (long)var10;
            var9 += var10;
            if (var9 == var1[var8].length) {
               ++var8;
               var9 = 0;
            }
         } while(var6 < var4);

         return var6;
      }
   }

   private static void write(OutputStream var0, byte[][] var1, long var2, long var4) throws IOException {
      if (var4 != 0L) {
         long var6 = 0L;
         int var9 = BigArrays.segment(var2);
         int var10 = BigArrays.displacement(var2);

         do {
            int var8 = (int)Math.min((long)(var1[var9].length - var10), Math.min(var4 - var6, 1048576L));
            var0.write(var1[var9], var10, var8);
            var6 += (long)var8;
            var10 += var8;
            if (var10 == var1[var9].length) {
               ++var9;
               var10 = 0;
            }
         } while(var6 < var4);

      }
   }

   private static void write(DataOutput var0, byte[][] var1, long var2, long var4) throws IOException {
      if (var4 != 0L) {
         long var6 = 0L;
         int var9 = BigArrays.segment(var2);
         int var10 = BigArrays.displacement(var2);

         do {
            int var8 = (int)Math.min((long)(var1[var9].length - var10), Math.min(var4 - var6, 1048576L));
            var0.write(var1[var9], var10, var8);
            var6 += (long)var8;
            var10 += var8;
            if (var10 == var1[var9].length) {
               ++var9;
               var10 = 0;
            }
         } while(var6 < var4);

      }
   }

   public static long loadBytes(InputStream var0, byte[][] var1, long var2, long var4) throws IOException {
      return read(var0, var1, var2, var4);
   }

   public static long loadBytes(InputStream var0, byte[][] var1) throws IOException {
      return read(var0, var1, 0L, ByteBigArrays.length(var1));
   }

   public static void storeBytes(byte[][] var0, long var1, long var3, OutputStream var5) throws IOException {
      write(var5, var0, var1, var3);
   }

   public static void storeBytes(byte[][] var0, OutputStream var1) throws IOException {
      write(var1, var0, 0L, ByteBigArrays.length(var0));
   }

   public static int loadBytes(DataInput var0, byte[] var1, int var2, int var3) throws IOException {
      ByteArrays.ensureOffsetLength(var1, var2, var3);
      int var4 = 0;

      try {
         for(var4 = 0; var4 < var3; ++var4) {
            var1[var4 + var2] = var0.readByte();
         }
      } catch (EOFException var6) {
      }

      return var4;
   }

   public static int loadBytes(DataInput var0, byte[] var1) throws IOException {
      int var2 = 0;

      try {
         int var3 = var1.length;

         for(var2 = 0; var2 < var3; ++var2) {
            var1[var2] = var0.readByte();
         }
      } catch (EOFException var4) {
      }

      return var2;
   }

   public static int loadBytes(File var0, byte[] var1, int var2, int var3) throws IOException {
      ByteArrays.ensureOffsetLength(var1, var2, var3);
      FileInputStream var4 = new FileInputStream(var0);
      int var5 = read(var4, var1, var2, var3);
      var4.close();
      return var5;
   }

   public static int loadBytes(CharSequence var0, byte[] var1, int var2, int var3) throws IOException {
      return loadBytes(new File(var0.toString()), var1, var2, var3);
   }

   public static int loadBytes(File var0, byte[] var1) throws IOException {
      FileInputStream var2 = new FileInputStream(var0);
      int var3 = read(var2, var1, 0, var1.length);
      var2.close();
      return var3;
   }

   public static int loadBytes(CharSequence var0, byte[] var1) throws IOException {
      return loadBytes(new File(var0.toString()), var1);
   }

   public static byte[] loadBytes(File var0) throws IOException {
      FileInputStream var1 = new FileInputStream(var0);
      long var2 = var1.getChannel().size() / 1L;
      if (var2 > 2147483647L) {
         var1.close();
         throw new IllegalArgumentException("File too long: " + var1.getChannel().size() + " bytes (" + var2 + " elements)");
      } else {
         byte[] var4 = new byte[(int)var2];
         if ((long)read(var1, var4, 0, (int)var2) < var2) {
            throw new EOFException();
         } else {
            var1.close();
            return var4;
         }
      }
   }

   public static byte[] loadBytes(CharSequence var0) throws IOException {
      return loadBytes(new File(var0.toString()));
   }

   public static void storeBytes(byte[] var0, int var1, int var2, DataOutput var3) throws IOException {
      ByteArrays.ensureOffsetLength(var0, var1, var2);
      write(var3, var0, var1, var2);
   }

   public static void storeBytes(byte[] var0, DataOutput var1) throws IOException {
      write((DataOutput)var1, var0, 0, var0.length);
   }

   public static void storeBytes(byte[] var0, int var1, int var2, File var3) throws IOException {
      ByteArrays.ensureOffsetLength(var0, var1, var2);
      FastBufferedOutputStream var4 = new FastBufferedOutputStream(new FileOutputStream(var3));
      write((OutputStream)var4, var0, var1, var2);
      var4.close();
   }

   public static void storeBytes(byte[] var0, int var1, int var2, CharSequence var3) throws IOException {
      storeBytes(var0, var1, var2, new File(var3.toString()));
   }

   public static void storeBytes(byte[] var0, File var1) throws IOException {
      FastBufferedOutputStream var2 = new FastBufferedOutputStream(new FileOutputStream(var1));
      write((OutputStream)var2, var0, 0, var0.length);
      var2.close();
   }

   public static void storeBytes(byte[] var0, CharSequence var1) throws IOException {
      storeBytes(var0, new File(var1.toString()));
   }

   public static long loadBytes(DataInput var0, byte[][] var1, long var2, long var4) throws IOException {
      ByteBigArrays.ensureOffsetLength(var1, var2, var4);
      long var6 = 0L;

      try {
         for(int var8 = BigArrays.segment(var2); var8 < BigArrays.segment(var2 + var4 + 134217727L); ++var8) {
            byte[] var9 = var1[var8];
            int var10 = (int)Math.min((long)var9.length, var2 + var4 - BigArrays.start(var8));

            for(int var11 = (int)Math.max(0L, var2 - BigArrays.start(var8)); var11 < var10; ++var11) {
               var9[var11] = var0.readByte();
               ++var6;
            }
         }
      } catch (EOFException var12) {
      }

      return var6;
   }

   public static long loadBytes(DataInput var0, byte[][] var1) throws IOException {
      long var2 = 0L;

      try {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            byte[] var5 = var1[var4];
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               var5[var7] = var0.readByte();
               ++var2;
            }
         }
      } catch (EOFException var8) {
      }

      return var2;
   }

   public static long loadBytes(File var0, byte[][] var1, long var2, long var4) throws IOException {
      ByteBigArrays.ensureOffsetLength(var1, var2, var4);
      FileInputStream var6 = new FileInputStream(var0);
      long var7 = read(var6, var1, var2, var4);
      var6.close();
      return var7;
   }

   public static long loadBytes(CharSequence var0, byte[][] var1, long var2, long var4) throws IOException {
      return loadBytes(new File(var0.toString()), var1, var2, var4);
   }

   public static long loadBytes(File var0, byte[][] var1) throws IOException {
      FileInputStream var2 = new FileInputStream(var0);
      long var3 = read(var2, var1, 0L, ByteBigArrays.length(var1));
      var2.close();
      return var3;
   }

   public static long loadBytes(CharSequence var0, byte[][] var1) throws IOException {
      return loadBytes(new File(var0.toString()), var1);
   }

   public static byte[][] loadBytesBig(File var0) throws IOException {
      FileInputStream var1 = new FileInputStream(var0);
      long var2 = var1.getChannel().size() / 1L;
      byte[][] var4 = ByteBigArrays.newBigArray(var2);
      if (read(var1, var4, 0L, var2) < var2) {
         throw new EOFException();
      } else {
         var1.close();
         return var4;
      }
   }

   public static byte[][] loadBytesBig(CharSequence var0) throws IOException {
      return loadBytesBig(new File(var0.toString()));
   }

   public static void storeBytes(byte[][] var0, long var1, long var3, DataOutput var5) throws IOException {
      ByteBigArrays.ensureOffsetLength(var0, var1, var3);
      write(var5, var0, var1, var3);
   }

   public static void storeBytes(byte[][] var0, DataOutput var1) throws IOException {
      write(var1, var0, 0L, ByteBigArrays.length(var0));
   }

   public static void storeBytes(byte[][] var0, long var1, long var3, File var5) throws IOException {
      ByteBigArrays.ensureOffsetLength(var0, var1, var3);
      FastBufferedOutputStream var6 = new FastBufferedOutputStream(new FileOutputStream(var5));
      write((OutputStream)var6, var0, var1, var3);
      var6.close();
   }

   public static void storeBytes(byte[][] var0, long var1, long var3, CharSequence var5) throws IOException {
      storeBytes(var0, var1, var3, new File(var5.toString()));
   }

   public static void storeBytes(byte[][] var0, File var1) throws IOException {
      FastBufferedOutputStream var2 = new FastBufferedOutputStream(new FileOutputStream(var1));
      write((OutputStream)var2, var0, 0L, ByteBigArrays.length(var0));
      var2.close();
   }

   public static void storeBytes(byte[][] var0, CharSequence var1) throws IOException {
      storeBytes(var0, new File(var1.toString()));
   }

   public static void storeBytes(ByteIterator var0, DataOutput var1) throws IOException {
      while(var0.hasNext()) {
         var1.writeByte(var0.nextByte());
      }

   }

   public static void storeBytes(ByteIterator var0, File var1) throws IOException {
      DataOutputStream var2 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var1)));

      while(var0.hasNext()) {
         var2.writeByte(var0.nextByte());
      }

      var2.close();
   }

   public static void storeBytes(ByteIterator var0, CharSequence var1) throws IOException {
      storeBytes(var0, new File(var1.toString()));
   }

   public static ByteIterator asByteIterator(DataInput var0) {
      return new BinIO.ByteDataInputWrapper(var0);
   }

   public static ByteIterator asByteIterator(File var0) throws IOException {
      return new BinIO.ByteDataInputWrapper(new DataInputStream(new FastBufferedInputStream(new FileInputStream(var0))));
   }

   public static ByteIterator asByteIterator(CharSequence var0) throws IOException {
      return asByteIterator(new File(var0.toString()));
   }

   public static ByteIterable asByteIterable(File var0) {
      return () -> {
         try {
            return asByteIterator(var0);
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      };
   }

   public static ByteIterable asByteIterable(CharSequence var0) {
      return () -> {
         try {
            return asByteIterator(var0);
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      };
   }

   public static int loadInts(DataInput var0, int[] var1, int var2, int var3) throws IOException {
      IntArrays.ensureOffsetLength(var1, var2, var3);
      int var4 = 0;

      try {
         for(var4 = 0; var4 < var3; ++var4) {
            var1[var4 + var2] = var0.readInt();
         }
      } catch (EOFException var6) {
      }

      return var4;
   }

   public static int loadInts(DataInput var0, int[] var1) throws IOException {
      int var2 = 0;

      try {
         int var3 = var1.length;

         for(var2 = 0; var2 < var3; ++var2) {
            var1[var2] = var0.readInt();
         }
      } catch (EOFException var4) {
      }

      return var2;
   }

   public static int loadInts(File var0, int[] var1, int var2, int var3) throws IOException {
      IntArrays.ensureOffsetLength(var1, var2, var3);
      FileInputStream var4 = new FileInputStream(var0);
      DataInputStream var5 = new DataInputStream(new FastBufferedInputStream(var4));
      int var6 = 0;

      try {
         for(var6 = 0; var6 < var3; ++var6) {
            var1[var6 + var2] = var5.readInt();
         }
      } catch (EOFException var8) {
      }

      var5.close();
      return var6;
   }

   public static int loadInts(CharSequence var0, int[] var1, int var2, int var3) throws IOException {
      return loadInts(new File(var0.toString()), var1, var2, var3);
   }

   public static int loadInts(File var0, int[] var1) throws IOException {
      FileInputStream var2 = new FileInputStream(var0);
      DataInputStream var3 = new DataInputStream(new FastBufferedInputStream(var2));
      int var4 = 0;

      try {
         int var5 = var1.length;

         for(var4 = 0; var4 < var5; ++var4) {
            var1[var4] = var3.readInt();
         }
      } catch (EOFException var6) {
      }

      var3.close();
      return var4;
   }

   public static int loadInts(CharSequence var0, int[] var1) throws IOException {
      return loadInts(new File(var0.toString()), var1);
   }

   public static int[] loadInts(File var0) throws IOException {
      FileInputStream var1 = new FileInputStream(var0);
      long var2 = var1.getChannel().size() / 4L;
      if (var2 > 2147483647L) {
         var1.close();
         throw new IllegalArgumentException("File too long: " + var1.getChannel().size() + " bytes (" + var2 + " elements)");
      } else {
         int[] var4 = new int[(int)var2];
         DataInputStream var5 = new DataInputStream(new FastBufferedInputStream(var1));

         for(int var6 = 0; (long)var6 < var2; ++var6) {
            var4[var6] = var5.readInt();
         }

         var5.close();
         return var4;
      }
   }

   public static int[] loadInts(CharSequence var0) throws IOException {
      return loadInts(new File(var0.toString()));
   }

   public static void storeInts(int[] var0, int var1, int var2, DataOutput var3) throws IOException {
      IntArrays.ensureOffsetLength(var0, var1, var2);

      for(int var4 = 0; var4 < var2; ++var4) {
         var3.writeInt(var0[var1 + var4]);
      }

   }

   public static void storeInts(int[] var0, DataOutput var1) throws IOException {
      int var2 = var0.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         var1.writeInt(var0[var3]);
      }

   }

   public static void storeInts(int[] var0, int var1, int var2, File var3) throws IOException {
      IntArrays.ensureOffsetLength(var0, var1, var2);
      DataOutputStream var4 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var3)));

      for(int var5 = 0; var5 < var2; ++var5) {
         var4.writeInt(var0[var1 + var5]);
      }

      var4.close();
   }

   public static void storeInts(int[] var0, int var1, int var2, CharSequence var3) throws IOException {
      storeInts(var0, var1, var2, new File(var3.toString()));
   }

   public static void storeInts(int[] var0, File var1) throws IOException {
      int var2 = var0.length;
      DataOutputStream var3 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var1)));

      for(int var4 = 0; var4 < var2; ++var4) {
         var3.writeInt(var0[var4]);
      }

      var3.close();
   }

   public static void storeInts(int[] var0, CharSequence var1) throws IOException {
      storeInts(var0, new File(var1.toString()));
   }

   public static long loadInts(DataInput var0, int[][] var1, long var2, long var4) throws IOException {
      IntBigArrays.ensureOffsetLength(var1, var2, var4);
      long var6 = 0L;

      try {
         for(int var8 = BigArrays.segment(var2); var8 < BigArrays.segment(var2 + var4 + 134217727L); ++var8) {
            int[] var9 = var1[var8];
            int var10 = (int)Math.min((long)var9.length, var2 + var4 - BigArrays.start(var8));

            for(int var11 = (int)Math.max(0L, var2 - BigArrays.start(var8)); var11 < var10; ++var11) {
               var9[var11] = var0.readInt();
               ++var6;
            }
         }
      } catch (EOFException var12) {
      }

      return var6;
   }

   public static long loadInts(DataInput var0, int[][] var1) throws IOException {
      long var2 = 0L;

      try {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            int[] var5 = var1[var4];
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               var5[var7] = var0.readInt();
               ++var2;
            }
         }
      } catch (EOFException var8) {
      }

      return var2;
   }

   public static long loadInts(File var0, int[][] var1, long var2, long var4) throws IOException {
      IntBigArrays.ensureOffsetLength(var1, var2, var4);
      FileInputStream var6 = new FileInputStream(var0);
      DataInputStream var7 = new DataInputStream(new FastBufferedInputStream(var6));
      long var8 = 0L;

      try {
         for(int var10 = BigArrays.segment(var2); var10 < BigArrays.segment(var2 + var4 + 134217727L); ++var10) {
            int[] var11 = var1[var10];
            int var12 = (int)Math.min((long)var11.length, var2 + var4 - BigArrays.start(var10));

            for(int var13 = (int)Math.max(0L, var2 - BigArrays.start(var10)); var13 < var12; ++var13) {
               var11[var13] = var7.readInt();
               ++var8;
            }
         }
      } catch (EOFException var14) {
      }

      var7.close();
      return var8;
   }

   public static long loadInts(CharSequence var0, int[][] var1, long var2, long var4) throws IOException {
      return loadInts(new File(var0.toString()), var1, var2, var4);
   }

   public static long loadInts(File var0, int[][] var1) throws IOException {
      FileInputStream var2 = new FileInputStream(var0);
      DataInputStream var3 = new DataInputStream(new FastBufferedInputStream(var2));
      long var4 = 0L;

      try {
         for(int var6 = 0; var6 < var1.length; ++var6) {
            int[] var7 = var1[var6];
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               var7[var9] = var3.readInt();
               ++var4;
            }
         }
      } catch (EOFException var10) {
      }

      var3.close();
      return var4;
   }

   public static long loadInts(CharSequence var0, int[][] var1) throws IOException {
      return loadInts(new File(var0.toString()), var1);
   }

   public static int[][] loadIntsBig(File var0) throws IOException {
      FileInputStream var1 = new FileInputStream(var0);
      long var2 = var1.getChannel().size() / 4L;
      int[][] var4 = IntBigArrays.newBigArray(var2);
      DataInputStream var5 = new DataInputStream(new FastBufferedInputStream(var1));

      for(int var6 = 0; var6 < var4.length; ++var6) {
         int[] var7 = var4[var6];
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            var7[var9] = var5.readInt();
         }
      }

      var5.close();
      return var4;
   }

   public static int[][] loadIntsBig(CharSequence var0) throws IOException {
      return loadIntsBig(new File(var0.toString()));
   }

   public static void storeInts(int[][] var0, long var1, long var3, DataOutput var5) throws IOException {
      IntBigArrays.ensureOffsetLength(var0, var1, var3);

      for(int var6 = BigArrays.segment(var1); var6 < BigArrays.segment(var1 + var3 + 134217727L); ++var6) {
         int[] var7 = var0[var6];
         int var8 = (int)Math.min((long)var7.length, var1 + var3 - BigArrays.start(var6));

         for(int var9 = (int)Math.max(0L, var1 - BigArrays.start(var6)); var9 < var8; ++var9) {
            var5.writeInt(var7[var9]);
         }
      }

   }

   public static void storeInts(int[][] var0, DataOutput var1) throws IOException {
      for(int var2 = 0; var2 < var0.length; ++var2) {
         int[] var3 = var0[var2];
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            var1.writeInt(var3[var5]);
         }
      }

   }

   public static void storeInts(int[][] var0, long var1, long var3, File var5) throws IOException {
      IntBigArrays.ensureOffsetLength(var0, var1, var3);
      DataOutputStream var6 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var5)));

      for(int var7 = BigArrays.segment(var1); var7 < BigArrays.segment(var1 + var3 + 134217727L); ++var7) {
         int[] var8 = var0[var7];
         int var9 = (int)Math.min((long)var8.length, var1 + var3 - BigArrays.start(var7));

         for(int var10 = (int)Math.max(0L, var1 - BigArrays.start(var7)); var10 < var9; ++var10) {
            var6.writeInt(var8[var10]);
         }
      }

      var6.close();
   }

   public static void storeInts(int[][] var0, long var1, long var3, CharSequence var5) throws IOException {
      storeInts(var0, var1, var3, new File(var5.toString()));
   }

   public static void storeInts(int[][] var0, File var1) throws IOException {
      DataOutputStream var2 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var1)));

      for(int var3 = 0; var3 < var0.length; ++var3) {
         int[] var4 = var0[var3];
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            var2.writeInt(var4[var6]);
         }
      }

      var2.close();
   }

   public static void storeInts(int[][] var0, CharSequence var1) throws IOException {
      storeInts(var0, new File(var1.toString()));
   }

   public static void storeInts(IntIterator var0, DataOutput var1) throws IOException {
      while(var0.hasNext()) {
         var1.writeInt(var0.nextInt());
      }

   }

   public static void storeInts(IntIterator var0, File var1) throws IOException {
      DataOutputStream var2 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var1)));

      while(var0.hasNext()) {
         var2.writeInt(var0.nextInt());
      }

      var2.close();
   }

   public static void storeInts(IntIterator var0, CharSequence var1) throws IOException {
      storeInts(var0, new File(var1.toString()));
   }

   public static IntIterator asIntIterator(DataInput var0) {
      return new BinIO.IntDataInputWrapper(var0);
   }

   public static IntIterator asIntIterator(File var0) throws IOException {
      return new BinIO.IntDataInputWrapper(new DataInputStream(new FastBufferedInputStream(new FileInputStream(var0))));
   }

   public static IntIterator asIntIterator(CharSequence var0) throws IOException {
      return asIntIterator(new File(var0.toString()));
   }

   public static IntIterable asIntIterable(File var0) {
      return () -> {
         try {
            return asIntIterator(var0);
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      };
   }

   public static IntIterable asIntIterable(CharSequence var0) {
      return () -> {
         try {
            return asIntIterator(var0);
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      };
   }

   public static int loadLongs(DataInput var0, long[] var1, int var2, int var3) throws IOException {
      LongArrays.ensureOffsetLength(var1, var2, var3);
      int var4 = 0;

      try {
         for(var4 = 0; var4 < var3; ++var4) {
            var1[var4 + var2] = var0.readLong();
         }
      } catch (EOFException var6) {
      }

      return var4;
   }

   public static int loadLongs(DataInput var0, long[] var1) throws IOException {
      int var2 = 0;

      try {
         int var3 = var1.length;

         for(var2 = 0; var2 < var3; ++var2) {
            var1[var2] = var0.readLong();
         }
      } catch (EOFException var4) {
      }

      return var2;
   }

   public static int loadLongs(File var0, long[] var1, int var2, int var3) throws IOException {
      LongArrays.ensureOffsetLength(var1, var2, var3);
      FileInputStream var4 = new FileInputStream(var0);
      DataInputStream var5 = new DataInputStream(new FastBufferedInputStream(var4));
      int var6 = 0;

      try {
         for(var6 = 0; var6 < var3; ++var6) {
            var1[var6 + var2] = var5.readLong();
         }
      } catch (EOFException var8) {
      }

      var5.close();
      return var6;
   }

   public static int loadLongs(CharSequence var0, long[] var1, int var2, int var3) throws IOException {
      return loadLongs(new File(var0.toString()), var1, var2, var3);
   }

   public static int loadLongs(File var0, long[] var1) throws IOException {
      FileInputStream var2 = new FileInputStream(var0);
      DataInputStream var3 = new DataInputStream(new FastBufferedInputStream(var2));
      int var4 = 0;

      try {
         int var5 = var1.length;

         for(var4 = 0; var4 < var5; ++var4) {
            var1[var4] = var3.readLong();
         }
      } catch (EOFException var6) {
      }

      var3.close();
      return var4;
   }

   public static int loadLongs(CharSequence var0, long[] var1) throws IOException {
      return loadLongs(new File(var0.toString()), var1);
   }

   public static long[] loadLongs(File var0) throws IOException {
      FileInputStream var1 = new FileInputStream(var0);
      long var2 = var1.getChannel().size() / 8L;
      if (var2 > 2147483647L) {
         var1.close();
         throw new IllegalArgumentException("File too long: " + var1.getChannel().size() + " bytes (" + var2 + " elements)");
      } else {
         long[] var4 = new long[(int)var2];
         DataInputStream var5 = new DataInputStream(new FastBufferedInputStream(var1));

         for(int var6 = 0; (long)var6 < var2; ++var6) {
            var4[var6] = var5.readLong();
         }

         var5.close();
         return var4;
      }
   }

   public static long[] loadLongs(CharSequence var0) throws IOException {
      return loadLongs(new File(var0.toString()));
   }

   public static void storeLongs(long[] var0, int var1, int var2, DataOutput var3) throws IOException {
      LongArrays.ensureOffsetLength(var0, var1, var2);

      for(int var4 = 0; var4 < var2; ++var4) {
         var3.writeLong(var0[var1 + var4]);
      }

   }

   public static void storeLongs(long[] var0, DataOutput var1) throws IOException {
      int var2 = var0.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         var1.writeLong(var0[var3]);
      }

   }

   public static void storeLongs(long[] var0, int var1, int var2, File var3) throws IOException {
      LongArrays.ensureOffsetLength(var0, var1, var2);
      DataOutputStream var4 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var3)));

      for(int var5 = 0; var5 < var2; ++var5) {
         var4.writeLong(var0[var1 + var5]);
      }

      var4.close();
   }

   public static void storeLongs(long[] var0, int var1, int var2, CharSequence var3) throws IOException {
      storeLongs(var0, var1, var2, new File(var3.toString()));
   }

   public static void storeLongs(long[] var0, File var1) throws IOException {
      int var2 = var0.length;
      DataOutputStream var3 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var1)));

      for(int var4 = 0; var4 < var2; ++var4) {
         var3.writeLong(var0[var4]);
      }

      var3.close();
   }

   public static void storeLongs(long[] var0, CharSequence var1) throws IOException {
      storeLongs(var0, new File(var1.toString()));
   }

   public static long loadLongs(DataInput var0, long[][] var1, long var2, long var4) throws IOException {
      LongBigArrays.ensureOffsetLength(var1, var2, var4);
      long var6 = 0L;

      try {
         for(int var8 = BigArrays.segment(var2); var8 < BigArrays.segment(var2 + var4 + 134217727L); ++var8) {
            long[] var9 = var1[var8];
            int var10 = (int)Math.min((long)var9.length, var2 + var4 - BigArrays.start(var8));

            for(int var11 = (int)Math.max(0L, var2 - BigArrays.start(var8)); var11 < var10; ++var11) {
               var9[var11] = var0.readLong();
               ++var6;
            }
         }
      } catch (EOFException var12) {
      }

      return var6;
   }

   public static long loadLongs(DataInput var0, long[][] var1) throws IOException {
      long var2 = 0L;

      try {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            long[] var5 = var1[var4];
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               var5[var7] = var0.readLong();
               ++var2;
            }
         }
      } catch (EOFException var8) {
      }

      return var2;
   }

   public static long loadLongs(File var0, long[][] var1, long var2, long var4) throws IOException {
      LongBigArrays.ensureOffsetLength(var1, var2, var4);
      FileInputStream var6 = new FileInputStream(var0);
      DataInputStream var7 = new DataInputStream(new FastBufferedInputStream(var6));
      long var8 = 0L;

      try {
         for(int var10 = BigArrays.segment(var2); var10 < BigArrays.segment(var2 + var4 + 134217727L); ++var10) {
            long[] var11 = var1[var10];
            int var12 = (int)Math.min((long)var11.length, var2 + var4 - BigArrays.start(var10));

            for(int var13 = (int)Math.max(0L, var2 - BigArrays.start(var10)); var13 < var12; ++var13) {
               var11[var13] = var7.readLong();
               ++var8;
            }
         }
      } catch (EOFException var14) {
      }

      var7.close();
      return var8;
   }

   public static long loadLongs(CharSequence var0, long[][] var1, long var2, long var4) throws IOException {
      return loadLongs(new File(var0.toString()), var1, var2, var4);
   }

   public static long loadLongs(File var0, long[][] var1) throws IOException {
      FileInputStream var2 = new FileInputStream(var0);
      DataInputStream var3 = new DataInputStream(new FastBufferedInputStream(var2));
      long var4 = 0L;

      try {
         for(int var6 = 0; var6 < var1.length; ++var6) {
            long[] var7 = var1[var6];
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               var7[var9] = var3.readLong();
               ++var4;
            }
         }
      } catch (EOFException var10) {
      }

      var3.close();
      return var4;
   }

   public static long loadLongs(CharSequence var0, long[][] var1) throws IOException {
      return loadLongs(new File(var0.toString()), var1);
   }

   public static long[][] loadLongsBig(File var0) throws IOException {
      FileInputStream var1 = new FileInputStream(var0);
      long var2 = var1.getChannel().size() / 8L;
      long[][] var4 = LongBigArrays.newBigArray(var2);
      DataInputStream var5 = new DataInputStream(new FastBufferedInputStream(var1));

      for(int var6 = 0; var6 < var4.length; ++var6) {
         long[] var7 = var4[var6];
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            var7[var9] = var5.readLong();
         }
      }

      var5.close();
      return var4;
   }

   public static long[][] loadLongsBig(CharSequence var0) throws IOException {
      return loadLongsBig(new File(var0.toString()));
   }

   public static void storeLongs(long[][] var0, long var1, long var3, DataOutput var5) throws IOException {
      LongBigArrays.ensureOffsetLength(var0, var1, var3);

      for(int var6 = BigArrays.segment(var1); var6 < BigArrays.segment(var1 + var3 + 134217727L); ++var6) {
         long[] var7 = var0[var6];
         int var8 = (int)Math.min((long)var7.length, var1 + var3 - BigArrays.start(var6));

         for(int var9 = (int)Math.max(0L, var1 - BigArrays.start(var6)); var9 < var8; ++var9) {
            var5.writeLong(var7[var9]);
         }
      }

   }

   public static void storeLongs(long[][] var0, DataOutput var1) throws IOException {
      for(int var2 = 0; var2 < var0.length; ++var2) {
         long[] var3 = var0[var2];
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            var1.writeLong(var3[var5]);
         }
      }

   }

   public static void storeLongs(long[][] var0, long var1, long var3, File var5) throws IOException {
      LongBigArrays.ensureOffsetLength(var0, var1, var3);
      DataOutputStream var6 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var5)));

      for(int var7 = BigArrays.segment(var1); var7 < BigArrays.segment(var1 + var3 + 134217727L); ++var7) {
         long[] var8 = var0[var7];
         int var9 = (int)Math.min((long)var8.length, var1 + var3 - BigArrays.start(var7));

         for(int var10 = (int)Math.max(0L, var1 - BigArrays.start(var7)); var10 < var9; ++var10) {
            var6.writeLong(var8[var10]);
         }
      }

      var6.close();
   }

   public static void storeLongs(long[][] var0, long var1, long var3, CharSequence var5) throws IOException {
      storeLongs(var0, var1, var3, new File(var5.toString()));
   }

   public static void storeLongs(long[][] var0, File var1) throws IOException {
      DataOutputStream var2 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var1)));

      for(int var3 = 0; var3 < var0.length; ++var3) {
         long[] var4 = var0[var3];
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            var2.writeLong(var4[var6]);
         }
      }

      var2.close();
   }

   public static void storeLongs(long[][] var0, CharSequence var1) throws IOException {
      storeLongs(var0, new File(var1.toString()));
   }

   public static void storeLongs(LongIterator var0, DataOutput var1) throws IOException {
      while(var0.hasNext()) {
         var1.writeLong(var0.nextLong());
      }

   }

   public static void storeLongs(LongIterator var0, File var1) throws IOException {
      DataOutputStream var2 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var1)));

      while(var0.hasNext()) {
         var2.writeLong(var0.nextLong());
      }

      var2.close();
   }

   public static void storeLongs(LongIterator var0, CharSequence var1) throws IOException {
      storeLongs(var0, new File(var1.toString()));
   }

   public static LongIterator asLongIterator(DataInput var0) {
      return new BinIO.LongDataInputWrapper(var0);
   }

   public static LongIterator asLongIterator(File var0) throws IOException {
      return new BinIO.LongDataInputWrapper(new DataInputStream(new FastBufferedInputStream(new FileInputStream(var0))));
   }

   public static LongIterator asLongIterator(CharSequence var0) throws IOException {
      return asLongIterator(new File(var0.toString()));
   }

   public static LongIterable asLongIterable(File var0) {
      return () -> {
         try {
            return asLongIterator(var0);
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      };
   }

   public static LongIterable asLongIterable(CharSequence var0) {
      return () -> {
         try {
            return asLongIterator(var0);
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      };
   }

   public static int loadDoubles(DataInput var0, double[] var1, int var2, int var3) throws IOException {
      DoubleArrays.ensureOffsetLength(var1, var2, var3);
      int var4 = 0;

      try {
         for(var4 = 0; var4 < var3; ++var4) {
            var1[var4 + var2] = var0.readDouble();
         }
      } catch (EOFException var6) {
      }

      return var4;
   }

   public static int loadDoubles(DataInput var0, double[] var1) throws IOException {
      int var2 = 0;

      try {
         int var3 = var1.length;

         for(var2 = 0; var2 < var3; ++var2) {
            var1[var2] = var0.readDouble();
         }
      } catch (EOFException var4) {
      }

      return var2;
   }

   public static int loadDoubles(File var0, double[] var1, int var2, int var3) throws IOException {
      DoubleArrays.ensureOffsetLength(var1, var2, var3);
      FileInputStream var4 = new FileInputStream(var0);
      DataInputStream var5 = new DataInputStream(new FastBufferedInputStream(var4));
      int var6 = 0;

      try {
         for(var6 = 0; var6 < var3; ++var6) {
            var1[var6 + var2] = var5.readDouble();
         }
      } catch (EOFException var8) {
      }

      var5.close();
      return var6;
   }

   public static int loadDoubles(CharSequence var0, double[] var1, int var2, int var3) throws IOException {
      return loadDoubles(new File(var0.toString()), var1, var2, var3);
   }

   public static int loadDoubles(File var0, double[] var1) throws IOException {
      FileInputStream var2 = new FileInputStream(var0);
      DataInputStream var3 = new DataInputStream(new FastBufferedInputStream(var2));
      int var4 = 0;

      try {
         int var5 = var1.length;

         for(var4 = 0; var4 < var5; ++var4) {
            var1[var4] = var3.readDouble();
         }
      } catch (EOFException var6) {
      }

      var3.close();
      return var4;
   }

   public static int loadDoubles(CharSequence var0, double[] var1) throws IOException {
      return loadDoubles(new File(var0.toString()), var1);
   }

   public static double[] loadDoubles(File var0) throws IOException {
      FileInputStream var1 = new FileInputStream(var0);
      long var2 = var1.getChannel().size() / 8L;
      if (var2 > 2147483647L) {
         var1.close();
         throw new IllegalArgumentException("File too long: " + var1.getChannel().size() + " bytes (" + var2 + " elements)");
      } else {
         double[] var4 = new double[(int)var2];
         DataInputStream var5 = new DataInputStream(new FastBufferedInputStream(var1));

         for(int var6 = 0; (long)var6 < var2; ++var6) {
            var4[var6] = var5.readDouble();
         }

         var5.close();
         return var4;
      }
   }

   public static double[] loadDoubles(CharSequence var0) throws IOException {
      return loadDoubles(new File(var0.toString()));
   }

   public static void storeDoubles(double[] var0, int var1, int var2, DataOutput var3) throws IOException {
      DoubleArrays.ensureOffsetLength(var0, var1, var2);

      for(int var4 = 0; var4 < var2; ++var4) {
         var3.writeDouble(var0[var1 + var4]);
      }

   }

   public static void storeDoubles(double[] var0, DataOutput var1) throws IOException {
      int var2 = var0.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         var1.writeDouble(var0[var3]);
      }

   }

   public static void storeDoubles(double[] var0, int var1, int var2, File var3) throws IOException {
      DoubleArrays.ensureOffsetLength(var0, var1, var2);
      DataOutputStream var4 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var3)));

      for(int var5 = 0; var5 < var2; ++var5) {
         var4.writeDouble(var0[var1 + var5]);
      }

      var4.close();
   }

   public static void storeDoubles(double[] var0, int var1, int var2, CharSequence var3) throws IOException {
      storeDoubles(var0, var1, var2, new File(var3.toString()));
   }

   public static void storeDoubles(double[] var0, File var1) throws IOException {
      int var2 = var0.length;
      DataOutputStream var3 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var1)));

      for(int var4 = 0; var4 < var2; ++var4) {
         var3.writeDouble(var0[var4]);
      }

      var3.close();
   }

   public static void storeDoubles(double[] var0, CharSequence var1) throws IOException {
      storeDoubles(var0, new File(var1.toString()));
   }

   public static long loadDoubles(DataInput var0, double[][] var1, long var2, long var4) throws IOException {
      DoubleBigArrays.ensureOffsetLength(var1, var2, var4);
      long var6 = 0L;

      try {
         for(int var8 = BigArrays.segment(var2); var8 < BigArrays.segment(var2 + var4 + 134217727L); ++var8) {
            double[] var9 = var1[var8];
            int var10 = (int)Math.min((long)var9.length, var2 + var4 - BigArrays.start(var8));

            for(int var11 = (int)Math.max(0L, var2 - BigArrays.start(var8)); var11 < var10; ++var11) {
               var9[var11] = var0.readDouble();
               ++var6;
            }
         }
      } catch (EOFException var12) {
      }

      return var6;
   }

   public static long loadDoubles(DataInput var0, double[][] var1) throws IOException {
      long var2 = 0L;

      try {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            double[] var5 = var1[var4];
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               var5[var7] = var0.readDouble();
               ++var2;
            }
         }
      } catch (EOFException var8) {
      }

      return var2;
   }

   public static long loadDoubles(File var0, double[][] var1, long var2, long var4) throws IOException {
      DoubleBigArrays.ensureOffsetLength(var1, var2, var4);
      FileInputStream var6 = new FileInputStream(var0);
      DataInputStream var7 = new DataInputStream(new FastBufferedInputStream(var6));
      long var8 = 0L;

      try {
         for(int var10 = BigArrays.segment(var2); var10 < BigArrays.segment(var2 + var4 + 134217727L); ++var10) {
            double[] var11 = var1[var10];
            int var12 = (int)Math.min((long)var11.length, var2 + var4 - BigArrays.start(var10));

            for(int var13 = (int)Math.max(0L, var2 - BigArrays.start(var10)); var13 < var12; ++var13) {
               var11[var13] = var7.readDouble();
               ++var8;
            }
         }
      } catch (EOFException var14) {
      }

      var7.close();
      return var8;
   }

   public static long loadDoubles(CharSequence var0, double[][] var1, long var2, long var4) throws IOException {
      return loadDoubles(new File(var0.toString()), var1, var2, var4);
   }

   public static long loadDoubles(File var0, double[][] var1) throws IOException {
      FileInputStream var2 = new FileInputStream(var0);
      DataInputStream var3 = new DataInputStream(new FastBufferedInputStream(var2));
      long var4 = 0L;

      try {
         for(int var6 = 0; var6 < var1.length; ++var6) {
            double[] var7 = var1[var6];
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               var7[var9] = var3.readDouble();
               ++var4;
            }
         }
      } catch (EOFException var10) {
      }

      var3.close();
      return var4;
   }

   public static long loadDoubles(CharSequence var0, double[][] var1) throws IOException {
      return loadDoubles(new File(var0.toString()), var1);
   }

   public static double[][] loadDoublesBig(File var0) throws IOException {
      FileInputStream var1 = new FileInputStream(var0);
      long var2 = var1.getChannel().size() / 8L;
      double[][] var4 = DoubleBigArrays.newBigArray(var2);
      DataInputStream var5 = new DataInputStream(new FastBufferedInputStream(var1));

      for(int var6 = 0; var6 < var4.length; ++var6) {
         double[] var7 = var4[var6];
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            var7[var9] = var5.readDouble();
         }
      }

      var5.close();
      return var4;
   }

   public static double[][] loadDoublesBig(CharSequence var0) throws IOException {
      return loadDoublesBig(new File(var0.toString()));
   }

   public static void storeDoubles(double[][] var0, long var1, long var3, DataOutput var5) throws IOException {
      DoubleBigArrays.ensureOffsetLength(var0, var1, var3);

      for(int var6 = BigArrays.segment(var1); var6 < BigArrays.segment(var1 + var3 + 134217727L); ++var6) {
         double[] var7 = var0[var6];
         int var8 = (int)Math.min((long)var7.length, var1 + var3 - BigArrays.start(var6));

         for(int var9 = (int)Math.max(0L, var1 - BigArrays.start(var6)); var9 < var8; ++var9) {
            var5.writeDouble(var7[var9]);
         }
      }

   }

   public static void storeDoubles(double[][] var0, DataOutput var1) throws IOException {
      for(int var2 = 0; var2 < var0.length; ++var2) {
         double[] var3 = var0[var2];
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            var1.writeDouble(var3[var5]);
         }
      }

   }

   public static void storeDoubles(double[][] var0, long var1, long var3, File var5) throws IOException {
      DoubleBigArrays.ensureOffsetLength(var0, var1, var3);
      DataOutputStream var6 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var5)));

      for(int var7 = BigArrays.segment(var1); var7 < BigArrays.segment(var1 + var3 + 134217727L); ++var7) {
         double[] var8 = var0[var7];
         int var9 = (int)Math.min((long)var8.length, var1 + var3 - BigArrays.start(var7));

         for(int var10 = (int)Math.max(0L, var1 - BigArrays.start(var7)); var10 < var9; ++var10) {
            var6.writeDouble(var8[var10]);
         }
      }

      var6.close();
   }

   public static void storeDoubles(double[][] var0, long var1, long var3, CharSequence var5) throws IOException {
      storeDoubles(var0, var1, var3, new File(var5.toString()));
   }

   public static void storeDoubles(double[][] var0, File var1) throws IOException {
      DataOutputStream var2 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var1)));

      for(int var3 = 0; var3 < var0.length; ++var3) {
         double[] var4 = var0[var3];
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            var2.writeDouble(var4[var6]);
         }
      }

      var2.close();
   }

   public static void storeDoubles(double[][] var0, CharSequence var1) throws IOException {
      storeDoubles(var0, new File(var1.toString()));
   }

   public static void storeDoubles(DoubleIterator var0, DataOutput var1) throws IOException {
      while(var0.hasNext()) {
         var1.writeDouble(var0.nextDouble());
      }

   }

   public static void storeDoubles(DoubleIterator var0, File var1) throws IOException {
      DataOutputStream var2 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var1)));

      while(var0.hasNext()) {
         var2.writeDouble(var0.nextDouble());
      }

      var2.close();
   }

   public static void storeDoubles(DoubleIterator var0, CharSequence var1) throws IOException {
      storeDoubles(var0, new File(var1.toString()));
   }

   public static DoubleIterator asDoubleIterator(DataInput var0) {
      return new BinIO.DoubleDataInputWrapper(var0);
   }

   public static DoubleIterator asDoubleIterator(File var0) throws IOException {
      return new BinIO.DoubleDataInputWrapper(new DataInputStream(new FastBufferedInputStream(new FileInputStream(var0))));
   }

   public static DoubleIterator asDoubleIterator(CharSequence var0) throws IOException {
      return asDoubleIterator(new File(var0.toString()));
   }

   public static DoubleIterable asDoubleIterable(File var0) {
      return () -> {
         try {
            return asDoubleIterator(var0);
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      };
   }

   public static DoubleIterable asDoubleIterable(CharSequence var0) {
      return () -> {
         try {
            return asDoubleIterator(var0);
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      };
   }

   public static int loadBooleans(DataInput var0, boolean[] var1, int var2, int var3) throws IOException {
      BooleanArrays.ensureOffsetLength(var1, var2, var3);
      int var4 = 0;

      try {
         for(var4 = 0; var4 < var3; ++var4) {
            var1[var4 + var2] = var0.readBoolean();
         }
      } catch (EOFException var6) {
      }

      return var4;
   }

   public static int loadBooleans(DataInput var0, boolean[] var1) throws IOException {
      int var2 = 0;

      try {
         int var3 = var1.length;

         for(var2 = 0; var2 < var3; ++var2) {
            var1[var2] = var0.readBoolean();
         }
      } catch (EOFException var4) {
      }

      return var2;
   }

   public static int loadBooleans(File var0, boolean[] var1, int var2, int var3) throws IOException {
      BooleanArrays.ensureOffsetLength(var1, var2, var3);
      FileInputStream var4 = new FileInputStream(var0);
      DataInputStream var5 = new DataInputStream(new FastBufferedInputStream(var4));
      int var6 = 0;

      try {
         for(var6 = 0; var6 < var3; ++var6) {
            var1[var6 + var2] = var5.readBoolean();
         }
      } catch (EOFException var8) {
      }

      var5.close();
      return var6;
   }

   public static int loadBooleans(CharSequence var0, boolean[] var1, int var2, int var3) throws IOException {
      return loadBooleans(new File(var0.toString()), var1, var2, var3);
   }

   public static int loadBooleans(File var0, boolean[] var1) throws IOException {
      FileInputStream var2 = new FileInputStream(var0);
      DataInputStream var3 = new DataInputStream(new FastBufferedInputStream(var2));
      int var4 = 0;

      try {
         int var5 = var1.length;

         for(var4 = 0; var4 < var5; ++var4) {
            var1[var4] = var3.readBoolean();
         }
      } catch (EOFException var6) {
      }

      var3.close();
      return var4;
   }

   public static int loadBooleans(CharSequence var0, boolean[] var1) throws IOException {
      return loadBooleans(new File(var0.toString()), var1);
   }

   public static boolean[] loadBooleans(File var0) throws IOException {
      FileInputStream var1 = new FileInputStream(var0);
      long var2 = var1.getChannel().size();
      if (var2 > 2147483647L) {
         var1.close();
         throw new IllegalArgumentException("File too long: " + var1.getChannel().size() + " bytes (" + var2 + " elements)");
      } else {
         boolean[] var4 = new boolean[(int)var2];
         DataInputStream var5 = new DataInputStream(new FastBufferedInputStream(var1));

         for(int var6 = 0; (long)var6 < var2; ++var6) {
            var4[var6] = var5.readBoolean();
         }

         var5.close();
         return var4;
      }
   }

   public static boolean[] loadBooleans(CharSequence var0) throws IOException {
      return loadBooleans(new File(var0.toString()));
   }

   public static void storeBooleans(boolean[] var0, int var1, int var2, DataOutput var3) throws IOException {
      BooleanArrays.ensureOffsetLength(var0, var1, var2);

      for(int var4 = 0; var4 < var2; ++var4) {
         var3.writeBoolean(var0[var1 + var4]);
      }

   }

   public static void storeBooleans(boolean[] var0, DataOutput var1) throws IOException {
      int var2 = var0.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         var1.writeBoolean(var0[var3]);
      }

   }

   public static void storeBooleans(boolean[] var0, int var1, int var2, File var3) throws IOException {
      BooleanArrays.ensureOffsetLength(var0, var1, var2);
      DataOutputStream var4 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var3)));

      for(int var5 = 0; var5 < var2; ++var5) {
         var4.writeBoolean(var0[var1 + var5]);
      }

      var4.close();
   }

   public static void storeBooleans(boolean[] var0, int var1, int var2, CharSequence var3) throws IOException {
      storeBooleans(var0, var1, var2, new File(var3.toString()));
   }

   public static void storeBooleans(boolean[] var0, File var1) throws IOException {
      int var2 = var0.length;
      DataOutputStream var3 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var1)));

      for(int var4 = 0; var4 < var2; ++var4) {
         var3.writeBoolean(var0[var4]);
      }

      var3.close();
   }

   public static void storeBooleans(boolean[] var0, CharSequence var1) throws IOException {
      storeBooleans(var0, new File(var1.toString()));
   }

   public static long loadBooleans(DataInput var0, boolean[][] var1, long var2, long var4) throws IOException {
      BooleanBigArrays.ensureOffsetLength(var1, var2, var4);
      long var6 = 0L;

      try {
         for(int var8 = BigArrays.segment(var2); var8 < BigArrays.segment(var2 + var4 + 134217727L); ++var8) {
            boolean[] var9 = var1[var8];
            int var10 = (int)Math.min((long)var9.length, var2 + var4 - BigArrays.start(var8));

            for(int var11 = (int)Math.max(0L, var2 - BigArrays.start(var8)); var11 < var10; ++var11) {
               var9[var11] = var0.readBoolean();
               ++var6;
            }
         }
      } catch (EOFException var12) {
      }

      return var6;
   }

   public static long loadBooleans(DataInput var0, boolean[][] var1) throws IOException {
      long var2 = 0L;

      try {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            boolean[] var5 = var1[var4];
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               var5[var7] = var0.readBoolean();
               ++var2;
            }
         }
      } catch (EOFException var8) {
      }

      return var2;
   }

   public static long loadBooleans(File var0, boolean[][] var1, long var2, long var4) throws IOException {
      BooleanBigArrays.ensureOffsetLength(var1, var2, var4);
      FileInputStream var6 = new FileInputStream(var0);
      DataInputStream var7 = new DataInputStream(new FastBufferedInputStream(var6));
      long var8 = 0L;

      try {
         for(int var10 = BigArrays.segment(var2); var10 < BigArrays.segment(var2 + var4 + 134217727L); ++var10) {
            boolean[] var11 = var1[var10];
            int var12 = (int)Math.min((long)var11.length, var2 + var4 - BigArrays.start(var10));

            for(int var13 = (int)Math.max(0L, var2 - BigArrays.start(var10)); var13 < var12; ++var13) {
               var11[var13] = var7.readBoolean();
               ++var8;
            }
         }
      } catch (EOFException var14) {
      }

      var7.close();
      return var8;
   }

   public static long loadBooleans(CharSequence var0, boolean[][] var1, long var2, long var4) throws IOException {
      return loadBooleans(new File(var0.toString()), var1, var2, var4);
   }

   public static long loadBooleans(File var0, boolean[][] var1) throws IOException {
      FileInputStream var2 = new FileInputStream(var0);
      DataInputStream var3 = new DataInputStream(new FastBufferedInputStream(var2));
      long var4 = 0L;

      try {
         for(int var6 = 0; var6 < var1.length; ++var6) {
            boolean[] var7 = var1[var6];
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               var7[var9] = var3.readBoolean();
               ++var4;
            }
         }
      } catch (EOFException var10) {
      }

      var3.close();
      return var4;
   }

   public static long loadBooleans(CharSequence var0, boolean[][] var1) throws IOException {
      return loadBooleans(new File(var0.toString()), var1);
   }

   public static boolean[][] loadBooleansBig(File var0) throws IOException {
      FileInputStream var1 = new FileInputStream(var0);
      long var2 = var1.getChannel().size();
      boolean[][] var4 = BooleanBigArrays.newBigArray(var2);
      DataInputStream var5 = new DataInputStream(new FastBufferedInputStream(var1));

      for(int var6 = 0; var6 < var4.length; ++var6) {
         boolean[] var7 = var4[var6];
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            var7[var9] = var5.readBoolean();
         }
      }

      var5.close();
      return var4;
   }

   public static boolean[][] loadBooleansBig(CharSequence var0) throws IOException {
      return loadBooleansBig(new File(var0.toString()));
   }

   public static void storeBooleans(boolean[][] var0, long var1, long var3, DataOutput var5) throws IOException {
      BooleanBigArrays.ensureOffsetLength(var0, var1, var3);

      for(int var6 = BigArrays.segment(var1); var6 < BigArrays.segment(var1 + var3 + 134217727L); ++var6) {
         boolean[] var7 = var0[var6];
         int var8 = (int)Math.min((long)var7.length, var1 + var3 - BigArrays.start(var6));

         for(int var9 = (int)Math.max(0L, var1 - BigArrays.start(var6)); var9 < var8; ++var9) {
            var5.writeBoolean(var7[var9]);
         }
      }

   }

   public static void storeBooleans(boolean[][] var0, DataOutput var1) throws IOException {
      for(int var2 = 0; var2 < var0.length; ++var2) {
         boolean[] var3 = var0[var2];
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            var1.writeBoolean(var3[var5]);
         }
      }

   }

   public static void storeBooleans(boolean[][] var0, long var1, long var3, File var5) throws IOException {
      BooleanBigArrays.ensureOffsetLength(var0, var1, var3);
      DataOutputStream var6 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var5)));

      for(int var7 = BigArrays.segment(var1); var7 < BigArrays.segment(var1 + var3 + 134217727L); ++var7) {
         boolean[] var8 = var0[var7];
         int var9 = (int)Math.min((long)var8.length, var1 + var3 - BigArrays.start(var7));

         for(int var10 = (int)Math.max(0L, var1 - BigArrays.start(var7)); var10 < var9; ++var10) {
            var6.writeBoolean(var8[var10]);
         }
      }

      var6.close();
   }

   public static void storeBooleans(boolean[][] var0, long var1, long var3, CharSequence var5) throws IOException {
      storeBooleans(var0, var1, var3, new File(var5.toString()));
   }

   public static void storeBooleans(boolean[][] var0, File var1) throws IOException {
      DataOutputStream var2 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var1)));

      for(int var3 = 0; var3 < var0.length; ++var3) {
         boolean[] var4 = var0[var3];
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            var2.writeBoolean(var4[var6]);
         }
      }

      var2.close();
   }

   public static void storeBooleans(boolean[][] var0, CharSequence var1) throws IOException {
      storeBooleans(var0, new File(var1.toString()));
   }

   public static void storeBooleans(BooleanIterator var0, DataOutput var1) throws IOException {
      while(var0.hasNext()) {
         var1.writeBoolean(var0.nextBoolean());
      }

   }

   public static void storeBooleans(BooleanIterator var0, File var1) throws IOException {
      DataOutputStream var2 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var1)));

      while(var0.hasNext()) {
         var2.writeBoolean(var0.nextBoolean());
      }

      var2.close();
   }

   public static void storeBooleans(BooleanIterator var0, CharSequence var1) throws IOException {
      storeBooleans(var0, new File(var1.toString()));
   }

   public static BooleanIterator asBooleanIterator(DataInput var0) {
      return new BinIO.BooleanDataInputWrapper(var0);
   }

   public static BooleanIterator asBooleanIterator(File var0) throws IOException {
      return new BinIO.BooleanDataInputWrapper(new DataInputStream(new FastBufferedInputStream(new FileInputStream(var0))));
   }

   public static BooleanIterator asBooleanIterator(CharSequence var0) throws IOException {
      return asBooleanIterator(new File(var0.toString()));
   }

   public static BooleanIterable asBooleanIterable(File var0) {
      return () -> {
         try {
            return asBooleanIterator(var0);
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      };
   }

   public static BooleanIterable asBooleanIterable(CharSequence var0) {
      return () -> {
         try {
            return asBooleanIterator(var0);
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      };
   }

   public static int loadShorts(DataInput var0, short[] var1, int var2, int var3) throws IOException {
      ShortArrays.ensureOffsetLength(var1, var2, var3);
      int var4 = 0;

      try {
         for(var4 = 0; var4 < var3; ++var4) {
            var1[var4 + var2] = var0.readShort();
         }
      } catch (EOFException var6) {
      }

      return var4;
   }

   public static int loadShorts(DataInput var0, short[] var1) throws IOException {
      int var2 = 0;

      try {
         int var3 = var1.length;

         for(var2 = 0; var2 < var3; ++var2) {
            var1[var2] = var0.readShort();
         }
      } catch (EOFException var4) {
      }

      return var2;
   }

   public static int loadShorts(File var0, short[] var1, int var2, int var3) throws IOException {
      ShortArrays.ensureOffsetLength(var1, var2, var3);
      FileInputStream var4 = new FileInputStream(var0);
      DataInputStream var5 = new DataInputStream(new FastBufferedInputStream(var4));
      int var6 = 0;

      try {
         for(var6 = 0; var6 < var3; ++var6) {
            var1[var6 + var2] = var5.readShort();
         }
      } catch (EOFException var8) {
      }

      var5.close();
      return var6;
   }

   public static int loadShorts(CharSequence var0, short[] var1, int var2, int var3) throws IOException {
      return loadShorts(new File(var0.toString()), var1, var2, var3);
   }

   public static int loadShorts(File var0, short[] var1) throws IOException {
      FileInputStream var2 = new FileInputStream(var0);
      DataInputStream var3 = new DataInputStream(new FastBufferedInputStream(var2));
      int var4 = 0;

      try {
         int var5 = var1.length;

         for(var4 = 0; var4 < var5; ++var4) {
            var1[var4] = var3.readShort();
         }
      } catch (EOFException var6) {
      }

      var3.close();
      return var4;
   }

   public static int loadShorts(CharSequence var0, short[] var1) throws IOException {
      return loadShorts(new File(var0.toString()), var1);
   }

   public static short[] loadShorts(File var0) throws IOException {
      FileInputStream var1 = new FileInputStream(var0);
      long var2 = var1.getChannel().size() / 2L;
      if (var2 > 2147483647L) {
         var1.close();
         throw new IllegalArgumentException("File too long: " + var1.getChannel().size() + " bytes (" + var2 + " elements)");
      } else {
         short[] var4 = new short[(int)var2];
         DataInputStream var5 = new DataInputStream(new FastBufferedInputStream(var1));

         for(int var6 = 0; (long)var6 < var2; ++var6) {
            var4[var6] = var5.readShort();
         }

         var5.close();
         return var4;
      }
   }

   public static short[] loadShorts(CharSequence var0) throws IOException {
      return loadShorts(new File(var0.toString()));
   }

   public static void storeShorts(short[] var0, int var1, int var2, DataOutput var3) throws IOException {
      ShortArrays.ensureOffsetLength(var0, var1, var2);

      for(int var4 = 0; var4 < var2; ++var4) {
         var3.writeShort(var0[var1 + var4]);
      }

   }

   public static void storeShorts(short[] var0, DataOutput var1) throws IOException {
      int var2 = var0.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         var1.writeShort(var0[var3]);
      }

   }

   public static void storeShorts(short[] var0, int var1, int var2, File var3) throws IOException {
      ShortArrays.ensureOffsetLength(var0, var1, var2);
      DataOutputStream var4 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var3)));

      for(int var5 = 0; var5 < var2; ++var5) {
         var4.writeShort(var0[var1 + var5]);
      }

      var4.close();
   }

   public static void storeShorts(short[] var0, int var1, int var2, CharSequence var3) throws IOException {
      storeShorts(var0, var1, var2, new File(var3.toString()));
   }

   public static void storeShorts(short[] var0, File var1) throws IOException {
      int var2 = var0.length;
      DataOutputStream var3 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var1)));

      for(int var4 = 0; var4 < var2; ++var4) {
         var3.writeShort(var0[var4]);
      }

      var3.close();
   }

   public static void storeShorts(short[] var0, CharSequence var1) throws IOException {
      storeShorts(var0, new File(var1.toString()));
   }

   public static long loadShorts(DataInput var0, short[][] var1, long var2, long var4) throws IOException {
      ShortBigArrays.ensureOffsetLength(var1, var2, var4);
      long var6 = 0L;

      try {
         for(int var8 = BigArrays.segment(var2); var8 < BigArrays.segment(var2 + var4 + 134217727L); ++var8) {
            short[] var9 = var1[var8];
            int var10 = (int)Math.min((long)var9.length, var2 + var4 - BigArrays.start(var8));

            for(int var11 = (int)Math.max(0L, var2 - BigArrays.start(var8)); var11 < var10; ++var11) {
               var9[var11] = var0.readShort();
               ++var6;
            }
         }
      } catch (EOFException var12) {
      }

      return var6;
   }

   public static long loadShorts(DataInput var0, short[][] var1) throws IOException {
      long var2 = 0L;

      try {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            short[] var5 = var1[var4];
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               var5[var7] = var0.readShort();
               ++var2;
            }
         }
      } catch (EOFException var8) {
      }

      return var2;
   }

   public static long loadShorts(File var0, short[][] var1, long var2, long var4) throws IOException {
      ShortBigArrays.ensureOffsetLength(var1, var2, var4);
      FileInputStream var6 = new FileInputStream(var0);
      DataInputStream var7 = new DataInputStream(new FastBufferedInputStream(var6));
      long var8 = 0L;

      try {
         for(int var10 = BigArrays.segment(var2); var10 < BigArrays.segment(var2 + var4 + 134217727L); ++var10) {
            short[] var11 = var1[var10];
            int var12 = (int)Math.min((long)var11.length, var2 + var4 - BigArrays.start(var10));

            for(int var13 = (int)Math.max(0L, var2 - BigArrays.start(var10)); var13 < var12; ++var13) {
               var11[var13] = var7.readShort();
               ++var8;
            }
         }
      } catch (EOFException var14) {
      }

      var7.close();
      return var8;
   }

   public static long loadShorts(CharSequence var0, short[][] var1, long var2, long var4) throws IOException {
      return loadShorts(new File(var0.toString()), var1, var2, var4);
   }

   public static long loadShorts(File var0, short[][] var1) throws IOException {
      FileInputStream var2 = new FileInputStream(var0);
      DataInputStream var3 = new DataInputStream(new FastBufferedInputStream(var2));
      long var4 = 0L;

      try {
         for(int var6 = 0; var6 < var1.length; ++var6) {
            short[] var7 = var1[var6];
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               var7[var9] = var3.readShort();
               ++var4;
            }
         }
      } catch (EOFException var10) {
      }

      var3.close();
      return var4;
   }

   public static long loadShorts(CharSequence var0, short[][] var1) throws IOException {
      return loadShorts(new File(var0.toString()), var1);
   }

   public static short[][] loadShortsBig(File var0) throws IOException {
      FileInputStream var1 = new FileInputStream(var0);
      long var2 = var1.getChannel().size() / 2L;
      short[][] var4 = ShortBigArrays.newBigArray(var2);
      DataInputStream var5 = new DataInputStream(new FastBufferedInputStream(var1));

      for(int var6 = 0; var6 < var4.length; ++var6) {
         short[] var7 = var4[var6];
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            var7[var9] = var5.readShort();
         }
      }

      var5.close();
      return var4;
   }

   public static short[][] loadShortsBig(CharSequence var0) throws IOException {
      return loadShortsBig(new File(var0.toString()));
   }

   public static void storeShorts(short[][] var0, long var1, long var3, DataOutput var5) throws IOException {
      ShortBigArrays.ensureOffsetLength(var0, var1, var3);

      for(int var6 = BigArrays.segment(var1); var6 < BigArrays.segment(var1 + var3 + 134217727L); ++var6) {
         short[] var7 = var0[var6];
         int var8 = (int)Math.min((long)var7.length, var1 + var3 - BigArrays.start(var6));

         for(int var9 = (int)Math.max(0L, var1 - BigArrays.start(var6)); var9 < var8; ++var9) {
            var5.writeShort(var7[var9]);
         }
      }

   }

   public static void storeShorts(short[][] var0, DataOutput var1) throws IOException {
      for(int var2 = 0; var2 < var0.length; ++var2) {
         short[] var3 = var0[var2];
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            var1.writeShort(var3[var5]);
         }
      }

   }

   public static void storeShorts(short[][] var0, long var1, long var3, File var5) throws IOException {
      ShortBigArrays.ensureOffsetLength(var0, var1, var3);
      DataOutputStream var6 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var5)));

      for(int var7 = BigArrays.segment(var1); var7 < BigArrays.segment(var1 + var3 + 134217727L); ++var7) {
         short[] var8 = var0[var7];
         int var9 = (int)Math.min((long)var8.length, var1 + var3 - BigArrays.start(var7));

         for(int var10 = (int)Math.max(0L, var1 - BigArrays.start(var7)); var10 < var9; ++var10) {
            var6.writeShort(var8[var10]);
         }
      }

      var6.close();
   }

   public static void storeShorts(short[][] var0, long var1, long var3, CharSequence var5) throws IOException {
      storeShorts(var0, var1, var3, new File(var5.toString()));
   }

   public static void storeShorts(short[][] var0, File var1) throws IOException {
      DataOutputStream var2 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var1)));

      for(int var3 = 0; var3 < var0.length; ++var3) {
         short[] var4 = var0[var3];
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            var2.writeShort(var4[var6]);
         }
      }

      var2.close();
   }

   public static void storeShorts(short[][] var0, CharSequence var1) throws IOException {
      storeShorts(var0, new File(var1.toString()));
   }

   public static void storeShorts(ShortIterator var0, DataOutput var1) throws IOException {
      while(var0.hasNext()) {
         var1.writeShort(var0.nextShort());
      }

   }

   public static void storeShorts(ShortIterator var0, File var1) throws IOException {
      DataOutputStream var2 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var1)));

      while(var0.hasNext()) {
         var2.writeShort(var0.nextShort());
      }

      var2.close();
   }

   public static void storeShorts(ShortIterator var0, CharSequence var1) throws IOException {
      storeShorts(var0, new File(var1.toString()));
   }

   public static ShortIterator asShortIterator(DataInput var0) {
      return new BinIO.ShortDataInputWrapper(var0);
   }

   public static ShortIterator asShortIterator(File var0) throws IOException {
      return new BinIO.ShortDataInputWrapper(new DataInputStream(new FastBufferedInputStream(new FileInputStream(var0))));
   }

   public static ShortIterator asShortIterator(CharSequence var0) throws IOException {
      return asShortIterator(new File(var0.toString()));
   }

   public static ShortIterable asShortIterable(File var0) {
      return () -> {
         try {
            return asShortIterator(var0);
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      };
   }

   public static ShortIterable asShortIterable(CharSequence var0) {
      return () -> {
         try {
            return asShortIterator(var0);
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      };
   }

   public static int loadChars(DataInput var0, char[] var1, int var2, int var3) throws IOException {
      CharArrays.ensureOffsetLength(var1, var2, var3);
      int var4 = 0;

      try {
         for(var4 = 0; var4 < var3; ++var4) {
            var1[var4 + var2] = var0.readChar();
         }
      } catch (EOFException var6) {
      }

      return var4;
   }

   public static int loadChars(DataInput var0, char[] var1) throws IOException {
      int var2 = 0;

      try {
         int var3 = var1.length;

         for(var2 = 0; var2 < var3; ++var2) {
            var1[var2] = var0.readChar();
         }
      } catch (EOFException var4) {
      }

      return var2;
   }

   public static int loadChars(File var0, char[] var1, int var2, int var3) throws IOException {
      CharArrays.ensureOffsetLength(var1, var2, var3);
      FileInputStream var4 = new FileInputStream(var0);
      DataInputStream var5 = new DataInputStream(new FastBufferedInputStream(var4));
      int var6 = 0;

      try {
         for(var6 = 0; var6 < var3; ++var6) {
            var1[var6 + var2] = var5.readChar();
         }
      } catch (EOFException var8) {
      }

      var5.close();
      return var6;
   }

   public static int loadChars(CharSequence var0, char[] var1, int var2, int var3) throws IOException {
      return loadChars(new File(var0.toString()), var1, var2, var3);
   }

   public static int loadChars(File var0, char[] var1) throws IOException {
      FileInputStream var2 = new FileInputStream(var0);
      DataInputStream var3 = new DataInputStream(new FastBufferedInputStream(var2));
      int var4 = 0;

      try {
         int var5 = var1.length;

         for(var4 = 0; var4 < var5; ++var4) {
            var1[var4] = var3.readChar();
         }
      } catch (EOFException var6) {
      }

      var3.close();
      return var4;
   }

   public static int loadChars(CharSequence var0, char[] var1) throws IOException {
      return loadChars(new File(var0.toString()), var1);
   }

   public static char[] loadChars(File var0) throws IOException {
      FileInputStream var1 = new FileInputStream(var0);
      long var2 = var1.getChannel().size() / 2L;
      if (var2 > 2147483647L) {
         var1.close();
         throw new IllegalArgumentException("File too long: " + var1.getChannel().size() + " bytes (" + var2 + " elements)");
      } else {
         char[] var4 = new char[(int)var2];
         DataInputStream var5 = new DataInputStream(new FastBufferedInputStream(var1));

         for(int var6 = 0; (long)var6 < var2; ++var6) {
            var4[var6] = var5.readChar();
         }

         var5.close();
         return var4;
      }
   }

   public static char[] loadChars(CharSequence var0) throws IOException {
      return loadChars(new File(var0.toString()));
   }

   public static void storeChars(char[] var0, int var1, int var2, DataOutput var3) throws IOException {
      CharArrays.ensureOffsetLength(var0, var1, var2);

      for(int var4 = 0; var4 < var2; ++var4) {
         var3.writeChar(var0[var1 + var4]);
      }

   }

   public static void storeChars(char[] var0, DataOutput var1) throws IOException {
      int var2 = var0.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         var1.writeChar(var0[var3]);
      }

   }

   public static void storeChars(char[] var0, int var1, int var2, File var3) throws IOException {
      CharArrays.ensureOffsetLength(var0, var1, var2);
      DataOutputStream var4 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var3)));

      for(int var5 = 0; var5 < var2; ++var5) {
         var4.writeChar(var0[var1 + var5]);
      }

      var4.close();
   }

   public static void storeChars(char[] var0, int var1, int var2, CharSequence var3) throws IOException {
      storeChars(var0, var1, var2, new File(var3.toString()));
   }

   public static void storeChars(char[] var0, File var1) throws IOException {
      int var2 = var0.length;
      DataOutputStream var3 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var1)));

      for(int var4 = 0; var4 < var2; ++var4) {
         var3.writeChar(var0[var4]);
      }

      var3.close();
   }

   public static void storeChars(char[] var0, CharSequence var1) throws IOException {
      storeChars(var0, new File(var1.toString()));
   }

   public static long loadChars(DataInput var0, char[][] var1, long var2, long var4) throws IOException {
      CharBigArrays.ensureOffsetLength(var1, var2, var4);
      long var6 = 0L;

      try {
         for(int var8 = BigArrays.segment(var2); var8 < BigArrays.segment(var2 + var4 + 134217727L); ++var8) {
            char[] var9 = var1[var8];
            int var10 = (int)Math.min((long)var9.length, var2 + var4 - BigArrays.start(var8));

            for(int var11 = (int)Math.max(0L, var2 - BigArrays.start(var8)); var11 < var10; ++var11) {
               var9[var11] = var0.readChar();
               ++var6;
            }
         }
      } catch (EOFException var12) {
      }

      return var6;
   }

   public static long loadChars(DataInput var0, char[][] var1) throws IOException {
      long var2 = 0L;

      try {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            char[] var5 = var1[var4];
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               var5[var7] = var0.readChar();
               ++var2;
            }
         }
      } catch (EOFException var8) {
      }

      return var2;
   }

   public static long loadChars(File var0, char[][] var1, long var2, long var4) throws IOException {
      CharBigArrays.ensureOffsetLength(var1, var2, var4);
      FileInputStream var6 = new FileInputStream(var0);
      DataInputStream var7 = new DataInputStream(new FastBufferedInputStream(var6));
      long var8 = 0L;

      try {
         for(int var10 = BigArrays.segment(var2); var10 < BigArrays.segment(var2 + var4 + 134217727L); ++var10) {
            char[] var11 = var1[var10];
            int var12 = (int)Math.min((long)var11.length, var2 + var4 - BigArrays.start(var10));

            for(int var13 = (int)Math.max(0L, var2 - BigArrays.start(var10)); var13 < var12; ++var13) {
               var11[var13] = var7.readChar();
               ++var8;
            }
         }
      } catch (EOFException var14) {
      }

      var7.close();
      return var8;
   }

   public static long loadChars(CharSequence var0, char[][] var1, long var2, long var4) throws IOException {
      return loadChars(new File(var0.toString()), var1, var2, var4);
   }

   public static long loadChars(File var0, char[][] var1) throws IOException {
      FileInputStream var2 = new FileInputStream(var0);
      DataInputStream var3 = new DataInputStream(new FastBufferedInputStream(var2));
      long var4 = 0L;

      try {
         for(int var6 = 0; var6 < var1.length; ++var6) {
            char[] var7 = var1[var6];
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               var7[var9] = var3.readChar();
               ++var4;
            }
         }
      } catch (EOFException var10) {
      }

      var3.close();
      return var4;
   }

   public static long loadChars(CharSequence var0, char[][] var1) throws IOException {
      return loadChars(new File(var0.toString()), var1);
   }

   public static char[][] loadCharsBig(File var0) throws IOException {
      FileInputStream var1 = new FileInputStream(var0);
      long var2 = var1.getChannel().size() / 2L;
      char[][] var4 = CharBigArrays.newBigArray(var2);
      DataInputStream var5 = new DataInputStream(new FastBufferedInputStream(var1));

      for(int var6 = 0; var6 < var4.length; ++var6) {
         char[] var7 = var4[var6];
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            var7[var9] = var5.readChar();
         }
      }

      var5.close();
      return var4;
   }

   public static char[][] loadCharsBig(CharSequence var0) throws IOException {
      return loadCharsBig(new File(var0.toString()));
   }

   public static void storeChars(char[][] var0, long var1, long var3, DataOutput var5) throws IOException {
      CharBigArrays.ensureOffsetLength(var0, var1, var3);

      for(int var6 = BigArrays.segment(var1); var6 < BigArrays.segment(var1 + var3 + 134217727L); ++var6) {
         char[] var7 = var0[var6];
         int var8 = (int)Math.min((long)var7.length, var1 + var3 - BigArrays.start(var6));

         for(int var9 = (int)Math.max(0L, var1 - BigArrays.start(var6)); var9 < var8; ++var9) {
            var5.writeChar(var7[var9]);
         }
      }

   }

   public static void storeChars(char[][] var0, DataOutput var1) throws IOException {
      for(int var2 = 0; var2 < var0.length; ++var2) {
         char[] var3 = var0[var2];
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            var1.writeChar(var3[var5]);
         }
      }

   }

   public static void storeChars(char[][] var0, long var1, long var3, File var5) throws IOException {
      CharBigArrays.ensureOffsetLength(var0, var1, var3);
      DataOutputStream var6 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var5)));

      for(int var7 = BigArrays.segment(var1); var7 < BigArrays.segment(var1 + var3 + 134217727L); ++var7) {
         char[] var8 = var0[var7];
         int var9 = (int)Math.min((long)var8.length, var1 + var3 - BigArrays.start(var7));

         for(int var10 = (int)Math.max(0L, var1 - BigArrays.start(var7)); var10 < var9; ++var10) {
            var6.writeChar(var8[var10]);
         }
      }

      var6.close();
   }

   public static void storeChars(char[][] var0, long var1, long var3, CharSequence var5) throws IOException {
      storeChars(var0, var1, var3, new File(var5.toString()));
   }

   public static void storeChars(char[][] var0, File var1) throws IOException {
      DataOutputStream var2 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var1)));

      for(int var3 = 0; var3 < var0.length; ++var3) {
         char[] var4 = var0[var3];
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            var2.writeChar(var4[var6]);
         }
      }

      var2.close();
   }

   public static void storeChars(char[][] var0, CharSequence var1) throws IOException {
      storeChars(var0, new File(var1.toString()));
   }

   public static void storeChars(CharIterator var0, DataOutput var1) throws IOException {
      while(var0.hasNext()) {
         var1.writeChar(var0.nextChar());
      }

   }

   public static void storeChars(CharIterator var0, File var1) throws IOException {
      DataOutputStream var2 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var1)));

      while(var0.hasNext()) {
         var2.writeChar(var0.nextChar());
      }

      var2.close();
   }

   public static void storeChars(CharIterator var0, CharSequence var1) throws IOException {
      storeChars(var0, new File(var1.toString()));
   }

   public static CharIterator asCharIterator(DataInput var0) {
      return new BinIO.CharDataInputWrapper(var0);
   }

   public static CharIterator asCharIterator(File var0) throws IOException {
      return new BinIO.CharDataInputWrapper(new DataInputStream(new FastBufferedInputStream(new FileInputStream(var0))));
   }

   public static CharIterator asCharIterator(CharSequence var0) throws IOException {
      return asCharIterator(new File(var0.toString()));
   }

   public static CharIterable asCharIterable(File var0) {
      return () -> {
         try {
            return asCharIterator(var0);
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      };
   }

   public static CharIterable asCharIterable(CharSequence var0) {
      return () -> {
         try {
            return asCharIterator(var0);
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      };
   }

   public static int loadFloats(DataInput var0, float[] var1, int var2, int var3) throws IOException {
      FloatArrays.ensureOffsetLength(var1, var2, var3);
      int var4 = 0;

      try {
         for(var4 = 0; var4 < var3; ++var4) {
            var1[var4 + var2] = var0.readFloat();
         }
      } catch (EOFException var6) {
      }

      return var4;
   }

   public static int loadFloats(DataInput var0, float[] var1) throws IOException {
      int var2 = 0;

      try {
         int var3 = var1.length;

         for(var2 = 0; var2 < var3; ++var2) {
            var1[var2] = var0.readFloat();
         }
      } catch (EOFException var4) {
      }

      return var2;
   }

   public static int loadFloats(File var0, float[] var1, int var2, int var3) throws IOException {
      FloatArrays.ensureOffsetLength(var1, var2, var3);
      FileInputStream var4 = new FileInputStream(var0);
      DataInputStream var5 = new DataInputStream(new FastBufferedInputStream(var4));
      int var6 = 0;

      try {
         for(var6 = 0; var6 < var3; ++var6) {
            var1[var6 + var2] = var5.readFloat();
         }
      } catch (EOFException var8) {
      }

      var5.close();
      return var6;
   }

   public static int loadFloats(CharSequence var0, float[] var1, int var2, int var3) throws IOException {
      return loadFloats(new File(var0.toString()), var1, var2, var3);
   }

   public static int loadFloats(File var0, float[] var1) throws IOException {
      FileInputStream var2 = new FileInputStream(var0);
      DataInputStream var3 = new DataInputStream(new FastBufferedInputStream(var2));
      int var4 = 0;

      try {
         int var5 = var1.length;

         for(var4 = 0; var4 < var5; ++var4) {
            var1[var4] = var3.readFloat();
         }
      } catch (EOFException var6) {
      }

      var3.close();
      return var4;
   }

   public static int loadFloats(CharSequence var0, float[] var1) throws IOException {
      return loadFloats(new File(var0.toString()), var1);
   }

   public static float[] loadFloats(File var0) throws IOException {
      FileInputStream var1 = new FileInputStream(var0);
      long var2 = var1.getChannel().size() / 4L;
      if (var2 > 2147483647L) {
         var1.close();
         throw new IllegalArgumentException("File too long: " + var1.getChannel().size() + " bytes (" + var2 + " elements)");
      } else {
         float[] var4 = new float[(int)var2];
         DataInputStream var5 = new DataInputStream(new FastBufferedInputStream(var1));

         for(int var6 = 0; (long)var6 < var2; ++var6) {
            var4[var6] = var5.readFloat();
         }

         var5.close();
         return var4;
      }
   }

   public static float[] loadFloats(CharSequence var0) throws IOException {
      return loadFloats(new File(var0.toString()));
   }

   public static void storeFloats(float[] var0, int var1, int var2, DataOutput var3) throws IOException {
      FloatArrays.ensureOffsetLength(var0, var1, var2);

      for(int var4 = 0; var4 < var2; ++var4) {
         var3.writeFloat(var0[var1 + var4]);
      }

   }

   public static void storeFloats(float[] var0, DataOutput var1) throws IOException {
      int var2 = var0.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         var1.writeFloat(var0[var3]);
      }

   }

   public static void storeFloats(float[] var0, int var1, int var2, File var3) throws IOException {
      FloatArrays.ensureOffsetLength(var0, var1, var2);
      DataOutputStream var4 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var3)));

      for(int var5 = 0; var5 < var2; ++var5) {
         var4.writeFloat(var0[var1 + var5]);
      }

      var4.close();
   }

   public static void storeFloats(float[] var0, int var1, int var2, CharSequence var3) throws IOException {
      storeFloats(var0, var1, var2, new File(var3.toString()));
   }

   public static void storeFloats(float[] var0, File var1) throws IOException {
      int var2 = var0.length;
      DataOutputStream var3 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var1)));

      for(int var4 = 0; var4 < var2; ++var4) {
         var3.writeFloat(var0[var4]);
      }

      var3.close();
   }

   public static void storeFloats(float[] var0, CharSequence var1) throws IOException {
      storeFloats(var0, new File(var1.toString()));
   }

   public static long loadFloats(DataInput var0, float[][] var1, long var2, long var4) throws IOException {
      FloatBigArrays.ensureOffsetLength(var1, var2, var4);
      long var6 = 0L;

      try {
         for(int var8 = BigArrays.segment(var2); var8 < BigArrays.segment(var2 + var4 + 134217727L); ++var8) {
            float[] var9 = var1[var8];
            int var10 = (int)Math.min((long)var9.length, var2 + var4 - BigArrays.start(var8));

            for(int var11 = (int)Math.max(0L, var2 - BigArrays.start(var8)); var11 < var10; ++var11) {
               var9[var11] = var0.readFloat();
               ++var6;
            }
         }
      } catch (EOFException var12) {
      }

      return var6;
   }

   public static long loadFloats(DataInput var0, float[][] var1) throws IOException {
      long var2 = 0L;

      try {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            float[] var5 = var1[var4];
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               var5[var7] = var0.readFloat();
               ++var2;
            }
         }
      } catch (EOFException var8) {
      }

      return var2;
   }

   public static long loadFloats(File var0, float[][] var1, long var2, long var4) throws IOException {
      FloatBigArrays.ensureOffsetLength(var1, var2, var4);
      FileInputStream var6 = new FileInputStream(var0);
      DataInputStream var7 = new DataInputStream(new FastBufferedInputStream(var6));
      long var8 = 0L;

      try {
         for(int var10 = BigArrays.segment(var2); var10 < BigArrays.segment(var2 + var4 + 134217727L); ++var10) {
            float[] var11 = var1[var10];
            int var12 = (int)Math.min((long)var11.length, var2 + var4 - BigArrays.start(var10));

            for(int var13 = (int)Math.max(0L, var2 - BigArrays.start(var10)); var13 < var12; ++var13) {
               var11[var13] = var7.readFloat();
               ++var8;
            }
         }
      } catch (EOFException var14) {
      }

      var7.close();
      return var8;
   }

   public static long loadFloats(CharSequence var0, float[][] var1, long var2, long var4) throws IOException {
      return loadFloats(new File(var0.toString()), var1, var2, var4);
   }

   public static long loadFloats(File var0, float[][] var1) throws IOException {
      FileInputStream var2 = new FileInputStream(var0);
      DataInputStream var3 = new DataInputStream(new FastBufferedInputStream(var2));
      long var4 = 0L;

      try {
         for(int var6 = 0; var6 < var1.length; ++var6) {
            float[] var7 = var1[var6];
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               var7[var9] = var3.readFloat();
               ++var4;
            }
         }
      } catch (EOFException var10) {
      }

      var3.close();
      return var4;
   }

   public static long loadFloats(CharSequence var0, float[][] var1) throws IOException {
      return loadFloats(new File(var0.toString()), var1);
   }

   public static float[][] loadFloatsBig(File var0) throws IOException {
      FileInputStream var1 = new FileInputStream(var0);
      long var2 = var1.getChannel().size() / 4L;
      float[][] var4 = FloatBigArrays.newBigArray(var2);
      DataInputStream var5 = new DataInputStream(new FastBufferedInputStream(var1));

      for(int var6 = 0; var6 < var4.length; ++var6) {
         float[] var7 = var4[var6];
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            var7[var9] = var5.readFloat();
         }
      }

      var5.close();
      return var4;
   }

   public static float[][] loadFloatsBig(CharSequence var0) throws IOException {
      return loadFloatsBig(new File(var0.toString()));
   }

   public static void storeFloats(float[][] var0, long var1, long var3, DataOutput var5) throws IOException {
      FloatBigArrays.ensureOffsetLength(var0, var1, var3);

      for(int var6 = BigArrays.segment(var1); var6 < BigArrays.segment(var1 + var3 + 134217727L); ++var6) {
         float[] var7 = var0[var6];
         int var8 = (int)Math.min((long)var7.length, var1 + var3 - BigArrays.start(var6));

         for(int var9 = (int)Math.max(0L, var1 - BigArrays.start(var6)); var9 < var8; ++var9) {
            var5.writeFloat(var7[var9]);
         }
      }

   }

   public static void storeFloats(float[][] var0, DataOutput var1) throws IOException {
      for(int var2 = 0; var2 < var0.length; ++var2) {
         float[] var3 = var0[var2];
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            var1.writeFloat(var3[var5]);
         }
      }

   }

   public static void storeFloats(float[][] var0, long var1, long var3, File var5) throws IOException {
      FloatBigArrays.ensureOffsetLength(var0, var1, var3);
      DataOutputStream var6 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var5)));

      for(int var7 = BigArrays.segment(var1); var7 < BigArrays.segment(var1 + var3 + 134217727L); ++var7) {
         float[] var8 = var0[var7];
         int var9 = (int)Math.min((long)var8.length, var1 + var3 - BigArrays.start(var7));

         for(int var10 = (int)Math.max(0L, var1 - BigArrays.start(var7)); var10 < var9; ++var10) {
            var6.writeFloat(var8[var10]);
         }
      }

      var6.close();
   }

   public static void storeFloats(float[][] var0, long var1, long var3, CharSequence var5) throws IOException {
      storeFloats(var0, var1, var3, new File(var5.toString()));
   }

   public static void storeFloats(float[][] var0, File var1) throws IOException {
      DataOutputStream var2 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var1)));

      for(int var3 = 0; var3 < var0.length; ++var3) {
         float[] var4 = var0[var3];
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            var2.writeFloat(var4[var6]);
         }
      }

      var2.close();
   }

   public static void storeFloats(float[][] var0, CharSequence var1) throws IOException {
      storeFloats(var0, new File(var1.toString()));
   }

   public static void storeFloats(FloatIterator var0, DataOutput var1) throws IOException {
      while(var0.hasNext()) {
         var1.writeFloat(var0.nextFloat());
      }

   }

   public static void storeFloats(FloatIterator var0, File var1) throws IOException {
      DataOutputStream var2 = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(var1)));

      while(var0.hasNext()) {
         var2.writeFloat(var0.nextFloat());
      }

      var2.close();
   }

   public static void storeFloats(FloatIterator var0, CharSequence var1) throws IOException {
      storeFloats(var0, new File(var1.toString()));
   }

   public static FloatIterator asFloatIterator(DataInput var0) {
      return new BinIO.FloatDataInputWrapper(var0);
   }

   public static FloatIterator asFloatIterator(File var0) throws IOException {
      return new BinIO.FloatDataInputWrapper(new DataInputStream(new FastBufferedInputStream(new FileInputStream(var0))));
   }

   public static FloatIterator asFloatIterator(CharSequence var0) throws IOException {
      return asFloatIterator(new File(var0.toString()));
   }

   public static FloatIterable asFloatIterable(File var0) {
      return () -> {
         try {
            return asFloatIterator(var0);
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      };
   }

   public static FloatIterable asFloatIterable(CharSequence var0) {
      return () -> {
         try {
            return asFloatIterator(var0);
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      };
   }

   private static final class FloatDataInputWrapper implements FloatIterator {
      private final DataInput dataInput;
      private boolean toAdvance = true;
      private boolean endOfProcess = false;
      private float next;

      public FloatDataInputWrapper(DataInput var1) {
         super();
         this.dataInput = var1;
      }

      public boolean hasNext() {
         if (!this.toAdvance) {
            return !this.endOfProcess;
         } else {
            this.toAdvance = false;

            try {
               this.next = this.dataInput.readFloat();
            } catch (EOFException var2) {
               this.endOfProcess = true;
            } catch (IOException var3) {
               throw new RuntimeException(var3);
            }

            return !this.endOfProcess;
         }
      }

      public float nextFloat() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.toAdvance = true;
            return this.next;
         }
      }
   }

   private static final class CharDataInputWrapper implements CharIterator {
      private final DataInput dataInput;
      private boolean toAdvance = true;
      private boolean endOfProcess = false;
      private char next;

      public CharDataInputWrapper(DataInput var1) {
         super();
         this.dataInput = var1;
      }

      public boolean hasNext() {
         if (!this.toAdvance) {
            return !this.endOfProcess;
         } else {
            this.toAdvance = false;

            try {
               this.next = this.dataInput.readChar();
            } catch (EOFException var2) {
               this.endOfProcess = true;
            } catch (IOException var3) {
               throw new RuntimeException(var3);
            }

            return !this.endOfProcess;
         }
      }

      public char nextChar() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.toAdvance = true;
            return this.next;
         }
      }
   }

   private static final class ShortDataInputWrapper implements ShortIterator {
      private final DataInput dataInput;
      private boolean toAdvance = true;
      private boolean endOfProcess = false;
      private short next;

      public ShortDataInputWrapper(DataInput var1) {
         super();
         this.dataInput = var1;
      }

      public boolean hasNext() {
         if (!this.toAdvance) {
            return !this.endOfProcess;
         } else {
            this.toAdvance = false;

            try {
               this.next = this.dataInput.readShort();
            } catch (EOFException var2) {
               this.endOfProcess = true;
            } catch (IOException var3) {
               throw new RuntimeException(var3);
            }

            return !this.endOfProcess;
         }
      }

      public short nextShort() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.toAdvance = true;
            return this.next;
         }
      }
   }

   private static final class BooleanDataInputWrapper implements BooleanIterator {
      private final DataInput dataInput;
      private boolean toAdvance = true;
      private boolean endOfProcess = false;
      private boolean next;

      public BooleanDataInputWrapper(DataInput var1) {
         super();
         this.dataInput = var1;
      }

      public boolean hasNext() {
         if (!this.toAdvance) {
            return !this.endOfProcess;
         } else {
            this.toAdvance = false;

            try {
               this.next = this.dataInput.readBoolean();
            } catch (EOFException var2) {
               this.endOfProcess = true;
            } catch (IOException var3) {
               throw new RuntimeException(var3);
            }

            return !this.endOfProcess;
         }
      }

      public boolean nextBoolean() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.toAdvance = true;
            return this.next;
         }
      }
   }

   private static final class DoubleDataInputWrapper implements DoubleIterator {
      private final DataInput dataInput;
      private boolean toAdvance = true;
      private boolean endOfProcess = false;
      private double next;

      public DoubleDataInputWrapper(DataInput var1) {
         super();
         this.dataInput = var1;
      }

      public boolean hasNext() {
         if (!this.toAdvance) {
            return !this.endOfProcess;
         } else {
            this.toAdvance = false;

            try {
               this.next = this.dataInput.readDouble();
            } catch (EOFException var2) {
               this.endOfProcess = true;
            } catch (IOException var3) {
               throw new RuntimeException(var3);
            }

            return !this.endOfProcess;
         }
      }

      public double nextDouble() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.toAdvance = true;
            return this.next;
         }
      }
   }

   private static final class LongDataInputWrapper implements LongIterator {
      private final DataInput dataInput;
      private boolean toAdvance = true;
      private boolean endOfProcess = false;
      private long next;

      public LongDataInputWrapper(DataInput var1) {
         super();
         this.dataInput = var1;
      }

      public boolean hasNext() {
         if (!this.toAdvance) {
            return !this.endOfProcess;
         } else {
            this.toAdvance = false;

            try {
               this.next = this.dataInput.readLong();
            } catch (EOFException var2) {
               this.endOfProcess = true;
            } catch (IOException var3) {
               throw new RuntimeException(var3);
            }

            return !this.endOfProcess;
         }
      }

      public long nextLong() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.toAdvance = true;
            return this.next;
         }
      }
   }

   private static final class IntDataInputWrapper implements IntIterator {
      private final DataInput dataInput;
      private boolean toAdvance = true;
      private boolean endOfProcess = false;
      private int next;

      public IntDataInputWrapper(DataInput var1) {
         super();
         this.dataInput = var1;
      }

      public boolean hasNext() {
         if (!this.toAdvance) {
            return !this.endOfProcess;
         } else {
            this.toAdvance = false;

            try {
               this.next = this.dataInput.readInt();
            } catch (EOFException var2) {
               this.endOfProcess = true;
            } catch (IOException var3) {
               throw new RuntimeException(var3);
            }

            return !this.endOfProcess;
         }
      }

      public int nextInt() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.toAdvance = true;
            return this.next;
         }
      }
   }

   private static final class ByteDataInputWrapper implements ByteIterator {
      private final DataInput dataInput;
      private boolean toAdvance = true;
      private boolean endOfProcess = false;
      private byte next;

      public ByteDataInputWrapper(DataInput var1) {
         super();
         this.dataInput = var1;
      }

      public boolean hasNext() {
         if (!this.toAdvance) {
            return !this.endOfProcess;
         } else {
            this.toAdvance = false;

            try {
               this.next = this.dataInput.readByte();
            } catch (EOFException var2) {
               this.endOfProcess = true;
            } catch (IOException var3) {
               throw new RuntimeException(var3);
            }

            return !this.endOfProcess;
         }
      }

      public byte nextByte() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.toAdvance = true;
            return this.next;
         }
      }
   }
}
