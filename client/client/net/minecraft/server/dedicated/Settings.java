package net.minecraft.server.dedicated;

import com.google.common.base.MoreObjects;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import javax.annotation.Nullable;
import net.minecraft.core.RegistryAccess;
import org.slf4j.Logger;

public abstract class Settings<T extends Settings<T>> {
   private static final Logger LOGGER = LogUtils.getLogger();
   protected final Properties properties;

   public Settings(Properties var1) {
      super();
      this.properties = var1;
   }

   public static Properties loadFromFile(Path var0) {
      try {
         try {
            Properties var13;
            try (InputStream var1 = Files.newInputStream(var0)) {
               CharsetDecoder var11 = StandardCharsets.UTF_8
                  .newDecoder()
                  .onMalformedInput(CodingErrorAction.REPORT)
                  .onUnmappableCharacter(CodingErrorAction.REPORT);
               Properties var12 = new Properties();
               var12.load(new InputStreamReader(var1, var11));
               var13 = var12;
            }

            return var13;
         } catch (CharacterCodingException var9) {
            LOGGER.info("Failed to load properties as UTF-8 from file {}, trying ISO_8859_1", var0);

            Properties var4;
            try (BufferedReader var2 = Files.newBufferedReader(var0, StandardCharsets.ISO_8859_1)) {
               Properties var3 = new Properties();
               var3.load(var2);
               var4 = var3;
            }

            return var4;
         }
      } catch (IOException var10) {
         LOGGER.error("Failed to load properties from file: {}", var0, var10);
         return new Properties();
      }
   }

   public void store(Path var1) {
      try (BufferedWriter var2 = Files.newBufferedWriter(var1, StandardCharsets.UTF_8)) {
         this.properties.store(var2, "Minecraft server properties");
      } catch (IOException var7) {
         LOGGER.error("Failed to store properties to file: {}", var1);
      }
   }

   private static <V extends Number> Function<String, V> wrapNumberDeserializer(Function<String, V> var0) {
      return var1 -> {
         try {
            return (V)var0.apply(var1);
         } catch (NumberFormatException var3) {
            return null;
         }
      };
   }

   protected static <V> Function<String, V> dispatchNumberOrString(IntFunction<V> var0, Function<String, V> var1) {
      return var2 -> {
         try {
            return (V)var0.apply(Integer.parseInt(var2));
         } catch (NumberFormatException var4) {
            return (V)var1.apply(var2);
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
         return (V)var2.apply(var3);
      }
   }

   protected <V> V get(String var1, Function<String, V> var2, Function<V, String> var3, V var4) {
      String var5 = this.getStringRaw(var1);
      Object var6 = MoreObjects.firstNonNull(var5 != null ? var2.apply(var5) : null, var4);
      this.properties.put(var1, var3.apply(var6));
      return (V)var6;
   }

   protected <V> Settings<T>.MutableValue<V> getMutable(String var1, Function<String, V> var2, Function<V, String> var3, V var4) {
      String var5 = this.getStringRaw(var1);
      Object var6 = MoreObjects.firstNonNull(var5 != null ? var2.apply(var5) : null, var4);
      this.properties.put(var1, var3.apply(var6));
      return new Settings.MutableValue<>(var1, (V)var6, var3);
   }

   protected <V> V get(String var1, Function<String, V> var2, UnaryOperator<V> var3, Function<V, String> var4, V var5) {
      return this.get(var1, var2x -> {
         Object var3x = var2.apply(var2x);
         return (V)(var3x != null ? var3.apply(var3x) : null);
      }, var4, (V)var5);
   }

   protected <V> V get(String var1, Function<String, V> var2, V var3) {
      return this.get(var1, var2, Objects::toString, (V)var3);
   }

   protected <V> Settings<T>.MutableValue<V> getMutable(String var1, Function<String, V> var2, V var3) {
      return this.getMutable(var1, var2, Objects::toString, (V)var3);
   }

   protected String get(String var1, String var2) {
      return this.get(var1, Function.identity(), Function.identity(), var2);
   }

   @Nullable
   protected String getLegacyString(String var1) {
      return this.getLegacy(var1, Function.identity());
   }

   protected int get(String var1, int var2) {
      return this.get(var1, wrapNumberDeserializer(Integer::parseInt), Integer.valueOf(var2));
   }

   protected Settings<T>.MutableValue<Integer> getMutable(String var1, int var2) {
      return this.getMutable(var1, wrapNumberDeserializer(Integer::parseInt), var2);
   }

   protected int get(String var1, UnaryOperator<Integer> var2, int var3) {
      return this.get(var1, wrapNumberDeserializer(Integer::parseInt), var2, Objects::toString, var3);
   }

   protected long get(String var1, long var2) {
      return this.get(var1, wrapNumberDeserializer(Long::parseLong), var2);
   }

   protected boolean get(String var1, boolean var2) {
      return this.get(var1, Boolean::valueOf, var2);
   }

   protected Settings<T>.MutableValue<Boolean> getMutable(String var1, boolean var2) {
      return this.getMutable(var1, Boolean::valueOf, var2);
   }

   @Nullable
   protected Boolean getLegacyBoolean(String var1) {
      return this.getLegacy(var1, Boolean::valueOf);
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

      MutableValue(String var2, V var3, Function<V, String> var4) {
         super();
         this.key = var2;
         this.value = (V)var3;
         this.serializer = var4;
      }

      @Override
      public V get() {
         return this.value;
      }

      public T update(RegistryAccess var1, V var2) {
         Properties var3 = Settings.this.cloneProperties();
         var3.put(this.key, this.serializer.apply((V)var2));
         return Settings.this.reload(var1, var3);
      }
   }
}
