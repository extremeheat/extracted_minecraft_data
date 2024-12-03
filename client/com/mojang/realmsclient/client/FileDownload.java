package com.mojang.realmsclient.client;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
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
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;

public class FileDownload {
   static final Logger LOGGER = LogUtils.getLogger();
   volatile boolean cancelled;
   volatile boolean finished;
   volatile boolean error;
   volatile boolean extracting;
   @Nullable
   private volatile File tempFile;
   volatile File resourcePackPath;
   @Nullable
   private volatile HttpGet request;
   @Nullable
   private Thread currentThread;
   private final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).build();
   private static final String[] INVALID_FILE_NAMES = new String[]{"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

   public FileDownload() {
      super();
   }

   public long contentLength(String var1) {
      CloseableHttpClient var2 = null;
      HttpGet var3 = null;

      long var5;
      try {
         var3 = new HttpGet(var1);
         var2 = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
         CloseableHttpResponse var4 = var2.execute(var3);
         var5 = Long.parseLong(var4.getFirstHeader("Content-Length").getValue());
         return var5;
      } catch (Throwable var16) {
         LOGGER.error("Unable to get content length for download");
         var5 = 0L;
      } finally {
         if (var3 != null) {
            var3.releaseConnection();
         }

         if (var2 != null) {
            try {
               var2.close();
            } catch (IOException var15) {
               LOGGER.error("Could not close http client", var15);
            }
         }

      }

      return var5;
   }

   public void download(WorldDownload var1, String var2, RealmsDownloadLatestWorldScreen.DownloadStatus var3, LevelStorageSource var4) {
      if (this.currentThread == null) {
         this.currentThread = new Thread(() -> {
            CloseableHttpClient var5 = null;

            try {
               this.tempFile = File.createTempFile("backup", ".tar.gz");
               this.request = new HttpGet(var1.downloadLink);
               var5 = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
               CloseableHttpResponse var6 = var5.execute(this.request);
               var3.totalBytes = Long.parseLong(var6.getFirstHeader("Content-Length").getValue());
               if (var6.getStatusLine().getStatusCode() == 200) {
                  FileOutputStream var7 = new FileOutputStream(this.tempFile);
                  ProgressListener var8 = new ProgressListener(var2.trim(), this.tempFile, var4, var3);
                  DownloadCountingOutputStream var9 = new DownloadCountingOutputStream(var7);
                  var9.setListener(var8);
                  IOUtils.copy(var6.getEntity().getContent(), var9);
                  return;
               }

               this.error = true;
               this.request.abort();
            } catch (Exception var93) {
               LOGGER.error("Caught exception while downloading: {}", var93.getMessage());
               this.error = true;
               return;
            } finally {
               this.request.releaseConnection();
               if (this.tempFile != null) {
                  this.tempFile.delete();
               }

               if (!this.error) {
                  if (!var1.resourcePackUrl.isEmpty() && !var1.resourcePackHash.isEmpty()) {
                     try {
                        this.tempFile = File.createTempFile("resources", ".tar.gz");
                        this.request = new HttpGet(var1.resourcePackUrl);
                        CloseableHttpResponse var15 = var5.execute(this.request);
                        var3.totalBytes = Long.parseLong(var15.getFirstHeader("Content-Length").getValue());
                        if (var15.getStatusLine().getStatusCode() != 200) {
                           this.error = true;
                           this.request.abort();
                           return;
                        }

                        FileOutputStream var16 = new FileOutputStream(this.tempFile);
                        ResourcePackProgressListener var17 = new ResourcePackProgressListener(this.tempFile, var3, var1);
                        DownloadCountingOutputStream var18 = new DownloadCountingOutputStream(var16);
                        var18.setListener(var17);
                        IOUtils.copy(var15.getEntity().getContent(), var18);
                     } catch (Exception var91) {
                        LOGGER.error("Caught exception while downloading: {}", var91.getMessage());
                        this.error = true;
                     } finally {
                        this.request.releaseConnection();
                        if (this.tempFile != null) {
                           this.tempFile.delete();
                        }

                     }
                  } else {
                     this.finished = true;
                  }
               }

               if (var5 != null) {
                  try {
                     var5.close();
                  } catch (IOException var90) {
                     LOGGER.error("Failed to close Realms download client");
                  }
               }

            }

         });
         this.currentThread.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
         this.currentThread.start();
      }
   }

   public void cancel() {
      if (this.request != null) {
         this.request.abort();
      }

      if (this.tempFile != null) {
         this.tempFile.delete();
      }

      this.cancelled = true;
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

   void untarGzipArchive(String var1, @Nullable File var2, LevelStorageSource var3) throws IOException {
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
            LOGGER.warn("{}", var40.getMessage());
         }

         this.resourcePackPath = new File(var49, var5 + File.separator + "resources.zip");
      }

   }

   class ProgressListener implements ActionListener {
      private final String worldName;
      private final File tempFile;
      private final LevelStorageSource levelStorageSource;
      private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;

      ProgressListener(final String var2, final File var3, final LevelStorageSource var4, final RealmsDownloadLatestWorldScreen.DownloadStatus var5) {
         super();
         this.worldName = var2;
         this.tempFile = var3;
         this.levelStorageSource = var4;
         this.downloadStatus = var5;
      }

      public void actionPerformed(ActionEvent var1) {
         this.downloadStatus.bytesWritten = ((DownloadCountingOutputStream)var1.getSource()).getByteCount();
         if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled && !FileDownload.this.error) {
            try {
               FileDownload.this.extracting = true;
               FileDownload.this.untarGzipArchive(this.worldName, this.tempFile, this.levelStorageSource);
            } catch (IOException var3) {
               FileDownload.LOGGER.error("Error extracting archive", var3);
               FileDownload.this.error = true;
            }
         }

      }
   }

   class ResourcePackProgressListener implements ActionListener {
      private final File tempFile;
      private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
      private final WorldDownload worldDownload;

      ResourcePackProgressListener(final File var2, final RealmsDownloadLatestWorldScreen.DownloadStatus var3, final WorldDownload var4) {
         super();
         this.tempFile = var2;
         this.downloadStatus = var3;
         this.worldDownload = var4;
      }

      public void actionPerformed(ActionEvent var1) {
         this.downloadStatus.bytesWritten = ((DownloadCountingOutputStream)var1.getSource()).getByteCount();
         if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled) {
            try {
               String var2 = Hashing.sha1().hashBytes(Files.toByteArray(this.tempFile)).toString();
               if (var2.equals(this.worldDownload.resourcePackHash)) {
                  FileUtils.copyFile(this.tempFile, FileDownload.this.resourcePackPath);
                  FileDownload.this.finished = true;
               } else {
                  FileDownload.LOGGER.error("Resourcepack had wrong hash (expected {}, found {}). Deleting it.", this.worldDownload.resourcePackHash, var2);
                  FileUtils.deleteQuietly(this.tempFile);
                  FileDownload.this.error = true;
               }
            } catch (IOException var3) {
               FileDownload.LOGGER.error("Error copying resourcepack file: {}", var3.getMessage());
               FileDownload.this.error = true;
            }
         }

      }
   }

   static class DownloadCountingOutputStream extends CountingOutputStream {
      @Nullable
      private ActionListener listener;

      public DownloadCountingOutputStream(OutputStream var1) {
         super(var1);
      }

      public void setListener(ActionListener var1) {
         this.listener = var1;
      }

      protected void afterWrite(int var1) throws IOException {
         super.afterWrite(var1);
         if (this.listener != null) {
            this.listener.actionPerformed(new ActionEvent(this, 0, (String)null));
         }

      }
   }
}
