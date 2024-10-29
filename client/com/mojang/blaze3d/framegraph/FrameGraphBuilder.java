package com.mojang.blaze3d.framegraph;

import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.ResourceDescriptor;
import com.mojang.blaze3d.resource.ResourceHandle;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class FrameGraphBuilder {
   private final List<InternalVirtualResource<?>> internalResources = new ArrayList();
   private final List<ExternalResource<?>> externalResources = new ArrayList();
   private final List<Pass> passes = new ArrayList();

   public FrameGraphBuilder() {
      super();
   }

   public FramePass addPass(String var1) {
      Pass var2 = new Pass(this.passes.size(), var1);
      this.passes.add(var2);
      return var2;
   }

   public <T> ResourceHandle<T> importExternal(String var1, T var2) {
      ExternalResource var3 = new ExternalResource(var1, (Pass)null, var2);
      this.externalResources.add(var3);
      return var3.handle;
   }

   public <T> ResourceHandle<T> createInternal(String var1, ResourceDescriptor<T> var2) {
      return this.createInternalResource(var1, var2, (Pass)null).handle;
   }

   <T> InternalVirtualResource<T> createInternalResource(String var1, ResourceDescriptor<T> var2, @Nullable Pass var3) {
      int var4 = this.internalResources.size();
      InternalVirtualResource var5 = new InternalVirtualResource(var4, var1, var3, var2);
      this.internalResources.add(var5);
      return var5;
   }

   public void execute(GraphicsResourceAllocator var1) {
      this.execute(var1, FrameGraphBuilder.Inspector.NONE);
   }

   public void execute(GraphicsResourceAllocator var1, Inspector var2) {
      BitSet var3 = this.identifyPassesToKeep();
      ArrayList var4 = new ArrayList(var3.cardinality());
      BitSet var5 = new BitSet(this.passes.size());
      Iterator var6 = this.passes.iterator();

      Pass var7;
      while(var6.hasNext()) {
         var7 = (Pass)var6.next();
         this.resolvePassOrder(var7, var3, var5, var4);
      }

      this.assignResourceLifetimes(var4);
      var6 = var4.iterator();

      while(var6.hasNext()) {
         var7 = (Pass)var6.next();
         Iterator var8 = var7.resourcesToAcquire.iterator();

         InternalVirtualResource var9;
         while(var8.hasNext()) {
            var9 = (InternalVirtualResource)var8.next();
            var2.acquireResource(var9.name);
            var9.acquire(var1);
         }

         var2.beforeExecutePass(var7.name);
         var7.task.run();
         var2.afterExecutePass(var7.name);

         for(int var10 = var7.resourcesToRelease.nextSetBit(0); var10 >= 0; var10 = var7.resourcesToRelease.nextSetBit(var10 + 1)) {
            var9 = (InternalVirtualResource)this.internalResources.get(var10);
            var2.releaseResource(var9.name);
            var9.release(var1);
         }
      }

   }

   private BitSet identifyPassesToKeep() {
      ArrayDeque var1 = new ArrayDeque(this.passes.size());
      BitSet var2 = new BitSet(this.passes.size());
      Iterator var3 = this.externalResources.iterator();

      while(var3.hasNext()) {
         VirtualResource var4 = (VirtualResource)var3.next();
         Pass var5 = var4.handle.createdBy;
         if (var5 != null) {
            this.discoverAllRequiredPasses(var5, var2, var1);
         }
      }

      var3 = this.passes.iterator();

      while(var3.hasNext()) {
         Pass var6 = (Pass)var3.next();
         if (var6.disableCulling) {
            this.discoverAllRequiredPasses(var6, var2, var1);
         }
      }

      return var2;
   }

   private void discoverAllRequiredPasses(Pass var1, BitSet var2, Deque<Pass> var3) {
      var3.add(var1);

      while(true) {
         Pass var4;
         do {
            if (var3.isEmpty()) {
               return;
            }

            var4 = (Pass)var3.poll();
         } while(var2.get(var4.id));

         var2.set(var4.id);

         for(int var5 = var4.requiredPassIds.nextSetBit(0); var5 >= 0; var5 = var4.requiredPassIds.nextSetBit(var5 + 1)) {
            var3.add((Pass)this.passes.get(var5));
         }
      }
   }

   private void resolvePassOrder(Pass var1, BitSet var2, BitSet var3, List<Pass> var4) {
      if (var3.get(var1.id)) {
         String var9 = (String)var3.stream().mapToObj((var1x) -> {
            return ((Pass)this.passes.get(var1x)).name;
         }).collect(Collectors.joining(", "));
         throw new IllegalStateException("Frame graph cycle detected between " + var9);
      } else if (var2.get(var1.id)) {
         var3.set(var1.id);
         var2.clear(var1.id);

         for(int var5 = var1.requiredPassIds.nextSetBit(0); var5 >= 0; var5 = var1.requiredPassIds.nextSetBit(var5 + 1)) {
            this.resolvePassOrder((Pass)this.passes.get(var5), var2, var3, var4);
         }

         Iterator var8 = var1.writesFrom.iterator();

         while(var8.hasNext()) {
            Handle var6 = (Handle)var8.next();

            for(int var7 = var6.readBy.nextSetBit(0); var7 >= 0; var7 = var6.readBy.nextSetBit(var7 + 1)) {
               if (var7 != var1.id) {
                  this.resolvePassOrder((Pass)this.passes.get(var7), var2, var3, var4);
               }
            }
         }

         var4.add(var1);
         var3.clear(var1.id);
      }
   }

   private void assignResourceLifetimes(Collection<Pass> var1) {
      Pass[] var2 = new Pass[this.internalResources.size()];
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Pass var4 = (Pass)var3.next();

         for(int var5 = var4.requiredResourceIds.nextSetBit(0); var5 >= 0; var5 = var4.requiredResourceIds.nextSetBit(var5 + 1)) {
            InternalVirtualResource var6 = (InternalVirtualResource)this.internalResources.get(var5);
            Pass var7 = var2[var5];
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

   private class Pass implements FramePass {
      final int id;
      final String name;
      final List<Handle<?>> writesFrom = new ArrayList();
      final BitSet requiredResourceIds = new BitSet();
      final BitSet requiredPassIds = new BitSet();
      Runnable task = () -> {
      };
      final List<InternalVirtualResource<?>> resourcesToAcquire = new ArrayList();
      final BitSet resourcesToRelease = new BitSet();
      boolean disableCulling;

      public Pass(final int var2, final String var3) {
         super();
         this.id = var2;
         this.name = var3;
      }

      private <T> void markResourceRequired(Handle<T> var1) {
         VirtualResource var3 = var1.holder;
         if (var3 instanceof InternalVirtualResource var2) {
            this.requiredResourceIds.set(var2.id);
         }

      }

      private void markPassRequired(Pass var1) {
         this.requiredPassIds.set(var1.id);
      }

      public <T> ResourceHandle<T> createsInternal(String var1, ResourceDescriptor<T> var2) {
         InternalVirtualResource var3 = FrameGraphBuilder.this.createInternalResource(var1, var2, this);
         this.requiredResourceIds.set(var3.id);
         return var3.handle;
      }

      public <T> void reads(ResourceHandle<T> var1) {
         this._reads((Handle)var1);
      }

      private <T> void _reads(Handle<T> var1) {
         this.markResourceRequired(var1);
         if (var1.createdBy != null) {
            this.markPassRequired(var1.createdBy);
         }

         var1.readBy.set(this.id);
      }

      public <T> ResourceHandle<T> readsAndWrites(ResourceHandle<T> var1) {
         return this._readsAndWrites((Handle)var1);
      }

      public void requires(FramePass var1) {
         this.requiredPassIds.set(((Pass)var1).id);
      }

      public void disableCulling() {
         this.disableCulling = true;
      }

      private <T> Handle<T> _readsAndWrites(Handle<T> var1) {
         this.writesFrom.add(var1);
         this._reads(var1);
         return var1.writeAndAlias(this);
      }

      public void executes(Runnable var1) {
         this.task = var1;
      }

      public String toString() {
         return this.name;
      }
   }

   static class ExternalResource<T> extends VirtualResource<T> {
      private final T resource;

      public ExternalResource(String var1, @Nullable Pass var2, T var3) {
         super(var1, var2);
         this.resource = var3;
      }

      public T get() {
         return this.resource;
      }
   }

   private static class Handle<T> implements ResourceHandle<T> {
      final VirtualResource<T> holder;
      private final int version;
      @Nullable
      final Pass createdBy;
      final BitSet readBy = new BitSet();
      @Nullable
      private Handle<T> aliasedBy;

      Handle(VirtualResource<T> var1, int var2, @Nullable Pass var3) {
         super();
         this.holder = var1;
         this.version = var2;
         this.createdBy = var3;
      }

      public T get() {
         return this.holder.get();
      }

      Handle<T> writeAndAlias(Pass var1) {
         if (this.holder.handle != this) {
            String var10002 = String.valueOf(this);
            throw new IllegalStateException("Handle " + var10002 + " is no longer valid, as its contents were moved into " + String.valueOf(this.aliasedBy));
         } else {
            Handle var2 = new Handle(this.holder, this.version + 1, var1);
            this.holder.handle = var2;
            this.aliasedBy = var2;
            return var2;
         }
      }

      public String toString() {
         String var10000;
         if (this.createdBy != null) {
            var10000 = String.valueOf(this.holder);
            return var10000 + "#" + this.version + " (from " + String.valueOf(this.createdBy) + ")";
         } else {
            var10000 = String.valueOf(this.holder);
            return var10000 + "#" + this.version;
         }
      }
   }

   private static class InternalVirtualResource<T> extends VirtualResource<T> {
      final int id;
      private final ResourceDescriptor<T> descriptor;
      @Nullable
      private T physicalResource;

      public InternalVirtualResource(int var1, String var2, @Nullable Pass var3, ResourceDescriptor<T> var4) {
         super(var2, var3);
         this.id = var1;
         this.descriptor = var4;
      }

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

   public interface Inspector {
      Inspector NONE = new Inspector() {
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

   private abstract static class VirtualResource<T> {
      public final String name;
      public Handle<T> handle;

      public VirtualResource(String var1, @Nullable Pass var2) {
         super();
         this.name = var1;
         this.handle = new Handle(this, 0, var2);
      }

      public abstract T get();

      public String toString() {
         return this.name;
      }
   }
}
