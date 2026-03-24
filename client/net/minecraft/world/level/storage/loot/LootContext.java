package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Sets;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.Nullable;

public class LootContext {
   private final LootParams params;
   private final RandomSource random;
   private final HolderGetter.Provider lootDataResolver;
   private final Set<VisitedEntry<?>> visitedElements = Sets.newLinkedHashSet();

   private LootContext(final LootParams params, final RandomSource random, final HolderGetter.Provider lootDataResolver) {
      super();
      this.params = params;
      this.random = random;
      this.lootDataResolver = lootDataResolver;
   }

   public boolean hasParameter(final ContextKey<?> key) {
      return this.params.contextMap().has(key);
   }

   public <T> T getParameter(final ContextKey<T> key) {
      return (T)this.params.contextMap().getOrThrow(key);
   }

   public <T> @Nullable T getOptionalParameter(final ContextKey<T> key) {
      return (T)this.params.contextMap().getOptional(key);
   }

   public void addDynamicDrops(final Identifier location, final Consumer<ItemStack> output) {
      this.params.addDynamicDrops(location, output);
   }

   public boolean hasVisitedElement(final VisitedEntry<?> element) {
      return this.visitedElements.contains(element);
   }

   public boolean pushVisitedElement(final VisitedEntry<?> element) {
      return this.visitedElements.add(element);
   }

   public void popVisitedElement(final VisitedEntry<?> element) {
      this.visitedElements.remove(element);
   }

   public HolderGetter.Provider getResolver() {
      return this.lootDataResolver;
   }

   public RandomSource getRandom() {
      return this.random;
   }

   public float getLuck() {
      return this.params.getLuck();
   }

   public ServerLevel getLevel() {
      return this.params.getLevel();
   }

   public static VisitedEntry<LootTable> createVisitedEntry(final LootTable table) {
      return new VisitedEntry<LootTable>(LootDataType.TABLE, table);
   }

   public static VisitedEntry<LootItemCondition> createVisitedEntry(final LootItemCondition table) {
      return new VisitedEntry<LootItemCondition>(LootDataType.PREDICATE, table);
   }

   public static VisitedEntry<LootItemFunction> createVisitedEntry(final LootItemFunction table) {
      return new VisitedEntry<LootItemFunction>(LootDataType.MODIFIER, table);
   }

   public static class Builder {
      private final LootParams params;
      private @Nullable RandomSource random;

      public Builder(final LootParams params) {
         super();
         this.params = params;
      }

      public Builder withOptionalRandomSeed(final long seed) {
         if (seed != 0L) {
            this.random = RandomSource.create(seed);
         }

         return this;
      }

      public Builder withOptionalRandomSource(final RandomSource randomSource) {
         this.random = randomSource;
         return this;
      }

      public ServerLevel getLevel() {
         return this.params.getLevel();
      }

      public LootContext create(final Optional<Identifier> randomSequenceKey) {
         ServerLevel level = this.getLevel();
         MinecraftServer server = level.getServer();
         Optional var10000 = Optional.ofNullable(this.random).or(() -> {
            Objects.requireNonNull(server);
            return randomSequenceKey.map(server::getRandomSequence);
         });
         Objects.requireNonNull(level);
         RandomSource random = (RandomSource)var10000.orElseGet(level::getRandom);
         return new LootContext(this.params, random, server.reloadableRegistries().lookup());
      }
   }

   public static enum EntityTarget implements StringRepresentable, LootContextArg.SimpleGetter<Entity> {
      THIS("this", LootContextParams.THIS_ENTITY),
      ATTACKER("attacker", LootContextParams.ATTACKING_ENTITY),
      DIRECT_ATTACKER("direct_attacker", LootContextParams.DIRECT_ATTACKING_ENTITY),
      ATTACKING_PLAYER("attacking_player", LootContextParams.LAST_DAMAGE_PLAYER),
      TARGET_ENTITY("target_entity", LootContextParams.TARGET_ENTITY),
      INTERACTING_ENTITY("interacting_entity", LootContextParams.INTERACTING_ENTITY);

      public static final StringRepresentable.EnumCodec<EntityTarget> CODEC = StringRepresentable.<EntityTarget>fromEnum(EntityTarget::values);
      private final String name;
      private final ContextKey<? extends Entity> param;

      private EntityTarget(final String name, final ContextKey<? extends Entity> param) {
         this.name = name;
         this.param = param;
      }

      public ContextKey<? extends Entity> contextParam() {
         return this.param;
      }

      public static EntityTarget getByName(final String name) {
         EntityTarget target = CODEC.byName(name);
         if (target != null) {
            return target;
         } else {
            throw new IllegalArgumentException("Invalid entity target " + name);
         }
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static EntityTarget[] $values() {
         return new EntityTarget[]{THIS, ATTACKER, DIRECT_ATTACKER, ATTACKING_PLAYER, TARGET_ENTITY, INTERACTING_ENTITY};
      }
   }

   public static enum BlockEntityTarget implements StringRepresentable, LootContextArg.SimpleGetter<BlockEntity> {
      BLOCK_ENTITY("block_entity", LootContextParams.BLOCK_ENTITY);

      private final String name;
      private final ContextKey<? extends BlockEntity> param;

      private BlockEntityTarget(final String name, final ContextKey<? extends BlockEntity> param) {
         this.name = name;
         this.param = param;
      }

      public ContextKey<? extends BlockEntity> contextParam() {
         return this.param;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static BlockEntityTarget[] $values() {
         return new BlockEntityTarget[]{BLOCK_ENTITY};
      }
   }

   public static enum ItemStackTarget implements StringRepresentable, LootContextArg.SimpleGetter<ItemInstance> {
      TOOL("tool", LootContextParams.TOOL);

      private final String name;
      private final ContextKey<? extends ItemInstance> param;

      private ItemStackTarget(final String name, final ContextKey<? extends ItemInstance> param) {
         this.name = name;
         this.param = param;
      }

      public ContextKey<? extends ItemInstance> contextParam() {
         return this.param;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static ItemStackTarget[] $values() {
         return new ItemStackTarget[]{TOOL};
      }
   }

   public static record VisitedEntry<T extends Validatable>(LootDataType<T> type, T value) {
      public VisitedEntry {
         super();
      }
   }
}
