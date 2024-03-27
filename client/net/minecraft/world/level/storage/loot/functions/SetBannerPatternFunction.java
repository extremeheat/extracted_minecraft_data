package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetBannerPatternFunction extends LootItemConditionalFunction {
   public static final MapCodec<SetBannerPatternFunction> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> commonFields(var0)
            .and(
               var0.group(
                  BannerPatternLayers.CODEC.fieldOf("patterns").forGetter(var0x -> var0x.patterns),
                  Codec.BOOL.fieldOf("append").forGetter(var0x -> var0x.append)
               )
            )
            .apply(var0, SetBannerPatternFunction::new)
   );
   private final BannerPatternLayers patterns;
   private final boolean append;

   SetBannerPatternFunction(List<LootItemCondition> var1, BannerPatternLayers var2, boolean var3) {
      super(var1);
      this.patterns = var2;
      this.append = var3;
   }

   @Override
   protected ItemStack run(ItemStack var1, LootContext var2) {
      if (this.append) {
         var1.update(
            DataComponents.BANNER_PATTERNS,
            BannerPatternLayers.EMPTY,
            this.patterns,
            (var0, var1x) -> new BannerPatternLayers.Builder().addAll(var0).addAll(var1x).build()
         );
      } else {
         var1.set(DataComponents.BANNER_PATTERNS, this.patterns);
      }

      return var1;
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_BANNER_PATTERN;
   }

   public static SetBannerPatternFunction.Builder setBannerPattern(boolean var0) {
      return new SetBannerPatternFunction.Builder(var0);
   }

   public static class Builder extends LootItemConditionalFunction.Builder<SetBannerPatternFunction.Builder> {
      private final BannerPatternLayers.Builder patterns = new BannerPatternLayers.Builder();
      private final boolean append;

      Builder(boolean var1) {
         super();
         this.append = var1;
      }

      protected SetBannerPatternFunction.Builder getThis() {
         return this;
      }

      @Override
      public LootItemFunction build() {
         return new SetBannerPatternFunction(this.getConditions(), this.patterns.build(), this.append);
      }

      public SetBannerPatternFunction.Builder addPattern(Holder<BannerPattern> var1, DyeColor var2) {
         this.patterns.add(var1, var2);
         return this;
      }
   }
}
