package net.minecraft.world.level;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicLike;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
import org.slf4j.Logger;

public class GameRules {
   public static final int DEFAULT_RANDOM_TICK_SPEED = 3;
   static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<Key<?>, Type<?>> GAME_RULE_TYPES = Maps.newTreeMap(Comparator.comparing((var0) -> {
      return var0.id;
   }));
   public static final Key<BooleanValue> RULE_DOFIRETICK;
   public static final Key<BooleanValue> RULE_MOBGRIEFING;
   public static final Key<BooleanValue> RULE_KEEPINVENTORY;
   public static final Key<BooleanValue> RULE_DOMOBSPAWNING;
   public static final Key<BooleanValue> RULE_DOMOBLOOT;
   public static final Key<BooleanValue> RULE_DOBLOCKDROPS;
   public static final Key<BooleanValue> RULE_DOENTITYDROPS;
   public static final Key<BooleanValue> RULE_COMMANDBLOCKOUTPUT;
   public static final Key<BooleanValue> RULE_NATURAL_REGENERATION;
   public static final Key<BooleanValue> RULE_DAYLIGHT;
   public static final Key<BooleanValue> RULE_LOGADMINCOMMANDS;
   public static final Key<BooleanValue> RULE_SHOWDEATHMESSAGES;
   public static final Key<IntegerValue> RULE_RANDOMTICKING;
   public static final Key<BooleanValue> RULE_SENDCOMMANDFEEDBACK;
   public static final Key<BooleanValue> RULE_REDUCEDDEBUGINFO;
   public static final Key<BooleanValue> RULE_SPECTATORSGENERATECHUNKS;
   public static final Key<IntegerValue> RULE_SPAWN_RADIUS;
   public static final Key<BooleanValue> RULE_DISABLE_ELYTRA_MOVEMENT_CHECK;
   public static final Key<IntegerValue> RULE_MAX_ENTITY_CRAMMING;
   public static final Key<BooleanValue> RULE_WEATHER_CYCLE;
   public static final Key<BooleanValue> RULE_LIMITED_CRAFTING;
   public static final Key<IntegerValue> RULE_MAX_COMMAND_CHAIN_LENGTH;
   public static final Key<BooleanValue> RULE_ANNOUNCE_ADVANCEMENTS;
   public static final Key<BooleanValue> RULE_DISABLE_RAIDS;
   public static final Key<BooleanValue> RULE_DOINSOMNIA;
   public static final Key<BooleanValue> RULE_DO_IMMEDIATE_RESPAWN;
   public static final Key<BooleanValue> RULE_DROWNING_DAMAGE;
   public static final Key<BooleanValue> RULE_FALL_DAMAGE;
   public static final Key<BooleanValue> RULE_FIRE_DAMAGE;
   public static final Key<BooleanValue> RULE_FREEZE_DAMAGE;
   public static final Key<BooleanValue> RULE_DO_PATROL_SPAWNING;
   public static final Key<BooleanValue> RULE_DO_TRADER_SPAWNING;
   public static final Key<BooleanValue> RULE_DO_WARDEN_SPAWNING;
   public static final Key<BooleanValue> RULE_FORGIVE_DEAD_PLAYERS;
   public static final Key<BooleanValue> RULE_UNIVERSAL_ANGER;
   public static final Key<IntegerValue> RULE_PLAYERS_SLEEPING_PERCENTAGE;
   private final Map<Key<?>, Value<?>> rules;

   private static <T extends Value<T>> Key<T> register(String var0, Category var1, Type<T> var2) {
      Key var3 = new Key(var0, var1);
      Type var4 = (Type)GAME_RULE_TYPES.put(var3, var2);
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
      this.rules = (Map)GAME_RULE_TYPES.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (var0) -> {
         return ((Type)var0.getValue()).createRule();
      }));
   }

   private GameRules(Map<Key<?>, Value<?>> var1) {
      super();
      this.rules = var1;
   }

   public <T extends Value<T>> T getRule(Key<T> var1) {
      return (Value)this.rules.get(var1);
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
         Optional var10000 = var1.get(var1x.id).asString().result();
         Objects.requireNonNull(var2);
         var10000.ifPresent(var2::deserialize);
      });
   }

   public GameRules copy() {
      return new GameRules((Map)this.rules.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (var0) -> {
         return ((Value)var0.getValue()).copy();
      })));
   }

   public static void visitGameRuleTypes(GameRuleTypeVisitor var0) {
      GAME_RULE_TYPES.forEach((var1, var2) -> {
         callVisitorCap(var0, var1, var2);
      });
   }

   private static <T extends Value<T>> void callVisitorCap(GameRuleTypeVisitor var0, Key<?> var1, Type<?> var2) {
      var0.visit(var1, var2);
      var2.callVisitor(var0, var1);
   }

   public void assignFrom(GameRules var1, @Nullable MinecraftServer var2) {
      var1.rules.keySet().forEach((var3) -> {
         this.assignCap(var3, var1, var2);
      });
   }

   private <T extends Value<T>> void assignCap(Key<T> var1, GameRules var2, @Nullable MinecraftServer var3) {
      Value var4 = var2.getRule(var1);
      this.getRule(var1).setFrom(var4, var3);
   }

   public boolean getBoolean(Key<BooleanValue> var1) {
      return ((BooleanValue)this.getRule(var1)).get();
   }

   public int getInt(Key<IntegerValue> var1) {
      return ((IntegerValue)this.getRule(var1)).get();
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
      RULE_FREEZE_DAMAGE = register("freezeDamage", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
      RULE_DO_PATROL_SPAWNING = register("doPatrolSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));
      RULE_DO_TRADER_SPAWNING = register("doTraderSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));
      RULE_DO_WARDEN_SPAWNING = register("doWardenSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));
      RULE_FORGIVE_DEAD_PLAYERS = register("forgiveDeadPlayers", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
      RULE_UNIVERSAL_ANGER = register("universalAnger", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
      RULE_PLAYERS_SLEEPING_PERCENTAGE = register("playersSleepingPercentage", GameRules.Category.PLAYER, GameRules.IntegerValue.create(100));
   }

   public static final class Key<T extends Value<T>> {
      final String id;
      private final Category category;

      public Key(String var1, Category var2) {
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
            return var1 instanceof Key && ((Key)var1).id.equals(this.id);
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

      public Category getCategory() {
         return this.category;
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

      // $FF: synthetic method
      private static Category[] $values() {
         return new Category[]{PLAYER, MOBS, SPAWNING, DROPS, UPDATES, CHAT, MISC};
      }
   }

   public static class Type<T extends Value<T>> {
      private final Supplier<ArgumentType<?>> argument;
      private final Function<Type<T>, T> constructor;
      final BiConsumer<MinecraftServer, T> callback;
      private final VisitorCaller<T> visitorCaller;

      Type(Supplier<ArgumentType<?>> var1, Function<Type<T>, T> var2, BiConsumer<MinecraftServer, T> var3, VisitorCaller<T> var4) {
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
         return (Value)this.constructor.apply(this);
      }

      public void callVisitor(GameRuleTypeVisitor var1, Key<T> var2) {
         this.visitorCaller.call(var1, var2, this);
      }
   }

   public abstract static class Value<T extends Value<T>> {
      protected final Type<T> type;

      public Value(Type<T> var1) {
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

   public interface GameRuleTypeVisitor {
      default <T extends Value<T>> void visit(Key<T> var1, Type<T> var2) {
      }

      default void visitBoolean(Key<BooleanValue> var1, Type<BooleanValue> var2) {
      }

      default void visitInteger(Key<IntegerValue> var1, Type<IntegerValue> var2) {
      }
   }

   public static class BooleanValue extends Value<BooleanValue> {
      private boolean value;

      static Type<BooleanValue> create(boolean var0, BiConsumer<MinecraftServer, BooleanValue> var1) {
         return new Type(BoolArgumentType::bool, (var1x) -> {
            return new BooleanValue(var1x, var0);
         }, var1, GameRuleTypeVisitor::visitBoolean);
      }

      static Type<BooleanValue> create(boolean var0) {
         return create(var0, (var0x, var1) -> {
         });
      }

      public BooleanValue(Type<BooleanValue> var1, boolean var2) {
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

      protected BooleanValue getSelf() {
         return this;
      }

      protected BooleanValue copy() {
         return new BooleanValue(this.type, this.value);
      }

      public void setFrom(BooleanValue var1, @Nullable MinecraftServer var2) {
         this.value = var1.value;
         this.onChanged(var2);
      }

      // $FF: synthetic method
      protected Value copy() {
         return this.copy();
      }

      // $FF: synthetic method
      protected Value getSelf() {
         return this.getSelf();
      }
   }

   public static class IntegerValue extends Value<IntegerValue> {
      private int value;

      private static Type<IntegerValue> create(int var0, BiConsumer<MinecraftServer, IntegerValue> var1) {
         return new Type(IntegerArgumentType::integer, (var1x) -> {
            return new IntegerValue(var1x, var0);
         }, var1, GameRuleTypeVisitor::visitInteger);
      }

      static Type<IntegerValue> create(int var0) {
         return create(var0, (var0x, var1) -> {
         });
      }

      public IntegerValue(Type<IntegerValue> var1, int var2) {
         super(var1);
         this.value = var2;
      }

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

      protected IntegerValue getSelf() {
         return this;
      }

      protected IntegerValue copy() {
         return new IntegerValue(this.type, this.value);
      }

      public void setFrom(IntegerValue var1, @Nullable MinecraftServer var2) {
         this.value = var1.value;
         this.onChanged(var2);
      }

      // $FF: synthetic method
      protected Value copy() {
         return this.copy();
      }

      // $FF: synthetic method
      protected Value getSelf() {
         return this.getSelf();
      }
   }

   private interface VisitorCaller<T extends Value<T>> {
      void call(GameRuleTypeVisitor var1, Key<T> var2, Type<T> var3);
   }
}
