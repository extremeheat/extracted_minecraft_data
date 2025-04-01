package net.minecraft.data.advancements.packs;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.players.PlayerUnlock;
import net.minecraft.server.players.PlayerUnlocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.mines.WorldEffects;

public class VanillaUnlockAdvancements implements AdvancementSubProvider {
   public VanillaUnlockAdvancements() {
      super();
   }

   public void generate(HolderLookup.Provider var1, Consumer<AdvancementHolder> var2) {
      HolderLookup.RegistryLookup var3 = var1.lookupOrThrow(Registries.ITEM);
      AdvancementHolder var4 = Advancement.Builder.advancement().display((ItemLike)Items.EXPERIENCE_BOTTLE, Component.translatable("advancements.unlocks.root.title"), Component.translatable("advancements.unlocks.root.description"), ResourceLocation.withDefaultNamespace("gui/advancements/backgrounds/unlocks"), AdvancementType.TASK, false, false, false).addCriterion("complete_level", PlayerTrigger.TriggerInstance.levelCompleted()).save(var2, "unlocks/root");
      AdvancementHolder var5 = Advancement.Builder.advancement().parent(var4).display((ItemLike)Items.BOOK, Component.translatable("advancements.unlocks.player_unlock_bought.title"), Component.translatable("advancements.unlocks.player_unlock_bought.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).rewards(AdvancementRewards.Builder.experience(10)).addCriterion("player_unlock_bought", PlayerTrigger.TriggerInstance.playerUnlockBought()).save(var2, "unlocks/kaizen");
      Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.POISONOUS_POTATO, Component.translatable("advancements.unlocks.player_unlock_unlocked.title"), Component.translatable("advancements.unlocks.player_unlock_unlocked.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).rewards(AdvancementRewards.Builder.experience(10)).addCriterion("player_unlock_unlocked", PlayerTrigger.TriggerInstance.playerUnlockUnlocked()).save(var2, "unlocks/how_do_i_unlock_that");
      AdvancementHolder var6 = Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.BOOKSHELF, Component.translatable("advancements.unlocks.sigma_grindset.title"), Component.translatable("advancements.unlocks.sigma_grindset.description"), (ResourceLocation)null, AdvancementType.CHALLENGE, true, true, true).addCriterion("player_unlock_bought", PlayerTrigger.TriggerInstance.playerUnlockBought(PlayerUnlocks.LEARNING_20)).save(var2, "unlocks/sigma_grindset");
      Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.LEAD, Component.translatable("advancements.unlocks.in_it_together.title"), Component.translatable("advancements.unlocks.in_it_together.description"), (ResourceLocation)null, AdvancementType.CHALLENGE, true, true, true).addCriterion("complete_level", PlayerTrigger.TriggerInstance.specialMineCompletedWithEffects(WorldEffects.SOUL_LINK)).save(var2, "unlocks/in_it_together");
      allUnlocksAdvancement(var2, var5, PlayerUnlocks.SCHOOL_OF_HARD_KNOCKS);
      allUnlocksAdvancement(var2, var6, PlayerUnlocks.EXPLORATION);
      allUnlocksAdvancement(var2, var5, PlayerUnlocks.CRAFTING);
      allUnlocksAdvancement(var2, var5, PlayerUnlocks.GATHERER);
      allUnlocksAdvancement(var2, var5, PlayerUnlocks.COMBATANT);
      Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.SLIME_BLOCK, Component.translatable("advancements.unlocks.jump_king.title"), Component.translatable("advancements.unlocks.jump_king.description"), (ResourceLocation)null, AdvancementType.CHALLENGE, true, true, true).addCriterion("player_unlock_bought", PlayerTrigger.TriggerInstance.playerUnlockBought(PlayerUnlocks.JUMPING_10)).save(var2, "unlocks/jump_king");
      Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.FIRE_WAND, Component.translatable("advancements.unlocks.free_sticks.title"), Component.translatable("advancements.unlocks.free_sticks.description"), (ResourceLocation)null, AdvancementType.CHALLENGE, true, true, false).addCriterion("complete_level", PlayerTrigger.TriggerInstance.levelCompletedWithPlayerUnlocks(PlayerUnlocks.FIRE_WAND, PlayerUnlocks.WIND_WAND, PlayerUnlocks.TELEPORTATION_WAND)).save(var2, "unlocks/free_sticks");
   }

   private static void allUnlocksAdvancement(Consumer<AdvancementHolder> var0, AdvancementHolder var1, Holder<PlayerUnlock> var2) {
      String var3 = "all_" + ((ResourceKey)var2.unwrapKey().get()).location().getPath();
      Advancement.Builder var4 = Advancement.Builder.advancement().parent(var1).display((ItemStack)((PlayerUnlock)var2.value()).display().getIcon(), Component.translatable("advancements.unlocks." + var3 + ".title"), Component.translatable("advancements.unlocks." + var3 + ".description"), (ResourceLocation)null, AdvancementType.CHALLENGE, true, true, false);
      BuiltInRegistries.PLAYER_UNLOCK.listElements().filter((var1x) -> rootOf(var1x) == var2).forEach((var1x) -> var4.addCriterion(var1x.getRegisteredName(), PlayerTrigger.TriggerInstance.specialMineCompletedWithPlayerUnlocks(var1x)));
      var4.save(var0, "unlocks/" + var3);
   }

   private static Holder<PlayerUnlock> rootOf(Holder<PlayerUnlock> var0) {
      return (Holder)((PlayerUnlock)var0.value()).parent().map(VanillaUnlockAdvancements::rootOf).orElse(var0);
   }
}
