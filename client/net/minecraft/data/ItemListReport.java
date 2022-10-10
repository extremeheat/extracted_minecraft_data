package net.minecraft.data;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class ItemListReport implements IDataProvider {
   private final DataGenerator field_200401_a;

   public ItemListReport(DataGenerator var1) {
      super();
      this.field_200401_a = var1;
   }

   public void func_200398_a(DirectoryCache var1) throws IOException {
      JsonObject var2 = new JsonObject();
      Iterator var3 = IRegistry.field_212630_s.iterator();

      while(var3.hasNext()) {
         Item var4 = (Item)var3.next();
         ResourceLocation var5 = IRegistry.field_212630_s.func_177774_c(var4);
         JsonObject var6 = new JsonObject();
         var6.addProperty("protocol_id", Item.func_150891_b(var4));
         var2.add(var5.toString(), var6);
      }

      Path var16 = this.field_200401_a.func_200391_b().resolve("reports/items.json");
      Files.createDirectories(var16.getParent());
      BufferedWriter var17 = Files.newBufferedWriter(var16, StandardCharsets.UTF_8);
      Throwable var18 = null;

      try {
         String var19 = (new GsonBuilder()).setPrettyPrinting().create().toJson(var2);
         var17.write(var19);
      } catch (Throwable var14) {
         var18 = var14;
         throw var14;
      } finally {
         if (var17 != null) {
            if (var18 != null) {
               try {
                  var17.close();
               } catch (Throwable var13) {
                  var18.addSuppressed(var13);
               }
            } else {
               var17.close();
            }
         }

      }

   }

   public String func_200397_b() {
      return "Item List";
   }
}
