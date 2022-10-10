package net.minecraft.client.gui.spectator;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.spectator.categories.SpectatorDetails;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class SpectatorMenu {
   private static final ISpectatorMenuObject field_178655_b = new SpectatorMenu.EndSpectatorObject();
   private static final ISpectatorMenuObject field_178656_c = new SpectatorMenu.MoveMenuObject(-1, true);
   private static final ISpectatorMenuObject field_178653_d = new SpectatorMenu.MoveMenuObject(1, true);
   private static final ISpectatorMenuObject field_178654_e = new SpectatorMenu.MoveMenuObject(1, false);
   public static final ISpectatorMenuObject field_178657_a = new ISpectatorMenuObject() {
      public void func_178661_a(SpectatorMenu var1) {
      }

      public ITextComponent func_178664_z_() {
         return new TextComponentString("");
      }

      public void func_178663_a(float var1, int var2) {
      }

      public boolean func_178662_A_() {
         return false;
      }
   };
   private final ISpectatorMenuRecipient field_178651_f;
   private final List<SpectatorDetails> field_178652_g = Lists.newArrayList();
   private ISpectatorMenuView field_178659_h = new BaseSpectatorGroup();
   private int field_178660_i = -1;
   private int field_178658_j;

   public SpectatorMenu(ISpectatorMenuRecipient var1) {
      super();
      this.field_178651_f = var1;
   }

   public ISpectatorMenuObject func_178643_a(int var1) {
      int var2 = var1 + this.field_178658_j * 6;
      if (this.field_178658_j > 0 && var1 == 0) {
         return field_178656_c;
      } else if (var1 == 7) {
         return var2 < this.field_178659_h.func_178669_a().size() ? field_178653_d : field_178654_e;
      } else if (var1 == 8) {
         return field_178655_b;
      } else {
         return var2 >= 0 && var2 < this.field_178659_h.func_178669_a().size() ? (ISpectatorMenuObject)MoreObjects.firstNonNull(this.field_178659_h.func_178669_a().get(var2), field_178657_a) : field_178657_a;
      }
   }

   public List<ISpectatorMenuObject> func_178642_a() {
      ArrayList var1 = Lists.newArrayList();

      for(int var2 = 0; var2 <= 8; ++var2) {
         var1.add(this.func_178643_a(var2));
      }

      return var1;
   }

   public ISpectatorMenuObject func_178645_b() {
      return this.func_178643_a(this.field_178660_i);
   }

   public ISpectatorMenuView func_178650_c() {
      return this.field_178659_h;
   }

   public void func_178644_b(int var1) {
      ISpectatorMenuObject var2 = this.func_178643_a(var1);
      if (var2 != field_178657_a) {
         if (this.field_178660_i == var1 && var2.func_178662_A_()) {
            var2.func_178661_a(this);
         } else {
            this.field_178660_i = var1;
         }
      }

   }

   public void func_178641_d() {
      this.field_178651_f.func_175257_a(this);
   }

   public int func_178648_e() {
      return this.field_178660_i;
   }

   public void func_178647_a(ISpectatorMenuView var1) {
      this.field_178652_g.add(this.func_178646_f());
      this.field_178659_h = var1;
      this.field_178660_i = -1;
      this.field_178658_j = 0;
   }

   public SpectatorDetails func_178646_f() {
      return new SpectatorDetails(this.field_178659_h, this.func_178642_a(), this.field_178660_i);
   }

   static class MoveMenuObject implements ISpectatorMenuObject {
      private final int field_178666_a;
      private final boolean field_178665_b;

      public MoveMenuObject(int var1, boolean var2) {
         super();
         this.field_178666_a = var1;
         this.field_178665_b = var2;
      }

      public void func_178661_a(SpectatorMenu var1) {
         var1.field_178658_j = var1.field_178658_j + this.field_178666_a;
      }

      public ITextComponent func_178664_z_() {
         return this.field_178666_a < 0 ? new TextComponentTranslation("spectatorMenu.previous_page", new Object[0]) : new TextComponentTranslation("spectatorMenu.next_page", new Object[0]);
      }

      public void func_178663_a(float var1, int var2) {
         Minecraft.func_71410_x().func_110434_K().func_110577_a(GuiSpectator.field_175269_a);
         if (this.field_178666_a < 0) {
            Gui.func_146110_a(0, 0, 144.0F, 0.0F, 16, 16, 256.0F, 256.0F);
         } else {
            Gui.func_146110_a(0, 0, 160.0F, 0.0F, 16, 16, 256.0F, 256.0F);
         }

      }

      public boolean func_178662_A_() {
         return this.field_178665_b;
      }
   }

   static class EndSpectatorObject implements ISpectatorMenuObject {
      private EndSpectatorObject() {
         super();
      }

      public void func_178661_a(SpectatorMenu var1) {
         var1.func_178641_d();
      }

      public ITextComponent func_178664_z_() {
         return new TextComponentTranslation("spectatorMenu.close", new Object[0]);
      }

      public void func_178663_a(float var1, int var2) {
         Minecraft.func_71410_x().func_110434_K().func_110577_a(GuiSpectator.field_175269_a);
         Gui.func_146110_a(0, 0, 128.0F, 0.0F, 16, 16, 256.0F, 256.0F);
      }

      public boolean func_178662_A_() {
         return true;
      }

      // $FF: synthetic method
      EndSpectatorObject(Object var1) {
         this();
      }
   }
}
