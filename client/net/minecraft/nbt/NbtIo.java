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
   public static CompoundTag readCompressed(InputStream var0) throws IOException {
      DataInputStream var1 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(var0)));
      Throwable var2 = null;

      CompoundTag var3;
      try {
         var3 = read(var1, NbtAccounter.UNLIMITED);
      } catch (Throwable var12) {
         var2 = var12;
         throw var12;
      } finally {
         if (var1 != null) {
            if (var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var11) {
                  var2.addSuppressed(var11);
               }
            } else {
               var1.close();
            }
         }

      }

      return var3;
   }

   public static void writeCompressed(CompoundTag var0, OutputStream var1) throws IOException {
      DataOutputStream var2 = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(var1)));
      Throwable var3 = null;

      try {
         write(var0, (DataOutput)var2);
      } catch (Throwable var12) {
         var3 = var12;
         throw var12;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var11) {
                  var3.addSuppressed(var11);
               }
            } else {
               var2.close();
            }
         }

      }

   }

   public static void safeWrite(CompoundTag var0, File var1) throws IOException {
      File var2 = new File(var1.getAbsolutePath() + "_tmp");
      if (var2.exists()) {
         var2.delete();
      }

      write(var0, var2);
      if (var1.exists()) {
         var1.delete();
      }

      if (var1.exists()) {
         throw new IOException("Failed to delete " + var1);
      } else {
         var2.renameTo(var1);
      }
   }

   public static void write(CompoundTag var0, File var1) throws IOException {
      DataOutputStream var2 = new DataOutputStream(new FileOutputStream(var1));

      try {
         write(var0, (DataOutput)var2);
      } finally {
         var2.close();
      }

   }

   @Nullable
   public static CompoundTag read(File var0) throws IOException {
      if (!var0.exists()) {
         return null;
      } else {
         DataInputStream var1 = new DataInputStream(new FileInputStream(var0));

         CompoundTag var2;
         try {
            var2 = read(var1, NbtAccounter.UNLIMITED);
         } finally {
            var1.close();
         }

         return var2;
      }
   }

   public static CompoundTag read(DataInputStream var0) throws IOException {
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

   private static void writeUnnamedTag(Tag var0, DataOutput var1) throws IOException {
      var1.writeByte(var0.getId());
      if (var0.getId() != 0) {
         var1.writeUTF("");
         var0.write(var1);
      }
   }

   private static Tag readUnnamedTag(DataInput var0, int var1, NbtAccounter var2) throws IOException {
      byte var3 = var0.readByte();
      if (var3 == 0) {
         return new EndTag();
      } else {
         var0.readUTF();
         Tag var4 = Tag.newTag(var3);

         try {
            var4.load(var0, var1, var2);
            return var4;
         } catch (IOException var8) {
            CrashReport var6 = CrashReport.forThrowable(var8, "Loading NBT data");
            CrashReportCategory var7 = var6.addCategory("NBT Tag");
            var7.setDetail("Tag type", (Object)var3);
            throw new ReportedException(var6);
         }
      }
   }
}
