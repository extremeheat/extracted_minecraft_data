package net.minecraft.tileentity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.WorldServer;

public class TileEntitySign extends TileEntity implements ICommandSource {
   public final ITextComponent[] field_145915_a = new ITextComponent[]{new TextComponentString(""), new TextComponentString(""), new TextComponentString(""), new TextComponentString("")};
   public int field_145918_i = -1;
   private boolean field_145916_j = true;
   private EntityPlayer field_145917_k;
   private final String[] field_212367_h = new String[4];

   public TileEntitySign() {
      super(TileEntityType.field_200978_i);
   }

   public NBTTagCompound func_189515_b(NBTTagCompound var1) {
      super.func_189515_b(var1);

      for(int var2 = 0; var2 < 4; ++var2) {
         String var3 = ITextComponent.Serializer.func_150696_a(this.field_145915_a[var2]);
         var1.func_74778_a("Text" + (var2 + 1), var3);
      }

      return var1;
   }

   public void func_145839_a(NBTTagCompound var1) {
      this.field_145916_j = false;
      super.func_145839_a(var1);

      for(int var2 = 0; var2 < 4; ++var2) {
         String var3 = var1.func_74779_i("Text" + (var2 + 1));
         ITextComponent var4 = ITextComponent.Serializer.func_150699_a(var3);
         if (this.field_145850_b instanceof WorldServer) {
            try {
               this.field_145915_a[var2] = TextComponentUtils.func_197680_a(this.func_195539_a((EntityPlayerMP)null), var4, (Entity)null);
            } catch (CommandSyntaxException var6) {
               this.field_145915_a[var2] = var4;
            }
         } else {
            this.field_145915_a[var2] = var4;
         }

         this.field_212367_h[var2] = null;
      }

   }

   public ITextComponent func_212366_a(int var1) {
      return this.field_145915_a[var1];
   }

   public void func_212365_a(int var1, ITextComponent var2) {
      this.field_145915_a[var1] = var2;
      this.field_212367_h[var1] = null;
   }

   @Nullable
   public String func_212364_a(int var1, Function<ITextComponent, String> var2) {
      if (this.field_212367_h[var1] == null && this.field_145915_a[var1] != null) {
         this.field_212367_h[var1] = (String)var2.apply(this.field_145915_a[var1]);
      }

      return this.field_212367_h[var1];
   }

   @Nullable
   public SPacketUpdateTileEntity func_189518_D_() {
      return new SPacketUpdateTileEntity(this.field_174879_c, 9, this.func_189517_E_());
   }

   public NBTTagCompound func_189517_E_() {
      return this.func_189515_b(new NBTTagCompound());
   }

   public boolean func_183000_F() {
      return true;
   }

   public boolean func_145914_a() {
      return this.field_145916_j;
   }

   public void func_145913_a(boolean var1) {
      this.field_145916_j = var1;
      if (!var1) {
         this.field_145917_k = null;
      }

   }

   public void func_145912_a(EntityPlayer var1) {
      this.field_145917_k = var1;
   }

   public EntityPlayer func_145911_b() {
      return this.field_145917_k;
   }

   public boolean func_174882_b(EntityPlayer var1) {
      ITextComponent[] var2 = this.field_145915_a;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ITextComponent var5 = var2[var4];
         Style var6 = var5 == null ? null : var5.func_150256_b();
         if (var6 != null && var6.func_150235_h() != null) {
            ClickEvent var7 = var6.func_150235_h();
            if (var7.func_150669_a() == ClickEvent.Action.RUN_COMMAND) {
               var1.func_184102_h().func_195571_aL().func_197059_a(this.func_195539_a((EntityPlayerMP)var1), var7.func_150668_b());
            }
         }
      }

      return true;
   }

   public void func_145747_a(ITextComponent var1) {
   }

   public CommandSource func_195539_a(@Nullable EntityPlayerMP var1) {
      String var2 = var1 == null ? "Sign" : var1.func_200200_C_().getString();
      Object var3 = var1 == null ? new TextComponentString("Sign") : var1.func_145748_c_();
      return new CommandSource(this, new Vec3d((double)this.field_174879_c.func_177958_n() + 0.5D, (double)this.field_174879_c.func_177956_o() + 0.5D, (double)this.field_174879_c.func_177952_p() + 0.5D), Vec2f.field_189974_a, (WorldServer)this.field_145850_b, 2, var2, (ITextComponent)var3, this.field_145850_b.func_73046_m(), var1);
   }

   public boolean func_195039_a() {
      return false;
   }

   public boolean func_195040_b() {
      return false;
   }

   public boolean func_195041_r_() {
      return false;
   }
}
