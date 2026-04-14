package net.minecraft.commands.arguments.selector.options;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.logging.LogUtils;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import org.slf4j.Logger;

public class EntitySelectorOptions {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<String, Option> OPTIONS = Maps.newHashMap();
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_OPTION = new DynamicCommandExceptionType((name) -> Component.translatableEscape("argument.entity.options.unknown", name));
   public static final DynamicCommandExceptionType ERROR_INAPPLICABLE_OPTION = new DynamicCommandExceptionType((name) -> Component.translatableEscape("argument.entity.options.inapplicable", name));
   public static final SimpleCommandExceptionType ERROR_RANGE_NEGATIVE = new SimpleCommandExceptionType(Component.translatable("argument.entity.options.distance.negative"));
   public static final SimpleCommandExceptionType ERROR_LEVEL_NEGATIVE = new SimpleCommandExceptionType(Component.translatable("argument.entity.options.level.negative"));
   public static final SimpleCommandExceptionType ERROR_LIMIT_TOO_SMALL = new SimpleCommandExceptionType(Component.translatable("argument.entity.options.limit.toosmall"));
   public static final DynamicCommandExceptionType ERROR_SORT_UNKNOWN = new DynamicCommandExceptionType((name) -> Component.translatableEscape("argument.entity.options.sort.irreversible", name));
   public static final DynamicCommandExceptionType ERROR_GAME_MODE_INVALID = new DynamicCommandExceptionType((name) -> Component.translatableEscape("argument.entity.options.mode.invalid", name));
   public static final DynamicCommandExceptionType ERROR_ENTITY_TYPE_INVALID = new DynamicCommandExceptionType((type) -> Component.translatableEscape("argument.entity.options.type.invalid", type));

   public EntitySelectorOptions() {
      super();
   }

   private static void register(final String name, final Modifier modifier, final Predicate<EntitySelectorParser> predicate, final Component description) {
      OPTIONS.put(name, new Option(modifier, predicate, description));
   }

   public static void bootStrap() {
      if (OPTIONS.isEmpty()) {
         register("name", (parser) -> {
            int start = parser.getReader().getCursor();
            boolean not = parser.shouldInvertValue();
            String name = parser.getReader().readString();
            if (parser.hasNameNotEquals() && !not) {
               parser.getReader().setCursor(start);
               throw ERROR_INAPPLICABLE_OPTION.createWithContext(parser.getReader(), "name");
            } else {
               if (not) {
                  parser.setHasNameNotEquals(true);
               } else {
                  parser.setHasNameEquals(true);
               }

               parser.addPredicate((e) -> e.getPlainTextName().equals(name) != not);
            }
         }, (s) -> !s.hasNameEquals(), Component.translatable("argument.entity.options.name.description"));
         register("distance", (parser) -> {
            int start = parser.getReader().getCursor();
            MinMaxBounds.Doubles value = MinMaxBounds.Doubles.fromReader(parser.getReader());
            if ((!value.min().isPresent() || !((Double)value.min().get() < 0.0)) && (!value.max().isPresent() || !((Double)value.max().get() < 0.0))) {
               parser.setDistance(value);
               parser.setWorldLimited();
            } else {
               parser.getReader().setCursor(start);
               throw ERROR_RANGE_NEGATIVE.createWithContext(parser.getReader());
            }
         }, (s) -> s.getDistance() == null, Component.translatable("argument.entity.options.distance.description"));
         register("level", (parser) -> {
            int start = parser.getReader().getCursor();
            MinMaxBounds.Ints value = MinMaxBounds.Ints.fromReader(parser.getReader());
            if ((!value.min().isPresent() || (Integer)value.min().get() >= 0) && (!value.max().isPresent() || (Integer)value.max().get() >= 0)) {
               parser.setLevel(value);
               parser.setIncludesEntities(false);
            } else {
               parser.getReader().setCursor(start);
               throw ERROR_LEVEL_NEGATIVE.createWithContext(parser.getReader());
            }
         }, (s) -> s.getLevel() == null, Component.translatable("argument.entity.options.level.description"));
         register("x", (parser) -> {
            parser.setWorldLimited();
            parser.setX(parser.getReader().readDouble());
         }, (s) -> s.getX() == null, Component.translatable("argument.entity.options.x.description"));
         register("y", (parser) -> {
            parser.setWorldLimited();
            parser.setY(parser.getReader().readDouble());
         }, (s) -> s.getY() == null, Component.translatable("argument.entity.options.y.description"));
         register("z", (parser) -> {
            parser.setWorldLimited();
            parser.setZ(parser.getReader().readDouble());
         }, (s) -> s.getZ() == null, Component.translatable("argument.entity.options.z.description"));
         register("dx", (parser) -> {
            parser.setWorldLimited();
            parser.setDeltaX(parser.getReader().readDouble());
         }, (s) -> s.getDeltaX() == null, Component.translatable("argument.entity.options.dx.description"));
         register("dy", (parser) -> {
            parser.setWorldLimited();
            parser.setDeltaY(parser.getReader().readDouble());
         }, (s) -> s.getDeltaY() == null, Component.translatable("argument.entity.options.dy.description"));
         register("dz", (parser) -> {
            parser.setWorldLimited();
            parser.setDeltaZ(parser.getReader().readDouble());
         }, (s) -> s.getDeltaZ() == null, Component.translatable("argument.entity.options.dz.description"));
         register("x_rotation", (parser) -> parser.setRotX(MinMaxBounds.FloatDegrees.fromReader(parser.getReader())), (s) -> s.getRotX() == null, Component.translatable("argument.entity.options.x_rotation.description"));
         register("y_rotation", (parser) -> parser.setRotY(MinMaxBounds.FloatDegrees.fromReader(parser.getReader())), (s) -> s.getRotY() == null, Component.translatable("argument.entity.options.y_rotation.description"));
         register("limit", (parser) -> {
            int start = parser.getReader().getCursor();
            int count = parser.getReader().readInt();
            if (count < 1) {
               parser.getReader().setCursor(start);
               throw ERROR_LIMIT_TOO_SMALL.createWithContext(parser.getReader());
            } else {
               parser.setMaxResults(count);
               parser.setLimited(true);
            }
         }, (s) -> !s.isCurrentEntity() && !s.isLimited(), Component.translatable("argument.entity.options.limit.description"));
         register("sort", (parser) -> {
            int start = parser.getReader().getCursor();
            String name = parser.getReader().readUnquotedString();
            parser.setSuggestions((b, n) -> SharedSuggestionProvider.suggest(Arrays.asList("nearest", "furthest", "random", "arbitrary"), b));
            BiConsumer var10001;
            switch (name) {
               case "nearest":
                  var10001 = EntitySelectorParser.ORDER_NEAREST;
                  break;
               case "furthest":
                  var10001 = EntitySelectorParser.ORDER_FURTHEST;
                  break;
               case "random":
                  var10001 = EntitySelectorParser.ORDER_RANDOM;
                  break;
               case "arbitrary":
                  var10001 = EntitySelector.ORDER_ARBITRARY;
                  break;
               default:
                  parser.getReader().setCursor(start);
                  throw ERROR_SORT_UNKNOWN.createWithContext(parser.getReader(), name);
            }

            parser.setOrder(var10001);
            parser.setSorted(true);
         }, (s) -> !s.isCurrentEntity() && !s.isSorted(), Component.translatable("argument.entity.options.sort.description"));
         register("gamemode", (parser) -> {
            parser.setSuggestions((b, m) -> {
               String prefix = b.getRemaining().toLowerCase(Locale.ROOT);
               boolean addNormal = !parser.hasGamemodeNotEquals();
               boolean addInverted = true;
               if (!prefix.isEmpty()) {
                  if (prefix.charAt(0) == '!') {
                     addNormal = false;
                     prefix = prefix.substring(1);
                  } else {
                     addInverted = false;
                  }
               }

               for(GameType type : GameType.values()) {
                  if (type.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
                     if (addInverted) {
                        b.suggest("!" + type.getName());
                     }

                     if (addNormal) {
                        b.suggest(type.getName());
                     }
                  }
               }

               return b.buildFuture();
            });
            int start = parser.getReader().getCursor();
            boolean inverted = parser.shouldInvertValue();
            if (parser.hasGamemodeNotEquals() && !inverted) {
               parser.getReader().setCursor(start);
               throw ERROR_INAPPLICABLE_OPTION.createWithContext(parser.getReader(), "gamemode");
            } else {
               String name = parser.getReader().readUnquotedString();
               GameType expected = GameType.byName(name, (GameType)null);
               if (expected == null) {
                  parser.getReader().setCursor(start);
                  throw ERROR_GAME_MODE_INVALID.createWithContext(parser.getReader(), name);
               } else {
                  parser.setIncludesEntities(false);
                  parser.addPredicate((e) -> {
                     if (e instanceof ServerPlayer player) {
                        GameType current = player.gameMode();
                        return current == expected ^ inverted;
                     } else {
                        return false;
                     }
                  });
                  if (inverted) {
                     parser.setHasGamemodeNotEquals(true);
                  } else {
                     parser.setHasGamemodeEquals(true);
                  }

               }
            }
         }, (s) -> !s.hasGamemodeEquals(), Component.translatable("argument.entity.options.gamemode.description"));
         register("team", (parser) -> {
            boolean inverted = parser.shouldInvertValue();
            String expected = parser.getReader().readUnquotedString();
            parser.addPredicate((e) -> {
               Team current = e.getTeam();
               String currentName = current == null ? "" : current.getName();
               return currentName.equals(expected) != inverted;
            });
            if (inverted) {
               parser.setHasTeamNotEquals(true);
            } else {
               parser.setHasTeamEquals(true);
            }

         }, (s) -> !s.hasTeamEquals(), Component.translatable("argument.entity.options.team.description"));
         register("type", (parser) -> {
            parser.setSuggestions((b, m) -> {
               SharedSuggestionProvider.suggestResource(BuiltInRegistries.ENTITY_TYPE.keySet(), b, String.valueOf('!'));
               SharedSuggestionProvider.suggestResource(BuiltInRegistries.ENTITY_TYPE.getTags().map((tag) -> tag.key().location()), b, "!#");
               if (!parser.isTypeLimitedInversely()) {
                  SharedSuggestionProvider.suggestResource(BuiltInRegistries.ENTITY_TYPE.keySet(), b);
                  SharedSuggestionProvider.suggestResource(BuiltInRegistries.ENTITY_TYPE.getTags().map((tag) -> tag.key().location()), b, String.valueOf('#'));
               }

               return b.buildFuture();
            });
            int start = parser.getReader().getCursor();
            boolean inverted = parser.shouldInvertValue();
            if (parser.isTypeLimitedInversely() && !inverted) {
               parser.getReader().setCursor(start);
               throw ERROR_INAPPLICABLE_OPTION.createWithContext(parser.getReader(), "type");
            } else {
               if (inverted) {
                  parser.setTypeLimitedInversely();
               }

               if (parser.isTag()) {
                  TagKey<EntityType<?>> id = TagKey.<EntityType<?>>create(Registries.ENTITY_TYPE, Identifier.read(parser.getReader()));
                  parser.addPredicate((e) -> e.is(id) != inverted);
               } else {
                  Identifier id = Identifier.read(parser.getReader());
                  EntityType<?> type = (EntityType)BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElseThrow(() -> {
                     parser.getReader().setCursor(start);
                     return ERROR_ENTITY_TYPE_INVALID.createWithContext(parser.getReader(), id.toString());
                  });
                  if (Objects.equals(EntityTypes.PLAYER, type) && !inverted) {
                     parser.setIncludesEntities(false);
                  }

                  parser.addPredicate((e) -> Objects.equals(type, e.getType()) != inverted);
                  if (!inverted) {
                     parser.limitToType(type);
                  }
               }

            }
         }, (s) -> !s.isTypeLimited(), Component.translatable("argument.entity.options.type.description"));
         register("tag", (parser) -> {
            boolean inverted = parser.shouldInvertValue();
            String tag = parser.getReader().readUnquotedString();
            parser.addPredicate((e) -> {
               if ("".equals(tag)) {
                  return e.entityTags().isEmpty() != inverted;
               } else {
                  return e.entityTags().contains(tag) != inverted;
               }
            });
         }, (s) -> true, Component.translatable("argument.entity.options.tag.description"));
         register("nbt", (parser) -> {
            boolean inverted = parser.shouldInvertValue();
            CompoundTag tag = TagParser.parseCompoundAsArgument(parser.getReader());
            parser.addPredicate((e) -> {
               try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(e.problemPath(), LOGGER)) {
                  TagValueOutput output = TagValueOutput.createWithContext(reporter, e.registryAccess());
                  e.saveWithoutId(output);
                  if (e instanceof ServerPlayer player) {
                     ItemStack selected = player.getInventory().getSelectedItem();
                     if (!selected.isEmpty()) {
                        output.store("SelectedItem", ItemStack.CODEC, selected);
                     }
                  }

                  return NbtUtils.compareNbt(tag, output.buildResult(), true) != inverted;
               }
            });
         }, (s) -> true, Component.translatable("argument.entity.options.nbt.description"));
         register("scores", (parser) -> {
            StringReader reader = parser.getReader();
            Map<String, MinMaxBounds.Ints> expected = Maps.newHashMap();
            reader.expect('{');
            reader.skipWhitespace();

            while(reader.canRead() && reader.peek() != '}') {
               reader.skipWhitespace();
               String name = reader.readUnquotedString();
               reader.skipWhitespace();
               reader.expect('=');
               reader.skipWhitespace();
               MinMaxBounds.Ints value = MinMaxBounds.Ints.fromReader(reader);
               expected.put(name, value);
               reader.skipWhitespace();
               if (reader.canRead() && reader.peek() == ',') {
                  reader.skip();
               }
            }

            reader.expect('}');
            if (!expected.isEmpty()) {
               parser.addPredicate((entity) -> {
                  Scoreboard scoreboard = entity.level().getServer().getScoreboard();

                  for(Map.Entry<String, MinMaxBounds.Ints> entry : expected.entrySet()) {
                     Objective objective = scoreboard.getObjective((String)entry.getKey());
                     if (objective == null) {
                        return false;
                     }

                     ReadOnlyScoreInfo scoreInfo = scoreboard.getPlayerScoreInfo(entity, objective);
                     if (scoreInfo == null) {
                        return false;
                     }

                     if (!((MinMaxBounds.Ints)entry.getValue()).matches(scoreInfo.value())) {
                        return false;
                     }
                  }

                  return true;
               });
            }

            parser.setHasScores(true);
         }, (s) -> !s.hasScores(), Component.translatable("argument.entity.options.scores.description"));
         register("advancements", (parser) -> {
            StringReader reader = parser.getReader();
            Map<Identifier, Predicate<AdvancementProgress>> expected = Maps.newHashMap();
            reader.expect('{');
            reader.skipWhitespace();

            while(reader.canRead() && reader.peek() != '}') {
               reader.skipWhitespace();
               Identifier name = Identifier.read(reader);
               reader.skipWhitespace();
               reader.expect('=');
               reader.skipWhitespace();
               if (reader.canRead() && reader.peek() == '{') {
                  Map<String, Predicate<CriterionProgress>> progress = Maps.newHashMap();
                  reader.skipWhitespace();
                  reader.expect('{');
                  reader.skipWhitespace();

                  while(reader.canRead() && reader.peek() != '}') {
                     reader.skipWhitespace();
                     String criterion = reader.readUnquotedString();
                     reader.skipWhitespace();
                     reader.expect('=');
                     reader.skipWhitespace();
                     boolean value = reader.readBoolean();
                     progress.put(criterion, (Predicate)(p) -> p.isDone() == value);
                     reader.skipWhitespace();
                     if (reader.canRead() && reader.peek() == ',') {
                        reader.skip();
                     }
                  }

                  reader.skipWhitespace();
                  reader.expect('}');
                  reader.skipWhitespace();
                  expected.put(name, (Predicate)(p) -> {
                     for(Map.Entry<String, Predicate<CriterionProgress>> entry : progress.entrySet()) {
                        CriterionProgress criterion = p.getCriterion((String)entry.getKey());
                        if (criterion == null || !((Predicate)entry.getValue()).test(criterion)) {
                           return false;
                        }
                     }

                     return true;
                  });
               } else {
                  boolean value = reader.readBoolean();
                  expected.put(name, (Predicate)(p) -> p.isDone() == value);
               }

               reader.skipWhitespace();
               if (reader.canRead() && reader.peek() == ',') {
                  reader.skip();
               }
            }

            reader.expect('}');
            if (!expected.isEmpty()) {
               parser.addPredicate((e) -> {
                  if (!(e instanceof ServerPlayer player)) {
                     return false;
                  } else {
                     PlayerAdvancements advancements = player.getAdvancements();
                     ServerAdvancementManager serverAdvancements = player.level().getServer().getAdvancements();

                     for(Map.Entry<Identifier, Predicate<AdvancementProgress>> entry : expected.entrySet()) {
                        AdvancementHolder advancement = serverAdvancements.get((Identifier)entry.getKey());
                        if (advancement == null || !((Predicate)entry.getValue()).test(advancements.getOrStartProgress(advancement))) {
                           return false;
                        }
                     }

                     return true;
                  }
               });
               parser.setIncludesEntities(false);
            }

            parser.setHasAdvancements(true);
         }, (s) -> !s.hasAdvancements(), Component.translatable("argument.entity.options.advancements.description"));
         register("predicate", (parser) -> {
            boolean inverted = parser.shouldInvertValue();
            ResourceKey<LootItemCondition> id = ResourceKey.create(Registries.PREDICATE, Identifier.read(parser.getReader()));
            parser.addPredicate((entity) -> {
               Level patt0$temp = entity.level();
               if (patt0$temp instanceof ServerLevel level) {
                  Optional<LootItemCondition> condition = level.getServer().reloadableRegistries().lookup().get(id).map(Holder::value);
                  if (condition.isEmpty()) {
                     return false;
                  } else {
                     LootParams lootParams = (new LootParams.Builder(level)).withParameter(LootContextParams.THIS_ENTITY, entity).withParameter(LootContextParams.ORIGIN, entity.position()).create(LootContextParamSets.SELECTOR);
                     LootContext context = (new LootContext.Builder(lootParams)).create(Optional.empty());
                     context.pushVisitedElement(LootContext.createVisitedEntry((LootItemCondition)condition.get()));
                     return inverted ^ ((LootItemCondition)condition.get()).test(context);
                  }
               } else {
                  return false;
               }
            });
         }, (s) -> true, Component.translatable("argument.entity.options.predicate.description"));
      }
   }

   public static Modifier get(final EntitySelectorParser parser, final String key, final int start) throws CommandSyntaxException {
      Option option = (Option)OPTIONS.get(key);
      if (option != null) {
         if (option.canUse.test(parser)) {
            return option.modifier;
         } else {
            throw ERROR_INAPPLICABLE_OPTION.createWithContext(parser.getReader(), key);
         }
      } else {
         parser.getReader().setCursor(start);
         throw ERROR_UNKNOWN_OPTION.createWithContext(parser.getReader(), key);
      }
   }

   public static void suggestNames(final EntitySelectorParser parser, final SuggestionsBuilder builder) {
      String lowerPrefix = builder.getRemaining().toLowerCase(Locale.ROOT);

      for(Map.Entry<String, Option> entry : OPTIONS.entrySet()) {
         if (((Option)entry.getValue()).canUse.test(parser) && ((String)entry.getKey()).toLowerCase(Locale.ROOT).startsWith(lowerPrefix)) {
            builder.suggest((String)entry.getKey() + "=", ((Option)entry.getValue()).description);
         }
      }

   }

   private static record Option(Modifier modifier, Predicate<EntitySelectorParser> canUse, Component description) {
      private Option {
         super();
      }
   }

   @FunctionalInterface
   public interface Modifier {
      void handle(EntitySelectorParser parser) throws CommandSyntaxException;
   }
}
