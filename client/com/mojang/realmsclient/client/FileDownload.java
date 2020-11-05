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
   private static final Logger LOGGER = LogManager.getLogger();
   private volatile boolean cancelled;
   private volatile boolean finished;
   private volatile boolean error;
   private volatile boolean extracting;
   private volatile File tempFile;
   private volatile File resourcePackPath;
   private volatile HttpGet request;
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
            FileDownload.DownloadCountingOutputStream var9;
            FileDownload.ResourcePackProgressListener var106;
            label1404: {
               label1379: {
                  try {
                     var90 = true;
                     this.tempFile = File.createTempFile("backup", ".tar.gz");
                     this.request = new HttpGet(var1.downloadLink);
                     var5 = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
                     var6 = var5.execute(this.request);
                     var3.totalBytes = Long.parseLong(var6.getFirstHeader("Content-Length").getValue());
                     if (var6.getStatusLine().getStatusCode() == 200) {
                        var7 = new FileOutputStream(this.tempFile);
                        FileDownload.ProgressListener var8 = new FileDownload.ProgressListener(var2.trim(), this.tempFile, var4, var3);
                        var9 = new FileDownload.DownloadCountingOutputStream(var7);
                        var9.setListener(var8);
                        IOUtils.copy(var6.getEntity().getContent(), var9);
                        var90 = false;
                        break label1404;
                     }

                     this.error = true;
                     this.request.abort();
                     var90 = false;
                  } catch (Exception var103) {
                     LOGGER.error("Caught exception while downloading: {}", var103.getMessage());
                     this.error = true;
                     var90 = false;
                     break label1379;
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
                        label1316: {
                           try {
                              this.tempFile = File.createTempFile("resources", ".tar.gz");
                              this.request = new HttpGet(var1.resourcePackUrl);
                              CloseableHttpResponse var105 = var5.execute(this.request);
                              var3.totalBytes = Long.parseLong(var105.getFirstHeader("Content-Length").getValue());
                              if (var105.getStatusLine().getStatusCode() == 200) {
                                 FileOutputStream var107 = new FileOutputStream(this.tempFile);
                                 FileDownload.ResourcePackProgressListener var108 = new FileDownload.ResourcePackProgressListener(this.tempFile, var3, var1);
                                 FileDownload.DownloadCountingOutputStream var10 = new FileDownload.DownloadCountingOutputStream(var107);
                                 var10.setListener(var108);
                                 IOUtils.copy(var105.getEntity().getContent(), var10);
                                 break label1316;
                              }

                              this.error = true;
                              this.request.abort();
                           } catch (Exception var97) {
                              LOGGER.error("Caught exception while downloading: {}", var97.getMessage());
                              this.error = true;
                              break label1316;
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

   private void untarGzipArchive(String var1, File var2, LevelStorageSource var3) throws IOException {
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
         Iterator var146 = var3.getLevelList().iterator();

         while(var146.hasNext()) {
            LevelSummary var148 = (LevelSummary)var146.next();
            if (var148.getLevelId().toLowerCase(Locale.ROOT).startsWith(var1.toLowerCase(Locale.ROOT))) {
               Matcher var151 = var4.matcher(var148.getLevelId());
               if (var151.matches()) {
                  if (Integer.valueOf(var151.group(1)) > var6) {
                     var6 = Integer.valueOf(var151.group(1));
                  }
               } else {
                  ++var6;
               }
            }
         }
      } catch (Exception var145) {
         LOGGER.error("Error getting level list", var145);
         this.error = true;
         return;
      }

      String var5;
      if (var3.isNewLevelIdAcceptable(var1) && var6 <= 1) {
         var5 = var1;
      } else {
         var5 = var1 + (var6 == 1 ? "" : "-" + var6);
         if (!var3.isNewLevelIdAcceptable(var5)) {
            boolean var147 = false;

            while(!var147) {
               ++var6;
               var5 = var1 + (var6 == 1 ? "" : "-" + var6);
               if (var3.isNewLevelIdAcceptable(var5)) {
                  var147 = true;
               }
            }
         }
      }

      TarArchiveInputStream var149 = null;
      File var150 = new File(Minecraft.getInstance().gameDirectory.getAbsolutePath(), "saves");
      boolean var108 = false;

      LevelStorageSource.LevelStorageAccess var154;
      Throwable var155;
      Path var156;
      label1421: {
         try {
            var108 = true;
            var150.mkdir();
            var149 = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(var2))));

            for(TarArchiveEntry var152 = var149.getNextTarEntry(); var152 != null; var152 = var149.getNextTarEntry()) {
               File var153 = new File(var150, var152.getName().replace("world", var5));
               if (var152.isDirectory()) {
                  var153.mkdirs();
               } else {
                  var153.createNewFile();
                  FileOutputStream var11 = new FileOutputStream(var153);
                  Throwable var12 = null;

                  try {
                     IOUtils.copy(var149, var11);
                  } catch (Throwable var135) {
                     var12 = var135;
                     throw var135;
                  } finally {
                     if (var11 != null) {
                        if (var12 != null) {
                           try {
                              var11.close();
                           } catch (Throwable var132) {
                              var12.addSuppressed(var132);
                           }
                        } else {
                           var11.close();
                        }
                     }

                  }
               }
            }

            var108 = false;
            break label1421;
         } catch (Exception var143) {
            LOGGER.error("Error extracting world", var143);
            this.error = true;
            var108 = false;
         } finally {
            if (var108) {
               if (var149 != null) {
                  var149.close();
               }

               if (var2 != null) {
                  var2.delete();
               }

               try {
                  LevelStorageSource.LevelStorageAccess var21 = var3.createAccess(var5);
                  Throwable var22 = null;

                  try {
                     var21.renameLevel(var5.trim());
                     Path var23 = var21.getLevelPath(LevelResource.LEVEL_DATA_FILE);
                     deletePlayerTag(var23.toFile());
                  } catch (Throwable var129) {
                     var22 = var129;
                     throw var129;
                  } finally {
                     if (var21 != null) {
                        if (var22 != null) {
                           try {
                              var21.close();
                           } catch (Throwable var128) {
                              var22.addSuppressed(var128);
                           }
                        } else {
                           var21.close();
                        }
                     }

                  }
               } catch (IOException var137) {
                  LOGGER.error("Failed to rename unpacked realms level {}", var5, var137);
               }

               this.resourcePackPath = new File(var150, var5 + File.separator + "resources.zip");
            }
         }

         if (var149 != null) {
            var149.close();
         }

         if (var2 != null) {
            var2.delete();
         }

         try {
            var154 = var3.createAccess(var5);
            var155 = null;

            try {
               var154.renameLevel(var5.trim());
               var156 = var154.getLevelPath(LevelResource.LEVEL_DATA_FILE);
               deletePlayerTag(var156.toFile());
            } catch (Throwable var131) {
               var155 = var131;
               throw var131;
            } finally {
               if (var154 != null) {
                  if (var155 != null) {
                     try {
                        var154.close();
                     } catch (Throwable var130) {
                        var155.addSuppressed(var130);
                     }
                  } else {
                     var154.close();
                  }
               }

            }
         } catch (IOException var139) {
            LOGGER.error("Failed to rename unpacked realms level {}", var5, var139);
         }

         this.resourcePackPath = new File(var150, var5 + File.separator + "resources.zip");
         return;
      }

      if (var149 != null) {
         var149.close();
      }

      if (var2 != null) {
         var2.delete();
      }

      try {
         var154 = var3.createAccess(var5);
         var155 = null;

         try {
            var154.renameLevel(var5.trim());
            var156 = var154.getLevelPath(LevelResource.LEVEL_DATA_FILE);
            deletePlayerTag(var156.toFile());
         } catch (Throwable var134) {
            var155 = var134;
            throw var134;
         } finally {
            if (var154 != null) {
               if (var155 != null) {
                  try {
                     var154.close();
                  } catch (Throwable var133) {
                     var155.addSuppressed(var133);
                  }
               } else {
                  var154.close();
               }
            }

         }
      } catch (IOException var142) {
         LOGGER.error("Failed to rename unpacked realms level {}", var5, var142);
      }

      this.resourcePackPath = new File(var150, var5 + File.separator + "resources.zip");
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

   class DownloadCountingOutputStream extends CountingOutputStream {
      private ActionListener listener;

      public DownloadCountingOutputStream(OutputStream var2) {
         super(var2);
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

   class ResourcePackProgressListener implements ActionListener {
      private final File tempFile;
      private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
      private final WorldDownload worldDownload;

      private ResourcePackProgressListener(File var2, RealmsDownloadLatestWorldScreen.DownloadStatus var3, WorldDownload var4) {
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

      // $FF: synthetic method
      ResourcePackProgressListener(File var2, RealmsDownloadLatestWorldScreen.DownloadStatus var3, WorldDownload var4, Object var5) {
         this(var2, var3, var4);
      }
   }

   class ProgressListener implements ActionListener {
      private final String worldName;
      private final File tempFile;
      private final LevelStorageSource levelStorageSource;
      private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;

      private ProgressListener(String var2, File var3, LevelStorageSource var4, RealmsDownloadLatestWorldScreen.DownloadStatus var5) {
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

      // $FF: synthetic method
      ProgressListener(String var2, File var3, LevelStorageSource var4, RealmsDownloadLatestWorldScreen.DownloadStatus var5, Object var6) {
         this(var2, var3, var4, var5);
      }
   }
}
