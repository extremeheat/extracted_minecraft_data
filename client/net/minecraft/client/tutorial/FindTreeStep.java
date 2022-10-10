package net.minecraft.client.tutorial;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.toasts.TutorialToast;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;

public class FindTreeStep implements ITutorialStep {
   private static final Set<Block> field_193268_a;
   private static final ITextComponent field_193269_b;
   private static final ITextComponent field_193270_c;
   private final Tutorial field_193271_d;
   private TutorialToast field_193272_e;
   private int field_193273_f;

   public FindTreeStep(Tutorial var1) {
      super();
      this.field_193271_d = var1;
   }

   public void func_193245_a() {
      ++this.field_193273_f;
      if (this.field_193271_d.func_194072_f() != GameType.SURVIVAL) {
         this.field_193271_d.func_193292_a(TutorialSteps.NONE);
      } else {
         if (this.field_193273_f == 1) {
            EntityPlayerSP var1 = this.field_193271_d.func_193295_e().field_71439_g;
            if (var1 != null) {
               Iterator var2 = field_193268_a.iterator();

               while(var2.hasNext()) {
                  Block var3 = (Block)var2.next();
                  if (var1.field_71071_by.func_70431_c(new ItemStack(var3))) {
                     this.field_193271_d.func_193292_a(TutorialSteps.CRAFT_PLANKS);
                     return;
                  }
               }

               if (func_194070_a(var1)) {
                  this.field_193271_d.func_193292_a(TutorialSteps.CRAFT_PLANKS);
                  return;
               }
            }
         }

         if (this.field_193273_f >= 6000 && this.field_193272_e == null) {
            this.field_193272_e = new TutorialToast(TutorialToast.Icons.TREE, field_193269_b, field_193270_c, false);
            this.field_193271_d.func_193295_e().func_193033_an().func_192988_a(this.field_193272_e);
         }

      }
   }

   public void func_193248_b() {
      if (this.field_193272_e != null) {
         this.field_193272_e.func_193670_a();
         this.field_193272_e = null;
      }

   }

   public void func_193246_a(WorldClient var1, RayTraceResult var2) {
      if (var2.field_72313_a == RayTraceResult.Type.BLOCK && var2.func_178782_a() != null) {
         IBlockState var3 = var1.func_180495_p(var2.func_178782_a());
         if (field_193268_a.contains(var3.func_177230_c())) {
            this.field_193271_d.func_193292_a(TutorialSteps.PUNCH_TREE);
         }
      }

   }

   public void func_193252_a(ItemStack var1) {
      Iterator var2 = field_193268_a.iterator();

      Block var3;
      do {
         if (!var2.hasNext()) {
            return;
         }

         var3 = (Block)var2.next();
      } while(var1.func_77973_b() != var3.func_199767_j());

      this.field_193271_d.func_193292_a(TutorialSteps.CRAFT_PLANKS);
   }

   public static boolean func_194070_a(EntityPlayerSP var0) {
      Iterator var1 = field_193268_a.iterator();

      Block var2;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         var2 = (Block)var1.next();
      } while(var0.func_146107_m().func_77444_a(StatList.field_188065_ae.func_199076_b(var2)) <= 0);

      return true;
   }

   static {
      field_193268_a = Sets.newHashSet(new Block[]{Blocks.field_196617_K, Blocks.field_196618_L, Blocks.field_196619_M, Blocks.field_196620_N, Blocks.field_196621_O, Blocks.field_196623_P, Blocks.field_196626_Q, Blocks.field_196629_R, Blocks.field_196631_S, Blocks.field_196634_T, Blocks.field_196637_U, Blocks.field_196639_V, Blocks.field_196642_W, Blocks.field_196645_X, Blocks.field_196647_Y, Blocks.field_196648_Z, Blocks.field_196572_aa, Blocks.field_196574_ab});
      field_193269_b = new TextComponentTranslation("tutorial.find_tree.title", new Object[0]);
      field_193270_c = new TextComponentTranslation("tutorial.find_tree.description", new Object[0]);
   }
}
