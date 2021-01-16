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
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.NoSuchElementException;

public class TextIO {
   public static final int BUFFER_SIZE = 8192;

   private TextIO() {
      super();
   }

   public static int loadInts(BufferedReader var0, int[] var1, int var2, int var3) throws IOException {
      IntArrays.ensureOffsetLength(var1, var2, var3);
      int var4 = 0;

      String var5;
      try {
         for(var4 = 0; var4 < var3 && (var5 = var0.readLine()) != null; ++var4) {
            var1[var4 + var2] = Integer.parseInt(var5.trim());
         }
      } catch (EOFException var7) {
      }

      return var4;
   }

   public static int loadInts(BufferedReader var0, int[] var1) throws IOException {
      return loadInts((BufferedReader)var0, var1, 0, var1.length);
   }

   public static int loadInts(File var0, int[] var1, int var2, int var3) throws IOException {
      BufferedReader var4 = new BufferedReader(new FileReader(var0));
      int var5 = loadInts(var4, var1, var2, var3);
      var4.close();
      return var5;
   }

   public static int loadInts(CharSequence var0, int[] var1, int var2, int var3) throws IOException {
      return loadInts(new File(var0.toString()), var1, var2, var3);
   }

   public static int loadInts(File var0, int[] var1) throws IOException {
      return loadInts((File)var0, var1, 0, var1.length);
   }

   public static int loadInts(CharSequence var0, int[] var1) throws IOException {
      return loadInts((CharSequence)var0, var1, 0, var1.length);
   }

   public static void storeInts(int[] var0, int var1, int var2, PrintStream var3) {
      IntArrays.ensureOffsetLength(var0, var1, var2);

      for(int var4 = 0; var4 < var2; ++var4) {
         var3.println(var0[var1 + var4]);
      }

   }

   public static void storeInts(int[] var0, PrintStream var1) {
      storeInts(var0, 0, var0.length, (PrintStream)var1);
   }

   public static void storeInts(int[] var0, int var1, int var2, File var3) throws IOException {
      PrintStream var4 = new PrintStream(new FastBufferedOutputStream(new FileOutputStream(var3)));
      storeInts(var0, var1, var2, var4);
      var4.close();
   }

   public static void storeInts(int[] var0, int var1, int var2, CharSequence var3) throws IOException {
      storeInts(var0, var1, var2, new File(var3.toString()));
   }

   public static void storeInts(int[] var0, File var1) throws IOException {
      storeInts(var0, 0, var0.length, (File)var1);
   }

   public static void storeInts(int[] var0, CharSequence var1) throws IOException {
      storeInts(var0, 0, var0.length, (CharSequence)var1);
   }

   public static void storeInts(IntIterator var0, PrintStream var1) {
      while(var0.hasNext()) {
         var1.println(var0.nextInt());
      }

   }

   public static void storeInts(IntIterator var0, File var1) throws IOException {
      PrintStream var2 = new PrintStream(new FastBufferedOutputStream(new FileOutputStream(var1)));
      storeInts(var0, var2);
      var2.close();
   }

   public static void storeInts(IntIterator var0, CharSequence var1) throws IOException {
      storeInts(var0, new File(var1.toString()));
   }

   public static long loadInts(BufferedReader var0, int[][] var1, long var2, long var4) throws IOException {
      IntBigArrays.ensureOffsetLength(var1, var2, var4);
      long var6 = 0L;

      try {
         for(int var9 = BigArrays.segment(var2); var9 < BigArrays.segment(var2 + var4 + 134217727L); ++var9) {
            int[] var10 = var1[var9];
            int var11 = (int)Math.min((long)var10.length, var2 + var4 - BigArrays.start(var9));

            for(int var12 = (int)Math.max(0L, var2 - BigArrays.start(var9)); var12 < var11; ++var12) {
               String var8;
               if ((var8 = var0.readLine()) == null) {
                  return var6;
               }

               var10[var12] = Integer.parseInt(var8.trim());
               ++var6;
            }
         }
      } catch (EOFException var13) {
      }

      return var6;
   }

   public static long loadInts(BufferedReader var0, int[][] var1) throws IOException {
      return loadInts(var0, var1, 0L, IntBigArrays.length(var1));
   }

   public static long loadInts(File var0, int[][] var1, long var2, long var4) throws IOException {
      BufferedReader var6 = new BufferedReader(new FileReader(var0));
      long var7 = loadInts(var6, var1, var2, var4);
      var6.close();
      return var7;
   }

   public static long loadInts(CharSequence var0, int[][] var1, long var2, long var4) throws IOException {
      return loadInts(new File(var0.toString()), var1, var2, var4);
   }

   public static long loadInts(File var0, int[][] var1) throws IOException {
      return loadInts(var0, var1, 0L, IntBigArrays.length(var1));
   }

   public static long loadInts(CharSequence var0, int[][] var1) throws IOException {
      return loadInts(var0, var1, 0L, IntBigArrays.length(var1));
   }

   public static void storeInts(int[][] var0, long var1, long var3, PrintStream var5) {
      IntBigArrays.ensureOffsetLength(var0, var1, var3);

      for(int var6 = BigArrays.segment(var1); var6 < BigArrays.segment(var1 + var3 + 134217727L); ++var6) {
         int[] var7 = var0[var6];
         int var8 = (int)Math.min((long)var7.length, var1 + var3 - BigArrays.start(var6));

         for(int var9 = (int)Math.max(0L, var1 - BigArrays.start(var6)); var9 < var8; ++var9) {
            var5.println(var7[var9]);
         }
      }

   }

   public static void storeInts(int[][] var0, PrintStream var1) {
      storeInts(var0, 0L, IntBigArrays.length(var0), var1);
   }

   public static void storeInts(int[][] var0, long var1, long var3, File var5) throws IOException {
      PrintStream var6 = new PrintStream(new FastBufferedOutputStream(new FileOutputStream(var5)));
      storeInts(var0, var1, var3, var6);
      var6.close();
   }

   public static void storeInts(int[][] var0, long var1, long var3, CharSequence var5) throws IOException {
      storeInts(var0, var1, var3, new File(var5.toString()));
   }

   public static void storeInts(int[][] var0, File var1) throws IOException {
      storeInts(var0, 0L, IntBigArrays.length(var0), var1);
   }

   public static void storeInts(int[][] var0, CharSequence var1) throws IOException {
      storeInts(var0, 0L, IntBigArrays.length(var0), var1);
   }

   public static IntIterator asIntIterator(BufferedReader var0) {
      return new TextIO.IntReaderWrapper(var0);
   }

   public static IntIterator asIntIterator(File var0) throws IOException {
      return new TextIO.IntReaderWrapper(new BufferedReader(new FileReader(var0)));
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

   public static int loadLongs(BufferedReader var0, long[] var1, int var2, int var3) throws IOException {
      LongArrays.ensureOffsetLength(var1, var2, var3);
      int var4 = 0;

      String var5;
      try {
         for(var4 = 0; var4 < var3 && (var5 = var0.readLine()) != null; ++var4) {
            var1[var4 + var2] = Long.parseLong(var5.trim());
         }
      } catch (EOFException var7) {
      }

      return var4;
   }

   public static int loadLongs(BufferedReader var0, long[] var1) throws IOException {
      return loadLongs((BufferedReader)var0, var1, 0, var1.length);
   }

   public static int loadLongs(File var0, long[] var1, int var2, int var3) throws IOException {
      BufferedReader var4 = new BufferedReader(new FileReader(var0));
      int var5 = loadLongs(var4, var1, var2, var3);
      var4.close();
      return var5;
   }

   public static int loadLongs(CharSequence var0, long[] var1, int var2, int var3) throws IOException {
      return loadLongs(new File(var0.toString()), var1, var2, var3);
   }

   public static int loadLongs(File var0, long[] var1) throws IOException {
      return loadLongs((File)var0, var1, 0, var1.length);
   }

   public static int loadLongs(CharSequence var0, long[] var1) throws IOException {
      return loadLongs((CharSequence)var0, var1, 0, var1.length);
   }

   public static void storeLongs(long[] var0, int var1, int var2, PrintStream var3) {
      LongArrays.ensureOffsetLength(var0, var1, var2);

      for(int var4 = 0; var4 < var2; ++var4) {
         var3.println(var0[var1 + var4]);
      }

   }

   public static void storeLongs(long[] var0, PrintStream var1) {
      storeLongs(var0, 0, var0.length, (PrintStream)var1);
   }

   public static void storeLongs(long[] var0, int var1, int var2, File var3) throws IOException {
      PrintStream var4 = new PrintStream(new FastBufferedOutputStream(new FileOutputStream(var3)));
      storeLongs(var0, var1, var2, var4);
      var4.close();
   }

   public static void storeLongs(long[] var0, int var1, int var2, CharSequence var3) throws IOException {
      storeLongs(var0, var1, var2, new File(var3.toString()));
   }

   public static void storeLongs(long[] var0, File var1) throws IOException {
      storeLongs(var0, 0, var0.length, (File)var1);
   }

   public static void storeLongs(long[] var0, CharSequence var1) throws IOException {
      storeLongs(var0, 0, var0.length, (CharSequence)var1);
   }

   public static void storeLongs(LongIterator var0, PrintStream var1) {
      while(var0.hasNext()) {
         var1.println(var0.nextLong());
      }

   }

   public static void storeLongs(LongIterator var0, File var1) throws IOException {
      PrintStream var2 = new PrintStream(new FastBufferedOutputStream(new FileOutputStream(var1)));
      storeLongs(var0, var2);
      var2.close();
   }

   public static void storeLongs(LongIterator var0, CharSequence var1) throws IOException {
      storeLongs(var0, new File(var1.toString()));
   }

   public static long loadLongs(BufferedReader var0, long[][] var1, long var2, long var4) throws IOException {
      LongBigArrays.ensureOffsetLength(var1, var2, var4);
      long var6 = 0L;

      try {
         for(int var9 = BigArrays.segment(var2); var9 < BigArrays.segment(var2 + var4 + 134217727L); ++var9) {
            long[] var10 = var1[var9];
            int var11 = (int)Math.min((long)var10.length, var2 + var4 - BigArrays.start(var9));

            for(int var12 = (int)Math.max(0L, var2 - BigArrays.start(var9)); var12 < var11; ++var12) {
               String var8;
               if ((var8 = var0.readLine()) == null) {
                  return var6;
               }

               var10[var12] = Long.parseLong(var8.trim());
               ++var6;
            }
         }
      } catch (EOFException var13) {
      }

      return var6;
   }

   public static long loadLongs(BufferedReader var0, long[][] var1) throws IOException {
      return loadLongs(var0, var1, 0L, LongBigArrays.length(var1));
   }

   public static long loadLongs(File var0, long[][] var1, long var2, long var4) throws IOException {
      BufferedReader var6 = new BufferedReader(new FileReader(var0));
      long var7 = loadLongs(var6, var1, var2, var4);
      var6.close();
      return var7;
   }

   public static long loadLongs(CharSequence var0, long[][] var1, long var2, long var4) throws IOException {
      return loadLongs(new File(var0.toString()), var1, var2, var4);
   }

   public static long loadLongs(File var0, long[][] var1) throws IOException {
      return loadLongs(var0, var1, 0L, LongBigArrays.length(var1));
   }

   public static long loadLongs(CharSequence var0, long[][] var1) throws IOException {
      return loadLongs(var0, var1, 0L, LongBigArrays.length(var1));
   }

   public static void storeLongs(long[][] var0, long var1, long var3, PrintStream var5) {
      LongBigArrays.ensureOffsetLength(var0, var1, var3);

      for(int var6 = BigArrays.segment(var1); var6 < BigArrays.segment(var1 + var3 + 134217727L); ++var6) {
         long[] var7 = var0[var6];
         int var8 = (int)Math.min((long)var7.length, var1 + var3 - BigArrays.start(var6));

         for(int var9 = (int)Math.max(0L, var1 - BigArrays.start(var6)); var9 < var8; ++var9) {
            var5.println(var7[var9]);
         }
      }

   }

   public static void storeLongs(long[][] var0, PrintStream var1) {
      storeLongs(var0, 0L, LongBigArrays.length(var0), var1);
   }

   public static void storeLongs(long[][] var0, long var1, long var3, File var5) throws IOException {
      PrintStream var6 = new PrintStream(new FastBufferedOutputStream(new FileOutputStream(var5)));
      storeLongs(var0, var1, var3, var6);
      var6.close();
   }

   public static void storeLongs(long[][] var0, long var1, long var3, CharSequence var5) throws IOException {
      storeLongs(var0, var1, var3, new File(var5.toString()));
   }

   public static void storeLongs(long[][] var0, File var1) throws IOException {
      storeLongs(var0, 0L, LongBigArrays.length(var0), var1);
   }

   public static void storeLongs(long[][] var0, CharSequence var1) throws IOException {
      storeLongs(var0, 0L, LongBigArrays.length(var0), var1);
   }

   public static LongIterator asLongIterator(BufferedReader var0) {
      return new TextIO.LongReaderWrapper(var0);
   }

   public static LongIterator asLongIterator(File var0) throws IOException {
      return new TextIO.LongReaderWrapper(new BufferedReader(new FileReader(var0)));
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

   public static int loadDoubles(BufferedReader var0, double[] var1, int var2, int var3) throws IOException {
      DoubleArrays.ensureOffsetLength(var1, var2, var3);
      int var4 = 0;

      String var5;
      try {
         for(var4 = 0; var4 < var3 && (var5 = var0.readLine()) != null; ++var4) {
            var1[var4 + var2] = Double.parseDouble(var5.trim());
         }
      } catch (EOFException var7) {
      }

      return var4;
   }

   public static int loadDoubles(BufferedReader var0, double[] var1) throws IOException {
      return loadDoubles((BufferedReader)var0, var1, 0, var1.length);
   }

   public static int loadDoubles(File var0, double[] var1, int var2, int var3) throws IOException {
      BufferedReader var4 = new BufferedReader(new FileReader(var0));
      int var5 = loadDoubles(var4, var1, var2, var3);
      var4.close();
      return var5;
   }

   public static int loadDoubles(CharSequence var0, double[] var1, int var2, int var3) throws IOException {
      return loadDoubles(new File(var0.toString()), var1, var2, var3);
   }

   public static int loadDoubles(File var0, double[] var1) throws IOException {
      return loadDoubles((File)var0, var1, 0, var1.length);
   }

   public static int loadDoubles(CharSequence var0, double[] var1) throws IOException {
      return loadDoubles((CharSequence)var0, var1, 0, var1.length);
   }

   public static void storeDoubles(double[] var0, int var1, int var2, PrintStream var3) {
      DoubleArrays.ensureOffsetLength(var0, var1, var2);

      for(int var4 = 0; var4 < var2; ++var4) {
         var3.println(var0[var1 + var4]);
      }

   }

   public static void storeDoubles(double[] var0, PrintStream var1) {
      storeDoubles(var0, 0, var0.length, (PrintStream)var1);
   }

   public static void storeDoubles(double[] var0, int var1, int var2, File var3) throws IOException {
      PrintStream var4 = new PrintStream(new FastBufferedOutputStream(new FileOutputStream(var3)));
      storeDoubles(var0, var1, var2, var4);
      var4.close();
   }

   public static void storeDoubles(double[] var0, int var1, int var2, CharSequence var3) throws IOException {
      storeDoubles(var0, var1, var2, new File(var3.toString()));
   }

   public static void storeDoubles(double[] var0, File var1) throws IOException {
      storeDoubles(var0, 0, var0.length, (File)var1);
   }

   public static void storeDoubles(double[] var0, CharSequence var1) throws IOException {
      storeDoubles(var0, 0, var0.length, (CharSequence)var1);
   }

   public static void storeDoubles(DoubleIterator var0, PrintStream var1) {
      while(var0.hasNext()) {
         var1.println(var0.nextDouble());
      }

   }

   public static void storeDoubles(DoubleIterator var0, File var1) throws IOException {
      PrintStream var2 = new PrintStream(new FastBufferedOutputStream(new FileOutputStream(var1)));
      storeDoubles(var0, var2);
      var2.close();
   }

   public static void storeDoubles(DoubleIterator var0, CharSequence var1) throws IOException {
      storeDoubles(var0, new File(var1.toString()));
   }

   public static long loadDoubles(BufferedReader var0, double[][] var1, long var2, long var4) throws IOException {
      DoubleBigArrays.ensureOffsetLength(var1, var2, var4);
      long var6 = 0L;

      try {
         for(int var9 = BigArrays.segment(var2); var9 < BigArrays.segment(var2 + var4 + 134217727L); ++var9) {
            double[] var10 = var1[var9];
            int var11 = (int)Math.min((long)var10.length, var2 + var4 - BigArrays.start(var9));

            for(int var12 = (int)Math.max(0L, var2 - BigArrays.start(var9)); var12 < var11; ++var12) {
               String var8;
               if ((var8 = var0.readLine()) == null) {
                  return var6;
               }

               var10[var12] = Double.parseDouble(var8.trim());
               ++var6;
            }
         }
      } catch (EOFException var13) {
      }

      return var6;
   }

   public static long loadDoubles(BufferedReader var0, double[][] var1) throws IOException {
      return loadDoubles(var0, var1, 0L, DoubleBigArrays.length(var1));
   }

   public static long loadDoubles(File var0, double[][] var1, long var2, long var4) throws IOException {
      BufferedReader var6 = new BufferedReader(new FileReader(var0));
      long var7 = loadDoubles(var6, var1, var2, var4);
      var6.close();
      return var7;
   }

   public static long loadDoubles(CharSequence var0, double[][] var1, long var2, long var4) throws IOException {
      return loadDoubles(new File(var0.toString()), var1, var2, var4);
   }

   public static long loadDoubles(File var0, double[][] var1) throws IOException {
      return loadDoubles(var0, var1, 0L, DoubleBigArrays.length(var1));
   }

   public static long loadDoubles(CharSequence var0, double[][] var1) throws IOException {
      return loadDoubles(var0, var1, 0L, DoubleBigArrays.length(var1));
   }

   public static void storeDoubles(double[][] var0, long var1, long var3, PrintStream var5) {
      DoubleBigArrays.ensureOffsetLength(var0, var1, var3);

      for(int var6 = BigArrays.segment(var1); var6 < BigArrays.segment(var1 + var3 + 134217727L); ++var6) {
         double[] var7 = var0[var6];
         int var8 = (int)Math.min((long)var7.length, var1 + var3 - BigArrays.start(var6));

         for(int var9 = (int)Math.max(0L, var1 - BigArrays.start(var6)); var9 < var8; ++var9) {
            var5.println(var7[var9]);
         }
      }

   }

   public static void storeDoubles(double[][] var0, PrintStream var1) {
      storeDoubles(var0, 0L, DoubleBigArrays.length(var0), var1);
   }

   public static void storeDoubles(double[][] var0, long var1, long var3, File var5) throws IOException {
      PrintStream var6 = new PrintStream(new FastBufferedOutputStream(new FileOutputStream(var5)));
      storeDoubles(var0, var1, var3, var6);
      var6.close();
   }

   public static void storeDoubles(double[][] var0, long var1, long var3, CharSequence var5) throws IOException {
      storeDoubles(var0, var1, var3, new File(var5.toString()));
   }

   public static void storeDoubles(double[][] var0, File var1) throws IOException {
      storeDoubles(var0, 0L, DoubleBigArrays.length(var0), var1);
   }

   public static void storeDoubles(double[][] var0, CharSequence var1) throws IOException {
      storeDoubles(var0, 0L, DoubleBigArrays.length(var0), var1);
   }

   public static DoubleIterator asDoubleIterator(BufferedReader var0) {
      return new TextIO.DoubleReaderWrapper(var0);
   }

   public static DoubleIterator asDoubleIterator(File var0) throws IOException {
      return new TextIO.DoubleReaderWrapper(new BufferedReader(new FileReader(var0)));
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

   public static int loadBooleans(BufferedReader var0, boolean[] var1, int var2, int var3) throws IOException {
      BooleanArrays.ensureOffsetLength(var1, var2, var3);
      int var4 = 0;

      String var5;
      try {
         for(var4 = 0; var4 < var3 && (var5 = var0.readLine()) != null; ++var4) {
            var1[var4 + var2] = Boolean.parseBoolean(var5.trim());
         }
      } catch (EOFException var7) {
      }

      return var4;
   }

   public static int loadBooleans(BufferedReader var0, boolean[] var1) throws IOException {
      return loadBooleans((BufferedReader)var0, var1, 0, var1.length);
   }

   public static int loadBooleans(File var0, boolean[] var1, int var2, int var3) throws IOException {
      BufferedReader var4 = new BufferedReader(new FileReader(var0));
      int var5 = loadBooleans(var4, var1, var2, var3);
      var4.close();
      return var5;
   }

   public static int loadBooleans(CharSequence var0, boolean[] var1, int var2, int var3) throws IOException {
      return loadBooleans(new File(var0.toString()), var1, var2, var3);
   }

   public static int loadBooleans(File var0, boolean[] var1) throws IOException {
      return loadBooleans((File)var0, var1, 0, var1.length);
   }

   public static int loadBooleans(CharSequence var0, boolean[] var1) throws IOException {
      return loadBooleans((CharSequence)var0, var1, 0, var1.length);
   }

   public static void storeBooleans(boolean[] var0, int var1, int var2, PrintStream var3) {
      BooleanArrays.ensureOffsetLength(var0, var1, var2);

      for(int var4 = 0; var4 < var2; ++var4) {
         var3.println(var0[var1 + var4]);
      }

   }

   public static void storeBooleans(boolean[] var0, PrintStream var1) {
      storeBooleans(var0, 0, var0.length, (PrintStream)var1);
   }

   public static void storeBooleans(boolean[] var0, int var1, int var2, File var3) throws IOException {
      PrintStream var4 = new PrintStream(new FastBufferedOutputStream(new FileOutputStream(var3)));
      storeBooleans(var0, var1, var2, var4);
      var4.close();
   }

   public static void storeBooleans(boolean[] var0, int var1, int var2, CharSequence var3) throws IOException {
      storeBooleans(var0, var1, var2, new File(var3.toString()));
   }

   public static void storeBooleans(boolean[] var0, File var1) throws IOException {
      storeBooleans(var0, 0, var0.length, (File)var1);
   }

   public static void storeBooleans(boolean[] var0, CharSequence var1) throws IOException {
      storeBooleans(var0, 0, var0.length, (CharSequence)var1);
   }

   public static void storeBooleans(BooleanIterator var0, PrintStream var1) {
      while(var0.hasNext()) {
         var1.println(var0.nextBoolean());
      }

   }

   public static void storeBooleans(BooleanIterator var0, File var1) throws IOException {
      PrintStream var2 = new PrintStream(new FastBufferedOutputStream(new FileOutputStream(var1)));
      storeBooleans(var0, var2);
      var2.close();
   }

   public static void storeBooleans(BooleanIterator var0, CharSequence var1) throws IOException {
      storeBooleans(var0, new File(var1.toString()));
   }

   public static long loadBooleans(BufferedReader var0, boolean[][] var1, long var2, long var4) throws IOException {
      BooleanBigArrays.ensureOffsetLength(var1, var2, var4);
      long var6 = 0L;

      try {
         for(int var9 = BigArrays.segment(var2); var9 < BigArrays.segment(var2 + var4 + 134217727L); ++var9) {
            boolean[] var10 = var1[var9];
            int var11 = (int)Math.min((long)var10.length, var2 + var4 - BigArrays.start(var9));

            for(int var12 = (int)Math.max(0L, var2 - BigArrays.start(var9)); var12 < var11; ++var12) {
               String var8;
               if ((var8 = var0.readLine()) == null) {
                  return var6;
               }

               var10[var12] = Boolean.parseBoolean(var8.trim());
               ++var6;
            }
         }
      } catch (EOFException var13) {
      }

      return var6;
   }

   public static long loadBooleans(BufferedReader var0, boolean[][] var1) throws IOException {
      return loadBooleans(var0, var1, 0L, BooleanBigArrays.length(var1));
   }

   public static long loadBooleans(File var0, boolean[][] var1, long var2, long var4) throws IOException {
      BufferedReader var6 = new BufferedReader(new FileReader(var0));
      long var7 = loadBooleans(var6, var1, var2, var4);
      var6.close();
      return var7;
   }

   public static long loadBooleans(CharSequence var0, boolean[][] var1, long var2, long var4) throws IOException {
      return loadBooleans(new File(var0.toString()), var1, var2, var4);
   }

   public static long loadBooleans(File var0, boolean[][] var1) throws IOException {
      return loadBooleans(var0, var1, 0L, BooleanBigArrays.length(var1));
   }

   public static long loadBooleans(CharSequence var0, boolean[][] var1) throws IOException {
      return loadBooleans(var0, var1, 0L, BooleanBigArrays.length(var1));
   }

   public static void storeBooleans(boolean[][] var0, long var1, long var3, PrintStream var5) {
      BooleanBigArrays.ensureOffsetLength(var0, var1, var3);

      for(int var6 = BigArrays.segment(var1); var6 < BigArrays.segment(var1 + var3 + 134217727L); ++var6) {
         boolean[] var7 = var0[var6];
         int var8 = (int)Math.min((long)var7.length, var1 + var3 - BigArrays.start(var6));

         for(int var9 = (int)Math.max(0L, var1 - BigArrays.start(var6)); var9 < var8; ++var9) {
            var5.println(var7[var9]);
         }
      }

   }

   public static void storeBooleans(boolean[][] var0, PrintStream var1) {
      storeBooleans(var0, 0L, BooleanBigArrays.length(var0), var1);
   }

   public static void storeBooleans(boolean[][] var0, long var1, long var3, File var5) throws IOException {
      PrintStream var6 = new PrintStream(new FastBufferedOutputStream(new FileOutputStream(var5)));
      storeBooleans(var0, var1, var3, var6);
      var6.close();
   }

   public static void storeBooleans(boolean[][] var0, long var1, long var3, CharSequence var5) throws IOException {
      storeBooleans(var0, var1, var3, new File(var5.toString()));
   }

   public static void storeBooleans(boolean[][] var0, File var1) throws IOException {
      storeBooleans(var0, 0L, BooleanBigArrays.length(var0), var1);
   }

   public static void storeBooleans(boolean[][] var0, CharSequence var1) throws IOException {
      storeBooleans(var0, 0L, BooleanBigArrays.length(var0), var1);
   }

   public static BooleanIterator asBooleanIterator(BufferedReader var0) {
      return new TextIO.BooleanReaderWrapper(var0);
   }

   public static BooleanIterator asBooleanIterator(File var0) throws IOException {
      return new TextIO.BooleanReaderWrapper(new BufferedReader(new FileReader(var0)));
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

   public static int loadBytes(BufferedReader var0, byte[] var1, int var2, int var3) throws IOException {
      ByteArrays.ensureOffsetLength(var1, var2, var3);
      int var4 = 0;

      String var5;
      try {
         for(var4 = 0; var4 < var3 && (var5 = var0.readLine()) != null; ++var4) {
            var1[var4 + var2] = Byte.parseByte(var5.trim());
         }
      } catch (EOFException var7) {
      }

      return var4;
   }

   public static int loadBytes(BufferedReader var0, byte[] var1) throws IOException {
      return loadBytes((BufferedReader)var0, var1, 0, var1.length);
   }

   public static int loadBytes(File var0, byte[] var1, int var2, int var3) throws IOException {
      BufferedReader var4 = new BufferedReader(new FileReader(var0));
      int var5 = loadBytes(var4, var1, var2, var3);
      var4.close();
      return var5;
   }

   public static int loadBytes(CharSequence var0, byte[] var1, int var2, int var3) throws IOException {
      return loadBytes(new File(var0.toString()), var1, var2, var3);
   }

   public static int loadBytes(File var0, byte[] var1) throws IOException {
      return loadBytes((File)var0, var1, 0, var1.length);
   }

   public static int loadBytes(CharSequence var0, byte[] var1) throws IOException {
      return loadBytes((CharSequence)var0, var1, 0, var1.length);
   }

   public static void storeBytes(byte[] var0, int var1, int var2, PrintStream var3) {
      ByteArrays.ensureOffsetLength(var0, var1, var2);

      for(int var4 = 0; var4 < var2; ++var4) {
         var3.println(var0[var1 + var4]);
      }

   }

   public static void storeBytes(byte[] var0, PrintStream var1) {
      storeBytes(var0, 0, var0.length, (PrintStream)var1);
   }

   public static void storeBytes(byte[] var0, int var1, int var2, File var3) throws IOException {
      PrintStream var4 = new PrintStream(new FastBufferedOutputStream(new FileOutputStream(var3)));
      storeBytes(var0, var1, var2, var4);
      var4.close();
   }

   public static void storeBytes(byte[] var0, int var1, int var2, CharSequence var3) throws IOException {
      storeBytes(var0, var1, var2, new File(var3.toString()));
   }

   public static void storeBytes(byte[] var0, File var1) throws IOException {
      storeBytes(var0, 0, var0.length, (File)var1);
   }

   public static void storeBytes(byte[] var0, CharSequence var1) throws IOException {
      storeBytes(var0, 0, var0.length, (CharSequence)var1);
   }

   public static void storeBytes(ByteIterator var0, PrintStream var1) {
      while(var0.hasNext()) {
         var1.println(var0.nextByte());
      }

   }

   public static void storeBytes(ByteIterator var0, File var1) throws IOException {
      PrintStream var2 = new PrintStream(new FastBufferedOutputStream(new FileOutputStream(var1)));
      storeBytes(var0, var2);
      var2.close();
   }

   public static void storeBytes(ByteIterator var0, CharSequence var1) throws IOException {
      storeBytes(var0, new File(var1.toString()));
   }

   public static long loadBytes(BufferedReader var0, byte[][] var1, long var2, long var4) throws IOException {
      ByteBigArrays.ensureOffsetLength(var1, var2, var4);
      long var6 = 0L;

      try {
         for(int var9 = BigArrays.segment(var2); var9 < BigArrays.segment(var2 + var4 + 134217727L); ++var9) {
            byte[] var10 = var1[var9];
            int var11 = (int)Math.min((long)var10.length, var2 + var4 - BigArrays.start(var9));

            for(int var12 = (int)Math.max(0L, var2 - BigArrays.start(var9)); var12 < var11; ++var12) {
               String var8;
               if ((var8 = var0.readLine()) == null) {
                  return var6;
               }

               var10[var12] = Byte.parseByte(var8.trim());
               ++var6;
            }
         }
      } catch (EOFException var13) {
      }

      return var6;
   }

   public static long loadBytes(BufferedReader var0, byte[][] var1) throws IOException {
      return loadBytes(var0, var1, 0L, ByteBigArrays.length(var1));
   }

   public static long loadBytes(File var0, byte[][] var1, long var2, long var4) throws IOException {
      BufferedReader var6 = new BufferedReader(new FileReader(var0));
      long var7 = loadBytes(var6, var1, var2, var4);
      var6.close();
      return var7;
   }

   public static long loadBytes(CharSequence var0, byte[][] var1, long var2, long var4) throws IOException {
      return loadBytes(new File(var0.toString()), var1, var2, var4);
   }

   public static long loadBytes(File var0, byte[][] var1) throws IOException {
      return loadBytes(var0, var1, 0L, ByteBigArrays.length(var1));
   }

   public static long loadBytes(CharSequence var0, byte[][] var1) throws IOException {
      return loadBytes(var0, var1, 0L, ByteBigArrays.length(var1));
   }

   public static void storeBytes(byte[][] var0, long var1, long var3, PrintStream var5) {
      ByteBigArrays.ensureOffsetLength(var0, var1, var3);

      for(int var6 = BigArrays.segment(var1); var6 < BigArrays.segment(var1 + var3 + 134217727L); ++var6) {
         byte[] var7 = var0[var6];
         int var8 = (int)Math.min((long)var7.length, var1 + var3 - BigArrays.start(var6));

         for(int var9 = (int)Math.max(0L, var1 - BigArrays.start(var6)); var9 < var8; ++var9) {
            var5.println(var7[var9]);
         }
      }

   }

   public static void storeBytes(byte[][] var0, PrintStream var1) {
      storeBytes(var0, 0L, ByteBigArrays.length(var0), var1);
   }

   public static void storeBytes(byte[][] var0, long var1, long var3, File var5) throws IOException {
      PrintStream var6 = new PrintStream(new FastBufferedOutputStream(new FileOutputStream(var5)));
      storeBytes(var0, var1, var3, var6);
      var6.close();
   }

   public static void storeBytes(byte[][] var0, long var1, long var3, CharSequence var5) throws IOException {
      storeBytes(var0, var1, var3, new File(var5.toString()));
   }

   public static void storeBytes(byte[][] var0, File var1) throws IOException {
      storeBytes(var0, 0L, ByteBigArrays.length(var0), var1);
   }

   public static void storeBytes(byte[][] var0, CharSequence var1) throws IOException {
      storeBytes(var0, 0L, ByteBigArrays.length(var0), var1);
   }

   public static ByteIterator asByteIterator(BufferedReader var0) {
      return new TextIO.ByteReaderWrapper(var0);
   }

   public static ByteIterator asByteIterator(File var0) throws IOException {
      return new TextIO.ByteReaderWrapper(new BufferedReader(new FileReader(var0)));
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

   public static int loadShorts(BufferedReader var0, short[] var1, int var2, int var3) throws IOException {
      ShortArrays.ensureOffsetLength(var1, var2, var3);
      int var4 = 0;

      String var5;
      try {
         for(var4 = 0; var4 < var3 && (var5 = var0.readLine()) != null; ++var4) {
            var1[var4 + var2] = Short.parseShort(var5.trim());
         }
      } catch (EOFException var7) {
      }

      return var4;
   }

   public static int loadShorts(BufferedReader var0, short[] var1) throws IOException {
      return loadShorts((BufferedReader)var0, var1, 0, var1.length);
   }

   public static int loadShorts(File var0, short[] var1, int var2, int var3) throws IOException {
      BufferedReader var4 = new BufferedReader(new FileReader(var0));
      int var5 = loadShorts(var4, var1, var2, var3);
      var4.close();
      return var5;
   }

   public static int loadShorts(CharSequence var0, short[] var1, int var2, int var3) throws IOException {
      return loadShorts(new File(var0.toString()), var1, var2, var3);
   }

   public static int loadShorts(File var0, short[] var1) throws IOException {
      return loadShorts((File)var0, var1, 0, var1.length);
   }

   public static int loadShorts(CharSequence var0, short[] var1) throws IOException {
      return loadShorts((CharSequence)var0, var1, 0, var1.length);
   }

   public static void storeShorts(short[] var0, int var1, int var2, PrintStream var3) {
      ShortArrays.ensureOffsetLength(var0, var1, var2);

      for(int var4 = 0; var4 < var2; ++var4) {
         var3.println(var0[var1 + var4]);
      }

   }

   public static void storeShorts(short[] var0, PrintStream var1) {
      storeShorts(var0, 0, var0.length, (PrintStream)var1);
   }

   public static void storeShorts(short[] var0, int var1, int var2, File var3) throws IOException {
      PrintStream var4 = new PrintStream(new FastBufferedOutputStream(new FileOutputStream(var3)));
      storeShorts(var0, var1, var2, var4);
      var4.close();
   }

   public static void storeShorts(short[] var0, int var1, int var2, CharSequence var3) throws IOException {
      storeShorts(var0, var1, var2, new File(var3.toString()));
   }

   public static void storeShorts(short[] var0, File var1) throws IOException {
      storeShorts(var0, 0, var0.length, (File)var1);
   }

   public static void storeShorts(short[] var0, CharSequence var1) throws IOException {
      storeShorts(var0, 0, var0.length, (CharSequence)var1);
   }

   public static void storeShorts(ShortIterator var0, PrintStream var1) {
      while(var0.hasNext()) {
         var1.println(var0.nextShort());
      }

   }

   public static void storeShorts(ShortIterator var0, File var1) throws IOException {
      PrintStream var2 = new PrintStream(new FastBufferedOutputStream(new FileOutputStream(var1)));
      storeShorts(var0, var2);
      var2.close();
   }

   public static void storeShorts(ShortIterator var0, CharSequence var1) throws IOException {
      storeShorts(var0, new File(var1.toString()));
   }

   public static long loadShorts(BufferedReader var0, short[][] var1, long var2, long var4) throws IOException {
      ShortBigArrays.ensureOffsetLength(var1, var2, var4);
      long var6 = 0L;

      try {
         for(int var9 = BigArrays.segment(var2); var9 < BigArrays.segment(var2 + var4 + 134217727L); ++var9) {
            short[] var10 = var1[var9];
            int var11 = (int)Math.min((long)var10.length, var2 + var4 - BigArrays.start(var9));

            for(int var12 = (int)Math.max(0L, var2 - BigArrays.start(var9)); var12 < var11; ++var12) {
               String var8;
               if ((var8 = var0.readLine()) == null) {
                  return var6;
               }

               var10[var12] = Short.parseShort(var8.trim());
               ++var6;
            }
         }
      } catch (EOFException var13) {
      }

      return var6;
   }

   public static long loadShorts(BufferedReader var0, short[][] var1) throws IOException {
      return loadShorts(var0, var1, 0L, ShortBigArrays.length(var1));
   }

   public static long loadShorts(File var0, short[][] var1, long var2, long var4) throws IOException {
      BufferedReader var6 = new BufferedReader(new FileReader(var0));
      long var7 = loadShorts(var6, var1, var2, var4);
      var6.close();
      return var7;
   }

   public static long loadShorts(CharSequence var0, short[][] var1, long var2, long var4) throws IOException {
      return loadShorts(new File(var0.toString()), var1, var2, var4);
   }

   public static long loadShorts(File var0, short[][] var1) throws IOException {
      return loadShorts(var0, var1, 0L, ShortBigArrays.length(var1));
   }

   public static long loadShorts(CharSequence var0, short[][] var1) throws IOException {
      return loadShorts(var0, var1, 0L, ShortBigArrays.length(var1));
   }

   public static void storeShorts(short[][] var0, long var1, long var3, PrintStream var5) {
      ShortBigArrays.ensureOffsetLength(var0, var1, var3);

      for(int var6 = BigArrays.segment(var1); var6 < BigArrays.segment(var1 + var3 + 134217727L); ++var6) {
         short[] var7 = var0[var6];
         int var8 = (int)Math.min((long)var7.length, var1 + var3 - BigArrays.start(var6));

         for(int var9 = (int)Math.max(0L, var1 - BigArrays.start(var6)); var9 < var8; ++var9) {
            var5.println(var7[var9]);
         }
      }

   }

   public static void storeShorts(short[][] var0, PrintStream var1) {
      storeShorts(var0, 0L, ShortBigArrays.length(var0), var1);
   }

   public static void storeShorts(short[][] var0, long var1, long var3, File var5) throws IOException {
      PrintStream var6 = new PrintStream(new FastBufferedOutputStream(new FileOutputStream(var5)));
      storeShorts(var0, var1, var3, var6);
      var6.close();
   }

   public static void storeShorts(short[][] var0, long var1, long var3, CharSequence var5) throws IOException {
      storeShorts(var0, var1, var3, new File(var5.toString()));
   }

   public static void storeShorts(short[][] var0, File var1) throws IOException {
      storeShorts(var0, 0L, ShortBigArrays.length(var0), var1);
   }

   public static void storeShorts(short[][] var0, CharSequence var1) throws IOException {
      storeShorts(var0, 0L, ShortBigArrays.length(var0), var1);
   }

   public static ShortIterator asShortIterator(BufferedReader var0) {
      return new TextIO.ShortReaderWrapper(var0);
   }

   public static ShortIterator asShortIterator(File var0) throws IOException {
      return new TextIO.ShortReaderWrapper(new BufferedReader(new FileReader(var0)));
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

   public static int loadFloats(BufferedReader var0, float[] var1, int var2, int var3) throws IOException {
      FloatArrays.ensureOffsetLength(var1, var2, var3);
      int var4 = 0;

      String var5;
      try {
         for(var4 = 0; var4 < var3 && (var5 = var0.readLine()) != null; ++var4) {
            var1[var4 + var2] = Float.parseFloat(var5.trim());
         }
      } catch (EOFException var7) {
      }

      return var4;
   }

   public static int loadFloats(BufferedReader var0, float[] var1) throws IOException {
      return loadFloats((BufferedReader)var0, var1, 0, var1.length);
   }

   public static int loadFloats(File var0, float[] var1, int var2, int var3) throws IOException {
      BufferedReader var4 = new BufferedReader(new FileReader(var0));
      int var5 = loadFloats(var4, var1, var2, var3);
      var4.close();
      return var5;
   }

   public static int loadFloats(CharSequence var0, float[] var1, int var2, int var3) throws IOException {
      return loadFloats(new File(var0.toString()), var1, var2, var3);
   }

   public static int loadFloats(File var0, float[] var1) throws IOException {
      return loadFloats((File)var0, var1, 0, var1.length);
   }

   public static int loadFloats(CharSequence var0, float[] var1) throws IOException {
      return loadFloats((CharSequence)var0, var1, 0, var1.length);
   }

   public static void storeFloats(float[] var0, int var1, int var2, PrintStream var3) {
      FloatArrays.ensureOffsetLength(var0, var1, var2);

      for(int var4 = 0; var4 < var2; ++var4) {
         var3.println(var0[var1 + var4]);
      }

   }

   public static void storeFloats(float[] var0, PrintStream var1) {
      storeFloats(var0, 0, var0.length, (PrintStream)var1);
   }

   public static void storeFloats(float[] var0, int var1, int var2, File var3) throws IOException {
      PrintStream var4 = new PrintStream(new FastBufferedOutputStream(new FileOutputStream(var3)));
      storeFloats(var0, var1, var2, var4);
      var4.close();
   }

   public static void storeFloats(float[] var0, int var1, int var2, CharSequence var3) throws IOException {
      storeFloats(var0, var1, var2, new File(var3.toString()));
   }

   public static void storeFloats(float[] var0, File var1) throws IOException {
      storeFloats(var0, 0, var0.length, (File)var1);
   }

   public static void storeFloats(float[] var0, CharSequence var1) throws IOException {
      storeFloats(var0, 0, var0.length, (CharSequence)var1);
   }

   public static void storeFloats(FloatIterator var0, PrintStream var1) {
      while(var0.hasNext()) {
         var1.println(var0.nextFloat());
      }

   }

   public static void storeFloats(FloatIterator var0, File var1) throws IOException {
      PrintStream var2 = new PrintStream(new FastBufferedOutputStream(new FileOutputStream(var1)));
      storeFloats(var0, var2);
      var2.close();
   }

   public static void storeFloats(FloatIterator var0, CharSequence var1) throws IOException {
      storeFloats(var0, new File(var1.toString()));
   }

   public static long loadFloats(BufferedReader var0, float[][] var1, long var2, long var4) throws IOException {
      FloatBigArrays.ensureOffsetLength(var1, var2, var4);
      long var6 = 0L;

      try {
         for(int var9 = BigArrays.segment(var2); var9 < BigArrays.segment(var2 + var4 + 134217727L); ++var9) {
            float[] var10 = var1[var9];
            int var11 = (int)Math.min((long)var10.length, var2 + var4 - BigArrays.start(var9));

            for(int var12 = (int)Math.max(0L, var2 - BigArrays.start(var9)); var12 < var11; ++var12) {
               String var8;
               if ((var8 = var0.readLine()) == null) {
                  return var6;
               }

               var10[var12] = Float.parseFloat(var8.trim());
               ++var6;
            }
         }
      } catch (EOFException var13) {
      }

      return var6;
   }

   public static long loadFloats(BufferedReader var0, float[][] var1) throws IOException {
      return loadFloats(var0, var1, 0L, FloatBigArrays.length(var1));
   }

   public static long loadFloats(File var0, float[][] var1, long var2, long var4) throws IOException {
      BufferedReader var6 = new BufferedReader(new FileReader(var0));
      long var7 = loadFloats(var6, var1, var2, var4);
      var6.close();
      return var7;
   }

   public static long loadFloats(CharSequence var0, float[][] var1, long var2, long var4) throws IOException {
      return loadFloats(new File(var0.toString()), var1, var2, var4);
   }

   public static long loadFloats(File var0, float[][] var1) throws IOException {
      return loadFloats(var0, var1, 0L, FloatBigArrays.length(var1));
   }

   public static long loadFloats(CharSequence var0, float[][] var1) throws IOException {
      return loadFloats(var0, var1, 0L, FloatBigArrays.length(var1));
   }

   public static void storeFloats(float[][] var0, long var1, long var3, PrintStream var5) {
      FloatBigArrays.ensureOffsetLength(var0, var1, var3);

      for(int var6 = BigArrays.segment(var1); var6 < BigArrays.segment(var1 + var3 + 134217727L); ++var6) {
         float[] var7 = var0[var6];
         int var8 = (int)Math.min((long)var7.length, var1 + var3 - BigArrays.start(var6));

         for(int var9 = (int)Math.max(0L, var1 - BigArrays.start(var6)); var9 < var8; ++var9) {
            var5.println(var7[var9]);
         }
      }

   }

   public static void storeFloats(float[][] var0, PrintStream var1) {
      storeFloats(var0, 0L, FloatBigArrays.length(var0), var1);
   }

   public static void storeFloats(float[][] var0, long var1, long var3, File var5) throws IOException {
      PrintStream var6 = new PrintStream(new FastBufferedOutputStream(new FileOutputStream(var5)));
      storeFloats(var0, var1, var3, var6);
      var6.close();
   }

   public static void storeFloats(float[][] var0, long var1, long var3, CharSequence var5) throws IOException {
      storeFloats(var0, var1, var3, new File(var5.toString()));
   }

   public static void storeFloats(float[][] var0, File var1) throws IOException {
      storeFloats(var0, 0L, FloatBigArrays.length(var0), var1);
   }

   public static void storeFloats(float[][] var0, CharSequence var1) throws IOException {
      storeFloats(var0, 0L, FloatBigArrays.length(var0), var1);
   }

   public static FloatIterator asFloatIterator(BufferedReader var0) {
      return new TextIO.FloatReaderWrapper(var0);
   }

   public static FloatIterator asFloatIterator(File var0) throws IOException {
      return new TextIO.FloatReaderWrapper(new BufferedReader(new FileReader(var0)));
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

   private static final class FloatReaderWrapper implements FloatIterator {
      private final BufferedReader reader;
      private boolean toAdvance = true;
      private String s;
      private float next;

      public FloatReaderWrapper(BufferedReader var1) {
         super();
         this.reader = var1;
      }

      public boolean hasNext() {
         if (!this.toAdvance) {
            return this.s != null;
         } else {
            this.toAdvance = false;

            try {
               this.s = this.reader.readLine();
            } catch (EOFException var2) {
            } catch (IOException var3) {
               throw new RuntimeException(var3);
            }

            if (this.s == null) {
               return false;
            } else {
               this.next = Float.parseFloat(this.s.trim());
               return true;
            }
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

   private static final class ShortReaderWrapper implements ShortIterator {
      private final BufferedReader reader;
      private boolean toAdvance = true;
      private String s;
      private short next;

      public ShortReaderWrapper(BufferedReader var1) {
         super();
         this.reader = var1;
      }

      public boolean hasNext() {
         if (!this.toAdvance) {
            return this.s != null;
         } else {
            this.toAdvance = false;

            try {
               this.s = this.reader.readLine();
            } catch (EOFException var2) {
            } catch (IOException var3) {
               throw new RuntimeException(var3);
            }

            if (this.s == null) {
               return false;
            } else {
               this.next = Short.parseShort(this.s.trim());
               return true;
            }
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

   private static final class ByteReaderWrapper implements ByteIterator {
      private final BufferedReader reader;
      private boolean toAdvance = true;
      private String s;
      private byte next;

      public ByteReaderWrapper(BufferedReader var1) {
         super();
         this.reader = var1;
      }

      public boolean hasNext() {
         if (!this.toAdvance) {
            return this.s != null;
         } else {
            this.toAdvance = false;

            try {
               this.s = this.reader.readLine();
            } catch (EOFException var2) {
            } catch (IOException var3) {
               throw new RuntimeException(var3);
            }

            if (this.s == null) {
               return false;
            } else {
               this.next = Byte.parseByte(this.s.trim());
               return true;
            }
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

   private static final class BooleanReaderWrapper implements BooleanIterator {
      private final BufferedReader reader;
      private boolean toAdvance = true;
      private String s;
      private boolean next;

      public BooleanReaderWrapper(BufferedReader var1) {
         super();
         this.reader = var1;
      }

      public boolean hasNext() {
         if (!this.toAdvance) {
            return this.s != null;
         } else {
            this.toAdvance = false;

            try {
               this.s = this.reader.readLine();
            } catch (EOFException var2) {
            } catch (IOException var3) {
               throw new RuntimeException(var3);
            }

            if (this.s == null) {
               return false;
            } else {
               this.next = Boolean.parseBoolean(this.s.trim());
               return true;
            }
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

   private static final class DoubleReaderWrapper implements DoubleIterator {
      private final BufferedReader reader;
      private boolean toAdvance = true;
      private String s;
      private double next;

      public DoubleReaderWrapper(BufferedReader var1) {
         super();
         this.reader = var1;
      }

      public boolean hasNext() {
         if (!this.toAdvance) {
            return this.s != null;
         } else {
            this.toAdvance = false;

            try {
               this.s = this.reader.readLine();
            } catch (EOFException var2) {
            } catch (IOException var3) {
               throw new RuntimeException(var3);
            }

            if (this.s == null) {
               return false;
            } else {
               this.next = Double.parseDouble(this.s.trim());
               return true;
            }
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

   private static final class LongReaderWrapper implements LongIterator {
      private final BufferedReader reader;
      private boolean toAdvance = true;
      private String s;
      private long next;

      public LongReaderWrapper(BufferedReader var1) {
         super();
         this.reader = var1;
      }

      public boolean hasNext() {
         if (!this.toAdvance) {
            return this.s != null;
         } else {
            this.toAdvance = false;

            try {
               this.s = this.reader.readLine();
            } catch (EOFException var2) {
            } catch (IOException var3) {
               throw new RuntimeException(var3);
            }

            if (this.s == null) {
               return false;
            } else {
               this.next = Long.parseLong(this.s.trim());
               return true;
            }
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

   private static final class IntReaderWrapper implements IntIterator {
      private final BufferedReader reader;
      private boolean toAdvance = true;
      private String s;
      private int next;

      public IntReaderWrapper(BufferedReader var1) {
         super();
         this.reader = var1;
      }

      public boolean hasNext() {
         if (!this.toAdvance) {
            return this.s != null;
         } else {
            this.toAdvance = false;

            try {
               this.s = this.reader.readLine();
            } catch (EOFException var2) {
            } catch (IOException var3) {
               throw new RuntimeException(var3);
            }

            if (this.s == null) {
               return false;
            } else {
               this.next = Integer.parseInt(this.s.trim());
               return true;
            }
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
}
