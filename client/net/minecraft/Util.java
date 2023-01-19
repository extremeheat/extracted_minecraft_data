package net.minecraft;

import com.google.common.base.Ticker;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.Hash.Strategy;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
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
import java.util.function.IntFunction;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class Util {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int DEFAULT_MAX_THREADS = 255;
   private static final String MAX_THREADS_SYSTEM_PROPERTY = "max.bg.threads";
   private static final AtomicInteger WORKER_COUNT = new AtomicInteger(1);
   private static final ExecutorService BOOTSTRAP_EXECUTOR = makeExecutor("Bootstrap");
   private static final ExecutorService BACKGROUND_EXECUTOR = makeExecutor("Main");
   private static final ExecutorService IO_POOL = makeIoExecutor();
   public static LongSupplier timeSource = System::nanoTime;
   public static final Ticker TICKER = new Ticker() {
      public long read() {
         return Util.timeSource.getAsLong();
      }
   };
   public static final UUID NIL_UUID = new UUID(0L, 0L);
   public static final FileSystemProvider ZIP_FILE_SYSTEM_PROVIDER = FileSystemProvider.installedProviders()
      .stream()
      .filter(var0 -> var0.getScheme().equalsIgnoreCase("jar"))
      .findFirst()
      .orElseThrow(() -> new IllegalStateException("No jar file system provider found"));
   private static Consumer<String> thePauser = var0 -> {
   };

   public Util() {
      super();
   }

   public static <K, V> Collector<Entry<? extends K, ? extends V>, ?, Map<K, V>> toMap() {
      return Collectors.toMap(Entry::getKey, Entry::getValue);
   }

   public static <T extends Comparable<T>> String getPropertyName(Property<T> var0, Object var1) {
      return var0.getName((T)var1);
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

   private static ExecutorService makeExecutor(String var0) {
      int var1 = Mth.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, getMaxThreads());
      Object var2;
      if (var1 <= 0) {
         var2 = MoreExecutors.newDirectExecutorService();
      } else {
         var2 = new ForkJoinPool(var1, var1x -> {
            ForkJoinWorkerThread var2x = new ForkJoinWorkerThread(var1x) {
               @Override
               protected void onTermination(Throwable var1) {
                  if (var1 != null) {
                     Util.LOGGER.warn("{} died", this.getName(), var1);
                  } else {
                     Util.LOGGER.debug("{} shutdown", this.getName());
                  }

                  super.onTermination(var1);
               }
            };
            var2x.setName("Worker-" + var0 + "-" + WORKER_COUNT.getAndIncrement());
            return var2x;
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

   public static ExecutorService bootstrapExecutor() {
      return BOOTSTRAP_EXECUTOR;
   }

   public static ExecutorService backgroundExecutor() {
      return BACKGROUND_EXECUTOR;
   }

   public static ExecutorService ioPool() {
      return IO_POOL;
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

   private static ExecutorService makeIoExecutor() {
      return Executors.newCachedThreadPool(var0 -> {
         Thread var1 = new Thread(var0);
         var1.setName("IO-Worker-" + WORKER_COUNT.getAndIncrement());
         var1.setUncaughtExceptionHandler(Util::onThreadException);
         return var1;
      });
   }

   public static <T> CompletableFuture<T> failedFuture(Throwable var0) {
      CompletableFuture var1 = new CompletableFuture();
      var1.completeExceptionally(var0);
      return var1;
   }

   public static void throwAsRuntime(Throwable var0) {
      throw var0 instanceof RuntimeException ? (RuntimeException)var0 : new RuntimeException(var0);
   }

   private static void onThreadException(Thread var0, Throwable var1) {
      pauseInIde(var1);
      if (var1 instanceof CompletionException) {
         var1 = var1.getCause();
      }

      if (var1 instanceof ReportedException) {
         Bootstrap.realStdoutPrintln(((ReportedException)var1).getReport().getFriendlyReport());
         System.exit(-1);
      }

      LOGGER.error(String.format("Caught exception in thread %s", var0), var1);
   }

   @Nullable
   public static Type<?> fetchChoiceType(TypeReference var0, String var1) {
      return !SharedConstants.CHECK_DATA_FIXER_SCHEMA ? null : doFetchChoiceType(var0, var1);
   }

   @Nullable
   private static Type<?> doFetchChoiceType(TypeReference var0, String var1) {
      Type var2 = null;

      try {
         var2 = DataFixers.getDataFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().getWorldVersion())).getChoiceType(var0, var1);
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

         return (V)var4;
      } : var1;
   }

   public static Util.OS getPlatform() {
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
      return var0.getInputArguments().stream().filter(var0x -> var0x.startsWith("-X"));
   }

   public static <T> T lastOf(List<T> var0) {
      return (T)var0.get(var0.size() - 1);
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
            return (T)var2.next();
         }
      }

      return (T)var3;
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

      return (T)var3;
   }

   public static <T> T make(Supplier<T> var0) {
      return (T)var0.get();
   }

   public static <T> T make(T var0, Consumer<T> var1) {
      var1.accept(var0);
      return (T)var0;
   }

   @Nullable
   public static <T, R> R mapNullable(@Nullable T var0, Function<T, R> var1) {
      return (R)(var0 == null ? null : var1.apply(var0));
   }

   public static <T, R> R mapNullable(@Nullable T var0, Function<T, R> var1, R var2) {
      return (R)(var0 == null ? var2 : var1.apply(var0));
   }

   public static <K> Strategy<K> identityStrategy() {
      return Util.IdentityStrategy.INSTANCE;
   }

   public static <V> CompletableFuture<List<V>> sequence(List<? extends CompletableFuture<V>> var0) {
      if (var0.isEmpty()) {
         return CompletableFuture.completedFuture(List.of());
      } else if (var0.size() == 1) {
         return ((CompletableFuture)var0.get(0)).thenApply(List::of);
      } else {
         CompletableFuture var1 = CompletableFuture.allOf(var0.toArray(new CompletableFuture[0]));
         return var1.thenApply(var1x -> var0.stream().map(CompletableFuture::join).toList());
      }
   }

   public static <V> CompletableFuture<List<V>> sequenceFailFast(List<? extends CompletableFuture<? extends V>> var0) {
      CompletableFuture var1 = new CompletableFuture();
      return fallibleSequence(var0, var1::completeExceptionally).applyToEither(var1, Function.identity());
   }

   public static <V> CompletableFuture<List<V>> sequenceFailFastAndCancel(List<? extends CompletableFuture<? extends V>> var0) {
      CompletableFuture var1 = new CompletableFuture();
      return fallibleSequence(var0, var2 -> {
         for(CompletableFuture var4 : var0) {
            var4.cancel(true);
         }

         var1.completeExceptionally(var2);
      }).applyToEither(var1, Function.identity());
   }

   private static <V> CompletableFuture<List<V>> fallibleSequence(List<? extends CompletableFuture<? extends V>> var0, Consumer<Throwable> var1) {
      ArrayList var2 = Lists.newArrayListWithCapacity(var0.size());
      CompletableFuture[] var3 = new CompletableFuture[var0.size()];
      var0.forEach(var3x -> {
         int var4 = var2.size();
         var2.add(null);
         var3[var4] = var3x.whenComplete((var3xx, var4x) -> {
            if (var4x != null) {
               var1.accept(var4x);
            } else {
               var2.set(var4, var3xx);
            }
         });
      });
      return CompletableFuture.allOf(var3).thenApply(var1x -> var2);
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

      return (T)var0;
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
      return (T)var0[var1.nextInt(var0.length)];
   }

   public static int getRandom(int[] var0, RandomSource var1) {
      return var0[var1.nextInt(var0.length)];
   }

   public static <T> T getRandom(List<T> var0, RandomSource var1) {
      return (T)var0.get(var1.nextInt(var0.size()));
   }

   public static <T> Optional<T> getRandomSafe(List<T> var0, RandomSource var1) {
      return var0.isEmpty() ? Optional.empty() : Optional.of(getRandom(var0, var1));
   }

   private static BooleanSupplier createRenamer(final Path var0, final Path var1) {
      return new BooleanSupplier() {
         @Override
         public boolean getAsBoolean() {
            try {
               Files.move(var0, var1);
               return true;
            } catch (IOException var2) {
               Util.LOGGER.error("Failed to rename", var2);
               return false;
            }
         }

         @Override
         public String toString() {
            return "rename " + var0 + " to " + var1;
         }
      };
   }

   private static BooleanSupplier createDeleter(final Path var0) {
      return new BooleanSupplier() {
         @Override
         public boolean getAsBoolean() {
            try {
               Files.deleteIfExists(var0);
               return true;
            } catch (IOException var2) {
               Util.LOGGER.warn("Failed to delete", var2);
               return false;
            }
         }

         @Override
         public String toString() {
            return "delete old " + var0;
         }
      };
   }

   private static BooleanSupplier createFileDeletedCheck(final Path var0) {
      return new BooleanSupplier() {
         @Override
         public boolean getAsBoolean() {
            return !Files.exists(var0);
         }

         @Override
         public String toString() {
            return "verify that " + var0 + " is deleted";
         }
      };
   }

   private static BooleanSupplier createFileCreatedCheck(final Path var0) {
      return new BooleanSupplier() {
         @Override
         public boolean getAsBoolean() {
            return Files.isRegularFile(var0);
         }

         @Override
         public String toString() {
            return "verify that " + var0 + " is present";
         }
      };
   }

   private static boolean executeInSequence(BooleanSupplier... var0) {
      for(BooleanSupplier var4 : var0) {
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

   public static void safeReplaceFile(File var0, File var1, File var2) {
      safeReplaceFile(var0.toPath(), var1.toPath(), var2.toPath());
   }

   public static void safeReplaceFile(Path var0, Path var1, Path var2) {
      safeReplaceOrMoveFile(var0, var1, var2, false);
   }

   public static void safeReplaceOrMoveFile(File var0, File var1, File var2, boolean var3) {
      safeReplaceOrMoveFile(var0.toPath(), var1.toPath(), var2.toPath(), var3);
   }

   public static void safeReplaceOrMoveFile(Path var0, Path var1, Path var2, boolean var3) {
      boolean var4 = true;
      if (!Files.exists(var0) || runWithRetries(10, "create backup " + var2, createDeleter(var2), createRenamer(var0, var2), createFileCreatedCheck(var2))) {
         if (runWithRetries(10, "remove old " + var0, createDeleter(var0), createFileDeletedCheck(var0))) {
            if (!runWithRetries(10, "replace " + var0 + " with " + var1, createRenamer(var1, var0), createFileCreatedCheck(var0)) && !var3) {
               runWithRetries(10, "restore " + var0 + " from " + var2, createRenamer(var2, var0), createFileCreatedCheck(var0));
            }
         }
      }
   }

   public static int offsetByCodepoints(String var0, int var1, int var2) {
      int var3 = var0.length();
      if (var2 >= 0) {
         for(int var4 = 0; var1 < var3 && var4 < var2; ++var4) {
            if (Character.isHighSurrogate(var0.charAt(var1++)) && var1 < var3 && Character.isLowSurrogate(var0.charAt(var1))) {
               ++var1;
            }
         }
      } else {
         for(int var5 = var2; var1 > 0 && var5 < 0; ++var5) {
            --var1;
            if (Character.isLowSurrogate(var0.charAt(var1)) && var1 > 0 && Character.isHighSurrogate(var0.charAt(var1 - 1))) {
               --var1;
            }
         }
      }

      return var1;
   }

   public static Consumer<String> prefix(String var0, Consumer<String> var1) {
      return var2 -> var1.accept(var0 + var2);
   }

   public static DataResult<int[]> fixedSize(IntStream var0, int var1) {
      int[] var2 = var0.limit((long)(var1 + 1)).toArray();
      if (var2.length != var1) {
         String var3 = "Input is not a list of " + var1 + " ints";
         return var2.length >= var1 ? DataResult.error(var3, Arrays.copyOf(var2, var1)) : DataResult.error(var3);
      } else {
         return DataResult.success(var2);
      }
   }

   public static <T> DataResult<List<T>> fixedSize(List<T> var0, int var1) {
      if (var0.size() != var1) {
         String var2 = "Input is not a list of " + var1 + " elements";
         return var0.size() >= var1 ? DataResult.error(var2, var0.subList(0, var1)) : DataResult.error(var2);
      } else {
         return DataResult.success(var0);
      }
   }

   public static void startTimerHackThread() {
      Thread var0 = new Thread("Timer hack thread") {
         @Override
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
      return var0.toLowerCase(Locale.ROOT)
         .chars()
         .mapToObj(var1x -> var1.test((char)var1x) ? Character.toString((char)var1x) : "_")
         .collect(Collectors.joining());
   }

   public static <T, R> Function<T, R> memoize(final Function<T, R> var0) {
      return new Function<T, R>() {
         private final Map<T, R> cache = Maps.newHashMap();

         @Override
         public R apply(T var1) {
            return this.cache.computeIfAbsent((T)var1, var0);
         }

         @Override
         public String toString() {
            return "memoize/1[function=" + var0 + ", size=" + this.cache.size() + "]";
         }
      };
   }

   public static <T, U, R> BiFunction<T, U, R> memoize(final BiFunction<T, U, R> var0) {
      return new BiFunction<T, U, R>() {
         private final Map<Pair<T, U>, R> cache = Maps.newHashMap();

         @Override
         public R apply(T var1, U var2) {
            return this.cache.computeIfAbsent(Pair.of(var1, var2), var1x -> (R)var0.apply(var1x.getFirst(), var1x.getSecond()));
         }

         @Override
         public String toString() {
            return "memoize/2[function=" + var0 + ", size=" + this.cache.size() + "]";
         }
      };
   }

   public static <T> List<T> toShuffledList(Stream<T> var0, RandomSource var1) {
      ObjectArrayList var2 = var0.collect(ObjectArrayList.toList());
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

   public static <T> void shuffle(ObjectArrayList<T> var0, RandomSource var1) {
      int var2 = var0.size();

      for(int var3 = var2; var3 > 1; --var3) {
         int var4 = var1.nextInt(var3);
         var0.set(var3 - 1, var0.set(var4, var0.get(var3 - 1)));
      }
   }

   public static <T> CompletableFuture<T> blockUntilDone(Function<Executor, CompletableFuture<T>> var0) {
      return blockUntilDone(var0, CompletableFuture::isDone);
   }

   public static <T> T blockUntilDone(Function<Executor, T> var0, Predicate<T> var1) {
      LinkedBlockingQueue var2 = new LinkedBlockingQueue();
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

      return (T)var3;
   }

   public static <T> ToIntFunction<T> createIndexLookup(List<T> var0) {
      return createIndexLookup(var0, Object2IntOpenHashMap::new);
   }

   public static <T> ToIntFunction<T> createIndexLookup(List<T> var0, IntFunction<Object2IntMap<T>> var1) {
      Object2IntMap var2 = (Object2IntMap)var1.apply(var0.size());

      for(int var3 = 0; var3 < var0.size(); ++var3) {
         var2.put(var0.get(var3), var3);
      }

      return var2;
   }

   static enum IdentityStrategy implements Strategy<Object> {
      INSTANCE;

      private IdentityStrategy() {
      }

      public int hashCode(Object var1) {
         return System.identityHashCode(var1);
      }

      public boolean equals(Object var1, Object var2) {
         return var1 == var2;
      }
   }

   public static enum OS {
      LINUX("linux"),
      SOLARIS("solaris"),
      WINDOWS("windows") {
         @Override
         protected String[] getOpenUrlArguments(URL var1) {
            return new String[]{"rundll32", "url.dll,FileProtocolHandler", var1.toString()};
         }
      },
      OSX("mac") {
         @Override
         protected String[] getOpenUrlArguments(URL var1) {
            return new String[]{"open", var1.toString()};
         }
      },
      UNKNOWN("unknown");

      private final String telemetryName;

      OS(String var3) {
         this.telemetryName = var3;
      }

      public void openUrl(URL var1) {
         try {
            Process var2 = AccessController.doPrivileged((PrivilegedExceptionAction<Process>)(() -> Runtime.getRuntime().exec(this.getOpenUrlArguments(var1))));

            for(String var4 : IOUtils.readLines(var2.getErrorStream())) {
               Util.LOGGER.error(var4);
            }

            var2.getInputStream().close();
            var2.getErrorStream().close();
            var2.getOutputStream().close();
         } catch (IOException | PrivilegedActionException var5) {
            Util.LOGGER.error("Couldn't open url '{}'", var1, var5);
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
            this.openUrl(new URI(var1).toURL());
         } catch (MalformedURLException | IllegalArgumentException | URISyntaxException var3) {
            Util.LOGGER.error("Couldn't open uri '{}'", var1, var3);
         }
      }

      public String telemetryName() {
         return this.telemetryName;
      }
   }
}
