package net.minecraft.world.level;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicLike;
import java.util.Comparator;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

public class GameRules {
   public static final int DEFAULT_RANDOM_TICK_SPEED = 3;
   static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<GameRules.Key<?>, GameRules.Type<?>> GAME_RULE_TYPES = Maps.newTreeMap(Comparator.comparing(var0 -> var0.id));
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOFIRETICK = register(
      "doFireTick", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_MOBGRIEFING = register(
      "mobGriefing", GameRules.Category.MOBS, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_KEEPINVENTORY = register(
      "keepInventory", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOMOBSPAWNING = register(
      "doMobSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOMOBLOOT = register(
      "doMobLoot", GameRules.Category.DROPS, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_PROJECTILESCANBREAKBLOCKS = register(
      "projectilesCanBreakBlocks", GameRules.Category.DROPS, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOBLOCKDROPS = register(
      "doTileDrops", GameRules.Category.DROPS, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOENTITYDROPS = register(
      "doEntityDrops", GameRules.Category.DROPS, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_COMMANDBLOCKOUTPUT = register(
      "commandBlockOutput", GameRules.Category.CHAT, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_NATURAL_REGENERATION = register(
      "naturalRegeneration", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DAYLIGHT = register(
      "doDaylightCycle", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_LOGADMINCOMMANDS = register(
      "logAdminCommands", GameRules.Category.CHAT, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_SHOWDEATHMESSAGES = register(
      "showDeathMessages", GameRules.Category.CHAT, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.IntegerValue> RULE_RANDOMTICKING = register(
      "randomTickSpeed", GameRules.Category.UPDATES, GameRules.IntegerValue.create(3)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_SENDCOMMANDFEEDBACK = register(
      "sendCommandFeedback", GameRules.Category.CHAT, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_REDUCEDDEBUGINFO = register(
      "reducedDebugInfo", GameRules.Category.MISC, GameRules.BooleanValue.create(false, (var0, var1) -> {
         int var2 = var1.get() ? 22 : 23;
   
         for (ServerPlayer var4 : var0.getPlayerList().getPlayers()) {
            var4.connection.send(new ClientboundEntityEventPacket(var4, (byte)var2));
         }
      })
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_SPECTATORSGENERATECHUNKS = register(
      "spectatorsGenerateChunks", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.IntegerValue> RULE_SPAWN_RADIUS = register(
      "spawnRadius", GameRules.Category.PLAYER, GameRules.IntegerValue.create(10)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DISABLE_ELYTRA_MOVEMENT_CHECK = register(
      "disableElytraMovementCheck", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false)
   );
   public static final GameRules.Key<GameRules.IntegerValue> RULE_MAX_ENTITY_CRAMMING = register(
      "maxEntityCramming", GameRules.Category.MOBS, GameRules.IntegerValue.create(24)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_WEATHER_CYCLE = register(
      "doWeatherCycle", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_LIMITED_CRAFTING = register(
      "doLimitedCrafting", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false, (var0, var1) -> {
         for (ServerPlayer var3 : var0.getPlayerList().getPlayers()) {
            var3.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.LIMITED_CRAFTING, var1.get() ? 1.0F : 0.0F));
         }
      })
   );
   public static final GameRules.Key<GameRules.IntegerValue> RULE_MAX_COMMAND_CHAIN_LENGTH = register(
      "maxCommandChainLength", GameRules.Category.MISC, GameRules.IntegerValue.create(65536)
   );
   public static final GameRules.Key<GameRules.IntegerValue> RULE_MAX_COMMAND_FORK_COUNT = register(
      "maxCommandForkCount", GameRules.Category.MISC, GameRules.IntegerValue.create(65536)
   );
   public static final GameRules.Key<GameRules.IntegerValue> RULE_COMMAND_MODIFICATION_BLOCK_LIMIT = register(
      "commandModificationBlockLimit", GameRules.Category.MISC, GameRules.IntegerValue.create(32768)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_ANNOUNCE_ADVANCEMENTS = register(
      "announceAdvancements", GameRules.Category.CHAT, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DISABLE_RAIDS = register(
      "disableRaids", GameRules.Category.MOBS, GameRules.BooleanValue.create(false)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DOINSOMNIA = register(
      "doInsomnia", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DO_IMMEDIATE_RESPAWN = register(
      "doImmediateRespawn", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false, (var0, var1) -> {
         for (ServerPlayer var3 : var0.getPlayerList().getPlayers()) {
            var3.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.IMMEDIATE_RESPAWN, var1.get() ? 1.0F : 0.0F));
         }
      })
   );
   public static final GameRules.Key<GameRules.IntegerValue> RULE_PLAYERS_NETHER_PORTAL_DEFAULT_DELAY = register(
      "playersNetherPortalDefaultDelay", GameRules.Category.PLAYER, GameRules.IntegerValue.create(80)
   );
   public static final GameRules.Key<GameRules.IntegerValue> RULE_PLAYERS_NETHER_PORTAL_CREATIVE_DELAY = register(
      "playersNetherPortalCreativeDelay", GameRules.Category.PLAYER, GameRules.IntegerValue.create(1)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DROWNING_DAMAGE = register(
      "drowningDamage", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_FALL_DAMAGE = register(
      "fallDamage", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_FIRE_DAMAGE = register(
      "fireDamage", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_FREEZE_DAMAGE = register(
      "freezeDamage", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DO_PATROL_SPAWNING = register(
      "doPatrolSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DO_TRADER_SPAWNING = register(
      "doTraderSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DO_WARDEN_SPAWNING = register(
      "doWardenSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_FORGIVE_DEAD_PLAYERS = register(
      "forgiveDeadPlayers", GameRules.Category.MOBS, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_UNIVERSAL_ANGER = register(
      "universalAnger", GameRules.Category.MOBS, GameRules.BooleanValue.create(false)
   );
   public static final GameRules.Key<GameRules.IntegerValue> RULE_PLAYERS_SLEEPING_PERCENTAGE = register(
      "playersSleepingPercentage", GameRules.Category.PLAYER, GameRules.IntegerValue.create(100)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_BLOCK_EXPLOSION_DROP_DECAY = register(
      "blockExplosionDropDecay", GameRules.Category.DROPS, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_MOB_EXPLOSION_DROP_DECAY = register(
      "mobExplosionDropDecay", GameRules.Category.DROPS, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_TNT_EXPLOSION_DROP_DECAY = register(
      "tntExplosionDropDecay", GameRules.Category.DROPS, GameRules.BooleanValue.create(false)
   );
   public static final GameRules.Key<GameRules.IntegerValue> RULE_SNOW_ACCUMULATION_HEIGHT = register(
      "snowAccumulationHeight", GameRules.Category.UPDATES, GameRules.IntegerValue.create(1)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_WATER_SOURCE_CONVERSION = register(
      "waterSourceConversion", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_LAVA_SOURCE_CONVERSION = register(
      "lavaSourceConversion", GameRules.Category.UPDATES, GameRules.BooleanValue.create(false)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_GLOBAL_SOUND_EVENTS = register(
      "globalSoundEvents", GameRules.Category.MISC, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_DO_VINES_SPREAD = register(
      "doVinesSpread", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.BooleanValue> RULE_ENDER_PEARLS_VANISH_ON_DEATH = register(
      "enderPearlsVanishOnDeath", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true)
   );
   public static final GameRules.Key<GameRules.IntegerValue> RULE_SPAWN_CHUNK_RADIUS = register(
      "spawnChunkRadius", GameRules.Category.MISC, GameRules.IntegerValue.create(2, 0, 32, (var0, var1) -> {
         ServerLevel var2 = var0.overworld();
         var2.setDefaultSpawnPos(var2.getSharedSpawnPos(), var2.getSharedSpawnAngle());
      })
   );
   private final Map<GameRules.Key<?>, GameRules.Value<?>> rules;

   private static <T extends GameRules.Value<T>> GameRules.Key<T> register(String var0, GameRules.Category var1, GameRules.Type<T> var2) {
      GameRules.Key var3 = new GameRules.Key(var0, var1);
      GameRules.Type var4 = GAME_RULE_TYPES.put(var3, var2);
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
      this.rules = GAME_RULE_TYPES.entrySet()
         .stream()
         .collect(ImmutableMap.toImmutableMap(Entry::getKey, var0 -> ((GameRules.Type)var0.getValue()).createRule()));
   }

   private GameRules(Map<GameRules.Key<?>, GameRules.Value<?>> var1) {
      super();
      this.rules = var1;
   }

   public <T extends GameRules.Value<T>> T getRule(GameRules.Key<T> var1) {
      return (T)this.rules.get(var1);
   }

   public CompoundTag createTag() {
      CompoundTag var1 = new CompoundTag();
      this.rules.forEach((var1x, var2) -> var1.putString(var1x.id, var2.serialize()));
      return var1;
   }

   private void loadFromTag(DynamicLike<?> var1) {
      this.rules.forEach((var1x, var2) -> var1.get(var1x.id).asString().ifSuccess(var2::deserialize));
   }

   public GameRules copy() {
      return new GameRules(
         this.rules.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, var0 -> ((GameRules.Value)var0.getValue()).copy()))
      );
   }

   public static void visitGameRuleTypes(GameRules.GameRuleTypeVisitor var0) {
      GAME_RULE_TYPES.forEach((var1, var2) -> callVisitorCap(var0, (GameRules.Key<?>)var1, (GameRules.Type<?>)var2));
   }

   private static <T extends GameRules.Value<T>> void callVisitorCap(GameRules.GameRuleTypeVisitor var0, GameRules.Key<?> var1, GameRules.Type<?> var2) {
      var0.visit(var1, var2);
      var2.callVisitor(var0, var1);
   }

   public void assignFrom(GameRules var1, @Nullable MinecraftServer var2) {
      var1.rules.keySet().forEach(var3 -> this.assignCap((GameRules.Key<?>)var3, var1, var2));
   }

   private <T extends GameRules.Value<T>> void assignCap(GameRules.Key<T> var1, GameRules var2, @Nullable MinecraftServer var3) {
      GameRules.Value var4 = var2.getRule(var1);
      this.getRule(var1).setFrom((T)var4, var3);
   }

   public boolean getBoolean(GameRules.Key<GameRules.BooleanValue> var1) {
      return this.<GameRules.BooleanValue>getRule(var1).get();
   }

   public int getInt(GameRules.Key<GameRules.IntegerValue> var1) {
      return this.<GameRules.IntegerValue>getRule(var1).get();
   }

   public static class BooleanValue extends GameRules.Value<GameRules.BooleanValue> {
      private boolean value;

      static GameRules.Type<GameRules.BooleanValue> create(boolean var0, BiConsumer<MinecraftServer, GameRules.BooleanValue> var1) {
         return new GameRules.Type<>(
            BoolArgumentType::bool, var1x -> new GameRules.BooleanValue(var1x, var0), var1, GameRules.GameRuleTypeVisitor::visitBoolean
         );
      }

      static GameRules.Type<GameRules.BooleanValue> create(boolean var0) {
         return create(var0, (var0x, var1) -> {
         });
      }

      public BooleanValue(GameRules.Type<GameRules.BooleanValue> var1, boolean var2) {
         super(var1);
         this.value = var2;
      }

      @Override
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

      @Override
      public String serialize() {
         return Boolean.toString(this.value);
      }

      @Override
      protected void deserialize(String var1) {
         this.value = Boolean.parseBoolean(var1);
      }

      @Override
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

   public interface GameRuleTypeVisitor {
      default <T extends GameRules.Value<T>> void visit(GameRules.Key<T> var1, GameRules.Type<T> var2) {
      }

      default void visitBoolean(GameRules.Key<GameRules.BooleanValue> var1, GameRules.Type<GameRules.BooleanValue> var2) {
      }

      default void visitInteger(GameRules.Key<GameRules.IntegerValue> var1, GameRules.Type<GameRules.IntegerValue> var2) {
      }
   }

   public static class IntegerValue extends GameRules.Value<GameRules.IntegerValue> {
      private int value;

      private static GameRules.Type<GameRules.IntegerValue> create(int var0, BiConsumer<MinecraftServer, GameRules.IntegerValue> var1) {
         return new GameRules.Type<>(
            IntegerArgumentType::integer, var1x -> new GameRules.IntegerValue(var1x, var0), var1, GameRules.GameRuleTypeVisitor::visitInteger
         );
      }

      static GameRules.Type<GameRules.IntegerValue> create(int var0, int var1, int var2, BiConsumer<MinecraftServer, GameRules.IntegerValue> var3) {
         return new GameRules.Type<>(
            () -> IntegerArgumentType.integer(var1, var2), var1x -> new GameRules.IntegerValue(var1x, var0), var3, GameRules.GameRuleTypeVisitor::visitInteger
         );
      }

      static GameRules.Type<GameRules.IntegerValue> create(int var0) {
         return create(var0, (var0x, var1) -> {
         });
      }

      public IntegerValue(GameRules.Type<GameRules.IntegerValue> var1, int var2) {
         super(var1);
         this.value = var2;
      }

      @Override
      protected void updateFromArgument(CommandContext<CommandSourceStack> var1, String var2) {
         this.value = IntegerArgumentType.getInteger(var1, var2);
      }

      public int get() {
         return this.value;
      }

      public void set(int var1, @Nullable MinecraftServer var2) {
         this.value = var1;
         this.onChanged(var2);
      }

      @Override
      public String serialize() {
         return Integer.toString(this.value);
      }

      @Override
      protected void deserialize(String var1) {
         this.value = safeParse(var1);
      }

      public boolean tryDeserialize(String var1) {
         try {
            StringReader var2 = new StringReader(var1);
            this.value = (Integer)this.type.argument.get().parse(var2);
            return !var2.canRead();
         } catch (CommandSyntaxException var3) {
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

      @Override
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
   }

   public static final class Key<T extends GameRules.Value<T>> {
      final String id;
      private final GameRules.Category category;

      public Key(String var1, GameRules.Category var2) {
         super();
         this.id = var1;
         this.category = var2;
      }

      @Override
      public String toString() {
         return this.id;
      }

      @Override
      public boolean equals(Object var1) {
         return this == var1 ? true : var1 instanceof GameRules.Key && ((GameRules.Key)var1).id.equals(this.id);
      }

      @Override
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

   public static class Type<T extends GameRules.Value<T>> {
      final Supplier<ArgumentType<?>> argument;
      private final Function<GameRules.Type<T>, T> constructor;
      final BiConsumer<MinecraftServer, T> callback;
      private final GameRules.VisitorCaller<T> visitorCaller;

      Type(Supplier<ArgumentType<?>> var1, Function<GameRules.Type<T>, T> var2, BiConsumer<MinecraftServer, T> var3, GameRules.VisitorCaller<T> var4) {
         super();
         this.argument = var1;
         this.constructor = var2;
         this.callback = var3;
         this.visitorCaller = var4;
      }

      public RequiredArgumentBuilder<CommandSourceStack, ?> createArgument(String var1) {
         return Commands.argument(var1, (ArgumentType<T>)this.argument.get());
      }

      public T createRule() {
         return this.constructor.apply(this);
      }

      public void callVisitor(GameRules.GameRuleTypeVisitor var1, GameRules.Key<T> var2) {
         this.visitorCaller.call(var1, var2, this);
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

      @Override
      public String toString() {
         return this.serialize();
      }

      public abstract int getCommandResult();

      protected abstract T getSelf();

      protected abstract T copy();

      public abstract void setFrom(T var1, @Nullable MinecraftServer var2);
   }

   interface VisitorCaller<T extends GameRules.Value<T>> {
      void call(GameRules.GameRuleTypeVisitor var1, GameRules.Key<T> var2, GameRules.Type<T> var3);
   }
}
