package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.core.Registry;

public class BlockStateProviderType {
   public static final BlockStateProviderType SIMPLE_STATE_PROVIDER = register("simple_state_provider", SimpleStateProvider::new);
   public static final BlockStateProviderType WEIGHTED_STATE_PROVIDER = register("weighted_state_provider", WeightedStateProvider::new);
   public static final BlockStateProviderType PLAIN_FLOWER_PROVIDER = register("plain_flower_provider", PlainFlowerProvider::new);
   public static final BlockStateProviderType FOREST_FLOWER_PROVIDER = register("forest_flower_provider", ForestFlowerProvider::new);
   private final Function deserializer;

   private static BlockStateProviderType register(String var0, Function var1) {
      return (BlockStateProviderType)Registry.register(Registry.BLOCKSTATE_PROVIDER_TYPES, (String)var0, new BlockStateProviderType(var1));
   }

   private BlockStateProviderType(Function var1) {
      this.deserializer = var1;
   }

   public BlockStateProvider deserialize(Dynamic var1) {
      return (BlockStateProvider)this.deserializer.apply(var1);
   }
}
