package net.minecraft.commands.arguments;

import com.mojang.serialization.Codec;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.SwizzleArgument;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class SerializableArgumentTypes {
   public static final Codec<SerializableArgument<?>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, SerializableArgument<?>> STREAM_CODEC;

   public SerializableArgumentTypes() {
      super();
   }

   public static void bootstrap(final WritableRegistry<SerializableArgument.Type<?>> registry) {
      Registry.register(registry, (String)"angle", AngleArgument.TYPE);
      Registry.register(registry, (String)"block_pos", BlockPosArgument.TYPE);
      Registry.register(registry, (String)"block_predicate", BlockPredicateArgument.TYPE);
      Registry.register(registry, (String)"block_state", BlockStateArgument.TYPE);
      Registry.register(registry, (String)"bool", BooleanArgument.TYPE);
      Registry.register(registry, (String)"column_pos", ColumnPosArgument.TYPE);
      Registry.register(registry, (String)"component", ComponentArgument.TYPE);
      Registry.register(registry, (String)"context_float_provider", ContextFloatProviderArgument.TYPE);
      Registry.register(registry, (String)"context_int_provider", ContextIntProviderArgument.TYPE);
      Registry.register(registry, (String)"dialog", DialogArgument.TYPE);
      Registry.register(registry, (String)"dimension", DimensionArgument.TYPE);
      Registry.register(registry, (String)"double", DoubleArgument.TYPE);
      Registry.register(registry, (String)"entity", EntityArgument.TYPE);
      Registry.register(registry, (String)"entity_anchor", EntityAnchorArgument.TYPE);
      Registry.register(registry, (String)"feature", FeatureArgument.TYPE);
      Registry.register(registry, (String)"float", FloatArgument.TYPE);
      Registry.register(registry, (String)"float_range", RangeArgument.Floats.TYPE);
      Registry.register(registry, (String)"function", FunctionArgument.TYPE);
      Registry.register(registry, (String)"game_profile", GameProfileArgument.TYPE);
      Registry.register(registry, (String)"gamemode", GameModeArgument.TYPE);
      Registry.register(registry, (String)"heightmap", HeightmapTypeArgument.TYPE);
      Registry.register(registry, (String)"hex_color", HexColorArgument.TYPE);
      Registry.register(registry, (String)"int_range", RangeArgument.Ints.TYPE);
      Registry.register(registry, (String)"integer", IntegerArgument.TYPE);
      Registry.register(registry, (String)"item_predicate", ItemPredicateArgument.TYPE);
      Registry.register(registry, (String)"item_slot", SlotArgument.TYPE);
      Registry.register(registry, (String)"item_slots", SlotsArgument.TYPE);
      Registry.register(registry, (String)"item_stack", ItemArgument.TYPE);
      Registry.register(registry, (String)"long", LongArgument.TYPE);
      Registry.register(registry, (String)"loot_modifier", LootModifierArgument.TYPE);
      Registry.register(registry, (String)"loot_predicate", LootPredicateArgument.TYPE);
      Registry.register(registry, (String)"loot_table", LootTableArgument.TYPE);
      Registry.register(registry, (String)"message", MessageArgument.TYPE);
      Registry.register(registry, (String)"nbt_compound_tag", CompoundTagArgument.TYPE);
      Registry.register(registry, (String)"nbt_path", NbtPathArgument.TYPE);
      Registry.register(registry, (String)"nbt_tag", NbtTagArgument.TYPE);
      Registry.register(registry, (String)"objective", ObjectiveArgument.TYPE);
      Registry.register(registry, (String)"objective_criteria", ObjectiveCriteriaArgument.TYPE);
      Registry.register(registry, (String)"operation", OperationArgument.TYPE);
      Registry.register(registry, (String)"particle", ParticleArgument.TYPE);
      Registry.register(registry, (String)"pattern", PatternStringArgument.TYPE);
      Registry.register(registry, (String)"resource", ResourceArgument.TYPE);
      Registry.register(registry, (String)"resource_location", IdentifierArgument.TYPE);
      Registry.register(registry, (String)"resource_or_tag", ResourceOrTagArgument.TYPE);
      Registry.register(registry, (String)"resource_selector", ResourceSelectorArgument.TYPE);
      Registry.register(registry, (String)"rotation", RotationArgument.TYPE);
      Registry.register(registry, (String)"score_holder", ScoreHolderArgument.TYPE);
      Registry.register(registry, (String)"scoreboard_slot", ScoreboardSlotArgument.TYPE);
      Registry.register(registry, (String)"slot_source", SlotSourceArgument.TYPE);
      Registry.register(registry, (String)"string", StringArgument.TYPE);
      Registry.register(registry, (String)"style", StyleArgument.TYPE);
      Registry.register(registry, (String)"swing_animation", SwingAnimationArgument.TYPE);
      Registry.register(registry, (String)"swizzle", SwizzleArgument.TYPE);
      Registry.register(registry, (String)"team", TeamArgument.TYPE);
      Registry.register(registry, (String)"team_color", TeamColorArgument.TYPE);
      Registry.register(registry, (String)"template_mirror", TemplateMirrorArgument.TYPE);
      Registry.register(registry, (String)"template_rotation", TemplateRotationArgument.TYPE);
      Registry.register(registry, (String)"time", TimeArgument.TYPE);
      Registry.register(registry, (String)"uuid", UuidArgument.TYPE);
      Registry.register(registry, (String)"vec2", Vec2Argument.TYPE);
      Registry.register(registry, (String)"vec3", Vec3Argument.TYPE);
   }

   static {
      CODEC = BuiltInRegistries.COMMAND_ARGUMENT_TYPE.byNameCodec().dispatch(SerializableArgument::type, SerializableArgument.Type::codec);
      STREAM_CODEC = ByteBufCodecs.registry(Registries.COMMAND_ARGUMENT_TYPE).dispatch(SerializableArgument::type, SerializableArgument.Type::streamCodec);
   }
}
