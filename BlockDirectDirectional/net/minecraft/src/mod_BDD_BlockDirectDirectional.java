package net.minecraft.src;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

public class mod_BDD_BlockDirectDirectional extends BaseMod {

	@MLProp(info="Enable Put away to the Planks with.")
	public static boolean isWoodDirection = true;
	@MLProp(info="Enable Put away to the TrapDoor with.")
	public static boolean isTrapDoorDirection = true;
	@MLProp(info="Enable Put away to the Cauldron with.")
	public static boolean isCauldronDirection = true;
	@MLProp(info="Enable Water is boiled by the Cauldron.")
	public static boolean isBoiled = true;

	@MLProp(info="Directional BlockID(0 : Disable)", min=0, max=4095)
	public static int BlockID = 0;
	@MLProp(info="IronTrapdoor BlockID(0 : Disable)", min=0, max=4095)
	public static int IronTrapdoorID = 0;


	public static int renderID = 0;
	public static Block planksDirectional;
	public static Block cauldronDirectional;
	public static Block trapdoorDirectional;
	public static Block directDirectional;
	public static Block ironTrapDoor; 
	public static int BLD_Inv = 0x08;
	public static int BLD_Vrt = 0x08;
	public static int BLD_Hor = 0x04;
	public static boolean isForge = ModLoader.isModLoaded("Forge");


	
	public static void debug(String str, Object...pArg) {
		System.out.println(String.format("BlockLogDirection-" + str, pArg));
	}

	@Override
	public String getVersion() {
		return "1.6.2-1";
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
			Block.blocksList[Block.planks.blockID] = null;
			planksDirectional = (new BDD_BlockWood(Block.planks.blockID, Block.planks)).setHardness(2.0F).setResistance(5.0F).setStepSound(Block.soundWoodFootstep).setUnlocalizedName("wood").func_111022_d("planks");
		}
		if (isCauldronDirection) {
			Block.blocksList[Block.cauldron.blockID] = null;
			cauldronDirectional = (new BDD_BlockCauldron(Block.cauldron.blockID)).setHardness(2.0F).setUnlocalizedName("cauldron").func_111022_d("cauldron");
		}
		if (isTrapDoorDirection) {
			Block.blocksList[Block.trapdoor.blockID] = null;
			trapdoorDirectional = (new BDD_BlockTrapDoor(Block.trapdoor.blockID, Material.wood)).setHardness(3.0F).setStepSound(Block.soundWoodFootstep).setUnlocalizedName("trapdoor").disableStats().func_111022_d("trapdoor");
		}
		if (IronTrapdoorID > 0) {
			ironTrapDoor = (new BDD_BlockTrapDoor(IronTrapdoorID, Material.iron)).setHardness(3F).setStepSound(Block.soundMetalFootstep).setUnlocalizedName("trapdooriron").func_111022_d("iron_bars").disableStats();
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
					List<Block> lblist = Arrays.asList(b);
					boolean flag = false;
					ArrayList<Block> arraylist = new ArrayList<Block>();
					arraylist.addAll(lblist);
					if (planksDirectional != null && lblist.indexOf(Block.planks) > -1) {
						// 木材が定義されている
						arraylist.add(planksDirectional);
						flag = true;
					}
					if (cauldronDirectional != null && lblist.indexOf(Block.cauldron) > -1) {
						// 大釜が定義されている
						arraylist.add(cauldronDirectional);
						flag = true;
					}
					if (trapdoorDirectional != null && lblist.indexOf(Block.trapdoor) > -1) {
						// 大釜が定義されている
						arraylist.add(trapdoorDirectional);
						flag = true;
					}
					
					if (flag) {
//						ModLoader.setPrivateValue(ItemTool.class, Item.itemsList[i], "blocksEffectiveAgainst", ((Object) (arraylist.toArray(new Block[0]))));
						ModLoader.setPrivateValue(ItemTool.class, (ItemTool)Item.itemsList[i], 0, ((Object) (arraylist.toArray(new Block[0]))));
						debug("ItemTool: ".concat(Item.itemsList[i].getUnlocalizedName()));
					}
				}
			}
			debug("ItemTool: Rewrite Over.");
		}
		catch(Exception exception) {
			debug("ItemTool: fail");
		}
		
		// Finalで定義されているので通常手段での書き換えは不可
		if (planksDirectional != null) {
			if (replaceBlock(Block.planks, planksDirectional)) {
				debug("Block.wood: directional.");
			} else {
				debug("Block.wood: fail");
			}
		}
		if (cauldronDirectional != null) {
			if (replaceBlock(Block.cauldron, cauldronDirectional)) {
				debug("Block.cauldron: directional.");
			} else {
				debug("Block.cauldron.: fail");
			}
		}
		if (trapdoorDirectional != null) {
			if (replaceBlock(Block.trapdoor, trapdoorDirectional)) {
				debug("Block.trapdoor: directional.");
			} else {
				debug("Block.trapdoor.: fail");
			}
		}
		
		// 向き付きブロック関係
		if (BlockID > 0) {
			directDirectional = (new BDD_BlockDirectDirectional(BlockID, Material.wood)).setHardness(2.0F).setResistance(5F).setStepSound(Block.soundWoodFootstep).setUnlocalizedName("direction");
			ModLoader.registerBlock(directDirectional, BDD_ItemBlockDirectDirectional.class);
			ModLoader.addName(directDirectional, "Directional Block");
			ModLoader.addName(directDirectional, "ja_JP", "向き付きブロック");
			
			ModLoader.addRecipe(new ItemStack(directDirectional, 4, 0), new Object[] {
				"CC",
				"PP",
				Character.valueOf('C'), Block.cloth,
				Character.valueOf('P'), Block.planks
			});
			ModLoader.addRecipe(new ItemStack(directDirectional, 4, 1), new Object[] {
				"PC",
				"PC",
				Character.valueOf('C'), Block.cloth,
				Character.valueOf('P'), Block.planks
			});
			ModLoader.addRecipe(new ItemStack(directDirectional, 4, 2), new Object[] {
				"PP",
				"CC",
				Character.valueOf('C'), Block.cloth,
				Character.valueOf('P'), Block.planks
			});
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
		renderblocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, i));
		var4.draw();
		
		var4.startDrawingQuads();
		var4.setNormal(0.0F, 1.0F, 0.0F);
		renderblocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, i));
		var4.draw();
		
		var4.startDrawingQuads();
		var4.setNormal(0.0F, 0.0F, -1.0F);
		renderblocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, i));
		var4.draw();
		var4.startDrawingQuads();
		var4.setNormal(0.0F, 0.0F, 1.0F);
		renderblocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, i));
		var4.draw();
		var4.startDrawingQuads();
		var4.setNormal(-1.0F, 0.0F, 0.0F);
		renderblocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, i));
		var4.draw();
		var4.startDrawingQuads();
		var4.setNormal(1.0F, 0.0F, 0.0F);
		renderblocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, i));
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
			
			BlockCauldron lbcauldron = (BlockCauldron)block;
			renderblocks.renderStandardBlock(lbcauldron, blockX, blockY, blockZ);
			Tessellator tessellator = Tessellator.instance;
			tessellator.setBrightness(lbcauldron.getMixedBrightnessForBlock(iblockaccess, blockX, blockY, blockZ));
			float var6 = 1.0F;
			int lcolor = lbcauldron.colorMultiplier(iblockaccess, blockX, blockY, blockZ);
			float lfcr = (float)(lcolor >> 16 & 255) / 255.0F;
			float lfcg = (float)(lcolor >> 8 & 255) / 255.0F;
			float lfcb = (float)(lcolor & 255) / 255.0F;
			
			if (EntityRenderer.anaglyphEnable) {
				float lfar = (lfcr * 30.0F + lfcg * 59.0F + lfcb * 11.0F) / 100.0F;
				float lfag = (lfcr * 30.0F + lfcg * 70.0F) / 100.0F;
				float lfab = (lfcr * 30.0F + lfcb * 70.0F) / 100.0F;
				lfcr = lfar;
				lfcg = lfag;
				lfcb = lfab;
			}
			
			tessellator.setColorOpaque_F(var6 * lfcr, var6 * lfcg, var6 * lfcb);
			Icon licon = lbcauldron.getBlockTextureFromSide(2);
			float var11 = 0.125F;
			renderblocks.renderFaceXPos(lbcauldron, (double)((float)blockX - 1.0F + var11), (double)blockY, (double)blockZ, licon);
			renderblocks.renderFaceXNeg(lbcauldron, (double)((float)blockX + 1.0F - var11), (double)blockY, (double)blockZ, licon);
			renderblocks.renderFaceZPos(lbcauldron, (double)blockX, (double)blockY, (double)((float)blockZ - 1.0F + var11), licon);
			renderblocks.renderFaceZNeg(lbcauldron, (double)blockX, (double)blockY, (double)((float)blockZ + 1.0F - var11), licon);
			licon = BlockCauldron.func_94375_b("inner");
			if (inv) {
				// 反転
				renderblocks.renderFaceYPos(lbcauldron, (double)blockX, (double)((float)blockY - 1.0F + 0.75F), (double)blockZ, licon);
				renderblocks.renderFaceYNeg(lbcauldron, (double)blockX, (double)((float)blockY + 1.0F - 0.25F), (double)blockZ, licon);
			} else {
				renderblocks.renderFaceYPos(lbcauldron, (double)blockX, (double)((float)blockY - 1.0F + 0.25F), (double)blockZ, licon);
				renderblocks.renderFaceYNeg(lbcauldron, (double)blockX, (double)((float)blockY + 1.0F - 0.75F), (double)blockZ, licon);
			}
			
			// 水面
			j &= 7;
			if (j > 0) {
				licon = BlockFluid.func_94424_b("water_still");
				if (j > 3) {
					j = 3;
				}
				renderblocks.renderFaceYPos(lbcauldron,
						(double)blockX, 
						(double)((float)blockY - 1.0F + (6.0F + (float)j * 3.0F) / 16.0F),
						(double)blockZ, licon);
			}
			resetUV(renderblocks);
			
			return true;
		}
		
		return false;
	}

}
