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
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.WeightedVariants;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockStateModel {
   void collectParts(RandomSource var1, List<BlockModelPart> var2);

   default List<BlockModelPart> collectParts(RandomSource var1) {
      ObjectArrayList var2 = new ObjectArrayList();
      this.collectParts(var1, var2);
      return var2;
   }

   TextureAtlasSprite particleIcon();

   public interface Unbaked extends ResolvableModel {
      Codec<Weighted<Variant>> ELEMENT_CODEC = RecordCodecBuilder.create((var0) -> var0.group(Variant.MAP_CODEC.forGetter(Weighted::value), ExtraCodecs.POSITIVE_INT.optionalFieldOf("weight", 1).forGetter(Weighted::weight)).apply(var0, Weighted::new));
      Codec<WeightedVariants.Unbaked> HARDCODED_WEIGHTED_CODEC = ExtraCodecs.nonEmptyList(ELEMENT_CODEC.listOf()).flatComapMap((var0) -> new WeightedVariants.Unbaked(WeightedList.of(Lists.transform(var0, (var0x) -> var0x.map(SingleVariant.Unbaked::new)))), (var0) -> {
         List var1 = var0.entries().unwrap();
         ArrayList var2 = new ArrayList(var1.size());

         for(Weighted var4 : var1) {
            Object var6 = var4.value();
            if (!(var6 instanceof SingleVariant.Unbaked)) {
               return DataResult.error(() -> "Only single variants are supported");
            }

            SingleVariant.Unbaked var5 = (SingleVariant.Unbaked)var6;
            var2.add(new Weighted(var5.variant(), var4.weight()));
         }

         return DataResult.success(var2);
      });
      Codec<Unbaked> CODEC = Codec.either(HARDCODED_WEIGHTED_CODEC, SingleVariant.Unbaked.CODEC).flatComapMap((var0) -> (Unbaked)var0.map((var0x) -> var0x, (var0x) -> var0x), (var0) -> {
         Objects.requireNonNull(var0);
         byte var2 = 0;
         DataResult var10000;
         //$FF: var2->value
         //0->net/minecraft/client/renderer/block/model/SingleVariant$Unbaked
         //1->net/minecraft/client/resources/model/WeightedVariants$Unbaked
         switch (var0.typeSwitch<invokedynamic>(var0, var2)) {
            case 0:
               SingleVariant.Unbaked var3 = (SingleVariant.Unbaked)var0;
               var10000 = DataResult.success(Either.right(var3));
               break;
            case 1:
               WeightedVariants.Unbaked var4 = (WeightedVariants.Unbaked)var0;
               var10000 = DataResult.success(Either.left(var4));
               break;
            default:
               var10000 = DataResult.error(() -> "Only a single variant or a list of variants are supported");
         }

         return var10000;
      });

      BlockStateModel bake(ModelBaker var1);

      default UnbakedRoot asRoot() {
         return new SimpleCachedUnbakedRoot(this);
      }
   }

   public static class SimpleCachedUnbakedRoot implements UnbakedRoot {
      final Unbaked contents;
      private final ModelBaker.SharedOperationKey<BlockStateModel> bakingKey = new ModelBaker.SharedOperationKey<BlockStateModel>() {
         public BlockStateModel compute(ModelBaker var1) {
            return SimpleCachedUnbakedRoot.this.contents.bake(var1);
         }

         // $FF: synthetic method
         public Object compute(final ModelBaker var1) {
            return this.compute(var1);
         }
      };

      public SimpleCachedUnbakedRoot(Unbaked var1) {
         super();
         this.contents = var1;
      }

      public void resolveDependencies(ResolvableModel.Resolver var1) {
         this.contents.resolveDependencies(var1);
      }

      public BlockStateModel bake(BlockState var1, ModelBaker var2) {
         return (BlockStateModel)var2.compute(this.bakingKey);
      }

      public Object visualEqualityGroup(BlockState var1) {
         return this;
      }
   }

   public interface UnbakedRoot extends ResolvableModel {
      BlockStateModel bake(BlockState var1, ModelBaker var2);

      Object visualEqualityGroup(BlockState var1);
   }
}
