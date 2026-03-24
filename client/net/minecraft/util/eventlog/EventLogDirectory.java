package net.minecraft.util.eventlog;

import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class EventLogDirectory {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int COMPRESS_BUFFER_SIZE = 4096;
   private static final String COMPRESSED_EXTENSION = ".gz";
   private final Path root;
   private final String extension;

   private EventLogDirectory(final Path root, final String extension) {
      super();
      this.root = root;
      this.extension = extension;
   }

   public static EventLogDirectory open(final Path root, final String extension) throws IOException {
      Files.createDirectories(root);
      return new EventLogDirectory(root, extension);
   }

   public FileList listFiles() throws IOException {
      Stream<Path> list = Files.list(this.root);

      FileList var2;
      try {
         var2 = new FileList(list.filter((x$0) -> Files.isRegularFile(x$0, new LinkOption[0])).map(this::parseFile).filter(Objects::nonNull).toList());
      } catch (Throwable var5) {
         if (list != null) {
            try {
               list.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (list != null) {
         list.close();
      }

      return var2;
   }

   private @Nullable File parseFile(final Path path) {
      String fileName = path.getFileName().toString();
      int extensionIndex = fileName.indexOf(46);
      if (extensionIndex == -1) {
         return null;
      } else {
         FileId id = EventLogDirectory.FileId.parse(fileName.substring(0, extensionIndex));
         if (id != null) {
            String extension = fileName.substring(extensionIndex);
            if (extension.equals(this.extension)) {
               return new RawFile(path, id);
            }

            if (extension.equals(this.extension + ".gz")) {
               return new CompressedFile(path, id);
            }
         }

         return null;
      }
   }

   private static void tryCompress(final Path raw, final Path compressed) throws IOException {
      if (Files.exists(compressed, new LinkOption[0])) {
         throw new IOException("Compressed target file already exists: " + String.valueOf(compressed));
      } else {
         FileChannel channel = FileChannel.open(raw, StandardOpenOption.WRITE, StandardOpenOption.READ);

         try {
            FileLock lock = channel.tryLock();
            if (lock == null) {
               throw new IOException("Raw log file is already locked, cannot compress: " + String.valueOf(raw));
            }

            writeCompressed(channel, compressed);
            channel.truncate(0L);
         } catch (Throwable var6) {
            if (channel != null) {
               try {
                  channel.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (channel != null) {
            channel.close();
         }

         Files.delete(raw);
      }
   }

   private static void writeCompressed(final ReadableByteChannel channel, final Path target) throws IOException {
      OutputStream output = new GZIPOutputStream(Files.newOutputStream(target));

      try {
         byte[] bytes = new byte[4096];
         ByteBuffer buffer = ByteBuffer.wrap(bytes);

         while(channel.read(buffer) >= 0) {
            buffer.flip();
            output.write(bytes, 0, buffer.limit());
            buffer.clear();
         }
      } catch (Throwable var6) {
         try {
            output.close();
         } catch (Throwable var5) {
            var6.addSuppressed(var5);
         }

         throw var6;
      }

      output.close();
   }

   public RawFile createNewFile(final LocalDate date) throws IOException {
      int index = 1;
      Set<FileId> files = this.listFiles().ids();

      FileId id;
      do {
         id = new FileId(date, index++);
      } while(files.contains(id));

      RawFile file = new RawFile(this.root.resolve(id.toFileName(this.extension)), id);
      Files.createFile(file.path());
      return file;
   }

   public static class FileList implements Iterable<File> {
      private final List<File> files;

      private FileList(final List<File> files) {
         super();
         this.files = new ArrayList(files);
      }

      public FileList prune(final LocalDate date, final int expiryDays) {
         this.files.removeIf((file) -> {
            FileId id = file.id();
            LocalDate expiresAt = id.date().plusDays((long)expiryDays);
            if (!date.isBefore(expiresAt)) {
               try {
                  Files.delete(file.path());
                  return true;
               } catch (IOException e) {
                  EventLogDirectory.LOGGER.warn("Failed to delete expired event log file: {}", file.path(), e);
               }
            }

            return false;
         });
         return this;
      }

      public FileList compressAll() {
         ListIterator<File> iterator = this.files.listIterator();

         while(iterator.hasNext()) {
            File file = (File)iterator.next();

            try {
               iterator.set(file.compress());
            } catch (IOException e) {
               EventLogDirectory.LOGGER.warn("Failed to compress event log file: {}", file.path(), e);
            }
         }

         return this;
      }

      public Iterator<File> iterator() {
         return this.files.iterator();
      }

      public Stream<File> stream() {
         return this.files.stream();
      }

      public Set<FileId> ids() {
         return (Set)this.files.stream().map(File::id).collect(Collectors.toSet());
      }
   }

   public static record RawFile(Path path, FileId id) implements File {
      public RawFile {
         super();
      }

      public FileChannel openChannel() throws IOException {
         return FileChannel.open(this.path, StandardOpenOption.WRITE, StandardOpenOption.READ);
      }

      public @Nullable Reader openReader() throws IOException {
         return Files.exists(this.path, new LinkOption[0]) ? Files.newBufferedReader(this.path) : null;
      }

      public CompressedFile compress() throws IOException {
         Path compressedPath = this.path.resolveSibling(this.path.getFileName().toString() + ".gz");
         EventLogDirectory.tryCompress(this.path, compressedPath);
         return new CompressedFile(compressedPath, this.id);
      }
   }

   public static record CompressedFile(Path path, FileId id) implements File {
      public CompressedFile {
         super();
      }

      public @Nullable Reader openReader() throws IOException {
         return !Files.exists(this.path, new LinkOption[0]) ? null : new BufferedReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(this.path)), StandardCharsets.UTF_8));
      }

      public CompressedFile compress() {
         return this;
      }
   }

   public static record FileId(LocalDate date, int index) {
      private static final DateTimeFormatter DATE_FORMATTER;

      public FileId {
         super();
      }

      public static @Nullable FileId parse(final String name) {
         int separator = name.indexOf("-");
         if (separator == -1) {
            return null;
         } else {
            String date = name.substring(0, separator);
            String index = name.substring(separator + 1);

            try {
               return new FileId(LocalDate.parse(date, DATE_FORMATTER), Integer.parseInt(index));
            } catch (DateTimeParseException | NumberFormatException var5) {
               return null;
            }
         }
      }

      public String toString() {
         String var10000 = DATE_FORMATTER.format(this.date);
         return var10000 + "-" + this.index;
      }

      public String toFileName(final String extension) {
         String var10000 = String.valueOf(this);
         return var10000 + extension;
      }

      static {
         DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
      }
   }

   public interface File {
      Path path();

      FileId id();

      @Nullable Reader openReader() throws IOException;

      CompressedFile compress() throws IOException;
   }
}
