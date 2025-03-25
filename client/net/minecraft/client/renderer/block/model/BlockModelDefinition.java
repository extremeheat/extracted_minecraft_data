package net.minecraft.client.renderer.block.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.client.renderer.block.model.multipart.MultiPartModel;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.slf4j.Logger;

public record BlockModelDefinition(Optional<SimpleModelSelectors> simpleModels, Optional<MultiPartDefinition> multiPart) {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec<BlockModelDefinition> CODEC = RecordCodecBuilder.create((var0) -> var0.group(BlockModelDefinition.SimpleModelSelectors.CODEC.optionalFieldOf("variants").forGetter(BlockModelDefinition::simpleModels), BlockModelDefinition.MultiPartDefinition.CODEC.optionalFieldOf("multipart").forGetter(BlockModelDefinition::multiPart)).apply(var0, BlockModelDefinition::new)).validate((var0) -> var0.simpleModels().isEmpty() && var0.multiPart().isEmpty() ? DataResult.error(() -> "Neither 'variants' nor 'multipart' found") : DataResult.success(var0));

   public BlockModelDefinition(Optional<SimpleModelSelectors> var1, Optional<MultiPartDefinition> var2) {
      super();
      this.simpleModels = var1;
      this.multiPart = var2;
   }

   public Map<BlockState, BlockStateModel.UnbakedRoot> instantiate(StateDefinition<Block, BlockState> var1, Supplier<String> var2) {
      IdentityHashMap var3 = new IdentityHashMap();
      this.simpleModels.ifPresent((var3x) -> var3x.instantiate(var1, var2, (var1x, var2x) -> {
            BlockStateModel.UnbakedRoot var3x = (BlockStateModel.UnbakedRoot)var3.put(var1x, var2x);
            if (var3x != null) {
               throw new IllegalArgumentException("Overlapping definition on state: " + String.valueOf(var1x));
            }
         }));
      this.multiPart.ifPresent((var2x) -> {
         ImmutableList var3x = var1.getPossibleStates();
         MultiPartModel.Unbaked var4 = var2x.instantiate(var1);

         for(BlockState var6 : var3x) {
            var3.putIfAbsent(var6, var4);
         }

      });
      return var3;
   }

   public static record SimpleModelSelectors(Map<String, BlockStateModel.Unbaked> models) {
      public static final Codec<SimpleModelSelectors> CODEC;

      public SimpleModelSelectors(Map<String, BlockStateModel.Unbaked> var1) {
         super();
         this.models = var1;
      }

      public void instantiate(StateDefinition<Block, BlockState> var1, Supplier<String> var2, BiConsumer<BlockState, BlockStateModel.UnbakedRoot> var3) {
         this.models.forEach((var3x, var4) -> {
            try {
               Predicate var5 = VariantSelector.predicate(var1, var3x);
               BlockStateModel.UnbakedRoot var6 = var4.asRoot();
               UnmodifiableIterator var7 = var1.getPossibleStates().iterator();

               while(var7.hasNext()) {
                  BlockState var8 = (BlockState)var7.next();
                  if (var5.test(var8)) {
                     var3.accept(var8, var6);
                  }
               }
            } catch (Exception var9) {
               BlockModelDefinition.LOGGER.warn("Exception loading blockstate definition: '{}' for variant: '{}': {}", new Object[]{var2.get(), var3x, var9.getMessage()});
            }

         });
      }

      static {
         CODEC = ExtraCodecs.nonEmptyMap(Codec.unboundedMap(Codec.STRING, BlockStateModel.Unbaked.CODEC)).xmap(SimpleModelSelectors::new, SimpleModelSelectors::models);
      }
   }

   public static record MultiPartDefinition(List<Selector> selectors) {
      public static final Codec<MultiPartDefinition> CODEC;

      public MultiPartDefinition(List<Selector> var1) {
         super();
         this.selectors = var1;
      }

      public MultiPartModel.Unbaked instantiate(StateDefinition<Block, BlockState> var1) {
         ImmutableList.Builder var2 = ImmutableList.builderWithExpectedSize(this.selectors.size());

         for(Selector var4 : this.selectors) {
            var2.add(new MultiPartModel.Selector(var4.instantiate(var1), var4.variant()));
         }

         return new MultiPartModel.Unbaked(var2.build());
      }

      static {
         CODEC = ExtraCodecs.nonEmptyList(Selector.CODEC.listOf()).xmap(MultiPartDefinition::new, MultiPartDefinition::selectors);
      }
   }
}
