package net.minecraft.commands.synchronization;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.permissions.PermissionCheck;
import net.minecraft.server.permissions.PermissionProviderCheck;
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

   private static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void serializeArgumentCap(JsonObject var0, ArgumentTypeInfo<A, T> var1, ArgumentTypeInfo.Template<A> var2) {
      var1.serializeToJson(var2, var0);
   }

   private static <T extends ArgumentType<?>> void serializeArgumentToJson(JsonObject var0, T var1) {
      ArgumentTypeInfo.Template var2 = ArgumentTypeInfos.unpack(var1);
      var0.addProperty("type", "argument");
      var0.addProperty("parser", String.valueOf(BuiltInRegistries.COMMAND_ARGUMENT_TYPE.getKey(var2.type())));
      JsonObject var3 = new JsonObject();
      serializeArgumentCap(var3, var2.type(), var2);
      if (!var3.isEmpty()) {
         var0.add("properties", var3);
      }

   }

   public static <S> JsonObject serializeNodeToJson(CommandDispatcher<S> var0, CommandNode<S> var1) {
      JsonObject var2 = new JsonObject();
      Objects.requireNonNull(var1);
      byte var4 = 0;
      //$FF: var4->value
      //0->com/mojang/brigadier/tree/RootCommandNode
      //1->com/mojang/brigadier/tree/LiteralCommandNode
      //2->com/mojang/brigadier/tree/ArgumentCommandNode
      switch (var1.typeSwitch<invokedynamic>(var1, var4)) {
         case 0:
            RootCommandNode var5 = (RootCommandNode)var1;
            var2.addProperty("type", "root");
            break;
         case 1:
            LiteralCommandNode var6 = (LiteralCommandNode)var1;
            var2.addProperty("type", "literal");
            break;
         case 2:
            ArgumentCommandNode var7 = (ArgumentCommandNode)var1;
            serializeArgumentToJson(var2, var7.getType());
            break;
         default:
            LOGGER.error("Could not serialize node {} ({})!", var1, var1.getClass());
            var2.addProperty("type", "unknown");
      }

      Collection var3 = var1.getChildren();
      if (!var3.isEmpty()) {
         JsonObject var8 = new JsonObject();

         for(CommandNode var15 : var3) {
            var8.add(var15.getName(), serializeNodeToJson(var0, var15));
         }

         var2.add("children", var8);
      }

      if (var1.getCommand() != null) {
         var2.addProperty("executable", true);
      }

      Predicate var12 = var1.getRequirement();
      if (var12 instanceof PermissionProviderCheck var9) {
         JsonElement var13 = (JsonElement)PermissionCheck.CODEC.encodeStart(JsonOps.INSTANCE, var9.test()).getOrThrow((var0x) -> new IllegalStateException("Failed to serialize requirement: " + var0x));
         var2.add("permissions", var13);
      }

      if (var1.getRedirect() != null) {
         Collection var10 = var0.getPath(var1.getRedirect());
         if (!var10.isEmpty()) {
            JsonArray var14 = new JsonArray();

            for(String var17 : var10) {
               var14.add(var17);
            }

            var2.add("redirect", var14);
         }
      }

      return var2;
   }

   public static <T> Set<ArgumentType<?>> findUsedArgumentTypes(CommandNode<T> var0) {
      ReferenceOpenHashSet var1 = new ReferenceOpenHashSet();
      HashSet var2 = new HashSet();
      findUsedArgumentTypes(var0, var2, var1);
      return var2;
   }

   private static <T> void findUsedArgumentTypes(CommandNode<T> var0, Set<ArgumentType<?>> var1, Set<CommandNode<T>> var2) {
      if (var2.add(var0)) {
         if (var0 instanceof ArgumentCommandNode) {
            ArgumentCommandNode var3 = (ArgumentCommandNode)var0;
            var1.add(var3.getType());
         }

         var0.getChildren().forEach((var2x) -> findUsedArgumentTypes(var2x, var1, var2));
         CommandNode var4 = var0.getRedirect();
         if (var4 != null) {
            findUsedArgumentTypes(var4, var1, var2);
         }

      }
   }
}
