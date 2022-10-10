package net.minecraft.data.advancements;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.criterion.ChangeDimensionTrigger;
import net.minecraft.advancements.criterion.DistancePredicate;
import net.minecraft.advancements.criterion.EnterBlockTrigger;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.KilledTrigger;
import net.minecraft.advancements.criterion.LevitationTrigger;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.PositionTrigger;
import net.minecraft.advancements.criterion.SummonedEntityTrigger;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.dimension.DimensionType;

public class EndAdvancements implements Consumer<Consumer<Advancement>> {
   public EndAdvancements() {
      super();
   }

   public void accept(Consumer<Advancement> var1) {
      Advancement var2 = Advancement.Builder.func_200278_a().func_203902_a(Blocks.field_150377_bs, new TextComponentTranslation("advancements.end.root.title", new Object[0]), new TextComponentTranslation("advancements.end.root.description", new Object[0]), new ResourceLocation("minecraft:textures/gui/advancements/backgrounds/end.png"), FrameType.TASK, false, false, false).func_200275_a("entered_end", ChangeDimensionTrigger.Instance.func_203911_a(DimensionType.THE_END)).func_203904_a(var1, "end/root");
      Advancement var3 = Advancement.Builder.func_200278_a().func_203905_a(var2).func_203902_a(Blocks.field_196716_eW, new TextComponentTranslation("advancements.end.kill_dragon.title", new Object[0]), new TextComponentTranslation("advancements.end.kill_dragon.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("killed_dragon", KilledTrigger.Instance.func_203928_a(EntityPredicate.Builder.func_203996_a().func_203998_a(EntityType.field_200802_p))).func_203904_a(var1, "end/kill_dragon");
      Advancement var4 = Advancement.Builder.func_200278_a().func_203905_a(var3).func_203902_a(Items.field_151079_bi, new TextComponentTranslation("advancements.end.enter_end_gateway.title", new Object[0]), new TextComponentTranslation("advancements.end.enter_end_gateway.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("entered_end_gateway", EnterBlockTrigger.Instance.func_203920_a(Blocks.field_185775_db)).func_203904_a(var1, "end/enter_end_gateway");
      Advancement var5 = Advancement.Builder.func_200278_a().func_203905_a(var3).func_203902_a(Items.field_185158_cP, new TextComponentTranslation("advancements.end.respawn_dragon.title", new Object[0]), new TextComponentTranslation("advancements.end.respawn_dragon.description", new Object[0]), (ResourceLocation)null, FrameType.GOAL, true, true, false).func_200275_a("summoned_dragon", SummonedEntityTrigger.Instance.func_203937_a(EntityPredicate.Builder.func_203996_a().func_203998_a(EntityType.field_200802_p))).func_203904_a(var1, "end/respawn_dragon");
      Advancement var6 = Advancement.Builder.func_200278_a().func_203905_a(var4).func_203902_a(Blocks.field_185767_cT, new TextComponentTranslation("advancements.end.find_end_city.title", new Object[0]), new TextComponentTranslation("advancements.end.find_end_city.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("in_city", PositionTrigger.Instance.func_203932_a(LocationPredicate.func_204007_a("EndCity"))).func_203904_a(var1, "end/find_end_city");
      Advancement var7 = Advancement.Builder.func_200278_a().func_203905_a(var3).func_203902_a(Items.field_185157_bK, new TextComponentTranslation("advancements.end.dragon_breath.title", new Object[0]), new TextComponentTranslation("advancements.end.dragon_breath.description", new Object[0]), (ResourceLocation)null, FrameType.GOAL, true, true, false).func_200275_a("dragon_breath", InventoryChangeTrigger.Instance.func_203922_a(Items.field_185157_bK)).func_203904_a(var1, "end/dragon_breath");
      Advancement var8 = Advancement.Builder.func_200278_a().func_203905_a(var6).func_203902_a(Items.field_190930_cZ, new TextComponentTranslation("advancements.end.levitate.title", new Object[0]), new TextComponentTranslation("advancements.end.levitate.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).func_200271_a(AdvancementRewards.Builder.func_203907_a(50)).func_200275_a("levitated", LevitationTrigger.Instance.func_203930_a(DistancePredicate.func_203993_b(MinMaxBounds.FloatBound.func_211355_b(50.0F)))).func_203904_a(var1, "end/levitate");
      Advancement var9 = Advancement.Builder.func_200278_a().func_203905_a(var6).func_203902_a(Items.field_185160_cR, new TextComponentTranslation("advancements.end.elytra.title", new Object[0]), new TextComponentTranslation("advancements.end.elytra.description", new Object[0]), (ResourceLocation)null, FrameType.GOAL, true, true, false).func_200275_a("elytra", InventoryChangeTrigger.Instance.func_203922_a(Items.field_185160_cR)).func_203904_a(var1, "end/elytra");
      Advancement var10 = Advancement.Builder.func_200278_a().func_203905_a(var3).func_203902_a(Blocks.field_150380_bt, new TextComponentTranslation("advancements.end.dragon_egg.title", new Object[0]), new TextComponentTranslation("advancements.end.dragon_egg.description", new Object[0]), (ResourceLocation)null, FrameType.GOAL, true, true, false).func_200275_a("dragon_egg", InventoryChangeTrigger.Instance.func_203922_a(Blocks.field_150380_bt)).func_203904_a(var1, "end/dragon_egg");
   }

   // $FF: synthetic method
   public void accept(Object var1) {
      this.accept((Consumer)var1);
   }
}
