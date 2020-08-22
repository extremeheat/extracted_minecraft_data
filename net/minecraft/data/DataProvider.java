package net.minecraft.data;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Objects;

public interface DataProvider {
   HashFunction SHA1 = Hashing.sha1();

   void run(HashCache var1) throws IOException;

   String getName();

   static void save(Gson var0, HashCache var1, JsonElement var2, Path var3) throws IOException {
      String var4 = var0.toJson(var2);
      String var5 = SHA1.hashUnencodedChars(var4).toString();
      if (!Objects.equals(var1.getHash(var3), var5) || !Files.exists(var3, new LinkOption[0])) {
         Files.createDirectories(var3.getParent());
         BufferedWriter var6 = Files.newBufferedWriter(var3);
         Throwable var7 = null;

         try {
            var6.write(var4);
         } catch (Throwable var16) {
            var7 = var16;
            throw var16;
         } finally {
            if (var6 != null) {
               if (var7 != null) {
                  try {
                     var6.close();
                  } catch (Throwable var15) {
                     var7.addSuppressed(var15);
                  }
               } else {
                  var6.close();
               }
            }

         }
      }

      var1.putNew(var3, var5);
   }
}
