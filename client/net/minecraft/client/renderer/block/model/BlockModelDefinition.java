package net.minecraft.client.renderer.block.model;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.slf4j.Logger;

public record BlockModelDefinition(Map<String, MultiVariant> variants, Optional<MultiPart.Definition> multiPart) {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec<BlockModelDefinition> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.unboundedMap(Codec.STRING, MultiVariant.CODEC).optionalFieldOf("variants", Map.of()).forGetter(BlockModelDefinition::variants), MultiPart.Definition.CODEC.optionalFieldOf("multipart").forGetter(BlockModelDefinition::multiPart)).apply(var0, BlockModelDefinition::new)).validate((var0) -> var0.variants().isEmpty() && var0.multiPart().isEmpty() ? DataResult.error(() -> "Neither 'variants' nor 'multipart' found") : DataResult.success(var0));

   public BlockModelDefinition(Map<String, MultiVariant> var1, Optional<MultiPart.Definition> var2) {
      super();
      this.variants = var1;
      this.multiPart = var2;
   }

   public Map<BlockState, BlockStateModel.Unbaked> instantiate(StateDefinition<Block, BlockState> var1, Supplier<String> var2) {
      IdentityHashMap var3 = new IdentityHashMap();
      ImmutableList var4 = var1.getPossibleStates();
      MultiPart var5;
      if (this.multiPart.isPresent()) {
         var5 = ((MultiPart.Definition)this.multiPart.get()).instantiate(var1);
         var4.forEach((var2x) -> var3.put(var2x, var5));
      } else {
         var5 = null;
      }

      this.variants.forEach((var6, var7) -> {
         try {
            Predicate var8 = VariantSelector.predicate(var1, var6);

            for(BlockState var10 : var4) {
               if (var8.test(var10)) {
                  BlockStateModel.Unbaked var11 = (BlockStateModel.Unbaked)var3.put(var10, var7);
                  if (var11 != null && var11 != var5) {
                     String var12 = (String)((Map.Entry)this.variants.entrySet().stream().filter((var1x) -> var1x.getValue() == var11).findFirst().get()).getKey();
                     throw new IllegalArgumentException("Overlapping definition with: " + var12);
                  }
               }
            }
         } catch (Exception var13) {
            LOGGER.warn("Exception loading blockstate definition: '{}' for variant: '{}': {}", new Object[]{var2.get(), var6, var13.getMessage()});
         }

      });
      return var3;
   }
}
