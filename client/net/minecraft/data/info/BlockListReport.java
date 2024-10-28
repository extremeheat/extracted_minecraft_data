package net.minecraft.data.info;

import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockTypes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockListReport implements DataProvider {
   private final PackOutput output;
   private final CompletableFuture<HolderLookup.Provider> registries;

   public BlockListReport(PackOutput var1, CompletableFuture<HolderLookup.Provider> var2) {
      super();
      this.output = var1;
      this.registries = var2;
   }

   public CompletableFuture<?> run(CachedOutput var1) {
      Path var2 = this.output.getOutputFolder(PackOutput.Target.REPORTS).resolve("blocks.json");
      return this.registries.thenCompose((var2x) -> {
         JsonObject var3 = new JsonObject();
         RegistryOps var4 = var2x.createSerializationContext(JsonOps.INSTANCE);
         var2x.lookupOrThrow(Registries.BLOCK).listElements().forEach((var2xx) -> {
            JsonObject var3x = new JsonObject();
            StateDefinition var4x = ((Block)var2xx.value()).getStateDefinition();
            if (!var4x.getProperties().isEmpty()) {
               JsonObject var5 = new JsonObject();
               Iterator var6 = var4x.getProperties().iterator();

               while(var6.hasNext()) {
                  Property var7 = (Property)var6.next();
                  JsonArray var8 = new JsonArray();
                  Iterator var9 = var7.getPossibleValues().iterator();

                  while(var9.hasNext()) {
                     Comparable var10 = (Comparable)var9.next();
                     var8.add(Util.getPropertyName(var7, var10));
                  }

                  var5.add(var7.getName(), var8);
               }

               var3x.add("properties", var5);
            }

            JsonArray var12 = new JsonArray();

            JsonObject var17;
            for(UnmodifiableIterator var13 = var4x.getPossibleStates().iterator(); var13.hasNext(); var12.add(var17)) {
               BlockState var15 = (BlockState)var13.next();
               var17 = new JsonObject();
               JsonObject var18 = new JsonObject();
               Iterator var19 = var4x.getProperties().iterator();

               while(var19.hasNext()) {
                  Property var11 = (Property)var19.next();
                  var18.addProperty(var11.getName(), Util.getPropertyName(var11, var15.getValue(var11)));
               }

               if (var18.size() > 0) {
                  var17.add("properties", var18);
               }

               var17.addProperty("id", Block.getId(var15));
               if (var15 == ((Block)var2xx.value()).defaultBlockState()) {
                  var17.addProperty("default", true);
               }
            }

            var3x.add("states", var12);
            String var14 = var2xx.getRegisteredName();
            JsonElement var16 = (JsonElement)BlockTypes.CODEC.codec().encodeStart(var4, (Block)var2xx.value()).getOrThrow((var1) -> {
               return new AssertionError("Failed to serialize block " + var14 + " (is type registered in BlockTypes?): " + var1);
            });
            var3x.add("definition", var16);
            var3.add(var14, var3x);
         });
         return DataProvider.saveStable(var1, var3, var2);
      });
   }

   public final String getName() {
      return "Block List";
   }
}
