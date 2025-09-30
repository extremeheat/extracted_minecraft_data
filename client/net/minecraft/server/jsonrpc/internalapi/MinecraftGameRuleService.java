package net.minecraft.server.jsonrpc.internalapi;

import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.jsonrpc.methods.GameRulesService;

public interface MinecraftGameRuleService {
   GameRulesService.TypedRule updateGameRule(GameRulesService.UntypedRule var1, ClientInfo var2);

   <T extends net.minecraft.world.level.GameRules.Value<T>> T getRule(net.minecraft.world.level.GameRules.Key<T> var1);

   GameRulesService.TypedRule getTypedRule(String var1, net.minecraft.world.level.GameRules.Value<?> var2);

   Stream<Map.Entry<net.minecraft.world.level.GameRules.Key<?>, net.minecraft.world.level.GameRules.Type<?>>> getAvailableGameRules();
}
