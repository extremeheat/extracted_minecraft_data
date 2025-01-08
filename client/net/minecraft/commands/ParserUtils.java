package net.minecraft.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.CharPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;

public class ParserUtils {
   public ParserUtils() {
      super();
   }

   public static <T> T parseSnbtWithCodec(Codec<T> var0, HolderLookup.Provider var1, DynamicCommandExceptionType var2, StringReader var3) throws CommandSyntaxException {
      int var4 = var3.getCursor();
      Tag var5 = (new TagParser(var3)).readValue();
      DataResult var6 = var0.parse(var1.createSerializationContext(NbtOps.INSTANCE), var5);
      return (T)var6.getOrThrow((var3x) -> {
         var3.setCursor(var4);
         return var2.createWithContext(var3, var3x);
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
