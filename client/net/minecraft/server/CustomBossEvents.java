package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class CustomBossEvents {
   private final MinecraftServer field_201386_a;
   private final Map<ResourceLocation, CustomBossEvent> field_201387_b = Maps.newHashMap();

   public CustomBossEvents(MinecraftServer var1) {
      super();
      this.field_201386_a = var1;
   }

   @Nullable
   public CustomBossEvent func_201384_a(ResourceLocation var1) {
      return (CustomBossEvent)this.field_201387_b.get(var1);
   }

   public CustomBossEvent func_201379_a(ResourceLocation var1, ITextComponent var2) {
      CustomBossEvent var3 = new CustomBossEvent(var1, var2);
      this.field_201387_b.put(var1, var3);
      return var3;
   }

   public void func_201385_a(CustomBossEvent var1) {
      this.field_201387_b.remove(var1.func_201364_a());
   }

   public Collection<ResourceLocation> func_201377_a() {
      return this.field_201387_b.keySet();
   }

   public Collection<CustomBossEvent> func_201378_b() {
      return this.field_201387_b.values();
   }

   public NBTTagCompound func_201380_c() {
      NBTTagCompound var1 = new NBTTagCompound();
      Iterator var2 = this.field_201387_b.values().iterator();

      while(var2.hasNext()) {
         CustomBossEvent var3 = (CustomBossEvent)var2.next();
         var1.func_74782_a(var3.func_201364_a().toString(), var3.func_201370_f());
      }

      return var1;
   }

   public void func_201381_a(NBTTagCompound var1) {
      Iterator var2 = var1.func_150296_c().iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         ResourceLocation var4 = new ResourceLocation(var3);
         this.field_201387_b.put(var4, CustomBossEvent.func_201371_a(var1.func_74775_l(var3), var4));
      }

   }

   public void func_201383_a(EntityPlayerMP var1) {
      Iterator var2 = this.field_201387_b.values().iterator();

      while(var2.hasNext()) {
         CustomBossEvent var3 = (CustomBossEvent)var2.next();
         var3.func_201361_c(var1);
      }

   }

   public void func_201382_b(EntityPlayerMP var1) {
      Iterator var2 = this.field_201387_b.values().iterator();

      while(var2.hasNext()) {
         CustomBossEvent var3 = (CustomBossEvent)var2.next();
         var3.func_201363_d(var1);
      }

   }
}
