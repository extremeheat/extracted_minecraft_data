package net.minecraft.client.gui;

import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.network.play.client.CPacketUpdateCommandMinecart;
import net.minecraft.tileentity.CommandBlockBaseLogic;

public class GuiEditCommandBlockMinecart extends GuiCommandBlockBase {
   private final CommandBlockBaseLogic field_184093_g;

   public GuiEditCommandBlockMinecart(CommandBlockBaseLogic var1) {
      super();
      this.field_184093_g = var1;
   }

   public CommandBlockBaseLogic func_195231_h() {
      return this.field_184093_g;
   }

   int func_195236_i() {
      return 150;
   }

   protected void func_73866_w_() {
      super.func_73866_w_();
      this.field_195238_s = this.func_195231_h().func_175571_m();
      this.func_195233_j();
      this.field_195237_a.func_146180_a(this.func_195231_h().func_145753_i());
   }

   protected void func_195235_a(CommandBlockBaseLogic var1) {
      if (var1 instanceof EntityMinecartCommandBlock.MinecartCommandLogic) {
         EntityMinecartCommandBlock.MinecartCommandLogic var2 = (EntityMinecartCommandBlock.MinecartCommandLogic)var1;
         this.field_146297_k.func_147114_u().func_147297_a(new CPacketUpdateCommandMinecart(var2.func_210167_g().func_145782_y(), this.field_195237_a.func_146179_b(), var1.func_175571_m()));
      }

   }
}
