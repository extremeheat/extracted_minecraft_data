package net.minecraft.tags;

import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public class TagManager implements PreparableReloadListener {
   private final SynchronizableTagCollection blocks;
   private final SynchronizableTagCollection items;
   private final SynchronizableTagCollection fluids;
   private final SynchronizableTagCollection entityTypes;

   public TagManager() {
      this.blocks = new SynchronizableTagCollection(Registry.BLOCK, "tags/blocks", "block");
      this.items = new SynchronizableTagCollection(Registry.ITEM, "tags/items", "item");
      this.fluids = new SynchronizableTagCollection(Registry.FLUID, "tags/fluids", "fluid");
      this.entityTypes = new SynchronizableTagCollection(Registry.ENTITY_TYPE, "tags/entity_types", "entity_type");
   }

   public SynchronizableTagCollection getBlocks() {
      return this.blocks;
   }

   public SynchronizableTagCollection getItems() {
      return this.items;
   }

   public SynchronizableTagCollection getFluids() {
      return this.fluids;
   }

   public SynchronizableTagCollection getEntityTypes() {
      return this.entityTypes;
   }

   public void serializeToNetwork(FriendlyByteBuf var1) {
      this.blocks.serializeToNetwork(var1);
      this.items.serializeToNetwork(var1);
      this.fluids.serializeToNetwork(var1);
      this.entityTypes.serializeToNetwork(var1);
   }

   public static TagManager deserializeFromNetwork(FriendlyByteBuf var0) {
      TagManager var1 = new TagManager();
      var1.getBlocks().loadFromNetwork(var0);
      var1.getItems().loadFromNetwork(var0);
      var1.getFluids().loadFromNetwork(var0);
      var1.getEntityTypes().loadFromNetwork(var0);
      return var1;
   }

   public CompletableFuture reload(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6) {
      CompletableFuture var7 = this.blocks.prepare(var2, var5);
      CompletableFuture var8 = this.items.prepare(var2, var5);
      CompletableFuture var9 = this.fluids.prepare(var2, var5);
      CompletableFuture var10 = this.entityTypes.prepare(var2, var5);
      CompletableFuture var10000 = var7.thenCombine(var8, Pair::of).thenCombine(var9.thenCombine(var10, Pair::of), (var0, var1x) -> {
         return new TagManager.Preparations((Map)var0.getFirst(), (Map)var0.getSecond(), (Map)var1x.getFirst(), (Map)var1x.getSecond());
      });
      var1.getClass();
      return var10000.thenCompose(var1::wait).thenAcceptAsync((var1x) -> {
         this.blocks.load(var1x.blocks);
         this.items.load(var1x.items);
         this.fluids.load(var1x.fluids);
         this.entityTypes.load(var1x.entityTypes);
         BlockTags.reset(this.blocks);
         ItemTags.reset(this.items);
         FluidTags.reset(this.fluids);
         EntityTypeTags.reset(this.entityTypes);
      }, var6);
   }

   public static class Preparations {
      final Map blocks;
      final Map items;
      final Map fluids;
      final Map entityTypes;

      public Preparations(Map var1, Map var2, Map var3, Map var4) {
         this.blocks = var1;
         this.items = var2;
         this.fluids = var3;
         this.entityTypes = var4;
      }
   }
}
