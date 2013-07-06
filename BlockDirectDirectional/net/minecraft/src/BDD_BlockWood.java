package net.minecraft.src;

import java.util.List;

public class BDD_BlockWood extends BlockWood {

	public Block fOrigin;


	public BDD_BlockWood(int par1, Block pWood) {
		super(par1);
		fOrigin = pWood;
	}

	@Override
	public int getRenderType() {
		return mod_BDD_BlockDirectDirectional.renderID;
	}

	@Override
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4,
			EntityLivingBase par5EntityLiving, ItemStack par6ItemStack) {
		// 向きに応じたメタデータを貼り付ける
		int l = determineOrientation(par1World, par2, par3, par4, (EntityPlayer)par5EntityLiving);
		par1World.setBlockMetadataWithNotify(par2, par3, par4, l, 2);
	}

	private static int determineOrientation(World world, int i, int j, int k, EntityLivingBase entityliving) {
		// ピストンあたりの丸パクリ
		int ll = world.getBlockMetadata(i, j, k);

		if(MathHelper.abs((float)entityliving.posX - (float)i) < 2.0F && MathHelper.abs((float)entityliving.posZ - (float)k) < 2.0F) {
			double d = (entityliving.posY + 1.82D) - (double)entityliving.yOffset;
			// 上下
			if ((d - (double)j > 2D) || ((double)j - d > 0.0D)) {
				ll |= mod_BDD_BlockDirectDirectional.BLD_Vrt;
			}
		}
		int l = MathHelper.floor_double((double)((entityliving.rotationYaw * 4F) / 360F) + 0.5D) & 3;
		switch (l) {
		case 0:
		case 2:
			ll |= mod_BDD_BlockDirectDirectional.BLD_Hor;
		}
		return ll;
	}

	@Override
	public Icon getIcon(int par1, int par2) {
		return super.getIcon(par1, par2 & 0x03);
	}

	@Override
	public int damageDropped(int i) {
		return i & 0x03;
	}

	@Override
	public void registerIcons(IconRegister par1IconRegister) {
		super.registerIcons(par1IconRegister);
		fOrigin.registerIcons(par1IconRegister);
	}

}
