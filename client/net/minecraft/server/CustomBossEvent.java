package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;

public class CustomBossEvent extends BossInfoServer {
   private final ResourceLocation field_201373_h;
   private final Set<UUID> field_201374_i = Sets.newHashSet();
   private int field_201375_j;
   private int field_201376_k = 100;

   public CustomBossEvent(ResourceLocation var1, ITextComponent var2) {
      super(var2, BossInfo.Color.WHITE, BossInfo.Overlay.PROGRESS);
      this.field_201373_h = var1;
      this.func_186735_a(0.0F);
   }

   public ResourceLocation func_201364_a() {
      return this.field_201373_h;
   }

   public void func_186760_a(EntityPlayerMP var1) {
      super.func_186760_a(var1);
      this.field_201374_i.add(var1.func_110124_au());
   }

   public void func_201372_a(UUID var1) {
      this.field_201374_i.add(var1);
   }

   public void func_186761_b(EntityPlayerMP var1) {
      super.func_186761_b(var1);
      this.field_201374_i.remove(var1.func_110124_au());
   }

   public void func_201360_b() {
      super.func_201360_b();
      this.field_201374_i.clear();
   }

   public int func_201365_c() {
      return this.field_201375_j;
   }

   public int func_201367_d() {
      return this.field_201376_k;
   }

   public void func_201362_a(int var1) {
      this.field_201375_j = var1;
      this.func_186735_a(MathHelper.func_76131_a((float)var1 / (float)this.field_201376_k, 0.0F, 1.0F));
   }

   public void func_201366_b(int var1) {
      this.field_201376_k = var1;
      this.func_186735_a(MathHelper.func_76131_a((float)this.field_201375_j / (float)var1, 0.0F, 1.0F));
   }

   public final ITextComponent func_201369_e() {
      return TextComponentUtils.func_197676_a(this.func_186744_e()).func_211710_a((var1) -> {
         var1.func_150238_a(this.func_186736_g().func_201482_a()).func_150209_a(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(this.func_201364_a().toString()))).func_179989_a(this.func_201364_a().toString());
      });
   }

   public boolean func_201368_a(Collection<EntityPlayerMP> var1) {
      HashSet var2 = Sets.newHashSet();
      HashSet var3 = Sets.newHashSet();
      Iterator var4 = this.field_201374_i.iterator();

      UUID var5;
      boolean var6;
      Iterator var7;
      while(var4.hasNext()) {
         var5 = (UUID)var4.next();
         var6 = false;
         var7 = var1.iterator();

         while(var7.hasNext()) {
            EntityPlayerMP var8 = (EntityPlayerMP)var7.next();
            if (var8.func_110124_au().equals(var5)) {
               var6 = true;
               break;
            }
         }

         if (!var6) {
            var2.add(var5);
         }
      }

      var4 = var1.iterator();

      EntityPlayerMP var9;
      while(var4.hasNext()) {
         var9 = (EntityPlayerMP)var4.next();
         var6 = false;
         var7 = this.field_201374_i.iterator();

         while(var7.hasNext()) {
            UUID var12 = (UUID)var7.next();
            if (var9.func_110124_au().equals(var12)) {
               var6 = true;
               break;
            }
         }

         if (!var6) {
            var3.add(var9);
         }
      }

      for(var4 = var2.iterator(); var4.hasNext(); this.field_201374_i.remove(var5)) {
         var5 = (UUID)var4.next();
         Iterator var11 = this.func_186757_c().iterator();

         while(var11.hasNext()) {
            EntityPlayerMP var10 = (EntityPlayerMP)var11.next();
            if (var10.func_110124_au().equals(var5)) {
               this.func_186761_b(var10);
               break;
            }
         }
      }

      var4 = var3.iterator();

      while(var4.hasNext()) {
         var9 = (EntityPlayerMP)var4.next();
         this.func_186760_a(var9);
      }

      return !var2.isEmpty() || !var3.isEmpty();
   }

   public NBTTagCompound func_201370_f() {
      NBTTagCompound var1 = new NBTTagCompound();
      var1.func_74778_a("Name", ITextComponent.Serializer.func_150696_a(this.field_186749_a));
      var1.func_74757_a("Visible", this.func_201359_g());
      var1.func_74768_a("Value", this.field_201375_j);
      var1.func_74768_a("Max", this.field_201376_k);
      var1.func_74778_a("Color", this.func_186736_g().func_201480_b());
      var1.func_74778_a("Overlay", this.func_186740_h().func_201486_a());
      var1.func_74757_a("DarkenScreen", this.func_186734_i());
      var1.func_74757_a("PlayBossMusic", this.func_186747_j());
      var1.func_74757_a("CreateWorldFog", this.func_186748_k());
      NBTTagList var2 = new NBTTagList();
      Iterator var3 = this.field_201374_i.iterator();

      while(var3.hasNext()) {
         UUID var4 = (UUID)var3.next();
         var2.add((INBTBase)NBTUtil.func_186862_a(var4));
      }

      var1.func_74782_a("Players", var2);
      return var1;
   }

   public static CustomBossEvent func_201371_a(NBTTagCompound var0, ResourceLocation var1) {
      CustomBossEvent var2 = new CustomBossEvent(var1, ITextComponent.Serializer.func_150699_a(var0.func_74779_i("Name")));
      var2.func_186758_d(var0.func_74767_n("Visible"));
      var2.func_201362_a(var0.func_74762_e("Value"));
      var2.func_201366_b(var0.func_74762_e("Max"));
      var2.func_186745_a(BossInfo.Color.func_201481_a(var0.func_74779_i("Color")));
      var2.func_186746_a(BossInfo.Overlay.func_201485_a(var0.func_74779_i("Overlay")));
      var2.func_186741_a(var0.func_74767_n("DarkenScreen"));
      var2.func_186742_b(var0.func_74767_n("PlayBossMusic"));
      var2.func_186743_c(var0.func_74767_n("CreateWorldFog"));
      NBTTagList var3 = var0.func_150295_c("Players", 10);

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         var2.func_201372_a(NBTUtil.func_186860_b(var3.func_150305_b(var4)));
      }

      return var2;
   }

   public void func_201361_c(EntityPlayerMP var1) {
      if (this.field_201374_i.contains(var1.func_110124_au())) {
         this.func_186760_a(var1);
      }

   }

   public void func_201363_d(EntityPlayerMP var1) {
      super.func_186761_b(var1);
   }
}
