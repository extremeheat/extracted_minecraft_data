package net.minecraft.client.model.geom;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.model.AllayModel;
import net.minecraft.client.model.ArmadilloModel;
import net.minecraft.client.model.ArmorStandArmorModel;
import net.minecraft.client.model.ArmorStandModel;
import net.minecraft.client.model.AxolotlModel;
import net.minecraft.client.model.BatModel;
import net.minecraft.client.model.BatatoModel;
import net.minecraft.client.model.BeeModel;
import net.minecraft.client.model.BlazeModel;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.BoggedModel;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.BreezeModel;
import net.minecraft.client.model.CamelModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ChestRaftModel;
import net.minecraft.client.model.ChestedHorseModel;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.CodModel;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.DolphinModel;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.EndermiteModel;
import net.minecraft.client.model.EvokerFangsModel;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.model.FrogModel;
import net.minecraft.client.model.GhastModel;
import net.minecraft.client.model.GoatModel;
import net.minecraft.client.model.GuardianModel;
import net.minecraft.client.model.HoglinModel;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.LavaSlimeModel;
import net.minecraft.client.model.LeashKnotModel;
import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.model.LlamaSpitModel;
import net.minecraft.client.model.MegaSpudModel;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.model.PandaModel;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.PiglinHeadModel;
import net.minecraft.client.model.PiglinModel;
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
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.client.renderer.blockentity.BellRenderer;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.ConduitRenderer;
import net.minecraft.client.renderer.blockentity.DecoratedPotRenderer;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.WitherSkullRenderer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.state.properties.WoodType;

public class LayerDefinitions {
   private static final CubeDeformation FISH_PATTERN_DEFORMATION = new CubeDeformation(0.008F);
   private static final CubeDeformation OUTER_ARMOR_DEFORMATION = new CubeDeformation(1.0F);
   private static final CubeDeformation INNER_ARMOR_DEFORMATION = new CubeDeformation(0.5F);

   public LayerDefinitions() {
      super();
   }

   public static Map<ModelLayerLocation, LayerDefinition> createRoots() {
      Builder var0 = ImmutableMap.builder();
      LayerDefinition var1 = LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F), 64, 64);
      LayerDefinition var2 = LayerDefinition.create(HumanoidArmorModel.createBodyLayer(OUTER_ARMOR_DEFORMATION), 64, 32);
      LayerDefinition var3 = LayerDefinition.create(HumanoidArmorModel.createBodyLayer(new CubeDeformation(1.02F)), 64, 32);
      LayerDefinition var4 = LayerDefinition.create(HumanoidArmorModel.createBodyLayer(INNER_ARMOR_DEFORMATION), 64, 32);
      LayerDefinition var5 = MinecartModel.createBodyLayer();
      LayerDefinition var6 = SkullModel.createMobHeadLayer();
      LayerDefinition var7 = LayerDefinition.create(HorseModel.createBodyMesh(CubeDeformation.NONE), 64, 64);
      LayerDefinition var8 = IllagerModel.createBodyLayer();
      LayerDefinition var9 = CowModel.createBodyLayer();
      LayerDefinition var10 = LayerDefinition.create(OcelotModel.createBodyMesh(CubeDeformation.NONE), 64, 32);
      LayerDefinition var11 = LayerDefinition.create(PiglinModel.createMesh(CubeDeformation.NONE), 64, 64);
      LayerDefinition var12 = LayerDefinition.create(PiglinHeadModel.createHeadModel(), 64, 64);
      LayerDefinition var13 = SkullModel.createHumanoidHeadLayer();
      LayerDefinition var14 = LlamaModel.createBodyLayer(CubeDeformation.NONE);
      LayerDefinition var15 = StriderModel.createBodyLayer();
      LayerDefinition var16 = HoglinModel.createBodyLayer();
      LayerDefinition var17 = SkeletonModel.createBodyLayer();
      LayerDefinition var18 = LayerDefinition.create(VillagerModel.createBodyModel(), 64, 64);
      LayerDefinition var19 = SpiderModel.createSpiderBodyLayer();
      var0.put(ModelLayers.ALLAY, AllayModel.createBodyLayer());
      var0.put(ModelLayers.ARMADILLO, ArmadilloModel.createBodyLayer());
      var0.put(ModelLayers.ARMOR_STAND, ArmorStandModel.createBodyLayer());
      var0.put(ModelLayers.ARMOR_STAND_INNER_ARMOR, ArmorStandArmorModel.createBodyLayer(INNER_ARMOR_DEFORMATION));
      var0.put(ModelLayers.ARMOR_STAND_OUTER_ARMOR, ArmorStandArmorModel.createBodyLayer(OUTER_ARMOR_DEFORMATION));
      var0.put(ModelLayers.AXOLOTL, AxolotlModel.createBodyLayer());
      var0.put(ModelLayers.BANNER, BannerRenderer.createBodyLayer());
      var0.put(ModelLayers.BAT, BatModel.createBodyLayer());
      var0.put(ModelLayers.BATATO, BatatoModel.createBodyLayer());
      var0.put(ModelLayers.BED_FOOT, BedRenderer.createFootLayer());
      var0.put(ModelLayers.BED_HEAD, BedRenderer.createHeadLayer());
      var0.put(ModelLayers.BEE, BeeModel.createBodyLayer());
      var0.put(ModelLayers.BELL, BellRenderer.createBodyLayer());
      var0.put(ModelLayers.BLAZE, BlazeModel.createBodyLayer());
      var0.put(ModelLayers.BOGGED, BoggedModel.createBodyLayer());
      var0.put(ModelLayers.BOGGED_INNER_ARMOR, var4);
      var0.put(ModelLayers.BOGGED_OUTER_ARMOR, var2);
      var0.put(ModelLayers.BOGGED_OUTER_LAYER, LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(0.2F), 0.0F), 64, 32));
      var0.put(ModelLayers.BOOK, BookModel.createBodyLayer());
      var0.put(ModelLayers.BREEZE, BreezeModel.createBodyLayer(32, 32));
      var0.put(ModelLayers.CAT, var10);
      var0.put(ModelLayers.CAT_COLLAR, LayerDefinition.create(OcelotModel.createBodyMesh(new CubeDeformation(0.01F)), 64, 32));
      var0.put(ModelLayers.CAMEL, CamelModel.createBodyLayer());
      var0.put(ModelLayers.CAVE_SPIDER, var19);
      var0.put(ModelLayers.CHEST, ChestRenderer.createSingleBodyLayer());
      var0.put(ModelLayers.CHEST_MINECART, var5);
      var0.put(ModelLayers.CHICKEN, ChickenModel.createBodyLayer());
      var0.put(ModelLayers.COD, CodModel.createBodyLayer());
      var0.put(ModelLayers.COMMAND_BLOCK_MINECART, var5);
      var0.put(ModelLayers.CONDUIT_EYE, ConduitRenderer.createEyeLayer());
      var0.put(ModelLayers.CONDUIT_WIND, ConduitRenderer.createWindLayer());
      var0.put(ModelLayers.CONDUIT_SHELL, ConduitRenderer.createShellLayer());
      var0.put(ModelLayers.CONDUIT_CAGE, ConduitRenderer.createCageLayer());
      var0.put(ModelLayers.COW, var9);
      var0.put(ModelLayers.CREEPER, CreeperModel.createBodyLayer(CubeDeformation.NONE));
      var0.put(ModelLayers.CREEPER_ARMOR, CreeperModel.createBodyLayer(new CubeDeformation(2.0F)));
      var0.put(ModelLayers.CREEPER_HEAD, var6);
      var0.put(ModelLayers.DECORATED_POT_BASE, DecoratedPotRenderer.createBaseLayer());
      var0.put(ModelLayers.DECORATED_POT_SIDES, DecoratedPotRenderer.createSidesLayer());
      var0.put(ModelLayers.DOLPHIN, DolphinModel.createBodyLayer());
      var0.put(ModelLayers.DONKEY, ChestedHorseModel.createBodyLayer());
      var0.put(ModelLayers.DOUBLE_CHEST_LEFT, ChestRenderer.createDoubleBodyLeftLayer());
      var0.put(ModelLayers.DOUBLE_CHEST_RIGHT, ChestRenderer.createDoubleBodyRightLayer());
      var0.put(ModelLayers.DRAGON_SKULL, DragonHeadModel.createHeadLayer());
      var0.put(ModelLayers.DROWNED, DrownedModel.createBodyLayer(CubeDeformation.NONE));
      var0.put(ModelLayers.DROWNED_INNER_ARMOR, var4);
      var0.put(ModelLayers.DROWNED_OUTER_ARMOR, var4);
      var0.put(ModelLayers.DROWNED_OUTER_LAYER, DrownedModel.createBodyLayer(new CubeDeformation(0.25F)));
      var0.put(ModelLayers.ELDER_GUARDIAN, GuardianModel.createBodyLayer(false));
      var0.put(ModelLayers.ELDER_GUARDIAN_SLAB, GuardianModel.createBodyLayer(true));
      var0.put(ModelLayers.ELYTRA, ElytraModel.createLayer());
      var0.put(ModelLayers.POISONOUS_POLYTRA, ElytraModel.createLayer());
      var0.put(ModelLayers.ENDERMAN, EndermanModel.createBodyLayer());
      var0.put(ModelLayers.ENDERMITE, EndermiteModel.createBodyLayer());
      var0.put(ModelLayers.ENDER_DRAGON, EnderDragonRenderer.createBodyLayer());
      var0.put(ModelLayers.END_CRYSTAL, EndCrystalRenderer.createBodyLayer());
      var0.put(ModelLayers.EVOKER, var8);
      var0.put(ModelLayers.EVOKER_FANGS, EvokerFangsModel.createBodyLayer());
      var0.put(ModelLayers.FOX, FoxModel.createBodyLayer());
      var0.put(ModelLayers.FROG, FrogModel.createBodyLayer());
      var0.put(ModelLayers.FURNACE_MINECART, var5);
      var0.put(ModelLayers.GHAST, GhastModel.createBodyLayer());
      var0.put(ModelLayers.GIANT, var1);
      var0.put(ModelLayers.GIANT_INNER_ARMOR, var4);
      var0.put(ModelLayers.GIANT_OUTER_ARMOR, var2);
      var0.put(ModelLayers.GLOW_SQUID, SquidModel.createBodyLayer());
      var0.put(ModelLayers.GOAT, GoatModel.createBodyLayer());
      var0.put(ModelLayers.GUARDIAN, GuardianModel.createBodyLayer(false));
      var0.put(ModelLayers.GUARDIAN_SLAB, GuardianModel.createBodyLayer(true));
      var0.put(ModelLayers.HOGLIN, var16);
      var0.put(ModelLayers.HOPPER_MINECART, var5);
      var0.put(ModelLayers.HORSE, var7);
      var0.put(ModelLayers.HORSE_ARMOR, LayerDefinition.create(HorseModel.createBodyMesh(new CubeDeformation(0.1F)), 64, 64));
      var0.put(ModelLayers.HUSK, var1);
      var0.put(ModelLayers.HUSK_INNER_ARMOR, var4);
      var0.put(ModelLayers.HUSK_OUTER_ARMOR, var2);
      var0.put(ModelLayers.ILLUSIONER, var8);
      var0.put(ModelLayers.IRON_GOLEM, IronGolemModel.createBodyLayer());
      var0.put(ModelLayers.LEASH_KNOT, LeashKnotModel.createBodyLayer());
      var0.put(ModelLayers.LLAMA, var14);
      var0.put(ModelLayers.LLAMA_DECOR, LlamaModel.createBodyLayer(new CubeDeformation(0.5F)));
      var0.put(ModelLayers.LLAMA_SPIT, LlamaSpitModel.createBodyLayer());
      var0.put(ModelLayers.MAGMA_CUBE, LavaSlimeModel.createBodyLayer());
      var0.put(ModelLayers.MINECART, var5);
      var0.put(ModelLayers.MOOSHROOM, var9);
      var0.put(ModelLayers.MULE, ChestedHorseModel.createBodyLayer());
      var0.put(ModelLayers.OCELOT, var10);
      var0.put(ModelLayers.PANDA, PandaModel.createBodyLayer());
      var0.put(ModelLayers.PARROT, ParrotModel.createBodyLayer());
      var0.put(ModelLayers.PHANTOM, PhantomModel.createBodyLayer());
      var0.put(ModelLayers.PIG, PigModel.createBodyLayer(CubeDeformation.NONE));
      var0.put(ModelLayers.PIG_SADDLE, PigModel.createBodyLayer(new CubeDeformation(0.5F)));
      var0.put(ModelLayers.PIGLIN, var11);
      var0.put(ModelLayers.PIGLIN_INNER_ARMOR, var4);
      var0.put(ModelLayers.PIGLIN_OUTER_ARMOR, var3);
      var0.put(ModelLayers.PIGLIN_BRUTE, var11);
      var0.put(ModelLayers.PIGLIN_BRUTE_INNER_ARMOR, var4);
      var0.put(ModelLayers.PIGLIN_BRUTE_OUTER_ARMOR, var3);
      var0.put(ModelLayers.PIGLIN_HEAD, var12);
      var0.put(ModelLayers.PILLAGER, var8);
      var0.put(ModelLayers.PLAYER, LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, false), 64, 64));
      var0.put(ModelLayers.PLAYER_HEAD, var13);
      var0.put(ModelLayers.PLAYER_INNER_ARMOR, var4);
      var0.put(ModelLayers.PLAYER_OUTER_ARMOR, var2);
      var0.put(ModelLayers.PLAYER_SLIM, LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, true), 64, 64));
      var0.put(ModelLayers.PLAYER_SLIM_INNER_ARMOR, var4);
      var0.put(ModelLayers.PLAYER_SLIM_OUTER_ARMOR, var2);
      var0.put(ModelLayers.PLAYER_SPIN_ATTACK, SpinAttackEffectLayer.createLayer());
      var0.put(ModelLayers.POLAR_BEAR, PolarBearModel.createBodyLayer());
      var0.put(ModelLayers.PUFFERFISH_BIG, PufferfishBigModel.createBodyLayer());
      var0.put(ModelLayers.PUFFERFISH_MEDIUM, PufferfishMidModel.createBodyLayer());
      var0.put(ModelLayers.PUFFERFISH_SMALL, PufferfishSmallModel.createBodyLayer());
      var0.put(ModelLayers.RABBIT, RabbitModel.createBodyLayer());
      var0.put(ModelLayers.RAVAGER, RavagerModel.createBodyLayer());
      var0.put(ModelLayers.SALMON, SalmonModel.createBodyLayer());
      var0.put(ModelLayers.SHEEP, SheepModel.createBodyLayer());
      var0.put(ModelLayers.SHEEP_FUR, SheepFurModel.createFurLayer());
      var0.put(ModelLayers.SHIELD, ShieldModel.createLayer());
      var0.put(ModelLayers.SHULKER, ShulkerModel.createBodyLayer());
      var0.put(ModelLayers.SHULKER_BULLET, ShulkerBulletModel.createBodyLayer());
      var0.put(ModelLayers.SILVERFISH, SilverfishModel.createBodyLayer());
      var0.put(ModelLayers.SKELETON, var17);
      var0.put(ModelLayers.SKELETON_INNER_ARMOR, var4);
      var0.put(ModelLayers.SKELETON_OUTER_ARMOR, var2);
      var0.put(ModelLayers.SKELETON_HORSE, var7);
      var0.put(ModelLayers.SKELETON_SKULL, var6);
      var0.put(ModelLayers.SLIME, SlimeModel.createInnerBodyLayer());
      var0.put(ModelLayers.SLIME_OUTER, SlimeModel.createOuterBodyLayer());
      var0.put(ModelLayers.MEGA_SPUD, MegaSpudModel.createInnerBodyLayer());
      var0.put(ModelLayers.MEGA_SPUD_OUTER, MegaSpudModel.createOuterBodyLayer());
      var0.put(ModelLayers.SNIFFER, SnifferModel.createBodyLayer());
      var0.put(ModelLayers.SNOW_GOLEM, SnowGolemModel.createBodyLayer());
      var0.put(ModelLayers.SPAWNER_MINECART, var5);
      var0.put(ModelLayers.SPIDER, var19);
      var0.put(ModelLayers.SQUID, SquidModel.createBodyLayer());
      var0.put(ModelLayers.STRAY, var17);
      var0.put(ModelLayers.STRAY_INNER_ARMOR, var4);
      var0.put(ModelLayers.STRAY_OUTER_ARMOR, var2);
      var0.put(ModelLayers.STRAY_OUTER_LAYER, LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(0.25F), 0.0F), 64, 32));
      var0.put(ModelLayers.STRIDER, var15);
      var0.put(ModelLayers.STRIDER_SADDLE, var15);
      var0.put(ModelLayers.TADPOLE, TadpoleModel.createBodyLayer());
      var0.put(ModelLayers.TNT_MINECART, var5);
      var0.put(ModelLayers.TRADER_LLAMA, var14);
      var0.put(ModelLayers.TRIDENT, TridentModel.createLayer());
      var0.put(ModelLayers.TROPICAL_FISH_LARGE, TropicalFishModelB.createBodyLayer(CubeDeformation.NONE));
      var0.put(ModelLayers.TROPICAL_FISH_LARGE_PATTERN, TropicalFishModelB.createBodyLayer(FISH_PATTERN_DEFORMATION));
      var0.put(ModelLayers.TROPICAL_FISH_SMALL, TropicalFishModelA.createBodyLayer(CubeDeformation.NONE));
      var0.put(ModelLayers.TROPICAL_FISH_SMALL_PATTERN, TropicalFishModelA.createBodyLayer(FISH_PATTERN_DEFORMATION));
      var0.put(ModelLayers.TURTLE, TurtleModel.createBodyLayer());
      var0.put(ModelLayers.VEX, VexModel.createBodyLayer());
      var0.put(ModelLayers.VILLAGER, var18);
      var0.put(ModelLayers.VINDICATOR, var8);
      var0.put(ModelLayers.WARDEN, WardenModel.createBodyLayer());
      var0.put(ModelLayers.WANDERING_TRADER, var18);
      var0.put(ModelLayers.WIND_CHARGE, WindChargeModel.createBodyLayer());
      var0.put(ModelLayers.WITCH, WitchModel.createBodyLayer());
      var0.put(ModelLayers.WITHER, WitherBossModel.createBodyLayer(CubeDeformation.NONE));
      var0.put(ModelLayers.WITHER_ARMOR, WitherBossModel.createBodyLayer(INNER_ARMOR_DEFORMATION));
      var0.put(ModelLayers.WITHER_SKULL, WitherSkullRenderer.createSkullLayer());
      var0.put(ModelLayers.WITHER_SKELETON, var17);
      var0.put(ModelLayers.WITHER_SKELETON_INNER_ARMOR, var4);
      var0.put(ModelLayers.WITHER_SKELETON_OUTER_ARMOR, var2);
      var0.put(ModelLayers.WITHER_SKELETON_SKULL, var6);
      var0.put(ModelLayers.WOLF, LayerDefinition.create(WolfModel.createMeshDefinition(CubeDeformation.NONE), 64, 32));
      var0.put(ModelLayers.WOLF_ARMOR, LayerDefinition.create(WolfModel.createMeshDefinition(new CubeDeformation(0.2F)), 64, 32));
      var0.put(ModelLayers.ZOGLIN, var16);
      var0.put(ModelLayers.ZOMBIE, var1);
      var0.put(ModelLayers.ZOMBIE_INNER_ARMOR, var4);
      var0.put(ModelLayers.ZOMBIE_OUTER_ARMOR, var2);
      var0.put(ModelLayers.ZOMBIE_HEAD, var13);
      var0.put(ModelLayers.ZOMBIE_HORSE, var7);
      var0.put(ModelLayers.ZOMBIE_VILLAGER, ZombieVillagerModel.createBodyLayer());
      var0.put(ModelLayers.ZOMBIE_VILLAGER_INNER_ARMOR, ZombieVillagerModel.createArmorLayer(INNER_ARMOR_DEFORMATION));
      var0.put(ModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR, ZombieVillagerModel.createArmorLayer(OUTER_ARMOR_DEFORMATION));
      var0.put(ModelLayers.ZOMBIFIED_PIGLIN, var11);
      var0.put(ModelLayers.ZOMBIFIED_PIGLIN_INNER_ARMOR, var4);
      var0.put(ModelLayers.ZOMBIFIED_PIGLIN_OUTER_ARMOR, var3);
      LayerDefinition var20 = BoatModel.createBodyModel();
      LayerDefinition var21 = ChestBoatModel.createBodyModel();
      LayerDefinition var22 = RaftModel.createBodyModel();
      LayerDefinition var23 = ChestRaftModel.createBodyModel();

      for(Boat.Type var27 : Boat.Type.values()) {
         if (var27 == Boat.Type.BAMBOO) {
            var0.put(ModelLayers.createBoatModelName(var27), var22);
            var0.put(ModelLayers.createChestBoatModelName(var27), var23);
         } else {
            var0.put(ModelLayers.createBoatModelName(var27), var20);
            var0.put(ModelLayers.createChestBoatModelName(var27), var21);
         }
      }

      LayerDefinition var28 = SignRenderer.createSignLayer();
      WoodType.values().forEach(var2x -> var0.put(ModelLayers.createSignModelName(var2x), var28));
      LayerDefinition var29 = HangingSignRenderer.createHangingSignLayer();
      WoodType.values().forEach(var2x -> var0.put(ModelLayers.createHangingSignModelName(var2x), var29));
      ImmutableMap var30 = var0.build();
      List var31 = ModelLayers.getKnownLocations().filter(var1x -> !var30.containsKey(var1x)).collect(Collectors.toList());
      if (!var31.isEmpty()) {
         throw new IllegalStateException("Missing layer definitions: " + var31);
      } else {
         return var30;
      }
   }
}
