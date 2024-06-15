package net.minecraft.util.eventlog;

import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
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
import javax.annotation.Nullable;
import org.slf4j.Logger;

public class EventLogDirectory {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int COMPRESS_BUFFER_SIZE = 4096;
   private static final String COMPRESSED_EXTENSION = ".gz";
   private final Path root;
   private final String extension;

   private EventLogDirectory(Path var1, String var2) {
      super();
      this.root = var1;
      this.extension = var2;
   }

   public static EventLogDirectory open(Path var0, String var1) throws IOException {
      Files.createDirectories(var0);
      return new EventLogDirectory(var0, var1);
   }

   public EventLogDirectory.FileList listFiles() throws IOException {
      EventLogDirectory.FileList var2;
      try (Stream var1 = Files.list(this.root)) {
         var2 = new EventLogDirectory.FileList(var1.filter(var0 -> Files.isRegularFile(var0)).map(this::parseFile).filter(Objects::nonNull).toList());
      }

      return var2;
   }

   @Nullable
   private EventLogDirectory.File parseFile(Path var1) {
      String var2 = var1.getFileName().toString();
      int var3 = var2.indexOf(46);
      if (var3 == -1) {
         return null;
      } else {
         EventLogDirectory.FileId var4 = EventLogDirectory.FileId.parse(var2.substring(0, var3));
         if (var4 != null) {
            String var5 = var2.substring(var3);
            if (var5.equals(this.extension)) {
               return new EventLogDirectory.RawFile(var1, var4);
            }

            if (var5.equals(this.extension + ".gz")) {
               return new EventLogDirectory.CompressedFile(var1, var4);
            }
         }

         return null;
      }
   }

   static void tryCompress(Path var0, Path var1) throws IOException {
      if (Files.exists(var1)) {
         throw new IOException("Compressed target file already exists: " + var1);
      } else {
         try (FileChannel var2 = FileChannel.open(var0, StandardOpenOption.WRITE, StandardOpenOption.READ)) {
            FileLock var3 = var2.tryLock();
            if (var3 == null) {
               throw new IOException("Raw log file is already locked, cannot compress: " + var0);
            }

            writeCompressed(var2, var1);
            var2.truncate(0L);
         }

         Files.delete(var0);
      }
   }

   private static void writeCompressed(ReadableByteChannel var0, Path var1) throws IOException {
      try (GZIPOutputStream var2 = new GZIPOutputStream(Files.newOutputStream(var1))) {
         byte[] var3 = new byte[4096];
         ByteBuffer var4 = ByteBuffer.wrap(var3);

         while (var0.read(var4) >= 0) {
            var4.flip();
            var2.write(var3, 0, var4.limit());
            var4.clear();
         }
      }
   }

   public EventLogDirectory.RawFile createNewFile(LocalDate var1) throws IOException {
      int var2 = 1;
      Set var4 = this.listFiles().ids();

      EventLogDirectory.FileId var3;
      do {
         var3 = new EventLogDirectory.FileId(var1, var2++);
      } while (var4.contains(var3));

      EventLogDirectory.RawFile var5 = new EventLogDirectory.RawFile(this.root.resolve(var3.toFileName(this.extension)), var3);
      Files.createFile(var5.path());
      return var5;
   }

   public static record CompressedFile(Path path, EventLogDirectory.FileId id) implements EventLogDirectory.File {
      public CompressedFile(Path path, EventLogDirectory.FileId id) {
         super();
         this.path = path;
         this.id = id;
      }

      @Nullable
      @Override
      public Reader openReader() throws IOException {
         return !Files.exists(this.path) ? null : new BufferedReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(this.path))));
      }

      @Override
      public EventLogDirectory.CompressedFile compress() {
         return this;
      }
   }

   public interface File {
      Path path();

      EventLogDirectory.FileId id();

      @Nullable
      Reader openReader() throws IOException;

      EventLogDirectory.CompressedFile compress() throws IOException;
   }

   public static record FileId(LocalDate date, int index) {
      private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

      public FileId(LocalDate date, int index) {
         super();
         this.date = date;
         this.index = index;
      }

      @Nullable
      public static EventLogDirectory.FileId parse(String var0) {
         int var1 = var0.indexOf("-");
         if (var1 == -1) {
            return null;
         } else {
            String var2 = var0.substring(0, var1);
            String var3 = var0.substring(var1 + 1);

            try {
               return new EventLogDirectory.FileId(LocalDate.parse(var2, DATE_FORMATTER), Integer.parseInt(var3));
            } catch (DateTimeParseException | NumberFormatException var5) {
               return null;
            }
         }
      }

      public String toString() {
         return DATE_FORMATTER.format(this.date) + "-" + this.index;
      }

      public String toFileName(String var1) {
         return this + var1;
      }
   }

   public static class FileList implements Iterable<EventLogDirectory.File> {
      private final List<EventLogDirectory.File> files;

      FileList(List<EventLogDirectory.File> var1) {
         super();
         this.files = new ArrayList<>(var1);
      }

      public EventLogDirectory.FileList prune(LocalDate var1, int var2) {
         this.files.removeIf(var2x -> {
            EventLogDirectory.FileId var3 = var2x.id();
            LocalDate var4 = var3.date().plusDays((long)var2);
            if (!var1.isBefore(var4)) {
               try {
                  Files.delete(var2x.path());
                  return true;
               } catch (IOException var6) {
                  EventLogDirectory.LOGGER.warn("Failed to delete expired event log file: {}", var2x.path(), var6);
               }
            }

            return false;
         });
         return this;
      }

      public EventLogDirectory.FileList compressAll() {
         ListIterator var1 = this.files.listIterator();

         while (var1.hasNext()) {
            EventLogDirectory.File var2 = (EventLogDirectory.File)var1.next();

            try {
               var1.set(var2.compress());
            } catch (IOException var4) {
               EventLogDirectory.LOGGER.warn("Failed to compress event log file: {}", var2.path(), var4);
            }
         }

         return this;
      }

      @Override
      public Iterator<EventLogDirectory.File> iterator() {
         return this.files.iterator();
      }

      public Stream<EventLogDirectory.File> stream() {
         return this.files.stream();
      }

      public Set<EventLogDirectory.FileId> ids() {
         return this.files.stream().map(EventLogDirectory.File::id).collect(Collectors.toSet());
      }
   }

   public static record RawFile(Path path, EventLogDirectory.FileId id) implements EventLogDirectory.File {
      public RawFile(Path path, EventLogDirectory.FileId id) {
         super();
         this.path = path;
         this.id = id;
      }

      public FileChannel openChannel() throws IOException {
         return FileChannel.open(this.path, StandardOpenOption.WRITE, StandardOpenOption.READ);
      }

      @Nullable
      @Override
      public Reader openReader() throws IOException {
         return Files.exists(this.path) ? Files.newBufferedReader(this.path) : null;
      }

      @Override
      public EventLogDirectory.CompressedFile compress() throws IOException {
         Path var1 = this.path.resolveSibling(this.path.getFileName().toString() + ".gz");
         EventLogDirectory.tryCompress(this.path, var1);
         return new EventLogDirectory.CompressedFile(var1, this.id);
      }
   }
}
