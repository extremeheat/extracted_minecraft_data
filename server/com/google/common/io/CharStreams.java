package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

@Beta
@GwtIncompatible
public final class CharStreams {
   static CharBuffer createBuffer() {
      return CharBuffer.allocate(2048);
   }

   private CharStreams() {
      super();
   }

   @CanIgnoreReturnValue
   public static long copy(Readable var0, Appendable var1) throws IOException {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      CharBuffer var2 = createBuffer();
      long var3 = 0L;

      while(var0.read(var2) != -1) {
         var2.flip();
         var1.append(var2);
         var3 += (long)var2.remaining();
         var2.clear();
      }

      return var3;
   }

   public static String toString(Readable var0) throws IOException {
      return toStringBuilder(var0).toString();
   }

   private static StringBuilder toStringBuilder(Readable var0) throws IOException {
      StringBuilder var1 = new StringBuilder();
      copy(var0, var1);
      return var1;
   }

   public static List<String> readLines(Readable var0) throws IOException {
      ArrayList var1 = new ArrayList();
      LineReader var2 = new LineReader(var0);

      String var3;
      while((var3 = var2.readLine()) != null) {
         var1.add(var3);
      }

      return var1;
   }

   @CanIgnoreReturnValue
   public static <T> T readLines(Readable var0, LineProcessor<T> var1) throws IOException {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      LineReader var2 = new LineReader(var0);

      String var3;
      while((var3 = var2.readLine()) != null && var1.processLine(var3)) {
      }

      return var1.getResult();
   }

   @CanIgnoreReturnValue
   public static long exhaust(Readable var0) throws IOException {
      long var1 = 0L;
      CharBuffer var5 = createBuffer();

      long var3;
      while((var3 = (long)var0.read(var5)) != -1L) {
         var1 += var3;
         var5.clear();
      }

      return var1;
   }

   public static void skipFully(Reader var0, long var1) throws IOException {
      Preconditions.checkNotNull(var0);

      while(var1 > 0L) {
         long var3 = var0.skip(var1);
         if (var3 == 0L) {
            throw new EOFException();
         }

         var1 -= var3;
      }

   }

   public static Writer nullWriter() {
      return CharStreams.NullWriter.INSTANCE;
   }

   public static Writer asWriter(Appendable var0) {
      return (Writer)(var0 instanceof Writer ? (Writer)var0 : new AppendableWriter(var0));
   }

   private static final class NullWriter extends Writer {
      private static final CharStreams.NullWriter INSTANCE = new CharStreams.NullWriter();

      private NullWriter() {
         super();
      }

      public void write(int var1) {
      }

      public void write(char[] var1) {
         Preconditions.checkNotNull(var1);
      }

      public void write(char[] var1, int var2, int var3) {
         Preconditions.checkPositionIndexes(var2, var2 + var3, var1.length);
      }

      public void write(String var1) {
         Preconditions.checkNotNull(var1);
      }

      public void write(String var1, int var2, int var3) {
         Preconditions.checkPositionIndexes(var2, var2 + var3, var1.length());
      }

      public Writer append(CharSequence var1) {
         Preconditions.checkNotNull(var1);
         return this;
      }

      public Writer append(CharSequence var1, int var2, int var3) {
         Preconditions.checkPositionIndexes(var2, var3, var1.length());
         return this;
      }

      public Writer append(char var1) {
         return this;
      }

      public void flush() {
      }

      public void close() {
      }

      public String toString() {
         return "CharStreams.nullWriter()";
      }
   }
}
