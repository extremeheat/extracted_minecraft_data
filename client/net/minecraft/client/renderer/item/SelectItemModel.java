package net.minecraft.client.renderer.item;

import com.mojang.math.Transformation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.multiplayer.CacheSlot;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RegistryContextSwapper;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

public class SelectItemModel<T> implements ItemModel {
   private final SelectItemModelProperty<T> property;
   private final ModelSelector<T> models;

   public SelectItemModel(final SelectItemModelProperty<T> property, final ModelSelector<T> models) {
      super();
      this.property = property;
      this.models = models;
   }

   public void update(final ItemStackRenderState output, final ItemStack item, final ItemModelResolver resolver, final ItemDisplayContext displayContext, final @Nullable ClientLevel level, final @Nullable ItemOwner owner, final int seed) {
      output.appendModelIdentityElement(this);
      T value = this.property.get(item, level, owner == null ? null : owner.asLivingEntity(), seed, displayContext);
      this.models.get(value, level).update(output, item, resolver, displayContext, level, owner, seed);
   }

   public static record Unbaked(Optional<Transformation> transformation, UnbakedSwitch<?, ?> unbakedSwitch, Optional<ItemModel.Unbaked> fallback) implements ItemModel.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Transformation.EXTENDED_CODEC.optionalFieldOf("transformation").forGetter(Unbaked::transformation), SelectItemModel.UnbakedSwitch.MAP_CODEC.forGetter(Unbaked::unbakedSwitch), ItemModels.CODEC.optionalFieldOf("fallback").forGetter(Unbaked::fallback)).apply(i, Unbaked::new));

      public Unbaked {
         super();
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public ItemModel bake(final ItemModel.BakingContext context, final Matrix4fc transformation) {
         Matrix4fc childTransform = Transformation.compose(transformation, this.transformation);
         ItemModel bakedFallback = (ItemModel)this.fallback.map((m) -> m.bake(context, childTransform)).orElseGet(() -> context.missingItemModel(childTransform));
         return this.unbakedSwitch.bake(context, childTransform, bakedFallback);
      }

      public void resolveDependencies(final ResolvableModel.Resolver resolver) {
         this.unbakedSwitch.resolveDependencies(resolver);
         this.fallback.ifPresent((m) -> m.resolveDependencies(resolver));
      }
   }

   public static record UnbakedSwitch<P extends SelectItemModelProperty<T>, T>(P property, List<SwitchCase<T>> cases) {
      public static final MapCodec<UnbakedSwitch<?, ?>> MAP_CODEC;

      public UnbakedSwitch {
         super();
      }

      public ItemModel bake(final ItemModel.BakingContext context, final Matrix4fc transformation, final ItemModel fallback) {
         Object2ObjectMap<T, ItemModel> bakedModels = new Object2ObjectOpenHashMap();

         for(SwitchCase<T> c : this.cases) {
            ItemModel.Unbaked caseModel = c.model;
            ItemModel bakedCaseModel = caseModel.bake(context, transformation);

            for(T value : c.values) {
               bakedModels.put(value, bakedCaseModel);
            }
         }

         bakedModels.defaultReturnValue(fallback);
         return new SelectItemModel(this.property, this.createModelGetter(bakedModels, context.contextSwapper()));
      }

      private ModelSelector<T> createModelGetter(final Object2ObjectMap<T, ItemModel> originalModels, final @Nullable RegistryContextSwapper registrySwapper) {
         if (registrySwapper == null) {
            return (value, var2) -> (ItemModel)originalModels.get(value);
         } else {
            ItemModel defaultModel = (ItemModel)originalModels.defaultReturnValue();
            CacheSlot<ClientLevel, Object2ObjectMap<T, ItemModel>> remappedModelCache = new CacheSlot<ClientLevel, Object2ObjectMap<T, ItemModel>>((clientLevel) -> {
               Object2ObjectMap<T, ItemModel> remappedModels = new Object2ObjectOpenHashMap(originalModels.size());
               remappedModels.defaultReturnValue(defaultModel);
               originalModels.forEach((value, model) -> registrySwapper.swapTo(this.property.valueCodec(), value, clientLevel.registryAccess()).ifSuccess((remappedValue) -> remappedModels.put(remappedValue, model)));
               return remappedModels;
            });
            return (value, context) -> {
               if (context == null) {
                  return (ItemModel)originalModels.get(value);
               } else {
                  return value == null ? defaultModel : (ItemModel)((Object2ObjectMap)remappedModelCache.compute(context)).get(value);
               }
            };
         }
      }

      public void resolveDependencies(final ResolvableModel.Resolver resolver) {
         for(SwitchCase<?> c : this.cases) {
            c.model.resolveDependencies(resolver);
         }

      }

      static {
         MAP_CODEC = SelectItemModelProperties.CODEC.dispatchMap("property", (unbaked) -> unbaked.property().type(), SelectItemModelProperty.Type::switchCodec);
      }
   }

   public static record SwitchCase<T>(List<T> values, ItemModel.Unbaked model) {
      public SwitchCase {
         super();
      }

      public static <T> Codec<SwitchCase<T>> codec(final Codec<T> valueCodec) {
         return RecordCodecBuilder.create((i) -> i.group(ExtraCodecs.nonEmptyList(ExtraCodecs.compactListCodec(valueCodec)).fieldOf("when").forGetter(SwitchCase::values), ItemModels.CODEC.fieldOf("model").forGetter(SwitchCase::model)).apply(i, SwitchCase::new));
      }
   }

   @FunctionalInterface
   public interface ModelSelector<T> {
      ItemModel get(@Nullable T value, @Nullable ClientLevel context);
   }
}
