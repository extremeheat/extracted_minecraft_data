package net.minecraft.tags;

import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class TagManager implements PreparableReloadListener {
   private final TagLoader<Block> blocks;
   private final TagLoader<Item> items;
   private final TagLoader<Fluid> fluids;
   private final TagLoader<EntityType<?>> entityTypes;
   private TagContainer tags;

   public TagManager() {
      super();
      this.blocks = new TagLoader(Registry.BLOCK::getOptional, "tags/blocks", "block");
      this.items = new TagLoader(Registry.ITEM::getOptional, "tags/items", "item");
      this.fluids = new TagLoader(Registry.FLUID::getOptional, "tags/fluids", "fluid");
      this.entityTypes = new TagLoader(Registry.ENTITY_TYPE::getOptional, "tags/entity_types", "entity_type");
      this.tags = TagContainer.EMPTY;
   }

   public TagContainer getTags() {
      return this.tags;
   }

   public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6) {
      CompletableFuture var7 = this.blocks.prepare(var2, var5);
      CompletableFuture var8 = this.items.prepare(var2, var5);
      CompletableFuture var9 = this.fluids.prepare(var2, var5);
      CompletableFuture var10 = this.entityTypes.prepare(var2, var5);
      CompletableFuture var10000 = CompletableFuture.allOf(var7, var8, var9, var10);
      var1.getClass();
      return var10000.thenCompose(var1::wait).thenAcceptAsync((var5x) -> {
         TagCollection var6 = this.blocks.load((Map)var7.join());
         TagCollection var7x = this.items.load((Map)var8.join());
         TagCollection var8x = this.fluids.load((Map)var9.join());
         TagCollection var9x = this.entityTypes.load((Map)var10.join());
         TagContainer var10x = TagContainer.of(var6, var7x, var8x, var9x);
         Multimap var11 = StaticTags.getAllMissingTags(var10x);
         if (!var11.isEmpty()) {
            throw new IllegalStateException("Missing required tags: " + (String)var11.entries().stream().map((var0) -> {
               return var0.getKey() + ":" + var0.getValue();
            }).sorted().collect(Collectors.joining(",")));
         } else {
            SerializationTags.bind(var10x);
            this.tags = var10x;
         }
      }, var6);
   }
}
