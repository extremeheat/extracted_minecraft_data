package net.minecraft.data.info;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;

public class CommandsReport implements DataProvider {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
   private final DataGenerator generator;

   public CommandsReport(DataGenerator var1) {
      super();
      this.generator = var1;
   }

   public void run(HashCache var1) throws IOException {
      Path var2 = this.generator.getOutputFolder().resolve("reports/commands.json");
      CommandDispatcher var3 = (new Commands(Commands.CommandSelection.ALL)).getDispatcher();
      DataProvider.save(GSON, var1, ArgumentTypes.serializeNodeToJson(var3, var3.getRoot()), var2);
   }

   public String getName() {
      return "Command Syntax";
   }
}
