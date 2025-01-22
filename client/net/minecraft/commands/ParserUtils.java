package net.minecraft.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import net.minecraft.CharPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.RegistryOps;

public class ParserUtils {
   public ParserUtils() {
      super();
   }

   public static <T> T parseSnbtWithCodec(Codec<T> var0, HolderLookup.Provider var1, DynamicCommandExceptionType var2, StringReader var3) throws CommandSyntaxException {
      return (T)parseSnbtWithCodec(JavaOps.INSTANCE, var0, var1, var2, var3);
   }

   public static <T, O> T parseSnbtWithCodec(DynamicOps<O> var0, Codec<T> var1, HolderLookup.Provider var2, DynamicCommandExceptionType var3, StringReader var4) throws CommandSyntaxException {
      int var5 = var4.getCursor();
      RegistryOps var6 = var2.createSerializationContext(var0);
      Object var7 = TagParser.parseAsArgument(var6, var4);
      DataResult var8 = var1.parse(var6, var7);
      return (T)var8.getOrThrow((var3x) -> {
         var4.setCursor(var5);
         return var3.createWithContext(var4, var3x);
      });
   }

   public static String readWhile(StringReader var0, CharPredicate var1) {
      int var2 = var0.getCursor();

      while(var0.canRead() && var1.test(var0.peek())) {
         var0.skip();
      }

      return var0.getString().substring(var2, var0.getCursor());
   }
}
