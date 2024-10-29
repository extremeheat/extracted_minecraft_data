package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;

public class LootParams {
   private final ServerLevel level;
   private final ContextMap params;
   private final Map<ResourceLocation, DynamicDrop> dynamicDrops;
   private final float luck;

   public LootParams(ServerLevel var1, ContextMap var2, Map<ResourceLocation, DynamicDrop> var3, float var4) {
      super();
      this.level = var1;
      this.params = var2;
      this.dynamicDrops = var3;
      this.luck = var4;
   }

   public ServerLevel getLevel() {
      return this.level;
   }

   public ContextMap contextMap() {
      return this.params;
   }

   public void addDynamicDrops(ResourceLocation var1, Consumer<ItemStack> var2) {
      DynamicDrop var3 = (DynamicDrop)this.dynamicDrops.get(var1);
      if (var3 != null) {
         var3.add(var2);
      }

   }

   public float getLuck() {
      return this.luck;
   }

   @FunctionalInterface
   public interface DynamicDrop {
      void add(Consumer<ItemStack> var1);
   }

   public static class Builder {
      private final ServerLevel level;
      private final ContextMap.Builder params = new ContextMap.Builder();
      private final Map<ResourceLocation, DynamicDrop> dynamicDrops = Maps.newHashMap();
      private float luck;

      public Builder(ServerLevel var1) {
         super();
         this.level = var1;
      }

      public ServerLevel getLevel() {
         return this.level;
      }

      public <T> Builder withParameter(ContextKey<T> var1, T var2) {
         this.params.withParameter(var1, var2);
         return this;
      }

      public <T> Builder withOptionalParameter(ContextKey<T> var1, @Nullable T var2) {
         this.params.withOptionalParameter(var1, var2);
         return this;
      }

      public <T> T getParameter(ContextKey<T> var1) {
         return this.params.getParameter(var1);
      }

      @Nullable
      public <T> T getOptionalParameter(ContextKey<T> var1) {
         return this.params.getOptionalParameter(var1);
      }

      public Builder withDynamicDrop(ResourceLocation var1, DynamicDrop var2) {
         DynamicDrop var3 = (DynamicDrop)this.dynamicDrops.put(var1, var2);
         if (var3 != null) {
            throw new IllegalStateException("Duplicated dynamic drop '" + String.valueOf(this.dynamicDrops) + "'");
         } else {
            return this;
         }
      }

      public Builder withLuck(float var1) {
         this.luck = var1;
         return this;
      }

      public LootParams create(ContextKeySet var1) {
         ContextMap var2 = this.params.create(var1);
         return new LootParams(this.level, var2, this.dynamicDrops, this.luck);
      }
   }
}
