package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuView;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class TeleportToTeam implements ISpectatorMenuView, ISpectatorMenuObject {
   private final List<ISpectatorMenuObject> field_178672_a = Lists.newArrayList();

   public TeleportToTeam() {
      super();
      Minecraft var1 = Minecraft.func_71410_x();
      Iterator var2 = var1.field_71441_e.func_96441_U().func_96525_g().iterator();

      while(var2.hasNext()) {
         ScorePlayerTeam var3 = (ScorePlayerTeam)var2.next();
         this.field_178672_a.add(new TeleportToTeam.TeamSelectionObject(var3));
      }

   }

   public List<ISpectatorMenuObject> func_178669_a() {
      return this.field_178672_a;
   }

   public ITextComponent func_178670_b() {
      return new TextComponentTranslation("spectatorMenu.team_teleport.prompt", new Object[0]);
   }

   public void func_178661_a(SpectatorMenu var1) {
      var1.func_178647_a(this);
   }

   public ITextComponent func_178664_z_() {
      return new TextComponentTranslation("spectatorMenu.team_teleport", new Object[0]);
   }

   public void func_178663_a(float var1, int var2) {
      Minecraft.func_71410_x().func_110434_K().func_110577_a(GuiSpectator.field_175269_a);
      Gui.func_146110_a(0, 0, 16.0F, 0.0F, 16, 16, 256.0F, 256.0F);
   }

   public boolean func_178662_A_() {
      Iterator var1 = this.field_178672_a.iterator();

      ISpectatorMenuObject var2;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         var2 = (ISpectatorMenuObject)var1.next();
      } while(!var2.func_178662_A_());

      return true;
   }

   class TeamSelectionObject implements ISpectatorMenuObject {
      private final ScorePlayerTeam field_178676_b;
      private final ResourceLocation field_178677_c;
      private final List<NetworkPlayerInfo> field_178675_d;

      public TeamSelectionObject(ScorePlayerTeam var2) {
         super();
         this.field_178676_b = var2;
         this.field_178675_d = Lists.newArrayList();
         Iterator var3 = var2.func_96670_d().iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            NetworkPlayerInfo var5 = Minecraft.func_71410_x().func_147114_u().func_175104_a(var4);
            if (var5 != null) {
               this.field_178675_d.add(var5);
            }
         }

         if (this.field_178675_d.isEmpty()) {
            this.field_178677_c = DefaultPlayerSkin.func_177335_a();
         } else {
            String var6 = ((NetworkPlayerInfo)this.field_178675_d.get((new Random()).nextInt(this.field_178675_d.size()))).func_178845_a().getName();
            this.field_178677_c = AbstractClientPlayer.func_110311_f(var6);
            AbstractClientPlayer.func_110304_a(this.field_178677_c, var6);
         }

      }

      public void func_178661_a(SpectatorMenu var1) {
         var1.func_178647_a(new TeleportToPlayer(this.field_178675_d));
      }

      public ITextComponent func_178664_z_() {
         return this.field_178676_b.func_96669_c();
      }

      public void func_178663_a(float var1, int var2) {
         Integer var3 = this.field_178676_b.func_178775_l().func_211163_e();
         if (var3 != null) {
            float var4 = (float)(var3 >> 16 & 255) / 255.0F;
            float var5 = (float)(var3 >> 8 & 255) / 255.0F;
            float var6 = (float)(var3 & 255) / 255.0F;
            Gui.func_73734_a(1, 1, 15, 15, MathHelper.func_180183_b(var4 * var1, var5 * var1, var6 * var1) | var2 << 24);
         }

         Minecraft.func_71410_x().func_110434_K().func_110577_a(this.field_178677_c);
         GlStateManager.func_179131_c(var1, var1, var1, (float)var2 / 255.0F);
         Gui.func_152125_a(2, 2, 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
         Gui.func_152125_a(2, 2, 40.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
      }

      public boolean func_178662_A_() {
         return !this.field_178675_d.isEmpty();
      }
   }
}
