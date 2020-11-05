package net.minecraft.world.level.storage.loot.parameters;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public class LootContextParamSets {
   private static final BiMap<ResourceLocation, LootContextParamSet> REGISTRY = HashBiMap.create();
   public static final LootContextParamSet EMPTY = register("empty", (var0) -> {
   });
   public static final LootContextParamSet CHEST = register("chest", (var0) -> {
      var0.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY);
   });
   public static final LootContextParamSet COMMAND = register("command", (var0) -> {
      var0.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY);
   });
   public static final LootContextParamSet SELECTOR = register("selector", (var0) -> {
      var0.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY);
   });
   public static final LootContextParamSet FISHING = register("fishing", (var0) -> {
      var0.required(LootContextParams.ORIGIN).required(LootContextParams.TOOL).optional(LootContextParams.THIS_ENTITY);
   });
   public static final LootContextParamSet ENTITY = register("entity", (var0) -> {
      var0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN).required(LootContextParams.DAMAGE_SOURCE).optional(LootContextParams.KILLER_ENTITY).optional(LootContextParams.DIRECT_KILLER_ENTITY).optional(LootContextParams.LAST_DAMAGE_PLAYER);
   });
   public static final LootContextParamSet GIFT = register("gift", (var0) -> {
      var0.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY);
   });
   public static final LootContextParamSet PIGLIN_BARTER = register("barter", (var0) -> {
      var0.required(LootContextParams.THIS_ENTITY);
   });
   public static final LootContextParamSet ADVANCEMENT_REWARD = register("advancement_reward", (var0) -> {
      var0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN);
   });
   public static final LootContextParamSet ADVANCEMENT_ENTITY = register("advancement_entity", (var0) -> {
      var0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN);
   });
   public static final LootContextParamSet ALL_PARAMS = register("generic", (var0) -> {
      var0.required(LootContextParams.THIS_ENTITY).required(LootContextParams.LAST_DAMAGE_PLAYER).required(LootContextParams.DAMAGE_SOURCE).required(LootContextParams.KILLER_ENTITY).required(LootContextParams.DIRECT_KILLER_ENTITY).required(LootContextParams.ORIGIN).required(LootContextParams.BLOCK_STATE).required(LootContextParams.BLOCK_ENTITY).required(LootContextParams.TOOL).required(LootContextParams.EXPLOSION_RADIUS);
   });
   public static final LootContextParamSet BLOCK = register("block", (var0) -> {
      var0.required(LootContextParams.BLOCK_STATE).required(LootContextParams.ORIGIN).required(LootContextParams.TOOL).optional(LootContextParams.THIS_ENTITY).optional(LootContextParams.BLOCK_ENTITY).optional(LootContextParams.EXPLOSION_RADIUS);
   });

   private static LootContextParamSet register(String var0, Consumer<LootContextParamSet.Builder> var1) {
      LootContextParamSet.Builder var2 = new LootContextParamSet.Builder();
      var1.accept(var2);
      LootContextParamSet var3 = var2.build();
      ResourceLocation var4 = new ResourceLocation(var0);
      LootContextParamSet var5 = (LootContextParamSet)REGISTRY.put(var4, var3);
      if (var5 != null) {
         throw new IllegalStateException("Loot table parameter set " + var4 + " is already registered");
      } else {
         return var3;
      }
   }

   @Nullable
   public static LootContextParamSet get(ResourceLocation var0) {
      return (LootContextParamSet)REGISTRY.get(var0);
   }

   @Nullable
   public static ResourceLocation getKey(LootContextParamSet var0) {
      return (ResourceLocation)REGISTRY.inverse().get(var0);
   }
}
