package org.apache.commons.io.input;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import org.apache.commons.io.Charsets;

public class ReversedLinesFileReader implements Closeable {
   private final int blockSize;
   private final Charset encoding;
   private final RandomAccessFile randomAccessFile;
   private final long totalByteLength;
   private final long totalBlockCount;
   private final byte[][] newLineSequences;
   private final int avoidNewlineSplitBufferSize;
   private final int byteDecrement;
   private ReversedLinesFileReader.FilePart currentFilePart;
   private boolean trailingNewlineOfFileSkipped;

   /** @deprecated */
   @Deprecated
   public ReversedLinesFileReader(File var1) throws IOException {
      this(var1, 4096, (Charset)Charset.defaultCharset());
   }

   public ReversedLinesFileReader(File var1, Charset var2) throws IOException {
      this(var1, 4096, (Charset)var2);
   }

   public ReversedLinesFileReader(File var1, int var2, Charset var3) throws IOException {
      super();
      this.trailingNewlineOfFileSkipped = false;
      this.blockSize = var2;
      this.encoding = var3;
      Charset var4 = Charsets.toCharset(var3);
      CharsetEncoder var5 = var4.newEncoder();
      float var6 = var5.maxBytesPerChar();
      if (var6 == 1.0F) {
         this.byteDecrement = 1;
      } else if (var4 == Charsets.UTF_8) {
         this.byteDecrement = 1;
      } else if (var4 != Charset.forName("Shift_JIS") && var4 != Charset.forName("windows-31j") && var4 != Charset.forName("x-windows-949") && var4 != Charset.forName("gbk") && var4 != Charset.forName("x-windows-950")) {
         if (var4 != Charsets.UTF_16BE && var4 != Charsets.UTF_16LE) {
            if (var4 == Charsets.UTF_16) {
               throw new UnsupportedEncodingException("For UTF-16, you need to specify the byte order (use UTF-16BE or UTF-16LE)");
            }

            throw new UnsupportedEncodingException("Encoding " + var3 + " is not supported yet (feel free to " + "submit a patch)");
         }

         this.byteDecrement = 2;
      } else {
         this.byteDecrement = 1;
      }

      this.newLineSequences = new byte[][]{"\r\n".getBytes(var3), "\n".getBytes(var3), "\r".getBytes(var3)};
      this.avoidNewlineSplitBufferSize = this.newLineSequences[0].length;
      this.randomAccessFile = new RandomAccessFile(var1, "r");
      this.totalByteLength = this.randomAccessFile.length();
      int var7 = (int)(this.totalByteLength % (long)var2);
      if (var7 > 0) {
         this.totalBlockCount = this.totalByteLength / (long)var2 + 1L;
      } else {
         this.totalBlockCount = this.totalByteLength / (long)var2;
         if (this.totalByteLength > 0L) {
            var7 = var2;
         }
      }

      this.currentFilePart = new ReversedLinesFileReader.FilePart(this.totalBlockCount, var7, (byte[])null);
   }

   public ReversedLinesFileReader(File var1, int var2, String var3) throws IOException {
      this(var1, var2, Charsets.toCharset(var3));
   }

   public String readLine() throws IOException {
      String var1;
      for(var1 = this.currentFilePart.readLine(); var1 == null; var1 = this.currentFilePart.readLine()) {
         this.currentFilePart = this.currentFilePart.rollOver();
         if (this.currentFilePart == null) {
            break;
         }
      }

      if ("".equals(var1) && !this.trailingNewlineOfFileSkipped) {
         this.trailingNewlineOfFileSkipped = true;
         var1 = this.readLine();
      }

      return var1;
   }

   public void close() throws IOException {
      this.randomAccessFile.close();
   }

   private class FilePart {
      private final long no;
      private final byte[] data;
      private byte[] leftOver;
      private int currentLastBytePos;

      private FilePart(long var2, int var4, byte[] var5) throws IOException {
         super();
         this.no = var2;
         int var6 = var4 + (var5 != null ? var5.length : 0);
         this.data = new byte[var6];
         long var7 = (var2 - 1L) * (long)ReversedLinesFileReader.this.blockSize;
         if (var2 > 0L) {
            ReversedLinesFileReader.this.randomAccessFile.seek(var7);
            int var9 = ReversedLinesFileReader.this.randomAccessFile.read(this.data, 0, var4);
            if (var9 != var4) {
               throw new IllegalStateException("Count of requested bytes and actually read bytes don't match");
            }
         }

         if (var5 != null) {
            System.arraycopy(var5, 0, this.data, var4, var5.length);
         }

         this.currentLastBytePos = this.data.length - 1;
         this.leftOver = null;
      }

      private ReversedLinesFileReader.FilePart rollOver() throws IOException {
         if (this.currentLastBytePos > -1) {
            throw new IllegalStateException("Current currentLastCharPos unexpectedly positive... last readLine() should have returned something! currentLastCharPos=" + this.currentLastBytePos);
         } else if (this.no > 1L) {
            return ReversedLinesFileReader.this.new FilePart(this.no - 1L, ReversedLinesFileReader.this.blockSize, this.leftOver);
         } else if (this.leftOver != null) {
            throw new IllegalStateException("Unexpected leftover of the last block: leftOverOfThisFilePart=" + new String(this.leftOver, ReversedLinesFileReader.this.encoding));
         } else {
            return null;
         }
      }

      private String readLine() throws IOException {
         String var1 = null;
         boolean var3 = this.no == 1L;
         int var4 = this.currentLastBytePos;

         while(var4 > -1) {
            if (!var3 && var4 < ReversedLinesFileReader.this.avoidNewlineSplitBufferSize) {
               this.createLeftOver();
               break;
            }

            int var2;
            if ((var2 = this.getNewLineMatchByteCount(this.data, var4)) > 0) {
               int var5 = var4 + 1;
               int var6 = this.currentLastBytePos - var5 + 1;
               if (var6 < 0) {
                  throw new IllegalStateException("Unexpected negative line length=" + var6);
               }

               byte[] var7 = new byte[var6];
               System.arraycopy(this.data, var5, var7, 0, var6);
               var1 = new String(var7, ReversedLinesFileReader.this.encoding);
               this.currentLastBytePos = var4 - var2;
               break;
            }

            var4 -= ReversedLinesFileReader.this.byteDecrement;
            if (var4 < 0) {
               this.createLeftOver();
               break;
            }
         }

         if (var3 && this.leftOver != null) {
            var1 = new String(this.leftOver, ReversedLinesFileReader.this.encoding);
            this.leftOver = null;
         }

         return var1;
      }

      private void createLeftOver() {
         int var1 = this.currentLastBytePos + 1;
         if (var1 > 0) {
            this.leftOver = new byte[var1];
            System.arraycopy(this.data, 0, this.leftOver, 0, var1);
         } else {
            this.leftOver = null;
         }

         this.currentLastBytePos = -1;
      }

      private int getNewLineMatchByteCount(byte[] var1, int var2) {
         byte[][] var3 = ReversedLinesFileReader.this.newLineSequences;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            byte[] var6 = var3[var5];
            boolean var7 = true;

            for(int var8 = var6.length - 1; var8 >= 0; --var8) {
               int var9 = var2 + var8 - (var6.length - 1);
               var7 &= var9 >= 0 && var1[var9] == var6[var8];
            }

            if (var7) {
               return var6.length;
            }
         }

         return 0;
      }

      // $FF: synthetic method
      FilePart(long var2, int var4, byte[] var5, Object var6) throws IOException {
         this(var2, var4, var5);
      }
   }
}
