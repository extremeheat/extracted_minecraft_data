package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class RandomizedIntStateProvider extends BlockStateProvider {
   public static final Codec<RandomizedIntStateProvider> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(BlockStateProvider.CODEC.fieldOf("source").forGetter((var0x) -> {
         return var0x.source;
      }), Codec.STRING.fieldOf("property").forGetter((var0x) -> {
         return var0x.propertyName;
      }), IntProvider.CODEC.fieldOf("values").forGetter((var0x) -> {
         return var0x.values;
      })).apply(var0, RandomizedIntStateProvider::new);
   });
   private final BlockStateProvider source;
   private final String propertyName;
   @Nullable
   private IntegerProperty property;
   private final IntProvider values;

   public RandomizedIntStateProvider(BlockStateProvider var1, IntegerProperty var2, IntProvider var3) {
      super();
      this.source = var1;
      this.property = var2;
      this.propertyName = var2.getName();
      this.values = var3;
      Collection var4 = var2.getPossibleValues();

      for(int var5 = var3.getMinValue(); var5 <= var3.getMaxValue(); ++var5) {
         if (!var4.contains(var5)) {
            String var10002 = var2.getName();
            throw new IllegalArgumentException("Property value out of range: " + var10002 + ": " + var5);
         }
      }

   }

   public RandomizedIntStateProvider(BlockStateProvider var1, String var2, IntProvider var3) {
      super();
      this.source = var1;
      this.propertyName = var2;
      this.values = var3;
   }

   protected BlockStateProviderType<?> type() {
      return BlockStateProviderType.RANDOMIZED_INT_STATE_PROVIDER;
   }

   public BlockState getState(Random var1, BlockPos var2) {
      BlockState var3 = this.source.getState(var1, var2);
      if (this.property == null || !var3.hasProperty(this.property)) {
         this.property = findProperty(var3, this.propertyName);
      }

      return (BlockState)var3.setValue(this.property, this.values.sample(var1));
   }

   private static IntegerProperty findProperty(BlockState var0, String var1) {
      Collection var2 = var0.getProperties();
      Optional var3 = var2.stream().filter((var1x) -> {
         return var1x.getName().equals(var1);
      }).filter((var0x) -> {
         return var0x instanceof IntegerProperty;
      }).map((var0x) -> {
         return (IntegerProperty)var0x;
      }).findAny();
      return (IntegerProperty)var3.orElseThrow(() -> {
         return new IllegalArgumentException("Illegal property: " + var1);
      });
   }
}
