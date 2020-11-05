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
   public static CompoundTag readCompressed(File var0) throws IOException {
      FileInputStream var1 = new FileInputStream(var0);
      Throwable var2 = null;

      CompoundTag var3;
      try {
         var3 = readCompressed((InputStream)var1);
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

   public static void writeCompressed(CompoundTag var0, File var1) throws IOException {
      FileOutputStream var2 = new FileOutputStream(var1);
      Throwable var3 = null;

      try {
         writeCompressed(var0, (OutputStream)var2);
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

   public static void write(CompoundTag var0, File var1) throws IOException {
      FileOutputStream var2 = new FileOutputStream(var1);
      Throwable var3 = null;

      try {
         DataOutputStream var4 = new DataOutputStream(var2);
         Throwable var5 = null;

         try {
            write(var0, (DataOutput)var4);
         } catch (Throwable var28) {
            var5 = var28;
            throw var28;
         } finally {
            if (var4 != null) {
               if (var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var27) {
                     var5.addSuppressed(var27);
                  }
               } else {
                  var4.close();
               }
            }

         }
      } catch (Throwable var30) {
         var3 = var30;
         throw var30;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var26) {
                  var3.addSuppressed(var26);
               }
            } else {
               var2.close();
            }
         }

      }

   }

   @Nullable
   public static CompoundTag read(File var0) throws IOException {
      if (!var0.exists()) {
         return null;
      } else {
         FileInputStream var1 = new FileInputStream(var0);
         Throwable var2 = null;

         Object var5;
         try {
            DataInputStream var3 = new DataInputStream(var1);
            Throwable var4 = null;

            try {
               var5 = read(var3, NbtAccounter.UNLIMITED);
            } catch (Throwable var28) {
               var5 = var28;
               var4 = var28;
               throw var28;
            } finally {
               if (var3 != null) {
                  if (var4 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var27) {
                        var4.addSuppressed(var27);
                     }
                  } else {
                     var3.close();
                  }
               }

            }
         } catch (Throwable var30) {
            var2 = var30;
            throw var30;
         } finally {
            if (var1 != null) {
               if (var2 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var26) {
                     var2.addSuppressed(var26);
                  }
               } else {
                  var1.close();
               }
            }

         }

         return (CompoundTag)var5;
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
         return EndTag.INSTANCE;
      } else {
         var0.readUTF();

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
