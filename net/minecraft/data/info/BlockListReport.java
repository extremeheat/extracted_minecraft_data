package net.minecraft.data.info;

import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockListReport implements DataProvider {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final DataGenerator generator;

   public BlockListReport(DataGenerator var1) {
      this.generator = var1;
   }

   public void run(HashCache var1) throws IOException {
      JsonObject var2 = new JsonObject();
      Iterator var3 = Registry.BLOCK.iterator();

      while(var3.hasNext()) {
         Block var4 = (Block)var3.next();
         ResourceLocation var5 = Registry.BLOCK.getKey(var4);
         JsonObject var6 = new JsonObject();
         StateDefinition var7 = var4.getStateDefinition();
         if (!var7.getProperties().isEmpty()) {
            JsonObject var8 = new JsonObject();
            Iterator var9 = var7.getProperties().iterator();

            while(true) {
               if (!var9.hasNext()) {
                  var6.add("properties", var8);
                  break;
               }

               Property var10 = (Property)var9.next();
               JsonArray var11 = new JsonArray();
               Iterator var12 = var10.getPossibleValues().iterator();

               while(var12.hasNext()) {
                  Comparable var13 = (Comparable)var12.next();
                  var11.add(Util.getPropertyName(var10, var13));
               }

               var8.add(var10.getName(), var11);
            }
         }

         JsonArray var16 = new JsonArray();

         JsonObject var19;
         for(UnmodifiableIterator var17 = var7.getPossibleStates().iterator(); var17.hasNext(); var16.add(var19)) {
            BlockState var18 = (BlockState)var17.next();
            var19 = new JsonObject();
            JsonObject var20 = new JsonObject();
            Iterator var21 = var7.getProperties().iterator();

            while(var21.hasNext()) {
               Property var14 = (Property)var21.next();
               var20.addProperty(var14.getName(), Util.getPropertyName(var14, var18.getValue(var14)));
            }

            if (var20.size() > 0) {
               var19.add("properties", var20);
            }

            var19.addProperty("id", Block.getId(var18));
            if (var18 == var4.defaultBlockState()) {
               var19.addProperty("default", true);
            }
         }

         var6.add("states", var16);
         var2.add(var5.toString(), var6);
      }

      Path var15 = this.generator.getOutputFolder().resolve("reports/blocks.json");
      DataProvider.save(GSON, var1, var2, var15);
   }

   public String getName() {
      return "Block List";
   }
}
