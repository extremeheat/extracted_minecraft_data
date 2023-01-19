package net.minecraft.data.info;

import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockListReport implements DataProvider {
   private final DataGenerator generator;

   public BlockListReport(DataGenerator var1) {
      super();
      this.generator = var1;
   }

   @Override
   public void run(CachedOutput var1) throws IOException {
      JsonObject var2 = new JsonObject();

      for(Block var4 : Registry.BLOCK) {
         ResourceLocation var5 = Registry.BLOCK.getKey(var4);
         JsonObject var6 = new JsonObject();
         StateDefinition var7 = var4.getStateDefinition();
         if (!var7.getProperties().isEmpty()) {
            JsonObject var8 = new JsonObject();

            for(Property var10 : var7.getProperties()) {
               JsonArray var11 = new JsonArray();

               for(Comparable var13 : var10.getPossibleValues()) {
                  var11.add(Util.getPropertyName(var10, var13));
               }

               var8.add(var10.getName(), var11);
            }

            var6.add("properties", var8);
         }

         JsonArray var16 = new JsonArray();

         JsonObject var19;
         for(UnmodifiableIterator var17 = var7.getPossibleStates().iterator(); var17.hasNext(); var16.add(var19)) {
            BlockState var18 = (BlockState)var17.next();
            var19 = new JsonObject();
            JsonObject var20 = new JsonObject();

            for(Property var14 : var7.getProperties()) {
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

      Path var15 = this.generator.getOutputFolder(DataGenerator.Target.REPORTS).resolve("blocks.json");
      DataProvider.saveStable(var1, var2, var15);
   }

   @Override
   public String getName() {
      return "Block List";
   }
}
