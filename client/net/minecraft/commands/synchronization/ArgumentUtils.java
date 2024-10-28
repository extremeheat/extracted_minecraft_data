package net.minecraft.commands.synchronization;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import org.slf4j.Logger;

public class ArgumentUtils {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final byte NUMBER_FLAG_MIN = 1;
   private static final byte NUMBER_FLAG_MAX = 2;

   public ArgumentUtils() {
      super();
   }

   public static int createNumberFlags(boolean var0, boolean var1) {
      int var2 = 0;
      if (var0) {
         var2 |= 1;
      }

      if (var1) {
         var2 |= 2;
      }

      return var2;
   }

   public static boolean numberHasMin(byte var0) {
      return (var0 & 1) != 0;
   }

   public static boolean numberHasMax(byte var0) {
      return (var0 & 2) != 0;
   }

   private static <A extends ArgumentType<?>> void serializeCap(JsonObject var0, ArgumentTypeInfo.Template<A> var1) {
      serializeCap(var0, var1.type(), var1);
   }

   private static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void serializeCap(JsonObject var0, ArgumentTypeInfo<A, T> var1, ArgumentTypeInfo.Template<A> var2) {
      var1.serializeToJson(var2, var0);
   }

   private static <T extends ArgumentType<?>> void serializeArgumentToJson(JsonObject var0, T var1) {
      ArgumentTypeInfo.Template var2 = ArgumentTypeInfos.unpack(var1);
      var0.addProperty("type", "argument");
      var0.addProperty("parser", BuiltInRegistries.COMMAND_ARGUMENT_TYPE.getKey(var2.type()).toString());
      JsonObject var3 = new JsonObject();
      serializeCap(var3, var2);
      if (var3.size() > 0) {
         var0.add("properties", var3);
      }

   }

   public static <S> JsonObject serializeNodeToJson(CommandDispatcher<S> var0, CommandNode<S> var1) {
      JsonObject var2 = new JsonObject();
      if (var1 instanceof RootCommandNode) {
         var2.addProperty("type", "root");
      } else if (var1 instanceof LiteralCommandNode) {
         var2.addProperty("type", "literal");
      } else if (var1 instanceof ArgumentCommandNode) {
         ArgumentCommandNode var3 = (ArgumentCommandNode)var1;
         serializeArgumentToJson(var2, var3.getType());
      } else {
         LOGGER.error("Could not serialize node {} ({})!", var1, var1.getClass());
         var2.addProperty("type", "unknown");
      }

      JsonObject var8 = new JsonObject();
      Iterator var4 = var1.getChildren().iterator();

      while(var4.hasNext()) {
         CommandNode var5 = (CommandNode)var4.next();
         var8.add(var5.getName(), serializeNodeToJson(var0, var5));
      }

      if (var8.size() > 0) {
         var2.add("children", var8);
      }

      if (var1.getCommand() != null) {
         var2.addProperty("executable", true);
      }

      if (var1.getRedirect() != null) {
         Collection var9 = var0.getPath(var1.getRedirect());
         if (!var9.isEmpty()) {
            JsonArray var10 = new JsonArray();
            Iterator var6 = var9.iterator();

            while(var6.hasNext()) {
               String var7 = (String)var6.next();
               var10.add(var7);
            }

            var2.add("redirect", var10);
         }
      }

      return var2;
   }

   public static <T> Set<ArgumentType<?>> findUsedArgumentTypes(CommandNode<T> var0) {
      Set var1 = Sets.newIdentityHashSet();
      HashSet var2 = Sets.newHashSet();
      findUsedArgumentTypes(var0, var2, var1);
      return var2;
   }

   private static <T> void findUsedArgumentTypes(CommandNode<T> var0, Set<ArgumentType<?>> var1, Set<CommandNode<T>> var2) {
      if (var2.add(var0)) {
         if (var0 instanceof ArgumentCommandNode) {
            ArgumentCommandNode var3 = (ArgumentCommandNode)var0;
            var1.add(var3.getType());
         }

         var0.getChildren().forEach((var2x) -> {
            findUsedArgumentTypes(var2x, var1, var2);
         });
         CommandNode var4 = var0.getRedirect();
         if (var4 != null) {
            findUsedArgumentTypes(var4, var1, var2);
         }

      }
   }
}
