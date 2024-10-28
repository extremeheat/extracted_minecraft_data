package net.minecraft.commands.functions;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.resources.ResourceLocation;

class FunctionBuilder<T extends ExecutionCommandSource<T>> {
   @Nullable
   private List<UnboundEntryAction<T>> plainEntries = new ArrayList();
   @Nullable
   private List<MacroFunction.Entry<T>> macroEntries;
   private final List<String> macroArguments = new ArrayList();

   FunctionBuilder() {
      super();
   }

   public void addCommand(UnboundEntryAction<T> var1) {
      if (this.macroEntries != null) {
         this.macroEntries.add(new MacroFunction.PlainTextEntry(var1));
      } else {
         this.plainEntries.add(var1);
      }

   }

   private int getArgumentIndex(String var1) {
      int var2 = this.macroArguments.indexOf(var1);
      if (var2 == -1) {
         var2 = this.macroArguments.size();
         this.macroArguments.add(var1);
      }

      return var2;
   }

   private IntList convertToIndices(List<String> var1) {
      IntArrayList var2 = new IntArrayList(var1.size());
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         var2.add(this.getArgumentIndex(var4));
      }

      return var2;
   }

   public void addMacro(String var1, int var2, T var3) {
      StringTemplate var4 = StringTemplate.fromString(var1, var2);
      if (this.plainEntries != null) {
         this.macroEntries = new ArrayList(this.plainEntries.size() + 1);
         Iterator var5 = this.plainEntries.iterator();

         while(var5.hasNext()) {
            UnboundEntryAction var6 = (UnboundEntryAction)var5.next();
            this.macroEntries.add(new MacroFunction.PlainTextEntry(var6));
         }

         this.plainEntries = null;
      }

      this.macroEntries.add(new MacroFunction.MacroEntry(var4, this.convertToIndices(var4.variables()), var3));
   }

   public CommandFunction<T> build(ResourceLocation var1) {
      return (CommandFunction)(this.macroEntries != null ? new MacroFunction(var1, this.macroEntries, this.macroArguments) : new PlainTextFunction(var1, this.plainEntries));
   }
}
