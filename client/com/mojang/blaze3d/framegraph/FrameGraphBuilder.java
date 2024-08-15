package com.mojang.blaze3d.framegraph;

import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.ResourceDescriptor;
import com.mojang.blaze3d.resource.ResourceHandle;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class FrameGraphBuilder {
   private final List<FrameGraphBuilder.InternalVirtualResource<?>> internalResources = new ArrayList<>();
   private final List<FrameGraphBuilder.ExternalResource<?>> externalResources = new ArrayList<>();
   private final List<FrameGraphBuilder.Pass> passes = new ArrayList<>();

   public FrameGraphBuilder() {
      super();
   }

   public FramePass addPass(String var1) {
      FrameGraphBuilder.Pass var2 = new FrameGraphBuilder.Pass(this.passes.size(), var1);
      this.passes.add(var2);
      return var2;
   }

   public <T> ResourceHandle<T> importExternal(String var1, T var2) {
      FrameGraphBuilder.ExternalResource var3 = new FrameGraphBuilder.ExternalResource<>(var1, null, var2);
      this.externalResources.add(var3);
      return var3.handle;
   }

   public <T> ResourceHandle<T> createInternal(String var1, ResourceDescriptor<T> var2) {
      return this.createInternalResource(var1, var2, null).handle;
   }

   <T> FrameGraphBuilder.InternalVirtualResource<T> createInternalResource(String var1, ResourceDescriptor<T> var2, @Nullable FrameGraphBuilder.Pass var3) {
      int var4 = this.internalResources.size();
      FrameGraphBuilder.InternalVirtualResource var5 = new FrameGraphBuilder.InternalVirtualResource(var4, var1, var3, var2);
      this.internalResources.add(var5);
      return var5;
   }

   public void execute(GraphicsResourceAllocator var1) {
      this.execute(var1, FrameGraphBuilder.Inspector.NONE);
   }

   public void execute(GraphicsResourceAllocator var1, FrameGraphBuilder.Inspector var2) {
      BitSet var3 = this.identifyPassesToKeep();
      ArrayList var4 = new ArrayList(var3.cardinality());
      BitSet var5 = new BitSet(this.passes.size());

      for (FrameGraphBuilder.Pass var7 : this.passes) {
         this.resolvePassOrder(var7, var3, var5, var4);
      }

      this.assignResourceLifetimes(var4);

      for (FrameGraphBuilder.Pass var11 : var4) {
         for (FrameGraphBuilder.InternalVirtualResource var9 : var11.resourcesToAcquire) {
            var2.acquireResource(var9.name);
            var9.acquire(var1);
         }

         var2.beforeExecutePass(var11.name);
         var11.task.run();
         var2.afterExecutePass(var11.name);

         for (int var12 = var11.resourcesToRelease.nextSetBit(0); var12 >= 0; var12 = var11.resourcesToRelease.nextSetBit(var12 + 1)) {
            FrameGraphBuilder.InternalVirtualResource var13 = this.internalResources.get(var12);
            var2.releaseResource(var13.name);
            var13.release(var1);
         }
      }
   }

   private BitSet identifyPassesToKeep() {
      ArrayDeque var1 = new ArrayDeque(this.passes.size());
      BitSet var2 = new BitSet(this.passes.size());

      for (FrameGraphBuilder.VirtualResource var4 : this.externalResources) {
         FrameGraphBuilder.Pass var5 = var4.handle.createdBy;
         if (var5 != null) {
            this.discoverAllRequiredPasses(var5, var2, var1);
         }
      }

      for (FrameGraphBuilder.Pass var7 : this.passes) {
         if (var7.disableCulling) {
            this.discoverAllRequiredPasses(var7, var2, var1);
         }
      }

      return var2;
   }

   private void discoverAllRequiredPasses(FrameGraphBuilder.Pass var1, BitSet var2, Deque<FrameGraphBuilder.Pass> var3) {
      var3.add(var1);

      while (!var3.isEmpty()) {
         FrameGraphBuilder.Pass var4 = (FrameGraphBuilder.Pass)var3.poll();
         if (!var2.get(var4.id)) {
            var2.set(var4.id);

            for (int var5 = var4.requiredPassIds.nextSetBit(0); var5 >= 0; var5 = var4.requiredPassIds.nextSetBit(var5 + 1)) {
               var3.add(this.passes.get(var5));
            }
         }
      }
   }

   private void resolvePassOrder(FrameGraphBuilder.Pass var1, BitSet var2, BitSet var3, List<FrameGraphBuilder.Pass> var4) {
      if (var3.get(var1.id)) {
         String var9 = var3.stream().mapToObj(var1x -> this.passes.get(var1x).name).collect(Collectors.joining(", "));
         throw new IllegalStateException("Frame graph cycle detected between " + var9);
      } else if (var2.get(var1.id)) {
         var3.set(var1.id);
         var2.clear(var1.id);

         for (int var5 = var1.requiredPassIds.nextSetBit(0); var5 >= 0; var5 = var1.requiredPassIds.nextSetBit(var5 + 1)) {
            this.resolvePassOrder(this.passes.get(var5), var2, var3, var4);
         }

         for (FrameGraphBuilder.Handle var6 : var1.writesFrom) {
            for (int var7 = var6.readBy.nextSetBit(0); var7 >= 0; var7 = var6.readBy.nextSetBit(var7 + 1)) {
               if (var7 != var1.id) {
                  this.resolvePassOrder(this.passes.get(var7), var2, var3, var4);
               }
            }
         }

         var4.add(var1);
         var3.clear(var1.id);
      }
   }

   private void assignResourceLifetimes(Collection<FrameGraphBuilder.Pass> var1) {
      FrameGraphBuilder.Pass[] var2 = new FrameGraphBuilder.Pass[this.internalResources.size()];

      for (FrameGraphBuilder.Pass var4 : var1) {
         for (int var5 = var4.requiredResourceIds.nextSetBit(0); var5 >= 0; var5 = var4.requiredResourceIds.nextSetBit(var5 + 1)) {
            FrameGraphBuilder.InternalVirtualResource var6 = this.internalResources.get(var5);
            FrameGraphBuilder.Pass var7 = var2[var5];
            var2[var5] = var4;
            if (var7 == null) {
               var4.resourcesToAcquire.add(var6);
            } else {
               var7.resourcesToRelease.clear(var5);
            }

            var4.resourcesToRelease.set(var5);
         }
      }
   }

   static class ExternalResource<T> extends FrameGraphBuilder.VirtualResource<T> {
      private final T resource;

      public ExternalResource(String var1, @Nullable FrameGraphBuilder.Pass var2, T var3) {
         super(var1, var2);
         this.resource = (T)var3;
      }

      @Override
      public T get() {
         return this.resource;
      }
   }

   static class Handle<T> implements ResourceHandle<T> {
      final FrameGraphBuilder.VirtualResource<T> holder;
      private final int version;
      @Nullable
      final FrameGraphBuilder.Pass createdBy;
      final BitSet readBy = new BitSet();
      @Nullable
      private FrameGraphBuilder.Handle<T> aliasedBy;

      Handle(FrameGraphBuilder.VirtualResource<T> var1, int var2, @Nullable FrameGraphBuilder.Pass var3) {
         super();
         this.holder = var1;
         this.version = var2;
         this.createdBy = var3;
      }

      @Override
      public T get() {
         return this.holder.get();
      }

      FrameGraphBuilder.Handle<T> writeAndAlias(FrameGraphBuilder.Pass var1) {
         if (this.holder.handle != this) {
            throw new IllegalStateException("Handle " + this + " is no longer valid, as its contents were moved into " + this.aliasedBy);
         } else {
            FrameGraphBuilder.Handle var2 = new FrameGraphBuilder.Handle<>(this.holder, this.version + 1, var1);
            this.holder.handle = var2;
            this.aliasedBy = var2;
            return var2;
         }
      }

      @Override
      public String toString() {
         return this.createdBy != null ? this.holder + "#" + this.version + " (from " + this.createdBy + ")" : this.holder + "#" + this.version;
      }
   }

   public interface Inspector {
      FrameGraphBuilder.Inspector NONE = new FrameGraphBuilder.Inspector() {
      };

      default void acquireResource(String var1) {
      }

      default void releaseResource(String var1) {
      }

      default void beforeExecutePass(String var1) {
      }

      default void afterExecutePass(String var1) {
      }
   }

   static class InternalVirtualResource<T> extends FrameGraphBuilder.VirtualResource<T> {
      final int id;
      private final ResourceDescriptor<T> descriptor;
      @Nullable
      private T physicalResource;

      public InternalVirtualResource(int var1, String var2, @Nullable FrameGraphBuilder.Pass var3, ResourceDescriptor<T> var4) {
         super(var2, var3);
         this.id = var1;
         this.descriptor = var4;
      }

      @Override
      public T get() {
         return Objects.requireNonNull(this.physicalResource, "Resource is not currently available");
      }

      public void acquire(GraphicsResourceAllocator var1) {
         if (this.physicalResource != null) {
            throw new IllegalStateException("Tried to acquire physical resource, but it was already assigned");
         } else {
            this.physicalResource = var1.acquire(this.descriptor);
         }
      }

      public void release(GraphicsResourceAllocator var1) {
         if (this.physicalResource == null) {
            throw new IllegalStateException("Tried to release physical resource that was not allocated");
         } else {
            var1.release(this.descriptor, this.physicalResource);
            this.physicalResource = null;
         }
      }
   }

   class Pass implements FramePass {
      final int id;
      final String name;
      final List<FrameGraphBuilder.Handle<?>> writesFrom = new ArrayList<>();
      final BitSet requiredResourceIds = new BitSet();
      final BitSet requiredPassIds = new BitSet();
      Runnable task = () -> {
      };
      final List<FrameGraphBuilder.InternalVirtualResource<?>> resourcesToAcquire = new ArrayList<>();
      final BitSet resourcesToRelease = new BitSet();
      boolean disableCulling;

      public Pass(final int nullx, final String nullxx) {
         super();
         this.id = nullx;
         this.name = nullxx;
      }

      private <T> void markResourceRequired(FrameGraphBuilder.Handle<T> var1) {
         if (var1.holder instanceof FrameGraphBuilder.InternalVirtualResource var2) {
            this.requiredResourceIds.set(var2.id);
         }
      }

      private void markPassRequired(FrameGraphBuilder.Pass var1) {
         this.requiredPassIds.set(var1.id);
      }

      @Override
      public <T> ResourceHandle<T> createsInternal(String var1, ResourceDescriptor<T> var2) {
         FrameGraphBuilder.InternalVirtualResource var3 = FrameGraphBuilder.this.createInternalResource(var1, var2, this);
         this.requiredResourceIds.set(var3.id);
         return var3.handle;
      }

      @Override
      public <T> void reads(ResourceHandle<T> var1) {
         this._reads((FrameGraphBuilder.Handle<T>)var1);
      }

      private <T> void _reads(FrameGraphBuilder.Handle<T> var1) {
         this.markResourceRequired(var1);
         if (var1.createdBy != null) {
            this.markPassRequired(var1.createdBy);
         }

         var1.readBy.set(this.id);
      }

      @Override
      public <T> ResourceHandle<T> readsAndWrites(ResourceHandle<T> var1) {
         return this._readsAndWrites((FrameGraphBuilder.Handle<T>)var1);
      }

      @Override
      public void requires(FramePass var1) {
         this.requiredPassIds.set(((FrameGraphBuilder.Pass)var1).id);
      }

      @Override
      public void disableCulling() {
         this.disableCulling = true;
      }

      private <T> FrameGraphBuilder.Handle<T> _readsAndWrites(FrameGraphBuilder.Handle<T> var1) {
         this.writesFrom.add(var1);
         this._reads(var1);
         return var1.writeAndAlias(this);
      }

      @Override
      public void executes(Runnable var1) {
         this.task = var1;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }

   abstract static class VirtualResource<T> {
      public final String name;
      public FrameGraphBuilder.Handle<T> handle;

      public VirtualResource(String var1, @Nullable FrameGraphBuilder.Pass var2) {
         super();
         this.name = var1;
         this.handle = new FrameGraphBuilder.Handle<>(this, 0, var2);
      }

      public abstract T get();

      @Override
      public String toString() {
         return this.name;
      }
   }
}
