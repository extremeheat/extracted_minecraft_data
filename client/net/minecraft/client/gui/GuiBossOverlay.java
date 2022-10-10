package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.BossInfo;

public class GuiBossOverlay extends Gui {
   private static final ResourceLocation field_184058_a = new ResourceLocation("textures/gui/bars.png");
   private final Minecraft field_184059_f;
   private final Map<UUID, BossInfoClient> field_184060_g = Maps.newLinkedHashMap();

   public GuiBossOverlay(Minecraft var1) {
      super();
      this.field_184059_f = var1;
   }

   public void func_184051_a() {
      if (!this.field_184060_g.isEmpty()) {
         int var1 = this.field_184059_f.field_195558_d.func_198107_o();
         int var2 = 12;
         Iterator var3 = this.field_184060_g.values().iterator();

         while(var3.hasNext()) {
            BossInfoClient var4 = (BossInfoClient)var3.next();
            int var5 = var1 / 2 - 91;
            GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_184059_f.func_110434_K().func_110577_a(field_184058_a);
            this.func_184052_a(var5, var2, var4);
            String var7 = var4.func_186744_e().func_150254_d();
            this.field_184059_f.field_71466_p.func_175063_a(var7, (float)(var1 / 2 - this.field_184059_f.field_71466_p.func_78256_a(var7) / 2), (float)(var2 - 9), 16777215);
            var2 += 10 + this.field_184059_f.field_71466_p.field_78288_b;
            if (var2 >= this.field_184059_f.field_195558_d.func_198087_p() / 3) {
               break;
            }
         }

      }
   }

   private void func_184052_a(int var1, int var2, BossInfo var3) {
      this.func_73729_b(var1, var2, 0, var3.func_186736_g().ordinal() * 5 * 2, 182, 5);
      if (var3.func_186740_h() != BossInfo.Overlay.PROGRESS) {
         this.func_73729_b(var1, var2, 0, 80 + (var3.func_186740_h().ordinal() - 1) * 5 * 2, 182, 5);
      }

      int var4 = (int)(var3.func_186738_f() * 183.0F);
      if (var4 > 0) {
         this.func_73729_b(var1, var2, 0, var3.func_186736_g().ordinal() * 5 * 2 + 5, var4, 5);
         if (var3.func_186740_h() != BossInfo.Overlay.PROGRESS) {
            this.func_73729_b(var1, var2, 0, 80 + (var3.func_186740_h().ordinal() - 1) * 5 * 2 + 5, var4, 5);
         }
      }

   }

   public void func_184055_a(SPacketUpdateBossInfo var1) {
      if (var1.func_186902_b() == SPacketUpdateBossInfo.Operation.ADD) {
         this.field_184060_g.put(var1.func_186908_a(), new BossInfoClient(var1));
      } else if (var1.func_186902_b() == SPacketUpdateBossInfo.Operation.REMOVE) {
         this.field_184060_g.remove(var1.func_186908_a());
      } else {
         ((BossInfoClient)this.field_184060_g.get(var1.func_186908_a())).func_186765_a(var1);
      }

   }

   public void func_184057_b() {
      this.field_184060_g.clear();
   }

   public boolean func_184054_d() {
      if (!this.field_184060_g.isEmpty()) {
         Iterator var1 = this.field_184060_g.values().iterator();

         while(var1.hasNext()) {
            BossInfo var2 = (BossInfo)var1.next();
            if (var2.func_186747_j()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean func_184053_e() {
      if (!this.field_184060_g.isEmpty()) {
         Iterator var1 = this.field_184060_g.values().iterator();

         while(var1.hasNext()) {
            BossInfo var2 = (BossInfo)var1.next();
            if (var2.func_186734_i()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean func_184056_f() {
      if (!this.field_184060_g.isEmpty()) {
         Iterator var1 = this.field_184060_g.values().iterator();

         while(var1.hasNext()) {
            BossInfo var2 = (BossInfo)var1.next();
            if (var2.func_186748_k()) {
               return true;
            }
         }
      }

      return false;
   }
}
