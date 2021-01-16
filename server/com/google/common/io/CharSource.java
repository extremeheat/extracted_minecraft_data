package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Ascii;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nullable;

@GwtIncompatible
public abstract class CharSource {
   protected CharSource() {
      super();
   }

   @Beta
   public ByteSource asByteSource(Charset var1) {
      return new CharSource.AsByteSource(var1);
   }

   public abstract Reader openStream() throws IOException;

   public BufferedReader openBufferedStream() throws IOException {
      Reader var1 = this.openStream();
      return var1 instanceof BufferedReader ? (BufferedReader)var1 : new BufferedReader(var1);
   }

   @Beta
   public Optional<Long> lengthIfKnown() {
      return Optional.absent();
   }

   @Beta
   public long length() throws IOException {
      Optional var1 = this.lengthIfKnown();
      if (var1.isPresent()) {
         return (Long)var1.get();
      } else {
         Closer var2 = Closer.create();

         long var4;
         try {
            Reader var3 = (Reader)var2.register(this.openStream());
            var4 = this.countBySkipping(var3);
         } catch (Throwable var9) {
            throw var2.rethrow(var9);
         } finally {
            var2.close();
         }

         return var4;
      }
   }

   private long countBySkipping(Reader var1) throws IOException {
      long var2;
      long var4;
      for(var2 = 0L; (var4 = var1.skip(9223372036854775807L)) != 0L; var2 += var4) {
      }

      return var2;
   }

   @CanIgnoreReturnValue
   public long copyTo(Appendable var1) throws IOException {
      Preconditions.checkNotNull(var1);
      Closer var2 = Closer.create();

      long var4;
      try {
         Reader var3 = (Reader)var2.register(this.openStream());
         var4 = CharStreams.copy(var3, var1);
      } catch (Throwable var9) {
         throw var2.rethrow(var9);
      } finally {
         var2.close();
      }

      return var4;
   }

   @CanIgnoreReturnValue
   public long copyTo(CharSink var1) throws IOException {
      Preconditions.checkNotNull(var1);
      Closer var2 = Closer.create();

      long var5;
      try {
         Reader var3 = (Reader)var2.register(this.openStream());
         Writer var4 = (Writer)var2.register(var1.openStream());
         var5 = CharStreams.copy(var3, var4);
      } catch (Throwable var10) {
         throw var2.rethrow(var10);
      } finally {
         var2.close();
      }

      return var5;
   }

   public String read() throws IOException {
      Closer var1 = Closer.create();

      String var3;
      try {
         Reader var2 = (Reader)var1.register(this.openStream());
         var3 = CharStreams.toString(var2);
      } catch (Throwable var7) {
         throw var1.rethrow(var7);
      } finally {
         var1.close();
      }

      return var3;
   }

   @Nullable
   public String readFirstLine() throws IOException {
      Closer var1 = Closer.create();

      String var3;
      try {
         BufferedReader var2 = (BufferedReader)var1.register(this.openBufferedStream());
         var3 = var2.readLine();
      } catch (Throwable var7) {
         throw var1.rethrow(var7);
      } finally {
         var1.close();
      }

      return var3;
   }

   public ImmutableList<String> readLines() throws IOException {
      Closer var1 = Closer.create();

      try {
         BufferedReader var2 = (BufferedReader)var1.register(this.openBufferedStream());
         ArrayList var3 = Lists.newArrayList();

         String var4;
         while((var4 = var2.readLine()) != null) {
            var3.add(var4);
         }

         ImmutableList var5 = ImmutableList.copyOf((Collection)var3);
         return var5;
      } catch (Throwable var9) {
         throw var1.rethrow(var9);
      } finally {
         var1.close();
      }
   }

   @Beta
   @CanIgnoreReturnValue
   public <T> T readLines(LineProcessor<T> var1) throws IOException {
      Preconditions.checkNotNull(var1);
      Closer var2 = Closer.create();

      Object var4;
      try {
         Reader var3 = (Reader)var2.register(this.openStream());
         var4 = CharStreams.readLines(var3, var1);
      } catch (Throwable var8) {
         throw var2.rethrow(var8);
      } finally {
         var2.close();
      }

      return var4;
   }

   public boolean isEmpty() throws IOException {
      Optional var1 = this.lengthIfKnown();
      if (var1.isPresent() && (Long)var1.get() == 0L) {
         return true;
      } else {
         Closer var2 = Closer.create();

         boolean var4;
         try {
            Reader var3 = (Reader)var2.register(this.openStream());
            var4 = var3.read() == -1;
         } catch (Throwable var8) {
            throw var2.rethrow(var8);
         } finally {
            var2.close();
         }

         return var4;
      }
   }

   public static CharSource concat(Iterable<? extends CharSource> var0) {
      return new CharSource.ConcatenatedCharSource(var0);
   }

   public static CharSource concat(Iterator<? extends CharSource> var0) {
      return concat((Iterable)ImmutableList.copyOf(var0));
   }

   public static CharSource concat(CharSource... var0) {
      return concat((Iterable)ImmutableList.copyOf((Object[])var0));
   }

   public static CharSource wrap(CharSequence var0) {
      return new CharSource.CharSequenceCharSource(var0);
   }

   public static CharSource empty() {
      return CharSource.EmptyCharSource.INSTANCE;
   }

   private static final class ConcatenatedCharSource extends CharSource {
      private final Iterable<? extends CharSource> sources;

      ConcatenatedCharSource(Iterable<? extends CharSource> var1) {
         super();
         this.sources = (Iterable)Preconditions.checkNotNull(var1);
      }

      public Reader openStream() throws IOException {
         return new MultiReader(this.sources.iterator());
      }

      public boolean isEmpty() throws IOException {
         Iterator var1 = this.sources.iterator();

         CharSource var2;
         do {
            if (!var1.hasNext()) {
               return true;
            }

            var2 = (CharSource)var1.next();
         } while(var2.isEmpty());

         return false;
      }

      public Optional<Long> lengthIfKnown() {
         long var1 = 0L;

         Optional var5;
         for(Iterator var3 = this.sources.iterator(); var3.hasNext(); var1 += (Long)var5.get()) {
            CharSource var4 = (CharSource)var3.next();
            var5 = var4.lengthIfKnown();
            if (!var5.isPresent()) {
               return Optional.absent();
            }
         }

         return Optional.of(var1);
      }

      public long length() throws IOException {
         long var1 = 0L;

         CharSource var4;
         for(Iterator var3 = this.sources.iterator(); var3.hasNext(); var1 += var4.length()) {
            var4 = (CharSource)var3.next();
         }

         return var1;
      }

      public String toString() {
         return "CharSource.concat(" + this.sources + ")";
      }
   }

   private static final class EmptyCharSource extends CharSource.CharSequenceCharSource {
      private static final CharSource.EmptyCharSource INSTANCE = new CharSource.EmptyCharSource();

      private EmptyCharSource() {
         super("");
      }

      public String toString() {
         return "CharSource.empty()";
      }
   }

   private static class CharSequenceCharSource extends CharSource {
      private static final Splitter LINE_SPLITTER = Splitter.onPattern("\r\n|\n|\r");
      private final CharSequence seq;

      protected CharSequenceCharSource(CharSequence var1) {
         super();
         this.seq = (CharSequence)Preconditions.checkNotNull(var1);
      }

      public Reader openStream() {
         return new CharSequenceReader(this.seq);
      }

      public String read() {
         return this.seq.toString();
      }

      public boolean isEmpty() {
         return this.seq.length() == 0;
      }

      public long length() {
         return (long)this.seq.length();
      }

      public Optional<Long> lengthIfKnown() {
         return Optional.of((long)this.seq.length());
      }

      private Iterable<String> lines() {
         return new Iterable<String>() {
            public Iterator<String> iterator() {
               return new AbstractIterator<String>() {
                  Iterator<String> lines;

                  {
                     this.lines = CharSource.CharSequenceCharSource.LINE_SPLITTER.split(CharSequenceCharSource.this.seq).iterator();
                  }

                  protected String computeNext() {
                     if (this.lines.hasNext()) {
                        String var1 = (String)this.lines.next();
                        if (this.lines.hasNext() || !var1.isEmpty()) {
                           return var1;
                        }
                     }

                     return (String)this.endOfData();
                  }
               };
            }
         };
      }

      public String readFirstLine() {
         Iterator var1 = this.lines().iterator();
         return var1.hasNext() ? (String)var1.next() : null;
      }

      public ImmutableList<String> readLines() {
         return ImmutableList.copyOf(this.lines());
      }

      public <T> T readLines(LineProcessor<T> var1) throws IOException {
         Iterator var2 = this.lines().iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            if (!var1.processLine(var3)) {
               break;
            }
         }

         return var1.getResult();
      }

      public String toString() {
         return "CharSource.wrap(" + Ascii.truncate(this.seq, 30, "...") + ")";
      }
   }

   private final class AsByteSource extends ByteSource {
      final Charset charset;

      AsByteSource(Charset var2) {
         super();
         this.charset = (Charset)Preconditions.checkNotNull(var2);
      }

      public CharSource asCharSource(Charset var1) {
         return var1.equals(this.charset) ? CharSource.this : super.asCharSource(var1);
      }

      public InputStream openStream() throws IOException {
         return new ReaderInputStream(CharSource.this.openStream(), this.charset, 8192);
      }

      public String toString() {
         return CharSource.this.toString() + ".asByteSource(" + this.charset + ")";
      }
   }
}
