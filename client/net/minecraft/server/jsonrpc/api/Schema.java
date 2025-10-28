package net.minecraft.server.jsonrpc.api;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.jsonrpc.methods.BanlistService;
import net.minecraft.server.jsonrpc.methods.DiscoveryService;
import net.minecraft.server.jsonrpc.methods.GameRulesService;
import net.minecraft.server.jsonrpc.methods.IpBanlistService;
import net.minecraft.server.jsonrpc.methods.Message;
import net.minecraft.server.jsonrpc.methods.OperatorService;
import net.minecraft.server.jsonrpc.methods.PlayerService;
import net.minecraft.server.jsonrpc.methods.ServerStateService;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.gamerules.GameRuleType;

public record Schema<T>(Optional<URI> reference, List<String> type, Optional<Schema<?>> items, Map<String, Schema<?>> properties, List<String> enumValues, Codec<T> codec) {
   public static final Codec<? extends Schema<?>> CODEC = Codec.recursive("Schema", (var0) -> RecordCodecBuilder.create((var1) -> var1.group(ReferenceUtil.REFERENCE_CODEC.optionalFieldOf("$ref").forGetter(Schema::reference), ExtraCodecs.compactListCodec(Codec.STRING).optionalFieldOf("type", List.of()).forGetter(Schema::type), var0.optionalFieldOf("items").forGetter(Schema::items), Codec.unboundedMap(Codec.STRING, var0).optionalFieldOf("properties", Map.of()).forGetter(Schema::properties), Codec.STRING.listOf().optionalFieldOf("enum", List.of()).forGetter(Schema::enumValues)).apply(var1, (var0x, var1x, var2, var3, var4) -> null))).validate((var0) -> var0 == null ? DataResult.error(() -> "Should not deserialize schema") : DataResult.success(var0));
   private static final List<SchemaComponent<?>> SCHEMA_REGISTRY = new ArrayList();
   public static final Schema<Boolean> BOOL_SCHEMA;
   public static final Schema<Integer> INT_SCHEMA;
   public static final Schema<Either<Boolean, Integer>> BOOL_OR_INT_SCHEMA;
   public static final Schema<Float> NUMBER_SCHEMA;
   public static final Schema<String> STRING_SCHEMA;
   public static final Schema<UUID> UUID_SCHEMA;
   public static final Schema<DiscoveryService.DiscoverResponse> DISCOVERY_SCHEMA;
   public static final SchemaComponent<Difficulty> DIFFICULTY_SCHEMA;
   public static final SchemaComponent<GameType> GAME_TYPE_SCHEMA;
   public static final Schema<PermissionLevel> PERMISSION_LEVEL_SCHEMA;
   public static final SchemaComponent<PlayerDto> PLAYER_SCHEMA;
   public static final SchemaComponent<DiscoveryService.DiscoverInfo> VERSION_SCHEMA;
   public static final SchemaComponent<ServerStateService.ServerState> SERVER_STATE_SCHEMA;
   public static final Schema<GameRuleType> RULE_TYPE_SCHEMA;
   public static final SchemaComponent<GameRulesService.GameRuleUpdate<?>> TYPED_GAME_RULE_SCHEMA;
   public static final SchemaComponent<GameRulesService.GameRuleUpdate<?>> UNTYPED_GAME_RULE_SCHEMA;
   public static final SchemaComponent<Message> MESSAGE_SCHEMA;
   public static final SchemaComponent<ServerStateService.SystemMessage> SYSTEM_MESSAGE_SCHEMA;
   public static final SchemaComponent<PlayerService.KickDto> KICK_PLAYER_SCHEMA;
   public static final SchemaComponent<OperatorService.OperatorDto> OPERATOR_SCHEMA;
   public static final SchemaComponent<IpBanlistService.IncomingIpBanDto> INCOMING_IP_BAN_SCHEMA;
   public static final SchemaComponent<IpBanlistService.IpBanDto> IP_BAN_SCHEMA;
   public static final SchemaComponent<BanlistService.UserBanDto> PLAYER_BAN_SCHEMA;

   public Schema(Optional<URI> var1, List<String> var2, Optional<Schema<?>> var3, Map<String, Schema<?>> var4, List<String> var5, Codec<T> var6) {
      super();
      this.reference = var1;
      this.type = var2;
      this.items = var3;
      this.properties = var4;
      this.enumValues = var5;
      this.codec = var6;
   }

   public static <T> Codec<Schema<T>> typedCodec() {
      return CODEC;
   }

   public Schema<T> info() {
      return new Schema<T>(this.reference, this.type, this.items.map(Schema::info), (Map)this.properties.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (var0) -> ((Schema)var0.getValue()).info())), this.enumValues, this.codec);
   }

   private static <T> SchemaComponent<T> registerSchema(String var0, Schema<T> var1) {
      SchemaComponent var2 = new SchemaComponent(var0, ReferenceUtil.createLocalReference(var0), var1);
      SCHEMA_REGISTRY.add(var2);
      return var2;
   }

   public static List<SchemaComponent<?>> getSchemaRegistry() {
      return SCHEMA_REGISTRY;
   }

   public static <T> Schema<T> ofRef(URI var0, Codec<T> var1) {
      return new Schema<T>(Optional.of(var0), List.of(), Optional.empty(), Map.of(), List.of(), var1);
   }

   public static <T> Schema<T> ofType(String var0, Codec<T> var1) {
      return ofTypes(List.of(var0), var1);
   }

   public static <T> Schema<T> ofTypes(List<String> var0, Codec<T> var1) {
      return new Schema<T>(Optional.empty(), var0, Optional.empty(), Map.of(), List.of(), var1);
   }

   public static <E extends Enum<E> & StringRepresentable> Schema<E> ofEnum(Supplier<E[]> var0) {
      return ofEnum(var0, StringRepresentable.fromEnum(var0));
   }

   public static <E extends Enum<E> & StringRepresentable> Schema<E> ofEnum(Supplier<E[]> var0, Codec<E> var1) {
      List var2 = Stream.of((Enum[])var0.get()).map((var0x) -> ((StringRepresentable)var0x).getSerializedName()).toList();
      return ofEnum(var2, var1);
   }

   public static <T> Schema<T> ofEnum(List<String> var0, Codec<T> var1) {
      return new Schema<T>(Optional.empty(), List.of("string"), Optional.empty(), Map.of(), var0, var1);
   }

   public static <T> Schema<List<T>> arrayOf(Schema<?> var0, Codec<T> var1) {
      return new Schema<List<T>>(Optional.empty(), List.of("array"), Optional.of(var0), Map.of(), List.of(), var1.listOf());
   }

   public static <T> Schema<T> record(Codec<T> var0) {
      return new Schema<T>(Optional.empty(), List.of("object"), Optional.empty(), Map.of(), List.of(), var0);
   }

   private static <T> Schema<T> record(Map<String, Schema<?>> var0, Codec<T> var1) {
      return new Schema<T>(Optional.empty(), List.of("object"), Optional.empty(), var0, List.of(), var1);
   }

   public Schema<T> withField(String var1, Schema<?> var2) {
      HashMap var3 = new HashMap(this.properties);
      var3.put(var1, var2);
      return record(var3, this.codec);
   }

   public Schema<List<T>> asArray() {
      return arrayOf(this, this.codec);
   }

   static {
      BOOL_SCHEMA = ofType("boolean", Codec.BOOL);
      INT_SCHEMA = ofType("integer", Codec.INT);
      BOOL_OR_INT_SCHEMA = ofTypes(List.of("boolean", "integer"), Codec.either(Codec.BOOL, Codec.INT));
      NUMBER_SCHEMA = ofType("number", Codec.FLOAT);
      STRING_SCHEMA = ofType("string", Codec.STRING);
      UUID_SCHEMA = ofType("string", UUIDUtil.CODEC);
      DISCOVERY_SCHEMA = ofType("string", DiscoveryService.DiscoverResponse.CODEC.codec());
      DIFFICULTY_SCHEMA = registerSchema("difficulty", ofEnum(Difficulty::values, Difficulty.CODEC));
      GAME_TYPE_SCHEMA = registerSchema("game_type", ofEnum(GameType::values, GameType.CODEC));
      PERMISSION_LEVEL_SCHEMA = ofType("integer", PermissionLevel.INT_CODEC);
      PLAYER_SCHEMA = registerSchema("player", record(PlayerDto.CODEC.codec()).withField("id", UUID_SCHEMA).withField("name", STRING_SCHEMA));
      VERSION_SCHEMA = registerSchema("version", record(DiscoveryService.DiscoverInfo.CODEC.codec()).withField("name", STRING_SCHEMA).withField("protocol", INT_SCHEMA));
      SERVER_STATE_SCHEMA = registerSchema("server_state", record(ServerStateService.ServerState.CODEC).withField("started", BOOL_SCHEMA).withField("players", PLAYER_SCHEMA.asRef().asArray()).withField("version", VERSION_SCHEMA.asRef()));
      RULE_TYPE_SCHEMA = ofEnum(GameRuleType::values);
      TYPED_GAME_RULE_SCHEMA = registerSchema("typed_game_rule", record(GameRulesService.GameRuleUpdate.TYPED_CODEC).withField("key", STRING_SCHEMA).withField("value", BOOL_OR_INT_SCHEMA).withField("type", RULE_TYPE_SCHEMA));
      UNTYPED_GAME_RULE_SCHEMA = registerSchema("untyped_game_rule", record(GameRulesService.GameRuleUpdate.CODEC).withField("key", STRING_SCHEMA).withField("value", BOOL_OR_INT_SCHEMA));
      MESSAGE_SCHEMA = registerSchema("message", record(Message.CODEC).withField("literal", STRING_SCHEMA).withField("translatable", STRING_SCHEMA).withField("translatableParams", STRING_SCHEMA.asArray()));
      SYSTEM_MESSAGE_SCHEMA = registerSchema("system_message", record(ServerStateService.SystemMessage.CODEC).withField("message", MESSAGE_SCHEMA.asRef()).withField("overlay", BOOL_SCHEMA).withField("receivingPlayers", PLAYER_SCHEMA.asRef().asArray()));
      KICK_PLAYER_SCHEMA = registerSchema("kick_player", record(PlayerService.KickDto.CODEC.codec()).withField("message", MESSAGE_SCHEMA.asRef()).withField("player", PLAYER_SCHEMA.asRef()));
      OPERATOR_SCHEMA = registerSchema("operator", record(OperatorService.OperatorDto.CODEC.codec()).withField("player", PLAYER_SCHEMA.asRef()).withField("bypassesPlayerLimit", BOOL_SCHEMA).withField("permissionLevel", INT_SCHEMA));
      INCOMING_IP_BAN_SCHEMA = registerSchema("incoming_ip_ban", record(IpBanlistService.IncomingIpBanDto.CODEC.codec()).withField("player", PLAYER_SCHEMA.asRef()).withField("ip", STRING_SCHEMA).withField("reason", STRING_SCHEMA).withField("source", STRING_SCHEMA).withField("expires", STRING_SCHEMA));
      IP_BAN_SCHEMA = registerSchema("ip_ban", record(IpBanlistService.IpBanDto.CODEC.codec()).withField("ip", STRING_SCHEMA).withField("reason", STRING_SCHEMA).withField("source", STRING_SCHEMA).withField("expires", STRING_SCHEMA));
      PLAYER_BAN_SCHEMA = registerSchema("user_ban", record(BanlistService.UserBanDto.CODEC.codec()).withField("player", PLAYER_SCHEMA.asRef()).withField("reason", STRING_SCHEMA).withField("source", STRING_SCHEMA).withField("expires", STRING_SCHEMA));
   }
}
