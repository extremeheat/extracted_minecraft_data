package net.minecraft.server.jsonrpc.methods;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleType;

public class GameRulesService {
   public GameRulesService() {
      super();
   }

   public static List<GameRuleUpdate<?>> get(MinecraftApi var0) {
      ArrayList var1 = new ArrayList();
      var0.gameRuleService().getAvailableGameRules().forEach((var2) -> addGameRule(var0, var2, var1));
      return var1;
   }

   private static <T> void addGameRule(MinecraftApi var0, GameRule<T> var1, List<GameRuleUpdate<?>> var2) {
      Object var3 = var0.gameRuleService().getRuleValue(var1);
      var2.add(getTypedRule(var0, var1, Objects.requireNonNull(var3)));
   }

   public static <T> GameRuleUpdate<T> getTypedRule(MinecraftApi var0, GameRule<T> var1, T var2) {
      return var0.gameRuleService().<T>getTypedRule(var1, var2);
   }

   public static <T> GameRuleUpdate<T> update(MinecraftApi var0, GameRuleUpdate<T> var1, ClientInfo var2) {
      return var0.gameRuleService().<T>updateGameRule(var1, var2);
   }

   public static record GameRuleUpdate<T>(GameRule<T> gameRule, T value) {
      public static final Codec<GameRuleUpdate<?>> TYPED_CODEC;
      public static final Codec<GameRuleUpdate<?>> CODEC;

      public GameRuleUpdate(GameRule<T> var1, T var2) {
         super();
         this.gameRule = var1;
         this.value = var2;
      }

      private static <T> MapCodec<? extends GameRuleUpdate<T>> getValueCodec(GameRule<T> var0) {
         return var0.valueCodec().fieldOf("value").xmap((var1) -> new GameRuleUpdate(var0, var1), GameRuleUpdate::value);
      }

      private static <T> MapCodec<? extends GameRuleUpdate<T>> getValueAndTypeCodec(GameRule<T> var0) {
         return RecordCodecBuilder.mapCodec((var1) -> var1.group(StringRepresentable.fromEnum(GameRuleType::values).fieldOf("type").forGetter((var0x) -> var0x.gameRule.gameRuleType()), var0.valueCodec().fieldOf("value").forGetter(GameRuleUpdate::value)).apply(var1, (var1x, var2) -> getUntypedRule(var0, var1x, var2)));
      }

      private static <T> GameRuleUpdate<T> getUntypedRule(GameRule<T> var0, GameRuleType var1, T var2) {
         if (var0.gameRuleType() != var1) {
            String var10002 = String.valueOf(var1);
            throw new InvalidParameterJsonRpcException("Stated type \"" + var10002 + "\" mismatches with actual type \"" + String.valueOf(var0.gameRuleType()) + "\" of gamerule \"" + var0.id() + "\"");
         } else {
            return new GameRuleUpdate<T>(var0, var2);
         }
      }

      static {
         TYPED_CODEC = BuiltInRegistries.GAME_RULE.byNameCodec().dispatch("key", GameRuleUpdate::gameRule, GameRuleUpdate::getValueAndTypeCodec);
         CODEC = BuiltInRegistries.GAME_RULE.byNameCodec().dispatch("key", GameRuleUpdate::gameRule, GameRuleUpdate::getValueCodec);
      }
   }
}
