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

   public FileList listFiles() throws IOException {
      Stream var1 = Files.list(this.root);

      FileList var2;
      try {
         var2 = new FileList(var1.filter((var0) -> {
            return Files.isRegularFile(var0, new LinkOption[0]);
         }).map(this::parseFile).filter(Objects::nonNull).toList());
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

      return var2;
   }

   @Nullable
   private File parseFile(Path var1) {
      String var2 = var1.getFileName().toString();
      int var3 = var2.indexOf(46);
      if (var3 == -1) {
         return null;
      } else {
         FileId var4 = EventLogDirectory.FileId.parse(var2.substring(0, var3));
         if (var4 != null) {
            String var5 = var2.substring(var3);
            if (var5.equals(this.extension)) {
               return new RawFile(var1, var4);
            }

            if (var5.equals(this.extension + ".gz")) {
               return new CompressedFile(var1, var4);
            }
         }

         return null;
      }
   }

   static void tryCompress(Path var0, Path var1) throws IOException {
      if (Files.exists(var1, new LinkOption[0])) {
         throw new IOException("Compressed target file already exists: " + String.valueOf(var1));
      } else {
         FileChannel var2 = FileChannel.open(var0, StandardOpenOption.WRITE, StandardOpenOption.READ);

         try {
            FileLock var3 = var2.tryLock();
            if (var3 == null) {
               throw new IOException("Raw log file is already locked, cannot compress: " + String.valueOf(var0));
            }

            writeCompressed(var2, var1);
            var2.truncate(0L);
         } catch (Throwable var6) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (var2 != null) {
            var2.close();
         }

         Files.delete(var0);
      }
   }

   private static void writeCompressed(ReadableByteChannel var0, Path var1) throws IOException {
      GZIPOutputStream var2 = new GZIPOutputStream(Files.newOutputStream(var1));

      try {
         byte[] var3 = new byte[4096];
         ByteBuffer var4 = ByteBuffer.wrap(var3);

         while(var0.read(var4) >= 0) {
            var4.flip();
            ((OutputStream)var2).write(var3, 0, var4.limit());
            var4.clear();
         }
      } catch (Throwable var6) {
         try {
            ((OutputStream)var2).close();
         } catch (Throwable var5) {
            var6.addSuppressed(var5);
         }

         throw var6;
      }

      ((OutputStream)var2).close();
   }

   public RawFile createNewFile(LocalDate var1) throws IOException {
      int var2 = 1;
      Set var4 = this.listFiles().ids();

      FileId var3;
      do {
         var3 = new FileId(var1, var2++);
      } while(var4.contains(var3));

      RawFile var5 = new RawFile(this.root.resolve(var3.toFileName(this.extension)), var3);
      Files.createFile(var5.path());
      return var5;
   }

   public static class FileList implements Iterable<File> {
      private final List<File> files;

      FileList(List<File> var1) {
         super();
         this.files = new ArrayList(var1);
      }

      public FileList prune(LocalDate var1, int var2) {
         this.files.removeIf((var2x) -> {
            FileId var3 = var2x.id();
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

      public FileList compressAll() {
         ListIterator var1 = this.files.listIterator();

         while(var1.hasNext()) {
            File var2 = (File)var1.next();

            try {
               var1.set(var2.compress());
            } catch (IOException var4) {
               EventLogDirectory.LOGGER.warn("Failed to compress event log file: {}", var2.path(), var4);
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

   public static record FileId(LocalDate date, int index) {
      private static final DateTimeFormatter DATE_FORMATTER;

      public FileId(LocalDate var1, int var2) {
         super();
         this.date = var1;
         this.index = var2;
      }

      @Nullable
      public static FileId parse(String var0) {
         int var1 = var0.indexOf("-");
         if (var1 == -1) {
            return null;
         } else {
            String var2 = var0.substring(0, var1);
            String var3 = var0.substring(var1 + 1);

            try {
               return new FileId(LocalDate.parse(var2, DATE_FORMATTER), Integer.parseInt(var3));
            } catch (DateTimeParseException | NumberFormatException var5) {
               return null;
            }
         }
      }

      public String toString() {
         String var10000 = DATE_FORMATTER.format(this.date);
         return var10000 + "-" + this.index;
      }

      public String toFileName(String var1) {
         String var10000 = String.valueOf(this);
         return var10000 + var1;
      }

      public LocalDate date() {
         return this.date;
      }

      public int index() {
         return this.index;
      }

      static {
         DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
      }
   }

   public static record RawFile(Path path, FileId id) implements File {
      public RawFile(Path var1, FileId var2) {
         super();
         this.path = var1;
         this.id = var2;
      }

      public FileChannel openChannel() throws IOException {
         return FileChannel.open(this.path, StandardOpenOption.WRITE, StandardOpenOption.READ);
      }

      @Nullable
      public Reader openReader() throws IOException {
         return Files.exists(this.path, new LinkOption[0]) ? Files.newBufferedReader(this.path) : null;
      }

      public CompressedFile compress() throws IOException {
         Path var1 = this.path.resolveSibling(this.path.getFileName().toString() + ".gz");
         EventLogDirectory.tryCompress(this.path, var1);
         return new CompressedFile(var1, this.id);
      }

      public Path path() {
         return this.path;
      }

      public FileId id() {
         return this.id;
      }
   }

   public static record CompressedFile(Path path, FileId id) implements File {
      public CompressedFile(Path var1, FileId var2) {
         super();
         this.path = var1;
         this.id = var2;
      }

      @Nullable
      public Reader openReader() throws IOException {
         return !Files.exists(this.path, new LinkOption[0]) ? null : new BufferedReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(this.path))));
      }

      public CompressedFile compress() {
         return this;
      }

      public Path path() {
         return this.path;
      }

      public FileId id() {
         return this.id;
      }
   }

   public interface File {
      Path path();

      FileId id();

      @Nullable
      Reader openReader() throws IOException;

      CompressedFile compress() throws IOException;
   }
}
