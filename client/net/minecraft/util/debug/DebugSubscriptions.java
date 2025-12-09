package net.minecraft.util.debug;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.level.redstone.Orientation;

public class DebugSubscriptions<T> {
   public static final DebugSubscription<?> DEDICATED_SERVER_TICK_TIME = registerSimple("dedicated_server_tick_time");
   public static final DebugSubscription<DebugBeeInfo> BEES;
   public static final DebugSubscription<DebugBrainDump> BRAINS;
   public static final DebugSubscription<DebugBreezeInfo> BREEZES;
   public static final DebugSubscription<DebugGoalInfo> GOAL_SELECTORS;
   public static final DebugSubscription<DebugPathInfo> ENTITY_PATHS;
   public static final DebugSubscription<DebugEntityBlockIntersection> ENTITY_BLOCK_INTERSECTIONS;
   public static final DebugSubscription<DebugHiveInfo> BEE_HIVES;
   public static final DebugSubscription<DebugPoiInfo> POIS;
   public static final DebugSubscription<Orientation> REDSTONE_WIRE_ORIENTATIONS;
   public static final DebugSubscription<Unit> VILLAGE_SECTIONS;
   public static final DebugSubscription<List<BlockPos>> RAIDS;
   public static final DebugSubscription<List<DebugStructureInfo>> STRUCTURES;
   public static final DebugSubscription<DebugGameEventListenerInfo> GAME_EVENT_LISTENERS;
   public static final DebugSubscription<BlockPos> NEIGHBOR_UPDATES;
   public static final DebugSubscription<DebugGameEventInfo> GAME_EVENTS;

   public DebugSubscriptions() {
      super();
   }

   public static DebugSubscription<?> bootstrap(Registry<DebugSubscription<?>> var0) {
      return DEDICATED_SERVER_TICK_TIME;
   }

   private static DebugSubscription<?> registerSimple(String var0) {
      return (DebugSubscription)Registry.register(BuiltInRegistries.DEBUG_SUBSCRIPTION, (Identifier)Identifier.withDefaultNamespace(var0), new DebugSubscription((StreamCodec)null));
   }

   private static <T> DebugSubscription<T> registerWithValue(String var0, StreamCodec<? super RegistryFriendlyByteBuf, T> var1) {
      return (DebugSubscription)Registry.register(BuiltInRegistries.DEBUG_SUBSCRIPTION, (Identifier)Identifier.withDefaultNamespace(var0), new DebugSubscription(var1));
   }

   private static <T> DebugSubscription<T> registerTemporaryValue(String var0, StreamCodec<? super RegistryFriendlyByteBuf, T> var1, int var2) {
      return (DebugSubscription)Registry.register(BuiltInRegistries.DEBUG_SUBSCRIPTION, (Identifier)Identifier.withDefaultNamespace(var0), new DebugSubscription(var1, var2));
   }

   static {
      BEES = registerWithValue("bees", DebugBeeInfo.STREAM_CODEC);
      BRAINS = registerWithValue("brains", DebugBrainDump.STREAM_CODEC);
      BREEZES = registerWithValue("breezes", DebugBreezeInfo.STREAM_CODEC);
      GOAL_SELECTORS = registerWithValue("goal_selectors", DebugGoalInfo.STREAM_CODEC);
      ENTITY_PATHS = registerWithValue("entity_paths", DebugPathInfo.STREAM_CODEC);
      ENTITY_BLOCK_INTERSECTIONS = registerTemporaryValue("entity_block_intersections", DebugEntityBlockIntersection.STREAM_CODEC, 100);
      BEE_HIVES = registerWithValue("bee_hives", DebugHiveInfo.STREAM_CODEC);
      POIS = registerWithValue("pois", DebugPoiInfo.STREAM_CODEC);
      REDSTONE_WIRE_ORIENTATIONS = registerTemporaryValue("redstone_wire_orientations", Orientation.STREAM_CODEC, 200);
      VILLAGE_SECTIONS = registerWithValue("village_sections", Unit.STREAM_CODEC);
      RAIDS = registerWithValue("raids", BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()));
      STRUCTURES = registerWithValue("structures", DebugStructureInfo.STREAM_CODEC.apply(ByteBufCodecs.list()));
      GAME_EVENT_LISTENERS = registerWithValue("game_event_listeners", DebugGameEventListenerInfo.STREAM_CODEC);
      NEIGHBOR_UPDATES = registerTemporaryValue("neighbor_updates", BlockPos.STREAM_CODEC, 200);
      GAME_EVENTS = registerTemporaryValue("game_events", DebugGameEventInfo.STREAM_CODEC, 60);
   }
}
