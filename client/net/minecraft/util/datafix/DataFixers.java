package net.minecraft.util.datafix;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.util.datafix.fixes.AbstractArrowPickupFix;
import net.minecraft.util.datafix.fixes.AddFlagIfNotPresentFix;
import net.minecraft.util.datafix.fixes.AddNewChoices;
import net.minecraft.util.datafix.fixes.AdvancementsFix;
import net.minecraft.util.datafix.fixes.AdvancementsRenameFix;
import net.minecraft.util.datafix.fixes.AreaEffectCloudPotionFix;
import net.minecraft.util.datafix.fixes.AttributesRename;
import net.minecraft.util.datafix.fixes.BannerEntityCustomNameToOverrideComponentFix;
import net.minecraft.util.datafix.fixes.BannerPatternFormatFix;
import net.minecraft.util.datafix.fixes.BedItemColorFix;
import net.minecraft.util.datafix.fixes.BeehiveFieldRenameFix;
import net.minecraft.util.datafix.fixes.BiomeFix;
import net.minecraft.util.datafix.fixes.BitStorageAlignFix;
import net.minecraft.util.datafix.fixes.BlendingDataFix;
import net.minecraft.util.datafix.fixes.BlendingDataRemoveFromNetherEndFix;
import net.minecraft.util.datafix.fixes.BlockEntityBannerColorFix;
import net.minecraft.util.datafix.fixes.BlockEntityBlockStateFix;
import net.minecraft.util.datafix.fixes.BlockEntityCustomNameToComponentFix;
import net.minecraft.util.datafix.fixes.BlockEntityIdFix;
import net.minecraft.util.datafix.fixes.BlockEntityJukeboxFix;
import net.minecraft.util.datafix.fixes.BlockEntityKeepPacked;
import net.minecraft.util.datafix.fixes.BlockEntityRenameFix;
import net.minecraft.util.datafix.fixes.BlockEntityShulkerBoxColorFix;
import net.minecraft.util.datafix.fixes.BlockEntitySignDoubleSidedEditableTextFix;
import net.minecraft.util.datafix.fixes.BlockEntitySignTextStrictJsonFix;
import net.minecraft.util.datafix.fixes.BlockEntityUUIDFix;
import net.minecraft.util.datafix.fixes.BlockNameFlatteningFix;
import net.minecraft.util.datafix.fixes.BlockPosFormatAndRenamesFix;
import net.minecraft.util.datafix.fixes.BlockRenameFix;
import net.minecraft.util.datafix.fixes.BlockStateStructureTemplateFix;
import net.minecraft.util.datafix.fixes.CatTypeFix;
import net.minecraft.util.datafix.fixes.CauldronRenameFix;
import net.minecraft.util.datafix.fixes.CavesAndCliffsRenames;
import net.minecraft.util.datafix.fixes.ChestedHorsesInventoryZeroIndexingFix;
import net.minecraft.util.datafix.fixes.ChunkBedBlockEntityInjecterFix;
import net.minecraft.util.datafix.fixes.ChunkBiomeFix;
import net.minecraft.util.datafix.fixes.ChunkDeleteIgnoredLightDataFix;
import net.minecraft.util.datafix.fixes.ChunkDeleteLightFix;
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
import net.minecraft.util.datafix.fixes.DecoratedPotFieldRenameFix;
import net.minecraft.util.datafix.fixes.DropInvalidSignDataFix;
import net.minecraft.util.datafix.fixes.DyeItemRenameFix;
import net.minecraft.util.datafix.fixes.EffectDurationFix;
import net.minecraft.util.datafix.fixes.EmptyItemInHotbarFix;
import net.minecraft.util.datafix.fixes.EmptyItemInVillagerTradeFix;
import net.minecraft.util.datafix.fixes.EntityArmorStandSilentFix;
import net.minecraft.util.datafix.fixes.EntityBlockStateFix;
import net.minecraft.util.datafix.fixes.EntityBrushableBlockFieldsRenameFix;
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
import net.minecraft.util.datafix.fixes.FeatureFlagRemoveFix;
import net.minecraft.util.datafix.fixes.FilteredBooksFix;
import net.minecraft.util.datafix.fixes.FilteredSignsFix;
import net.minecraft.util.datafix.fixes.FixProjectileStoredItem;
import net.minecraft.util.datafix.fixes.ForcePoiRebuild;
import net.minecraft.util.datafix.fixes.FurnaceRecipeFix;
import net.minecraft.util.datafix.fixes.GoatHornIdFix;
import net.minecraft.util.datafix.fixes.GossipUUIDFix;
import net.minecraft.util.datafix.fixes.HeightmapRenamingFix;
import net.minecraft.util.datafix.fixes.HorseBodyArmorItemFix;
import net.minecraft.util.datafix.fixes.IglooMetadataRemovalFix;
import net.minecraft.util.datafix.fixes.ItemBannerColorFix;
import net.minecraft.util.datafix.fixes.ItemCustomNameToComponentFix;
import net.minecraft.util.datafix.fixes.ItemIdFix;
import net.minecraft.util.datafix.fixes.ItemLoreFix;
import net.minecraft.util.datafix.fixes.ItemPotionFix;
import net.minecraft.util.datafix.fixes.ItemRemoveBlockEntityTagFix;
import net.minecraft.util.datafix.fixes.ItemRenameFix;
import net.minecraft.util.datafix.fixes.ItemShulkerBoxColorFix;
import net.minecraft.util.datafix.fixes.ItemSpawnEggFix;
import net.minecraft.util.datafix.fixes.ItemStackComponentizationFix;
import net.minecraft.util.datafix.fixes.ItemStackCustomNameToOverrideComponentFix;
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
import net.minecraft.util.datafix.fixes.LegacyDragonFightFix;
import net.minecraft.util.datafix.fixes.LevelDataGeneratorOptionsFix;
import net.minecraft.util.datafix.fixes.LevelFlatGeneratorInfoFix;
import net.minecraft.util.datafix.fixes.LevelLegacyWorldGenSettingsFix;
import net.minecraft.util.datafix.fixes.LevelUUIDFix;
import net.minecraft.util.datafix.fixes.LodestoneCompassComponentFix;
import net.minecraft.util.datafix.fixes.MapBannerBlockPosFormatFix;
import net.minecraft.util.datafix.fixes.MapIdFix;
import net.minecraft.util.datafix.fixes.MemoryExpiryDataFix;
import net.minecraft.util.datafix.fixes.MissingDimensionFix;
import net.minecraft.util.datafix.fixes.MobEffectIdFix;
import net.minecraft.util.datafix.fixes.MobSpawnerEntityIdentifiersFix;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.NamespacedTypeRenameFix;
import net.minecraft.util.datafix.fixes.NewVillageFix;
import net.minecraft.util.datafix.fixes.ObjectiveDisplayNameFix;
import net.minecraft.util.datafix.fixes.ObjectiveRenderTypeFix;
import net.minecraft.util.datafix.fixes.OminousBannerBlockEntityRenameFix;
import net.minecraft.util.datafix.fixes.OminousBannerRenameFix;
import net.minecraft.util.datafix.fixes.OptionsAccessibilityOnboardFix;
import net.minecraft.util.datafix.fixes.OptionsAddTextBackgroundFix;
import net.minecraft.util.datafix.fixes.OptionsAmbientOcclusionFix;
import net.minecraft.util.datafix.fixes.OptionsForceVBOFix;
import net.minecraft.util.datafix.fixes.OptionsKeyLwjgl3Fix;
import net.minecraft.util.datafix.fixes.OptionsKeyTranslationFix;
import net.minecraft.util.datafix.fixes.OptionsLowerCaseLanguageFix;
import net.minecraft.util.datafix.fixes.OptionsProgrammerArtFix;
import net.minecraft.util.datafix.fixes.OptionsRenameFieldFix;
import net.minecraft.util.datafix.fixes.OverreachingTickFix;
import net.minecraft.util.datafix.fixes.ParticleUnflatteningFix;
import net.minecraft.util.datafix.fixes.PlayerHeadBlockProfileFix;
import net.minecraft.util.datafix.fixes.PlayerUUIDFix;
import net.minecraft.util.datafix.fixes.PoiTypeRemoveFix;
import net.minecraft.util.datafix.fixes.PoiTypeRenameFix;
import net.minecraft.util.datafix.fixes.PrimedTntBlockStateFixer;
import net.minecraft.util.datafix.fixes.RandomSequenceSettingsFix;
import net.minecraft.util.datafix.fixes.RecipesFix;
import net.minecraft.util.datafix.fixes.RecipesRenameningFix;
import net.minecraft.util.datafix.fixes.RedstoneWireConnectionsFix;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.fixes.RemapChunkStatusFix;
import net.minecraft.util.datafix.fixes.RemoveEmptyItemInBrushableBlockFix;
import net.minecraft.util.datafix.fixes.RemoveGolemGossipFix;
import net.minecraft.util.datafix.fixes.RenameEnchantmentsFix;
import net.minecraft.util.datafix.fixes.RenamedCoralFansFix;
import net.minecraft.util.datafix.fixes.RenamedCoralFix;
import net.minecraft.util.datafix.fixes.ReorganizePoi;
import net.minecraft.util.datafix.fixes.SavedDataFeaturePoolElementFix;
import net.minecraft.util.datafix.fixes.SavedDataUUIDFix;
import net.minecraft.util.datafix.fixes.ScoreboardDisplaySlotFix;
import net.minecraft.util.datafix.fixes.SpawnerDataFix;
import net.minecraft.util.datafix.fixes.StatsCounterFix;
import net.minecraft.util.datafix.fixes.StatsRenameFix;
import net.minecraft.util.datafix.fixes.StriderGravityFix;
import net.minecraft.util.datafix.fixes.StructureReferenceCountFix;
import net.minecraft.util.datafix.fixes.StructureSettingsFlattenFix;
import net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix;
import net.minecraft.util.datafix.fixes.TeamDisplayNameFix;
import net.minecraft.util.datafix.fixes.TippedArrowPotionToItemFix;
import net.minecraft.util.datafix.fixes.TrappedChestBlockEntityFix;
import net.minecraft.util.datafix.fixes.TrialSpawnerConfigFix;
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
import net.minecraft.util.datafix.schemas.V3202;
import net.minecraft.util.datafix.schemas.V3203;
import net.minecraft.util.datafix.schemas.V3204;
import net.minecraft.util.datafix.schemas.V3325;
import net.minecraft.util.datafix.schemas.V3326;
import net.minecraft.util.datafix.schemas.V3327;
import net.minecraft.util.datafix.schemas.V3328;
import net.minecraft.util.datafix.schemas.V3438;
import net.minecraft.util.datafix.schemas.V3448;
import net.minecraft.util.datafix.schemas.V3682;
import net.minecraft.util.datafix.schemas.V3683;
import net.minecraft.util.datafix.schemas.V3685;
import net.minecraft.util.datafix.schemas.V3689;
import net.minecraft.util.datafix.schemas.V3799;
import net.minecraft.util.datafix.schemas.V3807;
import net.minecraft.util.datafix.schemas.V3808;
import net.minecraft.util.datafix.schemas.V3808_1;
import net.minecraft.util.datafix.schemas.V3808_2;
import net.minecraft.util.datafix.schemas.V3816;
import net.minecraft.util.datafix.schemas.V3818;
import net.minecraft.util.datafix.schemas.V3818_3;
import net.minecraft.util.datafix.schemas.V3818_4;
import net.minecraft.util.datafix.schemas.V3818_5;
import net.minecraft.util.datafix.schemas.V3825;
import net.minecraft.util.datafix.schemas.V501;
import net.minecraft.util.datafix.schemas.V700;
import net.minecraft.util.datafix.schemas.V701;
import net.minecraft.util.datafix.schemas.V702;
import net.minecraft.util.datafix.schemas.V703;
import net.minecraft.util.datafix.schemas.V704;
import net.minecraft.util.datafix.schemas.V705;
import net.minecraft.util.datafix.schemas.V808;
import net.minecraft.util.datafix.schemas.V99;

public class DataFixers {
   private static final BiFunction<Integer, Schema, Schema> SAME = Schema::new;
   private static final BiFunction<Integer, Schema, Schema> SAME_NAMESPACED = NamespacedSchema::new;
   private static final DataFixer dataFixer = createFixerUpper(SharedConstants.DATA_FIX_TYPES_TO_OPTIMIZE);
   public static final int BLENDING_VERSION = 3441;

   private DataFixers() {
      super();
   }

   public static DataFixer getDataFixer() {
      return dataFixer;
   }

   private static synchronized DataFixer createFixerUpper(Set<TypeReference> var0) {
      DataFixerBuilder var1 = new DataFixerBuilder(SharedConstants.getCurrentVersion().getDataVersion().getVersion());
      addFixers(var1);
      if (var0.isEmpty()) {
         return var1.buildUnoptimized();
      } else {
         ExecutorService var2 = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder().setNameFormat("Datafixer Bootstrap").setDaemon(true).setPriority(1).build()
         );
         return var1.buildOptimized(var0, var2);
      }
   }

   private static void addFixers(DataFixerBuilder var0) {
      var0.addSchema(99, V99::new);
      Schema var1 = var0.addSchema(100, V100::new);
      var0.addFixer(new EntityEquipmentToArmorAndHandFix(var1, true));
      Schema var2 = var0.addSchema(101, SAME);
      var0.addFixer(new BlockEntitySignTextStrictJsonFix(var2, false));
      Schema var3 = var0.addSchema(102, V102::new);
      var0.addFixer(new ItemIdFix(var3, true));
      var0.addFixer(new ItemPotionFix(var3, false));
      Schema var4 = var0.addSchema(105, SAME);
      var0.addFixer(new ItemSpawnEggFix(var4, true));
      Schema var5 = var0.addSchema(106, V106::new);
      var0.addFixer(new MobSpawnerEntityIdentifiersFix(var5, true));
      Schema var6 = var0.addSchema(107, V107::new);
      var0.addFixer(new EntityMinecartIdentifiersFix(var6));
      Schema var7 = var0.addSchema(108, SAME);
      var0.addFixer(new EntityStringUuidFix(var7, true));
      Schema var8 = var0.addSchema(109, SAME);
      var0.addFixer(new EntityHealthFix(var8, true));
      Schema var9 = var0.addSchema(110, SAME);
      var0.addFixer(new EntityHorseSaddleFix(var9, true));
      Schema var10 = var0.addSchema(111, SAME);
      var0.addFixer(new EntityPaintingItemFrameDirectionFix(var10, true));
      Schema var11 = var0.addSchema(113, SAME);
      var0.addFixer(new EntityRedundantChanceTagsFix(var11, true));
      Schema var12 = var0.addSchema(135, V135::new);
      var0.addFixer(new EntityRidingToPassengersFix(var12, true));
      Schema var13 = var0.addSchema(143, V143::new);
      var0.addFixer(new EntityTippedArrowFix(var13, true));
      Schema var14 = var0.addSchema(147, SAME);
      var0.addFixer(new EntityArmorStandSilentFix(var14, true));
      Schema var15 = var0.addSchema(165, SAME);
      var0.addFixer(new ItemWrittenBookPagesStrictJsonFix(var15, true));
      Schema var16 = var0.addSchema(501, V501::new);
      var0.addFixer(new AddNewChoices(var16, "Add 1.10 entities fix", References.ENTITY));
      Schema var17 = var0.addSchema(502, SAME);
      var0.addFixer(
         ItemRenameFix.create(
            var17,
            "cooked_fished item renamer",
            var0x -> Objects.equals(NamespacedSchema.ensureNamespaced(var0x), "minecraft:cooked_fished") ? "minecraft:cooked_fish" : var0x
         )
      );
      var0.addFixer(new EntityZombieVillagerTypeFix(var17, false));
      Schema var18 = var0.addSchema(505, SAME);
      var0.addFixer(new OptionsForceVBOFix(var18, false));
      Schema var19 = var0.addSchema(700, V700::new);
      var0.addFixer(new EntityElderGuardianSplitFix(var19, true));
      Schema var20 = var0.addSchema(701, V701::new);
      var0.addFixer(new EntitySkeletonSplitFix(var20, true));
      Schema var21 = var0.addSchema(702, V702::new);
      var0.addFixer(new EntityZombieSplitFix(var21));
      Schema var22 = var0.addSchema(703, V703::new);
      var0.addFixer(new EntityHorseSplitFix(var22, true));
      Schema var23 = var0.addSchema(704, V704::new);
      var0.addFixer(new BlockEntityIdFix(var23, true));
      Schema var24 = var0.addSchema(705, V705::new);
      var0.addFixer(new EntityIdFix(var24, true));
      Schema var25 = var0.addSchema(804, SAME_NAMESPACED);
      var0.addFixer(new ItemBannerColorFix(var25, true));
      Schema var26 = var0.addSchema(806, SAME_NAMESPACED);
      var0.addFixer(new ItemWaterPotionFix(var26, false));
      Schema var27 = var0.addSchema(808, V808::new);
      var0.addFixer(new AddNewChoices(var27, "added shulker box", References.BLOCK_ENTITY));
      Schema var28 = var0.addSchema(808, 1, SAME_NAMESPACED);
      var0.addFixer(new EntityShulkerColorFix(var28, false));
      Schema var29 = var0.addSchema(813, SAME_NAMESPACED);
      var0.addFixer(new ItemShulkerBoxColorFix(var29, false));
      var0.addFixer(new BlockEntityShulkerBoxColorFix(var29, false));
      Schema var30 = var0.addSchema(816, SAME_NAMESPACED);
      var0.addFixer(new OptionsLowerCaseLanguageFix(var30, false));
      Schema var31 = var0.addSchema(820, SAME_NAMESPACED);
      var0.addFixer(ItemRenameFix.create(var31, "totem item renamer", createRenamer("minecraft:totem", "minecraft:totem_of_undying")));
      Schema var32 = var0.addSchema(1022, V1022::new);
      var0.addFixer(new WriteAndReadFix(var32, "added shoulder entities to players", References.PLAYER));
      Schema var33 = var0.addSchema(1125, V1125::new);
      var0.addFixer(new ChunkBedBlockEntityInjecterFix(var33, true));
      var0.addFixer(new BedItemColorFix(var33, false));
      Schema var34 = var0.addSchema(1344, SAME_NAMESPACED);
      var0.addFixer(new OptionsKeyLwjgl3Fix(var34, false));
      Schema var35 = var0.addSchema(1446, SAME_NAMESPACED);
      var0.addFixer(new OptionsKeyTranslationFix(var35, false));
      Schema var36 = var0.addSchema(1450, SAME_NAMESPACED);
      var0.addFixer(new BlockStateStructureTemplateFix(var36, false));
      Schema var37 = var0.addSchema(1451, V1451::new);
      var0.addFixer(new AddNewChoices(var37, "AddTrappedChestFix", References.BLOCK_ENTITY));
      Schema var38 = var0.addSchema(1451, 1, V1451_1::new);
      var0.addFixer(new ChunkPalettedStorageFix(var38, true));
      Schema var39 = var0.addSchema(1451, 2, V1451_2::new);
      var0.addFixer(new BlockEntityBlockStateFix(var39, true));
      Schema var40 = var0.addSchema(1451, 3, V1451_3::new);
      var0.addFixer(new EntityBlockStateFix(var40, true));
      var0.addFixer(new ItemStackMapIdFix(var40, false));
      Schema var41 = var0.addSchema(1451, 4, V1451_4::new);
      var0.addFixer(new BlockNameFlatteningFix(var41, true));
      var0.addFixer(new ItemStackTheFlatteningFix(var41, false));
      Schema var42 = var0.addSchema(1451, 5, V1451_5::new);
      var0.addFixer(
         new ItemRemoveBlockEntityTagFix(
            var42,
            false,
            Set.of(
               "minecraft:note_block",
               "minecraft:flower_pot",
               "minecraft:dandelion",
               "minecraft:poppy",
               "minecraft:blue_orchid",
               "minecraft:allium",
               "minecraft:azure_bluet",
               "minecraft:red_tulip",
               "minecraft:orange_tulip",
               "minecraft:white_tulip",
               "minecraft:pink_tulip",
               "minecraft:oxeye_daisy",
               "minecraft:cactus",
               "minecraft:brown_mushroom",
               "minecraft:red_mushroom",
               "minecraft:oak_sapling",
               "minecraft:spruce_sapling",
               "minecraft:birch_sapling",
               "minecraft:jungle_sapling",
               "minecraft:acacia_sapling",
               "minecraft:dark_oak_sapling",
               "minecraft:dead_bush",
               "minecraft:fern"
            )
         )
      );
      var0.addFixer(new AddNewChoices(var42, "RemoveNoteBlockFlowerPotFix", References.BLOCK_ENTITY));
      var0.addFixer(new ItemStackSpawnEggFix(var42, false, "minecraft:spawn_egg"));
      var0.addFixer(new EntityWolfColorFix(var42, false));
      var0.addFixer(new BlockEntityBannerColorFix(var42, false));
      var0.addFixer(new LevelFlatGeneratorInfoFix(var42, false));
      Schema var43 = var0.addSchema(1451, 6, V1451_6::new);
      var0.addFixer(new StatsCounterFix(var43, true));
      var0.addFixer(new BlockEntityJukeboxFix(var43, false));
      Schema var44 = var0.addSchema(1451, 7, SAME_NAMESPACED);
      var0.addFixer(new VillagerTradeFix(var44));
      Schema var45 = var0.addSchema(1456, SAME_NAMESPACED);
      var0.addFixer(new EntityItemFrameDirectionFix(var45, false));
      Schema var46 = var0.addSchema(1458, SAME_NAMESPACED);
      var0.addFixer(new EntityCustomNameToComponentFix(var46, false));
      var0.addFixer(new ItemCustomNameToComponentFix(var46, false));
      var0.addFixer(new BlockEntityCustomNameToComponentFix(var46, false));
      Schema var47 = var0.addSchema(1460, V1460::new);
      var0.addFixer(new EntityPaintingMotiveFix(var47, false));
      Schema var48 = var0.addSchema(1466, V1466::new);
      var0.addFixer(new AddNewChoices(var48, "Add DUMMY block entity", References.BLOCK_ENTITY));
      var0.addFixer(new ChunkToProtochunkFix(var48, true));
      Schema var49 = var0.addSchema(1470, V1470::new);
      var0.addFixer(new AddNewChoices(var49, "Add 1.13 entities fix", References.ENTITY));
      Schema var50 = var0.addSchema(1474, SAME_NAMESPACED);
      var0.addFixer(new ColorlessShulkerEntityFix(var50, false));
      var0.addFixer(
         BlockRenameFix.create(
            var50,
            "Colorless shulker block fixer",
            var0x -> Objects.equals(NamespacedSchema.ensureNamespaced(var0x), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : var0x
         )
      );
      var0.addFixer(
         ItemRenameFix.create(
            var50,
            "Colorless shulker item fixer",
            var0x -> Objects.equals(NamespacedSchema.ensureNamespaced(var0x), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : var0x
         )
      );
      Schema var51 = var0.addSchema(1475, SAME_NAMESPACED);
      var0.addFixer(
         BlockRenameFix.create(
            var51, "Flowing fixer", createRenamer(ImmutableMap.of("minecraft:flowing_water", "minecraft:water", "minecraft:flowing_lava", "minecraft:lava"))
         )
      );
      Schema var52 = var0.addSchema(1480, SAME_NAMESPACED);
      var0.addFixer(BlockRenameFix.create(var52, "Rename coral blocks", createRenamer(RenamedCoralFix.RENAMED_IDS)));
      var0.addFixer(ItemRenameFix.create(var52, "Rename coral items", createRenamer(RenamedCoralFix.RENAMED_IDS)));
      Schema var53 = var0.addSchema(1481, V1481::new);
      var0.addFixer(new AddNewChoices(var53, "Add conduit", References.BLOCK_ENTITY));
      Schema var54 = var0.addSchema(1483, V1483::new);
      var0.addFixer(new EntityPufferfishRenameFix(var54, true));
      var0.addFixer(ItemRenameFix.create(var54, "Rename pufferfish egg item", createRenamer(EntityPufferfishRenameFix.RENAMED_IDS)));
      Schema var55 = var0.addSchema(1484, SAME_NAMESPACED);
      var0.addFixer(
         ItemRenameFix.create(
            var55,
            "Rename seagrass items",
            createRenamer(ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass"))
         )
      );
      var0.addFixer(
         BlockRenameFix.create(
            var55,
            "Rename seagrass blocks",
            createRenamer(ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass"))
         )
      );
      var0.addFixer(new HeightmapRenamingFix(var55, false));
      Schema var56 = var0.addSchema(1486, V1486::new);
      var0.addFixer(new EntityCodSalmonFix(var56, true));
      var0.addFixer(ItemRenameFix.create(var56, "Rename cod/salmon egg items", createRenamer(EntityCodSalmonFix.RENAMED_EGG_IDS)));
      Schema var57 = var0.addSchema(1487, SAME_NAMESPACED);
      var0.addFixer(
         ItemRenameFix.create(
            var57,
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
            var57,
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
      Schema var58 = var0.addSchema(1488, SAME_NAMESPACED);
      var0.addFixer(
         BlockRenameFix.create(
            var58, "Rename kelp/kelptop", createRenamer(ImmutableMap.of("minecraft:kelp_top", "minecraft:kelp", "minecraft:kelp", "minecraft:kelp_plant"))
         )
      );
      var0.addFixer(ItemRenameFix.create(var58, "Rename kelptop", createRenamer("minecraft:kelp_top", "minecraft:kelp")));
      var0.addFixer(new NamedEntityFix(var58, false, "Command block block entity custom name fix", References.BLOCK_ENTITY, "minecraft:command_block") {
         @Override
         protected Typed<?> fix(Typed<?> var1) {
            return var1.update(DSL.remainderFinder(), EntityCustomNameToComponentFix::fixTagCustomName);
         }
      });
      var0.addFixer(new NamedEntityFix(var58, false, "Command block minecart custom name fix", References.ENTITY, "minecraft:commandblock_minecart") {
         @Override
         protected Typed<?> fix(Typed<?> var1) {
            return var1.update(DSL.remainderFinder(), EntityCustomNameToComponentFix::fixTagCustomName);
         }
      });
      var0.addFixer(new IglooMetadataRemovalFix(var58, false));
      Schema var59 = var0.addSchema(1490, SAME_NAMESPACED);
      var0.addFixer(BlockRenameFix.create(var59, "Rename melon_block", createRenamer("minecraft:melon_block", "minecraft:melon")));
      var0.addFixer(
         ItemRenameFix.create(
            var59,
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
      Schema var60 = var0.addSchema(1492, SAME_NAMESPACED);
      var0.addFixer(new ChunkStructuresTemplateRenameFix(var60, false));
      Schema var61 = var0.addSchema(1494, SAME_NAMESPACED);
      var0.addFixer(new ItemStackEnchantmentNamesFix(var61, false));
      Schema var62 = var0.addSchema(1496, SAME_NAMESPACED);
      var0.addFixer(new LeavesFix(var62, false));
      Schema var63 = var0.addSchema(1500, SAME_NAMESPACED);
      var0.addFixer(new BlockEntityKeepPacked(var63, false));
      Schema var64 = var0.addSchema(1501, SAME_NAMESPACED);
      var0.addFixer(new AdvancementsFix(var64, false));
      Schema var65 = var0.addSchema(1502, SAME_NAMESPACED);
      var0.addFixer(new NamespacedTypeRenameFix(var65, "Recipes fix", References.RECIPE, createRenamer(RecipesFix.RECIPES)));
      Schema var66 = var0.addSchema(1506, SAME_NAMESPACED);
      var0.addFixer(new LevelDataGeneratorOptionsFix(var66, false));
      Schema var67 = var0.addSchema(1510, V1510::new);
      var0.addFixer(BlockRenameFix.create(var67, "Block renamening fix", createRenamer(EntityTheRenameningFix.RENAMED_BLOCKS)));
      var0.addFixer(ItemRenameFix.create(var67, "Item renamening fix", createRenamer(EntityTheRenameningFix.RENAMED_ITEMS)));
      var0.addFixer(new NamespacedTypeRenameFix(var67, "Recipes renamening fix", References.RECIPE, createRenamer(RecipesRenameningFix.RECIPES)));
      var0.addFixer(new EntityTheRenameningFix(var67, true));
      var0.addFixer(
         new StatsRenameFix(
            var67,
            "SwimStatsRenameFix",
            ImmutableMap.of("minecraft:swim_one_cm", "minecraft:walk_on_water_one_cm", "minecraft:dive_one_cm", "minecraft:walk_under_water_one_cm")
         )
      );
      Schema var68 = var0.addSchema(1514, SAME_NAMESPACED);
      var0.addFixer(new ObjectiveDisplayNameFix(var68, false));
      var0.addFixer(new TeamDisplayNameFix(var68, false));
      var0.addFixer(new ObjectiveRenderTypeFix(var68, false));
      Schema var69 = var0.addSchema(1515, SAME_NAMESPACED);
      var0.addFixer(BlockRenameFix.create(var69, "Rename coral fan blocks", createRenamer(RenamedCoralFansFix.RENAMED_IDS)));
      Schema var70 = var0.addSchema(1624, SAME_NAMESPACED);
      var0.addFixer(new TrappedChestBlockEntityFix(var70, false));
      Schema var71 = var0.addSchema(1800, V1800::new);
      var0.addFixer(new AddNewChoices(var71, "Added 1.14 mobs fix", References.ENTITY));
      var0.addFixer(ItemRenameFix.create(var71, "Rename dye items", createRenamer(DyeItemRenameFix.RENAMED_IDS)));
      Schema var72 = var0.addSchema(1801, V1801::new);
      var0.addFixer(new AddNewChoices(var72, "Added Illager Beast", References.ENTITY));
      Schema var73 = var0.addSchema(1802, SAME_NAMESPACED);
      var0.addFixer(
         BlockRenameFix.create(
            var73,
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
            var73,
            "Rename sign item & stone slabs",
            createRenamer(ImmutableMap.of("minecraft:stone_slab", "minecraft:smooth_stone_slab", "minecraft:sign", "minecraft:oak_sign"))
         )
      );
      Schema var74 = var0.addSchema(1803, SAME_NAMESPACED);
      var0.addFixer(new ItemLoreFix(var74, false));
      Schema var75 = var0.addSchema(1904, V1904::new);
      var0.addFixer(new AddNewChoices(var75, "Added Cats", References.ENTITY));
      var0.addFixer(new EntityCatSplitFix(var75, false));
      Schema var76 = var0.addSchema(1905, SAME_NAMESPACED);
      var0.addFixer(new ChunkStatusFix(var76, false));
      Schema var77 = var0.addSchema(1906, V1906::new);
      var0.addFixer(new AddNewChoices(var77, "Add POI Blocks", References.BLOCK_ENTITY));
      Schema var78 = var0.addSchema(1909, V1909::new);
      var0.addFixer(new AddNewChoices(var78, "Add jigsaw", References.BLOCK_ENTITY));
      Schema var79 = var0.addSchema(1911, SAME_NAMESPACED);
      var0.addFixer(new ChunkStatusFix2(var79, false));
      Schema var80 = var0.addSchema(1914, SAME_NAMESPACED);
      var0.addFixer(new WeaponSmithChestLootTableFix(var80, false));
      Schema var81 = var0.addSchema(1917, SAME_NAMESPACED);
      var0.addFixer(new CatTypeFix(var81, false));
      Schema var82 = var0.addSchema(1918, SAME_NAMESPACED);
      var0.addFixer(new VillagerDataFix(var82, "minecraft:villager"));
      var0.addFixer(new VillagerDataFix(var82, "minecraft:zombie_villager"));
      Schema var83 = var0.addSchema(1920, V1920::new);
      var0.addFixer(new NewVillageFix(var83, false));
      var0.addFixer(new AddNewChoices(var83, "Add campfire", References.BLOCK_ENTITY));
      Schema var84 = var0.addSchema(1925, SAME_NAMESPACED);
      var0.addFixer(new MapIdFix(var84, false));
      Schema var85 = var0.addSchema(1928, V1928::new);
      var0.addFixer(new EntityRavagerRenameFix(var85, true));
      var0.addFixer(ItemRenameFix.create(var85, "Rename ravager egg item", createRenamer(EntityRavagerRenameFix.RENAMED_IDS)));
      Schema var86 = var0.addSchema(1929, V1929::new);
      var0.addFixer(new AddNewChoices(var86, "Add Wandering Trader and Trader Llama", References.ENTITY));
      Schema var87 = var0.addSchema(1931, V1931::new);
      var0.addFixer(new AddNewChoices(var87, "Added Fox", References.ENTITY));
      Schema var88 = var0.addSchema(1936, SAME_NAMESPACED);
      var0.addFixer(new OptionsAddTextBackgroundFix(var88, false));
      Schema var89 = var0.addSchema(1946, SAME_NAMESPACED);
      var0.addFixer(new ReorganizePoi(var89, false));
      Schema var90 = var0.addSchema(1948, SAME_NAMESPACED);
      var0.addFixer(new OminousBannerRenameFix(var90));
      Schema var91 = var0.addSchema(1953, SAME_NAMESPACED);
      var0.addFixer(new OminousBannerBlockEntityRenameFix(var91, false));
      Schema var92 = var0.addSchema(1955, SAME_NAMESPACED);
      var0.addFixer(new VillagerRebuildLevelAndXpFix(var92, false));
      var0.addFixer(new ZombieVillagerRebuildXpFix(var92, false));
      Schema var93 = var0.addSchema(1961, SAME_NAMESPACED);
      var0.addFixer(new ChunkLightRemoveFix(var93, false));
      Schema var94 = var0.addSchema(1963, SAME_NAMESPACED);
      var0.addFixer(new RemoveGolemGossipFix(var94, false));
      Schema var95 = var0.addSchema(2100, V2100::new);
      var0.addFixer(new AddNewChoices(var95, "Added Bee and Bee Stinger", References.ENTITY));
      var0.addFixer(new AddNewChoices(var95, "Add beehive", References.BLOCK_ENTITY));
      var0.addFixer(
         new NamespacedTypeRenameFix(var95, "Rename sugar recipe", References.RECIPE, createRenamer("minecraft:sugar", "minecraft:sugar_from_sugar_cane"))
      );
      var0.addFixer(
         new AdvancementsRenameFix(
            var95, false, "Rename sugar recipe advancement", createRenamer("minecraft:recipes/misc/sugar", "minecraft:recipes/misc/sugar_from_sugar_cane")
         )
      );
      Schema var96 = var0.addSchema(2202, SAME_NAMESPACED);
      var0.addFixer(new ChunkBiomeFix(var96, false));
      Schema var97 = var0.addSchema(2209, SAME_NAMESPACED);
      UnaryOperator var98 = createRenamer("minecraft:bee_hive", "minecraft:beehive");
      var0.addFixer(ItemRenameFix.create(var97, "Rename bee_hive item to beehive", var98));
      var0.addFixer(new PoiTypeRenameFix(var97, "Rename bee_hive poi to beehive", var98));
      var0.addFixer(BlockRenameFix.create(var97, "Rename bee_hive block to beehive", var98));
      Schema var99 = var0.addSchema(2211, SAME_NAMESPACED);
      var0.addFixer(new StructureReferenceCountFix(var99, false));
      Schema var100 = var0.addSchema(2218, SAME_NAMESPACED);
      var0.addFixer(new ForcePoiRebuild(var100, false));
      Schema var101 = var0.addSchema(2501, V2501::new);
      var0.addFixer(new FurnaceRecipeFix(var101, true));
      Schema var102 = var0.addSchema(2502, V2502::new);
      var0.addFixer(new AddNewChoices(var102, "Added Hoglin", References.ENTITY));
      Schema var103 = var0.addSchema(2503, SAME_NAMESPACED);
      var0.addFixer(new WallPropertyFix(var103, false));
      var0.addFixer(
         new AdvancementsRenameFix(
            var103, false, "Composter category change", createRenamer("minecraft:recipes/misc/composter", "minecraft:recipes/decorations/composter")
         )
      );
      Schema var104 = var0.addSchema(2505, V2505::new);
      var0.addFixer(new AddNewChoices(var104, "Added Piglin", References.ENTITY));
      var0.addFixer(new MemoryExpiryDataFix(var104, "minecraft:villager"));
      Schema var105 = var0.addSchema(2508, SAME_NAMESPACED);
      var0.addFixer(
         ItemRenameFix.create(
            var105,
            "Renamed fungi items to fungus",
            createRenamer(ImmutableMap.of("minecraft:warped_fungi", "minecraft:warped_fungus", "minecraft:crimson_fungi", "minecraft:crimson_fungus"))
         )
      );
      var0.addFixer(
         BlockRenameFix.create(
            var105,
            "Renamed fungi blocks to fungus",
            createRenamer(ImmutableMap.of("minecraft:warped_fungi", "minecraft:warped_fungus", "minecraft:crimson_fungi", "minecraft:crimson_fungus"))
         )
      );
      Schema var106 = var0.addSchema(2509, V2509::new);
      var0.addFixer(new EntityZombifiedPiglinRenameFix(var106));
      var0.addFixer(ItemRenameFix.create(var106, "Rename zombie pigman egg item", createRenamer(EntityZombifiedPiglinRenameFix.RENAMED_IDS)));
      Schema var107 = var0.addSchema(2511, SAME_NAMESPACED);
      var0.addFixer(new EntityProjectileOwnerFix(var107));
      Schema var108 = var0.addSchema(2514, SAME_NAMESPACED);
      var0.addFixer(new EntityUUIDFix(var108));
      var0.addFixer(new BlockEntityUUIDFix(var108));
      var0.addFixer(new PlayerUUIDFix(var108));
      var0.addFixer(new LevelUUIDFix(var108));
      var0.addFixer(new SavedDataUUIDFix(var108));
      var0.addFixer(new ItemStackUUIDFix(var108));
      Schema var109 = var0.addSchema(2516, SAME_NAMESPACED);
      var0.addFixer(new GossipUUIDFix(var109, "minecraft:villager"));
      var0.addFixer(new GossipUUIDFix(var109, "minecraft:zombie_villager"));
      Schema var110 = var0.addSchema(2518, SAME_NAMESPACED);
      var0.addFixer(new JigsawPropertiesFix(var110, false));
      var0.addFixer(new JigsawRotationFix(var110, false));
      Schema var111 = var0.addSchema(2519, V2519::new);
      var0.addFixer(new AddNewChoices(var111, "Added Strider", References.ENTITY));
      Schema var112 = var0.addSchema(2522, V2522::new);
      var0.addFixer(new AddNewChoices(var112, "Added Zoglin", References.ENTITY));
      Schema var113 = var0.addSchema(2523, SAME_NAMESPACED);
      var0.addFixer(
         new AttributesRename(
            var113,
            "Attribute renames",
            createRenamerNoNamespace(
               ImmutableMap.builder()
                  .put("generic.maxHealth", "minecraft:generic.max_health")
                  .put("Max Health", "minecraft:generic.max_health")
                  .put("zombie.spawnReinforcements", "minecraft:zombie.spawn_reinforcements")
                  .put("Spawn Reinforcements Chance", "minecraft:zombie.spawn_reinforcements")
                  .put("horse.jumpStrength", "minecraft:horse.jump_strength")
                  .put("Jump Strength", "minecraft:horse.jump_strength")
                  .put("generic.followRange", "minecraft:generic.follow_range")
                  .put("Follow Range", "minecraft:generic.follow_range")
                  .put("generic.knockbackResistance", "minecraft:generic.knockback_resistance")
                  .put("Knockback Resistance", "minecraft:generic.knockback_resistance")
                  .put("generic.movementSpeed", "minecraft:generic.movement_speed")
                  .put("Movement Speed", "minecraft:generic.movement_speed")
                  .put("generic.flyingSpeed", "minecraft:generic.flying_speed")
                  .put("Flying Speed", "minecraft:generic.flying_speed")
                  .put("generic.attackDamage", "minecraft:generic.attack_damage")
                  .put("generic.attackKnockback", "minecraft:generic.attack_knockback")
                  .put("generic.attackSpeed", "minecraft:generic.attack_speed")
                  .put("generic.armorToughness", "minecraft:generic.armor_toughness")
                  .build()
            )
         )
      );
      Schema var114 = var0.addSchema(2527, SAME_NAMESPACED);
      var0.addFixer(new BitStorageAlignFix(var114));
      Schema var115 = var0.addSchema(2528, SAME_NAMESPACED);
      var0.addFixer(
         ItemRenameFix.create(
            var115,
            "Rename soul fire torch and soul fire lantern",
            createRenamer(ImmutableMap.of("minecraft:soul_fire_torch", "minecraft:soul_torch", "minecraft:soul_fire_lantern", "minecraft:soul_lantern"))
         )
      );
      var0.addFixer(
         BlockRenameFix.create(
            var115,
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
      Schema var116 = var0.addSchema(2529, SAME_NAMESPACED);
      var0.addFixer(new StriderGravityFix(var116, false));
      Schema var117 = var0.addSchema(2531, SAME_NAMESPACED);
      var0.addFixer(new RedstoneWireConnectionsFix(var117));
      Schema var118 = var0.addSchema(2533, SAME_NAMESPACED);
      var0.addFixer(new VillagerFollowRangeFix(var118));
      Schema var119 = var0.addSchema(2535, SAME_NAMESPACED);
      var0.addFixer(new EntityShulkerRotationFix(var119));
      Schema var120 = var0.addSchema(2538, SAME_NAMESPACED);
      var0.addFixer(new LevelLegacyWorldGenSettingsFix(var120));
      Schema var121 = var0.addSchema(2550, SAME_NAMESPACED);
      var0.addFixer(new WorldGenSettingsFix(var121));
      Schema var122 = var0.addSchema(2551, V2551::new);
      var0.addFixer(new WriteAndReadFix(var122, "add types to WorldGenData", References.WORLD_GEN_SETTINGS));
      Schema var123 = var0.addSchema(2552, SAME_NAMESPACED);
      var0.addFixer(new NamespacedTypeRenameFix(var123, "Nether biome rename", References.BIOME, createRenamer("minecraft:nether", "minecraft:nether_wastes")));
      Schema var124 = var0.addSchema(2553, SAME_NAMESPACED);
      var0.addFixer(new NamespacedTypeRenameFix(var124, "Biomes fix", References.BIOME, createRenamer(BiomeFix.BIOMES)));
      Schema var125 = var0.addSchema(2558, SAME_NAMESPACED);
      var0.addFixer(new MissingDimensionFix(var125, false));
      var0.addFixer(new OptionsRenameFieldFix(var125, false, "Rename swapHands setting", "key_key.swapHands", "key_key.swapOffhand"));
      Schema var126 = var0.addSchema(2568, V2568::new);
      var0.addFixer(new AddNewChoices(var126, "Added Piglin Brute", References.ENTITY));
      Schema var127 = var0.addSchema(2571, V2571::new);
      var0.addFixer(new AddNewChoices(var127, "Added Goat", References.ENTITY));
      Schema var128 = var0.addSchema(2679, SAME_NAMESPACED);
      var0.addFixer(new CauldronRenameFix(var128, false));
      Schema var129 = var0.addSchema(2680, SAME_NAMESPACED);
      var0.addFixer(ItemRenameFix.create(var129, "Renamed grass path item to dirt path", createRenamer("minecraft:grass_path", "minecraft:dirt_path")));
      var0.addFixer(BlockRenameFix.create(var129, "Renamed grass path block to dirt path", createRenamer("minecraft:grass_path", "minecraft:dirt_path")));
      Schema var130 = var0.addSchema(2684, V2684::new);
      var0.addFixer(new AddNewChoices(var130, "Added Sculk Sensor", References.BLOCK_ENTITY));
      Schema var131 = var0.addSchema(2686, V2686::new);
      var0.addFixer(new AddNewChoices(var131, "Added Axolotl", References.ENTITY));
      Schema var132 = var0.addSchema(2688, V2688::new);
      var0.addFixer(new AddNewChoices(var132, "Added Glow Squid", References.ENTITY));
      var0.addFixer(new AddNewChoices(var132, "Added Glow Item Frame", References.ENTITY));
      Schema var133 = var0.addSchema(2690, SAME_NAMESPACED);
      ImmutableMap var134 = ImmutableMap.builder()
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
      var0.addFixer(ItemRenameFix.create(var133, "Renamed copper block items to new oxidized terms", createRenamer(var134)));
      var0.addFixer(BlockRenameFix.create(var133, "Renamed copper blocks to new oxidized terms", createRenamer(var134)));
      Schema var135 = var0.addSchema(2691, SAME_NAMESPACED);
      ImmutableMap var136 = ImmutableMap.builder()
         .put("minecraft:waxed_copper", "minecraft:waxed_copper_block")
         .put("minecraft:oxidized_copper_block", "minecraft:oxidized_copper")
         .put("minecraft:weathered_copper_block", "minecraft:weathered_copper")
         .put("minecraft:exposed_copper_block", "minecraft:exposed_copper")
         .build();
      var0.addFixer(ItemRenameFix.create(var135, "Rename copper item suffixes", createRenamer(var136)));
      var0.addFixer(BlockRenameFix.create(var135, "Rename copper blocks suffixes", createRenamer(var136)));
      Schema var137 = var0.addSchema(2693, SAME_NAMESPACED);
      var0.addFixer(new AddFlagIfNotPresentFix(var137, References.WORLD_GEN_SETTINGS, "has_increased_height_already", false));
      Schema var138 = var0.addSchema(2696, SAME_NAMESPACED);
      ImmutableMap var139 = ImmutableMap.builder()
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
      var0.addFixer(ItemRenameFix.create(var138, "Renamed grimstone block items to deepslate", createRenamer(var139)));
      var0.addFixer(BlockRenameFix.create(var138, "Renamed grimstone blocks to deepslate", createRenamer(var139)));
      Schema var140 = var0.addSchema(2700, SAME_NAMESPACED);
      var0.addFixer(
         BlockRenameFix.create(
            var140,
            "Renamed cave vines blocks",
            createRenamer(ImmutableMap.of("minecraft:cave_vines_head", "minecraft:cave_vines", "minecraft:cave_vines_body", "minecraft:cave_vines_plant"))
         )
      );
      Schema var141 = var0.addSchema(2701, SAME_NAMESPACED);
      var0.addFixer(new SavedDataFeaturePoolElementFix(var141));
      Schema var142 = var0.addSchema(2702, SAME_NAMESPACED);
      var0.addFixer(new AbstractArrowPickupFix(var142));
      Schema var143 = var0.addSchema(2704, V2704::new);
      var0.addFixer(new AddNewChoices(var143, "Added Goat", References.ENTITY));
      Schema var144 = var0.addSchema(2707, V2707::new);
      var0.addFixer(new AddNewChoices(var144, "Added Marker", References.ENTITY));
      var0.addFixer(new AddFlagIfNotPresentFix(var144, References.WORLD_GEN_SETTINGS, "has_increased_height_already", true));
      Schema var145 = var0.addSchema(2710, SAME_NAMESPACED);
      var0.addFixer(
         new StatsRenameFix(var145, "Renamed play_one_minute stat to play_time", ImmutableMap.of("minecraft:play_one_minute", "minecraft:play_time"))
      );
      Schema var146 = var0.addSchema(2717, SAME_NAMESPACED);
      var0.addFixer(
         ItemRenameFix.create(
            var146, "Rename azalea_leaves_flowers", createRenamer(ImmutableMap.of("minecraft:azalea_leaves_flowers", "minecraft:flowering_azalea_leaves"))
         )
      );
      var0.addFixer(
         BlockRenameFix.create(
            var146,
            "Rename azalea_leaves_flowers items",
            createRenamer(ImmutableMap.of("minecraft:azalea_leaves_flowers", "minecraft:flowering_azalea_leaves"))
         )
      );
      Schema var147 = var0.addSchema(2825, SAME_NAMESPACED);
      var0.addFixer(new AddFlagIfNotPresentFix(var147, References.WORLD_GEN_SETTINGS, "has_increased_height_already", false));
      Schema var148 = var0.addSchema(2831, V2831::new);
      var0.addFixer(new SpawnerDataFix(var148));
      Schema var149 = var0.addSchema(2832, V2832::new);
      var0.addFixer(new WorldGenSettingsHeightAndBiomeFix(var149));
      var0.addFixer(new ChunkHeightAndBiomeFix(var149));
      Schema var150 = var0.addSchema(2833, SAME_NAMESPACED);
      var0.addFixer(new WorldGenSettingsDisallowOldCustomWorldsFix(var150));
      Schema var151 = var0.addSchema(2838, SAME_NAMESPACED);
      var0.addFixer(new NamespacedTypeRenameFix(var151, "Caves and Cliffs biome renames", References.BIOME, createRenamer(CavesAndCliffsRenames.RENAMES)));
      Schema var152 = var0.addSchema(2841, SAME_NAMESPACED);
      var0.addFixer(new ChunkProtoTickListFix(var152));
      Schema var153 = var0.addSchema(2842, V2842::new);
      var0.addFixer(new ChunkRenamesFix(var153));
      Schema var154 = var0.addSchema(2843, SAME_NAMESPACED);
      var0.addFixer(new OverreachingTickFix(var154));
      var0.addFixer(
         new NamespacedTypeRenameFix(var154, "Remove Deep Warm Ocean", References.BIOME, createRenamer("minecraft:deep_warm_ocean", "minecraft:warm_ocean"))
      );
      Schema var155 = var0.addSchema(2846, SAME_NAMESPACED);
      var0.addFixer(
         new AdvancementsRenameFix(
            var155,
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
      Schema var156 = var0.addSchema(2852, SAME_NAMESPACED);
      var0.addFixer(new WorldGenSettingsDisallowOldCustomWorldsFix(var156));
      Schema var157 = var0.addSchema(2967, SAME_NAMESPACED);
      var0.addFixer(new StructureSettingsFlattenFix(var157));
      Schema var158 = var0.addSchema(2970, SAME_NAMESPACED);
      var0.addFixer(new StructuresBecomeConfiguredFix(var158));
      Schema var159 = var0.addSchema(3076, V3076::new);
      var0.addFixer(new AddNewChoices(var159, "Added Sculk Catalyst", References.BLOCK_ENTITY));
      Schema var160 = var0.addSchema(3077, SAME_NAMESPACED);
      var0.addFixer(new ChunkDeleteIgnoredLightDataFix(var160));
      Schema var161 = var0.addSchema(3078, V3078::new);
      var0.addFixer(new AddNewChoices(var161, "Added Frog", References.ENTITY));
      var0.addFixer(new AddNewChoices(var161, "Added Tadpole", References.ENTITY));
      var0.addFixer(new AddNewChoices(var161, "Added Sculk Shrieker", References.BLOCK_ENTITY));
      Schema var162 = var0.addSchema(3081, V3081::new);
      var0.addFixer(new AddNewChoices(var162, "Added Warden", References.ENTITY));
      Schema var163 = var0.addSchema(3082, V3082::new);
      var0.addFixer(new AddNewChoices(var163, "Added Chest Boat", References.ENTITY));
      Schema var164 = var0.addSchema(3083, V3083::new);
      var0.addFixer(new AddNewChoices(var164, "Added Allay", References.ENTITY));
      Schema var165 = var0.addSchema(3084, SAME_NAMESPACED);
      var0.addFixer(
         new NamespacedTypeRenameFix(
            var165,
            "game_event_renames_3084",
            References.GAME_EVENT_NAME,
            createRenamer(
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
         )
      );
      Schema var166 = var0.addSchema(3086, SAME_NAMESPACED);
      var0.addFixer(
         new EntityVariantFix(
            var166, "Change cat variant type", References.ENTITY, "minecraft:cat", "CatType", Util.make(new Int2ObjectOpenHashMap(), var0x -> {
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
      ImmutableMap var167 = ImmutableMap.builder()
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
            var166, "Migrate cat variant advancement", "minecraft:husbandry/complete_catalogue", var1x -> (String)var167.getOrDefault(var1x, var1x)
         )
      );
      Schema var168 = var0.addSchema(3087, SAME_NAMESPACED);
      var0.addFixer(
         new EntityVariantFix(
            var168, "Change frog variant type", References.ENTITY, "minecraft:frog", "Variant", Util.make(new Int2ObjectOpenHashMap(), var0x -> {
               var0x.put(0, "minecraft:temperate");
               var0x.put(1, "minecraft:warm");
               var0x.put(2, "minecraft:cold");
            })::get
         )
      );
      Schema var169 = var0.addSchema(3090, SAME_NAMESPACED);
      var0.addFixer(new EntityPaintingFieldsRenameFix(var169));
      Schema var170 = var0.addSchema(3093, SAME_NAMESPACED);
      var0.addFixer(new EntityGoatMissingStateFix(var170));
      Schema var171 = var0.addSchema(3094, SAME_NAMESPACED);
      var0.addFixer(new GoatHornIdFix(var171));
      Schema var172 = var0.addSchema(3097, SAME_NAMESPACED);
      var0.addFixer(new FilteredBooksFix(var172));
      var0.addFixer(new FilteredSignsFix(var172));
      Map var173 = Map.of("minecraft:british", "minecraft:british_shorthair");
      var0.addFixer(new VariantRenameFix(var172, "Rename british shorthair", References.ENTITY, "minecraft:cat", var173));
      var0.addFixer(
         new CriteriaRenameFix(
            var172,
            "Migrate cat variant advancement for british shorthair",
            "minecraft:husbandry/complete_catalogue",
            var1x -> var173.getOrDefault(var1x, var1x)
         )
      );
      var0.addFixer(new PoiTypeRemoveFix(var172, "Remove unpopulated villager PoI types", Set.of("minecraft:unemployed", "minecraft:nitwit")::contains));
      Schema var174 = var0.addSchema(3108, SAME_NAMESPACED);
      var0.addFixer(new BlendingDataRemoveFromNetherEndFix(var174));
      Schema var175 = var0.addSchema(3201, SAME_NAMESPACED);
      var0.addFixer(new OptionsProgrammerArtFix(var175));
      Schema var176 = var0.addSchema(3202, V3202::new);
      var0.addFixer(new AddNewChoices(var176, "Added Hanging Sign", References.BLOCK_ENTITY));
      Schema var177 = var0.addSchema(3203, V3203::new);
      var0.addFixer(new AddNewChoices(var177, "Added Camel", References.ENTITY));
      Schema var178 = var0.addSchema(3204, V3204::new);
      var0.addFixer(new AddNewChoices(var178, "Added Chiseled Bookshelf", References.BLOCK_ENTITY));
      Schema var179 = var0.addSchema(3209, SAME_NAMESPACED);
      var0.addFixer(new ItemStackSpawnEggFix(var179, false, "minecraft:pig_spawn_egg"));
      Schema var180 = var0.addSchema(3214, SAME_NAMESPACED);
      var0.addFixer(new OptionsAmbientOcclusionFix(var180));
      Schema var181 = var0.addSchema(3319, SAME_NAMESPACED);
      var0.addFixer(new OptionsAccessibilityOnboardFix(var181));
      Schema var182 = var0.addSchema(3322, SAME_NAMESPACED);
      var0.addFixer(new EffectDurationFix(var182));
      Schema var183 = var0.addSchema(3325, V3325::new);
      var0.addFixer(new AddNewChoices(var183, "Added displays", References.ENTITY));
      Schema var184 = var0.addSchema(3326, V3326::new);
      var0.addFixer(new AddNewChoices(var184, "Added Sniffer", References.ENTITY));
      Schema var185 = var0.addSchema(3327, V3327::new);
      var0.addFixer(new AddNewChoices(var185, "Archaeology", References.BLOCK_ENTITY));
      Schema var186 = var0.addSchema(3328, V3328::new);
      var0.addFixer(new AddNewChoices(var186, "Added interaction", References.ENTITY));
      Schema var187 = var0.addSchema(3438, V3438::new);
      var0.addFixer(
         BlockEntityRenameFix.create(
            var187, "Rename Suspicious Sand to Brushable Block", createRenamer("minecraft:suspicious_sand", "minecraft:brushable_block")
         )
      );
      var0.addFixer(new EntityBrushableBlockFieldsRenameFix(var187));
      var0.addFixer(
         ItemRenameFix.create(
            var187,
            "Pottery shard renaming",
            createRenamer(
               ImmutableMap.of(
                  "minecraft:pottery_shard_archer",
                  "minecraft:archer_pottery_shard",
                  "minecraft:pottery_shard_prize",
                  "minecraft:prize_pottery_shard",
                  "minecraft:pottery_shard_arms_up",
                  "minecraft:arms_up_pottery_shard",
                  "minecraft:pottery_shard_skull",
                  "minecraft:skull_pottery_shard"
               )
            )
         )
      );
      var0.addFixer(new AddNewChoices(var187, "Added calibrated sculk sensor", References.BLOCK_ENTITY));
      Schema var188 = var0.addSchema(3439, SAME_NAMESPACED);
      var0.addFixer(new BlockEntitySignDoubleSidedEditableTextFix(var188, "Updated sign text format for Signs", "minecraft:sign"));
      var0.addFixer(new BlockEntitySignDoubleSidedEditableTextFix(var188, "Updated sign text format for Hanging Signs", "minecraft:hanging_sign"));
      Schema var189 = var0.addSchema(3440, SAME_NAMESPACED);
      var0.addFixer(
         new NamespacedTypeRenameFix(
            var189,
            "Replace experimental 1.20 overworld",
            References.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST,
            createRenamer("minecraft:overworld_update_1_20", "minecraft:overworld")
         )
      );
      var0.addFixer(new FeatureFlagRemoveFix(var189, "Remove 1.20 feature toggle", Set.of("minecraft:update_1_20")));
      Schema var190 = var0.addSchema(3441, SAME_NAMESPACED);
      var0.addFixer(new BlendingDataFix(var190));
      Schema var191 = var0.addSchema(3447, SAME_NAMESPACED);
      var0.addFixer(
         ItemRenameFix.create(
            var191,
            "Pottery shard item renaming to Pottery sherd",
            createRenamer(
               Stream.of(
                     "minecraft:angler_pottery_shard",
                     "minecraft:archer_pottery_shard",
                     "minecraft:arms_up_pottery_shard",
                     "minecraft:blade_pottery_shard",
                     "minecraft:brewer_pottery_shard",
                     "minecraft:burn_pottery_shard",
                     "minecraft:danger_pottery_shard",
                     "minecraft:explorer_pottery_shard",
                     "minecraft:friend_pottery_shard",
                     "minecraft:heart_pottery_shard",
                     "minecraft:heartbreak_pottery_shard",
                     "minecraft:howl_pottery_shard",
                     "minecraft:miner_pottery_shard",
                     "minecraft:mourner_pottery_shard",
                     "minecraft:plenty_pottery_shard",
                     "minecraft:prize_pottery_shard",
                     "minecraft:sheaf_pottery_shard",
                     "minecraft:shelter_pottery_shard",
                     "minecraft:skull_pottery_shard",
                     "minecraft:snort_pottery_shard"
                  )
                  .collect(Collectors.toMap(Function.identity(), var0x -> var0x.replace("_pottery_shard", "_pottery_sherd")))
            )
         )
      );
      Schema var192 = var0.addSchema(3448, V3448::new);
      var0.addFixer(new DecoratedPotFieldRenameFix(var192));
      Schema var193 = var0.addSchema(3450, SAME_NAMESPACED);
      var0.addFixer(
         new RemapChunkStatusFix(
            var193,
            "Remove liquid_carvers and heightmap chunk statuses",
            createRenamer(Map.of("minecraft:liquid_carvers", "minecraft:carvers", "minecraft:heightmaps", "minecraft:spawn"))
         )
      );
      Schema var194 = var0.addSchema(3451, SAME_NAMESPACED);
      var0.addFixer(new ChunkDeleteLightFix(var194));
      Schema var195 = var0.addSchema(3459, SAME_NAMESPACED);
      var0.addFixer(new LegacyDragonFightFix(var195));
      Schema var196 = var0.addSchema(3564, SAME_NAMESPACED);
      var0.addFixer(new DropInvalidSignDataFix(var196, "Drop invalid sign datafix data", "minecraft:sign"));
      var0.addFixer(new DropInvalidSignDataFix(var196, "Drop invalid hanging sign datafix data", "minecraft:hanging_sign"));
      Schema var197 = var0.addSchema(3565, SAME_NAMESPACED);
      var0.addFixer(new RandomSequenceSettingsFix(var197));
      Schema var198 = var0.addSchema(3566, SAME_NAMESPACED);
      var0.addFixer(new ScoreboardDisplaySlotFix(var198));
      Schema var199 = var0.addSchema(3568, SAME_NAMESPACED);
      var0.addFixer(new MobEffectIdFix(var199));
      Schema var200 = var0.addSchema(3682, V3682::new);
      var0.addFixer(new AddNewChoices(var200, "Added Crafter", References.BLOCK_ENTITY));
      Schema var201 = var0.addSchema(3683, V3683::new);
      var0.addFixer(new PrimedTntBlockStateFixer(var201));
      Schema var202 = var0.addSchema(3685, V3685::new);
      var0.addFixer(new FixProjectileStoredItem(var202));
      Schema var203 = var0.addSchema(3689, V3689::new);
      var0.addFixer(new AddNewChoices(var203, "Added Breeze", References.ENTITY));
      var0.addFixer(new AddNewChoices(var203, "Added Trial Spawner", References.BLOCK_ENTITY));
      Schema var204 = var0.addSchema(3692, SAME_NAMESPACED);
      UnaryOperator var205 = createRenamer(Map.of("minecraft:grass", "minecraft:short_grass"));
      var0.addFixer(BlockRenameFix.create(var204, "Rename grass block to short_grass", var205));
      var0.addFixer(ItemRenameFix.create(var204, "Rename grass item to short_grass", var205));
      Schema var206 = var0.addSchema(3799, V3799::new);
      var0.addFixer(new AddNewChoices(var206, "Added Armadillo", References.ENTITY));
      Schema var207 = var0.addSchema(3800, SAME_NAMESPACED);
      UnaryOperator var208 = createRenamer(Map.of("minecraft:scute", "minecraft:turtle_scute"));
      var0.addFixer(ItemRenameFix.create(var207, "Rename scute item to turtle_scute", var208));
      Schema var209 = var0.addSchema(3803, SAME_NAMESPACED);
      var0.addFixer(new RenameEnchantmentsFix(var209, "Rename sweeping enchant to sweeping_edge", Map.of("minecraft:sweeping", "minecraft:sweeping_edge")));
      Schema var210 = var0.addSchema(3807, V3807::new);
      var0.addFixer(new AddNewChoices(var210, "Added Vault", References.BLOCK_ENTITY));
      Schema var211 = var0.addSchema(3807, 1, SAME_NAMESPACED);
      var0.addFixer(new MapBannerBlockPosFormatFix(var211));
      Schema var212 = var0.addSchema(3808, V3808::new);
      var0.addFixer(new HorseBodyArmorItemFix(var212, "minecraft:horse", "ArmorItem", true));
      Schema var213 = var0.addSchema(3808, 1, V3808_1::new);
      var0.addFixer(new HorseBodyArmorItemFix(var213, "minecraft:llama", "DecorItem", false));
      Schema var214 = var0.addSchema(3808, 2, V3808_2::new);
      var0.addFixer(new HorseBodyArmorItemFix(var214, "minecraft:trader_llama", "DecorItem", false));
      Schema var215 = var0.addSchema(3809, SAME_NAMESPACED);
      var0.addFixer(new ChestedHorsesInventoryZeroIndexingFix(var215));
      Schema var216 = var0.addSchema(3812, SAME_NAMESPACED);
      var0.addFixer(new FixWolfHealth(var216));
      Schema var217 = var0.addSchema(3813, SAME_NAMESPACED);
      var0.addFixer(new BlockPosFormatAndRenamesFix(var217));
      Schema var218 = var0.addSchema(3814, SAME_NAMESPACED);
      var0.addFixer(
         new AttributesRename(var218, "Rename jump strength attribute", createRenamer("minecraft:horse.jump_strength", "minecraft:generic.jump_strength"))
      );
      Schema var219 = var0.addSchema(3816, V3816::new);
      var0.addFixer(new AddNewChoices(var219, "Added Bogged", References.ENTITY));
      Schema var220 = var0.addSchema(3818, V3818::new);
      var0.addFixer(new BeehiveFieldRenameFix(var220));
      var0.addFixer(new EmptyItemInHotbarFix(var220));
      Schema var221 = var0.addSchema(3818, 1, SAME_NAMESPACED);
      var0.addFixer(new BannerPatternFormatFix(var221));
      Schema var222 = var0.addSchema(3818, 2, SAME_NAMESPACED);
      var0.addFixer(new TippedArrowPotionToItemFix(var222));
      Schema var223 = var0.addSchema(3818, 3, V3818_3::new);
      var0.addFixer(new WriteAndReadFix(var223, "Inject data component types", References.DATA_COMPONENTS));
      Schema var224 = var0.addSchema(3818, 4, V3818_4::new);
      var0.addFixer(new ParticleUnflatteningFix(var224));
      Schema var225 = var0.addSchema(3818, 5, V3818_5::new);
      var0.addFixer(new ItemStackComponentizationFix(var225));
      Schema var226 = var0.addSchema(3818, 6, SAME_NAMESPACED);
      var0.addFixer(new AreaEffectCloudPotionFix(var226));
      Schema var227 = var0.addSchema(3820, SAME_NAMESPACED);
      var0.addFixer(new PlayerHeadBlockProfileFix(var227));
      var0.addFixer(new LodestoneCompassComponentFix(var227));
      Schema var228 = var0.addSchema(3825, V3825::new);
      var0.addFixer(new ItemStackCustomNameToOverrideComponentFix(var228));
      var0.addFixer(new BannerEntityCustomNameToOverrideComponentFix(var228));
      var0.addFixer(new TrialSpawnerConfigFix(var228));
      var0.addFixer(new AddNewChoices(var228, "Added Ominous Item Spawner", References.ENTITY));
      Schema var229 = var0.addSchema(3828, SAME_NAMESPACED);
      var0.addFixer(new EmptyItemInVillagerTradeFix(var229));
      Schema var230 = var0.addSchema(3833, SAME_NAMESPACED);
      var0.addFixer(new RemoveEmptyItemInBrushableBlockFix(var230));
   }

   private static UnaryOperator<String> createRenamerNoNamespace(Map<String, String> var0) {
      return var1 -> var0.getOrDefault(var1, var1);
   }

   private static UnaryOperator<String> createRenamer(Map<String, String> var0) {
      return var1 -> var0.getOrDefault(NamespacedSchema.ensureNamespaced(var1), var1);
   }

   private static UnaryOperator<String> createRenamer(String var0, String var1) {
      return var2 -> Objects.equals(NamespacedSchema.ensureNamespaced(var2), var0) ? var1 : var2;
   }
}
