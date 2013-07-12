package net.minecraft.src;

import java.io.ObjectInputStream.GetField;
import java.lang.reflect.Method;

public class BDD_BlockTrapDoor extends BlockTrapDoor {

	protected BDD_BlockTrapDoor(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	@Override
	public void onPoweredBlockChange(World par1World, int par2, int par3,
			int par4, boolean par5) {
		super.onPoweredBlockChange(par1World, par2, par3, par4, par5 ^ isWindow(par1World, par2, par3, par4));
	}

	@Override
	public void onNeighborBlockChange(World pWorld, int pX, int pY, int pZ, int pBlockID) {
		if (!pWorld.isRemote) {
			int lmeta = pWorld.getBlockMetadata(pX, pY, pZ);
			int ldir = lmeta & 3;
			int lx = pX;
			int lz = pZ;
			boolean lwindow = false;
			
			if (ldir == 0) {
				lz++;
			}
			if (ldir == 1) {
				lz--;
			}
			if (ldir == 2) {
				lx++;
			}
			if (ldir == 3) {
				lx--;
			}
			
			if (!isValidSupportBlock(pWorld.getBlockId(lx, pY, lz))) {
				// 通常の取り付け方向には何もない
				if ((lmeta & 8) > 0) {
					// 上付き
					lwindow = isValidSupportBlock(pWorld.getBlockId(pX, pY + 1, pZ));
				} else {
					// 下付き
					lwindow = isValidSupportBlock(pWorld.getBlockId(pX, pY - 1, pZ));
				}
				if (!lwindow) {
					// 固定できていないので破壊
					pWorld.setBlockToAir(pX, pY, pZ);
					this.dropBlockAsItem(pWorld, pX, pY, pZ, lmeta, 0);
				}
			}
			
			boolean lpower = pWorld.isBlockIndirectlyGettingPowered(pX, pY, pZ);
			if (lpower || pBlockID > 0 && Block.blocksList[pBlockID].canProvidePower()) {
				onPoweredBlockChange(pWorld, pX, pY, pZ, lpower);
			}
		}
	}

	@Override
	public int onBlockPlaced(World par1World, int par2, int par3, int par4,
			int par5, float par6, float par7, float par8, int par9) {
		int var10 = 0;
		
		if (par5 != 1 && par5 != 0) {
			if (par5 == 2) {
				var10 = 0;
			}
			if (par5 == 3) {
				var10 = 1;
			}
			if (par5 == 4) {
				var10 = 2;
			}
			if (par5 == 5) {
				var10 = 3;
			}
			// 上付き判定
			if (par7 > 0.5F) {
				var10 |= 8;
			}
		} else {
			if (Math.abs(par6 - 0.5F) > Math.abs(par8 - 0.5F)) {
				// X
				var10 = par6 > 0.6 ? 2 : 3;
			} else {
				// Z
				var10 = par8 > 0.6 ? 0 : 1;
			}
			var10 |= 4;
			if (par5 == 0) {
				var10 |= 8;
			}
		}
		
		return var10;
	}

	@Override
	public boolean canPlaceBlockOnSide(World par1World, int par2, int par3, int par4, int par5) {
		if (par5 == 0) {
			par3++;
		}
		if (par5 == 1) {
			par3--;
		}
		if (par5 == 2) {
			++par4;
		}
		if (par5 == 3) {
			--par4;
		}
		if (par5 == 4) {
			++par2;
		}
		if (par5 == 5) {
			--par2;
		}
		
		return isValidSupportBlock(par1World.getBlockId(par2, par3, par4));
	}


	/**
	 * トラップドアが上付き・下付きかを調べる
	 * @param pWorld
	 * @param pX
	 * @param pY
	 * @param pZ
	 * @return
	 */
	public boolean isWindow(World pWorld, int pX, int pY, int pZ) {
		int lmeta = pWorld.getBlockMetadata(pX, pY, pZ);
		int ldir = lmeta & 3;
		int lx = pX;
		int lz = pZ;;
		
		if (ldir == 0) {
			lz++;
		}
		if (ldir == 1) {
			lz--;
		}
		if (ldir == 2) {
			lx++;
		}
		if (ldir == 3) {
			lx--;
		}
		
		if (!isValidSupportBlock(pWorld.getBlockId(lx, pY, lz))) {
			// 通常の取り付け方向には何もない
			if ((lmeta & 8) > 0) {
				// 上付き
				return isValidSupportBlock(pWorld.getBlockId(pX, pY + 1, pZ));
			} else {
				// 下付き
				return isValidSupportBlock(pWorld.getBlockId(pX, pY - 1, pZ));
			}
		}
		return false;
	}

	protected boolean isValidSupportBlock(int pBlockID) {
		// TODO:丸パクりなのでバージョンアップ時には必ずチェックすること
		if (pBlockID <= 0) {
			return false;
		} else {
			Block lblock = Block.blocksList[pBlockID];
			return lblock != null && lblock.blockMaterial.isOpaque() && lblock.renderAsNormalBlock() || lblock == Block.glowStone || lblock instanceof BlockHalfSlab || lblock instanceof BlockStairs;
		}
	}

}
