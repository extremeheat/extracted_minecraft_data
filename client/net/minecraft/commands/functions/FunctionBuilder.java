package net.minecraft.commands.functions;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

class FunctionBuilder<Runtime extends ExecutionCommandSource<Runtime>> {
   private @Nullable List<UnboundEntryAction<Runtime>> plainEntries = new ArrayList();
   private @Nullable List<MacroFunction.Entry<Runtime>> macroEntries;
   private final List<String> macroArguments = new ArrayList();

   FunctionBuilder() {
      super();
   }

   public void addCommand(final UnboundEntryAction<Runtime> command) {
      if (this.macroEntries != null) {
         this.macroEntries.add(new MacroFunction.PlainTextEntry(command));
      } else {
         this.plainEntries.add(command);
      }

   }

   private int getArgumentIndex(final String id) {
      int index = this.macroArguments.indexOf(id);
      if (index == -1) {
         index = this.macroArguments.size();
         this.macroArguments.add(id);
      }

      return index;
   }

   private IntList convertToIndices(final List<String> ids) {
      IntArrayList result = new IntArrayList(ids.size());

      for(String id : ids) {
         result.add(this.getArgumentIndex(id));
      }

      return result;
   }

   public void addMacro(final String command, final int line) {
      StringTemplate parseResults;
      try {
         parseResults = StringTemplate.fromString(command);
      } catch (Exception e) {
         throw new IllegalArgumentException("Can't parse function line " + line + ": '" + command + "'", e);
      }

      if (this.plainEntries != null) {
         this.macroEntries = new ArrayList(this.plainEntries.size() + 1);

         for(UnboundEntryAction<Runtime> plainEntry : this.plainEntries) {
            this.macroEntries.add(new MacroFunction.PlainTextEntry(plainEntry));
         }

         this.plainEntries = null;
      }

      this.macroEntries.add(new MacroFunction.MacroEntry(parseResults, this.convertToIndices(parseResults.variables())));
   }

   public CommandFunction<Runtime> build(final Identifier id, final CommandParser.Provider<Runtime> parserProvider) {
      return (CommandFunction<Runtime>)(this.macroEntries != null ? new MacroFunction(parserProvider, id, this.macroEntries, this.macroArguments) : new PlainTextFunction(id, this.plainEntries));
   }
}
