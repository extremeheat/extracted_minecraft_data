package net.minecraft.world.level;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameRules {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Map<GameRules.Key<?>, GameRules.Type<?>> GAME_RULE_TYPES = Maps.newTreeMap(Comparator.comparing((var0) -> {
      return var0.id;
   }));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOFIRETICK = register("doFireTick", GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_MOBGRIEFING = register("mobGriefing", GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_KEEPINVENTORY = register("keepInventory", GameRules.BooleanValue.create(false));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOMOBSPAWNING = register("doMobSpawning", GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOMOBLOOT = register("doMobLoot", GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOBLOCKDROPS = register("doTileDrops", GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOENTITYDROPS = register("doEntityDrops", GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_COMMANDBLOCKOUTPUT = register("commandBlockOutput", GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_NATURAL_REGENERATION = register("naturalRegeneration", GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DAYLIGHT = register("doDaylightCycle", GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_LOGADMINCOMMANDS = register("logAdminCommands", GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_SHOWDEATHMESSAGES = register("showDeathMessages", GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.IntegerValue> RULE_RANDOMTICKING = register("randomTickSpeed", GameRules.IntegerValue.create(3));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_SENDCOMMANDFEEDBACK = register("sendCommandFeedback", GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_REDUCEDDEBUGINFO = register("reducedDebugInfo", GameRules.BooleanValue.create(false, (var0, var1) -> {
      int var2 = var1.get() ? 22 : 23;
      Iterator var3 = var0.getPlayerList().getPlayers().iterator();

      while(var3.hasNext()) {
         ServerPlayer var4 = (ServerPlayer)var3.next();
         var4.connection.send(new ClientboundEntityEventPacket(var4, (byte)var2));
      }

   }));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_SPECTATORSGENERATECHUNKS = register("spectatorsGenerateChunks", GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.IntegerValue> RULE_SPAWN_RADIUS = register("spawnRadius", GameRules.IntegerValue.create(10));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DISABLE_ELYTRA_MOVEMENT_CHECK = register("disableElytraMovementCheck", GameRules.BooleanValue.create(false));
   public static final GameRules.Key<GameRules.IntegerValue> RULE_MAX_ENTITY_CRAMMING = register("maxEntityCramming", GameRules.IntegerValue.create(24));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_WEATHER_CYCLE = register("doWeatherCycle", GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_LIMITED_CRAFTING = register("doLimitedCrafting", GameRules.BooleanValue.create(false));
   public static final GameRules.Key<GameRules.IntegerValue> RULE_MAX_COMMAND_CHAIN_LENGTH = register("maxCommandChainLength", GameRules.IntegerValue.create(65536));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_ANNOUNCE_ADVANCEMENTS = register("announceAdvancements", GameRules.BooleanValue.create(true));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DISABLE_RAIDS = register("disableRaids", GameRules.BooleanValue.create(false));
   private final Map<GameRules.Key<?>, GameRules.Value<?>> rules;

   private static <T extends GameRules.Value<T>> GameRules.Key<T> register(String var0, GameRules.Type<T> var1) {
      GameRules.Key var2 = new GameRules.Key(var0);
      GameRules.Type var3 = (GameRules.Type)GAME_RULE_TYPES.put(var2, var1);
      if (var3 != null) {
         throw new IllegalStateException("Duplicate game rule registration for " + var0);
      } else {
         return var2;
      }
   }

   public GameRules() {
      super();
      this.rules = (Map)GAME_RULE_TYPES.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (var0) -> {
         return ((GameRules.Type)var0.getValue()).createRule();
      }));
   }

   public <T extends GameRules.Value<T>> T getRule(GameRules.Key<T> var1) {
      return (GameRules.Value)this.rules.get(var1);
   }

   public CompoundTag createTag() {
      CompoundTag var1 = new CompoundTag();
      this.rules.forEach((var1x, var2) -> {
         var1.putString(var1x.id, var2.serialize());
      });
      return var1;
   }

   public void loadFromTag(CompoundTag var1) {
      this.rules.forEach((var1x, var2) -> {
         var2.deserialize(var1.getString(var1x.id));
      });
   }

   public static void visitGameRuleTypes(GameRules.GameRuleTypeVisitor var0) {
      GAME_RULE_TYPES.forEach((var1, var2) -> {
         cap(var0, var1, var2);
      });
   }

   private static <T extends GameRules.Value<T>> void cap(GameRules.GameRuleTypeVisitor var0, GameRules.Key<?> var1, GameRules.Type<?> var2) {
      var0.visit(var1, var2);
   }

   public boolean getBoolean(GameRules.Key<GameRules.BooleanValue> var1) {
      return ((GameRules.BooleanValue)this.getRule(var1)).get();
   }

   public int getInt(GameRules.Key<GameRules.IntegerValue> var1) {
      return ((GameRules.IntegerValue)this.getRule(var1)).get();
   }

   public static class BooleanValue extends GameRules.Value<GameRules.BooleanValue> {
      private boolean value;

      private static GameRules.Type<GameRules.BooleanValue> create(boolean var0, BiConsumer<MinecraftServer, GameRules.BooleanValue> var1) {
         return new GameRules.Type(BoolArgumentType::bool, (var1x) -> {
            return new GameRules.BooleanValue(var1x, var0);
         }, var1);
      }

      private static GameRules.Type<GameRules.BooleanValue> create(boolean var0) {
         return create(var0, (var0x, var1) -> {
         });
      }

      public BooleanValue(GameRules.Type<GameRules.BooleanValue> var1, boolean var2) {
         super(var1);
         this.value = var2;
      }

      protected void updateFromArgument(CommandContext<CommandSourceStack> var1, String var2) {
         this.value = BoolArgumentType.getBool(var1, var2);
      }

      public boolean get() {
         return this.value;
      }

      public void set(boolean var1, @Nullable MinecraftServer var2) {
         this.value = var1;
         this.onChanged(var2);
      }

      protected String serialize() {
         return Boolean.toString(this.value);
      }

      protected void deserialize(String var1) {
         this.value = Boolean.parseBoolean(var1);
      }

      public int getCommandResult() {
         return this.value ? 1 : 0;
      }

      protected GameRules.BooleanValue getSelf() {
         return this;
      }

      // $FF: synthetic method
      protected GameRules.Value getSelf() {
         return this.getSelf();
      }
   }

   public static class IntegerValue extends GameRules.Value<GameRules.IntegerValue> {
      private int value;

      private static GameRules.Type<GameRules.IntegerValue> create(int var0, BiConsumer<MinecraftServer, GameRules.IntegerValue> var1) {
         return new GameRules.Type(IntegerArgumentType::integer, (var1x) -> {
            return new GameRules.IntegerValue(var1x, var0);
         }, var1);
      }

      private static GameRules.Type<GameRules.IntegerValue> create(int var0) {
         return create(var0, (var0x, var1) -> {
         });
      }

      public IntegerValue(GameRules.Type<GameRules.IntegerValue> var1, int var2) {
         super(var1);
         this.value = var2;
      }

      protected void updateFromArgument(CommandContext<CommandSourceStack> var1, String var2) {
         this.value = IntegerArgumentType.getInteger(var1, var2);
      }

      public int get() {
         return this.value;
      }

      protected String serialize() {
         return Integer.toString(this.value);
      }

      protected void deserialize(String var1) {
         this.value = safeParse(var1);
      }

      private static int safeParse(String var0) {
         if (!var0.isEmpty()) {
            try {
               return Integer.parseInt(var0);
            } catch (NumberFormatException var2) {
               GameRules.LOGGER.warn("Failed to parse integer {}", var0);
            }
         }

         return 0;
      }

      public int getCommandResult() {
         return this.value;
      }

      protected GameRules.IntegerValue getSelf() {
         return this;
      }

      // $FF: synthetic method
      protected GameRules.Value getSelf() {
         return this.getSelf();
      }
   }

   public abstract static class Value<T extends GameRules.Value<T>> {
      private final GameRules.Type<T> type;

      public Value(GameRules.Type<T> var1) {
         super();
         this.type = var1;
      }

      protected abstract void updateFromArgument(CommandContext<CommandSourceStack> var1, String var2);

      public void setFromArgument(CommandContext<CommandSourceStack> var1, String var2) {
         this.updateFromArgument(var1, var2);
         this.onChanged(((CommandSourceStack)var1.getSource()).getServer());
      }

      protected void onChanged(@Nullable MinecraftServer var1) {
         if (var1 != null) {
            this.type.callback.accept(var1, this.getSelf());
         }

      }

      protected abstract void deserialize(String var1);

      protected abstract String serialize();

      public String toString() {
         return this.serialize();
      }

      public abstract int getCommandResult();

      protected abstract T getSelf();
   }

   public static class Type<T extends GameRules.Value<T>> {
      private final Supplier<ArgumentType<?>> argument;
      private final Function<GameRules.Type<T>, T> constructor;
      private final BiConsumer<MinecraftServer, T> callback;

      private Type(Supplier<ArgumentType<?>> var1, Function<GameRules.Type<T>, T> var2, BiConsumer<MinecraftServer, T> var3) {
         super();
         this.argument = var1;
         this.constructor = var2;
         this.callback = var3;
      }

      public RequiredArgumentBuilder<CommandSourceStack, ?> createArgument(String var1) {
         return Commands.argument(var1, (ArgumentType)this.argument.get());
      }

      public T createRule() {
         return (GameRules.Value)this.constructor.apply(this);
      }

      // $FF: synthetic method
      Type(Supplier var1, Function var2, BiConsumer var3, Object var4) {
         this(var1, var2, var3);
      }
   }

   public static final class Key<T extends GameRules.Value<T>> {
      private final String id;

      public Key(String var1) {
         super();
         this.id = var1;
      }

      public String toString() {
         return this.id;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            return var1 instanceof GameRules.Key && ((GameRules.Key)var1).id.equals(this.id);
         }
      }

      public int hashCode() {
         return this.id.hashCode();
      }

      public String getId() {
         return this.id;
      }
   }

   @FunctionalInterface
   public interface GameRuleTypeVisitor {
      <T extends GameRules.Value<T>> void visit(GameRules.Key<T> var1, GameRules.Type<T> var2);
   }
}
