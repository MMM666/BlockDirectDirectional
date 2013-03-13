package net.minecraft.src;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

public class mod_BDD_BlockDirectDirectional extends BaseMod {

	@MLProp(info="Enable Put away to the Planks with.")
	public static boolean isWoodDirection = true;
	@MLProp(info="Enable Put away to the Cauldron with.")
	public static boolean isCauldronDirection = true;
	@MLProp(info="Enable Water is boiled by the Cauldron.")
	public static boolean isBoiled = true;

	@MLProp(info="Directional BlockID(0 : Disable)", min=0, max=4095)
	public static int BlockID = 0;
	@MLProp(info="IronTrapdoor BlockID(0 : Disable)", min=0, max=4095)
	public static int IronTrapdoorID = 0;
	@MLProp(info="Sprite0 file name. (Null is Occupies.)")
	public static String spriteName0 = "/icon/tatami1.png";
	@MLProp(info="Sprite1 file name. (Null is Occupies.)")
	public static String spriteName1 = "/icon/tatami2.png";
	@MLProp(info="Sprite2 file name. (Null is Occupies.)")
	public static String spriteName2 = "/icon/tatami3.png";
	@MLProp(info="Sprite3 file name. (Null is Occupies.)")
	public static String spriteName3 = "/icon/tatami4.png";


	public static int renderID = 0;
	public static Block woodDirectional;
	public static Block cauldronDirectional;
	public static Block directDirectional;
	public static Block ironTrapDoor; 
	public static int sprite[] = new int[4];
	public static int BLD_Inv = 0x08;
	public static int BLD_Vrt = 0x08;
	public static int BLD_Hor = 0x04;
	public static boolean isForge = ModLoader.isModLoaded("Forge");


	
	public static void debug(String str) {
		System.out.print("BlockLogDirection-");
		System.out.println(str);
	}

	@Override
	public String getVersion() {
		return "1.4.7-2";
	}

	@Override
	public String getName() {
		return "BlockDirectional";
	}

	@Override
	public void load() {
		debug("Initialize.");
		// BlockのRenderIDを登録、特殊レンダリングを行う
		try {
			renderID = ModLoader.getUniqueBlockModelID(this, true);
		} catch (Exception e) {
		} catch (Error e) {
		}
		// 向き付きの木材を上書き、この時点で置き換わってる
		if (isWoodDirection) {
			Block.blocksList[5] = null;
			woodDirectional = (new BDD_BlockWood(5)).setHardness(2.0F).setResistance(5F).setStepSound(Block.soundWoodFootstep).setBlockName("wood").setRequiresSelfNotify();
		}
		if (isCauldronDirection) {
			Block.blocksList[118] = null;
			cauldronDirectional = (new BDD_BlockCauldron(118)).setHardness(2.0F).setBlockName("cauldron").setRequiresSelfNotify();
		}
		if (IronTrapdoorID > 0) {
			ironTrapDoor = (new BlockTrapDoor(IronTrapdoorID, Material.iron)).setHardness(3F).setStepSound(Block.soundMetalFootstep).setBlockName("trapdooriron").disableStats().setRequiresSelfNotify();
			ModLoader.registerBlock(ironTrapDoor);
			ModLoader.addRecipe(new ItemStack(ironTrapDoor, 2), new Object[] {
				" II", 
				"I I", 
				"II ", 
				Character.valueOf('I'), Item.ingotIron
			});
			ModLoader.addName(ironTrapDoor, "Iron Trapdoor");
			ModLoader.addName(ironTrapDoor, "ja_JP", "グレーチング");
		}
		
		try {
			// アイテムに有効破壊対象の追加
			for (int i = 0; i < Item.itemsList.length; i++) {
				if (Item.itemsList[i] instanceof ItemTool) {
					// 道具である
					// シリアル値で0
//					Block[] b = (Block[])ModLoader.getPrivateValue(ItemTool.class, Item.itemsList[i], "blocksEffectiveAgainst");
					Block[] b = (Block[])ModLoader.getPrivateValue(ItemTool.class, (ItemTool)Item.itemsList[i], 0);
					List<Block> l = Arrays.asList(b);
					boolean flag = false;
					ArrayList<Block> arraylist = new ArrayList<Block>();
					arraylist.addAll(l);
					if (woodDirectional != null && l.indexOf(Block.planks) > -1) {
						// 木材が定義されている
						arraylist.add(woodDirectional);
						flag = true;
					}
					if (cauldronDirectional != null && l.indexOf(Block.cauldron) > -1) {
						// 大釜が定義されている
						arraylist.add(cauldronDirectional);
						flag = true;
					}
					
					if (flag) {
//						ModLoader.setPrivateValue(ItemTool.class, Item.itemsList[i], "blocksEffectiveAgainst", ((Object) (arraylist.toArray(new Block[0]))));
						ModLoader.setPrivateValue(ItemTool.class, (ItemTool)Item.itemsList[i], 0, ((Object) (arraylist.toArray(new Block[0]))));
						debug("ItemTool: ".concat(Item.itemsList[i].getItemName()));
					}
				}
			}
			debug("ItemTool: Rewrite Over.");
		}
		catch(Exception exception) {
			debug("ItemTool: fail");
		}
		
		// Finalで定義されているので通常手段での書き換えは不可
		if (woodDirectional != null) {
			if (replaceBlock(Block.planks, woodDirectional)) {
				debug("Block.wood: directional.");
			} else {
				debug("Block.wood: fail");
			}
		}
		if (cauldronDirectional != null) {
			if (replaceBlock(Block.cauldron, cauldronDirectional)) {
				debug("Block.cauldron: directional.");
			} else {
				debug("Blockcauldron.: fail");
			}
		}
		
		// 向き付きブロック関係
		if (BlockID > 0) {
			boolean flag = false;
			if ((spriteName0 = spriteName0.trim()).length() > 0) {
				sprite[0] = ModLoader.addOverride("/terrain.png", spriteName0);
				flag = true;
			}
			if ((spriteName1 = spriteName1.trim()).length() > 0) {
				sprite[1] = ModLoader.addOverride("/terrain.png", spriteName1);
				flag = true;
			}
			if ((spriteName2 = spriteName2.trim()).length() > 0) {
				sprite[2] = ModLoader.addOverride("/terrain.png", spriteName2);
				flag = true;
			}
			if ((spriteName3 = spriteName3.trim()).length() > 0) {
				sprite[3] = ModLoader.addOverride("/terrain.png", spriteName3);
				flag = true;
			}
			if (flag) {
				directDirectional = (new BDD_BlockDirectDirectional(BlockID, Material.wood)).setHardness(2.0F).setResistance(5F).setStepSound(Block.soundWoodFootstep).setBlockName("direction").setRequiresSelfNotify();
				ModLoader.registerBlock(directDirectional, BDD_ItemBlockDirectDirectional.class);
				ModLoader.addName(directDirectional, "Directional Block");
				ModLoader.addName(directDirectional, "ja_JP", "向き付きブロック");
				
				if (sprite[0] > 0) {
					ModLoader.addRecipe(new ItemStack(directDirectional, 4, 0), new Object[] {
						"CC",
						"PP",
						Character.valueOf('C'), Block.cloth,
						Character.valueOf('P'), Block.planks
					});
				}
				if (sprite[1] > 0) {
					ModLoader.addRecipe(new ItemStack(directDirectional, 4, 1), new Object[] {
						"PC",
						"PC",
						Character.valueOf('C'), Block.cloth,
						Character.valueOf('P'), Block.planks
					});
				}
				if (sprite[2] > 0) {
					ModLoader.addRecipe(new ItemStack(directDirectional, 4, 2), new Object[] {
						"PP",
						"CC",
						Character.valueOf('C'), Block.cloth,
						Character.valueOf('P'), Block.planks
					});
				}
				if (sprite[3] > 0) {
					ModLoader.addRecipe(new ItemStack(directDirectional, 4, 3), new Object[] {
						"CP",
						"PC",
						Character.valueOf('C'), Block.cloth,
						Character.valueOf('P'), Block.planks
					});
				}
			}
		}
	}

	private boolean replaceBlock(Block pOriginal, Block pReplace) {
		if (isForge) {
			return false;
		}
		try {
			// Blockのstatic final分の置換え
			Field[] lfield = Block.class.getDeclaredFields();
			for (int li = 0; li < lfield.length; li++) {
				if (!Modifier.isStatic(lfield[li].getModifiers())) {
					// static以外は対象外
					continue;
				}
				
				Object lobject = lfield[li].get(null);
				if (lobject == pOriginal) {
					ModLoader.setPrivateValue(Block.class, null, li, pReplace);
					return true;
				}
			}
		}
		catch(Exception exception) {
		}
		return false;
	}

	@Override
	public void renderInvBlock(RenderBlocks renderblocks, Block block, int i, int k) {
		//インベントリでの描画
		Tessellator var4 = Tessellator.instance;
		
		if (renderblocks.useInventoryTint) {
			float par3 = 1.0F;
			int lcolor = block.getRenderColor(i);
			
			float var7 = (float)(lcolor >> 16 & 255) / 255.0F;
			float var8 = (float)(lcolor >> 8 & 255) / 255.0F;
			float var9 = (float)(lcolor & 255) / 255.0F;
			GL11.glColor4f(var7 * par3, var8 * par3, var9 * par3, 1.0F);
		}
		
//		renderblocks.updateCustomBlockBounds(block);
		
		block.setBlockBoundsForItemRender();
		renderblocks.setRenderBoundsFromBlock(block);
		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		var4.startDrawingQuads();
		var4.setNormal(0.0F, -1.0F, 0.0F);
		renderblocks.renderBottomFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(0, i));
		var4.draw();
		
		var4.startDrawingQuads();
		var4.setNormal(0.0F, 1.0F, 0.0F);
		renderblocks.renderTopFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(1, i));
		var4.draw();
		
		var4.startDrawingQuads();
		var4.setNormal(0.0F, 0.0F, -1.0F);
		renderblocks.renderEastFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(2, i));
		var4.draw();
		var4.startDrawingQuads();
		var4.setNormal(0.0F, 0.0F, 1.0F);
		renderblocks.renderWestFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(3, i));
		var4.draw();
		var4.startDrawingQuads();
		var4.setNormal(-1.0F, 0.0F, 0.0F);
		renderblocks.renderNorthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(4, i));
		var4.draw();
		var4.startDrawingQuads();
		var4.setNormal(1.0F, 0.0F, 0.0F);
		renderblocks.renderSouthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(5, i));
		var4.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	public void resetUV(RenderBlocks renderblocks) {
		// 面の回転設定を初期化
		renderblocks.uvRotateEast	= 0;
		renderblocks.uvRotateWest	= 0;
		renderblocks.uvRotateSouth	= 0;
		renderblocks.uvRotateNorth	= 0;
		renderblocks.uvRotateTop	= 0;
		renderblocks.uvRotateBottom	= 0;
	}

	@Override
	public boolean renderWorldBlock(RenderBlocks renderblocks, IBlockAccess iblockaccess, int blockX, int blockY, int blockZ, Block block, int renderType) {
		// ワールド上での描画
		if (block instanceof BDD_BlockWood || block instanceof BDD_BlockDirectDirectional) {
			int ll = iblockaccess.getBlockMetadata(blockX, blockY, blockZ);
			if ((ll & BLD_Vrt) > 0) {
				renderblocks.uvRotateEast = 1;
				renderblocks.uvRotateWest = 2;
				renderblocks.uvRotateSouth = 1;
				renderblocks.uvRotateNorth = 2;
			}
			if ((ll & BLD_Hor) > 0) {
				renderblocks.uvRotateTop = 2;
				renderblocks.uvRotateBottom = 1;
			}
			
			renderblocks.renderStandardBlock(block, blockX, blockY, blockZ);
			resetUV(renderblocks);
			
			return true;
		}
		if (block instanceof BDD_BlockCauldron) {
			int j = iblockaccess.getBlockMetadata(blockX, blockY, blockZ);
			boolean inv = (j & BLD_Inv) > 0;
			
			if (inv) {
				renderblocks.uvRotateEast = 3;
				renderblocks.uvRotateWest = 3;
				renderblocks.uvRotateSouth = 3;
				renderblocks.uvRotateNorth = 3;
			}
			
			BlockCauldron par1BlockCauldron = (BlockCauldron)block;
			renderblocks.renderStandardBlock(par1BlockCauldron, blockX, blockY, blockZ);
			Tessellator tessellator = Tessellator.instance;
			tessellator.setBrightness(par1BlockCauldron.getMixedBrightnessForBlock(iblockaccess, blockX, blockY, blockZ));
			float f = 1.0F;
			int i = par1BlockCauldron.colorMultiplier(iblockaccess, blockX, blockY, blockZ);
			float f1 = (float)(i >> 16 & 0xff) / 255F;
			float f2 = (float)(i >> 8 & 0xff) / 255F;
			float f3 = (float)(i & 0xff) / 255F;
			
			if (EntityRenderer.anaglyphEnable) {
				float f6 = (f1 * 30F + f2 * 59F + f3 * 11F) / 100F;
				float f4 = (f1 * 30F + f2 * 70F) / 100F;
				float f7 = (f1 * 30F + f3 * 70F) / 100F;
				f1 = f6;
				f2 = f4;
				f3 = f7;
			}
			
			tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);
			short ltx1 = 154;	// 内壁側面
			float f5 = 0.125F;
			renderblocks.renderSouthFace(par1BlockCauldron, ((float)blockX - 1.0F) + f5, blockY, blockZ, ltx1);
			renderblocks.renderNorthFace(par1BlockCauldron, ((float)blockX + 1.0F) - f5, blockY, blockZ, ltx1);
			renderblocks.renderWestFace(par1BlockCauldron, blockX, blockY, ((float)blockZ - 1.0F) + f5, ltx1);
			renderblocks.renderEastFace(par1BlockCauldron, blockX, blockY, ((float)blockZ + 1.0F) - f5, ltx1);
			short ltx2 = 139;	// 内壁底面
			if (inv) {
				// 反転
				renderblocks.renderTopFace(par1BlockCauldron, blockX, ((float)blockY - 1.0F) + 0.75F, blockZ, ltx2);
				renderblocks.renderBottomFace(par1BlockCauldron, blockX, ((float)blockY + 1.0F) - 0.25F, blockZ, ltx2);
				
			} else {
				renderblocks.renderTopFace(par1BlockCauldron, blockX, ((float)blockY - 1.0F) + 0.25F, blockZ, ltx2);
				renderblocks.renderBottomFace(par1BlockCauldron, blockX, ((float)blockY + 1.0F) - 0.75F, blockZ, ltx2);
			}
			
			// 水面
			j &= 7;
			if (j > 0) {
				short ltx3 = 205;
				
				if (j > 3) {
					j = 3;
				}
				
				renderblocks.renderTopFace(par1BlockCauldron, blockX, ((float)blockY - 1.0F) + (6F + (float)j * 3F) / 16F, blockZ, ltx3);
			}
			
			resetUV(renderblocks);
			return true;
		}
		
		return false;
	}

}
