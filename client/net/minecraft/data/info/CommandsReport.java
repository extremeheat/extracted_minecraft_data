package net.minecraft.data.info;

import com.mojang.brigadier.CommandDispatcher;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.ArgumentUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

public class CommandsReport implements DataProvider {
   private final PackOutput output;
   private final CompletableFuture<HolderLookup.Provider> registries;

   public CommandsReport(PackOutput var1, CompletableFuture<HolderLookup.Provider> var2) {
      super();
      this.output = var1;
      this.registries = var2;
   }

   public CompletableFuture<?> run(CachedOutput var1) {
      Path var2 = this.output.getOutputFolder(PackOutput.Target.REPORTS).resolve("commands.json");
      return this.registries.thenCompose((var2x) -> {
         CommandDispatcher var3 = (new Commands(Commands.CommandSelection.ALL, Commands.createValidationContext(var2x))).getDispatcher();
         return DataProvider.saveStable(var1, ArgumentUtils.serializeNodeToJson(var3, var3.getRoot()), var2);
      });
   }

   public final String getName() {
      return "Command Syntax";
   }
}
