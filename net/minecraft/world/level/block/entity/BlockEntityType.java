package net.minecraft.world.level.block.entity;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockEntityType {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final BlockEntityType FURNACE;
   public static final BlockEntityType CHEST;
   public static final BlockEntityType TRAPPED_CHEST;
   public static final BlockEntityType ENDER_CHEST;
   public static final BlockEntityType JUKEBOX;
   public static final BlockEntityType DISPENSER;
   public static final BlockEntityType DROPPER;
   public static final BlockEntityType SIGN;
   public static final BlockEntityType MOB_SPAWNER;
   public static final BlockEntityType PISTON;
   public static final BlockEntityType BREWING_STAND;
   public static final BlockEntityType ENCHANTING_TABLE;
   public static final BlockEntityType END_PORTAL;
   public static final BlockEntityType BEACON;
   public static final BlockEntityType SKULL;
   public static final BlockEntityType DAYLIGHT_DETECTOR;
   public static final BlockEntityType HOPPER;
   public static final BlockEntityType COMPARATOR;
   public static final BlockEntityType BANNER;
   public static final BlockEntityType STRUCTURE_BLOCK;
   public static final BlockEntityType END_GATEWAY;
   public static final BlockEntityType COMMAND_BLOCK;
   public static final BlockEntityType SHULKER_BOX;
   public static final BlockEntityType BED;
   public static final BlockEntityType CONDUIT;
   public static final BlockEntityType BARREL;
   public static final BlockEntityType SMOKER;
   public static final BlockEntityType BLAST_FURNACE;
   public static final BlockEntityType LECTERN;
   public static final BlockEntityType BELL;
   public static final BlockEntityType JIGSAW;
   public static final BlockEntityType CAMPFIRE;
   public static final BlockEntityType BEEHIVE;
   private final Supplier factory;
   private final Set validBlocks;
   private final Type dataType;

   @Nullable
   public static ResourceLocation getKey(BlockEntityType var0) {
      return Registry.BLOCK_ENTITY_TYPE.getKey(var0);
   }

   private static BlockEntityType register(String var0, BlockEntityType.Builder var1) {
      Type var2 = null;

      try {
         var2 = DataFixers.getDataFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().getWorldVersion())).getChoiceType(References.BLOCK_ENTITY, var0);
      } catch (IllegalArgumentException var4) {
         LOGGER.error("No data fixer registered for block entity {}", var0);
         if (SharedConstants.IS_RUNNING_IN_IDE) {
            throw var4;
         }
      }

      if (var1.validBlocks.isEmpty()) {
         LOGGER.warn("Block entity type {} requires at least one valid block to be defined!", var0);
      }

      return (BlockEntityType)Registry.register(Registry.BLOCK_ENTITY_TYPE, (String)var0, var1.build(var2));
   }

   public BlockEntityType(Supplier var1, Set var2, Type var3) {
      this.factory = var1;
      this.validBlocks = var2;
      this.dataType = var3;
   }

   @Nullable
   public BlockEntity create() {
      return (BlockEntity)this.factory.get();
   }

   public boolean isValid(Block var1) {
      return this.validBlocks.contains(var1);
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockGetter var1, BlockPos var2) {
      BlockEntity var3 = var1.getBlockEntity(var2);
      return var3 != null && var3.getType() == this ? var3 : null;
   }

   static {
      FURNACE = register("furnace", BlockEntityType.Builder.of(FurnaceBlockEntity::new, Blocks.FURNACE));
      CHEST = register("chest", BlockEntityType.Builder.of(ChestBlockEntity::new, Blocks.CHEST));
      TRAPPED_CHEST = register("trapped_chest", BlockEntityType.Builder.of(TrappedChestBlockEntity::new, Blocks.TRAPPED_CHEST));
      ENDER_CHEST = register("ender_chest", BlockEntityType.Builder.of(EnderChestBlockEntity::new, Blocks.ENDER_CHEST));
      JUKEBOX = register("jukebox", BlockEntityType.Builder.of(JukeboxBlockEntity::new, Blocks.JUKEBOX));
      DISPENSER = register("dispenser", BlockEntityType.Builder.of(DispenserBlockEntity::new, Blocks.DISPENSER));
      DROPPER = register("dropper", BlockEntityType.Builder.of(DropperBlockEntity::new, Blocks.DROPPER));
      SIGN = register("sign", BlockEntityType.Builder.of(SignBlockEntity::new, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN));
      MOB_SPAWNER = register("mob_spawner", BlockEntityType.Builder.of(SpawnerBlockEntity::new, Blocks.SPAWNER));
      PISTON = register("piston", BlockEntityType.Builder.of(PistonMovingBlockEntity::new, Blocks.MOVING_PISTON));
      BREWING_STAND = register("brewing_stand", BlockEntityType.Builder.of(BrewingStandBlockEntity::new, Blocks.BREWING_STAND));
      ENCHANTING_TABLE = register("enchanting_table", BlockEntityType.Builder.of(EnchantmentTableBlockEntity::new, Blocks.ENCHANTING_TABLE));
      END_PORTAL = register("end_portal", BlockEntityType.Builder.of(TheEndPortalBlockEntity::new, Blocks.END_PORTAL));
      BEACON = register("beacon", BlockEntityType.Builder.of(BeaconBlockEntity::new, Blocks.BEACON));
      SKULL = register("skull", BlockEntityType.Builder.of(SkullBlockEntity::new, Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL, Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD, Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD, Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD, Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD));
      DAYLIGHT_DETECTOR = register("daylight_detector", BlockEntityType.Builder.of(DaylightDetectorBlockEntity::new, Blocks.DAYLIGHT_DETECTOR));
      HOPPER = register("hopper", BlockEntityType.Builder.of(HopperBlockEntity::new, Blocks.HOPPER));
      COMPARATOR = register("comparator", BlockEntityType.Builder.of(ComparatorBlockEntity::new, Blocks.COMPARATOR));
      BANNER = register("banner", BlockEntityType.Builder.of(BannerBlockEntity::new, Blocks.WHITE_BANNER, Blocks.ORANGE_BANNER, Blocks.MAGENTA_BANNER, Blocks.LIGHT_BLUE_BANNER, Blocks.YELLOW_BANNER, Blocks.LIME_BANNER, Blocks.PINK_BANNER, Blocks.GRAY_BANNER, Blocks.LIGHT_GRAY_BANNER, Blocks.CYAN_BANNER, Blocks.PURPLE_BANNER, Blocks.BLUE_BANNER, Blocks.BROWN_BANNER, Blocks.GREEN_BANNER, Blocks.RED_BANNER, Blocks.BLACK_BANNER, Blocks.WHITE_WALL_BANNER, Blocks.ORANGE_WALL_BANNER, Blocks.MAGENTA_WALL_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, Blocks.YELLOW_WALL_BANNER, Blocks.LIME_WALL_BANNER, Blocks.PINK_WALL_BANNER, Blocks.GRAY_WALL_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, Blocks.CYAN_WALL_BANNER, Blocks.PURPLE_WALL_BANNER, Blocks.BLUE_WALL_BANNER, Blocks.BROWN_WALL_BANNER, Blocks.GREEN_WALL_BANNER, Blocks.RED_WALL_BANNER, Blocks.BLACK_WALL_BANNER));
      STRUCTURE_BLOCK = register("structure_block", BlockEntityType.Builder.of(StructureBlockEntity::new, Blocks.STRUCTURE_BLOCK));
      END_GATEWAY = register("end_gateway", BlockEntityType.Builder.of(TheEndGatewayBlockEntity::new, Blocks.END_GATEWAY));
      COMMAND_BLOCK = register("command_block", BlockEntityType.Builder.of(CommandBlockEntity::new, Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK));
      SHULKER_BOX = register("shulker_box", BlockEntityType.Builder.of(ShulkerBoxBlockEntity::new, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX));
      BED = register("bed", BlockEntityType.Builder.of(BedBlockEntity::new, Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED));
      CONDUIT = register("conduit", BlockEntityType.Builder.of(ConduitBlockEntity::new, Blocks.CONDUIT));
      BARREL = register("barrel", BlockEntityType.Builder.of(BarrelBlockEntity::new, Blocks.BARREL));
      SMOKER = register("smoker", BlockEntityType.Builder.of(SmokerBlockEntity::new, Blocks.SMOKER));
      BLAST_FURNACE = register("blast_furnace", BlockEntityType.Builder.of(BlastFurnaceBlockEntity::new, Blocks.BLAST_FURNACE));
      LECTERN = register("lectern", BlockEntityType.Builder.of(LecternBlockEntity::new, Blocks.LECTERN));
      BELL = register("bell", BlockEntityType.Builder.of(BellBlockEntity::new, Blocks.BELL));
      JIGSAW = register("jigsaw", BlockEntityType.Builder.of(JigsawBlockEntity::new, Blocks.JIGSAW));
      CAMPFIRE = register("campfire", BlockEntityType.Builder.of(CampfireBlockEntity::new, Blocks.CAMPFIRE));
      BEEHIVE = register("beehive", BlockEntityType.Builder.of(BeehiveBlockEntity::new, Blocks.BEE_NEST, Blocks.BEEHIVE));
   }

   public static final class Builder {
      private final Supplier factory;
      private final Set validBlocks;

      private Builder(Supplier var1, Set var2) {
         this.factory = var1;
         this.validBlocks = var2;
      }

      public static BlockEntityType.Builder of(Supplier var0, Block... var1) {
         return new BlockEntityType.Builder(var0, ImmutableSet.copyOf(var1));
      }

      public BlockEntityType build(Type var1) {
         return new BlockEntityType(this.factory, this.validBlocks, var1);
      }
   }
}
