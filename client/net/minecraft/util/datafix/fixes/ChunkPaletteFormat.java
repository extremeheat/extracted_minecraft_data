package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.BitArray;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.datafix.TypeReferences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkPaletteFormat extends DataFix {
   private static final Logger field_199145_a = LogManager.getLogger();
   private static final BitSet field_199146_b = new BitSet(256);
   private static final BitSet field_199147_c = new BitSet(256);
   private static final Dynamic<?> field_199148_d = BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:pumpkin'}");
   private static final Dynamic<?> field_199149_e = BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:podzol',Properties:{snowy:'true'}}");
   private static final Dynamic<?> field_199150_f = BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:grass_block',Properties:{snowy:'true'}}");
   private static final Dynamic<?> field_199151_g = BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:mycelium',Properties:{snowy:'true'}}");
   private static final Dynamic<?> field_199152_h = BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:sunflower',Properties:{half:'upper'}}");
   private static final Dynamic<?> field_199153_i = BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:lilac',Properties:{half:'upper'}}");
   private static final Dynamic<?> field_199154_j = BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:tall_grass',Properties:{half:'upper'}}");
   private static final Dynamic<?> field_199155_k = BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:large_fern',Properties:{half:'upper'}}");
   private static final Dynamic<?> field_199156_l = BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:rose_bush',Properties:{half:'upper'}}");
   private static final Dynamic<?> field_199157_m = BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:peony',Properties:{half:'upper'}}");
   private static final Map<String, Dynamic<?>> field_199158_n = (Map)DataFixUtils.make(Maps.newHashMap(), (var0) -> {
      var0.put("minecraft:air0", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:flower_pot'}"));
      var0.put("minecraft:red_flower0", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:potted_poppy'}"));
      var0.put("minecraft:red_flower1", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:potted_blue_orchid'}"));
      var0.put("minecraft:red_flower2", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:potted_allium'}"));
      var0.put("minecraft:red_flower3", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:potted_azure_bluet'}"));
      var0.put("minecraft:red_flower4", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:potted_red_tulip'}"));
      var0.put("minecraft:red_flower5", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:potted_orange_tulip'}"));
      var0.put("minecraft:red_flower6", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:potted_white_tulip'}"));
      var0.put("minecraft:red_flower7", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:potted_pink_tulip'}"));
      var0.put("minecraft:red_flower8", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:potted_oxeye_daisy'}"));
      var0.put("minecraft:yellow_flower0", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:potted_dandelion'}"));
      var0.put("minecraft:sapling0", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:potted_oak_sapling'}"));
      var0.put("minecraft:sapling1", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:potted_spruce_sapling'}"));
      var0.put("minecraft:sapling2", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:potted_birch_sapling'}"));
      var0.put("minecraft:sapling3", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:potted_jungle_sapling'}"));
      var0.put("minecraft:sapling4", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:potted_acacia_sapling'}"));
      var0.put("minecraft:sapling5", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:potted_dark_oak_sapling'}"));
      var0.put("minecraft:red_mushroom0", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:potted_red_mushroom'}"));
      var0.put("minecraft:brown_mushroom0", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:potted_brown_mushroom'}"));
      var0.put("minecraft:deadbush0", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:potted_dead_bush'}"));
      var0.put("minecraft:tallgrass2", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:potted_fern'}"));
      var0.put("minecraft:cactus0", BlockStateFlatteningMap.func_210049_b(2240));
   });
   private static final Map<String, Dynamic<?>> field_199159_o = (Map)DataFixUtils.make(Maps.newHashMap(), (var0) -> {
      func_209300_a(var0, 0, "skeleton", "skull");
      func_209300_a(var0, 1, "wither_skeleton", "skull");
      func_209300_a(var0, 2, "zombie", "head");
      func_209300_a(var0, 3, "player", "head");
      func_209300_a(var0, 4, "creeper", "head");
      func_209300_a(var0, 5, "dragon", "head");
   });
   private static final Map<String, Dynamic<?>> field_199160_p = (Map)DataFixUtils.make(Maps.newHashMap(), (var0) -> {
      func_209301_a(var0, "oak_door", 1024);
      func_209301_a(var0, "iron_door", 1136);
      func_209301_a(var0, "spruce_door", 3088);
      func_209301_a(var0, "birch_door", 3104);
      func_209301_a(var0, "jungle_door", 3120);
      func_209301_a(var0, "acacia_door", 3136);
      func_209301_a(var0, "dark_oak_door", 3152);
   });
   private static final Map<String, Dynamic<?>> field_199161_q = (Map)DataFixUtils.make(Maps.newHashMap(), (var0) -> {
      for(int var1 = 0; var1 < 26; ++var1) {
         var0.put("true" + var1, BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:note_block',Properties:{powered:'true',note:'" + var1 + "'}}"));
         var0.put("false" + var1, BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:note_block',Properties:{powered:'false',note:'" + var1 + "'}}"));
      }

   });
   private static final Int2ObjectMap<String> field_199162_r = (Int2ObjectMap)DataFixUtils.make(new Int2ObjectOpenHashMap(), (var0) -> {
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
   private static final Map<String, Dynamic<?>> field_199163_s = (Map)DataFixUtils.make(Maps.newHashMap(), (var0) -> {
      ObjectIterator var1 = field_199162_r.int2ObjectEntrySet().iterator();

      while(var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         if (!Objects.equals(var2.getValue(), "red")) {
            func_209307_a(var0, var2.getIntKey(), (String)var2.getValue());
         }
      }

   });
   private static final Map<String, Dynamic<?>> field_199164_t = (Map)DataFixUtils.make(Maps.newHashMap(), (var0) -> {
      ObjectIterator var1 = field_199162_r.int2ObjectEntrySet().iterator();

      while(var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         if (!Objects.equals(var2.getValue(), "white")) {
            func_209297_b(var0, 15 - var2.getIntKey(), (String)var2.getValue());
         }
      }

   });
   private static final Dynamic<?> field_199165_u;

   public ChunkPaletteFormat(Schema var1, boolean var2) {
      super(var1, var2);
   }

   private static void func_209300_a(Map<String, Dynamic<?>> var0, int var1, String var2, String var3) {
      var0.put(var1 + "north", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var2 + "_wall_" + var3 + "',Properties:{facing:'north'}}"));
      var0.put(var1 + "east", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var2 + "_wall_" + var3 + "',Properties:{facing:'east'}}"));
      var0.put(var1 + "south", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var2 + "_wall_" + var3 + "',Properties:{facing:'south'}}"));
      var0.put(var1 + "west", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var2 + "_wall_" + var3 + "',Properties:{facing:'west'}}"));

      for(int var4 = 0; var4 < 16; ++var4) {
         var0.put(var1 + "" + var4, BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var2 + "_" + var3 + "',Properties:{rotation:'" + var4 + "'}}"));
      }

   }

   private static void func_209301_a(Map<String, Dynamic<?>> var0, String var1, int var2) {
      var0.put("minecraft:" + var1 + "eastlowerleftfalsefalse", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
      var0.put("minecraft:" + var1 + "eastlowerleftfalsetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "eastlowerlefttruefalse", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
      var0.put("minecraft:" + var1 + "eastlowerlefttruetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "eastlowerrightfalsefalse", BlockStateFlatteningMap.func_210049_b(var2));
      var0.put("minecraft:" + var1 + "eastlowerrightfalsetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "eastlowerrighttruefalse", BlockStateFlatteningMap.func_210049_b(var2 + 4));
      var0.put("minecraft:" + var1 + "eastlowerrighttruetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "eastupperleftfalsefalse", BlockStateFlatteningMap.func_210049_b(var2 + 8));
      var0.put("minecraft:" + var1 + "eastupperleftfalsetrue", BlockStateFlatteningMap.func_210049_b(var2 + 10));
      var0.put("minecraft:" + var1 + "eastupperlefttruefalse", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
      var0.put("minecraft:" + var1 + "eastupperlefttruetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "eastupperrightfalsefalse", BlockStateFlatteningMap.func_210049_b(var2 + 9));
      var0.put("minecraft:" + var1 + "eastupperrightfalsetrue", BlockStateFlatteningMap.func_210049_b(var2 + 11));
      var0.put("minecraft:" + var1 + "eastupperrighttruefalse", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
      var0.put("minecraft:" + var1 + "eastupperrighttruetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "northlowerleftfalsefalse", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
      var0.put("minecraft:" + var1 + "northlowerleftfalsetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "northlowerlefttruefalse", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
      var0.put("minecraft:" + var1 + "northlowerlefttruetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "northlowerrightfalsefalse", BlockStateFlatteningMap.func_210049_b(var2 + 3));
      var0.put("minecraft:" + var1 + "northlowerrightfalsetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "northlowerrighttruefalse", BlockStateFlatteningMap.func_210049_b(var2 + 7));
      var0.put("minecraft:" + var1 + "northlowerrighttruetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "northupperleftfalsefalse", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
      var0.put("minecraft:" + var1 + "northupperleftfalsetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "northupperlefttruefalse", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
      var0.put("minecraft:" + var1 + "northupperlefttruetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "northupperrightfalsefalse", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
      var0.put("minecraft:" + var1 + "northupperrightfalsetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "northupperrighttruefalse", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
      var0.put("minecraft:" + var1 + "northupperrighttruetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "southlowerleftfalsefalse", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
      var0.put("minecraft:" + var1 + "southlowerleftfalsetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "southlowerlefttruefalse", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
      var0.put("minecraft:" + var1 + "southlowerlefttruetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "southlowerrightfalsefalse", BlockStateFlatteningMap.func_210049_b(var2 + 1));
      var0.put("minecraft:" + var1 + "southlowerrightfalsetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "southlowerrighttruefalse", BlockStateFlatteningMap.func_210049_b(var2 + 5));
      var0.put("minecraft:" + var1 + "southlowerrighttruetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "southupperleftfalsefalse", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
      var0.put("minecraft:" + var1 + "southupperleftfalsetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "southupperlefttruefalse", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
      var0.put("minecraft:" + var1 + "southupperlefttruetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "southupperrightfalsefalse", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
      var0.put("minecraft:" + var1 + "southupperrightfalsetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "southupperrighttruefalse", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
      var0.put("minecraft:" + var1 + "southupperrighttruetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "westlowerleftfalsefalse", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
      var0.put("minecraft:" + var1 + "westlowerleftfalsetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "westlowerlefttruefalse", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
      var0.put("minecraft:" + var1 + "westlowerlefttruetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "westlowerrightfalsefalse", BlockStateFlatteningMap.func_210049_b(var2 + 2));
      var0.put("minecraft:" + var1 + "westlowerrightfalsetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "westlowerrighttruefalse", BlockStateFlatteningMap.func_210049_b(var2 + 6));
      var0.put("minecraft:" + var1 + "westlowerrighttruetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "westupperleftfalsefalse", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
      var0.put("minecraft:" + var1 + "westupperleftfalsetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "westupperlefttruefalse", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
      var0.put("minecraft:" + var1 + "westupperlefttruetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "westupperrightfalsefalse", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
      var0.put("minecraft:" + var1 + "westupperrightfalsetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
      var0.put("minecraft:" + var1 + "westupperrighttruefalse", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
      var0.put("minecraft:" + var1 + "westupperrighttruetrue", BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var1 + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
   }

   private static void func_209307_a(Map<String, Dynamic<?>> var0, int var1, String var2) {
      var0.put("southfalsefoot" + var1, BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var2 + "_bed',Properties:{facing:'south',occupied:'false',part:'foot'}}"));
      var0.put("westfalsefoot" + var1, BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var2 + "_bed',Properties:{facing:'west',occupied:'false',part:'foot'}}"));
      var0.put("northfalsefoot" + var1, BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var2 + "_bed',Properties:{facing:'north',occupied:'false',part:'foot'}}"));
      var0.put("eastfalsefoot" + var1, BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var2 + "_bed',Properties:{facing:'east',occupied:'false',part:'foot'}}"));
      var0.put("southfalsehead" + var1, BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var2 + "_bed',Properties:{facing:'south',occupied:'false',part:'head'}}"));
      var0.put("westfalsehead" + var1, BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var2 + "_bed',Properties:{facing:'west',occupied:'false',part:'head'}}"));
      var0.put("northfalsehead" + var1, BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var2 + "_bed',Properties:{facing:'north',occupied:'false',part:'head'}}"));
      var0.put("eastfalsehead" + var1, BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var2 + "_bed',Properties:{facing:'east',occupied:'false',part:'head'}}"));
      var0.put("southtruehead" + var1, BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var2 + "_bed',Properties:{facing:'south',occupied:'true',part:'head'}}"));
      var0.put("westtruehead" + var1, BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var2 + "_bed',Properties:{facing:'west',occupied:'true',part:'head'}}"));
      var0.put("northtruehead" + var1, BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var2 + "_bed',Properties:{facing:'north',occupied:'true',part:'head'}}"));
      var0.put("easttruehead" + var1, BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var2 + "_bed',Properties:{facing:'east',occupied:'true',part:'head'}}"));
   }

   private static void func_209297_b(Map<String, Dynamic<?>> var0, int var1, String var2) {
      for(int var3 = 0; var3 < 16; ++var3) {
         var0.put("" + var3 + "_" + var1, BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var2 + "_banner',Properties:{rotation:'" + var3 + "'}}"));
      }

      var0.put("north_" + var1, BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var2 + "_wall_banner',Properties:{facing:'north'}}"));
      var0.put("south_" + var1, BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var2 + "_wall_banner',Properties:{facing:'south'}}"));
      var0.put("west_" + var1, BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var2 + "_wall_banner',Properties:{facing:'west'}}"));
      var0.put("east_" + var1, BlockStateFlatteningMap.func_210048_b("{Name:'minecraft:" + var2 + "_wall_banner',Properties:{facing:'east'}}"));
   }

   public static String func_209726_a(Dynamic<?> var0) {
      return var0.getString("Name");
   }

   public static String func_209719_a(Dynamic<?> var0, String var1) {
      return (String)var0.get("Properties").map((var1x) -> {
         return var1x.getString(var1);
      }).orElse("");
   }

   public static int func_209724_a(IntIdentityHashBiMap<Dynamic<?>> var0, Dynamic<?> var1) {
      int var2 = var0.func_186815_a(var1);
      if (var2 == -1) {
         var2 = var0.func_186808_c(var1);
      }

      return var2;
   }

   private Dynamic<?> func_209712_b(Dynamic<?> var1) {
      Optional var2 = var1.get("Level");
      return var2.isPresent() && ((Dynamic)var2.get()).get("Sections").flatMap(Dynamic::getStream).isPresent() ? var1.set("Level", (new ChunkPaletteFormat.UpgradeChunk((Dynamic)var2.get())).func_210058_a()) : var1;
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(TypeReferences.field_211287_c);
      Type var2 = this.getOutputSchema().getType(TypeReferences.field_211287_c);
      return this.writeFixAndRead("ChunkPalettedStorageFix", var1, var2, this::func_209712_b);
   }

   public static int func_210957_a(boolean var0, boolean var1, boolean var2, boolean var3) {
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
      field_199147_c.set(2);
      field_199147_c.set(3);
      field_199147_c.set(110);
      field_199147_c.set(140);
      field_199147_c.set(144);
      field_199147_c.set(25);
      field_199147_c.set(86);
      field_199147_c.set(26);
      field_199147_c.set(176);
      field_199147_c.set(177);
      field_199147_c.set(175);
      field_199147_c.set(64);
      field_199147_c.set(71);
      field_199147_c.set(193);
      field_199147_c.set(194);
      field_199147_c.set(195);
      field_199147_c.set(196);
      field_199147_c.set(197);
      field_199146_b.set(54);
      field_199146_b.set(146);
      field_199146_b.set(25);
      field_199146_b.set(26);
      field_199146_b.set(51);
      field_199146_b.set(53);
      field_199146_b.set(67);
      field_199146_b.set(108);
      field_199146_b.set(109);
      field_199146_b.set(114);
      field_199146_b.set(128);
      field_199146_b.set(134);
      field_199146_b.set(135);
      field_199146_b.set(136);
      field_199146_b.set(156);
      field_199146_b.set(163);
      field_199146_b.set(164);
      field_199146_b.set(180);
      field_199146_b.set(203);
      field_199146_b.set(55);
      field_199146_b.set(85);
      field_199146_b.set(113);
      field_199146_b.set(188);
      field_199146_b.set(189);
      field_199146_b.set(190);
      field_199146_b.set(191);
      field_199146_b.set(192);
      field_199146_b.set(93);
      field_199146_b.set(94);
      field_199146_b.set(101);
      field_199146_b.set(102);
      field_199146_b.set(160);
      field_199146_b.set(106);
      field_199146_b.set(107);
      field_199146_b.set(183);
      field_199146_b.set(184);
      field_199146_b.set(185);
      field_199146_b.set(186);
      field_199146_b.set(187);
      field_199146_b.set(132);
      field_199146_b.set(139);
      field_199146_b.set(199);
      field_199165_u = BlockStateFlatteningMap.func_210049_b(0);
   }

   public static enum Direction {
      DOWN(ChunkPaletteFormat.Direction.Offset.NEGATIVE, ChunkPaletteFormat.Direction.Axis.Y),
      UP(ChunkPaletteFormat.Direction.Offset.POSITIVE, ChunkPaletteFormat.Direction.Axis.Y),
      NORTH(ChunkPaletteFormat.Direction.Offset.NEGATIVE, ChunkPaletteFormat.Direction.Axis.Z),
      SOUTH(ChunkPaletteFormat.Direction.Offset.POSITIVE, ChunkPaletteFormat.Direction.Axis.Z),
      WEST(ChunkPaletteFormat.Direction.Offset.NEGATIVE, ChunkPaletteFormat.Direction.Axis.X),
      EAST(ChunkPaletteFormat.Direction.Offset.POSITIVE, ChunkPaletteFormat.Direction.Axis.X);

      private final ChunkPaletteFormat.Direction.Axis field_210941_g;
      private final ChunkPaletteFormat.Direction.Offset field_210942_h;

      private Direction(ChunkPaletteFormat.Direction.Offset var3, ChunkPaletteFormat.Direction.Axis var4) {
         this.field_210941_g = var4;
         this.field_210942_h = var3;
      }

      public ChunkPaletteFormat.Direction.Offset func_210939_a() {
         return this.field_210942_h;
      }

      public ChunkPaletteFormat.Direction.Axis func_210940_b() {
         return this.field_210941_g;
      }

      public static enum Offset {
         POSITIVE(1),
         NEGATIVE(-1);

         private final int field_210938_c;

         private Offset(int var3) {
            this.field_210938_c = var3;
         }

         public int func_210937_a() {
            return this.field_210938_c;
         }
      }

      public static enum Axis {
         X,
         Y,
         Z;

         private Axis() {
         }
      }
   }

   static class NibbleArray {
      private final byte[] field_210935_a;

      public NibbleArray() {
         super();
         this.field_210935_a = new byte[2048];
      }

      public NibbleArray(byte[] var1) {
         super();
         this.field_210935_a = var1;
         if (var1.length != 2048) {
            throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + var1.length);
         }
      }

      public int func_210932_a(int var1, int var2, int var3) {
         int var4 = this.func_210934_b(var2 << 8 | var3 << 4 | var1);
         return this.func_210933_a(var2 << 8 | var3 << 4 | var1) ? this.field_210935_a[var4] & 15 : this.field_210935_a[var4] >> 4 & 15;
      }

      private boolean func_210933_a(int var1) {
         return (var1 & 1) == 0;
      }

      private int func_210934_b(int var1) {
         return var1 >> 1;
      }
   }

   static final class UpgradeChunk {
      private int field_199227_a;
      private final ChunkPaletteFormat.Section[] field_199228_b = new ChunkPaletteFormat.Section[16];
      private final Dynamic<?> field_199229_c;
      private final int field_199230_d;
      private final int field_199231_e;
      private final Int2ObjectMap<Dynamic<?>> field_199232_f = new Int2ObjectLinkedOpenHashMap(16);

      public UpgradeChunk(Dynamic<?> var1) {
         super();
         this.field_199229_c = var1;
         this.field_199230_d = var1.getInt("xPos") << 4;
         this.field_199231_e = var1.getInt("zPos") << 4;
         var1.get("TileEntities").flatMap(Dynamic::getStream).ifPresent((var1x) -> {
            var1x.forEach((var1) -> {
               int var2 = var1.getInt("x") - this.field_199230_d & 15;
               int var3 = var1.getInt("y");
               int var4 = var1.getInt("z") - this.field_199231_e & 15;
               int var5 = var3 << 8 | var4 << 4 | var2;
               if (this.field_199232_f.put(var5, var1) != null) {
                  ChunkPaletteFormat.field_199145_a.warn("In chunk: {}x{} found a duplicate block entity at position: [{}, {}, {}]", this.field_199230_d, this.field_199231_e, var2, var3, var4);
               }

            });
         });
         boolean var2 = var1.getBoolean("convertedFromAlphaFormat");
         var1.get("Sections").flatMap(Dynamic::getStream).ifPresent((var1x) -> {
            var1x.forEach((var1) -> {
               ChunkPaletteFormat.Section var2 = new ChunkPaletteFormat.Section(var1);
               this.field_199227_a = var2.func_199207_b(this.field_199227_a);
               this.field_199228_b[var2.field_199212_c] = var2;
            });
         });
         ChunkPaletteFormat.Section[] var3 = this.field_199228_b;
         int var4 = var3.length;

         label261:
         for(int var5 = 0; var5 < var4; ++var5) {
            ChunkPaletteFormat.Section var6 = var3[var5];
            if (var6 != null) {
               ObjectIterator var7 = var6.field_199215_f.entrySet().iterator();

               while(true) {
                  label251:
                  while(true) {
                     if (!var7.hasNext()) {
                        continue label261;
                     }

                     java.util.Map.Entry var8 = (java.util.Map.Entry)var7.next();
                     int var9 = var6.field_199212_c << 12;
                     IntListIterator var10;
                     int var11;
                     Dynamic var12;
                     Dynamic var13;
                     int var14;
                     String var15;
                     String var21;
                     String var22;
                     switch((Integer)var8.getKey()) {
                     case 2:
                        var10 = ((IntList)var8.getValue()).iterator();

                        while(true) {
                           do {
                              do {
                                 if (!var10.hasNext()) {
                                    continue label251;
                                 }

                                 var11 = (Integer)var10.next();
                                 var11 |= var9;
                                 var12 = this.func_210064_a(var11);
                              } while(!"minecraft:grass_block".equals(ChunkPaletteFormat.func_209726_a(var12)));

                              var21 = ChunkPaletteFormat.func_209726_a(this.func_210064_a(func_199223_a(var11, ChunkPaletteFormat.Direction.UP)));
                           } while(!"minecraft:snow".equals(var21) && !"minecraft:snow_layer".equals(var21));

                           this.func_210060_a(var11, ChunkPaletteFormat.field_199150_f);
                        }
                     case 3:
                        var10 = ((IntList)var8.getValue()).iterator();

                        while(true) {
                           do {
                              do {
                                 if (!var10.hasNext()) {
                                    continue label251;
                                 }

                                 var11 = (Integer)var10.next();
                                 var11 |= var9;
                                 var12 = this.func_210064_a(var11);
                              } while(!"minecraft:podzol".equals(ChunkPaletteFormat.func_209726_a(var12)));

                              var21 = ChunkPaletteFormat.func_209726_a(this.func_210064_a(func_199223_a(var11, ChunkPaletteFormat.Direction.UP)));
                           } while(!"minecraft:snow".equals(var21) && !"minecraft:snow_layer".equals(var21));

                           this.func_210060_a(var11, ChunkPaletteFormat.field_199149_e);
                        }
                     case 25:
                        var10 = ((IntList)var8.getValue()).iterator();

                        while(true) {
                           if (!var10.hasNext()) {
                              continue label251;
                           }

                           var11 = (Integer)var10.next();
                           var11 |= var9;
                           var12 = this.func_210059_c(var11);
                           if (var12 != null) {
                              var21 = Boolean.toString(var12.getBoolean("powered")) + (byte)Math.min(Math.max(var12.getByte("note"), 0), 24);
                              this.func_210060_a(var11, (Dynamic)ChunkPaletteFormat.field_199161_q.getOrDefault(var21, ChunkPaletteFormat.field_199161_q.get("false0")));
                           }
                        }
                     case 26:
                        var10 = ((IntList)var8.getValue()).iterator();

                        while(true) {
                           if (!var10.hasNext()) {
                              continue label251;
                           }

                           var11 = (Integer)var10.next();
                           var11 |= var9;
                           var12 = this.func_210066_b(var11);
                           var13 = this.func_210064_a(var11);
                           if (var12 != null) {
                              var14 = var12.getInt("color");
                              if (var14 != 14 && var14 >= 0 && var14 < 16) {
                                 var15 = ChunkPaletteFormat.func_209719_a(var13, "facing") + ChunkPaletteFormat.func_209719_a(var13, "occupied") + ChunkPaletteFormat.func_209719_a(var13, "part") + var14;
                                 if (ChunkPaletteFormat.field_199163_s.containsKey(var15)) {
                                    this.func_210060_a(var11, (Dynamic)ChunkPaletteFormat.field_199163_s.get(var15));
                                 }
                              }
                           }
                        }
                     case 64:
                     case 71:
                     case 193:
                     case 194:
                     case 195:
                     case 196:
                     case 197:
                        var10 = ((IntList)var8.getValue()).iterator();

                        while(true) {
                           if (!var10.hasNext()) {
                              continue label251;
                           }

                           var11 = (Integer)var10.next();
                           var11 |= var9;
                           var12 = this.func_210064_a(var11);
                           if (ChunkPaletteFormat.func_209726_a(var12).endsWith("_door")) {
                              var13 = this.func_210064_a(var11);
                              if ("lower".equals(ChunkPaletteFormat.func_209719_a(var13, "half"))) {
                                 var14 = func_199223_a(var11, ChunkPaletteFormat.Direction.UP);
                                 Dynamic var23 = this.func_210064_a(var14);
                                 String var16 = ChunkPaletteFormat.func_209726_a(var13);
                                 if (var16.equals(ChunkPaletteFormat.func_209726_a(var23))) {
                                    String var17 = ChunkPaletteFormat.func_209719_a(var13, "facing");
                                    String var18 = ChunkPaletteFormat.func_209719_a(var13, "open");
                                    String var19 = var2 ? "left" : ChunkPaletteFormat.func_209719_a(var23, "hinge");
                                    String var20 = var2 ? "false" : ChunkPaletteFormat.func_209719_a(var23, "powered");
                                    this.func_210060_a(var11, (Dynamic)ChunkPaletteFormat.field_199160_p.get(var16 + var17 + "lower" + var19 + var18 + var20));
                                    this.func_210060_a(var14, (Dynamic)ChunkPaletteFormat.field_199160_p.get(var16 + var17 + "upper" + var19 + var18 + var20));
                                 }
                              }
                           }
                        }
                     case 86:
                        var10 = ((IntList)var8.getValue()).iterator();

                        while(true) {
                           do {
                              do {
                                 if (!var10.hasNext()) {
                                    continue label251;
                                 }

                                 var11 = (Integer)var10.next();
                                 var11 |= var9;
                                 var12 = this.func_210064_a(var11);
                              } while(!"minecraft:carved_pumpkin".equals(ChunkPaletteFormat.func_209726_a(var12)));

                              var21 = ChunkPaletteFormat.func_209726_a(this.func_210064_a(func_199223_a(var11, ChunkPaletteFormat.Direction.DOWN)));
                           } while(!"minecraft:grass_block".equals(var21) && !"minecraft:dirt".equals(var21));

                           this.func_210060_a(var11, ChunkPaletteFormat.field_199148_d);
                        }
                     case 110:
                        var10 = ((IntList)var8.getValue()).iterator();

                        while(true) {
                           do {
                              do {
                                 if (!var10.hasNext()) {
                                    continue label251;
                                 }

                                 var11 = (Integer)var10.next();
                                 var11 |= var9;
                                 var12 = this.func_210064_a(var11);
                              } while(!"minecraft:mycelium".equals(ChunkPaletteFormat.func_209726_a(var12)));

                              var21 = ChunkPaletteFormat.func_209726_a(this.func_210064_a(func_199223_a(var11, ChunkPaletteFormat.Direction.UP)));
                           } while(!"minecraft:snow".equals(var21) && !"minecraft:snow_layer".equals(var21));

                           this.func_210060_a(var11, ChunkPaletteFormat.field_199151_g);
                        }
                     case 140:
                        var10 = ((IntList)var8.getValue()).iterator();

                        while(true) {
                           if (!var10.hasNext()) {
                              continue label251;
                           }

                           var11 = (Integer)var10.next();
                           var11 |= var9;
                           var12 = this.func_210059_c(var11);
                           if (var12 != null) {
                              var21 = var12.getString("Item") + var12.getInt("Data");
                              this.func_210060_a(var11, (Dynamic)ChunkPaletteFormat.field_199158_n.getOrDefault(var21, ChunkPaletteFormat.field_199158_n.get("minecraft:air0")));
                           }
                        }
                     case 144:
                        var10 = ((IntList)var8.getValue()).iterator();

                        while(true) {
                           do {
                              if (!var10.hasNext()) {
                                 continue label251;
                              }

                              var11 = (Integer)var10.next();
                              var11 |= var9;
                              var12 = this.func_210066_b(var11);
                           } while(var12 == null);

                           var21 = String.valueOf(var12.getByte("SkullType"));
                           var22 = ChunkPaletteFormat.func_209719_a(this.func_210064_a(var11), "facing");
                           if (!"up".equals(var22) && !"down".equals(var22)) {
                              var15 = var21 + var22;
                           } else {
                              var15 = var21 + String.valueOf(var12.getInt("Rot"));
                           }

                           var12.remove("SkullType");
                           var12.remove("facing");
                           var12.remove("Rot");
                           this.func_210060_a(var11, (Dynamic)ChunkPaletteFormat.field_199159_o.getOrDefault(var15, ChunkPaletteFormat.field_199159_o.get("0north")));
                        }
                     case 175:
                        var10 = ((IntList)var8.getValue()).iterator();

                        while(true) {
                           if (!var10.hasNext()) {
                              continue label251;
                           }

                           var11 = (Integer)var10.next();
                           var11 |= var9;
                           var12 = this.func_210064_a(var11);
                           if ("upper".equals(ChunkPaletteFormat.func_209719_a(var12, "half"))) {
                              var13 = this.func_210064_a(func_199223_a(var11, ChunkPaletteFormat.Direction.DOWN));
                              var22 = ChunkPaletteFormat.func_209726_a(var13);
                              if ("minecraft:sunflower".equals(var22)) {
                                 this.func_210060_a(var11, ChunkPaletteFormat.field_199152_h);
                              } else if ("minecraft:lilac".equals(var22)) {
                                 this.func_210060_a(var11, ChunkPaletteFormat.field_199153_i);
                              } else if ("minecraft:tall_grass".equals(var22)) {
                                 this.func_210060_a(var11, ChunkPaletteFormat.field_199154_j);
                              } else if ("minecraft:large_fern".equals(var22)) {
                                 this.func_210060_a(var11, ChunkPaletteFormat.field_199155_k);
                              } else if ("minecraft:rose_bush".equals(var22)) {
                                 this.func_210060_a(var11, ChunkPaletteFormat.field_199156_l);
                              } else if ("minecraft:peony".equals(var22)) {
                                 this.func_210060_a(var11, ChunkPaletteFormat.field_199157_m);
                              }
                           }
                        }
                     case 176:
                     case 177:
                        var10 = ((IntList)var8.getValue()).iterator();

                        while(var10.hasNext()) {
                           var11 = (Integer)var10.next();
                           var11 |= var9;
                           var12 = this.func_210066_b(var11);
                           var13 = this.func_210064_a(var11);
                           if (var12 != null) {
                              var14 = var12.getInt("Base");
                              if (var14 != 15 && var14 >= 0 && var14 < 16) {
                                 var15 = ChunkPaletteFormat.func_209719_a(var13, (Integer)var8.getKey() == 176 ? "rotation" : "facing") + "_" + var14;
                                 if (ChunkPaletteFormat.field_199164_t.containsKey(var15)) {
                                    this.func_210060_a(var11, (Dynamic)ChunkPaletteFormat.field_199164_t.get(var15));
                                 }
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
      private Dynamic<?> func_210066_b(int var1) {
         return (Dynamic)this.field_199232_f.get(var1);
      }

      @Nullable
      private Dynamic<?> func_210059_c(int var1) {
         return (Dynamic)this.field_199232_f.remove(var1);
      }

      public static int func_199223_a(int var0, ChunkPaletteFormat.Direction var1) {
         switch(var1.func_210940_b()) {
         case X:
            int var2 = (var0 & 15) + var1.func_210939_a().func_210937_a();
            return var2 >= 0 && var2 <= 15 ? var0 & -16 | var2 : -1;
         case Y:
            int var3 = (var0 >> 8) + var1.func_210939_a().func_210937_a();
            return var3 >= 0 && var3 <= 255 ? var0 & 255 | var3 << 8 : -1;
         case Z:
            int var4 = (var0 >> 4 & 15) + var1.func_210939_a().func_210937_a();
            return var4 >= 0 && var4 <= 15 ? var0 & -241 | var4 << 4 : -1;
         default:
            return -1;
         }
      }

      private void func_210060_a(int var1, Dynamic<?> var2) {
         if (var1 >= 0 && var1 <= 65535) {
            ChunkPaletteFormat.Section var3 = this.func_199221_d(var1);
            if (var3 != null) {
               var3.func_210053_a(var1 & 4095, var2);
            }
         }
      }

      @Nullable
      private ChunkPaletteFormat.Section func_199221_d(int var1) {
         int var2 = var1 >> 12;
         return var2 < this.field_199228_b.length ? this.field_199228_b[var2] : null;
      }

      public Dynamic<?> func_210064_a(int var1) {
         if (var1 >= 0 && var1 <= 65535) {
            ChunkPaletteFormat.Section var2 = this.func_199221_d(var1);
            return var2 == null ? ChunkPaletteFormat.field_199165_u : var2.func_210056_a(var1 & 4095);
         } else {
            return ChunkPaletteFormat.field_199165_u;
         }
      }

      public Dynamic<?> func_210058_a() {
         Dynamic var1 = this.field_199229_c;
         if (this.field_199232_f.isEmpty()) {
            var1 = var1.remove("TileEntities");
         } else {
            var1 = var1.set("TileEntities", var1.createList(this.field_199232_f.values().stream()));
         }

         Dynamic var2 = var1.emptyMap();
         Dynamic var3 = var1.emptyList();
         ChunkPaletteFormat.Section[] var4 = this.field_199228_b;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            ChunkPaletteFormat.Section var7 = var4[var6];
            if (var7 != null) {
               var3 = var3.merge(var7.func_210051_a());
               var2 = var2.set(String.valueOf(var7.field_199212_c), var2.createIntList(Arrays.stream(var7.field_199216_g.toIntArray())));
            }
         }

         Dynamic var8 = var1.emptyMap();
         var8 = var8.set("Sides", var8.createByte((byte)this.field_199227_a));
         var8 = var8.set("Indices", var2);
         return var1.set("UpgradeData", var8).set("Sections", var3);
      }
   }

   static class Section {
      private final IntIdentityHashBiMap<Dynamic<?>> field_199210_a = new IntIdentityHashBiMap(32);
      private Dynamic<?> field_199211_b;
      private final Dynamic<?> field_199213_d;
      private final boolean field_199214_e;
      private final Int2ObjectMap<IntList> field_199215_f = new Int2ObjectLinkedOpenHashMap();
      private final IntList field_199216_g = new IntArrayList();
      public int field_199212_c;
      private final Set<Dynamic<?>> field_199217_h = Sets.newIdentityHashSet();
      private final int[] field_199218_i = new int[4096];

      public Section(Dynamic<?> var1) {
         super();
         this.field_199211_b = var1.emptyList();
         this.field_199213_d = var1;
         this.field_199212_c = var1.getInt("Y");
         this.field_199214_e = var1.get("Blocks").isPresent();
      }

      public Dynamic<?> func_210056_a(int var1) {
         if (var1 >= 0 && var1 <= 4095) {
            Dynamic var2 = (Dynamic)this.field_199210_a.func_186813_a(this.field_199218_i[var1]);
            return var2 == null ? ChunkPaletteFormat.field_199165_u : var2;
         } else {
            return ChunkPaletteFormat.field_199165_u;
         }
      }

      public void func_210053_a(int var1, Dynamic<?> var2) {
         if (this.field_199217_h.add(var2)) {
            this.field_199211_b = this.field_199211_b.merge("%%FILTER_ME%%".equals(ChunkPaletteFormat.func_209726_a(var2)) ? ChunkPaletteFormat.field_199165_u : var2);
         }

         this.field_199218_i[var1] = ChunkPaletteFormat.func_209724_a(this.field_199210_a, var2);
      }

      public int func_199207_b(int var1) {
         if (!this.field_199214_e) {
            return var1;
         } else {
            ByteBuffer var2 = (ByteBuffer)this.field_199213_d.get("Blocks").flatMap(Dynamic::getByteBuffer).get();
            ChunkPaletteFormat.NibbleArray var3 = (ChunkPaletteFormat.NibbleArray)this.field_199213_d.get("Data").flatMap(Dynamic::getByteBuffer).map((var0) -> {
               return new ChunkPaletteFormat.NibbleArray(DataFixUtils.toArray(var0));
            }).orElseGet(ChunkPaletteFormat.NibbleArray::new);
            ChunkPaletteFormat.NibbleArray var4 = (ChunkPaletteFormat.NibbleArray)this.field_199213_d.get("Add").flatMap(Dynamic::getByteBuffer).map((var0) -> {
               return new ChunkPaletteFormat.NibbleArray(DataFixUtils.toArray(var0));
            }).orElseGet(ChunkPaletteFormat.NibbleArray::new);
            this.field_199217_h.add(ChunkPaletteFormat.field_199165_u);
            ChunkPaletteFormat.func_209724_a(this.field_199210_a, ChunkPaletteFormat.field_199165_u);
            this.field_199211_b = this.field_199211_b.merge(ChunkPaletteFormat.field_199165_u);

            for(int var5 = 0; var5 < 4096; ++var5) {
               int var6 = var5 & 15;
               int var7 = var5 >> 8 & 15;
               int var8 = var5 >> 4 & 15;
               int var9 = var4.func_210932_a(var6, var7, var8) << 12 | (var2.get(var5) & 255) << 4 | var3.func_210932_a(var6, var7, var8);
               if (ChunkPaletteFormat.field_199147_c.get(var9 >> 4)) {
                  this.func_199205_a(var9 >> 4, var5);
               }

               if (ChunkPaletteFormat.field_199146_b.get(var9 >> 4)) {
                  int var10 = ChunkPaletteFormat.func_210957_a(var6 == 0, var6 == 15, var8 == 0, var8 == 15);
                  if (var10 == 0) {
                     this.field_199216_g.add(var5);
                  } else {
                     var1 |= var10;
                  }
               }

               this.func_210053_a(var5, BlockStateFlatteningMap.func_210049_b(var9));
            }

            return var1;
         }
      }

      private void func_199205_a(int var1, int var2) {
         Object var3 = (IntList)this.field_199215_f.get(var1);
         if (var3 == null) {
            var3 = new IntArrayList();
            this.field_199215_f.put(var1, var3);
         }

         ((IntList)var3).add(var2);
      }

      public Dynamic<?> func_210051_a() {
         Dynamic var1 = this.field_199213_d;
         if (!this.field_199214_e) {
            return var1;
         } else {
            var1 = var1.set("Palette", this.field_199211_b);
            int var2 = Math.max(4, DataFixUtils.ceillog2(this.field_199217_h.size()));
            BitArray var3 = new BitArray(var2, 4096);

            for(int var4 = 0; var4 < this.field_199218_i.length; ++var4) {
               var3.func_188141_a(var4, this.field_199218_i[var4]);
            }

            var1 = var1.set("BlockStates", var1.createLongList(Arrays.stream(var3.func_188143_a())));
            var1 = var1.remove("Blocks");
            var1 = var1.remove("Data");
            var1 = var1.remove("Add");
            return var1;
         }
      }
   }
}
