package net.minecraft.data.advancements;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.criterion.BrewedPotionTrigger;
import net.minecraft.advancements.criterion.ChangeDimensionTrigger;
import net.minecraft.advancements.criterion.ConstructBeaconTrigger;
import net.minecraft.advancements.criterion.DamageSourcePredicate;
import net.minecraft.advancements.criterion.DistancePredicate;
import net.minecraft.advancements.criterion.EffectsChangedTrigger;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.KilledTrigger;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.MobEffectsPredicate;
import net.minecraft.advancements.criterion.NetherTravelTrigger;
import net.minecraft.advancements.criterion.PositionTrigger;
import net.minecraft.advancements.criterion.SummonedEntityTrigger;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.dimension.DimensionType;

public class NetherAdvancements implements Consumer<Consumer<Advancement>> {
   public NetherAdvancements() {
      super();
   }

   public void accept(Consumer<Advancement> var1) {
      Advancement var2 = Advancement.Builder.func_200278_a().func_203902_a(Blocks.field_196817_hS, new TextComponentTranslation("advancements.nether.root.title", new Object[0]), new TextComponentTranslation("advancements.nether.root.description", new Object[0]), new ResourceLocation("minecraft:textures/gui/advancements/backgrounds/nether.png"), FrameType.TASK, false, false, false).func_200275_a("entered_nether", ChangeDimensionTrigger.Instance.func_203911_a(DimensionType.NETHER)).func_203904_a(var1, "nether/root");
      Advancement var3 = Advancement.Builder.func_200278_a().func_203905_a(var2).func_203902_a(Items.field_151059_bz, new TextComponentTranslation("advancements.nether.return_to_sender.title", new Object[0]), new TextComponentTranslation("advancements.nether.return_to_sender.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).func_200271_a(AdvancementRewards.Builder.func_203907_a(50)).func_200275_a("killed_ghast", KilledTrigger.Instance.func_203929_a(EntityPredicate.Builder.func_203996_a().func_203998_a(EntityType.field_200811_y), DamageSourcePredicate.Builder.func_203981_a().func_203978_a(true).func_203980_a(EntityPredicate.Builder.func_203996_a().func_203998_a(EntityType.field_200767_G)))).func_203904_a(var1, "nether/return_to_sender");
      Advancement var4 = Advancement.Builder.func_200278_a().func_203905_a(var2).func_203902_a(Blocks.field_196653_dH, new TextComponentTranslation("advancements.nether.find_fortress.title", new Object[0]), new TextComponentTranslation("advancements.nether.find_fortress.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("fortress", PositionTrigger.Instance.func_203932_a(LocationPredicate.func_204007_a("Fortress"))).func_203904_a(var1, "nether/find_fortress");
      Advancement var5 = Advancement.Builder.func_200278_a().func_203905_a(var2).func_203902_a(Items.field_151148_bJ, new TextComponentTranslation("advancements.nether.fast_travel.title", new Object[0]), new TextComponentTranslation("advancements.nether.fast_travel.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).func_200271_a(AdvancementRewards.Builder.func_203907_a(100)).func_200275_a("travelled", NetherTravelTrigger.Instance.func_203933_a(DistancePredicate.func_203995_a(MinMaxBounds.FloatBound.func_211355_b(7000.0F)))).func_203904_a(var1, "nether/fast_travel");
      Advancement var6 = Advancement.Builder.func_200278_a().func_203905_a(var3).func_203902_a(Items.field_151073_bk, new TextComponentTranslation("advancements.nether.uneasy_alliance.title", new Object[0]), new TextComponentTranslation("advancements.nether.uneasy_alliance.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).func_200271_a(AdvancementRewards.Builder.func_203907_a(100)).func_200275_a("killed_ghast", KilledTrigger.Instance.func_203928_a(EntityPredicate.Builder.func_203996_a().func_203998_a(EntityType.field_200811_y).func_203999_a(LocationPredicate.func_204008_a(DimensionType.OVERWORLD)))).func_203904_a(var1, "nether/uneasy_alliance");
      Advancement var7 = Advancement.Builder.func_200278_a().func_203905_a(var4).func_203902_a(Blocks.field_196705_eO, new TextComponentTranslation("advancements.nether.get_wither_skull.title", new Object[0]), new TextComponentTranslation("advancements.nether.get_wither_skull.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("wither_skull", InventoryChangeTrigger.Instance.func_203922_a(Blocks.field_196705_eO)).func_203904_a(var1, "nether/get_wither_skull");
      Advancement var8 = Advancement.Builder.func_200278_a().func_203905_a(var7).func_203902_a(Items.field_151156_bN, new TextComponentTranslation("advancements.nether.summon_wither.title", new Object[0]), new TextComponentTranslation("advancements.nether.summon_wither.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("summoned", SummonedEntityTrigger.Instance.func_203937_a(EntityPredicate.Builder.func_203996_a().func_203998_a(EntityType.field_200760_az))).func_203904_a(var1, "nether/summon_wither");
      Advancement var9 = Advancement.Builder.func_200278_a().func_203905_a(var4).func_203902_a(Items.field_151072_bj, new TextComponentTranslation("advancements.nether.obtain_blaze_rod.title", new Object[0]), new TextComponentTranslation("advancements.nether.obtain_blaze_rod.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("blaze_rod", InventoryChangeTrigger.Instance.func_203922_a(Items.field_151072_bj)).func_203904_a(var1, "nether/obtain_blaze_rod");
      Advancement var10 = Advancement.Builder.func_200278_a().func_203905_a(var8).func_203902_a(Blocks.field_150461_bJ, new TextComponentTranslation("advancements.nether.create_beacon.title", new Object[0]), new TextComponentTranslation("advancements.nether.create_beacon.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("beacon", ConstructBeaconTrigger.Instance.func_203912_a(MinMaxBounds.IntBound.func_211340_b(1))).func_203904_a(var1, "nether/create_beacon");
      Advancement var11 = Advancement.Builder.func_200278_a().func_203905_a(var10).func_203902_a(Blocks.field_150461_bJ, new TextComponentTranslation("advancements.nether.create_full_beacon.title", new Object[0]), new TextComponentTranslation("advancements.nether.create_full_beacon.description", new Object[0]), (ResourceLocation)null, FrameType.GOAL, true, true, false).func_200275_a("beacon", ConstructBeaconTrigger.Instance.func_203912_a(MinMaxBounds.IntBound.func_211345_a(4))).func_203904_a(var1, "nether/create_full_beacon");
      Advancement var12 = Advancement.Builder.func_200278_a().func_203905_a(var9).func_203902_a(Items.field_151068_bn, new TextComponentTranslation("advancements.nether.brew_potion.title", new Object[0]), new TextComponentTranslation("advancements.nether.brew_potion.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("potion", BrewedPotionTrigger.Instance.func_203910_c()).func_203904_a(var1, "nether/brew_potion");
      Advancement var13 = Advancement.Builder.func_200278_a().func_203905_a(var12).func_203902_a(Items.field_151117_aB, new TextComponentTranslation("advancements.nether.all_potions.title", new Object[0]), new TextComponentTranslation("advancements.nether.all_potions.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).func_200271_a(AdvancementRewards.Builder.func_203907_a(100)).func_200275_a("all_effects", EffectsChangedTrigger.Instance.func_203917_a(MobEffectsPredicate.func_204014_a().func_204015_a(MobEffects.field_76424_c).func_204015_a(MobEffects.field_76421_d).func_204015_a(MobEffects.field_76420_g).func_204015_a(MobEffects.field_76430_j).func_204015_a(MobEffects.field_76428_l).func_204015_a(MobEffects.field_76426_n).func_204015_a(MobEffects.field_76427_o).func_204015_a(MobEffects.field_76441_p).func_204015_a(MobEffects.field_76439_r).func_204015_a(MobEffects.field_76437_t).func_204015_a(MobEffects.field_76436_u).func_204015_a(MobEffects.field_204839_B).func_204015_a(MobEffects.field_76429_m))).func_203904_a(var1, "nether/all_potions");
      Advancement var14 = Advancement.Builder.func_200278_a().func_203905_a(var13).func_203902_a(Items.field_151133_ar, new TextComponentTranslation("advancements.nether.all_effects.title", new Object[0]), new TextComponentTranslation("advancements.nether.all_effects.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, true).func_200271_a(AdvancementRewards.Builder.func_203907_a(1000)).func_200275_a("all_effects", EffectsChangedTrigger.Instance.func_203917_a(MobEffectsPredicate.func_204014_a().func_204015_a(MobEffects.field_76424_c).func_204015_a(MobEffects.field_76421_d).func_204015_a(MobEffects.field_76420_g).func_204015_a(MobEffects.field_76430_j).func_204015_a(MobEffects.field_76428_l).func_204015_a(MobEffects.field_76426_n).func_204015_a(MobEffects.field_76427_o).func_204015_a(MobEffects.field_76441_p).func_204015_a(MobEffects.field_76439_r).func_204015_a(MobEffects.field_76437_t).func_204015_a(MobEffects.field_76436_u).func_204015_a(MobEffects.field_82731_v).func_204015_a(MobEffects.field_76422_e).func_204015_a(MobEffects.field_76419_f).func_204015_a(MobEffects.field_188424_y).func_204015_a(MobEffects.field_188423_x).func_204015_a(MobEffects.field_76444_x).func_204015_a(MobEffects.field_76438_s).func_204015_a(MobEffects.field_76431_k).func_204015_a(MobEffects.field_76429_m).func_204015_a(MobEffects.field_204839_B).func_204015_a(MobEffects.field_205136_C).func_204015_a(MobEffects.field_206827_D))).func_203904_a(var1, "nether/all_effects");
   }

   // $FF: synthetic method
   public void accept(Object var1) {
      this.accept((Consumer)var1);
   }
}
