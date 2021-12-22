package net.minecraft.client.tutorial;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class FindTreeTutorialStepInstance implements TutorialStepInstance {
   private static final int HINT_DELAY = 6000;
   private static final Set<Block> TREE_BLOCKS;
   private static final Component TITLE;
   private static final Component DESCRIPTION;
   private final Tutorial tutorial;
   private TutorialToast toast;
   private int timeWaiting;

   public FindTreeTutorialStepInstance(Tutorial var1) {
      super();
      this.tutorial = var1;
   }

   public void tick() {
      ++this.timeWaiting;
      if (!this.tutorial.isSurvival()) {
         this.tutorial.setStep(TutorialSteps.NONE);
      } else {
         if (this.timeWaiting == 1) {
            LocalPlayer var1 = this.tutorial.getMinecraft().player;
            if (var1 != null) {
               Iterator var2 = TREE_BLOCKS.iterator();

               while(var2.hasNext()) {
                  Block var3 = (Block)var2.next();
                  if (var1.getInventory().contains(new ItemStack(var3))) {
                     this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                     return;
                  }
               }

               if (hasPunchedTreesPreviously(var1)) {
                  this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                  return;
               }
            }
         }

         if (this.timeWaiting >= 6000 && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Icons.TREE, TITLE, DESCRIPTION, false);
            this.tutorial.getMinecraft().getToasts().addToast(this.toast);
         }

      }
   }

   public void clear() {
      if (this.toast != null) {
         this.toast.hide();
         this.toast = null;
      }

   }

   public void onLookAt(ClientLevel var1, HitResult var2) {
      if (var2.getType() == HitResult.Type.BLOCK) {
         BlockState var3 = var1.getBlockState(((BlockHitResult)var2).getBlockPos());
         if (TREE_BLOCKS.contains(var3.getBlock())) {
            this.tutorial.setStep(TutorialSteps.PUNCH_TREE);
         }
      }

   }

   public void onGetItem(ItemStack var1) {
      Iterator var2 = TREE_BLOCKS.iterator();

      Block var3;
      do {
         if (!var2.hasNext()) {
            return;
         }

         var3 = (Block)var2.next();
      } while(!var1.method_87(var3.asItem()));

      this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
   }

   public static boolean hasPunchedTreesPreviously(LocalPlayer var0) {
      Iterator var1 = TREE_BLOCKS.iterator();

      Block var2;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         var2 = (Block)var1.next();
      } while(var0.getStats().getValue(Stats.BLOCK_MINED.get(var2)) <= 0);

      return true;
   }

   static {
      TREE_BLOCKS = Sets.newHashSet(new Block[]{Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG, Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG, Blocks.WARPED_STEM, Blocks.CRIMSON_STEM, Blocks.OAK_WOOD, Blocks.SPRUCE_WOOD, Blocks.BIRCH_WOOD, Blocks.JUNGLE_WOOD, Blocks.ACACIA_WOOD, Blocks.DARK_OAK_WOOD, Blocks.WARPED_HYPHAE, Blocks.CRIMSON_HYPHAE, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.NETHER_WART_BLOCK, Blocks.WARPED_WART_BLOCK, Blocks.AZALEA_LEAVES, Blocks.FLOWERING_AZALEA_LEAVES});
      TITLE = new TranslatableComponent("tutorial.find_tree.title");
      DESCRIPTION = new TranslatableComponent("tutorial.find_tree.description");
   }
}
