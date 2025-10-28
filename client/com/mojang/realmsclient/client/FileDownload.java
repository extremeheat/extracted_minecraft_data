package com.mojang.realmsclient.client;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Locale;
import java.util.OptionalLong;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.CheckReturnValue;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.ReportedNbtException;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.validation.ContentValidationException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class FileDownload {
   private static final Logger LOGGER = LogUtils.getLogger();
   private volatile boolean cancelled;
   private volatile boolean finished;
   private volatile boolean error;
   private volatile boolean extracting;
   private volatile @Nullable File tempFile;
   private volatile File resourcePackPath;
   private volatile @Nullable CompletableFuture<?> pendingRequest;
   private @Nullable Thread currentThread;
   private static final String[] INVALID_FILE_NAMES = new String[]{"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

   public FileDownload() {
      super();
   }

   private <T> @Nullable T joinCancellableRequest(CompletableFuture<T> var1) throws Throwable {
      this.pendingRequest = var1;
      if (this.cancelled) {
         var1.cancel(true);
         return null;
      } else {
         try {
            try {
               return (T)var1.join();
            } catch (CompletionException var3) {
               throw var3.getCause();
            }
         } catch (CancellationException var4) {
            return null;
         }
      }
   }

   private static HttpClient createClient() {
      return HttpClient.newBuilder().executor(Util.nonCriticalIoPool()).connectTimeout(Duration.ofMinutes(2L)).build();
   }

   private static HttpRequest.Builder createRequest(String var0) {
      return HttpRequest.newBuilder(URI.create(var0)).timeout(Duration.ofMinutes(2L));
   }

   @CheckReturnValue
   public static OptionalLong contentLength(String var0) {
      try {
         HttpClient var1 = createClient();

         OptionalLong var3;
         try {
            HttpResponse var2 = var1.send(createRequest(var0).HEAD().build(), BodyHandlers.discarding());
            var3 = var2.headers().firstValueAsLong("Content-Length");
         } catch (Throwable var5) {
            if (var1 != null) {
               try {
                  var1.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (var1 != null) {
            var1.close();
         }

         return var3;
      } catch (Exception var6) {
         LOGGER.error("Unable to get content length for download");
         return OptionalLong.empty();
      }
   }

   public void download(WorldDownload var1, String var2, RealmsDownloadLatestWorldScreen.DownloadStatus var3, LevelStorageSource var4) {
      if (this.currentThread == null) {
         this.currentThread = new Thread(() -> {
            HttpClient var5 = createClient();

            label261: {
               try {
                  try {
                     this.tempFile = File.createTempFile("backup", ".tar.gz");
                     this.download(var3, var5, var1.downloadLink(), this.tempFile);
                     this.finishWorldDownload(var2.trim(), this.tempFile, var4, var3);
                  } catch (Exception var23) {
                     LOGGER.error("Caught exception while downloading world", var23);
                     this.error = true;
                  } finally {
                     this.pendingRequest = null;
                     if (this.tempFile != null) {
                        this.tempFile.delete();
                     }

                     this.tempFile = null;
                  }

                  if (this.error) {
                     break label261;
                  }

                  String var6 = var1.resourcePackUrl();
                  if (!var6.isEmpty() && !var1.resourcePackHash().isEmpty()) {
                     try {
                        this.tempFile = File.createTempFile("resources", ".tar.gz");
                        this.download(var3, var5, var6, this.tempFile);
                        this.finishResourcePackDownload(var3, this.tempFile, var1);
                     } catch (Exception var22) {
                        LOGGER.error("Caught exception while downloading resource pack", var22);
                        this.error = true;
                     } finally {
                        this.pendingRequest = null;
                        if (this.tempFile != null) {
                           this.tempFile.delete();
                        }

                        this.tempFile = null;
                     }
                  }

                  this.finished = true;
               } catch (Throwable var26) {
                  if (var5 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var21) {
                        var26.addSuppressed(var21);
                     }
                  }

                  throw var26;
               }

               if (var5 != null) {
                  var5.close();
               }

               return;
            }

            if (var5 != null) {
               var5.close();
            }

         });
         this.currentThread.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
         this.currentThread.start();
      }
   }

   private void download(RealmsDownloadLatestWorldScreen.DownloadStatus var1, HttpClient var2, String var3, File var4) throws IOException {
      HttpRequest var5 = createRequest(var3).GET().build();

      HttpResponse var6;
      try {
         var6 = (HttpResponse)this.joinCancellableRequest(var2.sendAsync(var5, BodyHandlers.ofInputStream()));
      } catch (Error var14) {
         throw var14;
      } catch (Throwable var15) {
         LOGGER.error("Failed to download {}", var3, var15);
         this.error = true;
         return;
      }

      if (var6 != null && !this.cancelled) {
         if (var6.statusCode() != 200) {
            this.error = true;
         } else {
            var1.totalBytes = var6.headers().firstValueAsLong("Content-Length").orElse(0L);
            InputStream var7 = (InputStream)var6.body();

            try {
               FileOutputStream var8 = new FileOutputStream(var4);

               try {
                  var7.transferTo(new DownloadCountingOutputStream(var8, var1));
               } catch (Throwable var13) {
                  try {
                     ((OutputStream)var8).close();
                  } catch (Throwable var12) {
                     var13.addSuppressed(var12);
                  }

                  throw var13;
               }

               ((OutputStream)var8).close();
            } catch (Throwable var16) {
               if (var7 != null) {
                  try {
                     var7.close();
                  } catch (Throwable var11) {
                     var16.addSuppressed(var11);
                  }
               }

               throw var16;
            }

            if (var7 != null) {
               var7.close();
            }

         }
      }
   }

   public void cancel() {
      if (this.tempFile != null) {
         this.tempFile.delete();
         this.tempFile = null;
      }

      this.cancelled = true;
      CompletableFuture var1 = this.pendingRequest;
      if (var1 != null) {
         var1.cancel(true);
      }

   }

   public boolean isFinished() {
      return this.finished;
   }

   public boolean isError() {
      return this.error;
   }

   public boolean isExtracting() {
      return this.extracting;
   }

   public static String findAvailableFolderName(String var0) {
      var0 = var0.replaceAll("[\\./\"]", "_");

      for(String var4 : INVALID_FILE_NAMES) {
         if (var0.equalsIgnoreCase(var4)) {
            var0 = "_" + var0 + "_";
         }
      }

      return var0;
   }

   private void untarGzipArchive(String var1, @Nullable File var2, LevelStorageSource var3) throws IOException {
      Pattern var4 = Pattern.compile(".*-([0-9]+)$");
      int var6 = 1;

      for(char var10 : SharedConstants.ILLEGAL_FILE_CHARACTERS) {
         var1 = var1.replace(var10, '_');
      }

      if (StringUtils.isEmpty(var1)) {
         var1 = "Realm";
      }

      var1 = findAvailableFolderName(var1);

      try {
         for(LevelStorageSource.LevelDirectory var48 : var3.findLevelCandidates()) {
            String var50 = var48.directoryName();
            if (var50.toLowerCase(Locale.ROOT).startsWith(var1.toLowerCase(Locale.ROOT))) {
               Matcher var52 = var4.matcher(var50);
               if (var52.matches()) {
                  int var11 = Integer.parseInt(var52.group(1));
                  if (var11 > var6) {
                     var6 = var11;
                  }
               } else {
                  ++var6;
               }
            }
         }
      } catch (Exception var43) {
         LOGGER.error("Error getting level list", var43);
         this.error = true;
         return;
      }

      String var5;
      if (var3.isNewLevelIdAcceptable(var1) && var6 <= 1) {
         var5 = var1;
      } else {
         var5 = var1 + (var6 == 1 ? "" : "-" + var6);
         if (!var3.isNewLevelIdAcceptable(var5)) {
            boolean var46 = false;

            while(!var46) {
               ++var6;
               var5 = var1 + (var6 == 1 ? "" : "-" + var6);
               if (var3.isNewLevelIdAcceptable(var5)) {
                  var46 = true;
               }
            }
         }
      }

      TarArchiveInputStream var47 = null;
      File var49 = new File(Minecraft.getInstance().gameDirectory.getAbsolutePath(), "saves");

      try {
         var49.mkdir();
         var47 = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(var2))));

         for(TarArchiveEntry var51 = var47.getNextTarEntry(); var51 != null; var51 = var47.getNextTarEntry()) {
            File var53 = new File(var49, var51.getName().replace("world", var5));
            if (var51.isDirectory()) {
               var53.mkdirs();
            } else {
               var53.createNewFile();
               FileOutputStream var54 = new FileOutputStream(var53);

               try {
                  IOUtils.copy(var47, var54);
               } catch (Throwable var37) {
                  try {
                     var54.close();
                  } catch (Throwable var36) {
                     var37.addSuppressed(var36);
                  }

                  throw var37;
               }

               var54.close();
            }
         }
      } catch (Exception var41) {
         LOGGER.error("Error extracting world", var41);
         this.error = true;
      } finally {
         if (var47 != null) {
            var47.close();
         }

         if (var2 != null) {
            var2.delete();
         }

         try (LevelStorageSource.LevelStorageAccess var15 = var3.validateAndCreateAccess(var5)) {
            var15.renameAndDropPlayer(var5);
         } catch (NbtException | ReportedNbtException | IOException var39) {
            LOGGER.error("Failed to modify unpacked realms level {}", var5, var39);
         } catch (ContentValidationException var40) {
            LOGGER.warn("Failed to download file", var40);
         }

         this.resourcePackPath = new File(var49, var5 + File.separator + "resources.zip");
      }

   }

   private void finishWorldDownload(String var1, File var2, LevelStorageSource var3, RealmsDownloadLatestWorldScreen.DownloadStatus var4) {
      if (var4.bytesWritten >= var4.totalBytes && !this.cancelled && !this.error) {
         try {
            this.extracting = true;
            this.untarGzipArchive(var1, var2, var3);
         } catch (IOException var6) {
            LOGGER.error("Error extracting archive", var6);
            this.error = true;
         }
      }

   }

   private void finishResourcePackDownload(RealmsDownloadLatestWorldScreen.DownloadStatus var1, File var2, WorldDownload var3) {
      if (var1.bytesWritten >= var1.totalBytes && !this.cancelled) {
         try {
            String var4 = Hashing.sha1().hashBytes(Files.toByteArray(var2)).toString();
            if (var4.equals(var3.resourcePackHash())) {
               FileUtils.copyFile(var2, this.resourcePackPath);
               this.finished = true;
            } else {
               LOGGER.error("Resourcepack had wrong hash (expected {}, found {}). Deleting it.", var3.resourcePackHash(), var4);
               FileUtils.deleteQuietly(var2);
               this.error = true;
            }
         } catch (IOException var5) {
            LOGGER.error("Error copying resourcepack file: {}", var5.getMessage());
            this.error = true;
         }
      }

   }

   static class DownloadCountingOutputStream extends CountingOutputStream {
      private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;

      public DownloadCountingOutputStream(OutputStream var1, RealmsDownloadLatestWorldScreen.DownloadStatus var2) {
         super(var1);
         this.downloadStatus = var2;
      }

      protected void afterWrite(int var1) throws IOException {
         super.afterWrite(var1);
         this.downloadStatus.bytesWritten = this.getByteCount();
      }
   }
}
