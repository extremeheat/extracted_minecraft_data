package net.minecraft.client.model.geom;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.List;
import net.minecraft.client.model.Model;

public class ModelPart {
   public float xTexSize;
   public float yTexSize;
   private int xTexOffs;
   private int yTexOffs;
   public float x;
   public float y;
   public float z;
   public float xRot;
   public float yRot;
   public float zRot;
   private boolean compiled;
   private int list;
   public boolean mirror;
   public boolean visible;
   public boolean neverRender;
   public final List<Cube> cubes;
   public List<ModelPart> children;
   public final String id;
   public float translateX;
   public float translateY;
   public float translateZ;

   public ModelPart(Model var1, String var2) {
      super();
      this.xTexSize = 64.0F;
      this.yTexSize = 32.0F;
      this.visible = true;
      this.cubes = Lists.newArrayList();
      var1.cubes.add(this);
      this.id = var2;
      this.setTexSize(var1.texWidth, var1.texHeight);
   }

   public ModelPart(Model var1) {
      this(var1, (String)null);
   }

   public ModelPart(Model var1, int var2, int var3) {
      this(var1);
      this.texOffs(var2, var3);
   }

   public void copyFrom(ModelPart var1) {
      this.xRot = var1.xRot;
      this.yRot = var1.yRot;
      this.zRot = var1.zRot;
      this.x = var1.x;
      this.y = var1.y;
      this.z = var1.z;
   }

   public void addChild(ModelPart var1) {
      if (this.children == null) {
         this.children = Lists.newArrayList();
      }

      this.children.add(var1);
   }

   public void removeChild(ModelPart var1) {
      if (this.children != null) {
         this.children.remove(var1);
      }

   }

   public ModelPart texOffs(int var1, int var2) {
      this.xTexOffs = var1;
      this.yTexOffs = var2;
      return this;
   }

   public ModelPart addBox(String var1, float var2, float var3, float var4, int var5, int var6, int var7, float var8, int var9, int var10) {
      var1 = this.id + "." + var1;
      this.texOffs(var9, var10);
      this.cubes.add((new Cube(this, this.xTexOffs, this.yTexOffs, var2, var3, var4, var5, var6, var7, var8)).setId(var1));
      return this;
   }

   public ModelPart addBox(float var1, float var2, float var3, int var4, int var5, int var6) {
      this.cubes.add(new Cube(this, this.xTexOffs, this.yTexOffs, var1, var2, var3, var4, var5, var6, 0.0F));
      return this;
   }

   public ModelPart addBox(float var1, float var2, float var3, int var4, int var5, int var6, boolean var7) {
      this.cubes.add(new Cube(this, this.xTexOffs, this.yTexOffs, var1, var2, var3, var4, var5, var6, 0.0F, var7));
      return this;
   }

   public void addBox(float var1, float var2, float var3, int var4, int var5, int var6, float var7) {
      this.cubes.add(new Cube(this, this.xTexOffs, this.yTexOffs, var1, var2, var3, var4, var5, var6, var7));
   }

   public void addBox(float var1, float var2, float var3, int var4, int var5, int var6, float var7, boolean var8) {
      this.cubes.add(new Cube(this, this.xTexOffs, this.yTexOffs, var1, var2, var3, var4, var5, var6, var7, var8));
   }

   public void setPos(float var1, float var2, float var3) {
      this.x = var1;
      this.y = var2;
      this.z = var3;
   }

   public void render(float var1) {
      if (!this.neverRender) {
         if (this.visible) {
            if (!this.compiled) {
               this.compile(var1);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translatef(this.translateX, this.translateY, this.translateZ);
            int var2;
            if (this.xRot == 0.0F && this.yRot == 0.0F && this.zRot == 0.0F) {
               if (this.x == 0.0F && this.y == 0.0F && this.z == 0.0F) {
                  GlStateManager.callList(this.list);
                  if (this.children != null) {
                     for(var2 = 0; var2 < this.children.size(); ++var2) {
                        ((ModelPart)this.children.get(var2)).render(var1);
                     }
                  }
               } else {
                  GlStateManager.pushMatrix();
                  GlStateManager.translatef(this.x * var1, this.y * var1, this.z * var1);
                  GlStateManager.callList(this.list);
                  if (this.children != null) {
                     for(var2 = 0; var2 < this.children.size(); ++var2) {
                        ((ModelPart)this.children.get(var2)).render(var1);
                     }
                  }

                  GlStateManager.popMatrix();
               }
            } else {
               GlStateManager.pushMatrix();
               GlStateManager.translatef(this.x * var1, this.y * var1, this.z * var1);
               if (this.zRot != 0.0F) {
                  GlStateManager.rotatef(this.zRot * 57.295776F, 0.0F, 0.0F, 1.0F);
               }

               if (this.yRot != 0.0F) {
                  GlStateManager.rotatef(this.yRot * 57.295776F, 0.0F, 1.0F, 0.0F);
               }

               if (this.xRot != 0.0F) {
                  GlStateManager.rotatef(this.xRot * 57.295776F, 1.0F, 0.0F, 0.0F);
               }

               GlStateManager.callList(this.list);
               if (this.children != null) {
                  for(var2 = 0; var2 < this.children.size(); ++var2) {
                     ((ModelPart)this.children.get(var2)).render(var1);
                  }
               }

               GlStateManager.popMatrix();
            }

            GlStateManager.popMatrix();
         }
      }
   }

   public void renderRollable(float var1) {
      if (!this.neverRender) {
         if (this.visible) {
            if (!this.compiled) {
               this.compile(var1);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translatef(this.x * var1, this.y * var1, this.z * var1);
            if (this.yRot != 0.0F) {
               GlStateManager.rotatef(this.yRot * 57.295776F, 0.0F, 1.0F, 0.0F);
            }

            if (this.xRot != 0.0F) {
               GlStateManager.rotatef(this.xRot * 57.295776F, 1.0F, 0.0F, 0.0F);
            }

            if (this.zRot != 0.0F) {
               GlStateManager.rotatef(this.zRot * 57.295776F, 0.0F, 0.0F, 1.0F);
            }

            GlStateManager.callList(this.list);
            GlStateManager.popMatrix();
         }
      }
   }

   public void translateTo(float var1) {
      if (!this.neverRender) {
         if (this.visible) {
            if (!this.compiled) {
               this.compile(var1);
            }

            if (this.xRot == 0.0F && this.yRot == 0.0F && this.zRot == 0.0F) {
               if (this.x != 0.0F || this.y != 0.0F || this.z != 0.0F) {
                  GlStateManager.translatef(this.x * var1, this.y * var1, this.z * var1);
               }
            } else {
               GlStateManager.translatef(this.x * var1, this.y * var1, this.z * var1);
               if (this.zRot != 0.0F) {
                  GlStateManager.rotatef(this.zRot * 57.295776F, 0.0F, 0.0F, 1.0F);
               }

               if (this.yRot != 0.0F) {
                  GlStateManager.rotatef(this.yRot * 57.295776F, 0.0F, 1.0F, 0.0F);
               }

               if (this.xRot != 0.0F) {
                  GlStateManager.rotatef(this.xRot * 57.295776F, 1.0F, 0.0F, 0.0F);
               }
            }

         }
      }
   }

   private void compile(float var1) {
      this.list = MemoryTracker.genLists(1);
      GlStateManager.newList(this.list, 4864);
      BufferBuilder var2 = Tesselator.getInstance().getBuilder();

      for(int var3 = 0; var3 < this.cubes.size(); ++var3) {
         ((Cube)this.cubes.get(var3)).compile(var2, var1);
      }

      GlStateManager.endList();
      this.compiled = true;
   }

   public ModelPart setTexSize(int var1, int var2) {
      this.xTexSize = (float)var1;
      this.yTexSize = (float)var2;
      return this;
   }
}
