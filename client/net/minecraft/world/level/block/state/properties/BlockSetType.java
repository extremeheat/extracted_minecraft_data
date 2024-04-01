package net.minecraft.world.level.block.state.properties;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.SoundType;

public record BlockSetType(
   String s,
   boolean t,
   boolean u,
   boolean v,
   BlockSetType.PressurePlateSensitivity w,
   SoundType x,
   SoundEvent y,
   SoundEvent z,
   SoundEvent A,
   SoundEvent B,
   SoundEvent C,
   SoundEvent D,
   SoundEvent E,
   SoundEvent F
) {
   private final String name;
   private final boolean canOpenByHand;
   private final boolean canOpenByWindCharge;
   private final boolean canButtonBeActivatedByArrows;
   private final BlockSetType.PressurePlateSensitivity pressurePlateSensitivity;
   private final SoundType soundType;
   private final SoundEvent doorClose;
   private final SoundEvent doorOpen;
   private final SoundEvent trapdoorClose;
   private final SoundEvent trapdoorOpen;
   private final SoundEvent pressurePlateClickOff;
   private final SoundEvent pressurePlateClickOn;
   private final SoundEvent buttonClickOff;
   private final SoundEvent buttonClickOn;
   private static final Map<String, BlockSetType> TYPES = new Object2ObjectArrayMap();
   public static final Codec<BlockSetType> CODEC = ExtraCodecs.stringResolverCodec(BlockSetType::name, TYPES::get);
   public static final BlockSetType IRON = register(
      new BlockSetType(
         "iron",
         false,
         false,
         false,
         BlockSetType.PressurePlateSensitivity.EVERYTHING,
         SoundType.METAL,
         SoundEvents.IRON_DOOR_CLOSE,
         SoundEvents.IRON_DOOR_OPEN,
         SoundEvents.IRON_TRAPDOOR_CLOSE,
         SoundEvents.IRON_TRAPDOOR_OPEN,
         SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF,
         SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON,
         SoundEvents.STONE_BUTTON_CLICK_OFF,
         SoundEvents.STONE_BUTTON_CLICK_ON
      )
   );
   public static final BlockSetType COPPER = register(
      new BlockSetType(
         "copper",
         true,
         true,
         false,
         BlockSetType.PressurePlateSensitivity.EVERYTHING,
         SoundType.COPPER,
         SoundEvents.COPPER_DOOR_CLOSE,
         SoundEvents.COPPER_DOOR_OPEN,
         SoundEvents.COPPER_TRAPDOOR_CLOSE,
         SoundEvents.COPPER_TRAPDOOR_OPEN,
         SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF,
         SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON,
         SoundEvents.STONE_BUTTON_CLICK_OFF,
         SoundEvents.STONE_BUTTON_CLICK_ON
      )
   );
   public static final BlockSetType GOLD = register(
      new BlockSetType(
         "gold",
         false,
         true,
         false,
         BlockSetType.PressurePlateSensitivity.EVERYTHING,
         SoundType.METAL,
         SoundEvents.IRON_DOOR_CLOSE,
         SoundEvents.IRON_DOOR_OPEN,
         SoundEvents.IRON_TRAPDOOR_CLOSE,
         SoundEvents.IRON_TRAPDOOR_OPEN,
         SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF,
         SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON,
         SoundEvents.STONE_BUTTON_CLICK_OFF,
         SoundEvents.STONE_BUTTON_CLICK_ON
      )
   );
   public static final BlockSetType STONE = register(
      new BlockSetType(
         "stone",
         true,
         true,
         false,
         BlockSetType.PressurePlateSensitivity.MOBS,
         SoundType.STONE,
         SoundEvents.IRON_DOOR_CLOSE,
         SoundEvents.IRON_DOOR_OPEN,
         SoundEvents.IRON_TRAPDOOR_CLOSE,
         SoundEvents.IRON_TRAPDOOR_OPEN,
         SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF,
         SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON,
         SoundEvents.STONE_BUTTON_CLICK_OFF,
         SoundEvents.STONE_BUTTON_CLICK_ON
      )
   );
   public static final BlockSetType POLISHED_BLACKSTONE = register(
      new BlockSetType(
         "polished_blackstone",
         true,
         true,
         false,
         BlockSetType.PressurePlateSensitivity.MOBS,
         SoundType.STONE,
         SoundEvents.IRON_DOOR_CLOSE,
         SoundEvents.IRON_DOOR_OPEN,
         SoundEvents.IRON_TRAPDOOR_CLOSE,
         SoundEvents.IRON_TRAPDOOR_OPEN,
         SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF,
         SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON,
         SoundEvents.STONE_BUTTON_CLICK_OFF,
         SoundEvents.STONE_BUTTON_CLICK_ON
      )
   );
   public static final BlockSetType OAK = register(new BlockSetType("oak"));
   public static final BlockSetType SPRUCE = register(new BlockSetType("spruce"));
   public static final BlockSetType BIRCH = register(new BlockSetType("birch"));
   public static final BlockSetType ACACIA = register(new BlockSetType("acacia"));
   public static final BlockSetType CHERRY = register(
      new BlockSetType(
         "cherry",
         true,
         true,
         true,
         BlockSetType.PressurePlateSensitivity.EVERYTHING,
         SoundType.CHERRY_WOOD,
         SoundEvents.CHERRY_WOOD_DOOR_CLOSE,
         SoundEvents.CHERRY_WOOD_DOOR_OPEN,
         SoundEvents.CHERRY_WOOD_TRAPDOOR_CLOSE,
         SoundEvents.CHERRY_WOOD_TRAPDOOR_OPEN,
         SoundEvents.CHERRY_WOOD_PRESSURE_PLATE_CLICK_OFF,
         SoundEvents.CHERRY_WOOD_PRESSURE_PLATE_CLICK_ON,
         SoundEvents.CHERRY_WOOD_BUTTON_CLICK_OFF,
         SoundEvents.CHERRY_WOOD_BUTTON_CLICK_ON
      )
   );
   public static final BlockSetType JUNGLE = register(new BlockSetType("jungle"));
   public static final BlockSetType DARK_OAK = register(new BlockSetType("dark_oak"));
   public static final BlockSetType CRIMSON = register(
      new BlockSetType(
         "crimson",
         true,
         true,
         true,
         BlockSetType.PressurePlateSensitivity.EVERYTHING,
         SoundType.NETHER_WOOD,
         SoundEvents.NETHER_WOOD_DOOR_CLOSE,
         SoundEvents.NETHER_WOOD_DOOR_OPEN,
         SoundEvents.NETHER_WOOD_TRAPDOOR_CLOSE,
         SoundEvents.NETHER_WOOD_TRAPDOOR_OPEN,
         SoundEvents.NETHER_WOOD_PRESSURE_PLATE_CLICK_OFF,
         SoundEvents.NETHER_WOOD_PRESSURE_PLATE_CLICK_ON,
         SoundEvents.NETHER_WOOD_BUTTON_CLICK_OFF,
         SoundEvents.NETHER_WOOD_BUTTON_CLICK_ON
      )
   );
   public static final BlockSetType WARPED = register(
      new BlockSetType(
         "warped",
         true,
         true,
         true,
         BlockSetType.PressurePlateSensitivity.EVERYTHING,
         SoundType.NETHER_WOOD,
         SoundEvents.NETHER_WOOD_DOOR_CLOSE,
         SoundEvents.NETHER_WOOD_DOOR_OPEN,
         SoundEvents.NETHER_WOOD_TRAPDOOR_CLOSE,
         SoundEvents.NETHER_WOOD_TRAPDOOR_OPEN,
         SoundEvents.NETHER_WOOD_PRESSURE_PLATE_CLICK_OFF,
         SoundEvents.NETHER_WOOD_PRESSURE_PLATE_CLICK_ON,
         SoundEvents.NETHER_WOOD_BUTTON_CLICK_OFF,
         SoundEvents.NETHER_WOOD_BUTTON_CLICK_ON
      )
   );
   public static final BlockSetType POTATO = register(
      new BlockSetType(
         "potato",
         true,
         true,
         true,
         BlockSetType.PressurePlateSensitivity.EVERYTHING,
         SoundType.NETHER_WOOD,
         SoundEvents.NETHER_WOOD_DOOR_CLOSE,
         SoundEvents.NETHER_WOOD_DOOR_OPEN,
         SoundEvents.NETHER_WOOD_TRAPDOOR_CLOSE,
         SoundEvents.NETHER_WOOD_TRAPDOOR_OPEN,
         SoundEvents.NETHER_WOOD_PRESSURE_PLATE_CLICK_OFF,
         SoundEvents.NETHER_WOOD_PRESSURE_PLATE_CLICK_ON,
         SoundEvents.NETHER_WOOD_BUTTON_CLICK_OFF,
         SoundEvents.NETHER_WOOD_BUTTON_CLICK_ON
      )
   );
   public static final BlockSetType MANGROVE = register(new BlockSetType("mangrove"));
   public static final BlockSetType BAMBOO = register(
      new BlockSetType(
         "bamboo",
         true,
         true,
         true,
         BlockSetType.PressurePlateSensitivity.EVERYTHING,
         SoundType.BAMBOO_WOOD,
         SoundEvents.BAMBOO_WOOD_DOOR_CLOSE,
         SoundEvents.BAMBOO_WOOD_DOOR_OPEN,
         SoundEvents.BAMBOO_WOOD_TRAPDOOR_CLOSE,
         SoundEvents.BAMBOO_WOOD_TRAPDOOR_OPEN,
         SoundEvents.BAMBOO_WOOD_PRESSURE_PLATE_CLICK_OFF,
         SoundEvents.BAMBOO_WOOD_PRESSURE_PLATE_CLICK_ON,
         SoundEvents.BAMBOO_WOOD_BUTTON_CLICK_OFF,
         SoundEvents.BAMBOO_WOOD_BUTTON_CLICK_ON
      )
   );

   public BlockSetType(String var1) {
      this(
         var1,
         true,
         true,
         true,
         BlockSetType.PressurePlateSensitivity.EVERYTHING,
         SoundType.WOOD,
         SoundEvents.WOODEN_DOOR_CLOSE,
         SoundEvents.WOODEN_DOOR_OPEN,
         SoundEvents.WOODEN_TRAPDOOR_CLOSE,
         SoundEvents.WOODEN_TRAPDOOR_OPEN,
         SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF,
         SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON,
         SoundEvents.WOODEN_BUTTON_CLICK_OFF,
         SoundEvents.WOODEN_BUTTON_CLICK_ON
      );
   }

   public BlockSetType(
      String var1,
      boolean var2,
      boolean var3,
      boolean var4,
      BlockSetType.PressurePlateSensitivity var5,
      SoundType var6,
      SoundEvent var7,
      SoundEvent var8,
      SoundEvent var9,
      SoundEvent var10,
      SoundEvent var11,
      SoundEvent var12,
      SoundEvent var13,
      SoundEvent var14
   ) {
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

   public static enum PressurePlateSensitivity {
      EVERYTHING,
      MOBS;

      private PressurePlateSensitivity() {
      }
   }
}
