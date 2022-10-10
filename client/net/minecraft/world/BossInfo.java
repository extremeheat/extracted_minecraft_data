package net.minecraft.world;

import java.util.UUID;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public abstract class BossInfo {
   private final UUID field_186756_h;
   protected ITextComponent field_186749_a;
   protected float field_186750_b;
   protected BossInfo.Color field_186751_c;
   protected BossInfo.Overlay field_186752_d;
   protected boolean field_186753_e;
   protected boolean field_186754_f;
   protected boolean field_186755_g;

   public BossInfo(UUID var1, ITextComponent var2, BossInfo.Color var3, BossInfo.Overlay var4) {
      super();
      this.field_186756_h = var1;
      this.field_186749_a = var2;
      this.field_186751_c = var3;
      this.field_186752_d = var4;
      this.field_186750_b = 1.0F;
   }

   public UUID func_186737_d() {
      return this.field_186756_h;
   }

   public ITextComponent func_186744_e() {
      return this.field_186749_a;
   }

   public void func_186739_a(ITextComponent var1) {
      this.field_186749_a = var1;
   }

   public float func_186738_f() {
      return this.field_186750_b;
   }

   public void func_186735_a(float var1) {
      this.field_186750_b = var1;
   }

   public BossInfo.Color func_186736_g() {
      return this.field_186751_c;
   }

   public void func_186745_a(BossInfo.Color var1) {
      this.field_186751_c = var1;
   }

   public BossInfo.Overlay func_186740_h() {
      return this.field_186752_d;
   }

   public void func_186746_a(BossInfo.Overlay var1) {
      this.field_186752_d = var1;
   }

   public boolean func_186734_i() {
      return this.field_186753_e;
   }

   public BossInfo func_186741_a(boolean var1) {
      this.field_186753_e = var1;
      return this;
   }

   public boolean func_186747_j() {
      return this.field_186754_f;
   }

   public BossInfo func_186742_b(boolean var1) {
      this.field_186754_f = var1;
      return this;
   }

   public BossInfo func_186743_c(boolean var1) {
      this.field_186755_g = var1;
      return this;
   }

   public boolean func_186748_k() {
      return this.field_186755_g;
   }

   public static enum Overlay {
      PROGRESS("progress"),
      NOTCHED_6("notched_6"),
      NOTCHED_10("notched_10"),
      NOTCHED_12("notched_12"),
      NOTCHED_20("notched_20");

      private final String field_201487_f;

      private Overlay(String var3) {
         this.field_201487_f = var3;
      }

      public String func_201486_a() {
         return this.field_201487_f;
      }

      public static BossInfo.Overlay func_201485_a(String var0) {
         BossInfo.Overlay[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            BossInfo.Overlay var4 = var1[var3];
            if (var4.field_201487_f.equals(var0)) {
               return var4;
            }
         }

         return PROGRESS;
      }
   }

   public static enum Color {
      PINK("pink", TextFormatting.RED),
      BLUE("blue", TextFormatting.BLUE),
      RED("red", TextFormatting.DARK_RED),
      GREEN("green", TextFormatting.GREEN),
      YELLOW("yellow", TextFormatting.YELLOW),
      PURPLE("purple", TextFormatting.DARK_BLUE),
      WHITE("white", TextFormatting.WHITE);

      private final String field_201483_h;
      private final TextFormatting field_201484_i;

      private Color(String var3, TextFormatting var4) {
         this.field_201483_h = var3;
         this.field_201484_i = var4;
      }

      public TextFormatting func_201482_a() {
         return this.field_201484_i;
      }

      public String func_201480_b() {
         return this.field_201483_h;
      }

      public static BossInfo.Color func_201481_a(String var0) {
         BossInfo.Color[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            BossInfo.Color var4 = var1[var3];
            if (var4.field_201483_h.equals(var0)) {
               return var4;
            }
         }

         return WHITE;
      }
   }
}
