package net.minecraft.util.datafix;

import com.google.gson.JsonObject;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.GsonHelper;

public class ComponentDataFixUtils {
   private static final String EMPTY_CONTENTS = createTextComponentJson("");

   public ComponentDataFixUtils() {
      super();
   }

   public static <T> Dynamic<T> createPlainTextComponent(DynamicOps<T> var0, String var1) {
      String var2 = createTextComponentJson(var1);
      return new Dynamic(var0, var0.createString(var2));
   }

   public static <T> Dynamic<T> createEmptyComponent(DynamicOps<T> var0) {
      return new Dynamic(var0, var0.createString(EMPTY_CONTENTS));
   }

   private static String createTextComponentJson(String var0) {
      JsonObject var1 = new JsonObject();
      var1.addProperty("text", var0);
      return GsonHelper.toStableString(var1);
   }

   public static <T> Dynamic<T> createTranslatableComponent(DynamicOps<T> var0, String var1) {
      JsonObject var2 = new JsonObject();
      var2.addProperty("translate", var1);
      return new Dynamic(var0, var0.createString(GsonHelper.toStableString(var2)));
   }

   public static <T> Dynamic<T> wrapLiteralStringAsComponent(Dynamic<T> var0) {
      return (Dynamic<T>)DataFixUtils.orElse(var0.asString().map(var1 -> createPlainTextComponent(var0.getOps(), var1)).result(), var0);
   }
}
