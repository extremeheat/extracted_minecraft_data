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
import java.util.Iterator;
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
            boolean var90 = false;

            CloseableHttpResponse var6;
            FileOutputStream var7;
            DownloadCountingOutputStream var9;
            ResourcePackProgressListener var106;
            label1405: {
               label1380: {
                  try {
                     var90 = true;
                     this.tempFile = File.createTempFile("backup", ".tar.gz");
                     this.request = new HttpGet(var1.downloadLink);
                     var5 = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
                     var6 = var5.execute(this.request);
                     var3.totalBytes = Long.parseLong(var6.getFirstHeader("Content-Length").getValue());
                     if (var6.getStatusLine().getStatusCode() == 200) {
                        var7 = new FileOutputStream(this.tempFile);
                        ProgressListener var8 = new ProgressListener(var2.trim(), this.tempFile, var4, var3);
                        var9 = new DownloadCountingOutputStream(var7);
                        var9.setListener(var8);
                        IOUtils.copy(var6.getEntity().getContent(), var9);
                        var90 = false;
                        break label1405;
                     }

                     this.error = true;
                     this.request.abort();
                     var90 = false;
                  } catch (Exception var103) {
                     LOGGER.error("Caught exception while downloading: {}", var103.getMessage());
                     this.error = true;
                     var90 = false;
                     break label1380;
                  } finally {
                     if (var90) {
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
                              } catch (Exception var95) {
                                 LOGGER.error("Caught exception while downloading: {}", var95.getMessage());
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
                           } catch (IOException var91) {
                              LOGGER.error("Failed to close Realms download client");
                           }
                        }

                     }
                  }

                  this.request.releaseConnection();
                  if (this.tempFile != null) {
                     this.tempFile.delete();
                  }

                  if (!this.error) {
                     if (!var1.resourcePackUrl.isEmpty() && !var1.resourcePackHash.isEmpty()) {
                        label1317: {
                           try {
                              this.tempFile = File.createTempFile("resources", ".tar.gz");
                              this.request = new HttpGet(var1.resourcePackUrl);
                              CloseableHttpResponse var105 = var5.execute(this.request);
                              var3.totalBytes = Long.parseLong(var105.getFirstHeader("Content-Length").getValue());
                              if (var105.getStatusLine().getStatusCode() == 200) {
                                 FileOutputStream var107 = new FileOutputStream(this.tempFile);
                                 ResourcePackProgressListener var108 = new ResourcePackProgressListener(this.tempFile, var3, var1);
                                 DownloadCountingOutputStream var10 = new DownloadCountingOutputStream(var107);
                                 var10.setListener(var108);
                                 IOUtils.copy(var105.getEntity().getContent(), var10);
                                 break label1317;
                              }

                              this.error = true;
                              this.request.abort();
                           } catch (Exception var97) {
                              LOGGER.error("Caught exception while downloading: {}", var97.getMessage());
                              this.error = true;
                              break label1317;
                           } finally {
                              this.request.releaseConnection();
                              if (this.tempFile != null) {
                                 this.tempFile.delete();
                              }

                           }

                           return;
                        }
                     } else {
                        this.finished = true;
                     }
                  }

                  if (var5 != null) {
                     try {
                        var5.close();
                     } catch (IOException var92) {
                        LOGGER.error("Failed to close Realms download client");
                     }
                  }

                  return;
               }

               this.request.releaseConnection();
               if (this.tempFile != null) {
                  this.tempFile.delete();
               }

               if (!this.error) {
                  if (!var1.resourcePackUrl.isEmpty() && !var1.resourcePackHash.isEmpty()) {
                     try {
                        this.tempFile = File.createTempFile("resources", ".tar.gz");
                        this.request = new HttpGet(var1.resourcePackUrl);
                        var6 = var5.execute(this.request);
                        var3.totalBytes = Long.parseLong(var6.getFirstHeader("Content-Length").getValue());
                        if (var6.getStatusLine().getStatusCode() != 200) {
                           this.error = true;
                           this.request.abort();
                           return;
                        }

                        var7 = new FileOutputStream(this.tempFile);
                        var106 = new ResourcePackProgressListener(this.tempFile, var3, var1);
                        var9 = new DownloadCountingOutputStream(var7);
                        var9.setListener(var106);
                        IOUtils.copy(var6.getEntity().getContent(), var9);
                     } catch (Exception var99) {
                        LOGGER.error("Caught exception while downloading: {}", var99.getMessage());
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
                  } catch (IOException var93) {
                     LOGGER.error("Failed to close Realms download client");
                  }

                  return;
               }

               return;
            }

            this.request.releaseConnection();
            if (this.tempFile != null) {
               this.tempFile.delete();
            }

            if (!this.error) {
               if (!var1.resourcePackUrl.isEmpty() && !var1.resourcePackHash.isEmpty()) {
                  try {
                     this.tempFile = File.createTempFile("resources", ".tar.gz");
                     this.request = new HttpGet(var1.resourcePackUrl);
                     var6 = var5.execute(this.request);
                     var3.totalBytes = Long.parseLong(var6.getFirstHeader("Content-Length").getValue());
                     if (var6.getStatusLine().getStatusCode() != 200) {
                        this.error = true;
                        this.request.abort();
                        return;
                     }

                     var7 = new FileOutputStream(this.tempFile);
                     var106 = new ResourcePackProgressListener(this.tempFile, var3, var1);
                     var9 = new DownloadCountingOutputStream(var7);
                     var9.setListener(var106);
                     IOUtils.copy(var6.getEntity().getContent(), var9);
                  } catch (Exception var101) {
                     LOGGER.error("Caught exception while downloading: {}", var101.getMessage());
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
               } catch (IOException var94) {
                  LOGGER.error("Failed to close Realms download client");
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
      String[] var1 = INVALID_FILE_NAMES;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String var4 = var1[var3];
         if (var0.equalsIgnoreCase(var4)) {
            var0 = "_" + var0 + "_";
         }
      }

      return var0;
   }

   void untarGzipArchive(String var1, @Nullable File var2, LevelStorageSource var3) throws IOException {
      Pattern var4 = Pattern.compile(".*-([0-9]+)$");
      int var6 = 1;
      char[] var7 = SharedConstants.ILLEGAL_FILE_CHARACTERS;
      int var8 = var7.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         char var10 = var7[var9];
         var1 = var1.replace(var10, '_');
      }

      if (StringUtils.isEmpty(var1)) {
         var1 = "Realm";
      }

      var1 = findAvailableFolderName(var1);

      try {
         Iterator var53 = var3.findLevelCandidates().iterator();

         while(var53.hasNext()) {
            LevelStorageSource.LevelDirectory var55 = (LevelStorageSource.LevelDirectory)var53.next();
            String var58 = var55.directoryName();
            if (var58.toLowerCase(Locale.ROOT).startsWith(var1.toLowerCase(Locale.ROOT))) {
               Matcher var60 = var4.matcher(var58);
               if (var60.matches()) {
                  int var11 = Integer.parseInt(var60.group(1));
                  if (var11 > var6) {
                     var6 = var11;
                  }
               } else {
                  ++var6;
               }
            }
         }
      } catch (Exception var52) {
         LOGGER.error("Error getting level list", var52);
         this.error = true;
         return;
      }

      String var5;
      if (var3.isNewLevelIdAcceptable(var1) && var6 <= 1) {
         var5 = var1;
      } else {
         var5 = var1 + (var6 == 1 ? "" : "-" + var6);
         if (!var3.isNewLevelIdAcceptable(var5)) {
            boolean var54 = false;

            while(!var54) {
               ++var6;
               var5 = var1 + (var6 == 1 ? "" : "-" + var6);
               if (var3.isNewLevelIdAcceptable(var5)) {
                  var54 = true;
               }
            }
         }
      }

      TarArchiveInputStream var56 = null;
      File var57 = new File(Minecraft.getInstance().gameDirectory.getAbsolutePath(), "saves");
      boolean var35 = false;

      LevelStorageSource.LevelStorageAccess var62;
      label463: {
         try {
            var35 = true;
            var57.mkdir();
            var56 = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(var2))));

            for(TarArchiveEntry var59 = var56.getNextTarEntry(); var59 != null; var59 = var56.getNextTarEntry()) {
               File var61 = new File(var57, var59.getName().replace("world", var5));
               if (var59.isDirectory()) {
                  var61.mkdirs();
               } else {
                  var61.createNewFile();
                  FileOutputStream var63 = new FileOutputStream(var61);

                  try {
                     IOUtils.copy(var56, var63);
                  } catch (Throwable var40) {
                     try {
                        var63.close();
                     } catch (Throwable var38) {
                        var40.addSuppressed(var38);
                     }

                     throw var40;
                  }

                  var63.close();
               }
            }

            var35 = false;
            break label463;
         } catch (Exception var50) {
            LOGGER.error("Error extracting world", var50);
            this.error = true;
            var35 = false;
         } finally {
            if (var35) {
               if (var56 != null) {
                  var56.close();
               }

               if (var2 != null) {
                  var2.delete();
               }

               try {
                  LevelStorageSource.LevelStorageAccess var15 = var3.validateAndCreateAccess(var5);

                  try {
                     var15.renameAndDropPlayer(var5);
                  } catch (Throwable var41) {
                     if (var15 != null) {
                        try {
                           var15.close();
                        } catch (Throwable var36) {
                           var41.addSuppressed(var36);
                        }
                     }

                     throw var41;
                  }

                  if (var15 != null) {
                     var15.close();
                  }
               } catch (NbtException | ReportedNbtException | IOException var42) {
                  LOGGER.error("Failed to modify unpacked realms level {}", var5, var42);
               } catch (ContentValidationException var43) {
                  LOGGER.warn("{}", var43.getMessage());
               }

               this.resourcePackPath = new File(var57, var5 + File.separator + "resources.zip");
            }
         }

         if (var56 != null) {
            var56.close();
         }

         if (var2 != null) {
            var2.delete();
         }

         try {
            var62 = var3.validateAndCreateAccess(var5);

            try {
               var62.renameAndDropPlayer(var5);
            } catch (Throwable var44) {
               if (var62 != null) {
                  try {
                     var62.close();
                  } catch (Throwable var37) {
                     var44.addSuppressed(var37);
                  }
               }

               throw var44;
            }

            if (var62 != null) {
               var62.close();
            }
         } catch (NbtException | ReportedNbtException | IOException var45) {
            LOGGER.error("Failed to modify unpacked realms level {}", var5, var45);
         } catch (ContentValidationException var46) {
            LOGGER.warn("{}", var46.getMessage());
         }

         this.resourcePackPath = new File(var57, var5 + File.separator + "resources.zip");
         return;
      }

      if (var56 != null) {
         var56.close();
      }

      if (var2 != null) {
         var2.delete();
      }

      try {
         var62 = var3.validateAndCreateAccess(var5);

         try {
            var62.renameAndDropPlayer(var5);
         } catch (Throwable var47) {
            if (var62 != null) {
               try {
                  var62.close();
               } catch (Throwable var39) {
                  var47.addSuppressed(var39);
               }
            }

            throw var47;
         }

         if (var62 != null) {
            var62.close();
         }
      } catch (NbtException | ReportedNbtException | IOException var48) {
         LOGGER.error("Failed to modify unpacked realms level {}", var5, var48);
      } catch (ContentValidationException var49) {
         LOGGER.warn("{}", var49.getMessage());
      }

      this.resourcePackPath = new File(var57, var5 + File.separator + "resources.zip");
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

   private static class DownloadCountingOutputStream extends CountingOutputStream {
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

   private class ProgressListener implements ActionListener {
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
}
