package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
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
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.util.DelegateDataOutput;
import net.minecraft.util.FastBufferedInputStream;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public class NbtIo {
   private static final OpenOption[] SYNC_OUTPUT_OPTIONS;

   public NbtIo() {
      super();
   }

   public static CompoundTag readCompressed(final Path file, final NbtAccounter accounter) throws IOException {
      InputStream rawInput = Files.newInputStream(file);

      CompoundTag var4;
      try {
         InputStream input = new FastBufferedInputStream(rawInput);

         try {
            var4 = readCompressed(input, accounter);
         } catch (Throwable var8) {
            try {
               input.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }

            throw var8;
         }

         input.close();
      } catch (Throwable var9) {
         if (rawInput != null) {
            try {
               rawInput.close();
            } catch (Throwable var6) {
               var9.addSuppressed(var6);
            }
         }

         throw var9;
      }

      if (rawInput != null) {
         rawInput.close();
      }

      return var4;
   }

   private static DataInputStream createDecompressorStream(final InputStream in) throws IOException {
      return new DataInputStream(new FastBufferedInputStream(new GZIPInputStream(in)));
   }

   private static DataOutputStream createCompressorStream(final OutputStream out) throws IOException {
      return new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(out)));
   }

   public static CompoundTag readCompressed(final InputStream in, final NbtAccounter accounter) throws IOException {
      DataInputStream dis = createDecompressorStream(in);

      CompoundTag var3;
      try {
         var3 = read(dis, accounter);
      } catch (Throwable var6) {
         if (dis != null) {
            try {
               dis.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (dis != null) {
         dis.close();
      }

      return var3;
   }

   public static void parseCompressed(final Path file, final StreamTagVisitor output, final NbtAccounter accounter) throws IOException {
      InputStream rawInput = Files.newInputStream(file);

      try {
         InputStream input = new FastBufferedInputStream(rawInput);

         try {
            parseCompressed(input, output, accounter);
         } catch (Throwable var9) {
            try {
               input.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }

            throw var9;
         }

         input.close();
      } catch (Throwable var10) {
         if (rawInput != null) {
            try {
               rawInput.close();
            } catch (Throwable var7) {
               var10.addSuppressed(var7);
            }
         }

         throw var10;
      }

      if (rawInput != null) {
         rawInput.close();
      }

   }

   public static void parseCompressed(final InputStream in, final StreamTagVisitor output, final NbtAccounter accounter) throws IOException {
      DataInputStream dis = createDecompressorStream(in);

      try {
         parse(dis, output, accounter);
      } catch (Throwable var7) {
         if (dis != null) {
            try {
               dis.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (dis != null) {
         dis.close();
      }

   }

   public static void writeCompressed(final CompoundTag tag, final Path file) throws IOException {
      OutputStream out = Files.newOutputStream(file, SYNC_OUTPUT_OPTIONS);

      try {
         OutputStream bufferedOut = new BufferedOutputStream(out);

         try {
            writeCompressed(tag, bufferedOut);
         } catch (Throwable var8) {
            try {
               bufferedOut.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }

            throw var8;
         }

         bufferedOut.close();
      } catch (Throwable var9) {
         if (out != null) {
            try {
               out.close();
            } catch (Throwable var6) {
               var9.addSuppressed(var6);
            }
         }

         throw var9;
      }

      if (out != null) {
         out.close();
      }

   }

   public static void writeCompressed(final CompoundTag tag, final OutputStream out) throws IOException {
      DataOutputStream dos = createCompressorStream(out);

      try {
         write(tag, (DataOutput)dos);
      } catch (Throwable var6) {
         if (dos != null) {
            try {
               dos.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (dos != null) {
         dos.close();
      }

   }

   public static void write(final CompoundTag tag, final Path file) throws IOException {
      OutputStream out = Files.newOutputStream(file, SYNC_OUTPUT_OPTIONS);

      try {
         OutputStream bufferedOut = new BufferedOutputStream(out);

         try {
            DataOutputStream dos = new DataOutputStream(bufferedOut);

            try {
               write(tag, (DataOutput)dos);
            } catch (Throwable var10) {
               try {
                  dos.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }

               throw var10;
            }

            dos.close();
         } catch (Throwable var11) {
            try {
               bufferedOut.close();
            } catch (Throwable var8) {
               var11.addSuppressed(var8);
            }

            throw var11;
         }

         bufferedOut.close();
      } catch (Throwable var12) {
         if (out != null) {
            try {
               out.close();
            } catch (Throwable var7) {
               var12.addSuppressed(var7);
            }
         }

         throw var12;
      }

      if (out != null) {
         out.close();
      }

   }

   public static @Nullable CompoundTag read(final Path file) throws IOException {
      if (!Files.exists(file, new LinkOption[0])) {
         return null;
      } else {
         InputStream in = Files.newInputStream(file);

         CompoundTag var3;
         try {
            DataInputStream dis = new DataInputStream(in);

            try {
               var3 = read(dis, NbtAccounter.unlimitedHeap());
            } catch (Throwable var7) {
               try {
                  dis.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }

               throw var7;
            }

            dis.close();
         } catch (Throwable var8) {
            if (in != null) {
               try {
                  in.close();
               } catch (Throwable var5) {
                  var8.addSuppressed(var5);
               }
            }

            throw var8;
         }

         if (in != null) {
            in.close();
         }

         return var3;
      }
   }

   public static CompoundTag read(final DataInput input) throws IOException {
      return read(input, NbtAccounter.unlimitedHeap());
   }

   public static CompoundTag read(final DataInput input, final NbtAccounter accounter) throws IOException {
      Tag tag = readUnnamedTag(input, accounter);
      if (tag instanceof CompoundTag compoundTag) {
         return compoundTag;
      } else {
         throw new IOException("Root tag must be a named compound tag");
      }
   }

   public static void write(final CompoundTag tag, final DataOutput output) throws IOException {
      writeUnnamedTagWithFallback(tag, output);
   }

   public static void parse(final DataInput input, final StreamTagVisitor output, final NbtAccounter accounter) throws IOException {
      TagType<?> type = TagTypes.getType(input.readByte());
      if (type == EndTag.TYPE) {
         if (output.visitRootEntry(EndTag.TYPE) == StreamTagVisitor.ValueResult.CONTINUE) {
            output.visitEnd();
         }

      } else {
         switch (output.visitRootEntry(type)) {
            case HALT:
            default:
               break;
            case BREAK:
               StringTag.skipString(input);
               type.skip(input, accounter);
               break;
            case CONTINUE:
               StringTag.skipString(input);
               type.parse(input, output, accounter);
         }

      }
   }

   public static Tag readAnyTag(final DataInput input, final NbtAccounter accounter) throws IOException {
      byte type = input.readByte();
      return (Tag)(type == 0 ? EndTag.INSTANCE : readTagSafe(input, accounter, type));
   }

   public static void writeAnyTag(final Tag tag, final DataOutput output) throws IOException {
      output.writeByte(tag.getId());
      if (tag.getId() != 0) {
         tag.write(output);
      }
   }

   public static void writeUnnamedTag(final Tag tag, final DataOutput output) throws IOException {
      output.writeByte(tag.getId());
      if (tag.getId() != 0) {
         output.writeUTF("");
         tag.write(output);
      }
   }

   public static void writeUnnamedTagWithFallback(final Tag tag, final DataOutput output) throws IOException {
      writeUnnamedTag(tag, new StringFallbackDataOutput(output));
   }

   @VisibleForTesting
   public static Tag readUnnamedTag(final DataInput input, final NbtAccounter accounter) throws IOException {
      byte type = input.readByte();
      if (type == 0) {
         return EndTag.INSTANCE;
      } else {
         StringTag.skipString(input);
         return readTagSafe(input, accounter, type);
      }
   }

   private static Tag readTagSafe(final DataInput input, final NbtAccounter accounter, final byte type) {
      try {
         return TagTypes.getType(type).load(input, accounter);
      } catch (IOException e) {
         CrashReport report = CrashReport.forThrowable(e, "Loading NBT data");
         CrashReportCategory category = report.addCategory("NBT Tag");
         category.setDetail("Tag type", type);
         throw new ReportedNbtException(report);
      }
   }

   static {
      SYNC_OUTPUT_OPTIONS = new OpenOption[]{StandardOpenOption.SYNC, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};
   }

   public static class StringFallbackDataOutput extends DelegateDataOutput {
      public StringFallbackDataOutput(final DataOutput parent) {
         super(parent);
      }

      public void writeUTF(final String s) throws IOException {
         try {
            super.writeUTF(s);
         } catch (UTFDataFormatException exception) {
            Util.logAndPauseIfInIde("Failed to write NBT String", exception);
            super.writeUTF("");
         }

      }
   }
}
