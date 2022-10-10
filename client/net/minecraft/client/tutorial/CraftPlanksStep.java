package net.minecraft.client.tutorial;

import java.util.Iterator;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.toasts.TutorialToast;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;

public class CraftPlanksStep implements ITutorialStep {
   private static final ITextComponent field_193286_a = new TextComponentTranslation("tutorial.craft_planks.title", new Object[0]);
   private static final ITextComponent field_193287_b = new TextComponentTranslation("tutorial.craft_planks.description", new Object[0]);
   private final Tutorial field_193288_c;
   private TutorialToast field_193289_d;
   private int field_193290_e;

   public CraftPlanksStep(Tutorial var1) {
      super();
      this.field_193288_c = var1;
   }

   public void func_193245_a() {
      ++this.field_193290_e;
      if (this.field_193288_c.func_194072_f() != GameType.SURVIVAL) {
         this.field_193288_c.func_193292_a(TutorialSteps.NONE);
      } else {
         if (this.field_193290_e == 1) {
            EntityPlayerSP var1 = this.field_193288_c.func_193295_e().field_71439_g;
            if (var1 != null) {
               if (var1.field_71071_by.func_199712_a(ItemTags.field_199905_b)) {
                  this.field_193288_c.func_193292_a(TutorialSteps.NONE);
                  return;
               }

               if (func_199761_a(var1, ItemTags.field_199905_b)) {
                  this.field_193288_c.func_193292_a(TutorialSteps.NONE);
                  return;
               }
            }
         }

         if (this.field_193290_e >= 1200 && this.field_193289_d == null) {
            this.field_193289_d = new TutorialToast(TutorialToast.Icons.WOODEN_PLANKS, field_193286_a, field_193287_b, false);
            this.field_193288_c.func_193295_e().func_193033_an().func_192988_a(this.field_193289_d);
         }

      }
   }

   public void func_193248_b() {
      if (this.field_193289_d != null) {
         this.field_193289_d.func_193670_a();
         this.field_193289_d = null;
      }

   }

   public void func_193252_a(ItemStack var1) {
      Item var2 = var1.func_77973_b();
      if (ItemTags.field_199905_b.func_199685_a_(var2)) {
         this.field_193288_c.func_193292_a(TutorialSteps.NONE);
      }

   }

   public static boolean func_199761_a(EntityPlayerSP var0, Tag<Item> var1) {
      Iterator var2 = var1.func_199885_a().iterator();

      Item var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (Item)var2.next();
      } while(var0.func_146107_m().func_77444_a(StatList.field_188066_af.func_199076_b(var3)) <= 0);

      return true;
   }
}
