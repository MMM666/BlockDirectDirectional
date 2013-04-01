package net.minecraft.src;

import java.util.Random;

public class BDD_BlockCauldron extends BlockCauldron {

	public BDD_BlockCauldron(int par1) {
		super(par1);
		setTickRandomly(mod_BDD_BlockDirectDirectional.isBoiled);
	}

	@Override
	public int getRenderType() {
		return mod_BDD_BlockDirectDirectional.renderID;
	}

	@Override
	public Icon getIcon(int par1, int par2) {
		if (isInvert(par2)) {
			par1 = par1 == 0 ? 1 : par1 == 1 ? 0 : par1;
		}
		return super.getIcon(par1, par2);
	}

	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, 
			EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9) {
		// 反転中は水が入りません
		if (par1World.isRemote) return true;
		int i = par1World.getBlockMetadata(par2, par3, par4);
		if (!isInvert(i)) {
			return super.onBlockActivated(par1World, par2, par3, par4, par5EntityPlayer, par6, par7, par8, par9);
		}
		return true;
	}

	@Override
	public int onBlockPlaced(World par1World, int par2, int par3, int par4,
			int par5, float par6, float par7, float par8, int par9) {
		// 設置向きの判定
		if (par5 == 0 || par5 != 1 && (double)par7 > 0.5D) {
			par9 |= mod_BDD_BlockDirectDirectional.BLD_Inv;
		}
		return par9;
	}

	@Override
	public void fillWithRain(World par1World, int par2, int par3, int par4) {
		// 逆さの釜には水がたまらない。
		if (!isInvert(par1World.getBlockMetadata(par2, par3, par4))) {
			super.fillWithRain(par1World, par2, par3, par4);
		}
	}

	@Override
	public int tickRate(World par1World) {
		// 湯気の発生率
		return 50;
	}

	@Override
	public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
		super.randomDisplayTick(par1World, par2, par3, par4, par5Random);
		if (!mod_BDD_BlockDirectDirectional.isBoiled) return;
		int lmd = par1World.getBlockMetadata(par2, par3, par4);
		if ((lmd & 3) == 0) return;
		// 表示はクライアントのみ
		int lid = par1World.getBlockId(par2, par3 - 1, par4);
		if (	lid == Block.fire.blockID ||
				lid == Block.lavaStill.blockID ||
				lid == Block.lavaMoving.blockID) {
			// 煮える！
			BDD_Client.showFX(par1World, par2, par3, par4, par5Random, lmd);
		}
	}

	/**
	 * 反転チェック
	 */
	public static boolean isInvert(int pMetadata) {
		return (pMetadata & mod_BDD_BlockDirectDirectional.BLD_Inv) > 0;
	}

}
