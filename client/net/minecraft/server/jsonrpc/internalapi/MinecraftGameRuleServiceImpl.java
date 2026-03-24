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

   public MinecraftGameRuleServiceImpl(final DedicatedServer server, final JsonRpcLogger jsonrpcLogger) {
      super();
      this.server = server;
      this.gameRules = server.getGameRules();
      this.jsonrpcLogger = jsonrpcLogger;
   }

   public <T> GameRulesService.GameRuleUpdate<T> updateGameRule(final GameRulesService.GameRuleUpdate<T> update, final ClientInfo clientInfo) {
      GameRule<T> gameRule = update.gameRule();
      T oldValue = (T)this.gameRules.get(gameRule);
      T newValue = update.value();
      this.gameRules.set(gameRule, newValue, this.server);
      this.jsonrpcLogger.log(clientInfo, "Game rule '{}' updated from '{}' to '{}'", gameRule.id(), gameRule.serialize(oldValue), gameRule.serialize(newValue));
      return update;
   }

   public <T> GameRulesService.GameRuleUpdate<T> getTypedRule(final GameRule<T> gameRule, final T value) {
      return new GameRulesService.GameRuleUpdate<T>(gameRule, value);
   }

   public Stream<GameRule<?>> getAvailableGameRules() {
      return this.gameRules.availableRules();
   }

   public <T> T getRuleValue(final GameRule<T> gameRule) {
      return (T)this.gameRules.get(gameRule);
   }
}
