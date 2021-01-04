package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.math.Matrix4f;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentScreen extends AbstractContainerScreen<EnchantmentMenu> {
   private static final ResourceLocation ENCHANTING_TABLE_LOCATION = new ResourceLocation("textures/gui/container/enchanting_table.png");
   private static final ResourceLocation ENCHANTING_BOOK_LOCATION = new ResourceLocation("textures/entity/enchanting_table_book.png");
   private static final BookModel BOOK_MODEL = new BookModel();
   private final Random random = new Random();
   public int time;
   public float flip;
   public float oFlip;
   public float flipT;
   public float flipA;
   public float open;
   public float oOpen;
   private ItemStack last;

   public EnchantmentScreen(EnchantmentMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
      this.last = ItemStack.EMPTY;
   }

   protected void renderLabels(int var1, int var2) {
      this.font.draw(this.title.getColoredString(), 12.0F, 5.0F, 4210752);
      this.font.draw(this.inventory.getDisplayName().getColoredString(), 8.0F, (float)(this.imageHeight - 96 + 2), 4210752);
   }

   public void tick() {
      super.tick();
      this.tickBook();
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      int var6 = (this.width - this.imageWidth) / 2;
      int var7 = (this.height - this.imageHeight) / 2;

      for(int var8 = 0; var8 < 3; ++var8) {
         double var9 = var1 - (double)(var6 + 60);
         double var11 = var3 - (double)(var7 + 14 + 19 * var8);
         if (var9 >= 0.0D && var11 >= 0.0D && var9 < 108.0D && var11 < 19.0D && ((EnchantmentMenu)this.menu).clickMenuButton(this.minecraft.player, var8)) {
            this.minecraft.gameMode.handleInventoryButtonClick(((EnchantmentMenu)this.menu).containerId, var8);
            return true;
         }
      }

      return super.mouseClicked(var1, var3, var5);
   }

   protected void renderBg(float var1, int var2, int var3) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(ENCHANTING_TABLE_LOCATION);
      int var4 = (this.width - this.imageWidth) / 2;
      int var5 = (this.height - this.imageHeight) / 2;
      this.blit(var4, var5, 0, 0, this.imageWidth, this.imageHeight);
      GlStateManager.pushMatrix();
      GlStateManager.matrixMode(5889);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      int var6 = (int)this.minecraft.window.getGuiScale();
      GlStateManager.viewport((this.width - 320) / 2 * var6, (this.height - 240) / 2 * var6, 320 * var6, 240 * var6);
      GlStateManager.translatef(-0.34F, 0.23F, 0.0F);
      GlStateManager.multMatrix(Matrix4f.perspective(90.0D, 1.3333334F, 9.0F, 80.0F));
      float var7 = 1.0F;
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      Lighting.turnOn();
      GlStateManager.translatef(0.0F, 3.3F, -16.0F);
      GlStateManager.scalef(1.0F, 1.0F, 1.0F);
      float var8 = 5.0F;
      GlStateManager.scalef(5.0F, 5.0F, 5.0F);
      GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
      this.minecraft.getTextureManager().bind(ENCHANTING_BOOK_LOCATION);
      GlStateManager.rotatef(20.0F, 1.0F, 0.0F, 0.0F);
      float var9 = Mth.lerp(var1, this.oOpen, this.open);
      GlStateManager.translatef((1.0F - var9) * 0.2F, (1.0F - var9) * 0.1F, (1.0F - var9) * 0.25F);
      GlStateManager.rotatef(-(1.0F - var9) * 90.0F - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
      float var10 = Mth.lerp(var1, this.oFlip, this.flip) + 0.25F;
      float var11 = Mth.lerp(var1, this.oFlip, this.flip) + 0.75F;
      var10 = (var10 - (float)Mth.fastFloor((double)var10)) * 1.6F - 0.3F;
      var11 = (var11 - (float)Mth.fastFloor((double)var11)) * 1.6F - 0.3F;
      if (var10 < 0.0F) {
         var10 = 0.0F;
      }

      if (var11 < 0.0F) {
         var11 = 0.0F;
      }

      if (var10 > 1.0F) {
         var10 = 1.0F;
      }

      if (var11 > 1.0F) {
         var11 = 1.0F;
      }

      GlStateManager.enableRescaleNormal();
      BOOK_MODEL.render(0.0F, var10, var11, var9, 0.0F, 0.0625F);
      GlStateManager.disableRescaleNormal();
      Lighting.turnOff();
      GlStateManager.matrixMode(5889);
      GlStateManager.viewport(0, 0, this.minecraft.window.getWidth(), this.minecraft.window.getHeight());
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      Lighting.turnOff();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      EnchantmentNames.getInstance().initSeed((long)((EnchantmentMenu)this.menu).getEnchantmentSeed());
      int var12 = ((EnchantmentMenu)this.menu).getGoldCount();

      for(int var13 = 0; var13 < 3; ++var13) {
         int var14 = var4 + 60;
         int var15 = var14 + 20;
         this.blitOffset = 0;
         this.minecraft.getTextureManager().bind(ENCHANTING_TABLE_LOCATION);
         int var16 = ((EnchantmentMenu)this.menu).costs[var13];
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         if (var16 == 0) {
            this.blit(var14, var5 + 14 + 19 * var13, 0, 185, 108, 19);
         } else {
            String var17 = "" + var16;
            int var18 = 86 - this.font.width(var17);
            String var19 = EnchantmentNames.getInstance().getRandomName(this.font, var18);
            Font var20 = this.minecraft.getFontManager().get(Minecraft.ALT_FONT);
            int var21 = 6839882;
            if ((var12 < var13 + 1 || this.minecraft.player.experienceLevel < var16) && !this.minecraft.player.abilities.instabuild) {
               this.blit(var14, var5 + 14 + 19 * var13, 0, 185, 108, 19);
               this.blit(var14 + 1, var5 + 15 + 19 * var13, 16 * var13, 239, 16, 16);
               var20.drawWordWrap(var19, var15, var5 + 16 + 19 * var13, var18, (var21 & 16711422) >> 1);
               var21 = 4226832;
            } else {
               int var22 = var2 - (var4 + 60);
               int var23 = var3 - (var5 + 14 + 19 * var13);
               if (var22 >= 0 && var23 >= 0 && var22 < 108 && var23 < 19) {
                  this.blit(var14, var5 + 14 + 19 * var13, 0, 204, 108, 19);
                  var21 = 16777088;
               } else {
                  this.blit(var14, var5 + 14 + 19 * var13, 0, 166, 108, 19);
               }

               this.blit(var14 + 1, var5 + 15 + 19 * var13, 16 * var13, 223, 16, 16);
               var20.drawWordWrap(var19, var15, var5 + 16 + 19 * var13, var18, var21);
               var21 = 8453920;
            }

            var20 = this.minecraft.font;
            var20.drawShadow(var17, (float)(var15 + 86 - var20.width(var17)), (float)(var5 + 16 + 19 * var13 + 7), var21);
         }
      }

   }

   public void render(int var1, int var2, float var3) {
      var3 = this.minecraft.getFrameTime();
      this.renderBackground();
      super.render(var1, var2, var3);
      this.renderTooltip(var1, var2);
      boolean var4 = this.minecraft.player.abilities.instabuild;
      int var5 = ((EnchantmentMenu)this.menu).getGoldCount();

      for(int var6 = 0; var6 < 3; ++var6) {
         int var7 = ((EnchantmentMenu)this.menu).costs[var6];
         Enchantment var8 = Enchantment.byId(((EnchantmentMenu)this.menu).enchantClue[var6]);
         int var9 = ((EnchantmentMenu)this.menu).levelClue[var6];
         int var10 = var6 + 1;
         if (this.isHovering(60, 14 + 19 * var6, 108, 17, (double)var1, (double)var2) && var7 > 0 && var9 >= 0 && var8 != null) {
            ArrayList var11 = Lists.newArrayList();
            var11.add("" + ChatFormatting.WHITE + ChatFormatting.ITALIC + I18n.get("container.enchant.clue", var8.getFullname(var9).getColoredString()));
            if (!var4) {
               var11.add("");
               if (this.minecraft.player.experienceLevel < var7) {
                  var11.add(ChatFormatting.RED + I18n.get("container.enchant.level.requirement", ((EnchantmentMenu)this.menu).costs[var6]));
               } else {
                  String var12;
                  if (var10 == 1) {
                     var12 = I18n.get("container.enchant.lapis.one");
                  } else {
                     var12 = I18n.get("container.enchant.lapis.many", var10);
                  }

                  ChatFormatting var13 = var5 >= var10 ? ChatFormatting.GRAY : ChatFormatting.RED;
                  var11.add(var13 + "" + var12);
                  if (var10 == 1) {
                     var12 = I18n.get("container.enchant.level.one");
                  } else {
                     var12 = I18n.get("container.enchant.level.many", var10);
                  }

                  var11.add(ChatFormatting.GRAY + "" + var12);
               }
            }

            this.renderTooltip(var11, var1, var2);
            break;
         }
      }

   }

   public void tickBook() {
      ItemStack var1 = ((EnchantmentMenu)this.menu).getSlot(0).getItem();
      if (!ItemStack.matches(var1, this.last)) {
         this.last = var1;

         do {
            this.flipT += (float)(this.random.nextInt(4) - this.random.nextInt(4));
         } while(this.flip <= this.flipT + 1.0F && this.flip >= this.flipT - 1.0F);
      }

      ++this.time;
      this.oFlip = this.flip;
      this.oOpen = this.open;
      boolean var2 = false;

      for(int var3 = 0; var3 < 3; ++var3) {
         if (((EnchantmentMenu)this.menu).costs[var3] != 0) {
            var2 = true;
         }
      }

      if (var2) {
         this.open += 0.2F;
      } else {
         this.open -= 0.2F;
      }

      this.open = Mth.clamp(this.open, 0.0F, 1.0F);
      float var5 = (this.flipT - this.flip) * 0.4F;
      float var4 = 0.2F;
      var5 = Mth.clamp(var5, -0.2F, 0.2F);
      this.flipA += (var5 - this.flipA) * 0.9F;
      this.flip += this.flipA;
   }
}
