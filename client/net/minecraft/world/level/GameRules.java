package net.minecraft.world.level;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.DynamicLike;
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
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameRules {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Map<GameRules.Key<?>, GameRules.Type<?>> GAME_RULE_TYPES = Maps.newTreeMap(Comparator.comparing((var0) -> {
      return var0.id;
   }));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOFIRETICK;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_MOBGRIEFING;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_KEEPINVENTORY;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOMOBSPAWNING;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOMOBLOOT;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOBLOCKDROPS;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOENTITYDROPS;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_COMMANDBLOCKOUTPUT;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_NATURAL_REGENERATION;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DAYLIGHT;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_LOGADMINCOMMANDS;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_SHOWDEATHMESSAGES;
   public static final GameRules.Key<GameRules.IntegerValue> RULE_RANDOMTICKING;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_SENDCOMMANDFEEDBACK;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_REDUCEDDEBUGINFO;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_SPECTATORSGENERATECHUNKS;
   public static final GameRules.Key<GameRules.IntegerValue> RULE_SPAWN_RADIUS;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DISABLE_ELYTRA_MOVEMENT_CHECK;
   public static final GameRules.Key<GameRules.IntegerValue> RULE_MAX_ENTITY_CRAMMING;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_WEATHER_CYCLE;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_LIMITED_CRAFTING;
   public static final GameRules.Key<GameRules.IntegerValue> RULE_MAX_COMMAND_CHAIN_LENGTH;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_ANNOUNCE_ADVANCEMENTS;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DISABLE_RAIDS;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOINSOMNIA;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DO_IMMEDIATE_RESPAWN;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DROWNING_DAMAGE;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_FALL_DAMAGE;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_FIRE_DAMAGE;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DO_PATROL_SPAWNING;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DO_TRADER_SPAWNING;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_FORGIVE_DEAD_PLAYERS;
   public static final GameRules.Key<GameRules.BooleanValue> RULE_UNIVERSAL_ANGER;
   private final Map<GameRules.Key<?>, GameRules.Value<?>> rules;

   private static <T extends GameRules.Value<T>> GameRules.Key<T> register(String var0, GameRules.Category var1, GameRules.Type<T> var2) {
      GameRules.Key var3 = new GameRules.Key(var0, var1);
      GameRules.Type var4 = (GameRules.Type)GAME_RULE_TYPES.put(var3, var2);
      if (var4 != null) {
         throw new IllegalStateException("Duplicate game rule registration for " + var0);
      } else {
         return var3;
      }
   }

   public GameRules(DynamicLike<?> var1) {
      this();
      this.loadFromTag(var1);
   }

   public GameRules() {
      super();
      this.rules = (Map)GAME_RULE_TYPES.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (var0) -> {
         return ((GameRules.Type)var0.getValue()).createRule();
      }));
   }

   private GameRules(Map<GameRules.Key<?>, GameRules.Value<?>> var1) {
      super();
      this.rules = var1;
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

   private void loadFromTag(DynamicLike<?> var1) {
      this.rules.forEach((var1x, var2) -> {
         var1.get(var1x.id).asString().result().ifPresent(var2::deserialize);
      });
   }

   public GameRules copy() {
      return new GameRules((Map)this.rules.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (var0) -> {
         return ((GameRules.Value)var0.getValue()).copy();
      })));
   }

   public static void visitGameRuleTypes(GameRules.GameRuleTypeVisitor var0) {
      GAME_RULE_TYPES.forEach((var1, var2) -> {
         callVisitorCap(var0, var1, var2);
      });
   }

   private static <T extends GameRules.Value<T>> void callVisitorCap(GameRules.GameRuleTypeVisitor var0, GameRules.Key<?> var1, GameRules.Type<?> var2) {
      var0.visit(var1, var2);
      var2.callVisitor(var0, var1);
   }

   public void assignFrom(GameRules var1, @Nullable MinecraftServer var2) {
      var1.rules.keySet().forEach((var3) -> {
         this.assignCap(var3, var1, var2);
      });
   }

   private <T extends GameRules.Value<T>> void assignCap(GameRules.Key<T> var1, GameRules var2, @Nullable MinecraftServer var3) {
      GameRules.Value var4 = var2.getRule(var1);
      this.getRule(var1).setFrom(var4, var3);
   }

   public boolean getBoolean(GameRules.Key<GameRules.BooleanValue> var1) {
      return ((GameRules.BooleanValue)this.getRule(var1)).get();
   }

   public int getInt(GameRules.Key<GameRules.IntegerValue> var1) {
      return ((GameRules.IntegerValue)this.getRule(var1)).get();
   }

   static {
      RULE_DOFIRETICK = register("doFireTick", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
      RULE_MOBGRIEFING = register("mobGriefing", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
      RULE_KEEPINVENTORY = register("keepInventory", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
      RULE_DOMOBSPAWNING = register("doMobSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));
      RULE_DOMOBLOOT = register("doMobLoot", GameRules.Category.DROPS, GameRules.BooleanValue.create(true));
      RULE_DOBLOCKDROPS = register("doTileDrops", GameRules.Category.DROPS, GameRules.BooleanValue.create(true));
      RULE_DOENTITYDROPS = register("doEntityDrops", GameRules.Category.DROPS, GameRules.BooleanValue.create(true));
      RULE_COMMANDBLOCKOUTPUT = register("commandBlockOutput", GameRules.Category.CHAT, GameRules.BooleanValue.create(true));
      RULE_NATURAL_REGENERATION = register("naturalRegeneration", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
      RULE_DAYLIGHT = register("doDaylightCycle", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
      RULE_LOGADMINCOMMANDS = register("logAdminCommands", GameRules.Category.CHAT, GameRules.BooleanValue.create(true));
      RULE_SHOWDEATHMESSAGES = register("showDeathMessages", GameRules.Category.CHAT, GameRules.BooleanValue.create(true));
      RULE_RANDOMTICKING = register("randomTickSpeed", GameRules.Category.UPDATES, GameRules.IntegerValue.create(3));
      RULE_SENDCOMMANDFEEDBACK = register("sendCommandFeedback", GameRules.Category.CHAT, GameRules.BooleanValue.create(true));
      RULE_REDUCEDDEBUGINFO = register("reducedDebugInfo", GameRules.Category.MISC, GameRules.BooleanValue.create(false, (var0, var1) -> {
         int var2 = var1.get() ? 22 : 23;
         Iterator var3 = var0.getPlayerList().getPlayers().iterator();

         while(var3.hasNext()) {
            ServerPlayer var4 = (ServerPlayer)var3.next();
            var4.connection.send(new ClientboundEntityEventPacket(var4, (byte)var2));
         }

      }));
      RULE_SPECTATORSGENERATECHUNKS = register("spectatorsGenerateChunks", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
      RULE_SPAWN_RADIUS = register("spawnRadius", GameRules.Category.PLAYER, GameRules.IntegerValue.create(10));
      RULE_DISABLE_ELYTRA_MOVEMENT_CHECK = register("disableElytraMovementCheck", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
      RULE_MAX_ENTITY_CRAMMING = register("maxEntityCramming", GameRules.Category.MOBS, GameRules.IntegerValue.create(24));
      RULE_WEATHER_CYCLE = register("doWeatherCycle", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
      RULE_LIMITED_CRAFTING = register("doLimitedCrafting", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
      RULE_MAX_COMMAND_CHAIN_LENGTH = register("maxCommandChainLength", GameRules.Category.MISC, GameRules.IntegerValue.create(65536));
      RULE_ANNOUNCE_ADVANCEMENTS = register("announceAdvancements", GameRules.Category.CHAT, GameRules.BooleanValue.create(true));
      RULE_DISABLE_RAIDS = register("disableRaids", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
      RULE_DOINSOMNIA = register("doInsomnia", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));
      RULE_DO_IMMEDIATE_RESPAWN = register("doImmediateRespawn", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false, (var0, var1) -> {
         Iterator var2 = var0.getPlayerList().getPlayers().iterator();

         while(var2.hasNext()) {
            ServerPlayer var3 = (ServerPlayer)var2.next();
            var3.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.IMMEDIATE_RESPAWN, var1.get() ? 1.0F : 0.0F));
         }

      }));
      RULE_DROWNING_DAMAGE = register("drowningDamage", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
      RULE_FALL_DAMAGE = register("fallDamage", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
      RULE_FIRE_DAMAGE = register("fireDamage", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
      RULE_DO_PATROL_SPAWNING = register("doPatrolSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));
      RULE_DO_TRADER_SPAWNING = register("doTraderSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));
      RULE_FORGIVE_DEAD_PLAYERS = register("forgiveDeadPlayers", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
      RULE_UNIVERSAL_ANGER = register("universalAnger", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
   }

   public static class BooleanValue extends GameRules.Value<GameRules.BooleanValue> {
      private boolean value;

      private static GameRules.Type<GameRules.BooleanValue> create(boolean var0, BiConsumer<MinecraftServer, GameRules.BooleanValue> var1) {
         return new GameRules.Type(BoolArgumentType::bool, (var1x) -> {
            return new GameRules.BooleanValue(var1x, var0);
         }, var1, GameRules.GameRuleTypeVisitor::visitBoolean);
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

      public String serialize() {
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

      protected GameRules.BooleanValue copy() {
         return new GameRules.BooleanValue(this.type, this.value);
      }

      public void setFrom(GameRules.BooleanValue var1, @Nullable MinecraftServer var2) {
         this.value = var1.value;
         this.onChanged(var2);
      }

      // $FF: synthetic method
      protected GameRules.Value copy() {
         return this.copy();
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
         }, var1, GameRules.GameRuleTypeVisitor::visitInteger);
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

      public String serialize() {
         return Integer.toString(this.value);
      }

      protected void deserialize(String var1) {
         this.value = safeParse(var1);
      }

      public boolean tryDeserialize(String var1) {
         try {
            this.value = Integer.parseInt(var1);
            return true;
         } catch (NumberFormatException var3) {
            return false;
         }
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

      protected GameRules.IntegerValue copy() {
         return new GameRules.IntegerValue(this.type, this.value);
      }

      public void setFrom(GameRules.IntegerValue var1, @Nullable MinecraftServer var2) {
         this.value = var1.value;
         this.onChanged(var2);
      }

      // $FF: synthetic method
      protected GameRules.Value copy() {
         return this.copy();
      }

      // $FF: synthetic method
      protected GameRules.Value getSelf() {
         return this.getSelf();
      }
   }

   public abstract static class Value<T extends GameRules.Value<T>> {
      protected final GameRules.Type<T> type;

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

      public abstract String serialize();

      public String toString() {
         return this.serialize();
      }

      public abstract int getCommandResult();

      protected abstract T getSelf();

      protected abstract T copy();

      public abstract void setFrom(T var1, @Nullable MinecraftServer var2);
   }

   public static class Type<T extends GameRules.Value<T>> {
      private final Supplier<ArgumentType<?>> argument;
      private final Function<GameRules.Type<T>, T> constructor;
      private final BiConsumer<MinecraftServer, T> callback;
      private final GameRules.VisitorCaller<T> visitorCaller;

      private Type(Supplier<ArgumentType<?>> var1, Function<GameRules.Type<T>, T> var2, BiConsumer<MinecraftServer, T> var3, GameRules.VisitorCaller<T> var4) {
         super();
         this.argument = var1;
         this.constructor = var2;
         this.callback = var3;
         this.visitorCaller = var4;
      }

      public RequiredArgumentBuilder<CommandSourceStack, ?> createArgument(String var1) {
         return Commands.argument(var1, (ArgumentType)this.argument.get());
      }

      public T createRule() {
         return (GameRules.Value)this.constructor.apply(this);
      }

      public void callVisitor(GameRules.GameRuleTypeVisitor var1, GameRules.Key<T> var2) {
         this.visitorCaller.call(var1, var2, this);
      }

      // $FF: synthetic method
      Type(Supplier var1, Function var2, BiConsumer var3, GameRules.VisitorCaller var4, Object var5) {
         this(var1, var2, var3, var4);
      }
   }

   public static final class Key<T extends GameRules.Value<T>> {
      private final String id;
      private final GameRules.Category category;

      public Key(String var1, GameRules.Category var2) {
         super();
         this.id = var1;
         this.category = var2;
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

      public String getDescriptionId() {
         return "gamerule." + this.id;
      }

      public GameRules.Category getCategory() {
         return this.category;
      }
   }

   public interface GameRuleTypeVisitor {
      default <T extends GameRules.Value<T>> void visit(GameRules.Key<T> var1, GameRules.Type<T> var2) {
      }

      default void visitBoolean(GameRules.Key<GameRules.BooleanValue> var1, GameRules.Type<GameRules.BooleanValue> var2) {
      }

      default void visitInteger(GameRules.Key<GameRules.IntegerValue> var1, GameRules.Type<GameRules.IntegerValue> var2) {
      }
   }

   interface VisitorCaller<T extends GameRules.Value<T>> {
      void call(GameRules.GameRuleTypeVisitor var1, GameRules.Key<T> var2, GameRules.Type<T> var3);
   }

   public static enum Category {
      PLAYER("gamerule.category.player"),
      MOBS("gamerule.category.mobs"),
      SPAWNING("gamerule.category.spawning"),
      DROPS("gamerule.category.drops"),
      UPDATES("gamerule.category.updates"),
      CHAT("gamerule.category.chat"),
      MISC("gamerule.category.misc");

      private final String descriptionId;

      private Category(String var3) {
         this.descriptionId = var3;
      }

      public String getDescriptionId() {
         return this.descriptionId;
      }
   }
}
