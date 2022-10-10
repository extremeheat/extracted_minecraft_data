package net.minecraft.client.multiplayer;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.AdvancementToast;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketSeenAdvancements;
import net.minecraft.network.play.server.SPacketAdvancementInfo;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientAdvancementManager {
   private static final Logger field_192800_a = LogManager.getLogger();
   private final Minecraft field_192801_b;
   private final AdvancementList field_192802_c = new AdvancementList();
   private final Map<Advancement, AdvancementProgress> field_192803_d = Maps.newHashMap();
   @Nullable
   private ClientAdvancementManager.IListener field_192804_e;
   @Nullable
   private Advancement field_194231_f;

   public ClientAdvancementManager(Minecraft var1) {
      super();
      this.field_192801_b = var1;
   }

   public void func_192799_a(SPacketAdvancementInfo var1) {
      if (var1.func_192602_d()) {
         this.field_192802_c.func_192087_a();
         this.field_192803_d.clear();
      }

      this.field_192802_c.func_192085_a(var1.func_192600_b());
      this.field_192802_c.func_192083_a(var1.func_192603_a());
      Iterator var2 = var1.func_192604_c().entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         Advancement var4 = this.field_192802_c.func_192084_a((ResourceLocation)var3.getKey());
         if (var4 != null) {
            AdvancementProgress var5 = (AdvancementProgress)var3.getValue();
            var5.func_192099_a(var4.func_192073_f(), var4.func_192074_h());
            this.field_192803_d.put(var4, var5);
            if (this.field_192804_e != null) {
               this.field_192804_e.func_191933_a(var4, var5);
            }

            if (!var1.func_192602_d() && var5.func_192105_a() && var4.func_192068_c() != null && var4.func_192068_c().func_193223_h()) {
               this.field_192801_b.func_193033_an().func_192988_a(new AdvancementToast(var4));
            }
         } else {
            field_192800_a.warn("Server informed client about progress for unknown advancement {}", var3.getKey());
         }
      }

   }

   public AdvancementList func_194229_a() {
      return this.field_192802_c;
   }

   public void func_194230_a(@Nullable Advancement var1, boolean var2) {
      NetHandlerPlayClient var3 = this.field_192801_b.func_147114_u();
      if (var3 != null && var1 != null && var2) {
         var3.func_147297_a(CPacketSeenAdvancements.func_194163_a(var1));
      }

      if (this.field_194231_f != var1) {
         this.field_194231_f = var1;
         if (this.field_192804_e != null) {
            this.field_192804_e.func_193982_e(var1);
         }
      }

   }

   public void func_192798_a(@Nullable ClientAdvancementManager.IListener var1) {
      this.field_192804_e = var1;
      this.field_192802_c.func_192086_a(var1);
      if (var1 != null) {
         Iterator var2 = this.field_192803_d.entrySet().iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            var1.func_191933_a((Advancement)var3.getKey(), (AdvancementProgress)var3.getValue());
         }

         var1.func_193982_e(this.field_194231_f);
      }

   }

   public interface IListener extends AdvancementList.Listener {
      void func_191933_a(Advancement var1, AdvancementProgress var2);

      void func_193982_e(@Nullable Advancement var1);
   }
}
