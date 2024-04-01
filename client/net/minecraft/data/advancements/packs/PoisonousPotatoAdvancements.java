package net.minecraft.data.advancements.packs;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ChangeDimensionTrigger;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.advancements.critereon.PotatoRefinementTrigger;
import net.minecraft.advancements.critereon.RecipeCraftedTrigger;
import net.minecraft.advancements.critereon.ThrowLubricatedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;

public class PoisonousPotatoAdvancements implements AdvancementSubProvider {
   public PoisonousPotatoAdvancements() {
      super();
   }

   @Override
   public void generate(HolderLookup.Provider var1, Consumer<AdvancementHolder> var2) {
      AdvancementHolder var3 = potatoAdvancement("root")
         .display(Items.POISONOUS_POTATO.getDefaultInstance(), AdvancementType.TASK, false, false, false)
         .addCriterion("joined_world", PlayerTrigger.TriggerInstance.located(Optional.empty()))
         .save(var2);
      potatoAdvancement("get_peeled")
         .parent(var3)
         .display(((Item)Items.POTATO_PEELS_MAP.get(DyeColor.WHITE)).getDefaultInstance(), AdvancementType.TASK, true, true, false)
         .addCriterion("get_peeled", PlayerTrigger.TriggerInstance.getPeeled())
         .save(var2);
      AdvancementHolder var4 = potatoAdvancement("enter_the_potato")
         .parent(var3)
         .display(Items.POTATO_OF_KNOWLEDGE.getDefaultInstance(), AdvancementType.TASK, true, true, true)
         .addCriterion("entered_potato", ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.POTATO))
         .save(var2);
      VanillaAdventureAdvancements.addBiomes(potatoAdvancement("all_potatoed"), var1, MultiNoiseBiomeSourceParameterList.Preset.POTATO.usedBiomes().toList())
         .parent(var4)
         .display(Items.GRAVTATER.getDefaultInstance(), AdvancementType.CHALLENGE, true, true, false)
         .save(var2);
      potatoAdvancement("eat_armor")
         .parent(var3)
         .display(Items.POISONOUS_POTATO_CHESTPLATE.getDefaultInstance(), AdvancementType.TASK, true, true, true)
         .addCriterion("eat_armor", PlayerTrigger.TriggerInstance.eatArmor())
         .save(var2);
      AdvancementHolder var5 = potatoAdvancement("rumbled")
         .parent(var3)
         .display(Items.POISONOUS_POTATO_PLANT.getDefaultInstance(), AdvancementType.TASK, false, true, false)
         .addCriterion("rumble_plant", PlayerTrigger.TriggerInstance.rumbleThePlant())
         .save(var2);
      potatoAdvancement("good_plant")
         .parent(var5)
         .display(Items.POTATO_STAFF.getDefaultInstance(), AdvancementType.TASK, true, true, false)
         .addCriterion("compost_staff", PlayerTrigger.TriggerInstance.compostedStaff())
         .save(var2);
      AdvancementHolder var6 = potatoAdvancement("get_oily")
         .parent(var3)
         .display(Items.POTATO_OIL.getDefaultInstance(), AdvancementType.TASK, true, false, false)
         .addCriterion("refine_potato_oil", PotatoRefinementTrigger.TriggerInstance.refined(Items.POTATO_OIL))
         .save(var2);
      AdvancementHolder var7 = potatoAdvancement("lubricate")
         .parent(var6)
         .display(Items.POTATO_OIL.getDefaultInstance(), AdvancementType.TASK, true, false, false)
         .addCriterion("lubricate_item", PotatoRefinementTrigger.TriggerInstance.lubricatedAtLeast(1))
         .save(var2);
      potatoAdvancement("mega_lubricate")
         .parent(var6)
         .display(Items.POTATO_OIL.getDefaultInstance().makeFoil(), AdvancementType.TASK, true, false, true)
         .addCriterion("mega_lubricate_item", PotatoRefinementTrigger.TriggerInstance.lubricatedAtLeast(10))
         .save(var2);
      AdvancementHolder var8 = potatoAdvancement("lubricate_whee")
         .parent(var7)
         .display(Items.ICE.getDefaultInstance(), AdvancementType.TASK, true, true, true)
         .addCriterion("throw_lubricated_item", ThrowLubricatedTrigger.TriggerInstance.thrownWithAtLeast(1))
         .save(var2);
      potatoAdvancement("mega_lubricate_whee")
         .parent(var8)
         .display(Items.ICE.getDefaultInstance().makeFoil(), AdvancementType.TASK, true, true, true)
         .addCriterion("throw_mega_lubricated_item", ThrowLubricatedTrigger.TriggerInstance.thrownWithAtLeast(10))
         .save(var2);
      potatoAdvancement("lubricate_boots")
         .parent(var7)
         .display(Items.POISONOUS_POTA_TOES.getDefaultInstance(), AdvancementType.TASK, true, true, true)
         .addCriterion(
            "lubricate_boots", PotatoRefinementTrigger.TriggerInstance.lubricatedAtLeast(ItemPredicate.Builder.item().of(ItemTags.FOOT_ARMOR).build(), 1)
         )
         .save(var2);
      potatoAdvancement("sweet_potato_talker")
         .parent(var3)
         .display(Items.POTATO_FLOWER.getDefaultInstance(), AdvancementType.TASK, true, true, false)
         .addCriterion("said_potato", PlayerTrigger.TriggerInstance.saidPotato(99))
         .save(var2);
      potatoAdvancement("craft_poisonous_potato_sticks")
         .parent(var3)
         .display(Items.POISONOUS_POTATO_STICKS.getDefaultInstance(), AdvancementType.TASK, true, false, false)
         .addCriterion("poisonous_potato_sticks", InventoryChangeTrigger.TriggerInstance.hasItems(Items.POISONOUS_POTATO_STICKS))
         .save(var2);
      potatoAdvancement("craft_poisonous_potato_slices")
         .parent(var3)
         .display(Items.POISONOUS_POTATO_SLICES.getDefaultInstance(), AdvancementType.TASK, true, false, false)
         .addCriterion("poisonous_potato_slices", InventoryChangeTrigger.TriggerInstance.hasItems(Items.POISONOUS_POTATO_SLICES))
         .save(var2);
      potatoAdvancement("craft_poisonous_potato_fries")
         .parent(var3)
         .display(Items.POISONOUS_POTATO_FRIES.getDefaultInstance(), AdvancementType.TASK, true, false, false)
         .addCriterion("poisonous_potato_fries", InventoryChangeTrigger.TriggerInstance.hasItems(Items.POISONOUS_POTATO_FRIES))
         .save(var2);
      potatoAdvancement("craft_poisonous_potato_chips")
         .parent(var3)
         .display(Items.POISONOUS_POTATO_CHIPS.getDefaultInstance(), AdvancementType.TASK, true, false, false)
         .addCriterion("poisonous_potato_chips", InventoryChangeTrigger.TriggerInstance.hasItems(Items.POISONOUS_POTATO_CHIPS))
         .save(var2);
      AdvancementHolder var9 = potatoAdvancement("poisonous_potato_taster")
         .parent(var3)
         .display(Items.POISONOUS_POTATO_STICKS.getDefaultInstance(), AdvancementType.TASK, true, true, false)
         .addCriterion("ate_poisonous_potato_sticks", ConsumeItemTrigger.TriggerInstance.usedItem(Items.POISONOUS_POTATO_STICKS))
         .addCriterion("ate_poisonous_potato_slices", ConsumeItemTrigger.TriggerInstance.usedItem(Items.POISONOUS_POTATO_SLICES))
         .save(var2);
      potatoAdvancement("poisonous_potato_gourmet")
         .parent(var9)
         .display(Items.POISONOUS_POTATO_CHIPS.getDefaultInstance(), AdvancementType.TASK, true, true, false)
         .addCriterion("ate_poisonous_potato_sticks", ConsumeItemTrigger.TriggerInstance.usedItem(Items.POISONOUS_POTATO_STICKS))
         .addCriterion("ate_poisonous_potato_slices", ConsumeItemTrigger.TriggerInstance.usedItem(Items.POISONOUS_POTATO_SLICES))
         .addCriterion("ate_poisonous_potato_fries", ConsumeItemTrigger.TriggerInstance.usedItem(Items.POISONOUS_POTATO_FRIES))
         .addCriterion("ate_poisonous_potato_chips", ConsumeItemTrigger.TriggerInstance.usedItem(Items.POISONOUS_POTATO_CHIPS))
         .save(var2);
      potatoAdvancement("bring_home_the_corruption")
         .parent(var4)
         .display(Items.CORRUPTED_PEELGRASS_BLOCK.getDefaultInstance(), AdvancementType.TASK, true, true, true)
         .addCriterion("bring_home_the_corruption", PlayerTrigger.TriggerInstance.bringHomeCorruption())
         .save(var2);
      AdvancementHolder var10 = potatoAdvancement("potato_peeler")
         .parent(var3)
         .display(Items.POTATO_PEELER.getDefaultInstance(), AdvancementType.TASK, true, false, false)
         .addCriterion("potato_peeler", InventoryChangeTrigger.TriggerInstance.hasItems(Items.POTATO_PEELER))
         .save(var2);
      potatoAdvancement("peel_all_the_things")
         .parent(var10)
         .display(Items.POTATO_PEELER.getDefaultInstance(), AdvancementType.CHALLENGE, true, true, true)
         .addCriterion("peel_block", playerTrigger(CriteriaTriggers.PEEL_BLOCK))
         .addCriterion("peel_sheep", playerTrigger(CriteriaTriggers.PEEL_POTATO_SHEEP))
         .addCriterion("peel_armor", playerTrigger(CriteriaTriggers.PEEL_POTATO_ARMOR))
         .save(var2);
      potatoAdvancement("well_done")
         .parent(var3)
         .display(Items.CHARCOAL.getDefaultInstance(), AdvancementType.TASK, true, true, false)
         .addCriterion("well_done", RecipeCraftedTrigger.TriggerInstance.craftedItem(new ResourceLocation("overcooked_potatoes")))
         .save(var2);
   }

   private static PoisonousPotatoAdvancements.ExtendedBuilder potatoAdvancement(String var0) {
      return new PoisonousPotatoAdvancements.ExtendedBuilder(var0).sendsTelemetryEvent();
   }

   private static Criterion<PlayerTrigger.TriggerInstance> playerTrigger(PlayerTrigger var0) {
      return var0.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()));
   }

   static class ExtendedBuilder extends Advancement.Builder {
      private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/advancements/backgrounds/potato.png");
      private final String name;

      ExtendedBuilder(String var1) {
         super();
         this.name = var1;
      }

      public PoisonousPotatoAdvancements.ExtendedBuilder sendsTelemetryEvent() {
         return (PoisonousPotatoAdvancements.ExtendedBuilder)super.sendsTelemetryEvent();
      }

      public PoisonousPotatoAdvancements.ExtendedBuilder display(ItemStack var1, AdvancementType var2, boolean var3, boolean var4, boolean var5) {
         return (PoisonousPotatoAdvancements.ExtendedBuilder)this.display(
            var1,
            Component.translatable("advancements.potato." + this.name + ".title"),
            Component.translatable("advancements.potato." + this.name + ".description"),
            BACKGROUND,
            var2,
            var3,
            var4,
            var5
         );
      }

      public PoisonousPotatoAdvancements.ExtendedBuilder parent(AdvancementHolder var1) {
         return (PoisonousPotatoAdvancements.ExtendedBuilder)super.parent(var1);
      }

      public PoisonousPotatoAdvancements.ExtendedBuilder rewards(AdvancementRewards.Builder var1) {
         return (PoisonousPotatoAdvancements.ExtendedBuilder)super.rewards(var1);
      }

      public PoisonousPotatoAdvancements.ExtendedBuilder rewards(AdvancementRewards var1) {
         return (PoisonousPotatoAdvancements.ExtendedBuilder)super.rewards(var1);
      }

      public PoisonousPotatoAdvancements.ExtendedBuilder addCriterion(String var1, Criterion<?> var2) {
         return (PoisonousPotatoAdvancements.ExtendedBuilder)super.addCriterion(var1, var2);
      }

      public PoisonousPotatoAdvancements.ExtendedBuilder requirements(AdvancementRequirements.Strategy var1) {
         return (PoisonousPotatoAdvancements.ExtendedBuilder)super.requirements(var1);
      }

      public PoisonousPotatoAdvancements.ExtendedBuilder requirements(AdvancementRequirements var1) {
         return (PoisonousPotatoAdvancements.ExtendedBuilder)super.requirements(var1);
      }

      public AdvancementHolder save(Consumer<AdvancementHolder> var1) {
         return this.save(var1, "potato/" + this.name);
      }
   }
}
