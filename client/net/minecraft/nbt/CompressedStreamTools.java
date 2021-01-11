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
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;

public class CompressedStreamTools {
   public static NBTTagCompound func_74796_a(InputStream var0) throws IOException {
      DataInputStream var1 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(var0)));

      NBTTagCompound var2;
      try {
         var2 = func_152456_a(var1, NBTSizeTracker.field_152451_a);
      } finally {
         var1.close();
      }

      return var2;
   }

   public static void func_74799_a(NBTTagCompound var0, OutputStream var1) throws IOException {
      DataOutputStream var2 = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(var1)));

      try {
         func_74800_a(var0, var2);
      } finally {
         var2.close();
      }

   }

   public static void func_74793_a(NBTTagCompound var0, File var1) throws IOException {
      File var2 = new File(var1.getAbsolutePath() + "_tmp");
      if (var2.exists()) {
         var2.delete();
      }

      func_74795_b(var0, var2);
      if (var1.exists()) {
         var1.delete();
      }

      if (var1.exists()) {
         throw new IOException("Failed to delete " + var1);
      } else {
         var2.renameTo(var1);
      }
   }

   public static void func_74795_b(NBTTagCompound var0, File var1) throws IOException {
      DataOutputStream var2 = new DataOutputStream(new FileOutputStream(var1));

      try {
         func_74800_a(var0, var2);
      } finally {
         var2.close();
      }

   }

   public static NBTTagCompound func_74797_a(File var0) throws IOException {
      if (!var0.exists()) {
         return null;
      } else {
         DataInputStream var1 = new DataInputStream(new FileInputStream(var0));

         NBTTagCompound var2;
         try {
            var2 = func_152456_a(var1, NBTSizeTracker.field_152451_a);
         } finally {
            var1.close();
         }

         return var2;
      }
   }

   public static NBTTagCompound func_74794_a(DataInputStream var0) throws IOException {
      return func_152456_a(var0, NBTSizeTracker.field_152451_a);
   }

   public static NBTTagCompound func_152456_a(DataInput var0, NBTSizeTracker var1) throws IOException {
      NBTBase var2 = func_152455_a(var0, 0, var1);
      if (var2 instanceof NBTTagCompound) {
         return (NBTTagCompound)var2;
      } else {
         throw new IOException("Root tag must be a named compound tag");
      }
   }

   public static void func_74800_a(NBTTagCompound var0, DataOutput var1) throws IOException {
      func_150663_a(var0, var1);
   }

   private static void func_150663_a(NBTBase var0, DataOutput var1) throws IOException {
      var1.writeByte(var0.func_74732_a());
      if (var0.func_74732_a() != 0) {
         var1.writeUTF("");
         var0.func_74734_a(var1);
      }
   }

   private static NBTBase func_152455_a(DataInput var0, int var1, NBTSizeTracker var2) throws IOException {
      byte var3 = var0.readByte();
      if (var3 == 0) {
         return new NBTTagEnd();
      } else {
         var0.readUTF();
         NBTBase var4 = NBTBase.func_150284_a(var3);

         try {
            var4.func_152446_a(var0, var1, var2);
            return var4;
         } catch (IOException var8) {
            CrashReport var6 = CrashReport.func_85055_a(var8, "Loading NBT data");
            CrashReportCategory var7 = var6.func_85058_a("NBT Tag");
            var7.func_71507_a("Tag name", "[UNNAMED TAG]");
            var7.func_71507_a("Tag type", var3);
            throw new ReportedException(var6);
         }
      }
   }
}
