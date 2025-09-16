package net.minecraft.server.jsonrpc.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.server.jsonrpc.methods.GameRulesService;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;

public record Schema(Optional<URI> reference, Optional<String> type, Optional<FlatSchema> items, Optional<Map<String, FlatSchema>> properties, Optional<List<String>> enumValues) {
   public static final MapCodec<Schema> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ReferenceUtil.REFERENCE_CODEC.optionalFieldOf("$ref").forGetter(Schema::reference), Codec.STRING.optionalFieldOf("type").forGetter(Schema::type), FlatSchema.CODEC.codec().optionalFieldOf("items").forGetter(Schema::items), Codec.unboundedMap(Codec.STRING, FlatSchema.CODEC.codec()).optionalFieldOf("properties").forGetter(Schema::properties), Codec.STRING.listOf().optionalFieldOf("enum").forGetter(Schema::enumValues)).apply(var0, Schema::new));
   private static final List<SchemaComponent> SCHEMA_REGISTRY = new ArrayList();
   public static final Schema BOOL_SCHEMA = ofType("boolean");
   public static final Schema INT_SCHEMA = ofType("integer");
   public static final Schema NUMBER_SCHEMA = ofType("number");
   public static final Schema STRING_SCHEMA = ofType("string");
   public static final Schema UUID_SCHEMA;
   public static final Schema UUID_LIST_SCHEMA;
   public static final SchemaComponent DIFFICULTY_SCHEMA;
   public static final SchemaComponent GAME_TYPE_SCHEMA;
   public static final SchemaComponent PLAYER_SCHEMA;
   public static final SchemaComponent VERSION_SCHEMA;
   public static final SchemaComponent SERVER_STATE_SCHEMA;
   public static final Schema RULE_TYPE_SCHEMA;
   public static final SchemaComponent TYPED_GAME_RULE_SCHEMA;
   public static final SchemaComponent UNTYPED_GAME_RULE_SCHEMA;
   public static final SchemaComponent MESSAGE_SCHEMA;
   public static final SchemaComponent SYSTEM_MESSAGE_SCHEMA;
   public static final SchemaComponent KICK_PLAYER_SCHEMA;
   public static final SchemaComponent OPERATOR_SCHEMA;
   public static final SchemaComponent INCOMING_IP_BAN_SCHEMA;
   public static final SchemaComponent IP_BAN_SCHEMA;
   public static final SchemaComponent PLAYER_BAN_SCHEMA;

   public Schema(Optional<URI> var1, Optional<String> var2, Optional<FlatSchema> var3, Optional<Map<String, FlatSchema>> var4, Optional<List<String>> var5) {
      super();
      this.reference = var1;
      this.type = var2;
      this.items = var3;
      this.properties = var4;
      this.enumValues = var5;
   }

   private static SchemaComponent registerSchema(String var0, Schema var1) {
      SchemaComponent var2 = new SchemaComponent(var0, ReferenceUtil.createLocalReference(var0), var1);
      SCHEMA_REGISTRY.add(var2);
      return var2;
   }

   public static List<SchemaComponent> getSchemaRegistry() {
      return SCHEMA_REGISTRY;
   }

   public static Schema ofRef(URI var0) {
      return new Schema(Optional.of(var0), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
   }

   public static Schema ofType(String var0) {
      return new Schema(Optional.empty(), Optional.of(var0), Optional.empty(), Optional.empty(), Optional.empty());
   }

   public static <E extends Enum<E> & StringRepresentable> Schema ofEnum(Supplier<E[]> var0) {
      List var1 = Stream.of((Enum[])var0.get()).map((var0x) -> ((StringRepresentable)var0x).getSerializedName()).toList();
      return ofEnum(var1);
   }

   public static Schema ofEnum(List<String> var0) {
      return new Schema(Optional.empty(), Optional.of("string"), Optional.empty(), Optional.empty(), Optional.of(var0));
   }

   public static Schema arrayOf(FlatSchema var0) {
      return new Schema(Optional.empty(), Optional.of("array"), Optional.of(var0), Optional.empty(), Optional.empty());
   }

   public static Schema record() {
      return new Schema(Optional.empty(), Optional.of("object"), Optional.empty(), Optional.empty(), Optional.empty());
   }

   public static Schema record(Map<String, FlatSchema> var0) {
      return new Schema(Optional.empty(), Optional.of("object"), Optional.empty(), Optional.of(var0), Optional.empty());
   }

   public Schema withField(String var1, FlatSchema var2) {
      HashMap var3 = new HashMap();
      Optional var10000 = this.properties;
      Objects.requireNonNull(var3);
      var10000.ifPresent(var3::putAll);
      var3.put(var1, var2);
      return record(var3);
   }

   public Schema asArray() {
      return arrayOf(this.flatten());
   }

   public FlatSchema flatten() {
      if (!this.items.isPresent() || !((FlatSchema)this.items.get()).reference().isPresent() && !((FlatSchema)this.items.get()).type().isPresent()) {
         return this.enumValues.isPresent() ? new FlatSchema(this.reference, this.type, Optional.empty(), this.enumValues) : new FlatSchema(this.reference, this.type, Optional.empty(), Optional.empty());
      } else {
         return new FlatSchema(this.reference, this.type, Optional.of(new TypeRefSchema(((FlatSchema)this.items.get()).reference(), ((FlatSchema)this.items.get()).type(), Optional.empty())), Optional.empty());
      }
   }

   static {
      UUID_SCHEMA = STRING_SCHEMA;
      UUID_LIST_SCHEMA = UUID_SCHEMA.asArray();
      DIFFICULTY_SCHEMA = registerSchema("difficulty", ofEnum(Difficulty::values));
      GAME_TYPE_SCHEMA = registerSchema("game_type", ofEnum(GameType::values));
      PLAYER_SCHEMA = registerSchema("player", record().withField("id", UUID_SCHEMA.flatten()).withField("name", STRING_SCHEMA.flatten()));
      VERSION_SCHEMA = registerSchema("version", record().withField("name", STRING_SCHEMA.flatten()).withField("protocol", INT_SCHEMA.flatten()));
      SERVER_STATE_SCHEMA = registerSchema("server_state", record().withField("started", BOOL_SCHEMA.flatten()).withField("players", PLAYER_SCHEMA.asRef().asArray().flatten()).withField("version", VERSION_SCHEMA.asRef().flatten()));
      RULE_TYPE_SCHEMA = ofEnum(GameRulesService.RuleType::values);
      TYPED_GAME_RULE_SCHEMA = registerSchema("typed_game_rule", record().withField("key", FlatSchema.ofType("string")).withField("value", STRING_SCHEMA.flatten()).withField("type", RULE_TYPE_SCHEMA.flatten()));
      UNTYPED_GAME_RULE_SCHEMA = registerSchema("untyped_game_rule", record().withField("key", FlatSchema.ofType("string")).withField("value", STRING_SCHEMA.flatten()));
      MESSAGE_SCHEMA = registerSchema("message", record().withField("literal", STRING_SCHEMA.flatten()).withField("translatable", STRING_SCHEMA.flatten()).withField("translatableParams", STRING_SCHEMA.asArray().flatten()));
      SYSTEM_MESSAGE_SCHEMA = registerSchema("system_message", record().withField("message", MESSAGE_SCHEMA.asRef().flatten()).withField("overlay", BOOL_SCHEMA.flatten()).withField("receivingPlayers", PLAYER_SCHEMA.asRef().asArray().flatten()));
      KICK_PLAYER_SCHEMA = registerSchema("kick_player", record().withField("message", MESSAGE_SCHEMA.asRef().flatten()).withField("players", PLAYER_SCHEMA.asRef().asArray().flatten()));
      OPERATOR_SCHEMA = registerSchema("operator", record().withField("player", PLAYER_SCHEMA.asRef().flatten()).withField("bypassesPlayerLimit", BOOL_SCHEMA.flatten()).withField("permissionLevel", INT_SCHEMA.flatten()));
      INCOMING_IP_BAN_SCHEMA = registerSchema("incoming_ip_ban", record().withField("player", PLAYER_SCHEMA.asRef().flatten()).withField("ip", STRING_SCHEMA.flatten()).withField("reason", STRING_SCHEMA.flatten()).withField("source", STRING_SCHEMA.flatten()).withField("expires", STRING_SCHEMA.flatten()));
      IP_BAN_SCHEMA = registerSchema("ip_ban", record().withField("ip", STRING_SCHEMA.flatten()).withField("reason", STRING_SCHEMA.flatten()).withField("source", STRING_SCHEMA.flatten()).withField("expires", STRING_SCHEMA.flatten()));
      PLAYER_BAN_SCHEMA = registerSchema("user_ban", record().withField("player", PLAYER_SCHEMA.asRef().flatten()).withField("reason", STRING_SCHEMA.flatten()).withField("source", STRING_SCHEMA.flatten()).withField("expires", STRING_SCHEMA.flatten()));
   }
}
