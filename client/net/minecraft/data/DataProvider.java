package net.minecraft.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.function.ToIntFunction;
import net.minecraft.Util;
import net.minecraft.util.GsonHelper;

public interface DataProvider {
   ToIntFunction<String> FIXED_ORDER_FIELDS = (ToIntFunction)Util.make(new Object2IntOpenHashMap(), (var0) -> {
      var0.put("type", 0);
      var0.put("parent", 1);
      var0.defaultReturnValue(2);
   });
   Comparator<String> KEY_COMPARATOR = Comparator.comparingInt(FIXED_ORDER_FIELDS).thenComparing((var0) -> {
      return var0;
   });

   void run(CachedOutput var1) throws IOException;

   String getName();

   static void saveStable(CachedOutput var0, JsonElement var1, Path var2) throws IOException {
      ByteArrayOutputStream var3 = new ByteArrayOutputStream();
      HashingOutputStream var4 = new HashingOutputStream(Hashing.sha1(), var3);
      OutputStreamWriter var5 = new OutputStreamWriter(var4, StandardCharsets.UTF_8);
      JsonWriter var6 = new JsonWriter(var5);
      var6.setSerializeNulls(false);
      var6.setIndent("  ");
      GsonHelper.writeValue(var6, var1, KEY_COMPARATOR);
      var6.close();
      var0.writeIfNeeded(var2, var3.toByteArray(), var4.hash());
   }
}
