package net.minecraft;

import com.google.common.base.Ticker;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceImmutableList;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SingleKeyCache;
import net.minecraft.util.TimeSource;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.block.state.properties.Property;
import org.slf4j.Logger;

public class Util {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int DEFAULT_MAX_THREADS = 255;
   private static final int DEFAULT_SAFE_FILE_OPERATION_RETRIES = 10;
   private static final String MAX_THREADS_SYSTEM_PROPERTY = "max.bg.threads";
   private static final ExecutorService BACKGROUND_EXECUTOR = makeExecutor("Main");
   private static final ExecutorService IO_POOL = makeIoExecutor("IO-Worker-", false);
   private static final ExecutorService DOWNLOAD_POOL = makeIoExecutor("Download-", true);
   private static final DateTimeFormatter FILENAME_DATE_TIME_FORMATTER;
   public static final int LINEAR_LOOKUP_THRESHOLD = 8;
   public static final long NANOS_PER_MILLI = 1000000L;
   public static TimeSource.NanoTimeSource timeSource;
   public static final Ticker TICKER;
   public static final UUID NIL_UUID;
   public static final FileSystemProvider ZIP_FILE_SYSTEM_PROVIDER;
   private static Consumer<String> thePauser;

   public Util() {
      super();
   }

   public static <K, V> Collector<Map.Entry<? extends K, ? extends V>, ?, Map<K, V>> toMap() {
      return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
   }

   public static <T> Collector<T, ?, List<T>> toMutableList() {
      return Collectors.toCollection(Lists::newArrayList);
   }

   public static <T extends Comparable<T>> String getPropertyName(Property<T> var0, Object var1) {
      return var0.getName((Comparable)var1);
   }

   public static String makeDescriptionId(String var0, @Nullable ResourceLocation var1) {
      return var1 == null ? var0 + ".unregistered_sadface" : var0 + "." + var1.getNamespace() + "." + var1.getPath().replace('/', '.');
   }

   public static long getMillis() {
      return getNanos() / 1000000L;
   }

   public static long getNanos() {
      return timeSource.getAsLong();
   }

   public static long getEpochMillis() {
      return Instant.now().toEpochMilli();
   }

   public static String getFilenameFormattedDateTime() {
      return FILENAME_DATE_TIME_FORMATTER.format(ZonedDateTime.now());
   }

   private static ExecutorService makeExecutor(String var0) {
      int var1 = Mth.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, getMaxThreads());
      Object var2;
      if (var1 <= 0) {
         var2 = MoreExecutors.newDirectExecutorService();
      } else {
         AtomicInteger var3 = new AtomicInteger(1);
         var2 = new ForkJoinPool(var1, (var2x) -> {
            ForkJoinWorkerThread var3x = new ForkJoinWorkerThread(var2x) {
               protected void onTermination(Throwable var1) {
                  if (var1 != null) {
                     Util.LOGGER.warn("{} died", this.getName(), var1);
                  } else {
                     Util.LOGGER.debug("{} shutdown", this.getName());
                  }

                  super.onTermination(var1);
               }
            };
            var3x.setName("Worker-" + var0 + "-" + var3.getAndIncrement());
            return var3x;
         }, Util::onThreadException, true);
      }

      return (ExecutorService)var2;
   }

   private static int getMaxThreads() {
      String var0 = System.getProperty("max.bg.threads");
      if (var0 != null) {
         try {
            int var1 = Integer.parseInt(var0);
            if (var1 >= 1 && var1 <= 255) {
               return var1;
            }

            LOGGER.error("Wrong {} property value '{}'. Should be an integer value between 1 and {}.", new Object[]{"max.bg.threads", var0, 255});
         } catch (NumberFormatException var2) {
            LOGGER.error("Could not parse {} property value '{}'. Should be an integer value between 1 and {}.", new Object[]{"max.bg.threads", var0, 255});
         }
      }

      return 255;
   }

   public static ExecutorService backgroundExecutor() {
      return BACKGROUND_EXECUTOR;
   }

   public static ExecutorService ioPool() {
      return IO_POOL;
   }

   public static ExecutorService nonCriticalIoPool() {
      return DOWNLOAD_POOL;
   }

   public static void shutdownExecutors() {
      shutdownExecutor(BACKGROUND_EXECUTOR);
      shutdownExecutor(IO_POOL);
   }

   private static void shutdownExecutor(ExecutorService var0) {
      var0.shutdown();

      boolean var1;
      try {
         var1 = var0.awaitTermination(3L, TimeUnit.SECONDS);
      } catch (InterruptedException var3) {
         var1 = false;
      }

      if (!var1) {
         var0.shutdownNow();
      }

   }

   private static ExecutorService makeIoExecutor(String var0, boolean var1) {
      AtomicInteger var2 = new AtomicInteger(1);
      return Executors.newCachedThreadPool((var3) -> {
         Thread var4 = new Thread(var3);
         var4.setName(var0 + var2.getAndIncrement());
         var4.setDaemon(var1);
         var4.setUncaughtExceptionHandler(Util::onThreadException);
         return var4;
      });
   }

   public static void throwAsRuntime(Throwable var0) {
      throw var0 instanceof RuntimeException ? (RuntimeException)var0 : new RuntimeException(var0);
   }

   private static void onThreadException(Thread var0, Throwable var1) {
      pauseInIde(var1);
      if (var1 instanceof CompletionException) {
         var1 = var1.getCause();
      }

      if (var1 instanceof ReportedException var2) {
         Bootstrap.realStdoutPrintln(var2.getReport().getFriendlyReport());
         System.exit(-1);
      }

      LOGGER.error(String.format(Locale.ROOT, "Caught exception in thread %s", var0), var1);
   }

   @Nullable
   public static Type<?> fetchChoiceType(DSL.TypeReference var0, String var1) {
      return !SharedConstants.CHECK_DATA_FIXER_SCHEMA ? null : doFetchChoiceType(var0, var1);
   }

   @Nullable
   private static Type<?> doFetchChoiceType(DSL.TypeReference var0, String var1) {
      Type var2 = null;

      try {
         var2 = DataFixers.getDataFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().getDataVersion().getVersion())).getChoiceType(var0, var1);
      } catch (IllegalArgumentException var4) {
         LOGGER.error("No data fixer registered for {}", var1);
         if (SharedConstants.IS_RUNNING_IN_IDE) {
            throw var4;
         }
      }

      return var2;
   }

   public static Runnable wrapThreadWithTaskName(String var0, Runnable var1) {
      return SharedConstants.IS_RUNNING_IN_IDE ? () -> {
         Thread var2 = Thread.currentThread();
         String var3 = var2.getName();
         var2.setName(var0);

         try {
            var1.run();
         } finally {
            var2.setName(var3);
         }

      } : var1;
   }

   public static <V> Supplier<V> wrapThreadWithTaskName(String var0, Supplier<V> var1) {
      return SharedConstants.IS_RUNNING_IN_IDE ? () -> {
         Thread var2 = Thread.currentThread();
         String var3 = var2.getName();
         var2.setName(var0);

         Object var4;
         try {
            var4 = var1.get();
         } finally {
            var2.setName(var3);
         }

         return var4;
      } : var1;
   }

   public static <T> String getRegisteredName(Registry<T> var0, T var1) {
      ResourceLocation var2 = var0.getKey(var1);
      return var2 == null ? "[unregistered]" : var2.toString();
   }

   public static <T> Predicate<T> allOf(List<? extends Predicate<T>> var0) {
      List var1 = List.copyOf(var0);
      Predicate var10000;
      switch (var1.size()) {
         case 0 -> var10000 = (var0x) -> {
   return true;
};
         case 1 -> var10000 = (Predicate)var1.get(0);
         case 2 -> var10000 = ((Predicate)var1.get(0)).and((Predicate)var1.get(1));
         default -> var10000 = (var1x) -> {
   Iterator var2 = var1.iterator();

   Predicate var3;
   do {
      if (!var2.hasNext()) {
         return true;
      }

      var3 = (Predicate)var2.next();
   } while(var3.test(var1x));

   return false;
};
      }

      return var10000;
   }

   public static <T> Predicate<T> anyOf(List<? extends Predicate<T>> var0) {
      List var1 = List.copyOf(var0);
      Predicate var10000;
      switch (var1.size()) {
         case 0 -> var10000 = (var0x) -> {
   return false;
};
         case 1 -> var10000 = (Predicate)var1.get(0);
         case 2 -> var10000 = ((Predicate)var1.get(0)).or((Predicate)var1.get(1));
         default -> var10000 = (var1x) -> {
   Iterator var2 = var1.iterator();

   Predicate var3;
   do {
      if (!var2.hasNext()) {
         return false;
      }

      var3 = (Predicate)var2.next();
   } while(!var3.test(var1x));

   return true;
};
      }

      return var10000;
   }

   public static OS getPlatform() {
      String var0 = System.getProperty("os.name").toLowerCase(Locale.ROOT);
      if (var0.contains("win")) {
         return Util.OS.WINDOWS;
      } else if (var0.contains("mac")) {
         return Util.OS.OSX;
      } else if (var0.contains("solaris")) {
         return Util.OS.SOLARIS;
      } else if (var0.contains("sunos")) {
         return Util.OS.SOLARIS;
      } else if (var0.contains("linux")) {
         return Util.OS.LINUX;
      } else {
         return var0.contains("unix") ? Util.OS.LINUX : Util.OS.UNKNOWN;
      }
   }

   public static Stream<String> getVmArguments() {
      RuntimeMXBean var0 = ManagementFactory.getRuntimeMXBean();
      return var0.getInputArguments().stream().filter((var0x) -> {
         return var0x.startsWith("-X");
      });
   }

   public static <T> T lastOf(List<T> var0) {
      return var0.get(var0.size() - 1);
   }

   public static <T> T findNextInIterable(Iterable<T> var0, @Nullable T var1) {
      Iterator var2 = var0.iterator();
      Object var3 = var2.next();
      if (var1 != null) {
         Object var4 = var3;

         while(var4 != var1) {
            if (var2.hasNext()) {
               var4 = var2.next();
            }
         }

         if (var2.hasNext()) {
            return var2.next();
         }
      }

      return var3;
   }

   public static <T> T findPreviousInIterable(Iterable<T> var0, @Nullable T var1) {
      Iterator var2 = var0.iterator();

      Object var3;
      Object var4;
      for(var3 = null; var2.hasNext(); var3 = var4) {
         var4 = var2.next();
         if (var4 == var1) {
            if (var3 == null) {
               var3 = var2.hasNext() ? Iterators.getLast(var2) : var1;
            }
            break;
         }
      }

      return var3;
   }

   public static <T> T make(Supplier<T> var0) {
      return var0.get();
   }

   public static <T> T make(T var0, Consumer<? super T> var1) {
      var1.accept(var0);
      return var0;
   }

   public static <V> CompletableFuture<List<V>> sequence(List<? extends CompletableFuture<V>> var0) {
      if (var0.isEmpty()) {
         return CompletableFuture.completedFuture(List.of());
      } else if (var0.size() == 1) {
         return ((CompletableFuture)var0.get(0)).thenApply(List::of);
      } else {
         CompletableFuture var1 = CompletableFuture.allOf((CompletableFuture[])var0.toArray(new CompletableFuture[0]));
         return var1.thenApply((var1x) -> {
            return var0.stream().map(CompletableFuture::join).toList();
         });
      }
   }

   public static <V> CompletableFuture<List<V>> sequenceFailFast(List<? extends CompletableFuture<? extends V>> var0) {
      CompletableFuture var1 = new CompletableFuture();
      Objects.requireNonNull(var1);
      return fallibleSequence(var0, var1::completeExceptionally).applyToEither(var1, Function.identity());
   }

   public static <V> CompletableFuture<List<V>> sequenceFailFastAndCancel(List<? extends CompletableFuture<? extends V>> var0) {
      CompletableFuture var1 = new CompletableFuture();
      return fallibleSequence(var0, (var2) -> {
         if (var1.completeExceptionally(var2)) {
            Iterator var3 = var0.iterator();

            while(var3.hasNext()) {
               CompletableFuture var4 = (CompletableFuture)var3.next();
               var4.cancel(true);
            }
         }

      }).applyToEither(var1, Function.identity());
   }

   private static <V> CompletableFuture<List<V>> fallibleSequence(List<? extends CompletableFuture<? extends V>> var0, Consumer<Throwable> var1) {
      ArrayList var2 = Lists.newArrayListWithCapacity(var0.size());
      CompletableFuture[] var3 = new CompletableFuture[var0.size()];
      var0.forEach((var3x) -> {
         int var4 = var2.size();
         var2.add((Object)null);
         var3[var4] = var3x.whenComplete((var3xx, var4x) -> {
            if (var4x != null) {
               var1.accept(var4x);
            } else {
               var2.set(var4, var3xx);
            }

         });
      });
      return CompletableFuture.allOf(var3).thenApply((var1x) -> {
         return var2;
      });
   }

   public static <T> Optional<T> ifElse(Optional<T> var0, Consumer<T> var1, Runnable var2) {
      if (var0.isPresent()) {
         var1.accept(var0.get());
      } else {
         var2.run();
      }

      return var0;
   }

   public static <T> Supplier<T> name(Supplier<T> var0, Supplier<String> var1) {
      return var0;
   }

   public static Runnable name(Runnable var0, Supplier<String> var1) {
      return var0;
   }

   public static void logAndPauseIfInIde(String var0) {
      LOGGER.error(var0);
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         doPause(var0);
      }

   }

   public static void logAndPauseIfInIde(String var0, Throwable var1) {
      LOGGER.error(var0, var1);
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         doPause(var0);
      }

   }

   public static <T extends Throwable> T pauseInIde(T var0) {
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         LOGGER.error("Trying to throw a fatal exception, pausing in IDE", var0);
         doPause(var0.getMessage());
      }

      return var0;
   }

   public static void setPause(Consumer<String> var0) {
      thePauser = var0;
   }

   private static void doPause(String var0) {
      Instant var1 = Instant.now();
      LOGGER.warn("Did you remember to set a breakpoint here?");
      boolean var2 = Duration.between(var1, Instant.now()).toMillis() > 500L;
      if (!var2) {
         thePauser.accept(var0);
      }

   }

   public static String describeError(Throwable var0) {
      if (var0.getCause() != null) {
         return describeError(var0.getCause());
      } else {
         return var0.getMessage() != null ? var0.getMessage() : var0.toString();
      }
   }

   public static <T> T getRandom(T[] var0, RandomSource var1) {
      return var0[var1.nextInt(var0.length)];
   }

   public static int getRandom(int[] var0, RandomSource var1) {
      return var0[var1.nextInt(var0.length)];
   }

   public static <T> T getRandom(List<T> var0, RandomSource var1) {
      return var0.get(var1.nextInt(var0.size()));
   }

   public static <T> Optional<T> getRandomSafe(List<T> var0, RandomSource var1) {
      return var0.isEmpty() ? Optional.empty() : Optional.of(getRandom(var0, var1));
   }

   private static BooleanSupplier createRenamer(final Path var0, final Path var1) {
      return new BooleanSupplier() {
         public boolean getAsBoolean() {
            try {
               Files.move(var0, var1);
               return true;
            } catch (IOException var2) {
               Util.LOGGER.error("Failed to rename", var2);
               return false;
            }
         }

         public String toString() {
            String var10000 = String.valueOf(var0);
            return "rename " + var10000 + " to " + String.valueOf(var1);
         }
      };
   }

   private static BooleanSupplier createDeleter(final Path var0) {
      return new BooleanSupplier() {
         public boolean getAsBoolean() {
            try {
               Files.deleteIfExists(var0);
               return true;
            } catch (IOException var2) {
               Util.LOGGER.warn("Failed to delete", var2);
               return false;
            }
         }

         public String toString() {
            return "delete old " + String.valueOf(var0);
         }
      };
   }

   private static BooleanSupplier createFileDeletedCheck(final Path var0) {
      return new BooleanSupplier() {
         public boolean getAsBoolean() {
            return !Files.exists(var0, new LinkOption[0]);
         }

         public String toString() {
            return "verify that " + String.valueOf(var0) + " is deleted";
         }
      };
   }

   private static BooleanSupplier createFileCreatedCheck(final Path var0) {
      return new BooleanSupplier() {
         public boolean getAsBoolean() {
            return Files.isRegularFile(var0, new LinkOption[0]);
         }

         public String toString() {
            return "verify that " + String.valueOf(var0) + " is present";
         }
      };
   }

   private static boolean executeInSequence(BooleanSupplier... var0) {
      BooleanSupplier[] var1 = var0;
      int var2 = var0.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         BooleanSupplier var4 = var1[var3];
         if (!var4.getAsBoolean()) {
            LOGGER.warn("Failed to execute {}", var4);
            return false;
         }
      }

      return true;
   }

   private static boolean runWithRetries(int var0, String var1, BooleanSupplier... var2) {
      for(int var3 = 0; var3 < var0; ++var3) {
         if (executeInSequence(var2)) {
            return true;
         }

         LOGGER.error("Failed to {}, retrying {}/{}", new Object[]{var1, var3, var0});
      }

      LOGGER.error("Failed to {}, aborting, progress might be lost", var1);
      return false;
   }

   public static void safeReplaceFile(Path var0, Path var1, Path var2) {
      safeReplaceOrMoveFile(var0, var1, var2, false);
   }

   public static boolean safeReplaceOrMoveFile(Path var0, Path var1, Path var2, boolean var3) {
      if (Files.exists(var0, new LinkOption[0]) && !runWithRetries(10, "create backup " + String.valueOf(var2), createDeleter(var2), createRenamer(var0, var2), createFileCreatedCheck(var2))) {
         return false;
      } else if (!runWithRetries(10, "remove old " + String.valueOf(var0), createDeleter(var0), createFileDeletedCheck(var0))) {
         return false;
      } else if (!runWithRetries(10, "replace " + String.valueOf(var0) + " with " + String.valueOf(var1), createRenamer(var1, var0), createFileCreatedCheck(var0)) && !var3) {
         runWithRetries(10, "restore " + String.valueOf(var0) + " from " + String.valueOf(var2), createRenamer(var2, var0), createFileCreatedCheck(var0));
         return false;
      } else {
         return true;
      }
   }

   public static int offsetByCodepoints(String var0, int var1, int var2) {
      int var3 = var0.length();
      int var4;
      if (var2 >= 0) {
         for(var4 = 0; var1 < var3 && var4 < var2; ++var4) {
            if (Character.isHighSurrogate(var0.charAt(var1++)) && var1 < var3 && Character.isLowSurrogate(var0.charAt(var1))) {
               ++var1;
            }
         }
      } else {
         for(var4 = var2; var1 > 0 && var4 < 0; ++var4) {
            --var1;
            if (Character.isLowSurrogate(var0.charAt(var1)) && var1 > 0 && Character.isHighSurrogate(var0.charAt(var1 - 1))) {
               --var1;
            }
         }
      }

      return var1;
   }

   public static Consumer<String> prefix(String var0, Consumer<String> var1) {
      return (var2) -> {
         var1.accept(var0 + var2);
      };
   }

   public static DataResult<int[]> fixedSize(IntStream var0, int var1) {
      int[] var2 = var0.limit((long)(var1 + 1)).toArray();
      if (var2.length != var1) {
         Supplier var3 = () -> {
            return "Input is not a list of " + var1 + " ints";
         };
         return var2.length >= var1 ? DataResult.error(var3, Arrays.copyOf(var2, var1)) : DataResult.error(var3);
      } else {
         return DataResult.success(var2);
      }
   }

   public static DataResult<long[]> fixedSize(LongStream var0, int var1) {
      long[] var2 = var0.limit((long)(var1 + 1)).toArray();
      if (var2.length != var1) {
         Supplier var3 = () -> {
            return "Input is not a list of " + var1 + " longs";
         };
         return var2.length >= var1 ? DataResult.error(var3, Arrays.copyOf(var2, var1)) : DataResult.error(var3);
      } else {
         return DataResult.success(var2);
      }
   }

   public static <T> DataResult<List<T>> fixedSize(List<T> var0, int var1) {
      if (var0.size() != var1) {
         Supplier var2 = () -> {
            return "Input is not a list of " + var1 + " elements";
         };
         return var0.size() >= var1 ? DataResult.error(var2, var0.subList(0, var1)) : DataResult.error(var2);
      } else {
         return DataResult.success(var0);
      }
   }

   public static void startTimerHackThread() {
      Thread var0 = new Thread("Timer hack thread") {
         public void run() {
            while(true) {
               try {
                  Thread.sleep(2147483647L);
               } catch (InterruptedException var2) {
                  Util.LOGGER.warn("Timer hack thread interrupted, that really should not happen");
                  return;
               }
            }
         }
      };
      var0.setDaemon(true);
      var0.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      var0.start();
   }

   public static void copyBetweenDirs(Path var0, Path var1, Path var2) throws IOException {
      Path var3 = var0.relativize(var2);
      Path var4 = var1.resolve(var3);
      Files.copy(var2, var4);
   }

   public static String sanitizeName(String var0, CharPredicate var1) {
      return (String)var0.toLowerCase(Locale.ROOT).chars().mapToObj((var1x) -> {
         return var1.test((char)var1x) ? Character.toString((char)var1x) : "_";
      }).collect(Collectors.joining());
   }

   public static <K, V> SingleKeyCache<K, V> singleKeyCache(Function<K, V> var0) {
      return new SingleKeyCache(var0);
   }

   public static <T, R> Function<T, R> memoize(final Function<T, R> var0) {
      return new Function<T, R>() {
         private final Map<T, R> cache = new ConcurrentHashMap();

         public R apply(T var1) {
            return this.cache.computeIfAbsent(var1, var0);
         }

         public String toString() {
            String var10000 = String.valueOf(var0);
            return "memoize/1[function=" + var10000 + ", size=" + this.cache.size() + "]";
         }
      };
   }

   public static <T, U, R> BiFunction<T, U, R> memoize(final BiFunction<T, U, R> var0) {
      return new BiFunction<T, U, R>() {
         private final Map<Pair<T, U>, R> cache = new ConcurrentHashMap();

         public R apply(T var1, U var2) {
            return this.cache.computeIfAbsent(Pair.of(var1, var2), (var1x) -> {
               return var0.apply(var1x.getFirst(), var1x.getSecond());
            });
         }

         public String toString() {
            String var10000 = String.valueOf(var0);
            return "memoize/2[function=" + var10000 + ", size=" + this.cache.size() + "]";
         }
      };
   }

   public static <T> List<T> toShuffledList(Stream<T> var0, RandomSource var1) {
      ObjectArrayList var2 = (ObjectArrayList)var0.collect(ObjectArrayList.toList());
      shuffle(var2, var1);
      return var2;
   }

   public static IntArrayList toShuffledList(IntStream var0, RandomSource var1) {
      IntArrayList var2 = IntArrayList.wrap(var0.toArray());
      int var3 = var2.size();

      for(int var4 = var3; var4 > 1; --var4) {
         int var5 = var1.nextInt(var4);
         var2.set(var4 - 1, var2.set(var5, var2.getInt(var4 - 1)));
      }

      return var2;
   }

   public static <T> List<T> shuffledCopy(T[] var0, RandomSource var1) {
      ObjectArrayList var2 = new ObjectArrayList(var0);
      shuffle(var2, var1);
      return var2;
   }

   public static <T> List<T> shuffledCopy(ObjectArrayList<T> var0, RandomSource var1) {
      ObjectArrayList var2 = new ObjectArrayList(var0);
      shuffle(var2, var1);
      return var2;
   }

   public static <T> void shuffle(List<T> var0, RandomSource var1) {
      int var2 = var0.size();

      for(int var3 = var2; var3 > 1; --var3) {
         int var4 = var1.nextInt(var3);
         var0.set(var3 - 1, var0.set(var4, var0.get(var3 - 1)));
      }

   }

   public static <T> CompletableFuture<T> blockUntilDone(Function<Executor, CompletableFuture<T>> var0) {
      return (CompletableFuture)blockUntilDone(var0, CompletableFuture::isDone);
   }

   public static <T> T blockUntilDone(Function<Executor, T> var0, Predicate<T> var1) {
      LinkedBlockingQueue var2 = new LinkedBlockingQueue();
      Objects.requireNonNull(var2);
      Object var3 = var0.apply(var2::add);

      while(!var1.test(var3)) {
         try {
            Runnable var4 = (Runnable)var2.poll(100L, TimeUnit.MILLISECONDS);
            if (var4 != null) {
               var4.run();
            }
         } catch (InterruptedException var5) {
            LOGGER.warn("Interrupted wait");
            break;
         }
      }

      int var6 = var2.size();
      if (var6 > 0) {
         LOGGER.warn("Tasks left in queue: {}", var6);
      }

      return var3;
   }

   public static <T> ToIntFunction<T> createIndexLookup(List<T> var0) {
      int var1 = var0.size();
      if (var1 < 8) {
         Objects.requireNonNull(var0);
         return var0::indexOf;
      } else {
         Object2IntOpenHashMap var2 = new Object2IntOpenHashMap(var1);
         var2.defaultReturnValue(-1);

         for(int var3 = 0; var3 < var1; ++var3) {
            var2.put(var0.get(var3), var3);
         }

         return var2;
      }
   }

   public static <T> ToIntFunction<T> createIndexIdentityLookup(List<T> var0) {
      int var1 = var0.size();
      if (var1 < 8) {
         ReferenceImmutableList var4 = new ReferenceImmutableList(var0);
         Objects.requireNonNull(var4);
         return var4::indexOf;
      } else {
         Reference2IntOpenHashMap var2 = new Reference2IntOpenHashMap(var1);
         var2.defaultReturnValue(-1);

         for(int var3 = 0; var3 < var1; ++var3) {
            var2.put(var0.get(var3), var3);
         }

         return var2;
      }
   }

   public static <A, B> Typed<B> writeAndReadTypedOrThrow(Typed<A> var0, Type<B> var1, UnaryOperator<Dynamic<?>> var2) {
      Dynamic var3 = (Dynamic)var0.write().getOrThrow();
      return readTypedOrThrow(var1, (Dynamic)var2.apply(var3), true);
   }

   public static <T> Typed<T> readTypedOrThrow(Type<T> var0, Dynamic<?> var1) {
      return readTypedOrThrow(var0, var1, false);
   }

   public static <T> Typed<T> readTypedOrThrow(Type<T> var0, Dynamic<?> var1, boolean var2) {
      DataResult var3 = var0.readTyped(var1).map(Pair::getFirst);

      CrashReport var5;
      CrashReportCategory var6;
      try {
         if (var2) {
            return (Typed)var3.getPartialOrThrow(IllegalStateException::new);
         }
      } catch (IllegalStateException var8) {
         var5 = CrashReport.forThrowable(var8, "Reading type");
         var6 = var5.addCategory("Info");
         var6.setDetail("Data", (Object)var1);
         var6.setDetail("Type", (Object)var0);
         throw new ReportedException(var5);
      }

      try {
         return (Typed)var3.getOrThrow(IllegalStateException::new);
      } catch (IllegalStateException var7) {
         var5 = CrashReport.forThrowable(var7, "Reading type");
         var6 = var5.addCategory("Info");
         var6.setDetail("Data", (Object)var1);
         var6.setDetail("Type", (Object)var0);
         throw new ReportedException(var5);
      }
   }

   public static <T> List<T> copyAndAdd(List<T> var0, T var1) {
      return ImmutableList.builderWithExpectedSize(var0.size() + 1).addAll(var0).add(var1).build();
   }

   public static <T> List<T> copyAndAdd(T var0, List<T> var1) {
      return ImmutableList.builderWithExpectedSize(var1.size() + 1).add(var0).addAll(var1).build();
   }

   public static <K, V> Map<K, V> copyAndPut(Map<K, V> var0, K var1, V var2) {
      return ImmutableMap.builderWithExpectedSize(var0.size() + 1).putAll(var0).put(var1, var2).buildKeepingLast();
   }

   static {
      FILENAME_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss", Locale.ROOT);
      timeSource = System::nanoTime;
      TICKER = new Ticker() {
         public long read() {
            return Util.timeSource.getAsLong();
         }
      };
      NIL_UUID = new UUID(0L, 0L);
      ZIP_FILE_SYSTEM_PROVIDER = (FileSystemProvider)FileSystemProvider.installedProviders().stream().filter((var0) -> {
         return var0.getScheme().equalsIgnoreCase("jar");
      }).findFirst().orElseThrow(() -> {
         return new IllegalStateException("No jar file system provider found");
      });
      thePauser = (var0) -> {
      };
   }

   public static enum OS {
      LINUX("linux"),
      SOLARIS("solaris"),
      WINDOWS("windows") {
         protected String[] getOpenUrlArguments(URL var1) {
            return new String[]{"rundll32", "url.dll,FileProtocolHandler", var1.toString()};
         }
      },
      OSX("mac") {
         protected String[] getOpenUrlArguments(URL var1) {
            return new String[]{"open", var1.toString()};
         }
      },
      UNKNOWN("unknown");

      private final String telemetryName;

      OS(final String var3) {
         this.telemetryName = var3;
      }

      public void openUrl(URL var1) {
         try {
            Process var2 = (Process)AccessController.doPrivileged(() -> {
               return Runtime.getRuntime().exec(this.getOpenUrlArguments(var1));
            });
            var2.getInputStream().close();
            var2.getErrorStream().close();
            var2.getOutputStream().close();
         } catch (IOException | PrivilegedActionException var3) {
            Util.LOGGER.error("Couldn't open url '{}'", var1, var3);
         }

      }

      public void openUri(URI var1) {
         try {
            this.openUrl(var1.toURL());
         } catch (MalformedURLException var3) {
            Util.LOGGER.error("Couldn't open uri '{}'", var1, var3);
         }

      }

      public void openFile(File var1) {
         try {
            this.openUrl(var1.toURI().toURL());
         } catch (MalformedURLException var3) {
            Util.LOGGER.error("Couldn't open file '{}'", var1, var3);
         }

      }

      protected String[] getOpenUrlArguments(URL var1) {
         String var2 = var1.toString();
         if ("file".equals(var1.getProtocol())) {
            var2 = var2.replace("file:", "file://");
         }

         return new String[]{"xdg-open", var2};
      }

      public void openUri(String var1) {
         try {
            this.openUrl((new URI(var1)).toURL());
         } catch (MalformedURLException | IllegalArgumentException | URISyntaxException var3) {
            Util.LOGGER.error("Couldn't open uri '{}'", var1, var3);
         }

      }

      public String telemetryName() {
         return this.telemetryName;
      }

      // $FF: synthetic method
      private static OS[] $values() {
         return new OS[]{LINUX, SOLARIS, WINDOWS, OSX, UNKNOWN};
      }
   }
}
