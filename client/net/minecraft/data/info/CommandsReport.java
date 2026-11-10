package net.minecraft.data.info;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SuggestionBox;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

public class CommandsReport implements DataProvider {
   private final PackOutput output;

   public CommandsReport(final PackOutput output) {
      super();
      this.output = output;
   }

   public CompletableFuture<?> run(final CachedOutput cache) {
      Path path = this.output.getOutputFolder(PackOutput.Target.REPORTS).resolve("commands.json");
      SuggestionBox parseBox = Commands.createVanilla(Commands.CommandSelection.ALL, Commands.createValidationContext()).suggestionBox();
      return DataProvider.saveStable(cache, parseBox.dumpReport(), path);
   }

   public final String getName() {
      return "Command Syntax";
   }
}
