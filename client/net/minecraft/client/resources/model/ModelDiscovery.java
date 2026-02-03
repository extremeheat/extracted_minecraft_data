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

   public ModelDiscovery(final Map<Identifier, UnbakedModel> unbakedModels, final UnbakedModel missingUnbakedModel) {
      super();
      this.missingModel = new ModelWrapper(MissingBlockModel.LOCATION, missingUnbakedModel, true);
      this.modelWrappers.put(MissingBlockModel.LOCATION, this.missingModel);
      this.uncachedResolver = (rawId) -> {
         Identifier id = (Identifier)rawId;
         UnbakedModel rawModel = (UnbakedModel)unbakedModels.get(id);
         if (rawModel == null) {
            LOGGER.warn("Missing block model: {}", id);
            return this.missingModel;
         } else {
            return this.createAndQueueWrapper(id, rawModel);
         }
      };
      this.resolver = this::getOrCreateModel;
   }

   private static boolean isRoot(final UnbakedModel model) {
      return model.parent() == null;
   }

   private ModelWrapper getOrCreateModel(final Identifier id) {
      return (ModelWrapper)this.modelWrappers.computeIfAbsent(id, this.uncachedResolver);
   }

   private ModelWrapper createAndQueueWrapper(final Identifier id, final UnbakedModel rawModel) {
      boolean isRoot = isRoot(rawModel);
      ModelWrapper result = new ModelWrapper(id, rawModel, isRoot);
      if (!isRoot) {
         this.parentDiscoveryQueue.add(result);
      }

      return result;
   }

   public void addRoot(final ResolvableModel model) {
      model.resolveDependencies(this.resolver);
   }

   public void addSpecialModel(final Identifier id, final UnbakedModel model) {
      if (!isRoot(model)) {
         LOGGER.warn("Trying to add non-root special model {}, ignoring", id);
      } else {
         ModelWrapper previous = (ModelWrapper)this.modelWrappers.put(id, this.createAndQueueWrapper(id, model));
         if (previous != null) {
            LOGGER.warn("Duplicate special model {}", id);
         }

      }
   }

   public ResolvedModel missingModel() {
      return this.missingModel;
   }

   public Map<Identifier, ResolvedModel> resolve() {
      List<ModelWrapper> toValidate = new ArrayList();
      this.discoverDependencies(toValidate);
      propagateValidity(toValidate);
      ImmutableMap.Builder<Identifier, ResolvedModel> result = ImmutableMap.builder();
      this.modelWrappers.forEach((location, model) -> {
         if (model.valid) {
            result.put(location, model);
         } else {
            LOGGER.warn("Model {} ignored due to cyclic dependency", location);
         }

      });
      return result.build();
   }

   private void discoverDependencies(final List<ModelWrapper> toValidate) {
      ModelWrapper current;
      while((current = (ModelWrapper)this.parentDiscoveryQueue.poll()) != null) {
         Identifier parentLocation = (Identifier)Objects.requireNonNull(current.wrapped.parent());
         ModelWrapper parent = this.getOrCreateModel(parentLocation);
         current.parent = parent;
         if (parent.valid) {
            current.valid = true;
         } else {
            toValidate.add(current);
         }
      }

   }

   private static void propagateValidity(final List<ModelWrapper> toValidate) {
      boolean progressed = true;

      while(progressed) {
         progressed = false;
         Iterator<ModelWrapper> iterator = toValidate.iterator();

         while(iterator.hasNext()) {
            ModelWrapper model = (ModelWrapper)iterator.next();
            if (((ModelWrapper)Objects.requireNonNull(model.parent)).valid) {
               model.valid = true;
               iterator.remove();
               progressed = true;
            }
         }
      }

   }

   private static record Slot<T>(int index) {
      private Slot {
         super();
      }
   }

   private static class ModelWrapper implements ResolvedModel {
      private static final Slot<Boolean> KEY_AMBIENT_OCCLUSION = slot(0);
      private static final Slot<UnbakedModel.GuiLight> KEY_GUI_LIGHT = slot(1);
      private static final Slot<UnbakedGeometry> KEY_GEOMETRY = slot(2);
      private static final Slot<ItemTransforms> KEY_TRANSFORMS = slot(3);
      private static final Slot<TextureSlots> KEY_TEXTURE_SLOTS = slot(4);
      private static final Slot<TextureAtlasSprite> KEY_PARTICLE_SPRITE = slot(5);
      private static final Slot<QuadCollection> KEY_DEFAULT_GEOMETRY = slot(6);
      private static final int SLOT_COUNT = 7;
      private final Identifier id;
      private boolean valid;
      private @Nullable ModelWrapper parent;
      private final UnbakedModel wrapped;
      private final AtomicReferenceArray<@Nullable Object> fixedSlots = new AtomicReferenceArray(7);
      private final Map<ModelState, QuadCollection> modelBakeCache = new ConcurrentHashMap();

      private static <T> Slot<T> slot(final int index) {
         Objects.checkIndex(index, 7);
         return new Slot<T>(index);
      }

      private ModelWrapper(final Identifier id, final UnbakedModel wrapped, final boolean valid) {
         super();
         this.id = id;
         this.wrapped = wrapped;
         this.valid = valid;
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

      private <T> @Nullable T getSlot(final Slot<T> key) {
         return (T)this.fixedSlots.get(key.index);
      }

      private <T> T updateSlot(final Slot<T> key, final T value) {
         T currentValue = (T)this.fixedSlots.compareAndExchange(key.index, (Object)null, value);
         return (T)(currentValue == null ? value : currentValue);
      }

      private <T> T getSimpleProperty(final Slot<T> key, final Function<ResolvedModel, T> getter) {
         T result = (T)this.getSlot(key);
         return (T)(result != null ? result : this.updateSlot(key, getter.apply(this)));
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

      public TextureAtlasSprite resolveParticleSprite(final TextureSlots textureSlots, final ModelBaker baker) {
         TextureAtlasSprite result = (TextureAtlasSprite)this.getSlot(KEY_PARTICLE_SPRITE);
         return result != null ? result : (TextureAtlasSprite)this.updateSlot(KEY_PARTICLE_SPRITE, ResolvedModel.resolveParticleSprite(textureSlots, baker, this));
      }

      private QuadCollection bakeDefaultState(final TextureSlots textureSlots, final ModelBaker baker, final ModelState state) {
         QuadCollection result = (QuadCollection)this.getSlot(KEY_DEFAULT_GEOMETRY);
         return result != null ? result : (QuadCollection)this.updateSlot(KEY_DEFAULT_GEOMETRY, this.getTopGeometry().bake(textureSlots, baker, state, this));
      }

      public QuadCollection bakeTopGeometry(final TextureSlots textureSlots, final ModelBaker baker, final ModelState state) {
         return state == BlockModelRotation.IDENTITY ? this.bakeDefaultState(textureSlots, baker, state) : (QuadCollection)this.modelBakeCache.computeIfAbsent(state, (s) -> {
            UnbakedGeometry topGeometry = this.getTopGeometry();
            return topGeometry.bake(textureSlots, baker, s, this);
         });
      }
   }
}
