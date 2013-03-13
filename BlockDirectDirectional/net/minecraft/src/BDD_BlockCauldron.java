package net.minecraft.src;

import java.util.Random;

public class BDD_BlockCauldron extends BlockCauldron {

	private int ftickRate;

	public BDD_BlockCauldron(int par1) {
		super(par1);
		setTickRandomly(true);
		ftickRate = mod_BDD_BlockDirectDirectional.isBoiled ? 50 : super.tickRate();
	}

	@Override
	public int getRenderType() {
		return mod_BDD_BlockDirectDirectional.renderID;
	}

	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, 
			EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9) {
		// 反転中は水が入りません
		if (par1World.isRemote) return true;
		int i = par1World.getBlockMetadata(par2, par3, par4);
		if ((i & mod_BDD_BlockDirectDirectional.BLD_Inv) == 0) {
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
	public int getBlockTextureFromSideAndMetadata(int par1, int par2) {
		if ((par2 & mod_BDD_BlockDirectDirectional.BLD_Inv) > 0) {
			switch (par1) {
			case 0:
				return 138;
			case 1:
				return 155;
			}
		} else {
			switch (par1){
			case 0:
				return 155;
			case 1:
				return 138;
			}
		}
		return 154;
	}

	@Override
	public void fillWithRain(World par1World, int par2, int par3, int par4) {
		// 逆さの釜には水がたまらない。
		if ((par1World.getBlockMetadata(par2, par3, par4) & mod_BDD_BlockDirectDirectional.BLD_Inv) == 0) {
			super.fillWithRain(par1World, par2, par3, par4);
		}
	}

	@Override
	public int tickRate() {
		// 湯気の発生率
		return ftickRate;
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

}
