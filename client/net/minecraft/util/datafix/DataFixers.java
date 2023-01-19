package net.minecraft.util.datafix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.util.datafix.fixes.AbstractArrowPickupFix;
import net.minecraft.util.datafix.fixes.AddFlagIfNotPresentFix;
import net.minecraft.util.datafix.fixes.AddNewChoices;
import net.minecraft.util.datafix.fixes.AdvancementsFix;
import net.minecraft.util.datafix.fixes.AdvancementsRenameFix;
import net.minecraft.util.datafix.fixes.AttributesRename;
import net.minecraft.util.datafix.fixes.BedItemColorFix;
import net.minecraft.util.datafix.fixes.BiomeFix;
import net.minecraft.util.datafix.fixes.BitStorageAlignFix;
import net.minecraft.util.datafix.fixes.BlendingDataFix;
import net.minecraft.util.datafix.fixes.BlockEntityBannerColorFix;
import net.minecraft.util.datafix.fixes.BlockEntityBlockStateFix;
import net.minecraft.util.datafix.fixes.BlockEntityCustomNameToComponentFix;
import net.minecraft.util.datafix.fixes.BlockEntityIdFix;
import net.minecraft.util.datafix.fixes.BlockEntityJukeboxFix;
import net.minecraft.util.datafix.fixes.BlockEntityKeepPacked;
import net.minecraft.util.datafix.fixes.BlockEntityShulkerBoxColorFix;
import net.minecraft.util.datafix.fixes.BlockEntitySignTextStrictJsonFix;
import net.minecraft.util.datafix.fixes.BlockEntityUUIDFix;
import net.minecraft.util.datafix.fixes.BlockNameFlatteningFix;
import net.minecraft.util.datafix.fixes.BlockRenameFix;
import net.minecraft.util.datafix.fixes.BlockRenameFixWithJigsaw;
import net.minecraft.util.datafix.fixes.BlockStateStructureTemplateFix;
import net.minecraft.util.datafix.fixes.CatTypeFix;
import net.minecraft.util.datafix.fixes.CauldronRenameFix;
import net.minecraft.util.datafix.fixes.CavesAndCliffsRenames;
import net.minecraft.util.datafix.fixes.ChunkBedBlockEntityInjecterFix;
import net.minecraft.util.datafix.fixes.ChunkBiomeFix;
import net.minecraft.util.datafix.fixes.ChunkDeleteIgnoredLightDataFix;
import net.minecraft.util.datafix.fixes.ChunkHeightAndBiomeFix;
import net.minecraft.util.datafix.fixes.ChunkLightRemoveFix;
import net.minecraft.util.datafix.fixes.ChunkPalettedStorageFix;
import net.minecraft.util.datafix.fixes.ChunkProtoTickListFix;
import net.minecraft.util.datafix.fixes.ChunkRenamesFix;
import net.minecraft.util.datafix.fixes.ChunkStatusFix;
import net.minecraft.util.datafix.fixes.ChunkStatusFix2;
import net.minecraft.util.datafix.fixes.ChunkStructuresTemplateRenameFix;
import net.minecraft.util.datafix.fixes.ChunkToProtochunkFix;
import net.minecraft.util.datafix.fixes.ColorlessShulkerEntityFix;
import net.minecraft.util.datafix.fixes.CriteriaRenameFix;
import net.minecraft.util.datafix.fixes.DyeItemRenameFix;
import net.minecraft.util.datafix.fixes.EntityArmorStandSilentFix;
import net.minecraft.util.datafix.fixes.EntityBlockStateFix;
import net.minecraft.util.datafix.fixes.EntityCatSplitFix;
import net.minecraft.util.datafix.fixes.EntityCodSalmonFix;
import net.minecraft.util.datafix.fixes.EntityCustomNameToComponentFix;
import net.minecraft.util.datafix.fixes.EntityElderGuardianSplitFix;
import net.minecraft.util.datafix.fixes.EntityEquipmentToArmorAndHandFix;
import net.minecraft.util.datafix.fixes.EntityGoatMissingStateFix;
import net.minecraft.util.datafix.fixes.EntityHealthFix;
import net.minecraft.util.datafix.fixes.EntityHorseSaddleFix;
import net.minecraft.util.datafix.fixes.EntityHorseSplitFix;
import net.minecraft.util.datafix.fixes.EntityIdFix;
import net.minecraft.util.datafix.fixes.EntityItemFrameDirectionFix;
import net.minecraft.util.datafix.fixes.EntityMinecartIdentifiersFix;
import net.minecraft.util.datafix.fixes.EntityPaintingFieldsRenameFix;
import net.minecraft.util.datafix.fixes.EntityPaintingItemFrameDirectionFix;
import net.minecraft.util.datafix.fixes.EntityPaintingMotiveFix;
import net.minecraft.util.datafix.fixes.EntityProjectileOwnerFix;
import net.minecraft.util.datafix.fixes.EntityPufferfishRenameFix;
import net.minecraft.util.datafix.fixes.EntityRavagerRenameFix;
import net.minecraft.util.datafix.fixes.EntityRedundantChanceTagsFix;
import net.minecraft.util.datafix.fixes.EntityRidingToPassengersFix;
import net.minecraft.util.datafix.fixes.EntityShulkerColorFix;
import net.minecraft.util.datafix.fixes.EntityShulkerRotationFix;
import net.minecraft.util.datafix.fixes.EntitySkeletonSplitFix;
import net.minecraft.util.datafix.fixes.EntityStringUuidFix;
import net.minecraft.util.datafix.fixes.EntityTheRenameningFix;
import net.minecraft.util.datafix.fixes.EntityTippedArrowFix;
import net.minecraft.util.datafix.fixes.EntityUUIDFix;
import net.minecraft.util.datafix.fixes.EntityVariantFix;
import net.minecraft.util.datafix.fixes.EntityWolfColorFix;
import net.minecraft.util.datafix.fixes.EntityZombieSplitFix;
import net.minecraft.util.datafix.fixes.EntityZombieVillagerTypeFix;
import net.minecraft.util.datafix.fixes.EntityZombifiedPiglinRenameFix;
import net.minecraft.util.datafix.fixes.FilteredBooksFix;
import net.minecraft.util.datafix.fixes.FilteredSignsFix;
import net.minecraft.util.datafix.fixes.ForcePoiRebuild;
import net.minecraft.util.datafix.fixes.FurnaceRecipeFix;
import net.minecraft.util.datafix.fixes.GoatHornIdFix;
import net.minecraft.util.datafix.fixes.GossipUUIDFix;
import net.minecraft.util.datafix.fixes.HeightmapRenamingFix;
import net.minecraft.util.datafix.fixes.IglooMetadataRemovalFix;
import net.minecraft.util.datafix.fixes.ItemBannerColorFix;
import net.minecraft.util.datafix.fixes.ItemCustomNameToComponentFix;
import net.minecraft.util.datafix.fixes.ItemIdFix;
import net.minecraft.util.datafix.fixes.ItemLoreFix;
import net.minecraft.util.datafix.fixes.ItemPotionFix;
import net.minecraft.util.datafix.fixes.ItemRenameFix;
import net.minecraft.util.datafix.fixes.ItemShulkerBoxColorFix;
import net.minecraft.util.datafix.fixes.ItemSpawnEggFix;
import net.minecraft.util.datafix.fixes.ItemStackEnchantmentNamesFix;
import net.minecraft.util.datafix.fixes.ItemStackMapIdFix;
import net.minecraft.util.datafix.fixes.ItemStackSpawnEggFix;
import net.minecraft.util.datafix.fixes.ItemStackTheFlatteningFix;
import net.minecraft.util.datafix.fixes.ItemStackUUIDFix;
import net.minecraft.util.datafix.fixes.ItemWaterPotionFix;
import net.minecraft.util.datafix.fixes.ItemWrittenBookPagesStrictJsonFix;
import net.minecraft.util.datafix.fixes.JigsawPropertiesFix;
import net.minecraft.util.datafix.fixes.JigsawRotationFix;
import net.minecraft.util.datafix.fixes.LeavesFix;
import net.minecraft.util.datafix.fixes.LevelDataGeneratorOptionsFix;
import net.minecraft.util.datafix.fixes.LevelFlatGeneratorInfoFix;
import net.minecraft.util.datafix.fixes.LevelUUIDFix;
import net.minecraft.util.datafix.fixes.MapIdFix;
import net.minecraft.util.datafix.fixes.MemoryExpiryDataFix;
import net.minecraft.util.datafix.fixes.MissingDimensionFix;
import net.minecraft.util.datafix.fixes.MobSpawnerEntityIdentifiersFix;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.NewVillageFix;
import net.minecraft.util.datafix.fixes.ObjectiveDisplayNameFix;
import net.minecraft.util.datafix.fixes.ObjectiveRenderTypeFix;
import net.minecraft.util.datafix.fixes.OminousBannerBlockEntityRenameFix;
import net.minecraft.util.datafix.fixes.OminousBannerRenameFix;
import net.minecraft.util.datafix.fixes.OptionsAddTextBackgroundFix;
import net.minecraft.util.datafix.fixes.OptionsForceVBOFix;
import net.minecraft.util.datafix.fixes.OptionsKeyLwjgl3Fix;
import net.minecraft.util.datafix.fixes.OptionsKeyTranslationFix;
import net.minecraft.util.datafix.fixes.OptionsLowerCaseLanguageFix;
import net.minecraft.util.datafix.fixes.OptionsRenameFieldFix;
import net.minecraft.util.datafix.fixes.OverreachingTickFix;
import net.minecraft.util.datafix.fixes.PlayerUUIDFix;
import net.minecraft.util.datafix.fixes.PoiTypeRemoveFix;
import net.minecraft.util.datafix.fixes.PoiTypeRenameFix;
import net.minecraft.util.datafix.fixes.RecipesFix;
import net.minecraft.util.datafix.fixes.RecipesRenameFix;
import net.minecraft.util.datafix.fixes.RecipesRenameningFix;
import net.minecraft.util.datafix.fixes.RedstoneWireConnectionsFix;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.fixes.RemoveGolemGossipFix;
import net.minecraft.util.datafix.fixes.RenameBiomesFix;
import net.minecraft.util.datafix.fixes.RenamedCoralFansFix;
import net.minecraft.util.datafix.fixes.RenamedCoralFix;
import net.minecraft.util.datafix.fixes.ReorganizePoi;
import net.minecraft.util.datafix.fixes.SavedDataFeaturePoolElementFix;
import net.minecraft.util.datafix.fixes.SavedDataUUIDFix;
import net.minecraft.util.datafix.fixes.SavedDataVillageCropFix;
import net.minecraft.util.datafix.fixes.SimpleRenameFix;
import net.minecraft.util.datafix.fixes.SpawnerDataFix;
import net.minecraft.util.datafix.fixes.StatsCounterFix;
import net.minecraft.util.datafix.fixes.StatsRenameFix;
import net.minecraft.util.datafix.fixes.StriderGravityFix;
import net.minecraft.util.datafix.fixes.StructureReferenceCountFix;
import net.minecraft.util.datafix.fixes.StructureSettingsFlattenFix;
import net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix;
import net.minecraft.util.datafix.fixes.TeamDisplayNameFix;
import net.minecraft.util.datafix.fixes.TrappedChestBlockEntityFix;
import net.minecraft.util.datafix.fixes.VariantRenameFix;
import net.minecraft.util.datafix.fixes.VillagerDataFix;
import net.minecraft.util.datafix.fixes.VillagerFollowRangeFix;
import net.minecraft.util.datafix.fixes.VillagerRebuildLevelAndXpFix;
import net.minecraft.util.datafix.fixes.VillagerTradeFix;
import net.minecraft.util.datafix.fixes.WallPropertyFix;
import net.minecraft.util.datafix.fixes.WeaponSmithChestLootTableFix;
import net.minecraft.util.datafix.fixes.WorldGenSettingsDisallowOldCustomWorldsFix;
import net.minecraft.util.datafix.fixes.WorldGenSettingsFix;
import net.minecraft.util.datafix.fixes.WorldGenSettingsHeightAndBiomeFix;
import net.minecraft.util.datafix.fixes.WriteAndReadFix;
import net.minecraft.util.datafix.fixes.ZombieVillagerRebuildXpFix;
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import net.minecraft.util.datafix.schemas.V100;
import net.minecraft.util.datafix.schemas.V102;
import net.minecraft.util.datafix.schemas.V1022;
import net.minecraft.util.datafix.schemas.V106;
import net.minecraft.util.datafix.schemas.V107;
import net.minecraft.util.datafix.schemas.V1125;
import net.minecraft.util.datafix.schemas.V135;
import net.minecraft.util.datafix.schemas.V143;
import net.minecraft.util.datafix.schemas.V1451;
import net.minecraft.util.datafix.schemas.V1451_1;
import net.minecraft.util.datafix.schemas.V1451_2;
import net.minecraft.util.datafix.schemas.V1451_3;
import net.minecraft.util.datafix.schemas.V1451_4;
import net.minecraft.util.datafix.schemas.V1451_5;
import net.minecraft.util.datafix.schemas.V1451_6;
import net.minecraft.util.datafix.schemas.V1451_7;
import net.minecraft.util.datafix.schemas.V1460;
import net.minecraft.util.datafix.schemas.V1466;
import net.minecraft.util.datafix.schemas.V1470;
import net.minecraft.util.datafix.schemas.V1481;
import net.minecraft.util.datafix.schemas.V1483;
import net.minecraft.util.datafix.schemas.V1486;
import net.minecraft.util.datafix.schemas.V1510;
import net.minecraft.util.datafix.schemas.V1800;
import net.minecraft.util.datafix.schemas.V1801;
import net.minecraft.util.datafix.schemas.V1904;
import net.minecraft.util.datafix.schemas.V1906;
import net.minecraft.util.datafix.schemas.V1909;
import net.minecraft.util.datafix.schemas.V1920;
import net.minecraft.util.datafix.schemas.V1928;
import net.minecraft.util.datafix.schemas.V1929;
import net.minecraft.util.datafix.schemas.V1931;
import net.minecraft.util.datafix.schemas.V2100;
import net.minecraft.util.datafix.schemas.V2501;
import net.minecraft.util.datafix.schemas.V2502;
import net.minecraft.util.datafix.schemas.V2505;
import net.minecraft.util.datafix.schemas.V2509;
import net.minecraft.util.datafix.schemas.V2519;
import net.minecraft.util.datafix.schemas.V2522;
import net.minecraft.util.datafix.schemas.V2551;
import net.minecraft.util.datafix.schemas.V2568;
import net.minecraft.util.datafix.schemas.V2571;
import net.minecraft.util.datafix.schemas.V2684;
import net.minecraft.util.datafix.schemas.V2686;
import net.minecraft.util.datafix.schemas.V2688;
import net.minecraft.util.datafix.schemas.V2704;
import net.minecraft.util.datafix.schemas.V2707;
import net.minecraft.util.datafix.schemas.V2831;
import net.minecraft.util.datafix.schemas.V2832;
import net.minecraft.util.datafix.schemas.V2842;
import net.minecraft.util.datafix.schemas.V3076;
import net.minecraft.util.datafix.schemas.V3078;
import net.minecraft.util.datafix.schemas.V3081;
import net.minecraft.util.datafix.schemas.V3082;
import net.minecraft.util.datafix.schemas.V3083;
import net.minecraft.util.datafix.schemas.V501;
import net.minecraft.util.datafix.schemas.V700;
import net.minecraft.util.datafix.schemas.V701;
import net.minecraft.util.datafix.schemas.V702;
import net.minecraft.util.datafix.schemas.V703;
import net.minecraft.util.datafix.schemas.V704;
import net.minecraft.util.datafix.schemas.V705;
import net.minecraft.util.datafix.schemas.V808;
import net.minecraft.util.datafix.schemas.V99;
import org.slf4j.Logger;

public class DataFixers {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final BiFunction<Integer, Schema, Schema> SAME = Schema::new;
   private static final BiFunction<Integer, Schema, Schema> SAME_NAMESPACED = NamespacedSchema::new;
   private static final DataFixer dataFixer = createFixerUpper();
   public static final int BLENDING_VERSION = 3088;

   private DataFixers() {
      super();
   }

   public static DataFixer getDataFixer() {
      return dataFixer;
   }

   private static synchronized DataFixer createFixerUpper() {
      DataFixerBuilder var0 = new DataFixerBuilder(SharedConstants.getCurrentVersion().getWorldVersion());
      addFixers(var0);

      boolean var1 = switch(SharedConstants.DATAFIXER_OPTIMIZATION_OPTION) {
         case UNINITIALIZED_OPTIMIZED -> true;
         case UNINITIALIZED_UNOPTIMIZED -> false;
         default -> throw new IllegalStateException("Already loaded");
      };
      SharedConstants.DATAFIXER_OPTIMIZATION_OPTION = var1
         ? DataFixerOptimizationOption.INITIALIZED_OPTIMIZED
         : DataFixerOptimizationOption.INITIALIZED_UNOPTIMIZED;
      LOGGER.info("Building {} datafixer", var1 ? "optimized" : "unoptimized");
      return var1 ? var0.buildOptimized(Util.bootstrapExecutor()) : var0.buildUnoptimized();
   }

   private static void addFixers(DataFixerBuilder var0) {
      Schema var1 = var0.addSchema(99, V99::new);
      Schema var2 = var0.addSchema(100, V100::new);
      var0.addFixer(new EntityEquipmentToArmorAndHandFix(var2, true));
      Schema var3 = var0.addSchema(101, SAME);
      var0.addFixer(new BlockEntitySignTextStrictJsonFix(var3, false));
      Schema var4 = var0.addSchema(102, V102::new);
      var0.addFixer(new ItemIdFix(var4, true));
      var0.addFixer(new ItemPotionFix(var4, false));
      Schema var5 = var0.addSchema(105, SAME);
      var0.addFixer(new ItemSpawnEggFix(var5, true));
      Schema var6 = var0.addSchema(106, V106::new);
      var0.addFixer(new MobSpawnerEntityIdentifiersFix(var6, true));
      Schema var7 = var0.addSchema(107, V107::new);
      var0.addFixer(new EntityMinecartIdentifiersFix(var7, true));
      Schema var8 = var0.addSchema(108, SAME);
      var0.addFixer(new EntityStringUuidFix(var8, true));
      Schema var9 = var0.addSchema(109, SAME);
      var0.addFixer(new EntityHealthFix(var9, true));
      Schema var10 = var0.addSchema(110, SAME);
      var0.addFixer(new EntityHorseSaddleFix(var10, true));
      Schema var11 = var0.addSchema(111, SAME);
      var0.addFixer(new EntityPaintingItemFrameDirectionFix(var11, true));
      Schema var12 = var0.addSchema(113, SAME);
      var0.addFixer(new EntityRedundantChanceTagsFix(var12, true));
      Schema var13 = var0.addSchema(135, V135::new);
      var0.addFixer(new EntityRidingToPassengersFix(var13, true));
      Schema var14 = var0.addSchema(143, V143::new);
      var0.addFixer(new EntityTippedArrowFix(var14, true));
      Schema var15 = var0.addSchema(147, SAME);
      var0.addFixer(new EntityArmorStandSilentFix(var15, true));
      Schema var16 = var0.addSchema(165, SAME);
      var0.addFixer(new ItemWrittenBookPagesStrictJsonFix(var16, true));
      Schema var17 = var0.addSchema(501, V501::new);
      var0.addFixer(new AddNewChoices(var17, "Add 1.10 entities fix", References.ENTITY));
      Schema var18 = var0.addSchema(502, SAME);
      var0.addFixer(
         ItemRenameFix.create(
            var18,
            "cooked_fished item renamer",
            var0x -> Objects.equals(NamespacedSchema.ensureNamespaced(var0x), "minecraft:cooked_fished") ? "minecraft:cooked_fish" : var0x
         )
      );
      var0.addFixer(new EntityZombieVillagerTypeFix(var18, false));
      Schema var19 = var0.addSchema(505, SAME);
      var0.addFixer(new OptionsForceVBOFix(var19, false));
      Schema var20 = var0.addSchema(700, V700::new);
      var0.addFixer(new EntityElderGuardianSplitFix(var20, true));
      Schema var21 = var0.addSchema(701, V701::new);
      var0.addFixer(new EntitySkeletonSplitFix(var21, true));
      Schema var22 = var0.addSchema(702, V702::new);
      var0.addFixer(new EntityZombieSplitFix(var22, true));
      Schema var23 = var0.addSchema(703, V703::new);
      var0.addFixer(new EntityHorseSplitFix(var23, true));
      Schema var24 = var0.addSchema(704, V704::new);
      var0.addFixer(new BlockEntityIdFix(var24, true));
      Schema var25 = var0.addSchema(705, V705::new);
      var0.addFixer(new EntityIdFix(var25, true));
      Schema var26 = var0.addSchema(804, SAME_NAMESPACED);
      var0.addFixer(new ItemBannerColorFix(var26, true));
      Schema var27 = var0.addSchema(806, SAME_NAMESPACED);
      var0.addFixer(new ItemWaterPotionFix(var27, false));
      Schema var28 = var0.addSchema(808, V808::new);
      var0.addFixer(new AddNewChoices(var28, "added shulker box", References.BLOCK_ENTITY));
      Schema var29 = var0.addSchema(808, 1, SAME_NAMESPACED);
      var0.addFixer(new EntityShulkerColorFix(var29, false));
      Schema var30 = var0.addSchema(813, SAME_NAMESPACED);
      var0.addFixer(new ItemShulkerBoxColorFix(var30, false));
      var0.addFixer(new BlockEntityShulkerBoxColorFix(var30, false));
      Schema var31 = var0.addSchema(816, SAME_NAMESPACED);
      var0.addFixer(new OptionsLowerCaseLanguageFix(var31, false));
      Schema var32 = var0.addSchema(820, SAME_NAMESPACED);
      var0.addFixer(ItemRenameFix.create(var32, "totem item renamer", createRenamer("minecraft:totem", "minecraft:totem_of_undying")));
      Schema var33 = var0.addSchema(1022, V1022::new);
      var0.addFixer(new WriteAndReadFix(var33, "added shoulder entities to players", References.PLAYER));
      Schema var34 = var0.addSchema(1125, V1125::new);
      var0.addFixer(new ChunkBedBlockEntityInjecterFix(var34, true));
      var0.addFixer(new BedItemColorFix(var34, false));
      Schema var35 = var0.addSchema(1344, SAME_NAMESPACED);
      var0.addFixer(new OptionsKeyLwjgl3Fix(var35, false));
      Schema var36 = var0.addSchema(1446, SAME_NAMESPACED);
      var0.addFixer(new OptionsKeyTranslationFix(var36, false));
      Schema var37 = var0.addSchema(1450, SAME_NAMESPACED);
      var0.addFixer(new BlockStateStructureTemplateFix(var37, false));
      Schema var38 = var0.addSchema(1451, V1451::new);
      var0.addFixer(new AddNewChoices(var38, "AddTrappedChestFix", References.BLOCK_ENTITY));
      Schema var39 = var0.addSchema(1451, 1, V1451_1::new);
      var0.addFixer(new ChunkPalettedStorageFix(var39, true));
      Schema var40 = var0.addSchema(1451, 2, V1451_2::new);
      var0.addFixer(new BlockEntityBlockStateFix(var40, true));
      Schema var41 = var0.addSchema(1451, 3, V1451_3::new);
      var0.addFixer(new EntityBlockStateFix(var41, true));
      var0.addFixer(new ItemStackMapIdFix(var41, false));
      Schema var42 = var0.addSchema(1451, 4, V1451_4::new);
      var0.addFixer(new BlockNameFlatteningFix(var42, true));
      var0.addFixer(new ItemStackTheFlatteningFix(var42, false));
      Schema var43 = var0.addSchema(1451, 5, V1451_5::new);
      var0.addFixer(new AddNewChoices(var43, "RemoveNoteBlockFlowerPotFix", References.BLOCK_ENTITY));
      var0.addFixer(new ItemStackSpawnEggFix(var43, false));
      var0.addFixer(new EntityWolfColorFix(var43, false));
      var0.addFixer(new BlockEntityBannerColorFix(var43, false));
      var0.addFixer(new LevelFlatGeneratorInfoFix(var43, false));
      Schema var44 = var0.addSchema(1451, 6, V1451_6::new);
      var0.addFixer(new StatsCounterFix(var44, true));
      var0.addFixer(new WriteAndReadFix(var44, "Rewrite objectives", References.OBJECTIVE));
      var0.addFixer(new BlockEntityJukeboxFix(var44, false));
      Schema var45 = var0.addSchema(1451, 7, V1451_7::new);
      var0.addFixer(new SavedDataVillageCropFix(var45, true));
      Schema var46 = var0.addSchema(1451, 7, SAME_NAMESPACED);
      var0.addFixer(new VillagerTradeFix(var46, false));
      Schema var47 = var0.addSchema(1456, SAME_NAMESPACED);
      var0.addFixer(new EntityItemFrameDirectionFix(var47, false));
      Schema var48 = var0.addSchema(1458, SAME_NAMESPACED);
      var0.addFixer(new EntityCustomNameToComponentFix(var48, false));
      var0.addFixer(new ItemCustomNameToComponentFix(var48, false));
      var0.addFixer(new BlockEntityCustomNameToComponentFix(var48, false));
      Schema var49 = var0.addSchema(1460, V1460::new);
      var0.addFixer(new EntityPaintingMotiveFix(var49, false));
      Schema var50 = var0.addSchema(1466, V1466::new);
      var0.addFixer(new ChunkToProtochunkFix(var50, true));
      Schema var51 = var0.addSchema(1470, V1470::new);
      var0.addFixer(new AddNewChoices(var51, "Add 1.13 entities fix", References.ENTITY));
      Schema var52 = var0.addSchema(1474, SAME_NAMESPACED);
      var0.addFixer(new ColorlessShulkerEntityFix(var52, false));
      var0.addFixer(
         BlockRenameFix.create(
            var52,
            "Colorless shulker block fixer",
            var0x -> Objects.equals(NamespacedSchema.ensureNamespaced(var0x), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : var0x
         )
      );
      var0.addFixer(
         ItemRenameFix.create(
            var52,
            "Colorless shulker item fixer",
            var0x -> Objects.equals(NamespacedSchema.ensureNamespaced(var0x), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : var0x
         )
      );
      Schema var53 = var0.addSchema(1475, SAME_NAMESPACED);
      var0.addFixer(
         BlockRenameFix.create(
            var53, "Flowing fixer", createRenamer(ImmutableMap.of("minecraft:flowing_water", "minecraft:water", "minecraft:flowing_lava", "minecraft:lava"))
         )
      );
      Schema var54 = var0.addSchema(1480, SAME_NAMESPACED);
      var0.addFixer(BlockRenameFix.create(var54, "Rename coral blocks", createRenamer(RenamedCoralFix.RENAMED_IDS)));
      var0.addFixer(ItemRenameFix.create(var54, "Rename coral items", createRenamer(RenamedCoralFix.RENAMED_IDS)));
      Schema var55 = var0.addSchema(1481, V1481::new);
      var0.addFixer(new AddNewChoices(var55, "Add conduit", References.BLOCK_ENTITY));
      Schema var56 = var0.addSchema(1483, V1483::new);
      var0.addFixer(new EntityPufferfishRenameFix(var56, true));
      var0.addFixer(ItemRenameFix.create(var56, "Rename pufferfish egg item", createRenamer(EntityPufferfishRenameFix.RENAMED_IDS)));
      Schema var57 = var0.addSchema(1484, SAME_NAMESPACED);
      var0.addFixer(
         ItemRenameFix.create(
            var57,
            "Rename seagrass items",
            createRenamer(ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass"))
         )
      );
      var0.addFixer(
         BlockRenameFix.create(
            var57,
            "Rename seagrass blocks",
            createRenamer(ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass"))
         )
      );
      var0.addFixer(new HeightmapRenamingFix(var57, false));
      Schema var58 = var0.addSchema(1486, V1486::new);
      var0.addFixer(new EntityCodSalmonFix(var58, true));
      var0.addFixer(ItemRenameFix.create(var58, "Rename cod/salmon egg items", createRenamer(EntityCodSalmonFix.RENAMED_EGG_IDS)));
      Schema var59 = var0.addSchema(1487, SAME_NAMESPACED);
      var0.addFixer(
         ItemRenameFix.create(
            var59,
            "Rename prismarine_brick(s)_* blocks",
            createRenamer(
               ImmutableMap.of(
                  "minecraft:prismarine_bricks_slab",
                  "minecraft:prismarine_brick_slab",
                  "minecraft:prismarine_bricks_stairs",
                  "minecraft:prismarine_brick_stairs"
               )
            )
         )
      );
      var0.addFixer(
         BlockRenameFix.create(
            var59,
            "Rename prismarine_brick(s)_* items",
            createRenamer(
               ImmutableMap.of(
                  "minecraft:prismarine_bricks_slab",
                  "minecraft:prismarine_brick_slab",
                  "minecraft:prismarine_bricks_stairs",
                  "minecraft:prismarine_brick_stairs"
               )
            )
         )
      );
      Schema var60 = var0.addSchema(1488, SAME_NAMESPACED);
      var0.addFixer(
         BlockRenameFix.create(
            var60, "Rename kelp/kelptop", createRenamer(ImmutableMap.of("minecraft:kelp_top", "minecraft:kelp", "minecraft:kelp", "minecraft:kelp_plant"))
         )
      );
      var0.addFixer(ItemRenameFix.create(var60, "Rename kelptop", createRenamer("minecraft:kelp_top", "minecraft:kelp")));
      var0.addFixer(new NamedEntityFix(var60, false, "Command block block entity custom name fix", References.BLOCK_ENTITY, "minecraft:command_block") {
         @Override
         protected Typed<?> fix(Typed<?> var1) {
            return var1.update(DSL.remainderFinder(), EntityCustomNameToComponentFix::fixTagCustomName);
         }
      });
      var0.addFixer(new NamedEntityFix(var60, false, "Command block minecart custom name fix", References.ENTITY, "minecraft:commandblock_minecart") {
         @Override
         protected Typed<?> fix(Typed<?> var1) {
            return var1.update(DSL.remainderFinder(), EntityCustomNameToComponentFix::fixTagCustomName);
         }
      });
      var0.addFixer(new IglooMetadataRemovalFix(var60, false));
      Schema var61 = var0.addSchema(1490, SAME_NAMESPACED);
      var0.addFixer(BlockRenameFix.create(var61, "Rename melon_block", createRenamer("minecraft:melon_block", "minecraft:melon")));
      var0.addFixer(
         ItemRenameFix.create(
            var61,
            "Rename melon_block/melon/speckled_melon",
            createRenamer(
               ImmutableMap.of(
                  "minecraft:melon_block",
                  "minecraft:melon",
                  "minecraft:melon",
                  "minecraft:melon_slice",
                  "minecraft:speckled_melon",
                  "minecraft:glistering_melon_slice"
               )
            )
         )
      );
      Schema var62 = var0.addSchema(1492, SAME_NAMESPACED);
      var0.addFixer(new ChunkStructuresTemplateRenameFix(var62, false));
      Schema var63 = var0.addSchema(1494, SAME_NAMESPACED);
      var0.addFixer(new ItemStackEnchantmentNamesFix(var63, false));
      Schema var64 = var0.addSchema(1496, SAME_NAMESPACED);
      var0.addFixer(new LeavesFix(var64, false));
      Schema var65 = var0.addSchema(1500, SAME_NAMESPACED);
      var0.addFixer(new BlockEntityKeepPacked(var65, false));
      Schema var66 = var0.addSchema(1501, SAME_NAMESPACED);
      var0.addFixer(new AdvancementsFix(var66, false));
      Schema var67 = var0.addSchema(1502, SAME_NAMESPACED);
      var0.addFixer(new RecipesFix(var67, false));
      Schema var68 = var0.addSchema(1506, SAME_NAMESPACED);
      var0.addFixer(new LevelDataGeneratorOptionsFix(var68, false));
      Schema var69 = var0.addSchema(1510, V1510::new);
      var0.addFixer(BlockRenameFix.create(var69, "Block renamening fix", createRenamer(EntityTheRenameningFix.RENAMED_BLOCKS)));
      var0.addFixer(ItemRenameFix.create(var69, "Item renamening fix", createRenamer(EntityTheRenameningFix.RENAMED_ITEMS)));
      var0.addFixer(new RecipesRenameningFix(var69, false));
      var0.addFixer(new EntityTheRenameningFix(var69, true));
      var0.addFixer(
         new StatsRenameFix(
            var69,
            "SwimStatsRenameFix",
            ImmutableMap.of("minecraft:swim_one_cm", "minecraft:walk_on_water_one_cm", "minecraft:dive_one_cm", "minecraft:walk_under_water_one_cm")
         )
      );
      Schema var70 = var0.addSchema(1514, SAME_NAMESPACED);
      var0.addFixer(new ObjectiveDisplayNameFix(var70, false));
      var0.addFixer(new TeamDisplayNameFix(var70, false));
      var0.addFixer(new ObjectiveRenderTypeFix(var70, false));
      Schema var71 = var0.addSchema(1515, SAME_NAMESPACED);
      var0.addFixer(BlockRenameFix.create(var71, "Rename coral fan blocks", createRenamer(RenamedCoralFansFix.RENAMED_IDS)));
      Schema var72 = var0.addSchema(1624, SAME_NAMESPACED);
      var0.addFixer(new TrappedChestBlockEntityFix(var72, false));
      Schema var73 = var0.addSchema(1800, V1800::new);
      var0.addFixer(new AddNewChoices(var73, "Added 1.14 mobs fix", References.ENTITY));
      var0.addFixer(ItemRenameFix.create(var73, "Rename dye items", createRenamer(DyeItemRenameFix.RENAMED_IDS)));
      Schema var74 = var0.addSchema(1801, V1801::new);
      var0.addFixer(new AddNewChoices(var74, "Added Illager Beast", References.ENTITY));
      Schema var75 = var0.addSchema(1802, SAME_NAMESPACED);
      var0.addFixer(
         BlockRenameFix.create(
            var75,
            "Rename sign blocks & stone slabs",
            createRenamer(
               ImmutableMap.of(
                  "minecraft:stone_slab",
                  "minecraft:smooth_stone_slab",
                  "minecraft:sign",
                  "minecraft:oak_sign",
                  "minecraft:wall_sign",
                  "minecraft:oak_wall_sign"
               )
            )
         )
      );
      var0.addFixer(
         ItemRenameFix.create(
            var75,
            "Rename sign item & stone slabs",
            createRenamer(ImmutableMap.of("minecraft:stone_slab", "minecraft:smooth_stone_slab", "minecraft:sign", "minecraft:oak_sign"))
         )
      );
      Schema var76 = var0.addSchema(1803, SAME_NAMESPACED);
      var0.addFixer(new ItemLoreFix(var76, false));
      Schema var77 = var0.addSchema(1904, V1904::new);
      var0.addFixer(new AddNewChoices(var77, "Added Cats", References.ENTITY));
      var0.addFixer(new EntityCatSplitFix(var77, false));
      Schema var78 = var0.addSchema(1905, SAME_NAMESPACED);
      var0.addFixer(new ChunkStatusFix(var78, false));
      Schema var79 = var0.addSchema(1906, V1906::new);
      var0.addFixer(new AddNewChoices(var79, "Add POI Blocks", References.BLOCK_ENTITY));
      Schema var80 = var0.addSchema(1909, V1909::new);
      var0.addFixer(new AddNewChoices(var80, "Add jigsaw", References.BLOCK_ENTITY));
      Schema var81 = var0.addSchema(1911, SAME_NAMESPACED);
      var0.addFixer(new ChunkStatusFix2(var81, false));
      Schema var82 = var0.addSchema(1914, SAME_NAMESPACED);
      var0.addFixer(new WeaponSmithChestLootTableFix(var82, false));
      Schema var83 = var0.addSchema(1917, SAME_NAMESPACED);
      var0.addFixer(new CatTypeFix(var83, false));
      Schema var84 = var0.addSchema(1918, SAME_NAMESPACED);
      var0.addFixer(new VillagerDataFix(var84, "minecraft:villager"));
      var0.addFixer(new VillagerDataFix(var84, "minecraft:zombie_villager"));
      Schema var85 = var0.addSchema(1920, V1920::new);
      var0.addFixer(new NewVillageFix(var85, false));
      var0.addFixer(new AddNewChoices(var85, "Add campfire", References.BLOCK_ENTITY));
      Schema var86 = var0.addSchema(1925, SAME_NAMESPACED);
      var0.addFixer(new MapIdFix(var86, false));
      Schema var87 = var0.addSchema(1928, V1928::new);
      var0.addFixer(new EntityRavagerRenameFix(var87, true));
      var0.addFixer(ItemRenameFix.create(var87, "Rename ravager egg item", createRenamer(EntityRavagerRenameFix.RENAMED_IDS)));
      Schema var88 = var0.addSchema(1929, V1929::new);
      var0.addFixer(new AddNewChoices(var88, "Add Wandering Trader and Trader Llama", References.ENTITY));
      Schema var89 = var0.addSchema(1931, V1931::new);
      var0.addFixer(new AddNewChoices(var89, "Added Fox", References.ENTITY));
      Schema var90 = var0.addSchema(1936, SAME_NAMESPACED);
      var0.addFixer(new OptionsAddTextBackgroundFix(var90, false));
      Schema var91 = var0.addSchema(1946, SAME_NAMESPACED);
      var0.addFixer(new ReorganizePoi(var91, false));
      Schema var92 = var0.addSchema(1948, SAME_NAMESPACED);
      var0.addFixer(new OminousBannerRenameFix(var92));
      Schema var93 = var0.addSchema(1953, SAME_NAMESPACED);
      var0.addFixer(new OminousBannerBlockEntityRenameFix(var93, false));
      Schema var94 = var0.addSchema(1955, SAME_NAMESPACED);
      var0.addFixer(new VillagerRebuildLevelAndXpFix(var94, false));
      var0.addFixer(new ZombieVillagerRebuildXpFix(var94, false));
      Schema var95 = var0.addSchema(1961, SAME_NAMESPACED);
      var0.addFixer(new ChunkLightRemoveFix(var95, false));
      Schema var96 = var0.addSchema(1963, SAME_NAMESPACED);
      var0.addFixer(new RemoveGolemGossipFix(var96, false));
      Schema var97 = var0.addSchema(2100, V2100::new);
      var0.addFixer(new AddNewChoices(var97, "Added Bee and Bee Stinger", References.ENTITY));
      var0.addFixer(new AddNewChoices(var97, "Add beehive", References.BLOCK_ENTITY));
      var0.addFixer(new RecipesRenameFix(var97, false, "Rename sugar recipe", createRenamer("minecraft:sugar", "sugar_from_sugar_cane")));
      var0.addFixer(
         new AdvancementsRenameFix(
            var97, false, "Rename sugar recipe advancement", createRenamer("minecraft:recipes/misc/sugar", "minecraft:recipes/misc/sugar_from_sugar_cane")
         )
      );
      Schema var98 = var0.addSchema(2202, SAME_NAMESPACED);
      var0.addFixer(new ChunkBiomeFix(var98, false));
      Schema var99 = var0.addSchema(2209, SAME_NAMESPACED);
      UnaryOperator var100 = createRenamer("minecraft:bee_hive", "minecraft:beehive");
      var0.addFixer(ItemRenameFix.create(var99, "Rename bee_hive item to beehive", var100));
      var0.addFixer(new PoiTypeRenameFix(var99, "Rename bee_hive poi to beehive", var100));
      var0.addFixer(BlockRenameFix.create(var99, "Rename bee_hive block to beehive", var100));
      Schema var101 = var0.addSchema(2211, SAME_NAMESPACED);
      var0.addFixer(new StructureReferenceCountFix(var101, false));
      Schema var102 = var0.addSchema(2218, SAME_NAMESPACED);
      var0.addFixer(new ForcePoiRebuild(var102, false));
      Schema var103 = var0.addSchema(2501, V2501::new);
      var0.addFixer(new FurnaceRecipeFix(var103, true));
      Schema var104 = var0.addSchema(2502, V2502::new);
      var0.addFixer(new AddNewChoices(var104, "Added Hoglin", References.ENTITY));
      Schema var105 = var0.addSchema(2503, SAME_NAMESPACED);
      var0.addFixer(new WallPropertyFix(var105, false));
      var0.addFixer(
         new AdvancementsRenameFix(
            var105, false, "Composter category change", createRenamer("minecraft:recipes/misc/composter", "minecraft:recipes/decorations/composter")
         )
      );
      Schema var106 = var0.addSchema(2505, V2505::new);
      var0.addFixer(new AddNewChoices(var106, "Added Piglin", References.ENTITY));
      var0.addFixer(new MemoryExpiryDataFix(var106, "minecraft:villager"));
      Schema var107 = var0.addSchema(2508, SAME_NAMESPACED);
      var0.addFixer(
         ItemRenameFix.create(
            var107,
            "Renamed fungi items to fungus",
            createRenamer(ImmutableMap.of("minecraft:warped_fungi", "minecraft:warped_fungus", "minecraft:crimson_fungi", "minecraft:crimson_fungus"))
         )
      );
      var0.addFixer(
         BlockRenameFix.create(
            var107,
            "Renamed fungi blocks to fungus",
            createRenamer(ImmutableMap.of("minecraft:warped_fungi", "minecraft:warped_fungus", "minecraft:crimson_fungi", "minecraft:crimson_fungus"))
         )
      );
      Schema var108 = var0.addSchema(2509, V2509::new);
      var0.addFixer(new EntityZombifiedPiglinRenameFix(var108));
      var0.addFixer(ItemRenameFix.create(var108, "Rename zombie pigman egg item", createRenamer(EntityZombifiedPiglinRenameFix.RENAMED_IDS)));
      Schema var109 = var0.addSchema(2511, SAME_NAMESPACED);
      var0.addFixer(new EntityProjectileOwnerFix(var109));
      Schema var110 = var0.addSchema(2514, SAME_NAMESPACED);
      var0.addFixer(new EntityUUIDFix(var110));
      var0.addFixer(new BlockEntityUUIDFix(var110));
      var0.addFixer(new PlayerUUIDFix(var110));
      var0.addFixer(new LevelUUIDFix(var110));
      var0.addFixer(new SavedDataUUIDFix(var110));
      var0.addFixer(new ItemStackUUIDFix(var110));
      Schema var111 = var0.addSchema(2516, SAME_NAMESPACED);
      var0.addFixer(new GossipUUIDFix(var111, "minecraft:villager"));
      var0.addFixer(new GossipUUIDFix(var111, "minecraft:zombie_villager"));
      Schema var112 = var0.addSchema(2518, SAME_NAMESPACED);
      var0.addFixer(new JigsawPropertiesFix(var112, false));
      var0.addFixer(new JigsawRotationFix(var112, false));
      Schema var113 = var0.addSchema(2519, V2519::new);
      var0.addFixer(new AddNewChoices(var113, "Added Strider", References.ENTITY));
      Schema var114 = var0.addSchema(2522, V2522::new);
      var0.addFixer(new AddNewChoices(var114, "Added Zoglin", References.ENTITY));
      Schema var115 = var0.addSchema(2523, SAME_NAMESPACED);
      var0.addFixer(new AttributesRename(var115));
      Schema var116 = var0.addSchema(2527, SAME_NAMESPACED);
      var0.addFixer(new BitStorageAlignFix(var116));
      Schema var117 = var0.addSchema(2528, SAME_NAMESPACED);
      var0.addFixer(
         ItemRenameFix.create(
            var117,
            "Rename soul fire torch and soul fire lantern",
            createRenamer(ImmutableMap.of("minecraft:soul_fire_torch", "minecraft:soul_torch", "minecraft:soul_fire_lantern", "minecraft:soul_lantern"))
         )
      );
      var0.addFixer(
         BlockRenameFix.create(
            var117,
            "Rename soul fire torch and soul fire lantern",
            createRenamer(
               ImmutableMap.of(
                  "minecraft:soul_fire_torch",
                  "minecraft:soul_torch",
                  "minecraft:soul_fire_wall_torch",
                  "minecraft:soul_wall_torch",
                  "minecraft:soul_fire_lantern",
                  "minecraft:soul_lantern"
               )
            )
         )
      );
      Schema var118 = var0.addSchema(2529, SAME_NAMESPACED);
      var0.addFixer(new StriderGravityFix(var118, false));
      Schema var119 = var0.addSchema(2531, SAME_NAMESPACED);
      var0.addFixer(new RedstoneWireConnectionsFix(var119));
      Schema var120 = var0.addSchema(2533, SAME_NAMESPACED);
      var0.addFixer(new VillagerFollowRangeFix(var120));
      Schema var121 = var0.addSchema(2535, SAME_NAMESPACED);
      var0.addFixer(new EntityShulkerRotationFix(var121));
      Schema var122 = var0.addSchema(2550, SAME_NAMESPACED);
      var0.addFixer(new WorldGenSettingsFix(var122));
      Schema var123 = var0.addSchema(2551, V2551::new);
      var0.addFixer(new WriteAndReadFix(var123, "add types to WorldGenData", References.WORLD_GEN_SETTINGS));
      Schema var124 = var0.addSchema(2552, SAME_NAMESPACED);
      var0.addFixer(new RenameBiomesFix(var124, false, "Nether biome rename", ImmutableMap.of("minecraft:nether", "minecraft:nether_wastes")));
      Schema var125 = var0.addSchema(2553, SAME_NAMESPACED);
      var0.addFixer(new BiomeFix(var125, false));
      Schema var126 = var0.addSchema(2558, SAME_NAMESPACED);
      var0.addFixer(new MissingDimensionFix(var126, false));
      var0.addFixer(new OptionsRenameFieldFix(var126, false, "Rename swapHands setting", "key_key.swapHands", "key_key.swapOffhand"));
      Schema var127 = var0.addSchema(2568, V2568::new);
      var0.addFixer(new AddNewChoices(var127, "Added Piglin Brute", References.ENTITY));
      Schema var128 = var0.addSchema(2571, V2571::new);
      var0.addFixer(new AddNewChoices(var128, "Added Goat", References.ENTITY));
      Schema var129 = var0.addSchema(2679, SAME_NAMESPACED);
      var0.addFixer(new CauldronRenameFix(var129, false));
      Schema var130 = var0.addSchema(2680, SAME_NAMESPACED);
      var0.addFixer(ItemRenameFix.create(var130, "Renamed grass path item to dirt path", createRenamer("minecraft:grass_path", "minecraft:dirt_path")));
      var0.addFixer(
         BlockRenameFixWithJigsaw.create(var130, "Renamed grass path block to dirt path", createRenamer("minecraft:grass_path", "minecraft:dirt_path"))
      );
      Schema var131 = var0.addSchema(2684, V2684::new);
      var0.addFixer(new AddNewChoices(var131, "Added Sculk Sensor", References.BLOCK_ENTITY));
      Schema var132 = var0.addSchema(2686, V2686::new);
      var0.addFixer(new AddNewChoices(var132, "Added Axolotl", References.ENTITY));
      Schema var133 = var0.addSchema(2688, V2688::new);
      var0.addFixer(new AddNewChoices(var133, "Added Glow Squid", References.ENTITY));
      var0.addFixer(new AddNewChoices(var133, "Added Glow Item Frame", References.ENTITY));
      Schema var134 = var0.addSchema(2690, SAME_NAMESPACED);
      ImmutableMap var135 = ImmutableMap.builder()
         .put("minecraft:weathered_copper_block", "minecraft:oxidized_copper_block")
         .put("minecraft:semi_weathered_copper_block", "minecraft:weathered_copper_block")
         .put("minecraft:lightly_weathered_copper_block", "minecraft:exposed_copper_block")
         .put("minecraft:weathered_cut_copper", "minecraft:oxidized_cut_copper")
         .put("minecraft:semi_weathered_cut_copper", "minecraft:weathered_cut_copper")
         .put("minecraft:lightly_weathered_cut_copper", "minecraft:exposed_cut_copper")
         .put("minecraft:weathered_cut_copper_stairs", "minecraft:oxidized_cut_copper_stairs")
         .put("minecraft:semi_weathered_cut_copper_stairs", "minecraft:weathered_cut_copper_stairs")
         .put("minecraft:lightly_weathered_cut_copper_stairs", "minecraft:exposed_cut_copper_stairs")
         .put("minecraft:weathered_cut_copper_slab", "minecraft:oxidized_cut_copper_slab")
         .put("minecraft:semi_weathered_cut_copper_slab", "minecraft:weathered_cut_copper_slab")
         .put("minecraft:lightly_weathered_cut_copper_slab", "minecraft:exposed_cut_copper_slab")
         .put("minecraft:waxed_semi_weathered_copper", "minecraft:waxed_weathered_copper")
         .put("minecraft:waxed_lightly_weathered_copper", "minecraft:waxed_exposed_copper")
         .put("minecraft:waxed_semi_weathered_cut_copper", "minecraft:waxed_weathered_cut_copper")
         .put("minecraft:waxed_lightly_weathered_cut_copper", "minecraft:waxed_exposed_cut_copper")
         .put("minecraft:waxed_semi_weathered_cut_copper_stairs", "minecraft:waxed_weathered_cut_copper_stairs")
         .put("minecraft:waxed_lightly_weathered_cut_copper_stairs", "minecraft:waxed_exposed_cut_copper_stairs")
         .put("minecraft:waxed_semi_weathered_cut_copper_slab", "minecraft:waxed_weathered_cut_copper_slab")
         .put("minecraft:waxed_lightly_weathered_cut_copper_slab", "minecraft:waxed_exposed_cut_copper_slab")
         .build();
      var0.addFixer(ItemRenameFix.create(var134, "Renamed copper block items to new oxidized terms", createRenamer(var135)));
      var0.addFixer(BlockRenameFixWithJigsaw.create(var134, "Renamed copper blocks to new oxidized terms", createRenamer(var135)));
      Schema var136 = var0.addSchema(2691, SAME_NAMESPACED);
      ImmutableMap var137 = ImmutableMap.builder()
         .put("minecraft:waxed_copper", "minecraft:waxed_copper_block")
         .put("minecraft:oxidized_copper_block", "minecraft:oxidized_copper")
         .put("minecraft:weathered_copper_block", "minecraft:weathered_copper")
         .put("minecraft:exposed_copper_block", "minecraft:exposed_copper")
         .build();
      var0.addFixer(ItemRenameFix.create(var136, "Rename copper item suffixes", createRenamer(var137)));
      var0.addFixer(BlockRenameFixWithJigsaw.create(var136, "Rename copper blocks suffixes", createRenamer(var137)));
      Schema var138 = var0.addSchema(2693, SAME_NAMESPACED);
      var0.addFixer(new AddFlagIfNotPresentFix(var138, References.WORLD_GEN_SETTINGS, "has_increased_height_already", false));
      Schema var139 = var0.addSchema(2696, SAME_NAMESPACED);
      ImmutableMap var140 = ImmutableMap.builder()
         .put("minecraft:grimstone", "minecraft:deepslate")
         .put("minecraft:grimstone_slab", "minecraft:cobbled_deepslate_slab")
         .put("minecraft:grimstone_stairs", "minecraft:cobbled_deepslate_stairs")
         .put("minecraft:grimstone_wall", "minecraft:cobbled_deepslate_wall")
         .put("minecraft:polished_grimstone", "minecraft:polished_deepslate")
         .put("minecraft:polished_grimstone_slab", "minecraft:polished_deepslate_slab")
         .put("minecraft:polished_grimstone_stairs", "minecraft:polished_deepslate_stairs")
         .put("minecraft:polished_grimstone_wall", "minecraft:polished_deepslate_wall")
         .put("minecraft:grimstone_tiles", "minecraft:deepslate_tiles")
         .put("minecraft:grimstone_tile_slab", "minecraft:deepslate_tile_slab")
         .put("minecraft:grimstone_tile_stairs", "minecraft:deepslate_tile_stairs")
         .put("minecraft:grimstone_tile_wall", "minecraft:deepslate_tile_wall")
         .put("minecraft:grimstone_bricks", "minecraft:deepslate_bricks")
         .put("minecraft:grimstone_brick_slab", "minecraft:deepslate_brick_slab")
         .put("minecraft:grimstone_brick_stairs", "minecraft:deepslate_brick_stairs")
         .put("minecraft:grimstone_brick_wall", "minecraft:deepslate_brick_wall")
         .put("minecraft:chiseled_grimstone", "minecraft:chiseled_deepslate")
         .build();
      var0.addFixer(ItemRenameFix.create(var139, "Renamed grimstone block items to deepslate", createRenamer(var140)));
      var0.addFixer(BlockRenameFixWithJigsaw.create(var139, "Renamed grimstone blocks to deepslate", createRenamer(var140)));
      Schema var141 = var0.addSchema(2700, SAME_NAMESPACED);
      var0.addFixer(
         BlockRenameFixWithJigsaw.create(
            var141,
            "Renamed cave vines blocks",
            createRenamer(ImmutableMap.of("minecraft:cave_vines_head", "minecraft:cave_vines", "minecraft:cave_vines_body", "minecraft:cave_vines_plant"))
         )
      );
      Schema var142 = var0.addSchema(2701, SAME_NAMESPACED);
      var0.addFixer(new SavedDataFeaturePoolElementFix(var142));
      Schema var143 = var0.addSchema(2702, SAME_NAMESPACED);
      var0.addFixer(new AbstractArrowPickupFix(var143));
      Schema var144 = var0.addSchema(2704, V2704::new);
      var0.addFixer(new AddNewChoices(var144, "Added Goat", References.ENTITY));
      Schema var145 = var0.addSchema(2707, V2707::new);
      var0.addFixer(new AddNewChoices(var145, "Added Marker", References.ENTITY));
      var0.addFixer(new AddFlagIfNotPresentFix(var145, References.WORLD_GEN_SETTINGS, "has_increased_height_already", true));
      Schema var146 = var0.addSchema(2710, SAME_NAMESPACED);
      var0.addFixer(
         new StatsRenameFix(var146, "Renamed play_one_minute stat to play_time", ImmutableMap.of("minecraft:play_one_minute", "minecraft:play_time"))
      );
      Schema var147 = var0.addSchema(2717, SAME_NAMESPACED);
      var0.addFixer(
         ItemRenameFix.create(
            var147, "Rename azalea_leaves_flowers", createRenamer(ImmutableMap.of("minecraft:azalea_leaves_flowers", "minecraft:flowering_azalea_leaves"))
         )
      );
      var0.addFixer(
         BlockRenameFix.create(
            var147,
            "Rename azalea_leaves_flowers items",
            createRenamer(ImmutableMap.of("minecraft:azalea_leaves_flowers", "minecraft:flowering_azalea_leaves"))
         )
      );
      Schema var148 = var0.addSchema(2825, SAME_NAMESPACED);
      var0.addFixer(new AddFlagIfNotPresentFix(var148, References.WORLD_GEN_SETTINGS, "has_increased_height_already", false));
      Schema var149 = var0.addSchema(2831, V2831::new);
      var0.addFixer(new SpawnerDataFix(var149));
      Schema var150 = var0.addSchema(2832, V2832::new);
      var0.addFixer(new WorldGenSettingsHeightAndBiomeFix(var150));
      var0.addFixer(new ChunkHeightAndBiomeFix(var150));
      Schema var151 = var0.addSchema(2833, SAME_NAMESPACED);
      var0.addFixer(new WorldGenSettingsDisallowOldCustomWorldsFix(var151));
      Schema var152 = var0.addSchema(2838, SAME_NAMESPACED);
      var0.addFixer(new RenameBiomesFix(var152, false, "Caves and Cliffs biome renames", CavesAndCliffsRenames.RENAMES));
      Schema var153 = var0.addSchema(2841, SAME_NAMESPACED);
      var0.addFixer(new ChunkProtoTickListFix(var153));
      Schema var154 = var0.addSchema(2842, V2842::new);
      var0.addFixer(new ChunkRenamesFix(var154));
      Schema var155 = var0.addSchema(2843, SAME_NAMESPACED);
      var0.addFixer(new OverreachingTickFix(var155));
      var0.addFixer(new RenameBiomesFix(var155, false, "Remove Deep Warm Ocean", Map.of("minecraft:deep_warm_ocean", "minecraft:warm_ocean")));
      Schema var156 = var0.addSchema(2846, SAME_NAMESPACED);
      var0.addFixer(
         new AdvancementsRenameFix(
            var156,
            false,
            "Rename some C&C part 2 advancements",
            createRenamer(
               ImmutableMap.of(
                  "minecraft:husbandry/play_jukebox_in_meadows",
                  "minecraft:adventure/play_jukebox_in_meadows",
                  "minecraft:adventure/caves_and_cliff",
                  "minecraft:adventure/fall_from_world_height",
                  "minecraft:adventure/ride_strider_in_overworld_lava",
                  "minecraft:nether/ride_strider_in_overworld_lava"
               )
            )
         )
      );
      Schema var157 = var0.addSchema(2852, SAME_NAMESPACED);
      var0.addFixer(new WorldGenSettingsDisallowOldCustomWorldsFix(var157));
      Schema var158 = var0.addSchema(2967, SAME_NAMESPACED);
      var0.addFixer(new StructureSettingsFlattenFix(var158));
      Schema var159 = var0.addSchema(2970, SAME_NAMESPACED);
      var0.addFixer(new StructuresBecomeConfiguredFix(var159));
      Schema var160 = var0.addSchema(3076, V3076::new);
      var0.addFixer(new AddNewChoices(var160, "Added Sculk Catalyst", References.BLOCK_ENTITY));
      Schema var161 = var0.addSchema(3077, SAME_NAMESPACED);
      var0.addFixer(new ChunkDeleteIgnoredLightDataFix(var161));
      Schema var162 = var0.addSchema(3078, V3078::new);
      var0.addFixer(new AddNewChoices(var162, "Added Frog", References.ENTITY));
      var0.addFixer(new AddNewChoices(var162, "Added Tadpole", References.ENTITY));
      var0.addFixer(new AddNewChoices(var162, "Added Sculk Shrieker", References.BLOCK_ENTITY));
      Schema var163 = var0.addSchema(3081, V3081::new);
      var0.addFixer(new AddNewChoices(var163, "Added Warden", References.ENTITY));
      Schema var164 = var0.addSchema(3082, V3082::new);
      var0.addFixer(new AddNewChoices(var164, "Added Chest Boat", References.ENTITY));
      Schema var165 = var0.addSchema(3083, V3083::new);
      var0.addFixer(new AddNewChoices(var165, "Added Allay", References.ENTITY));
      Schema var166 = var0.addSchema(3084, SAME_NAMESPACED);
      var0.addFixer(
         new SimpleRenameFix(
            var166,
            References.GAME_EVENT_NAME,
            ImmutableMap.builder()
               .put("minecraft:block_press", "minecraft:block_activate")
               .put("minecraft:block_switch", "minecraft:block_activate")
               .put("minecraft:block_unpress", "minecraft:block_deactivate")
               .put("minecraft:block_unswitch", "minecraft:block_deactivate")
               .put("minecraft:drinking_finish", "minecraft:drink")
               .put("minecraft:elytra_free_fall", "minecraft:elytra_glide")
               .put("minecraft:entity_damaged", "minecraft:entity_damage")
               .put("minecraft:entity_dying", "minecraft:entity_die")
               .put("minecraft:entity_killed", "minecraft:entity_die")
               .put("minecraft:mob_interact", "minecraft:entity_interact")
               .put("minecraft:ravager_roar", "minecraft:entity_roar")
               .put("minecraft:ring_bell", "minecraft:block_change")
               .put("minecraft:shulker_close", "minecraft:container_close")
               .put("minecraft:shulker_open", "minecraft:container_open")
               .put("minecraft:wolf_shaking", "minecraft:entity_shake")
               .build()
         )
      );
      Schema var167 = var0.addSchema(3086, SAME_NAMESPACED);
      var0.addFixer(
         new EntityVariantFix(
            var167, "Change cat variant type", References.ENTITY, "minecraft:cat", "CatType", Util.make(new Int2ObjectOpenHashMap(), var0x -> {
               var0x.defaultReturnValue("minecraft:tabby");
               var0x.put(0, "minecraft:tabby");
               var0x.put(1, "minecraft:black");
               var0x.put(2, "minecraft:red");
               var0x.put(3, "minecraft:siamese");
               var0x.put(4, "minecraft:british");
               var0x.put(5, "minecraft:calico");
               var0x.put(6, "minecraft:persian");
               var0x.put(7, "minecraft:ragdoll");
               var0x.put(8, "minecraft:white");
               var0x.put(9, "minecraft:jellie");
               var0x.put(10, "minecraft:all_black");
            })::get
         )
      );
      ImmutableMap var168 = ImmutableMap.builder()
         .put("textures/entity/cat/tabby.png", "minecraft:tabby")
         .put("textures/entity/cat/black.png", "minecraft:black")
         .put("textures/entity/cat/red.png", "minecraft:red")
         .put("textures/entity/cat/siamese.png", "minecraft:siamese")
         .put("textures/entity/cat/british_shorthair.png", "minecraft:british")
         .put("textures/entity/cat/calico.png", "minecraft:calico")
         .put("textures/entity/cat/persian.png", "minecraft:persian")
         .put("textures/entity/cat/ragdoll.png", "minecraft:ragdoll")
         .put("textures/entity/cat/white.png", "minecraft:white")
         .put("textures/entity/cat/jellie.png", "minecraft:jellie")
         .put("textures/entity/cat/all_black.png", "minecraft:all_black")
         .build();
      var0.addFixer(
         new CriteriaRenameFix(
            var167, "Migrate cat variant advancement", "minecraft:husbandry/complete_catalogue", var1x -> (String)var168.getOrDefault(var1x, var1x)
         )
      );
      Schema var169 = var0.addSchema(3087, SAME_NAMESPACED);
      var0.addFixer(
         new EntityVariantFix(
            var169, "Change frog variant type", References.ENTITY, "minecraft:frog", "Variant", Util.make(new Int2ObjectOpenHashMap(), var0x -> {
               var0x.put(0, "minecraft:temperate");
               var0x.put(1, "minecraft:warm");
               var0x.put(2, "minecraft:cold");
            })::get
         )
      );
      Schema var170 = var0.addSchema(3088, SAME_NAMESPACED);
      var0.addFixer(new BlendingDataFix(var170));
      Schema var171 = var0.addSchema(3090, SAME_NAMESPACED);
      var0.addFixer(new EntityPaintingFieldsRenameFix(var171));
      Schema var172 = var0.addSchema(3093, SAME_NAMESPACED);
      var0.addFixer(new EntityGoatMissingStateFix(var172));
      Schema var173 = var0.addSchema(3094, SAME_NAMESPACED);
      var0.addFixer(new GoatHornIdFix(var173));
      Schema var174 = var0.addSchema(3097, SAME_NAMESPACED);
      var0.addFixer(new FilteredBooksFix(var174));
      var0.addFixer(new FilteredSignsFix(var174));
      Map var175 = Map.of("minecraft:british", "minecraft:british_shorthair");
      var0.addFixer(new VariantRenameFix(var174, "Rename british shorthair", References.ENTITY, "minecraft:cat", var175));
      var0.addFixer(
         new CriteriaRenameFix(
            var174,
            "Migrate cat variant advancement for british shorthair",
            "minecraft:husbandry/complete_catalogue",
            var1x -> var175.getOrDefault(var1x, var1x)
         )
      );
      var0.addFixer(new PoiTypeRemoveFix(var174, "Remove unpopulated villager PoI types", Set.of("minecraft:unemployed", "minecraft:nitwit")::contains));
   }

   private static UnaryOperator<String> createRenamer(Map<String, String> var0) {
      return var1 -> var0.getOrDefault(var1, var1);
   }

   private static UnaryOperator<String> createRenamer(String var0, String var1) {
      return var2 -> Objects.equals(var2, var0) ? var1 : var2;
   }
}
