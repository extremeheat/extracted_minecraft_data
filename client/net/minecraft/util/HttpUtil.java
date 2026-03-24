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
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;
import org.apache.commons.io.IOUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class HttpUtil {
   private static final Logger LOGGER = LogUtils.getLogger();

   private HttpUtil() {
      super();
   }

   public static Path downloadFile(final Path targetDir, final URL url, final Map<String, String> headers, final HashFunction hashFunction, final @Nullable HashCode requestedHash, final int maxSize, final Proxy proxy, final DownloadProgressListener listener) {
      HttpURLConnection connection = null;
      InputStream input = null;
      listener.requestStart();
      Path targetFile;
      if (requestedHash != null) {
         targetFile = cachedFilePath(targetDir, requestedHash);

         try {
            if (checkExistingFile(targetFile, hashFunction, requestedHash)) {
               LOGGER.info("Returning cached file since actual hash matches requested");
               listener.requestFinished(true);
               updateModificationTime(targetFile);
               return targetFile;
            }
         } catch (IOException e) {
            LOGGER.warn("Failed to check cached file {}", targetFile, e);
         }

         try {
            LOGGER.warn("Existing file {} not found or had mismatched hash", targetFile);
            Files.deleteIfExists(targetFile);
         } catch (IOException e) {
            listener.requestFinished(false);
            throw new UncheckedIOException("Failed to remove existing file " + String.valueOf(targetFile), e);
         }
      } else {
         targetFile = null;
      }

      Path var15;
      try {
         connection = (HttpURLConnection)url.openConnection(proxy);
         connection.setInstanceFollowRedirects(true);
         Objects.requireNonNull(connection);
         headers.forEach(connection::setRequestProperty);
         input = connection.getInputStream();
         long contentLength = connection.getContentLengthLong();
         OptionalLong size = contentLength != -1L ? OptionalLong.of(contentLength) : OptionalLong.empty();
         FileUtil.createDirectoriesSafe(targetDir);
         listener.downloadStart(size);
         if (size.isPresent() && size.getAsLong() > (long)maxSize) {
            String var41 = String.valueOf(size);
            throw new IOException("Filesize is bigger than maximum allowed (file is " + var41 + ", limit is " + maxSize + ")");
         }

         if (targetFile == null) {
            Path tmpPath = Files.createTempFile(targetDir, "download", ".tmp");

            try {
               HashCode actualHash = downloadAndHash(hashFunction, maxSize, listener, input, tmpPath);
               Path actualPath = cachedFilePath(targetDir, actualHash);
               if (!checkExistingFile(actualPath, hashFunction, actualHash)) {
                  Files.move(tmpPath, actualPath, StandardCopyOption.REPLACE_EXISTING);
               } else {
                  updateModificationTime(actualPath);
               }

               listener.requestFinished(true);
               Path var17 = actualPath;
               return var17;
            } finally {
               Files.deleteIfExists(tmpPath);
            }
         }

         HashCode actualHash = downloadAndHash(hashFunction, maxSize, listener, input, targetFile);
         if (!actualHash.equals(requestedHash)) {
            String var10002 = String.valueOf(actualHash);
            throw new IOException("Hash of downloaded file (" + var10002 + ") did not match requested (" + String.valueOf(requestedHash) + ")");
         }

         listener.requestFinished(true);
         var15 = targetFile;
      } catch (Throwable t) {
         if (connection != null) {
            InputStream error = connection.getErrorStream();
            if (error != null) {
               try {
                  LOGGER.error("HTTP response error: {}", IOUtils.toString(error, StandardCharsets.UTF_8));
               } catch (Exception var32) {
                  LOGGER.error("Failed to read response from server");
               }
            }
         }

         listener.requestFinished(false);
         throw new IllegalStateException("Failed to download file " + String.valueOf(url), t);
      } finally {
         IOUtils.closeQuietly(input);
      }

      return var15;
   }

   private static void updateModificationTime(final Path targetFile) {
      try {
         Files.setLastModifiedTime(targetFile, FileTime.from(Instant.now()));
      } catch (IOException e) {
         LOGGER.warn("Failed to update modification time of {}", targetFile, e);
      }

   }

   private static HashCode hashFile(final Path file, final HashFunction hashFunction) throws IOException {
      Hasher hasher = hashFunction.newHasher();
      OutputStream outputStream = Funnels.asOutputStream(hasher);

      try {
         InputStream fileInput = Files.newInputStream(file);

         try {
            fileInput.transferTo(outputStream);
         } catch (Throwable var9) {
            if (fileInput != null) {
               try {
                  fileInput.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (fileInput != null) {
            fileInput.close();
         }
      } catch (Throwable var10) {
         if (outputStream != null) {
            try {
               outputStream.close();
            } catch (Throwable var7) {
               var10.addSuppressed(var7);
            }
         }

         throw var10;
      }

      if (outputStream != null) {
         outputStream.close();
      }

      return hasher.hash();
   }

   private static boolean checkExistingFile(final Path file, final HashFunction hashFunction, final HashCode expectedHash) throws IOException {
      if (Files.exists(file, new LinkOption[0])) {
         HashCode actualHash = hashFile(file, hashFunction);
         if (actualHash.equals(expectedHash)) {
            return true;
         }

         LOGGER.warn("Mismatched hash of file {}, expected {} but found {}", new Object[]{file, expectedHash, actualHash});
      }

      return false;
   }

   private static Path cachedFilePath(final Path targetDir, final HashCode requestedHash) {
      return targetDir.resolve(requestedHash.toString());
   }

   private static HashCode downloadAndHash(final HashFunction hashFunction, final int maxSize, final DownloadProgressListener listener, final InputStream input, final Path downloadFile) throws IOException {
      OutputStream output = Files.newOutputStream(downloadFile, StandardOpenOption.CREATE);

      HashCode var11;
      try {
         Hasher hasher = hashFunction.newHasher();
         byte[] buffer = new byte[8196];
         long readSoFar = 0L;

         int read;
         while((read = input.read(buffer)) >= 0) {
            readSoFar += (long)read;
            listener.downloadedBytes(readSoFar);
            if (readSoFar > (long)maxSize) {
               throw new IOException("Filesize was bigger than maximum allowed (got >= " + readSoFar + ", limit was " + maxSize + ")");
            }

            if (Thread.interrupted()) {
               LOGGER.error("INTERRUPTED");
               throw new IOException("Download interrupted");
            }

            output.write(buffer, 0, read);
            hasher.putBytes(buffer, 0, read);
         }

         var11 = hasher.hash();
      } catch (Throwable var13) {
         if (output != null) {
            try {
               output.close();
            } catch (Throwable var12) {
               var13.addSuppressed(var12);
            }
         }

         throw var13;
      }

      if (output != null) {
         output.close();
      }

      return var11;
   }

   public static int getAvailablePort() {
      try {
         ServerSocket server = new ServerSocket(0);

         int var1;
         try {
            var1 = server.getLocalPort();
         } catch (Throwable var4) {
            try {
               server.close();
            } catch (Throwable var3) {
               var4.addSuppressed(var3);
            }

            throw var4;
         }

         server.close();
         return var1;
      } catch (IOException var5) {
         return 25564;
      }
   }

   public static boolean isPortAvailable(final int port) {
      if (port >= 0 && port <= 65535) {
         try {
            ServerSocket server = new ServerSocket(port);

            boolean var2;
            try {
               var2 = server.getLocalPort() == port;
            } catch (Throwable var5) {
               try {
                  server.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }

               throw var5;
            }

            server.close();
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

      void downloadStart(OptionalLong sizeBytes);

      void downloadedBytes(long bytesSoFar);

      void requestFinished(boolean success);
   }
}
