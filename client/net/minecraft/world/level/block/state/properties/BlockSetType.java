package net.minecraft.world.level.block.state.properties;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;

public record BlockSetType(String name, boolean canOpenByHand, boolean canOpenByWindCharge, boolean canButtonBeActivatedByArrows, PressurePlateSensitivity pressurePlateSensitivity, SoundType soundType, SoundEvent doorClose, SoundEvent doorOpen, SoundEvent trapdoorClose, SoundEvent trapdoorOpen, SoundEvent pressurePlateClickOff, SoundEvent pressurePlateClickOn, SoundEvent buttonClickOff, SoundEvent buttonClickOn) {
   private static final Map<String, BlockSetType> TYPES = new Object2ObjectArrayMap();
   public static final Codec<BlockSetType> CODEC;
   public static final BlockSetType IRON;
   public static final BlockSetType COPPER;
   public static final BlockSetType GOLD;
   public static final BlockSetType STONE;
   public static final BlockSetType POLISHED_BLACKSTONE;
   public static final BlockSetType OAK;
   public static final BlockSetType SPRUCE;
   public static final BlockSetType BIRCH;
   public static final BlockSetType ACACIA;
   public static final BlockSetType CHERRY;
   public static final BlockSetType JUNGLE;
   public static final BlockSetType DARK_OAK;
   public static final BlockSetType PALE_OAK;
   public static final BlockSetType CRIMSON;
   public static final BlockSetType WARPED;
   public static final BlockSetType MANGROVE;
   public static final BlockSetType BAMBOO;

   public BlockSetType(String var1) {
      this(var1, true, true, true, BlockSetType.PressurePlateSensitivity.EVERYTHING, SoundType.WOOD, SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.WOODEN_DOOR_OPEN, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON, SoundEvents.WOODEN_BUTTON_CLICK_OFF, SoundEvents.WOODEN_BUTTON_CLICK_ON);
   }

   public BlockSetType(String var1, boolean var2, boolean var3, boolean var4, PressurePlateSensitivity var5, SoundType var6, SoundEvent var7, SoundEvent var8, SoundEvent var9, SoundEvent var10, SoundEvent var11, SoundEvent var12, SoundEvent var13, SoundEvent var14) {
      super();
      this.name = var1;
      this.canOpenByHand = var2;
      this.canOpenByWindCharge = var3;
      this.canButtonBeActivatedByArrows = var4;
      this.pressurePlateSensitivity = var5;
      this.soundType = var6;
      this.doorClose = var7;
      this.doorOpen = var8;
      this.trapdoorClose = var9;
      this.trapdoorOpen = var10;
      this.pressurePlateClickOff = var11;
      this.pressurePlateClickOn = var12;
      this.buttonClickOff = var13;
      this.buttonClickOn = var14;
   }

   private static BlockSetType register(BlockSetType var0) {
      TYPES.put(var0.name, var0);
      return var0;
   }

   public static Stream<BlockSetType> values() {
      return TYPES.values().stream();
   }

   static {
      Function var10000 = BlockSetType::name;
      Map var10001 = TYPES;
      Objects.requireNonNull(var10001);
      CODEC = Codec.stringResolver(var10000, var10001::get);
      IRON = register(new BlockSetType("iron", false, false, false, BlockSetType.PressurePlateSensitivity.EVERYTHING, SoundType.METAL, SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN, SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON, SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON));
      COPPER = register(new BlockSetType("copper", true, true, false, BlockSetType.PressurePlateSensitivity.EVERYTHING, SoundType.COPPER, SoundEvents.COPPER_DOOR_CLOSE, SoundEvents.COPPER_DOOR_OPEN, SoundEvents.COPPER_TRAPDOOR_CLOSE, SoundEvents.COPPER_TRAPDOOR_OPEN, SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON, SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON));
      GOLD = register(new BlockSetType("gold", false, true, false, BlockSetType.PressurePlateSensitivity.EVERYTHING, SoundType.METAL, SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN, SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON, SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON));
      STONE = register(new BlockSetType("stone", true, true, false, BlockSetType.PressurePlateSensitivity.MOBS, SoundType.STONE, SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN, SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON));
      POLISHED_BLACKSTONE = register(new BlockSetType("polished_blackstone", true, true, false, BlockSetType.PressurePlateSensitivity.MOBS, SoundType.STONE, SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN, SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON));
      OAK = register(new BlockSetType("oak"));
      SPRUCE = register(new BlockSetType("spruce"));
      BIRCH = register(new BlockSetType("birch"));
      ACACIA = register(new BlockSetType("acacia"));
      CHERRY = register(new BlockSetType("cherry", true, true, true, BlockSetType.PressurePlateSensitivity.EVERYTHING, SoundType.CHERRY_WOOD, SoundEvents.CHERRY_WOOD_DOOR_CLOSE, SoundEvents.CHERRY_WOOD_DOOR_OPEN, SoundEvents.CHERRY_WOOD_TRAPDOOR_CLOSE, SoundEvents.CHERRY_WOOD_TRAPDOOR_OPEN, SoundEvents.CHERRY_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEvents.CHERRY_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEvents.CHERRY_WOOD_BUTTON_CLICK_OFF, SoundEvents.CHERRY_WOOD_BUTTON_CLICK_ON));
      JUNGLE = register(new BlockSetType("jungle"));
      DARK_OAK = register(new BlockSetType("dark_oak"));
      PALE_OAK = register(new BlockSetType("pale_oak"));
      CRIMSON = register(new BlockSetType("crimson", true, true, true, BlockSetType.PressurePlateSensitivity.EVERYTHING, SoundType.NETHER_WOOD, SoundEvents.NETHER_WOOD_DOOR_CLOSE, SoundEvents.NETHER_WOOD_DOOR_OPEN, SoundEvents.NETHER_WOOD_TRAPDOOR_CLOSE, SoundEvents.NETHER_WOOD_TRAPDOOR_OPEN, SoundEvents.NETHER_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEvents.NETHER_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEvents.NETHER_WOOD_BUTTON_CLICK_OFF, SoundEvents.NETHER_WOOD_BUTTON_CLICK_ON));
      WARPED = register(new BlockSetType("warped", true, true, true, BlockSetType.PressurePlateSensitivity.EVERYTHING, SoundType.NETHER_WOOD, SoundEvents.NETHER_WOOD_DOOR_CLOSE, SoundEvents.NETHER_WOOD_DOOR_OPEN, SoundEvents.NETHER_WOOD_TRAPDOOR_CLOSE, SoundEvents.NETHER_WOOD_TRAPDOOR_OPEN, SoundEvents.NETHER_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEvents.NETHER_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEvents.NETHER_WOOD_BUTTON_CLICK_OFF, SoundEvents.NETHER_WOOD_BUTTON_CLICK_ON));
      MANGROVE = register(new BlockSetType("mangrove"));
      BAMBOO = register(new BlockSetType("bamboo", true, true, true, BlockSetType.PressurePlateSensitivity.EVERYTHING, SoundType.BAMBOO_WOOD, SoundEvents.BAMBOO_WOOD_DOOR_CLOSE, SoundEvents.BAMBOO_WOOD_DOOR_OPEN, SoundEvents.BAMBOO_WOOD_TRAPDOOR_CLOSE, SoundEvents.BAMBOO_WOOD_TRAPDOOR_OPEN, SoundEvents.BAMBOO_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BAMBOO_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEvents.BAMBOO_WOOD_BUTTON_CLICK_OFF, SoundEvents.BAMBOO_WOOD_BUTTON_CLICK_ON));
   }

   public static enum PressurePlateSensitivity {
      EVERYTHING,
      MOBS;

      private PressurePlateSensitivity() {
      }

      // $FF: synthetic method
      private static PressurePlateSensitivity[] $values() {
         return new PressurePlateSensitivity[]{EVERYTHING, MOBS};
      }
   }
}
