package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

public class LootParams {
   private final ServerLevel level;
   private final Map<LootContextParam<?>, Object> params;
   private final Map<ResourceLocation, LootParams.DynamicDrop> dynamicDrops;
   private final float luck;

   public LootParams(ServerLevel var1, Map<LootContextParam<?>, Object> var2, Map<ResourceLocation, LootParams.DynamicDrop> var3, float var4) {
      super();
      this.level = var1;
      this.params = var2;
      this.dynamicDrops = var3;
      this.luck = var4;
   }

   public ServerLevel getLevel() {
      return this.level;
   }

   public boolean hasParam(LootContextParam<?> var1) {
      return this.params.containsKey(var1);
   }

   public <T> T getParameter(LootContextParam<T> var1) {
      Object var2 = this.params.get(var1);
      if (var2 == null) {
         throw new NoSuchElementException(var1.getName().toString());
      } else {
         return (T)var2;
      }
   }

   @Nullable
   public <T> T getOptionalParameter(LootContextParam<T> var1) {
      return (T)this.params.get(var1);
   }

   @Nullable
   public <T> T getParamOrNull(LootContextParam<T> var1) {
      return (T)this.params.get(var1);
   }

   public void addDynamicDrops(ResourceLocation var1, Consumer<ItemStack> var2) {
      LootParams.DynamicDrop var3 = this.dynamicDrops.get(var1);
      if (var3 != null) {
         var3.add(var2);
      }
   }

   public float getLuck() {
      return this.luck;
   }

   public static class Builder {
      private final ServerLevel level;
      private final Map<LootContextParam<?>, Object> params = Maps.newIdentityHashMap();
      private final Map<ResourceLocation, LootParams.DynamicDrop> dynamicDrops = Maps.newHashMap();
      private float luck;

      public Builder(ServerLevel var1) {
         super();
         this.level = var1;
      }

      public ServerLevel getLevel() {
         return this.level;
      }

      public <T> LootParams.Builder withParameter(LootContextParam<T> var1, T var2) {
         this.params.put(var1, var2);
         return this;
      }

      public <T> LootParams.Builder withOptionalParameter(LootContextParam<T> var1, @Nullable T var2) {
         if (var2 == null) {
            this.params.remove(var1);
         } else {
            this.params.put(var1, var2);
         }

         return this;
      }

      public <T> T getParameter(LootContextParam<T> var1) {
         Object var2 = this.params.get(var1);
         if (var2 == null) {
            throw new NoSuchElementException(var1.getName().toString());
         } else {
            return (T)var2;
         }
      }

      @Nullable
      public <T> T getOptionalParameter(LootContextParam<T> var1) {
         return (T)this.params.get(var1);
      }

      public LootParams.Builder withDynamicDrop(ResourceLocation var1, LootParams.DynamicDrop var2) {
         LootParams.DynamicDrop var3 = this.dynamicDrops.put(var1, var2);
         if (var3 != null) {
            throw new IllegalStateException("Duplicated dynamic drop '" + this.dynamicDrops + "'");
         } else {
            return this;
         }
      }

      public LootParams.Builder withLuck(float var1) {
         this.luck = var1;
         return this;
      }

      public LootParams create(LootContextParamSet var1) {
         SetView var2 = Sets.difference(this.params.keySet(), var1.getAllowed());
         if (!var2.isEmpty()) {
            throw new IllegalArgumentException("Parameters not allowed in this parameter set: " + var2);
         } else {
            SetView var3 = Sets.difference(var1.getRequired(), this.params.keySet());
            if (!var3.isEmpty()) {
               throw new IllegalArgumentException("Missing required parameters: " + var3);
            } else {
               return new LootParams(this.level, this.params, this.dynamicDrops, this.luck);
            }
         }
      }
   }

   @FunctionalInterface
   public interface DynamicDrop {
      void add(Consumer<ItemStack> var1);
   }
}
