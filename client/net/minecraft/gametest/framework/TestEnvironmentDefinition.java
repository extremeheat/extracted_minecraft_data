package net.minecraft.gametest.framework;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Unit;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleMap;
import net.minecraft.world.level.gamerules.GameRules;
import org.slf4j.Logger;

public interface TestEnvironmentDefinition<SavedDataType> {
   Codec<TestEnvironmentDefinition<?>> DIRECT_CODEC = BuiltInRegistries.TEST_ENVIRONMENT_DEFINITION_TYPE.byNameCodec().dispatch(TestEnvironmentDefinition::codec, (c) -> c);
   Codec<Holder<TestEnvironmentDefinition<?>>> CODEC = RegistryFileCodec.<Holder<TestEnvironmentDefinition<?>>>create(Registries.TEST_ENVIRONMENT, DIRECT_CODEC);

   static MapCodec<? extends TestEnvironmentDefinition<?>> bootstrap(final Registry<MapCodec<? extends TestEnvironmentDefinition<?>>> registry) {
      Registry.register(registry, (String)"all_of", TestEnvironmentDefinition.AllOf.CODEC);
      Registry.register(registry, (String)"game_rules", TestEnvironmentDefinition.SetGameRules.CODEC);
      Registry.register(registry, (String)"clock_time", TestEnvironmentDefinition.ClockTime.CODEC);
      Registry.register(registry, (String)"weather", TestEnvironmentDefinition.Weather.CODEC);
      return (MapCodec)Registry.register(registry, (String)"function", TestEnvironmentDefinition.Functions.CODEC);
   }

   SavedDataType setup(ServerLevel level);

   void teardown(final ServerLevel level, final SavedDataType saveData);

   MapCodec<? extends TestEnvironmentDefinition<SavedDataType>> codec();

   static <T> Activation<T> activate(final TestEnvironmentDefinition<T> environment, final ServerLevel level) {
      return new Activation<T>(environment.setup(level), environment, level);
   }

   public static class Activation<T> {
      private final T value;
      private final TestEnvironmentDefinition<T> definition;
      private final ServerLevel level;

      private Activation(final T value, final TestEnvironmentDefinition<T> definition, final ServerLevel level) {
         super();
         this.value = value;
         this.definition = definition;
         this.level = level;
      }

      public void teardown() {
         this.definition.teardown(this.level, this.value);
      }
   }

   public static record Weather(Type weather) implements TestEnvironmentDefinition<Type> {
      public static final MapCodec<Weather> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(TestEnvironmentDefinition.Weather.Type.CODEC.fieldOf("weather").forGetter(Weather::weather)).apply(i, Weather::new));

      public Weather {
         super();
      }

      public Type setup(final ServerLevel level) {
         Type previous;
         if (level.isThundering()) {
            previous = TestEnvironmentDefinition.Weather.Type.THUNDER;
         } else if (level.isRaining()) {
            previous = TestEnvironmentDefinition.Weather.Type.RAIN;
         } else {
            previous = TestEnvironmentDefinition.Weather.Type.CLEAR;
         }

         this.weather.apply(level);
         return previous;
      }

      public void teardown(final ServerLevel level, final Type saveData) {
         level.resetWeatherCycle();
         saveData.apply(level);
      }

      public MapCodec<Weather> codec() {
         return CODEC;
      }

      public static enum Type implements StringRepresentable {
         CLEAR("clear", 100000, 0, false, false),
         RAIN("rain", 0, 100000, true, false),
         THUNDER("thunder", 0, 100000, true, true);

         public static final Codec<Type> CODEC = StringRepresentable.<Type>fromEnum(Type::values);
         private final String id;
         private final int clearTime;
         private final int rainTime;
         private final boolean raining;
         private final boolean thundering;

         private Type(final String id, final int clearTime, final int rainTime, final boolean raining, final boolean thundering) {
            this.id = id;
            this.clearTime = clearTime;
            this.rainTime = rainTime;
            this.raining = raining;
            this.thundering = thundering;
         }

         void apply(final ServerLevel level) {
            level.getServer().setWeatherParameters(this.clearTime, this.rainTime, this.raining, this.thundering);
         }

         public String getSerializedName() {
            return this.id;
         }

         // $FF: synthetic method
         private static Type[] $values() {
            return new Type[]{CLEAR, RAIN, THUNDER};
         }
      }
   }

   public static record ClockTime(Holder<WorldClock> clock, int time) implements TestEnvironmentDefinition<Long> {
      public static final MapCodec<ClockTime> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(WorldClock.CODEC.fieldOf("clock").forGetter(ClockTime::clock), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("time").forGetter(ClockTime::time)).apply(i, ClockTime::new));

      public ClockTime {
         super();
      }

      public Long setup(final ServerLevel level) {
         MinecraftServer server = level.getServer();
         long previous = server.clockManager().getTotalTicks(this.clock);
         server.clockManager().setTotalTicks(this.clock, (long)this.time);
         return previous;
      }

      public void teardown(final ServerLevel level, final Long saveData) {
         MinecraftServer server = level.getServer();
         server.clockManager().setTotalTicks(this.clock, saveData);
      }

      public MapCodec<ClockTime> codec() {
         return CODEC;
      }
   }

   public static record SetGameRules(GameRuleMap gameRulesMap) implements TestEnvironmentDefinition<GameRuleMap> {
      public static final MapCodec<SetGameRules> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(GameRuleMap.CODEC.fieldOf("rules").forGetter(SetGameRules::gameRulesMap)).apply(i, SetGameRules::new));

      public SetGameRules {
         super();
      }

      public GameRuleMap setup(final ServerLevel level) {
         GameRuleMap originalState = GameRuleMap.of();
         GameRules gameRules = level.getGameRules();
         this.gameRulesMap.keySet().forEach((rule) -> setFromActive(originalState, rule, gameRules));
         gameRules.setAll(this.gameRulesMap, level.getServer());
         return originalState;
      }

      private static <T> void setFromActive(final GameRuleMap map, final GameRule<T> rule, final GameRules rules) {
         map.set(rule, rules.get(rule));
      }

      public void teardown(final ServerLevel level, final GameRuleMap saveData) {
         level.getGameRules().setAll(saveData, level.getServer());
      }

      public MapCodec<SetGameRules> codec() {
         return CODEC;
      }
   }

   public static record Functions(Optional<Identifier> setupFunction, Optional<Identifier> teardownFunction) implements TestEnvironmentDefinition<Unit> {
      private static final Logger LOGGER = LogUtils.getLogger();
      public static final MapCodec<Functions> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Identifier.CODEC.optionalFieldOf("setup").forGetter(Functions::setupFunction), Identifier.CODEC.optionalFieldOf("teardown").forGetter(Functions::teardownFunction)).apply(i, Functions::new));

      public Functions {
         super();
      }

      public Unit setup(final ServerLevel level) {
         this.setupFunction.ifPresent((p) -> run(level, p));
         return Unit.INSTANCE;
      }

      public void teardown(final ServerLevel level, final Unit saveData) {
         this.teardownFunction.ifPresent((p) -> run(level, p));
      }

      private static void run(final ServerLevel level, final Identifier functionId) {
         MinecraftServer server = level.getServer();
         ServerFunctionManager functions = server.getFunctions();
         Optional<CommandFunction<CommandSourceStack>> function = functions.get(functionId);
         if (function.isPresent()) {
            CommandSourceStack source = server.createCommandSourceStack().withPermission(LevelBasedPermissionSet.GAMEMASTER).withSuppressedOutput().withLevel(level);
            functions.execute((CommandFunction)function.get(), source);
         } else {
            LOGGER.error("Test Batch failed for non-existent function {}", functionId);
         }

      }

      public MapCodec<Functions> codec() {
         return CODEC;
      }
   }

   public static record AllOf(List<Holder<TestEnvironmentDefinition<?>>> definitions) implements TestEnvironmentDefinition<List<? extends Activation<?>>> {
      public static final MapCodec<AllOf> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(TestEnvironmentDefinition.CODEC.listOf().fieldOf("definitions").forGetter(AllOf::definitions)).apply(i, AllOf::new));

      public AllOf(final TestEnvironmentDefinition<?>... defs) {
         this(Arrays.stream(defs).map(AllOf::holder).toList());
      }

      public AllOf {
         super();
      }

      private static Holder<TestEnvironmentDefinition<?>> holder(final TestEnvironmentDefinition<?> holder) {
         return Holder.<TestEnvironmentDefinition<?>>direct(holder);
      }

      public List<? extends Activation<?>> setup(final ServerLevel level) {
         return this.definitions.stream().map((b) -> TestEnvironmentDefinition.activate((TestEnvironmentDefinition)b.value(), level)).toList();
      }

      public void teardown(final ServerLevel level, final List<? extends Activation<?>> activations) {
         activations.reversed().forEach(Activation::teardown);
      }

      public MapCodec<AllOf> codec() {
         return CODEC;
      }
   }
}
