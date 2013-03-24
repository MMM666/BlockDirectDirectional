package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

public class BDD_BlockDirectDirectional extends Block {

	protected Icon ficon[] = new Icon[4];


	public BDD_BlockDirectDirectional(int i, Material material) {
		super(i, material);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	/**
	 * アイコンのロード
	 */
	@Override
	public void registerIcons(IconRegister par1IconRegister) {
		ficon[0] = par1IconRegister.registerIcon("direction0");
		ficon[1] = par1IconRegister.registerIcon("direction1");
		ficon[2] = par1IconRegister.registerIcon("direction2");
		ficon[3] = par1IconRegister.registerIcon("direction3");
	}

	@Override
	public Icon getBlockTextureFromSideAndMetadata(int i, int j) {
		return ficon[j & 0x03];
	}

	@Override
	public int getRenderType() {
		return mod_BDD_BlockDirectDirectional.renderID;
	}

	@Override
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4,
			EntityLiving par5EntityLiving, ItemStack par6ItemStack) {
		// 向きに応じたメタデータを貼り付ける
		int l = determineOrientation(par1World, par2, par3, par4, par5EntityLiving);
		par1World.setBlockMetadataWithNotify(par2, par3, par4, l, 2);
	}

	private static int determineOrientation(World world, int i, int j, int k, EntityLiving entityliving) {
		// ピストンあたりの丸パクリ
		int ll = world.getBlockMetadata(i, j, k);

		if(MathHelper.abs((float)entityliving.posX - (float)i) < 2.0F && MathHelper.abs((float)entityliving.posZ - (float)k) < 2.0F) {
			double d = (entityliving.posY + entityliving.getEyeHeight()) - (double)entityliving.yOffset;
			// 上下
			if ((d - (double)j > 2D) || ((double)j - d > 0.0D)) {
				ll |= mod_BDD_BlockDirectDirectional.BLD_Vrt;
			}
		}

		int l = MathHelper.floor_double((double)((entityliving.rotationYaw * 4F) / 360F) + 0.5D) & 3;
		switch (l) {
		case 0:
		case 2:
			return ll |= mod_BDD_BlockDirectDirectional.BLD_Hor;
		}
		return ll;
	}

	@Override
	public int damageDropped(int i) {
		return i & 0x03;
	}

	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (int li = 0; li < 4; li++) {
			par3List.add(new ItemStack(par1, 1, li));
		}
	}

}
