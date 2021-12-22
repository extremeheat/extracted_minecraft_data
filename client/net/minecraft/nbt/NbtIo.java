package net.minecraft.nbt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;

public class NbtIo {
   public NbtIo() {
      super();
   }

   public static CompoundTag readCompressed(File var0) throws IOException {
      FileInputStream var1 = new FileInputStream(var0);

      CompoundTag var2;
      try {
         var2 = readCompressed((InputStream)var1);
      } catch (Throwable var5) {
         try {
            var1.close();
         } catch (Throwable var4) {
            var5.addSuppressed(var4);
         }

         throw var5;
      }

      var1.close();
      return var2;
   }

   public static CompoundTag readCompressed(InputStream var0) throws IOException {
      DataInputStream var1 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(var0)));

      CompoundTag var2;
      try {
         var2 = read(var1, NbtAccounter.UNLIMITED);
      } catch (Throwable var5) {
         try {
            var1.close();
         } catch (Throwable var4) {
            var5.addSuppressed(var4);
         }

         throw var5;
      }

      var1.close();
      return var2;
   }

   public static void writeCompressed(CompoundTag var0, File var1) throws IOException {
      FileOutputStream var2 = new FileOutputStream(var1);

      try {
         writeCompressed(var0, (OutputStream)var2);
      } catch (Throwable var6) {
         try {
            var2.close();
         } catch (Throwable var5) {
            var6.addSuppressed(var5);
         }

         throw var6;
      }

      var2.close();
   }

   public static void writeCompressed(CompoundTag var0, OutputStream var1) throws IOException {
      DataOutputStream var2 = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(var1)));

      try {
         write(var0, (DataOutput)var2);
      } catch (Throwable var6) {
         try {
            var2.close();
         } catch (Throwable var5) {
            var6.addSuppressed(var5);
         }

         throw var6;
      }

      var2.close();
   }

   public static void write(CompoundTag var0, File var1) throws IOException {
      FileOutputStream var2 = new FileOutputStream(var1);

      try {
         DataOutputStream var3 = new DataOutputStream(var2);

         try {
            write(var0, (DataOutput)var3);
         } catch (Throwable var8) {
            try {
               var3.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }

            throw var8;
         }

         var3.close();
      } catch (Throwable var9) {
         try {
            var2.close();
         } catch (Throwable var6) {
            var9.addSuppressed(var6);
         }

         throw var9;
      }

      var2.close();
   }

   @Nullable
   public static CompoundTag read(File var0) throws IOException {
      if (!var0.exists()) {
         return null;
      } else {
         FileInputStream var1 = new FileInputStream(var0);

         CompoundTag var3;
         try {
            DataInputStream var2 = new DataInputStream(var1);

            try {
               var3 = read(var2, NbtAccounter.UNLIMITED);
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
            try {
               var1.close();
            } catch (Throwable var5) {
               var8.addSuppressed(var5);
            }

            throw var8;
         }

         var1.close();
         return var3;
      }
   }

   public static CompoundTag read(DataInput var0) throws IOException {
      return read(var0, NbtAccounter.UNLIMITED);
   }

   public static CompoundTag read(DataInput var0, NbtAccounter var1) throws IOException {
      Tag var2 = readUnnamedTag(var0, 0, var1);
      if (var2 instanceof CompoundTag) {
         return (CompoundTag)var2;
      } else {
         throw new IOException("Root tag must be a named compound tag");
      }
   }

   public static void write(CompoundTag var0, DataOutput var1) throws IOException {
      writeUnnamedTag(var0, var1);
   }

   public static void parse(DataInput var0, StreamTagVisitor var1) throws IOException {
      TagType var2 = TagTypes.getType(var0.readByte());
      if (var2 == EndTag.TYPE) {
         if (var1.visitRootEntry(EndTag.TYPE) == StreamTagVisitor.ValueResult.CONTINUE) {
            var1.visitEnd();
         }

      } else {
         switch(var1.visitRootEntry(var2)) {
         case HALT:
         default:
            break;
         case BREAK:
            StringTag.skipString(var0);
            var2.skip(var0);
            break;
         case CONTINUE:
            StringTag.skipString(var0);
            var2.parse(var0, var1);
         }

      }
   }

   public static void writeUnnamedTag(Tag var0, DataOutput var1) throws IOException {
      var1.writeByte(var0.getId());
      if (var0.getId() != 0) {
         var1.writeUTF("");
         var0.write(var1);
      }
   }

   private static Tag readUnnamedTag(DataInput var0, int var1, NbtAccounter var2) throws IOException {
      byte var3 = var0.readByte();
      if (var3 == 0) {
         return EndTag.INSTANCE;
      } else {
         StringTag.skipString(var0);

         try {
            return TagTypes.getType(var3).load(var0, var1, var2);
         } catch (IOException var7) {
            CrashReport var5 = CrashReport.forThrowable(var7, "Loading NBT data");
            CrashReportCategory var6 = var5.addCategory("NBT Tag");
            var6.setDetail("Tag type", (Object)var3);
            throw new ReportedException(var5);
         }
      }
   }
}
