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
   private static final OpenOption[] SYNC_OUTPUT_OPTIONS = new OpenOption[]{
      StandardOpenOption.SYNC, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
   };

   public NbtIo() {
      super();
   }

   public static CompoundTag readCompressed(Path var0, NbtAccounter var1) throws IOException {
      CompoundTag var4;
      try (
         InputStream var2 = Files.newInputStream(var0);
         FastBufferedInputStream var3 = new FastBufferedInputStream(var2);
      ) {
         var4 = readCompressed(var3, var1);
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
      CompoundTag var3;
      try (DataInputStream var2 = createDecompressorStream(var0)) {
         var3 = read(var2, var1);
      }

      return var3;
   }

   public static void parseCompressed(Path var0, StreamTagVisitor var1, NbtAccounter var2) throws IOException {
      try (
         InputStream var3 = Files.newInputStream(var0);
         FastBufferedInputStream var4 = new FastBufferedInputStream(var3);
      ) {
         parseCompressed(var4, var1, var2);
      }
   }

   public static void parseCompressed(InputStream var0, StreamTagVisitor var1, NbtAccounter var2) throws IOException {
      try (DataInputStream var3 = createDecompressorStream(var0)) {
         parse(var3, var1, var2);
      }
   }

   public static void writeCompressed(CompoundTag var0, Path var1) throws IOException {
      try (
         OutputStream var2 = Files.newOutputStream(var1, SYNC_OUTPUT_OPTIONS);
         BufferedOutputStream var3 = new BufferedOutputStream(var2);
      ) {
         writeCompressed(var0, var3);
      }
   }

   public static void writeCompressed(CompoundTag var0, OutputStream var1) throws IOException {
      try (DataOutputStream var2 = createCompressorStream(var1)) {
         write(var0, var2);
      }
   }

   public static void write(CompoundTag var0, Path var1) throws IOException {
      try (
         OutputStream var2 = Files.newOutputStream(var1, SYNC_OUTPUT_OPTIONS);
         BufferedOutputStream var3 = new BufferedOutputStream(var2);
         DataOutputStream var4 = new DataOutputStream(var3);
      ) {
         write(var0, var4);
      }
   }

   @Nullable
   public static CompoundTag read(Path var0) throws IOException {
      if (!Files.exists(var0)) {
         return null;
      } else {
         CompoundTag var3;
         try (
            InputStream var1 = Files.newInputStream(var0);
            DataInputStream var2 = new DataInputStream(var1);
         ) {
            var3 = read(var2, NbtAccounter.unlimitedHeap());
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
         switch(var1.visitRootEntry(var3)) {
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
      writeUnnamedTag(var0, new NbtIo.StringFallbackDataOutput(var1));
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
         var5.setDetail("Tag type", var2);
         throw new ReportedNbtException(var4);
      }
   }

   public static class StringFallbackDataOutput extends DelegateDataOutput {
      public StringFallbackDataOutput(DataOutput var1) {
         super(var1);
      }

      @Override
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
