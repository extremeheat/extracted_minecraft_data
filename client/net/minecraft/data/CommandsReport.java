package net.minecraft.data;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.datafix.DataFixesManager;

public class CommandsReport implements IDataProvider {
   private final DataGenerator field_200400_a;

   public CommandsReport(DataGenerator var1) {
      super();
      this.field_200400_a = var1;
   }

   public void func_200398_a(DirectoryCache var1) throws IOException {
      YggdrasilAuthenticationService var2 = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
      MinecraftSessionService var3 = var2.createMinecraftSessionService();
      GameProfileRepository var4 = var2.createProfileRepository();
      File var5 = new File(this.field_200400_a.func_200391_b().toFile(), "tmp");
      PlayerProfileCache var6 = new PlayerProfileCache(var4, new File(var5, MinecraftServer.field_152367_a.getName()));
      DedicatedServer var7 = new DedicatedServer(var5, DataFixesManager.func_210901_a(), var2, var3, var4, var6);
      var7.func_195571_aL().func_200378_a(this.field_200400_a.func_200391_b().resolve("reports/commands.json").toFile());
   }

   public String func_200397_b() {
      return "Command Syntax";
   }
}
