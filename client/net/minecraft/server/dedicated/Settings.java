package net.minecraft.server.dedicated;

import com.google.common.base.MoreObjects;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.minecraft.core.RegistryAccess;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public abstract class Settings<T extends Settings<T>> {
   private static final Logger LOGGER = LogUtils.getLogger();
   protected final Properties properties;

   public Settings(final Properties properties) {
      super();
      this.properties = properties;
   }

   public static Properties loadFromFile(final Path file) {
      try {
         try {
            InputStream is = Files.newInputStream(file);

            Properties var13;
            try {
               CharsetDecoder reportingUtf8Decoder = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
               Properties properties = new Properties();
               properties.load(new InputStreamReader(is, reportingUtf8Decoder));
               var13 = properties;
            } catch (Throwable var8) {
               if (is != null) {
                  try {
                     is.close();
                  } catch (Throwable var6) {
                     var8.addSuppressed(var6);
                  }
               }

               throw var8;
            }

            if (is != null) {
               is.close();
            }

            return var13;
         } catch (CharacterCodingException var9) {
            LOGGER.info("Failed to load properties as UTF-8 from file {}, trying ISO_8859_1", file);
            Reader reader = Files.newBufferedReader(file, StandardCharsets.ISO_8859_1);

            Properties var4;
            try {
               Properties properties = new Properties();
               properties.load(reader);
               var4 = properties;
            } catch (Throwable var7) {
               if (reader != null) {
                  try {
                     reader.close();
                  } catch (Throwable var5) {
                     var7.addSuppressed(var5);
                  }
               }

               throw var7;
            }

            if (reader != null) {
               reader.close();
            }

            return var4;
         }
      } catch (IOException e) {
         LOGGER.error("Failed to load properties from file: {}", file, e);
         return new Properties();
      }
   }

   public void store(final Path output) {
      try {
         Writer os = Files.newBufferedWriter(output, StandardCharsets.UTF_8);

         try {
            this.properties.store(os, "Minecraft server properties");
         } catch (Throwable var6) {
            if (os != null) {
               try {
                  os.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (os != null) {
            os.close();
         }
      } catch (IOException var7) {
         LOGGER.error("Failed to store properties to file: {}", output);
      }

   }

   private static <V extends Number> Function<String, @Nullable V> wrapNumberDeserializer(final Function<String, V> inner) {
      return (s) -> {
         try {
            return (Number)inner.apply(s);
         } catch (NumberFormatException var3) {
            return null;
         }
      };
   }

   protected static <V> Function<String, @Nullable V> dispatchNumberOrString(final IntFunction<@Nullable V> intDeserializer, final Function<String, @Nullable V> stringDeserializer) {
      return (s) -> {
         try {
            return intDeserializer.apply(Integer.parseInt(s));
         } catch (NumberFormatException var4) {
            return stringDeserializer.apply(s);
         }
      };
   }

   private @Nullable String getStringRaw(final String key) {
      return (String)this.properties.get(key);
   }

   protected <V> @Nullable V getLegacy(final String key, final Function<String, V> deserializer) {
      String value = this.getStringRaw(key);
      if (value == null) {
         return null;
      } else {
         this.properties.remove(key);
         return (V)deserializer.apply(value);
      }
   }

   protected <V> V get(final String key, final Function<String, @Nullable V> deserializer, final Function<V, String> serializer, final V defaultValue) {
      String value = this.getStringRaw(key);
      V result = (V)MoreObjects.firstNonNull(value != null ? deserializer.apply(value) : null, defaultValue);
      this.properties.put(key, serializer.apply(result));
      return result;
   }

   protected <V> Settings<T>.MutableValue<V> getMutable(final String key, final Function<String, @Nullable V> deserializer, final Function<V, String> serializer, final V defaultValue) {
      String value = this.getStringRaw(key);
      V result = (V)MoreObjects.firstNonNull(value != null ? deserializer.apply(value) : null, defaultValue);
      this.properties.put(key, serializer.apply(result));
      return new MutableValue<V>(key, result, serializer);
   }

   protected <V> V get(final String key, final Function<String, @Nullable V> deserializer, final UnaryOperator<V> validator, final Function<V, String> serializer, final V defaultValue) {
      return (V)this.get(key, (s) -> {
         V result = (V)deserializer.apply(s);
         return result != null ? validator.apply(result) : null;
      }, serializer, defaultValue);
   }

   protected <V> V get(final String key, final Function<String, V> deserializer, final V defaultValue) {
      return (V)this.get(key, deserializer, Objects::toString, defaultValue);
   }

   protected <V> Settings<T>.MutableValue<V> getMutable(final String key, final Function<String, V> deserializer, final V defaultValue) {
      return this.<V>getMutable(key, deserializer, Objects::toString, defaultValue);
   }

   protected String get(final String key, final String defaultValue) {
      return (String)this.get(key, Function.identity(), Function.identity(), defaultValue);
   }

   protected @Nullable String getLegacyString(final String key) {
      return (String)this.getLegacy(key, Function.identity());
   }

   protected int get(final String key, final int defaultValue) {
      return (Integer)this.get(key, wrapNumberDeserializer(Integer::parseInt), defaultValue);
   }

   protected Settings<T>.MutableValue<Integer> getMutable(final String key, final int defaultValue) {
      return this.<Integer>getMutable(key, wrapNumberDeserializer(Integer::parseInt), defaultValue);
   }

   protected Settings<T>.MutableValue<String> getMutable(final String key, final String defaultValue) {
      return this.<String>getMutable(key, String::new, defaultValue);
   }

   protected int get(final String key, final UnaryOperator<Integer> validator, final int defaultValue) {
      return (Integer)this.get(key, wrapNumberDeserializer(Integer::parseInt), validator, Objects::toString, defaultValue);
   }

   protected long get(final String key, final long defaultValue) {
      return (Long)this.get(key, wrapNumberDeserializer(Long::parseLong), defaultValue);
   }

   protected boolean get(final String key, final boolean defaultValue) {
      return (Boolean)this.get(key, Boolean::valueOf, defaultValue);
   }

   protected Settings<T>.MutableValue<Boolean> getMutable(final String key, final boolean defaultValue) {
      return this.<Boolean>getMutable(key, Boolean::valueOf, defaultValue);
   }

   protected @Nullable Boolean getLegacyBoolean(final String key) {
      return (Boolean)this.getLegacy(key, Boolean::valueOf);
   }

   protected Properties cloneProperties() {
      Properties result = new Properties();
      result.putAll(this.properties);
      return result;
   }

   protected abstract T reload(final RegistryAccess registryAccess, final Properties properties);

   public class MutableValue<V> implements Supplier<V> {
      private final String key;
      private final V value;
      private final Function<V, String> serializer;

      private MutableValue(final String key, final V value, final Function<V, String> serializer) {
         Objects.requireNonNull(Settings.this);
         super();
         this.key = key;
         this.value = value;
         this.serializer = serializer;
      }

      public V get() {
         return this.value;
      }

      public T update(final RegistryAccess registryAccess, final V value) {
         Properties properties = Settings.this.cloneProperties();
         properties.put(this.key, this.serializer.apply(value));
         return (T)Settings.this.reload(registryAccess, properties);
      }
   }
}
