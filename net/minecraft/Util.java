package net.minecraft;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import it.unimi.dsi.fastutil.Hash.Strategy;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {
   private static final AtomicInteger WORKER_COUNT = new AtomicInteger(1);
   private static final ExecutorService BACKGROUND_EXECUTOR = makeBackgroundExecutor();
   public static LongSupplier timeSource = System::nanoTime;
   private static final Logger LOGGER = LogManager.getLogger();

   public static Collector toMap() {
      return Collectors.toMap(Entry::getKey, Entry::getValue);
   }

   public static String getPropertyName(Property var0, Object var1) {
      return var0.getName((Comparable)var1);
   }

   public static String makeDescriptionId(String var0, @Nullable ResourceLocation var1) {
      return var1 == null ? var0 + ".unregistered_sadface" : var0 + '.' + var1.getNamespace() + '.' + var1.getPath().replace('/', '.');
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

   private static ExecutorService makeBackgroundExecutor() {
      int var0 = Mth.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, 7);
      Object var1;
      if (var0 <= 0) {
         var1 = MoreExecutors.newDirectExecutorService();
      } else {
         var1 = new ForkJoinPool(var0, (var0x) -> {
            ForkJoinWorkerThread var1 = new ForkJoinWorkerThread(var0x) {
               protected void onTermination(Throwable var1) {
                  if (var1 != null) {
                     Util.LOGGER.warn("{} died", this.getName(), var1);
                  } else {
                     Util.LOGGER.debug("{} shutdown", this.getName());
                  }

                  super.onTermination(var1);
               }
            };
            var1.setName("Server-Worker-" + WORKER_COUNT.getAndIncrement());
            return var1;
         }, (var0x, var1x) -> {
            pauseInIde(var1x);
            if (var1x instanceof CompletionException) {
               var1x = var1x.getCause();
            }

            if (var1x instanceof ReportedException) {
               Bootstrap.realStdoutPrintln(((ReportedException)var1x).getReport().getFriendlyReport());
               System.exit(-1);
            }

            LOGGER.error(String.format("Caught exception in thread %s", var0x), var1x);
         }, true);
      }

      return (ExecutorService)var1;
   }

   public static Executor backgroundExecutor() {
      return BACKGROUND_EXECUTOR;
   }

   public static void shutdownBackgroundExecutor() {
      BACKGROUND_EXECUTOR.shutdown();

      boolean var0;
      try {
         var0 = BACKGROUND_EXECUTOR.awaitTermination(3L, TimeUnit.SECONDS);
      } catch (InterruptedException var2) {
         var0 = false;
      }

      if (!var0) {
         BACKGROUND_EXECUTOR.shutdownNow();
      }

   }

   public static CompletableFuture failedFuture(Throwable var0) {
      CompletableFuture var1 = new CompletableFuture();
      var1.completeExceptionally(var0);
      return var1;
   }

   public static void throwAsRuntime(Throwable var0) {
      throw var0 instanceof RuntimeException ? (RuntimeException)var0 : new RuntimeException(var0);
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

   public static Stream getVmArguments() {
      RuntimeMXBean var0 = ManagementFactory.getRuntimeMXBean();
      return var0.getInputArguments().stream().filter((var0x) -> {
         return var0x.startsWith("-X");
      });
   }

   public static Object lastOf(List var0) {
      return var0.get(var0.size() - 1);
   }

   public static Object findNextInIterable(Iterable var0, @Nullable Object var1) {
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

   public static Object findPreviousInIterable(Iterable var0, @Nullable Object var1) {
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

   public static Object make(Supplier var0) {
      return var0.get();
   }

   public static Object make(Object var0, Consumer var1) {
      var1.accept(var0);
      return var0;
   }

   public static Strategy identityStrategy() {
      return Util.IdentityStrategy.INSTANCE;
   }

   public static CompletableFuture sequence(List var0) {
      ArrayList var1 = Lists.newArrayListWithCapacity(var0.size());
      CompletableFuture[] var2 = new CompletableFuture[var0.size()];
      CompletableFuture var3 = new CompletableFuture();
      var0.forEach((var3x) -> {
         int var4 = var1.size();
         var1.add((Object)null);
         var2[var4] = var3x.whenComplete((var3xx, var4x) -> {
            if (var4x != null) {
               var3.completeExceptionally(var4x);
            } else {
               var1.set(var4, var3xx);
            }

         });
      });
      return CompletableFuture.allOf(var2).applyToEither(var3, (var1x) -> {
         return var1;
      });
   }

   public static Stream toStream(Optional var0) {
      return (Stream)DataFixUtils.orElseGet(var0.map(Stream::of), Stream::empty);
   }

   public static Optional ifElse(Optional var0, Consumer var1, Runnable var2) {
      if (var0.isPresent()) {
         var1.accept(var0.get());
      } else {
         var2.run();
      }

      return var0;
   }

   public static Runnable name(Runnable var0, Supplier var1) {
      return var0;
   }

   public static Optional readUUID(String var0, Dynamic var1) {
      return var1.get(var0 + "Most").asNumber().flatMap((var2) -> {
         return var1.get(var0 + "Least").asNumber().map((var1x) -> {
            return new UUID(var2.longValue(), var1x.longValue());
         });
      });
   }

   public static Dynamic writeUUID(String var0, UUID var1, Dynamic var2) {
      return var2.set(var0 + "Most", var2.createLong(var1.getMostSignificantBits())).set(var0 + "Least", var2.createLong(var1.getLeastSignificantBits()));
   }

   public static Throwable pauseInIde(Throwable var0) {
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         LOGGER.error("Trying to throw a fatal exception, pausing in IDE", var0);

         while(true) {
            try {
               Thread.sleep(1000L);
               LOGGER.error("paused");
            } catch (InterruptedException var2) {
               return var0;
            }
         }
      } else {
         return var0;
      }
   }

   public static String describeError(Throwable var0) {
      if (var0.getCause() != null) {
         return describeError(var0.getCause());
      } else {
         return var0.getMessage() != null ? var0.getMessage() : var0.toString();
      }
   }

   static enum IdentityStrategy implements Strategy {
      INSTANCE;

      public int hashCode(Object var1) {
         return System.identityHashCode(var1);
      }

      public boolean equals(Object var1, Object var2) {
         return var1 == var2;
      }
   }

   public static enum OS {
      LINUX,
      SOLARIS,
      WINDOWS {
         protected String[] getOpenUrlArguments(URL var1) {
            return new String[]{"rundll32", "url.dll,FileProtocolHandler", var1.toString()};
         }
      },
      OSX {
         protected String[] getOpenUrlArguments(URL var1) {
            return new String[]{"open", var1.toString()};
         }
      },
      UNKNOWN;

      private OS() {
      }

      public void openUrl(URL var1) {
         try {
            Process var2 = (Process)AccessController.doPrivileged(() -> {
               return Runtime.getRuntime().exec(this.getOpenUrlArguments(var1));
            });
            Iterator var3 = IOUtils.readLines(var2.getErrorStream()).iterator();

            while(var3.hasNext()) {
               String var4 = (String)var3.next();
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
            this.openUrl((new URI(var1)).toURL());
         } catch (MalformedURLException | IllegalArgumentException | URISyntaxException var3) {
            Util.LOGGER.error("Couldn't open uri '{}'", var1, var3);
         }

      }

      // $FF: synthetic method
      OS(Object var3) {
         this();
      }
   }
}
