package net.minecraft.stats;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.StatCollector;

public class Achievement extends StatBase {
   public final int field_75993_a;
   public final int field_75991_b;
   public final Achievement field_75992_c;
   private final String field_75996_k;
   private IStatStringFormat field_75994_l;
   public final ItemStack field_75990_d;
   private boolean field_75995_m;

   public Achievement(String var1, String var2, int var3, int var4, Item var5, Achievement var6) {
      this(var1, var2, var3, var4, new ItemStack(var5), var6);
   }

   public Achievement(String var1, String var2, int var3, int var4, Block var5, Achievement var6) {
      this(var1, var2, var3, var4, new ItemStack(var5), var6);
   }

   public Achievement(String var1, String var2, int var3, int var4, ItemStack var5, Achievement var6) {
      super(var1, new ChatComponentTranslation("achievement." + var2, new Object[0]));
      this.field_75990_d = var5;
      this.field_75996_k = "achievement." + var2 + ".desc";
      this.field_75993_a = var3;
      this.field_75991_b = var4;
      if (var3 < AchievementList.field_76010_a) {
         AchievementList.field_76010_a = var3;
      }

      if (var4 < AchievementList.field_76008_b) {
         AchievementList.field_76008_b = var4;
      }

      if (var3 > AchievementList.field_76009_c) {
         AchievementList.field_76009_c = var3;
      }

      if (var4 > AchievementList.field_76006_d) {
         AchievementList.field_76006_d = var4;
      }

      this.field_75992_c = var6;
   }

   public Achievement func_75966_h() {
      this.field_75972_f = true;
      return this;
   }

   public Achievement func_75987_b() {
      this.field_75995_m = true;
      return this;
   }

   public Achievement func_75971_g() {
      super.func_75971_g();
      AchievementList.field_76007_e.add(this);
      return this;
   }

   public boolean func_75967_d() {
      return true;
   }

   public IChatComponent func_150951_e() {
      IChatComponent var1 = super.func_150951_e();
      var1.func_150256_b().func_150238_a(this.func_75984_f() ? EnumChatFormatting.DARK_PURPLE : EnumChatFormatting.GREEN);
      return var1;
   }

   public Achievement func_150953_b(Class<? extends IJsonSerializable> var1) {
      return (Achievement)super.func_150953_b(var1);
   }

   public String func_75989_e() {
      return this.field_75994_l != null ? this.field_75994_l.func_74535_a(StatCollector.func_74838_a(this.field_75996_k)) : StatCollector.func_74838_a(this.field_75996_k);
   }

   public Achievement func_75988_a(IStatStringFormat var1) {
      this.field_75994_l = var1;
      return this;
   }

   public boolean func_75984_f() {
      return this.field_75995_m;
   }

   // $FF: synthetic method
   public StatBase func_150953_b(Class var1) {
      return this.func_150953_b(var1);
   }

   // $FF: synthetic method
   public StatBase func_75971_g() {
      return this.func_75971_g();
   }

   // $FF: synthetic method
   public StatBase func_75966_h() {
      return this.func_75966_h();
   }
}
