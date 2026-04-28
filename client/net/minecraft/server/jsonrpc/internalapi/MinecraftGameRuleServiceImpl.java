package net.minecraft.server.jsonrpc.internalapi;

import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.jsonrpc.JsonRpcLogger;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.jsonrpc.methods.GameRulesService;
import net.minecraft.server.notifications.NotificationManager;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;

public class MinecraftGameRuleServiceImpl implements MinecraftGameRuleService {
   private final NotificationManager notificationManager;
   private final JsonRpcLogger jsonrpcLogger;

   public MinecraftGameRuleServiceImpl(final NotificationManager notificationManager, final JsonRpcLogger jsonrpcLogger) {
      super();
      this.notificationManager = notificationManager;
      this.jsonrpcLogger = jsonrpcLogger;
   }

   private DedicatedServer server() {
      return (DedicatedServer)Objects.requireNonNull(this.notificationManager.server());
   }

   public <T> GameRulesService.GameRuleUpdate<T> updateGameRule(final GameRulesService.GameRuleUpdate<T> update, final ClientInfo clientInfo) {
      GameRule<T> gameRule = update.gameRule();
      MinecraftServer server = this.server();
      GameRules gameRules = server.getGameRules();
      T oldValue = (T)gameRules.get(gameRule);
      T newValue = update.value();
      gameRules.set(gameRule, newValue, server);
      this.jsonrpcLogger.log(clientInfo, "Game rule '{}' updated from '{}' to '{}'", gameRule.id(), gameRule.serialize(oldValue), gameRule.serialize(newValue));
      return update;
   }

   public <T> GameRulesService.GameRuleUpdate<T> getTypedRule(final GameRule<T> gameRule, final T value) {
      return new GameRulesService.GameRuleUpdate<T>(gameRule, value);
   }

   public Stream<GameRule<?>> getAvailableGameRules() {
      return this.server().getGameRules().availableRules();
   }

   public <T> T getRuleValue(final GameRule<T> gameRule) {
      return (T)this.server().getGameRules().get(gameRule);
   }
}
