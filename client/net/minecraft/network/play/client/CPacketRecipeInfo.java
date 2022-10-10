package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.ResourceLocation;

public class CPacketRecipeInfo implements Packet<INetHandlerPlayServer> {
   private CPacketRecipeInfo.Purpose field_194157_a;
   private ResourceLocation field_193649_d;
   private boolean field_192631_e;
   private boolean field_192632_f;
   private boolean field_202498_e;
   private boolean field_202499_f;

   public CPacketRecipeInfo() {
      super();
   }

   public CPacketRecipeInfo(IRecipe var1) {
      super();
      this.field_194157_a = CPacketRecipeInfo.Purpose.SHOWN;
      this.field_193649_d = var1.func_199560_c();
   }

   public CPacketRecipeInfo(boolean var1, boolean var2, boolean var3, boolean var4) {
      super();
      this.field_194157_a = CPacketRecipeInfo.Purpose.SETTINGS;
      this.field_192631_e = var1;
      this.field_192632_f = var2;
      this.field_202498_e = var3;
      this.field_202499_f = var4;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_194157_a = (CPacketRecipeInfo.Purpose)var1.func_179257_a(CPacketRecipeInfo.Purpose.class);
      if (this.field_194157_a == CPacketRecipeInfo.Purpose.SHOWN) {
         this.field_193649_d = var1.func_192575_l();
      } else if (this.field_194157_a == CPacketRecipeInfo.Purpose.SETTINGS) {
         this.field_192631_e = var1.readBoolean();
         this.field_192632_f = var1.readBoolean();
         this.field_202498_e = var1.readBoolean();
         this.field_202499_f = var1.readBoolean();
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179249_a(this.field_194157_a);
      if (this.field_194157_a == CPacketRecipeInfo.Purpose.SHOWN) {
         var1.func_192572_a(this.field_193649_d);
      } else if (this.field_194157_a == CPacketRecipeInfo.Purpose.SETTINGS) {
         var1.writeBoolean(this.field_192631_e);
         var1.writeBoolean(this.field_192632_f);
         var1.writeBoolean(this.field_202498_e);
         var1.writeBoolean(this.field_202499_f);
      }

   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_191984_a(this);
   }

   public CPacketRecipeInfo.Purpose func_194156_a() {
      return this.field_194157_a;
   }

   public ResourceLocation func_199619_b() {
      return this.field_193649_d;
   }

   public boolean func_192624_c() {
      return this.field_192631_e;
   }

   public boolean func_192625_d() {
      return this.field_192632_f;
   }

   public boolean func_202496_e() {
      return this.field_202498_e;
   }

   public boolean func_202497_f() {
      return this.field_202499_f;
   }

   public static enum Purpose {
      SHOWN,
      SETTINGS;

      private Purpose() {
      }
   }
}
