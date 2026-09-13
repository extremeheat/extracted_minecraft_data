package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

class GuiScreenOptionsSounds$Button extends GuiButton {
   private final SoundCategory field_146153_r;
   private final String field_146152_s;
   public float field_146156_o;
   public boolean field_146155_p;

   public GuiScreenOptionsSounds$Button(GuiScreenOptionsSounds var1, int var2, int var3, int var4, SoundCategory var5, boolean var6) {
      super(var2, var3, var4, var6 ? 310 : 150, 20, "");
      this.field_146154_q = var1;
      this.field_146156_o = 1.0F;
      this.field_146153_r = var5;
      this.field_146152_s = I18n.func_135052_a("soundCategory." + var5.func_147155_a());
      this.field_146126_j = this.field_146152_s + ": " + var1.func_146504_a(var5);
      this.field_146156_o = GuiScreenOptionsSounds.access$000(var1).func_151438_a(var5);
   }

   @Override
   public int func_146114_a(boolean var1) {
      return 0;
   }

   @Override
   protected void func_146119_b(Minecraft var1, int var2, int var3) {
      if (this.field_146125_m) {
         if (this.field_146155_p) {
            this.field_146156_o = (float)(var2 - (this.field_146128_h + 4)) / (float)(this.field_146120_f - 8);
            if (this.field_146156_o < 0.0F) {
               this.field_146156_o = 0.0F;
            }

            if (this.field_146156_o > 1.0F) {
               this.field_146156_o = 1.0F;
            }

            var1.field_71474_y.func_151439_a(this.field_146153_r, this.field_146156_o);
            var1.field_71474_y.func_74303_b();
            this.field_146126_j = this.field_146152_s + ": " + this.field_146154_q.func_146504_a(this.field_146153_r);
         }

         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.func_73729_b(this.field_146128_h + (int)(this.field_146156_o * (float)(this.field_146120_f - 8)), this.field_146129_i, 0, 66, 4, 20);
         this.func_73729_b(this.field_146128_h + (int)(this.field_146156_o * (float)(this.field_146120_f - 8)) + 4, this.field_146129_i, 196, 66, 4, 20);
      }
   }

   @Override
   public boolean func_146116_c(Minecraft var1, int var2, int var3) {
      if (super.func_146116_c(var1, var2, var3)) {
         this.field_146156_o = (float)(var2 - (this.field_146128_h + 4)) / (float)(this.field_146120_f - 8);
         if (this.field_146156_o < 0.0F) {
            this.field_146156_o = 0.0F;
         }

         if (this.field_146156_o > 1.0F) {
            this.field_146156_o = 1.0F;
         }

         var1.field_71474_y.func_151439_a(this.field_146153_r, this.field_146156_o);
         var1.field_71474_y.func_74303_b();
         this.field_146126_j = this.field_146152_s + ": " + this.field_146154_q.func_146504_a(this.field_146153_r);
         this.field_146155_p = true;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void func_146113_a(SoundHandler var1) {
   }

   @Override
   public void func_146118_a(int var1, int var2) {
      if (this.field_146155_p) {
         if (this.field_146153_r == SoundCategory.MASTER) {
            float var10000 = 1.0F;
         } else {
            GuiScreenOptionsSounds.access$000(this.field_146154_q).func_151438_a(this.field_146153_r);
         }

         this.field_146154_q.field_146297_k.func_147118_V().func_147682_a(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
      }

      this.field_146155_p = false;
   }
}
