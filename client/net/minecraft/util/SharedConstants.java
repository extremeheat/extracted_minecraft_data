package net.minecraft.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.types.constant.NamespacedStringType;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import net.minecraft.command.TranslatableExceptionProvider;
import net.minecraft.util.datafix.NamespacedSchema;

public class SharedConstants {
   public static final Level field_184877_a;
   public static boolean field_206244_b;
   public static final char[] field_71567_b;

   public static boolean func_71566_a(char var0) {
      return var0 != 167 && var0 >= ' ' && var0 != 127;
   }

   public static String func_71565_a(String var0) {
      StringBuilder var1 = new StringBuilder();
      char[] var2 = var0.toCharArray();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         char var5 = var2[var4];
         if (func_71566_a(var5)) {
            var1.append(var5);
         }
      }

      return var1.toString();
   }

   static {
      field_184877_a = Level.DISABLED;
      field_71567_b = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '"', ':'};
      ResourceLeakDetector.setLevel(field_184877_a);
      CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES = false;
      CommandSyntaxException.BUILT_IN_EXCEPTIONS = new TranslatableExceptionProvider();
      NamespacedStringType.ENSURE_NAMESPACE = NamespacedSchema::func_206477_f;
   }
}
