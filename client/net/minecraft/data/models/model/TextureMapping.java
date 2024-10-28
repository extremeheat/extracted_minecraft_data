package net.minecraft.data.models.model;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class TextureMapping {
   private final Map<TextureSlot, ResourceLocation> slots = Maps.newHashMap();
   private final Set<TextureSlot> forcedSlots = Sets.newHashSet();

   public TextureMapping() {
      super();
   }

   public TextureMapping put(TextureSlot var1, ResourceLocation var2) {
      this.slots.put(var1, var2);
      return this;
   }

   public TextureMapping putForced(TextureSlot var1, ResourceLocation var2) {
      this.slots.put(var1, var2);
      this.forcedSlots.add(var1);
      return this;
   }

   public Stream<TextureSlot> getForced() {
      return this.forcedSlots.stream();
   }

   public TextureMapping copySlot(TextureSlot var1, TextureSlot var2) {
      this.slots.put(var2, (ResourceLocation)this.slots.get(var1));
      return this;
   }

   public TextureMapping copyForced(TextureSlot var1, TextureSlot var2) {
      this.slots.put(var2, (ResourceLocation)this.slots.get(var1));
      this.forcedSlots.add(var2);
      return this;
   }

   public ResourceLocation get(TextureSlot var1) {
      for(TextureSlot var2 = var1; var2 != null; var2 = var2.getParent()) {
         ResourceLocation var3 = (ResourceLocation)this.slots.get(var2);
         if (var3 != null) {
            return var3;
         }
      }

      throw new IllegalStateException("Can't find texture for slot " + String.valueOf(var1));
   }

   public TextureMapping copyAndUpdate(TextureSlot var1, ResourceLocation var2) {
      TextureMapping var3 = new TextureMapping();
      var3.slots.putAll(this.slots);
      var3.forcedSlots.addAll(this.forcedSlots);
      var3.put(var1, var2);
      return var3;
   }

   public static TextureMapping cube(Block var0) {
      ResourceLocation var1 = getBlockTexture(var0);
      return cube(var1);
   }

   public static TextureMapping defaultTexture(Block var0) {
      ResourceLocation var1 = getBlockTexture(var0);
      return defaultTexture(var1);
   }

   public static TextureMapping defaultTexture(ResourceLocation var0) {
      return (new TextureMapping()).put(TextureSlot.TEXTURE, var0);
   }

   public static TextureMapping cube(ResourceLocation var0) {
      return (new TextureMapping()).put(TextureSlot.ALL, var0);
   }

   public static TextureMapping cross(Block var0) {
      return singleSlot(TextureSlot.CROSS, getBlockTexture(var0));
   }

   public static TextureMapping cross(ResourceLocation var0) {
      return singleSlot(TextureSlot.CROSS, var0);
   }

   public static TextureMapping plant(Block var0) {
      return singleSlot(TextureSlot.PLANT, getBlockTexture(var0));
   }

   public static TextureMapping plant(ResourceLocation var0) {
      return singleSlot(TextureSlot.PLANT, var0);
   }

   public static TextureMapping rail(Block var0) {
      return singleSlot(TextureSlot.RAIL, getBlockTexture(var0));
   }

   public static TextureMapping rail(ResourceLocation var0) {
      return singleSlot(TextureSlot.RAIL, var0);
   }

   public static TextureMapping wool(Block var0) {
      return singleSlot(TextureSlot.WOOL, getBlockTexture(var0));
   }

   public static TextureMapping flowerbed(Block var0) {
      return (new TextureMapping()).put(TextureSlot.FLOWERBED, getBlockTexture(var0)).put(TextureSlot.STEM, getBlockTexture(var0, "_stem"));
   }

   public static TextureMapping wool(ResourceLocation var0) {
      return singleSlot(TextureSlot.WOOL, var0);
   }

   public static TextureMapping stem(Block var0) {
      return singleSlot(TextureSlot.STEM, getBlockTexture(var0));
   }

   public static TextureMapping attachedStem(Block var0, Block var1) {
      return (new TextureMapping()).put(TextureSlot.STEM, getBlockTexture(var0)).put(TextureSlot.UPPER_STEM, getBlockTexture(var1));
   }

   public static TextureMapping pattern(Block var0) {
      return singleSlot(TextureSlot.PATTERN, getBlockTexture(var0));
   }

   public static TextureMapping fan(Block var0) {
      return singleSlot(TextureSlot.FAN, getBlockTexture(var0));
   }

   public static TextureMapping crop(ResourceLocation var0) {
      return singleSlot(TextureSlot.CROP, var0);
   }

   public static TextureMapping pane(Block var0, Block var1) {
      return (new TextureMapping()).put(TextureSlot.PANE, getBlockTexture(var0)).put(TextureSlot.EDGE, getBlockTexture(var1, "_top"));
   }

   public static TextureMapping singleSlot(TextureSlot var0, ResourceLocation var1) {
      return (new TextureMapping()).put(var0, var1);
   }

   public static TextureMapping column(Block var0) {
      return (new TextureMapping()).put(TextureSlot.SIDE, getBlockTexture(var0, "_side")).put(TextureSlot.END, getBlockTexture(var0, "_top"));
   }

   public static TextureMapping cubeTop(Block var0) {
      return (new TextureMapping()).put(TextureSlot.SIDE, getBlockTexture(var0, "_side")).put(TextureSlot.TOP, getBlockTexture(var0, "_top"));
   }

   public static TextureMapping pottedAzalea(Block var0) {
      return (new TextureMapping()).put(TextureSlot.PLANT, getBlockTexture(var0, "_plant")).put(TextureSlot.SIDE, getBlockTexture(var0, "_side")).put(TextureSlot.TOP, getBlockTexture(var0, "_top"));
   }

   public static TextureMapping logColumn(Block var0) {
      return (new TextureMapping()).put(TextureSlot.SIDE, getBlockTexture(var0)).put(TextureSlot.END, getBlockTexture(var0, "_top")).put(TextureSlot.PARTICLE, getBlockTexture(var0));
   }

   public static TextureMapping column(ResourceLocation var0, ResourceLocation var1) {
      return (new TextureMapping()).put(TextureSlot.SIDE, var0).put(TextureSlot.END, var1);
   }

   public static TextureMapping fence(Block var0) {
      return (new TextureMapping()).put(TextureSlot.TEXTURE, getBlockTexture(var0)).put(TextureSlot.SIDE, getBlockTexture(var0, "_side")).put(TextureSlot.TOP, getBlockTexture(var0, "_top"));
   }

   public static TextureMapping customParticle(Block var0) {
      return (new TextureMapping()).put(TextureSlot.TEXTURE, getBlockTexture(var0)).put(TextureSlot.PARTICLE, getBlockTexture(var0, "_particle"));
   }

   public static TextureMapping cubeBottomTop(Block var0) {
      return (new TextureMapping()).put(TextureSlot.SIDE, getBlockTexture(var0, "_side")).put(TextureSlot.TOP, getBlockTexture(var0, "_top")).put(TextureSlot.BOTTOM, getBlockTexture(var0, "_bottom"));
   }

   public static TextureMapping cubeBottomTopWithWall(Block var0) {
      ResourceLocation var1 = getBlockTexture(var0);
      return (new TextureMapping()).put(TextureSlot.WALL, var1).put(TextureSlot.SIDE, var1).put(TextureSlot.TOP, getBlockTexture(var0, "_top")).put(TextureSlot.BOTTOM, getBlockTexture(var0, "_bottom"));
   }

   public static TextureMapping columnWithWall(Block var0) {
      ResourceLocation var1 = getBlockTexture(var0);
      return (new TextureMapping()).put(TextureSlot.TEXTURE, var1).put(TextureSlot.WALL, var1).put(TextureSlot.SIDE, var1).put(TextureSlot.END, getBlockTexture(var0, "_top"));
   }

   public static TextureMapping door(ResourceLocation var0, ResourceLocation var1) {
      return (new TextureMapping()).put(TextureSlot.TOP, var0).put(TextureSlot.BOTTOM, var1);
   }

   public static TextureMapping door(Block var0) {
      return (new TextureMapping()).put(TextureSlot.TOP, getBlockTexture(var0, "_top")).put(TextureSlot.BOTTOM, getBlockTexture(var0, "_bottom"));
   }

   public static TextureMapping particle(Block var0) {
      return (new TextureMapping()).put(TextureSlot.PARTICLE, getBlockTexture(var0));
   }

   public static TextureMapping particle(ResourceLocation var0) {
      return (new TextureMapping()).put(TextureSlot.PARTICLE, var0);
   }

   public static TextureMapping fire0(Block var0) {
      return (new TextureMapping()).put(TextureSlot.FIRE, getBlockTexture(var0, "_0"));
   }

   public static TextureMapping fire1(Block var0) {
      return (new TextureMapping()).put(TextureSlot.FIRE, getBlockTexture(var0, "_1"));
   }

   public static TextureMapping lantern(Block var0) {
      return (new TextureMapping()).put(TextureSlot.LANTERN, getBlockTexture(var0));
   }

   public static TextureMapping torch(Block var0) {
      return (new TextureMapping()).put(TextureSlot.TORCH, getBlockTexture(var0));
   }

   public static TextureMapping torch(ResourceLocation var0) {
      return (new TextureMapping()).put(TextureSlot.TORCH, var0);
   }

   public static TextureMapping trialSpawner(Block var0, String var1, String var2) {
      return (new TextureMapping()).put(TextureSlot.SIDE, getBlockTexture(var0, var1)).put(TextureSlot.TOP, getBlockTexture(var0, var2)).put(TextureSlot.BOTTOM, getBlockTexture(var0, "_bottom"));
   }

   public static TextureMapping vault(Block var0, String var1, String var2, String var3, String var4) {
      return (new TextureMapping()).put(TextureSlot.FRONT, getBlockTexture(var0, var1)).put(TextureSlot.SIDE, getBlockTexture(var0, var2)).put(TextureSlot.TOP, getBlockTexture(var0, var3)).put(TextureSlot.BOTTOM, getBlockTexture(var0, var4));
   }

   public static TextureMapping particleFromItem(Item var0) {
      return (new TextureMapping()).put(TextureSlot.PARTICLE, getItemTexture(var0));
   }

   public static TextureMapping commandBlock(Block var0) {
      return (new TextureMapping()).put(TextureSlot.SIDE, getBlockTexture(var0, "_side")).put(TextureSlot.FRONT, getBlockTexture(var0, "_front")).put(TextureSlot.BACK, getBlockTexture(var0, "_back"));
   }

   public static TextureMapping orientableCube(Block var0) {
      return (new TextureMapping()).put(TextureSlot.SIDE, getBlockTexture(var0, "_side")).put(TextureSlot.FRONT, getBlockTexture(var0, "_front")).put(TextureSlot.TOP, getBlockTexture(var0, "_top")).put(TextureSlot.BOTTOM, getBlockTexture(var0, "_bottom"));
   }

   public static TextureMapping orientableCubeOnlyTop(Block var0) {
      return (new TextureMapping()).put(TextureSlot.SIDE, getBlockTexture(var0, "_side")).put(TextureSlot.FRONT, getBlockTexture(var0, "_front")).put(TextureSlot.TOP, getBlockTexture(var0, "_top"));
   }

   public static TextureMapping orientableCubeSameEnds(Block var0) {
      return (new TextureMapping()).put(TextureSlot.SIDE, getBlockTexture(var0, "_side")).put(TextureSlot.FRONT, getBlockTexture(var0, "_front")).put(TextureSlot.END, getBlockTexture(var0, "_end"));
   }

   public static TextureMapping top(Block var0) {
      return (new TextureMapping()).put(TextureSlot.TOP, getBlockTexture(var0, "_top"));
   }

   public static TextureMapping craftingTable(Block var0, Block var1) {
      return (new TextureMapping()).put(TextureSlot.PARTICLE, getBlockTexture(var0, "_front")).put(TextureSlot.DOWN, getBlockTexture(var1)).put(TextureSlot.UP, getBlockTexture(var0, "_top")).put(TextureSlot.NORTH, getBlockTexture(var0, "_front")).put(TextureSlot.EAST, getBlockTexture(var0, "_side")).put(TextureSlot.SOUTH, getBlockTexture(var0, "_side")).put(TextureSlot.WEST, getBlockTexture(var0, "_front"));
   }

   public static TextureMapping fletchingTable(Block var0, Block var1) {
      return (new TextureMapping()).put(TextureSlot.PARTICLE, getBlockTexture(var0, "_front")).put(TextureSlot.DOWN, getBlockTexture(var1)).put(TextureSlot.UP, getBlockTexture(var0, "_top")).put(TextureSlot.NORTH, getBlockTexture(var0, "_front")).put(TextureSlot.SOUTH, getBlockTexture(var0, "_front")).put(TextureSlot.EAST, getBlockTexture(var0, "_side")).put(TextureSlot.WEST, getBlockTexture(var0, "_side"));
   }

   public static TextureMapping snifferEgg(String var0) {
      return (new TextureMapping()).put(TextureSlot.PARTICLE, getBlockTexture(Blocks.SNIFFER_EGG, var0 + "_north")).put(TextureSlot.BOTTOM, getBlockTexture(Blocks.SNIFFER_EGG, var0 + "_bottom")).put(TextureSlot.TOP, getBlockTexture(Blocks.SNIFFER_EGG, var0 + "_top")).put(TextureSlot.NORTH, getBlockTexture(Blocks.SNIFFER_EGG, var0 + "_north")).put(TextureSlot.SOUTH, getBlockTexture(Blocks.SNIFFER_EGG, var0 + "_south")).put(TextureSlot.EAST, getBlockTexture(Blocks.SNIFFER_EGG, var0 + "_east")).put(TextureSlot.WEST, getBlockTexture(Blocks.SNIFFER_EGG, var0 + "_west"));
   }

   public static TextureMapping campfire(Block var0) {
      return (new TextureMapping()).put(TextureSlot.LIT_LOG, getBlockTexture(var0, "_log_lit")).put(TextureSlot.FIRE, getBlockTexture(var0, "_fire"));
   }

   public static TextureMapping candleCake(Block var0, boolean var1) {
      return (new TextureMapping()).put(TextureSlot.PARTICLE, getBlockTexture(Blocks.CAKE, "_side")).put(TextureSlot.BOTTOM, getBlockTexture(Blocks.CAKE, "_bottom")).put(TextureSlot.TOP, getBlockTexture(Blocks.CAKE, "_top")).put(TextureSlot.SIDE, getBlockTexture(Blocks.CAKE, "_side")).put(TextureSlot.CANDLE, getBlockTexture(var0, var1 ? "_lit" : ""));
   }

   public static TextureMapping cauldron(ResourceLocation var0) {
      return (new TextureMapping()).put(TextureSlot.PARTICLE, getBlockTexture(Blocks.CAULDRON, "_side")).put(TextureSlot.SIDE, getBlockTexture(Blocks.CAULDRON, "_side")).put(TextureSlot.TOP, getBlockTexture(Blocks.CAULDRON, "_top")).put(TextureSlot.BOTTOM, getBlockTexture(Blocks.CAULDRON, "_bottom")).put(TextureSlot.INSIDE, getBlockTexture(Blocks.CAULDRON, "_inner")).put(TextureSlot.CONTENT, var0);
   }

   public static TextureMapping sculkShrieker(boolean var0) {
      String var1 = var0 ? "_can_summon" : "";
      return (new TextureMapping()).put(TextureSlot.PARTICLE, getBlockTexture(Blocks.SCULK_SHRIEKER, "_bottom")).put(TextureSlot.SIDE, getBlockTexture(Blocks.SCULK_SHRIEKER, "_side")).put(TextureSlot.TOP, getBlockTexture(Blocks.SCULK_SHRIEKER, "_top")).put(TextureSlot.INNER_TOP, getBlockTexture(Blocks.SCULK_SHRIEKER, var1 + "_inner_top")).put(TextureSlot.BOTTOM, getBlockTexture(Blocks.SCULK_SHRIEKER, "_bottom"));
   }

   public static TextureMapping layer0(Item var0) {
      return (new TextureMapping()).put(TextureSlot.LAYER0, getItemTexture(var0));
   }

   public static TextureMapping layer0(Block var0) {
      return (new TextureMapping()).put(TextureSlot.LAYER0, getBlockTexture(var0));
   }

   public static TextureMapping layer0(ResourceLocation var0) {
      return (new TextureMapping()).put(TextureSlot.LAYER0, var0);
   }

   public static TextureMapping layered(ResourceLocation var0, ResourceLocation var1) {
      return (new TextureMapping()).put(TextureSlot.LAYER0, var0).put(TextureSlot.LAYER1, var1);
   }

   public static TextureMapping layered(ResourceLocation var0, ResourceLocation var1, ResourceLocation var2) {
      return (new TextureMapping()).put(TextureSlot.LAYER0, var0).put(TextureSlot.LAYER1, var1).put(TextureSlot.LAYER2, var2);
   }

   public static ResourceLocation getBlockTexture(Block var0) {
      ResourceLocation var1 = BuiltInRegistries.BLOCK.getKey(var0);
      return var1.withPrefix("block/");
   }

   public static ResourceLocation getBlockTexture(Block var0, String var1) {
      ResourceLocation var2 = BuiltInRegistries.BLOCK.getKey(var0);
      return var2.withPath((var1x) -> {
         return "block/" + var1x + var1;
      });
   }

   public static ResourceLocation getItemTexture(Item var0) {
      ResourceLocation var1 = BuiltInRegistries.ITEM.getKey(var0);
      return var1.withPrefix("item/");
   }

   public static ResourceLocation getItemTexture(Item var0, String var1) {
      ResourceLocation var2 = BuiltInRegistries.ITEM.getKey(var0);
      return var2.withPath((var1x) -> {
         return "item/" + var1x + var1;
      });
   }
}
