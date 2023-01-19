package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootContext {
   private final RandomSource random;
   private final float luck;
   private final ServerLevel level;
   private final Function<ResourceLocation, LootTable> lootTables;
   private final Set<LootTable> visitedTables = Sets.newLinkedHashSet();
   private final Function<ResourceLocation, LootItemCondition> conditions;
   private final Set<LootItemCondition> visitedConditions = Sets.newLinkedHashSet();
   private final Map<LootContextParam<?>, Object> params;
   private final Map<ResourceLocation, LootContext.DynamicDrop> dynamicDrops;

   LootContext(
      RandomSource var1,
      float var2,
      ServerLevel var3,
      Function<ResourceLocation, LootTable> var4,
      Function<ResourceLocation, LootItemCondition> var5,
      Map<LootContextParam<?>, Object> var6,
      Map<ResourceLocation, LootContext.DynamicDrop> var7
   ) {
      super();
      this.random = var1;
      this.luck = var2;
      this.level = var3;
      this.lootTables = var4;
      this.conditions = var5;
      this.params = ImmutableMap.copyOf(var6);
      this.dynamicDrops = ImmutableMap.copyOf(var7);
   }

   public boolean hasParam(LootContextParam<?> var1) {
      return this.params.containsKey(var1);
   }

   public <T> T getParam(LootContextParam<T> var1) {
      Object var2 = this.params.get(var1);
      if (var2 == null) {
         throw new NoSuchElementException(var1.getName().toString());
      } else {
         return (T)var2;
      }
   }

   public void addDynamicDrops(ResourceLocation var1, Consumer<ItemStack> var2) {
      LootContext.DynamicDrop var3 = this.dynamicDrops.get(var1);
      if (var3 != null) {
         var3.add(this, var2);
      }
   }

   @Nullable
   public <T> T getParamOrNull(LootContextParam<T> var1) {
      return (T)this.params.get(var1);
   }

   public boolean addVisitedTable(LootTable var1) {
      return this.visitedTables.add(var1);
   }

   public void removeVisitedTable(LootTable var1) {
      this.visitedTables.remove(var1);
   }

   public boolean addVisitedCondition(LootItemCondition var1) {
      return this.visitedConditions.add(var1);
   }

   public void removeVisitedCondition(LootItemCondition var1) {
      this.visitedConditions.remove(var1);
   }

   public LootTable getLootTable(ResourceLocation var1) {
      return this.lootTables.apply(var1);
   }

   @Nullable
   public LootItemCondition getCondition(ResourceLocation var1) {
      return this.conditions.apply(var1);
   }

   public RandomSource getRandom() {
      return this.random;
   }

   public float getLuck() {
      return this.luck;
   }

   public ServerLevel getLevel() {
      return this.level;
   }

   public static class Builder {
      private final ServerLevel level;
      private final Map<LootContextParam<?>, Object> params = Maps.newIdentityHashMap();
      private final Map<ResourceLocation, LootContext.DynamicDrop> dynamicDrops = Maps.newHashMap();
      private RandomSource random;
      private float luck;

      public Builder(ServerLevel var1) {
         super();
         this.level = var1;
      }

      public LootContext.Builder withRandom(RandomSource var1) {
         this.random = var1;
         return this;
      }

      public LootContext.Builder withOptionalRandomSeed(long var1) {
         if (var1 != 0L) {
            this.random = RandomSource.create(var1);
         }

         return this;
      }

      public LootContext.Builder withOptionalRandomSeed(long var1, RandomSource var3) {
         if (var1 == 0L) {
            this.random = var3;
         } else {
            this.random = RandomSource.create(var1);
         }

         return this;
      }

      public LootContext.Builder withLuck(float var1) {
         this.luck = var1;
         return this;
      }

      public <T> LootContext.Builder withParameter(LootContextParam<T> var1, T var2) {
         this.params.put(var1, var2);
         return this;
      }

      public <T> LootContext.Builder withOptionalParameter(LootContextParam<T> var1, @Nullable T var2) {
         if (var2 == null) {
            this.params.remove(var1);
         } else {
            this.params.put(var1, var2);
         }

         return this;
      }

      public LootContext.Builder withDynamicDrop(ResourceLocation var1, LootContext.DynamicDrop var2) {
         LootContext.DynamicDrop var3 = this.dynamicDrops.put(var1, var2);
         if (var3 != null) {
            throw new IllegalStateException("Duplicated dynamic drop '" + this.dynamicDrops + "'");
         } else {
            return this;
         }
      }

      public ServerLevel getLevel() {
         return this.level;
      }

      public <T> T getParameter(LootContextParam<T> var1) {
         Object var2 = this.params.get(var1);
         if (var2 == null) {
            throw new IllegalArgumentException("No parameter " + var1);
         } else {
            return (T)var2;
         }
      }

      @Nullable
      public <T> T getOptionalParameter(LootContextParam<T> var1) {
         return (T)this.params.get(var1);
      }

      public LootContext create(LootContextParamSet var1) {
         SetView var2 = Sets.difference(this.params.keySet(), var1.getAllowed());
         if (!var2.isEmpty()) {
            throw new IllegalArgumentException("Parameters not allowed in this parameter set: " + var2);
         } else {
            SetView var3 = Sets.difference(var1.getRequired(), this.params.keySet());
            if (!var3.isEmpty()) {
               throw new IllegalArgumentException("Missing required parameters: " + var3);
            } else {
               RandomSource var4 = this.random;
               if (var4 == null) {
                  var4 = RandomSource.create();
               }

               MinecraftServer var5 = this.level.getServer();
               return new LootContext(var4, this.luck, this.level, var5.getLootTables()::get, var5.getPredicateManager()::get, this.params, this.dynamicDrops);
            }
         }
      }
   }

   @FunctionalInterface
   public interface DynamicDrop {
      void add(LootContext var1, Consumer<ItemStack> var2);
   }

   public static enum EntityTarget {
      THIS("this", LootContextParams.THIS_ENTITY),
      KILLER("killer", LootContextParams.KILLER_ENTITY),
      DIRECT_KILLER("direct_killer", LootContextParams.DIRECT_KILLER_ENTITY),
      KILLER_PLAYER("killer_player", LootContextParams.LAST_DAMAGE_PLAYER);

      final String name;
      private final LootContextParam<? extends Entity> param;

      private EntityTarget(String var3, LootContextParam<? extends Entity> var4) {
         this.name = var3;
         this.param = var4;
      }

      public LootContextParam<? extends Entity> getParam() {
         return this.param;
      }

      public static LootContext.EntityTarget getByName(String var0) {
         for(LootContext.EntityTarget var4 : values()) {
            if (var4.name.equals(var0)) {
               return var4;
            }
         }

         throw new IllegalArgumentException("Invalid entity target " + var0);
      }

      public static class Serializer extends TypeAdapter<LootContext.EntityTarget> {
         public Serializer() {
            super();
         }

         public void write(JsonWriter var1, LootContext.EntityTarget var2) throws IOException {
            var1.value(var2.name);
         }

         public LootContext.EntityTarget read(JsonReader var1) throws IOException {
            return LootContext.EntityTarget.getByName(var1.nextString());
         }
      }
   }
}