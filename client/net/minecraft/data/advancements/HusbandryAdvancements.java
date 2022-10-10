package net.minecraft.data.advancements;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.criterion.BredAnimalsTrigger;
import net.minecraft.advancements.criterion.ConsumeItemTrigger;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.FilledBucketTrigger;
import net.minecraft.advancements.criterion.FishingRodHookedTrigger;
import net.minecraft.advancements.criterion.ItemDurabilityTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.PlacedBlockTrigger;
import net.minecraft.advancements.criterion.TameAnimalTrigger;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentTranslation;

public class HusbandryAdvancements implements Consumer<Consumer<Advancement>> {
   private static final EntityType<?>[] field_204290_a;
   private static final Item[] field_204866_b;
   private static final Item[] field_204867_c;
   private static final Item[] field_204291_b;

   public HusbandryAdvancements() {
      super();
   }

   public void accept(Consumer<Advancement> var1) {
      Advancement var2 = Advancement.Builder.func_200278_a().func_203902_a(Blocks.field_150407_cf, new TextComponentTranslation("advancements.husbandry.root.title", new Object[0]), new TextComponentTranslation("advancements.husbandry.root.description", new Object[0]), new ResourceLocation("minecraft:textures/gui/advancements/backgrounds/husbandry.png"), FrameType.TASK, false, false, false).func_200275_a("consumed_item", ConsumeItemTrigger.Instance.func_203914_c()).func_203904_a(var1, "husbandry/root");
      Advancement var3 = Advancement.Builder.func_200278_a().func_203905_a(var2).func_203902_a(Items.field_151015_O, new TextComponentTranslation("advancements.husbandry.plant_seed.title", new Object[0]), new TextComponentTranslation("advancements.husbandry.plant_seed.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200270_a(RequirementsStrategy.OR).func_200275_a("wheat", PlacedBlockTrigger.Instance.func_203934_a(Blocks.field_150464_aj)).func_200275_a("pumpkin_stem", PlacedBlockTrigger.Instance.func_203934_a(Blocks.field_150393_bb)).func_200275_a("melon_stem", PlacedBlockTrigger.Instance.func_203934_a(Blocks.field_150394_bc)).func_200275_a("beetroots", PlacedBlockTrigger.Instance.func_203934_a(Blocks.field_185773_cZ)).func_200275_a("nether_wart", PlacedBlockTrigger.Instance.func_203934_a(Blocks.field_150388_bm)).func_203904_a(var1, "husbandry/plant_seed");
      Advancement var4 = Advancement.Builder.func_200278_a().func_203905_a(var2).func_203902_a(Items.field_151015_O, new TextComponentTranslation("advancements.husbandry.breed_an_animal.title", new Object[0]), new TextComponentTranslation("advancements.husbandry.breed_an_animal.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200270_a(RequirementsStrategy.OR).func_200275_a("bred", BredAnimalsTrigger.Instance.func_203908_c()).func_203904_a(var1, "husbandry/breed_an_animal");
      Advancement var5 = this.func_204288_a(Advancement.Builder.func_200278_a()).func_203905_a(var3).func_203902_a(Items.field_151034_e, new TextComponentTranslation("advancements.husbandry.balanced_diet.title", new Object[0]), new TextComponentTranslation("advancements.husbandry.balanced_diet.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).func_200271_a(AdvancementRewards.Builder.func_203907_a(100)).func_203904_a(var1, "husbandry/balanced_diet");
      Advancement var6 = Advancement.Builder.func_200278_a().func_203905_a(var3).func_203902_a(Items.field_151012_L, new TextComponentTranslation("advancements.husbandry.break_diamond_hoe.title", new Object[0]), new TextComponentTranslation("advancements.husbandry.break_diamond_hoe.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).func_200271_a(AdvancementRewards.Builder.func_203907_a(100)).func_200275_a("broke_hoe", ItemDurabilityTrigger.Instance.func_211182_a(ItemPredicate.Builder.func_200309_a().func_200308_a(Items.field_151012_L).func_200310_b(), MinMaxBounds.IntBound.func_211345_a(-1))).func_203904_a(var1, "husbandry/break_diamond_hoe");
      Advancement var7 = Advancement.Builder.func_200278_a().func_203905_a(var2).func_203902_a(Items.field_151058_ca, new TextComponentTranslation("advancements.husbandry.tame_an_animal.title", new Object[0]), new TextComponentTranslation("advancements.husbandry.tame_an_animal.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_200275_a("tamed_animal", TameAnimalTrigger.Instance.func_203938_c()).func_203904_a(var1, "husbandry/tame_an_animal");
      Advancement var8 = this.func_204289_b(Advancement.Builder.func_200278_a()).func_203905_a(var4).func_203902_a(Items.field_151150_bK, new TextComponentTranslation("advancements.husbandry.breed_all_animals.title", new Object[0]), new TextComponentTranslation("advancements.husbandry.breed_all_animals.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).func_200271_a(AdvancementRewards.Builder.func_203907_a(100)).func_203904_a(var1, "husbandry/bred_all_animals");
      Advancement var9 = this.func_204864_d(Advancement.Builder.func_200278_a()).func_203905_a(var2).func_200270_a(RequirementsStrategy.OR).func_203902_a(Items.field_151112_aM, new TextComponentTranslation("advancements.husbandry.fishy_business.title", new Object[0]), new TextComponentTranslation("advancements.husbandry.fishy_business.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_203904_a(var1, "husbandry/fishy_business");
      Advancement var10 = this.func_204865_c(Advancement.Builder.func_200278_a()).func_203905_a(var9).func_200270_a(RequirementsStrategy.OR).func_203902_a(Items.field_203795_aL, new TextComponentTranslation("advancements.husbandry.tactical_fishing.title", new Object[0]), new TextComponentTranslation("advancements.husbandry.tactical_fishing.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).func_203904_a(var1, "husbandry/tactical_fishing");
   }

   private Advancement.Builder func_204288_a(Advancement.Builder var1) {
      Item[] var2 = field_204291_b;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Item var5 = var2[var4];
         var1.func_200275_a(IRegistry.field_212630_s.func_177774_c(var5).func_110623_a(), ConsumeItemTrigger.Instance.func_203913_a(var5));
      }

      return var1;
   }

   private Advancement.Builder func_204289_b(Advancement.Builder var1) {
      EntityType[] var2 = field_204290_a;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EntityType var5 = var2[var4];
         var1.func_200275_a(EntityType.func_200718_a(var5).toString(), BredAnimalsTrigger.Instance.func_203909_a(EntityPredicate.Builder.func_203996_a().func_203998_a(var5)));
      }

      return var1;
   }

   private Advancement.Builder func_204865_c(Advancement.Builder var1) {
      Item[] var2 = field_204867_c;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Item var5 = var2[var4];
         var1.func_200275_a(IRegistry.field_212630_s.func_177774_c(var5).func_110623_a(), FilledBucketTrigger.Instance.func_204827_a(ItemPredicate.Builder.func_200309_a().func_200308_a(var5).func_200310_b()));
      }

      return var1;
   }

   private Advancement.Builder func_204864_d(Advancement.Builder var1) {
      Item[] var2 = field_204866_b;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Item var5 = var2[var4];
         var1.func_200275_a(IRegistry.field_212630_s.func_177774_c(var5).func_110623_a(), FishingRodHookedTrigger.Instance.func_204829_a(ItemPredicate.field_192495_a, EntityPredicate.field_192483_a, ItemPredicate.Builder.func_200309_a().func_200308_a(var5).func_200310_b()));
      }

      return var1;
   }

   // $FF: synthetic method
   public void accept(Object var1) {
      this.accept((Consumer)var1);
   }

   static {
      field_204290_a = new EntityType[]{EntityType.field_200762_B, EntityType.field_200737_ac, EntityType.field_200796_j, EntityType.field_200780_T, EntityType.field_200784_X, EntityType.field_200795_i, EntityType.field_200724_aC, EntityType.field_200781_U, EntityType.field_200736_ab, EntityType.field_200769_I, EntityType.field_203099_aq};
      field_204866_b = new Item[]{Items.field_196086_aW, Items.field_196088_aY, Items.field_196089_aZ, Items.field_196087_aX};
      field_204867_c = new Item[]{Items.field_203797_aN, Items.field_204272_aO, Items.field_203795_aL, Items.field_203796_aM};
      field_204291_b = new Item[]{Items.field_151034_e, Items.field_151009_A, Items.field_151025_P, Items.field_151147_al, Items.field_151157_am, Items.field_151153_ao, Items.field_196100_at, Items.field_196086_aW, Items.field_196087_aX, Items.field_196088_aY, Items.field_196089_aZ, Items.field_196102_ba, Items.field_196104_bb, Items.field_151106_aX, Items.field_151127_ba, Items.field_151082_bd, Items.field_151083_be, Items.field_151076_bf, Items.field_151077_bg, Items.field_151078_bh, Items.field_151070_bp, Items.field_151172_bF, Items.field_151174_bG, Items.field_151168_bH, Items.field_151170_bI, Items.field_151150_bK, Items.field_151158_bO, Items.field_179558_bo, Items.field_179559_bp, Items.field_179560_bq, Items.field_179561_bm, Items.field_179557_bn, Items.field_185161_cS, Items.field_185164_cV, Items.field_185165_cW, Items.field_203180_bP};
   }
}
