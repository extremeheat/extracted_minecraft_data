package net.minecraft.client.tutorial;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentKeybind;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;

public class Tutorial {
   private final Minecraft field_193304_a;
   @Nullable
   private ITutorialStep field_193305_b;

   public Tutorial(Minecraft var1) {
      super();
      this.field_193304_a = var1;
   }

   public void func_193293_a(MovementInput var1) {
      if (this.field_193305_b != null) {
         this.field_193305_b.func_193247_a(var1);
      }

   }

   public void func_195872_a(double var1, double var3) {
      if (this.field_193305_b != null) {
         this.field_193305_b.func_195870_a(var1, var3);
      }

   }

   public void func_193297_a(@Nullable WorldClient var1, @Nullable RayTraceResult var2) {
      if (this.field_193305_b != null && var2 != null && var1 != null) {
         this.field_193305_b.func_193246_a(var1, var2);
      }

   }

   public void func_193294_a(WorldClient var1, BlockPos var2, IBlockState var3, float var4) {
      if (this.field_193305_b != null) {
         this.field_193305_b.func_193250_a(var1, var2, var3, var4);
      }

   }

   public void func_193296_a() {
      if (this.field_193305_b != null) {
         this.field_193305_b.func_193251_c();
      }

   }

   public void func_193301_a(ItemStack var1) {
      if (this.field_193305_b != null) {
         this.field_193305_b.func_193252_a(var1);
      }

   }

   public void func_193300_b() {
      if (this.field_193305_b != null) {
         this.field_193305_b.func_193248_b();
         this.field_193305_b = null;
      }
   }

   public void func_193302_c() {
      if (this.field_193305_b != null) {
         this.func_193300_b();
      }

      this.field_193305_b = this.field_193304_a.field_71474_y.field_193631_S.func_193309_a(this);
   }

   public void func_193303_d() {
      if (this.field_193305_b != null) {
         if (this.field_193304_a.field_71441_e != null) {
            this.field_193305_b.func_193245_a();
         } else {
            this.func_193300_b();
         }
      } else if (this.field_193304_a.field_71441_e != null) {
         this.func_193302_c();
      }

   }

   public void func_193292_a(TutorialSteps var1) {
      this.field_193304_a.field_71474_y.field_193631_S = var1;
      this.field_193304_a.field_71474_y.func_74303_b();
      if (this.field_193305_b != null) {
         this.field_193305_b.func_193248_b();
         this.field_193305_b = var1.func_193309_a(this);
      }

   }

   public Minecraft func_193295_e() {
      return this.field_193304_a;
   }

   public GameType func_194072_f() {
      return this.field_193304_a.field_71442_b == null ? GameType.NOT_SET : this.field_193304_a.field_71442_b.func_178889_l();
   }

   public static ITextComponent func_193291_a(String var0) {
      return (new TextComponentKeybind("key." + var0)).func_211708_a(TextFormatting.BOLD);
   }
}
