package net.minecraft.data.info;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockListReport implements DataProvider {
   private final PackOutput output;
   private final CompletableFuture<HolderLookup.Provider> registries;

   public BlockListReport(final PackOutput output, final CompletableFuture<HolderLookup.Provider> registries) {
      super();
      this.output = output;
      this.registries = registries;
   }

   public CompletableFuture<?> run(final CachedOutput cache) {
      Path path = this.output.getOutputFolder(PackOutput.Target.REPORTS).resolve("blocks.json");
      return this.registries.thenCompose((registries) -> {
         JsonObject root = new JsonObject();
         registries.lookupOrThrow(Registries.BLOCK).listElements().forEach((block) -> {
            JsonObject entry = new JsonObject();
            StateDefinition<Block, BlockState> definition = ((Block)block.value()).getStateDefinition();
            if (!definition.getProperties().isEmpty()) {
               JsonObject properties = new JsonObject();

               for(Property<?> property : definition.getProperties()) {
                  JsonArray values = new JsonArray();

                  for(Comparable<?> value : property.getPossibleValues()) {
                     values.add(Util.getPropertyName(property, value));
                  }

                  properties.add(property.getName(), values);
               }

               entry.add("properties", properties);
            }

            JsonArray protocol = new JsonArray();

            JsonObject stateEntry;
            for(Iterator i$ = definition.getPossibleStates().iterator(); i$.hasNext(); protocol.add(stateEntry)) {
               BlockState state = (BlockState)i$.next();
               stateEntry = new JsonObject();
               JsonObject properties = new JsonObject();

               for(Property<?> property : definition.getProperties()) {
                  properties.addProperty(property.getName(), Util.getPropertyName(property, state.getValue(property)));
               }

               if (!properties.isEmpty()) {
                  stateEntry.add("properties", properties);
               }

               stateEntry.addProperty("id", Block.getId(state));
               if (state == ((Block)block.value()).defaultBlockState()) {
                  stateEntry.addProperty("default", true);
               }
            }

            entry.add("states", protocol);
            String id = block.getRegisteredName();
            root.add(id, entry);
         });
         return DataProvider.saveStable(cache, root, path);
      });
   }

   public final String getName() {
      return "Block List";
   }
}
