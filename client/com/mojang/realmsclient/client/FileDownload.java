package com.mojang.realmsclient.client;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsAnvilLevelStorageSource;
import net.minecraft.realms.RealmsLevelSummary;
import net.minecraft.realms.RealmsSharedConstants;
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

   public void download(final WorldDownload var1, final String var2, final RealmsDownloadLatestWorldScreen.DownloadStatus var3, final RealmsAnvilLevelStorageSource var4) {
      if (this.currentThread == null) {
         this.currentThread = new Thread() {
            public void run() {
               CloseableHttpClient var1x = null;
               boolean var86 = false;

               CloseableHttpResponse var2x;
               FileOutputStream var3x;
               FileDownload.DownloadCountingOutputStream var5;
               FileDownload.ResourcePackProgressListener var102;
               label1377: {
                  label1398: {
                     try {
                        var86 = true;
                        FileDownload.this.tempFile = File.createTempFile("backup", ".tar.gz");
                        FileDownload.this.request = new HttpGet(var1.downloadLink);
                        var1x = HttpClientBuilder.create().setDefaultRequestConfig(FileDownload.this.requestConfig).build();
                        var2x = var1x.execute(FileDownload.this.request);
                        var3.totalBytes = Long.parseLong(var2x.getFirstHeader("Content-Length").getValue());
                        if (var2x.getStatusLine().getStatusCode() == 200) {
                           var3x = new FileOutputStream(FileDownload.this.tempFile);
                           FileDownload.ProgressListener var4x = FileDownload.this.new ProgressListener(var2.trim(), FileDownload.this.tempFile, var4, var3, var1);
                           var5 = FileDownload.this.new DownloadCountingOutputStream(var3x);
                           var5.setListener(var4x);
                           IOUtils.copy(var2x.getEntity().getContent(), var5);
                           var86 = false;
                           break label1377;
                        }

                        FileDownload.this.error = true;
                        FileDownload.this.request.abort();
                        var86 = false;
                        break label1398;
                     } catch (Exception var99) {
                        FileDownload.LOGGER.error("Caught exception while downloading: " + var99.getMessage());
                        FileDownload.this.error = true;
                        var86 = false;
                     } finally {
                        if (var86) {
                           FileDownload.this.request.releaseConnection();
                           if (FileDownload.this.tempFile != null) {
                              FileDownload.this.tempFile.delete();
                           }

                           if (!FileDownload.this.error) {
                              if (!var1.resourcePackUrl.isEmpty() && !var1.resourcePackHash.isEmpty()) {
                                 try {
                                    FileDownload.this.tempFile = File.createTempFile("resources", ".tar.gz");
                                    FileDownload.this.request = new HttpGet(var1.resourcePackUrl);
                                    CloseableHttpResponse var11 = var1x.execute(FileDownload.this.request);
                                    var3.totalBytes = Long.parseLong(var11.getFirstHeader("Content-Length").getValue());
                                    if (var11.getStatusLine().getStatusCode() != 200) {
                                       FileDownload.this.error = true;
                                       FileDownload.this.request.abort();
                                       return;
                                    }

                                    FileOutputStream var12 = new FileOutputStream(FileDownload.this.tempFile);
                                    FileDownload.ResourcePackProgressListener var13 = FileDownload.this.new ResourcePackProgressListener(FileDownload.this.tempFile, var3, var1);
                                    FileDownload.DownloadCountingOutputStream var14 = FileDownload.this.new DownloadCountingOutputStream(var12);
                                    var14.setListener(var13);
                                    IOUtils.copy(var11.getEntity().getContent(), var14);
                                 } catch (Exception var91) {
                                    FileDownload.LOGGER.error("Caught exception while downloading: " + var91.getMessage());
                                    FileDownload.this.error = true;
                                 } finally {
                                    FileDownload.this.request.releaseConnection();
                                    if (FileDownload.this.tempFile != null) {
                                       FileDownload.this.tempFile.delete();
                                    }

                                 }
                              } else {
                                 FileDownload.this.finished = true;
                              }
                           }

                           if (var1x != null) {
                              try {
                                 var1x.close();
                              } catch (IOException var87) {
                                 FileDownload.LOGGER.error("Failed to close Realms download client");
                              }
                           }

                        }
                     }

                     FileDownload.this.request.releaseConnection();
                     if (FileDownload.this.tempFile != null) {
                        FileDownload.this.tempFile.delete();
                     }

                     if (!FileDownload.this.error) {
                        if (!var1.resourcePackUrl.isEmpty() && !var1.resourcePackHash.isEmpty()) {
                           try {
                              FileDownload.this.tempFile = File.createTempFile("resources", ".tar.gz");
                              FileDownload.this.request = new HttpGet(var1.resourcePackUrl);
                              var2x = var1x.execute(FileDownload.this.request);
                              var3.totalBytes = Long.parseLong(var2x.getFirstHeader("Content-Length").getValue());
                              if (var2x.getStatusLine().getStatusCode() != 200) {
                                 FileDownload.this.error = true;
                                 FileDownload.this.request.abort();
                                 return;
                              }

                              var3x = new FileOutputStream(FileDownload.this.tempFile);
                              var102 = FileDownload.this.new ResourcePackProgressListener(FileDownload.this.tempFile, var3, var1);
                              var5 = FileDownload.this.new DownloadCountingOutputStream(var3x);
                              var5.setListener(var102);
                              IOUtils.copy(var2x.getEntity().getContent(), var5);
                           } catch (Exception var95) {
                              FileDownload.LOGGER.error("Caught exception while downloading: " + var95.getMessage());
                              FileDownload.this.error = true;
                           } finally {
                              FileDownload.this.request.releaseConnection();
                              if (FileDownload.this.tempFile != null) {
                                 FileDownload.this.tempFile.delete();
                              }

                           }
                        } else {
                           FileDownload.this.finished = true;
                        }
                     }

                     if (var1x != null) {
                        try {
                           var1x.close();
                        } catch (IOException var89) {
                           FileDownload.LOGGER.error("Failed to close Realms download client");
                        }

                        return;
                     }

                     return;
                  }

                  FileDownload.this.request.releaseConnection();
                  if (FileDownload.this.tempFile != null) {
                     FileDownload.this.tempFile.delete();
                  }

                  if (!FileDownload.this.error) {
                     if (!var1.resourcePackUrl.isEmpty() && !var1.resourcePackHash.isEmpty()) {
                        label1317: {
                           try {
                              FileDownload.this.tempFile = File.createTempFile("resources", ".tar.gz");
                              FileDownload.this.request = new HttpGet(var1.resourcePackUrl);
                              CloseableHttpResponse var101 = var1x.execute(FileDownload.this.request);
                              var3.totalBytes = Long.parseLong(var101.getFirstHeader("Content-Length").getValue());
                              if (var101.getStatusLine().getStatusCode() == 200) {
                                 FileOutputStream var103 = new FileOutputStream(FileDownload.this.tempFile);
                                 FileDownload.ResourcePackProgressListener var104 = FileDownload.this.new ResourcePackProgressListener(FileDownload.this.tempFile, var3, var1);
                                 FileDownload.DownloadCountingOutputStream var6 = FileDownload.this.new DownloadCountingOutputStream(var103);
                                 var6.setListener(var104);
                                 IOUtils.copy(var101.getEntity().getContent(), var6);
                                 break label1317;
                              }

                              FileDownload.this.error = true;
                              FileDownload.this.request.abort();
                           } catch (Exception var93) {
                              FileDownload.LOGGER.error("Caught exception while downloading: " + var93.getMessage());
                              FileDownload.this.error = true;
                              break label1317;
                           } finally {
                              FileDownload.this.request.releaseConnection();
                              if (FileDownload.this.tempFile != null) {
                                 FileDownload.this.tempFile.delete();
                              }

                           }

                           return;
                        }
                     } else {
                        FileDownload.this.finished = true;
                     }
                  }

                  if (var1x != null) {
                     try {
                        var1x.close();
                     } catch (IOException var88) {
                        FileDownload.LOGGER.error("Failed to close Realms download client");
                     }
                  }

                  return;
               }

               FileDownload.this.request.releaseConnection();
               if (FileDownload.this.tempFile != null) {
                  FileDownload.this.tempFile.delete();
               }

               if (!FileDownload.this.error) {
                  if (!var1.resourcePackUrl.isEmpty() && !var1.resourcePackHash.isEmpty()) {
                     try {
                        FileDownload.this.tempFile = File.createTempFile("resources", ".tar.gz");
                        FileDownload.this.request = new HttpGet(var1.resourcePackUrl);
                        var2x = var1x.execute(FileDownload.this.request);
                        var3.totalBytes = Long.parseLong(var2x.getFirstHeader("Content-Length").getValue());
                        if (var2x.getStatusLine().getStatusCode() != 200) {
                           FileDownload.this.error = true;
                           FileDownload.this.request.abort();
                           return;
                        }

                        var3x = new FileOutputStream(FileDownload.this.tempFile);
                        var102 = FileDownload.this.new ResourcePackProgressListener(FileDownload.this.tempFile, var3, var1);
                        var5 = FileDownload.this.new DownloadCountingOutputStream(var3x);
                        var5.setListener(var102);
                        IOUtils.copy(var2x.getEntity().getContent(), var5);
                     } catch (Exception var97) {
                        FileDownload.LOGGER.error("Caught exception while downloading: " + var97.getMessage());
                        FileDownload.this.error = true;
                     } finally {
                        FileDownload.this.request.releaseConnection();
                        if (FileDownload.this.tempFile != null) {
                           FileDownload.this.tempFile.delete();
                        }

                     }
                  } else {
                     FileDownload.this.finished = true;
                  }
               }

               if (var1x != null) {
                  try {
                     var1x.close();
                  } catch (IOException var90) {
                     FileDownload.LOGGER.error("Failed to close Realms download client");
                  }
               }

            }
         };
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

   private void untarGzipArchive(String var1, File var2, RealmsAnvilLevelStorageSource var3) throws IOException {
      Pattern var4 = Pattern.compile(".*-([0-9]+)$");
      int var6 = 1;
      char[] var7 = RealmsSharedConstants.ILLEGAL_FILE_CHARACTERS;
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
         Iterator var24 = var3.getLevelList().iterator();

         while(var24.hasNext()) {
            RealmsLevelSummary var26 = (RealmsLevelSummary)var24.next();
            if (var26.getLevelId().toLowerCase(Locale.ROOT).startsWith(var1.toLowerCase(Locale.ROOT))) {
               Matcher var29 = var4.matcher(var26.getLevelId());
               if (var29.matches()) {
                  if (Integer.valueOf(var29.group(1)) > var6) {
                     var6 = Integer.valueOf(var29.group(1));
                  }
               } else {
                  ++var6;
               }
            }
         }
      } catch (Exception var23) {
         LOGGER.error("Error getting level list", var23);
         this.error = true;
         return;
      }

      String var5;
      if (var3.isNewLevelIdAcceptable(var1) && var6 <= 1) {
         var5 = var1;
      } else {
         var5 = var1 + (var6 == 1 ? "" : "-" + var6);
         if (!var3.isNewLevelIdAcceptable(var5)) {
            boolean var25 = false;

            while(!var25) {
               ++var6;
               var5 = var1 + (var6 == 1 ? "" : "-" + var6);
               if (var3.isNewLevelIdAcceptable(var5)) {
                  var25 = true;
               }
            }
         }
      }

      TarArchiveInputStream var27 = null;
      File var28 = new File(Realms.getGameDirectoryPath(), "saves");
      boolean var20 = false;

      File var31;
      label301: {
         try {
            var20 = true;
            var28.mkdir();
            var27 = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(var2))));

            for(TarArchiveEntry var30 = var27.getNextTarEntry(); var30 != null; var30 = var27.getNextTarEntry()) {
               var31 = new File(var28, var30.getName().replace("world", var5));
               if (var30.isDirectory()) {
                  var31.mkdirs();
               } else {
                  var31.createNewFile();
                  byte[] var11 = new byte[1024];
                  BufferedOutputStream var12 = new BufferedOutputStream(new FileOutputStream(var31));
                  boolean var13 = false;

                  int var33;
                  while((var33 = var27.read(var11)) != -1) {
                     var12.write(var11, 0, var33);
                  }

                  var12.close();
                  Object var32 = null;
               }
            }

            var20 = false;
            break label301;
         } catch (Exception var21) {
            LOGGER.error("Error extracting world", var21);
            this.error = true;
            var20 = false;
         } finally {
            if (var20) {
               if (var27 != null) {
                  var27.close();
               }

               if (var2 != null) {
                  var2.delete();
               }

               var3.renameLevel(var5, var5.trim());
               File var16 = new File(var28, var5 + File.separator + "level.dat");
               Realms.deletePlayerTag(var16);
               this.resourcePackPath = new File(var28, var5 + File.separator + "resources.zip");
            }
         }

         if (var27 != null) {
            var27.close();
         }

         if (var2 != null) {
            var2.delete();
         }

         var3.renameLevel(var5, var5.trim());
         var31 = new File(var28, var5 + File.separator + "level.dat");
         Realms.deletePlayerTag(var31);
         this.resourcePackPath = new File(var28, var5 + File.separator + "resources.zip");
         return;
      }

      if (var27 != null) {
         var27.close();
      }

      if (var2 != null) {
         var2.delete();
      }

      var3.renameLevel(var5, var5.trim());
      var31 = new File(var28, var5 + File.separator + "level.dat");
      Realms.deletePlayerTag(var31);
      this.resourcePackPath = new File(var28, var5 + File.separator + "resources.zip");
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
                  FileDownload.LOGGER.error("Resourcepack had wrong hash (expected " + this.worldDownload.resourcePackHash + ", found " + var2 + "). Deleting it.");
                  FileUtils.deleteQuietly(this.tempFile);
                  FileDownload.this.error = true;
               }
            } catch (IOException var3) {
               FileDownload.LOGGER.error("Error copying resourcepack file", var3.getMessage());
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
      private final RealmsAnvilLevelStorageSource levelStorageSource;
      private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
      private final WorldDownload worldDownload;

      private ProgressListener(String var2, File var3, RealmsAnvilLevelStorageSource var4, RealmsDownloadLatestWorldScreen.DownloadStatus var5, WorldDownload var6) {
         super();
         this.worldName = var2;
         this.tempFile = var3;
         this.levelStorageSource = var4;
         this.downloadStatus = var5;
         this.worldDownload = var6;
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
      ProgressListener(String var2, File var3, RealmsAnvilLevelStorageSource var4, RealmsDownloadLatestWorldScreen.DownloadStatus var5, WorldDownload var6, Object var7) {
         this(var2, var3, var4, var5, var6);
      }
   }
}
