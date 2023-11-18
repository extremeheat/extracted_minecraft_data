package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetBannerPatternFunction extends LootItemConditionalFunction {
   private static final Codec<Pair<Holder<BannerPattern>, DyeColor>> PATTERN_CODEC = Codec.mapPair(
         BuiltInRegistries.BANNER_PATTERN.holderByNameCodec().fieldOf("pattern"), DyeColor.CODEC.fieldOf("color")
      )
      .codec();
   public static final Codec<SetBannerPatternFunction> CODEC = RecordCodecBuilder.create(
      var0 -> commonFields(var0)
            .and(
               var0.group(
                  PATTERN_CODEC.listOf().fieldOf("patterns").forGetter(var0x -> var0x.patterns), Codec.BOOL.fieldOf("append").forGetter(var0x -> var0x.append)
               )
            )
            .apply(var0, SetBannerPatternFunction::new)
   );
   private final List<Pair<Holder<BannerPattern>, DyeColor>> patterns;
   private final boolean append;

   SetBannerPatternFunction(List<LootItemCondition> var1, List<Pair<Holder<BannerPattern>, DyeColor>> var2, boolean var3) {
      super(var1);
      this.patterns = var2;
      this.append = var3;
   }

   @Override
   protected ItemStack run(ItemStack var1, LootContext var2) {
      CompoundTag var3 = BlockItem.getBlockEntityData(var1);
      if (var3 == null) {
         var3 = new CompoundTag();
      }

      BannerPattern.Builder var4 = new BannerPattern.Builder();
      this.patterns.forEach(var4::addPattern);
      ListTag var5 = var4.toListTag();
      ListTag var6;
      if (this.append) {
         var6 = var3.getList("Patterns", 10).copy();
         var6.addAll(var5);
      } else {
         var6 = var5;
      }

      var3.put("Patterns", var6);
      BlockItem.setBlockEntityData(var1, BlockEntityType.BANNER, var3);
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
      private final com.google.common.collect.ImmutableList.Builder<Pair<Holder<BannerPattern>, DyeColor>> patterns = ImmutableList.builder();
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

      public SetBannerPatternFunction.Builder addPattern(ResourceKey<BannerPattern> var1, DyeColor var2) {
         return this.addPattern(BuiltInRegistries.BANNER_PATTERN.getHolderOrThrow(var1), var2);
      }

      public SetBannerPatternFunction.Builder addPattern(Holder<BannerPattern> var1, DyeColor var2) {
         this.patterns.add(Pair.of(var1, var2));
         return this;
      }
   }
}
