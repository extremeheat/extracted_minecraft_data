package net.minecraft.data;

import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.IRegistry;

public class BlockListReport implements IDataProvider {
   private final DataGenerator field_200399_a;

   public BlockListReport(DataGenerator var1) {
      super();
      this.field_200399_a = var1;
   }

   public void func_200398_a(DirectoryCache var1) throws IOException {
      JsonObject var2 = new JsonObject();
      Iterator var3 = IRegistry.field_212618_g.iterator();

      while(var3.hasNext()) {
         Block var4 = (Block)var3.next();
         ResourceLocation var5 = IRegistry.field_212618_g.func_177774_c(var4);
         JsonObject var6 = new JsonObject();
         StateContainer var7 = var4.func_176194_O();
         if (!var7.func_177623_d().isEmpty()) {
            JsonObject var8 = new JsonObject();
            Iterator var9 = var7.func_177623_d().iterator();

            while(true) {
               if (!var9.hasNext()) {
                  var6.add("properties", var8);
                  break;
               }

               IProperty var10 = (IProperty)var9.next();
               JsonArray var11 = new JsonArray();
               Iterator var12 = var10.func_177700_c().iterator();

               while(var12.hasNext()) {
                  Comparable var13 = (Comparable)var12.next();
                  var11.add(Util.func_200269_a(var10, var13));
               }

               var8.add(var10.func_177701_a(), var11);
            }
         }

         JsonArray var28 = new JsonArray();

         JsonObject var31;
         for(UnmodifiableIterator var29 = var7.func_177619_a().iterator(); var29.hasNext(); var28.add(var31)) {
            IBlockState var30 = (IBlockState)var29.next();
            var31 = new JsonObject();
            JsonObject var32 = new JsonObject();
            Iterator var33 = var7.func_177623_d().iterator();

            while(var33.hasNext()) {
               IProperty var14 = (IProperty)var33.next();
               var32.addProperty(var14.func_177701_a(), Util.func_200269_a(var14, var30.func_177229_b(var14)));
            }

            if (var32.size() > 0) {
               var31.add("properties", var32);
            }

            var31.addProperty("id", Block.func_196246_j(var30));
            if (var30 == var4.func_176223_P()) {
               var31.addProperty("default", true);
            }
         }

         var6.add("states", var28);
         var2.add(var5.toString(), var6);
      }

      Path var24 = this.field_200399_a.func_200391_b().resolve("reports/blocks.json");
      Files.createDirectories(var24.getParent());
      BufferedWriter var25 = Files.newBufferedWriter(var24, StandardCharsets.UTF_8);
      Throwable var26 = null;

      try {
         String var27 = (new GsonBuilder()).setPrettyPrinting().create().toJson(var2);
         var25.write(var27);
      } catch (Throwable var22) {
         var26 = var22;
         throw var22;
      } finally {
         if (var25 != null) {
            if (var26 != null) {
               try {
                  var25.close();
               } catch (Throwable var21) {
                  var26.addSuppressed(var21);
               }
            } else {
               var25.close();
            }
         }

      }

   }

   public String func_200397_b() {
      return "Block List";
   }
}
