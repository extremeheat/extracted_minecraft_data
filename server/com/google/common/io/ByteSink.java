package com.google.common.io;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

@GwtIncompatible
public abstract class ByteSink {
   protected ByteSink() {
      super();
   }

   public CharSink asCharSink(Charset var1) {
      return new ByteSink.AsCharSink(var1);
   }

   public abstract OutputStream openStream() throws IOException;

   public OutputStream openBufferedStream() throws IOException {
      OutputStream var1 = this.openStream();
      return var1 instanceof BufferedOutputStream ? (BufferedOutputStream)var1 : new BufferedOutputStream(var1);
   }

   public void write(byte[] var1) throws IOException {
      Preconditions.checkNotNull(var1);
      Closer var2 = Closer.create();

      try {
         OutputStream var3 = (OutputStream)var2.register(this.openStream());
         var3.write(var1);
         var3.flush();
      } catch (Throwable var7) {
         throw var2.rethrow(var7);
      } finally {
         var2.close();
      }

   }

   @CanIgnoreReturnValue
   public long writeFrom(InputStream var1) throws IOException {
      Preconditions.checkNotNull(var1);
      Closer var2 = Closer.create();

      long var6;
      try {
         OutputStream var3 = (OutputStream)var2.register(this.openStream());
         long var4 = ByteStreams.copy(var1, var3);
         var3.flush();
         var6 = var4;
      } catch (Throwable var11) {
         throw var2.rethrow(var11);
      } finally {
         var2.close();
      }

      return var6;
   }

   private final class AsCharSink extends CharSink {
      private final Charset charset;

      private AsCharSink(Charset var2) {
         super();
         this.charset = (Charset)Preconditions.checkNotNull(var2);
      }

      public Writer openStream() throws IOException {
         return new OutputStreamWriter(ByteSink.this.openStream(), this.charset);
      }

      public String toString() {
         return ByteSink.this.toString() + ".asCharSink(" + this.charset + ")";
      }

      // $FF: synthetic method
      AsCharSink(Charset var2, Object var3) {
         this(var2);
      }
   }
}
