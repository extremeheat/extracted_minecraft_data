package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Sets;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootContext {
   private final LootParams params;
   private final RandomSource random;
   private final LootDataResolver lootDataResolver;
   private final Set<LootContext.VisitedEntry<?>> visitedElements = Sets.newLinkedHashSet();

   LootContext(LootParams var1, RandomSource var2, LootDataResolver var3) {
      super();
      this.params = var1;
      this.random = var2;
      this.lootDataResolver = var3;
   }

   public boolean hasParam(LootContextParam<?> var1) {
      return this.params.hasParam(var1);
   }

   public <T> T getParam(LootContextParam<T> var1) {
      return this.params.getParameter(var1);
   }

   public void addDynamicDrops(ResourceLocation var1, Consumer<ItemStack> var2) {
      this.params.addDynamicDrops(var1, var2);
   }

   @Nullable
   public <T> T getParamOrNull(LootContextParam<T> var1) {
      return this.params.getParamOrNull(var1);
   }

   public boolean hasVisitedElement(LootContext.VisitedEntry<?> var1) {
      return this.visitedElements.contains(var1);
   }

   public boolean pushVisitedElement(LootContext.VisitedEntry<?> var1) {
      return this.visitedElements.add(var1);
   }

   public void popVisitedElement(LootContext.VisitedEntry<?> var1) {
      this.visitedElements.remove(var1);
   }

   public LootDataResolver getResolver() {
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

   public static LootContext.VisitedEntry<LootTable> createVisitedEntry(LootTable var0) {
      return new LootContext.VisitedEntry<>(LootDataType.TABLE, var0);
   }

   public static LootContext.VisitedEntry<LootItemCondition> createVisitedEntry(LootItemCondition var0) {
      return new LootContext.VisitedEntry<>(LootDataType.PREDICATE, var0);
   }

   public static LootContext.VisitedEntry<LootItemFunction> createVisitedEntry(LootItemFunction var0) {
      return new LootContext.VisitedEntry<>(LootDataType.MODIFIER, var0);
   }

   public static class Builder {
      private final LootParams params;
      @Nullable
      private RandomSource random;

      public Builder(LootParams var1) {
         super();
         this.params = var1;
      }

      public LootContext.Builder withOptionalRandomSeed(long var1) {
         if (var1 != 0L) {
            this.random = RandomSource.create(var1);
         }

         return this;
      }

      public ServerLevel getLevel() {
         return this.params.getLevel();
      }

      public LootContext create(@Nullable ResourceLocation var1) {
         ServerLevel var2 = this.getLevel();
         MinecraftServer var3 = var2.getServer();
         RandomSource var4;
         if (this.random != null) {
            var4 = this.random;
         } else if (var1 != null) {
            var4 = var2.getRandomSequence(var1);
         } else {
            var4 = var2.getRandom();
         }

         return new LootContext(this.params, var4, var3.getLootData());
      }
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

   public static record VisitedEntry<T>(LootDataType<T> a, T b) {
      private final LootDataType<T> type;
      private final T value;

      public VisitedEntry(LootDataType<T> var1, T var2) {
         super();
         this.type = var1;
         this.value = (T)var2;
      }
   }
}
