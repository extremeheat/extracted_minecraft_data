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
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class LootContext {
   private final Random random;
   private final float luck;
   private final ServerLevel level;
   private final LootTables lootTables;
   private final Set<LootTable> visitedTables;
   private final Map<LootContextParam<?>, Object> params;
   private final Map<ResourceLocation, LootContext.DynamicDrop> dynamicDrops;

   private LootContext(Random var1, float var2, ServerLevel var3, LootTables var4, Map<LootContextParam<?>, Object> var5, Map<ResourceLocation, LootContext.DynamicDrop> var6) {
      super();
      this.visitedTables = Sets.newLinkedHashSet();
      this.random = var1;
      this.luck = var2;
      this.level = var3;
      this.lootTables = var4;
      this.params = ImmutableMap.copyOf(var5);
      this.dynamicDrops = ImmutableMap.copyOf(var6);
   }

   public boolean hasParam(LootContextParam<?> var1) {
      return this.params.containsKey(var1);
   }

   public void addDynamicDrops(ResourceLocation var1, Consumer<ItemStack> var2) {
      LootContext.DynamicDrop var3 = (LootContext.DynamicDrop)this.dynamicDrops.get(var1);
      if (var3 != null) {
         var3.add(this, var2);
      }

   }

   @Nullable
   public <T> T getParamOrNull(LootContextParam<T> var1) {
      return this.params.get(var1);
   }

   public boolean addVisitedTable(LootTable var1) {
      return this.visitedTables.add(var1);
   }

   public void removeVisitedTable(LootTable var1) {
      this.visitedTables.remove(var1);
   }

   public LootTables getLootTables() {
      return this.lootTables;
   }

   public Random getRandom() {
      return this.random;
   }

   public float getLuck() {
      return this.luck;
   }

   public ServerLevel getLevel() {
      return this.level;
   }

   // $FF: synthetic method
   LootContext(Random var1, float var2, ServerLevel var3, LootTables var4, Map var5, Map var6, Object var7) {
      this(var1, var2, var3, var4, var5, var6);
   }

   public static enum EntityTarget {
      THIS("this", LootContextParams.THIS_ENTITY),
      KILLER("killer", LootContextParams.KILLER_ENTITY),
      DIRECT_KILLER("direct_killer", LootContextParams.DIRECT_KILLER_ENTITY),
      KILLER_PLAYER("killer_player", LootContextParams.LAST_DAMAGE_PLAYER);

      private final String name;
      private final LootContextParam<? extends Entity> param;

      private EntityTarget(String var3, LootContextParam<? extends Entity> var4) {
         this.name = var3;
         this.param = var4;
      }

      public LootContextParam<? extends Entity> getParam() {
         return this.param;
      }

      public static LootContext.EntityTarget getByName(String var0) {
         LootContext.EntityTarget[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            LootContext.EntityTarget var4 = var1[var3];
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

         // $FF: synthetic method
         public Object read(JsonReader var1) throws IOException {
            return this.read(var1);
         }

         // $FF: synthetic method
         public void write(JsonWriter var1, Object var2) throws IOException {
            this.write(var1, (LootContext.EntityTarget)var2);
         }
      }
   }

   public static class Builder {
      private final ServerLevel level;
      private final Map<LootContextParam<?>, Object> params = Maps.newIdentityHashMap();
      private final Map<ResourceLocation, LootContext.DynamicDrop> dynamicDrops = Maps.newHashMap();
      private Random random;
      private float luck;

      public Builder(ServerLevel var1) {
         super();
         this.level = var1;
      }

      public LootContext.Builder withRandom(Random var1) {
         this.random = var1;
         return this;
      }

      public LootContext.Builder withOptionalRandomSeed(long var1) {
         if (var1 != 0L) {
            this.random = new Random(var1);
         }

         return this;
      }

      public LootContext.Builder withOptionalRandomSeed(long var1, Random var3) {
         if (var1 == 0L) {
            this.random = var3;
         } else {
            this.random = new Random(var1);
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
         LootContext.DynamicDrop var3 = (LootContext.DynamicDrop)this.dynamicDrops.put(var1, var2);
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
            return var2;
         }
      }

      @Nullable
      public <T> T getOptionalParameter(LootContextParam<T> var1) {
         return this.params.get(var1);
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
               Random var4 = this.random;
               if (var4 == null) {
                  var4 = new Random();
               }

               return new LootContext(var4, this.luck, this.level, this.level.getServer().getLootTables(), this.params, this.dynamicDrops);
            }
         }
      }
   }

   @FunctionalInterface
   public interface DynamicDrop {
      void add(LootContext var1, Consumer<ItemStack> var2);
   }
}
