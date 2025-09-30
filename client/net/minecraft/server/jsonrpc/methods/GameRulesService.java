package net.minecraft.server.jsonrpc.methods;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.GameRules;

public class GameRulesService {
   public GameRulesService() {
      super();
   }

   public static List<TypedRule> get(MinecraftApi var0) {
      List var1 = var0.gameRuleService().getAvailableGameRules().map(Map.Entry::getKey).toList();
      ArrayList var2 = new ArrayList();

      for(GameRules.Key var4 : var1) {
         GameRules.Value var5 = var0.gameRuleService().getRule(var4);
         var2.add(getTypedRule(var0, var4.getId(), var5));
      }

      return var2;
   }

   public static TypedRule getTypedRule(MinecraftApi var0, String var1, GameRules.Value<?> var2) {
      return var0.gameRuleService().getTypedRule(var1, var2);
   }

   public static TypedRule update(MinecraftApi var0, UntypedRule var1, ClientInfo var2) {
      return var0.gameRuleService().updateGameRule(var1, var2);
   }

   public static enum RuleType implements StringRepresentable {
      INT("integer"),
      BOOL("boolean");

      private final String name;

      private RuleType(final String var3) {
         this.name = var3;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static RuleType[] $values() {
         return new RuleType[]{INT, BOOL};
      }
   }

   public static record TypedRule(String key, String value, RuleType type) {
      public static final MapCodec<TypedRule> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.STRING.fieldOf("key").forGetter(TypedRule::key), Codec.STRING.fieldOf("value").forGetter(TypedRule::value), StringRepresentable.fromEnum(RuleType::values).fieldOf("type").forGetter(TypedRule::type)).apply(var0, TypedRule::new));

      public TypedRule(String var1, String var2, RuleType var3) {
         super();
         this.key = var1;
         this.value = var2;
         this.type = var3;
      }
   }

   public static record UntypedRule(String key, String value) {
      public static final MapCodec<UntypedRule> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.STRING.fieldOf("key").forGetter(UntypedRule::key), Codec.STRING.fieldOf("value").forGetter(UntypedRule::value)).apply(var0, UntypedRule::new));

      public UntypedRule(String var1, String var2) {
         super();
         this.key = var1;
         this.value = var2;
      }
   }
}
