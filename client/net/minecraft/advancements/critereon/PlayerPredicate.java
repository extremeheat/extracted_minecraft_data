package net.minecraft.advancements.critereon;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMaps;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry;
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
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.StatsCounter;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public record PlayerPredicate(
   MinMaxBounds.Ints d,
   Optional<GameType> e,
   List<PlayerPredicate.StatMatcher<?>> f,
   Object2BooleanMap<ResourceLocation> g,
   Map<ResourceLocation, PlayerPredicate.AdvancementPredicate> h,
   Optional<EntityPredicate> i
) implements EntitySubPredicate {
   private final MinMaxBounds.Ints level;
   private final Optional<GameType> gameType;
   private final List<PlayerPredicate.StatMatcher<?>> stats;
   private final Object2BooleanMap<ResourceLocation> recipes;
   private final Map<ResourceLocation, PlayerPredicate.AdvancementPredicate> advancements;
   private final Optional<EntityPredicate> lookingAt;
   public static final int LOOKING_AT_RANGE = 100;
   public static final MapCodec<PlayerPredicate> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               ExtraCodecs.strictOptionalField(MinMaxBounds.Ints.CODEC, "level", MinMaxBounds.Ints.ANY).forGetter(PlayerPredicate::level),
               GameType.CODEC.optionalFieldOf("gamemode").forGetter(PlayerPredicate::gameType),
               ExtraCodecs.strictOptionalField(PlayerPredicate.StatMatcher.CODEC.listOf(), "stats", List.of()).forGetter(PlayerPredicate::stats),
               ExtraCodecs.strictOptionalField(ExtraCodecs.object2BooleanMap(ResourceLocation.CODEC), "recipes", Object2BooleanMaps.emptyMap())
                  .forGetter(PlayerPredicate::recipes),
               ExtraCodecs.strictOptionalField(Codec.unboundedMap(ResourceLocation.CODEC, PlayerPredicate.AdvancementPredicate.CODEC), "advancements", Map.of())
                  .forGetter(PlayerPredicate::advancements),
               ExtraCodecs.strictOptionalField(EntityPredicate.CODEC, "looking_at").forGetter(PlayerPredicate::lookingAt)
            )
            .apply(var0, PlayerPredicate::new)
   );

   public PlayerPredicate(
      MinMaxBounds.Ints var1,
      Optional<GameType> var2,
      List<PlayerPredicate.StatMatcher<?>> var3,
      Object2BooleanMap<ResourceLocation> var4,
      Map<ResourceLocation, PlayerPredicate.AdvancementPredicate> var5,
      Optional<EntityPredicate> var6
   ) {
      super();
      this.level = var1;
      this.gameType = var2;
      this.stats = var3;
      this.recipes = var4;
      this.advancements = var5;
      this.lookingAt = var6;
   }

   @Override
   public boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3) {
      if (!(var1 instanceof ServerPlayer)) {
         return false;
      } else {
         ServerPlayer var4 = (ServerPlayer)var1;
         if (!this.level.matches(var4.experienceLevel)) {
            return false;
         } else if (this.gameType.isPresent() && this.gameType.get() != var4.gameMode.getGameModeForPlayer()) {
            return false;
         } else {
            ServerStatsCounter var5 = var4.getStats();

            for(PlayerPredicate.StatMatcher var7 : this.stats) {
               if (!var7.matches(var5)) {
                  return false;
               }
            }

            ServerRecipeBook var12 = var4.getRecipeBook();
            ObjectIterator var13 = this.recipes.object2BooleanEntrySet().iterator();

            while(var13.hasNext()) {
               Entry var8 = (Entry)var13.next();
               if (var12.contains((ResourceLocation)var8.getKey()) != var8.getBooleanValue()) {
                  return false;
               }
            }

            if (!this.advancements.isEmpty()) {
               PlayerAdvancements var14 = var4.getAdvancements();
               ServerAdvancementManager var16 = var4.getServer().getAdvancements();

               for(java.util.Map.Entry var10 : this.advancements.entrySet()) {
                  AdvancementHolder var11 = var16.get((ResourceLocation)var10.getKey());
                  if (var11 == null || !((PlayerPredicate.AdvancementPredicate)var10.getValue()).test(var14.getOrStartProgress(var11))) {
                     return false;
                  }
               }
            }

            if (this.lookingAt.isPresent()) {
               Vec3 var15 = var4.getEyePosition();
               Vec3 var17 = var4.getViewVector(1.0F);
               Vec3 var18 = var15.add(var17.x * 100.0, var17.y * 100.0, var17.z * 100.0);
               EntityHitResult var19 = ProjectileUtil.getEntityHitResult(
                  var4.level(), var4, var15, var18, new AABB(var15, var18).inflate(1.0), var0 -> !var0.isSpectator(), 0.0F
               );
               if (var19 == null || var19.getType() != HitResult.Type.ENTITY) {
                  return false;
               }

               Entity var20 = var19.getEntity();
               if (!((EntityPredicate)this.lookingAt.get()).matches(var4, var20) || !var4.hasLineOfSight(var20)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   @Override
   public MapCodec<PlayerPredicate> codec() {
      return EntitySubPredicates.PLAYER;
   }

   static record AdvancementCriterionsPredicate(Object2BooleanMap<String> c) implements PlayerPredicate.AdvancementPredicate {
      private final Object2BooleanMap<String> criterions;
      public static final Codec<PlayerPredicate.AdvancementCriterionsPredicate> CODEC = ExtraCodecs.object2BooleanMap(Codec.STRING)
         .xmap(PlayerPredicate.AdvancementCriterionsPredicate::new, PlayerPredicate.AdvancementCriterionsPredicate::criterions);

      AdvancementCriterionsPredicate(Object2BooleanMap<String> var1) {
         super();
         this.criterions = var1;
      }

      public boolean test(AdvancementProgress var1) {
         ObjectIterator var2 = this.criterions.object2BooleanEntrySet().iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            CriterionProgress var4 = var1.getCriterion((String)var3.getKey());
            if (var4 == null || var4.isDone() != var3.getBooleanValue()) {
               return false;
            }
         }

         return true;
      }
   }

   static record AdvancementDonePredicate(boolean c) implements PlayerPredicate.AdvancementPredicate {
      private final boolean state;
      public static final Codec<PlayerPredicate.AdvancementDonePredicate> CODEC = Codec.BOOL
         .xmap(PlayerPredicate.AdvancementDonePredicate::new, PlayerPredicate.AdvancementDonePredicate::state);

      AdvancementDonePredicate(boolean var1) {
         super();
         this.state = var1;
      }

      public boolean test(AdvancementProgress var1) {
         return var1.isDone() == this.state;
      }
   }

   interface AdvancementPredicate extends Predicate<AdvancementProgress> {
      Codec<PlayerPredicate.AdvancementPredicate> CODEC = Codec.either(
            PlayerPredicate.AdvancementDonePredicate.CODEC, PlayerPredicate.AdvancementCriterionsPredicate.CODEC
         )
         .xmap(var0 -> (PlayerPredicate.AdvancementPredicate)var0.map(var0x -> var0x, var0x -> var0x), var0 -> {
            if (var0 instanceof PlayerPredicate.AdvancementDonePredicate var1) {
               return Either.left(var1);
            } else if (var0 instanceof PlayerPredicate.AdvancementCriterionsPredicate var2) {
               return Either.right(var2);
            } else {
               throw new UnsupportedOperationException();
            }
         });
   }

   public static class Builder {
      private MinMaxBounds.Ints level = MinMaxBounds.Ints.ANY;
      private Optional<GameType> gameType = Optional.empty();
      private final com.google.common.collect.ImmutableList.Builder<PlayerPredicate.StatMatcher<?>> stats = ImmutableList.builder();
      private final Object2BooleanMap<ResourceLocation> recipes = new Object2BooleanOpenHashMap();
      private final Map<ResourceLocation, PlayerPredicate.AdvancementPredicate> advancements = Maps.newHashMap();
      private Optional<EntityPredicate> lookingAt = Optional.empty();

      public Builder() {
         super();
      }

      public static PlayerPredicate.Builder player() {
         return new PlayerPredicate.Builder();
      }

      public PlayerPredicate.Builder setLevel(MinMaxBounds.Ints var1) {
         this.level = var1;
         return this;
      }

      public <T> PlayerPredicate.Builder addStat(StatType<T> var1, Holder.Reference<T> var2, MinMaxBounds.Ints var3) {
         this.stats.add(new PlayerPredicate.StatMatcher<T>(var1, var2, var3));
         return this;
      }

      public PlayerPredicate.Builder addRecipe(ResourceLocation var1, boolean var2) {
         this.recipes.put(var1, var2);
         return this;
      }

      public PlayerPredicate.Builder setGameType(GameType var1) {
         this.gameType = Optional.of(var1);
         return this;
      }

      public PlayerPredicate.Builder setLookingAt(EntityPredicate.Builder var1) {
         this.lookingAt = Optional.of(var1.build());
         return this;
      }

      public PlayerPredicate.Builder checkAdvancementDone(ResourceLocation var1, boolean var2) {
         this.advancements.put(var1, new PlayerPredicate.AdvancementDonePredicate(var2));
         return this;
      }

      public PlayerPredicate.Builder checkAdvancementCriterions(ResourceLocation var1, Map<String, Boolean> var2) {
         this.advancements.put(var1, new PlayerPredicate.AdvancementCriterionsPredicate(new Object2BooleanOpenHashMap(var2)));
         return this;
      }

      public PlayerPredicate build() {
         return new PlayerPredicate(this.level, this.gameType, this.stats.build(), this.recipes, this.advancements, this.lookingAt);
      }
   }

   static record StatMatcher<T>(StatType<T> b, Holder<T> c, MinMaxBounds.Ints d, Supplier<Stat<T>> e) {
      private final StatType<T> type;
      private final Holder<T> value;
      private final MinMaxBounds.Ints range;
      private final Supplier<Stat<T>> stat;
      public static final Codec<PlayerPredicate.StatMatcher<?>> CODEC = BuiltInRegistries.STAT_TYPE
         .byNameCodec()
         .dispatch(PlayerPredicate.StatMatcher::type, PlayerPredicate.StatMatcher::createTypedCodec);

      public StatMatcher(StatType<T> var1, Holder<T> var2, MinMaxBounds.Ints var3) {
         this(var1, var2, var3, Suppliers.memoize(() -> var1.get(var2.value())));
      }

      private StatMatcher(StatType<T> var1, Holder<T> var2, MinMaxBounds.Ints var3, Supplier<Stat<T>> var4) {
         super();
         this.type = var1;
         this.value = var2;
         this.range = var3;
         this.stat = var4;
      }

      private static <T> Codec<PlayerPredicate.StatMatcher<T>> createTypedCodec(StatType<T> var0) {
         return RecordCodecBuilder.create(
            var1 -> var1.group(
                     var0.getRegistry().holderByNameCodec().fieldOf("stat").forGetter(PlayerPredicate.StatMatcher::value),
                     ExtraCodecs.strictOptionalField(MinMaxBounds.Ints.CODEC, "value", MinMaxBounds.Ints.ANY).forGetter(PlayerPredicate.StatMatcher::range)
                  )
                  .apply(var1, (var1x, var2) -> new PlayerPredicate.StatMatcher(var0, var1x, var2))
         );
      }

      public boolean matches(StatsCounter var1) {
         return this.range.matches(var1.getValue(this.stat.get()));
      }
   }
}
