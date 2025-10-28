package net.minecraft.server.jsonrpc.internalapi;

import java.util.stream.Stream;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.jsonrpc.JsonRpcLogger;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.jsonrpc.methods.GameRulesService;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;

public class MinecraftGameRuleServiceImpl implements MinecraftGameRuleService {
   private final DedicatedServer server;
   private final GameRules gameRules;
   private final JsonRpcLogger jsonrpcLogger;

   public MinecraftGameRuleServiceImpl(DedicatedServer var1, JsonRpcLogger var2) {
      super();
      this.server = var1;
      this.gameRules = var1.getWorldData().getGameRules();
      this.jsonrpcLogger = var2;
   }

   public <T> GameRulesService.GameRuleUpdate<T> updateGameRule(GameRulesService.GameRuleUpdate<T> var1, ClientInfo var2) {
      GameRule var3 = var1.gameRule();
      Object var4 = this.gameRules.get(var3);
      Object var5 = var1.value();
      this.gameRules.set(var3, var5, this.server);
      this.jsonrpcLogger.log(var2, "Game rule '{}' updated from '{}' to '{}'", var3.id(), var3.serialize(var4), var3.serialize(var5));
      return var1;
   }

   public <T> GameRulesService.GameRuleUpdate<T> getTypedRule(GameRule<T> var1, T var2) {
      return new GameRulesService.GameRuleUpdate<T>(var1, var2);
   }

   public Stream<GameRule<?>> getAvailableGameRules() {
      return this.gameRules.availableRules();
   }

   public <T> T getRuleValue(GameRule<T> var1) {
      return (T)this.gameRules.get(var1);
   }
}
