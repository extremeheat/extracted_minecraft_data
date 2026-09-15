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
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.util.datafix.PackedBitStorage;
import org.jspecify.annotations.Nullable;
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
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int SIZE = 4096;

   public ChunkPalettedStorageFix(final Schema outputSchema, final boolean changesType) {
      super(outputSchema, changesType);
   }

   private static Dynamic<?> legacyBlockState(final String id, final Map<String, String> properties) {
      Dynamic<Tag> dynamic = new Dynamic(NbtOps.INSTANCE, new CompoundTag());
      Dynamic<Tag> blockState = dynamic.set("Name", dynamic.createString(id));
      if (!properties.isEmpty()) {
         blockState = blockState.set("Properties", dynamic.createMap((Map)properties.entrySet().stream().collect(Collectors.toMap((entry) -> dynamic.createString((String)entry.getKey()), (entry) -> dynamic.createString((String)entry.getValue())))));
      }

      return blockState;
   }

   private static Dynamic<?> legacyBlockState(final String id) {
      return legacyBlockState(id, Map.of());
   }

   public static String getName(final Dynamic<?> state) {
      return state.get("Name").asString("");
   }

   public static String getProperty(final Dynamic<?> state, final String property) {
      return state.get("Properties").get(property).asString("");
   }

   public static int idFor(final CrudeIncrementalIntIdentityHashBiMap<Dynamic<?>> states, final Dynamic<?> state) {
      int id = states.getId(state);
      if (id == -1) {
         id = states.add(state);
      }

      return id;
   }

   private Dynamic<?> fix(final Dynamic<?> input) {
      Optional<? extends Dynamic<?>> level = input.get("Level").result();
      return level.isPresent() && ((Dynamic)level.get()).get("Sections").asStreamOpt().result().isPresent() ? input.set("Level", (new UpgradeChunk((Dynamic)level.get())).write()) : input;
   }

   public TypeRewriteRule makeRule() {
      Type<?> oldType = this.getInputSchema().getType(References.CHUNK);
      Type<?> newType = this.getOutputSchema().getType(References.CHUNK);
      return this.writeFixAndRead("ChunkPalettedStorageFix", oldType, newType, this::fix);
   }

   public static int getSideMask(final boolean west, final boolean east, final boolean north, final boolean south) {
      int s = 0;
      if (north) {
         if (east) {
            s |= 2;
         } else if (west) {
            s |= 128;
         } else {
            s |= 1;
         }
      } else if (south) {
         if (west) {
            s |= 32;
         } else if (east) {
            s |= 8;
         } else {
            s |= 16;
         }
      } else if (east) {
         s |= 4;
      } else if (west) {
         s |= 64;
      }

      return s;
   }

   private static class MappingConstants {
      private static final BitSet VIRTUAL = new BitSet(256);
      private static final BitSet FIX = new BitSet(256);
      private static final Dynamic<?> PUMPKIN = ChunkPalettedStorageFix.legacyBlockState("minecraft:pumpkin");
      private static final Dynamic<?> SNOWY_PODZOL = ChunkPalettedStorageFix.legacyBlockState("minecraft:podzol", Map.of("snowy", "true"));
      private static final Dynamic<?> SNOWY_GRASS = ChunkPalettedStorageFix.legacyBlockState("minecraft:grass_block", Map.of("snowy", "true"));
      private static final Dynamic<?> SNOWY_MYCELIUM = ChunkPalettedStorageFix.legacyBlockState("minecraft:mycelium", Map.of("snowy", "true"));
      private static final Dynamic<?> UPPER_SUNFLOWER = ChunkPalettedStorageFix.legacyBlockState("minecraft:sunflower", Map.of("half", "upper"));
      private static final Dynamic<?> UPPER_LILAC = ChunkPalettedStorageFix.legacyBlockState("minecraft:lilac", Map.of("half", "upper"));
      private static final Dynamic<?> UPPER_TALL_GRASS = ChunkPalettedStorageFix.legacyBlockState("minecraft:tall_grass", Map.of("half", "upper"));
      private static final Dynamic<?> UPPER_LARGE_FERN = ChunkPalettedStorageFix.legacyBlockState("minecraft:large_fern", Map.of("half", "upper"));
      private static final Dynamic<?> UPPER_ROSE_BUSH = ChunkPalettedStorageFix.legacyBlockState("minecraft:rose_bush", Map.of("half", "upper"));
      private static final Dynamic<?> UPPER_PEONY = ChunkPalettedStorageFix.legacyBlockState("minecraft:peony", Map.of("half", "upper"));
      private static final Map<String, Dynamic<?>> FLOWER_POT_MAP = (Map)DataFixUtils.make(Maps.newHashMap(), (map) -> {
         map.put("minecraft:air0", ChunkPalettedStorageFix.legacyBlockState("minecraft:flower_pot"));
         map.put("minecraft:red_flower0", ChunkPalettedStorageFix.legacyBlockState("minecraft:potted_poppy"));
         map.put("minecraft:red_flower1", ChunkPalettedStorageFix.legacyBlockState("minecraft:potted_blue_orchid"));
         map.put("minecraft:red_flower2", ChunkPalettedStorageFix.legacyBlockState("minecraft:potted_allium"));
         map.put("minecraft:red_flower3", ChunkPalettedStorageFix.legacyBlockState("minecraft:potted_azure_bluet"));
         map.put("minecraft:red_flower4", ChunkPalettedStorageFix.legacyBlockState("minecraft:potted_red_tulip"));
         map.put("minecraft:red_flower5", ChunkPalettedStorageFix.legacyBlockState("minecraft:potted_orange_tulip"));
         map.put("minecraft:red_flower6", ChunkPalettedStorageFix.legacyBlockState("minecraft:potted_white_tulip"));
         map.put("minecraft:red_flower7", ChunkPalettedStorageFix.legacyBlockState("minecraft:potted_pink_tulip"));
         map.put("minecraft:red_flower8", ChunkPalettedStorageFix.legacyBlockState("minecraft:potted_oxeye_daisy"));
         map.put("minecraft:yellow_flower0", ChunkPalettedStorageFix.legacyBlockState("minecraft:potted_dandelion"));
         map.put("minecraft:sapling0", ChunkPalettedStorageFix.legacyBlockState("minecraft:potted_oak_sapling"));
         map.put("minecraft:sapling1", ChunkPalettedStorageFix.legacyBlockState("minecraft:potted_spruce_sapling"));
         map.put("minecraft:sapling2", ChunkPalettedStorageFix.legacyBlockState("minecraft:potted_birch_sapling"));
         map.put("minecraft:sapling3", ChunkPalettedStorageFix.legacyBlockState("minecraft:potted_jungle_sapling"));
         map.put("minecraft:sapling4", ChunkPalettedStorageFix.legacyBlockState("minecraft:potted_acacia_sapling"));
         map.put("minecraft:sapling5", ChunkPalettedStorageFix.legacyBlockState("minecraft:potted_dark_oak_sapling"));
         map.put("minecraft:red_mushroom0", ChunkPalettedStorageFix.legacyBlockState("minecraft:potted_red_mushroom"));
         map.put("minecraft:brown_mushroom0", ChunkPalettedStorageFix.legacyBlockState("minecraft:potted_brown_mushroom"));
         map.put("minecraft:deadbush0", ChunkPalettedStorageFix.legacyBlockState("minecraft:potted_dead_bush"));
         map.put("minecraft:tallgrass2", ChunkPalettedStorageFix.legacyBlockState("minecraft:potted_fern"));
         map.put("minecraft:cactus0", ChunkPalettedStorageFix.legacyBlockState("minecraft:potted_cactus"));
      });
      private static final Map<String, Dynamic<?>> SKULL_MAP = (Map)DataFixUtils.make(Maps.newHashMap(), (map) -> {
         mapSkull(map, 0, "skeleton", "skull");
         mapSkull(map, 1, "wither_skeleton", "skull");
         mapSkull(map, 2, "zombie", "head");
         mapSkull(map, 3, "player", "head");
         mapSkull(map, 4, "creeper", "head");
         mapSkull(map, 5, "dragon", "head");
      });
      private static final Map<String, Dynamic<?>> DOOR_MAP = (Map)DataFixUtils.make(Maps.newHashMap(), (map) -> {
         mapDoor(map, "oak_door");
         mapDoor(map, "iron_door");
         mapDoor(map, "spruce_door");
         mapDoor(map, "birch_door");
         mapDoor(map, "jungle_door");
         mapDoor(map, "acacia_door");
         mapDoor(map, "dark_oak_door");
      });
      private static final Map<String, Dynamic<?>> NOTE_BLOCK_MAP = (Map)DataFixUtils.make(Maps.newHashMap(), (map) -> {
         for(int i = 0; i < 26; ++i) {
            map.put("true" + i, ChunkPalettedStorageFix.legacyBlockState("minecraft:note_block", Map.of("powered", "true", "note", String.valueOf(i))));
            map.put("false" + i, ChunkPalettedStorageFix.legacyBlockState("minecraft:note_block", Map.of("powered", "false", "note", String.valueOf(i))));
         }

      });
      private static final Int2ObjectMap<String> DYE_COLOR_MAP = (Int2ObjectMap)DataFixUtils.make(new Int2ObjectOpenHashMap(), (map) -> {
         map.put(0, "white");
         map.put(1, "orange");
         map.put(2, "magenta");
         map.put(3, "light_blue");
         map.put(4, "yellow");
         map.put(5, "lime");
         map.put(6, "pink");
         map.put(7, "gray");
         map.put(8, "light_gray");
         map.put(9, "cyan");
         map.put(10, "purple");
         map.put(11, "blue");
         map.put(12, "brown");
         map.put(13, "green");
         map.put(14, "red");
         map.put(15, "black");
      });
      private static final Map<String, Dynamic<?>> BED_BLOCK_MAP = (Map)DataFixUtils.make(Maps.newHashMap(), (map) -> {
         Iterator i$ = DYE_COLOR_MAP.int2ObjectEntrySet().iterator();

         while(i$.hasNext()) {
            Int2ObjectMap.Entry<String> entry = (Int2ObjectMap.Entry)i$.next();
            if (!Objects.equals(entry.getValue(), "red")) {
               addBeds(map, entry.getIntKey(), (String)entry.getValue());
            }
         }

      });
      private static final Map<String, Dynamic<?>> BANNER_BLOCK_MAP = (Map)DataFixUtils.make(Maps.newHashMap(), (map) -> {
         Iterator i$ = DYE_COLOR_MAP.int2ObjectEntrySet().iterator();

         while(i$.hasNext()) {
            Int2ObjectMap.Entry<String> entry = (Int2ObjectMap.Entry)i$.next();
            if (!Objects.equals(entry.getValue(), "white")) {
               addBanners(map, 15 - entry.getIntKey(), (String)entry.getValue());
            }
         }

      });
      private static final Dynamic<?> AIR;

      private MappingConstants() {
         super();
      }

      private static void mapSkull(final Map<String, Dynamic<?>> map, final int i, final String name, final String type) {
         map.put(i + "north", ChunkPalettedStorageFix.legacyBlockState("minecraft:" + name + "_wall_" + type, Map.of("facing", "north")));
         map.put(i + "east", ChunkPalettedStorageFix.legacyBlockState("minecraft:" + name + "_wall_" + type, Map.of("facing", "east")));
         map.put(i + "south", ChunkPalettedStorageFix.legacyBlockState("minecraft:" + name + "_wall_" + type, Map.of("facing", "south")));
         map.put(i + "west", ChunkPalettedStorageFix.legacyBlockState("minecraft:" + name + "_wall_" + type, Map.of("facing", "west")));

         for(int rot = 0; rot < 16; ++rot) {
            map.put("" + i + rot, ChunkPalettedStorageFix.legacyBlockState("minecraft:" + name + "_" + type, Map.of("rotation", String.valueOf(rot))));
         }

      }

      private static void mapDoor(final Map<String, Dynamic<?>> map, final String type) {
         String id = "minecraft:" + type;
         map.put("minecraft:" + type + "eastlowerleftfalsefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "east", "half", "lower", "hinge", "left", "open", "false", "powered", "false")));
         map.put("minecraft:" + type + "eastlowerleftfalsetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "east", "half", "lower", "hinge", "left", "open", "false", "powered", "true")));
         map.put("minecraft:" + type + "eastlowerlefttruefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "east", "half", "lower", "hinge", "left", "open", "true", "powered", "false")));
         map.put("minecraft:" + type + "eastlowerlefttruetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "east", "half", "lower", "hinge", "left", "open", "true", "powered", "true")));
         map.put("minecraft:" + type + "eastlowerrightfalsefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "east", "half", "lower", "hinge", "right", "open", "false", "powered", "false")));
         map.put("minecraft:" + type + "eastlowerrightfalsetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "east", "half", "lower", "hinge", "right", "open", "false", "powered", "true")));
         map.put("minecraft:" + type + "eastlowerrighttruefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "east", "half", "lower", "hinge", "right", "open", "true", "powered", "false")));
         map.put("minecraft:" + type + "eastlowerrighttruetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "east", "half", "lower", "hinge", "right", "open", "true", "powered", "true")));
         map.put("minecraft:" + type + "eastupperleftfalsefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "east", "half", "upper", "hinge", "left", "open", "false", "powered", "false")));
         map.put("minecraft:" + type + "eastupperleftfalsetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "east", "half", "upper", "hinge", "left", "open", "false", "powered", "true")));
         map.put("minecraft:" + type + "eastupperlefttruefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "east", "half", "upper", "hinge", "left", "open", "true", "powered", "false")));
         map.put("minecraft:" + type + "eastupperlefttruetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "east", "half", "upper", "hinge", "left", "open", "true", "powered", "true")));
         map.put("minecraft:" + type + "eastupperrightfalsefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "east", "half", "upper", "hinge", "right", "open", "false", "powered", "false")));
         map.put("minecraft:" + type + "eastupperrightfalsetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "east", "half", "upper", "hinge", "right", "open", "false", "powered", "true")));
         map.put("minecraft:" + type + "eastupperrighttruefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "east", "half", "upper", "hinge", "right", "open", "true", "powered", "false")));
         map.put("minecraft:" + type + "eastupperrighttruetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "east", "half", "upper", "hinge", "right", "open", "true", "powered", "true")));
         map.put("minecraft:" + type + "northlowerleftfalsefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "north", "half", "lower", "hinge", "left", "open", "false", "powered", "false")));
         map.put("minecraft:" + type + "northlowerleftfalsetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "north", "half", "lower", "hinge", "left", "open", "false", "powered", "true")));
         map.put("minecraft:" + type + "northlowerlefttruefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "north", "half", "lower", "hinge", "left", "open", "true", "powered", "false")));
         map.put("minecraft:" + type + "northlowerlefttruetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "north", "half", "lower", "hinge", "left", "open", "true", "powered", "true")));
         map.put("minecraft:" + type + "northlowerrightfalsefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "north", "half", "lower", "hinge", "right", "open", "false", "powered", "false")));
         map.put("minecraft:" + type + "northlowerrightfalsetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "north", "half", "lower", "hinge", "right", "open", "false", "powered", "true")));
         map.put("minecraft:" + type + "northlowerrighttruefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "north", "half", "lower", "hinge", "right", "open", "true", "powered", "false")));
         map.put("minecraft:" + type + "northlowerrighttruetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "north", "half", "lower", "hinge", "right", "open", "true", "powered", "true")));
         map.put("minecraft:" + type + "northupperleftfalsefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "north", "half", "upper", "hinge", "left", "open", "false", "powered", "false")));
         map.put("minecraft:" + type + "northupperleftfalsetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "north", "half", "upper", "hinge", "left", "open", "false", "powered", "true")));
         map.put("minecraft:" + type + "northupperlefttruefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "north", "half", "upper", "hinge", "left", "open", "true", "powered", "false")));
         map.put("minecraft:" + type + "northupperlefttruetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "north", "half", "upper", "hinge", "left", "open", "true", "powered", "true")));
         map.put("minecraft:" + type + "northupperrightfalsefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "north", "half", "upper", "hinge", "right", "open", "false", "powered", "false")));
         map.put("minecraft:" + type + "northupperrightfalsetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "north", "half", "upper", "hinge", "right", "open", "false", "powered", "true")));
         map.put("minecraft:" + type + "northupperrighttruefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "north", "half", "upper", "hinge", "right", "open", "true", "powered", "false")));
         map.put("minecraft:" + type + "northupperrighttruetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "north", "half", "upper", "hinge", "right", "open", "true", "powered", "true")));
         map.put("minecraft:" + type + "southlowerleftfalsefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "south", "half", "lower", "hinge", "left", "open", "false", "powered", "false")));
         map.put("minecraft:" + type + "southlowerleftfalsetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "south", "half", "lower", "hinge", "left", "open", "false", "powered", "true")));
         map.put("minecraft:" + type + "southlowerlefttruefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "south", "half", "lower", "hinge", "left", "open", "true", "powered", "false")));
         map.put("minecraft:" + type + "southlowerlefttruetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "south", "half", "lower", "hinge", "left", "open", "true", "powered", "true")));
         map.put("minecraft:" + type + "southlowerrightfalsefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "south", "half", "lower", "hinge", "right", "open", "false", "powered", "false")));
         map.put("minecraft:" + type + "southlowerrightfalsetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "south", "half", "lower", "hinge", "right", "open", "false", "powered", "true")));
         map.put("minecraft:" + type + "southlowerrighttruefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "south", "half", "lower", "hinge", "right", "open", "true", "powered", "false")));
         map.put("minecraft:" + type + "southlowerrighttruetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "south", "half", "lower", "hinge", "right", "open", "true", "powered", "true")));
         map.put("minecraft:" + type + "southupperleftfalsefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "south", "half", "upper", "hinge", "left", "open", "false", "powered", "false")));
         map.put("minecraft:" + type + "southupperleftfalsetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "south", "half", "upper", "hinge", "left", "open", "false", "powered", "true")));
         map.put("minecraft:" + type + "southupperlefttruefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "south", "half", "upper", "hinge", "left", "open", "true", "powered", "false")));
         map.put("minecraft:" + type + "southupperlefttruetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "south", "half", "upper", "hinge", "left", "open", "true", "powered", "true")));
         map.put("minecraft:" + type + "southupperrightfalsefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "south", "half", "upper", "hinge", "right", "open", "false", "powered", "false")));
         map.put("minecraft:" + type + "southupperrightfalsetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "south", "half", "upper", "hinge", "right", "open", "false", "powered", "true")));
         map.put("minecraft:" + type + "southupperrighttruefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "south", "half", "upper", "hinge", "right", "open", "true", "powered", "false")));
         map.put("minecraft:" + type + "southupperrighttruetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "south", "half", "upper", "hinge", "right", "open", "true", "powered", "true")));
         map.put("minecraft:" + type + "westlowerleftfalsefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "west", "half", "lower", "hinge", "left", "open", "false", "powered", "false")));
         map.put("minecraft:" + type + "westlowerleftfalsetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "west", "half", "lower", "hinge", "left", "open", "false", "powered", "true")));
         map.put("minecraft:" + type + "westlowerlefttruefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "west", "half", "lower", "hinge", "left", "open", "true", "powered", "false")));
         map.put("minecraft:" + type + "westlowerlefttruetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "west", "half", "lower", "hinge", "left", "open", "true", "powered", "true")));
         map.put("minecraft:" + type + "westlowerrightfalsefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "west", "half", "lower", "hinge", "right", "open", "false", "powered", "false")));
         map.put("minecraft:" + type + "westlowerrightfalsetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "west", "half", "lower", "hinge", "right", "open", "false", "powered", "true")));
         map.put("minecraft:" + type + "westlowerrighttruefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "west", "half", "lower", "hinge", "right", "open", "true", "powered", "false")));
         map.put("minecraft:" + type + "westlowerrighttruetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "west", "half", "lower", "hinge", "right", "open", "true", "powered", "true")));
         map.put("minecraft:" + type + "westupperleftfalsefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "west", "half", "upper", "hinge", "left", "open", "false", "powered", "false")));
         map.put("minecraft:" + type + "westupperleftfalsetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "west", "half", "upper", "hinge", "left", "open", "false", "powered", "true")));
         map.put("minecraft:" + type + "westupperlefttruefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "west", "half", "upper", "hinge", "left", "open", "true", "powered", "false")));
         map.put("minecraft:" + type + "westupperlefttruetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "west", "half", "upper", "hinge", "left", "open", "true", "powered", "true")));
         map.put("minecraft:" + type + "westupperrightfalsefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "west", "half", "upper", "hinge", "right", "open", "false", "powered", "false")));
         map.put("minecraft:" + type + "westupperrightfalsetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "west", "half", "upper", "hinge", "right", "open", "false", "powered", "true")));
         map.put("minecraft:" + type + "westupperrighttruefalse", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "west", "half", "upper", "hinge", "right", "open", "true", "powered", "false")));
         map.put("minecraft:" + type + "westupperrighttruetrue", ChunkPalettedStorageFix.legacyBlockState(id, Map.of("facing", "west", "half", "upper", "hinge", "right", "open", "true", "powered", "true")));
      }

      private static void addBeds(final Map<String, Dynamic<?>> map, final int colorId, final String color) {
         map.put("southfalsefoot" + colorId, ChunkPalettedStorageFix.legacyBlockState("minecraft:" + color + "_bed", Map.of("facing", "south", "occupied", "false", "part", "foot")));
         map.put("westfalsefoot" + colorId, ChunkPalettedStorageFix.legacyBlockState("minecraft:" + color + "_bed", Map.of("facing", "west", "occupied", "false", "part", "foot")));
         map.put("northfalsefoot" + colorId, ChunkPalettedStorageFix.legacyBlockState("minecraft:" + color + "_bed", Map.of("facing", "north", "occupied", "false", "part", "foot")));
         map.put("eastfalsefoot" + colorId, ChunkPalettedStorageFix.legacyBlockState("minecraft:" + color + "_bed", Map.of("facing", "east", "occupied", "false", "part", "foot")));
         map.put("southfalsehead" + colorId, ChunkPalettedStorageFix.legacyBlockState("minecraft:" + color + "_bed", Map.of("facing", "south", "occupied", "false", "part", "head")));
         map.put("westfalsehead" + colorId, ChunkPalettedStorageFix.legacyBlockState("minecraft:" + color + "_bed", Map.of("facing", "west", "occupied", "false", "part", "head")));
         map.put("northfalsehead" + colorId, ChunkPalettedStorageFix.legacyBlockState("minecraft:" + color + "_bed", Map.of("facing", "north", "occupied", "false", "part", "head")));
         map.put("eastfalsehead" + colorId, ChunkPalettedStorageFix.legacyBlockState("minecraft:" + color + "_bed", Map.of("facing", "east", "occupied", "false", "part", "head")));
         map.put("southtruehead" + colorId, ChunkPalettedStorageFix.legacyBlockState("minecraft:" + color + "_bed", Map.of("facing", "south", "occupied", "true", "part", "head")));
         map.put("westtruehead" + colorId, ChunkPalettedStorageFix.legacyBlockState("minecraft:" + color + "_bed", Map.of("facing", "west", "occupied", "true", "part", "head")));
         map.put("northtruehead" + colorId, ChunkPalettedStorageFix.legacyBlockState("minecraft:" + color + "_bed", Map.of("facing", "north", "occupied", "true", "part", "head")));
         map.put("easttruehead" + colorId, ChunkPalettedStorageFix.legacyBlockState("minecraft:" + color + "_bed", Map.of("facing", "east", "occupied", "true", "part", "head")));
      }

      private static void addBanners(final Map<String, Dynamic<?>> map, final int colorId, final String color) {
         for(int i = 0; i < 16; ++i) {
            map.put(i + "_" + colorId, ChunkPalettedStorageFix.legacyBlockState("minecraft:" + color + "_banner", Map.of("rotation", String.valueOf(i))));
         }

         map.put("north_" + colorId, ChunkPalettedStorageFix.legacyBlockState("minecraft:" + color + "_wall_banner", Map.of("facing", "north")));
         map.put("south_" + colorId, ChunkPalettedStorageFix.legacyBlockState("minecraft:" + color + "_wall_banner", Map.of("facing", "south")));
         map.put("west_" + colorId, ChunkPalettedStorageFix.legacyBlockState("minecraft:" + color + "_wall_banner", Map.of("facing", "west")));
         map.put("east_" + colorId, ChunkPalettedStorageFix.legacyBlockState("minecraft:" + color + "_wall_banner", Map.of("facing", "east")));
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
         AIR = ChunkPalettedStorageFix.legacyBlockState("minecraft:air");
      }
   }

   private static class Section {
      private final CrudeIncrementalIntIdentityHashBiMap<Dynamic<?>> palette = CrudeIncrementalIntIdentityHashBiMap.<Dynamic<?>>create(32);
      private final List<Dynamic<?>> listTag = Lists.newArrayList();
      private final Dynamic<?> section;
      private final boolean hasData;
      private final Int2ObjectMap<IntList> toFix = new Int2ObjectLinkedOpenHashMap();
      private final IntList update = new IntArrayList();
      public final int y;
      private final Set<Dynamic<?>> seen = Sets.newIdentityHashSet();
      private final int[] buffer = new int[4096];

      public Section(final Dynamic<?> section) {
         super();
         this.section = section;
         this.y = section.get("Y").asInt(0);
         this.hasData = section.get("Blocks").result().isPresent();
      }

      public Dynamic<?> getBlock(final int pos) {
         if (pos >= 0 && pos <= 4095) {
            Dynamic<?> tag = this.palette.byId(this.buffer[pos]);
            return tag == null ? ChunkPalettedStorageFix.MappingConstants.AIR : tag;
         } else {
            return ChunkPalettedStorageFix.MappingConstants.AIR;
         }
      }

      public void setBlock(final int idx, final Dynamic<?> blockState) {
         if (this.seen.add(blockState)) {
            this.listTag.add("%%FILTER_ME%%".equals(ChunkPalettedStorageFix.getName(blockState)) ? ChunkPalettedStorageFix.MappingConstants.AIR : blockState);
         }

         this.buffer[idx] = ChunkPalettedStorageFix.idFor(this.palette, blockState);
      }

      public int upgrade(int sides) {
         if (!this.hasData) {
            return sides;
         } else {
            ByteBuffer blocks = (ByteBuffer)this.section.get("Blocks").asByteBufferOpt().result().get();
            DataLayer data = (DataLayer)this.section.get("Data").asByteBufferOpt().map((buffer) -> new DataLayer(DataFixUtils.toArray(buffer))).result().orElseGet(DataLayer::new);
            DataLayer addBlocks = (DataLayer)this.section.get("Add").asByteBufferOpt().map((buffer) -> new DataLayer(DataFixUtils.toArray(buffer))).result().orElseGet(DataLayer::new);
            this.seen.add(ChunkPalettedStorageFix.MappingConstants.AIR);
            ChunkPalettedStorageFix.idFor(this.palette, ChunkPalettedStorageFix.MappingConstants.AIR);
            this.listTag.add(ChunkPalettedStorageFix.MappingConstants.AIR);

            for(int idx = 0; idx < 4096; ++idx) {
               int xx = idx & 15;
               int yy = idx >> 8 & 15;
               int zz = idx >> 4 & 15;
               int id = addBlocks.get(xx, yy, zz) << 12 | (blocks.get(idx) & 255) << 4 | data.get(xx, yy, zz);
               if (ChunkPalettedStorageFix.MappingConstants.FIX.get(id >> 4)) {
                  this.addFix(id >> 4, idx);
               }

               if (ChunkPalettedStorageFix.MappingConstants.VIRTUAL.get(id >> 4)) {
                  int s = ChunkPalettedStorageFix.getSideMask(xx == 0, xx == 15, zz == 0, zz == 15);
                  if (s == 0) {
                     this.update.add(idx);
                  } else {
                     sides |= s;
                  }
               }

               this.setBlock(idx, BlockStateData.getTag(id));
            }

            return sides;
         }
      }

      private void addFix(final int id, final int position) {
         IntList list = (IntList)this.toFix.get(id);
         if (list == null) {
            list = new IntArrayList();
            this.toFix.put(id, list);
         }

         list.add(position);
      }

      public Dynamic<?> write() {
         Dynamic<?> section = this.section;
         if (!this.hasData) {
            return section;
         } else {
            section = section.set("Palette", section.createList(this.listTag.stream()));
            int size = Math.max(4, DataFixUtils.ceillog2(this.seen.size()));
            PackedBitStorage storage = new PackedBitStorage(size, 4096);

            for(int j = 0; j < this.buffer.length; ++j) {
               storage.set(j, this.buffer[j]);
            }

            section = section.set("BlockStates", section.createLongList(Arrays.stream(storage.getRaw())));
            section = section.remove("Blocks");
            section = section.remove("Data");
            section = section.remove("Add");
            return section;
         }
      }
   }

   private static final class UpgradeChunk {
      private int sides;
      private final @Nullable ChunkPalettedStorageFix.Section[] sections = new Section[16];
      private final Dynamic<?> level;
      private final int x;
      private final int z;
      private final Int2ObjectMap<Dynamic<?>> blockEntities = new Int2ObjectLinkedOpenHashMap(16);

      public UpgradeChunk(final Dynamic<?> level) {
         super();
         this.level = level;
         this.x = level.get("xPos").asInt(0) << 4;
         this.z = level.get("zPos").asInt(0) << 4;
         level.get("TileEntities").asStreamOpt().ifSuccess((s) -> s.forEach((entity) -> {
               int x = entity.get("x").asInt(0) - this.x & 15;
               int y = entity.get("y").asInt(0);
               int z = entity.get("z").asInt(0) - this.z & 15;
               int key = y << 8 | z << 4 | x;
               if (this.blockEntities.put(key, entity) != null) {
                  ChunkPalettedStorageFix.LOGGER.warn("In chunk: {}x{} found a duplicate block entity at position: [{}, {}, {}]", new Object[]{this.x, this.z, x, y, z});
               }

            }));
         boolean convertedFromAlphaFormat = level.get("convertedFromAlphaFormat").asBoolean(false);
         level.get("Sections").asStreamOpt().ifSuccess((s) -> s.forEach((sec) -> {
               Section section = new Section(sec);
               this.sides = section.upgrade(this.sides);
               this.sections[section.y] = section;
            }));

         for(Section section : this.sections) {
            if (section != null) {
               ObjectIterator var7 = section.toFix.int2ObjectEntrySet().iterator();

               while(var7.hasNext()) {
                  Int2ObjectMap.Entry<IntList> entry = (Int2ObjectMap.Entry)var7.next();
                  int dy = section.y << 12;
                  switch (entry.getIntKey()) {
                     case 2:
                        IntListIterator var30 = ((IntList)entry.getValue()).iterator();

                        while(var30.hasNext()) {
                           int pos = (Integer)var30.next();
                           pos |= dy;
                           Dynamic<?> state = this.getBlock(pos);
                           if ("minecraft:grass_block".equals(ChunkPalettedStorageFix.getName(state))) {
                              String name = ChunkPalettedStorageFix.getName(this.getBlock(relative(pos, ChunkPalettedStorageFix.Direction.UP)));
                              if ("minecraft:snow".equals(name) || "minecraft:snow_layer".equals(name)) {
                                 this.setBlock(pos, ChunkPalettedStorageFix.MappingConstants.SNOWY_GRASS);
                              }
                           }
                        }
                        break;
                     case 3:
                        IntListIterator var29 = ((IntList)entry.getValue()).iterator();

                        while(var29.hasNext()) {
                           int pos = (Integer)var29.next();
                           pos |= dy;
                           Dynamic<?> state = this.getBlock(pos);
                           if ("minecraft:podzol".equals(ChunkPalettedStorageFix.getName(state))) {
                              String name = ChunkPalettedStorageFix.getName(this.getBlock(relative(pos, ChunkPalettedStorageFix.Direction.UP)));
                              if ("minecraft:snow".equals(name) || "minecraft:snow_layer".equals(name)) {
                                 this.setBlock(pos, ChunkPalettedStorageFix.MappingConstants.SNOWY_PODZOL);
                              }
                           }
                        }
                        break;
                     case 25:
                        IntListIterator var28 = ((IntList)entry.getValue()).iterator();

                        while(var28.hasNext()) {
                           int pos = (Integer)var28.next();
                           pos |= dy;
                           Dynamic<?> entity = this.removeBlockEntity(pos);
                           if (entity != null) {
                              String var82 = Boolean.toString(entity.get("powered").asBoolean(false));
                              String key = var82 + (byte)Math.min(Math.max(entity.get("note").asInt(0), 0), 24);
                              this.setBlock(pos, (Dynamic)ChunkPalettedStorageFix.MappingConstants.NOTE_BLOCK_MAP.getOrDefault(key, (Dynamic)ChunkPalettedStorageFix.MappingConstants.NOTE_BLOCK_MAP.get("false0")));
                           }
                        }
                        break;
                     case 26:
                        IntListIterator var27 = ((IntList)entry.getValue()).iterator();

                        while(var27.hasNext()) {
                           int pos = (Integer)var27.next();
                           pos |= dy;
                           Dynamic<?> entity = this.getBlockEntity(pos);
                           Dynamic<?> state = this.getBlock(pos);
                           if (entity != null) {
                              int color = entity.get("color").asInt(0);
                              if (color != 14 && color >= 0 && color < 16) {
                                 String var81 = ChunkPalettedStorageFix.getProperty(state, "facing");
                                 String key = var81 + ChunkPalettedStorageFix.getProperty(state, "occupied") + ChunkPalettedStorageFix.getProperty(state, "part") + color;
                                 if (ChunkPalettedStorageFix.MappingConstants.BED_BLOCK_MAP.containsKey(key)) {
                                    this.setBlock(pos, (Dynamic)ChunkPalettedStorageFix.MappingConstants.BED_BLOCK_MAP.get(key));
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
                        IntListIterator var26 = ((IntList)entry.getValue()).iterator();

                        while(var26.hasNext()) {
                           int pos = (Integer)var26.next();
                           pos |= dy;
                           Dynamic<?> state = this.getBlock(pos);
                           if (ChunkPalettedStorageFix.getName(state).endsWith("_door")) {
                              Dynamic<?> lower = this.getBlock(pos);
                              if ("lower".equals(ChunkPalettedStorageFix.getProperty(lower, "half"))) {
                                 int abovePos = relative(pos, ChunkPalettedStorageFix.Direction.UP);
                                 Dynamic<?> upper = this.getBlock(abovePos);
                                 String name = ChunkPalettedStorageFix.getName(lower);
                                 if (name.equals(ChunkPalettedStorageFix.getName(upper))) {
                                    String facing = ChunkPalettedStorageFix.getProperty(lower, "facing");
                                    String open = ChunkPalettedStorageFix.getProperty(lower, "open");
                                    String hinge = convertedFromAlphaFormat ? "left" : ChunkPalettedStorageFix.getProperty(upper, "hinge");
                                    String powered = convertedFromAlphaFormat ? "false" : ChunkPalettedStorageFix.getProperty(upper, "powered");
                                    this.setBlock(pos, (Dynamic)ChunkPalettedStorageFix.MappingConstants.DOOR_MAP.get(name + facing + "lower" + hinge + open + powered));
                                    this.setBlock(abovePos, (Dynamic)ChunkPalettedStorageFix.MappingConstants.DOOR_MAP.get(name + facing + "upper" + hinge + open + powered));
                                 }
                              }
                           }
                        }
                        break;
                     case 86:
                        IntListIterator var25 = ((IntList)entry.getValue()).iterator();

                        while(var25.hasNext()) {
                           int pos = (Integer)var25.next();
                           pos |= dy;
                           Dynamic<?> state = this.getBlock(pos);
                           if ("minecraft:carved_pumpkin".equals(ChunkPalettedStorageFix.getName(state))) {
                              String name = ChunkPalettedStorageFix.getName(this.getBlock(relative(pos, ChunkPalettedStorageFix.Direction.DOWN)));
                              if ("minecraft:grass_block".equals(name) || "minecraft:dirt".equals(name)) {
                                 this.setBlock(pos, ChunkPalettedStorageFix.MappingConstants.PUMPKIN);
                              }
                           }
                        }
                        break;
                     case 110:
                        IntListIterator var24 = ((IntList)entry.getValue()).iterator();

                        while(var24.hasNext()) {
                           int pos = (Integer)var24.next();
                           pos |= dy;
                           Dynamic<?> state = this.getBlock(pos);
                           if ("minecraft:mycelium".equals(ChunkPalettedStorageFix.getName(state))) {
                              String name = ChunkPalettedStorageFix.getName(this.getBlock(relative(pos, ChunkPalettedStorageFix.Direction.UP)));
                              if ("minecraft:snow".equals(name) || "minecraft:snow_layer".equals(name)) {
                                 this.setBlock(pos, ChunkPalettedStorageFix.MappingConstants.SNOWY_MYCELIUM);
                              }
                           }
                        }
                        break;
                     case 140:
                        IntListIterator var23 = ((IntList)entry.getValue()).iterator();

                        while(var23.hasNext()) {
                           int pos = (Integer)var23.next();
                           pos |= dy;
                           Dynamic<?> entity = this.removeBlockEntity(pos);
                           if (entity != null) {
                              String var80 = entity.get("Item").asString("");
                              String key = var80 + entity.get("Data").asInt(0);
                              this.setBlock(pos, (Dynamic)ChunkPalettedStorageFix.MappingConstants.FLOWER_POT_MAP.getOrDefault(key, (Dynamic)ChunkPalettedStorageFix.MappingConstants.FLOWER_POT_MAP.get("minecraft:air0")));
                           }
                        }
                        break;
                     case 144:
                        IntListIterator var22 = ((IntList)entry.getValue()).iterator();

                        while(var22.hasNext()) {
                           int pos = (Integer)var22.next();
                           pos |= dy;
                           Dynamic<?> entity = this.getBlockEntity(pos);
                           if (entity != null) {
                              String type = String.valueOf(entity.get("SkullType").asInt(0));
                              String facing = ChunkPalettedStorageFix.getProperty(this.getBlock(pos), "facing");
                              String key;
                              if (!"up".equals(facing) && !"down".equals(facing)) {
                                 key = type + facing;
                              } else {
                                 key = type + entity.get("Rot").asInt(0);
                              }

                              entity.remove("SkullType");
                              entity.remove("facing");
                              entity.remove("Rot");
                              this.setBlock(pos, (Dynamic)ChunkPalettedStorageFix.MappingConstants.SKULL_MAP.getOrDefault(key, (Dynamic)ChunkPalettedStorageFix.MappingConstants.SKULL_MAP.get("0north")));
                           }
                        }
                        break;
                     case 175:
                        IntListIterator var21 = ((IntList)entry.getValue()).iterator();

                        while(var21.hasNext()) {
                           int pos = (Integer)var21.next();
                           pos |= dy;
                           Dynamic<?> block = this.getBlock(pos);
                           if ("upper".equals(ChunkPalettedStorageFix.getProperty(block, "half"))) {
                              Dynamic<?> below = this.getBlock(relative(pos, ChunkPalettedStorageFix.Direction.DOWN));
                              switch (ChunkPalettedStorageFix.getName(below)) {
                                 case "minecraft:sunflower":
                                    this.setBlock(pos, ChunkPalettedStorageFix.MappingConstants.UPPER_SUNFLOWER);
                                    break;
                                 case "minecraft:lilac":
                                    this.setBlock(pos, ChunkPalettedStorageFix.MappingConstants.UPPER_LILAC);
                                    break;
                                 case "minecraft:tall_grass":
                                    this.setBlock(pos, ChunkPalettedStorageFix.MappingConstants.UPPER_TALL_GRASS);
                                    break;
                                 case "minecraft:large_fern":
                                    this.setBlock(pos, ChunkPalettedStorageFix.MappingConstants.UPPER_LARGE_FERN);
                                    break;
                                 case "minecraft:rose_bush":
                                    this.setBlock(pos, ChunkPalettedStorageFix.MappingConstants.UPPER_ROSE_BUSH);
                                    break;
                                 case "minecraft:peony":
                                    this.setBlock(pos, ChunkPalettedStorageFix.MappingConstants.UPPER_PEONY);
                              }
                           }
                        }
                        break;
                     case 176:
                     case 177:
                        IntListIterator var10 = ((IntList)entry.getValue()).iterator();

                        while(var10.hasNext()) {
                           int pos = (Integer)var10.next();
                           pos |= dy;
                           Dynamic<?> entity = this.getBlockEntity(pos);
                           Dynamic<?> state = this.getBlock(pos);
                           if (entity != null) {
                              int color = entity.get("Base").asInt(0);
                              if (color != 15 && color >= 0 && color < 16) {
                                 String var10000 = ChunkPalettedStorageFix.getProperty(state, entry.getIntKey() == 176 ? "rotation" : "facing");
                                 String key = var10000 + "_" + color;
                                 if (ChunkPalettedStorageFix.MappingConstants.BANNER_BLOCK_MAP.containsKey(key)) {
                                    this.setBlock(pos, (Dynamic)ChunkPalettedStorageFix.MappingConstants.BANNER_BLOCK_MAP.get(key));
                                 }
                              }
                           }
                        }
                  }
               }
            }
         }

      }

      private @Nullable Dynamic<?> getBlockEntity(final int pos) {
         return (Dynamic)this.blockEntities.get(pos);
      }

      private @Nullable Dynamic<?> removeBlockEntity(final int pos) {
         return (Dynamic)this.blockEntities.remove(pos);
      }

      public static int relative(final int pos, final Direction direction) {
         int var10000;
         switch (direction.getAxis().ordinal()) {
            case 0:
               int x = (pos & 15) + direction.getAxisDirection().getStep();
               var10000 = x >= 0 && x <= 15 ? pos & -16 | x : -1;
               break;
            case 1:
               int y = (pos >> 8) + direction.getAxisDirection().getStep();
               var10000 = y >= 0 && y <= 255 ? pos & 255 | y << 8 : -1;
               break;
            case 2:
               int z = (pos >> 4 & 15) + direction.getAxisDirection().getStep();
               var10000 = z >= 0 && z <= 15 ? pos & -241 | z << 4 : -1;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      private void setBlock(final int pos, final Dynamic<?> block) {
         if (pos >= 0 && pos <= 65535) {
            Section section = this.getSection(pos);
            if (section != null) {
               section.setBlock(pos & 4095, block);
            }
         }
      }

      private @Nullable Section getSection(final int pos) {
         int sectionY = pos >> 12;
         return sectionY < this.sections.length ? this.sections[sectionY] : null;
      }

      public Dynamic<?> getBlock(final int pos) {
         if (pos >= 0 && pos <= 65535) {
            Section section = this.getSection(pos);
            return section == null ? ChunkPalettedStorageFix.MappingConstants.AIR : section.getBlock(pos & 4095);
         } else {
            return ChunkPalettedStorageFix.MappingConstants.AIR;
         }
      }

      public Dynamic<?> write() {
         Dynamic<?> level = this.level;
         if (this.blockEntities.isEmpty()) {
            level = level.remove("TileEntities");
         } else {
            level = level.set("TileEntities", level.createList(this.blockEntities.values().stream()));
         }

         Dynamic<?> indices = level.emptyMap();
         List<Dynamic<?>> sections = Lists.newArrayList();

         for(Section section : this.sections) {
            if (section != null) {
               sections.add(section.write());
               indices = indices.set(String.valueOf(section.y), indices.createIntList(Arrays.stream(section.update.toIntArray())));
            }
         }

         Dynamic<?> tag = level.emptyMap();
         tag = tag.set("Sides", tag.createByte((byte)this.sides));
         tag = tag.set("Indices", indices);
         return level.set("UpgradeData", tag).set("Sections", tag.createList(sections.stream()));
      }
   }

   private static class DataLayer {
      private static final int SIZE = 2048;
      private static final int NIBBLE_SIZE = 4;
      private final byte[] data;

      public DataLayer() {
         super();
         this.data = new byte[2048];
      }

      public DataLayer(final byte[] data) {
         super();
         this.data = data;
         if (data.length != 2048) {
            throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + data.length);
         }
      }

      public int get(final int x, final int y, final int z) {
         int position = this.getPosition(y << 8 | z << 4 | x);
         return this.isFirst(y << 8 | z << 4 | x) ? this.data[position] & 15 : this.data[position] >> 4 & 15;
      }

      private boolean isFirst(final int position) {
         return (position & 1) == 0;
      }

      private int getPosition(final int position) {
         return position >> 1;
      }
   }

   public static enum Direction {
      DOWN(ChunkPalettedStorageFix.Direction.AxisDirection.NEGATIVE, ChunkPalettedStorageFix.Direction.Axis.Y),
      UP(ChunkPalettedStorageFix.Direction.AxisDirection.POSITIVE, ChunkPalettedStorageFix.Direction.Axis.Y),
      NORTH(ChunkPalettedStorageFix.Direction.AxisDirection.NEGATIVE, ChunkPalettedStorageFix.Direction.Axis.Z),
      SOUTH(ChunkPalettedStorageFix.Direction.AxisDirection.POSITIVE, ChunkPalettedStorageFix.Direction.Axis.Z),
      WEST(ChunkPalettedStorageFix.Direction.AxisDirection.NEGATIVE, ChunkPalettedStorageFix.Direction.Axis.X),
      EAST(ChunkPalettedStorageFix.Direction.AxisDirection.POSITIVE, ChunkPalettedStorageFix.Direction.Axis.X);

      private final Axis axis;
      private final AxisDirection axisDirection;

      private Direction(final AxisDirection axisDirection, final Axis axis) {
         this.axis = axis;
         this.axisDirection = axisDirection;
      }

      public AxisDirection getAxisDirection() {
         return this.axisDirection;
      }

      public Axis getAxis() {
         return this.axis;
      }

      // $FF: synthetic method
      private static Direction[] $values() {
         return new Direction[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
      }

      public static enum Axis {
         X,
         Y,
         Z;

         private Axis() {
         }

         // $FF: synthetic method
         private static Axis[] $values() {
            return new Axis[]{X, Y, Z};
         }
      }

      public static enum AxisDirection {
         POSITIVE(1),
         NEGATIVE(-1);

         private final int step;

         private AxisDirection(final int step) {
            this.step = step;
         }

         public int getStep() {
            return this.step;
         }

         // $FF: synthetic method
         private static AxisDirection[] $values() {
            return new AxisDirection[]{POSITIVE, NEGATIVE};
         }
      }
   }
}
