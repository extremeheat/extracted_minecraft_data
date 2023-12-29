package net.minecraft.util;

import com.google.common.hash.Funnels;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Map;
import java.util.OptionalLong;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class HttpUtil {
   private static final Logger LOGGER = LogUtils.getLogger();

   private HttpUtil() {
      super();
   }

   public static Path downloadFile(
      Path var0, URL var1, Map<String, String> var2, HashFunction var3, @Nullable HashCode var4, int var5, Proxy var6, HttpUtil.DownloadProgressListener var7
   ) {
      HttpURLConnection var8 = null;
      InputStream var9 = null;
      var7.requestStart();
      Path var10;
      if (var4 != null) {
         var10 = cachedFilePath(var0, var4);

         try {
            if (checkExistingFile(var10, var3, var4)) {
               LOGGER.info("Returning cached file since actual hash matches requested");
               var7.requestFinished(true);
               updateModificationTime(var10);
               return var10;
            }
         } catch (IOException var35) {
            LOGGER.warn("Failed to check cached file {}", var10, var35);
         }

         try {
            LOGGER.warn("Existing file {} not found or had mismatched hash", var10);
            Files.deleteIfExists(var10);
         } catch (IOException var34) {
            var7.requestFinished(false);
            throw new UncheckedIOException("Failed to remove existing file " + var10, var34);
         }
      } else {
         var10 = null;
      }

      Path var15;
      try {
         var8 = (HttpURLConnection)var1.openConnection(var6);
         var8.setInstanceFollowRedirects(true);
         var2.forEach(var8::setRequestProperty);
         var9 = var8.getInputStream();
         long var11 = var8.getContentLengthLong();
         OptionalLong var13 = var11 != -1L ? OptionalLong.of(var11) : OptionalLong.empty();
         FileUtil.createDirectoriesSafe(var0);
         var7.downloadStart(var13);
         if (var13.isPresent() && var13.getAsLong() > (long)var5) {
            throw new IOException("Filesize is bigger than maximum allowed (file is " + var13 + ", limit is " + var5 + ")");
         }

         if (var10 == null) {
            Path var38 = Files.createTempFile(var0, "download", ".tmp");

            try {
               HashCode var39 = downloadAndHash(var3, var5, var7, var9, var38);
               Path var16 = cachedFilePath(var0, var39);
               if (!checkExistingFile(var16, var3, var39)) {
                  Files.move(var38, var16, StandardCopyOption.REPLACE_EXISTING);
               } else {
                  updateModificationTime(var16);
               }

               var7.requestFinished(true);
               return var16;
            } finally {
               Files.deleteIfExists(var38);
            }
         }

         HashCode var14 = downloadAndHash(var3, var5, var7, var9, var10);
         if (!var14.equals(var4)) {
            throw new IOException("Hash of downloaded file (" + var14 + ") did not match requested (" + var4 + ")");
         }

         var7.requestFinished(true);
         var15 = var10;
      } catch (Throwable var36) {
         if (var8 != null) {
            InputStream var12 = var8.getErrorStream();
            if (var12 != null) {
               try {
                  LOGGER.error("HTTP response error: {}", IOUtils.toString(var12, StandardCharsets.UTF_8));
               } catch (Exception var32) {
                  LOGGER.error("Failed to read response from server");
               }
            }
         }

         var7.requestFinished(false);
         throw new IllegalStateException("Failed to download file " + var1, var36);
      } finally {
         IOUtils.closeQuietly(var9);
      }

      return var15;
   }

   private static void updateModificationTime(Path var0) {
      try {
         Files.setLastModifiedTime(var0, FileTime.from(Instant.now()));
      } catch (IOException var2) {
         LOGGER.warn("Failed to update modification time of {}", var0, var2);
      }
   }

   private static HashCode hashFile(Path var0, HashFunction var1) throws IOException {
      Hasher var2 = var1.newHasher();

      try (
         OutputStream var3 = Funnels.asOutputStream(var2);
         InputStream var4 = Files.newInputStream(var0);
      ) {
         var4.transferTo(var3);
      }

      return var2.hash();
   }

   private static boolean checkExistingFile(Path var0, HashFunction var1, HashCode var2) throws IOException {
      if (Files.exists(var0)) {
         HashCode var3 = hashFile(var0, var1);
         if (var3.equals(var2)) {
            return true;
         }

         LOGGER.warn("Mismatched hash of file {}, expected {} but found {}", new Object[]{var0, var2, var3});
      }

      return false;
   }

   private static Path cachedFilePath(Path var0, HashCode var1) {
      return var0.resolve(var1.toString());
   }

   private static HashCode downloadAndHash(HashFunction var0, int var1, HttpUtil.DownloadProgressListener var2, InputStream var3, Path var4) throws IOException {
      HashCode var11;
      try (OutputStream var5 = Files.newOutputStream(var4, StandardOpenOption.CREATE)) {
         Hasher var6 = var0.newHasher();
         byte[] var7 = new byte[8196];
         long var9 = 0L;

         int var8;
         while((var8 = var3.read(var7)) >= 0) {
            var9 += (long)var8;
            var2.downloadedBytes(var9);
            if (var9 > (long)var1) {
               throw new IOException("Filesize was bigger than maximum allowed (got >= " + var9 + ", limit was " + var1 + ")");
            }

            if (Thread.interrupted()) {
               LOGGER.error("INTERRUPTED");
               throw new IOException("Download interrupted");
            }

            var5.write(var7, 0, var8);
            var6.putBytes(var7, 0, var8);
         }

         var11 = var6.hash();
      }

      return var11;
   }

   public static int getAvailablePort() {
      try {
         int var1;
         try (ServerSocket var0 = new ServerSocket(0)) {
            var1 = var0.getLocalPort();
         }

         return var1;
      } catch (IOException var5) {
         return 25564;
      }
   }

   public static boolean isPortAvailable(int var0) {
      if (var0 >= 0 && var0 <= 65535) {
         try {
            boolean var2;
            try (ServerSocket var1 = new ServerSocket(var0)) {
               var2 = var1.getLocalPort() == var0;
            }

            return var2;
         } catch (IOException var6) {
            return false;
         }
      } else {
         return false;
      }
   }

   public interface DownloadProgressListener {
      void requestStart();

      void downloadStart(OptionalLong var1);

      void downloadedBytes(long var1);

      void requestFinished(boolean var1);
   }
}
