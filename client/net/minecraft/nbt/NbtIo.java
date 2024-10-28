package net.minecraft.nbt;

import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UTFDataFormatException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.Util;
import net.minecraft.util.DelegateDataOutput;
import net.minecraft.util.FastBufferedInputStream;

public class NbtIo {
   private static final OpenOption[] SYNC_OUTPUT_OPTIONS;

   public NbtIo() {
      super();
   }

   public static CompoundTag readCompressed(Path var0, NbtAccounter var1) throws IOException {
      InputStream var2 = Files.newInputStream(var0);

      CompoundTag var4;
      try {
         FastBufferedInputStream var3 = new FastBufferedInputStream(var2);

         try {
            var4 = readCompressed((InputStream)var3, var1);
         } catch (Throwable var8) {
            try {
               ((InputStream)var3).close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }

            throw var8;
         }

         ((InputStream)var3).close();
      } catch (Throwable var9) {
         if (var2 != null) {
            try {
               var2.close();
            } catch (Throwable var6) {
               var9.addSuppressed(var6);
            }
         }

         throw var9;
      }

      if (var2 != null) {
         var2.close();
      }

      return var4;
   }

   private static DataInputStream createDecompressorStream(InputStream var0) throws IOException {
      return new DataInputStream(new FastBufferedInputStream(new GZIPInputStream(var0)));
   }

   private static DataOutputStream createCompressorStream(OutputStream var0) throws IOException {
      return new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(var0)));
   }

   public static CompoundTag readCompressed(InputStream var0, NbtAccounter var1) throws IOException {
      DataInputStream var2 = createDecompressorStream(var0);

      CompoundTag var3;
      try {
         var3 = read(var2, var1);
      } catch (Throwable var6) {
         if (var2 != null) {
            try {
               var2.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (var2 != null) {
         var2.close();
      }

      return var3;
   }

   public static void parseCompressed(Path var0, StreamTagVisitor var1, NbtAccounter var2) throws IOException {
      InputStream var3 = Files.newInputStream(var0);

      try {
         FastBufferedInputStream var4 = new FastBufferedInputStream(var3);

         try {
            parseCompressed((InputStream)var4, var1, var2);
         } catch (Throwable var9) {
            try {
               ((InputStream)var4).close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }

            throw var9;
         }

         ((InputStream)var4).close();
      } catch (Throwable var10) {
         if (var3 != null) {
            try {
               var3.close();
            } catch (Throwable var7) {
               var10.addSuppressed(var7);
            }
         }

         throw var10;
      }

      if (var3 != null) {
         var3.close();
      }

   }

   public static void parseCompressed(InputStream var0, StreamTagVisitor var1, NbtAccounter var2) throws IOException {
      DataInputStream var3 = createDecompressorStream(var0);

      try {
         parse(var3, var1, var2);
      } catch (Throwable var7) {
         if (var3 != null) {
            try {
               var3.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (var3 != null) {
         var3.close();
      }

   }

   public static void writeCompressed(CompoundTag var0, Path var1) throws IOException {
      OutputStream var2 = Files.newOutputStream(var1, SYNC_OUTPUT_OPTIONS);

      try {
         BufferedOutputStream var3 = new BufferedOutputStream(var2);

         try {
            writeCompressed(var0, (OutputStream)var3);
         } catch (Throwable var8) {
            try {
               ((OutputStream)var3).close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }

            throw var8;
         }

         ((OutputStream)var3).close();
      } catch (Throwable var9) {
         if (var2 != null) {
            try {
               var2.close();
            } catch (Throwable var6) {
               var9.addSuppressed(var6);
            }
         }

         throw var9;
      }

      if (var2 != null) {
         var2.close();
      }

   }

   public static void writeCompressed(CompoundTag var0, OutputStream var1) throws IOException {
      DataOutputStream var2 = createCompressorStream(var1);

      try {
         write(var0, (DataOutput)var2);
      } catch (Throwable var6) {
         if (var2 != null) {
            try {
               var2.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (var2 != null) {
         var2.close();
      }

   }

   public static void write(CompoundTag var0, Path var1) throws IOException {
      OutputStream var2 = Files.newOutputStream(var1, SYNC_OUTPUT_OPTIONS);

      try {
         BufferedOutputStream var3 = new BufferedOutputStream(var2);

         try {
            DataOutputStream var4 = new DataOutputStream(var3);

            try {
               write(var0, (DataOutput)var4);
            } catch (Throwable var10) {
               try {
                  var4.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }

               throw var10;
            }

            var4.close();
         } catch (Throwable var11) {
            try {
               ((OutputStream)var3).close();
            } catch (Throwable var8) {
               var11.addSuppressed(var8);
            }

            throw var11;
         }

         ((OutputStream)var3).close();
      } catch (Throwable var12) {
         if (var2 != null) {
            try {
               var2.close();
            } catch (Throwable var7) {
               var12.addSuppressed(var7);
            }
         }

         throw var12;
      }

      if (var2 != null) {
         var2.close();
      }

   }

   @Nullable
   public static CompoundTag read(Path var0) throws IOException {
      if (!Files.exists(var0, new LinkOption[0])) {
         return null;
      } else {
         InputStream var1 = Files.newInputStream(var0);

         CompoundTag var3;
         try {
            DataInputStream var2 = new DataInputStream(var1);

            try {
               var3 = read(var2, NbtAccounter.unlimitedHeap());
            } catch (Throwable var7) {
               try {
                  var2.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }

               throw var7;
            }

            var2.close();
         } catch (Throwable var8) {
            if (var1 != null) {
               try {
                  var1.close();
               } catch (Throwable var5) {
                  var8.addSuppressed(var5);
               }
            }

            throw var8;
         }

         if (var1 != null) {
            var1.close();
         }

         return var3;
      }
   }

   public static CompoundTag read(DataInput var0) throws IOException {
      return read(var0, NbtAccounter.unlimitedHeap());
   }

   public static CompoundTag read(DataInput var0, NbtAccounter var1) throws IOException {
      Tag var2 = readUnnamedTag(var0, var1);
      if (var2 instanceof CompoundTag) {
         return (CompoundTag)var2;
      } else {
         throw new IOException("Root tag must be a named compound tag");
      }
   }

   public static void write(CompoundTag var0, DataOutput var1) throws IOException {
      writeUnnamedTagWithFallback(var0, var1);
   }

   public static void parse(DataInput var0, StreamTagVisitor var1, NbtAccounter var2) throws IOException {
      TagType var3 = TagTypes.getType(var0.readByte());
      if (var3 == EndTag.TYPE) {
         if (var1.visitRootEntry(EndTag.TYPE) == StreamTagVisitor.ValueResult.CONTINUE) {
            var1.visitEnd();
         }

      } else {
         switch (var1.visitRootEntry(var3)) {
            case HALT:
            default:
               break;
            case BREAK:
               StringTag.skipString(var0);
               var3.skip(var0, var2);
               break;
            case CONTINUE:
               StringTag.skipString(var0);
               var3.parse(var0, var1, var2);
         }

      }
   }

   public static Tag readAnyTag(DataInput var0, NbtAccounter var1) throws IOException {
      byte var2 = var0.readByte();
      return (Tag)(var2 == 0 ? EndTag.INSTANCE : readTagSafe(var0, var1, var2));
   }

   public static void writeAnyTag(Tag var0, DataOutput var1) throws IOException {
      var1.writeByte(var0.getId());
      if (var0.getId() != 0) {
         var0.write(var1);
      }
   }

   public static void writeUnnamedTag(Tag var0, DataOutput var1) throws IOException {
      var1.writeByte(var0.getId());
      if (var0.getId() != 0) {
         var1.writeUTF("");
         var0.write(var1);
      }
   }

   public static void writeUnnamedTagWithFallback(Tag var0, DataOutput var1) throws IOException {
      writeUnnamedTag(var0, new StringFallbackDataOutput(var1));
   }

   private static Tag readUnnamedTag(DataInput var0, NbtAccounter var1) throws IOException {
      byte var2 = var0.readByte();
      if (var2 == 0) {
         return EndTag.INSTANCE;
      } else {
         StringTag.skipString(var0);
         return readTagSafe(var0, var1, var2);
      }
   }

   private static Tag readTagSafe(DataInput var0, NbtAccounter var1, byte var2) {
      try {
         return TagTypes.getType(var2).load(var0, var1);
      } catch (IOException var6) {
         CrashReport var4 = CrashReport.forThrowable(var6, "Loading NBT data");
         CrashReportCategory var5 = var4.addCategory("NBT Tag");
         var5.setDetail("Tag type", (Object)var2);
         throw new ReportedNbtException(var4);
      }
   }

   static {
      SYNC_OUTPUT_OPTIONS = new OpenOption[]{StandardOpenOption.SYNC, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};
   }

   public static class StringFallbackDataOutput extends DelegateDataOutput {
      public StringFallbackDataOutput(DataOutput var1) {
         super(var1);
      }

      public void writeUTF(String var1) throws IOException {
         try {
            super.writeUTF(var1);
         } catch (UTFDataFormatException var3) {
            Util.logAndPauseIfInIde("Failed to write NBT String", var3);
            super.writeUTF("");
         }

      }
   }
}
