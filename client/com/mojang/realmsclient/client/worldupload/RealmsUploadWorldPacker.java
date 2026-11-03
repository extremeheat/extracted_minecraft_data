package com.mojang.realmsclient.client.worldupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
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

   public static File pack(final Path directoryToPack, final BooleanSupplier isCanceled) throws IOException {
      return (new RealmsUploadWorldPacker(directoryToPack, isCanceled)).tarGzipArchive(File.createTempFile("realms-upload-file", ".tar.gz"));
   }

   private RealmsUploadWorldPacker(final Path directoryToPack, final BooleanSupplier isCanceled) {
      super();
      this.isCanceled = isCanceled;
      this.directoryToPack = directoryToPack;
   }

   private File tarGzipArchive(final File file) throws IOException {
      try {
         OutputStream output = new FileOutputStream(file);

         try {
            GZIPOutputStream gzip = new GZIPOutputStream(output);

            try {
               TarArchiveOutputStream tar = new TarArchiveOutputStream(gzip);

               try {
                  tar.setLongFileMode(3);
                  this.addFileToTarGz(tar, this.directoryToPack, "world", true);
                  if (this.isCanceled.getAsBoolean()) {
                     throw new RealmsUploadCanceledException();
                  }

                  tar.finish();
               } catch (Throwable var11) {
                  try {
                     tar.close();
                  } catch (Throwable var10) {
                     var11.addSuppressed(var10);
                  }

                  throw var11;
               }

               tar.close();
            } catch (Throwable var12) {
               try {
                  gzip.close();
               } catch (Throwable var9) {
                  var12.addSuppressed(var9);
               }

               throw var12;
            }

            gzip.close();
         } catch (Throwable var13) {
            try {
               output.close();
            } catch (Throwable var8) {
               var13.addSuppressed(var8);
            }

            throw var13;
         }

         output.close();
         this.verifyBelowSizeLimit(file.length());
         return file;
      } catch (RuntimeException | Error | IOException var14) {
         try {
            Files.deleteIfExists(file.toPath());
         } catch (IOException cleanupFailure) {
            ((Throwable)var14).addSuppressed(cleanupFailure);
         }

         throw var14;
      }
   }

   private void addFileToTarGz(final TarArchiveOutputStream out, final Path path, final String base, final boolean root) throws IOException {
      if (this.isCanceled.getAsBoolean()) {
         throw new RealmsUploadCanceledException();
      } else {
         this.verifyBelowSizeLimit(out.getBytesWritten());
         File file = path.toFile();
         String entryName = root ? base : base + file.getName();
         TarArchiveEntry entry = new TarArchiveEntry(file, entryName);
         out.putArchiveEntry(entry);
         if (file.isFile()) {
            InputStream is = new FileInputStream(file);

            try {
               is.transferTo(out);
            } catch (Throwable var14) {
               try {
                  is.close();
               } catch (Throwable var13) {
                  var14.addSuppressed(var13);
               }

               throw var14;
            }

            is.close();
            out.closeArchiveEntry();
         } else {
            out.closeArchiveEntry();
            File[] children = file.listFiles();
            if (children != null) {
               for(File child : children) {
                  this.addFileToTarGz(out, child.toPath(), entryName + "/", false);
               }
            }
         }

      }
   }

   private void verifyBelowSizeLimit(final long sizeInByte) {
      if (sizeInByte > 5368709120L) {
         throw new RealmsUploadTooLargeException(5368709120L);
      }
   }
}
