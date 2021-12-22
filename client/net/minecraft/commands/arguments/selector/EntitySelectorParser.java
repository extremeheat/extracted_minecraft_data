package net.minecraft.commands.arguments.selector;

import com.google.common.primitives.Doubles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.WrappedMinMaxBounds;
import net.minecraft.commands.arguments.selector.options.EntitySelectorOptions;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntitySelectorParser {
   public static final char SYNTAX_SELECTOR_START = '@';
   private static final char SYNTAX_OPTIONS_START = '[';
   private static final char SYNTAX_OPTIONS_END = ']';
   public static final char SYNTAX_OPTIONS_KEY_VALUE_SEPARATOR = '=';
   private static final char SYNTAX_OPTIONS_SEPARATOR = ',';
   public static final char SYNTAX_NOT = '!';
   public static final char SYNTAX_TAG = '#';
   private static final char SELECTOR_NEAREST_PLAYER = 'p';
   private static final char SELECTOR_ALL_PLAYERS = 'a';
   private static final char SELECTOR_RANDOM_PLAYERS = 'r';
   private static final char SELECTOR_CURRENT_ENTITY = 's';
   private static final char SELECTOR_ALL_ENTITIES = 'e';
   public static final SimpleCommandExceptionType ERROR_INVALID_NAME_OR_UUID = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.invalid"));
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_SELECTOR_TYPE = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("argument.entity.selector.unknown", new Object[]{var0});
   });
   public static final SimpleCommandExceptionType ERROR_SELECTORS_NOT_ALLOWED = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.selector.not_allowed"));
   public static final SimpleCommandExceptionType ERROR_MISSING_SELECTOR_TYPE = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.selector.missing"));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_END_OF_OPTIONS = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.options.unterminated"));
   public static final DynamicCommandExceptionType ERROR_EXPECTED_OPTION_VALUE = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("argument.entity.options.valueless", new Object[]{var0});
   });
   public static final BiConsumer<Vec3, List<? extends Entity>> ORDER_ARBITRARY = (var0, var1) -> {
   };
   public static final BiConsumer<Vec3, List<? extends Entity>> ORDER_NEAREST = (var0, var1) -> {
      var1.sort((var1x, var2) -> {
         return Doubles.compare(var1x.distanceToSqr(var0), var2.distanceToSqr(var0));
      });
   };
   public static final BiConsumer<Vec3, List<? extends Entity>> ORDER_FURTHEST = (var0, var1) -> {
      var1.sort((var1x, var2) -> {
         return Doubles.compare(var2.distanceToSqr(var0), var1x.distanceToSqr(var0));
      });
   };
   public static final BiConsumer<Vec3, List<? extends Entity>> ORDER_RANDOM = (var0, var1) -> {
      Collections.shuffle(var1);
   };
   public static final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> SUGGEST_NOTHING = (var0, var1) -> {
      return var0.buildFuture();
   };
   private final StringReader reader;
   private final boolean allowSelectors;
   private int maxResults;
   private boolean includesEntities;
   private boolean worldLimited;
   private MinMaxBounds.Doubles distance;
   private MinMaxBounds.Ints level;
   // $FF: renamed from: x java.lang.Double
   @Nullable
   private Double field_189;
   // $FF: renamed from: y java.lang.Double
   @Nullable
   private Double field_190;
   // $FF: renamed from: z java.lang.Double
   @Nullable
   private Double field_191;
   @Nullable
   private Double deltaX;
   @Nullable
   private Double deltaY;
   @Nullable
   private Double deltaZ;
   private WrappedMinMaxBounds rotX;
   private WrappedMinMaxBounds rotY;
   private Predicate<Entity> predicate;
   private BiConsumer<Vec3, List<? extends Entity>> order;
   private boolean currentEntity;
   @Nullable
   private String playerName;
   private int startPosition;
   @Nullable
   private UUID entityUUID;
   private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestions;
   private boolean hasNameEquals;
   private boolean hasNameNotEquals;
   private boolean isLimited;
   private boolean isSorted;
   private boolean hasGamemodeEquals;
   private boolean hasGamemodeNotEquals;
   private boolean hasTeamEquals;
   private boolean hasTeamNotEquals;
   @Nullable
   private EntityType<?> type;
   private boolean typeInverse;
   private boolean hasScores;
   private boolean hasAdvancements;
   private boolean usesSelectors;

   public EntitySelectorParser(StringReader var1) {
      this(var1, true);
   }

   public EntitySelectorParser(StringReader var1, boolean var2) {
      super();
      this.distance = MinMaxBounds.Doubles.ANY;
      this.level = MinMaxBounds.Ints.ANY;
      this.rotX = WrappedMinMaxBounds.ANY;
      this.rotY = WrappedMinMaxBounds.ANY;
      this.predicate = (var0) -> {
         return true;
      };
      this.order = ORDER_ARBITRARY;
      this.suggestions = SUGGEST_NOTHING;
      this.reader = var1;
      this.allowSelectors = var2;
   }

   public EntitySelector getSelector() {
      AABB var1;
      if (this.deltaX == null && this.deltaY == null && this.deltaZ == null) {
         if (this.distance.getMax() != null) {
            double var2 = (Double)this.distance.getMax();
            var1 = new AABB(-var2, -var2, -var2, var2 + 1.0D, var2 + 1.0D, var2 + 1.0D);
         } else {
            var1 = null;
         }
      } else {
         var1 = this.createAabb(this.deltaX == null ? 0.0D : this.deltaX, this.deltaY == null ? 0.0D : this.deltaY, this.deltaZ == null ? 0.0D : this.deltaZ);
      }

      Function var4;
      if (this.field_189 == null && this.field_190 == null && this.field_191 == null) {
         var4 = (var0) -> {
            return var0;
         };
      } else {
         var4 = (var1x) -> {
            return new Vec3(this.field_189 == null ? var1x.field_414 : this.field_189, this.field_190 == null ? var1x.field_415 : this.field_190, this.field_191 == null ? var1x.field_416 : this.field_191);
         };
      }

      return new EntitySelector(this.maxResults, this.includesEntities, this.worldLimited, this.predicate, this.distance, var4, var1, this.order, this.currentEntity, this.playerName, this.entityUUID, this.type, this.usesSelectors);
   }

   private AABB createAabb(double var1, double var3, double var5) {
      boolean var7 = var1 < 0.0D;
      boolean var8 = var3 < 0.0D;
      boolean var9 = var5 < 0.0D;
      double var10 = var7 ? var1 : 0.0D;
      double var12 = var8 ? var3 : 0.0D;
      double var14 = var9 ? var5 : 0.0D;
      double var16 = (var7 ? 0.0D : var1) + 1.0D;
      double var18 = (var8 ? 0.0D : var3) + 1.0D;
      double var20 = (var9 ? 0.0D : var5) + 1.0D;
      return new AABB(var10, var12, var14, var16, var18, var20);
   }

   private void finalizePredicates() {
      if (this.rotX != WrappedMinMaxBounds.ANY) {
         this.predicate = this.predicate.and(this.createRotationPredicate(this.rotX, Entity::getXRot));
      }

      if (this.rotY != WrappedMinMaxBounds.ANY) {
         this.predicate = this.predicate.and(this.createRotationPredicate(this.rotY, Entity::getYRot));
      }

      if (!this.level.isAny()) {
         this.predicate = this.predicate.and((var1) -> {
            return !(var1 instanceof ServerPlayer) ? false : this.level.matches(((ServerPlayer)var1).experienceLevel);
         });
      }

   }

   private Predicate<Entity> createRotationPredicate(WrappedMinMaxBounds var1, ToDoubleFunction<Entity> var2) {
      double var3 = (double)Mth.wrapDegrees(var1.getMin() == null ? 0.0F : var1.getMin());
      double var5 = (double)Mth.wrapDegrees(var1.getMax() == null ? 359.0F : var1.getMax());
      return (var5x) -> {
         double var6 = Mth.wrapDegrees(var2.applyAsDouble(var5x));
         if (var3 > var5) {
            return var6 >= var3 || var6 <= var5;
         } else {
            return var6 >= var3 && var6 <= var5;
         }
      };
   }

   protected void parseSelector() throws CommandSyntaxException {
      this.usesSelectors = true;
      this.suggestions = this::suggestSelector;
      if (!this.reader.canRead()) {
         throw ERROR_MISSING_SELECTOR_TYPE.createWithContext(this.reader);
      } else {
         int var1 = this.reader.getCursor();
         char var2 = this.reader.read();
         if (var2 == 'p') {
            this.maxResults = 1;
            this.includesEntities = false;
            this.order = ORDER_NEAREST;
            this.limitToType(EntityType.PLAYER);
         } else if (var2 == 'a') {
            this.maxResults = 2147483647;
            this.includesEntities = false;
            this.order = ORDER_ARBITRARY;
            this.limitToType(EntityType.PLAYER);
         } else if (var2 == 'r') {
            this.maxResults = 1;
            this.includesEntities = false;
            this.order = ORDER_RANDOM;
            this.limitToType(EntityType.PLAYER);
         } else if (var2 == 's') {
            this.maxResults = 1;
            this.includesEntities = true;
            this.currentEntity = true;
         } else {
            if (var2 != 'e') {
               this.reader.setCursor(var1);
               throw ERROR_UNKNOWN_SELECTOR_TYPE.createWithContext(this.reader, "@" + String.valueOf(var2));
            }

            this.maxResults = 2147483647;
            this.includesEntities = true;
            this.order = ORDER_ARBITRARY;
            this.predicate = Entity::isAlive;
         }

         this.suggestions = this::suggestOpenOptions;
         if (this.reader.canRead() && this.reader.peek() == '[') {
            this.reader.skip();
            this.suggestions = this::suggestOptionsKeyOrClose;
            this.parseOptions();
         }

      }
   }

   protected void parseNameOrUUID() throws CommandSyntaxException {
      if (this.reader.canRead()) {
         this.suggestions = this::suggestName;
      }

      int var1 = this.reader.getCursor();
      String var2 = this.reader.readString();

      try {
         this.entityUUID = UUID.fromString(var2);
         this.includesEntities = true;
      } catch (IllegalArgumentException var4) {
         if (var2.isEmpty() || var2.length() > 16) {
            this.reader.setCursor(var1);
            throw ERROR_INVALID_NAME_OR_UUID.createWithContext(this.reader);
         }

         this.includesEntities = false;
         this.playerName = var2;
      }

      this.maxResults = 1;
   }

   protected void parseOptions() throws CommandSyntaxException {
      this.suggestions = this::suggestOptionsKey;
      this.reader.skipWhitespace();

      while(this.reader.canRead() && this.reader.peek() != ']') {
         this.reader.skipWhitespace();
         int var1 = this.reader.getCursor();
         String var2 = this.reader.readString();
         EntitySelectorOptions.Modifier var3 = EntitySelectorOptions.get(this, var2, var1);
         this.reader.skipWhitespace();
         if (this.reader.canRead() && this.reader.peek() == '=') {
            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestions = SUGGEST_NOTHING;
            var3.handle(this);
            this.reader.skipWhitespace();
            this.suggestions = this::suggestOptionsNextOrClose;
            if (!this.reader.canRead()) {
               continue;
            }

            if (this.reader.peek() == ',') {
               this.reader.skip();
               this.suggestions = this::suggestOptionsKey;
               continue;
            }

            if (this.reader.peek() != ']') {
               throw ERROR_EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
            }
            break;
         }

         this.reader.setCursor(var1);
         throw ERROR_EXPECTED_OPTION_VALUE.createWithContext(this.reader, var2);
      }

      if (this.reader.canRead()) {
         this.reader.skip();
         this.suggestions = SUGGEST_NOTHING;
      } else {
         throw ERROR_EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
      }
   }

   public boolean shouldInvertValue() {
      this.reader.skipWhitespace();
      if (this.reader.canRead() && this.reader.peek() == '!') {
         this.reader.skip();
         this.reader.skipWhitespace();
         return true;
      } else {
         return false;
      }
   }

   public boolean isTag() {
      this.reader.skipWhitespace();
      if (this.reader.canRead() && this.reader.peek() == '#') {
         this.reader.skip();
         this.reader.skipWhitespace();
         return true;
      } else {
         return false;
      }
   }

   public StringReader getReader() {
      return this.reader;
   }

   public void addPredicate(Predicate<Entity> var1) {
      this.predicate = this.predicate.and(var1);
   }

   public void setWorldLimited() {
      this.worldLimited = true;
   }

   public MinMaxBounds.Doubles getDistance() {
      return this.distance;
   }

   public void setDistance(MinMaxBounds.Doubles var1) {
      this.distance = var1;
   }

   public MinMaxBounds.Ints getLevel() {
      return this.level;
   }

   public void setLevel(MinMaxBounds.Ints var1) {
      this.level = var1;
   }

   public WrappedMinMaxBounds getRotX() {
      return this.rotX;
   }

   public void setRotX(WrappedMinMaxBounds var1) {
      this.rotX = var1;
   }

   public WrappedMinMaxBounds getRotY() {
      return this.rotY;
   }

   public void setRotY(WrappedMinMaxBounds var1) {
      this.rotY = var1;
   }

   @Nullable
   public Double getX() {
      return this.field_189;
   }

   @Nullable
   public Double getY() {
      return this.field_190;
   }

   @Nullable
   public Double getZ() {
      return this.field_191;
   }

   public void setX(double var1) {
      this.field_189 = var1;
   }

   public void setY(double var1) {
      this.field_190 = var1;
   }

   public void setZ(double var1) {
      this.field_191 = var1;
   }

   public void setDeltaX(double var1) {
      this.deltaX = var1;
   }

   public void setDeltaY(double var1) {
      this.deltaY = var1;
   }

   public void setDeltaZ(double var1) {
      this.deltaZ = var1;
   }

   @Nullable
   public Double getDeltaX() {
      return this.deltaX;
   }

   @Nullable
   public Double getDeltaY() {
      return this.deltaY;
   }

   @Nullable
   public Double getDeltaZ() {
      return this.deltaZ;
   }

   public void setMaxResults(int var1) {
      this.maxResults = var1;
   }

   public void setIncludesEntities(boolean var1) {
      this.includesEntities = var1;
   }

   public BiConsumer<Vec3, List<? extends Entity>> getOrder() {
      return this.order;
   }

   public void setOrder(BiConsumer<Vec3, List<? extends Entity>> var1) {
      this.order = var1;
   }

   public EntitySelector parse() throws CommandSyntaxException {
      this.startPosition = this.reader.getCursor();
      this.suggestions = this::suggestNameOrSelector;
      if (this.reader.canRead() && this.reader.peek() == '@') {
         if (!this.allowSelectors) {
            throw ERROR_SELECTORS_NOT_ALLOWED.createWithContext(this.reader);
         }

         this.reader.skip();
         this.parseSelector();
      } else {
         this.parseNameOrUUID();
      }

      this.finalizePredicates();
      return this.getSelector();
   }

   private static void fillSelectorSuggestions(SuggestionsBuilder var0) {
      var0.suggest("@p", new TranslatableComponent("argument.entity.selector.nearestPlayer"));
      var0.suggest("@a", new TranslatableComponent("argument.entity.selector.allPlayers"));
      var0.suggest("@r", new TranslatableComponent("argument.entity.selector.randomPlayer"));
      var0.suggest("@s", new TranslatableComponent("argument.entity.selector.self"));
      var0.suggest("@e", new TranslatableComponent("argument.entity.selector.allEntities"));
   }

   private CompletableFuture<Suggestions> suggestNameOrSelector(SuggestionsBuilder var1, Consumer<SuggestionsBuilder> var2) {
      var2.accept(var1);
      if (this.allowSelectors) {
         fillSelectorSuggestions(var1);
      }

      return var1.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestName(SuggestionsBuilder var1, Consumer<SuggestionsBuilder> var2) {
      SuggestionsBuilder var3 = var1.createOffset(this.startPosition);
      var2.accept(var3);
      return var1.add(var3).buildFuture();
   }

   private CompletableFuture<Suggestions> suggestSelector(SuggestionsBuilder var1, Consumer<SuggestionsBuilder> var2) {
      SuggestionsBuilder var3 = var1.createOffset(var1.getStart() - 1);
      fillSelectorSuggestions(var3);
      var1.add(var3);
      return var1.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOpenOptions(SuggestionsBuilder var1, Consumer<SuggestionsBuilder> var2) {
      var1.suggest(String.valueOf('['));
      return var1.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOptionsKeyOrClose(SuggestionsBuilder var1, Consumer<SuggestionsBuilder> var2) {
      var1.suggest(String.valueOf(']'));
      EntitySelectorOptions.suggestNames(this, var1);
      return var1.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOptionsKey(SuggestionsBuilder var1, Consumer<SuggestionsBuilder> var2) {
      EntitySelectorOptions.suggestNames(this, var1);
      return var1.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOptionsNextOrClose(SuggestionsBuilder var1, Consumer<SuggestionsBuilder> var2) {
      var1.suggest(String.valueOf(','));
      var1.suggest(String.valueOf(']'));
      return var1.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestEquals(SuggestionsBuilder var1, Consumer<SuggestionsBuilder> var2) {
      var1.suggest(String.valueOf('='));
      return var1.buildFuture();
   }

   public boolean isCurrentEntity() {
      return this.currentEntity;
   }

   public void setSuggestions(BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> var1) {
      this.suggestions = var1;
   }

   public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder var1, Consumer<SuggestionsBuilder> var2) {
      return (CompletableFuture)this.suggestions.apply(var1.createOffset(this.reader.getCursor()), var2);
   }

   public boolean hasNameEquals() {
      return this.hasNameEquals;
   }

   public void setHasNameEquals(boolean var1) {
      this.hasNameEquals = var1;
   }

   public boolean hasNameNotEquals() {
      return this.hasNameNotEquals;
   }

   public void setHasNameNotEquals(boolean var1) {
      this.hasNameNotEquals = var1;
   }

   public boolean isLimited() {
      return this.isLimited;
   }

   public void setLimited(boolean var1) {
      this.isLimited = var1;
   }

   public boolean isSorted() {
      return this.isSorted;
   }

   public void setSorted(boolean var1) {
      this.isSorted = var1;
   }

   public boolean hasGamemodeEquals() {
      return this.hasGamemodeEquals;
   }

   public void setHasGamemodeEquals(boolean var1) {
      this.hasGamemodeEquals = var1;
   }

   public boolean hasGamemodeNotEquals() {
      return this.hasGamemodeNotEquals;
   }

   public void setHasGamemodeNotEquals(boolean var1) {
      this.hasGamemodeNotEquals = var1;
   }

   public boolean hasTeamEquals() {
      return this.hasTeamEquals;
   }

   public void setHasTeamEquals(boolean var1) {
      this.hasTeamEquals = var1;
   }

   public boolean hasTeamNotEquals() {
      return this.hasTeamNotEquals;
   }

   public void setHasTeamNotEquals(boolean var1) {
      this.hasTeamNotEquals = var1;
   }

   public void limitToType(EntityType<?> var1) {
      this.type = var1;
   }

   public void setTypeLimitedInversely() {
      this.typeInverse = true;
   }

   public boolean isTypeLimited() {
      return this.type != null;
   }

   public boolean isTypeLimitedInversely() {
      return this.typeInverse;
   }

   public boolean hasScores() {
      return this.hasScores;
   }

   public void setHasScores(boolean var1) {
      this.hasScores = var1;
   }

   public boolean hasAdvancements() {
      return this.hasAdvancements;
   }

   public void setHasAdvancements(boolean var1) {
      this.hasAdvancements = var1;
   }
}
