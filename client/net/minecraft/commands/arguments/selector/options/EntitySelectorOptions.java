package net.minecraft.commands.arguments.selector.options;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.WrappedMinMaxBounds;
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
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

public class EntitySelectorOptions {
   private static final Map<String, Option> OPTIONS = Maps.newHashMap();
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_OPTION = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("argument.entity.options.unknown", var0);
   });
   public static final DynamicCommandExceptionType ERROR_INAPPLICABLE_OPTION = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("argument.entity.options.inapplicable", var0);
   });
   public static final SimpleCommandExceptionType ERROR_RANGE_NEGATIVE = new SimpleCommandExceptionType(Component.translatable("argument.entity.options.distance.negative"));
   public static final SimpleCommandExceptionType ERROR_LEVEL_NEGATIVE = new SimpleCommandExceptionType(Component.translatable("argument.entity.options.level.negative"));
   public static final SimpleCommandExceptionType ERROR_LIMIT_TOO_SMALL = new SimpleCommandExceptionType(Component.translatable("argument.entity.options.limit.toosmall"));
   public static final DynamicCommandExceptionType ERROR_SORT_UNKNOWN = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("argument.entity.options.sort.irreversible", var0);
   });
   public static final DynamicCommandExceptionType ERROR_GAME_MODE_INVALID = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("argument.entity.options.mode.invalid", var0);
   });
   public static final DynamicCommandExceptionType ERROR_ENTITY_TYPE_INVALID = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("argument.entity.options.type.invalid", var0);
   });

   public EntitySelectorOptions() {
      super();
   }

   private static void register(String var0, Modifier var1, Predicate<EntitySelectorParser> var2, Component var3) {
      OPTIONS.put(var0, new Option(var1, var2, var3));
   }

   public static void bootStrap() {
      if (OPTIONS.isEmpty()) {
         register("name", (var0) -> {
            int var1 = var0.getReader().getCursor();
            boolean var2 = var0.shouldInvertValue();
            String var3 = var0.getReader().readString();
            if (var0.hasNameNotEquals() && !var2) {
               var0.getReader().setCursor(var1);
               throw ERROR_INAPPLICABLE_OPTION.createWithContext(var0.getReader(), "name");
            } else {
               if (var2) {
                  var0.setHasNameNotEquals(true);
               } else {
                  var0.setHasNameEquals(true);
               }

               var0.addPredicate((var2x) -> {
                  return var2x.getName().getString().equals(var3) != var2;
               });
            }
         }, (var0) -> {
            return !var0.hasNameEquals();
         }, Component.translatable("argument.entity.options.name.description"));
         register("distance", (var0) -> {
            int var1 = var0.getReader().getCursor();
            MinMaxBounds.Doubles var2 = MinMaxBounds.Doubles.fromReader(var0.getReader());
            if ((!var2.min().isPresent() || !((Double)var2.min().get() < 0.0)) && (!var2.max().isPresent() || !((Double)var2.max().get() < 0.0))) {
               var0.setDistance(var2);
               var0.setWorldLimited();
            } else {
               var0.getReader().setCursor(var1);
               throw ERROR_RANGE_NEGATIVE.createWithContext(var0.getReader());
            }
         }, (var0) -> {
            return var0.getDistance().isAny();
         }, Component.translatable("argument.entity.options.distance.description"));
         register("level", (var0) -> {
            int var1 = var0.getReader().getCursor();
            MinMaxBounds.Ints var2 = MinMaxBounds.Ints.fromReader(var0.getReader());
            if ((!var2.min().isPresent() || (Integer)var2.min().get() >= 0) && (!var2.max().isPresent() || (Integer)var2.max().get() >= 0)) {
               var0.setLevel(var2);
               var0.setIncludesEntities(false);
            } else {
               var0.getReader().setCursor(var1);
               throw ERROR_LEVEL_NEGATIVE.createWithContext(var0.getReader());
            }
         }, (var0) -> {
            return var0.getLevel().isAny();
         }, Component.translatable("argument.entity.options.level.description"));
         register("x", (var0) -> {
            var0.setWorldLimited();
            var0.setX(var0.getReader().readDouble());
         }, (var0) -> {
            return var0.getX() == null;
         }, Component.translatable("argument.entity.options.x.description"));
         register("y", (var0) -> {
            var0.setWorldLimited();
            var0.setY(var0.getReader().readDouble());
         }, (var0) -> {
            return var0.getY() == null;
         }, Component.translatable("argument.entity.options.y.description"));
         register("z", (var0) -> {
            var0.setWorldLimited();
            var0.setZ(var0.getReader().readDouble());
         }, (var0) -> {
            return var0.getZ() == null;
         }, Component.translatable("argument.entity.options.z.description"));
         register("dx", (var0) -> {
            var0.setWorldLimited();
            var0.setDeltaX(var0.getReader().readDouble());
         }, (var0) -> {
            return var0.getDeltaX() == null;
         }, Component.translatable("argument.entity.options.dx.description"));
         register("dy", (var0) -> {
            var0.setWorldLimited();
            var0.setDeltaY(var0.getReader().readDouble());
         }, (var0) -> {
            return var0.getDeltaY() == null;
         }, Component.translatable("argument.entity.options.dy.description"));
         register("dz", (var0) -> {
            var0.setWorldLimited();
            var0.setDeltaZ(var0.getReader().readDouble());
         }, (var0) -> {
            return var0.getDeltaZ() == null;
         }, Component.translatable("argument.entity.options.dz.description"));
         register("x_rotation", (var0) -> {
            var0.setRotX(WrappedMinMaxBounds.fromReader(var0.getReader(), true, Mth::wrapDegrees));
         }, (var0) -> {
            return var0.getRotX() == WrappedMinMaxBounds.ANY;
         }, Component.translatable("argument.entity.options.x_rotation.description"));
         register("y_rotation", (var0) -> {
            var0.setRotY(WrappedMinMaxBounds.fromReader(var0.getReader(), true, Mth::wrapDegrees));
         }, (var0) -> {
            return var0.getRotY() == WrappedMinMaxBounds.ANY;
         }, Component.translatable("argument.entity.options.y_rotation.description"));
         register("limit", (var0) -> {
            int var1 = var0.getReader().getCursor();
            int var2 = var0.getReader().readInt();
            if (var2 < 1) {
               var0.getReader().setCursor(var1);
               throw ERROR_LIMIT_TOO_SMALL.createWithContext(var0.getReader());
            } else {
               var0.setMaxResults(var2);
               var0.setLimited(true);
            }
         }, (var0) -> {
            return !var0.isCurrentEntity() && !var0.isLimited();
         }, Component.translatable("argument.entity.options.limit.description"));
         register("sort", (var0) -> {
            int var1 = var0.getReader().getCursor();
            String var2 = var0.getReader().readUnquotedString();
            var0.setSuggestions((var0x, var1x) -> {
               return SharedSuggestionProvider.suggest((Iterable)Arrays.asList("nearest", "furthest", "random", "arbitrary"), var0x);
            });
            BiConsumer var10001;
            switch (var2) {
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
                  var0.getReader().setCursor(var1);
                  throw ERROR_SORT_UNKNOWN.createWithContext(var0.getReader(), var2);
            }

            var0.setOrder(var10001);
            var0.setSorted(true);
         }, (var0) -> {
            return !var0.isCurrentEntity() && !var0.isSorted();
         }, Component.translatable("argument.entity.options.sort.description"));
         register("gamemode", (var0) -> {
            var0.setSuggestions((var1x, var2x) -> {
               String var3 = var1x.getRemaining().toLowerCase(Locale.ROOT);
               boolean var4 = !var0.hasGamemodeNotEquals();
               boolean var5 = true;
               if (!var3.isEmpty()) {
                  if (var3.charAt(0) == '!') {
                     var4 = false;
                     var3 = var3.substring(1);
                  } else {
                     var5 = false;
                  }
               }

               GameType[] var6 = GameType.values();
               int var7 = var6.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  GameType var9 = var6[var8];
                  if (var9.getName().toLowerCase(Locale.ROOT).startsWith(var3)) {
                     if (var5) {
                        var1x.suggest("!" + var9.getName());
                     }

                     if (var4) {
                        var1x.suggest(var9.getName());
                     }
                  }
               }

               return var1x.buildFuture();
            });
            int var1 = var0.getReader().getCursor();
            boolean var2 = var0.shouldInvertValue();
            if (var0.hasGamemodeNotEquals() && !var2) {
               var0.getReader().setCursor(var1);
               throw ERROR_INAPPLICABLE_OPTION.createWithContext(var0.getReader(), "gamemode");
            } else {
               String var3 = var0.getReader().readUnquotedString();
               GameType var4 = GameType.byName(var3, (GameType)null);
               if (var4 == null) {
                  var0.getReader().setCursor(var1);
                  throw ERROR_GAME_MODE_INVALID.createWithContext(var0.getReader(), var3);
               } else {
                  var0.setIncludesEntities(false);
                  var0.addPredicate((var2x) -> {
                     if (!(var2x instanceof ServerPlayer)) {
                        return false;
                     } else {
                        GameType var3 = ((ServerPlayer)var2x).gameMode.getGameModeForPlayer();
                        return var2 ? var3 != var4 : var3 == var4;
                     }
                  });
                  if (var2) {
                     var0.setHasGamemodeNotEquals(true);
                  } else {
                     var0.setHasGamemodeEquals(true);
                  }

               }
            }
         }, (var0) -> {
            return !var0.hasGamemodeEquals();
         }, Component.translatable("argument.entity.options.gamemode.description"));
         register("team", (var0) -> {
            boolean var1 = var0.shouldInvertValue();
            String var2 = var0.getReader().readUnquotedString();
            var0.addPredicate((var2x) -> {
               if (!(var2x instanceof LivingEntity)) {
                  return false;
               } else {
                  PlayerTeam var3 = var2x.getTeam();
                  String var4 = var3 == null ? "" : ((Team)var3).getName();
                  return var4.equals(var2) != var1;
               }
            });
            if (var1) {
               var0.setHasTeamNotEquals(true);
            } else {
               var0.setHasTeamEquals(true);
            }

         }, (var0) -> {
            return !var0.hasTeamEquals();
         }, Component.translatable("argument.entity.options.team.description"));
         register("type", (var0) -> {
            var0.setSuggestions((var1x, var2x) -> {
               SharedSuggestionProvider.suggestResource((Iterable)BuiltInRegistries.ENTITY_TYPE.keySet(), var1x, String.valueOf('!'));
               SharedSuggestionProvider.suggestResource(BuiltInRegistries.ENTITY_TYPE.getTagNames().map(TagKey::location), var1x, "!#");
               if (!var0.isTypeLimitedInversely()) {
                  SharedSuggestionProvider.suggestResource((Iterable)BuiltInRegistries.ENTITY_TYPE.keySet(), var1x);
                  SharedSuggestionProvider.suggestResource(BuiltInRegistries.ENTITY_TYPE.getTagNames().map(TagKey::location), var1x, String.valueOf('#'));
               }

               return var1x.buildFuture();
            });
            int var1 = var0.getReader().getCursor();
            boolean var2 = var0.shouldInvertValue();
            if (var0.isTypeLimitedInversely() && !var2) {
               var0.getReader().setCursor(var1);
               throw ERROR_INAPPLICABLE_OPTION.createWithContext(var0.getReader(), "type");
            } else {
               if (var2) {
                  var0.setTypeLimitedInversely();
               }

               if (var0.isTag()) {
                  TagKey var3 = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.read(var0.getReader()));
                  var0.addPredicate((var2x) -> {
                     return var2x.getType().is(var3) != var2;
                  });
               } else {
                  ResourceLocation var5 = ResourceLocation.read(var0.getReader());
                  EntityType var4 = (EntityType)BuiltInRegistries.ENTITY_TYPE.getOptional(var5).orElseThrow(() -> {
                     var0.getReader().setCursor(var1);
                     return ERROR_ENTITY_TYPE_INVALID.createWithContext(var0.getReader(), var5.toString());
                  });
                  if (Objects.equals(EntityType.PLAYER, var4) && !var2) {
                     var0.setIncludesEntities(false);
                  }

                  var0.addPredicate((var2x) -> {
                     return Objects.equals(var4, var2x.getType()) != var2;
                  });
                  if (!var2) {
                     var0.limitToType(var4);
                  }
               }

            }
         }, (var0) -> {
            return !var0.isTypeLimited();
         }, Component.translatable("argument.entity.options.type.description"));
         register("tag", (var0) -> {
            boolean var1 = var0.shouldInvertValue();
            String var2 = var0.getReader().readUnquotedString();
            var0.addPredicate((var2x) -> {
               if ("".equals(var2)) {
                  return var2x.getTags().isEmpty() != var1;
               } else {
                  return var2x.getTags().contains(var2) != var1;
               }
            });
         }, (var0) -> {
            return true;
         }, Component.translatable("argument.entity.options.tag.description"));
         register("nbt", (var0) -> {
            boolean var1 = var0.shouldInvertValue();
            CompoundTag var2 = (new TagParser(var0.getReader())).readStruct();
            var0.addPredicate((var2x) -> {
               CompoundTag var3 = var2x.saveWithoutId(new CompoundTag());
               if (var2x instanceof ServerPlayer var4) {
                  ItemStack var5 = var4.getInventory().getSelected();
                  if (!var5.isEmpty()) {
                     var3.put("SelectedItem", var5.save(var4.registryAccess()));
                  }
               }

               return NbtUtils.compareNbt(var2, var3, true) != var1;
            });
         }, (var0) -> {
            return true;
         }, Component.translatable("argument.entity.options.nbt.description"));
         register("scores", (var0) -> {
            StringReader var1 = var0.getReader();
            HashMap var2 = Maps.newHashMap();
            var1.expect('{');
            var1.skipWhitespace();

            while(var1.canRead() && var1.peek() != '}') {
               var1.skipWhitespace();
               String var3 = var1.readUnquotedString();
               var1.skipWhitespace();
               var1.expect('=');
               var1.skipWhitespace();
               MinMaxBounds.Ints var4 = MinMaxBounds.Ints.fromReader(var1);
               var2.put(var3, var4);
               var1.skipWhitespace();
               if (var1.canRead() && var1.peek() == ',') {
                  var1.skip();
               }
            }

            var1.expect('}');
            if (!var2.isEmpty()) {
               var0.addPredicate((var1x) -> {
                  ServerScoreboard var2x = var1x.getServer().getScoreboard();
                  Iterator var3 = var2.entrySet().iterator();

                  Map.Entry var4;
                  ReadOnlyScoreInfo var6;
                  do {
                     if (!var3.hasNext()) {
                        return true;
                     }

                     var4 = (Map.Entry)var3.next();
                     Objective var5 = ((Scoreboard)var2x).getObjective((String)var4.getKey());
                     if (var5 == null) {
                        return false;
                     }

                     var6 = ((Scoreboard)var2x).getPlayerScoreInfo(var1x, var5);
                     if (var6 == null) {
                        return false;
                     }
                  } while(((MinMaxBounds.Ints)var4.getValue()).matches(var6.value()));

                  return false;
               });
            }

            var0.setHasScores(true);
         }, (var0) -> {
            return !var0.hasScores();
         }, Component.translatable("argument.entity.options.scores.description"));
         register("advancements", (var0) -> {
            StringReader var1 = var0.getReader();
            HashMap var2 = Maps.newHashMap();
            var1.expect('{');
            var1.skipWhitespace();

            while(var1.canRead() && var1.peek() != '}') {
               var1.skipWhitespace();
               ResourceLocation var3 = ResourceLocation.read(var1);
               var1.skipWhitespace();
               var1.expect('=');
               var1.skipWhitespace();
               if (var1.canRead() && var1.peek() == '{') {
                  HashMap var7 = Maps.newHashMap();
                  var1.skipWhitespace();
                  var1.expect('{');
                  var1.skipWhitespace();

                  while(var1.canRead() && var1.peek() != '}') {
                     var1.skipWhitespace();
                     String var5 = var1.readUnquotedString();
                     var1.skipWhitespace();
                     var1.expect('=');
                     var1.skipWhitespace();
                     boolean var6 = var1.readBoolean();
                     var7.put(var5, (var1x) -> {
                        return var1x.isDone() == var6;
                     });
                     var1.skipWhitespace();
                     if (var1.canRead() && var1.peek() == ',') {
                        var1.skip();
                     }
                  }

                  var1.skipWhitespace();
                  var1.expect('}');
                  var1.skipWhitespace();
                  var2.put(var3, (var1x) -> {
                     Iterator var2 = var7.entrySet().iterator();

                     Map.Entry var3;
                     CriterionProgress var4;
                     do {
                        if (!var2.hasNext()) {
                           return true;
                        }

                        var3 = (Map.Entry)var2.next();
                        var4 = var1x.getCriterion((String)var3.getKey());
                     } while(var4 != null && ((Predicate)var3.getValue()).test(var4));

                     return false;
                  });
               } else {
                  boolean var4 = var1.readBoolean();
                  var2.put(var3, (var1x) -> {
                     return var1x.isDone() == var4;
                  });
               }

               var1.skipWhitespace();
               if (var1.canRead() && var1.peek() == ',') {
                  var1.skip();
               }
            }

            var1.expect('}');
            if (!var2.isEmpty()) {
               var0.addPredicate((var1x) -> {
                  if (!(var1x instanceof ServerPlayer var2x)) {
                     return false;
                  } else {
                     PlayerAdvancements var3 = var2x.getAdvancements();
                     ServerAdvancementManager var4 = var2x.getServer().getAdvancements();
                     Iterator var5 = var2.entrySet().iterator();

                     Map.Entry var6;
                     AdvancementHolder var7;
                     do {
                        if (!var5.hasNext()) {
                           return true;
                        }

                        var6 = (Map.Entry)var5.next();
                        var7 = var4.get((ResourceLocation)var6.getKey());
                     } while(var7 != null && ((Predicate)var6.getValue()).test(var3.getOrStartProgress(var7)));

                     return false;
                  }
               });
               var0.setIncludesEntities(false);
            }

            var0.setHasAdvancements(true);
         }, (var0) -> {
            return !var0.hasAdvancements();
         }, Component.translatable("argument.entity.options.advancements.description"));
         register("predicate", (var0) -> {
            boolean var1 = var0.shouldInvertValue();
            ResourceKey var2 = ResourceKey.create(Registries.PREDICATE, ResourceLocation.read(var0.getReader()));
            var0.addPredicate((var2x) -> {
               if (!(var2x.level() instanceof ServerLevel)) {
                  return false;
               } else {
                  ServerLevel var3 = (ServerLevel)var2x.level();
                  Optional var4 = var3.getServer().reloadableRegistries().lookup().get(Registries.PREDICATE, var2).map(Holder::value);
                  if (var4.isEmpty()) {
                     return false;
                  } else {
                     LootParams var5 = (new LootParams.Builder(var3)).withParameter(LootContextParams.THIS_ENTITY, var2x).withParameter(LootContextParams.ORIGIN, var2x.position()).create(LootContextParamSets.SELECTOR);
                     LootContext var6 = (new LootContext.Builder(var5)).create(Optional.empty());
                     var6.pushVisitedElement(LootContext.createVisitedEntry((LootItemCondition)var4.get()));
                     return var1 ^ ((LootItemCondition)var4.get()).test(var6);
                  }
               }
            });
         }, (var0) -> {
            return true;
         }, Component.translatable("argument.entity.options.predicate.description"));
      }
   }

   public static Modifier get(EntitySelectorParser var0, String var1, int var2) throws CommandSyntaxException {
      Option var3 = (Option)OPTIONS.get(var1);
      if (var3 != null) {
         if (var3.canUse.test(var0)) {
            return var3.modifier;
         } else {
            throw ERROR_INAPPLICABLE_OPTION.createWithContext(var0.getReader(), var1);
         }
      } else {
         var0.getReader().setCursor(var2);
         throw ERROR_UNKNOWN_OPTION.createWithContext(var0.getReader(), var1);
      }
   }

   public static void suggestNames(EntitySelectorParser var0, SuggestionsBuilder var1) {
      String var2 = var1.getRemaining().toLowerCase(Locale.ROOT);
      Iterator var3 = OPTIONS.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry var4 = (Map.Entry)var3.next();
         if (((Option)var4.getValue()).canUse.test(var0) && ((String)var4.getKey()).toLowerCase(Locale.ROOT).startsWith(var2)) {
            var1.suggest((String)var4.getKey() + "=", ((Option)var4.getValue()).description);
         }
      }

   }

   static record Option(Modifier modifier, Predicate<EntitySelectorParser> canUse, Component description) {
      final Modifier modifier;
      final Predicate<EntitySelectorParser> canUse;
      final Component description;

      Option(Modifier modifier, Predicate<EntitySelectorParser> canUse, Component description) {
         super();
         this.modifier = modifier;
         this.canUse = canUse;
         this.description = description;
      }

      public Modifier modifier() {
         return this.modifier;
      }

      public Predicate<EntitySelectorParser> canUse() {
         return this.canUse;
      }

      public Component description() {
         return this.description;
      }
   }

   public interface Modifier {
      void handle(EntitySelectorParser var1) throws CommandSyntaxException;
   }
}
