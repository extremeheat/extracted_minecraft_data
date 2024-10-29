package net.minecraft.client.model.geom;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.model.AbstractEquineModel;
import net.minecraft.client.model.AllayModel;
import net.minecraft.client.model.ArmadilloModel;
import net.minecraft.client.model.ArmorStandArmorModel;
import net.minecraft.client.model.ArmorStandModel;
import net.minecraft.client.model.ArrowModel;
import net.minecraft.client.model.AxolotlModel;
import net.minecraft.client.model.BatModel;
import net.minecraft.client.model.BeeModel;
import net.minecraft.client.model.BeeStingerModel;
import net.minecraft.client.model.BellModel;
import net.minecraft.client.model.BlazeModel;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.BoggedModel;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.BreezeModel;
import net.minecraft.client.model.CamelModel;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.model.ChestModel;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.CodModel;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.CreakingModel;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.DolphinModel;
import net.minecraft.client.model.DonkeyModel;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EndCrystalModel;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.EndermiteModel;
import net.minecraft.client.model.EvokerFangsModel;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.model.FrogModel;
import net.minecraft.client.model.GhastModel;
import net.minecraft.client.model.GoatModel;
import net.minecraft.client.model.GuardianModel;
import net.minecraft.client.model.HoglinModel;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.LavaSlimeModel;
import net.minecraft.client.model.LeashKnotModel;
import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.model.LlamaSpitModel;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.model.PandaModel;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.PiglinHeadModel;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.PlayerCapeModel;
import net.minecraft.client.model.PlayerEarsModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.PolarBearModel;
import net.minecraft.client.model.PufferfishBigModel;
import net.minecraft.client.model.PufferfishMidModel;
import net.minecraft.client.model.PufferfishSmallModel;
import net.minecraft.client.model.RabbitModel;
import net.minecraft.client.model.RaftModel;
import net.minecraft.client.model.RavagerModel;
import net.minecraft.client.model.SalmonModel;
import net.minecraft.client.model.SheepFurModel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.ShulkerBulletModel;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.SilverfishModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.SnifferModel;
import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.SpinAttackEffectModel;
import net.minecraft.client.model.SquidModel;
import net.minecraft.client.model.StriderModel;
import net.minecraft.client.model.TadpoleModel;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.model.TurtleModel;
import net.minecraft.client.model.VexModel;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.WardenModel;
import net.minecraft.client.model.WindChargeModel;
import net.minecraft.client.model.WitchModel;
import net.minecraft.client.model.WitherBossModel;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.ZombieVillagerModel;
import net.minecraft.client.model.dragon.DragonHeadModel;
import net.minecraft.client.model.dragon.EnderDragonModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.client.renderer.blockentity.ConduitRenderer;
import net.minecraft.client.renderer.blockentity.DecoratedPotRenderer;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.WitherSkullRenderer;
import net.minecraft.world.level.block.state.properties.WoodType;

public class LayerDefinitions {
   private static final CubeDeformation FISH_PATTERN_DEFORMATION = new CubeDeformation(0.008F);
   private static final CubeDeformation OUTER_ARMOR_DEFORMATION = new CubeDeformation(1.0F);
   private static final CubeDeformation INNER_ARMOR_DEFORMATION = new CubeDeformation(0.5F);

   public LayerDefinitions() {
      super();
   }

   public static Map<ModelLayerLocation, LayerDefinition> createRoots() {
      ImmutableMap.Builder var0 = ImmutableMap.builder();
      LayerDefinition var1 = LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F), 64, 64);
      LayerDefinition var2 = LayerDefinition.create(HumanoidArmorModel.createBodyLayer(OUTER_ARMOR_DEFORMATION), 64, 32);
      LayerDefinition var3 = LayerDefinition.create(HumanoidArmorModel.createBodyLayer(new CubeDeformation(1.02F)), 64, 32);
      LayerDefinition var4 = LayerDefinition.create(HumanoidArmorModel.createBodyLayer(INNER_ARMOR_DEFORMATION), 64, 32);
      LayerDefinition var5 = MinecartModel.createBodyLayer();
      LayerDefinition var6 = SkullModel.createMobHeadLayer();
      LayerDefinition var7 = LayerDefinition.create(AbstractEquineModel.createBodyMesh(CubeDeformation.NONE), 64, 64);
      LayerDefinition var8 = LayerDefinition.create(AbstractEquineModel.createBabyMesh(CubeDeformation.NONE), 64, 64);
      MeshTransformer var9 = MeshTransformer.scaling(0.9375F);
      LayerDefinition var10 = IllagerModel.createBodyLayer().apply(var9);
      LayerDefinition var11 = AxolotlModel.createBodyLayer();
      LayerDefinition var12 = BeeModel.createBodyLayer();
      LayerDefinition var13 = CowModel.createBodyLayer();
      LayerDefinition var14 = var13.apply(CowModel.BABY_TRANSFORMER);
      LayerDefinition var15 = ElytraModel.createLayer();
      LayerDefinition var16 = LayerDefinition.create(OcelotModel.createBodyMesh(CubeDeformation.NONE), 64, 32);
      LayerDefinition var17 = var16.apply(CatModel.CAT_TRANSFORMER);
      LayerDefinition var18 = LayerDefinition.create(OcelotModel.createBodyMesh(new CubeDeformation(0.01F)), 64, 32).apply(CatModel.CAT_TRANSFORMER);
      LayerDefinition var19 = LayerDefinition.create(PiglinModel.createMesh(CubeDeformation.NONE), 64, 64);
      LayerDefinition var20 = LayerDefinition.create(PiglinHeadModel.createHeadModel(), 64, 64);
      LayerDefinition var21 = SkullModel.createHumanoidHeadLayer();
      LayerDefinition var22 = LlamaModel.createBodyLayer(CubeDeformation.NONE);
      LayerDefinition var23 = LlamaModel.createBodyLayer(new CubeDeformation(0.5F));
      LayerDefinition var24 = StriderModel.createBodyLayer();
      LayerDefinition var25 = HoglinModel.createBodyLayer();
      LayerDefinition var26 = HoglinModel.createBabyLayer();
      LayerDefinition var27 = SkeletonModel.createBodyLayer();
      LayerDefinition var28 = LayerDefinition.create(VillagerModel.createBodyModel(), 64, 64).apply(var9);
      LayerDefinition var29 = SpiderModel.createSpiderBodyLayer();
      LayerDefinition var30 = ArmadilloModel.createBodyLayer();
      LayerDefinition var31 = CamelModel.createBodyLayer();
      LayerDefinition var32 = ChickenModel.createBodyLayer();
      LayerDefinition var33 = GoatModel.createBodyLayer();
      LayerDefinition var34 = PandaModel.createBodyLayer();
      LayerDefinition var35 = PigModel.createBodyLayer(CubeDeformation.NONE);
      LayerDefinition var36 = PigModel.createBodyLayer(new CubeDeformation(0.5F));
      LayerDefinition var37 = PolarBearModel.createBodyLayer();
      LayerDefinition var38 = SheepModel.createBodyLayer();
      LayerDefinition var39 = SheepFurModel.createFurLayer();
      LayerDefinition var40 = SnifferModel.createBodyLayer();
      LayerDefinition var41 = TurtleModel.createBodyLayer();
      LayerDefinition var42 = LayerDefinition.create(WolfModel.createMeshDefinition(CubeDeformation.NONE), 64, 32);
      LayerDefinition var43 = LayerDefinition.create(WolfModel.createMeshDefinition(new CubeDeformation(0.2F)), 64, 32);
      LayerDefinition var44 = ZombieVillagerModel.createBodyLayer();
      LayerDefinition var45 = ArmorStandModel.createBodyLayer();
      LayerDefinition var46 = ArmorStandArmorModel.createBodyLayer(INNER_ARMOR_DEFORMATION);
      LayerDefinition var47 = ArmorStandArmorModel.createBodyLayer(OUTER_ARMOR_DEFORMATION);
      LayerDefinition var48 = DrownedModel.createBodyLayer(CubeDeformation.NONE);
      LayerDefinition var49 = DrownedModel.createBodyLayer(new CubeDeformation(0.25F));
      LayerDefinition var50 = SquidModel.createBodyLayer();
      LayerDefinition var51 = DolphinModel.createBodyLayer();
      LayerDefinition var52 = SalmonModel.createBodyLayer();
      var0.put(ModelLayers.ALLAY, AllayModel.createBodyLayer());
      var0.put(ModelLayers.ARMADILLO, var30);
      var0.put(ModelLayers.ARMADILLO_BABY, var30.apply(ArmadilloModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.ARMOR_STAND, var45);
      var0.put(ModelLayers.ARMOR_STAND_INNER_ARMOR, var46);
      var0.put(ModelLayers.ARMOR_STAND_OUTER_ARMOR, var47);
      var0.put(ModelLayers.ARMOR_STAND_SMALL, var45.apply(HumanoidModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.ARMOR_STAND_SMALL_INNER_ARMOR, var46.apply(HumanoidModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.ARMOR_STAND_SMALL_OUTER_ARMOR, var47.apply(HumanoidModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.ARROW, ArrowModel.createBodyLayer());
      var0.put(ModelLayers.AXOLOTL, var11);
      var0.put(ModelLayers.AXOLOTL_BABY, var11.apply(AxolotlModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.BANNER, BannerRenderer.createBodyLayer());
      var0.put(ModelLayers.BAT, BatModel.createBodyLayer());
      var0.put(ModelLayers.BED_FOOT, BedRenderer.createFootLayer());
      var0.put(ModelLayers.BED_HEAD, BedRenderer.createHeadLayer());
      var0.put(ModelLayers.BEE, var12);
      var0.put(ModelLayers.BEE_BABY, var12.apply(BeeModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.BEE_STINGER, BeeStingerModel.createBodyLayer());
      var0.put(ModelLayers.BELL, BellModel.createBodyLayer());
      var0.put(ModelLayers.BLAZE, BlazeModel.createBodyLayer());
      var0.put(ModelLayers.BOAT_WATER_PATCH, BoatModel.createWaterPatch());
      var0.put(ModelLayers.BOGGED, BoggedModel.createBodyLayer());
      var0.put(ModelLayers.BOGGED_INNER_ARMOR, var4);
      var0.put(ModelLayers.BOGGED_OUTER_ARMOR, var2);
      var0.put(ModelLayers.BOGGED_OUTER_LAYER, LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(0.2F), 0.0F), 64, 32));
      var0.put(ModelLayers.BOOK, BookModel.createBodyLayer());
      var0.put(ModelLayers.BREEZE, BreezeModel.createBodyLayer(32, 32));
      var0.put(ModelLayers.BREEZE_WIND, BreezeModel.createBodyLayer(128, 128));
      var0.put(ModelLayers.CAT, var17);
      var0.put(ModelLayers.CAT_BABY, var17.apply(CatModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.CAT_COLLAR, var18);
      var0.put(ModelLayers.CAT_BABY_COLLAR, var18.apply(CatModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.CAMEL, var31);
      var0.put(ModelLayers.CAMEL_BABY, var31.apply(CamelModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.CAVE_SPIDER, var29.apply(MeshTransformer.scaling(0.7F)));
      var0.put(ModelLayers.CHEST, ChestModel.createSingleBodyLayer());
      var0.put(ModelLayers.CHEST_MINECART, var5);
      var0.put(ModelLayers.CHICKEN, var32);
      var0.put(ModelLayers.CHICKEN_BABY, var32.apply(ChickenModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.COD, CodModel.createBodyLayer());
      var0.put(ModelLayers.COMMAND_BLOCK_MINECART, var5);
      var0.put(ModelLayers.CONDUIT_EYE, ConduitRenderer.createEyeLayer());
      var0.put(ModelLayers.CONDUIT_WIND, ConduitRenderer.createWindLayer());
      var0.put(ModelLayers.CONDUIT_SHELL, ConduitRenderer.createShellLayer());
      var0.put(ModelLayers.CONDUIT_CAGE, ConduitRenderer.createCageLayer());
      var0.put(ModelLayers.COW, var13);
      var0.put(ModelLayers.COW_BABY, var14);
      var0.put(ModelLayers.CREAKING, CreakingModel.createBodyLayer());
      var0.put(ModelLayers.CREEPER, CreeperModel.createBodyLayer(CubeDeformation.NONE));
      var0.put(ModelLayers.CREEPER_ARMOR, CreeperModel.createBodyLayer(new CubeDeformation(2.0F)));
      var0.put(ModelLayers.CREEPER_HEAD, var6);
      var0.put(ModelLayers.DECORATED_POT_BASE, DecoratedPotRenderer.createBaseLayer());
      var0.put(ModelLayers.DECORATED_POT_SIDES, DecoratedPotRenderer.createSidesLayer());
      var0.put(ModelLayers.DOLPHIN, var51);
      var0.put(ModelLayers.DOLPHIN_BABY, var51.apply(DolphinModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.DONKEY, DonkeyModel.createBodyLayer());
      var0.put(ModelLayers.DONKEY_BABY, DonkeyModel.createBabyLayer());
      var0.put(ModelLayers.DOUBLE_CHEST_LEFT, ChestModel.createDoubleBodyLeftLayer());
      var0.put(ModelLayers.DOUBLE_CHEST_RIGHT, ChestModel.createDoubleBodyRightLayer());
      var0.put(ModelLayers.DRAGON_SKULL, DragonHeadModel.createHeadLayer());
      var0.put(ModelLayers.DROWNED, var48);
      var0.put(ModelLayers.DROWNED_INNER_ARMOR, var4);
      var0.put(ModelLayers.DROWNED_OUTER_ARMOR, var4);
      var0.put(ModelLayers.DROWNED_OUTER_LAYER, var49);
      var0.put(ModelLayers.DROWNED_BABY, var48.apply(HumanoidModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.DROWNED_BABY_INNER_ARMOR, var4.apply(HumanoidModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.DROWNED_BABY_OUTER_ARMOR, var4.apply(HumanoidModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.DROWNED_BABY_OUTER_LAYER, var49.apply(HumanoidModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.ELDER_GUARDIAN, GuardianModel.createElderGuardianLayer());
      var0.put(ModelLayers.ELYTRA, var15);
      var0.put(ModelLayers.ELYTRA_BABY, var15.apply(ElytraModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.ENDERMAN, EndermanModel.createBodyLayer());
      var0.put(ModelLayers.ENDERMITE, EndermiteModel.createBodyLayer());
      var0.put(ModelLayers.ENDER_DRAGON, EnderDragonModel.createBodyLayer());
      var0.put(ModelLayers.END_CRYSTAL, EndCrystalModel.createBodyLayer());
      var0.put(ModelLayers.EVOKER, var10);
      var0.put(ModelLayers.EVOKER_FANGS, EvokerFangsModel.createBodyLayer());
      var0.put(ModelLayers.FOX, FoxModel.createBodyLayer());
      var0.put(ModelLayers.FOX_BABY, FoxModel.createBodyLayer().apply(FoxModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.FROG, FrogModel.createBodyLayer());
      var0.put(ModelLayers.FURNACE_MINECART, var5);
      var0.put(ModelLayers.GHAST, GhastModel.createBodyLayer());
      MeshTransformer var53 = MeshTransformer.scaling(6.0F);
      var0.put(ModelLayers.GIANT, var1.apply(var53));
      var0.put(ModelLayers.GIANT_INNER_ARMOR, var4.apply(var53));
      var0.put(ModelLayers.GIANT_OUTER_ARMOR, var2.apply(var53));
      var0.put(ModelLayers.GLOW_SQUID, var50);
      var0.put(ModelLayers.GLOW_SQUID_BABY, var50.apply(SquidModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.GOAT, var33);
      var0.put(ModelLayers.GOAT_BABY, var33.apply(GoatModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.GUARDIAN, GuardianModel.createBodyLayer());
      var0.put(ModelLayers.HOGLIN, var25);
      var0.put(ModelLayers.HOGLIN_BABY, var26);
      var0.put(ModelLayers.HOPPER_MINECART, var5);
      var0.put(ModelLayers.HORSE, var7);
      var0.put(ModelLayers.HORSE_BABY, var8);
      var0.put(ModelLayers.HORSE_ARMOR, LayerDefinition.create(AbstractEquineModel.createBodyMesh(new CubeDeformation(0.1F)), 64, 64));
      var0.put(ModelLayers.HORSE_BABY_ARMOR, LayerDefinition.create(AbstractEquineModel.createBabyMesh(new CubeDeformation(0.1F)), 64, 64));
      MeshTransformer var54 = MeshTransformer.scaling(1.0625F);
      var0.put(ModelLayers.HUSK, var1.apply(var54));
      var0.put(ModelLayers.HUSK_INNER_ARMOR, var4.apply(var54));
      var0.put(ModelLayers.HUSK_OUTER_ARMOR, var2.apply(var54));
      var0.put(ModelLayers.HUSK_BABY, var1.apply(HumanoidModel.BABY_TRANSFORMER).apply(var54));
      var0.put(ModelLayers.HUSK_BABY_INNER_ARMOR, var4.apply(HumanoidModel.BABY_TRANSFORMER).apply(var54));
      var0.put(ModelLayers.HUSK_BABY_OUTER_ARMOR, var2.apply(HumanoidModel.BABY_TRANSFORMER).apply(var54));
      var0.put(ModelLayers.ILLUSIONER, var10);
      var0.put(ModelLayers.IRON_GOLEM, IronGolemModel.createBodyLayer());
      var0.put(ModelLayers.LEASH_KNOT, LeashKnotModel.createBodyLayer());
      var0.put(ModelLayers.LLAMA, var22);
      var0.put(ModelLayers.LLAMA_BABY, var22.apply(LlamaModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.LLAMA_DECOR, var23);
      var0.put(ModelLayers.LLAMA_BABY_DECOR, var23.apply(LlamaModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.LLAMA_SPIT, LlamaSpitModel.createBodyLayer());
      var0.put(ModelLayers.MAGMA_CUBE, LavaSlimeModel.createBodyLayer());
      var0.put(ModelLayers.MINECART, var5);
      var0.put(ModelLayers.MOOSHROOM, var13);
      var0.put(ModelLayers.MOOSHROOM_BABY, var14);
      var0.put(ModelLayers.MULE, DonkeyModel.createBodyLayer());
      var0.put(ModelLayers.MULE_BABY, DonkeyModel.createBabyLayer());
      var0.put(ModelLayers.OCELOT, var16);
      var0.put(ModelLayers.OCELOT_BABY, var16.apply(OcelotModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.PANDA, var34);
      var0.put(ModelLayers.PANDA_BABY, var34.apply(PandaModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.PARROT, ParrotModel.createBodyLayer());
      var0.put(ModelLayers.PHANTOM, PhantomModel.createBodyLayer());
      var0.put(ModelLayers.PIG, var35);
      var0.put(ModelLayers.PIG_BABY, var35.apply(PigModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.PIG_SADDLE, var36);
      var0.put(ModelLayers.PIG_BABY_SADDLE, var36.apply(PigModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.PIGLIN, var19);
      var0.put(ModelLayers.PIGLIN_INNER_ARMOR, var4);
      var0.put(ModelLayers.PIGLIN_OUTER_ARMOR, var3);
      var0.put(ModelLayers.PIGLIN_BRUTE, var19);
      var0.put(ModelLayers.PIGLIN_BRUTE_INNER_ARMOR, var4);
      var0.put(ModelLayers.PIGLIN_BRUTE_OUTER_ARMOR, var3);
      var0.put(ModelLayers.PIGLIN_BABY, var19.apply(HumanoidModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.PIGLIN_BABY_INNER_ARMOR, var4.apply(HumanoidModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.PIGLIN_BABY_OUTER_ARMOR, var3.apply(HumanoidModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.PIGLIN_HEAD, var20);
      var0.put(ModelLayers.PILLAGER, var10);
      var0.put(ModelLayers.PLAYER, LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, false), 64, 64));
      var0.put(ModelLayers.PLAYER_EARS, PlayerEarsModel.createEarsLayer());
      var0.put(ModelLayers.PLAYER_CAPE, PlayerCapeModel.createCapeLayer());
      var0.put(ModelLayers.PLAYER_HEAD, var21);
      var0.put(ModelLayers.PLAYER_INNER_ARMOR, var4);
      var0.put(ModelLayers.PLAYER_OUTER_ARMOR, var2);
      var0.put(ModelLayers.PLAYER_SLIM, LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, true), 64, 64));
      var0.put(ModelLayers.PLAYER_SLIM_INNER_ARMOR, var4);
      var0.put(ModelLayers.PLAYER_SLIM_OUTER_ARMOR, var2);
      var0.put(ModelLayers.PLAYER_SPIN_ATTACK, SpinAttackEffectModel.createLayer());
      var0.put(ModelLayers.POLAR_BEAR, var37);
      var0.put(ModelLayers.POLAR_BEAR_BABY, var37.apply(PolarBearModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.PUFFERFISH_BIG, PufferfishBigModel.createBodyLayer());
      var0.put(ModelLayers.PUFFERFISH_MEDIUM, PufferfishMidModel.createBodyLayer());
      var0.put(ModelLayers.PUFFERFISH_SMALL, PufferfishSmallModel.createBodyLayer());
      var0.put(ModelLayers.RABBIT, RabbitModel.createBodyLayer(false));
      var0.put(ModelLayers.RABBIT_BABY, RabbitModel.createBodyLayer(true));
      var0.put(ModelLayers.RAVAGER, RavagerModel.createBodyLayer());
      var0.put(ModelLayers.SALMON, var52);
      var0.put(ModelLayers.SALMON_SMALL, var52.apply(SalmonModel.SMALL_TRANSFORMER));
      var0.put(ModelLayers.SALMON_LARGE, var52.apply(SalmonModel.LARGE_TRANSFORMER));
      var0.put(ModelLayers.SHEEP, var38);
      var0.put(ModelLayers.SHEEP_BABY, var38.apply(SheepModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.SHEEP_WOOL, var39);
      var0.put(ModelLayers.SHEEP_BABY_WOOL, var39.apply(SheepModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.SHIELD, ShieldModel.createLayer());
      var0.put(ModelLayers.SHULKER, ShulkerModel.createBodyLayer());
      var0.put(ModelLayers.SHULKER_BOX, ShulkerModel.createBoxLayer());
      var0.put(ModelLayers.SHULKER_BULLET, ShulkerBulletModel.createBodyLayer());
      var0.put(ModelLayers.SILVERFISH, SilverfishModel.createBodyLayer());
      var0.put(ModelLayers.SKELETON, var27);
      var0.put(ModelLayers.SKELETON_INNER_ARMOR, var4);
      var0.put(ModelLayers.SKELETON_OUTER_ARMOR, var2);
      var0.put(ModelLayers.SKELETON_HORSE, var7);
      var0.put(ModelLayers.SKELETON_HORSE_BABY, var8);
      var0.put(ModelLayers.SKELETON_SKULL, var6);
      var0.put(ModelLayers.SLIME, SlimeModel.createInnerBodyLayer());
      var0.put(ModelLayers.SLIME_OUTER, SlimeModel.createOuterBodyLayer());
      var0.put(ModelLayers.SNIFFER, var40);
      var0.put(ModelLayers.SNIFFER_BABY, var40.apply(SnifferModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.SNOW_GOLEM, SnowGolemModel.createBodyLayer());
      var0.put(ModelLayers.SPAWNER_MINECART, var5);
      var0.put(ModelLayers.SPIDER, var29);
      var0.put(ModelLayers.SQUID, var50);
      var0.put(ModelLayers.SQUID_BABY, var50.apply(SquidModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.STRAY, var27);
      var0.put(ModelLayers.STRAY_INNER_ARMOR, var4);
      var0.put(ModelLayers.STRAY_OUTER_ARMOR, var2);
      var0.put(ModelLayers.STRAY_OUTER_LAYER, LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(0.25F), 0.0F), 64, 32));
      var0.put(ModelLayers.STRIDER, var24);
      var0.put(ModelLayers.STRIDER_SADDLE, var24);
      var0.put(ModelLayers.TADPOLE, TadpoleModel.createBodyLayer());
      var0.put(ModelLayers.TNT_MINECART, var5);
      var0.put(ModelLayers.TRADER_LLAMA, var22);
      var0.put(ModelLayers.TRADER_LLAMA_BABY, var22.apply(LlamaModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.TRIDENT, TridentModel.createLayer());
      var0.put(ModelLayers.TROPICAL_FISH_LARGE, TropicalFishModelB.createBodyLayer(CubeDeformation.NONE));
      var0.put(ModelLayers.TROPICAL_FISH_LARGE_PATTERN, TropicalFishModelB.createBodyLayer(FISH_PATTERN_DEFORMATION));
      var0.put(ModelLayers.TROPICAL_FISH_SMALL, TropicalFishModelA.createBodyLayer(CubeDeformation.NONE));
      var0.put(ModelLayers.TROPICAL_FISH_SMALL_PATTERN, TropicalFishModelA.createBodyLayer(FISH_PATTERN_DEFORMATION));
      var0.put(ModelLayers.TURTLE, var41);
      var0.put(ModelLayers.TURTLE_BABY, var41.apply(TurtleModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.VEX, VexModel.createBodyLayer());
      var0.put(ModelLayers.VILLAGER, var28);
      var0.put(ModelLayers.VINDICATOR, var10);
      var0.put(ModelLayers.WARDEN, WardenModel.createBodyLayer());
      var0.put(ModelLayers.WANDERING_TRADER, var28);
      var0.put(ModelLayers.WIND_CHARGE, WindChargeModel.createBodyLayer());
      var0.put(ModelLayers.WITCH, WitchModel.createBodyLayer().apply(var9));
      var0.put(ModelLayers.WITHER, WitherBossModel.createBodyLayer(CubeDeformation.NONE));
      var0.put(ModelLayers.WITHER_ARMOR, WitherBossModel.createBodyLayer(INNER_ARMOR_DEFORMATION));
      var0.put(ModelLayers.WITHER_SKULL, WitherSkullRenderer.createSkullLayer());
      MeshTransformer var55 = MeshTransformer.scaling(1.2F);
      var0.put(ModelLayers.WITHER_SKELETON, var27.apply(var55));
      var0.put(ModelLayers.WITHER_SKELETON_INNER_ARMOR, var4.apply(var55));
      var0.put(ModelLayers.WITHER_SKELETON_OUTER_ARMOR, var2.apply(var55));
      var0.put(ModelLayers.WITHER_SKELETON_SKULL, var6);
      var0.put(ModelLayers.WOLF, var42);
      var0.put(ModelLayers.WOLF_ARMOR, var43);
      var0.put(ModelLayers.WOLF_BABY, var42.apply(WolfModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.WOLF_BABY_ARMOR, var43.apply(WolfModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.ZOGLIN, var25);
      var0.put(ModelLayers.ZOGLIN_BABY, var26);
      var0.put(ModelLayers.ZOMBIE, var1);
      var0.put(ModelLayers.ZOMBIE_INNER_ARMOR, var4);
      var0.put(ModelLayers.ZOMBIE_OUTER_ARMOR, var2);
      var0.put(ModelLayers.ZOMBIE_BABY, var1.apply(HumanoidModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.ZOMBIE_BABY_INNER_ARMOR, var4.apply(HumanoidModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.ZOMBIE_BABY_OUTER_ARMOR, var2.apply(HumanoidModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.ZOMBIE_HEAD, var21);
      var0.put(ModelLayers.ZOMBIE_HORSE, var7);
      var0.put(ModelLayers.ZOMBIE_HORSE_BABY, var8);
      var0.put(ModelLayers.ZOMBIE_VILLAGER, var44);
      var0.put(ModelLayers.ZOMBIE_VILLAGER_INNER_ARMOR, ZombieVillagerModel.createArmorLayer(INNER_ARMOR_DEFORMATION));
      var0.put(ModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR, ZombieVillagerModel.createArmorLayer(OUTER_ARMOR_DEFORMATION));
      var0.put(ModelLayers.ZOMBIE_VILLAGER_BABY, var44.apply(HumanoidModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.ZOMBIE_VILLAGER_BABY_INNER_ARMOR, ZombieVillagerModel.createArmorLayer(INNER_ARMOR_DEFORMATION).apply(HumanoidModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.ZOMBIE_VILLAGER_BABY_OUTER_ARMOR, ZombieVillagerModel.createArmorLayer(OUTER_ARMOR_DEFORMATION).apply(HumanoidModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.ZOMBIFIED_PIGLIN, var19);
      var0.put(ModelLayers.ZOMBIFIED_PIGLIN_INNER_ARMOR, var4);
      var0.put(ModelLayers.ZOMBIFIED_PIGLIN_OUTER_ARMOR, var3);
      var0.put(ModelLayers.ZOMBIFIED_PIGLIN_BABY, var19.apply(HumanoidModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.ZOMBIFIED_PIGLIN_BABY_INNER_ARMOR, var4.apply(HumanoidModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.ZOMBIFIED_PIGLIN_BABY_OUTER_ARMOR, var3.apply(HumanoidModel.BABY_TRANSFORMER));
      var0.put(ModelLayers.BAMBOO_RAFT, RaftModel.createRaftModel());
      var0.put(ModelLayers.BAMBOO_CHEST_RAFT, RaftModel.createChestRaftModel());
      LayerDefinition var56 = BoatModel.createBoatModel();
      LayerDefinition var57 = BoatModel.createChestBoatModel();
      var0.put(ModelLayers.OAK_BOAT, var56);
      var0.put(ModelLayers.OAK_CHEST_BOAT, var57);
      var0.put(ModelLayers.SPRUCE_BOAT, var56);
      var0.put(ModelLayers.SPRUCE_CHEST_BOAT, var57);
      var0.put(ModelLayers.BIRCH_BOAT, var56);
      var0.put(ModelLayers.BIRCH_CHEST_BOAT, var57);
      var0.put(ModelLayers.JUNGLE_BOAT, var56);
      var0.put(ModelLayers.JUNGLE_CHEST_BOAT, var57);
      var0.put(ModelLayers.ACACIA_BOAT, var56);
      var0.put(ModelLayers.ACACIA_CHEST_BOAT, var57);
      var0.put(ModelLayers.CHERRY_BOAT, var56);
      var0.put(ModelLayers.CHERRY_CHEST_BOAT, var57);
      var0.put(ModelLayers.DARK_OAK_BOAT, var56);
      var0.put(ModelLayers.DARK_OAK_CHEST_BOAT, var57);
      var0.put(ModelLayers.PALE_OAK_BOAT, var56);
      var0.put(ModelLayers.PALE_OAK_CHEST_BOAT, var57);
      var0.put(ModelLayers.MANGROVE_BOAT, var56);
      var0.put(ModelLayers.MANGROVE_CHEST_BOAT, var57);
      LayerDefinition var58 = SignRenderer.createSignLayer(true);
      LayerDefinition var59 = SignRenderer.createSignLayer(false);
      LayerDefinition var60 = HangingSignRenderer.createHangingSignLayer();
      WoodType.values().forEach((var4x) -> {
         var0.put(ModelLayers.createStandingSignModelName(var4x), var58);
         var0.put(ModelLayers.createWallSignModelName(var4x), var59);
         var0.put(ModelLayers.createHangingSignModelName(var4x), var60);
      });
      ImmutableMap var61 = var0.build();
      List var62 = (List)ModelLayers.getKnownLocations().filter((var1x) -> {
         return !var61.containsKey(var1x);
      }).collect(Collectors.toList());
      if (!var62.isEmpty()) {
         throw new IllegalStateException("Missing layer definitions: " + String.valueOf(var62));
      } else {
         return var61;
      }
   }
}
