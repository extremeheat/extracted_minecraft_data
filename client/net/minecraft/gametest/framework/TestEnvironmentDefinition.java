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
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.TheGame;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.GameRules;
import org.slf4j.Logger;

public interface TestEnvironmentDefinition {
   Codec<TestEnvironmentDefinition> DIRECT_CODEC = BuiltInRegistries.TEST_ENVIRONMENT_DEFINITION_TYPE.byNameCodec().dispatch(TestEnvironmentDefinition::codec, (var0) -> var0);
   Codec<Holder<TestEnvironmentDefinition>> CODEC = RegistryFileCodec.<Holder<TestEnvironmentDefinition>>create(Registries.TEST_ENVIRONMENT, DIRECT_CODEC);

   static MapCodec<? extends TestEnvironmentDefinition> bootstrap(Registry<MapCodec<? extends TestEnvironmentDefinition>> var0) {
      Registry.register(var0, (String)"all_of", TestEnvironmentDefinition.AllOf.CODEC);
      Registry.register(var0, (String)"game_rules", TestEnvironmentDefinition.SetGameRules.CODEC);
      Registry.register(var0, (String)"time_of_day", TestEnvironmentDefinition.TimeOfDay.CODEC);
      Registry.register(var0, (String)"weather", TestEnvironmentDefinition.Weather.CODEC);
      return (MapCodec)Registry.register(var0, (String)"function", TestEnvironmentDefinition.Functions.CODEC);
   }

   void setup(ServerLevel var1);

   default void teardown(ServerLevel var1) {
   }

   MapCodec<? extends TestEnvironmentDefinition> codec();

   public static record Weather(Type weather) implements TestEnvironmentDefinition {
      public static final MapCodec<Weather> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(TestEnvironmentDefinition.Weather.Type.CODEC.fieldOf("weather").forGetter(Weather::weather)).apply(var0, Weather::new));

      public Weather(Type var1) {
         super();
         this.weather = var1;
      }

      public void setup(ServerLevel var1) {
         this.weather.apply(var1);
      }

      public void teardown(ServerLevel var1) {
         var1.resetWeatherCycle();
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

         private Type(final String var3, final int var4, final int var5, final boolean var6, final boolean var7) {
            this.id = var3;
            this.clearTime = var4;
            this.rainTime = var5;
            this.raining = var6;
            this.thundering = var7;
         }

         void apply(ServerLevel var1) {
            var1.setWeatherParameters(this.clearTime, this.rainTime, this.raining, this.thundering);
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

   public static record TimeOfDay(int time) implements TestEnvironmentDefinition {
      public static final MapCodec<TimeOfDay> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("time").forGetter(TimeOfDay::time)).apply(var0, TimeOfDay::new));

      public TimeOfDay(int var1) {
         super();
         this.time = var1;
      }

      public void setup(ServerLevel var1) {
         var1.setDayTime((long)this.time);
      }

      public MapCodec<TimeOfDay> codec() {
         return CODEC;
      }
   }

   public static record SetGameRules(List<Entry<Boolean, GameRules.BooleanValue>> boolRules, List<Entry<Integer, GameRules.IntegerValue>> intRules) implements TestEnvironmentDefinition {
      public static final MapCodec<SetGameRules> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(TestEnvironmentDefinition.SetGameRules.Entry.codec(GameRules.BooleanValue.class, Codec.BOOL).listOf().fieldOf("bool_rules").forGetter(SetGameRules::boolRules), TestEnvironmentDefinition.SetGameRules.Entry.codec(GameRules.IntegerValue.class, Codec.INT).listOf().fieldOf("int_rules").forGetter(SetGameRules::intRules)).apply(var0, SetGameRules::new));

      public SetGameRules(List<Entry<Boolean, GameRules.BooleanValue>> var1, List<Entry<Integer, GameRules.IntegerValue>> var2) {
         super();
         this.boolRules = var1;
         this.intRules = var2;
      }

      public void setup(ServerLevel var1) {
         GameRules var2 = var1.getGameRules();
         TheGame var3 = var1.theGame();

         for(Entry var5 : this.boolRules) {
            ((GameRules.BooleanValue)var2.getRule(var5.key())).set((Boolean)var5.value(), var3);
         }

         for(Entry var7 : this.intRules) {
            ((GameRules.IntegerValue)var2.getRule(var7.key())).set((Integer)var7.value(), var3);
         }

      }

      public void teardown(ServerLevel var1) {
         GameRules var2 = var1.getGameRules();
         TheGame var3 = var1.theGame();

         for(Entry var5 : this.boolRules) {
            ((GameRules.BooleanValue)var2.getRule(var5.key())).setFrom((GameRules.BooleanValue)GameRules.getType(var5.key()).createRule(), var3);
         }

         for(Entry var7 : this.intRules) {
            ((GameRules.IntegerValue)var2.getRule(var7.key())).setFrom((GameRules.IntegerValue)GameRules.getType(var7.key()).createRule(), var3);
         }

      }

      public MapCodec<SetGameRules> codec() {
         return CODEC;
      }

      public static <S, T extends GameRules.Value<T>> Entry<S, T> entry(GameRules.Key<T> var0, S var1) {
         return new Entry<S, T>(var0, var1);
      }

      public static record Entry<S, T extends GameRules.Value<T>>(GameRules.Key<T> key, S value) {
         public Entry(GameRules.Key<T> var1, S var2) {
            super();
            this.key = var1;
            this.value = var2;
         }

         public static <S, T extends GameRules.Value<T>> Codec<Entry<S, T>> codec(Class<T> var0, Codec<S> var1) {
            return RecordCodecBuilder.create((var2) -> var2.group(GameRules.keyCodec(var0).fieldOf("rule").forGetter(Entry::key), var1.fieldOf("value").forGetter(Entry::value)).apply(var2, Entry::new));
         }
      }
   }

   public static record Functions(Optional<ResourceLocation> setupFunction, Optional<ResourceLocation> teardownFunction) implements TestEnvironmentDefinition {
      private static final Logger LOGGER = LogUtils.getLogger();
      public static final MapCodec<Functions> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocation.CODEC.optionalFieldOf("setup").forGetter(Functions::setupFunction), ResourceLocation.CODEC.optionalFieldOf("teardown").forGetter(Functions::teardownFunction)).apply(var0, Functions::new));

      public Functions(Optional<ResourceLocation> var1, Optional<ResourceLocation> var2) {
         super();
         this.setupFunction = var1;
         this.teardownFunction = var2;
      }

      public void setup(ServerLevel var1) {
         this.setupFunction.ifPresent((var1x) -> run(var1, var1x));
      }

      public void teardown(ServerLevel var1) {
         this.teardownFunction.ifPresent((var1x) -> run(var1, var1x));
      }

      private static void run(ServerLevel var0, ResourceLocation var1) {
         TheGame var2 = var0.theGame();
         ServerFunctionManager var3 = var0.theGame().getFunctions();
         Optional var4 = var3.get(var1);
         if (var4.isPresent()) {
            CommandSourceStack var5 = var2.createCommandSourceStack().withPermission(2).withSuppressedOutput().withLevel(var0);
            var3.execute((CommandFunction)var4.get(), var5);
         } else {
            LOGGER.error("Test Batch failed for non-existent function {}", var1);
         }

      }

      public MapCodec<Functions> codec() {
         return CODEC;
      }
   }

   public static record AllOf(List<Holder<TestEnvironmentDefinition>> definitions) implements TestEnvironmentDefinition {
      public static final MapCodec<AllOf> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(TestEnvironmentDefinition.CODEC.listOf().fieldOf("definitions").forGetter(AllOf::definitions)).apply(var0, AllOf::new));

      public AllOf(TestEnvironmentDefinition... var1) {
         this(Arrays.stream(var1).map(Holder::direct).toList());
      }

      public AllOf(List<Holder<TestEnvironmentDefinition>> var1) {
         super();
         this.definitions = var1;
      }

      public void setup(ServerLevel var1) {
         this.definitions.forEach((var1x) -> ((TestEnvironmentDefinition)var1x.value()).setup(var1));
      }

      public void teardown(ServerLevel var1) {
         this.definitions.forEach((var1x) -> ((TestEnvironmentDefinition)var1x.value()).teardown(var1));
      }

      public MapCodec<AllOf> codec() {
         return CODEC;
      }
   }
}
