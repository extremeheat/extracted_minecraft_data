package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;

public class SPacketTabComplete implements Packet<INetHandlerPlayClient> {
   private int field_197690_a;
   private Suggestions field_197691_b;

   public SPacketTabComplete() {
      super();
   }

   public SPacketTabComplete(int var1, Suggestions var2) {
      super();
      this.field_197690_a = var1;
      this.field_197691_b = var2;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_197690_a = var1.func_150792_a();
      int var2 = var1.func_150792_a();
      int var3 = var1.func_150792_a();
      StringRange var4 = StringRange.between(var2, var2 + var3);
      int var5 = var1.func_150792_a();
      ArrayList var6 = Lists.newArrayListWithCapacity(var5);

      for(int var7 = 0; var7 < var5; ++var7) {
         String var8 = var1.func_150789_c(32767);
         ITextComponent var9 = var1.readBoolean() ? var1.func_179258_d() : null;
         var6.add(new Suggestion(var4, var8, var9));
      }

      this.field_197691_b = new Suggestions(var4, var6);
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_197690_a);
      var1.func_150787_b(this.field_197691_b.getRange().getStart());
      var1.func_150787_b(this.field_197691_b.getRange().getLength());
      var1.func_150787_b(this.field_197691_b.getList().size());
      Iterator var2 = this.field_197691_b.getList().iterator();

      while(var2.hasNext()) {
         Suggestion var3 = (Suggestion)var2.next();
         var1.func_180714_a(var3.getText());
         var1.writeBoolean(var3.getTooltip() != null);
         if (var3.getTooltip() != null) {
            var1.func_179256_a(TextComponentUtils.func_202465_a(var3.getTooltip()));
         }
      }

   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_195510_a(this);
   }

   public int func_197689_a() {
      return this.field_197690_a;
   }

   public Suggestions func_197687_b() {
      return this.field_197691_b;
   }
}
