package net.minecraft.data.advancements;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.criterion.ChanneledLightningTrigger;
import net.minecraft.advancements.criterion.DamagePredicate;
import net.minecraft.advancements.criterion.DamageSourcePredicate;
import net.minecraft.advancements.criterion.DistancePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.KilledTrigger;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.PlayerHurtEntityTrigger;
import net.minecraft.advancements.criterion.PositionTrigger;
import net.minecraft.advancements.criterion.SummonedEntityTrigger;
import net.minecraft.advancements.criterion.UsedTotemTrigger;
import net.minecraft.advancements.criterion.VillagerTradeTrigger;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.biome.Biome;

public class AdventureAdvancements implements Consumer<Consumer<Advancement>> {
   private static final Biome[] field_204286_a;
   private static final EntityType<?>[] field_204287_b;

   public AdventureAdvancements() {
      super();
   }

   public void accept(Consumer<Advancement> var1) {
      Advancement var2 = Advancement.Builder.func_200278_a().func_203902_a(Items.field_151148_bJ, new TextComponentTranslation("advancements.adventure.root.title", new Object[0]), new TextComponentTranslation("advancements.adventure.root.description", new Object[0]), new ResourceLocation("minecraft:textures/gui/advancements/backgrounds/adventure.png"), FrameType.TASK, false, false, false).func_200270_a(RequirementsStrategy.OR).func_200275_a("killed_something", KilledTrigger.Instance.func_203927_c()).func_200275_a("killed_by_something", KilledTrigger.Instance.func_203926_d()).func_203904_a(var1, "adventure/root");
      Advancement var3 = Advancement.Builder.func_200278_a().func_203905_a(var2).func_203902_a(Blocks.field_196550_aA, new TextComponentTranslation("advancements.adventure.sleep_in_bed.title", new Object[0]), new TextComponentTranslation("advancements.adventure.sleep_in_bed.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("slept_in_bed", PositionTrigger.Instance.func_203931_c()).func_203904_a(var1, "adventure/sleep_in_bed");
      Advancement var4 = this.func_204285_b(Advancement.Builder.func_200278_a()).func_203905_a(var3).func_203902_a(Items.field_151175_af, new TextComponentTranslation("advancements.adventure.adventuring_time.title", new Object[0]), new TextComponentTranslation("advancements.adventure.adventuring_time.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).func_200271_a(AdvancementRewards.Builder.func_203907_a(500)).func_203904_a(var1, "adventure/adventuring_time");
      Advancement var5 = Advancement.Builder.func_200278_a().func_203905_a(var2).func_203902_a(Items.field_151166_bC, new TextComponentTranslation("advancements.adventure.trade.title", new Object[0]), new TextComponentTranslation("advancements.adventure.trade.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("traded", VillagerTradeTrigger.Instance.func_203939_c()).func_203904_a(var1, "adventure/trade");
      Advancement var6 = this.func_204284_a(Advancement.Builder.func_200278_a()).func_203905_a(var2).func_203902_a(Items.field_151040_l, new TextComponentTranslation("advancements.adventure.kill_a_mob.title", new Object[0]), new TextComponentTranslation("advancements.adventure.kill_a_mob.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200270_a(RequirementsStrategy.OR).func_203904_a(var1, "adventure/kill_a_mob");
      Advancement var7 = this.func_204284_a(Advancement.Builder.func_200278_a()).func_203905_a(var6).func_203902_a(Items.field_151048_u, new TextComponentTranslation("advancements.adventure.kill_all_mobs.title", new Object[0]), new TextComponentTranslation("advancements.adventure.kill_all_mobs.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).func_200271_a(AdvancementRewards.Builder.func_203907_a(100)).func_203904_a(var1, "adventure/kill_all_mobs");
      Advancement var8 = Advancement.Builder.func_200278_a().func_203905_a(var6).func_203902_a(Items.field_151031_f, new TextComponentTranslation("advancements.adventure.shoot_arrow.title", new Object[0]), new TextComponentTranslation("advancements.adventure.shoot_arrow.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("shot_arrow", PlayerHurtEntityTrigger.Instance.func_203936_a(DamagePredicate.Builder.func_203971_a().func_203969_a(DamageSourcePredicate.Builder.func_203981_a().func_203978_a(true).func_203980_a(EntityPredicate.Builder.func_203996_a().func_203998_a(EntityType.field_200790_d))))).func_203904_a(var1, "adventure/shoot_arrow");
      Advancement var9 = Advancement.Builder.func_200278_a().func_203905_a(var6).func_203902_a(Items.field_203184_eO, new TextComponentTranslation("advancements.adventure.throw_trident.title", new Object[0]), new TextComponentTranslation("advancements.adventure.throw_trident.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("shot_trident", PlayerHurtEntityTrigger.Instance.func_203936_a(DamagePredicate.Builder.func_203971_a().func_203969_a(DamageSourcePredicate.Builder.func_203981_a().func_203978_a(true).func_203980_a(EntityPredicate.Builder.func_203996_a().func_203998_a(EntityType.field_203098_aL))))).func_203904_a(var1, "adventure/throw_trident");
      Advancement var10 = Advancement.Builder.func_200278_a().func_203905_a(var9).func_203902_a(Items.field_203184_eO, new TextComponentTranslation("advancements.adventure.very_very_frightening.title", new Object[0]), new TextComponentTranslation("advancements.adventure.very_very_frightening.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("struck_villager", ChanneledLightningTrigger.Instance.func_204824_a(EntityPredicate.Builder.func_203996_a().func_203998_a(EntityType.field_200756_av).func_204000_b())).func_203904_a(var1, "adventure/very_very_frightening");
      Advancement var11 = Advancement.Builder.func_200278_a().func_203905_a(var5).func_203902_a(Blocks.field_196625_cS, new TextComponentTranslation("advancements.adventure.summon_iron_golem.title", new Object[0]), new TextComponentTranslation("advancements.adventure.summon_iron_golem.description", new Object[0]), (ResourceLocation)null, FrameType.GOAL, true, true, false).func_200275_a("summoned_golem", SummonedEntityTrigger.Instance.func_203937_a(EntityPredicate.Builder.func_203996_a().func_203998_a(EntityType.field_200757_aw))).func_203904_a(var1, "adventure/summon_iron_golem");
      Advancement var12 = Advancement.Builder.func_200278_a().func_203905_a(var8).func_203902_a(Items.field_151032_g, new TextComponentTranslation("advancements.adventure.sniper_duel.title", new Object[0]), new TextComponentTranslation("advancements.adventure.sniper_duel.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).func_200271_a(AdvancementRewards.Builder.func_203907_a(50)).func_200275_a("killed_skeleton", KilledTrigger.Instance.func_203929_a(EntityPredicate.Builder.func_203996_a().func_203998_a(EntityType.field_200741_ag).func_203997_a(DistancePredicate.func_203995_a(MinMaxBounds.FloatBound.func_211355_b(50.0F))), DamageSourcePredicate.Builder.func_203981_a().func_203978_a(true))).func_203904_a(var1, "adventure/sniper_duel");
      Advancement var13 = Advancement.Builder.func_200278_a().func_203905_a(var6).func_203902_a(Items.field_190929_cY, new TextComponentTranslation("advancements.adventure.totem_of_undying.title", new Object[0]), new TextComponentTranslation("advancements.adventure.totem_of_undying.description", new Object[0]), (ResourceLocation)null, FrameType.GOAL, true, true, false).func_200275_a("used_totem", UsedTotemTrigger.Instance.func_203941_a(Items.field_190929_cY)).func_203904_a(var1, "adventure/totem_of_undying");
   }

   private Advancement.Builder func_204284_a(Advancement.Builder var1) {
      EntityType[] var2 = field_204287_b;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EntityType var5 = var2[var4];
         var1.func_200275_a(IRegistry.field_212629_r.func_177774_c(var5).toString(), KilledTrigger.Instance.func_203928_a(EntityPredicate.Builder.func_203996_a().func_203998_a(var5)));
      }

      return var1;
   }

   private Advancement.Builder func_204285_b(Advancement.Builder var1) {
      Biome[] var2 = field_204286_a;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Biome var5 = var2[var4];
         var1.func_200275_a(IRegistry.field_212624_m.func_177774_c(var5).toString(), PositionTrigger.Instance.func_203932_a(LocationPredicate.func_204010_a(var5)));
      }

      return var1;
   }

   // $FF: synthetic method
   public void accept(Object var1) {
      this.accept((Consumer)var1);
   }

   static {
      field_204286_a = new Biome[]{Biomes.field_150582_Q, Biomes.field_76781_i, Biomes.field_76780_h, Biomes.field_76769_d, Biomes.field_76785_t, Biomes.field_150581_V, Biomes.field_150584_S, Biomes.field_150589_Z, Biomes.field_76767_f, Biomes.field_150576_N, Biomes.field_76774_n, Biomes.field_76784_u, Biomes.field_76775_o, Biomes.field_150607_aa, Biomes.field_150588_X, Biomes.field_76772_c, Biomes.field_76777_m, Biomes.field_150578_U, Biomes.field_150577_O, Biomes.field_76792_x, Biomes.field_150574_L, Biomes.field_76788_q, Biomes.field_76770_e, Biomes.field_76786_s, Biomes.field_76782_w, Biomes.field_76787_r, Biomes.field_150587_Y, Biomes.field_150579_T, Biomes.field_150608_ab, Biomes.field_150585_R, Biomes.field_76768_g, Biomes.field_150583_P, Biomes.field_76789_p, Biomes.field_150580_W, Biomes.field_203614_T, Biomes.field_203615_U, Biomes.field_203616_V, Biomes.field_203618_X, Biomes.field_203619_Y, Biomes.field_203620_Z};
      field_204287_b = new EntityType[]{EntityType.field_200794_h, EntityType.field_200748_an, EntityType.field_200785_Y, EntityType.field_200803_q, EntityType.field_200786_Z, EntityType.field_200792_f, EntityType.field_200797_k, EntityType.field_200806_t, EntityType.field_200811_y, EntityType.field_200761_A, EntityType.field_200763_C, EntityType.field_200771_K, EntityType.field_200738_ad, EntityType.field_200740_af, EntityType.field_200741_ag, EntityType.field_200743_ai, EntityType.field_200750_ap, EntityType.field_200758_ax, EntityType.field_200759_ay, EntityType.field_200722_aA, EntityType.field_200725_aD, EntityType.field_200727_aF, EntityType.field_203097_aH, EntityType.field_204724_o};
   }
}
