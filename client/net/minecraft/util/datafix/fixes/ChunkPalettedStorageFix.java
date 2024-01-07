package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.util.datafix.PackedBitStorage;
import org.slf4j.Logger;

public class ChunkPalettedStorageFix extends DataFix {
   private static final int NORTH_WEST_MASK = 128;
   private static final int WEST_MASK = 64;
   private static final int SOUTH_WEST_MASK = 32;
   private static final int SOUTH_MASK = 16;
   private static final int SOUTH_EAST_MASK = 8;
   private static final int EAST_MASK = 4;
   private static final int NORTH_EAST_MASK = 2;
   private static final int NORTH_MASK = 1;
   static final Logger LOGGER = LogUtils.getLogger();
   static final BitSet VIRTUAL = new BitSet(256);
   static final BitSet FIX = new BitSet(256);
   static final Dynamic<?> PUMPKIN = BlockStateData.parse("{Name:'minecraft:pumpkin'}");
   static final Dynamic<?> SNOWY_PODZOL = BlockStateData.parse("{Name:'minecraft:podzol',Properties:{snowy:'true'}}");
   static final Dynamic<?> SNOWY_GRASS = BlockStateData.parse("{Name:'minecraft:grass_block',Properties:{snowy:'true'}}");
   static final Dynamic<?> SNOWY_MYCELIUM = BlockStateData.parse("{Name:'minecraft:mycelium',Properties:{snowy:'true'}}");
   static final Dynamic<?> UPPER_SUNFLOWER = BlockStateData.parse("{Name:'minecraft:sunflower',Properties:{half:'upper'}}");
   static final Dynamic<?> UPPER_LILAC = BlockStateData.parse("{Name:'minecraft:lilac',Properties:{half:'upper'}}");
   static final Dynamic<?> UPPER_TALL_GRASS = BlockStateData.parse("{Name:'minecraft:tall_grass',Properties:{half:'upper'}}");
   static final Dynamic<?> UPPER_LARGE_FERN = BlockStateData.parse("{Name:'minecraft:large_fern',Properties:{half:'upper'}}");
   static final Dynamic<?> UPPER_ROSE_BUSH = BlockStateData.parse("{Name:'minecraft:rose_bush',Properties:{half:'upper'}}");
   static final Dynamic<?> UPPER_PEONY = BlockStateData.parse("{Name:'minecraft:peony',Properties:{half:'upper'}}");
   static final Map<String, Dynamic<?>> FLOWER_POT_MAP = (Map<String, Dynamic<?>>)DataFixUtils.make(Maps.newHashMap(), var0 -> {
      var0.put("minecraft:air0", BlockStateData.parse("{Name:'minecraft:flower_pot'}"));
      var0.put("minecraft:red_flower0", BlockStateData.parse("{Name:'minecraft:potted_poppy'}"));
      var0.put("minecraft:red_flower1", BlockStateData.parse("{Name:'minecraft:potted_blue_orchid'}"));
      var0.put("minecraft:red_flower2", BlockStateData.parse("{Name:'minecraft:potted_allium'}"));
      var0.put("minecraft:red_flower3", BlockStateData.parse("{Name:'minecraft:potted_azure_bluet'}"));
      var0.put("minecraft:red_flower4", BlockStateData.parse("{Name:'minecraft:potted_red_tulip'}"));
      var0.put("minecraft:red_flower5", BlockStateData.parse("{Name:'minecraft:potted_orange_tulip'}"));
      var0.put("minecraft:red_flower6", BlockStateData.parse("{Name:'minecraft:potted_white_tulip'}"));
      var0.put("minecraft:red_flower7", BlockStateData.parse("{Name:'minecraft:potted_pink_tulip'}"));
      var0.put("minecraft:red_flower8", BlockStateData.parse("{Name:'minecraft:potted_oxeye_daisy'}"));
      var0.put("minecraft:yellow_flower0", BlockStateData.parse("{Name:'minecraft:potted_dandelion'}"));
      var0.put("minecraft:sapling0", BlockStateData.parse("{Name:'minecraft:potted_oak_sapling'}"));
      var0.put("minecraft:sapling1", BlockStateData.parse("{Name:'minecraft:potted_spruce_sapling'}"));
      var0.put("minecraft:sapling2", BlockStateData.parse("{Name:'minecraft:potted_birch_sapling'}"));
      var0.put("minecraft:sapling3", BlockStateData.parse("{Name:'minecraft:potted_jungle_sapling'}"));
      var0.put("minecraft:sapling4", BlockStateData.parse("{Name:'minecraft:potted_acacia_sapling'}"));
      var0.put("minecraft:sapling5", BlockStateData.parse("{Name:'minecraft:potted_dark_oak_sapling'}"));
      var0.put("minecraft:red_mushroom0", BlockStateData.parse("{Name:'minecraft:potted_red_mushroom'}"));
      var0.put("minecraft:brown_mushroom0", BlockStateData.parse("{Name:'minecraft:potted_brown_mushroom'}"));
      var0.put("minecraft:deadbush0", BlockStateData.parse("{Name:'minecraft:potted_dead_bush'}"));
      var0.put("minecraft:tallgrass2", BlockStateData.parse("{Name:'minecraft:potted_fern'}"));
      var0.put("minecraft:cactus0", BlockStateData.getTag(2240));
   });
   static final Map<String, Dynamic<?>> SKULL_MAP = (Map<String, Dynamic<?>>)DataFixUtils.make(Maps.newHashMap(), var0 -> {
      mapSkull(var0, 0, "skeleton", "skull");
      mapSkull(var0, 1, "wither_skeleton", "skull");
      mapSkull(var0, 2, "zombie", "head");
      mapSkull(var0, 3, "player", "head");
      mapSkull(var0, 4, "creeper", "head");
      mapSkull(var0, 5, "dragon", "head");
   });
   static final Map<String, Dynamic<?>> DOOR_MAP = (Map<String, Dynamic<?>>)DataFixUtils.make(Maps.newHashMap(), var0 -> {
      mapDoor(var0, "oak_door", 1024);
      mapDoor(var0, "iron_door", 1136);
      mapDoor(var0, "spruce_door", 3088);
      mapDoor(var0, "birch_door", 3104);
      mapDoor(var0, "jungle_door", 3120);
      mapDoor(var0, "acacia_door", 3136);
      mapDoor(var0, "dark_oak_door", 3152);
   });
   static final Map<String, Dynamic<?>> NOTE_BLOCK_MAP = (Map<String, Dynamic<?>>)DataFixUtils.make(Maps.newHashMap(), var0 -> {
      for(int var1 = 0; var1 < 26; ++var1) {
         var0.put("true" + var1, BlockStateData.parse("{Name:'minecraft:note_block',Properties:{powered:'true',note:'" + var1 + "'}}"));
         var0.put("false" + var1, BlockStateData.parse("{Name:'minecraft:note_block',Properties:{powered:'false',note:'" + var1 + "'}}"));
      }
   });
   private static final Int2ObjectMap<String> DYE_COLOR_MAP = (Int2ObjectMap<String>)DataFixUtils.make(new Int2ObjectOpenHashMap(), var0 -> {
      var0.put(0, "white");
      var0.put(1, "orange");
      var0.put(2, "magenta");
      var0.put(3, "light_blue");
      var0.put(4, "yellow");
      var0.put(5, "lime");
      var0.put(6, "pink");
      var0.put(7, "gray");
      var0.put(8, "light_gray");
      var0.put(9, "cyan");
      var0.put(10, "purple");
      var0.put(11, "blue");
      var0.put(12, "brown");
      var0.put(13, "green");
      var0.put(14, "red");
      var0.put(15, "black");
   });
   static final Map<String, Dynamic<?>> BED_BLOCK_MAP = (Map<String, Dynamic<?>>)DataFixUtils.make(Maps.newHashMap(), var0 -> {
      ObjectIterator var1 = DYE_COLOR_MAP.int2ObjectEntrySet().iterator();

      while(var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         if (!Objects.equals(var2.getValue(), "red")) {
            addBeds(var0, var2.getIntKey(), (String)var2.getValue());
         }
      }
   });
   static final Map<String, Dynamic<?>> BANNER_BLOCK_MAP = (Map<String, Dynamic<?>>)DataFixUtils.make(Maps.newHashMap(), var0 -> {
      ObjectIterator var1 = DYE_COLOR_MAP.int2ObjectEntrySet().iterator();

      while(var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         if (!Objects.equals(var2.getValue(), "white")) {
            addBanners(var0, 15 - var2.getIntKey(), (String)var2.getValue());
         }
      }
   });
   static final Dynamic<?> AIR = BlockStateData.getTag(0);
   private static final int SIZE = 4096;

   public ChunkPalettedStorageFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   private static void mapSkull(Map<String, Dynamic<?>> var0, int var1, String var2, String var3) {
      var0.put(var1 + "north", BlockStateData.parse("{Name:'minecraft:" + var2 + "_wall_" + var3 + "',Properties:{facing:'north'}}"));
      var0.put(var1 + "east", BlockStateData.parse("{Name:'minecraft:" + var2 + "_wall_" + var3 + "',Properties:{facing:'east'}}"));
      var0.put(var1 + "south", BlockStateData.parse("{Name:'minecraft:" + var2 + "_wall_" + var3 + "',Properties:{facing:'south'}}"));
      var0.put(var1 + "west", BlockStateData.parse("{Name:'minecraft:" + var2 + "_wall_" + var3 + "',Properties:{facing:'west'}}"));

      for(int var4 = 0; var4 < 16; ++var4) {
         var0.put("" + var1 + var4, BlockStateData.parse("{Name:'minecraft:" + var2 + "_" + var3 + "',Properties:{rotation:'" + var4 + "'}}"));
      }
   }

   private static void mapDoor(Map<String, Dynamic<?>> var0, String var1, int var2) {
      var0.put(
         "minecraft:" + var1 + "eastlowerleftfalsefalse",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'false'}}")
      );
      var0.put(
         "minecraft:" + var1 + "eastlowerleftfalsetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'true'}}")
      );
      var0.put(
         "minecraft:" + var1 + "eastlowerlefttruefalse",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'false'}}")
      );
      var0.put(
         "minecraft:" + var1 + "eastlowerlefttruetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'true'}}")
      );
      var0.put("minecraft:" + var1 + "eastlowerrightfalsefalse", BlockStateData.getTag(var2));
      var0.put(
         "minecraft:" + var1 + "eastlowerrightfalsetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'true'}}")
      );
      var0.put("minecraft:" + var1 + "eastlowerrighttruefalse", BlockStateData.getTag(var2 + 4));
      var0.put(
         "minecraft:" + var1 + "eastlowerrighttruetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'true'}}")
      );
      var0.put("minecraft:" + var1 + "eastupperleftfalsefalse", BlockStateData.getTag(var2 + 8));
      var0.put("minecraft:" + var1 + "eastupperleftfalsetrue", BlockStateData.getTag(var2 + 10));
      var0.put(
         "minecraft:" + var1 + "eastupperlefttruefalse",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'false'}}")
      );
      var0.put(
         "minecraft:" + var1 + "eastupperlefttruetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'true'}}")
      );
      var0.put("minecraft:" + var1 + "eastupperrightfalsefalse", BlockStateData.getTag(var2 + 9));
      var0.put("minecraft:" + var1 + "eastupperrightfalsetrue", BlockStateData.getTag(var2 + 11));
      var0.put(
         "minecraft:" + var1 + "eastupperrighttruefalse",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'false'}}")
      );
      var0.put(
         "minecraft:" + var1 + "eastupperrighttruetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'true'}}")
      );
      var0.put(
         "minecraft:" + var1 + "northlowerleftfalsefalse",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'false'}}")
      );
      var0.put(
         "minecraft:" + var1 + "northlowerleftfalsetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'true'}}")
      );
      var0.put(
         "minecraft:" + var1 + "northlowerlefttruefalse",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'false'}}")
      );
      var0.put(
         "minecraft:" + var1 + "northlowerlefttruetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'true'}}")
      );
      var0.put("minecraft:" + var1 + "northlowerrightfalsefalse", BlockStateData.getTag(var2 + 3));
      var0.put(
         "minecraft:" + var1 + "northlowerrightfalsetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'true'}}")
      );
      var0.put("minecraft:" + var1 + "northlowerrighttruefalse", BlockStateData.getTag(var2 + 7));
      var0.put(
         "minecraft:" + var1 + "northlowerrighttruetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'true'}}")
      );
      var0.put(
         "minecraft:" + var1 + "northupperleftfalsefalse",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'false'}}")
      );
      var0.put(
         "minecraft:" + var1 + "northupperleftfalsetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'true'}}")
      );
      var0.put(
         "minecraft:" + var1 + "northupperlefttruefalse",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'false'}}")
      );
      var0.put(
         "minecraft:" + var1 + "northupperlefttruetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'true'}}")
      );
      var0.put(
         "minecraft:" + var1 + "northupperrightfalsefalse",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'false'}}")
      );
      var0.put(
         "minecraft:" + var1 + "northupperrightfalsetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'true'}}")
      );
      var0.put(
         "minecraft:" + var1 + "northupperrighttruefalse",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'false'}}")
      );
      var0.put(
         "minecraft:" + var1 + "northupperrighttruetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'true'}}")
      );
      var0.put(
         "minecraft:" + var1 + "southlowerleftfalsefalse",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'false'}}")
      );
      var0.put(
         "minecraft:" + var1 + "southlowerleftfalsetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'true'}}")
      );
      var0.put(
         "minecraft:" + var1 + "southlowerlefttruefalse",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'false'}}")
      );
      var0.put(
         "minecraft:" + var1 + "southlowerlefttruetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'true'}}")
      );
      var0.put("minecraft:" + var1 + "southlowerrightfalsefalse", BlockStateData.getTag(var2 + 1));
      var0.put(
         "minecraft:" + var1 + "southlowerrightfalsetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'true'}}")
      );
      var0.put("minecraft:" + var1 + "southlowerrighttruefalse", BlockStateData.getTag(var2 + 5));
      var0.put(
         "minecraft:" + var1 + "southlowerrighttruetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'true'}}")
      );
      var0.put(
         "minecraft:" + var1 + "southupperleftfalsefalse",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'false'}}")
      );
      var0.put(
         "minecraft:" + var1 + "southupperleftfalsetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'true'}}")
      );
      var0.put(
         "minecraft:" + var1 + "southupperlefttruefalse",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'false'}}")
      );
      var0.put(
         "minecraft:" + var1 + "southupperlefttruetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'true'}}")
      );
      var0.put(
         "minecraft:" + var1 + "southupperrightfalsefalse",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'false'}}")
      );
      var0.put(
         "minecraft:" + var1 + "southupperrightfalsetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'true'}}")
      );
      var0.put(
         "minecraft:" + var1 + "southupperrighttruefalse",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'false'}}")
      );
      var0.put(
         "minecraft:" + var1 + "southupperrighttruetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'true'}}")
      );
      var0.put(
         "minecraft:" + var1 + "westlowerleftfalsefalse",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'false'}}")
      );
      var0.put(
         "minecraft:" + var1 + "westlowerleftfalsetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'true'}}")
      );
      var0.put(
         "minecraft:" + var1 + "westlowerlefttruefalse",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'false'}}")
      );
      var0.put(
         "minecraft:" + var1 + "westlowerlefttruetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'true'}}")
      );
      var0.put("minecraft:" + var1 + "westlowerrightfalsefalse", BlockStateData.getTag(var2 + 2));
      var0.put(
         "minecraft:" + var1 + "westlowerrightfalsetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'true'}}")
      );
      var0.put("minecraft:" + var1 + "westlowerrighttruefalse", BlockStateData.getTag(var2 + 6));
      var0.put(
         "minecraft:" + var1 + "westlowerrighttruetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'true'}}")
      );
      var0.put(
         "minecraft:" + var1 + "westupperleftfalsefalse",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'false'}}")
      );
      var0.put(
         "minecraft:" + var1 + "westupperleftfalsetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'true'}}")
      );
      var0.put(
         "minecraft:" + var1 + "westupperlefttruefalse",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'false'}}")
      );
      var0.put(
         "minecraft:" + var1 + "westupperlefttruetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'true'}}")
      );
      var0.put(
         "minecraft:" + var1 + "westupperrightfalsefalse",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'false'}}")
      );
      var0.put(
         "minecraft:" + var1 + "westupperrightfalsetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'true'}}")
      );
      var0.put(
         "minecraft:" + var1 + "westupperrighttruefalse",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'false'}}")
      );
      var0.put(
         "minecraft:" + var1 + "westupperrighttruetrue",
         BlockStateData.parse("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'true'}}")
      );
   }

   private static void addBeds(Map<String, Dynamic<?>> var0, int var1, String var2) {
      var0.put("southfalsefoot" + var1, BlockStateData.parse("{Name:'minecraft:" + var2 + "_bed',Properties:{facing:'south',occupied:'false',part:'foot'}}"));
      var0.put("westfalsefoot" + var1, BlockStateData.parse("{Name:'minecraft:" + var2 + "_bed',Properties:{facing:'west',occupied:'false',part:'foot'}}"));
      var0.put("northfalsefoot" + var1, BlockStateData.parse("{Name:'minecraft:" + var2 + "_bed',Properties:{facing:'north',occupied:'false',part:'foot'}}"));
      var0.put("eastfalsefoot" + var1, BlockStateData.parse("{Name:'minecraft:" + var2 + "_bed',Properties:{facing:'east',occupied:'false',part:'foot'}}"));
      var0.put("southfalsehead" + var1, BlockStateData.parse("{Name:'minecraft:" + var2 + "_bed',Properties:{facing:'south',occupied:'false',part:'head'}}"));
      var0.put("westfalsehead" + var1, BlockStateData.parse("{Name:'minecraft:" + var2 + "_bed',Properties:{facing:'west',occupied:'false',part:'head'}}"));
      var0.put("northfalsehead" + var1, BlockStateData.parse("{Name:'minecraft:" + var2 + "_bed',Properties:{facing:'north',occupied:'false',part:'head'}}"));
      var0.put("eastfalsehead" + var1, BlockStateData.parse("{Name:'minecraft:" + var2 + "_bed',Properties:{facing:'east',occupied:'false',part:'head'}}"));
      var0.put("southtruehead" + var1, BlockStateData.parse("{Name:'minecraft:" + var2 + "_bed',Properties:{facing:'south',occupied:'true',part:'head'}}"));
      var0.put("westtruehead" + var1, BlockStateData.parse("{Name:'minecraft:" + var2 + "_bed',Properties:{facing:'west',occupied:'true',part:'head'}}"));
      var0.put("northtruehead" + var1, BlockStateData.parse("{Name:'minecraft:" + var2 + "_bed',Properties:{facing:'north',occupied:'true',part:'head'}}"));
      var0.put("easttruehead" + var1, BlockStateData.parse("{Name:'minecraft:" + var2 + "_bed',Properties:{facing:'east',occupied:'true',part:'head'}}"));
   }

   private static void addBanners(Map<String, Dynamic<?>> var0, int var1, String var2) {
      for(int var3 = 0; var3 < 16; ++var3) {
         var0.put(var3 + "_" + var1, BlockStateData.parse("{Name:'minecraft:" + var2 + "_banner',Properties:{rotation:'" + var3 + "'}}"));
      }

      var0.put("north_" + var1, BlockStateData.parse("{Name:'minecraft:" + var2 + "_wall_banner',Properties:{facing:'north'}}"));
      var0.put("south_" + var1, BlockStateData.parse("{Name:'minecraft:" + var2 + "_wall_banner',Properties:{facing:'south'}}"));
      var0.put("west_" + var1, BlockStateData.parse("{Name:'minecraft:" + var2 + "_wall_banner',Properties:{facing:'west'}}"));
      var0.put("east_" + var1, BlockStateData.parse("{Name:'minecraft:" + var2 + "_wall_banner',Properties:{facing:'east'}}"));
   }

   public static String getName(Dynamic<?> var0) {
      return var0.get("Name").asString("");
   }

   public static String getProperty(Dynamic<?> var0, String var1) {
      return var0.get("Properties").get(var1).asString("");
   }

   public static int idFor(CrudeIncrementalIntIdentityHashBiMap<Dynamic<?>> var0, Dynamic<?> var1) {
      int var2 = var0.getId(var1);
      if (var2 == -1) {
         var2 = var0.add(var1);
      }

      return var2;
   }

   private Dynamic<?> fix(Dynamic<?> var1) {
      Optional var2 = var1.get("Level").result();
      return var2.isPresent() && ((Dynamic)var2.get()).get("Sections").asStreamOpt().result().isPresent()
         ? var1.set("Level", new ChunkPalettedStorageFix.UpgradeChunk((Dynamic<?>)var2.get()).write())
         : var1;
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.CHUNK);
      Type var2 = this.getOutputSchema().getType(References.CHUNK);
      return this.writeFixAndRead("ChunkPalettedStorageFix", var1, var2, this::fix);
   }

   public static int getSideMask(boolean var0, boolean var1, boolean var2, boolean var3) {
      int var4 = 0;
      if (var2) {
         if (var1) {
            var4 |= 2;
         } else if (var0) {
            var4 |= 128;
         } else {
            var4 |= 1;
         }
      } else if (var3) {
         if (var0) {
            var4 |= 32;
         } else if (var1) {
            var4 |= 8;
         } else {
            var4 |= 16;
         }
      } else if (var1) {
         var4 |= 4;
      } else if (var0) {
         var4 |= 64;
      }

      return var4;
   }

   static {
      FIX.set(2);
      FIX.set(3);
      FIX.set(110);
      FIX.set(140);
      FIX.set(144);
      FIX.set(25);
      FIX.set(86);
      FIX.set(26);
      FIX.set(176);
      FIX.set(177);
      FIX.set(175);
      FIX.set(64);
      FIX.set(71);
      FIX.set(193);
      FIX.set(194);
      FIX.set(195);
      FIX.set(196);
      FIX.set(197);
      VIRTUAL.set(54);
      VIRTUAL.set(146);
      VIRTUAL.set(25);
      VIRTUAL.set(26);
      VIRTUAL.set(51);
      VIRTUAL.set(53);
      VIRTUAL.set(67);
      VIRTUAL.set(108);
      VIRTUAL.set(109);
      VIRTUAL.set(114);
      VIRTUAL.set(128);
      VIRTUAL.set(134);
      VIRTUAL.set(135);
      VIRTUAL.set(136);
      VIRTUAL.set(156);
      VIRTUAL.set(163);
      VIRTUAL.set(164);
      VIRTUAL.set(180);
      VIRTUAL.set(203);
      VIRTUAL.set(55);
      VIRTUAL.set(85);
      VIRTUAL.set(113);
      VIRTUAL.set(188);
      VIRTUAL.set(189);
      VIRTUAL.set(190);
      VIRTUAL.set(191);
      VIRTUAL.set(192);
      VIRTUAL.set(93);
      VIRTUAL.set(94);
      VIRTUAL.set(101);
      VIRTUAL.set(102);
      VIRTUAL.set(160);
      VIRTUAL.set(106);
      VIRTUAL.set(107);
      VIRTUAL.set(183);
      VIRTUAL.set(184);
      VIRTUAL.set(185);
      VIRTUAL.set(186);
      VIRTUAL.set(187);
      VIRTUAL.set(132);
      VIRTUAL.set(139);
      VIRTUAL.set(199);
   }

   static class DataLayer {
      private static final int SIZE = 2048;
      private static final int NIBBLE_SIZE = 4;
      private final byte[] data;

      public DataLayer() {
         super();
         this.data = new byte[2048];
      }

      public DataLayer(byte[] var1) {
         super();
         this.data = var1;
         if (var1.length != 2048) {
            throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + var1.length);
         }
      }

      public int get(int var1, int var2, int var3) {
         int var4 = this.getPosition(var2 << 8 | var3 << 4 | var1);
         return this.isFirst(var2 << 8 | var3 << 4 | var1) ? this.data[var4] & 15 : this.data[var4] >> 4 & 15;
      }

      private boolean isFirst(int var1) {
         return (var1 & 1) == 0;
      }

      private int getPosition(int var1) {
         return var1 >> 1;
      }
   }

   public static enum Direction {
      DOWN(ChunkPalettedStorageFix.Direction.AxisDirection.NEGATIVE, ChunkPalettedStorageFix.Direction.Axis.Y),
      UP(ChunkPalettedStorageFix.Direction.AxisDirection.POSITIVE, ChunkPalettedStorageFix.Direction.Axis.Y),
      NORTH(ChunkPalettedStorageFix.Direction.AxisDirection.NEGATIVE, ChunkPalettedStorageFix.Direction.Axis.Z),
      SOUTH(ChunkPalettedStorageFix.Direction.AxisDirection.POSITIVE, ChunkPalettedStorageFix.Direction.Axis.Z),
      WEST(ChunkPalettedStorageFix.Direction.AxisDirection.NEGATIVE, ChunkPalettedStorageFix.Direction.Axis.X),
      EAST(ChunkPalettedStorageFix.Direction.AxisDirection.POSITIVE, ChunkPalettedStorageFix.Direction.Axis.X);

      private final ChunkPalettedStorageFix.Direction.Axis axis;
      private final ChunkPalettedStorageFix.Direction.AxisDirection axisDirection;

      private Direction(ChunkPalettedStorageFix.Direction.AxisDirection var3, ChunkPalettedStorageFix.Direction.Axis var4) {
         this.axis = var4;
         this.axisDirection = var3;
      }

      public ChunkPalettedStorageFix.Direction.AxisDirection getAxisDirection() {
         return this.axisDirection;
      }

      public ChunkPalettedStorageFix.Direction.Axis getAxis() {
         return this.axis;
      }

      public static enum Axis {
         X,
         Y,
         Z;

         private Axis() {
         }
      }

      public static enum AxisDirection {
         POSITIVE(1),
         NEGATIVE(-1);

         private final int step;

         private AxisDirection(int var3) {
            this.step = var3;
         }

         public int getStep() {
            return this.step;
         }
      }
   }

   static class Section {
      private final CrudeIncrementalIntIdentityHashBiMap<Dynamic<?>> palette = CrudeIncrementalIntIdentityHashBiMap.create(32);
      private final List<Dynamic<?>> listTag;
      private final Dynamic<?> section;
      private final boolean hasData;
      final Int2ObjectMap<IntList> toFix = new Int2ObjectLinkedOpenHashMap();
      final IntList update = new IntArrayList();
      public final int y;
      private final Set<Dynamic<?>> seen = Sets.newIdentityHashSet();
      private final int[] buffer = new int[4096];

      public Section(Dynamic<?> var1) {
         super();
         this.listTag = Lists.newArrayList();
         this.section = var1;
         this.y = var1.get("Y").asInt(0);
         this.hasData = var1.get("Blocks").result().isPresent();
      }

      public Dynamic<?> getBlock(int var1) {
         if (var1 >= 0 && var1 <= 4095) {
            Dynamic var2 = (Dynamic)this.palette.byId(this.buffer[var1]);
            return var2 == null ? ChunkPalettedStorageFix.AIR : var2;
         } else {
            return ChunkPalettedStorageFix.AIR;
         }
      }

      public void setBlock(int var1, Dynamic<?> var2) {
         if (this.seen.add(var2)) {
            this.listTag.add("%%FILTER_ME%%".equals(ChunkPalettedStorageFix.getName(var2)) ? ChunkPalettedStorageFix.AIR : var2);
         }

         this.buffer[var1] = ChunkPalettedStorageFix.idFor(this.palette, var2);
      }

      public int upgrade(int var1) {
         if (!this.hasData) {
            return var1;
         } else {
            ByteBuffer var2 = (ByteBuffer)this.section.get("Blocks").asByteBufferOpt().result().get();
            ChunkPalettedStorageFix.DataLayer var3 = this.section
               .get("Data")
               .asByteBufferOpt()
               .map(var0 -> new ChunkPalettedStorageFix.DataLayer(DataFixUtils.toArray(var0)))
               .result()
               .orElseGet(ChunkPalettedStorageFix.DataLayer::new);
            ChunkPalettedStorageFix.DataLayer var4 = this.section
               .get("Add")
               .asByteBufferOpt()
               .map(var0 -> new ChunkPalettedStorageFix.DataLayer(DataFixUtils.toArray(var0)))
               .result()
               .orElseGet(ChunkPalettedStorageFix.DataLayer::new);
            this.seen.add(ChunkPalettedStorageFix.AIR);
            ChunkPalettedStorageFix.idFor(this.palette, ChunkPalettedStorageFix.AIR);
            this.listTag.add(ChunkPalettedStorageFix.AIR);

            for(int var5 = 0; var5 < 4096; ++var5) {
               int var6 = var5 & 15;
               int var7 = var5 >> 8 & 15;
               int var8 = var5 >> 4 & 15;
               int var9 = var4.get(var6, var7, var8) << 12 | (var2.get(var5) & 255) << 4 | var3.get(var6, var7, var8);
               if (ChunkPalettedStorageFix.FIX.get(var9 >> 4)) {
                  this.addFix(var9 >> 4, var5);
               }

               if (ChunkPalettedStorageFix.VIRTUAL.get(var9 >> 4)) {
                  int var10 = ChunkPalettedStorageFix.getSideMask(var6 == 0, var6 == 15, var8 == 0, var8 == 15);
                  if (var10 == 0) {
                     this.update.add(var5);
                  } else {
                     var1 |= var10;
                  }
               }

               this.setBlock(var5, BlockStateData.getTag(var9));
            }

            return var1;
         }
      }

      private void addFix(int var1, int var2) {
         Object var3 = (IntList)this.toFix.get(var1);
         if (var3 == null) {
            var3 = new IntArrayList();
            this.toFix.put(var1, var3);
         }

         var3.add(var2);
      }

      public Dynamic<?> write() {
         Dynamic var1 = this.section;
         if (!this.hasData) {
            return var1;
         } else {
            var1 = var1.set("Palette", var1.createList(this.listTag.stream()));
            int var2 = Math.max(4, DataFixUtils.ceillog2(this.seen.size()));
            PackedBitStorage var3 = new PackedBitStorage(var2, 4096);

            for(int var4 = 0; var4 < this.buffer.length; ++var4) {
               var3.set(var4, this.buffer[var4]);
            }

            var1 = var1.set("BlockStates", var1.createLongList(Arrays.stream(var3.getRaw())));
            var1 = var1.remove("Blocks");
            var1 = var1.remove("Data");
            return var1.remove("Add");
         }
      }
   }

   static final class UpgradeChunk {
      private int sides;
      private final ChunkPalettedStorageFix.Section[] sections = new ChunkPalettedStorageFix.Section[16];
      private final Dynamic<?> level;
      private final int x;
      private final int z;
      private final Int2ObjectMap<Dynamic<?>> blockEntities = new Int2ObjectLinkedOpenHashMap(16);

      public UpgradeChunk(Dynamic<?> var1) {
         super();
         this.level = var1;
         this.x = var1.get("xPos").asInt(0) << 4;
         this.z = var1.get("zPos").asInt(0) << 4;
         var1.get("TileEntities")
            .asStreamOpt()
            .result()
            .ifPresent(
               var1x -> var1x.forEach(
                     var1xx -> {
                        int var2xx = var1xx.get("x").asInt(0) - this.x & 15;
                        int var3 = var1xx.get("y").asInt(0);
                        int var4 = var1xx.get("z").asInt(0) - this.z & 15;
                        int var5 = var3 << 8 | var4 << 4 | var2xx;
                        if (this.blockEntities.put(var5, var1xx) != null) {
                           ChunkPalettedStorageFix.LOGGER
                              .warn(
                                 "In chunk: {}x{} found a duplicate block entity at position: [{}, {}, {}]", new Object[]{this.x, this.z, var2xx, var3, var4}
                              );
                        }
                     }
                  )
            );
         boolean var2 = var1.get("convertedFromAlphaFormat").asBoolean(false);
         var1.get("Sections").asStreamOpt().result().ifPresent(var1x -> var1x.forEach(var1xx -> {
               ChunkPalettedStorageFix.Section var2xx = new ChunkPalettedStorageFix.Section(var1xx);
               this.sides = var2xx.upgrade(this.sides);
               this.sections[var2xx.y] = var2xx;
            }));

         for(ChunkPalettedStorageFix.Section var6 : this.sections) {
            if (var6 != null) {
               ObjectIterator var7 = var6.toFix.entrySet().iterator();

               while(var7.hasNext()) {
                  java.util.Map.Entry var8 = (java.util.Map.Entry)var7.next();
                  int var9 = var6.y << 12;
                  switch(var8.getKey()) {
                     case 2:
                        IntListIterator var30 = ((IntList)var8.getValue()).iterator();

                        while(var30.hasNext()) {
                           int var50 = var30.next();
                           var50 |= var9;
                           Dynamic var61 = this.getBlock(var50);
                           if ("minecraft:grass_block".equals(ChunkPalettedStorageFix.getName(var61))) {
                              String var71 = ChunkPalettedStorageFix.getName(this.getBlock(relative(var50, ChunkPalettedStorageFix.Direction.UP)));
                              if ("minecraft:snow".equals(var71) || "minecraft:snow_layer".equals(var71)) {
                                 this.setBlock(var50, ChunkPalettedStorageFix.SNOWY_GRASS);
                              }
                           }
                        }
                        break;
                     case 3:
                        IntListIterator var29 = ((IntList)var8.getValue()).iterator();

                        while(var29.hasNext()) {
                           int var48 = var29.next();
                           var48 |= var9;
                           Dynamic var60 = this.getBlock(var48);
                           if ("minecraft:podzol".equals(ChunkPalettedStorageFix.getName(var60))) {
                              String var70 = ChunkPalettedStorageFix.getName(this.getBlock(relative(var48, ChunkPalettedStorageFix.Direction.UP)));
                              if ("minecraft:snow".equals(var70) || "minecraft:snow_layer".equals(var70)) {
                                 this.setBlock(var48, ChunkPalettedStorageFix.SNOWY_PODZOL);
                              }
                           }
                        }
                        break;
                     case 25:
                        IntListIterator var28 = ((IntList)var8.getValue()).iterator();

                        while(var28.hasNext()) {
                           int var46 = var28.next();
                           var46 |= var9;
                           Dynamic var59 = this.removeBlockEntity(var46);
                           if (var59 != null) {
                              String var69 = Boolean.toString(var59.get("powered").asBoolean(false))
                                 + (byte)Math.min(Math.max(var59.get("note").asInt(0), 0), 24);
                              this.setBlock(
                                 var46,
                                 (Dynamic<?>)ChunkPalettedStorageFix.NOTE_BLOCK_MAP
                                    .getOrDefault(var69, (Dynamic)ChunkPalettedStorageFix.NOTE_BLOCK_MAP.get("false0"))
                              );
                           }
                        }
                        break;
                     case 26:
                        IntListIterator var27 = ((IntList)var8.getValue()).iterator();

                        while(var27.hasNext()) {
                           int var44 = var27.next();
                           var44 |= var9;
                           Dynamic var58 = this.getBlockEntity(var44);
                           Dynamic var68 = this.getBlock(var44);
                           if (var58 != null) {
                              int var75 = var58.get("color").asInt(0);
                              if (var75 != 14 && var75 >= 0 && var75 < 16) {
                                 String var78 = ChunkPalettedStorageFix.getProperty(var68, "facing")
                                    + ChunkPalettedStorageFix.getProperty(var68, "occupied")
                                    + ChunkPalettedStorageFix.getProperty(var68, "part")
                                    + var75;
                                 if (ChunkPalettedStorageFix.BED_BLOCK_MAP.containsKey(var78)) {
                                    this.setBlock(var44, (Dynamic<?>)ChunkPalettedStorageFix.BED_BLOCK_MAP.get(var78));
                                 }
                              }
                           }
                        }
                        break;
                     case 64:
                     case 71:
                     case 193:
                     case 194:
                     case 195:
                     case 196:
                     case 197:
                        IntListIterator var26 = ((IntList)var8.getValue()).iterator();

                        while(var26.hasNext()) {
                           int var42 = var26.next();
                           var42 |= var9;
                           Dynamic var57 = this.getBlock(var42);
                           if (ChunkPalettedStorageFix.getName(var57).endsWith("_door")) {
                              Dynamic var67 = this.getBlock(var42);
                              if ("lower".equals(ChunkPalettedStorageFix.getProperty(var67, "half"))) {
                                 int var74 = relative(var42, ChunkPalettedStorageFix.Direction.UP);
                                 Dynamic var77 = this.getBlock(var74);
                                 String var16 = ChunkPalettedStorageFix.getName(var67);
                                 if (var16.equals(ChunkPalettedStorageFix.getName(var77))) {
                                    String var17 = ChunkPalettedStorageFix.getProperty(var67, "facing");
                                    String var18 = ChunkPalettedStorageFix.getProperty(var67, "open");
                                    String var19 = var2 ? "left" : ChunkPalettedStorageFix.getProperty(var77, "hinge");
                                    String var20 = var2 ? "false" : ChunkPalettedStorageFix.getProperty(var77, "powered");
                                    this.setBlock(var42, (Dynamic<?>)ChunkPalettedStorageFix.DOOR_MAP.get(var16 + var17 + "lower" + var19 + var18 + var20));
                                    this.setBlock(var74, (Dynamic<?>)ChunkPalettedStorageFix.DOOR_MAP.get(var16 + var17 + "upper" + var19 + var18 + var20));
                                 }
                              }
                           }
                        }
                        break;
                     case 86:
                        IntListIterator var25 = ((IntList)var8.getValue()).iterator();

                        while(var25.hasNext()) {
                           int var40 = var25.next();
                           var40 |= var9;
                           Dynamic var56 = this.getBlock(var40);
                           if ("minecraft:carved_pumpkin".equals(ChunkPalettedStorageFix.getName(var56))) {
                              String var66 = ChunkPalettedStorageFix.getName(this.getBlock(relative(var40, ChunkPalettedStorageFix.Direction.DOWN)));
                              if ("minecraft:grass_block".equals(var66) || "minecraft:dirt".equals(var66)) {
                                 this.setBlock(var40, ChunkPalettedStorageFix.PUMPKIN);
                              }
                           }
                        }
                        break;
                     case 110:
                        IntListIterator var24 = ((IntList)var8.getValue()).iterator();

                        while(var24.hasNext()) {
                           int var38 = var24.next();
                           var38 |= var9;
                           Dynamic var55 = this.getBlock(var38);
                           if ("minecraft:mycelium".equals(ChunkPalettedStorageFix.getName(var55))) {
                              String var65 = ChunkPalettedStorageFix.getName(this.getBlock(relative(var38, ChunkPalettedStorageFix.Direction.UP)));
                              if ("minecraft:snow".equals(var65) || "minecraft:snow_layer".equals(var65)) {
                                 this.setBlock(var38, ChunkPalettedStorageFix.SNOWY_MYCELIUM);
                              }
                           }
                        }
                        break;
                     case 140:
                        IntListIterator var23 = ((IntList)var8.getValue()).iterator();

                        while(var23.hasNext()) {
                           int var36 = var23.next();
                           var36 |= var9;
                           Dynamic var54 = this.removeBlockEntity(var36);
                           if (var54 != null) {
                              String var64 = var54.get("Item").asString("") + var54.get("Data").asInt(0);
                              this.setBlock(
                                 var36,
                                 (Dynamic<?>)ChunkPalettedStorageFix.FLOWER_POT_MAP
                                    .getOrDefault(var64, (Dynamic)ChunkPalettedStorageFix.FLOWER_POT_MAP.get("minecraft:air0"))
                              );
                           }
                        }
                        break;
                     case 144:
                        IntListIterator var22 = ((IntList)var8.getValue()).iterator();

                        while(var22.hasNext()) {
                           int var34 = var22.next();
                           var34 |= var9;
                           Dynamic var53 = this.getBlockEntity(var34);
                           if (var53 != null) {
                              String var63 = String.valueOf(var53.get("SkullType").asInt(0));
                              String var73 = ChunkPalettedStorageFix.getProperty(this.getBlock(var34), "facing");
                              String var76;
                              if (!"up".equals(var73) && !"down".equals(var73)) {
                                 var76 = var63 + var73;
                              } else {
                                 var76 = var63 + var53.get("Rot").asInt(0);
                              }

                              var53.remove("SkullType");
                              var53.remove("facing");
                              var53.remove("Rot");
                              this.setBlock(
                                 var34,
                                 (Dynamic<?>)ChunkPalettedStorageFix.SKULL_MAP.getOrDefault(var76, (Dynamic)ChunkPalettedStorageFix.SKULL_MAP.get("0north"))
                              );
                           }
                        }
                        break;
                     case 175:
                        IntListIterator var21 = ((IntList)var8.getValue()).iterator();

                        while(var21.hasNext()) {
                           int var32 = var21.next();
                           var32 |= var9;
                           Dynamic var52 = this.getBlock(var32);
                           if ("upper".equals(ChunkPalettedStorageFix.getProperty(var52, "half"))) {
                              Dynamic var62 = this.getBlock(relative(var32, ChunkPalettedStorageFix.Direction.DOWN));
                              String var72 = ChunkPalettedStorageFix.getName(var62);
                              if ("minecraft:sunflower".equals(var72)) {
                                 this.setBlock(var32, ChunkPalettedStorageFix.UPPER_SUNFLOWER);
                              } else if ("minecraft:lilac".equals(var72)) {
                                 this.setBlock(var32, ChunkPalettedStorageFix.UPPER_LILAC);
                              } else if ("minecraft:tall_grass".equals(var72)) {
                                 this.setBlock(var32, ChunkPalettedStorageFix.UPPER_TALL_GRASS);
                              } else if ("minecraft:large_fern".equals(var72)) {
                                 this.setBlock(var32, ChunkPalettedStorageFix.UPPER_LARGE_FERN);
                              } else if ("minecraft:rose_bush".equals(var72)) {
                                 this.setBlock(var32, ChunkPalettedStorageFix.UPPER_ROSE_BUSH);
                              } else if ("minecraft:peony".equals(var72)) {
                                 this.setBlock(var32, ChunkPalettedStorageFix.UPPER_PEONY);
                              }
                           }
                        }
                        break;
                     case 176:
                     case 177:
                        IntListIterator var10 = ((IntList)var8.getValue()).iterator();

                        while(var10.hasNext()) {
                           int var11 = var10.next();
                           var11 |= var9;
                           Dynamic var12 = this.getBlockEntity(var11);
                           Dynamic var13 = this.getBlock(var11);
                           if (var12 != null) {
                              int var14 = var12.get("Base").asInt(0);
                              if (var14 != 15 && var14 >= 0 && var14 < 16) {
                                 String var15 = ChunkPalettedStorageFix.getProperty(var13, var8.getKey() == 176 ? "rotation" : "facing") + "_" + var14;
                                 if (ChunkPalettedStorageFix.BANNER_BLOCK_MAP.containsKey(var15)) {
                                    this.setBlock(var11, (Dynamic<?>)ChunkPalettedStorageFix.BANNER_BLOCK_MAP.get(var15));
                                 }
                              }
                           }
                        }
                  }
               }
            }
         }
      }

      @Nullable
      private Dynamic<?> getBlockEntity(int var1) {
         return (Dynamic<?>)this.blockEntities.get(var1);
      }

      @Nullable
      private Dynamic<?> removeBlockEntity(int var1) {
         return (Dynamic<?>)this.blockEntities.remove(var1);
      }

      public static int relative(int var0, ChunkPalettedStorageFix.Direction var1) {
         switch(var1.getAxis()) {
            case X:
               int var2 = (var0 & 15) + var1.getAxisDirection().getStep();
               return var2 >= 0 && var2 <= 15 ? var0 & -16 | var2 : -1;
            case Y:
               int var3 = (var0 >> 8) + var1.getAxisDirection().getStep();
               return var3 >= 0 && var3 <= 255 ? var0 & 0xFF | var3 << 8 : -1;
            case Z:
               int var4 = (var0 >> 4 & 15) + var1.getAxisDirection().getStep();
               return var4 >= 0 && var4 <= 15 ? var0 & -241 | var4 << 4 : -1;
            default:
               return -1;
         }
      }

      private void setBlock(int var1, Dynamic<?> var2) {
         if (var1 >= 0 && var1 <= 65535) {
            ChunkPalettedStorageFix.Section var3 = this.getSection(var1);
            if (var3 != null) {
               var3.setBlock(var1 & 4095, var2);
            }
         }
      }

      @Nullable
      private ChunkPalettedStorageFix.Section getSection(int var1) {
         int var2 = var1 >> 12;
         return var2 < this.sections.length ? this.sections[var2] : null;
      }

      public Dynamic<?> getBlock(int var1) {
         if (var1 >= 0 && var1 <= 65535) {
            ChunkPalettedStorageFix.Section var2 = this.getSection(var1);
            return var2 == null ? ChunkPalettedStorageFix.AIR : var2.getBlock(var1 & 4095);
         } else {
            return ChunkPalettedStorageFix.AIR;
         }
      }

      public Dynamic<?> write() {
         Dynamic var1 = this.level;
         if (this.blockEntities.isEmpty()) {
            var1 = var1.remove("TileEntities");
         } else {
            var1 = var1.set("TileEntities", var1.createList(this.blockEntities.values().stream()));
         }

         Dynamic var2 = var1.emptyMap();
         ArrayList var3 = Lists.newArrayList();

         for(ChunkPalettedStorageFix.Section var7 : this.sections) {
            if (var7 != null) {
               var3.add(var7.write());
               var2 = var2.set(String.valueOf(var7.y), var2.createIntList(Arrays.stream(var7.update.toIntArray())));
            }
         }

         Dynamic var9 = var1.emptyMap();
         var9 = var9.set("Sides", var9.createByte((byte)this.sides));
         var9 = var9.set("Indices", var2);
         return var1.set("UpgradeData", var9).set("Sections", var9.createList(var3.stream()));
      }
   }
}
