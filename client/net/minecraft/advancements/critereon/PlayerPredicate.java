package net.minecraft.advancements.critereon;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMaps;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.StatsCounter;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public record PlayerPredicate(MinMaxBounds.Ints level, GameTypePredicate gameType, List<StatMatcher<?>> stats, Object2BooleanMap<ResourceLocation> recipes, Map<ResourceLocation, AdvancementPredicate> advancements, Optional<EntityPredicate> lookingAt) implements EntitySubPredicate {
   public static final int LOOKING_AT_RANGE = 100;
   public static final MapCodec<PlayerPredicate> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(MinMaxBounds.Ints.CODEC.optionalFieldOf("level", MinMaxBounds.Ints.ANY).forGetter(PlayerPredicate::level), GameTypePredicate.CODEC.optionalFieldOf("gamemode", GameTypePredicate.ANY).forGetter(PlayerPredicate::gameType), PlayerPredicate.StatMatcher.CODEC.listOf().optionalFieldOf("stats", List.of()).forGetter(PlayerPredicate::stats), ExtraCodecs.object2BooleanMap(ResourceLocation.CODEC).optionalFieldOf("recipes", Object2BooleanMaps.emptyMap()).forGetter(PlayerPredicate::recipes), Codec.unboundedMap(ResourceLocation.CODEC, PlayerPredicate.AdvancementPredicate.CODEC).optionalFieldOf("advancements", Map.of()).forGetter(PlayerPredicate::advancements), EntityPredicate.CODEC.optionalFieldOf("looking_at").forGetter(PlayerPredicate::lookingAt)).apply(var0, PlayerPredicate::new);
   });

   public PlayerPredicate(MinMaxBounds.Ints var1, GameTypePredicate var2, List<StatMatcher<?>> var3, Object2BooleanMap<ResourceLocation> var4, Map<ResourceLocation, AdvancementPredicate> var5, Optional<EntityPredicate> var6) {
      super();
      this.level = var1;
      this.gameType = var2;
      this.stats = var3;
      this.recipes = var4;
      this.advancements = var5;
      this.lookingAt = var6;
   }

   public boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3) {
      if (!(var1 instanceof ServerPlayer var4)) {
         return false;
      } else if (!this.level.matches(var4.experienceLevel)) {
         return false;
      } else if (!this.gameType.matches(var4.gameMode.getGameModeForPlayer())) {
         return false;
      } else {
         ServerStatsCounter var5 = var4.getStats();
         Iterator var6 = this.stats.iterator();

         while(var6.hasNext()) {
            StatMatcher var7 = (StatMatcher)var6.next();
            if (!var7.matches(var5)) {
               return false;
            }
         }

         ServerRecipeBook var12 = var4.getRecipeBook();
         ObjectIterator var13 = this.recipes.object2BooleanEntrySet().iterator();

         while(var13.hasNext()) {
            Object2BooleanMap.Entry var8 = (Object2BooleanMap.Entry)var13.next();
            if (((RecipeBook)var12).contains((ResourceLocation)var8.getKey()) != var8.getBooleanValue()) {
               return false;
            }
         }

         if (!this.advancements.isEmpty()) {
            label88: {
               PlayerAdvancements var14 = var4.getAdvancements();
               ServerAdvancementManager var16 = var4.getServer().getAdvancements();
               Iterator var9 = this.advancements.entrySet().iterator();

               Map.Entry var10;
               AdvancementHolder var11;
               do {
                  if (!var9.hasNext()) {
                     break label88;
                  }

                  var10 = (Map.Entry)var9.next();
                  var11 = var16.get((ResourceLocation)var10.getKey());
               } while(var11 != null && ((AdvancementPredicate)var10.getValue()).test(var14.getOrStartProgress(var11)));

               return false;
            }
         }

         if (this.lookingAt.isPresent()) {
            Vec3 var15 = var4.getEyePosition();
            Vec3 var17 = var4.getViewVector(1.0F);
            Vec3 var18 = var15.add(var17.x * 100.0, var17.y * 100.0, var17.z * 100.0);
            EntityHitResult var19 = ProjectileUtil.getEntityHitResult(var4.level(), var4, var15, var18, (new AABB(var15, var18)).inflate(1.0), (var0) -> {
               return !var0.isSpectator();
            }, 0.0F);
            if (var19 != null && var19.getType() == HitResult.Type.ENTITY) {
               Entity var20 = var19.getEntity();
               if (((EntityPredicate)this.lookingAt.get()).matches(var4, var20) && var4.hasLineOfSight(var20)) {
                  return true;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return true;
         }
      }
   }

   public MapCodec<PlayerPredicate> codec() {
      return EntitySubPredicates.PLAYER;
   }

   public MinMaxBounds.Ints level() {
      return this.level;
   }

   public GameTypePredicate gameType() {
      return this.gameType;
   }

   public List<StatMatcher<?>> stats() {
      return this.stats;
   }

   public Object2BooleanMap<ResourceLocation> recipes() {
      return this.recipes;
   }

   public Map<ResourceLocation, AdvancementPredicate> advancements() {
      return this.advancements;
   }

   public Optional<EntityPredicate> lookingAt() {
      return this.lookingAt;
   }

   static record StatMatcher<T>(StatType<T> type, Holder<T> value, MinMaxBounds.Ints range, Supplier<Stat<T>> stat) {
      public static final Codec<StatMatcher<?>> CODEC;

      public StatMatcher(StatType<T> var1, Holder<T> var2, MinMaxBounds.Ints var3) {
         this(var1, var2, var3, Suppliers.memoize(() -> {
            return var1.get(var2.value());
         }));
      }

      private StatMatcher(StatType<T> var1, Holder<T> var2, MinMaxBounds.Ints var3, Supplier<Stat<T>> var4) {
         super();
         this.type = var1;
         this.value = var2;
         this.range = var3;
         this.stat = var4;
      }

      private static <T> MapCodec<StatMatcher<T>> createTypedCodec(StatType<T> var0) {
         return RecordCodecBuilder.mapCodec((var1) -> {
            return var1.group(var0.getRegistry().holderByNameCodec().fieldOf("stat").forGetter(StatMatcher::value), MinMaxBounds.Ints.CODEC.optionalFieldOf("value", MinMaxBounds.Ints.ANY).forGetter(StatMatcher::range)).apply(var1, (var1x, var2) -> {
               return new StatMatcher(var0, var1x, var2);
            });
         });
      }

      public boolean matches(StatsCounter var1) {
         return this.range.matches(var1.getValue((Stat)this.stat.get()));
      }

      public StatType<T> type() {
         return this.type;
      }

      public Holder<T> value() {
         return this.value;
      }

      public MinMaxBounds.Ints range() {
         return this.range;
      }

      public Supplier<Stat<T>> stat() {
         return this.stat;
      }

      static {
         CODEC = BuiltInRegistries.STAT_TYPE.byNameCodec().dispatch(StatMatcher::type, StatMatcher::createTypedCodec);
      }
   }

   private interface AdvancementPredicate extends Predicate<AdvancementProgress> {
      Codec<AdvancementPredicate> CODEC = Codec.either(PlayerPredicate.AdvancementDonePredicate.CODEC, PlayerPredicate.AdvancementCriterionsPredicate.CODEC).xmap(Either::unwrap, (var0) -> {
         if (var0 instanceof AdvancementDonePredicate var1) {
            return Either.left(var1);
         } else if (var0 instanceof AdvancementCriterionsPredicate var2) {
            return Either.right(var2);
         } else {
            throw new UnsupportedOperationException();
         }
      });
   }

   public static class Builder {
      private MinMaxBounds.Ints level;
      private GameTypePredicate gameType;
      private final ImmutableList.Builder<StatMatcher<?>> stats;
      private final Object2BooleanMap<ResourceLocation> recipes;
      private final Map<ResourceLocation, AdvancementPredicate> advancements;
      private Optional<EntityPredicate> lookingAt;

      public Builder() {
         super();
         this.level = MinMaxBounds.Ints.ANY;
         this.gameType = GameTypePredicate.ANY;
         this.stats = ImmutableList.builder();
         this.recipes = new Object2BooleanOpenHashMap();
         this.advancements = Maps.newHashMap();
         this.lookingAt = Optional.empty();
      }

      public static Builder player() {
         return new Builder();
      }

      public Builder setLevel(MinMaxBounds.Ints var1) {
         this.level = var1;
         return this;
      }

      public <T> Builder addStat(StatType<T> var1, Holder.Reference<T> var2, MinMaxBounds.Ints var3) {
         this.stats.add(new StatMatcher(var1, var2, var3));
         return this;
      }

      public Builder addRecipe(ResourceLocation var1, boolean var2) {
         this.recipes.put(var1, var2);
         return this;
      }

      public Builder setGameType(GameTypePredicate var1) {
         this.gameType = var1;
         return this;
      }

      public Builder setLookingAt(EntityPredicate.Builder var1) {
         this.lookingAt = Optional.of(var1.build());
         return this;
      }

      public Builder checkAdvancementDone(ResourceLocation var1, boolean var2) {
         this.advancements.put(var1, new AdvancementDonePredicate(var2));
         return this;
      }

      public Builder checkAdvancementCriterions(ResourceLocation var1, Map<String, Boolean> var2) {
         this.advancements.put(var1, new AdvancementCriterionsPredicate(new Object2BooleanOpenHashMap(var2)));
         return this;
      }

      public PlayerPredicate build() {
         return new PlayerPredicate(this.level, this.gameType, this.stats.build(), this.recipes, this.advancements, this.lookingAt);
      }
   }

   private static record AdvancementCriterionsPredicate(Object2BooleanMap<String> criterions) implements AdvancementPredicate {
      public static final Codec<AdvancementCriterionsPredicate> CODEC;

      AdvancementCriterionsPredicate(Object2BooleanMap<String> var1) {
         super();
         this.criterions = var1;
      }

      public boolean test(AdvancementProgress var1) {
         ObjectIterator var2 = this.criterions.object2BooleanEntrySet().iterator();

         Object2BooleanMap.Entry var3;
         CriterionProgress var4;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            var3 = (Object2BooleanMap.Entry)var2.next();
            var4 = var1.getCriterion((String)var3.getKey());
         } while(var4 != null && var4.isDone() == var3.getBooleanValue());

         return false;
      }

      public Object2BooleanMap<String> criterions() {
         return this.criterions;
      }

      // $FF: synthetic method
      public boolean test(final Object var1) {
         return this.test((AdvancementProgress)var1);
      }

      static {
         CODEC = ExtraCodecs.object2BooleanMap(Codec.STRING).xmap(AdvancementCriterionsPredicate::new, AdvancementCriterionsPredicate::criterions);
      }
   }

   private static record AdvancementDonePredicate(boolean state) implements AdvancementPredicate {
      public static final Codec<AdvancementDonePredicate> CODEC;

      AdvancementDonePredicate(boolean var1) {
         super();
         this.state = var1;
      }

      public boolean test(AdvancementProgress var1) {
         return var1.isDone() == this.state;
      }

      public boolean state() {
         return this.state;
      }

      // $FF: synthetic method
      public boolean test(final Object var1) {
         return this.test((AdvancementProgress)var1);
      }

      static {
         CODEC = Codec.BOOL.xmap(AdvancementDonePredicate::new, AdvancementDonePredicate::state);
      }
   }
}
