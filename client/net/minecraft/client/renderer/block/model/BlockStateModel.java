package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.WeightedVariants;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockStateModel {
   void collectParts(RandomSource random, List<BlockModelPart> output);

   default List<BlockModelPart> collectParts(final RandomSource random) {
      List<BlockModelPart> parts = new ObjectArrayList();
      this.collectParts(random, parts);
      return parts;
   }

   Material.Baked particleMaterial();

   boolean hasTranslucency();

   public interface Unbaked extends ResolvableModel {
      Codec<Weighted<Variant>> ELEMENT_CODEC = RecordCodecBuilder.create((i) -> i.group(Variant.MAP_CODEC.forGetter(Weighted::value), ExtraCodecs.POSITIVE_INT.optionalFieldOf("weight", 1).forGetter(Weighted::weight)).apply(i, Weighted::new));
      Codec<WeightedVariants.Unbaked> HARDCODED_WEIGHTED_CODEC = ExtraCodecs.nonEmptyList(ELEMENT_CODEC.listOf()).flatComapMap((w) -> new WeightedVariants.Unbaked(WeightedList.of(Lists.transform(w, (e) -> e.map(SingleVariant.Unbaked::new)))), (unbaked) -> {
         List<Weighted<Unbaked>> entries = unbaked.entries().unwrap();
         List<Weighted<Variant>> result = new ArrayList(entries.size());

         for(Weighted<Unbaked> entry : entries) {
            Object patt0$temp = entry.value();
            if (!(patt0$temp instanceof SingleVariant.Unbaked)) {
               return DataResult.error(() -> "Only single variants are supported");
            }

            SingleVariant.Unbaked singleVariant = (SingleVariant.Unbaked)patt0$temp;
            result.add(new Weighted(singleVariant.variant(), entry.weight()));
         }

         return DataResult.success(result);
      });
      Codec<Unbaked> CODEC = Codec.either(HARDCODED_WEIGHTED_CODEC, SingleVariant.Unbaked.CODEC).flatComapMap((v) -> (Unbaked)v.map((l) -> l, (r) -> r), (o) -> {
         Objects.requireNonNull(o);
         int index$1 = 0;
         DataResult var10000;
         //$FF: index$1->value
         //0->net/minecraft/client/renderer/block/model/SingleVariant$Unbaked
         //1->net/minecraft/client/resources/model/WeightedVariants$Unbaked
         switch (o.typeSwitch<invokedynamic>(o, index$1)) {
            case 0:
               SingleVariant.Unbaked single = (SingleVariant.Unbaked)o;
               var10000 = DataResult.success(Either.right(single));
               break;
            case 1:
               WeightedVariants.Unbaked multiple = (WeightedVariants.Unbaked)o;
               var10000 = DataResult.success(Either.left(multiple));
               break;
            default:
               var10000 = DataResult.error(() -> "Only a single variant or a list of variants are supported");
         }

         return var10000;
      });

      BlockStateModel bake(ModelBaker modelBakery);

      default UnbakedRoot asRoot() {
         return new SimpleCachedUnbakedRoot(this);
      }
   }

   public static class SimpleCachedUnbakedRoot implements UnbakedRoot {
      private final Unbaked contents;
      private final ModelBaker.SharedOperationKey<BlockStateModel> bakingKey = new ModelBaker.SharedOperationKey<BlockStateModel>() {
         {
            Objects.requireNonNull(SimpleCachedUnbakedRoot.this);
         }

         public BlockStateModel compute(final ModelBaker modelBakery) {
            return SimpleCachedUnbakedRoot.this.contents.bake(modelBakery);
         }
      };

      public SimpleCachedUnbakedRoot(final Unbaked contents) {
         super();
         this.contents = contents;
      }

      public void resolveDependencies(final ResolvableModel.Resolver resolver) {
         this.contents.resolveDependencies(resolver);
      }

      public BlockStateModel bake(final BlockState blockState, final ModelBaker modelBakery) {
         return (BlockStateModel)modelBakery.compute(this.bakingKey);
      }

      public Object visualEqualityGroup(final BlockState blockState) {
         return this;
      }
   }

   public interface UnbakedRoot extends ResolvableModel {
      BlockStateModel bake(BlockState blockState, ModelBaker modelBakery);

      Object visualEqualityGroup(BlockState blockState);
   }
}
