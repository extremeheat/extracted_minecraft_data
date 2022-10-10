package net.minecraft.util.datafix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiFunction;
import net.minecraft.util.datafix.fixes.AddBedTileEntity;
import net.minecraft.util.datafix.fixes.AddNewChoices;
import net.minecraft.util.datafix.fixes.AdvancementsFix;
import net.minecraft.util.datafix.fixes.ArmorStandSilent;
import net.minecraft.util.datafix.fixes.BannerItemColor;
import net.minecraft.util.datafix.fixes.BedItemColor;
import net.minecraft.util.datafix.fixes.BiomeRenames;
import net.minecraft.util.datafix.fixes.BlockEntityBannerColor;
import net.minecraft.util.datafix.fixes.BlockEntityKeepPacked;
import net.minecraft.util.datafix.fixes.BlockNameFlattening;
import net.minecraft.util.datafix.fixes.BlockRename;
import net.minecraft.util.datafix.fixes.BlockStateFlattenGenOptions;
import net.minecraft.util.datafix.fixes.BlockStateFlattenStructures;
import net.minecraft.util.datafix.fixes.BlockStateFlattenVillageCrops;
import net.minecraft.util.datafix.fixes.BlockStateFlatternEntities;
import net.minecraft.util.datafix.fixes.BookPagesStrictJSON;
import net.minecraft.util.datafix.fixes.ChunkGenStatus;
import net.minecraft.util.datafix.fixes.ChunkPaletteFormat;
import net.minecraft.util.datafix.fixes.ChunkStructuresTemplateRenameFix;
import net.minecraft.util.datafix.fixes.ColorlessShulkerEntityFix;
import net.minecraft.util.datafix.fixes.CoralFansRenameList;
import net.minecraft.util.datafix.fixes.CustomNameStringToComponentEntity;
import net.minecraft.util.datafix.fixes.CustomNameStringToComponentFixTileEntity;
import net.minecraft.util.datafix.fixes.CustomNameStringToComponentItem;
import net.minecraft.util.datafix.fixes.ElderGuardianSplit;
import net.minecraft.util.datafix.fixes.EntityArmorAndHeld;
import net.minecraft.util.datafix.fixes.EntityCodSalmonFix;
import net.minecraft.util.datafix.fixes.EntityHealth;
import net.minecraft.util.datafix.fixes.EntityId;
import net.minecraft.util.datafix.fixes.EntityItemFrameFacing;
import net.minecraft.util.datafix.fixes.EntityRenameing1510;
import net.minecraft.util.datafix.fixes.ForceVBOOn;
import net.minecraft.util.datafix.fixes.HeightmapRenamingFix;
import net.minecraft.util.datafix.fixes.HorseSaddle;
import net.minecraft.util.datafix.fixes.HorseSplit;
import net.minecraft.util.datafix.fixes.IglooMetadataRemoval;
import net.minecraft.util.datafix.fixes.ItemFilledMapMetadata;
import net.minecraft.util.datafix.fixes.ItemIntIDToString;
import net.minecraft.util.datafix.fixes.ItemRename;
import net.minecraft.util.datafix.fixes.ItemSpawnEggSplit;
import net.minecraft.util.datafix.fixes.ItemStackDataFlattening;
import net.minecraft.util.datafix.fixes.ItemStackEnchantmentFix;
import net.minecraft.util.datafix.fixes.JukeboxRecordItem;
import net.minecraft.util.datafix.fixes.KeyOptionsTranslation;
import net.minecraft.util.datafix.fixes.LWJGL3KeyOptions;
import net.minecraft.util.datafix.fixes.LeavesFix;
import net.minecraft.util.datafix.fixes.LevelDataGeneratorOptionsFix;
import net.minecraft.util.datafix.fixes.MinecartEntityTypes;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.ObjectiveDisplayName;
import net.minecraft.util.datafix.fixes.ObjectiveRenderType;
import net.minecraft.util.datafix.fixes.OptionsLowerCaseLanguage;
import net.minecraft.util.datafix.fixes.PaintingDirection;
import net.minecraft.util.datafix.fixes.PaintingMotive;
import net.minecraft.util.datafix.fixes.PistonPushedBlock;
import net.minecraft.util.datafix.fixes.PotionItems;
import net.minecraft.util.datafix.fixes.PotionWater;
import net.minecraft.util.datafix.fixes.PufferfishRename;
import net.minecraft.util.datafix.fixes.RecipesRenaming;
import net.minecraft.util.datafix.fixes.RedundantChanceTags;
import net.minecraft.util.datafix.fixes.RenamedCoral;
import net.minecraft.util.datafix.fixes.RenamedRecipes;
import net.minecraft.util.datafix.fixes.RidingToPassengers;
import net.minecraft.util.datafix.fixes.ShulkerBoxEntityColor;
import net.minecraft.util.datafix.fixes.ShulkerBoxItemColor;
import net.minecraft.util.datafix.fixes.ShulkerBoxTileColor;
import net.minecraft.util.datafix.fixes.SignStrictJSON;
import net.minecraft.util.datafix.fixes.SkeletonSplit;
import net.minecraft.util.datafix.fixes.SpawnEggNames;
import net.minecraft.util.datafix.fixes.SpawnerEntityTypes;
import net.minecraft.util.datafix.fixes.StatsRenaming;
import net.minecraft.util.datafix.fixes.StringToUUID;
import net.minecraft.util.datafix.fixes.SwimStatsRename;
import net.minecraft.util.datafix.fixes.TeamDisplayName;
import net.minecraft.util.datafix.fixes.TileEntityId;
import net.minecraft.util.datafix.fixes.TippedArrow;
import net.minecraft.util.datafix.fixes.TrappedChestTileEntitySplit;
import net.minecraft.util.datafix.fixes.VillagerTrades;
import net.minecraft.util.datafix.fixes.WolfCollarColor;
import net.minecraft.util.datafix.fixes.ZombieProfToType;
import net.minecraft.util.datafix.fixes.ZombieSplit;
import net.minecraft.util.datafix.versions.V0099;
import net.minecraft.util.datafix.versions.V0100;
import net.minecraft.util.datafix.versions.V0102;
import net.minecraft.util.datafix.versions.V0106;
import net.minecraft.util.datafix.versions.V0107;
import net.minecraft.util.datafix.versions.V0135;
import net.minecraft.util.datafix.versions.V0143;
import net.minecraft.util.datafix.versions.V0501;
import net.minecraft.util.datafix.versions.V0700;
import net.minecraft.util.datafix.versions.V0701;
import net.minecraft.util.datafix.versions.V0702;
import net.minecraft.util.datafix.versions.V0703;
import net.minecraft.util.datafix.versions.V0704;
import net.minecraft.util.datafix.versions.V0705;
import net.minecraft.util.datafix.versions.V0808;
import net.minecraft.util.datafix.versions.V1022;
import net.minecraft.util.datafix.versions.V1125;
import net.minecraft.util.datafix.versions.V1451;
import net.minecraft.util.datafix.versions.V1451_1;
import net.minecraft.util.datafix.versions.V1451_2;
import net.minecraft.util.datafix.versions.V1451_3;
import net.minecraft.util.datafix.versions.V1451_4;
import net.minecraft.util.datafix.versions.V1451_5;
import net.minecraft.util.datafix.versions.V1451_6;
import net.minecraft.util.datafix.versions.V1451_7;
import net.minecraft.util.datafix.versions.V1460;
import net.minecraft.util.datafix.versions.V1466;
import net.minecraft.util.datafix.versions.V1470;
import net.minecraft.util.datafix.versions.V1481;
import net.minecraft.util.datafix.versions.V1483;
import net.minecraft.util.datafix.versions.V1486;
import net.minecraft.util.datafix.versions.V1510;

public class DataFixesManager {
   private static final BiFunction<Integer, Schema, Schema> field_207592_e = Schema::new;
   private static final BiFunction<Integer, Schema, Schema> field_207593_f = NamespacedSchema::new;
   private static final DataFixer field_210902_d = func_188279_a();

   private static DataFixer func_188279_a() {
      DataFixerBuilder var0 = new DataFixerBuilder(1631);
      func_210891_a(var0);
      return var0.build(ForkJoinPool.commonPool());
   }

   public static DataFixer func_210901_a() {
      return field_210902_d;
   }

   private static void func_210891_a(DataFixerBuilder var0) {
      Schema var1 = var0.addSchema(99, V0099::new);
      Schema var2 = var0.addSchema(100, V0100::new);
      var0.addFixer(new EntityArmorAndHeld(var2, true));
      Schema var3 = var0.addSchema(101, field_207592_e);
      var0.addFixer(new SignStrictJSON(var3, false));
      Schema var4 = var0.addSchema(102, V0102::new);
      var0.addFixer(new ItemIntIDToString(var4, true));
      var0.addFixer(new PotionItems(var4, false));
      Schema var5 = var0.addSchema(105, field_207592_e);
      var0.addFixer(new SpawnEggNames(var5, true));
      Schema var6 = var0.addSchema(106, V0106::new);
      var0.addFixer(new SpawnerEntityTypes(var6, true));
      Schema var7 = var0.addSchema(107, V0107::new);
      var0.addFixer(new MinecartEntityTypes(var7, true));
      Schema var8 = var0.addSchema(108, field_207592_e);
      var0.addFixer(new StringToUUID(var8, true));
      Schema var9 = var0.addSchema(109, field_207592_e);
      var0.addFixer(new EntityHealth(var9, true));
      Schema var10 = var0.addSchema(110, field_207592_e);
      var0.addFixer(new HorseSaddle(var10, true));
      Schema var11 = var0.addSchema(111, field_207592_e);
      var0.addFixer(new PaintingDirection(var11, true));
      Schema var12 = var0.addSchema(113, field_207592_e);
      var0.addFixer(new RedundantChanceTags(var12, true));
      Schema var13 = var0.addSchema(135, V0135::new);
      var0.addFixer(new RidingToPassengers(var13, true));
      Schema var14 = var0.addSchema(143, V0143::new);
      var0.addFixer(new TippedArrow(var14, true));
      Schema var15 = var0.addSchema(147, field_207592_e);
      var0.addFixer(new ArmorStandSilent(var15, true));
      Schema var16 = var0.addSchema(165, field_207592_e);
      var0.addFixer(new BookPagesStrictJSON(var16, true));
      Schema var17 = var0.addSchema(501, V0501::new);
      var0.addFixer(new AddNewChoices(var17, "Add 1.10 entities fix", TypeReferences.field_211299_o));
      Schema var18 = var0.addSchema(502, field_207592_e);
      var0.addFixer(ItemRename.func_207476_a(var18, "cooked_fished item renamer", (var0x) -> {
         return Objects.equals(NamespacedSchema.func_206477_f(var0x), "minecraft:cooked_fished") ? "minecraft:cooked_fish" : var0x;
      }));
      var0.addFixer(new ZombieProfToType(var18, false));
      Schema var19 = var0.addSchema(505, field_207592_e);
      var0.addFixer(new ForceVBOOn(var19, false));
      Schema var20 = var0.addSchema(700, V0700::new);
      var0.addFixer(new ElderGuardianSplit(var20, true));
      Schema var21 = var0.addSchema(701, V0701::new);
      var0.addFixer(new SkeletonSplit(var21, true));
      Schema var22 = var0.addSchema(702, V0702::new);
      var0.addFixer(new ZombieSplit(var22, true));
      Schema var23 = var0.addSchema(703, V0703::new);
      var0.addFixer(new HorseSplit(var23, true));
      Schema var24 = var0.addSchema(704, V0704::new);
      var0.addFixer(new TileEntityId(var24, true));
      Schema var25 = var0.addSchema(705, V0705::new);
      var0.addFixer(new EntityId(var25, true));
      Schema var26 = var0.addSchema(804, field_207593_f);
      var0.addFixer(new BannerItemColor(var26, true));
      Schema var27 = var0.addSchema(806, field_207593_f);
      var0.addFixer(new PotionWater(var27, false));
      Schema var28 = var0.addSchema(808, V0808::new);
      var0.addFixer(new AddNewChoices(var28, "added shulker box", TypeReferences.field_211294_j));
      Schema var29 = var0.addSchema(808, 1, field_207593_f);
      var0.addFixer(new ShulkerBoxEntityColor(var29, false));
      Schema var30 = var0.addSchema(813, field_207593_f);
      var0.addFixer(new ShulkerBoxItemColor(var30, false));
      var0.addFixer(new ShulkerBoxTileColor(var30, false));
      Schema var31 = var0.addSchema(816, field_207593_f);
      var0.addFixer(new OptionsLowerCaseLanguage(var31, false));
      Schema var32 = var0.addSchema(820, field_207593_f);
      var0.addFixer(ItemRename.func_207476_a(var32, "totem item renamer", (var0x) -> {
         return Objects.equals(var0x, "minecraft:totem") ? "minecraft:totem_of_undying" : var0x;
      }));
      Schema var33 = var0.addSchema(1022, V1022::new);
      var0.addFixer(new WriteAndReadDataFix(var33, "added shoulder entities to players", TypeReferences.field_211286_b));
      Schema var34 = var0.addSchema(1125, V1125::new);
      var0.addFixer(new AddBedTileEntity(var34, true));
      var0.addFixer(new BedItemColor(var34, false));
      Schema var35 = var0.addSchema(1344, field_207593_f);
      var0.addFixer(new LWJGL3KeyOptions(var35, false));
      Schema var36 = var0.addSchema(1446, field_207593_f);
      var0.addFixer(new KeyOptionsTranslation(var36, false));
      Schema var37 = var0.addSchema(1450, field_207593_f);
      var0.addFixer(new BlockStateFlattenStructures(var37, false));
      Schema var38 = var0.addSchema(1451, V1451::new);
      var0.addFixer(new AddNewChoices(var38, "AddTrappedChestFix", TypeReferences.field_211294_j));
      Schema var39 = var0.addSchema(1451, 1, V1451_1::new);
      var0.addFixer(new ChunkPaletteFormat(var39, true));
      Schema var40 = var0.addSchema(1451, 2, V1451_2::new);
      var0.addFixer(new PistonPushedBlock(var40, true));
      Schema var41 = var0.addSchema(1451, 3, V1451_3::new);
      var0.addFixer(new BlockStateFlatternEntities(var41, true));
      var0.addFixer(new ItemFilledMapMetadata(var41, false));
      Schema var42 = var0.addSchema(1451, 4, V1451_4::new);
      var0.addFixer(new BlockNameFlattening(var42, true));
      var0.addFixer(new ItemStackDataFlattening(var42, false));
      Schema var43 = var0.addSchema(1451, 5, V1451_5::new);
      var0.addFixer(new AddNewChoices(var43, "RemoveNoteBlockFlowerPotFix", TypeReferences.field_211294_j));
      var0.addFixer(new ItemSpawnEggSplit(var43, false));
      var0.addFixer(new WolfCollarColor(var43, false));
      var0.addFixer(new BlockEntityBannerColor(var43, false));
      var0.addFixer(new BlockStateFlattenGenOptions(var43, false));
      Schema var44 = var0.addSchema(1451, 6, V1451_6::new);
      var0.addFixer(new StatsRenaming(var44, true));
      var0.addFixer(new JukeboxRecordItem(var44, false));
      Schema var45 = var0.addSchema(1451, 7, V1451_7::new);
      var0.addFixer(new BlockStateFlattenVillageCrops(var45, true));
      Schema var46 = var0.addSchema(1451, 7, field_207593_f);
      var0.addFixer(new VillagerTrades(var46, false));
      Schema var47 = var0.addSchema(1456, field_207593_f);
      var0.addFixer(new EntityItemFrameFacing(var47, false));
      Schema var48 = var0.addSchema(1458, field_207593_f);
      var0.addFixer(new CustomNameStringToComponentEntity(var48, false));
      var0.addFixer(new CustomNameStringToComponentItem(var48, false));
      var0.addFixer(new CustomNameStringToComponentFixTileEntity(var48, false));
      Schema var49 = var0.addSchema(1460, V1460::new);
      var0.addFixer(new PaintingMotive(var49, false));
      Schema var50 = var0.addSchema(1466, V1466::new);
      var0.addFixer(new ChunkGenStatus(var50, true));
      Schema var51 = var0.addSchema(1470, V1470::new);
      var0.addFixer(new AddNewChoices(var51, "Add 1.13 entities fix", TypeReferences.field_211299_o));
      Schema var52 = var0.addSchema(1474, field_207593_f);
      var0.addFixer(new ColorlessShulkerEntityFix(var52, false));
      var0.addFixer(BlockRename.func_207437_a(var52, "Colorless shulker block fixer", (var0x) -> {
         return Objects.equals(NamespacedSchema.func_206477_f(var0x), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : var0x;
      }));
      var0.addFixer(ItemRename.func_207476_a(var52, "Colorless shulker item fixer", (var0x) -> {
         return Objects.equals(NamespacedSchema.func_206477_f(var0x), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : var0x;
      }));
      Schema var53 = var0.addSchema(1475, field_207593_f);
      var0.addFixer(BlockRename.func_207437_a(var53, "Flowing fixer", (var0x) -> {
         return (String)ImmutableMap.of("minecraft:flowing_water", "minecraft:water", "minecraft:flowing_lava", "minecraft:lava").getOrDefault(var0x, var0x);
      }));
      Schema var54 = var0.addSchema(1480, field_207593_f);
      var0.addFixer(BlockRename.func_207437_a(var54, "Rename coral blocks", (var0x) -> {
         return (String)RenamedCoral.field_204918_a.getOrDefault(var0x, var0x);
      }));
      var0.addFixer(ItemRename.func_207476_a(var54, "Rename coral items", (var0x) -> {
         return (String)RenamedCoral.field_204918_a.getOrDefault(var0x, var0x);
      }));
      Schema var55 = var0.addSchema(1481, V1481::new);
      var0.addFixer(new AddNewChoices(var55, "Add conduit", TypeReferences.field_211294_j));
      Schema var56 = var0.addSchema(1483, V1483::new);
      var0.addFixer(new PufferfishRename(var56, true));
      var0.addFixer(ItemRename.func_207476_a(var56, "Rename pufferfish egg item", (var0x) -> {
         return (String)PufferfishRename.field_207461_a.getOrDefault(var0x, var0x);
      }));
      Schema var57 = var0.addSchema(1484, field_207593_f);
      var0.addFixer(ItemRename.func_207476_a(var57, "Rename seagrass items", (var0x) -> {
         return (String)ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass").getOrDefault(var0x, var0x);
      }));
      var0.addFixer(BlockRename.func_207437_a(var57, "Rename seagrass blocks", (var0x) -> {
         return (String)ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass").getOrDefault(var0x, var0x);
      }));
      var0.addFixer(new HeightmapRenamingFix(var57, false));
      Schema var58 = var0.addSchema(1486, V1486::new);
      var0.addFixer(new EntityCodSalmonFix(var58, true));
      var0.addFixer(ItemRename.func_207476_a(var58, "Rename cod/salmon egg items", (var0x) -> {
         return (String)EntityCodSalmonFix.field_209759_b.getOrDefault(var0x, var0x);
      }));
      Schema var59 = var0.addSchema(1487, field_207593_f);
      var0.addFixer(ItemRename.func_207476_a(var59, "Rename prismarine_brick(s)_* blocks", (var0x) -> {
         return (String)ImmutableMap.of("minecraft:prismarine_bricks_slab", "minecraft:prismarine_brick_slab", "minecraft:prismarine_bricks_stairs", "minecraft:prismarine_brick_stairs").getOrDefault(var0x, var0x);
      }));
      var0.addFixer(BlockRename.func_207437_a(var59, "Rename prismarine_brick(s)_* items", (var0x) -> {
         return (String)ImmutableMap.of("minecraft:prismarine_bricks_slab", "minecraft:prismarine_brick_slab", "minecraft:prismarine_bricks_stairs", "minecraft:prismarine_brick_stairs").getOrDefault(var0x, var0x);
      }));
      Schema var60 = var0.addSchema(1488, field_207593_f);
      var0.addFixer(BlockRename.func_207437_a(var60, "Rename kelp/kelptop", (var0x) -> {
         return (String)ImmutableMap.of("minecraft:kelp_top", "minecraft:kelp", "minecraft:kelp", "minecraft:kelp_plant").getOrDefault(var0x, var0x);
      }));
      var0.addFixer(ItemRename.func_207476_a(var60, "Rename kelptop", (var0x) -> {
         return Objects.equals(var0x, "minecraft:kelp_top") ? "minecraft:kelp" : var0x;
      }));
      var0.addFixer(new NamedEntityFix(var60, false, "Command block block entity custom name fix", TypeReferences.field_211294_j, "minecraft:command_block") {
         protected Typed<?> func_207419_a(Typed<?> var1) {
            return var1.update(DSL.remainderFinder(), CustomNameStringToComponentEntity::func_209740_a);
         }
      });
      var0.addFixer(new NamedEntityFix(var60, false, "Command block minecart custom name fix", TypeReferences.field_211299_o, "minecraft:commandblock_minecart") {
         protected Typed<?> func_207419_a(Typed<?> var1) {
            return var1.update(DSL.remainderFinder(), CustomNameStringToComponentEntity::func_209740_a);
         }
      });
      var0.addFixer(new IglooMetadataRemoval(var60, false));
      Schema var61 = var0.addSchema(1490, field_207593_f);
      var0.addFixer(BlockRename.func_207437_a(var61, "Rename melon_block", (var0x) -> {
         return Objects.equals(var0x, "minecraft:melon_block") ? "minecraft:melon" : var0x;
      }));
      var0.addFixer(ItemRename.func_207476_a(var61, "Rename melon_block/melon/speckled_melon", (var0x) -> {
         return (String)ImmutableMap.of("minecraft:melon_block", "minecraft:melon", "minecraft:melon", "minecraft:melon_slice", "minecraft:speckled_melon", "minecraft:glistering_melon_slice").getOrDefault(var0x, var0x);
      }));
      Schema var62 = var0.addSchema(1492, field_207593_f);
      var0.addFixer(new ChunkStructuresTemplateRenameFix(var62, false));
      Schema var63 = var0.addSchema(1494, field_207593_f);
      var0.addFixer(new ItemStackEnchantmentFix(var63, false));
      Schema var64 = var0.addSchema(1496, field_207593_f);
      var0.addFixer(new LeavesFix(var64, false));
      Schema var65 = var0.addSchema(1500, field_207593_f);
      var0.addFixer(new BlockEntityKeepPacked(var65, false));
      Schema var66 = var0.addSchema(1501, field_207593_f);
      var0.addFixer(new AdvancementsFix(var66, false));
      Schema var67 = var0.addSchema(1502, field_207593_f);
      var0.addFixer(new RenamedRecipes(var67, false));
      Schema var68 = var0.addSchema(1506, field_207593_f);
      var0.addFixer(new LevelDataGeneratorOptionsFix(var68, false));
      Schema var69 = var0.addSchema(1508, field_207593_f);
      var0.addFixer(new BiomeRenames(var69, false));
      Schema var70 = var0.addSchema(1510, V1510::new);
      var0.addFixer(BlockRename.func_207437_a(var70, "Block renamening fix", (var0x) -> {
         return (String)EntityRenameing1510.field_210596_b.getOrDefault(var0x, var0x);
      }));
      var0.addFixer(ItemRename.func_207476_a(var70, "Item renamening fix", (var0x) -> {
         return (String)EntityRenameing1510.field_210597_c.getOrDefault(var0x, var0x);
      }));
      var0.addFixer(new RecipesRenaming(var70, false));
      var0.addFixer(new EntityRenameing1510(var70, true));
      var0.addFixer(new SwimStatsRename(var70, false));
      Schema var71 = var0.addSchema(1514, field_207593_f);
      var0.addFixer(new ObjectiveDisplayName(var71, false));
      var0.addFixer(new TeamDisplayName(var71, false));
      var0.addFixer(new ObjectiveRenderType(var71, false));
      Schema var72 = var0.addSchema(1515, field_207593_f);
      var0.addFixer(BlockRename.func_207437_a(var72, "Rename coral fan blocks", (var0x) -> {
         return (String)CoralFansRenameList.field_211870_a.getOrDefault(var0x, var0x);
      }));
      Schema var73 = var0.addSchema(1624, field_207593_f);
      var0.addFixer(new TrappedChestTileEntitySplit(var73, false));
   }
}
