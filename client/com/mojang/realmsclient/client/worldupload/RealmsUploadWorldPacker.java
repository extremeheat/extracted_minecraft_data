package com.mojang.realmsclient.client.worldupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.function.BooleanSupplier;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

public class RealmsUploadWorldPacker {
   private static final long SIZE_LIMIT = 5368709120L;
   private static final String WORLD_FOLDER_NAME = "world";
   private final BooleanSupplier isCanceled;
   private final Path directoryToPack;

   public static File pack(Path var0, BooleanSupplier var1) throws IOException {
      return (new RealmsUploadWorldPacker(var0, var1)).tarGzipArchive();
   }

   private RealmsUploadWorldPacker(Path var1, BooleanSupplier var2) {
      super();
      this.isCanceled = var2;
      this.directoryToPack = var1;
   }

   private File tarGzipArchive() throws IOException {
      TarArchiveOutputStream var1 = null;

      File var3;
      try {
         File var2 = File.createTempFile("realms-upload-file", ".tar.gz");
         var1 = new TarArchiveOutputStream(new GZIPOutputStream(new FileOutputStream(var2)));
         var1.setLongFileMode(3);
         this.addFileToTarGz(var1, this.directoryToPack, "world", true);
         if (this.isCanceled.getAsBoolean()) {
            throw new RealmsUploadCanceledException();
         }

         var1.finish();
         this.verifyBelowSizeLimit(var2.length());
         var3 = var2;
      } finally {
         if (var1 != null) {
            var1.close();
         }

      }

      return var3;
   }

   private void addFileToTarGz(TarArchiveOutputStream var1, Path var2, String var3, boolean var4) throws IOException {
      if (this.isCanceled.getAsBoolean()) {
         throw new RealmsUploadCanceledException();
      } else {
         this.verifyBelowSizeLimit(var1.getBytesWritten());
         File var5 = var2.toFile();
         String var6 = var4 ? var3 : var3 + var5.getName();
         TarArchiveEntry var7 = new TarArchiveEntry(var5, var6);
         var1.putArchiveEntry(var7);
         if (var5.isFile()) {
            FileInputStream var8 = new FileInputStream(var5);

            try {
               ((InputStream)var8).transferTo(var1);
            } catch (Throwable var14) {
               try {
                  ((InputStream)var8).close();
               } catch (Throwable var13) {
                  var14.addSuppressed(var13);
               }

               throw var14;
            }

            ((InputStream)var8).close();
            var1.closeArchiveEntry();
         } else {
            var1.closeArchiveEntry();
            File[] var15 = var5.listFiles();
            if (var15 != null) {
               File[] var9 = var15;
               int var10 = var15.length;

               for(int var11 = 0; var11 < var10; ++var11) {
                  File var12 = var9[var11];
                  this.addFileToTarGz(var1, var12.toPath(), var6 + "/", false);
               }
            }
         }

      }
   }

   private void verifyBelowSizeLimit(long var1) {
      if (var1 > 5368709120L) {
         throw new RealmsUploadTooLargeException(5368709120L);
      }
   }
}
