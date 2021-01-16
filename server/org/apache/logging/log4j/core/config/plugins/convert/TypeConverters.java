package org.apache.logging.log4j.core.config.plugins.convert;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Provider;
import java.security.Security;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.xml.bind.DatatypeConverter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.rolling.action.Duration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.util.CronExpression;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;

public final class TypeConverters {
   public static final String CATEGORY = "TypeConverter";
   private static final Logger LOGGER = StatusLogger.getLogger();

   public TypeConverters() {
      super();
   }

   public static <T> T convert(String var0, Class<? extends T> var1, Object var2) {
      TypeConverter var3 = TypeConverterRegistry.getInstance().findCompatibleConverter(var1);
      if (var0 == null) {
         return parseDefaultValue(var3, var2);
      } else {
         try {
            return var3.convert(var0);
         } catch (Exception var5) {
            LOGGER.warn((String)"Error while converting string [{}] to type [{}]. Using default value [{}].", (Object)var0, var1, var2, var5);
            return parseDefaultValue(var3, var2);
         }
      }
   }

   private static <T> T parseDefaultValue(TypeConverter<T> var0, Object var1) {
      if (var1 == null) {
         return null;
      } else if (!(var1 instanceof String)) {
         return var1;
      } else {
         try {
            return var0.convert((String)var1);
         } catch (Exception var3) {
            LOGGER.debug((String)"Can't parse default value [{}] for type [{}].", (Object)var1, var0.getClass(), var3);
            return null;
         }
      }
   }

   @Plugin(
      name = "UUID",
      category = "TypeConverter"
   )
   public static class UuidConverter implements TypeConverter<UUID> {
      public UuidConverter() {
         super();
      }

      public UUID convert(String var1) throws Exception {
         return UUID.fromString(var1);
      }
   }

   @Plugin(
      name = "URL",
      category = "TypeConverter"
   )
   public static class UrlConverter implements TypeConverter<URL> {
      public UrlConverter() {
         super();
      }

      public URL convert(String var1) throws MalformedURLException {
         return new URL(var1);
      }
   }

   @Plugin(
      name = "URI",
      category = "TypeConverter"
   )
   public static class UriConverter implements TypeConverter<URI> {
      public UriConverter() {
         super();
      }

      public URI convert(String var1) throws URISyntaxException {
         return new URI(var1);
      }
   }

   @Plugin(
      name = "String",
      category = "TypeConverter"
   )
   public static class StringConverter implements TypeConverter<String> {
      public StringConverter() {
         super();
      }

      public String convert(String var1) {
         return var1;
      }
   }

   @Plugin(
      name = "Short",
      category = "TypeConverter"
   )
   public static class ShortConverter implements TypeConverter<Short> {
      public ShortConverter() {
         super();
      }

      public Short convert(String var1) {
         return Short.valueOf(var1);
      }
   }

   @Plugin(
      name = "SecurityProvider",
      category = "TypeConverter"
   )
   public static class SecurityProviderConverter implements TypeConverter<Provider> {
      public SecurityProviderConverter() {
         super();
      }

      public Provider convert(String var1) {
         return Security.getProvider(var1);
      }
   }

   @Plugin(
      name = "Pattern",
      category = "TypeConverter"
   )
   public static class PatternConverter implements TypeConverter<Pattern> {
      public PatternConverter() {
         super();
      }

      public Pattern convert(String var1) {
         return Pattern.compile(var1);
      }
   }

   @Plugin(
      name = "Path",
      category = "TypeConverter"
   )
   public static class PathConverter implements TypeConverter<Path> {
      public PathConverter() {
         super();
      }

      public Path convert(String var1) throws Exception {
         return Paths.get(var1);
      }
   }

   @Plugin(
      name = "Long",
      category = "TypeConverter"
   )
   public static class LongConverter implements TypeConverter<Long> {
      public LongConverter() {
         super();
      }

      public Long convert(String var1) {
         return Long.valueOf(var1);
      }
   }

   @Plugin(
      name = "Level",
      category = "TypeConverter"
   )
   public static class LevelConverter implements TypeConverter<Level> {
      public LevelConverter() {
         super();
      }

      public Level convert(String var1) {
         return Level.valueOf(var1);
      }
   }

   @Plugin(
      name = "Integer",
      category = "TypeConverter"
   )
   public static class IntegerConverter implements TypeConverter<Integer> {
      public IntegerConverter() {
         super();
      }

      public Integer convert(String var1) {
         return Integer.valueOf(var1);
      }
   }

   @Plugin(
      name = "InetAddress",
      category = "TypeConverter"
   )
   public static class InetAddressConverter implements TypeConverter<InetAddress> {
      public InetAddressConverter() {
         super();
      }

      public InetAddress convert(String var1) throws Exception {
         return InetAddress.getByName(var1);
      }
   }

   @Plugin(
      name = "Float",
      category = "TypeConverter"
   )
   public static class FloatConverter implements TypeConverter<Float> {
      public FloatConverter() {
         super();
      }

      public Float convert(String var1) {
         return Float.valueOf(var1);
      }
   }

   @Plugin(
      name = "File",
      category = "TypeConverter"
   )
   public static class FileConverter implements TypeConverter<File> {
      public FileConverter() {
         super();
      }

      public File convert(String var1) {
         return new File(var1);
      }
   }

   @Plugin(
      name = "Duration",
      category = "TypeConverter"
   )
   public static class DurationConverter implements TypeConverter<Duration> {
      public DurationConverter() {
         super();
      }

      public Duration convert(String var1) {
         return Duration.parse(var1);
      }
   }

   @Plugin(
      name = "Double",
      category = "TypeConverter"
   )
   public static class DoubleConverter implements TypeConverter<Double> {
      public DoubleConverter() {
         super();
      }

      public Double convert(String var1) {
         return Double.valueOf(var1);
      }
   }

   @Plugin(
      name = "CronExpression",
      category = "TypeConverter"
   )
   public static class CronExpressionConverter implements TypeConverter<CronExpression> {
      public CronExpressionConverter() {
         super();
      }

      public CronExpression convert(String var1) throws Exception {
         return new CronExpression(var1);
      }
   }

   @Plugin(
      name = "Class",
      category = "TypeConverter"
   )
   public static class ClassConverter implements TypeConverter<Class<?>> {
      public ClassConverter() {
         super();
      }

      public Class<?> convert(String var1) throws ClassNotFoundException {
         String var2 = var1.toLowerCase();
         byte var3 = -1;
         switch(var2.hashCode()) {
         case -1325958191:
            if (var2.equals("double")) {
               var3 = 3;
            }
            break;
         case 104431:
            if (var2.equals("int")) {
               var3 = 5;
            }
            break;
         case 3039496:
            if (var2.equals("byte")) {
               var3 = 1;
            }
            break;
         case 3052374:
            if (var2.equals("char")) {
               var3 = 2;
            }
            break;
         case 3327612:
            if (var2.equals("long")) {
               var3 = 6;
            }
            break;
         case 3625364:
            if (var2.equals("void")) {
               var3 = 8;
            }
            break;
         case 64711720:
            if (var2.equals("boolean")) {
               var3 = 0;
            }
            break;
         case 97526364:
            if (var2.equals("float")) {
               var3 = 4;
            }
            break;
         case 109413500:
            if (var2.equals("short")) {
               var3 = 7;
            }
         }

         switch(var3) {
         case 0:
            return Boolean.TYPE;
         case 1:
            return Byte.TYPE;
         case 2:
            return Character.TYPE;
         case 3:
            return Double.TYPE;
         case 4:
            return Float.TYPE;
         case 5:
            return Integer.TYPE;
         case 6:
            return Long.TYPE;
         case 7:
            return Short.TYPE;
         case 8:
            return Void.TYPE;
         default:
            return LoaderUtil.loadClass(var1);
         }
      }
   }

   @Plugin(
      name = "Charset",
      category = "TypeConverter"
   )
   public static class CharsetConverter implements TypeConverter<Charset> {
      public CharsetConverter() {
         super();
      }

      public Charset convert(String var1) {
         return Charset.forName(var1);
      }
   }

   @Plugin(
      name = "CharacterArray",
      category = "TypeConverter"
   )
   public static class CharArrayConverter implements TypeConverter<char[]> {
      public CharArrayConverter() {
         super();
      }

      public char[] convert(String var1) {
         return var1.toCharArray();
      }
   }

   @Plugin(
      name = "Character",
      category = "TypeConverter"
   )
   public static class CharacterConverter implements TypeConverter<Character> {
      public CharacterConverter() {
         super();
      }

      public Character convert(String var1) {
         if (var1.length() != 1) {
            throw new IllegalArgumentException("Character string must be of length 1: " + var1);
         } else {
            return var1.toCharArray()[0];
         }
      }
   }

   @Plugin(
      name = "Byte",
      category = "TypeConverter"
   )
   public static class ByteConverter implements TypeConverter<Byte> {
      public ByteConverter() {
         super();
      }

      public Byte convert(String var1) {
         return Byte.valueOf(var1);
      }
   }

   @Plugin(
      name = "ByteArray",
      category = "TypeConverter"
   )
   public static class ByteArrayConverter implements TypeConverter<byte[]> {
      private static final String PREFIX_0x = "0x";
      private static final String PREFIX_BASE64 = "Base64:";

      public ByteArrayConverter() {
         super();
      }

      public byte[] convert(String var1) {
         byte[] var2;
         if (var1 != null && !var1.isEmpty()) {
            String var3;
            if (var1.startsWith("Base64:")) {
               var3 = var1.substring("Base64:".length());
               var2 = DatatypeConverter.parseBase64Binary(var3);
            } else if (var1.startsWith("0x")) {
               var3 = var1.substring("0x".length());
               var2 = DatatypeConverter.parseHexBinary(var3);
            } else {
               var2 = var1.getBytes(Charset.defaultCharset());
            }
         } else {
            var2 = new byte[0];
         }

         return var2;
      }
   }

   @Plugin(
      name = "Boolean",
      category = "TypeConverter"
   )
   public static class BooleanConverter implements TypeConverter<Boolean> {
      public BooleanConverter() {
         super();
      }

      public Boolean convert(String var1) {
         return Boolean.valueOf(var1);
      }
   }

   @Plugin(
      name = "BigInteger",
      category = "TypeConverter"
   )
   public static class BigIntegerConverter implements TypeConverter<BigInteger> {
      public BigIntegerConverter() {
         super();
      }

      public BigInteger convert(String var1) {
         return new BigInteger(var1);
      }
   }

   @Plugin(
      name = "BigDecimal",
      category = "TypeConverter"
   )
   public static class BigDecimalConverter implements TypeConverter<BigDecimal> {
      public BigDecimalConverter() {
         super();
      }

      public BigDecimal convert(String var1) {
         return new BigDecimal(var1);
      }
   }
}
