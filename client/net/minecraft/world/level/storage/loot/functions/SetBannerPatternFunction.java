package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetBannerPatternFunction extends LootItemConditionalFunction {
   final List<Pair<BannerPattern, DyeColor>> patterns;
   final boolean append;

   SetBannerPatternFunction(LootItemCondition[] var1, List<Pair<BannerPattern, DyeColor>> var2, boolean var3) {
      super(var1);
      this.patterns = var2;
      this.append = var3;
   }

   protected ItemStack run(ItemStack var1, LootContext var2) {
      CompoundTag var3 = BlockItem.getBlockEntityData(var1);
      if (var3 == null) {
         var3 = new CompoundTag();
      }

      BannerPattern.Builder var4 = new BannerPattern.Builder();
      List var10000 = this.patterns;
      Objects.requireNonNull(var4);
      var10000.forEach(var4::addPattern);
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

   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_BANNER_PATTERN;
   }

   public static SetBannerPatternFunction.Builder setBannerPattern(boolean var0) {
      return new SetBannerPatternFunction.Builder(var0);
   }

   public static class Builder extends LootItemConditionalFunction.Builder<SetBannerPatternFunction.Builder> {
      private final com.google.common.collect.ImmutableList.Builder<Pair<BannerPattern, DyeColor>> patterns = ImmutableList.builder();
      private final boolean append;

      Builder(boolean var1) {
         super();
         this.append = var1;
      }

      protected SetBannerPatternFunction.Builder getThis() {
         return this;
      }

      public LootItemFunction build() {
         return new SetBannerPatternFunction(this.getConditions(), this.patterns.build(), this.append);
      }

      public SetBannerPatternFunction.Builder addPattern(BannerPattern var1, DyeColor var2) {
         this.patterns.add(Pair.of(var1, var2));
         return this;
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetBannerPatternFunction> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, SetBannerPatternFunction var2, JsonSerializationContext var3) {
         super.serialize(var1, (LootItemConditionalFunction)var2, var3);
         JsonArray var4 = new JsonArray();
         var2.patterns.forEach((var1x) -> {
            JsonObject var2 = new JsonObject();
            var2.addProperty("pattern", ((BannerPattern)var1x.getFirst()).getFilename());
            var2.addProperty("color", ((DyeColor)var1x.getSecond()).getName());
            var4.add(var2);
         });
         var1.add("patterns", var4);
         var1.addProperty("append", var2.append);
      }

      public SetBannerPatternFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         com.google.common.collect.ImmutableList.Builder var4 = ImmutableList.builder();
         JsonArray var5 = GsonHelper.getAsJsonArray(var1, "patterns");

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            JsonObject var7 = GsonHelper.convertToJsonObject(var5.get(var6), "pattern[" + var6 + "]");
            String var8 = GsonHelper.getAsString(var7, "pattern");
            BannerPattern var9 = BannerPattern.byFilename(var8);
            if (var9 == null) {
               throw new JsonSyntaxException("Unknown pattern: " + var8);
            }

            String var10 = GsonHelper.getAsString(var7, "color");
            DyeColor var11 = DyeColor.byName(var10, (DyeColor)null);
            if (var11 == null) {
               throw new JsonSyntaxException("Unknown color: " + var10);
            }

            var4.add(Pair.of(var9, var11));
         }

         boolean var12 = GsonHelper.getAsBoolean(var1, "append");
         return new SetBannerPatternFunction(var3, var4.build(), var12);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
