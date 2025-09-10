package net.minecraft.server.jsonrpc.internalapi;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.jsonrpc.JsonRpcLogger;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.jsonrpc.methods.GameRulesService;
import net.minecraft.server.jsonrpc.methods.InvalidParameterJsonRpcException;
import net.minecraft.world.flag.FeatureFlagSet;

public class MinecraftGameRuleServiceImpl implements MinecraftGameRuleService {
   private final DedicatedServer server;
   private final JsonRpcLogger jsonrpcLogger;

   public MinecraftGameRuleServiceImpl(DedicatedServer var1, JsonRpcLogger var2) {
      super();
      this.server = var1;
      this.jsonrpcLogger = var2;
   }

   public GameRulesService.TypedRule updateGameRule(GameRulesService.UntypedRule var1, ClientInfo var2) {
      net.minecraft.world.level.GameRules.Value var3 = this.getRuleValue(var1.key());
      String var4 = var3.serialize();
      if (var3 instanceof net.minecraft.world.level.GameRules.BooleanValue var6) {
         var6.set(Boolean.parseBoolean(var1.value()), this.server);
      } else {
         if (!(var3 instanceof net.minecraft.world.level.GameRules.IntegerValue)) {
            throw new InvalidParameterJsonRpcException("Unknown rule type for key: " + var1.key());
         }

         net.minecraft.world.level.GameRules.IntegerValue var5 = (net.minecraft.world.level.GameRules.IntegerValue)var3;
         var5.set(Integer.parseInt(var1.value()), this.server);
      }

      GameRulesService.TypedRule var7 = this.getTypedRule(var1.key(), var3);
      this.jsonrpcLogger.log(var2, "Game rule '{}' updated from '{}' to '{}'", var7.key(), var4, var7.value());
      this.server.onGameRuleChanged(var1.key(), var3);
      return var7;
   }

   public <T extends net.minecraft.world.level.GameRules.Value<T>> T getRule(net.minecraft.world.level.GameRules.Key<T> var1) {
      return (T)this.server.getGameRules().getRule(var1);
   }

   public GameRulesService.TypedRule getTypedRule(String var1, net.minecraft.world.level.GameRules.Value<?> var2) {
      Objects.requireNonNull(var2);
      byte var4 = 0;
      GameRulesService.TypedRule var10000;
      //$FF: var4->value
      //0->net/minecraft/world/level/GameRules$BooleanValue
      //1->net/minecraft/world/level/GameRules$IntegerValue
      switch (var2.typeSwitch<invokedynamic>(var2, var4)) {
         case 0:
            net.minecraft.world.level.GameRules.BooleanValue var5 = (net.minecraft.world.level.GameRules.BooleanValue)var2;
            var10000 = new GameRulesService.TypedRule(var1, String.valueOf(var5.get()), GameRulesService.RuleType.BOOL);
            break;
         case 1:
            net.minecraft.world.level.GameRules.IntegerValue var6 = (net.minecraft.world.level.GameRules.IntegerValue)var2;
            var10000 = new GameRulesService.TypedRule(var1, String.valueOf(var6.get()), GameRulesService.RuleType.INT);
            break;
         default:
            throw new InvalidParameterJsonRpcException("Unknown rule type");
      }

      return var10000;
   }

   public Stream<Map.Entry<net.minecraft.world.level.GameRules.Key<?>, net.minecraft.world.level.GameRules.Type<?>>> getAvailableGameRules() {
      FeatureFlagSet var1 = this.server.getWorldData().getLevelSettings().getDataConfiguration().enabledFeatures();
      return net.minecraft.world.level.GameRules.availableRules(var1);
   }

   private Optional<net.minecraft.world.level.GameRules.Key<?>> getRuleKey(String var1) {
      Stream var2 = this.getAvailableGameRules();
      return var2.filter((var1x) -> ((net.minecraft.world.level.GameRules.Key)var1x.getKey()).getId().equals(var1)).findFirst().map(Map.Entry::getKey);
   }

   private net.minecraft.world.level.GameRules.Value<?> getRuleValue(String var1) {
      net.minecraft.world.level.GameRules.Key var2 = (net.minecraft.world.level.GameRules.Key)this.getRuleKey(var1).orElseThrow(() -> new InvalidParameterJsonRpcException("Game rule '" + var1 + "' does not exist"));
      return this.server.getGameRules().getRule(var2);
   }
}
