package net.minecraft.util;

import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.Hash.Strategy;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.state.IProperty;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {
   public static LongSupplier field_211180_a = System::nanoTime;
   private static final Logger field_195650_a = LogManager.getLogger();
   private static final Pattern field_209538_b = Pattern.compile(".*\\.|(?:CON|PRN|AUX|NUL|COM1|COM2|COM3|COM4|COM5|COM6|COM7|COM8|COM9|LPT1|LPT2|LPT3|LPT4|LPT5|LPT6|LPT7|LPT8|LPT9)(?:\\..*)?", 2);

   public static <K, V> Collector<Entry<? extends K, ? extends V>, ?, Map<K, V>> func_199749_a() {
      return Collectors.toMap(Entry::getKey, Entry::getValue);
   }

   public static <T extends Comparable<T>> String func_200269_a(IProperty<T> var0, Object var1) {
      return var0.func_177702_a((Comparable)var1);
   }

   public static String func_200697_a(String var0, @Nullable ResourceLocation var1) {
      return var1 == null ? var0 + ".unregistered_sadface" : var0 + '.' + var1.func_110624_b() + '.' + var1.func_110623_a().replace('/', '.');
   }

   public static long func_211177_b() {
      return func_211178_c() / 1000000L;
   }

   public static long func_211178_c() {
      return field_211180_a.getAsLong();
   }

   public static long func_211179_d() {
      return Instant.now().toEpochMilli();
   }

   public static Util.EnumOS func_110647_a() {
      String var0 = System.getProperty("os.name").toLowerCase(Locale.ROOT);
      if (var0.contains("win")) {
         return Util.EnumOS.WINDOWS;
      } else if (var0.contains("mac")) {
         return Util.EnumOS.OSX;
      } else if (var0.contains("solaris")) {
         return Util.EnumOS.SOLARIS;
      } else if (var0.contains("sunos")) {
         return Util.EnumOS.SOLARIS;
      } else if (var0.contains("linux")) {
         return Util.EnumOS.LINUX;
      } else {
         return var0.contains("unix") ? Util.EnumOS.LINUX : Util.EnumOS.UNKNOWN;
      }
   }

   public static Stream<String> func_211565_f() {
      RuntimeMXBean var0 = ManagementFactory.getRuntimeMXBean();
      return var0.getInputArguments().stream().filter((var0x) -> {
         return var0x.startsWith("-X");
      });
   }

   public static boolean func_209537_a(Path var0) {
      Path var1 = var0.normalize();
      return var1.equals(var0);
   }

   public static boolean func_209536_b(Path var0) {
      Iterator var1 = var0.iterator();

      Path var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (Path)var1.next();
      } while(!field_209538_b.matcher(var2.toString()).matches());

      return false;
   }

   public static Path func_209535_a(Path var0, String var1, String var2) {
      String var3 = var1 + var2;
      Path var4 = Paths.get(var3);
      if (var4.endsWith(var2)) {
         throw new InvalidPathException(var3, "empty resource name");
      } else {
         return var0.resolve(var4);
      }
   }

   @Nullable
   public static <V> V func_181617_a(FutureTask<V> var0, Logger var1) {
      try {
         var0.run();
         return var0.get();
      } catch (ExecutionException var3) {
         var1.fatal("Error executing task", var3);
      } catch (InterruptedException var4) {
         var1.fatal("Error executing task", var4);
      }

      return null;
   }

   public static <T> T func_184878_a(List<T> var0) {
      return var0.get(var0.size() - 1);
   }

   public static <T> T func_195647_a(Iterable<T> var0, @Nullable T var1) {
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

   public static <T> T func_195648_b(Iterable<T> var0, @Nullable T var1) {
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

   public static <T> T func_199748_a(Supplier<T> var0) {
      return var0.get();
   }

   public static <T> T func_200696_a(T var0, Consumer<T> var1) {
      var1.accept(var0);
      return var0;
   }

   public static <K> Strategy<K> func_212443_g() {
      return Util.IdentityStrategy.INSTANCE;
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

   public static enum EnumOS {
      LINUX,
      SOLARIS,
      WINDOWS {
         protected String[] func_195643_b(URL var1) {
            return new String[]{"rundll32", "url.dll,FileProtocolHandler", var1.toString()};
         }
      },
      OSX {
         protected String[] func_195643_b(URL var1) {
            return new String[]{"open", var1.toString()};
         }
      },
      UNKNOWN;

      private EnumOS() {
      }

      public void func_195639_a(URL var1) {
         try {
            Process var2 = (Process)AccessController.doPrivileged(() -> {
               return Runtime.getRuntime().exec(this.func_195643_b(var1));
            });
            Iterator var3 = IOUtils.readLines(var2.getErrorStream()).iterator();

            while(var3.hasNext()) {
               String var4 = (String)var3.next();
               Util.field_195650_a.error(var4);
            }

            var2.getInputStream().close();
            var2.getErrorStream().close();
            var2.getOutputStream().close();
         } catch (IOException | PrivilegedActionException var5) {
            Util.field_195650_a.error("Couldn't open url '{}'", var1, var5);
         }

      }

      public void func_195642_a(URI var1) {
         try {
            this.func_195639_a(var1.toURL());
         } catch (MalformedURLException var3) {
            Util.field_195650_a.error("Couldn't open uri '{}'", var1, var3);
         }

      }

      public void func_195641_a(File var1) {
         try {
            this.func_195639_a(var1.toURI().toURL());
         } catch (MalformedURLException var3) {
            Util.field_195650_a.error("Couldn't open file '{}'", var1, var3);
         }

      }

      protected String[] func_195643_b(URL var1) {
         String var2 = var1.toString();
         if ("file".equals(var1.getProtocol())) {
            var2 = var2.replace("file:", "file://");
         }

         return new String[]{"xdg-open", var2};
      }

      public void func_195640_a(String var1) {
         try {
            this.func_195639_a((new URI(var1)).toURL());
         } catch (MalformedURLException | IllegalArgumentException | URISyntaxException var3) {
            Util.field_195650_a.error("Couldn't open uri '{}'", var1, var3);
         }

      }

      // $FF: synthetic method
      EnumOS(Object var3) {
         this();
      }
   }
}
