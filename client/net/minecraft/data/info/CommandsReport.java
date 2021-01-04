package net.minecraft.data.info;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.brigadier.CommandDispatcher;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.server.level.progress.LoggerChunkProgressListener;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.datafix.DataFixers;

public class CommandsReport implements DataProvider {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
   private final DataGenerator generator;

   public CommandsReport(DataGenerator var1) {
      super();
      this.generator = var1;
   }

   public void run(HashCache var1) throws IOException {
      YggdrasilAuthenticationService var2 = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
      MinecraftSessionService var3 = var2.createMinecraftSessionService();
      GameProfileRepository var4 = var2.createProfileRepository();
      File var5 = new File(this.generator.getOutputFolder().toFile(), "tmp");
      GameProfileCache var6 = new GameProfileCache(var4, new File(var5, MinecraftServer.USERID_CACHE_FILE.getName()));
      DedicatedServerSettings var7 = new DedicatedServerSettings(Paths.get("server.properties"));
      DedicatedServer var8 = new DedicatedServer(var5, var7, DataFixers.getDataFixer(), var2, var3, var4, var6, LoggerChunkProgressListener::new, var7.getProperties().levelName);
      Path var9 = this.generator.getOutputFolder().resolve("reports/commands.json");
      CommandDispatcher var10 = var8.getCommands().getDispatcher();
      DataProvider.save(GSON, var1, ArgumentTypes.serializeNodeToJson(var10, var10.getRoot()), var9);
   }

   public String getName() {
      return "Command Syntax";
   }
}
