package net.minecraft.client.resources.model;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class ModelDiscovery {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Object2ObjectMap<Identifier, ModelWrapper> modelWrappers = new Object2ObjectOpenHashMap();
   private final ModelWrapper missingModel;
   private final Object2ObjectFunction<Identifier, ModelWrapper> uncachedResolver;
   private final ResolvableModel.Resolver resolver;
   private final Queue<ModelWrapper> parentDiscoveryQueue = new ArrayDeque();

   public ModelDiscovery(Map<Identifier, UnbakedModel> var1, UnbakedModel var2) {
      super();
      this.missingModel = new ModelWrapper(MissingBlockModel.LOCATION, var2, true);
      this.modelWrappers.put(MissingBlockModel.LOCATION, this.missingModel);
      this.uncachedResolver = (var2x) -> {
         Identifier var3 = (Identifier)var2x;
         UnbakedModel var4 = (UnbakedModel)var1.get(var3);
         if (var4 == null) {
            LOGGER.warn("Missing block model: {}", var3);
            return this.missingModel;
         } else {
            return this.createAndQueueWrapper(var3, var4);
         }
      };
      this.resolver = this::getOrCreateModel;
   }

   private static boolean isRoot(UnbakedModel var0) {
      return var0.parent() == null;
   }

   private ModelWrapper getOrCreateModel(Identifier var1) {
      return (ModelWrapper)this.modelWrappers.computeIfAbsent(var1, this.uncachedResolver);
   }

   private ModelWrapper createAndQueueWrapper(Identifier var1, UnbakedModel var2) {
      boolean var3 = isRoot(var2);
      ModelWrapper var4 = new ModelWrapper(var1, var2, var3);
      if (!var3) {
         this.parentDiscoveryQueue.add(var4);
      }

      return var4;
   }

   public void addRoot(ResolvableModel var1) {
      var1.resolveDependencies(this.resolver);
   }

   public void addSpecialModel(Identifier var1, UnbakedModel var2) {
      if (!isRoot(var2)) {
         LOGGER.warn("Trying to add non-root special model {}, ignoring", var1);
      } else {
         ModelWrapper var3 = (ModelWrapper)this.modelWrappers.put(var1, this.createAndQueueWrapper(var1, var2));
         if (var3 != null) {
            LOGGER.warn("Duplicate special model {}", var1);
         }

      }
   }

   public ResolvedModel missingModel() {
      return this.missingModel;
   }

   public Map<Identifier, ResolvedModel> resolve() {
      ArrayList var1 = new ArrayList();
      this.discoverDependencies(var1);
      propagateValidity(var1);
      ImmutableMap.Builder var2 = ImmutableMap.builder();
      this.modelWrappers.forEach((var1x, var2x) -> {
         if (var2x.valid) {
            var2.put(var1x, var2x);
         } else {
            LOGGER.warn("Model {} ignored due to cyclic dependency", var1x);
         }

      });
      return var2.build();
   }

   private void discoverDependencies(List<ModelWrapper> var1) {
      ModelWrapper var2;
      while((var2 = (ModelWrapper)this.parentDiscoveryQueue.poll()) != null) {
         Identifier var3 = (Identifier)Objects.requireNonNull(var2.wrapped.parent());
         ModelWrapper var4 = this.getOrCreateModel(var3);
         var2.parent = var4;
         if (var4.valid) {
            var2.valid = true;
         } else {
            var1.add(var2);
         }
      }

   }

   private static void propagateValidity(List<ModelWrapper> var0) {
      boolean var1 = true;

      while(var1) {
         var1 = false;
         Iterator var2 = var0.iterator();

         while(var2.hasNext()) {
            ModelWrapper var3 = (ModelWrapper)var2.next();
            if (((ModelWrapper)Objects.requireNonNull(var3.parent)).valid) {
               var3.valid = true;
               var2.remove();
               var1 = true;
            }
         }
      }

   }

   static record Slot<T>(int index) {
      final int index;

      Slot(int var1) {
         super();
         this.index = var1;
      }
   }

   static class ModelWrapper implements ResolvedModel {
      private static final Slot<Boolean> KEY_AMBIENT_OCCLUSION = slot(0);
      private static final Slot<UnbakedModel.GuiLight> KEY_GUI_LIGHT = slot(1);
      private static final Slot<UnbakedGeometry> KEY_GEOMETRY = slot(2);
      private static final Slot<ItemTransforms> KEY_TRANSFORMS = slot(3);
      private static final Slot<TextureSlots> KEY_TEXTURE_SLOTS = slot(4);
      private static final Slot<TextureAtlasSprite> KEY_PARTICLE_SPRITE = slot(5);
      private static final Slot<QuadCollection> KEY_DEFAULT_GEOMETRY = slot(6);
      private static final int SLOT_COUNT = 7;
      private final Identifier id;
      boolean valid;
      @Nullable ModelWrapper parent;
      final UnbakedModel wrapped;
      private final AtomicReferenceArray<@Nullable Object> fixedSlots = new AtomicReferenceArray(7);
      private final Map<ModelState, QuadCollection> modelBakeCache = new ConcurrentHashMap();

      private static <T> Slot<T> slot(int var0) {
         Objects.checkIndex(var0, 7);
         return new Slot<T>(var0);
      }

      ModelWrapper(Identifier var1, UnbakedModel var2, boolean var3) {
         super();
         this.id = var1;
         this.wrapped = var2;
         this.valid = var3;
      }

      public UnbakedModel wrapped() {
         return this.wrapped;
      }

      public @Nullable ResolvedModel parent() {
         return this.parent;
      }

      public String debugName() {
         return this.id.toString();
      }

      private <T> @Nullable T getSlot(Slot<T> var1) {
         return (T)this.fixedSlots.get(var1.index);
      }

      private <T> T updateSlot(Slot<T> var1, T var2) {
         Object var3 = this.fixedSlots.compareAndExchange(var1.index, (Object)null, var2);
         return var3 == null ? var2 : var3;
      }

      private <T> T getSimpleProperty(Slot<T> var1, Function<ResolvedModel, T> var2) {
         Object var3 = this.getSlot(var1);
         return (T)(var3 != null ? var3 : this.updateSlot(var1, var2.apply(this)));
      }

      public boolean getTopAmbientOcclusion() {
         return (Boolean)this.getSimpleProperty(KEY_AMBIENT_OCCLUSION, ResolvedModel::findTopAmbientOcclusion);
      }

      public UnbakedModel.GuiLight getTopGuiLight() {
         return (UnbakedModel.GuiLight)this.getSimpleProperty(KEY_GUI_LIGHT, ResolvedModel::findTopGuiLight);
      }

      public ItemTransforms getTopTransforms() {
         return (ItemTransforms)this.getSimpleProperty(KEY_TRANSFORMS, ResolvedModel::findTopTransforms);
      }

      public UnbakedGeometry getTopGeometry() {
         return (UnbakedGeometry)this.getSimpleProperty(KEY_GEOMETRY, ResolvedModel::findTopGeometry);
      }

      public TextureSlots getTopTextureSlots() {
         return (TextureSlots)this.getSimpleProperty(KEY_TEXTURE_SLOTS, ResolvedModel::findTopTextureSlots);
      }

      public TextureAtlasSprite resolveParticleSprite(TextureSlots var1, ModelBaker var2) {
         TextureAtlasSprite var3 = (TextureAtlasSprite)this.getSlot(KEY_PARTICLE_SPRITE);
         return var3 != null ? var3 : (TextureAtlasSprite)this.updateSlot(KEY_PARTICLE_SPRITE, ResolvedModel.resolveParticleSprite(var1, var2, this));
      }

      private QuadCollection bakeDefaultState(TextureSlots var1, ModelBaker var2, ModelState var3) {
         QuadCollection var4 = (QuadCollection)this.getSlot(KEY_DEFAULT_GEOMETRY);
         return var4 != null ? var4 : (QuadCollection)this.updateSlot(KEY_DEFAULT_GEOMETRY, this.getTopGeometry().bake(var1, var2, var3, this));
      }

      public QuadCollection bakeTopGeometry(TextureSlots var1, ModelBaker var2, ModelState var3) {
         return var3 == BlockModelRotation.IDENTITY ? this.bakeDefaultState(var1, var2, var3) : (QuadCollection)this.modelBakeCache.computeIfAbsent(var3, (var3x) -> {
            UnbakedGeometry var4 = this.getTopGeometry();
            return var4.bake(var1, var2, var3x, this);
         });
      }
   }
}
