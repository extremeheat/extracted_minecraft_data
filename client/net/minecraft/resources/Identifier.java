package net.minecraft.resources;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import java.util.function.UnaryOperator;
import net.minecraft.IdentifierException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.Nullable;

public final class Identifier implements Comparable<Identifier> {
   public static final Codec<Identifier> CODEC;
   public static final StreamCodec<ByteBuf, Identifier> STREAM_CODEC;
   public static final SimpleCommandExceptionType ERROR_INVALID;
   public static final char NAMESPACE_SEPARATOR = ':';
   public static final String DEFAULT_NAMESPACE = "minecraft";
   public static final String REALMS_NAMESPACE = "realms";
   private final String namespace;
   private final String path;

   private Identifier(String var1, String var2) {
      super();

      assert isValidNamespace(var1);

      assert isValidPath(var2);

      this.namespace = var1;
      this.path = var2;
   }

   private static Identifier createUntrusted(String var0, String var1) {
      return new Identifier(assertValidNamespace(var0, var1), assertValidPath(var0, var1));
   }

   public static Identifier fromNamespaceAndPath(String var0, String var1) {
      return createUntrusted(var0, var1);
   }

   public static Identifier parse(String var0) {
      return bySeparator(var0, ':');
   }

   public static Identifier withDefaultNamespace(String var0) {
      return new Identifier("minecraft", assertValidPath("minecraft", var0));
   }

   public static @Nullable Identifier tryParse(String var0) {
      return tryBySeparator(var0, ':');
   }

   public static @Nullable Identifier tryBuild(String var0, String var1) {
      return isValidNamespace(var0) && isValidPath(var1) ? new Identifier(var0, var1) : null;
   }

   public static Identifier bySeparator(String var0, char var1) {
      int var2 = var0.indexOf(var1);
      if (var2 >= 0) {
         String var3 = var0.substring(var2 + 1);
         if (var2 != 0) {
            String var4 = var0.substring(0, var2);
            return createUntrusted(var4, var3);
         } else {
            return withDefaultNamespace(var3);
         }
      } else {
         return withDefaultNamespace(var0);
      }
   }

   public static @Nullable Identifier tryBySeparator(String var0, char var1) {
      int var2 = var0.indexOf(var1);
      if (var2 >= 0) {
         String var3 = var0.substring(var2 + 1);
         if (!isValidPath(var3)) {
            return null;
         } else if (var2 != 0) {
            String var4 = var0.substring(0, var2);
            return isValidNamespace(var4) ? new Identifier(var4, var3) : null;
         } else {
            return new Identifier("minecraft", var3);
         }
      } else {
         return isValidPath(var0) ? new Identifier("minecraft", var0) : null;
      }
   }

   public static DataResult<Identifier> read(String var0) {
      try {
         return DataResult.success(parse(var0));
      } catch (IdentifierException var2) {
         return DataResult.error(() -> "Not a valid resource location: " + var0 + " " + var2.getMessage());
      }
   }

   public String getPath() {
      return this.path;
   }

   public String getNamespace() {
      return this.namespace;
   }

   public Identifier withPath(String var1) {
      return new Identifier(this.namespace, assertValidPath(this.namespace, var1));
   }

   public Identifier withPath(UnaryOperator<String> var1) {
      return this.withPath((String)var1.apply(this.path));
   }

   public Identifier withPrefix(String var1) {
      return this.withPath(var1 + this.path);
   }

   public Identifier withSuffix(String var1) {
      return this.withPath(this.path + var1);
   }

   public String toString() {
      return this.namespace + ":" + this.path;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Identifier)) {
         return false;
      } else {
         Identifier var2 = (Identifier)var1;
         return this.namespace.equals(var2.namespace) && this.path.equals(var2.path);
      }
   }

   public int hashCode() {
      return 31 * this.namespace.hashCode() + this.path.hashCode();
   }

   public int compareTo(Identifier var1) {
      int var2 = this.path.compareTo(var1.path);
      if (var2 == 0) {
         var2 = this.namespace.compareTo(var1.namespace);
      }

      return var2;
   }

   public String toDebugFileName() {
      return this.toString().replace('/', '_').replace(':', '_');
   }

   public String toLanguageKey() {
      return this.namespace + "." + this.path;
   }

   public String toShortLanguageKey() {
      return this.namespace.equals("minecraft") ? this.path : this.toLanguageKey();
   }

   public String toShortString() {
      return this.namespace.equals("minecraft") ? this.path : this.toString();
   }

   public String toLanguageKey(String var1) {
      return var1 + "." + this.toLanguageKey();
   }

   public String toLanguageKey(String var1, String var2) {
      return var1 + "." + this.toLanguageKey() + "." + var2;
   }

   private static String readGreedy(StringReader var0) {
      int var1 = var0.getCursor();

      while(var0.canRead() && isAllowedInIdentifier(var0.peek())) {
         var0.skip();
      }

      return var0.getString().substring(var1, var0.getCursor());
   }

   public static Identifier read(StringReader var0) throws CommandSyntaxException {
      int var1 = var0.getCursor();
      String var2 = readGreedy(var0);

      try {
         return parse(var2);
      } catch (IdentifierException var4) {
         var0.setCursor(var1);
         throw ERROR_INVALID.createWithContext(var0);
      }
   }

   public static Identifier readNonEmpty(StringReader var0) throws CommandSyntaxException {
      int var1 = var0.getCursor();
      String var2 = readGreedy(var0);
      if (var2.isEmpty()) {
         throw ERROR_INVALID.createWithContext(var0);
      } else {
         try {
            return parse(var2);
         } catch (IdentifierException var4) {
            var0.setCursor(var1);
            throw ERROR_INVALID.createWithContext(var0);
         }
      }
   }

   public static boolean isAllowedInIdentifier(char var0) {
      return var0 >= '0' && var0 <= '9' || var0 >= 'a' && var0 <= 'z' || var0 == '_' || var0 == ':' || var0 == '/' || var0 == '.' || var0 == '-';
   }

   public static boolean isValidPath(String var0) {
      for(int var1 = 0; var1 < var0.length(); ++var1) {
         if (!validPathChar(var0.charAt(var1))) {
            return false;
         }
      }

      return true;
   }

   public static boolean isValidNamespace(String var0) {
      for(int var1 = 0; var1 < var0.length(); ++var1) {
         if (!validNamespaceChar(var0.charAt(var1))) {
            return false;
         }
      }

      return true;
   }

   private static String assertValidNamespace(String var0, String var1) {
      if (!isValidNamespace(var0)) {
         throw new IdentifierException("Non [a-z0-9_.-] character in namespace of location: " + var0 + ":" + var1);
      } else {
         return var0;
      }
   }

   public static boolean validPathChar(char var0) {
      return var0 == '_' || var0 == '-' || var0 >= 'a' && var0 <= 'z' || var0 >= '0' && var0 <= '9' || var0 == '/' || var0 == '.';
   }

   private static boolean validNamespaceChar(char var0) {
      return var0 == '_' || var0 == '-' || var0 >= 'a' && var0 <= 'z' || var0 >= '0' && var0 <= '9' || var0 == '.';
   }

   private static String assertValidPath(String var0, String var1) {
      if (!isValidPath(var1)) {
         throw new IdentifierException("Non [a-z0-9/._-] character in path of location: " + var0 + ":" + var1);
      } else {
         return var1;
      }
   }

   // $FF: synthetic method
   public int compareTo(final Object var1) {
      return this.compareTo((Identifier)var1);
   }

   static {
      CODEC = Codec.STRING.comapFlatMap(Identifier::read, Identifier::toString).stable();
      STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(Identifier::parse, Identifier::toString);
      ERROR_INVALID = new SimpleCommandExceptionType(Component.translatable("argument.id.invalid"));
   }
}
