package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Sets;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
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

   LootContext(LootParams var1, RandomSource var2, HolderGetter.Provider var3) {
      super();
      this.params = var1;
      this.random = var2;
      this.lootDataResolver = var3;
   }

   public boolean hasParameter(ContextKey<?> var1) {
      return this.params.contextMap().has(var1);
   }

   public <T> T getParameter(ContextKey<T> var1) {
      return (T)this.params.contextMap().getOrThrow(var1);
   }

   public <T> @Nullable T getOptionalParameter(ContextKey<T> var1) {
      return (T)this.params.contextMap().getOptional(var1);
   }

   public void addDynamicDrops(ResourceLocation var1, Consumer<ItemStack> var2) {
      this.params.addDynamicDrops(var1, var2);
   }

   public boolean hasVisitedElement(VisitedEntry<?> var1) {
      return this.visitedElements.contains(var1);
   }

   public boolean pushVisitedElement(VisitedEntry<?> var1) {
      return this.visitedElements.add(var1);
   }

   public void popVisitedElement(VisitedEntry<?> var1) {
      this.visitedElements.remove(var1);
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

   public static VisitedEntry<LootTable> createVisitedEntry(LootTable var0) {
      return new VisitedEntry<LootTable>(LootDataType.TABLE, var0);
   }

   public static VisitedEntry<LootItemCondition> createVisitedEntry(LootItemCondition var0) {
      return new VisitedEntry<LootItemCondition>(LootDataType.PREDICATE, var0);
   }

   public static VisitedEntry<LootItemFunction> createVisitedEntry(LootItemFunction var0) {
      return new VisitedEntry<LootItemFunction>(LootDataType.MODIFIER, var0);
   }

   public static class Builder {
      private final LootParams params;
      private @Nullable RandomSource random;

      public Builder(LootParams var1) {
         super();
         this.params = var1;
      }

      public Builder withOptionalRandomSeed(long var1) {
         if (var1 != 0L) {
            this.random = RandomSource.create(var1);
         }

         return this;
      }

      public Builder withOptionalRandomSource(RandomSource var1) {
         this.random = var1;
         return this;
      }

      public ServerLevel getLevel() {
         return this.params.getLevel();
      }

      public LootContext create(Optional<ResourceLocation> var1) {
         ServerLevel var2 = this.getLevel();
         MinecraftServer var3 = var2.getServer();
         Optional var10000 = Optional.ofNullable(this.random).or(() -> {
            Objects.requireNonNull(var2);
            return var1.map(var2::getRandomSequence);
         });
         Objects.requireNonNull(var2);
         RandomSource var4 = (RandomSource)var10000.orElseGet(var2::getRandom);
         return new LootContext(this.params, var4, var3.reloadableRegistries().lookup());
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

      private EntityTarget(final String var3, final ContextKey<? extends Entity> var4) {
         this.name = var3;
         this.param = var4;
      }

      public ContextKey<? extends Entity> contextParam() {
         return this.param;
      }

      public static EntityTarget getByName(String var0) {
         EntityTarget var1 = CODEC.byName(var0);
         if (var1 != null) {
            return var1;
         } else {
            throw new IllegalArgumentException("Invalid entity target " + var0);
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

      private BlockEntityTarget(final String var3, final ContextKey<? extends BlockEntity> var4) {
         this.name = var3;
         this.param = var4;
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

   public static enum ItemStackTarget implements StringRepresentable, LootContextArg.SimpleGetter<ItemStack> {
      TOOL("tool", LootContextParams.TOOL);

      private final String name;
      private final ContextKey<? extends ItemStack> param;

      private ItemStackTarget(final String var3, final ContextKey<? extends ItemStack> var4) {
         this.name = var3;
         this.param = var4;
      }

      public ContextKey<? extends ItemStack> contextParam() {
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

   public static record VisitedEntry<T>(LootDataType<T> type, T value) {
      public VisitedEntry(LootDataType<T> var1, T var2) {
         super();
         this.type = var1;
         this.value = var2;
      }
   }
}
