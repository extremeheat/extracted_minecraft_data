package net.minecraft.server.dedicated;

import com.google.common.base.MoreObjects;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.core.RegistryAccess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Settings<T extends Settings<T>> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Properties properties;

   public Settings(Properties var1) {
      super();
      this.properties = var1;
   }

   public static Properties loadFromFile(Path var0) {
      Properties var1 = new Properties();

      try {
         InputStream var2 = Files.newInputStream(var0);
         Throwable var3 = null;

         try {
            var1.load(var2);
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  var2.close();
               }
            }

         }
      } catch (IOException var15) {
         LOGGER.error("Failed to load properties from file: {}", var0);
      }

      return var1;
   }

   public void store(Path var1) {
      try {
         OutputStream var2 = Files.newOutputStream(var1);
         Throwable var3 = null;

         try {
            this.properties.store(var2, "Minecraft server properties");
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  var2.close();
               }
            }

         }
      } catch (IOException var15) {
         LOGGER.error("Failed to store properties to file: {}", var1);
      }

   }

   private static <V extends Number> Function<String, V> wrapNumberDeserializer(Function<String, V> var0) {
      return (var1) -> {
         try {
            return (Number)var0.apply(var1);
         } catch (NumberFormatException var3) {
            return null;
         }
      };
   }

   protected static <V> Function<String, V> dispatchNumberOrString(IntFunction<V> var0, Function<String, V> var1) {
      return (var2) -> {
         try {
            return var0.apply(Integer.parseInt(var2));
         } catch (NumberFormatException var4) {
            return var1.apply(var2);
         }
      };
   }

   @Nullable
   private String getStringRaw(String var1) {
      return (String)this.properties.get(var1);
   }

   @Nullable
   protected <V> V getLegacy(String var1, Function<String, V> var2) {
      String var3 = this.getStringRaw(var1);
      if (var3 == null) {
         return null;
      } else {
         this.properties.remove(var1);
         return var2.apply(var3);
      }
   }

   protected <V> V get(String var1, Function<String, V> var2, Function<V, String> var3, V var4) {
      String var5 = this.getStringRaw(var1);
      Object var6 = MoreObjects.firstNonNull(var5 != null ? var2.apply(var5) : null, var4);
      this.properties.put(var1, var3.apply(var6));
      return var6;
   }

   protected <V> Settings<T>.MutableValue<V> getMutable(String var1, Function<String, V> var2, Function<V, String> var3, V var4) {
      String var5 = this.getStringRaw(var1);
      Object var6 = MoreObjects.firstNonNull(var5 != null ? var2.apply(var5) : null, var4);
      this.properties.put(var1, var3.apply(var6));
      return new Settings.MutableValue(var1, var6, var3);
   }

   protected <V> V get(String var1, Function<String, V> var2, UnaryOperator<V> var3, Function<V, String> var4, V var5) {
      return this.get(var1, (var2x) -> {
         Object var3x = var2.apply(var2x);
         return var3x != null ? var3.apply(var3x) : null;
      }, var4, var5);
   }

   protected <V> V get(String var1, Function<String, V> var2, V var3) {
      return this.get(var1, var2, Objects::toString, var3);
   }

   protected <V> Settings<T>.MutableValue<V> getMutable(String var1, Function<String, V> var2, V var3) {
      return this.getMutable(var1, var2, Objects::toString, var3);
   }

   protected String get(String var1, String var2) {
      return (String)this.get(var1, Function.identity(), Function.identity(), var2);
   }

   @Nullable
   protected String getLegacyString(String var1) {
      return (String)this.getLegacy(var1, Function.identity());
   }

   protected int get(String var1, int var2) {
      return (Integer)this.get(var1, wrapNumberDeserializer(Integer::parseInt), var2);
   }

   protected Settings<T>.MutableValue<Integer> getMutable(String var1, int var2) {
      return this.getMutable(var1, wrapNumberDeserializer(Integer::parseInt), var2);
   }

   protected int get(String var1, UnaryOperator<Integer> var2, int var3) {
      return (Integer)this.get(var1, wrapNumberDeserializer(Integer::parseInt), var2, Objects::toString, var3);
   }

   protected long get(String var1, long var2) {
      return (Long)this.get(var1, wrapNumberDeserializer(Long::parseLong), var2);
   }

   protected boolean get(String var1, boolean var2) {
      return (Boolean)this.get(var1, Boolean::valueOf, var2);
   }

   protected Settings<T>.MutableValue<Boolean> getMutable(String var1, boolean var2) {
      return this.getMutable(var1, Boolean::valueOf, var2);
   }

   @Nullable
   protected Boolean getLegacyBoolean(String var1) {
      return (Boolean)this.getLegacy(var1, Boolean::valueOf);
   }

   protected Properties cloneProperties() {
      Properties var1 = new Properties();
      var1.putAll(this.properties);
      return var1;
   }

   protected abstract T reload(RegistryAccess var1, Properties var2);

   public class MutableValue<V> implements Supplier<V> {
      private final String key;
      private final V value;
      private final Function<V, String> serializer;

      private MutableValue(String var2, V var3, Function<V, String> var4) {
         super();
         this.key = var2;
         this.value = var3;
         this.serializer = var4;
      }

      public V get() {
         return this.value;
      }

      public T update(RegistryAccess var1, V var2) {
         Properties var3 = Settings.this.cloneProperties();
         var3.put(this.key, this.serializer.apply(var2));
         return Settings.this.reload(var1, var3);
      }

      // $FF: synthetic method
      MutableValue(String var2, Object var3, Function var4, Object var5) {
         this(var2, var3, var4);
      }
   }
}
