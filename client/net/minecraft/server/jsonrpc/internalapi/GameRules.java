package net.minecraft.server.jsonrpc.internalapi;

public interface GameRules {
   <T extends net.minecraft.world.level.GameRules.Value<T>> T getRule(net.minecraft.world.level.GameRules.Key<T> var1);
}
