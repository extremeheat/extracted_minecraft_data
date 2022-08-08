package net.minecraft.data.info;

import com.mojang.brigadier.CommandDispatcher;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.ArgumentUtils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;

public class CommandsReport implements DataProvider {
   private final DataGenerator generator;

   public CommandsReport(DataGenerator var1) {
      super();
      this.generator = var1;
   }

   public void run(CachedOutput var1) throws IOException {
      Path var2 = this.generator.getOutputFolder(DataGenerator.Target.REPORTS).resolve("commands.json");
      CommandDispatcher var3 = (new Commands(Commands.CommandSelection.ALL, new CommandBuildContext((RegistryAccess)RegistryAccess.BUILTIN.get()))).getDispatcher();
      DataProvider.saveStable(var1, ArgumentUtils.serializeNodeToJson(var3, var3.getRoot()), var2);
   }

   public String getName() {
      return "Command Syntax";
   }
}
