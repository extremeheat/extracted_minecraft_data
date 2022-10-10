package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.CPacketUpdateCommandBlock;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.math.BlockPos;

public class GuiCommandBlock extends GuiCommandBlockBase {
   private final TileEntityCommandBlock field_184078_g;
   private GuiButton field_184079_s;
   private GuiButton field_184080_t;
   private GuiButton field_184081_u;
   private TileEntityCommandBlock.Mode field_184082_w;
   private boolean field_184084_y;
   private boolean field_184085_z;

   public GuiCommandBlock(TileEntityCommandBlock var1) {
      super();
      this.field_184082_w = TileEntityCommandBlock.Mode.REDSTONE;
      this.field_184078_g = var1;
   }

   CommandBlockBaseLogic func_195231_h() {
      return this.field_184078_g.func_145993_a();
   }

   int func_195236_i() {
      return 135;
   }

   protected void func_73866_w_() {
      super.func_73866_w_();
      this.field_184079_s = this.func_189646_b(new GuiButton(5, this.field_146294_l / 2 - 50 - 100 - 4, 165, 100, 20, I18n.func_135052_a("advMode.mode.sequence")) {
         public void func_194829_a(double var1, double var3) {
            GuiCommandBlock.this.func_184074_h();
            GuiCommandBlock.this.func_184073_g();
         }
      });
      this.field_184080_t = this.func_189646_b(new GuiButton(6, this.field_146294_l / 2 - 50, 165, 100, 20, I18n.func_135052_a("advMode.mode.unconditional")) {
         public void func_194829_a(double var1, double var3) {
            GuiCommandBlock.this.field_184084_y = !GuiCommandBlock.this.field_184084_y;
            GuiCommandBlock.this.func_184077_i();
         }
      });
      this.field_184081_u = this.func_189646_b(new GuiButton(7, this.field_146294_l / 2 + 50 + 4, 165, 100, 20, I18n.func_135052_a("advMode.mode.redstoneTriggered")) {
         public void func_194829_a(double var1, double var3) {
            GuiCommandBlock.this.field_184085_z = !GuiCommandBlock.this.field_184085_z;
            GuiCommandBlock.this.func_184076_j();
         }
      });
      this.field_195240_g.field_146124_l = false;
      this.field_195242_i.field_146124_l = false;
      this.field_184079_s.field_146124_l = false;
      this.field_184080_t.field_146124_l = false;
      this.field_184081_u.field_146124_l = false;
   }

   public void func_184075_a() {
      CommandBlockBaseLogic var1 = this.field_184078_g.func_145993_a();
      this.field_195237_a.func_146180_a(var1.func_145753_i());
      this.field_195238_s = var1.func_175571_m();
      this.field_184082_w = this.field_184078_g.func_184251_i();
      this.field_184084_y = this.field_184078_g.func_184258_j();
      this.field_184085_z = this.field_184078_g.func_184254_e();
      this.func_195233_j();
      this.func_184073_g();
      this.func_184077_i();
      this.func_184076_j();
      this.field_195240_g.field_146124_l = true;
      this.field_195242_i.field_146124_l = true;
      this.field_184079_s.field_146124_l = true;
      this.field_184080_t.field_146124_l = true;
      this.field_184081_u.field_146124_l = true;
   }

   public void func_175273_b(Minecraft var1, int var2, int var3) {
      super.func_175273_b(var1, var2, var3);
      this.func_195233_j();
      this.func_184073_g();
      this.func_184077_i();
      this.func_184076_j();
      this.field_195240_g.field_146124_l = true;
      this.field_195242_i.field_146124_l = true;
      this.field_184079_s.field_146124_l = true;
      this.field_184080_t.field_146124_l = true;
      this.field_184081_u.field_146124_l = true;
   }

   protected void func_195235_a(CommandBlockBaseLogic var1) {
      this.field_146297_k.func_147114_u().func_147297_a(new CPacketUpdateCommandBlock(new BlockPos(var1.func_210165_f()), this.field_195237_a.func_146179_b(), this.field_184082_w, var1.func_175571_m(), this.field_184084_y, this.field_184085_z));
   }

   private void func_184073_g() {
      switch(this.field_184082_w) {
      case SEQUENCE:
         this.field_184079_s.field_146126_j = I18n.func_135052_a("advMode.mode.sequence");
         break;
      case AUTO:
         this.field_184079_s.field_146126_j = I18n.func_135052_a("advMode.mode.auto");
         break;
      case REDSTONE:
         this.field_184079_s.field_146126_j = I18n.func_135052_a("advMode.mode.redstone");
      }

   }

   private void func_184074_h() {
      switch(this.field_184082_w) {
      case SEQUENCE:
         this.field_184082_w = TileEntityCommandBlock.Mode.AUTO;
         break;
      case AUTO:
         this.field_184082_w = TileEntityCommandBlock.Mode.REDSTONE;
         break;
      case REDSTONE:
         this.field_184082_w = TileEntityCommandBlock.Mode.SEQUENCE;
      }

   }

   private void func_184077_i() {
      if (this.field_184084_y) {
         this.field_184080_t.field_146126_j = I18n.func_135052_a("advMode.mode.conditional");
      } else {
         this.field_184080_t.field_146126_j = I18n.func_135052_a("advMode.mode.unconditional");
      }

   }

   private void func_184076_j() {
      if (this.field_184085_z) {
         this.field_184081_u.field_146126_j = I18n.func_135052_a("advMode.mode.autoexec.bat");
      } else {
         this.field_184081_u.field_146126_j = I18n.func_135052_a("advMode.mode.redstoneTriggered");
      }

   }
}
