package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class MegaTreeConfiguration extends TreeConfiguration {
   public final int heightInterval;
   public final int crownHeight;

   protected MegaTreeConfiguration(BlockStateProvider var1, BlockStateProvider var2, List var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4);
      this.heightInterval = var5;
      this.crownHeight = var6;
   }

   public Dynamic serialize(DynamicOps var1) {
      Dynamic var2 = new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("height_interval"), var1.createInt(this.heightInterval), var1.createString("crown_height"), var1.createInt(this.crownHeight))));
      return var2.merge(super.serialize(var1));
   }

   public static MegaTreeConfiguration deserialize(Dynamic var0) {
      TreeConfiguration var1 = TreeConfiguration.deserialize(var0);
      return new MegaTreeConfiguration(var1.trunkProvider, var1.leavesProvider, var1.decorators, var1.baseHeight, var0.get("height_interval").asInt(0), var0.get("crown_height").asInt(0));
   }

   public static class MegaTreeConfigurationBuilder extends TreeConfiguration.TreeConfigurationBuilder {
      private List decorators = ImmutableList.of();
      private int baseHeight;
      private int heightInterval;
      private int crownHeight;

      public MegaTreeConfigurationBuilder(BlockStateProvider var1, BlockStateProvider var2) {
         super(var1, var2);
      }

      public MegaTreeConfiguration.MegaTreeConfigurationBuilder decorators(List var1) {
         this.decorators = var1;
         return this;
      }

      public MegaTreeConfiguration.MegaTreeConfigurationBuilder baseHeight(int var1) {
         this.baseHeight = var1;
         return this;
      }

      public MegaTreeConfiguration.MegaTreeConfigurationBuilder heightInterval(int var1) {
         this.heightInterval = var1;
         return this;
      }

      public MegaTreeConfiguration.MegaTreeConfigurationBuilder crownHeight(int var1) {
         this.crownHeight = var1;
         return this;
      }

      public MegaTreeConfiguration build() {
         return new MegaTreeConfiguration(this.trunkProvider, this.leavesProvider, this.decorators, this.baseHeight, this.heightInterval, this.crownHeight);
      }

      // $FF: synthetic method
      public TreeConfiguration build() {
         return this.build();
      }

      // $FF: synthetic method
      public TreeConfiguration.TreeConfigurationBuilder baseHeight(int var1) {
         return this.baseHeight(var1);
      }
   }
}
