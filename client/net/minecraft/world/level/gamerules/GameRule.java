package net.minecraft.world.level.gamerules;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Objects;
import java.util.function.ToIntFunction;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;

public final class GameRule<T> {
   private final GameRuleCategory category;
   private final GameRuleType gameRuleType;
   private final ArgumentType<T> argument;
   private final GameRules.VisitorCaller<T> visitorCaller;
   private final Codec<T> valueCodec;
   private final ToIntFunction<T> commandResultFunction;
   private final T defaultValue;
   private final FeatureFlagSet requiredFeatures;

   public GameRule(GameRuleCategory var1, GameRuleType var2, ArgumentType<T> var3, GameRules.VisitorCaller<T> var4, Codec<T> var5, ToIntFunction<T> var6, T var7, FeatureFlagSet var8) {
      super();
      this.category = var1;
      this.gameRuleType = var2;
      this.argument = var3;
      this.visitorCaller = var4;
      this.valueCodec = var5;
      this.commandResultFunction = var6;
      this.defaultValue = var7;
      this.requiredFeatures = var8;
   }

   public String toString() {
      return this.id();
   }

   public String id() {
      return this.getResourceLocation().toShortString();
   }

   public ResourceLocation getResourceLocation() {
      return (ResourceLocation)Objects.requireNonNull(BuiltInRegistries.GAME_RULE.getKey(this));
   }

   public String getDescriptionId() {
      return Util.makeDescriptionId("gamerule", this.getResourceLocation());
   }

   public String serialize(T var1) {
      return var1.toString();
   }

   public DataResult<T> deserialize(String var1) {
      try {
         StringReader var2 = new StringReader(var1);
         Object var3 = this.argument.parse(var2);
         return var2.canRead() ? DataResult.error(() -> "Failed to deserialize; trailing characters", var3) : DataResult.success(var3);
      } catch (CommandSyntaxException var4) {
         return DataResult.error(() -> "Failed to deserialize");
      }
   }

   public Class<T> valueClass() {
      return this.defaultValue.getClass();
   }

   public void callVisitor(GameRuleTypeVisitor var1) {
      this.visitorCaller.call(var1, this);
   }

   public int getCommandResult(T var1) {
      return this.commandResultFunction.applyAsInt(var1);
   }

   public GameRuleCategory category() {
      return this.category;
   }

   public GameRuleType gameRuleType() {
      return this.gameRuleType;
   }

   public ArgumentType<T> argument() {
      return this.argument;
   }

   public Codec<T> valueCodec() {
      return this.valueCodec;
   }

   public T defaultValue() {
      return this.defaultValue;
   }

   public FeatureFlagSet requiredFeatures() {
      return this.requiredFeatures;
   }
}
