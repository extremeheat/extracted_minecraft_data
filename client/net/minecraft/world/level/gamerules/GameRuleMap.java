package net.minecraft.world.level.gamerules;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jspecify.annotations.Nullable;

public final class GameRuleMap {
   public static final Codec<GameRuleMap> CODEC;
   private final Reference2ObjectMap<GameRule<?>, Object> map;

   GameRuleMap(Reference2ObjectMap<GameRule<?>, Object> var1) {
      super();
      this.map = var1;
   }

   private static GameRuleMap ofTrusted(Map<GameRule<?>, Object> var0) {
      return new GameRuleMap(new Reference2ObjectOpenHashMap(var0));
   }

   public static GameRuleMap of() {
      return new GameRuleMap(new Reference2ObjectOpenHashMap());
   }

   public static GameRuleMap of(Stream<GameRule<?>> var0) {
      Reference2ObjectOpenHashMap var1 = new Reference2ObjectOpenHashMap();
      var0.forEach((var1x) -> var1.put(var1x, var1x.defaultValue()));
      return new GameRuleMap(var1);
   }

   public static GameRuleMap copyOf(GameRuleMap var0) {
      return new GameRuleMap(new Reference2ObjectOpenHashMap(var0.map));
   }

   public boolean has(GameRule<?> var1) {
      return this.map.containsKey(var1);
   }

   public <T> @Nullable T get(GameRule<T> var1) {
      return (T)this.map.get(var1);
   }

   public <T> void set(GameRule<T> var1, T var2) {
      this.map.put(var1, var2);
   }

   public <T> @Nullable T remove(GameRule<T> var1) {
      return (T)this.map.remove(var1);
   }

   public Set<GameRule<?>> keySet() {
      return this.map.keySet();
   }

   public int size() {
      return this.map.size();
   }

   public String toString() {
      return this.map.toString();
   }

   public GameRuleMap withOther(GameRuleMap var1) {
      GameRuleMap var2 = copyOf(this);
      var2.setFromIf(var1, (var0) -> true);
      return var2;
   }

   public void setFromIf(GameRuleMap var1, Predicate<GameRule<?>> var2) {
      for(GameRule var4 : var1.keySet()) {
         if (var2.test(var4)) {
            setGameRule(var1, var4, this);
         }
      }

   }

   private static <T> void setGameRule(GameRuleMap var0, GameRule<T> var1, GameRuleMap var2) {
      var2.set(var1, Objects.requireNonNull(var0.get(var1)));
   }

   private Reference2ObjectMap<GameRule<?>, Object> map() {
      return this.map;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 != null && var1.getClass() == this.getClass()) {
         GameRuleMap var2 = (GameRuleMap)var1;
         return Objects.equals(this.map, var2.map);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.map});
   }

   static {
      CODEC = Codec.dispatchedMap(BuiltInRegistries.GAME_RULE.byNameCodec(), GameRule::valueCodec).xmap(GameRuleMap::ofTrusted, GameRuleMap::map);
   }

   public static class Builder {
      final Reference2ObjectMap<GameRule<?>, Object> map = new Reference2ObjectOpenHashMap();

      public Builder() {
         super();
      }

      public <T> Builder set(GameRule<T> var1, T var2) {
         this.map.put(var1, var2);
         return this;
      }

      public GameRuleMap build() {
         return new GameRuleMap(this.map);
      }
   }
}
