package net.minecraft.server.jsonrpc.methods;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleType;

public class GameRulesService {
   public GameRulesService() {
      super();
   }

   public static List<GameRuleUpdate<?>> get(final MinecraftApi minecraftApi) {
      List<GameRuleUpdate<?>> rules = new ArrayList();
      minecraftApi.gameRuleService().getAvailableGameRules().forEach((gameRule) -> addGameRule(minecraftApi, gameRule, rules));
      return rules;
   }

   private static <T> void addGameRule(final MinecraftApi minecraftApi, final GameRule<T> gameRule, final List<GameRuleUpdate<?>> rules) {
      T value = (T)minecraftApi.gameRuleService().getRuleValue(gameRule);
      rules.add(getTypedRule(minecraftApi, gameRule, value));
   }

   public static <T> GameRuleUpdate<T> getTypedRule(final MinecraftApi minecraftApi, final GameRule<T> gameRule, final T value) {
      return minecraftApi.gameRuleService().<T>getTypedRule(gameRule, value);
   }

   public static <T> GameRuleUpdate<T> update(final MinecraftApi minecraftApi, final GameRuleUpdate<T> update, final ClientInfo clientInfo) {
      return minecraftApi.gameRuleService().<T>updateGameRule(update, clientInfo);
   }

   public static record GameRuleUpdate<T>(GameRule<T> gameRule, T value) {
      public static final Codec<GameRuleUpdate<?>> TYPED_CODEC;
      public static final Codec<GameRuleUpdate<?>> CODEC;

      public GameRuleUpdate {
         super();
      }

      private static <T> MapCodec<? extends GameRuleUpdate<T>> getValueCodec(final GameRule<T> gameRule) {
         return gameRule.valueCodec().fieldOf("value").xmap((value) -> new GameRuleUpdate(gameRule, value), GameRuleUpdate::value);
      }

      private static <T> MapCodec<? extends GameRuleUpdate<T>> getValueAndTypeCodec(final GameRule<T> gameRule) {
         return RecordCodecBuilder.mapCodec((i) -> i.group(StringRepresentable.fromEnum(GameRuleType::values).fieldOf("type").forGetter((r) -> r.gameRule.gameRuleType()), gameRule.valueCodec().fieldOf("value").forGetter(GameRuleUpdate::value)).apply(i, (type, value) -> getUntypedRule(gameRule, type, value)));
      }

      private static <T> GameRuleUpdate<T> getUntypedRule(final GameRule<T> gameRule, final GameRuleType readType, final T value) {
         if (gameRule.gameRuleType() != readType) {
            String var10002 = String.valueOf(readType);
            throw new InvalidParameterJsonRpcException("Stated type \"" + var10002 + "\" mismatches with actual type \"" + String.valueOf(gameRule.gameRuleType()) + "\" of gamerule \"" + gameRule.id() + "\"");
         } else {
            return new GameRuleUpdate<T>(gameRule, value);
         }
      }

      static {
         TYPED_CODEC = BuiltInRegistries.GAME_RULE.byNameCodec().dispatch("key", GameRuleUpdate::gameRule, GameRuleUpdate::getValueAndTypeCodec);
         CODEC = BuiltInRegistries.GAME_RULE.byNameCodec().dispatch("key", GameRuleUpdate::gameRule, GameRuleUpdate::getValueCodec);
      }
   }
}
