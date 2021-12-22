package com.mojang.realmsclient.client;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
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
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileDownload {
   static final Logger LOGGER = LogManager.getLogger();
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

            label1409: {
               CloseableHttpResponse var6;
               FileOutputStream var7;
               FileDownload.DownloadCountingOutputStream var9;
               FileDownload.ResourcePackProgressListener var106;
               label1403: {
                  try {
                     var90 = true;
                     this.tempFile = File.createTempFile("backup", ".tar.gz");
                     this.request = new HttpGet(var1.downloadLink);
                     var5 = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
                     var6 = var5.execute(this.request);
                     var3.totalBytes = Long.parseLong(var6.getFirstHeader("Content-Length").getValue());
                     if (var6.getStatusLine().getStatusCode() != 200) {
                        this.error = true;
                        this.request.abort();
                        var90 = false;
                        break label1409;
                     }

                     var7 = new FileOutputStream(this.tempFile);
                     FileDownload.ProgressListener var8 = new FileDownload.ProgressListener(var2.trim(), this.tempFile, var4, var3);
                     var9 = new FileDownload.DownloadCountingOutputStream(var7);
                     var9.setListener(var8);
                     IOUtils.copy(var6.getEntity().getContent(), var9);
                     var90 = false;
                     break label1403;
                  } catch (Exception var103) {
                     LOGGER.error("Caught exception while downloading: {}", var103.getMessage());
                     this.error = true;
                     var90 = false;
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
                                 FileDownload.ResourcePackProgressListener var17 = new FileDownload.ResourcePackProgressListener(this.tempFile, var3, var1);
                                 FileDownload.DownloadCountingOutputStream var18 = new FileDownload.DownloadCountingOutputStream(var16);
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
                           var106 = new FileDownload.ResourcePackProgressListener(this.tempFile, var3, var1);
                           var9 = new FileDownload.DownloadCountingOutputStream(var7);
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
                        var106 = new FileDownload.ResourcePackProgressListener(this.tempFile, var3, var1);
                        var9 = new FileDownload.DownloadCountingOutputStream(var7);
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
                     CloseableHttpResponse var105 = var5.execute(this.request);
                     var3.totalBytes = Long.parseLong(var105.getFirstHeader("Content-Length").getValue());
                     if (var105.getStatusLine().getStatusCode() != 200) {
                        this.error = true;
                        this.request.abort();
                        return;
                     }

                     FileOutputStream var107 = new FileOutputStream(this.tempFile);
                     FileDownload.ResourcePackProgressListener var108 = new FileDownload.ResourcePackProgressListener(this.tempFile, var3, var1);
                     FileDownload.DownloadCountingOutputStream var10 = new FileDownload.DownloadCountingOutputStream(var107);
                     var10.setListener(var108);
                     IOUtils.copy(var105.getEntity().getContent(), var10);
                  } catch (Exception var97) {
                     LOGGER.error("Caught exception while downloading: {}", var97.getMessage());
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
               } catch (IOException var92) {
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
         Iterator var47 = var3.getLevelList().iterator();

         while(var47.hasNext()) {
            LevelSummary var49 = (LevelSummary)var47.next();
            if (var49.getLevelId().toLowerCase(Locale.ROOT).startsWith(var1.toLowerCase(Locale.ROOT))) {
               Matcher var52 = var4.matcher(var49.getLevelId());
               if (var52.matches()) {
                  int var54 = Integer.parseInt(var52.group(1));
                  if (var54 > var6) {
                     var6 = var54;
                  }
               } else {
                  ++var6;
               }
            }
         }
      } catch (Exception var46) {
         LOGGER.error("Error getting level list", var46);
         this.error = true;
         return;
      }

      String var5;
      if (var3.isNewLevelIdAcceptable(var1) && var6 <= 1) {
         var5 = var1;
      } else {
         var5 = var1 + (var6 == 1 ? "" : "-" + var6);
         if (!var3.isNewLevelIdAcceptable(var5)) {
            boolean var48 = false;

            while(!var48) {
               ++var6;
               var5 = var1 + (var6 == 1 ? "" : "-" + var6);
               if (var3.isNewLevelIdAcceptable(var5)) {
                  var48 = true;
               }
            }
         }
      }

      TarArchiveInputStream var50 = null;
      File var51 = new File(Minecraft.getInstance().gameDirectory.getAbsolutePath(), "saves");
      boolean var32 = false;

      LevelStorageSource.LevelStorageAccess var56;
      Path var57;
      label454: {
         try {
            var32 = true;
            var51.mkdir();
            var50 = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(var2))));

            for(TarArchiveEntry var53 = var50.getNextTarEntry(); var53 != null; var53 = var50.getNextTarEntry()) {
               File var55 = new File(var51, var53.getName().replace("world", var5));
               if (var53.isDirectory()) {
                  var55.mkdirs();
               } else {
                  var55.createNewFile();
                  FileOutputStream var11 = new FileOutputStream(var55);

                  try {
                     IOUtils.copy(var50, var11);
                  } catch (Throwable var37) {
                     try {
                        var11.close();
                     } catch (Throwable var35) {
                        var37.addSuppressed(var35);
                     }

                     throw var37;
                  }

                  var11.close();
               }
            }

            var32 = false;
            break label454;
         } catch (Exception var44) {
            LOGGER.error("Error extracting world", var44);
            this.error = true;
            var32 = false;
         } finally {
            if (var32) {
               if (var50 != null) {
                  var50.close();
               }

               if (var2 != null) {
                  var2.delete();
               }

               try {
                  LevelStorageSource.LevelStorageAccess var15 = var3.createAccess(var5);

                  try {
                     var15.renameLevel(var5.trim());
                     Path var16 = var15.getLevelPath(LevelResource.LEVEL_DATA_FILE);
                     deletePlayerTag(var16.toFile());
                  } catch (Throwable var38) {
                     if (var15 != null) {
                        try {
                           var15.close();
                        } catch (Throwable var33) {
                           var38.addSuppressed(var33);
                        }
                     }

                     throw var38;
                  }

                  if (var15 != null) {
                     var15.close();
                  }
               } catch (IOException var39) {
                  LOGGER.error("Failed to rename unpacked realms level {}", var5, var39);
               }

               this.resourcePackPath = new File(var51, var5 + File.separator + "resources.zip");
            }
         }

         if (var50 != null) {
            var50.close();
         }

         if (var2 != null) {
            var2.delete();
         }

         try {
            var56 = var3.createAccess(var5);

            try {
               var56.renameLevel(var5.trim());
               var57 = var56.getLevelPath(LevelResource.LEVEL_DATA_FILE);
               deletePlayerTag(var57.toFile());
            } catch (Throwable var40) {
               if (var56 != null) {
                  try {
                     var56.close();
                  } catch (Throwable var34) {
                     var40.addSuppressed(var34);
                  }
               }

               throw var40;
            }

            if (var56 != null) {
               var56.close();
            }
         } catch (IOException var41) {
            LOGGER.error("Failed to rename unpacked realms level {}", var5, var41);
         }

         this.resourcePackPath = new File(var51, var5 + File.separator + "resources.zip");
         return;
      }

      if (var50 != null) {
         var50.close();
      }

      if (var2 != null) {
         var2.delete();
      }

      try {
         var56 = var3.createAccess(var5);

         try {
            var56.renameLevel(var5.trim());
            var57 = var56.getLevelPath(LevelResource.LEVEL_DATA_FILE);
            deletePlayerTag(var57.toFile());
         } catch (Throwable var42) {
            if (var56 != null) {
               try {
                  var56.close();
               } catch (Throwable var36) {
                  var42.addSuppressed(var36);
               }
            }

            throw var42;
         }

         if (var56 != null) {
            var56.close();
         }
      } catch (IOException var43) {
         LOGGER.error("Failed to rename unpacked realms level {}", var5, var43);
      }

      this.resourcePackPath = new File(var51, var5 + File.separator + "resources.zip");
   }

   private static void deletePlayerTag(File var0) {
      if (var0.exists()) {
         try {
            CompoundTag var1 = NbtIo.readCompressed(var0);
            CompoundTag var2 = var1.getCompound("Data");
            var2.remove("Player");
            NbtIo.writeCompressed(var1, var0);
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      }

   }

   class ResourcePackProgressListener implements ActionListener {
      private final File tempFile;
      private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
      private final WorldDownload worldDownload;

      ResourcePackProgressListener(File var2, RealmsDownloadLatestWorldScreen.DownloadStatus var3, WorldDownload var4) {
         super();
         this.tempFile = var2;
         this.downloadStatus = var3;
         this.worldDownload = var4;
      }

      public void actionPerformed(ActionEvent var1) {
         this.downloadStatus.bytesWritten = ((FileDownload.DownloadCountingOutputStream)var1.getSource()).getByteCount();
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

      ProgressListener(String var2, File var3, LevelStorageSource var4, RealmsDownloadLatestWorldScreen.DownloadStatus var5) {
         super();
         this.worldName = var2;
         this.tempFile = var3;
         this.levelStorageSource = var4;
         this.downloadStatus = var5;
      }

      public void actionPerformed(ActionEvent var1) {
         this.downloadStatus.bytesWritten = ((FileDownload.DownloadCountingOutputStream)var1.getSource()).getByteCount();
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
