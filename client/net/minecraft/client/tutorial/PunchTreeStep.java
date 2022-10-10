package net.minecraft.client.tutorial;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.toasts.TutorialToast;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;

public class PunchTreeStep implements ITutorialStep {
   private static final ITextComponent field_193275_b = new TextComponentTranslation("tutorial.punch_tree.title", new Object[0]);
   private static final ITextComponent field_193276_c = new TextComponentTranslation("tutorial.punch_tree.description", new Object[]{Tutorial.func_193291_a("attack")});
   private final Tutorial field_193277_d;
   private TutorialToast field_193278_e;
   private int field_193279_f;
   private int field_193280_g;

   public PunchTreeStep(Tutorial var1) {
      super();
      this.field_193277_d = var1;
   }

   public void func_193245_a() {
      ++this.field_193279_f;
      if (this.field_193277_d.func_194072_f() != GameType.SURVIVAL) {
         this.field_193277_d.func_193292_a(TutorialSteps.NONE);
      } else {
         if (this.field_193279_f == 1) {
            EntityPlayerSP var1 = this.field_193277_d.func_193295_e().field_71439_g;
            if (var1 != null) {
               if (var1.field_71071_by.func_199712_a(ItemTags.field_200038_h)) {
                  this.field_193277_d.func_193292_a(TutorialSteps.CRAFT_PLANKS);
                  return;
               }

               if (FindTreeStep.func_194070_a(var1)) {
                  this.field_193277_d.func_193292_a(TutorialSteps.CRAFT_PLANKS);
                  return;
               }
            }
         }

         if ((this.field_193279_f >= 600 || this.field_193280_g > 3) && this.field_193278_e == null) {
            this.field_193278_e = new TutorialToast(TutorialToast.Icons.TREE, field_193275_b, field_193276_c, true);
            this.field_193277_d.func_193295_e().func_193033_an().func_192988_a(this.field_193278_e);
         }

      }
   }

   public void func_193248_b() {
      if (this.field_193278_e != null) {
         this.field_193278_e.func_193670_a();
         this.field_193278_e = null;
      }

   }

   public void func_193250_a(WorldClient var1, BlockPos var2, IBlockState var3, float var4) {
      boolean var5 = var3.func_203425_a(BlockTags.field_200031_h);
      if (var5 && var4 > 0.0F) {
         if (this.field_193278_e != null) {
            this.field_193278_e.func_193669_a(var4);
         }

         if (var4 >= 1.0F) {
            this.field_193277_d.func_193292_a(TutorialSteps.OPEN_INVENTORY);
         }
      } else if (this.field_193278_e != null) {
         this.field_193278_e.func_193669_a(0.0F);
      } else if (var5) {
         ++this.field_193280_g;
      }

   }

   public void func_193252_a(ItemStack var1) {
      if (ItemTags.field_200038_h.func_199685_a_(var1.func_77973_b())) {
         this.field_193277_d.func_193292_a(TutorialSteps.CRAFT_PLANKS);
      }
   }
}
